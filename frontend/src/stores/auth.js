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
  
  // Permission getters
  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isBusiness = computed(() => userRole.value === 'BUSINESS')
  const isCourier = computed(() => userRole.value === 'COURIER')
  const isCustomer = computed(() => userRole.value === 'CUSTOMER')
  
  // Module permissions
  const canAccessUsers = computed(() => isAdmin.value || isBusiness.value)
  const canAccessCouriers = computed(() => isAdmin.value || isBusiness.value)
  const canAccessCustomers = computed(() => isAdmin.value || isBusiness.value)
  const canAccessDeliveries = computed(() => isAdmin.value || isBusiness.value || isCourier.value)
  const canAccessTeams = computed(() => isAdmin.value || isBusiness.value)
  const canAccessSMS = computed(() => isAdmin.value || isBusiness.value)
  const canAccessPrices = computed(() => isAdmin.value || isBusiness.value)
  
  // CRUD permissions
  const canCreateUsers = computed(() => isAdmin.value)
  const canEditUsers = computed(() => isAdmin.value)
  const canDeleteUsers = computed(() => isAdmin.value)
  
  const canCreateCouriers = computed(() => isAdmin.value || isBusiness.value)
  const canEditCouriers = computed(() => isAdmin.value || isBusiness.value)
  const canDeleteCouriers = computed(() => isAdmin.value)
  
  const canCreateCustomers = computed(() => isAdmin.value || isBusiness.value)
  const canEditCustomers = computed(() => isAdmin.value || isBusiness.value)
  const canDeleteCustomers = computed(() => isAdmin.value)
  
  const canCreateDeliveries = computed(() => isAdmin.value || isBusiness.value)
  const canEditDeliveries = computed(() => isAdmin.value || isBusiness.value || isCourier.value)
  const canDeleteDeliveries = computed(() => isAdmin.value || isBusiness.value)
  
  const canManageTeams = computed(() => isAdmin.value || isBusiness.value)
  const canManageSMS = computed(() => isAdmin.value || isBusiness.value)
  const canManagePrices = computed(() => isAdmin.value || isBusiness.value)

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
    
    // Role checks
    isAdmin,
    isBusiness,
    isCourier,
    isCustomer,
    
    // Module permissions
    canAccessUsers,
    canAccessCouriers,
    canAccessCustomers,
    canAccessDeliveries,
    canAccessTeams,
    canAccessSMS,
    canAccessPrices,
    
    // CRUD permissions
    canCreateUsers,
    canEditUsers,
    canDeleteUsers,
    canCreateCouriers,
    canEditCouriers,
    canDeleteCouriers,
    canCreateCustomers,
    canEditCustomers,
    canDeleteCustomers,
    canCreateDeliveries,
    canEditDeliveries,
    canDeleteDeliveries,
    canManageTeams,
    canManageSMS,
    canManagePrices,
    
    // Actions
    setAuth,
    clearAuth,
    getAuthHeaders,
    isTokenExpired,
    validateAuth
  }
})
