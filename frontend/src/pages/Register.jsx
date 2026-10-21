
import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import api from '../services/api.js'

export default function Register() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    hoTen: '',
    tenDangNhap: '',
    matKhau: '',
    email: '',
    sdt: '',
  })

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (
      !form.hoTen.trim() ||
      !form.tenDangNhap.trim() ||
      !form.matKhau ||
      !form.email.trim()
    ) {
      setError('Vui lòng nhập đầy đủ thông tin bắt buộc.')
      return
    }

    if (form.matKhau.length < 8) {
      setError('Mật khẩu phải có ít nhất 8 ký tự.')
      return
    }

    setLoading(true)

    try {
      await api.post('/auth/register', {
        hoTen: form.hoTen.trim(),
        tenDangNhap: form.tenDangNhap.trim(),
        matKhau: form.matKhau,
        email: form.email.trim(),
        sdt: form.sdt.trim(),
      })

      navigate('/login', {
        replace: true,
        state: {
          message: 'Đăng ký thành công! Vui lòng đăng nhập.',
        },
      })
    } catch (err) {
      const status = err.response?.status

      if (status === 409) {
        setError('Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.')
      } else if (status === 400) {
        setError(
          err.response?.data?.detail ||
          err.response?.data?.message ||
          'Thông tin đăng ký không hợp lệ.'
        )
      } else if (!err.response) {
        setError('Không thể kết nối máy chủ. Vui lòng thử lại.')
      } else {
        setError('Đăng ký thất bại. Vui lòng thử lại.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-heading">
          <h1>Đăng ký tài khoản</h1>
          <p>Tạo tài khoản khách hàng để sử dụng hệ thống quản lý tiệc cưới.</p>
        </div>

        {error && (
          <p className="auth-error" role="alert">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit}>
          <div className="register-field">
            <label htmlFor="hoTen">Họ và tên *</label>
            <input
              id="hoTen"
              name="hoTen"
              type="text"
              value={form.hoTen}
              onChange={handleChange}
              maxLength={100}
              autoComplete="name"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="tenDangNhap">Tên đăng nhập *</label>
            <input
              id="tenDangNhap"
              name="tenDangNhap"
              type="text"
              value={form.tenDangNhap}
              onChange={handleChange}
              minLength={4}
              maxLength={50}
              autoComplete="username"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="matKhau">Mật khẩu *</label>
            <input
              id="matKhau"
              name="matKhau"
              type="password"
              value={form.matKhau}
              onChange={handleChange}
              minLength={8}
              maxLength={72}
              autoComplete="new-password"
              required
            />
            <small>Mật khẩu cần có ít nhất 8 ký tự.</small>
          </div>

          <div className="register-field">
            <label htmlFor="email">Email *</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              maxLength={100}
              autoComplete="email"
              required
            />
          </div>

          <div className="register-field">
            <label htmlFor="sdt">Số điện thoại</label>
            <input
              id="sdt"
              name="sdt"
              type="tel"
              value={form.sdt}
              onChange={handleChange}
              maxLength={15}
              autoComplete="tel"
            />
          </div>

          <button
            className="auth-submit"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>

        <p className="register-link">
          Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
        </p>
      </section>
    </main>
  )
}
