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

