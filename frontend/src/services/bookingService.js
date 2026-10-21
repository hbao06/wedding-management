import api from './api.js'

export function layDanhSachSanh() {
  return api.get('/bookings/halls').then((response) => response.data)
}

export function taoPhieu(payload) {
  return api.post('/bookings', payload).then((response) => response.data)
}

export function layPhieu(maPhieu) {
  return api.get(`/bookings/${encodeURIComponent(maPhieu)}`).then((response) => response.data)
}
