import { useCallback, useEffect, useState } from 'react'
import { Check, Printer, Search } from 'lucide-react'
import { confirmPayment, previewPayment, searchPaymentBookings } from '../services/api.js'

const money = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 })

function todayLocal() {
  const date = new Date()
  const part = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${part(date.getMonth() + 1)}-${part(date.getDate())}`
}

function messageFor(error) {
  if (error.response?.status === 401) return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'
  if (error.response?.status === 403) return 'Tài khoản này không có quyền thanh toán.'
  if (error.response?.status === 404) return 'Không tìm thấy phiếu đặt tiệc.'
  if (error.response?.status === 409) return 'Thông tin thanh toán đã thay đổi hoặc phiếu không còn hợp lệ. Hãy kiểm tra lại.'
  if (error.response?.status === 400) return 'Ngày thanh toán không hợp lệ.'
  return 'Không thể kết nối máy chủ. Vui lòng thử lại.'
}

export default function Payment() {
  const [query, setQuery] = useState('')
  const [bookings, setBookings] = useState([])
  const [selected, setSelected] = useState(null)
  const [paymentDate, setPaymentDate] = useState(todayLocal)
  const [preview, setPreview] = useState(null)
  const [loading, setLoading] = useState(true)
  const [previewLoading, setPreviewLoading] = useState(false)
  const [confirming, setConfirming] = useState(false)
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [error, setError] = useState('')
  const [detailError, setDetailError] = useState('')

  const search = useCallback(async (value) => {
    setLoading(true)
    setError('')
    try {
      setBookings(await searchPaymentBookings(value))
    } catch (requestError) {
      setError(messageFor(requestError))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { search('') }, [search])

  useEffect(() => {
    if (!selected || !paymentDate) return
    let active = true
    setPreviewLoading(true)
    setPreview(null)
    setDetailError('')
    previewPayment(selected.maPhieu, paymentDate).then((result) => {
      if (active) setPreview(result)
    }).catch((requestError) => {
      if (active) setDetailError(messageFor(requestError))
    }).finally(() => {
      if (active) setPreviewLoading(false)
    })
    return () => { active = false }
  }, [selected, paymentDate])

  async function confirm() {
    if (!preview || confirming) return
    setConfirming(true)
    setDetailError('')
    try {
      const result = await confirmPayment(preview.maPhieu, paymentDate, preview.soTienCanThu)
      setPreview(result)
      setSelected((current) => ({ ...current, daThanhToan: true }))
      setBookings((current) => current.map((item) => item.maPhieu === result.maPhieu
        ? { ...item, daThanhToan: true } : item))
      setConfirmOpen(false)
    } catch (requestError) {
      setDetailError(messageFor(requestError))
      setConfirmOpen(false)
      if (requestError.response?.status === 409) {
        try {
          const refreshed = await previewPayment(preview.maPhieu, paymentDate)
          setPreview(refreshed)
          if (refreshed.daThanhToan) {
            setSelected((current) => ({ ...current, daThanhToan: true }))
            setBookings((current) => current.map((item) => item.maPhieu === refreshed.maPhieu
              ? { ...item, daThanhToan: true } : item))
          }
        } catch {
          setPreview(null)
        }
      }
    } finally {
      setConfirming(false)
    }
  }

  return (
    <main className="payment-main">
      <div className="page-heading">
        <div><p className="eyebrow">KẾ TOÁN</p><h1>Thanh toán tiệc cưới</h1></div>
      </div>
      <form className="payment-search" onSubmit={(event) => { event.preventDefault(); search(query) }}>
        <label htmlFor="payment-query">Tìm phiếu đặt tiệc</label>
        <div className="payment-search-row">
          <input id="payment-query" value={query} onChange={(event) => setQuery(event.target.value)}
            placeholder="Mã phiếu hoặc tên cô dâu, chú rể" maxLength={100} />
          <button type="submit" disabled={loading}><Search size={18} /> Tìm</button>
        </div>
      </form>
      {error && <p className="payment-error" role="alert">{error}</p>}
      <div className="payment-grid">
        <section className="payment-list" aria-label="Danh sách phiếu">
          <div className="section-heading"><h2>Phiếu đặt tiệc</h2><span>{bookings.length} kết quả</span></div>
          {loading ? <p className="payment-empty">Đang tải...</p> : bookings.length === 0
            ? <p className="payment-empty">Không có phiếu phù hợp.</p>
            : bookings.map((item) => (
              <button key={item.maPhieu} type="button"
                className={`payment-row ${selected?.maPhieu === item.maPhieu ? 'selected' : ''}`}
                onClick={() => { setSelected(item); setPaymentDate(todayLocal()); setConfirmOpen(false) }}>
                <span><strong>{item.maPhieu}</strong><small>{item.tenChuRe} &amp; {item.tenCoDau}</small></span>
                <span className="payment-row-side"><time>{item.ngayDaiTiec}</time><small className={item.daThanhToan ? 'paid' : 'unpaid'}>{item.daThanhToan ? 'Đã thanh toán' : 'Chưa thanh toán'}</small></span>
              </button>
            ))}
        </section>
        <section className="payment-detail" aria-label="Chi tiết thanh toán">
          <div className="section-heading"><h2>Chi tiết thanh toán</h2></div>
          {!selected ? <p className="payment-empty">Chọn một phiếu để xem số tiền cần thu.</p> : <>
            <div className="payment-ident"><strong>{selected.tenChuRe} &amp; {selected.tenCoDau}</strong><span>Mã phiếu {selected.maPhieu}</span></div>
            <label className="payment-date-label" htmlFor="payment-date">Ngày thanh toán</label>
            <input id="payment-date" type="date" value={preview?.daThanhToan ? preview.ngayThanhToan : paymentDate}
              min={selected.ngayDaiTiec} max={todayLocal()} disabled={selected.daThanhToan}
              onChange={(event) => { setPaymentDate(event.target.value); setConfirmOpen(false) }} />
            {previewLoading && <p className="payment-empty">Đang tính...</p>}
            {detailError && <p className="payment-error" role="alert">{detailError}</p>}
            {preview && <>
              <dl className="payment-breakdown">
                <div><dt>Tổng tiền hóa đơn</dt><dd>{money.format(preview.tongTienHoaDon)}</dd></div>
                <div><dt>Tiền đặt cọc</dt><dd>-{money.format(preview.tienDatCoc)}</dd></div>
                <div><dt>Phạt trễ hạn ({preview.soNgayTre} ngày)</dt><dd>{money.format(preview.tienPhatTreHan)}</dd></div>
                <div className="payment-total"><dt>{preview.daThanhToan ? 'Số tiền đã thu' : 'Số tiền cần thu'}</dt><dd>{money.format(preview.soTienCanThu)}</dd></div>
              </dl>
              {preview.daThanhToan ? <div className="payment-receipt-actions">
                  <p className="payment-success"><Check size={18} /> Đã thanh toán · Hóa đơn {preview.maHD}</p>
                  <button type="button" className="payment-secondary" onClick={() => window.print()} title="In hóa đơn hoặc lưu PDF"><Printer size={17} /> In hóa đơn</button>
                </div>
                : confirmOpen ? <div className="payment-confirm">
                    <p>Xác nhận đã nhận {money.format(preview.soTienCanThu)}?</p>
                    <div><button type="button" className="payment-secondary" onClick={() => setConfirmOpen(false)} disabled={confirming}>Hủy</button>
                      <button type="button" className="payment-primary" onClick={confirm} disabled={confirming}>{confirming ? 'Đang lưu...' : 'Xác nhận đã thu'}</button></div>
                  </div>
                : <button type="button" className="payment-primary payment-full" onClick={() => setConfirmOpen(true)}>Xác nhận thanh toán</button>}
            </>}
          </>}
        </section>
      </div>
    </main>
  )
}
