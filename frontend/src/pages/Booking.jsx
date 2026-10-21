import { useEffect, useMemo, useState } from 'react'
import { HeartHandshake } from 'lucide-react'
import { useNavigate } from 'react-router'
import { layDanhSachSanh, taoPhieu } from '../services/bookingService.js'
import './booking.css'

const CA_OPTIONS = [
  { value: 'SANG', label: 'Sáng' },
  { value: 'TRUA', label: 'Trưa' },
  { value: 'TOI', label: 'Tối' },
]

const formatTien = (value) => `${Number(value || 0).toLocaleString('vi-VN')} đ`

function ngayMai() {
  const date = new Date()
  date.setDate(date.getDate() + 1)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

const FORM_BAN_DAU = {
  maKH: '',
  maNV: '',
  maSanh: '',
  tenChuRe: '',
  tenCoDau: '',
  ngayDaiTiec: '',
  ca: 'TOI',
  soLuongBan: '',
  donGiaBan: '',
  tienDatCoc: '',
}

export default function Booking() {
  const navigate = useNavigate()
  const [danhSachSanh, setDanhSachSanh] = useState([])
  const [form, setForm] = useState(FORM_BAN_DAU)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    layDanhSachSanh()
      .then((data) => {
        setDanhSachSanh(data)
        if (data.length > 0) {
          setForm((prev) => ({
            ...prev,
            maSanh: data[0].maSanh,
            donGiaBan: String(data[0].donGiaToiThieu),
          }))
        }
      })
      .catch(() => setError('Không tải được danh sách sảnh. Hãy kiểm tra Backend rồi tải lại trang.'))
  }, [])

  const sanhDangChon = danhSachSanh.find((sanh) => sanh.maSanh === form.maSanh)
  const tongTien = useMemo(
    () => (Number(form.soLuongBan) || 0) * (Number(form.donGiaBan) || 0),
    [form.soLuongBan, form.donGiaBan],
  )

  function handleChange(event) {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  function handleChonSanh(event) {
    const maSanh = event.target.value
    const sanh = danhSachSanh.find((item) => item.maSanh === maSanh)
    setForm((prev) => ({
      ...prev,
      maSanh,
      donGiaBan: sanh ? String(sanh.donGiaToiThieu) : prev.donGiaBan,
    }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const phieu = await taoPhieu({
        maKH: form.maKH.trim(),
        maNV: form.maNV.trim(),
        maSanh: form.maSanh,
        tenChuRe: form.tenChuRe.trim(),
        tenCoDau: form.tenCoDau.trim(),
        ngayDaiTiec: form.ngayDaiTiec,
        ca: form.ca,
        soLuongBan: Number(form.soLuongBan),
        donGiaBan: Number(form.donGiaBan),
        tienDatCoc: Number(form.tienDatCoc),
      })
      navigate(`/booking-result/${phieu.maPhieu}`)
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể lập phiếu. Hãy kiểm tra dữ liệu và kết nối Backend.')
    } finally {
      setSubmitting(false)
    }
  }

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
            <h1>Lập phiếu đặt tiệc cưới</h1>
          </div>
        </div>

        {error && <div className="bk-error" role="alert">{error}</div>}

        <form onSubmit={handleSubmit}>
          <section className="bk-card">
            <h2>Thông tin cô dâu và chú rể</h2>
            <div className="bk-grid">
              <div className="bk-field">
                <label htmlFor="tenChuRe">Tên chú rể</label>
                <input id="tenChuRe" name="tenChuRe" required maxLength={100} value={form.tenChuRe} onChange={handleChange} />
              </div>
              <div className="bk-field">
                <label htmlFor="tenCoDau">Tên cô dâu</label>
                <input id="tenCoDau" name="tenCoDau" required maxLength={100} value={form.tenCoDau} onChange={handleChange} />
              </div>
            </div>
          </section>

          <section className="bk-card">
            <h2>Sảnh và thời gian tổ chức</h2>
            <div className="bk-grid">
              <div className="bk-field">
                <label htmlFor="maSanh">Sảnh</label>
                <select id="maSanh" name="maSanh" required value={form.maSanh} onChange={handleChonSanh}>
                  {danhSachSanh.map((sanh) => (
                    <option key={sanh.maSanh} value={sanh.maSanh}>
                      {sanh.tenSanh} (loại {sanh.loaiSanh})
                    </option>
                  ))}
                </select>
              </div>
              <div className="bk-field">
                <label htmlFor="ca">Ca đãi tiệc</label>
                <select id="ca" name="ca" value={form.ca} onChange={handleChange}>
                  {CA_OPTIONS.map((ca) => (
                    <option key={ca.value} value={ca.value}>{ca.label}</option>
                  ))}
                </select>
              </div>
              <div className="bk-field">
                <label htmlFor="ngayDaiTiec">Ngày đãi tiệc</label>
                <input id="ngayDaiTiec" name="ngayDaiTiec" type="date" required min={ngayMai()} value={form.ngayDaiTiec} onChange={handleChange} />
              </div>
              <div className="bk-field">
                <label htmlFor="soLuongBan">Số bàn</label>
                <input id="soLuongBan" name="soLuongBan" type="number" required min="1" max={sanhDangChon?.soBanToiDa} value={form.soLuongBan} onChange={handleChange} />
                {sanhDangChon && <span className="bk-hint">Tối đa {sanhDangChon.soBanToiDa} bàn</span>}
              </div>
              <div className="bk-field">
                <label htmlFor="donGiaBan">Đơn giá mỗi bàn (đ)</label>
                <input id="donGiaBan" name="donGiaBan" type="number" required min={sanhDangChon?.donGiaToiThieu || 0} value={form.donGiaBan} onChange={handleChange} />
                {sanhDangChon && <span className="bk-hint">Tối thiểu {formatTien(sanhDangChon.donGiaToiThieu)}</span>}
              </div>
            </div>
          </section>

          <section className="bk-card">
            <h2>Tổng kết và đặt cọc</h2>
            <div className="bk-total-row">
              <span>Tổng tiền dự kiến (chưa gồm dịch vụ)</span>
              <strong>{formatTien(tongTien)}</strong>
            </div>
            <div className="bk-grid">
              <div className="bk-field">
                <label htmlFor="tienDatCoc">Tiền đặt cọc (đ)</label>
                <input id="tienDatCoc" name="tienDatCoc" type="number" required min="0" value={form.tienDatCoc} onChange={handleChange} />
                <span className="bk-hint">Backend kiểm tra tỉ lệ cọc tối thiểu theo quy định của nhà hàng</span>
              </div>
            </div>
          </section>

          <section className="bk-card">
            <h2>Tạm thời: mã khách hàng và nhân viên</h2>
            <p className="bk-note">
              Hai mã này sẽ lấy tự động khi tích hợp đăng nhập và tài khoản khách hàng. Hiện nhập tay để thử.
            </p>
            <div className="bk-grid">
              <div className="bk-field">
                <label htmlFor="maKH">Mã khách hàng</label>
                <input id="maKH" name="maKH" required maxLength={10} value={form.maKH} onChange={handleChange} />
              </div>
              <div className="bk-field">
                <label htmlFor="maNV">Mã nhân viên lập phiếu</label>
                <input id="maNV" name="maNV" required maxLength={10} value={form.maNV} onChange={handleChange} />
              </div>
            </div>
          </section>

          <div className="bk-actions">
            <button type="button" className="bk-button secondary" onClick={() => setForm(FORM_BAN_DAU)}>
              Hủy
            </button>
            <button type="submit" className="bk-button" disabled={submitting || danhSachSanh.length === 0}>
              {submitting ? 'Đang lập phiếu...' : 'Xác nhận lập phiếu'}
            </button>
          </div>
        </form>
      </main>
    </div>
  )
}
