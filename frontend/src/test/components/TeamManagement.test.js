import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import TeamManagement from '../../components/TeamManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
const mockBackendService = {
  getTeams: vi.fn(),
  getCouriers: vi.fn(),
  getUsers: vi.fn(),
  createTeam: vi.fn(),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn()
}

// Mock do auth store
const mockAuthStore = {
  isAuthenticated: true,
  canAccessTeams: true,
  canManageTeams: true
}

// Mock sample data
const mockTeams = [
  {
    id: 1,
    factorCourier: 15.5,
    status: 'active',
    business: { id: 1, name: 'Empresa A' },
    courier: { 
      id: 2, 
      name: 'João Silva', 
      email: 'joao@email.com', 
      mobile: '11999999999' 
    }
  },
  {
    id: 2,
    factorCourier: 20.0,
    status: 'inactive',
    business: { id: 2, name: 'Empresa B' },
    courier: { 
      id: 3, 
      name: 'Maria Santos', 
      email: 'maria@email.com', 
      mobile: '11888888888' 
    }
  }
]

const mockCouriers = [
  { 
    id: 2, 
    name: 'João Silva', 
    email: 'joao@email.com', 
    mobile: '11999999999',
    businessId: 1
  },
  { 
    id: 3, 
    name: 'Maria Santos', 
    email: 'maria@email.com', 
    mobile: '11888888888',
    businessId: 2
  },
  { 
    id: 4, 
    name: 'Carlos Lima', 
    email: 'carlos@email.com', 
    mobile: '11777777777',
    businessId: 1
  }
]

const mockBusinesses = [
  { id: 1, name: 'Empresa A', role: 'BUSINESS' },
  { id: 2, name: 'Empresa B', role: 'BUSINESS' }
]

