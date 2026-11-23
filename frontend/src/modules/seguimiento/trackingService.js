import httpClient from '../../shared/httpClient';
const headers = (token) => ({ Authorization: 'Bearer ' + token });
export async function fetchActiveVehicles({ token }) {
  const response = await httpClient.get('/api/vehicles', { params: { status: 'ACTIVE', page: 0, size: 100 }, headers: headers(token) });
  return response.data;
}
export async function fetchLatest({ token, id }) {
  const response = await httpClient.get('/api/tracking/vehicles/' + id + '/latest', { headers: headers(token) });
  return response.data;
}
export async function fetchTrail({ token, id }) {
  const response = await httpClient.get('/api/tracking/vehicles/' + id + '/trail', { params: { limit: 50 }, headers: headers(token) });
  return response.data;
}
export async function createVehicle({ token, vehicle }) {
  const response = await httpClient.post('/api/vehicles', vehicle, { headers: headers(token) });
  return response.data;
}

export async function subscribeTelemetry({ token, signal, onReading }) {
  const baseUrl = httpClient.defaults.baseURL || window.location.origin;
  while (!signal.aborted) {
    try {
      const response = await fetch(baseUrl + '/api/tracking/stream', {
        headers: { Authorization: 'Bearer ' + token, Accept: 'text/event-stream' },
        signal,
      });
      if (!response.ok || !response.body) throw new Error('No fue posible abrir el canal de telemetría.');
      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';
      while (!signal.aborted) {
        const { value, done } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, '\n');
        let boundary;
        while ((boundary = buffer.indexOf('\n\n')) >= 0) {
          const eventBlock = buffer.slice(0, boundary);
          buffer = buffer.slice(boundary + 2);
          const data = eventBlock.split('\n').filter((line) => line.startsWith('data:'))
            .map((line) => line.slice(5).trim()).join('\n');
          if (data) {
            try { onReading(JSON.parse(data)); } catch { /* ignore malformed event; keep stream alive */ }
          }
        }
      }
    } catch (error) {
      if (signal.aborted) return;
      if (error.name === 'AbortError') return;
    }
    if (!signal.aborted) await new Promise((resolve) => window.setTimeout(resolve, 1200));
  }
}

async function fetchAllPages(path, token, params = {}) {
  const config = { params: { page: 0, size: 100, ...params }, headers: headers(token) };
  const first = await httpClient.get(path, config);
  const totalPages = Number(first.data.totalPages || 1);
  const rest = await Promise.all(Array.from({ length: Math.max(0, totalPages - 1) }, (_, index) =>
    httpClient.get(path, { ...config, params: { ...config.params, page: index + 1 } })
  ));
  return [...(first.data.content || []), ...rest.flatMap((page) => page.data.content || [])];
}

export async function fetchProductTraceability({ token, productId }) {
  const [movements, purchaseOrders, orders, dispatches] = await Promise.all([
    fetchAllPages('/api/inventory/movements', token, { productId }),
    fetchAllPages('/api/purchase-orders', token),
    fetchAllPages('/api/orders', token),
    fetchAllPages('/api/dispatches', token),
  ]);
  const events = [];
  purchaseOrders.forEach((order) => (order.details || []).filter((detail) => Number(detail.productId) === Number(productId)).forEach((detail) => {
    events.push({ id: `purchase-${order.id}-${detail.id}`, date: order.orderDate, stage: 'Abastecimiento', title: `Orden de compra #${order.id}`, status: order.status, detail: `Recibido ${detail.receivedQuantity} de ${detail.quantity} unidades` });
  }));
  movements.forEach((movement) => (movement.details || []).filter((detail) => Number(detail.productId) === Number(productId)).forEach((detail, index) => {
    events.push({ id: `movement-${movement.id}-${index}`, date: movement.date, stage: 'Almacenamiento', title: movement.type || 'Movimiento de inventario', status: movement.status, detail: `${detail.quantity > 0 ? '+' : ''}${detail.quantity} unidades · espacio #${detail.storageSpaceId}${movement.reason ? ` · ${movement.reason}` : ''}` });
  }));
  const ordersForProduct = orders.filter((order) => (order.details || []).some((detail) => Number(detail.productId) === Number(productId)));
  ordersForProduct.forEach((order) => {
    const quantity = (order.details || []).filter((detail) => Number(detail.productId) === Number(productId)).reduce((sum, detail) => sum + Number(detail.quantity || 0), 0);
    events.push({ id: `order-${order.id}`, date: order.createdAt, stage: 'Pedido y entrega', title: `Pedido ${order.orderReference}`, status: order.status, detail: `${order.customerName} · ${quantity} unidades · ${order.deliveryCity}` });
    dispatches.filter((dispatch) => Number(dispatch.orderId) === Number(order.id)).forEach((dispatch) => {
      events.push({ id: `dispatch-${dispatch.id}`, dispatchId: dispatch.id, date: dispatch.createdAt, stage: 'Despacho y transporte', title: `Despacho #${dispatch.id}`, status: dispatch.status, detail: `Registrado · Bodega #${dispatch.warehouseId}${dispatch.vehicleId ? ` · vehículo #${dispatch.vehicleId}` : ''}`, vehicleId: dispatch.vehicleId });
    });
  });
  await Promise.all(events.filter((event) => event.stage === 'Despacho y transporte' && event.vehicleId && event.status === 'EN_TRANSPORTE').map(async (event) => {
    try {
      const response = await httpClient.get(`/api/tracking/dispatches/${event.dispatchId}/latest`, { headers: headers(token) });
      event.location = response.data;
    } catch { /* no telemetry has arrived for this dispatch yet */ }
  }));
  return events.sort((left, right) => {
    if (left.date && right.date) return new Date(right.date) - new Date(left.date);
    if (left.date) return -1;
    if (right.date) return 1;
    return left.stage.localeCompare(right.stage, 'es');
  });
}

