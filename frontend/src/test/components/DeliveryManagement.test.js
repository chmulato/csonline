import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import DeliveryManagement from '../../components/DeliveryManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getDeliveries: vi.fn(),
    getCustomers: vi.fn(),
    getCouriers: vi.fn(),
    getUsers: vi.fn(),
    createDelivery: vi.fn(),
    updateDelivery: vi.fn(),
    deleteDelivery: vi.fn()
  }
}))

// Import the mocked service
import { backendService } from '../../services/backend.js'

// Mock global confirm
global.confirm = vi.fn(() => true)

// Mock sample data
const mockDeliveries = [
  {
    id: 1,
    start: 'São Paulo',
    destination: 'Rio de Janeiro',
    contact: 'João Silva',
    volume: '1 caixa',
    weight: 5.5,
    km: 430,
    cost: 150.00,
    received: false,
    completed: false,
    business: { id: 1, name: 'Empresa A' },
    customer: { id: 1, user: { name: 'Cliente Teste 1' } },
    courier: { id: 2, user: { name: 'Entregador A' } },
    datatime: '2025-08-10T10:00:00Z'
  },
  {
    id: 2,
    start: 'Belo Horizonte',
    destination: 'Salvador',
    contact: 'Maria Santos',
    volume: '2 pacotes',
    weight: 8.0,
    km: 1200,
    cost: 350.00,
    received: true,
    completed: false,
    business: { id: 1, name: 'Empresa A' },
    customer: { id: 2, user: { name: 'Cliente Teste 2' } },
    courier: { id: 3, user: { name: 'Entregador B' } },
    datatime: '2025-08-09T14:30:00Z'
  }
]

const mockCustomers = [
  { id: 1, user: { name: 'Cliente Teste 1' } },
  { id: 2, user: { name: 'Cliente Teste 2' } }
]

const mockCouriers = [
  { id: 2, user: { name: 'Entregador A' } },
  { id: 3, user: { name: 'Entregador B' } }
]

const mockUsers = [
  { id: 1, name: 'Empresa Teste', profile: 'BUSINESS' }
]

