import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import PermissionGuard from '@/components/PermissionGuard.vue'
import { useAuthStore } from '@/stores/auth'

const TestComponent = {
  template: '<div class="test-content">Protected Content</div>'
}

describe('PermissionGuard - Basic Tests', () => {
  let authStore
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    authStore = useAuthStore()
    // Correctly set token and user data
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN' // Set default role
    authStore.userId = 1
    authStore.username = 'test-user'
    vi.clearAllMocks()
  })

  describe('Basic Role Access', () => {
    it('should show content for admin role', async () => {
      // Ensure admin role is set
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN'
        },
        slots: {
          default: TestComponent
        },
        global: {
          plugins: [pinia]
        }
      })

      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).toContain('Protected Content')
    })

    it('should hide content for unauthorized role', async () => {
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN'
        },
        slots: {
          default: TestComponent
        },
        global: {
          plugins: [pinia]
        }
      })

      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).not.toContain('Protected Content')
      expect(wrapper.text()).toContain('Acesso Negado')
    })
  })

  describe('Permission-based Access', () => {
    it('should show content when user has required permission', async () => {
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredPermission: 'canCreateUsers'
        },
        slots: {
          default: TestComponent
        },
        global: {
          plugins: [pinia]
        }
      })

      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).toContain('Protected Content')
    })
  })
})
