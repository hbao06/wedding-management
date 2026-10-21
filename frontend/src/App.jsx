
import { useCallback, useEffect, useState } from 'react'
import {
  Database,
  HeartHandshake,
  RefreshCw,
  Server,
} from 'lucide-react'
import { Route, Routes, useNavigate } from 'react-router'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import { getHealth } from './services/api.js'
import api from './services/api.js'

function Dashboard() {
  const [nguoiDung, setNguoiDung] = useState(() => {
    try {
      return JSON.parse(
        sessionStorage.getItem('nguoiDung') || 'null'
      )
    } catch {
      return null
    }
  })

  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  const navigate = useNavigate()

  useEffect(() => {
    if (!nguoiDung) {
      navigate('/login', { replace: true })
    }
  }, [nguoiDung, navigate])

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

  useEffect(() => {
    refresh()
  }, [refresh])

  const connected =
    !loading && !error && health?.database === 'UP'

  const dangXuat = async () => {
    try {
      await api.post('/auth/logout')
    } catch (error) {
      console.error('Không thể gọi API đăng xuất:', error)
    } finally {
      sessionStorage.removeItem('nguoiDung')
      setNguoiDung(null)
      navigate('/login', { replace: true })
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">
            <HeartHandshake size={22} />
          </span>
          <span>Quản lý tiệc cưới</span>
        </div>

        <span className="environment">
          Môi trường phát triển
        </span>

        {nguoiDung && (
          <span className="environment">
            Xin chào, {nguoiDung.hoTen}
          </span>
        )}

        <button
          className="logout-button"
          onClick={dangXuat}
        >
          Đăng xuất
        </button>
      </header>

      <main className="main-content">
        <div className="page-heading">
          <div>
            <p className="eyebrow">TỔNG QUAN</p>
            <h1>Trạng thái hệ thống</h1>
          </div>

          <button
            className="refresh-button"
            onClick={refresh}
            disabled={loading}
            title="Kiểm tra lại kết nối"
            aria-label="Kiểm tra lại kết nối"
          >
            <RefreshCw
              size={18}
              className={loading ? 'spinning' : ''}
            />
          </button>
        </div>

        <div className="status-list" aria-live="polite">
          <section className="status-row">
            <div className="status-icon server-icon">
              <Server size={21} />
            </div>
            <div className="status-copy">
              <h2>Backend API</h2>
              <p>localhost:8080</p>
            </div>
            <span
              className={`status-label ${
                connected ? 'online' : error ? 'offline' : 'pending'
              }`}
            >
              {loading
                ? 'Đang kiểm tra'
                : connected
                  ? 'Đang chạy'
                  : 'Không kết nối'}
            </span>
          </section>

          <section className="status-row">
            <div className="status-icon database-icon">
              <Database size={21} />
            </div>
            <div className="status-copy">
              <h2>Cơ sở dữ liệu</h2>
              <p>wedding_db</p>
            </div>
            <span
              className={`status-label ${
                connected ? 'online' : error ? 'offline' : 'pending'
              }`}
            >
              {loading
                ? 'Đang kiểm tra'
                : connected
                  ? 'Đã kết nối'
                  : 'Chưa xác nhận'}
            </span>
          </section>
        </div>

        <div className="summary-band">
          <div>
            <span className="summary-label">
              SẢNH TRONG HỆ THỐNG
            </span>
            <strong>{connected ? health.soSanh : '—'}</strong>
          </div>

          <p>
            {error
              ? 'Không thể lấy dữ liệu từ Backend. Hãy kiểm tra Backend rồi thử lại.'
              : 'Dữ liệu được lấy trực tiếp từ MySQL qua API.'}
          </p>
        </div>
      </main>
    </div>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="*" element={<Dashboard />} />
    </Routes>
  )
}
