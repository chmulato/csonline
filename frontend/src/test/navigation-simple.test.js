import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

// Usar apenas o auth store sem o router para evitar conflitos
describe('Navigation Tests - Auth Store Only', () => {
  let authStore

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
  })

  it('should handle auth store state', () => {
    authStore.token = 'test-token'
    authStore.userRole = 'ADMIN'
    
    expect(authStore.isAuthenticated).toBe(true)
    expect(authStore.isAdmin).toBe(true)
  })

  it('should check admin permissions correctly', () => {
    authStore.userRole = 'ADMIN'
    expect(authStore.canAccessUsers).toBe(true)
    expect(authStore.canAccessCouriers).toBe(true)
    expect(authStore.canAccessCustomers).toBe(true)
    expect(authStore.canAccessDeliveries).toBe(true)
  })

  it('should check business permissions correctly', () => {
    authStore.userRole = 'BUSINESS'
    expect(authStore.canAccessUsers).toBe(false)
    expect(authStore.canAccessCouriers).toBe(true)
    expect(authStore.canAccessCustomers).toBe(true)
    expect(authStore.canAccessDeliveries).toBe(true)
  })

  it('should check courier permissions correctly', () => {
    authStore.userRole = 'COURIER'
    expect(authStore.canAccessUsers).toBe(false)
    expect(authStore.canAccessCouriers).toBe(false)
    expect(authStore.canAccessCustomers).toBe(false)
    expect(authStore.canAccessDeliveries).toBe(true)
  })

  it('should check customer permissions correctly', () => {
    authStore.userRole = 'CUSTOMER'
    expect(authStore.canAccessUsers).toBe(false)
    expect(authStore.canAccessCouriers).toBe(false)
    expect(authStore.canAccessCustomers).toBe(false)
    expect(authStore.canAccessDeliveries).toBe(true)
  })
})
