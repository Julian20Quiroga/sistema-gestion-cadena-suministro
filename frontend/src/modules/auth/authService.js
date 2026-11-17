import httpClient from '../../shared/httpClient';

export const loginRequest = async (credentials) => {
  const response = await httpClient.post('/api/auth/login', credentials);

  return response.data;
};