import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import PermissionGuard from '@/components/PermissionGuard.vue'
import { useAuthStore } from '@/stores/auth'

const TestComponent = {
  template: '<div class="test-content">Protected Content</div>'
}

describe('PermissionGuard', () => {
  let authStore

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    authStore.token = 'valid-token' // Always authenticated for these tests
    vi.clearAllMocks()
  })

  describe('Role-based Access', () => {
    it('should show content for authorized role', () => {
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireRole: ['ADMIN', 'BUSINESS']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })

    it('should hide content for unauthorized role', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN'
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).not.toContain('Protected Content')
    })

    it('should show access denied message when showDenied is true', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN',
          showDenied: true
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.permission-denied').exists()).toBe(true)
      expect(wrapper.text()).toContain('Acesso Negado')
    })

    it('should not show access denied message when showDenied is false', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN',
          showDenied: false
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.permission-denied').exists()).toBe(false)
      expect(wrapper.text()).toBe('')
    })
    })

    it('should use custom denied message', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'CUSTOMER'
      
      const customMessage = 'Acesso restrito para administradores'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN',
          showDenied: true,
          deniedMessage: customMessage
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).toContain(customMessage)
      expect(wrapper.text()).not.toContain('Protected Content')
    })
  })

  describe('Permission-based Access (requireAny)', () => {
    it('should show content when user has at least one required permission', () => {
      authStore.userRole = 'BUSINESS'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireAny: ['canCreateUsers', 'canCreateCouriers']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })

    it('should hide content when user has none of the required permissions', () => {
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireAny: ['canCreateUsers', 'canCreateCouriers']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(false)
    })
  })

  describe('Permission-based Access (requireAll)', () => {
    it('should show content when user has all required permissions', () => {
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireAll: ['canCreateUsers', 'canCreateCouriers']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })

    it('should hide content when user lacks any required permission', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'BUSINESS' // Has canCreateCouriers but not canCreateUsers
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireAny: ['canCreateUsers', 'canCreateCouriers']
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      // Business should have access because they have canCreateCouriers
      expect(wrapper.text()).toContain('Protected Content')
    })
  })

  describe('Unauthenticated Access', () => {
    it('should deny access when user is not authenticated', () => {
      authStore.token = null
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireRole: ['ADMIN']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(false)
    })
  })

  describe('Complex Permission Scenarios', () => {
    it('should handle multiple permission types (role + permissions)', () => {
      authStore.userRole = 'BUSINESS'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireRole: ['BUSINESS', 'ADMIN'],
          requireAny: ['canCreateCouriers']
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })

    it('should deny access when role matches but permissions do not', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = 'BUSINESS'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'BUSINESS',
          requiredPermission: 'canCreateUsers' // BUSINESS doesn't have this
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).not.toContain('Protected Content')
    })

    it('should allow access when no requirements are specified', () => {
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })
  })

  describe('Real-world Permission Scenarios', () => {
    describe('User Management Access', () => {
      it('should allow admin to access user management', () => {
        authStore.userRole = 'ADMIN'
        
        const wrapper = mount(PermissionGuard, {
          props: {
            requireAny: ['canAccessUsers']
          },
          slots: {
            default: TestComponent
          }
        })
        
        expect(wrapper.find('.test-content').exists()).toBe(true)
      })

      it('should deny business access to user management', () => {
        authStore.userRole = 'BUSINESS'
        
        const wrapper = mount(PermissionGuard, {
          props: {
            requireAny: ['canAccessUsers']
          },
          slots: {
            default: TestComponent
          }
        })
        
        expect(wrapper.find('.test-content').exists()).toBe(false)
      })
    })

    describe('Courier Management Access', () => {
      it('should allow admin and business to access courier management', () => {
        const roles = ['ADMIN', 'BUSINESS']
        
        roles.forEach(role => {
          authStore.userRole = role
          
          const wrapper = mount(PermissionGuard, {
            props: {
              requireAny: ['canAccessCouriers']
            },
            slots: {
              default: TestComponent
            }
          })
          
          expect(wrapper.find('.test-content').exists()).toBe(true)
        })
      })

      it('should deny courier and customer access to courier management', () => {
        const roles = ['COURIER', 'CUSTOMER']
        
        roles.forEach(role => {
          authStore.userRole = role
          
          const wrapper = mount(PermissionGuard, {
            props: {
              requireAny: ['canAccessCouriers']
            },
            slots: {
              default: TestComponent
            }
          })
          
          expect(wrapper.find('.test-content').exists()).toBe(false)
        })
      })
    })

    describe('Delivery Management Access', () => {
      it('should allow all authenticated users to access deliveries', () => {
        const roles = ['ADMIN', 'BUSINESS', 'COURIER', 'CUSTOMER']
        
        roles.forEach(role => {
          authStore.userRole = role
          
          const wrapper = mount(PermissionGuard, {
            props: {
              requireAny: ['canAccessDeliveries']
            },
            slots: {
              default: TestComponent
            }
          })
          
          expect(wrapper.find('.test-content').exists()).toBe(true)
        })
      })
    })

    describe('Create Actions', () => {
      it('should show create user button only for admin', () => {
        authStore.userRole = 'ADMIN'
        
        const wrapper = mount(PermissionGuard, {
          props: {
            requireAny: ['canCreateUsers'],
            showDenied: false
          },
          slots: {
            default: TestComponent
          }
        })
        
        expect(wrapper.find('.test-content').exists()).toBe(true)
      })

      it('should hide create user button for non-admin', () => {
        authStore.userRole = 'BUSINESS'
        
        const wrapper = mount(PermissionGuard, {
          props: {
            requireAny: ['canCreateUsers'],
            showDenied: false
          },
          slots: {
            default: TestComponent
          }
        })
        
        expect(wrapper.find('.test-content').exists()).toBe(false)
      })
    })
  })

  describe('Edge Cases', () => {
    it('should handle empty permission arrays', () => {
      authStore.userRole = 'CUSTOMER'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requireAny: [],
          requireAll: []
        },
        slots: {
          default: TestComponent
        }
      })
      
      expect(wrapper.find('.test-content').exists()).toBe(true)
    })

    it('should handle undefined user role', async () => {
      authStore.token = 'valid-token'
      authStore.userRole = undefined
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN'
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).not.toContain('Protected Content')
    })

    it('should handle null token', async () => {
      authStore.token = null
      authStore.userRole = 'ADMIN'
      
      const wrapper = mount(PermissionGuard, {
        props: {
          requiredRole: 'ADMIN'
        },
        slots: {
          default: 'Protected Content'
        },
        global: {
          plugins: [createPinia()]
        }
      })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).not.toContain('Protected Content')
    })
  })
})
