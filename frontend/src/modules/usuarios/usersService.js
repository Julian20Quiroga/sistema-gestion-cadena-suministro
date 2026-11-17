import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({ Authorization: 'Bearer ' + token });

export async function fetchUsers({ token }) {
  const response = await httpClient.get('/api/users', { headers: authHeaders(token) });
  return response.data;
}

export async function createUser({ token, user }) {
  const response = await httpClient.post('/api/users', user, { headers: authHeaders(token) });
  return response.data;
}