describe('TeamManagement.vue', () => {
  let wrapper
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup mocks
    mockBackendService.getTeams.mockResolvedValue(mockTeams)
    mockBackendService.getCouriers.mockResolvedValue(mockCouriers)
    mockBackendService.getUsers.mockResolvedValue(mockBusinesses)
    mockBackendService.createTeam.mockResolvedValue({ id: 3, ...mockTeams[0] })
    mockBackendService.updateTeam.mockResolvedValue(mockTeams[0])
    mockBackendService.deleteTeam.mockResolvedValue()

    // Mock global properties
    const globalProperties = {
      $backendService: mockBackendService
    }

    wrapper = mount(TeamManagement, {
      global: {
        plugins: [pinia],
        properties: globalProperties,
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
      expect(wrapper.find('h2').text()).toBe('Gestão de Times')
    })

    it('deve mostrar estado de loading', async () => {
      const loadingWrapper = mount(TeamManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        },
        data() {
          return { loading: true }
        }
      })
      
      expect(loadingWrapper.find('.loading').exists()).toBe(true)
      expect(loadingWrapper.find('.loading').text()).toBe('Carregando...')
    })

    it('deve exibir botões de ação', () => {
      expect(wrapper.find('button').text()).toContain('Novo Time')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })
  })

  describe('Listagem de Times', () => {
    beforeEach(async () => {
      await wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar a tabela de times', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      
      const headers = wrapper.findAll('th')
      expect(headers).toHaveLength(8)
      expect(headers[0].text()).toBe('ID')
      expect(headers[1].text()).toBe('Empresa (CD)')
      expect(headers[2].text()).toBe('Entregador')
    })

    it('deve exibir times na tabela', () => {
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(2)
      
      // Primeira linha
      const firstRow = rows[0]
      const cells = firstRow.findAll('td')
      expect(cells[0].text()).toBe('1')
      expect(cells[1].text()).toBe('Empresa A')
      expect(cells[2].text()).toBe('João Silva')
      expect(cells[3].text()).toBe('joao@email.com')
      expect(cells[4].text()).toBe('11999999999')
      expect(cells[5].text()).toBe('15.5%')
    })

    it('deve exibir status com classes CSS apropriadas', () => {
      const rows = wrapper.findAll('tbody tr')
      const firstRowStatus = rows[0].findAll('td')[6]
      const statusSpan = firstRowStatus.find('span')
      
      expect(statusSpan.classes()).toContain('status-active')
    })

    it('deve exibir botões de ação', () => {
      const rows = wrapper.findAll('tbody tr')
      const firstRowActions = rows[0].findAll('td')[7]
      const buttons = firstRowActions.findAll('button')
      
      expect(buttons).toHaveLength(2)
      expect(buttons[0].text()).toBe('Editar')
      expect(buttons[1].text()).toBe('Excluir')
    })
  })

  describe('Filtros', () => {
    beforeEach(async () => {
      await wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar filtro de empresas', () => {
      const businessFilter = wrapper.find('select')
      expect(businessFilter.exists()).toBe(true)
      
      const options = businessFilter.findAll('option')
      expect(options[0].text()).toBe('Todas as Empresas')
    })

    it('deve filtrar por empresa', async () => {
      const businessFilter = wrapper.find('select')
      await businessFilter.setValue('1')
      
      wrapper.vm.filterTeams()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredTeams).toHaveLength(1)
      expect(wrapper.vm.filteredTeams[0].business.id).toBe(1)
    })

    it('deve mostrar todos os times quando filtro vazio', async () => {
      const businessFilter = wrapper.find('select')
      await businessFilter.setValue('')
      
      wrapper.vm.filterTeams()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredTeams).toHaveLength(2)
    })
  })

  describe('Modal de Formulário', () => {
    it('deve abrir modal ao clicar em Novo Time', async () => {
      await wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
      
      const newButton = wrapper.find('button')
      await newButton.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      expect(wrapper.find('.modal h3').text()).toBe('Novo Time')
    })

    it('deve fechar modal ao cancelar', async () => {
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      
      wrapper.vm.cancel()
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.modal').exists()).toBe(false)
    })

    it('deve limpar formulário ao cancelar', () => {
      wrapper.vm.form = {
        business: { id: 1 },
        courier: { id: 2 },
        factorCourier: 15.5
      }
      
      wrapper.vm.cancel()
      
      expect(wrapper.vm.form.business.id).toBe('')
      expect(wrapper.vm.form.courier.id).toBe('')
      expect(wrapper.vm.form.factorCourier).toBe('')
    })
  })

  describe('Formulário de Time', () => {
    beforeEach(async () => {
      await wrapper.vm.loadTeams()
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar campos do formulário', () => {
      const modal = wrapper.find('.modal')
      const selects = modal.findAll('select')
      const inputs = modal.findAll('input')
      
      expect(selects).toHaveLength(3) // business, courier, status
      expect(inputs).toHaveLength(1) // factorCourier
    })

    it('deve filtrar entregadores por empresa selecionada', async () => {
      wrapper.vm.form.business.id = 1
      wrapper.vm.onBusinessChange()
      
      expect(wrapper.vm.availableCouriers).toHaveLength(2) // Entregadores da empresa 1
      expect(wrapper.vm.availableCouriers.every(c => c.businessId === 1)).toBe(true)
    })

    it('deve exibir informações do entregador selecionado', async () => {
      wrapper.vm.form.courier.id = 2
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.selectedCourier).toBeTruthy()
      expect(wrapper.vm.selectedCourier.name).toBe('João Silva')
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(async () => {
      await wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
    })

    it('deve criar novo time', async () => {
      const newTeam = {
        business: { id: 1 },
        courier: { id: 4 },
        factorCourier: 18.0,
        status: 'active'
      }

      wrapper.vm.form = newTeam
      await wrapper.vm.saveTeam()
      
      expect(mockBackendService.createTeam).toHaveBeenCalledWith({
        businessId: 1,
        courierId: 4,
        factorCourier: 18.0,
        status: 'active'
      })
    })

    it('deve editar time existente', async () => {
      const existingTeam = mockTeams[0]
      wrapper.vm.editTeam(existingTeam)
      
      expect(wrapper.vm.editingTeam).toBe(existingTeam)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.business.id).toBe(existingTeam.business.id)
      expect(wrapper.vm.form.courier.id).toBe(existingTeam.courier.id)
    })

    it('deve atualizar time', async () => {
      const updatedTeam = { 
        id: 1,
        business: { id: 1 },
        courier: { id: 2 },
        factorCourier: 22.0,
        status: 'active'
      }
      
      wrapper.vm.form = updatedTeam
      wrapper.vm.editingTeam = mockTeams[0]
      
      await wrapper.vm.saveTeam()
      
      expect(mockBackendService.updateTeam).toHaveBeenCalledWith(1, {
        businessId: 1,
        courierId: 2,
        factorCourier: 22.0,
        status: 'active'
      })
    })

    it('deve excluir time com confirmação', async () => {
      window.confirm = vi.fn(() => true)
      
      await wrapper.vm.deleteTeam(1)
      
      expect(mockBackendService.deleteTeam).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir este time?')
    })

    it('não deve excluir se usuário cancelar', async () => {
      window.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteTeam(1)
      
      expect(mockBackendService.deleteTeam).not.toHaveBeenCalled()
    })
  })

  describe('Métodos Utilitários', () => {
    beforeEach(async () => {
      await wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
    })

    it('deve retornar classe CSS correta para status', () => {
      expect(wrapper.vm.getStatusClass({ status: 'active' })).toBe('status-active')
      expect(wrapper.vm.getStatusClass({ status: 'inactive' })).toBe('status-inactive')
      expect(wrapper.vm.getStatusClass({ status: 'suspended' })).toBe('status-suspended')
    })

    it('deve retornar texto de status correto', () => {
      expect(wrapper.vm.getStatusText({ status: 'active' })).toBe('Ativo')
      expect(wrapper.vm.getStatusText({ status: 'inactive' })).toBe('Inativo')
      expect(wrapper.vm.getStatusText({ status: 'suspended' })).toBe('Suspenso')
    })

    it('deve retornar empresa original do entregador', () => {
      const originalBusiness = wrapper.vm.getOriginalBusiness(2)
      expect(originalBusiness).toBe('Empresa A')
    })

    it('deve obter empresas de clientes', () => {
      const customerBusinesses = wrapper.vm.customerBusinesses
      expect(customerBusinesses).toHaveLength(2)
      expect(customerBusinesses.every(b => b.role === 'BUSINESS')).toBe(true)
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar times', async () => {
      mockBackendService.getTeams.mockRejectedValue(new Error('Erro de API'))
      
      const errorWrapper = mount(TeamManagement, {
        global: {
          plugins: [pinia],
          properties: { $backendService: mockBackendService }
        }
      })

      await errorWrapper.vm.loadTeams()
      await errorWrapper.vm.$nextTick()
      
      expect(errorWrapper.find('.error').exists()).toBe(true)
      expect(errorWrapper.find('.error').text()).toContain('Erro de API')
    })

    it('deve tratar erro ao salvar time', async () => {
      mockBackendService.createTeam.mockRejectedValue(new Error('Erro ao criar'))
      
      wrapper.vm.form = {
        business: { id: 1 },
        courier: { id: 2 },
        factorCourier: 15.0,
        status: 'active'
      }
      
      await wrapper.vm.saveTeam()
      
      expect(wrapper.vm.error).toBeTruthy()
    })
  })

  describe('Validação', () => {
    it('deve validar campos obrigatórios', async () => {
      wrapper.vm.showForm = true
      wrapper.vm.form = {}
      
      const form = wrapper.find('form')
      await form.trigger('submit.prevent')
      
      // Como os campos são required no HTML, o browser deve impedir o submit
      expect(wrapper.vm.form.business.id).toBeFalsy()
      expect(wrapper.vm.form.courier.id).toBeFalsy()
    })

    it('deve validar range do fator de comissão', () => {
      const input = wrapper.find('input[type="number"]')
      expect(input.attributes('min')).toBe('0')
      expect(input.attributes('max')).toBe('100')
      expect(input.attributes('step')).toBe('0.01')
    })
  })

  describe('Navegação', () => {
    it('deve ter método goBack', () => {
      expect(typeof wrapper.vm.goBack).toBe('function')
    })

    it('deve chamar goBack ao clicar no botão voltar', async () => {
      const goBackSpy = vi.spyOn(wrapper.vm, 'goBack')
      const backButton = wrapper.find('.back-btn')
      
      await backButton.trigger('click')
      
      expect(goBackSpy).toHaveBeenCalled()
    })
  })
})
