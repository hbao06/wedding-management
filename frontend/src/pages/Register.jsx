import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { HeartHandshake } from 'lucide-react'
import api from '../services/api.js'

const initialForm = {
  hoTen: '', tenDangNhap: '', matKhau: '', email: '', sdt: '',
  hoTenChuRe: '', hoTenCoDau: '',
}

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState(initialForm)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  function update(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function submit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      await api.post('/auth/register', {
        ...form,
        hoTen: form.hoTen.trim(),
        tenDangNhap: form.tenDangNhap.trim(),
        email: form.email.trim(),
        sdt: form.sdt.trim(),
        hoTenChuRe: form.hoTenChuRe.trim(),
        hoTenCoDau: form.hoTenCoDau.trim(),
      })
      navigate('/login', { replace: true,
        state: { message: 'Đăng ký thành công. Vui lòng đăng nhập.' } })
    } catch (requestError) {
      const status = requestError.response?.status
      setError(status === 409 ? 'Tên đăng nhập đã tồn tại.'
        : status === 400 ? 'Thông tin đăng ký không hợp lệ.'
        : 'Không thể đăng ký. Vui lòng thử lại.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="login-page register-page">
      <section className="login-card register-card">
        <div className="login-logo"><HeartHandshake size={32} /></div>
        <h1>Đăng ký tài khoản</h1>
        <p className="login-description">Tài khoản khách hàng</p>
        <form onSubmit={submit}>
          <label htmlFor="hoTen">Họ và tên</label>
          <input id="hoTen" name="hoTen" value={form.hoTen} onChange={update}
            maxLength={100} autoComplete="name" required />
          <div className="register-columns">
            <div><label htmlFor="hoTenChuRe">Tên chú rể</label>
              <input id="hoTenChuRe" name="hoTenChuRe" value={form.hoTenChuRe}
                onChange={update} maxLength={100} required /></div>
            <div><label htmlFor="hoTenCoDau">Tên cô dâu</label>
              <input id="hoTenCoDau" name="hoTenCoDau" value={form.hoTenCoDau}
                onChange={update} maxLength={100} required /></div>
          </div>
          <label htmlFor="registerUsername">Tên đăng nhập</label>
          <input id="registerUsername" name="tenDangNhap" value={form.tenDangNhap}
            onChange={update} minLength={4} maxLength={50} autoComplete="username" required />
          <label htmlFor="registerPassword">Mật khẩu</label>
          <input id="registerPassword" name="matKhau" type="password" value={form.matKhau}
            onChange={update} minLength={8} maxLength={72} autoComplete="new-password" required />
          <label htmlFor="registerEmail">Email</label>
          <input id="registerEmail" name="email" type="email" value={form.email}
            onChange={update} maxLength={100} autoComplete="email" required />
          <label htmlFor="registerPhone">Số điện thoại</label>
          <input id="registerPhone" name="sdt" type="tel" value={form.sdt}
            onChange={update} maxLength={15} autoComplete="tel" />
          {error && <p className="login-error" role="alert">{error}</p>}
          <button className="login-submit" type="submit" disabled={loading}>
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>
        <p className="register-link">Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
      </section>
    </main>
  )
}
