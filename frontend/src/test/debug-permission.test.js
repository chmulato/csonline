import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import PermissionGuard from '@/components/PermissionGuard.vue'
import { useAuthStore } from '@/stores/auth'

describe('PermissionGuard Debug', () => {
  let authStore

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    vi.clearAllMocks()
  })

  it('should debug auth store state', async () => {
    // Set auth data
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'
    authStore.userId = 1
    authStore.username = 'test-user'

    console.log('Auth Store State:')
    console.log('- token:', authStore.token)
    console.log('- userRole:', authStore.userRole)
    console.log('- isAuthenticated:', authStore.isAuthenticated)
    console.log('- isAdmin:', authStore.isAdmin)
    
    // Get the current pinia instance
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Create auth store on the same pinia instance
    const storeInComponent = useAuthStore()
    storeInComponent.token = 'valid-token'
    storeInComponent.userRole = 'ADMIN'
    storeInComponent.userId = 1
    storeInComponent.username = 'test-user'
    
    const wrapper = mount(PermissionGuard, {
      props: {
        requiredRole: 'ADMIN'
      },
      slots: {
        default: '<div class="test-content">Protected Content</div>'
      },
      global: {
        plugins: [pinia]
      }
    })

    await wrapper.vm.$nextTick()
    
    console.log('Component HTML:', wrapper.html())
    console.log('Component Text:', wrapper.text())
    
    // The test should pass
    expect(wrapper.text()).toContain('Protected Content')
  })
})
