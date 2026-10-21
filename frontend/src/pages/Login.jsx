
import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import api from '../services/api.js'

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()

  const [tenDangNhap, setTenDangNhap] = useState('')
  const [matKhau, setMatKhau] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const message = location.state?.message

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await api.post('/auth/login', {
        tenDangNhap,
        matKhau,
      })

      sessionStorage.setItem(
        'nguoiDung',
        JSON.stringify(response.data)
      )

      navigate('/', { replace: true })
    } catch (err) {
      const status = err.response?.status

      if (status === 401) {
        setError('Tên đăng nhập hoặc mật khẩu không đúng.')
      } else if (status === 400) {
        setError('Vui lòng kiểm tra lại thông tin đăng nhập.')
      } else if (!err.response) {
        setError('Không thể kết nối máy chủ. Hãy kiểm tra Backend.')
      } else {
        setError('Đăng nhập thất bại. Vui lòng thử lại.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-heading">
          <h1>Đăng nhập</h1>
          <p>Quản lý tiệc cưới</p>
        </div>

        {message && (
          <p className="auth-success" role="status">
            {message}
          </p>
        )}

        {error && (
          <p className="auth-error" role="alert">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          <div className="register-field">
            <label htmlFor="tenDangNhap">
              Tên đăng nhập
            </label>
            <input
              id="tenDangNhap"
              type="text"
              value={tenDangNhap}
              onChange={(e) => setTenDangNhap(e.target.value)}
              autoComplete="username"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="matKhau">Mật khẩu</label>
            <input
              id="matKhau"
              type="password"
              value={matKhau}
              onChange={(e) => setMatKhau(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>

          <button
            className="auth-submit"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <p className="register-link">
          Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
        </p>
      </section>
    </main>
  )
}
