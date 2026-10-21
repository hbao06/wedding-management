import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 5000,
  withCredentials: true,
})

export function getHealth() {
  return api.get('/health').then((response) => response.data)
}

export default api