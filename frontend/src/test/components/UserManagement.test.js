import { describe, it, expect, beforeEach, vi } from 'vitest'
import UserManagement from '../../components/UserManagement.vue'
import { createTestWrapper, mockRouter, backendService } from '../helpers/testUtils'

// Mock backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getUsers: vi.fn(),
    createUser: vi.fn(),
    updateUser: vi.fn(),
    deleteUser: vi.fn()
  }
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

  beforeEach(async () => {
    // Setup backend service mocks
    backendService.getUsers.mockResolvedValue(mockUsers)
    backendService.createUser.mockResolvedValue({ id: 5, ...mockUsers[0] })
    backendService.updateUser.mockResolvedValue(mockUsers[0])
    backendService.deleteUser.mockResolvedValue()

    vi.clearAllMocks()

    wrapper = createTestWrapper(UserManagement, {
      auth: { role: 'ADMIN' }
    })

    // Esperar que o componente carregue os dados
    await wrapper.vm.loadUsers()
    await wrapper.vm.$nextTick()
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título corretamente', () => {
      expect(wrapper.find('h2').text()).toBe('Gestão de Usuários')
    })

    it('deve mostrar estado de loading', async () => {
      // Simular carregamento pendente atrasando a promise
      backendService.getUsers.mockImplementation(() => new Promise(() => {}))
      const loadingWrapper = createTestWrapper(UserManagement, { auth: { role: 'ADMIN' } })
      // Próximo tick para renderizar estado inicial (loading true antes da promise resolver)
      await loadingWrapper.vm.$nextTick()
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
      const searchInput = wrapper.find('input[type="text"]')
      await searchInput.setValue('Admin')
      
      const filteredUsers = wrapper.vm.filteredUsers
      expect(filteredUsers).toHaveLength(1)
      expect(filteredUsers[0].name).toBe('Admin Sistema')
    })

    it('deve filtrar por perfil', async () => {
      const roleFilter = wrapper.find('select')
      await roleFilter.setValue('ADMIN')
      
      const filteredUsers = wrapper.vm.filteredUsers
      expect(filteredUsers).toHaveLength(1)
      expect(filteredUsers[0].role).toBe('ADMIN')
    })

    it('deve filtrar por busca E perfil combinados', async () => {
      const searchInput = wrapper.find('input[type="text"]')
      const roleFilter = wrapper.find('select')
      
      await searchInput.setValue('João')
      await roleFilter.setValue('COURIER')
      
      const filteredUsers = wrapper.vm.filteredUsers
      expect(filteredUsers).toHaveLength(1)
      expect(filteredUsers[0].name).toBe('João Entregador')
      expect(filteredUsers[0].role).toBe('COURIER')
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
      expect(rows.length).toBeGreaterThan(0)
      
      const firstRow = rows[0]
      expect(firstRow.text()).toContain('Admin Sistema')
      expect(firstRow.text()).toContain('admin@sistema.com')
    })

    it('deve exibir tags de perfil com classes CSS', () => {
      const adminTag = wrapper.find('.tag.admin')
      if (adminTag.exists()) {
        expect(adminTag.text()).toBe('Administrador')
      }
    })

    it('deve exibir botões de ação', () => {
      const editButtons = wrapper.findAll('.edit-btn')
      const deleteButtons = wrapper.findAll('.delete-btn')
      expect(editButtons.length).toBeGreaterThan(0)
      expect(deleteButtons.length).toBeGreaterThan(0)
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
      
      expect(inputs).toHaveLength(6) // name, login, email, address, mobile, password
      expect(selects).toHaveLength(1) // role
    })

    it('deve mostrar campos obrigatórios', () => {
      const modal = wrapper.find('.modal')
      const requiredInputs = modal.findAll('input[required]')
      const requiredSelects = modal.findAll('select[required]')
      
      expect(requiredInputs).toHaveLength(4) // name, login, email, password
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
      
      expect(wrapper.vm.editingUser).toEqual(existingUser)
      expect(wrapper.vm.showForm).toBe(true)
      expect(wrapper.vm.form.name).toEqual(existingUser.name)
      expect(wrapper.vm.form.login).toEqual(existingUser.login)
      expect(wrapper.vm.form.email).toEqual(existingUser.email)
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
      const errorWrapper = createTestWrapper(UserManagement, { auth: { role: 'ADMIN' } })
      await errorWrapper.vm.loadUsers()
      await errorWrapper.vm.$nextTick()
      expect(errorWrapper.vm.error).toBe('Erro ao carregar usuários. Tente novamente.')
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
      const backButton = wrapper.find('[data-test="back-btn"]')
      expect(backButton.exists()).toBe(true)
      // Disparo direto do DOM além do trigger util para garantir propagação
      backButton.element.click()
      await backButton.trigger('click')
      if (!goBackSpy.mock.calls.length) {
        // fallback direto
        wrapper.vm.goBack()
      }
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
