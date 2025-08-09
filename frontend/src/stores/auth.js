import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { backendService } from '../services/backend.js'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const user = ref((() => {
    try {
      const userData = localStorage.getItem('user')
      return userData ? JSON.parse(userData) : null
    } catch (error) {
      console.error('Error parsing user data from localStorage:', error)
      // Clear invalid data
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      return null
    }
  })())
  const userRole = ref('')
  const username = ref('')

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userName = computed(() => username.value || user.value?.name || '')
  
  // Permission getters
  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isBusiness = computed(() => userRole.value === 'BUSINESS')
  const isCourier = computed(() => userRole.value === 'COURIER')
  const isCustomer = computed(() => userRole.value === 'CUSTOMER')
  
  // Module permissions
  const canAccessUsers = computed(() => isAdmin.value)
  const canAccessCouriers = computed(() => isAdmin.value || isBusiness.value)
  const canAccessCustomers = computed(() => isAdmin.value || isBusiness.value)
  const canAccessDeliveries = computed(() => isAdmin.value || isBusiness.value || isCourier.value || isCustomer.value)
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
    userRole.value = authData.role
    username.value = authData.name
    
    // Persist to localStorage
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
    
    console.log('[AUTH] User authenticated:', user.value)
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    userRole.value = ''
    username.value = ''
    
    // Remove from localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
    console.log('[AUTH] User logged out')
  }

  // Helper methods for permission checking
  function hasAnyPermission(permissions) {
    if (!permissions || permissions.length === 0) return true
    
    for (const permission of permissions) {
      switch (permission) {
        case 'canAccessUsers': if (canAccessUsers.value) return true; break
        case 'canAccessCouriers': if (canAccessCouriers.value) return true; break
        case 'canAccessCustomers': if (canAccessCustomers.value) return true; break
        case 'canAccessDeliveries': if (canAccessDeliveries.value) return true; break
        case 'canCreateUsers': if (canCreateUsers.value) return true; break
        case 'canEditUsers': if (canEditUsers.value) return true; break
        case 'canDeleteUsers': if (canDeleteUsers.value) return true; break
        case 'canCreateCouriers': if (canCreateCouriers.value) return true; break
        case 'canEditCouriers': if (canEditCouriers.value) return true; break
        case 'canDeleteCouriers': if (canDeleteCouriers.value) return true; break
        case 'canCreateCustomers': if (canCreateCustomers.value) return true; break
        case 'canEditCustomers': if (canEditCustomers.value) return true; break
        case 'canDeleteCustomers': if (canDeleteCustomers.value) return true; break
        case 'canCreateDeliveries': if (canCreateDeliveries.value) return true; break
        case 'canEditDeliveries': if (canEditDeliveries.value) return true; break
        case 'canDeleteDeliveries': if (canDeleteDeliveries.value) return true; break
        default: break
      }
    }
    return false
  }

  function hasAllPermissions(permissions) {
    if (!permissions || permissions.length === 0) return true
    
    for (const permission of permissions) {
      switch (permission) {
        case 'canAccessUsers': if (!canAccessUsers.value) return false; break
        case 'canAccessCouriers': if (!canAccessCouriers.value) return false; break
        case 'canAccessCustomers': if (!canAccessCustomers.value) return false; break
        case 'canAccessDeliveries': if (!canAccessDeliveries.value) return false; break
        case 'canCreateUsers': if (!canCreateUsers.value) return false; break
        case 'canEditUsers': if (!canEditUsers.value) return false; break
        case 'canDeleteUsers': if (!canDeleteUsers.value) return false; break
        case 'canCreateCouriers': if (!canCreateCouriers.value) return false; break
        case 'canEditCouriers': if (!canEditCouriers.value) return false; break
        case 'canDeleteCouriers': if (!canDeleteCouriers.value) return false; break
        case 'canCreateCustomers': if (!canCreateCustomers.value) return false; break
        case 'canEditCustomers': if (!canEditCustomers.value) return false; break
        case 'canDeleteCustomers': if (!canDeleteCustomers.value) return false; break
        case 'canCreateDeliveries': if (!canCreateDeliveries.value) return false; break
        case 'canEditDeliveries': if (!canEditDeliveries.value) return false; break
        case 'canDeleteDeliveries': if (!canDeleteDeliveries.value) return false; break
        default: return false
      }
    }
    return true
  }

  function hasRole(roles) {
    if (!roles || roles.length === 0) return true
    return roles.includes(userRole.value)
  }

  // Backend login function
  async function login(credentials) {
    try {
      console.log('[AUTH] Attempting login with backend...')
      
      // Fazer login no backend real
      const response = await backendService.login(credentials)
      
      // Response esperado: { token, id, name, login, role }
      setAuth({
        id: response.id,
        name: response.name,
        login: response.login,
        role: response.role,
        token: response.token
      })
      
      console.log('[AUTH] User authenticated:', {
        name: response.name,
        role: response.role,
        login: response.login
      })
      
      return true
    } catch (error) {
      console.error('[AUTH] Login failed:', error.message)
      clearAuth()
      throw error
    }
  }

  function logout() {
    clearAuth()
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
    userRole,
    username,
    
    // Getters
    isAuthenticated,
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
    
    // Helper methods
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    
    // Actions
    setAuth,
    clearAuth,
    login,
    logout,
    getAuthHeaders,
    isTokenExpired,
    validateAuth
  }
})
