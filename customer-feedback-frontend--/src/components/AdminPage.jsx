import { useEffect, useState } from 'react'
import { api } from '../api'

export default function AdminPage() {
  const [tab, setTab] = useState('feedback')
  const [feedback, setFeedback] = useState([])
  const [users, setUsers] = useState([])
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)

  // feedback form state
  const [editing, setEditing] = useState(null)
  const [category, setCategory] = useState('Product')
  const [rating, setRating] = useState(5)
  const [text, setText] = useState('')
  const [userId, setUserId] = useState('')

  async function load() {
    try {
      const fb = await api.get('/admin/feedback')
      const us = await api.get('/admin/users')
      setFeedback(fb)
      setUsers(us)
      if (!userId && us.length) setUserId(String(us[0].id))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let ignore = false;
    (async () => {
      try {
        const fb = await api.get('/admin/feedback')
        const us = await api.get('/admin/users')
        if (!ignore) {
          setFeedback(fb)
          setUsers(us)
          if (us.length) setUserId(String(us[0].id))
        }
      } catch (err) {
        if (!ignore) {
          setError(err.message)
        }
      } finally {
        if (!ignore) setLoading(false)
      }
    })()
    return () => {
      ignore = true
    }
  }, [])

  function resetForm() {
    setEditing(null)
    setCategory('Product')
    setRating(5)
    setText('')
    setUserId(users.length ? String(users[0].id) : '')
  }

  function startEdit(f) {
    setEditing(f)
    setCategory(f.category)
    setRating(f.rating)
    setText(f.message)
    setError('')
    setMessage('')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!text.trim()) {
      setError('Please write a feedback message.')
      return
    }
    if (!editing && !userId) {
      setError('Select a user to attach this feedback to.')
      return
    }
    setError('')
    setMessage('')
    try {
      const payload = { category, rating, message: text }
      if (editing) {
        await api.put('/admin/feedback/' + editing.id, payload)
        setMessage('Feedback updated.')
      } else {
        await api.post('/admin/feedback?userId=' + encodeURIComponent(userId), payload)
        setMessage('Feedback created.')
      }
      await load()
      resetForm()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDeleteFeedback(f) {
    if (!window.confirm(`Delete feedback #${f.id}?`)) return
    try {
      await api.del('/admin/feedback/' + f.id)
      setMessage('Feedback deleted.')
      await load()
      if (editing && editing.id === f.id) resetForm()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDeleteUser(u) {
    if (!window.confirm(`Delete user "${u.name}" and all their feedback?`)) return
    try {
      await api.del('/admin/users/' + u.id)
      setMessage(`User "${u.name}" deleted.`)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <p className="muted">Loading admin dashboard…</p>

  return (
    <div className="page">
      <h1>Admin Dashboard</h1>

      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="tabs">
        <button
          type="button"
          className={tab === 'feedback' ? 'tab active' : 'tab'}
          onClick={() => setTab('feedback')}
        >
          Feedback ({feedback.length})
        </button>
        <button
          type="button"
          className={tab === 'users' ? 'tab active' : 'tab'}
          onClick={() => setTab('users')}
        >
          Users ({users.length})
        </button>
      </div>

      {tab === 'feedback' && (
        <>
          <div className="card">
            <h2>{editing ? `Edit Feedback #${editing.id}` : 'Create Feedback'}</h2>
            <form className="form" onSubmit={handleSubmit}>
              {!editing && (
                <label>
                  For user
                  <select value={userId} onChange={(e) => setUserId(e.target.value)}>
                    {users.map((u) => (
                      <option key={u.id} value={u.id}>
                        {u.name} ({u.email})
                      </option>
                    ))}
                  </select>
                </label>
              )}
              <label>
                Category
                <select value={category} onChange={(e) => setCategory(e.target.value)}>
                  <option>Product</option>
                  <option>Service</option>
                  <option>Support</option>
                  <option>Website</option>
                  <option>Other</option>
                </select>
              </label>
              <label>
                Rating ({rating}/5)
                <input
                  type="range"
                  min="1"
                  max="5"
                  value={rating}
                  onChange={(e) => setRating(Number(e.target.value))}
                />
              </label>
              <label>
                Message
                <textarea
                  rows="3"
                  value={text}
                  onChange={(e) => setText(e.target.value)}
                  required
                />
              </label>
              <div className="actions">
                <button className="btn primary" type="submit">
                  {editing ? 'Save Changes' : 'Create Feedback'}
                </button>
                {editing && (
                  <button className="btn" type="button" onClick={resetForm}>
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="card">
            <h2>All Feedback</h2>
            {feedback.length === 0 ? (
              <p className="muted">No feedback submitted yet.</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Category</th>
                    <th>Rating</th>
                    <th>Message</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {feedback.map((f) => (
                    <tr key={f.id}>
                      <td>{f.id}</td>
                      <td>
                        {f.userName}
                        <div className="muted">{f.userEmail}</div>
                      </td>
                      <td>{f.category}</td>
                      <td>{'★'.repeat(f.rating)}</td>
                      <td className="cell-message">{f.message}</td>
                      <td>
                        <div className="cell-actions">
                          <button
                            className="btn"
                            type="button"
                            onClick={() => startEdit(f)}
                          >
                            Edit
                          </button>
                          <button
                            className="btn danger"
                            type="button"
                            onClick={() => handleDeleteFeedback(f)}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      {tab === 'users' && (
        <div className="card">
          <h2>Registered Users</h2>
          {users.length === 0 ? (
            <p className="muted">No registered users.</p>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.name}</td>
                    <td>{u.email}</td>
                    <td>
                      <span className="badge">{u.role}</span>
                    </td>
                    <td>
                      <button
                        className="btn danger"
                        type="button"
                        onClick={() => handleDeleteUser(u)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  )
}