describe('DeliveryManagement.vue', () => {
  let wrapper
  let pinia

  beforeEach(async () => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'
    
    // Setup backend service mocks
    backendService.getDeliveries.mockResolvedValue(mockDeliveries)
    backendService.getCustomers.mockResolvedValue(mockCustomers)
    backendService.getCouriers.mockResolvedValue(mockCouriers)
    backendService.getUsers.mockResolvedValue(mockUsers)
    backendService.createDelivery.mockResolvedValue({ id: 3, ...mockDeliveries[0] })
    backendService.updateDelivery.mockResolvedValue(mockDeliveries[0])
    backendService.deleteDelivery.mockResolvedValue()

    wrapper = mount(DeliveryManagement, {
      global: {
        plugins: [pinia]
      }
    })

    // Wait for component to load data
    await wrapper.vm.$nextTick()
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Entregas')
    })

    it('deve mostrar estado de loading inicialmente', async () => {
      // Cria um wrapper com loading = true
      const loadingWrapper = mount(DeliveryManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        },
        data() {
          return { loading: true }
        }
      })
      
      expect(loadingWrapper.find('.loading').exists()).toBe(true)
      expect(loadingWrapper.find('.loading p').text()).toBe('Carregando entregas...')
    })

    it('deve exibir botões de ação quando carregado', async () => {
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.primary-btn').text()).toBe('Nova Entrega')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })
  })

  describe('Listagem de Entregas', () => {
    beforeEach(async () => {
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar a tabela de entregas', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      
      const headers = wrapper.findAll('th')
      expect(headers).toHaveLength(14)
      expect(headers[0].text()).toBe('ID')
      expect(headers[1].text()).toBe('Empresa')
      expect(headers[2].text()).toBe('Cliente')
    })

    it('deve exibir as entregas na tabela', () => {
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(2)
      
      // Primeira entrega
      const firstRow = rows[0]
      const cells = firstRow.findAll('td')
      expect(cells[0].text()).toBe('1')
      expect(cells[4].text()).toBe('São Paulo')
      expect(cells[5].text()).toBe('Rio de Janeiro')
    })

    it('deve formatar valores monetários corretamente', () => {
      const rows = wrapper.findAll('tbody tr')
      const firstRowCost = rows[0].findAll('td')[10]
      expect(firstRowCost.text()).toBe('R$ 150.00')
    })

    it('deve exibir status com classes CSS apropriadas', () => {
      const rows = wrapper.findAll('tbody tr')
      const statusCell = rows[0].findAll('td')[11]
      const statusSpan = statusCell.find('span')
      
      expect(statusSpan.classes()).toContain('status-pending')
    })
  })

  describe('Filtros e Busca', () => {
    beforeEach(async () => {
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar filtros de status', () => {
      const statusFilter = wrapper.find('select')
      expect(statusFilter.exists()).toBe(true)
      
      const options = statusFilter.findAll('option')
      expect(options).toHaveLength(4)
      expect(options[0].text()).toBe('Todos os Status')
      expect(options[1].text()).toBe('Pendente')
    })

    it('deve renderizar campo de busca', () => {
      const searchInput = wrapper.find('.search-input')
      expect(searchInput.exists()).toBe(true)
      expect(searchInput.attributes('placeholder')).toBe('Buscar por cliente, entregador...')
    })

    it('deve filtrar por status', async () => {
      const statusFilter = wrapper.find('select')
      await statusFilter.setValue('pending')
      
      wrapper.vm.filterDeliveries()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredDeliveries).toHaveLength(1)
      expect(wrapper.vm.filteredDeliveries[0].status).toBe('pending')
    })

    it('deve filtrar por texto de busca', async () => {
      const searchInput = wrapper.find('.search-input')
      await searchInput.setValue('João')
      
      wrapper.vm.filterDeliveries()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredDeliveries).toHaveLength(1)
      expect(wrapper.vm.filteredDeliveries[0].contact).toBe('João Silva')
    })
  })

  describe('Modal de Formulário', () => {
    it('deve abrir modal ao clicar em Nova Entrega', async () => {
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
      
      const newButton = wrapper.find('.primary-btn')
      await newButton.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      expect(wrapper.find('.modal h3').text()).toBe('Nova Entrega')
    })

    it('deve fechar modal ao cancelar', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      
      wrapper.vm.cancelForm()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.modal').exists()).toBe(false)
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(async () => {
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
    })

    it('deve chamar API para criar nova entrega', async () => {
      const newDelivery = {
        start: 'Brasília',
        destination: 'Goiânia',
        contact: 'Ana Costa',
        volume: '1 envelope',
        weight: 0.5,
        km: 200,
        customerId: 1,
        courierId: 2
      }

      await wrapper.vm.saveDelivery(newDelivery)
      
      expect(mockBackendService.createDelivery).toHaveBeenCalledWith(newDelivery)
    })

    it('deve chamar API para atualizar entrega', async () => {
      const updatedDelivery = { ...mockDeliveries[0], contact: 'João Silva Updated' }
      wrapper.vm.editingDelivery = mockDeliveries[0]
      
      await wrapper.vm.saveDelivery(updatedDelivery)
      
      expect(mockBackendService.updateDelivery).toHaveBeenCalledWith(1, updatedDelivery)
    })

    it('deve chamar API para excluir entrega', async () => {
      window.confirm = vi.fn(() => true)
      
      await wrapper.vm.deleteDelivery(1)
      
      expect(mockBackendService.deleteDelivery).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir esta entrega?')
    })

    it('não deve excluir se usuário cancelar confirmação', async () => {
      window.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteDelivery(1)
      
      expect(mockBackendService.deleteDelivery).not.toHaveBeenCalled()
    })
  })

  describe('Métodos Utilitários', () => {
    beforeEach(async () => {
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
    })

    it('deve retornar nome do cliente corretamente', () => {
      const delivery = { customerId: 1 }
      const customerName = wrapper.vm.getCustomerName(delivery)
      expect(customerName).toBe('Cliente Teste 1')
    })

    it('deve retornar nome do entregador corretamente', () => {
      const delivery = { courierId: 2 }
      const courierName = wrapper.vm.getCourierName(delivery)
      expect(courierName).toBe('Entregador A')
    })

    it('deve retornar nome da empresa corretamente', () => {
      const delivery = { businessId: 1 }
      const businessName = wrapper.vm.getBusinessName(delivery)
      expect(businessName).toBe('Empresa Teste')
    })

    it('deve formatar data corretamente', () => {
      const dateString = '2025-08-10T10:00:00Z'
      const formatted = wrapper.vm.formatDate(dateString)
      expect(formatted).toMatch(/\d{2}\/\d{2}\/\d{4}/)
    })

    it('deve retornar classe CSS correta para status', () => {
      expect(wrapper.vm.getStatusClass({ status: 'pending' })).toBe('status-pending')
      expect(wrapper.vm.getStatusClass({ status: 'completed' })).toBe('status-completed')
      expect(wrapper.vm.getStatusClass({ status: 'received' })).toBe('status-received')
    })

    it('deve retornar texto de status correto', () => {
      expect(wrapper.vm.getStatusText({ status: 'pending' })).toBe('Pendente')
      expect(wrapper.vm.getStatusText({ status: 'completed' })).toBe('Finalizada')
      expect(wrapper.vm.getStatusText({ status: 'received' })).toBe('Recebida')
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar entregas', async () => {
      mockBackendService.getDeliveries.mockRejectedValue(new Error('Erro de API'))
      
      const errorWrapper = mount(DeliveryManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        }
      })

      await errorWrapper.vm.loadDeliveries()
      await errorWrapper.vm.$nextTick()
      
      expect(errorWrapper.find('.error').exists()).toBe(true)
      expect(errorWrapper.find('.error p').text()).toContain('Erro ao carregar entregas')
    })

    it('deve permitir tentar novamente após erro', async () => {
      const errorWrapper = mount(DeliveryManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        },
        data() {
          return { error: 'Erro de teste' }
        }
      })

      const retryButton = errorWrapper.find('.error button')
      expect(retryButton.text()).toBe('Tentar novamente')
      
      await retryButton.trigger('click')
      expect(mockBackendService.getDeliveries).toHaveBeenCalled()
    })
  })

  describe('Estados Vazios', () => {
    it('deve exibir mensagem quando não há entregas', async () => {
      mockBackendService.getDeliveries.mockResolvedValue([])
      
      await wrapper.vm.loadDeliveries()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.empty-state').exists()).toBe(true)
      expect(wrapper.find('.empty-state p').text()).toBe('Nenhuma entrega encontrada.')
    })
  })
})
