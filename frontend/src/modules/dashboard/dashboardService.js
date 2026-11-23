import httpClient from '../../shared/httpClient';

const pageSize = 100;
const headers = (token) => ({ Authorization: 'Bearer ' + token });

async function fetchAllPages(path, token, extraParams = {}) {
  const first = await httpClient.get(path, {
    params: { page: 0, size: pageSize, ...extraParams },
    headers: headers(token),
  });
  const firstPage = first.data;
  const pages = Number(firstPage.totalPages || 1);
  const rest = await Promise.all(Array.from({ length: Math.max(0, pages - 1) }, (_, index) =>
    httpClient.get(path, {
      params: { page: index + 1, size: pageSize, ...extraParams },
      headers: headers(token),
    })
  ));
  return [ ...(firstPage.content || []), ...rest.flatMap((response) => response.data.content || []) ];
}

export async function fetchDashboardData(token) {
  const [products, suppliers, inventory, orders, warehouses, purchaseOrders, movements, dispatches, vehicles, forecastResponse] = await Promise.all([
    fetchAllPages('/api/products', token),
    fetchAllPages('/api/supplier', token),
    fetchAllPages('/api/inventory/levels', token),
    fetchAllPages('/api/orders', token),
    fetchAllPages('/api/warehouses', token),
    fetchAllPages('/api/purchase-orders', token),
    fetchAllPages('/api/inventory/movements', token),
    fetchAllPages('/api/dispatches', token),
    fetchAllPages('/api/vehicles', token),
    httpClient.post('/api/forecast/generate', {
      productId: null,
      algorithm: 'MOVING_AVERAGE',
      periodsAhead: 3,
      autoCreateSupplyNeeds: false,
    }, { headers: headers(token) }),
  ]);

  const activeProducts = products.filter((product) => product.status === 'ACTIVE');
  const activeSuppliers = suppliers.filter((supplier) => supplier.status === 'ACTIVE');
  const activeWarehouses = warehouses.filter((warehouse) => warehouse.status === 'ACTIVE');
  const openOrders = orders.filter((order) => ['REGISTRADO', 'PREPARACION', 'DESPACHADO'].includes(order.status));
  const openPurchaseOrders = purchaseOrders.filter((order) => ['REGISTRADA', 'RECIBIDA_PARCIAL'].includes(order.status));
  const activeDispatches = dispatches.filter((dispatch) => ['PENDIENTE', 'PREPARANDO', 'EN_TRANSPORTE'].includes(dispatch.status));
  const routeEligibleOrders = orders.filter((order) => ['REGISTRADO', 'PREPARACION'].includes(order.status)
    && order.deliveryLatitude != null && order.deliveryLongitude != null);
  const layouts = await Promise.all(activeWarehouses.map(async (warehouse) => {
    const response = await httpClient.get(`/api/warehouses/${warehouse.id}/structure`, { headers: headers(token) });
    return response.data;
  }));
  const locationNodes = [];
  const collectLocations = (node) => {
    if (node.type === 'LOCATION') locationNodes.push(node);
    (node.children || []).forEach(collectLocations);
  };
  layouts.forEach(collectLocations);
  const forecastItems = forecastResponse.data.forecasts || [];

  return {
    metrics: {
      products: activeProducts.length,
      suppliers: activeSuppliers.length,
      availableUnits: inventory.reduce((sum, item) => sum + Number(item.availableQuantity || 0), 0),
      activeOrders: openOrders.length,
      warehouses: activeWarehouses.length,
      openPurchaseOrders: openPurchaseOrders.length,
      activeDispatches: activeDispatches.length,
      vehicles: vehicles.filter((vehicle) => vehicle.status === 'ACTIVE').length,
      routeEligibleOrders: routeEligibleOrders.length,
      projectedDemand: forecastItems.reduce((sum, item) => {
        const projectedTotal = Number(item.totalProjectedDemand || 0);
        const projectionsTotal = (item.projections || []).reduce((projectionSum, value) => projectionSum + Number(value || 0), 0);
        return sum + (projectedTotal || projectionsTotal);
      }, 0),
      suggestedProcurement: forecastItems.reduce((sum, item) => sum + Number(item.recommendedProcurementQuantity || 0), 0),
      storageUsagePercent: locationNodes.reduce((sum, location) => sum + Number(location.capacity || 0), 0)
        ? Math.round(100 * locationNodes.reduce((sum, location) => sum + Number(location.occupancy || 0), 0)
          / locationNodes.reduce((sum, location) => sum + Number(location.capacity || 0), 0)) : 0,
    },
    recentMovements: movements.slice(0, 6),
    ordersByStatus: orders.reduce((counts, order) => {
      counts[order.status] = (counts[order.status] || 0) + 1;
      return counts;
    }, {}),
    dispatchesByStatus: dispatches.reduce((counts, dispatch) => {
      counts[dispatch.status] = (counts[dispatch.status] || 0) + 1;
      return counts;
    }, {}),
  };
}
