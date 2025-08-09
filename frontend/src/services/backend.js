/**
 * Backend Integration Service
 * Serviço para comunicação real com o backend CSOnline
 */

import { 
  API_CONFIG, 
  DEFAULT_HEADERS, 
  STATUS_CODES, 
  ERROR_MESSAGES,
  RETRY_CONFIG,
  formatDataForBackend,
  debugLog
} from '../config/backend.js'
import { useAuthStore } from '../stores/auth.js'

export class BackendService {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL
    this.retryCount = 0
  }

  /**
   * Método base para requisições HTTP
   */
  async request(endpoint, options = {}) {
    const authStore = useAuthStore()
    
    // Verificar autenticação se necessário
    if (authStore.token && authStore.isTokenExpired()) {
      authStore.clearAuth()
      throw new Error(ERROR_MESSAGES.UNAUTHORIZED)
    }

    const url = `${this.baseURL}${endpoint}`
    const config = {
      headers: {
        ...DEFAULT_HEADERS,
        ...options.headers
      },
      ...options
    }

    // Adicionar token de autenticação
    if (authStore.token && endpoint !== API_CONFIG.ENDPOINTS.LOGIN) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }

    debugLog(`${config.method || 'GET'} ${url}`, config.body ? JSON.parse(config.body) : null)

    try {
      const response = await this.fetchWithRetry(url, config)
      return await this.handleResponse(response)
    } catch (error) {
      debugLog(`ERROR ${config.method || 'GET'} ${url}:`, error.message)
      throw this.handleError(error)
    }
  }

  /**
   * Fetch com retry automático
   */
  async fetchWithRetry(url, config, retryCount = 0) {
    try {
      const response = await fetch(url, config)
      return response
    } catch (error) {
      if (retryCount < RETRY_CONFIG.MAX_RETRIES && this.shouldRetry(error)) {
        const delay = RETRY_CONFIG.RETRY_DELAY * Math.pow(RETRY_CONFIG.RETRY_MULTIPLIER, retryCount)
        debugLog(`Retrying request in ${delay}ms (attempt ${retryCount + 1}/${RETRY_CONFIG.MAX_RETRIES})`)
        
        await new Promise(resolve => setTimeout(resolve, delay))
        return this.fetchWithRetry(url, config, retryCount + 1)
      }
      throw error
    }
  }

  /**
   * Verificar se deve tentar novamente
   */
  shouldRetry(error) {
    return error.name === 'TypeError' || // Network error
           error.message.includes('fetch')
  }

  /**
   * Processar resposta do backend
   */
  async handleResponse(response) {
    debugLog(`Response ${response.status}`, { url: response.url, ok: response.ok })

    // Tratar diferentes status codes
    switch (response.status) {
      case STATUS_CODES.OK:
      case STATUS_CODES.CREATED:
        try {
          return await response.json()
        } catch {
          return {} // Retornar objeto vazio se não for JSON
        }
        
      case STATUS_CODES.NO_CONTENT:
        return null
        
      case STATUS_CODES.UNAUTHORIZED:
        const authStore = useAuthStore()
        authStore.clearAuth()
        throw new Error(ERROR_MESSAGES.UNAUTHORIZED)
        
      case STATUS_CODES.FORBIDDEN:
        throw new Error(ERROR_MESSAGES.FORBIDDEN)
        
      case STATUS_CODES.NOT_FOUND:
        throw new Error(ERROR_MESSAGES.NOT_FOUND)
        
      case STATUS_CODES.CONFLICT:
        try {
          const errorData = await response.json()
          throw new Error(errorData.error || ERROR_MESSAGES.CONFLICT)
        } catch {
          throw new Error(ERROR_MESSAGES.CONFLICT)
        }
        
      case STATUS_CODES.BAD_REQUEST:
        try {
          const errorData = await response.json()
          throw new Error(errorData.error || ERROR_MESSAGES.VALIDATION_ERROR)
        } catch {
          throw new Error(ERROR_MESSAGES.VALIDATION_ERROR)
        }
        
      case STATUS_CODES.INTERNAL_SERVER_ERROR:
        throw new Error(ERROR_MESSAGES.SERVER_ERROR)
        
      default:
        throw new Error(ERROR_MESSAGES.UNKNOWN_ERROR)
    }
  }

  /**
   * Processar erros
   */
  handleError(error) {
    if (error.name === 'TypeError' || error.message.includes('fetch')) {
      return new Error(ERROR_MESSAGES.NETWORK_ERROR)
    }
    return error
  }

  // === MÉTODOS DE CONVENIÊNCIA ===

  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' })
  }

  post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data)
    })
  }

  put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    })
  }

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' })
  }

  // === MÉTODOS ESPECÍFICOS DA API ===

  /**
   * Autenticação
   */
  async login(credentials) {
    debugLog('Login attempt', { login: credentials.login })
    const response = await this.post(API_CONFIG.ENDPOINTS.LOGIN, {
      login: credentials.login,
      password: credentials.password
    })
    debugLog('Login successful', { user: response.name, role: response.role })
    return response
  }

  /**
   * Couriers
   */
  async getCouriers() {
    return this.get(API_CONFIG.ENDPOINTS.COURIERS)
  }

  async getCourierById(id) {
    return this.get(API_CONFIG.ENDPOINTS.COURIER_BY_ID(id))
  }

  async createCourier(courierData) {
    const formattedData = formatDataForBackend(courierData, 'courier')
    return this.post(API_CONFIG.ENDPOINTS.COURIERS, formattedData)
  }

  async updateCourier(id, courierData) {
    const formattedData = formatDataForBackend(courierData, 'courier')
    return this.put(API_CONFIG.ENDPOINTS.COURIER_BY_ID(id), formattedData)
  }

  async deleteCourier(id) {
    return this.delete(API_CONFIG.ENDPOINTS.COURIER_BY_ID(id))
  }

  /**
   * Customers
   */
  async getCustomers() {
    return this.get(API_CONFIG.ENDPOINTS.CUSTOMERS)
  }

  async getCustomerById(id) {
    return this.get(API_CONFIG.ENDPOINTS.CUSTOMER_BY_ID(id))
  }

  async createCustomer(customerData) {
    const formattedData = formatDataForBackend(customerData, 'customer')
    return this.post(API_CONFIG.ENDPOINTS.CUSTOMERS, formattedData)
  }

  async updateCustomer(id, customerData) {
    const formattedData = formatDataForBackend(customerData, 'customer')
    return this.put(API_CONFIG.ENDPOINTS.CUSTOMER_BY_ID(id), formattedData)
  }

  async deleteCustomer(id) {
    return this.delete(API_CONFIG.ENDPOINTS.CUSTOMER_BY_ID(id))
  }

  /**
   * Users (para dropdowns de empresas/usuários)
   */
  async getUsers() {
    return this.get(API_CONFIG.ENDPOINTS.USERS)
  }

  async getUserById(id) {
    return this.get(API_CONFIG.ENDPOINTS.USER_BY_ID(id))
  }

  async createUser(userData) {
    const data = formatDataForBackend(userData)
    return this.post(API_CONFIG.ENDPOINTS.USERS, data)
  }

  async updateUser(id, userData) {
    const data = formatDataForBackend(userData)
    return this.put(API_CONFIG.ENDPOINTS.USER_BY_ID(id), data)
  }

  async deleteUser(id) {
    return this.delete(API_CONFIG.ENDPOINTS.USER_BY_ID(id))
  }

  /**
   * Teams
   */
  async getTeams() {
    return this.get(API_CONFIG.ENDPOINTS.TEAMS)
  }

  async getTeamById(id) {
    return this.get(API_CONFIG.ENDPOINTS.TEAM_BY_ID(id))
  }

  async createTeam(teamData) {
    const data = formatDataForBackend(teamData)
    return this.post(API_CONFIG.ENDPOINTS.TEAMS, data)
  }

  async updateTeam(id, teamData) {
    const data = formatDataForBackend(teamData)
    return this.put(API_CONFIG.ENDPOINTS.TEAM_BY_ID(id), data)
  }

  async deleteTeam(id) {
    return this.delete(API_CONFIG.ENDPOINTS.TEAM_BY_ID(id))
  }

  /**
   * Deliveries
   */
  async getDeliveries() {
    return this.get(API_CONFIG.ENDPOINTS.DELIVERIES)
  }

  async getDeliveryById(id) {
    return this.get(API_CONFIG.ENDPOINTS.DELIVERY_BY_ID(id))
  }

  async createDelivery(deliveryData) {
    const data = formatDataForBackend(deliveryData)
    return this.post(API_CONFIG.ENDPOINTS.DELIVERIES, data)
  }

  async updateDelivery(id, deliveryData) {
    const data = formatDataForBackend(deliveryData)
    return this.put(API_CONFIG.ENDPOINTS.DELIVERY_BY_ID(id), data)
  }

  async deleteDelivery(id) {
    return this.delete(API_CONFIG.ENDPOINTS.DELIVERY_BY_ID(id))
  }

  /**
   * Prices
   */
  async getPrices() {
    return this.get(API_CONFIG.ENDPOINTS.PRICES)
  }

  async getPriceById(id) {
    return this.get(API_CONFIG.ENDPOINTS.PRICE_BY_ID(id))
  }

  async createPrice(priceData) {
    const data = formatDataForBackend(priceData)
    return this.post(API_CONFIG.ENDPOINTS.PRICES, data)
  }

  async updatePrice(id, priceData) {
    const data = formatDataForBackend(priceData)
    return this.put(API_CONFIG.ENDPOINTS.PRICE_BY_ID(id), data)
  }

  async deletePrice(id) {
    return this.delete(API_CONFIG.ENDPOINTS.PRICE_BY_ID(id))
  }

  /**
   * Métodos para SMS/WhatsApp
   */
  async getSMS() {
    return this.get(API_CONFIG.ENDPOINTS.SMS)
  }

  async createSMS(smsData) {
    const data = formatDataForBackend(smsData)
    return this.post(API_CONFIG.ENDPOINTS.SMS, data)
  }

  async updateSMS(id, smsData) {
    const data = formatDataForBackend(smsData)
    return this.put(`${API_CONFIG.ENDPOINTS.SMS}/${id}`, data)
  }

  async deleteSMS(id) {
    return this.delete(`${API_CONFIG.ENDPOINTS.SMS}/${id}`)
  }

  /**
   * Health check
   */
  async healthCheck() {
    try {
      // Tentar buscar usuários como health check
      await this.get('/users')
      return { status: 'healthy', backend: 'connected' }
    } catch (error) {
      return { status: 'unhealthy', backend: 'disconnected', error: error.message }
    }
  }
}

// Instância singleton
export const backendService = new BackendService()

// Wrapper para compatibilidade com o código existente
export const apiClient = backendService
