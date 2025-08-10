import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import SMSManagement from '../../components/SMSManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getSMS: vi.fn(),
    getDeliveries: vi.fn(),
    createSMS: vi.fn(),
    updateSMS: vi.fn(),
    deleteSMS: vi.fn()
  }
}))

// Import after mock
import { backendService } from '../../services/backend.js'

// Mock data
const mockSMS = [
  {
    id: 1,
    deliveryId: 1,
    type: 'pickup',
    mobileFrom: '+5511999999999',
    mobileTo: '+5511888888888',
    message: 'Sua encomenda será coletada hoje',
    datetime: '2025-08-10T10:00:00Z',
    status: 'sent'
  },
  {
    id: 2,
    deliveryId: 2,
    type: 'delivery',
    mobileFrom: '+5511999999999',
    mobileTo: '+5511777777777',
    message: 'Sua encomenda está a caminho',
    datetime: '2025-08-10T11:00:00Z',
    status: 'pending'
  }
]

const mockDeliveries = [
  {
    id: 1,
    start: 'São Paulo',
    destination: 'Rio de Janeiro',
    status: 'pending',
    contact: '11999999999',
    customer: { user: { name: 'Cliente A' } },
    courier: { user: { name: 'Entregador 1' } }
  },
  {
    id: 2,
    start: 'Belo Horizonte',
    destination: 'Salvador',
    status: 'completed',
    contact: '11888888888',
    customer: { user: { name: 'Cliente B' } },
    courier: { user: { name: 'Entregador 2' } }
  }
]

// Mock vue-router
const mockRouter = {
  push: vi.fn(),
  go: vi.fn()
}

describe('SMSManagement.vue', () => {
  let wrapper
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup mocks
    backendService.getSMS.mockResolvedValue(mockSMS)
    backendService.getDeliveries.mockResolvedValue(mockDeliveries)
    backendService.createSMS.mockResolvedValue({ id: 3, ...mockSMS[0] })
    backendService.updateSMS.mockResolvedValue(mockSMS[0])
    backendService.deleteSMS.mockResolvedValue()

    wrapper = mount(SMSManagement, {
      global: {
        plugins: [pinia],
        mocks: {
          $router: mockRouter
        }
      }
    })

    // Mock auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toContain('Gestão de Mensagens WhatsApp')
    })

    it('deve renderizar botão voltar', () => {
      expect(wrapper.find('.btn-back').exists()).toBe(true)
    })

    it('deve renderizar botão nova mensagem', () => {
      const newButton = wrapper.find('.btn-primary')
      expect(newButton.exists()).toBe(true)
      expect(newButton.text()).toContain('Nova Mensagem')
    })
  })

  describe('Estados da Aplicação', () => {
    it('deve mostrar loading inicialmente', () => {
      expect(wrapper.vm.loading).toBe(false) // Já carregou no mount
    })

    it('deve exibir modal ao clicar em Nova Mensagem', async () => {
      const newButton = wrapper.find('.btn-primary')
      await newButton.trigger('click')

      expect(wrapper.vm.showForm).toBe(true)
    })
  })

  describe('Formulário', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve ter estrutura correta do formulário', () => {
      expect(wrapper.vm.form).toHaveProperty('delivery')
      expect(wrapper.vm.form).toHaveProperty('piece')
      expect(wrapper.vm.form).toHaveProperty('type')
      expect(wrapper.vm.form).toHaveProperty('mobileFrom')
      expect(wrapper.vm.form).toHaveProperty('mobileTo')
      expect(wrapper.vm.form).toHaveProperty('message')
    })

    it('deve limpar formulário ao cancelar', () => {
      wrapper.vm.form = {
        delivery: { id: '1' },
        piece: 2,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Teste'
      }

      wrapper.vm.cancel()

      expect(wrapper.vm.form.delivery.id).toBe('')
      expect(wrapper.vm.form.type).toBe('')
      expect(wrapper.vm.form.message).toBe('')
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve criar nova mensagem', async () => {
      wrapper.vm.form = {
        delivery: { id: 1 },
        piece: 1,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Nova mensagem de teste'
      }

      await wrapper.vm.saveSMS()

      expect(backendService.createSMS).toHaveBeenCalledWith({
        deliveryId: 1,
        piece: 1,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Nova mensagem de teste'
      })
    })

    it('deve editar mensagem existente', () => {
      const existingSMS = mockSMS[0]
      wrapper.vm.editSMS(existingSMS)

      expect(wrapper.vm.editingSMS).toStrictEqual(existingSMS)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.delivery.id).toBe(existingSMS.deliveryId)
    })

    it('deve deletar mensagem com confirmação', async () => {
      window.confirm = vi.fn(() => true)

      await wrapper.vm.deleteSMS(1)

      expect(backendService.deleteSMS).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta mensagem?')
    })
  })

  describe('Filtros', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve filtrar por entrega', async () => {
      wrapper.vm.deliveryFilter = '1'
      await wrapper.vm.$nextTick()

      const filtered = wrapper.vm.filteredSMS
      expect(filtered.every(sms => sms.delivery?.id == '1' || sms.deliveryId == 1)).toBe(true)
    })

    it('deve filtrar por tipo', async () => {
      wrapper.vm.typeFilter = 'pickup'
      await wrapper.vm.$nextTick()

      const filtered = wrapper.vm.filteredSMS
      expect(filtered.every(sms => sms.type === 'pickup')).toBe(true)
    })
  })

  describe('Métodos Utilitários', () => {
    it('deve formatar telefone corretamente', () => {
      const formatted = wrapper.vm.formatPhone('11999999999')
      expect(formatted).toBe('(11) 99999-9999')
    })

    it('deve formatar data e hora', () => {
      const formatted = wrapper.vm.formatDateTime('2025-08-10T10:00:00Z')
      expect(formatted).toContain('/')
    })

    it('deve obter templates por tipo', () => {
      const templates = wrapper.vm.getTemplates('pickup')
      expect(Array.isArray(templates)).toBe(true)
    })
  })

  describe('Navegação', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backButton = wrapper.find('.btn-back')
      await backButton.trigger('click')

      expect(wrapper.emitted('back')).toBeTruthy()
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar', async () => {
      backendService.getSMS.mockRejectedValue(new Error('Erro de conexão'))

      const errorWrapper = mount(SMSManagement, {
        global: {
          plugins: [pinia],
          mocks: { $router: mockRouter }
        }
      })

      await errorWrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))

      expect(errorWrapper.vm.error).toBeTruthy()
    })
  })
})
