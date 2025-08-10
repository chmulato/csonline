import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import MainLayout from '../../components/MainLayout.vue'
import { createMockAuthStore } from '../helpers/testUtils'

// Mock vue-router
const mockRouter = {
  push: vi.fn(),
  back: vi.fn(),
  currentRoute: { value: { path: '/dashboard' } }
}

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter
}))

describe('MainLayout Component', () => {
  let wrapper
  let authStore
  let pinia

  beforeEach(() => {
    // Configurar auth store
    const mockAuth = createMockAuthStore({ role: 'ADMIN' })
    pinia = mockAuth.pinia
    authStore = mockAuth.authStore
    
    vi.clearAllMocks()

    wrapper = mount(MainLayout, {
      global: {
        plugins: [pinia],
        mocks: {
          $router: mockRouter
        }
      }
    })
  })

  describe('Renderização Inicial', () => {
    it('deve renderizar o título principal', () => {
      expect(wrapper.find('h1').text()).toBe('CSOnline - Gestão CD')
    })

    it('deve mostrar informações do usuário logado', () => {
      const userInfo = wrapper.find('.user-info')
      expect(userInfo.text()).toContain('Admin User')
      expect(userInfo.text()).toContain('ADMIN')
    })

    it('deve renderizar o botão de menu', () => {
      const menuBtn = wrapper.find('.menu-btn')
      expect(menuBtn.exists()).toBe(true)
      expect(menuBtn.text()).toBe('☰')
    })

    it('deve mostrar mensagem de boas-vindas', () => {
      expect(wrapper.find('h2').text()).toBe('Bem-vindo ao CSOnline!')
      expect(wrapper.find('main p').text()).toBe('Sistema de Gestão de Centros de Distribuição')
    })
  })

  describe('Menu de Navegação', () => {
    it('deve mostrar/esconder drawer ao clicar no menu', async () => {
      expect(wrapper.find('.drawer').exists()).toBe(false)
      
      await wrapper.find('.menu-btn').trigger('click')
      expect(wrapper.find('.drawer').exists()).toBe(true)
      
      await wrapper.find('.menu-btn').trigger('click')
      expect(wrapper.find('.drawer').exists()).toBe(false)
    })

    it('deve exibir opções de menu para usuário ADMIN', async () => {
      await wrapper.find('.menu-btn').trigger('click')
      
      const navItems = wrapper.findAll('.drawer li a')
      const navTexts = navItems.map(item => item.text())
      
      expect(navTexts).toContain('Entregas')
      expect(navTexts).toContain('Centros de Distribuição')
      expect(navTexts).toContain('Entregadores')
      expect(navTexts).toContain('Times')
      expect(navTexts).toContain('Usuários')
      expect(navTexts).toContain('Preços')
      expect(navTexts).toContain('SMS/WhatsApp')
      expect(navTexts).toContain('Sair')
    })
  })

  describe('Dashboard Cards', () => {
    it('deve renderizar cards do dashboard', () => {
      const cards = wrapper.findAll('.dashboard-cards .card')
      expect(cards.length).toBeGreaterThan(0)
    })

    it('deve exibir card de Entregas para ADMIN', () => {
      const cards = wrapper.findAll('.card')
      const deliveryCard = cards.find(card => card.find('h3').text() === 'Entregas')
      expect(deliveryCard).toBeTruthy()
    })

    it('deve exibir card de Centros de Distribuição para ADMIN', () => {
      const cards = wrapper.findAll('.card')
      const customerCard = cards.find(card => card.find('h3').text() === 'Centros de Distribuição')
      expect(customerCard).toBeTruthy()
    })
  })

  describe('Permissões por Perfil', () => {
    it('deve mostrar todas as opções para ADMIN', async () => {
      await wrapper.find('.menu-btn').trigger('click')
      
      expect(wrapper.find('a').element.textContent).toContain('Entregas')
      expect(wrapper.text()).toContain('Usuários')
      expect(wrapper.text()).toContain('Preços')
    })
  })
})
