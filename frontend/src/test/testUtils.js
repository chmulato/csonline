import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { vi } from 'vitest'

// Mock do backend service para uso global nos testes
export const backendService = {
  // SMS methods
  getSMS: vi.fn(),
  createSMS: vi.fn(),
  updateSMS: vi.fn(),
  deleteSMS: vi.fn(),
  sendSMS: vi.fn(),
  
  // Deliveries methods
  getDeliveries: vi.fn(),
  createDelivery: vi.fn(),
  updateDelivery: vi.fn(),
  deleteDelivery: vi.fn(),
  
  // Couriers methods
  getCouriers: vi.fn(),
  createCourier: vi.fn(),
  updateCourier: vi.fn(),
  deleteCourier: vi.fn(),
  
  // Customers methods
  getCustomers: vi.fn(),
  createCustomer: vi.fn(),
  updateCustomer: vi.fn(),
  deleteCustomer: vi.fn(),
  
  // Users methods
  getUsers: vi.fn(),
  createUser: vi.fn(),
  updateUser: vi.fn(),
  deleteUser: vi.fn(),
  
  // Teams methods
  getTeams: vi.fn(),
  createTeam: vi.fn(),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn(),
  
  // Price methods
  getPrices: vi.fn(),
  createPrice: vi.fn(),
  updatePrice: vi.fn(),
  deletePrice: vi.fn(),
  
  // Vehicle methods
  getVehicles: vi.fn(),
  createVehicle: vi.fn(),
  updateVehicle: vi.fn(),
  deleteVehicle: vi.fn(),
  
  // Generic HTTP methods
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
  request: vi.fn()
}

// Mock do router para uso global
export const mockRouter = {
  push: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
  replace: vi.fn(),
  currentRoute: { value: { name: 'test', params: {}, query: {} } }
}

// Função para criar mock do auth store
export function createMockAuthStore() {
  return {
    token: 'valid-test-token',
    userRole: 'ADMIN',
    isAuthenticated: true,
    user: { id: 1, name: 'Test User', email: 'test@test.com' },
    permissions: ['manage_all'],
    
    // Computed getters simulados
    canAccessSMS: true,
    canManageSMS: true,
    canAccessCouriers: true,
    canManageCouriers: true,
    canAccessCustomers: true,
    canManageCustomers: true,
    canAccessDeliveries: true,
    canManageDeliveries: true,
    canAccessUsers: true,
    canManageUsers: true,
    canAccessTeams: true,
    canManageTeams: true,
    canAccessPrices: true,
    canManagePrices: true,
    canAccessVehicles: true,
    canManageVehicles: true,
    
    // Methods
    login: vi.fn(),
    logout: vi.fn(),
    checkAuth: vi.fn(),
    isTokenExpired: vi.fn(() => false)
  }
}

// Função principal para criar wrapper de teste
export function createTestWrapper(component, options = {}) {
  // Create fresh Pinia instance for each test
  const pinia = createPinia()
  setActivePinia(pinia)
  
  // Default mount options
  const defaultOptions = {
    global: {
      plugins: [pinia],
      mocks: {
        $router: mockRouter
      },
      stubs: {
        'router-link': true,
        'router-view': true
      }
    }
  }
  
  // Merge with provided options
  const mountOptions = {
    ...defaultOptions,
    ...options,
    global: {
      ...defaultOptions.global,
      ...(options.global || {}),
      provide: {
  backendService,
  // merge global.provide passed in mount options
  ...(options.global && options.global.provide ? options.global.provide : {}),
  // also merge top-level provide (used by some legacy tests)
  ...(options.provide || {})
      }
    }
  }
  
  // Create wrapper
  const wrapper = mount(component, mountOptions)
  
  // Setup auth store after mounting (avoiding dynamic imports)
  try {
    const pinia = wrapper.vm.$pinia
    if (pinia) {
      // Manually setup auth store state without dynamic import
      const stores = pinia._s
      for (const store of stores.values()) {
        if (store.$id === 'auth') {
          const mockAuth = createMockAuthStore()
          Object.assign(store, mockAuth)
          break
        }
      }
    }
  } catch (e) {
    // If auth store setup fails, continue without it
    console.warn('Could not setup auth store in tests:', e.message)
  }
  
  return wrapper
}

// Helper para resetar todos os mocks
export function resetAllMocks() {
  Object.values(backendService).forEach(fn => {
    if (vi.isMockFunction(fn)) {
      fn.mockReset()
    }
  })
  
  Object.values(mockRouter).forEach(fn => {
    if (vi.isMockFunction(fn)) {
      fn.mockReset()
    }
  })
}

// Helper para configurar respostas padrão dos mocks
export function setupDefaultMocks() {
  backendService.getSMS.mockResolvedValue([])
  backendService.getDeliveries.mockResolvedValue([])
  backendService.getCouriers.mockResolvedValue([])
  backendService.getCustomers.mockResolvedValue([])
  backendService.getUsers.mockResolvedValue([])
  backendService.getTeams.mockResolvedValue([])
  backendService.getPrices.mockResolvedValue([])
  backendService.getVehicles.mockResolvedValue([])
  
  backendService.createSMS.mockResolvedValue({ id: 1 })
  backendService.createDelivery.mockResolvedValue({ id: 1 })
  backendService.createCourier.mockResolvedValue({ id: 1 })
  backendService.createCustomer.mockResolvedValue({ id: 1 })
  backendService.createUser.mockResolvedValue({ id: 1 })
  backendService.createTeam.mockResolvedValue({ id: 1 })
  backendService.createPrice.mockResolvedValue({ id: 1 })
  backendService.createVehicle.mockResolvedValue({ id: 1 })
  
  backendService.updateSMS.mockResolvedValue({ id: 1 })
  backendService.updateDelivery.mockResolvedValue({ id: 1 })
  backendService.updateCourier.mockResolvedValue({ id: 1 })
  backendService.updateCustomer.mockResolvedValue({ id: 1 })
  backendService.updateUser.mockResolvedValue({ id: 1 })
  backendService.updateTeam.mockResolvedValue({ id: 1 })
  backendService.updatePrice.mockResolvedValue({ id: 1 })
  backendService.updateVehicle.mockResolvedValue({ id: 1 })
  
  backendService.deleteSMS.mockResolvedValue(true)
  backendService.deleteDelivery.mockResolvedValue(true)
  backendService.deleteCourier.mockResolvedValue(true)
  backendService.deleteCustomer.mockResolvedValue(true)
  backendService.deleteUser.mockResolvedValue(true)
  backendService.deleteTeam.mockResolvedValue(true)
  backendService.deletePrice.mockResolvedValue(true)
  backendService.deleteVehicle.mockResolvedValue(true)
}
