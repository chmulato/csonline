import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import DeliveryManagement from '../../components/DeliveryManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getDeliveries: vi.fn(() => Promise.resolve([])),
    getCustomers: vi.fn(() => Promise.resolve([])),
    getCouriers: vi.fn(() => Promise.resolve([])),
    getUsers: vi.fn(() => Promise.resolve([])),
    createDelivery: vi.fn(),
    updateDelivery: vi.fn(),
    deleteDelivery: vi.fn()
  }
}))

describe('DeliveryManagement - Testes Básicos', () => {
  let wrapper
  let pinia

  beforeEach(async () => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'

    wrapper = mount(DeliveryManagement, {
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
      expect(wrapper.text()).toContain('Gestão de Entregas')
    })

    it('deve ter botões de ação', () => {
      expect(wrapper.text()).toContain('Nova Entrega')
      expect(wrapper.text()).toContain('Voltar')
    })

    it('deve ter estrutura de filtros', () => {
      expect(wrapper.text()).toContain('Todos os Status')
    })

    it('deve ter estrutura de tabela', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
    })
  })

  describe('Funcionalidade de Modal', () => {
    it('deve mostrar modal ao clicar em Nova Entrega', async () => {
      const novaEntregaBtn = wrapper.find('.primary-btn')
      await novaEntregaBtn.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(true)
    })

    it('deve fechar modal ao cancelar', async () => {
      const novaEntregaBtn = wrapper.find('.primary-btn')
      await novaEntregaBtn.trigger('click')
      
      const cancelBtn = wrapper.find('.cancel-btn')
      await cancelBtn.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(false)
    })
  })

  describe('Emissão de Eventos', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backBtn = wrapper.find('.back-btn')
      await backBtn.trigger('click')
      
      expect(wrapper.emitted('back')).toBeTruthy()
    })
  })

  describe('Estado de Loading', () => {
    it('deve ter propriedade loading reativa', () => {
      expect(wrapper.vm.loading).toBeDefined()
    })

    it('deve ter propriedade error reativa', () => {
      expect(wrapper.vm.error).toBeDefined()
    })
  })
})
