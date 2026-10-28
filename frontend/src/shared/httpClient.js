import axios from 'axios';

const httpClient = axios.create({
  baseURL: 'http://127.0.0.1:5050',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default httpClient;