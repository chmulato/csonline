import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

describe('Auth Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    global.localStorage.clear()
  })

  describe('Role Verification', () => {
    it('should correctly identify admin role', () => {
      const store = useAuthStore()
      store.userRole = 'ADMIN'
      
      expect(store.isAdmin).toBe(true)
      expect(store.isBusiness).toBe(false)
      expect(store.isCourier).toBe(false)
      expect(store.isCustomer).toBe(false)
    })

    it('should correctly identify business role', () => {
      const store = useAuthStore()
      store.userRole = 'BUSINESS'
      
      expect(store.isAdmin).toBe(false)
      expect(store.isBusiness).toBe(true)
      expect(store.isCourier).toBe(false)
      expect(store.isCustomer).toBe(false)
    })

    it('should correctly identify courier role', () => {
      const store = useAuthStore()
      store.userRole = 'COURIER'
      
      expect(store.isAdmin).toBe(false)
      expect(store.isBusiness).toBe(false)
      expect(store.isCourier).toBe(true)
      expect(store.isCustomer).toBe(false)
    })

    it('should correctly identify customer role', () => {
      const store = useAuthStore()
      store.userRole = 'CUSTOMER'
      
      expect(store.isAdmin).toBe(false)
      expect(store.isBusiness).toBe(false)
      expect(store.isCourier).toBe(false)
      expect(store.isCustomer).toBe(true)
    })
  })

  describe('Module Access Permissions', () => {
    it('should allow admin access to all modules', () => {
      const store = useAuthStore()
      store.userRole = 'ADMIN'
      
      expect(store.canAccessUsers).toBe(true)
      expect(store.canAccessCouriers).toBe(true)
      expect(store.canAccessCustomers).toBe(true)
      expect(store.canAccessDeliveries).toBe(true)
    })

    it('should limit business access appropriately', () => {
      const store = useAuthStore()
      store.userRole = 'BUSINESS'
      
      expect(store.canAccessUsers).toBe(false)
      expect(store.canAccessCouriers).toBe(true)
      expect(store.canAccessCustomers).toBe(true)
      expect(store.canAccessDeliveries).toBe(true)
    })

    it('should limit courier access appropriately', () => {
      const store = useAuthStore()
      store.userRole = 'COURIER'

      expect(store.canAccessUsers).toBe(false)
      expect(store.canAccessCouriers).toBe(false)
      expect(store.canAccessCustomers).toBe(false)
      expect(store.canAccessDeliveries).toBe(true)
    })

    it('should limit customer access appropriately', () => {
      const store = useAuthStore()
      store.userRole = 'CUSTOMER'

      expect(store.canAccessUsers).toBe(false)
      expect(store.canAccessCouriers).toBe(false)
      expect(store.canAccessCustomers).toBe(false)
      expect(store.canAccessDeliveries).toBe(true)
    })
  })

  describe('CRUD Permissions - Users', () => {
    it('should allow only admin to manage users', () => {
      const roles = ['ADMIN', 'BUSINESS', 'COURIER', 'CUSTOMER']
      
      roles.forEach(role => {
        const store = useAuthStore()
        store.userRole = role
        
        const shouldHaveAccess = role === 'ADMIN'
        expect(store.canCreateUsers).toBe(shouldHaveAccess)
        expect(store.canEditUsers).toBe(shouldHaveAccess)
        expect(store.canDeleteUsers).toBe(shouldHaveAccess)
      })
    })
  })

  describe('CRUD Permissions - Couriers', () => {
    it('should allow admin and business to manage couriers', () => {
      const testCases = [
        { role: 'ADMIN', expected: true },
        { role: 'BUSINESS', expected: true },
        { role: 'COURIER', expected: false },
        { role: 'CUSTOMER', expected: false }
      ]
      
      testCases.forEach(({ role, expected }) => {
        const store = useAuthStore()
        store.userRole = role
        
        expect(store.canCreateCouriers).toBe(expected)
        expect(store.canEditCouriers).toBe(expected)
        expect(store.canDeleteCouriers).toBe(role === 'ADMIN')
      })
    })
  })

  describe('CRUD Permissions - Deliveries', () => {
    it('should have correct delivery permissions by role', () => {
      const testCases = [
        { role: 'ADMIN', create: true, edit: true, delete: true },
        { role: 'BUSINESS', create: true, edit: true, delete: true },
        { role: 'COURIER', create: false, edit: true, delete: false },
        { role: 'CUSTOMER', create: false, edit: false, delete: false }
      ]
      
      testCases.forEach(({ role, create, edit, delete: del }) => {
        const store = useAuthStore()
        store.userRole = role
        
        expect(store.canCreateDeliveries).toBe(create)
        expect(store.canEditDeliveries).toBe(edit)
        expect(store.canDeleteDeliveries).toBe(del)
      })
    })
  })

  describe('Permission Helper Methods', () => {
    it('should correctly check multiple permissions with hasAnyPermission', () => {
      const store = useAuthStore()
      store.userRole = 'BUSINESS'
      
      expect(store.hasAnyPermission(['canCreateUsers', 'canCreateCouriers'])).toBe(true)
      expect(store.hasAnyPermission(['canCreateUsers', 'canDeleteUsers'])).toBe(false)
    })

    it('should correctly check all permissions with hasAllPermissions', () => {
      const store = useAuthStore()
      store.userRole = 'ADMIN'
      
      expect(store.hasAllPermissions(['canCreateUsers', 'canCreateCouriers'])).toBe(true)
      
      store.userRole = 'BUSINESS'
      expect(store.hasAllPermissions(['canCreateUsers', 'canCreateCouriers'])).toBe(false)
    })

    it('should correctly check roles with hasRole', () => {
      const store = useAuthStore()
      store.userRole = 'BUSINESS'
      
      expect(store.hasRole(['ADMIN', 'BUSINESS'])).toBe(true)
      expect(store.hasRole(['ADMIN', 'COURIER'])).toBe(false)
    })
  })

  describe('Authentication State', () => {
    it('should be authenticated when token exists', () => {
      const store = useAuthStore()
      store.token = 'valid-jwt-token'
      
      expect(store.isAuthenticated).toBe(true)
    })

    it('should not be authenticated when no token', () => {
      const store = useAuthStore()
      store.token = null
      
      expect(store.isAuthenticated).toBe(false)
    })
  })

  describe('Login Process', () => {
    it('should login successfully with valid credentials', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          token: 'jwt-token',
          user: { id: 1, name: 'Admin', role: 'ADMIN' }
        })
      })

      const store = useAuthStore()
      const result = await store.login({ login: 'admin', password: 'admin123' })
      
      expect(result).toBe(true)
      expect(store.token).toBe('mock-admin-token')
      expect(store.user.name).toBe('Admin User')
      expect(store.userRole).toBe('ADMIN')
      expect(global.localStorage.setItem).toHaveBeenCalledWith('token', 'mock-admin-token')
    })

    it('should fail login with invalid credentials', async () => {
      global.fetch.mockResolvedValueOnce({
        ok: false,
        status: 401
      })

      const store = useAuthStore()
      const result = await store.login({ login: 'invalid', password: 'wrong' })
      
      expect(result).toBe(false)
      expect(store.token).toBe('')
      expect(store.user).toBeNull()
      expect(store.userRole).toBe('')
    })

    it('should handle network errors during login', async () => {
      global.fetch.mockRejectedValueOnce(new Error('Network error'))

      const store = useAuthStore()
      const result = await store.login({ login: 'invalid', password: 'invalid' })
      
      expect(result).toBe(false)
    })
  })

  describe('Logout Process', () => {
    it('should clear all auth data on logout', () => {
      const store = useAuthStore()
      store.token = 'jwt-token'
      store.user = { id: 1, name: 'Admin' }
      store.userRole = 'ADMIN'
      
      store.logout()
      
      expect(store.token).toBe('')
      expect(store.user).toBeNull()
      expect(store.userRole).toBe('')
      expect(global.localStorage.removeItem).toHaveBeenCalledWith('token')
    })
  })

  describe('Token Persistence', () => {
    it('should load token from localStorage on initialization', () => {
      global.localStorage.getItem.mockReturnValue('stored-token')
      
      const store = useAuthStore()
      
      expect(store.token).toBe('stored-token')
      expect(global.localStorage.getItem).toHaveBeenCalledWith('token')
    })
  })
})
