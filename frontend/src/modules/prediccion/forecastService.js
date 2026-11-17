import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({
  Authorization: 'Bearer ' + token,
});

export async function fetchForecastStrategies({ token }) {
  const response = await httpClient.get('/api/forecast/strategies', {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchProductForecast({ token, productId, algorithm, periodsAhead }) {
  const response = await httpClient.get('/api/forecast/products/' + productId, {
    params: { algorithm, periodsAhead },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function generateForecast({ token, request }) {
  const response = await httpClient.post('/api/forecast/generate', request, {
    headers: authHeaders(token),
  });
  return response.data;
}
