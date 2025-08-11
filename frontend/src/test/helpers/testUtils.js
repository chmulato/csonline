// Helper para mocking do auth store nos testes
import { vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { mount } from '@vue/test-utils'
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

// Router mock para testes
export const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
  currentRoute: {
    value: {
      path: '/',
      name: 'Home',
      params: {},
      query: {}
    }
  },
  options: {}
}

// Backend service mock singleton
export const backendService = {
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

// Function to create test wrapper with common setup
export function createTestWrapper(component, propsData = {}, options = {}) {
  const { authStore, router } = options
  
  let pinia
  if (authStore && authStore.pinia) {
    pinia = authStore.pinia
  } else {
    pinia = createPinia()
    setActivePinia(pinia)
  }

  const wrapper = mount(component, {
    props: propsData,
    global: {
      plugins: [pinia],
      properties: {
        $backendService: backendService
      },
      mocks: {
        $router: router || mockRouter,
        $route: (router && router.currentRoute && router.currentRoute.value) || mockRouter.currentRoute.value
      },
      stubs: {
        'router-link': true,
        'router-view': true
      }
    }
  })

  return wrapper
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
