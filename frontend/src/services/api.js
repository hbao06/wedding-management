import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 5000,
})

export function getHealth() {
  return api.get('/health').then((response) => response.data)
}

export function getCurrentUser() {
  return api.get('/auth/me').then((response) => response.data)
}

export function searchPaymentBookings(q = '') {
  return api.get('/thanh-toan/phieu', { params: { q } }).then((response) => response.data)
}

export function previewPayment(maPhieu, ngayThanhToan) {
  return api.get(`/thanh-toan/phieu/${encodeURIComponent(maPhieu)}/xem-truoc`, {
    params: { ngayThanhToan },
  }).then((response) => response.data)
}

export function confirmPayment(maPhieu, ngayThanhToan, soTienCanThu) {
  return api.post('/thanh-toan/hoa-don', { maPhieu, ngayThanhToan, soTienCanThu })
    .then((response) => response.data)
}

export default api
