import { useEffect, useState } from 'react'
import { getSession, clearSession } from './api'
import LoginPage from './components/LoginPage.jsx'
import RegisterPage from './components/RegisterPage.jsx'
import UserFeedbackPage from './components/UserFeedbackPage.jsx'
import AdminPage from './components/AdminPage.jsx'
import './App.css'

export default function App() {
  const stored = getSession()
  const [user, setUser] = useState(stored)
  const [page, setPage] = useState(() => (stored ? 'app' : 'login'))

  // Force logout when the backend rejects the JWT (401) on any API call.
  useEffect(() => {
    function onUnauthorized() {
      clearSession()
      setUser(null)
      setPage('login')
    }
    window.addEventListener('auth:unauthorized', onUnauthorized)
    return () => window.removeEventListener('auth:unauthorized', onUnauthorized)
  }, [])

  function handleSession(data) {
    setUser(data)
    setPage('app')
  }

  function handleLogout() {
    clearSession()
    setUser(null)
    setPage('login')
  }

  if (!user) {
    return page === 'register' ? (
      <RegisterPage
        onRegister={handleSession}
        onGoLogin={() => setPage('login')}
      />
    ) : (
      <LoginPage
        onLogin={handleSession}
        onGoRegister={() => setPage('register')}
      />
    )
  }

  return (
    <div className="app">
      <nav className="navbar">
        <span className="brand">Customer Feedback</span>
        <span className="nav-user">
          {user.name} <span className="badge">{user.role}</span>
        </span>
        <button type="button" className="btn nav" onClick={handleLogout}>
          Logout
        </button>
      </nav>

      {user.role === 'ADMIN' ? (
        <AdminPage />
      ) : (
        <UserFeedbackPage user={user} />
      )}
    </div>
  )
}