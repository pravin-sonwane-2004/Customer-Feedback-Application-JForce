// Lightweight API client for the Customer Feedback backend.
const API_BASE = 'http://localhost:8080/api'

// The logged-in user session (including the JWT token and role) is stored in sessionStorage.
export function getSession() {
  try {
    return JSON.parse(sessionStorage.getItem('session'))
  } catch {
    return null
  }
}

export function setSession(user) {
  sessionStorage.setItem('session', JSON.stringify(user))
}

export function clearSession() {
  sessionStorage.removeItem('session')
}

// Adds "Authorization: Bearer <token>" to every request when a session exists.
function authHeaders() {
  const session = getSession()
  return session && session.token ? { Authorization: 'Bearer ' + session.token } : {}
}

async function request(path, { method = 'GET', body } = {}) {
  const headers = { ...authHeaders() }
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  let res
  try {
    res = await fetch(API_BASE + path, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new Error(
      'Cannot reach the server.',
    )
  }

  // Token missing, expired or invalid -> forget the session and go back to login.
  if (res.status === 401) {
    clearSession()
    window.dispatchEvent(new Event('auth:unauthorized'))
  }

  if (res.status === 204) return null
  const text = await res.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }
  if (!res.ok) {
    const detail = data && data.error ? data.error : data && data.message ? data.message : ''
    throw new Error(detail || `Request failed (${res.status})`)
  }
  return data
}

export const api = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body }),
  put: (path, body) => request(path, { method: 'PUT', body }),
  del: (path) => request(path, { method: 'DELETE' }),
}