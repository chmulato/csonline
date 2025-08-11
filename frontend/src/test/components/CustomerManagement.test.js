// Testes para o componente CustomerManagement
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import CustomerManagement from '../../components/CustomerManagement.vue'
import { createTestWrapper, mockRouter } from '../testUtils.js'

// Mock do backend service - usando vi.mock() para controle total
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getCustomers: vi.fn(),
    getUsers: vi.fn(), 
    createCustomer: vi.fn(),
    updateCustomer: vi.fn(),
    deleteCustomer: vi.fn()
  }
}))

// Import the mocked backend service
import { backendService } from '../../services/backend.js'

describe('CustomerManagement Component', () => {
  let wrapper

  const mockCustomers = [
    {
      id: 1,
      user: {
        id: 1,
        name: 'Centro Distribuição A',
        email: 'centro.a@email.com',
        mobile: '41999887766',
        address: 'Rua A, 123 - Centro, Curitiba-PR'
      },
      business: { id: 1, name: 'Empresa Controladora A' },
      factorCustomer: 25.5,
      priceTable: 'Tabela Padrão'
    },
    {
      id: 2,
      user: {
        id: 2,
        name: 'Centro Distribuição B',
        email: 'centro.b@email.com',
        mobile: '41888776655',
        address: 'Av. B, 456 - Zona Sul, Curitiba-PR'
      },
      business: { id: 2, name: 'Empresa Controladora B' },
      factorCustomer: 30.0,
      priceTable: 'Tabela Premium'
    }
  ]

  beforeEach(() => {
    // Reset all mocks before each test
    vi.clearAllMocks()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  describe('Renderização Inicial', () => {
    it('should render the component title', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('h2').text()).toBe('Gestão de Empresas (Centro de Distribuições)')
    })

    it('should render action buttons', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('button').text()).toContain('Nova Empresa')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })

    it('should not show form modal initially', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.modal').exists()).toBe(false)
    })
  })

  describe('Carregamento de Dados', () => {
    it('should load customers on mount', async () => {
      backendService.getCustomers.mockResolvedValue(mockCustomers)
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()
      
      expect(backendService.getCustomers).toHaveBeenCalled()
    })

    it('should handle API errors when loading customers', async () => {
      backendService.getCustomers.mockRejectedValue(new Error('API Error'))
      backendService.getUsers.mockResolvedValue([])

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      expect(consoleSpy).toHaveBeenCalledWith('[CUSTOMER] Error loading customers:', 'API Error')
      consoleSpy.mockRestore()
    })
  })

  describe('Exibição de Dados', () => {
    it('should display customer data in table rows', async () => {
      backendService.getCustomers.mockResolvedValue(mockCustomers)
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      // Wait for component to fully load and process data
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 10))
      
      // The component needs time to process the customer data
      expect(wrapper.vm.customers).toHaveLength(2)
    })

    it('should show empty table when no customers', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(0)
    })
  })

  describe('Operações CRUD', () => {
    it('should call save API when form is submitted for new customer', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])
      backendService.createCustomer.mockResolvedValue({ id: 3, name: 'Nova Empresa' })

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      wrapper.vm.showForm = true
      wrapper.vm.form = {
        user: {
          name: 'Nova Empresa',
          email: 'nova@email.com',
          address: 'Rua Nova, 789'
        },
        factorCustomer: 20.0
      }
      
      await wrapper.vm.saveCustomer()
      
      // Check that the customer was created with the expected structure
      expect(backendService.createCustomer).toHaveBeenCalledWith(
        expect.objectContaining({
          user: expect.objectContaining({
            name: 'Nova Empresa',
            email: 'nova@email.com',
            address: 'Rua Nova, 789'
          }),
          factorCustomer: 20.0
        })
      )
    })

    it('should call delete API when delete button is clicked', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])
      backendService.deleteCustomer.mockResolvedValue()

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      // Mock window.confirm
      window.confirm = vi.fn(() => true)

      await wrapper.vm.deleteCustomer(1)
      
      expect(backendService.deleteCustomer).toHaveBeenCalledWith(1)
    })
  })

  describe('Navegação', () => {
    it('should have goBack method available', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()
      
      expect(typeof wrapper.vm.goBack).toBe('function')
    })
  })

  describe('Tratamento de Erros', () => {
    it('should handle save errors gracefully', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])
      backendService.createCustomer.mockRejectedValue(new Error('Save Error'))

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      wrapper.vm.form.user.name = 'Teste'
      await wrapper.vm.saveCustomer()
      
      expect(consoleSpy).toHaveBeenCalledWith('[CUSTOMER] Error saving customer:', 'Save Error')
      consoleSpy.mockRestore()
    })

    it('should handle delete errors gracefully', async () => {
      backendService.getCustomers.mockResolvedValue([])
      backendService.getUsers.mockResolvedValue([])
      backendService.deleteCustomer.mockRejectedValue(new Error('Delete Error'))

      wrapper = createTestWrapper(CustomerManagement)
      await wrapper.vm.$nextTick()

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      window.confirm = vi.fn(() => true)
      
      await wrapper.vm.deleteCustomer(999)
      
      expect(consoleSpy).toHaveBeenCalledWith('[CUSTOMER] Error deleting customer:', 'Delete Error')
      consoleSpy.mockRestore()
    })
  })
})