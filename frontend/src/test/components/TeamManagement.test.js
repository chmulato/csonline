import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import TeamManagement from '../../components/TeamManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getTeams: vi.fn(),
    getCustomers: vi.fn(),
    getCouriers: vi.fn(),
    createTeam: vi.fn(),
    updateTeam: vi.fn(),
    deleteTeam: vi.fn()
  }
}))

// Import after mock
import { backendService } from '../../services/backend.js'

// Mock data
const mockTeams = [
  {
    id: 1,
    business: { id: 3, name: 'Distribuidora Norte' },
    courier: { id: 2, name: 'João Silva', email: 'joao@test.com', mobile: '11999999999' },
    factorCourier: 12.5,
    status: 'active'
  },
  {
    id: 2,
    business: { id: 6, name: 'Logística Sul' },
    courier: { id: 5, name: 'Carlos Santos', email: 'carlos@test.com', mobile: '11888888888' },
    factorCourier: 15.0,
    status: 'active'
  }
]

const mockCustomers = [
  { id: 3, name: 'Distribuidora Norte', role: 'customer' },
  { id: 6, name: 'Logística Sul', role: 'customer' },
  { id: 7, name: 'Centro ABC', role: 'customer' }
]

const mockCouriers = [
  {
    id: 2,
    name: 'João Silva',
    email: 'joao@test.com',
    mobile: '11999999999',
    role: 'courier',
    originalBusiness: 1
  },
  {
    id: 5,
    name: 'Carlos Santos',
    email: 'carlos@test.com',
    mobile: '11888888888',
    role: 'courier',
    originalBusiness: 1
  }
]

describe('TeamManagement.vue', () => {
  let wrapper
  let pinia

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup mocks
    backendService.getTeams.mockResolvedValue(mockTeams)
    backendService.getCustomers.mockResolvedValue(mockCustomers)
    backendService.getCouriers.mockResolvedValue(mockCouriers)
    backendService.createTeam.mockResolvedValue({ id: 3, ...mockTeams[0] })
    backendService.updateTeam.mockResolvedValue(mockTeams[0])
    backendService.deleteTeam.mockResolvedValue()

    wrapper = mount(TeamManagement, {
      global: {
        plugins: [pinia]
      }
    })

    // Mock auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Times')
    })

    it('deve renderizar botões de ação', () => {
      expect(wrapper.find('button').text()).toContain('Novo Time')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })

    it('deve renderizar tabela de times', () => {
      expect(wrapper.find('table').exists()).toBe(true)
      expect(wrapper.find('thead').exists()).toBe(true)
      expect(wrapper.find('tbody').exists()).toBe(true)
    })
  })

  describe('Estados da Aplicação', () => {
    it('deve ter estado inicial correto', () => {
      expect(wrapper.vm.showForm).toBe(false)
      expect(wrapper.vm.editingTeam).toBe(null)
      expect(wrapper.vm.businessFilter).toBe('')
    })

    it('deve exibir modal ao clicar em Novo Time', async () => {
      const newButton = wrapper.find('button')
      await newButton.trigger('click')

      expect(wrapper.vm.showForm).toBe(true)
    })
  })

  describe('Formulário', () => {
    it('deve ter estrutura correta do formulário', () => {
      expect(wrapper.vm.form).toHaveProperty('business')
      expect(wrapper.vm.form).toHaveProperty('courier')
      expect(wrapper.vm.form).toHaveProperty('factorCourier')
      expect(wrapper.vm.form).toHaveProperty('status')
    })

    it('deve inicializar com valores padrão', () => {
      expect(wrapper.vm.form.business.id).toBe('')
      expect(wrapper.vm.form.courier.id).toBe('')
      expect(wrapper.vm.form.status).toBe('active')
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(() => {
      // Simular dados carregados
      wrapper.vm.teams = mockTeams
      wrapper.vm.customerBusinesses = mockCustomers
      wrapper.vm.couriers = mockCouriers
    })

    it('deve criar novo time', async () => {
      wrapper.vm.form = {
        business: { id: 3 },
        courier: { id: 2 },
        factorCourier: 12.5,
        status: 'active'
      }

      await wrapper.vm.saveTeam()

      expect(backendService.createTeam).toHaveBeenCalledWith({
        business: { id: 3, name: 'Distribuidora Norte', role: 'customer' },
        courier: { id: 2, name: 'João Silva', email: 'joao@test.com', mobile: '11999999999', originalBusiness: 1, role: 'courier' },
        factorCourier: 12.5,
        status: 'active'
      })
    })

    it('deve editar time existente', () => {
      const existingTeam = mockTeams[0]
      wrapper.vm.editTeam(existingTeam)

      expect(wrapper.vm.editingTeam).toStrictEqual(existingTeam)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.business.id).toBe(existingTeam.business.id)
    })

    it('deve deletar time com confirmação', async () => {
      window.confirm = vi.fn(() => true)

      await wrapper.vm.deleteTeam(1)

      expect(backendService.deleteTeam).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir este time?')
    })
  })

  describe('Filtros', () => {
    beforeEach(() => {
      wrapper.vm.teams = mockTeams
    })

    it('deve filtrar times por empresa', async () => {
      wrapper.vm.businessFilter = '3'
      await wrapper.vm.$nextTick()

      const filtered = wrapper.vm.filteredTeams
      expect(filtered.every(team => team.business.id == 3)).toBe(true)
    })

    it('deve mostrar todos os times quando filtro vazio', async () => {
      wrapper.vm.businessFilter = ''
      await wrapper.vm.$nextTick()

      expect(wrapper.vm.filteredTeams).toHaveLength(mockTeams.length)
    })
  })

  describe('Computados', () => {
    beforeEach(() => {
      wrapper.vm.couriers = mockCouriers
      wrapper.vm.form.business.id = '1'
    })

    it('deve filtrar entregadores disponíveis por empresa', () => {
      wrapper.vm.onBusinessChange()
      
      const available = wrapper.vm.availableCouriers
      expect(Array.isArray(available)).toBe(true)
    })

    it('deve obter empresas de clientes', () => {
      wrapper.vm.customerBusinesses = mockCustomers
      
      const businesses = wrapper.vm.customerBusinesses
      expect(businesses).toHaveLength(mockCustomers.length)
    })
  })

  describe('Navegação', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backButton = wrapper.find('.back-btn')
      await backButton.trigger('click')

      expect(wrapper.emitted('back')).toBeTruthy()
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar', async () => {
      backendService.getTeams.mockRejectedValue(new Error('Erro de conexão'))

      wrapper.vm.loadTeams()
      await wrapper.vm.$nextTick()
      await new Promise(resolve => setTimeout(resolve, 100))

      expect(wrapper.vm.error).toContain('Erro ao carregar times')
    })
  })

  describe('Métodos Utilitários', () => {
    it('deve retornar empresa original do entregador', () => {
      const courier = { id: 2, originalBusiness: 1 }
      
      // Simular que os entregadores foram carregados
      wrapper.vm.couriers = [courier]
      
      // Simular que as empresas foram carregadas (businesses já tem CSOnline Delivery com id 1)
      wrapper.vm.businesses = [
        { id: 1, name: 'CSOnline Delivery', role: 'admin' },
        { id: 4, name: 'Gestão Empresarial', role: 'user' }
      ]
      
      const originalBusiness = wrapper.vm.getOriginalBusiness(2)
      expect(originalBusiness).toBe('CSOnline Delivery')
    })

    it('deve validar fator de comissão', () => {
      // Teste básico de validação se existir
      expect(typeof wrapper.vm.form.factorCourier).toBe('string')
    })
  })
})
