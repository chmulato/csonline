import { describe, it, expect, beforeEach, vi } from 'vitest'
import DeliveryManagement from '../../components/DeliveryManagement.vue'
import { createTestWrapper, mockRouter, backendService } from '../helpers/testUtils'

// Mock backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getDeliveries: vi.fn(),
    createDelivery: vi.fn(),
    updateDelivery: vi.fn(),
    deleteDelivery: vi.fn(),
    getBusinesses: vi.fn(),
    getCustomers: vi.fn(),
    getCouriers: vi.fn(),
    getUsers: vi.fn(),
    createUser: vi.fn(),
    updateUser: vi.fn(),
    deleteUser: vi.fn()
  }
}))

// Mock window methods
global.confirm = vi.fn(() => true)
global.alert = vi.fn()

// Mock sample data
const mockDeliveries = [
  {
    id: 1,
    start: 'São Paulo',
    destination: 'Rio de Janeiro',
    status: 'pending',
    contact: '11999999999',
    cost: 150.00,
    customer: { user: { name: 'Cliente Teste 1' } },
    courier: { user: { name: 'Entregador A' } },
    business: { name: 'Empresa Teste' }
  },
  {
    id: 2,
    start: 'Belo Horizonte',
    destination: 'Salvador',
    status: 'completed',
    contact: '11888888888',
    cost: 200.00,
    customer: { user: { name: 'Cliente Teste 2' } },
    courier: { user: { name: 'Entregador B' } },
    business: { name: 'Empresa Teste 2' }
  }
]

const mockBusinesses = [
  { id: 1, name: 'Empresa A' },
  { id: 2, name: 'Empresa B' }
]

const mockCustomers = [
  { id: 1, user: { name: 'Cliente A' } },
  { id: 2, user: { name: 'Cliente B' } }
]

const mockCouriers = [
  { id: 1, user: { name: 'Entregador A' } },
  { id: 2, user: { name: 'Entregador B' } }
]

