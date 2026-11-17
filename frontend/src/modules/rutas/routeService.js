import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({ Authorization: 'Bearer ' + token });

export async function fetchRouteVehicles({ token }) {
  const response = await httpClient.get('/api/vehicles', {
    params: { status: 'ACTIVE', page: 0, size: 100 },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchRouteWarehouses({ token }) {
  const response = await httpClient.get('/api/warehouses', {
    params: { status: 'ACTIVE', page: 0, size: 100 },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchRouteOrders({ token }) {
  const response = await httpClient.get('/api/orders', {
    params: { page: 0, size: 100 },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function updateWarehouseCoordinates({ token, warehouse, latitude, longitude }) {
  const response = await httpClient.put('/api/warehouses/' + warehouse.id, {
    name: warehouse.name,
    address: warehouse.address,
    phone: warehouse.phone,
    headquarterId: Number(warehouse.headquarterId),
    latitude: Number(latitude),
    longitude: Number(longitude),
    status: warehouse.status,
  }, { headers: authHeaders(token) });
  return response.data;
}

export async function optimizeRoute({ token, warehouseId, vehicleId, orderIds }) {
  const response = await httpClient.post('/api/vehicles/optimize-route', {
    warehouseId: Number(warehouseId),
    vehicleId: Number(vehicleId),
    orderIds: orderIds.map(Number),
    strategyName: 'NEAREST_NEIGHBOR',
  }, { headers: authHeaders(token) });
  return response.data;
}
