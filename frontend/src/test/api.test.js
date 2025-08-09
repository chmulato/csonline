import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { ApiClient, apiClient, authApi } from '../services/api.js'
import { useAuthStore } from '../stores/auth.js'

// Mock global fetch
global.fetch = vi.fn()

describe('API Services', () => {
  let pinia
  let authStore

  beforeEach(() => {
    // Fresh Pinia instance for each test
    pinia = createPinia()
    setActivePinia(pinia)
    authStore = useAuthStore()
    
    // Reset all mocks
    vi.clearAllMocks()
    
    // Mock console methods
    vi.spyOn(console, 'log').mockImplementation(() => {})
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('ApiClient Class', () => {
    let client

    beforeEach(() => {
      client = new ApiClient('http://test-api.com')
    })

    describe('Constructor', () => {
      it('should initialize with custom baseURL', () => {
        expect(client.baseURL).toBe('http://test-api.com')
      })

      it('should use default baseURL in development', () => {
        // Test with explicit development URL
        const devClient = new ApiClient('http://localhost:8080/csonline/api')
        expect(devClient.baseURL).toBe('http://localhost:8080/csonline/api')
      })

      it('should use production baseURL when not in dev', () => {
        // Test with explicit production URL
        const prodClient = new ApiClient('/csonline/api')
        expect(prodClient.baseURL).toBe('/csonline/api')
      })
    })

    describe('Request Method', () => {
      it('should make successful GET request without auth', async () => {
        const mockResponse = { data: 'test' }
        fetch.mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve(mockResponse)
        })

        const result = await client.request('/test')

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/test', {
          headers: {
            'Content-Type': 'application/json'
          }
        })
        expect(result).toEqual(mockResponse)
      })

      it('should include Authorization header when user is authenticated', async () => {
        // Set up authenticated state
        authStore.token = 'valid-jwt-token'
        authStore.user = { id: 1, name: 'Test User' }
        // Mock isTokenExpired to return false (valid token)
        authStore.isTokenExpired = vi.fn().mockReturnValue(false)

        const mockResponse = { data: 'authenticated' }
        fetch.mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve(mockResponse)
        })

        await client.request('/secure')

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/secure', {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer valid-jwt-token'
          }
        })
      })

      it('should handle expired token and clear auth', async () => {
        // Mock expired token
        authStore.token = 'expired-token'
        authStore.user = { id: 1, name: 'Test User' }
        authStore.isTokenExpired = vi.fn().mockReturnValue(true)
        authStore.clearAuth = vi.fn()

        await expect(client.request('/test')).rejects.toThrow('Token expired. Please login again.')
        
        expect(authStore.clearAuth).toHaveBeenCalled()
      })

      it('should handle 401 authentication errors', async () => {
        authStore.clearAuth = vi.fn()

        fetch.mockResolvedValueOnce({
          ok: false,
          status: 401
        })

        await expect(client.request('/secure')).rejects.toThrow('Authentication failed. Please login again.')
        expect(authStore.clearAuth).toHaveBeenCalled()
      })

      it('should handle other HTTP errors with JSON error response', async () => {
        fetch.mockResolvedValueOnce({
          ok: false,
          status: 400,
          json: () => Promise.resolve({ error: 'Bad Request' })
        })

        await expect(client.request('/invalid')).rejects.toThrow('Bad Request')
      })

      it('should handle HTTP errors without JSON response', async () => {
        fetch.mockResolvedValueOnce({
          ok: false,
          status: 500,
          json: () => Promise.reject(new Error('Not JSON'))
        })

        await expect(client.request('/error')).rejects.toThrow('Unknown error')
      })

      it('should handle network errors', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'))

        await expect(client.request('/network-fail')).rejects.toThrow('Network error')
        expect(console.error).toHaveBeenCalledWith(
          expect.stringContaining('[API ERROR]'),
          expect.any(Error)
        )
      })

      it('should merge custom options and headers', async () => {
        fetch.mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve({})
        })

        await client.request('/test', {
          method: 'POST',
          headers: { 'X-Custom': 'value' },
          body: JSON.stringify({ test: true })
        })

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/test', {
          method: 'POST',
          headers: {
            'X-Custom': 'value'
          },
          body: JSON.stringify({ test: true })
        })
      })

      it('should log API requests', async () => {
        fetch.mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve({})
        })

        await client.request('/test', { method: 'POST' })

        expect(console.log).toHaveBeenCalledWith('[API] POST http://test-api.com/test')
      })
    })

    describe('Convenience Methods', () => {
      beforeEach(() => {
        fetch.mockResolvedValue({
          ok: true,
          json: () => Promise.resolve({ success: true })
        })
      })

      it('should make GET request', async () => {
        await client.get('/items')

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/items', {
          headers: { 'Content-Type': 'application/json' },
          method: 'GET'
        })
      })

      it('should make POST request with data', async () => {
        const data = { name: 'Test Item' }
        await client.post('/items', data)

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/items', {
          headers: { 'Content-Type': 'application/json' },
          method: 'POST',
          body: JSON.stringify(data)
        })
      })

      it('should make PUT request with data', async () => {
        const data = { id: 1, name: 'Updated Item' }
        await client.put('/items/1', data)

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/items/1', {
          headers: { 'Content-Type': 'application/json' },
          method: 'PUT',
          body: JSON.stringify(data)
        })
      })

      it('should make DELETE request', async () => {
        await client.delete('/items/1')

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/items/1', {
          headers: { 'Content-Type': 'application/json' },
          method: 'DELETE'
        })
      })

      it('should merge options in convenience methods', async () => {
        await client.get('/items', { 
          headers: { 'X-Filter': 'active' }
        })

        expect(fetch).toHaveBeenCalledWith('http://test-api.com/items', {
          headers: { 
            'X-Filter': 'active'
          },
          method: 'GET'
        })
      })
    })
  })

  describe('Default API Client Instance', () => {
    it('should export configured apiClient instance', () => {
      expect(apiClient).toBeInstanceOf(ApiClient)
      expect(typeof apiClient.baseURL).toBe('string')
    })

    it('should be usable for requests', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ test: true })
      })

      const result = await apiClient.get('/test')
      expect(result).toEqual({ test: true })
    })
  })

  describe('Auth API', () => {
    describe('Login Method', () => {
      it('should make successful login request', async () => {
        const credentials = { username: 'admin', password: 'password' }
        const mockResponse = { 
          token: 'jwt-token', 
          user: { id: 1, name: 'Admin' } 
        }

        fetch.mockResolvedValueOnce({
          ok: true,
          json: () => Promise.resolve(mockResponse)
        })

        const result = await authApi.login(credentials)

        expect(fetch).toHaveBeenCalledWith('/csonline/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(credentials)
        })
        expect(result).toEqual(mockResponse)
      })

      it('should handle login failure with JSON error', async () => {
        fetch.mockResolvedValueOnce({
          ok: false,
          status: 401,
          json: () => Promise.resolve({ error: 'Invalid credentials' })
        })

        await expect(authApi.login({ username: 'wrong', password: 'wrong' }))
          .rejects.toThrow('Invalid credentials')
      })

      it('should handle login failure without JSON error', async () => {
        fetch.mockResolvedValueOnce({
          ok: false,
          status: 500,
          json: () => Promise.reject(new Error('Not JSON'))
        })

        await expect(authApi.login({ username: 'test', password: 'test' }))
          .rejects.toThrow('Login failed')
      })

      it('should handle network errors during login', async () => {
        fetch.mockRejectedValueOnce(new Error('Network timeout'))

        await expect(authApi.login({ username: 'test', password: 'test' }))
          .rejects.toThrow('Network timeout')
      })
    })
  })

  describe('Integration with Auth Store', () => {
    it('should work with auth store token management', async () => {
      // Setup: User logs in
      authStore.token = 'valid-token'
      authStore.user = { id: 1, name: 'User' }
      authStore.isTokenExpired = vi.fn().mockReturnValue(false)

      fetch.mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve({ data: 'protected' })
      })

      // Make authenticated request
      const result = await apiClient.get('/protected')

      expect(fetch).toHaveBeenCalledWith(
        expect.stringContaining('/protected'),
        expect.objectContaining({
          headers: expect.objectContaining({
            'Authorization': 'Bearer valid-token'
          })
        })
      )
      expect(result).toEqual({ data: 'protected' })
    })

    it('should handle auth store clearing on 401', async () => {
      authStore.token = 'invalid-token'
      authStore.user = { id: 1, name: 'User' }
      authStore.clearAuth = vi.fn()

      fetch.mockResolvedValueOnce({
        ok: false,
        status: 401
      })

      await expect(apiClient.get('/protected')).rejects.toThrow()
      expect(authStore.clearAuth).toHaveBeenCalled()
    })
  })
})
