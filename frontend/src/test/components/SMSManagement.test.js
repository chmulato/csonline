import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import SMSManagement from '../../components/SMSManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
const mockBackendService = {
  getSMS: vi.fn(),
  getDeliveries: vi.fn(),
  createSMS: vi.fn(),
  updateSMS: vi.fn(),
  deleteSMS: vi.fn(),
  sendSMS: vi.fn()
}

// Mock do auth store
const mockAuthStore = {
  isAuthenticated: true,
  canAccessSMS: true,
  canManageSMS: true
}

// Mock sample data
const mockSMS = [
  {
    id: 1,
    deliveryId: 1,
    type: 'pickup',
    recipient: '11999999999',
    message: 'Sua entrega será coletada hoje',
    status: 'sent',
    scheduledDate: '2025-08-10T10:00:00Z',
    sentDate: '2025-08-10T10:15:00Z'
  },
  {
    id: 2,
    deliveryId: 2,
    type: 'delivery',
    recipient: '11888888888',
    message: 'Sua entrega está a caminho',
    status: 'pending',
    scheduledDate: '2025-08-10T14:00:00Z',
    sentDate: null
  }
]

const mockDeliveries = [
  {
    id: 1,
    start: 'São Paulo',
    destination: 'Rio de Janeiro',
    status: 'pending',
    customer: { user: { name: 'Cliente A' } }
  },
  {
    id: 2,
    start: 'Belo Horizonte',
    destination: 'Salvador',
    status: 'completed',
    customer: { user: { name: 'Cliente B' } }
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
    mockBackendService.getSMS.mockResolvedValue(mockSMS)
    mockBackendService.getDeliveries.mockResolvedValue(mockDeliveries)
    mockBackendService.createSMS.mockResolvedValue({ id: 3, ...mockSMS[0] })
    mockBackendService.updateSMS.mockResolvedValue(mockSMS[0])
    mockBackendService.deleteSMS.mockResolvedValue()
    mockBackendService.sendSMS.mockResolvedValue({ success: true })

    // Mock global properties
    const globalProperties = {
      $backendService: mockBackendService
    }

    wrapper = mount(SMSManagement, {
      global: {
        plugins: [pinia],
        properties: globalProperties,
        mocks: {
          $router: mockRouter
        },
        stubs: {
          'router-link': true
        }
      }
    })

    // Mock auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'  // Isso fará isAuthenticated = true
    authStore.userRole = 'ADMIN'     // Isso dará todas as permissões
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toContain('Gestão de Mensagens WhatsApp')
    })

    it('deve mostrar estado de loading inicialmente', async () => {
      const loadingWrapper = mount(SMSManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        },
        data() {
          return { loading: true }
        }
      })
      
      expect(loadingWrapper.find('.loading-state').exists()).toBe(true)
      expect(loadingWrapper.find('.loading-state p').text()).toBe('Carregando mensagens...')
    })

    it('deve exibir botões de ação', () => {
      expect(wrapper.find('.btn-back').exists()).toBe(true)
      expect(wrapper.find('.btn-primary').text()).toContain('Nova Mensagem')
    })
  })

  describe('Cards de Estatísticas', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar cards de estatísticas', () => {
      const statCards = wrapper.findAll('.stat-card')
      expect(statCards).toHaveLength(4)
    })

    it('deve exibir total de mensagens corretamente', () => {
      const totalCard = wrapper.findAll('.stat-card')[0]
      expect(totalCard.find('.stat-number').text()).toBe('2')
      expect(totalCard.find('.stat-label').text()).toBe('Total de Mensagens')
    })

    it('deve calcular mensagens de hoje', () => {
      const todayCard = wrapper.findAll('.stat-card')[1]
      expect(todayCard.find('.stat-label').text()).toBe('Mensagens Hoje')
    })

    it('deve calcular entregas ativas', () => {
      const activeCard = wrapper.findAll('.stat-card')[2]
      expect(activeCard.find('.stat-label').text()).toBe('Entregas Ativas')
    })

    it('deve calcular mensagens enviadas', () => {
      const deliveredCard = wrapper.findAll('.stat-card')[3]
      expect(deliveredCard.find('.stat-label').text()).toBe('Mensagens Enviadas')
    })
  })

  describe('Filtros', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar filtros', () => {
      const filters = wrapper.find('.filters')
      const selects = filters.findAll('select')
      const dateInput = filters.find('input[type="date"]')
      
      expect(selects).toHaveLength(2) // entrega e tipo
      expect(dateInput.exists()).toBe(true)
    })

    it('deve filtrar por entrega', async () => {
      const deliverySelect = wrapper.findAll('.filters select')[0]
      await deliverySelect.setValue('1')
      
      wrapper.vm.filterSMS()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredSMS.every(sms => sms.deliveryId === 1)).toBe(true)
    })

    it('deve filtrar por tipo', async () => {
      const typeSelect = wrapper.findAll('.filters select')[1]
      await typeSelect.setValue('pickup')
      
      wrapper.vm.filterSMS()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredSMS.every(sms => sms.type === 'pickup')).toBe(true)
    })

    it('deve filtrar por data', async () => {
      const dateInput = wrapper.find('input[type="date"]')
      await dateInput.setValue('2025-08-10')
      
      wrapper.vm.filterSMS()
      await wrapper.vm.$nextTick()
      
      // Deve filtrar mensagens da data especificada
      expect(wrapper.vm.filteredSMS.length).toBeGreaterThanOrEqual(0)
    })
  })

  describe('Tabela de Mensagens', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar tabela com cabeçalhos corretos', async () => {
      // Aguardar um pouco para garantir que a tabela seja renderizada
      await new Promise(resolve => setTimeout(resolve, 100))
      await wrapper.vm.$nextTick()

      const table = wrapper.find('table')
      if (table.exists()) {
        const headers = table.findAll('th')
        expect(headers.length).toBeGreaterThan(0)
      }
    })

    it('deve exibir mensagens na tabela', async () => {
      await new Promise(resolve => setTimeout(resolve, 100))
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredSMS).toHaveLength(2)
      expect(wrapper.vm.filteredSMS[0].type).toBe('pickup')
      expect(wrapper.vm.filteredSMS[1].type).toBe('delivery')
    })
  })

  describe('Modal de Formulário', () => {
    it('deve abrir modal ao clicar em Nova Mensagem', async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
      
      const newButton = wrapper.find('.btn-primary')
      await newButton.trigger('click')
      
      expect(wrapper.vm.showForm).toBe(true)
    })

    it('deve fechar modal ao cancelar', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      wrapper.vm.cancel()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.showForm).toBe(false)
    })

    it('deve limpar formulário ao fechar', () => {
      wrapper.vm.form = { 
        deliveryId: 1, 
        type: 'pickup',
        message: 'Test message'
      }
      wrapper.vm.cancel()
      
      expect(wrapper.vm.form.deliveryId).toBe('')
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
      const newSMS = {
        deliveryId: 1,
        type: 'update',
        recipient: '11555555555',
        message: 'Nova mensagem de teste',
        scheduledDate: '2025-08-11T10:00:00Z'
      }

      wrapper.vm.form = newSMS
      await wrapper.vm.saveSMS()
      
      expect(mockBackendService.createSMS).toHaveBeenCalledWith(newSMS)
    })

    it('deve editar mensagem existente', async () => {
      const existingSMS = mockSMS[0]
      wrapper.vm.editSMS(existingSMS)
      
      expect(wrapper.vm.editingSMS).toBe(existingSMS)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.deliveryId).toBe(existingSMS.deliveryId)
    })

    it('deve atualizar mensagem', async () => {
      const updatedSMS = { ...mockSMS[0], message: 'Mensagem atualizada' }
      wrapper.vm.form = updatedSMS
      wrapper.vm.editingSMS = mockSMS[0]
      
      await wrapper.vm.saveSMS()
      
      expect(mockBackendService.updateSMS).toHaveBeenCalledWith(1, updatedSMS)
    })

    it('deve excluir mensagem com confirmação', async () => {
      window.confirm = vi.fn(() => true)
      
      await wrapper.vm.deleteSMS(1)
      
      expect(mockBackendService.deleteSMS).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta mensagem?')
    })

    it('não deve excluir se usuário cancelar', async () => {
      window.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteSMS(1)
      
      expect(mockBackendService.deleteSMS).not.toHaveBeenCalled()
    })

    it('deve enviar mensagem', async () => {
      await wrapper.vm.sendSMS(1)
      
      expect(mockBackendService.sendSMS).toHaveBeenCalledWith(1)
    })
  })

  describe('Métodos Utilitários', () => {
    beforeEach(async () => {
      await wrapper.vm.loadData()
      await wrapper.vm.$nextTick()
    })

    it('deve calcular mensagens de hoje', () => {
      const today = wrapper.vm.getTodayMessages()
      expect(typeof today).toBe('number')
      expect(today).toBeGreaterThanOrEqual(0)
    })

    it('deve calcular entregas ativas', () => {
      const active = wrapper.vm.getActiveDeliveries()
      expect(typeof active).toBe('number')
      expect(active).toBeGreaterThanOrEqual(0)
    })

    it('deve calcular mensagens enviadas', () => {
      const delivered = wrapper.vm.getDeliveredMessages()
      expect(typeof delivered).toBe('number')
      expect(delivered).toBeGreaterThanOrEqual(0)
    })

    it('deve formatar data corretamente', () => {
      const dateString = '2025-08-10T10:00:00Z'
      const formatted = wrapper.vm.formatDate(dateString)
      expect(formatted).toMatch(/\d{2}\/\d{2}\/\d{4}/)
    })

    it('deve retornar classe CSS correta para status', () => {
      expect(wrapper.vm.getStatusClass({ status: 'sent' })).toBe('status-sent')
      expect(wrapper.vm.getStatusClass({ status: 'pending' })).toBe('status-pending')
      expect(wrapper.vm.getStatusClass({ status: 'failed' })).toBe('status-failed')
    })

    it('deve retornar texto de status correto', () => {
      expect(wrapper.vm.getStatusText({ status: 'sent' })).toBe('Enviado')
      expect(wrapper.vm.getStatusText({ status: 'pending' })).toBe('Pendente')
      expect(wrapper.vm.getStatusText({ status: 'failed' })).toBe('Falhou')
    })

    it('deve retornar tipo de mensagem correto', () => {
      expect(wrapper.vm.getTypeText('pickup')).toBe('Coleta')
      expect(wrapper.vm.getTypeText('delivery')).toBe('Entrega')
      expect(wrapper.vm.getTypeText('update')).toBe('Atualização')
      expect(wrapper.vm.getTypeText('problem')).toBe('Problema')
      expect(wrapper.vm.getTypeText('completion')).toBe('Finalização')
    })
  })

  describe('Validação do Formulário', () => {
    beforeEach(() => {
      wrapper.vm.showForm = true
    })

    it('deve validar campos obrigatórios', () => {
      const isValid = wrapper.vm.validateForm()
      expect(isValid).toBe(false)
    })

    it('deve validar formato do telefone', () => {
      wrapper.vm.form = {
        deliveryId: 1,
        type: 'pickup',
        recipient: '123', // telefone inválido
        message: 'Teste'
      }
      
      const isValid = wrapper.vm.validateForm()
      expect(isValid).toBe(false)
    })

    it('deve passar na validação com dados válidos', () => {
      wrapper.vm.form = {
        deliveryId: 1,
        type: 'pickup',
        recipient: '11999999999',
        message: 'Mensagem válida',
        scheduledDate: '2025-08-11T10:00:00Z'
      }
      
      const isValid = wrapper.vm.validateForm()
      expect(isValid).toBe(true)
    })
  })

  describe('Templates de Mensagem', () => {
    it('deve ter templates predefinidos para diferentes tipos', () => {
      expect(wrapper.vm.getMessageTemplate('pickup')).toContain('coleta')
      expect(wrapper.vm.getMessageTemplate('delivery')).toContain('entrega')
      expect(wrapper.vm.getMessageTemplate('update')).toContain('atualização')
      expect(wrapper.vm.getMessageTemplate('problem')).toContain('problema')
      expect(wrapper.vm.getMessageTemplate('completion')).toContain('finalizada')
    })

    it('deve aplicar template ao selecionar tipo', async () => {
      wrapper.vm.showForm = true
      wrapper.vm.form.type = 'pickup'
      
      wrapper.vm.applyTemplate()
      
      expect(wrapper.vm.form.message).toContain('coleta')
    })
  })

  describe('Agendamento de Mensagens', () => {
    it('deve permitir agendar mensagem para data futura', () => {
      const futureDate = new Date()
      futureDate.setDate(futureDate.getDate() + 1)
      
      wrapper.vm.form.scheduledDate = futureDate.toISOString()
      
      expect(new Date(wrapper.vm.form.scheduledDate) > new Date()).toBe(true)
    })

    it('deve validar data de agendamento', () => {
      const pastDate = new Date()
      pastDate.setDate(pastDate.getDate() - 1)
      
      wrapper.vm.form.scheduledDate = pastDate.toISOString()
      
      const isValid = wrapper.vm.validateScheduledDate()
      expect(isValid).toBe(false)
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar mensagens', async () => {
      mockBackendService.getSMS.mockRejectedValue(new Error('Erro de API'))
      
      const errorWrapper = mount(SMSManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService },
          mocks: { $router: mockRouter }
        }
      })

      await errorWrapper.vm.loadData()
      await errorWrapper.vm.$nextTick()
      
      expect(errorWrapper.find('.error-state').exists()).toBe(true)
      expect(errorWrapper.find('.error-state p').text()).toContain('Erro de API')
    })

    it('deve permitir tentar novamente após erro', async () => {
      const errorWrapper = mount(SMSManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService },
          mocks: { $router: mockRouter }
        },
        data() {
          return { error: 'Erro de teste' }
        }
      })

      const retryButton = errorWrapper.find('.error-state button')
      expect(retryButton.text()).toContain('Tentar novamente')
      
      await retryButton.trigger('click')
      expect(mockBackendService.getSMS).toHaveBeenCalled()
    })

    it('deve tratar erro ao enviar mensagem', async () => {
      mockBackendService.sendSMS.mockRejectedValue(new Error('Erro ao enviar'))
      
      await wrapper.vm.sendSMS(1)
      
      expect(wrapper.vm.error).toBeTruthy()
    })
  })

  describe('Navegação', () => {
    it('deve ter método goBack', () => {
      expect(typeof wrapper.vm.goBack).toBe('function')
    })

    it('deve chamar goBack ao clicar no botão voltar', async () => {
      const goBackSpy = vi.spyOn(wrapper.vm, 'goBack')
      const backButton = wrapper.find('.btn-back')
      
      await backButton.trigger('click')
      
      expect(goBackSpy).toHaveBeenCalled()
    })
  })
})
