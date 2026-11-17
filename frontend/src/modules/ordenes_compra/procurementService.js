import httpClient from '../../shared/httpClient';

const headers = (token) => ({ Authorization: 'Bearer ' + token });

export async function fetchPurchaseOrders({ token, page = 0, size = 10, status = '' }) {
  const response = await httpClient.get('/api/purchase-orders', {
    params: { page, size, ...(status ? { status } : {}) },
    headers: headers(token),
  });
  return response.data;
}

export async function fetchProcurementSuppliers({ token }) {
  const response = await httpClient.get('/api/supplier', {
    params: { page: 0, size: 100, status: 'ACTIVE' },
    headers: headers(token),
  });
  return response.data;
}

export async function fetchProcurementProducts({ token }) {
  const response = await httpClient.get('/api/products', {
    params: { page: 0, size: 100, status: 'ACTIVE' },
    headers: headers(token),
  });
  return response.data;
}

export async function fetchProcurementWarehouses({ token }) {
  const response = await httpClient.get('/api/warehouses', {
    params: { page: 0, size: 100, status: 'ACTIVE' },
    headers: headers(token),
  });
  return response.data;
}

export async function fetchFreeLocations({ token, warehouseId }) {
  const response = await httpClient.get('/api/warehouses/' + warehouseId + '/free-locations', {
    params: { minFree: 1 },
    headers: headers(token),
  });
  return response.data;
}

export async function createPurchaseOrder({ token, supplierId, warehouseId, items }) {
  const response = await httpClient.post('/api/purchase-orders', {
    supplierId: Number(supplierId),
    warehouseId: Number(warehouseId),
    items: items.map((item) => ({
      productId: Number(item.productId),
      quantity: Number(item.quantity),
      unitPrice: Number(item.unitPrice),
    })),
  }, { headers: headers(token) });
  return response.data;
}

export async function receivePurchaseOrder({ token, orderId, items }) {
  const response = await httpClient.post('/api/purchase-orders/' + orderId + '/reception', {
    items: items.map((item) => ({
      productId: Number(item.productId),
      storageSpaceId: Number(item.storageSpaceId),
      quantity: Number(item.quantity),
    })),
  }, { headers: headers(token) });
  return response.data;
}

export async function cancelPurchaseOrder({ token, orderId }) {
  await httpClient.post('/api/purchase-orders/' + orderId + '/cancel', {}, {
    headers: headers(token),
  });
}
