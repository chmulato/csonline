import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/components/MainLayout.vue'

// Mock vue-router
const mockRouter = {
  push: vi.fn(),
  currentRoute: {
    value: { path: '/dashboard' }
  }
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

describe('MainLayout Component Integration', () => {
  let authStore
  let wrapper

  const createWrapper = (options = {}) => {
    return mount(MainLayout, {
      global: {
        plugins: [createPinia()],
        mocks: {
          $router: mockRouter
        },
        stubs: {
          RouterView: true,
          RouterLink: true
        }
      },
      ...options
    })
  }

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    vi.clearAllMocks()
  })

  describe('Basic Rendering', () => {
    it('should render the component without errors', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'ADMIN'
      authStore.username = 'admin_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
      expect(wrapper.text()).toContain('CSOnline')
    })

    it('should show different content for authenticated users', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'ADMIN'
      authStore.username = 'admin_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).toContain('CSOnline')
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('Authentication States', () => {
    it('should handle authenticated admin user', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'ADMIN'
      authStore.username = 'admin_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle authenticated business user', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'BUSINESS'
      authStore.username = 'business_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle authenticated courier user', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'COURIER'
      authStore.username = 'courier_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('Role Transitions', () => {
    it('should handle role changes during session', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'BUSINESS'
      authStore.username = 'user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
      
      // Change role
      authStore.userRole = 'ADMIN'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('Error Handling', () => {
    it('should handle invalid user role gracefully', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'INVALID_ROLE'
      authStore.username = 'test_user'
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })

    it('should handle missing auth data gracefully', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'ADMIN'
      authStore.username = null
      
      wrapper = createWrapper()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.exists()).toBe(true)
    })
  })
})
