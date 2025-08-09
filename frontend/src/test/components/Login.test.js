// Testes para o componente Login
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Login from '../../components/Login.vue'
import { useAuthStore } from '../../stores/auth'

// Mock fetch global
global.fetch = vi.fn()

describe('Login Component', () => {
  let wrapper
  let authStore

  beforeEach(() => {
    // Criar nova instância do Pinia para cada teste
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Inicializar auth store
    authStore = useAuthStore()
    
    // Mock da função setAuth
    authStore.setAuth = vi.fn((data) => {
      authStore.token = 'valid-token'
      authStore.user = data
    })

    // Mock fetch padrão (sucesso)
    fetch.mockResolvedValue({
      ok: true,
      json: async () => ({
        id: 1,
        name: 'Admin User',
        login: 'admin',
        role: 'ADMIN',
        token: 'valid-token'
      })
    })

    wrapper = mount(Login, {
      global: {
        plugins: [pinia]
      }
    })
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('Renderização Inicial', () => {
    it('should render login form correctly', () => {
      expect(wrapper.find('h2').text()).toBe('CSOnline - Login')
      expect(wrapper.find('input[type="text"]').exists()).toBe(true)
      expect(wrapper.find('input[type="password"]').exists()).toBe(true)
      expect(wrapper.find('button[type="submit"]').exists()).toBe(true)
    })

    it('should have empty form fields initially', () => {
      expect(wrapper.find('input[type="text"]').element.value).toBe('')
      expect(wrapper.find('input[type="password"]').element.value).toBe('')
    })

    it('should show "Entrar" button text initially', () => {
      expect(wrapper.find('button[type="submit"]').text()).toBe('Entrar')
    })
  })

  describe('Interação do Usuário', () => {
    it('should update username when typing', async () => {
      const usernameInput = wrapper.find('input[type="text"]')
      await usernameInput.setValue('testuser')
      expect(wrapper.vm.username).toBe('testuser')
    })

    it('should update password when typing', async () => {
      const passwordInput = wrapper.find('input[type="password"]')
      await passwordInput.setValue('testpass')
      expect(wrapper.vm.password).toBe('testpass')
    })

    it('should disable form fields when loading', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('input[type="text"]').attributes('disabled')).toBeDefined()
      expect(wrapper.find('input[type="password"]').attributes('disabled')).toBeDefined()
      expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeDefined()
    })

    it('should show loading text when submitting', async () => {
      wrapper.vm.loading = true
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('button[type="submit"]').text()).toBe('Entrando...')
    })
  })

  describe('Autenticação', () => {
    it('should call fetch API with correct credentials', async () => {
      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('admin')
      await wrapper.find('input[type="password"]').setValue('admin123')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(fetch).toHaveBeenCalledWith('/csonline/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          login: 'admin',
          password: 'admin123'
        })
      })
    })

    it('should call authStore.setAuth on successful login', async () => {
      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('admin')
      await wrapper.find('input[type="password"]').setValue('admin123')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(authStore.setAuth).toHaveBeenCalledWith({
        id: 1,
        name: 'Admin User',
        login: 'admin',
        role: 'ADMIN',
        token: 'valid-token'
      })
    })

    it('should emit login event on successful authentication', async () => {
      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('admin')
      await wrapper.find('input[type="password"]').setValue('admin123')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(wrapper.emitted('login')).toBeTruthy()
      expect(wrapper.emitted('login')[0][0]).toEqual({
        id: 1,
        name: 'Admin User',
        login: 'admin',
        role: 'ADMIN',
        token: 'valid-token'
      })
    })

    it('should show error message for invalid credentials', async () => {
      // Mock de resposta de erro
      fetch.mockResolvedValueOnce({
        ok: false,
        json: async () => ({ error: 'Usuário ou senha inválidos' })
      })

      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('wrong')
      await wrapper.find('input[type="password"]').setValue('credentials')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.message).toContain('Usuário ou senha inválidos')
      expect(wrapper.vm.messageType).toBe('error')
    })

    it('should show network error message', async () => {
      // Mock de erro de rede
      fetch.mockRejectedValueOnce(new Error('Network Error'))

      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('admin')
      await wrapper.find('input[type="password"]').setValue('admin123')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.message).toBe('Erro de conexão com o servidor')
      expect(wrapper.vm.messageType).toBe('error')
    })

    it('should clear loading state after login attempt', async () => {
      const form = wrapper.find('form')
      
      await wrapper.find('input[type="text"]').setValue('admin')
      await wrapper.find('input[type="password"]').setValue('admin123')
      
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.loading).toBe(false)
    })

    it('should show validation error for empty fields', async () => {
      const form = wrapper.find('form')
      
      // Deixar campos vazios
      await form.trigger('submit.prevent')
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.message).toBe('Por favor, preencha todos os campos')
      expect(wrapper.vm.messageType).toBe('error')
    })
  })

  describe('Validação de Formulário', () => {
    it('should require username field', () => {
      const usernameInput = wrapper.find('input[type="text"]')
      expect(usernameInput.attributes('required')).toBeDefined()
    })

    it('should require password field', () => {
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.attributes('required')).toBeDefined()
    })

    it('should have correct placeholders', () => {
      expect(wrapper.find('input[type="text"]').attributes('placeholder')).toBe('Usuário')
      expect(wrapper.find('input[type="password"]').attributes('placeholder')).toBe('Senha')
    })
  })

  describe('Mensagens de Status', () => {
    it('should not show message initially', () => {
      expect(wrapper.find('.error').exists()).toBe(false)
      expect(wrapper.find('.success').exists()).toBe(false)
    })

    it('should show error message with correct class', async () => {
      wrapper.vm.message = 'Erro de teste'
      wrapper.vm.messageType = 'error'
      await wrapper.vm.$nextTick()
      
      const messageElement = wrapper.find('.error')
      expect(messageElement.exists()).toBe(true)
      expect(messageElement.text()).toBe('Erro de teste')
    })

    it('should show success message with correct class', async () => {
      wrapper.vm.message = 'Sucesso de teste'
      wrapper.vm.messageType = 'success'
      await wrapper.vm.$nextTick()
      
      const messageElement = wrapper.find('.success')
      expect(messageElement.exists()).toBe(true)
      expect(messageElement.text()).toBe('Sucesso de teste')
    })
  })
})
