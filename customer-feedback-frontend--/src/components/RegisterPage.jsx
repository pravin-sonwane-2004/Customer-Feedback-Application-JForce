import { useState } from 'react'
import { api, setSession } from '../api'

export default function RegisterPage({ onRegister, onGoLogin }) {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    if (password.length < 6) {
      setError('Password must be at least 6 characters.')
      return
    }
    setLoading(true)
    try {
      // Registration returns the safe user record (no password).
      await api.post('/auth/register', { name, email, password })
      // Log the new account in right away so the session includes the JWT.
      const data = await api.post('/auth/login', { email, password })
      setSession(data)
      onRegister(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>Create Account</h1>
      <p className="subtitle">Register to submit feedback</p>

      <form className="form" onSubmit={handleSubmit}>
        <label>
          Full name
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Pravin Sonwane"
            required
          />
        </label>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 6 characters"
            required
          />
        </label>

        {error && <div className="alert error">{error}</div>}

        <button className="btn primary full" type="submit" disabled={loading}>
          {loading ? 'Creating account…' : 'Register'}
        </button>
      </form>

      <p className="switch">
        Already have an account?{' '}
        <button type="button" className="link" onClick={onGoLogin}>
          Log in
        </button>
      </p>
    </div>
  )
}