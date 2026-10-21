import { useEffect, useState } from 'react'
import { HeartHandshake } from 'lucide-react'
import { useNavigate, useParams } from 'react-router'
import { layPhieu } from '../services/bookingService.js'
import './booking.css'

const CA_LABEL = { SANG: 'Sáng', TRUA: 'Trưa', TOI: 'Tối' }

const formatTien = (value) => `${Number(value || 0).toLocaleString('vi-VN')} đ`

function formatNgay(value) {
  if (!value) return ''
  const [year, month, day] = value.split('-')
  return `${day}/${month}/${year}`
}

export default function BookingResult() {
  const { maPhieu } = useParams()
  const navigate = useNavigate()
  const [phieu, setPhieu] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    layPhieu(maPhieu)
      .then(setPhieu)
      .catch((err) => setError(err.response?.data?.message || 'Không tải được phiếu đặt tiệc.'))
      .finally(() => setLoading(false))
  }, [maPhieu])

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true"><HeartHandshake size={22} /></span>
          <span>Quản lý tiệc cưới</span>
        </div>
        <span className="environment">Môi trường phát triển</span>
      </header>

      <main className="main-content">
        <div className="page-heading">
          <div>
            <p className="eyebrow">NGHIỆP VỤ / ĐẶT TIỆC</p>
            <h1>Kết quả lập phiếu</h1>
          </div>
        </div>

        {loading && <p>Đang tải phiếu...</p>}
        {error && <div className="bk-error" role="alert">{error}</div>}

        {phieu && (
          <section className="bk-card">
            <h2>Phiếu {phieu.maPhieu}</h2>
            <div className="bk-result-row"><span>Chú rể</span><span>{phieu.tenChuRe}</span></div>
            <div className="bk-result-row"><span>Cô dâu</span><span>{phieu.tenCoDau}</span></div>
            <div className="bk-result-row"><span>Sảnh</span><span>{phieu.tenSanh}</span></div>
            <div className="bk-result-row">
              <span>Ngày và ca</span>
              <span>{formatNgay(phieu.ngayDaiTiec)} - {CA_LABEL[phieu.ca] || phieu.ca}</span>
            </div>
            <div className="bk-result-row"><span>Số bàn</span><span>{phieu.soLuongBan}</span></div>
            <div className="bk-result-row"><span>Đơn giá mỗi bàn</span><span>{formatTien(phieu.donGiaBan)}</span></div>
            <div className="bk-result-row"><span>Tổng tiền dự kiến</span><strong>{formatTien(phieu.tongTienDuKien)}</strong></div>
            <div className="bk-result-row"><span>Tiền đã cọc</span><span>{formatTien(phieu.tienDatCoc)}</span></div>
            <div className="bk-result-row"><span>Số tiền còn lại</span><strong>{formatTien(phieu.soTienConLai)}</strong></div>
            <div className="bk-result-row"><span>Trạng thái</span><span>{phieu.trangThai}</span></div>
          </section>
        )}

        <div className="bk-actions">
          <button type="button" className="bk-button secondary" onClick={() => navigate('/booking')}>
            Lập phiếu mới
          </button>
          {phieu && (
            <button type="button" className="bk-button" onClick={() => window.print()}>
              In phiếu
            </button>
          )}
        </div>
      </main>
    </div>
  )
}
