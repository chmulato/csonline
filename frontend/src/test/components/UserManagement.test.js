import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserManagement from '../../components/UserManagement.vue'
import { createMockAuthStore } from '../helpers/testUtils'

// Mock backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getUsers: vi.fn(),
    createUser: vi.fn(),
    updateUser: vi.fn(),
    deleteUser: vi.fn()
  }
}))

// Import the mocked service after mock declaration
import { backendService } from '../../services/backend.js'

// Mock vue-router
const mockRouter = {
  push: vi.fn(),
  back: vi.fn(),
  currentRoute: { value: { path: '/users' } }
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

// Mock global confirm
global.confirm = vi.fn(() => true)

// Mock sample data
const mockUsers = [
  {
    id: 1,
    name: 'Admin Sistema',
    login: 'admin',
    email: 'admin@sistema.com',
    role: 'ADMIN',
    address: 'Rua Admin, 123',
    mobile: '11999999999'
  },
  {
    id: 2,
    name: 'Empresa Teste',
    login: 'empresa',
    email: 'empresa@teste.com',
    role: 'BUSINESS',
    address: 'Av. Empresa, 456',
    mobile: '11888888888'
  },
  {
    id: 3,
    name: 'João Entregador',
    login: 'joao',
    email: 'joao@entrega.com',
    role: 'COURIER',
    address: 'Rua Entregador, 789',
    mobile: '11777777777'
  },
  {
    id: 4,
    name: 'Maria Cliente',
    login: 'maria',
    email: 'maria@cliente.com',
    role: 'CUSTOMER',
    address: 'Rua Cliente, 321',
    mobile: '11666666666'
  }
]

describe('UserManagement.vue', () => {
  let wrapper
  let authStore
  let pinia

  beforeEach(async () => {
    // Configurar auth store
    const mockAuth = createMockAuthStore({ role: 'ADMIN' })
    pinia = mockAuth.pinia
    authStore = mockAuth.authStore
    
    // Setup backend service mocks
    backendService.getUsers.mockResolvedValue(mockUsers)
    backendService.createUser.mockResolvedValue({ id: 5, ...mockUsers[0] })
    backendService.updateUser.mockResolvedValue(mockUsers[0])
    backendService.deleteUser.mockResolvedValue()

    vi.clearAllMocks()

    wrapper = mount(UserManagement, {
      global: {
        plugins: [pinia],
        mocks: {
          $router: mockRouter
        }
      }
    })

    // Wait for component to load data
    await wrapper.vm.$nextTick()
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Usuários')
    })

    it('deve mostrar estado de loading', async () => {
      const loadingWrapper = mount(UserManagement, {
        global: {
          plugins: [pinia],
          mocks: { $router: mockRouter }
        },
        data() {
          return { loading: true }
        }
      })
      
      expect(loadingWrapper.find('.loading').exists()).toBe(true)
      expect(loadingWrapper.find('.loading').text()).toBe('Carregando usuários...')
    })

    it('deve exibir botões de ação', () => {
      expect(wrapper.find('button').text()).toContain('Novo Usuário')
      expect(wrapper.find('.back-btn').text()).toBe('Voltar')
    })
  })

  describe('Filtros e Busca', () => {
    beforeEach(async () => {
      await wrapper.vm.loadUsers()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar campo de busca', () => {
      const searchInput = wrapper.find('input[type="text"]')
      expect(searchInput.exists()).toBe(true)
      expect(searchInput.attributes('placeholder')).toBe('Buscar usuários...')
    })

    it('deve renderizar filtro de perfis', () => {
      const roleFilter = wrapper.find('select')
      expect(roleFilter.exists()).toBe(true)
      
      const options = roleFilter.findAll('option')
      expect(options).toHaveLength(5)
      expect(options[0].text()).toBe('Todos os perfis')
      expect(options[1].text()).toBe('Administradores')
      expect(options[2].text()).toBe('Centros de Distribuição')
    })

    it('deve filtrar por termo de busca', async () => {
      wrapper.vm.searchTerm = 'Admin'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredUsers).toHaveLength(1)
      expect(wrapper.vm.filteredUsers[0].name).toBe('Admin Sistema')
    })

    it('deve filtrar por perfil', async () => {
      wrapper.vm.roleFilter = 'BUSINESS'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredUsers).toHaveLength(1)
      expect(wrapper.vm.filteredUsers[0].role).toBe('BUSINESS')
    })

    it('deve filtrar por busca E perfil combinados', async () => {
      wrapper.vm.searchTerm = 'João'
      wrapper.vm.roleFilter = 'COURIER'
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.filteredUsers).toHaveLength(1)
      expect(wrapper.vm.filteredUsers[0].name).toBe('João Entregador')
      expect(wrapper.vm.filteredUsers[0].role).toBe('COURIER')
    })
  })

  describe('Listagem de Usuários', () => {
    beforeEach(async () => {
      await wrapper.vm.loadUsers()
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar a tabela de usuários', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      
      const headers = wrapper.findAll('th')
      expect(headers).toHaveLength(7)
      expect(headers[0].text()).toBe('ID')
      expect(headers[1].text()).toBe('Nome')
      expect(headers[2].text()).toBe('Login')
      expect(headers[3].text()).toBe('Email')
      expect(headers[4].text()).toBe('Perfil')
    })

    it('deve exibir usuários na tabela', () => {
      const rows = wrapper.findAll('tbody tr')
      expect(rows).toHaveLength(4)
      
      // Primeira linha
      const firstRow = rows[0]
      const cells = firstRow.findAll('td')
      expect(cells[0].text()).toBe('1')
      expect(cells[1].text()).toBe('Admin Sistema')
      expect(cells[2].text()).toBe('admin')
      expect(cells[3].text()).toBe('admin@sistema.com')
    })

    it('deve exibir tags de perfil com classes CSS', () => {
      const rows = wrapper.findAll('tbody tr')
      const firstRowRole = rows[0].findAll('td')[4]
      const roleTag = firstRowRole.find('.role-tag')
      
      expect(roleTag.exists()).toBe(true)
      expect(roleTag.classes()).toContain('admin')
    })

    it('deve exibir botões de ação', () => {
      const rows = wrapper.findAll('tbody tr')
      const firstRowActions = rows[0].findAll('td')[6]
      const buttons = firstRowActions.findAll('button')
      
      expect(buttons).toHaveLength(2)
      expect(buttons[0].text()).toBe('Editar')
      expect(buttons[1].text()).toBe('Excluir')
    })
  })

  describe('Modal de Formulário', () => {
    it('deve abrir modal ao clicar em Novo Usuário', async () => {
      await wrapper.vm.loadUsers()
      await wrapper.vm.$nextTick()
      
      const newButton = wrapper.find('button')
      await newButton.trigger('click')
      
      expect(wrapper.find('.modal').exists()).toBe(true)
      expect(wrapper.find('.modal h3').text()).toBe('Novo Usuário')
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
        name: 'Teste',
        login: 'teste',
        email: 'teste@email.com'
      }
      
      wrapper.vm.cancel()
      
      expect(wrapper.vm.form.name).toBe('')
      expect(wrapper.vm.form.login).toBe('')
      expect(wrapper.vm.form.email).toBe('')
    })
  })

  describe('Formulário de Usuário', () => {
    beforeEach(async () => {
      await wrapper.vm.loadUsers()
      wrapper.vm.showForm = true
      await wrapper.vm.$nextTick()
    })

    it('deve renderizar campos do formulário', () => {
      const modal = wrapper.find('.modal')
      const inputs = modal.findAll('input')
      const selects = modal.findAll('select')
      
      expect(inputs).toHaveLength(5) // name, login, email, address, mobile, password
      expect(selects).toHaveLength(1) // role
    })

    it('deve mostrar campos obrigatórios', () => {
      const modal = wrapper.find('.modal')
      const requiredInputs = modal.findAll('input[required]')
      const requiredSelects = modal.findAll('select[required]')
      
      expect(requiredInputs).toHaveLength(3) // name, login, email, password
      expect(requiredSelects).toHaveLength(1) // role
    })

    it('deve ter opções de perfil corretas', () => {
      const roleSelect = wrapper.find('.modal select')
      const options = roleSelect.findAll('option')
      
      expect(options).toHaveLength(5)
      expect(options[0].text()).toBe('Selecione o perfil')
      expect(options[1].text()).toBe('Administrador')
      expect(options[2].text()).toBe('Centro de Distribuição')
      expect(options[3].text()).toBe('Entregador')
      expect(options[4].text()).toBe('Cliente')
    })

    it('deve ajustar placeholder da senha para edição', async () => {
      wrapper.vm.editingUser = mockUsers[0]
      await wrapper.vm.$nextTick()
      
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.attributes('placeholder')).toBe('Nova senha (deixe vazio para manter)')
      expect(passwordInput.attributes('required')).toBeUndefined()
    })
  })

  describe('Operações CRUD', () => {
    beforeEach(async () => {
      await wrapper.vm.loadUsers()
      await wrapper.vm.$nextTick()
    })

    it('deve criar novo usuário', async () => {
      const newUser = {
        name: 'Novo Usuario',
        login: 'novo',
        email: 'novo@email.com',
        role: 'CUSTOMER',
        address: 'Rua Nova',
        mobile: '11555555555',
        password: '123456'
      }

      wrapper.vm.form = newUser
      await wrapper.vm.saveUser()
      
      expect(backendService.createUser).toHaveBeenCalledWith(newUser)
    })

    it('deve editar usuário existente', async () => {
      const existingUser = mockUsers[0]
      wrapper.vm.editUser(existingUser)
      
      expect(wrapper.vm.editingUser).toBe(existingUser)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.name).toBe(existingUser.name)
      expect(wrapper.vm.form.login).toBe(existingUser.login)
      expect(wrapper.vm.form.email).toBe(existingUser.email)
    })

    it('deve atualizar usuário', async () => {
      const updatedUser = { 
        id: 1,
        name: 'Admin Sistema Updated',
        login: 'admin',
        email: 'admin@sistema.com',
        role: 'ADMIN',
        address: 'Rua Admin Updated, 123',
        mobile: '11999999999'
      }
      
      wrapper.vm.form = updatedUser
      wrapper.vm.editingUser = mockUsers[0]
      
      await wrapper.vm.saveUser()
      
      expect(backendService.updateUser).toHaveBeenCalledWith(1, updatedUser)
    })

    it('deve excluir usuário com confirmação', async () => {
      window.confirm = vi.fn(() => true)
      
      await wrapper.vm.deleteUser(1)
      
      expect(backendService.deleteUser).toHaveBeenCalledWith(1)
      expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir este usuário?')
    })

    it('não deve excluir se usuário cancelar', async () => {
      window.confirm = vi.fn(() => false)
      
      await wrapper.vm.deleteUser(1)
      
      expect(backendService.deleteUser).not.toHaveBeenCalled()
    })
  })

  describe('Métodos Utilitários', () => {
    it('deve retornar label correta para perfis', () => {
      expect(wrapper.vm.getRoleLabel('ADMIN')).toBe('Administrador')
      expect(wrapper.vm.getRoleLabel('BUSINESS')).toBe('Centro de Distribuição')
      expect(wrapper.vm.getRoleLabel('COURIER')).toBe('Entregador')
      expect(wrapper.vm.getRoleLabel('CUSTOMER')).toBe('Cliente')
    })

    it('deve retornar valor padrão para perfil desconhecido', () => {
      expect(wrapper.vm.getRoleLabel('UNKNOWN')).toBe('UNKNOWN')
    })
  })

  describe('Validação', () => {
    beforeEach(() => {
      wrapper.vm.showForm = true
    })

    it('deve validar campos obrigatórios para novo usuário', async () => {
      wrapper.vm.form = {}
      
      const form = wrapper.find('form')
      await form.trigger('submit.prevent')
      
      // Campos obrigatórios devem estar vazios
      expect(wrapper.vm.form.name).toBeFalsy()
      expect(wrapper.vm.form.login).toBeFalsy()
      expect(wrapper.vm.form.email).toBeFalsy()
      expect(wrapper.vm.form.role).toBeFalsy()
    })

    it('deve validar email', () => {
      const emailInput = wrapper.find('input[type="email"]')
      expect(emailInput.exists()).toBe(true)
    })
  })

  describe('Tratamento de Erros', () => {
    it('deve exibir erro quando falha ao carregar usuários', async () => {
      backendService.getUsers.mockRejectedValue(new Error('Erro de API'))
      
      const errorWrapper = mount(UserManagement, {
        global: {
          plugins: [pinia],
          mocks: { $router: mockRouter }
        }
      })

      await errorWrapper.vm.loadUsers()
      await errorWrapper.vm.$nextTick()
      
      expect(errorWrapper.find('.error').exists()).toBe(true)
      expect(errorWrapper.find('.error').text()).toContain('Erro de API')
    })

    it('deve tratar erro ao salvar usuário', async () => {
      backendService.createUser.mockRejectedValue(new Error('Erro ao criar'))
      
      wrapper.vm.form = {
        name: 'Teste',
        login: 'teste',
        email: 'teste@email.com',
        role: 'CUSTOMER',
        password: '123456'
      }
      
      await wrapper.vm.saveUser()
      
      expect(wrapper.vm.error).toBeTruthy()
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

  describe('Estados da UI', () => {
    it('deve desabilitar botão salvar durante loading', async () => {
      wrapper.vm.showForm = true
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      
      const saveButton = wrapper.find('button[type="submit"]')
      expect(saveButton.attributes('disabled')).toBeDefined()
      expect(saveButton.text()).toBe('Salvando...')
    })

    it('deve mostrar texto normal quando não está loading', async () => {
      wrapper.vm.showForm = true
      wrapper.vm.loading = false
      await wrapper.vm.$nextTick()
      
      const saveButton = wrapper.find('button[type="submit"]')
      expect(saveButton.attributes('disabled')).toBeUndefined()
      expect(saveButton.text()).toBe('Salvar')
    })

    it('deve exibir N/A para endereço vazio', async () => {
      const userWithoutAddress = { ...mockUsers[0], address: null }
      wrapper.vm.users = [userWithoutAddress]
      await wrapper.vm.$nextTick()
      
      const rows = wrapper.findAll('tbody tr')
      const addressCell = rows[0].findAll('td')[5]
      expect(addressCell.text()).toBe('N/A')
    })
  })
})
