import httpClient from '../../shared/httpClient';
import { fetchProducts } from '../productos/productService';

const authHeaders = (token) => ({
  Authorization: 'Bearer ' + token,
});

export { fetchProducts };

export async function fetchOrders({ token, reference = '', status = '', page = 0, size = 10 }) {
  const params = { page, size };
  if (reference.trim()) params.reference = reference.trim();
  if (status) params.status = status;

  const response = await httpClient.get('/api/orders', {
    params,
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchActiveWarehouses({ token }) {
  const response = await httpClient.get('/api/warehouses', {
    params: { status: 'ACTIVE', page: 0, size: 100 },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchDispatchForOrder({ token, orderId }) {
  const response = await httpClient.get('/api/dispatches', {
    params: { orderId, page: 0, size: 1 },
    headers: authHeaders(token),
  });
  return response.data.content?.[0] || null;
}

export async function createDispatch({ token, orderId, warehouseId }) {
  const response = await httpClient.post('/api/dispatches', {
    orderId: Number(orderId),
    warehouseId: Number(warehouseId),
    vehicleId: null,
  }, { headers: authHeaders(token) });
  return response.data;
}

const dispatchTransitionPaths = {
  prepare: 'start-preparation',
  ship: 'ship',
  deliver: 'deliver',
  cancel: 'cancel',
};

export async function transitionDispatch({ token, id, action }) {
  const path = dispatchTransitionPaths[action];
  if (!path) throw new Error('Acción de despacho no soportada');

  const response = await httpClient.post('/api/dispatches/' + id + '/' + path, {}, {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function createOrder({ token, order }) {
  const response = await httpClient.post('/api/orders', order, {
    headers: authHeaders(token),
  });
  return response.data;
}

const transitionPaths = {
  prepare: 'start-preparation',
  dispatch: 'mark-dispatched',
  deliver: 'mark-delivered',
  cancel: 'cancel',
};

export async function transitionOrder({ token, id, action }) {
  const path = transitionPaths[action];
  if (!path) throw new Error('Acción de pedido no soportada');

  const response = await httpClient.post('/api/orders/' + id + '/' + path, {}, {
    headers: authHeaders(token),
  });
  return response.data;
}
