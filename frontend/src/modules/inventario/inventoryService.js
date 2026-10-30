import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({ Authorization: 'Bearer ' + token });

export async function fetchInventoryLevels({ token, page = 0, size = 20, productId, warehouseId }) {
  const params = { page, size };
  if (productId) params.productId = Number(productId);
  if (warehouseId) params.warehouseId = Number(warehouseId);
  const response = await httpClient.get('/api/inventory/levels', { params, headers: authHeaders(token) });
  return response.data;
}

export async function fetchInventoryMovements({ token, page = 0, size = 20, productId }) {
  const params = { page, size };
  if (productId) params.productId = Number(productId);
  const response = await httpClient.get('/api/inventory/movements', { params, headers: authHeaders(token) });
  return response.data;
}

export async function fetchInventoryProducts({ token }) {
  const response = await httpClient.get('/api/products', {
    params: { page: 0, size: 100 }, headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchInventoryWarehouses({ token }) {
  const response = await httpClient.get('/api/warehouses', {
    params: { page: 0, size: 100, status: 'ACTIVE' }, headers: authHeaders(token),
  });
  return response.data;
}
