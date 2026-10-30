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
