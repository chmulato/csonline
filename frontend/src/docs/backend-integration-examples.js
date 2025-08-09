/**
 * Backend Integration Examples
 * Exemplos de como integrar os componentes Vue com o backend CSOnline
 */

// Exemplo 1: Atualizando CourierManagement para usar backend real

// ANTES (mock):
/*
const loadCouriers = async () => {
  try {
    // Simulação de dados
    couriers.value = [
      { id: 1, name: 'João Silva', factorCourier: 10.5 },
      { id: 2, name: 'Maria Santos', factorCourier: 8.0 }
    ]
  } catch (error) {
    console.error('Erro ao carregar entregadores:', error)
  }
}
*/

// DEPOIS (backend real):
/*
import { backendService } from '../services/backend.js'

const loadCouriers = async () => {
  try {
    couriers.value = await backendService.getCouriers()
  } catch (error) {
    console.error('Erro ao carregar entregadores:', error)
    // Tratamento de erro apropriado
  }
}

const saveCourier = async (courierData) => {
  try {
    if (courierData.id) {
      await backendService.updateCourier(courierData.id, courierData)
    } else {
      await backendService.createCourier(courierData)
    }
    await loadCouriers() // Recarregar lista
  } catch (error) {
    console.error('Erro ao salvar entregador:', error)
    throw error
  }
}

const deleteCourier = async (courierId) => {
  try {
    await backendService.deleteCourier(courierId)
    await loadCouriers() // Recarregar lista
  } catch (error) {
    console.error('Erro ao deletar entregador:', error)
    throw error
  }
}
*/

// Exemplo 2: Atualizando CustomerManagement para usar backend real

// ANTES (mock):
/*
const loadCustomers = async () => {
  customers.value = [
    { id: 1, name: 'Empresa ABC', factorCustomer: 15.0 },
    { id: 2, name: 'Loja XYZ', factorCustomer: 12.5 }
  ]
}
*/

// DEPOIS (backend real):
/*
const loadCustomers = async () => {
  try {
    customers.value = await backendService.getCustomers()
  } catch (error) {
    console.error('Erro ao carregar clientes:', error)
  }
}

const saveCustomer = async (customerData) => {
  try {
    if (customerData.id) {
      await backendService.updateCustomer(customerData.id, customerData)
    } else {
      await backendService.createCustomer(customerData)
    }
    await loadCustomers()
  } catch (error) {
    console.error('Erro ao salvar cliente:', error)
    throw error
  }
}
*/

// Exemplo 3: Atualizando Login Component

// ANTES (mock):
/*
const handleLogin = async () => {
  try {
    if (login.value === 'admin' && password.value === 'demo') {
      authStore.setAuth('mock-token', { name: 'Admin', role: 'ADMIN' })
      router.push('/')
    } else {
      throw new Error('Credenciais inválidas')
    }
  } catch (error) {
    errorMessage.value = error.message
  }
}
*/

// DEPOIS (backend real):
/*
const handleLogin = async () => {
  try {
    const result = await authStore.login({
      login: login.value,
      password: password.value
    })
    
    if (result) {
      router.push('/')
    }
  } catch (error) {
    errorMessage.value = error.message
  }
}
*/

// Exemplo 4: Tratamento de Erros do Backend

/*
const handleApiError = (error) => {
  switch (error.message) {
    case 'Authentication failed. Please login again.':
      router.push('/login')
      break
    case 'Acesso negado. Você não tem permissão.':
      // Mostrar mensagem de permissão negada
      break
    case 'Dados já existem ou conflito de operação.':
      // Mostrar mensagem de conflito
      break
    default:
      // Erro genérico
      console.error('Erro da API:', error.message)
  }
}
*/

// Exemplo 5: Configuração de Interceptors Globais

/*
// No main.js ou app.js
import { backendService } from './services/backend.js'
import { useAuthStore } from './stores/auth.js'

// Interceptor para logout automático em caso de 401
window.addEventListener('unhandledrejection', (event) => {
  if (event.reason.message.includes('Authentication failed')) {
    const authStore = useAuthStore()
    authStore.clearAuth()
    window.location.href = '/login'
  }
})
*/

export default {
  name: 'BackendIntegrationExamples'
}
