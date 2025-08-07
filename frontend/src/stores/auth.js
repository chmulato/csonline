import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || '')
  const userName = computed(() => user.value?.name || '')

  // Actions
  function setAuth(authData) {
    token.value = authData.token
    user.value = {
      id: authData.id,
      name: authData.name,
      login: authData.login,
      role: authData.role
    }
    
    // Persist to localStorage
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
    
    console.log('[AUTH] User authenticated:', user.value)
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    
    // Remove from localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
    console.log('[AUTH] User logged out')
  }

  function getAuthHeaders() {
    return {
      'Authorization': `Bearer ${token.value}`,
      'Content-Type': 'application/json'
    }
  }

  // Check if token is expired (basic check)
  function isTokenExpired() {
    if (!token.value) return true
    
    try {
      const payload = JSON.parse(atob(token.value.split('.')[1]))
      const exp = payload.exp * 1000 // Convert to milliseconds
      return Date.now() >= exp
    } catch (error) {
      console.error('Error checking token expiration:', error)
      return true
    }
  }

  // Validate and refresh auth state
  function validateAuth() {
    if (isTokenExpired()) {
      clearAuth()
      return false
    }
    return isAuthenticated.value
  }

  return {
    // State
    token,
    user,
    
    // Getters
    isAuthenticated,
    userRole,
    userName,
    
    // Actions
    setAuth,
    clearAuth,
    getAuthHeaders,
    isTokenExpired,
    validateAuth
  }
})
