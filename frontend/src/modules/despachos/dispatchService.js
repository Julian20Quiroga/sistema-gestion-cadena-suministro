import httpClient from '../../shared/httpClient';
const authHeaders = (token) => ({ Authorization: 'Bearer ' + token });
export async function fetchDispatches({ token, status = '', page = 0, size = 10 }) {
  const params = { page, size }; if (status) params.status = status;
  const result = await httpClient.get('/api/dispatches', { params, headers: authHeaders(token) });
  return result.data;
}
export async function transitionDispatch({ token, id, action }) {
  const paths = { prepare: 'start-preparation', ship: 'ship', deliver: 'deliver', cancel: 'cancel' };
  if (!paths[action]) throw new Error('Acción no soportada');
  const result = await httpClient.post('/api/dispatches/' + id + '/' + paths[action], {}, { headers: authHeaders(token) });
  return result.data;
}
