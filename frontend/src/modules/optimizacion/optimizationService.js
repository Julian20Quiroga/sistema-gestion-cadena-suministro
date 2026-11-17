import httpClient from '../../shared/httpClient';

const authHeaders = (token) => ({
  Authorization: 'Bearer ' + token,
});

export async function fetchWarehouses({ token }) {
  const response = await httpClient.get('/api/warehouses', {
    params: { page: 0, size: 100 },
    headers: authHeaders(token),
  });
  return response.data;
}

export async function fetchSnapshots({ token, warehouseId }) {
  const response = await httpClient.get('/api/warehouses/' + warehouseId + '/snapshots', {
    headers: authHeaders(token),
  });
  return response.data;
}

export async function createSnapshot({ token, warehouseId, snapshotName }) {
  const response = await httpClient.post(
    '/api/warehouses/' + warehouseId + '/snapshots',
    { snapshotName },
    { headers: authHeaders(token) }
  );
  return response.data;
}

export async function optimizeWarehouse({ token, warehouseId, autoSnapshot }) {
  const response = await httpClient.post(
    '/api/warehouses/' + warehouseId + '/optimize-layout',
    null,
    {
      params: { autoSnapshot },
      headers: authHeaders(token),
    }
  );
  return response.data;
}

export async function restoreSnapshot({ token, warehouseId, snapshotId }) {
  const response = await httpClient.post(
    '/api/warehouses/' + warehouseId + '/snapshots/' + snapshotId + '/restore',
    null,
    { headers: authHeaders(token) }
  );
  return response.data;
}
