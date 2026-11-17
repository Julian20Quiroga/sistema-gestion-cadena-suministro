import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({
  Authorization: 'Bearer ' + token,
});

export async function fetchProducts({ token, page = 0, size = 10, name = '' }) {
  const response = await httpClient.get('/api/products', {
    params: { page, size, ...(name ? { name } : {}) },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchSuppliers({ token, page = 0, size = 100 }) {
  const response = await httpClient.get('/api/supplier', {
    params: { page, size },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function createProduct({ token, product }) {
  const response = await httpClient.post('/api/products', product, {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function updateProduct({ token, id, product }) {
  const response = await httpClient.put('/api/products/' + id, product, {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function deleteProduct({ token, id }) {
  await httpClient.delete('/api/products/' + id, {
    headers: authHeaders(token),
  });
}