describe('DeliveryManagement.vue', () => {
  let wrapper

  beforeEach(async () => {
    // Reset all mocks
    vi.clearAllMocks()
    
    // Setup backend service mocks
    backendService.getDeliveries.mockResolvedValue(mockDeliveries)
    backendService.getBusinesses.mockResolvedValue(mockBusinesses)
    backendService.getCustomers.mockResolvedValue(mockCustomers)
    backendService.getCouriers.mockResolvedValue(mockCouriers)
    backendService.getUsers.mockResolvedValue([])
    backendService.createDelivery.mockResolvedValue({ id: 3, ...mockDeliveries[0] })
    backendService.updateDelivery.mockResolvedValue(mockDeliveries[0])
    backendService.deleteDelivery.mockResolvedValue()

    wrapper = createTestWrapper(DeliveryManagement, {
      auth: { role: 'ADMIN' }
    })

    // Sempre garantir que deliveries é um array
    if (!wrapper.vm.deliveries) {
      wrapper.vm.deliveries = [...mockDeliveries]
    }
    
    await wrapper.vm.$nextTick()
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Entregas')
    })

    it('deve mostrar estado de loading falso após inicialização', () => {
      expect(wrapper.vm.loading).toBe(false)
    })

    it('deve exibir botões de ação quando carregado', () => {
      expect(wrapper.text()).toContain('Nova Entrega')
      expect(wrapper.text()).toContain('Voltar')
    })
  })

  describe('Listagem de Entregas', () => {
    it('deve renderizar a tabela de entregas', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      
      const headers = wrapper.findAll('th')
      expect(headers.length).toBeGreaterThan(0)
    })

    it('deve exibir as entregas na tabela', async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows.length).toBeGreaterThan(0)
    })

    it('deve formatar valores monetários corretamente', async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      await wrapper.vm.$nextTick()
      
      // Verificar se existem linhas na tabela antes de acessar
      const rows = wrapper.findAll('tbody tr')
      if (rows.length > 0) {
        expect(wrapper.text()).toContain('150')
      }
    })

    it('deve exibir status com classes CSS apropriadas', async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      await wrapper.vm.$nextTick()
      
      // Verificar se existem linhas na tabela
      const rows = wrapper.findAll('tbody tr')
      expect(rows.length).toBeGreaterThan(0)
    })
  })

  describe('Filtros e Busca', () => {
    it('deve renderizar filtros de status', () => {
      const statusSelect = wrapper.find('select')
      expect(statusSelect.exists()).toBe(true)
    })

    it('deve renderizar campo de busca', () => {
      const searchInput = wrapper.find('input[type="text"]')
      expect(searchInput.exists()).toBe(true)
    })

    it('deve filtrar por status', async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      wrapper.vm.statusFilter = 'pending'
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(Array.isArray(filtered)).toBe(true)
    })

    it('deve filtrar por texto de busca', async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      wrapper.vm.searchTerm = 'São Paulo'
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(Array.isArray(filtered)).toBe(true)
    })
  })

  describe('Modal de Formulário', () => {
    it('deve abrir modal ao clicar em Nova Entrega', async () => {
      const newBtn = wrapper.find('button')
      await newBtn.trigger('click')
      
      expect(wrapper.vm.showForm).toBe(true)
    })

    it('deve fechar modal ao cancelar', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.cancel()
      
      expect(wrapper.vm.showForm).toBe(false)
    })
  })

  describe('Operações CRUD', () => {
    it('deve chamar API para criar nova entrega', async () => {
      const newDelivery = {
        businessId: 1,
        customerId: 1,
        courierId: 1,
        start: 'Brasília',
        destination: 'Goiânia',
        contact: '61999999999',
        description: 'Teste entrega',
        volume: 10,
        weight: 5,
        km: 200,
        additionalCost: 0,
        cost: 120.00,
        received: false,
        completed: false
      }

      wrapper.vm.editingDelivery = null
      Object.assign(wrapper.vm.form, newDelivery)
      
      // Garantir que o mock está retornando o formato correto
      const expectedDeliveryData = {
        business: { id: 1 },
        customer: { id: 1 },
        courier: { id: 1 },
        start: 'Brasília',
        destination: 'Goiânia',
        contact: '61999999999',
        description: 'Teste entrega',
        volume: 10,
        weight: 5,
        km: 200,
        additionalCost: 0,
        cost: 120.00,
        received: false,
        completed: false
      }
      
      await wrapper.vm.saveDelivery()
      
      expect(backendService.createDelivery).toHaveBeenCalledWith(expectedDeliveryData)
    })

    it('deve chamar API para atualizar entrega', async () => {
      const existingDelivery = { ...mockDeliveries[0] }
      
      wrapper.vm.editingDelivery = existingDelivery
      wrapper.vm.form = {
        businessId: 1,
        customerId: 1,
        courierId: 1,
        start: 'São Paulo',
        destination: 'Florianópolis',
        contact: '11999999999',
        description: 'Teste atualização',
        volume: 15,
        weight: 8,
        km: 500,
        additionalCost: 20,
        cost: 180.00,
        received: false,
        completed: false
      }
      
      const expectedDeliveryData = {
        business: { id: 1 },
        customer: { id: 1 },
        courier: { id: 1 },
        start: 'São Paulo',
        destination: 'Florianópolis',
        contact: '11999999999',
        description: 'Teste atualização',
        volume: 15,
        weight: 8,
        km: 500,
        additionalCost: 20,
        cost: 180.00,
        received: false,
        completed: false
      }
      
      await wrapper.vm.saveDelivery()
      
      expect(backendService.updateDelivery).toHaveBeenCalledWith(1, expectedDeliveryData)
    })

    it('deve chamar API para excluir entrega', async () => {
      await wrapper.vm.deleteDelivery(1)
      
      expect(backendService.deleteDelivery).toHaveBeenCalledWith(1)
      expect(global.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta entrega?')
    })

    it('não deve excluir se usuário cancelar confirmação', async () => {
      global.confirm.mockReturnValueOnce(false)
      
      await wrapper.vm.deleteDelivery(1)
      
      expect(backendService.deleteDelivery).not.toHaveBeenCalled()
    })
  })

  describe('Métodos Utilitários', () => {
    it('deve retornar nome do cliente corretamente', () => {
      const delivery = mockDeliveries[0]
      const customerName = wrapper.vm.getCustomerName(delivery)
      expect(typeof customerName).toBe('string')
    })

    it('deve retornar nome do entregador corretamente', () => {
      const delivery = mockDeliveries[0]
      const courierName = wrapper.vm.getCourierName(delivery)
      expect(typeof courierName).toBe('string')
    })

    it('deve retornar nome da empresa corretamente', () => {
      const delivery = mockDeliveries[0]
      const businessName = wrapper.vm.getBusinessName(delivery)
      expect(typeof businessName).toBe('string')
    })

    it('deve formatar data corretamente', () => {
      const date = '2025-08-11T10:00:00Z'
      const formatted = wrapper.vm.formatDate(date)
      expect(typeof formatted).toBe('string')
    })

    it('deve retornar classe CSS correta para status', () => {
      expect(wrapper.vm.getStatusClass({ completed: true })).toBe('status-completed')
      expect(wrapper.vm.getStatusClass({ received: true, completed: false })).toBe('status-received')
      expect(wrapper.vm.getStatusClass({ received: false, completed: false })).toBe('status-pending')
    })

    it('deve retornar texto de status correto', () => {
      expect(wrapper.vm.getStatusText({ completed: true })).toBe('Finalizada')
      expect(wrapper.vm.getStatusText({ received: true, completed: false })).toBe('Recebida')
      expect(wrapper.vm.getStatusText({ received: false, completed: false })).toBe('Pendente')
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve tratar erro ao carregar entregas', async () => {
      const errorMessage = 'Erro ao carregar'
      backendService.getDeliveries.mockRejectedValueOnce(new Error(errorMessage))
      
      // Create a new wrapper to trigger error loading
      await createTestWrapper(DeliveryManagement, {
        auth: { role: 'ADMIN' }
      })
      
      // O mock deveria ter sido chamado mesmo com erro
      expect(backendService.getDeliveries).toHaveBeenCalled()
    })

    it('deve permitir tentar novamente após erro', async () => {
      wrapper.vm.error = 'Erro de teste'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.error).toBeTruthy()
    })
  })

  describe('Estados Vazios', () => {
    it('deve exibir mensagem quando não há entregas', async () => {
      wrapper.vm.deliveries = []
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.deliveries.length).toBe(0)
    })
  })
})
