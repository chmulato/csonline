// Helper para mocking do auth store nos testes
import { vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../../stores/auth'

export function createMockAuthStore(userData = {}) {
  const pinia = createPinia()
  setActivePinia(pinia)
  
  const authStore = useAuthStore()
  
  // Configurar dados do usuário
  const defaultUser = {
    id: 1,
    name: 'Admin User',
    login: 'admin',
    role: 'ADMIN',
    ...userData
  }
  
  // Simular login bem-sucedido usando o método setAuth
  authStore.setAuth({
    token: 'valid-token',
    id: defaultUser.id,
    name: defaultUser.name,
    login: defaultUser.login,
    role: defaultUser.role
  })
  
  return { pinia, authStore }
}

export function mockBackendService() {
  return {
    // Customer methods
    getCustomers: vi.fn().mockResolvedValue([]),
    createCustomer: vi.fn().mockResolvedValue({ success: true }),
    updateCustomer: vi.fn().mockResolvedValue({ success: true }),
    deleteCustomer: vi.fn().mockResolvedValue({ success: true }),
    
    // Business methods
    getBusinesses: vi.fn().mockResolvedValue([]),
    
    // User methods
    getUsers: vi.fn().mockResolvedValue([]),
    createUser: vi.fn().mockResolvedValue({ success: true }),
    updateUser: vi.fn().mockResolvedValue({ success: true }),
    deleteUser: vi.fn().mockResolvedValue({ success: true }),
    
    // Courier methods
    getCouriers: vi.fn().mockResolvedValue([]),
    createCourier: vi.fn().mockResolvedValue({ success: true }),
    updateCourier: vi.fn().mockResolvedValue({ success: true }),
    deleteCourier: vi.fn().mockResolvedValue({ success: true }),
    
    // Delivery methods
    getDeliveries: vi.fn().mockResolvedValue([]),
    createDelivery: vi.fn().mockResolvedValue({ success: true }),
    updateDelivery: vi.fn().mockResolvedValue({ success: true }),
    deleteDelivery: vi.fn().mockResolvedValue({ success: true }),
    
    // Price methods
    getPrices: vi.fn().mockResolvedValue([]),
    createPrice: vi.fn().mockResolvedValue({ success: true }),
    updatePrice: vi.fn().mockResolvedValue({ success: true }),
    deletePrice: vi.fn().mockResolvedValue({ success: true }),
    
    // SMS methods
    getSMS: vi.fn().mockResolvedValue([]),
    createSMS: vi.fn().mockResolvedValue({ success: true }),
    updateSMS: vi.fn().mockResolvedValue({ success: true }),
    deleteSMS: vi.fn().mockResolvedValue({ success: true }),
    
    // Team methods
    getTeams: vi.fn().mockResolvedValue([]),
    createTeam: vi.fn().mockResolvedValue({ success: true }),
    updateTeam: vi.fn().mockResolvedValue({ success: true }),
    deleteTeam: vi.fn().mockResolvedValue({ success: true }),
    
    // Auth methods
    authenticate: vi.fn().mockResolvedValue({ success: true, token: 'valid-token' })
  }
}
