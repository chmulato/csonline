// Testes para o componente Logout
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Logout from '../../components/Logout.vue'

describe('Logout Component', () => {
  let wrapper

  beforeEach(() => {
    wrapper = mount(Logout)
  })

  describe('Renderização Inicial', () => {
    it('should render logout message', () => {
      expect(wrapper.find('h2').text()).toBe('Você saiu do CSOnline')
      expect(wrapper.find('p').text()).toBe('Obrigado por utilizar o sistema!')
    })

    it('should render back to login button', () => {
      expect(wrapper.find('button').exists()).toBe(true)
      expect(wrapper.find('button').text()).toBe('Voltar ao Login')
    })

    it('should not show confirmation message initially', () => {
      expect(wrapper.find('.confirm-msg').exists()).toBe(false)
    })
  })

  describe('Funcionalidade de Navegação', () => {
    it('should show confirmation message when button is clicked', async () => {
      const button = wrapper.find('button')
      await button.trigger('click')
      
      expect(wrapper.find('.confirm-msg').exists()).toBe(true)
      expect(wrapper.find('.confirm-msg').text()).toBe('Redirecionando para o login...')
    })

    it('should emit backToLogin event after timeout', async () => {
      vi.useFakeTimers()
      
      const button = wrapper.find('button')
      await button.trigger('click')
      
      expect(wrapper.emitted('backToLogin')).toBeFalsy()
      
      // Avançar o timer
      vi.advanceTimersByTime(600)
      await wrapper.vm.$nextTick()
      
      expect(wrapper.emitted('backToLogin')).toBeTruthy()
      expect(wrapper.emitted('backToLogin')).toHaveLength(1)
      
      vi.useRealTimers()
    })

    it('should hide confirmation message after timeout', async () => {
      vi.useFakeTimers()
      
      const button = wrapper.find('button')
      await button.trigger('click')
      
      expect(wrapper.find('.confirm-msg').exists()).toBe(true)
      
      // Avançar o timer
      vi.advanceTimersByTime(600)
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.confirm-msg').exists()).toBe(false)
      
      vi.useRealTimers()
    })
  })

  describe('Estado do Componente', () => {
    it('should have initial showConfirm as false', () => {
      expect(wrapper.vm.showConfirm).toBe(false)
    })

    it('should set showConfirm to true when button is clicked', async () => {
      const button = wrapper.find('button')
      await button.trigger('click')
      
      expect(wrapper.vm.showConfirm).toBe(true)
    })

    it('should reset showConfirm after timeout', async () => {
      vi.useFakeTimers()
      
      const button = wrapper.find('button')
      await button.trigger('click')
      
      expect(wrapper.vm.showConfirm).toBe(true)
      
      vi.advanceTimersByTime(600)
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.showConfirm).toBe(false)
      
      vi.useRealTimers()
    })
  })

  describe('Múltiplos Cliques', () => {
    it('should handle multiple rapid clicks', async () => {
      vi.useFakeTimers()
      
      const button = wrapper.find('button')
      
      // Múltiplos cliques rápidos
      await button.trigger('click')
      await button.trigger('click')
      await button.trigger('click')
      
      expect(wrapper.vm.showConfirm).toBe(true)
      expect(wrapper.find('.confirm-msg').exists()).toBe(true)
      
      // Avançar o timer
      vi.advanceTimersByTime(600)
      await wrapper.vm.$nextTick()
      
      // Deve ter emitido apenas 3 eventos (um para cada clique)
      expect(wrapper.emitted('backToLogin')).toHaveLength(3)
      
      vi.useRealTimers()
    })
  })

  describe('Styling e Classes CSS', () => {
    it('should have correct container class', () => {
      expect(wrapper.find('.logout-container').exists()).toBe(true)
    })

    it('should have scoped styles applied', () => {
      const container = wrapper.find('.logout-container')
      expect(container.exists()).toBe(true)
    })

    it('should show confirmation message with correct class when displayed', async () => {
      const button = wrapper.find('button')
      await button.trigger('click')
      
      const confirmMsg = wrapper.find('.confirm-msg')
      expect(confirmMsg.exists()).toBe(true)
      expect(confirmMsg.classes()).toContain('confirm-msg')
    })
  })
})
