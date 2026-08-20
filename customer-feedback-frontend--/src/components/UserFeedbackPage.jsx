import { useEffect, useState } from 'react'
import { api } from '../api'

export default function UserFeedbackPage({ user }) {
  const [feedback, setFeedback] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  // form state
  const [editing, setEditing] = useState(null)
  const [category, setCategory] = useState('Product')
  const [rating, setRating] = useState(5)
  const [text, setText] = useState('')

  async function load() {
    try {
      const data = await api.get('/feedback/' + user.id)
      setFeedback(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let ignore = false
    ;(async () => {
      try {
        const data = await api.get('/feedback/' + user.id)
        if (!ignore) {
          setFeedback(data)
          setLoading(false)
        }
      } catch (err) {
        if (!ignore) {
          setError(err.message)
          setLoading(false)
        }
      }
    })()

    return () => {
      ignore = true
    }
  }, [user.id])

  function resetForm() {
    setEditing(null)
    setCategory('Product')
    setRating(5)
    setText('')
  }

  function startEdit(f) {
    setEditing(f)
    setCategory(f.category)
    setRating(f.rating)
    setText(f.message)
    setMessage('')
    setError('')
  }

  function cancelEdit() {
    resetForm()
    setMessage('')
    setError('')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!text.trim()) {
      setError('Please write a short feedback message.')
      return
    }
    setError('')
    setMessage('')
    try {
      const payload = { category, rating, message: text }
      if (editing) {
        await api.put('/feedback/' + editing.id + '?userId=' + user.id, payload)
        setMessage('Feedback updated.')
      } else {
        await api.post('/feedback?userId=' + user.id, payload)
        setMessage('Feedback submitted. Thank you!')
      }
      await load()
      resetForm()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDelete(f) {
    if (!window.confirm('Delete this feedback?')) return
    setError('')
    setMessage('')
    try {
      await api.del('/feedback/' + f.id + '?userId=' + user.id)
      setMessage('Feedback deleted.')
      await load()
      if (editing && editing.id === f.id) resetForm()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <p className="muted">Loading your feedback…</p>

  return (
    <div className="page">
      <h1>Your Feedback</h1>
      <p className="subtitle">
        {feedback.length > 0
          ? 'You have previously submitted feedback. Edit it below, or add another.'
          : 'You have not submitted any feedback yet. Use the form below to add your first piece of feedback.'}
      </p>

      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="card">
        <h2>{editing ? `Editing feedback #${editing.id}` : 'Add / Edit Feedback'}</h2>
        <form className="form" onSubmit={handleSubmit}>
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
              rows="4"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Tell us what you think…"
              required
            />
          </label>

          <div className="actions">
            <button className="btn primary" type="submit">
              {editing ? 'Save Changes' : 'Submit Feedback'}
            </button>
            {editing && (
              <button className="btn" type="button" onClick={cancelEdit}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card">
        <h2>Previously Submitted</h2>
        {feedback.length === 0 ? (
          <p className="muted">No feedback submitted yet.</p>
        ) : (
          <ul className="feedback-list">
            {feedback.map((f) => (
              <li key={f.id} className="feedback-item">
                <div className="feedback-head">
                  <span className="badge">{f.category}</span>
                  <span className="stars">{'★'.repeat(f.rating)}</span>
                  <span className="muted date">#{f.id}</span>
                </div>
                <p>{f.message}</p>
                <div className="actions">
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
                    onClick={() => handleDelete(f)}
                  >
                    Delete
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}