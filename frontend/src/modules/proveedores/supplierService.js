import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({
  Authorization: 'Bearer ' + token,
});

export async function fetchSuppliers({ token, page = 0, size = 10, name = '' }) {
  const response = await httpClient.get('/api/supplier', {
    params: { page, size, ...(name ? { name } : {}) },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function createSupplier({ token, supplier }) {
  const response = await httpClient.post('/api/supplier', supplier, {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function updateSupplier({ token, id, supplier }) {
  const response = await httpClient.put('/api/supplier/' + id, supplier, {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function deleteSupplier({ token, id }) {
  await httpClient.delete('/api/supplier/' + id, {
    headers: authHeaders(token),
  });
}