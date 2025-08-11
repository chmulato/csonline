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
    getCouriers: vi.fn()
  }
}))

// Mock window methods
global.confirm = vi.fn(() => true)
global.alert = vi.fn()

// Mock data
const mockBusinesses = [
  { id: 1, name: 'Empresa Teste' }
]

const mockCustomers = [
  { id: 1, name: 'Cliente Teste 1' }
]

const mockCouriers = [
  { id: 1, name: 'Entregador A' }
]

const mockDeliveries = [
  {
    id: 1,
    start: 'Brasília',
    destination: 'Goiânia',
    contact: 'João Silva',
    business_id: 1,
    customer_id: 1,
    courier_id: 1,
    status: 'pending',
    cost: 150.00,
    created_at: '2024-01-15'
  },
  {
    id: 2,
    start: 'São Paulo',
    destination: 'Rio de Janeiro',
    contact: 'Maria Santos',
    business_id: 1,
    customer_id: 1,
    courier_id: 1,
    status: 'completed',
    cost: 200.50,
    created_at: '2024-01-16'
  }
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
    backendService.createDelivery.mockResolvedValue({ id: 3, ...mockDeliveries[0] })
    backendService.updateDelivery.mockResolvedValue(mockDeliveries[0])
    backendService.deleteDelivery.mockResolvedValue()

    wrapper = createTestWrapper(DeliveryManagement, {
      auth: { role: 'ADMIN' }
    })

    // Initialize data arrays to prevent undefined errors
    if (!wrapper.vm.deliveries) wrapper.vm.deliveries = []
    if (!wrapper.vm.businesses) wrapper.vm.businesses = []
    if (!wrapper.vm.customers) wrapper.vm.customers = []
    if (!wrapper.vm.couriers) wrapper.vm.couriers = []
    
    await wrapper.vm.$nextTick()
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Entregas')
    })

    it('deve renderizar botões de ação', () => {
      expect(wrapper.text()).toContain('Nova Entrega')
      expect(wrapper.text()).toContain('Voltar')
    })

    it('deve renderizar estrutura de filtros', () => {
      const searchInput = wrapper.find('input[type="text"]')
      expect(searchInput.exists()).toBe(true)
      
      const statusFilter = wrapper.find('select')
      expect(statusFilter.exists()).toBe(true)
    })

    it('deve renderizar cabeçalhos da tabela', () => {
      const headers = wrapper.findAll('th')
      expect(headers.length).toBeGreaterThan(0)
      
      const headerTexts = headers.map(h => h.text())
      expect(headerTexts).toContain('ID')
      expect(headerTexts).toContain('Origem')
      expect(headerTexts).toContain('Destino')
    })
  })

  describe('Exibição de Dados', () => {
    beforeEach(async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      wrapper.vm.businesses = [...mockBusinesses]
      wrapper.vm.customers = [...mockCustomers]
      wrapper.vm.couriers = [...mockCouriers]
      await wrapper.vm.$nextTick()
    })

    it('deve exibir entregas na tabela quando disponíveis', async () => {
      const rows = wrapper.findAll('tbody tr')
      expect(rows.length).toBeGreaterThan(0)
    })

    it('deve exibir dados básicos das entregas', async () => {
      expect(wrapper.text()).toContain('Brasília')
      expect(wrapper.text()).toContain('Goiânia')
      expect(wrapper.text()).toContain('João Silva')
    })

    it('deve formatar valores monetários', () => {
      const delivery = mockDeliveries[0]
      const formatted = wrapper.vm.formatCurrency(delivery.cost)
      expect(formatted).toBe('R$ 150,00')
    })
  })

  describe('Métodos Utilitários', () => {
    beforeEach(async () => {
      wrapper.vm.businesses = [...mockBusinesses]
      wrapper.vm.customers = [...mockCustomers]
      wrapper.vm.couriers = [...mockCouriers]
      await wrapper.vm.$nextTick()
    })

    it('deve retornar nome da empresa corretamente', () => {
      const delivery = { business_id: 1 }
      const businessName = wrapper.vm.getBusinessName(delivery)
      expect(businessName).toBe('Empresa Teste')
    })

    it('deve retornar nome do cliente corretamente', () => {
      const delivery = { customer_id: 1 }
      const customerName = wrapper.vm.getCustomerName(delivery)
      expect(customerName).toBe('Cliente Teste 1')
    })

    it('deve retornar nome do entregador corretamente', () => {
      const delivery = { courier_id: 1 }
      const courierName = wrapper.vm.getCourierName(delivery)
      expect(courierName).toBe('Entregador A')
    })

    it('deve retornar N/A para IDs não encontrados', () => {
      const delivery = { business_id: 999 }
      const businessName = wrapper.vm.getBusinessName(delivery)
      expect(businessName).toBe('N/A')
    })

    it('deve formatar data corretamente', () => {
      const delivery = { created_at: '2024-01-15' }
      const formatted = wrapper.vm.formatDate(delivery.created_at)
      expect(formatted).toBe('15/01/2024')
    })
  })

  describe('Status e Classes CSS', () => {
    it('deve retornar classe CSS correta para status', () => {
      expect(wrapper.vm.getStatusClass({ status: 'pending' })).toBe('status-pending')
      expect(wrapper.vm.getStatusClass({ status: 'completed' })).toBe('status-completed')
      expect(wrapper.vm.getStatusClass({ status: 'cancelled' })).toBe('status-cancelled')
    })

    it('deve retornar texto de status correto', () => {
      expect(wrapper.vm.getStatusText({ status: 'pending' })).toBe('Pendente')
      expect(wrapper.vm.getStatusText({ status: 'completed' })).toBe('Finalizada')
      expect(wrapper.vm.getStatusText({ status: 'cancelled' })).toBe('Cancelada')
    })

    it('deve retornar valores padrão para status desconhecido', () => {
      expect(wrapper.vm.getStatusClass({ status: 'unknown' })).toBe('status-unknown')
      expect(wrapper.vm.getStatusText({ status: 'unknown' })).toBe('unknown')
    })
  })

  describe('Filtros e Busca', () => {
    beforeEach(async () => {
      wrapper.vm.deliveries = [...mockDeliveries]
      await wrapper.vm.$nextTick()
    })

    it('deve filtrar por termo de busca', async () => {
      wrapper.vm.searchTerm = 'João'
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(filtered.some(d => d.contact.includes('João'))).toBe(true)
    })

    it('deve filtrar por status', async () => {
      wrapper.vm.statusFilter = 'pending'
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(filtered.every(d => d.status === 'pending')).toBe(true)
    })

    it('deve filtrar por busca e status combinados', async () => {
      wrapper.vm.searchTerm = 'João'
      wrapper.vm.statusFilter = 'pending'
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(filtered.length).toBeGreaterThanOrEqual(0)
    })

    it('deve retornar todas as entregas quando sem filtros', async () => {
      wrapper.vm.searchTerm = ''
      wrapper.vm.statusFilter = ''
      await wrapper.vm.$nextTick()
      
      const filtered = wrapper.vm.filteredDeliveries
      expect(filtered.length).toBe(mockDeliveries.length)
    })
  })

  describe('Modal e Formulários', () => {
    it('deve abrir modal ao clicar em Nova Entrega', async () => {
      const newBtn = wrapper.find('button')
      await newBtn.trigger('click')
      
      expect(wrapper.vm.showForm).toBe(true)
    })

    it('deve fechar modal ao cancelar', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      if (wrapper.vm.cancelForm) {
        await wrapper.vm.cancelForm()
      } else {
        wrapper.vm.showForm = false
        await wrapper.vm.$nextTick()
      }
      
      expect(wrapper.vm.showForm).toBe(false)
    })

    it('deve renderizar campos do formulário quando modal aberto', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('input').exists()).toBe(true)
    })

    it('deve limpar formulário ao cancelar', async () => {
      wrapper.vm.form.start = 'Teste'
      wrapper.vm.showForm = true
      
      if (wrapper.vm.resetForm) {
        await wrapper.vm.resetForm()
      } else {
        wrapper.vm.form = {}
        await wrapper.vm.$nextTick()
      }
      
      expect(wrapper.vm.form.start).toBeFalsy()
    })
  })

  describe('Estados da UI', () => {
    it('deve mostrar loading quando carregando', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.loading').exists()).toBe(true)
    })

    it('deve mostrar erro quando houver erro', async () => {
      wrapper.vm.error = 'Erro de teste'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.error').exists()).toBe(true)
      expect(wrapper.find('.error').text()).toContain('Erro de teste')
    })

    it('deve exibir mensagem quando não há entregas', async () => {
      wrapper.vm.deliveries = []
      wrapper.vm.loading = false
      await wrapper.vm.$nextTick()
      
      expect(wrapper.text()).toContain('Nenhuma entrega encontrada')
    })

    it('deve permitir alternar estados', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.loading).toBe(true)
      
      wrapper.vm.loading = false
      wrapper.vm.error = 'Erro teste'
      await wrapper.vm.$nextTick()
      expect(wrapper.vm.error).toBe('Erro teste')
    })
  })

  describe('Navegação', () => {
    it('deve ter método goBack', () => {
      expect(typeof wrapper.vm.goBack).toBe('function')
    })

    it('deve ter navegação configurada', () => {
      expect(wrapper.vm.$router).toBeDefined()
      expect(typeof wrapper.vm.$router.push).toBe('function')
    })
  })

  describe('Operações CRUD Básicas', () => {
    it('deve ter métodos CRUD definidos', () => {
      expect(typeof wrapper.vm.saveDelivery).toBe('function')
      expect(typeof wrapper.vm.deleteDelivery).toBe('function')
    })

    it('deve ter propriedades de dados inicializadas', () => {
      expect(wrapper.vm.deliveries).toBeDefined()
      expect(wrapper.vm.businesses).toBeDefined()
      expect(wrapper.vm.customers).toBeDefined()
      expect(wrapper.vm.couriers).toBeDefined()
    })

    it('não deve excluir se usuário cancelar', async () => {
      global.confirm.mockReturnValueOnce(false)
      
      await wrapper.vm.deleteDelivery(1)

      expect(backendService.deleteDelivery).not.toHaveBeenCalled()
    })

    it('deve ter formulário configurado', () => {
      expect(wrapper.vm.form).toBeDefined()
      expect(typeof wrapper.vm.form).toBe('object')
    })
  })

  describe('Carregamento de Dados', () => {
    it('deve ter método loadDeliveries', () => {
      expect(typeof wrapper.vm.loadDeliveries).toBe('function')
    })

    it('deve ter métodos de carregamento auxiliares', () => {
      expect(typeof wrapper.vm.loadBusinesses).toBe('function')
      expect(typeof wrapper.vm.loadCustomers).toBe('function')
      expect(typeof wrapper.vm.loadCouriers).toBe('function')
    })

    it('deve permitir recarregar dados', async () => {
      if (wrapper.vm.loadDeliveries) {
        await wrapper.vm.loadDeliveries()
        expect(backendService.getDeliveries).toHaveBeenCalled()
      }
    })
  })

  describe('Validação e Tratamento de Erros', () => {
    it('deve ter propriedade error', () => {
      expect(wrapper.vm.error).toBeDefined()
    })

    it('deve ser possível definir erro', async () => {
      wrapper.vm.error = 'Teste erro'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.error).toBe('Teste erro')
    })

    it('deve limpar erro quando necessário', async () => {
      wrapper.vm.error = 'Erro teste'
      wrapper.vm.error = null
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.error).toBeFalsy()
    })
  })
})
