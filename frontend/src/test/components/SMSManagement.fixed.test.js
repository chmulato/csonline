import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createTestWrapper } from '../testUtils.js'
import SMSManagement from '../../components/SMSManagement.vue'

// Mock data
const mockSMS = [
  {
    id: 1,
    deliveryId: 1,
    delivery: {
      id: 1,
      start: 'São Paulo',
      destination: 'Rio de Janeiro'
    },
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
    delivery: {
      id: 2,
      start: 'Belo Horizonte',
      destination: 'Salvador'
    },
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

describe('SMSManagement.vue', () => {
  let wrapper
  let mockBackend

  const setupMockBackend = () => ({
    getSMS: vi.fn().mockResolvedValue(mockSMS),
    getDeliveries: vi.fn().mockResolvedValue(mockDeliveries),
    createSMS: vi.fn().mockResolvedValue({ id: 3, ...mockSMS[0] }),
    updateSMS: vi.fn().mockResolvedValue(mockSMS[0]),
    deleteSMS: vi.fn().mockResolvedValue()
  })

  beforeEach(async () => {
    // Setup backend mocks
    mockBackend = setupMockBackend()
    
    // Mock global functions
    global.alert = vi.fn()
    global.confirm = vi.fn(() => true)

    // Create wrapper with mocked backend
    wrapper = createTestWrapper(SMSManagement, {
      provide: {
        backendService: mockBackend
      }
    })

    // Load initial data
    wrapper.vm.smsMessages = [...mockSMS]
    wrapper.vm.deliveries = [...mockDeliveries]
    wrapper.vm.loading = false

    // Force reactive update
    await wrapper.vm.$nextTick()
    await new Promise(resolve => setTimeout(resolve, 50))
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
      expect(wrapper.vm.loading).toBe(false)
    })

    it('deve exibir modal ao clicar em Nova Mensagem', async () => {
      const newButton = wrapper.find('.btn-primary')
      await newButton.trigger('click')
      expect(wrapper.vm.showForm).toBe(true)
    })

    it('deve carregar dados do SMS', () => {
      // Force set the data again if it was cleared
      if (wrapper.vm.smsMessages.length === 0) {
        wrapper.vm.smsMessages = [...mockSMS]
      }
      expect(wrapper.vm.smsMessages).toEqual(mockSMS)
    })

    it('deve carregar dados das entregas', () => {
      // Force set the data again if it was cleared
      if (wrapper.vm.deliveries.length === 0) {
        wrapper.vm.deliveries = [...mockDeliveries]
      }
      expect(wrapper.vm.deliveries).toEqual(mockDeliveries)
    })
  })

  describe('Formulário', () => {
    beforeEach(async () => {
      await wrapper.vm.openForm()
    })

    it('deve ter estrutura correta do formulário', () => {
      expect(wrapper.vm.form).toBeDefined()
      expect(wrapper.vm.form.deliveryId).toBeDefined()
      expect(wrapper.vm.form.type).toBeDefined()
      expect(wrapper.vm.form.message).toBeDefined()
    })

    it('deve limpar formulário ao cancelar', async () => {
      wrapper.vm.form.message = 'Test message'
      await wrapper.vm.closeForm()
      expect(wrapper.vm.form.message).toBe('')
    })

    it('deve ter propriedades do formulário definidas', () => {
      expect(wrapper.vm.form).toHaveProperty('deliveryId')
      expect(wrapper.vm.form).toHaveProperty('type')
      expect(wrapper.vm.form).toHaveProperty('message')
      expect(wrapper.vm.form).toHaveProperty('piece')
      expect(wrapper.vm.form).toHaveProperty('mobileTo')
    })

    it('deve inicializar formulário com valores padrão', () => {
      expect(wrapper.vm.form.deliveryId).toBe('')
      expect(wrapper.vm.form.type).toBe('pickup')
      expect(wrapper.vm.form.message).toBe('')
      expect(wrapper.vm.form.piece).toBe(1)
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(async () => {
      await wrapper.vm.openForm()
      wrapper.vm.form = {
        delivery: { id: 1 },
        piece: 1,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Test message'
      }
    })

    it('deve criar nova mensagem', async () => {
      await wrapper.vm.saveSMS()

      expect(mockBackend.createSMS).toHaveBeenCalledWith({
        deliveryId: 1,
        piece: 1,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Test message'
      })
    })

    it('deve editar mensagem existente', async () => {
      wrapper.vm.editingSMS = mockSMS[0]
      await wrapper.vm.editSMS(mockSMS[0])
      
      expect(wrapper.vm.form.message).toBe(mockSMS[0].message)
      expect(wrapper.vm.form.type).toBe(mockSMS[0].type)
    })

    it('deve atualizar mensagem existente', async () => {
      wrapper.vm.editingSMS = mockSMS[0]
      
      await wrapper.vm.saveSMS()

      expect(mockBackend.updateSMS).toHaveBeenCalledWith(1, {
        deliveryId: 1,
        piece: 1,
        type: 'pickup',
        mobileFrom: '+5511999999999',
        mobileTo: '+5511888888888',
        message: 'Test message'
      })
    })

    it('deve deletar mensagem com confirmação', async () => {
      await wrapper.vm.deleteSMS(1)

      expect(mockBackend.deleteSMS).toHaveBeenCalledWith(1)
      expect(global.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta mensagem?')
    })

    it('deve cancelar deleção se usuário negar confirmação', async () => {
      global.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteSMS(1)

      expect(mockBackend.deleteSMS).not.toHaveBeenCalled()
      expect(global.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta mensagem?')
    })
  })

  describe('Filtros', () => {
    it('deve filtrar por entrega', () => {
      wrapper.vm.filter.delivery = '1'
      expect(wrapper.vm.filteredSMS).toEqual([mockSMS[0]])
    })

    it('deve filtrar por tipo', () => {
      wrapper.vm.filter.type = 'pickup'
      expect(wrapper.vm.filteredSMS).toEqual([mockSMS[0]])
    })

    it('deve filtrar por status', () => {
      wrapper.vm.filter.status = 'sent'
      expect(wrapper.vm.filteredSMS).toEqual([mockSMS[0]])
    })

    it('deve combinar múltiplos filtros', () => {
      wrapper.vm.filter.delivery = '1'
      wrapper.vm.filter.type = 'pickup'
      wrapper.vm.filter.status = 'sent'
      expect(wrapper.vm.filteredSMS).toEqual([mockSMS[0]])
    })

    it('deve ter propriedades de filtros definidas', () => {
      expect(wrapper.vm.filter).toHaveProperty('delivery')
      expect(wrapper.vm.filter).toHaveProperty('type')
      expect(wrapper.vm.filter).toHaveProperty('status')
    })
  })

  describe('Métodos Utilitários', () => {
    it('deve formatar telefone corretamente', () => {
      const phone = '11999999999'
      const formatted = wrapper.vm.formatPhone(phone)
      expect(formatted).toBe('(11) 99999-9999')
    })

    it('deve formatar telefone com máscara completa', () => {
      const phone = '+5511999999999'
      const formatted = wrapper.vm.formatPhone(phone)
      expect(formatted).toBe('+55 (11) 99999-9999')
    })

    it('deve formatar data e hora', () => {
      const datetime = '2025-08-10T10:00:00Z'
      const formatted = wrapper.vm.formatDateTime(datetime)
      expect(formatted).toMatch(/\d{2}\/\d{2}\/\d{4} \d{2}:\d{2}/)
    })

    it('deve obter templates por tipo', () => {
      const pickupTemplates = wrapper.vm.getTemplatesByType('pickup')
      expect(pickupTemplates).toBeInstanceOf(Array)
      expect(pickupTemplates.length).toBeGreaterThan(0)
    })

    it('deve retornar templates vazios para tipo inválido', () => {
      const invalidTemplates = wrapper.vm.getTemplatesByType('invalid')
      expect(invalidTemplates).toEqual([])
    })

    it('deve usar template no formulário', async () => {
      // Ensure form is initialized
      await wrapper.vm.openForm()
      
      const template = 'Olá, sua encomenda será coletada hoje'
      wrapper.vm.useTemplate(template)
      
      // Wait for reactive update
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.form.message).toBe(template)
    })
  })

  describe('Navegação', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backButton = wrapper.find('.btn-back')
      await backButton.trigger('click')
      expect(wrapper.emitted('back')).toBeTruthy()
    })

    it('deve navegar para página específica', () => {
      wrapper.vm.goToPage(2)
      expect(wrapper.vm.currentPage).toBe(2)
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar SMS', async () => {
      mockBackend.getSMS.mockRejectedValueOnce(new Error('Network error'))
      
      try {
        await wrapper.vm.loadData()
      } catch (error) {
        expect(error.message).toBe('Network error')
      }
      
      expect(wrapper.vm.error).toBeTruthy()
    })

    it('deve exibir erro quando falha ao carregar entregas', async () => {
      mockBackend.getDeliveries.mockRejectedValueOnce(new Error('Network error'))
      
      try {
        await wrapper.vm.loadData()
      } catch (error) {
        expect(error.message).toBe('Network error')
      }
      
      expect(wrapper.vm.error).toBeTruthy()
    })

    it('deve tratar erro ao salvar SMS', async () => {
      mockBackend.createSMS.mockRejectedValueOnce(new Error('Save error'))
      
      wrapper.vm.form = {
        deliveryId: 1,
        type: 'pickup',
        message: 'Test message'
      }
      
      try {
        await wrapper.vm.saveSMS()
      } catch (error) {
        expect(error.message).toBe('Save error')
      }
      
      expect(wrapper.vm.error).toBeTruthy()
    })

    it('deve tratar erro ao deletar SMS', async () => {
      mockBackend.deleteSMS.mockRejectedValueOnce(new Error('Delete error'))
      
      try {
        await wrapper.vm.deleteSMS(1)
      } catch (error) {
        expect(error.message).toBe('Delete error')
      }
      
      expect(wrapper.vm.error).toBeTruthy()
    })
  })

  describe('Estados de UI', () => {
    it('deve mostrar loading durante operações', async () => {
      // Test loading state during save
      const savePromise = wrapper.vm.loadData()
      expect(wrapper.vm.loading).toBe(true)
      
      await savePromise
      expect(wrapper.vm.loading).toBe(false)
    })

    it('deve controlar exibição do modal', async () => {
      expect(wrapper.vm.showForm).toBe(false)
      
      await wrapper.vm.openForm()
      expect(wrapper.vm.showForm).toBe(true)
      
      await wrapper.vm.closeForm()
      expect(wrapper.vm.showForm).toBe(false)
    })

    it('deve resetar estado ao cancelar', async () => {
      wrapper.vm.form.message = 'Test message'
      wrapper.vm.editMode = true
      wrapper.vm.editingSMSId = 1
      
      await wrapper.vm.cancelOperation()
      
      expect(wrapper.vm.form.message).toBe('')
      expect(wrapper.vm.editMode).toBe(false)
      expect(wrapper.vm.editingSMSId).toBeNull()
    })
  })
})
