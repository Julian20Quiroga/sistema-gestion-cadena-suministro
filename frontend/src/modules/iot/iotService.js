import httpClient from '../../shared/httpClient';

export async function fetchIotVehicles({ token }) {
  const response = await httpClient.get('/api/vehicles', {
    params: { status: 'ACTIVE', page: 0, size: 100 },
    headers: { Authorization: 'Bearer ' + token },
  });
  return response.data;
}

export async function sendTelemetry({ token, apiKey, reading }) {
  const response = await httpClient.post('/api/tracking/telemetry', reading, {
    headers: {
      Authorization: 'Bearer ' + token,
      'X-Vehicle-Api-Key': apiKey.trim(),
    },
  });
  return response.data;
}
