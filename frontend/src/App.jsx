import { useCallback, useEffect, useState } from 'react'
import { Database, HeartHandshake, LogOut, RefreshCw, Server } from 'lucide-react'
import { Navigate, NavLink, Route, Routes, useNavigate } from 'react-router'
import Login from './pages/Login.jsx'
import Payment from './pages/Payment.jsx'
import api, { getCurrentUser, getHealth } from './services/api.js'
import Booking from './pages/Booking.jsx'
import BookingResult from './pages/BookingResult.jsx'

function Dashboard() {
  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const refresh = useCallback(async () => {
    setLoading(true)
    setError(false)
    try {
      setHealth(await getHealth())
    } catch {
      setHealth(null)
      setError(true)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { refresh() }, [refresh])
  const connected = !loading && !error && health?.database === 'UP'

  return (
    <main className="main-content">
      <div className="page-heading">
        <div><p className="eyebrow">TỔNG QUAN</p><h1>Trạng thái hệ thống</h1></div>
        <button className="refresh-button" onClick={refresh} disabled={loading} title="Kiểm tra lại kết nối" aria-label="Kiểm tra lại kết nối">
          <RefreshCw size={18} className={loading ? 'spinning' : ''} />
        </button>
      </div>
      <div className="status-list" aria-live="polite">
        <section className="status-row">
          <div className="status-icon server-icon"><Server size={21} /></div>
          <div className="status-copy"><h2>Backend API</h2><p>localhost:8080</p></div>
          <span className={`status-label ${connected ? 'online' : error ? 'offline' : 'pending'}`}>
            {loading ? 'Đang kiểm tra' : connected ? 'Đang chạy' : 'Không kết nối'}
          </span>
        </section>
        <section className="status-row">
          <div className="status-icon database-icon"><Database size={21} /></div>
          <div className="status-copy"><h2>Cơ sở dữ liệu</h2><p>wedding_db</p></div>
          <span className={`status-label ${connected ? 'online' : error ? 'offline' : 'pending'}`}>
            {loading ? 'Đang kiểm tra' : connected ? 'Đã kết nối' : 'Chưa xác nhận'}
          </span>
        </section>
      </div>
      <div className="summary-band">
        <div><span className="summary-label">SẢNH TRONG HỆ THỐNG</span><strong>{connected ? health.soSanh : '—'}</strong></div>
        <p>{error ? 'Không thể lấy dữ liệu từ Backend. Hãy kiểm tra tiến trình Backend rồi thử lại.' : 'Dữ liệu được lấy trực tiếp từ MySQL qua API.'}</p>
      </div>
    </main>
  )
}

function Workspace({ user, onLogout }) {
  const navigate = useNavigate()
  const [logoutError, setLogoutError] = useState(false)

  async function logout() {
    try {
      await api.post('/auth/logout')
      onLogout()
      navigate('/login', { replace: true })
    } catch {
      setLogoutError(true)
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true"><HeartHandshake size={22} /></span>
          <span>Quản lý tiệc cưới</span>
        </div>
        <nav className="topbar-nav" aria-label="Điều hướng chính">
          <NavLink to="/" end>Tổng quan</NavLink>
          {user.chucVu === 'Kế toán' && <NavLink to="/thanh-toan">Thanh toán</NavLink>}
        </nav>
        <div className="topbar-actions">
          <span className="environment">{user.hoTen}</span>
          {logoutError && <span className="logout-error" role="alert">Không thể đăng xuất</span>}
          <button className="logout-button" onClick={logout} title="Đăng xuất" aria-label="Đăng xuất"><LogOut size={18} /></button>
        </div>
      </header>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/thanh-toan" element={user.chucVu === 'Kế toán' ? <Payment /> : <Navigate to="/" replace />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  )
}

export default function App() {
  const [user, setUser] = useState(null)
  const [checking, setChecking] = useState(true)

  useEffect(() => {
    let active = true
    getCurrentUser().then((current) => {
      if (active) setUser(current)
    }).catch(() => {
      if (active) setUser(null)
    }).finally(() => {
      if (active) setChecking(false)
    })
    return () => { active = false }
  }, [])

  if (checking) return <div className="session-loading">Đang tải...</div>

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to={user.chucVu === 'Kế toán' ? '/thanh-toan' : '/'} replace /> : <Login onLogin={setUser} />} />
      <Route path="/booking" element={user ? <Booking /> : <Navigate to="/login" replace />} />
      <Route path="/booking-result/:maPhieu" element={user ? <BookingResult /> : <Navigate to="/login" replace />} />
      <Route path="*" element={user ? <Workspace user={user} onLogout={() => setUser(null)} /> : <Navigate to="/login" replace />} />
    </Routes>
  )
}