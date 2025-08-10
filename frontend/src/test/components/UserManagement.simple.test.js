import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import UserManagement from '../../components/UserManagement.vue'
import { useAuthStore } from '../../stores/auth.js'

// Mock do backend service
vi.mock('../../services/backend.js', () => ({
  backendService: {
    getUsers: vi.fn(() => Promise.resolve([])),
    createUser: vi.fn(),
    updateUser: vi.fn(),
    deleteUser: vi.fn()
  }
}))

describe('UserManagement - Testes Básicos', () => {
  let wrapper
  let pinia

  beforeEach(async () => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Setup auth store
    const authStore = useAuthStore()
    authStore.token = 'valid-token'
    authStore.userRole = 'ADMIN'

    wrapper = mount(UserManagement, {
      global: {
        plugins: [pinia]
      }
    })

    await wrapper.vm.$nextTick()
  })

  describe('Renderização Básica', () => {
    it('deve renderizar sem erros', () => {
      expect(wrapper.exists()).toBe(true)
    })

    it('deve ter o título correto', () => {
      expect(wrapper.text()).toContain('Gestão de Usuários')
    })

    it('deve ter botões de ação', () => {
      expect(wrapper.text()).toContain('Novo Usuário')
      expect(wrapper.text()).toContain('Voltar')
    })

    it('deve ter filtros de busca', () => {
      expect(wrapper.find('input[placeholder*="Buscar"]').exists()).toBe(true)
      expect(wrapper.find('select').exists()).toBe(true)
    })

    it('deve ter estrutura de tabela', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      expect(wrapper.text()).toContain('Nome')
      expect(wrapper.text()).toContain('Email')
      expect(wrapper.text()).toContain('Perfil')
    })
  })

  describe('Funcionalidade de Modal', () => {
    it('deve mostrar modal ao clicar em Novo Usuário', async () => {
      const novoUsuarioBtn = wrapper.find('button:first-child')
      await novoUsuarioBtn.trigger('click')
      
      expect(wrapper.vm.showForm).toBe(true)
    })
  })

  describe('Emissão de Eventos', () => {
    it('deve emitir evento back ao clicar em voltar', async () => {
      const backBtn = wrapper.find('.back-btn')
      await backBtn.trigger('click')
      
      expect(wrapper.emitted('back')).toBeTruthy()
    })
  })

  describe('Propriedades Reativas', () => {
    it('deve ter propriedades reativas básicas', () => {
      expect(wrapper.vm.loading).toBeDefined()
      expect(wrapper.vm.error).toBeDefined()
      expect(wrapper.vm.showForm).toBeDefined()
      expect(wrapper.vm.searchTerm).toBeDefined()
      expect(wrapper.vm.roleFilter).toBeDefined()
    })
  })
})
