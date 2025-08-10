import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import TeamManagement from '../../components/TeamManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getTeams: vi.fn(() => Promise.resolve([])),
    getCouriers: vi.fn(() => Promise.resolve([])),
    getUsers: vi.fn(() => Promise.resolve([])),
    createTeam: vi.fn(),
    updateTeam: vi.fn(),
    deleteTeam: vi.fn()
  }
}))

describe('TeamManagement - Testes Básicos', () => {
  let wrapper
  let pinia

  beforeEach(async () => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'

    wrapper = mount(TeamManagement, {
      global: {
        plugins: [pinia]
      }
    })

    await wrapper.vm.$nextTick()
  })

  describe('Renderização Básica', () => {
    it('deve renderizar sem erros', () => {
      expect(wrapper.exists()).toBe(true)
    })

    it('deve ter título relacionado a times', () => {
      const text = wrapper.text()
      expect(text).toMatch(/time|team|grupo/i)
    })

    it('deve ter botão de voltar', () => {
      expect(wrapper.find('.back-btn, .btn-back').exists()).toBe(true)
    })
  })

  describe('Estados do Componente', () => {
    it('deve ter propriedades reativas básicas', () => {
      expect(wrapper.vm.loading).toBeDefined()
    })
  })

  describe('Funcionalidades', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backBtn = wrapper.find('.back-btn, .btn-back')
      if (backBtn.exists()) {
        await backBtn.trigger('click')
        expect(wrapper.emitted('back')).toBeTruthy()
      }
    })
  })
})
