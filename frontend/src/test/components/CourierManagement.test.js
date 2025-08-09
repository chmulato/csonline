// Testes para o componente CourierManagement
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import CourierManagement from '../../components/CourierManagement.vue'
import PermissionGuard from '../../components/PermissionGuard.vue'
import { useAuthStore } from '../../stores/auth'

// Mock fetch global
global.fetch = vi.fn()

describe('CourierManagement Component', () => {
  let wrapper
  let authStore
  let router

  const mockCouriers = [
    {
      id: 1,
      user: { id: 1, name: 'João Silva', email: 'joao@email.com', mobile: '11999999999' },
      business: { id: 1, name: 'Empresa A' },
      factorCourier: 15
    },
    {
      id: 2,
      user: { id: 2, name: 'Maria Santos', email: 'maria@email.com', mobile: '11888888888' },
      business: { id: 2, name: 'Empresa B' },
      factorCourier: 20
    }
  ]

  const mockBusinesses = [
    { id: 1, name: 'Empresa A' },
    { id: 2, name: 'Empresa B' }
  ]

  beforeEach(() => {
    // Criar nova instância do Pinia para cada teste
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Configurar router
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
        { path: '/couriers', name: 'couriers', component: CourierManagement }
      ]
    })

    // Inicializar auth store com usuário ADMIN
    authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.user = { id: 1, name: 'Admin User', login: 'admin', role: 'ADMIN' }
    authStore.isAuthenticated = true

    // Mock das funções fetch
    fetch.mockResolvedValue({
      ok: true,
      json: async () => mockCouriers
    })

    wrapper = mount(CourierManagement, {
      global: {
        plugins: [pinia, router],
        components: {
          PermissionGuard
        }
      }
    })
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('Renderização Inicial', () => {
    it('should render the component title', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Entregadores')
    })

    it('should show role indicator for admin user', () => {
      const roleIndicator = wrapper.find('.role-badge')
      expect(roleIndicator.text()).toContain('ADMIN')
      expect(roleIndicator.text()).toContain('Administrador do sistema')
    })

    it('should render search input', () => {
      expect(wrapper.find('.search-input').exists()).toBe(true)
      expect(wrapper.find('.search-input').attributes('placeholder')).toBe('Buscar entregadores...')
    })

    it('should render business filter dropdown', () => {
      expect(wrapper.find('.business-filter').exists()).toBe(true)
    })

    it('should render table headers', () => {
      const headers = wrapper.findAll('th')
      expect(headers[0].text()).toBe('ID')
      expect(headers[1].text()).toBe('Nome')
      expect(headers[2].text()).toBe('Email')
      expect(headers[3].text()).toBe('WhatsApp')
      expect(headers[4].text()).toBe('Empresa')
      expect(headers[5].text()).toBe('Fator de Comissão')
    })
  })

  describe('Permissões e Controle de Acesso', () => {
    it('should show new courier button for admin users', () => {
      expect(wrapper.find('button').text()).toContain('Novo Entregador')
    })

    it('should hide new courier button for users without permission', async () => {
      // Simular usuário sem permissão de criação
      authStore.user.role = 'CUSTOMER'
      
      wrapper = mount(CourierManagement, {
        global: {
          plugins: [createPinia(), router],
          components: { PermissionGuard }
        }
      })

      await wrapper.vm.$nextTick()
      
      // O botão não deve aparecer devido ao PermissionGuard
      expect(wrapper.find('button:contains("Novo Entregador")').exists()).toBe(false)
    })

    it('should render back button', () => {
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })
  })

  describe('Carregamento de Dados', () => {
    it('should load couriers on mount', async () => {
      // O componente deve chamar fetch para carregar entregadores
      await wrapper.vm.$nextTick()
      
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/csonline/api/couriers',
        expect.objectContaining({
          headers: {
            'Authorization': 'Bearer valid-token',
            'Content-Type': 'application/json'
          }
        })
      )
    })

    it('should load businesses on mount', async () => {
      // Mock separado para businesses
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

    it('should handle API errors gracefully', async () => {
      fetch.mockResolvedValueOnce({
        ok: false,
        statusText: 'Internal Server Error'
      })

      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      
      await wrapper.vm.loadCouriers()
      
      expect(consoleSpy).toHaveBeenCalledWith('Erro ao carregar entregadores:', 'Internal Server Error')
    })
  })

  describe('Funcionalidade de Busca', () => {
    it('should filter couriers by search term', async () => {
      // Simular dados carregados
      wrapper.vm.couriers = mockCouriers
      
      const searchInput = wrapper.find('.search-input')
      await searchInput.setValue('João')
      
      expect(wrapper.vm.searchTerm).toBe('João')
      
      // O filtro deve retornar apenas entregadores que contenham 'João'
      const filtered = wrapper.vm.filteredCouriers
      expect(filtered).toHaveLength(1)
      expect(filtered[0].user.name).toContain('João')
    })

    it('should filter by business when business filter is selected', async () => {
      wrapper.vm.couriers = mockCouriers
      wrapper.vm.businesses = mockBusinesses
      
      const businessFilter = wrapper.find('.business-filter')
      await businessFilter.setValue('1')
      
      expect(wrapper.vm.businessFilter).toBe('1')
      
      const filtered = wrapper.vm.filteredCouriers
      expect(filtered.every(courier => courier.business.id === 1)).toBe(true)
    })

    it('should clear search when input is empty', async () => {
      wrapper.vm.couriers = mockCouriers
      
      const searchInput = wrapper.find('.search-input')
      await searchInput.setValue('João')
      await searchInput.setValue('')
      
      expect(wrapper.vm.filteredCouriers).toHaveLength(2)
    })
  })

  describe('Exibição de Dados', () => {
    it('should display courier data in table rows', async () => {
      wrapper.vm.couriers = mockCouriers
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(2)
      
      // Verificar primeira linha
      const firstRowCells = rows[0].findAll('td')
      expect(firstRowCells[0].text()).toBe('1')
      expect(firstRowCells[1].text()).toBe('João Silva')
      expect(firstRowCells[2].text()).toBe('joao@email.com')
      expect(firstRowCells[3].text()).toBe('11999999999')
      expect(firstRowCells[4].text()).toBe('Empresa A')
      expect(firstRowCells[5].text()).toBe('15%')
    })

    it('should show empty table when no couriers', async () => {
      wrapper.vm.couriers = []
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(0)
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

  describe('Descrições de Papel', () => {
    it('should return correct role description for ADMIN', () => {
      authStore.user.role = 'ADMIN'
      expect(wrapper.vm.getRoleDescription()).toBe('Administrador do sistema')
    })

    it('should return correct role description for BUSINESS', () => {
      authStore.user.role = 'BUSINESS'
      expect(wrapper.vm.getRoleDescription()).toBe('Gestor de empresa')
    })

    it('should return correct role description for COURIER', () => {
      authStore.user.role = 'COURIER'
      expect(wrapper.vm.getRoleDescription()).toBe('Entregador')
    })

    it('should return correct role description for CUSTOMER', () => {
      authStore.user.role = 'CUSTOMER'
      expect(wrapper.vm.getRoleDescription()).toBe('Cliente final')
    })

    it('should return default description for unknown role', () => {
      authStore.user.role = 'UNKNOWN'
      expect(wrapper.vm.getRoleDescription()).toBe('Papel indefinido')
    })
  })

  describe('Responsividade', () => {
    it('should maintain table structure on different screen sizes', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      expect(table.findAll('th')).toHaveLength(7) // 6 colunas + ações
    })

    it('should handle long text content appropriately', async () => {
      const longNameCourier = {
        ...mockCouriers[0],
        user: { ...mockCouriers[0].user, name: 'Nome Muito Longo Para Testar Quebra de Layout' }
      }
      
      wrapper.vm.couriers = [longNameCourier]
      await wrapper.vm.$nextTick()
      
      const nameCell = wrapper.find('tbody tr td:nth-child(2)')
      expect(nameCell.text()).toContain('Nome Muito Longo')
    })
  })
})
