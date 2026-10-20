
import { useState } from 'react'
import { useNavigate } from 'react-router'
import { HeartHandshake, LockKeyhole, UserRound } from 'lucide-react'
import api from '../services/api.js'

export default function Login() {
  const navigate = useNavigate()

  const [tenDangNhap, setTenDangNhap] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await api.post('/auth/login', {
        tenDangNhap,
        matKhau,
      })

      // Lưu thông tin người dùng sau khi đăng nhập thành công.
      sessionStorage.setItem('nguoiDung', JSON.stringify(response.data))
      navigate('/')
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Tên đăng nhập hoặc mật khẩu không đúng.')
      } else if (err.response?.status === 400) {
        setError('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.')
      } else {
        setError('Không thể kết nối máy chủ. Vui lòng thử lại.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-card">
        <div className="login-logo">
          <HeartHandshake size={32} />
        </div>

        <h1>Quản lý tiệc cưới</h1>
        <p className="login-description">
          Đăng nhập để tiếp tục
        </p>

        <form onSubmit={handleSubmit}>
          <label htmlFor="tenDangNhap">Tên đăng nhập</label>
          <div className="login-input">
            <UserRound size={18} />
            <input
              id="tenDangNhap"
              type="text"
              placeholder="Nhập tên đăng nhập"
              value={tenDangNhap}
              onChange={(event) => setTenDangNhap(event.target.value)}
              autoComplete="username"
              required
            />
          </div>

          <label htmlFor="matKhau">Mật khẩu</label>
          <div className="login-input">
            <LockKeyhole size={18} />
            <input
              id="matKhau"
              type="password"
              placeholder="Nhập mật khẩu"
              value={matKhau}
              onChange={(event) => setMatKhau(event.target.value)}
              autoComplete="current-password"
              required
            />
          </div>

          {error && (
            <p className="login-error" role="alert">{error}</p>
          )}

          <button
            className="login-submit"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <p className="login-footer">
          Hệ thống quản lý tiệc cưới
        </p>
      </section>
    </main>
  )
}
