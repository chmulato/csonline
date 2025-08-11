                       import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import PriceManagement from '../../components/PriceManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getPrices: vi.fn(() => Promise.resolve([])),
    getCustomers: vi.fn(() => Promise.resolve([])),
    getUsers: vi.fn(() => Promise.resolve([])),
    createPrice: vi.fn(),
    updatePrice: vi.fn(),
    deletePrice: vi.fn()
  }
}))

describe('PriceManagement - Testes Básicos', () => {
  let wrapper
  let pinia

  beforeEach(async () => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'

    wrapper = mount(PriceManagement, {
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

    it('deve ter o título correto', () => {
      expect(wrapper.text()).toContain('Gestão de Preços')
    })

    it('deve ter botão de voltar', () => {
      expect(wrapper.find('.btn-back').exists()).toBe(true)
      expect(wrapper.text()).toContain('Voltar')
    })

    it('deve ter botão de novo preço', () => {
      expect(wrapper.text()).toContain('Novo Preço')
    })
  })

  describe('Estados do Componente', () => {
    it('deve ter propriedades reativas básicas', () => {
      expect(wrapper.vm.loading).toBeDefined()
      expect(wrapper.vm.error).toBeDefined()
    })

    it('deve mostrar estado de loading quando necessário', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.loading-state').exists()).toBe(true)
      expect(wrapper.text()).toContain('Carregando preços')
    })
  })

  describe('Funcionalidades', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backBtn = wrapper.find('.btn-back')
      await backBtn.trigger('click')
      
      expect(wrapper.emitted('back')).toBeTruthy()
    })
  })
})
