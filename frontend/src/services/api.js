import { useAuthStore } from '../stores/auth'

/**
 * Interceptor HTTP para adicionar JWT token automaticamente
 */
export class ApiClient {
  constructor(baseURL = import.meta.env.DEV ? 'http://localhost:8080/csonline/api' : '/csonline/api') {
    this.baseURL = baseURL
  }

  async request(endpoint, options = {}) {
    const authStore = useAuthStore()
    
    // Validate auth before making request
    if (authStore.token && authStore.isTokenExpired()) {
      authStore.clearAuth()
      throw new Error('Token expired. Please login again.')
    }

    const url = `${this.baseURL}${endpoint}`
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    }

    // Add Authorization header if user is authenticated
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }

    console.log(`[API] ${config.method || 'GET'} ${url}`)

    try {
      const response = await fetch(url, config)
      
      // Handle authentication errors
      if (response.status === 401) {
        authStore.clearAuth()
        throw new Error('Authentication failed. Please login again.')
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'Unknown error' }))
        throw new Error(errorData.error || `HTTP ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error(`[API ERROR] ${config.method || 'GET'} ${url}:`, error)
      throw error
    }
  }

  // Convenience methods
  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' })
  }

  post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data)
    })
  }

  put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    })
  }

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' })
  }
}

// Create default instance
export const apiClient = new ApiClient()

// Auth-specific methods
export const authApi = {
  async login(credentials) {
    const response = await fetch('/csonline/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(credentials)
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ error: 'Login failed' }))
      throw new Error(errorData.error || 'Authentication failed')
    }

    return await response.json()
  }
}
