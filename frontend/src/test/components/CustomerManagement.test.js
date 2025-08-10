// Testes para o componente CustomerManagement
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import CustomerManagement from '../../components/CustomerManagement.vue'
import { useAuthStore } from '../../stores/auth'

// Mock vue-router
const mockRouter = {
  push: vi.fn(),
  back: vi.fn(),
  currentRoute: { value: { path: '/customer-management' } }
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  createRouter: vi.fn(() => mockRouter),
  createWebHistory: vi.fn(() => ({}))
}))

// Mock fetch global
global.fetch = vi.fn()

describe('CustomerManagement Component', () => {
  let wrapper
  let authStore
  let router

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

  const mockBusinesses = [
    { id: 1, name: 'Empresa Controladora A' },
    { id: 2, name: 'Empresa Controladora B' }
  ]

  beforeEach(() => {
    // Criar nova instância do Pinia para cada teste
    const pinia = createPinia()
    setActivePinia(pinia)

    // Inicializar auth store
    authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.user = { id: 1, name: 'Admin User', login: 'admin', role: 'ADMIN' }
    authStore.isAuthenticated = true

    // Mock das funções fetch
    fetch.mockResolvedValue({
      ok: true,
      json: async () => mockCustomers
    })

    wrapper = mount(CustomerManagement, {
      global: {
        plugins: [pinia, router]
      }
    })
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('Renderização Inicial', () => {
    it('should render the component title', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Empresas (Centro de Distribuições)')
    })

    it('should render action buttons', () => {
      expect(wrapper.find('button').text()).toContain('Nova Empresa')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })

    it('should render table headers', () => {
      const headers = wrapper.findAll('th')
      expect(headers[0].text()).toBe('Nome')
      expect(headers[1].text()).toBe('Email')
      expect(headers[2].text()).toBe('WhatsApp')
      expect(headers[3].text()).toBe('Endereço')
      expect(headers[4].text()).toBe('Fator (%)')
      expect(headers[5].text()).toBe('Tabela de Preço')
      expect(headers[6].text()).toBe('Ações')
    })

    it('should not show form modal initially', () => {
      expect(wrapper.find('.modal').exists()).toBe(false)
    })
  })

  describe('Carregamento de Dados', () => {
    it('should load customers on mount', async () => {
      await wrapper.vm.$nextTick()
      
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/csonline/api/customers',
        expect.objectContaining({
          headers: {
            'Authorization': 'Bearer valid-token',
            'Content-Type': 'application/json'
          }
        })
      )
    })

    it('should handle API errors when loading customers', async () => {
      fetch.mockResolvedValueOnce({
        ok: false,
        statusText: 'Forbidden'
      })

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      await wrapper.vm.loadCustomers()
      
      expect(consoleSpy).toHaveBeenCalledWith('Erro ao carregar empresas:', 'Forbidden')
    })

    it('should load businesses for form dropdown', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockBusinesses
      })

      await wrapper.vm.loadBusinesses()
      
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/csonline/api/businesses',
        expect.objectContaining({
          headers: {
            'Authorization': 'Bearer valid-token',
            'Content-Type': 'application/json'
          }
        })
      )
    })
  })

  describe('Exibição de Dados', () => {
    it('should display customer data in table rows', async () => {
      wrapper.vm.customers = mockCustomers
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(2)
      
      // Verificar primeira linha
      const firstRowCells = rows[0].findAll('td')
      expect(firstRowCells[0].text()).toBe('Centro Distribuição A')
      expect(firstRowCells[1].text()).toBe('centro.a@email.com')
      expect(firstRowCells[2].text()).toBe('41999887766')
      expect(firstRowCells[3].text()).toBe('Rua A, 123 - Centro, Curitiba-PR')
      expect(firstRowCells[4].text()).toBe('25.5%')
      expect(firstRowCells[5].text()).toBe('Tabela Padrão')
    })

    it('should show action buttons for each customer', async () => {
      wrapper.vm.customers = mockCustomers
      await wrapper.vm.$nextTick()
      
      const firstRow = wrapper.find('tbody tr')
      const actionButtons = firstRow.findAll('button')
      
      expect(actionButtons[0].text()).toBe('Editar')
      expect(actionButtons[1].text()).toBe('Excluir')
    })

    it('should show empty table when no customers', async () => {
      wrapper.vm.customers = []
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(0)
    })
  })

  describe('Modal de Formulário', () => {
    it('should show form modal when Nova Empresa button is clicked', async () => {
      const newButton = wrapper.find('button')
      await newButton.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      expect(wrapper.find('.modal h3').text()).toBe('Nova Empresa')
    })

    it('should render all form fields', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      const inputs = wrapper.findAll('input')
      expect(inputs[0].attributes('placeholder')).toBe('Nome da Empresa')
      expect(inputs[1].attributes('placeholder')).toBe('Email')
      expect(inputs[2].attributes('placeholder')).toBe('WhatsApp (ex: 41999887766)')
      expect(inputs[3].attributes('placeholder')).toBe('Endereço Completo')
      expect(inputs[4].attributes('placeholder')).toBe('Fator de Comissão (%)')
      
      expect(wrapper.find('select').exists()).toBe(true)
    })

    it('should show correct title for editing customer', async () => {
      wrapper.vm.editingCustomer = mockCustomers[0]
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.modal h3').text()).toBe('Editar Empresa')
    })

    it('should populate form fields when editing', async () => {
      wrapper.vm.editCustomer(mockCustomers[0])
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.form.user.name).toBe('Centro Distribuição A')
      expect(wrapper.vm.form.user.email).toBe('centro.a@email.com')
      expect(wrapper.vm.form.factorCustomer).toBe(25.5)
    })
  })

  describe('Validação de Formulário', () => {
    beforeEach(async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
    })

    it('should require all form fields', () => {
      const requiredInputs = wrapper.findAll('input[required]')
      expect(requiredInputs).toHaveLength(5) // name, email, mobile, address, factor
      
      const requiredSelect = wrapper.find('select[required]')
      expect(requiredSelect.exists()).toBe(true)
    })

    it('should validate email format', () => {
      const emailInput = wrapper.find('input[type="email"]')
      expect(emailInput.exists()).toBe(true)
    })

    it('should validate factor range', () => {
      const factorInput = wrapper.find('input[type="number"]')
      expect(factorInput.attributes('min')).toBe('0')
      expect(factorInput.attributes('max')).toBe('100')
      expect(factorInput.attributes('step')).toBe('0.01')
    })

    it('should validate phone format', () => {
      const phoneInput = wrapper.find('input[type="tel"]')
      expect(phoneInput.exists()).toBe(true)
    })
  })

  describe('Operações CRUD', () => {
    it('should call save API when form is submitted for new customer', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 3, ...wrapper.vm.form })
      })

      wrapper.vm.showForm = true
      wrapper.vm.form = {
        user: {
          name: 'Nova Empresa',
          email: 'nova@email.com',
          mobile: '41777666555',
          address: 'Rua Nova, 789'
        },
        business: { id: 1 },
        factorCustomer: 20.0,
        priceTable: 'Tabela Nova'
      }
      
      await wrapper.vm.saveCustomer()
      
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/csonline/api/customers',
        expect.objectContaining({
          method: 'POST',
          headers: {
            'Authorization': 'Bearer valid-token',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(wrapper.vm.form)
        })
      )
    })

    it('should call update API when editing existing customer', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCustomers[0]
      })

      wrapper.vm.editingCustomer = mockCustomers[0]
      wrapper.vm.form = mockCustomers[0]
      
      await wrapper.vm.saveCustomer()
      
      expect(fetch).toHaveBeenCalledWith(
        `http://localhost:8080/csonline/api/customers/${mockCustomers[0].id}`,
        expect.objectContaining({
          method: 'PUT'
        })
      )
    })

    it('should call delete API when delete button is clicked', async () => {
      // Mock window.confirm
      window.confirm = vi.fn(() => true)
      
      fetch.mockResolvedValueOnce({
        ok: true
      })

      await wrapper.vm.deleteCustomer(1)
      
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/csonline/api/customers/1',
        expect.objectContaining({
          method: 'DELETE'
        })
      )
    })

    it('should not delete when user cancels confirmation', async () => {
      window.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteCustomer(1)
      
      expect(fetch).not.toHaveBeenCalledWith(
        expect.stringContaining('DELETE')
      )
    })
  })

  describe('Navegação', () => {
    it('should call router back when back button is clicked', async () => {
      const routerSpy = vi.spyOn(router, 'back')
      
      const backButton = wrapper.find('.back-btn')
      await backButton.trigger('click')
      
      expect(routerSpy).toHaveBeenCalledOnce()
    })
  })

  describe('Tratamento de Erros', () => {
    it('should handle save errors gracefully', async () => {
      fetch.mockResolvedValueOnce({
        ok: false,
        statusText: 'Bad Request'
      })

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      await wrapper.vm.saveCustomer()
      
      expect(consoleSpy).toHaveBeenCalledWith('Erro ao salvar empresa:', 'Bad Request')
    })

    it('should handle delete errors gracefully', async () => {
      window.confirm = vi.fn(() => true)
      
      fetch.mockResolvedValueOnce({
        ok: false,
        statusText: 'Not Found'
      })

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      await wrapper.vm.deleteCustomer(999)
      
      expect(consoleSpy).toHaveBeenCalledWith('Erro ao excluir empresa:', 'Not Found')
    })

    it('should handle network errors', async () => {
      fetch.mockRejectedValueOnce(new Error('Network Error'))

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      await wrapper.vm.loadCustomers()
      
      expect(consoleSpy).toHaveBeenCalledWith('Erro na requisição:', expect.any(Error))
    })
  })

  describe('Limpeza de Formulário', () => {
    it('should reset form when canceling', async () => {
      wrapper.vm.showForm = true
      wrapper.vm.form.user.name = 'Teste'
      
      await wrapper.vm.cancel()
      
      expect(wrapper.vm.showForm).toBe(false)
      expect(wrapper.vm.form.user.name).toBe('')
      expect(wrapper.vm.editingCustomer).toBeNull()
    })

    it('should reset form after successful save', async () => {
      fetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 3 })
      })

      wrapper.vm.showForm = true
      wrapper.vm.form.user.name = 'Teste'
      
      await wrapper.vm.saveCustomer()
      
      expect(wrapper.vm.showForm).toBe(false)
      expect(wrapper.vm.form.user.name).toBe('')
    })
  })
})
