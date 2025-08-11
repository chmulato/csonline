/**
 * Backend Integration Configuration
 * Configurações para integração com o backend CSOnline (JAX-RS + WildFly)
 */

// Base URL da API
export const API_CONFIG = {
  BASE_URL: import.meta.env.DEV ? 'http://localhost:8080/csonline/api' : '/csonline/api',
  ENDPOINTS: {
    // Autenticação
    LOGIN: '/login',
    
    // Couriers
    COURIERS: '/couriers',
    COURIER_BY_ID: (id) => `/couriers/${id}`,
    
    // Customers
    CUSTOMERS: '/customers', 
    CUSTOMER_BY_ID: (id) => `/customers/${id}`,
    
    // Users (para dropdown de empresas/usuários)
    USERS: '/users',
    USER_BY_ID: (id) => `/users/${id}`,
    
    // Teams
    TEAMS: '/teams',
    TEAM_BY_ID: (id) => `/teams/${id}`,
    
    // Deliveries
    DELIVERIES: '/deliveries',
    DELIVERY_BY_ID: (id) => `/deliveries/${id}`,
    
    // Prices
    PRICES: '/prices',
    PRICE_BY_ID: (id) => `/prices/${id}`,
    
    // SMS
    SMS: '/sms'
  }
}

// Headers padrão para requisições
export const DEFAULT_HEADERS = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

// Configurações de timeout
export const TIMEOUT_CONFIG = {
  DEFAULT: 30000, // 30 segundos
  UPLOAD: 60000,  // 60 segundos para uploads
  DOWNLOAD: 120000 // 120 segundos para downloads
}

// Status codes esperados do backend
export const STATUS_CODES = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500
}

// Mensagens de erro padrão
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Erro de conectividade. Verifique sua conexão.',
  TIMEOUT: 'Tempo limite de requisição excedido.',
  UNAUTHORIZED: 'Sessão expirada. Faça login novamente.',
  FORBIDDEN: 'Acesso negado. Você não tem permissão.',
  NOT_FOUND: 'Recurso não encontrado.',
  CONFLICT: 'Dados já existem ou conflito de operação.',
  SERVER_ERROR: 'Erro interno do servidor. Tente novamente.',
  VALIDATION_ERROR: 'Dados inválidos. Verifique os campos.',
  UNKNOWN_ERROR: 'Erro desconhecido. Contate o suporte.'
}

// Mapeamento de roles do backend
export const USER_ROLES = {
  ADMIN: 'ADMIN',
  BUSINESS: 'BUSINESS', 
  COURIER: 'COURIER',
  CUSTOMER: 'CUSTOMER'
}

// Configurações de retry para requisições
export const RETRY_CONFIG = {
  MAX_RETRIES: 3,
  RETRY_DELAY: 1000, // 1 segundo
  RETRY_MULTIPLIER: 2 // Exponential backoff
}

// Validação de response do backend
export function validateBackendResponse(response, expectedStatus = STATUS_CODES.OK) {
  if (!response) {
    throw new Error(ERROR_MESSAGES.UNKNOWN_ERROR)
  }
  
  if (response.status !== expectedStatus) {
    switch (response.status) {
      case STATUS_CODES.UNAUTHORIZED:
        throw new Error(ERROR_MESSAGES.UNAUTHORIZED)
      case STATUS_CODES.FORBIDDEN:
        throw new Error(ERROR_MESSAGES.FORBIDDEN)
      case STATUS_CODES.NOT_FOUND:
        throw new Error(ERROR_MESSAGES.NOT_FOUND)
      case STATUS_CODES.CONFLICT:
        throw new Error(ERROR_MESSAGES.CONFLICT)
      case STATUS_CODES.INTERNAL_SERVER_ERROR:
        throw new Error(ERROR_MESSAGES.SERVER_ERROR)
      default:
        throw new Error(ERROR_MESSAGES.UNKNOWN_ERROR)
    }
  }
}

// Formatação de dados para envio ao backend
export function formatDataForBackend(data, type) {
  switch (type) {
    case 'courier':
      return {
        id: data.id || null,
        factorCourier: parseFloat(data.factorCourier) || 0,
        businessId: data.businessId || null,
        userId: data.userId || null,
        // Inclui dados de usuário quando criação (user presente). Backend deve ignorar se null.
        user: data.user ? {
          id: data.user.id || null,
            name: data.user.name || '',
            login: data.user.login || data.user.email || '',
            email: data.user.email || '',
            phone: data.user.phone || data.user.mobile || '',
            role: data.user.role || USER_ROLES.COURIER,
            password: data.user.password || null
        } : undefined
      }
      
    case 'customer':
      return {
        id: data.id || null,
        factorCustomer: parseFloat(data.factorCustomer) || 0,
        priceTable: data.priceTable || null,
        user: data.user ? {
          id: data.user.id || null,
          name: data.user.name || '',
          login: data.user.login || data.user.email || '',
          email: data.user.email || '',
          phone: data.user.phone || data.user.mobile || '',
          address: data.user.address || '',
          role: data.user.role || USER_ROLES.CUSTOMER
        } : null
      }
      
    case 'user':
      return {
        id: data.id || null,
        name: data.name || '',
        login: data.login || '',
        email: data.email || '',
        phone: data.phone || '',
        role: data.role || USER_ROLES.CUSTOMER,
        password: data.password || null // Apenas para criação
      }
      
    case 'delivery':
      return {
        id: data.id || null,
        businessId: data.businessId || data.business?.id || null,
        customerId: data.customerId || data.customer?.id || null,
        courierId: data.courierId || data.courier?.id || null,
        start: data.start || '',
        destination: data.destination || '',
        contact: data.contact || '',
        description: data.description || '',
        volume: data.volume || '',
        weight: data.weight || '',
        km: typeof data.km === 'number' ? data.km : parseFloat(data.km) || 0,
        additionalCost: typeof data.additionalCost === 'number' ? data.additionalCost : parseFloat(data.additionalCost) || 0,
        cost: typeof data.cost === 'number' ? data.cost : parseFloat(data.cost) || 0,
        received: !!data.received,
        completed: !!data.completed
      }

    case 'price':
      return {
        id: data.id || null,
        tableName: data.tableName || '',
        customerId: data.customerId || data.customer?.id || null,
        businessId: data.businessId || data.business?.id || null,
        vehicle: (data.vehicle || '').toLowerCase(),
        local: data.local || '',
        price: typeof data.price === 'number' ? data.price : parseFloat(data.price) || 0
      }

    case 'sms':
      return {
        id: data.id || null,
        deliveryId: data.deliveryId || data.delivery?.id || null,
        piece: parseInt(data.piece) || 1,
        type: data.type || '',
        mobileFrom: data.mobileFrom || '',
        mobileTo: data.mobileTo || '',
        message: data.message || ''
      }

    case 'team':
      return {
        id: data.id || null,
        business: data.business ? { id: data.business.id, name: data.business.name, role: data.business.role } : null,
        courier: data.courier ? { id: data.courier.id, name: data.courier.name, email: data.courier.email, mobile: data.courier.mobile, originalBusiness: data.courier.originalBusiness, role: data.courier.role } : null,
        businessId: data.businessId || data.business?.id || null, // caso backend espere ids planos
        courierId: data.courierId || data.courier?.id || null,
        factorCourier: typeof data.factorCourier === 'number' ? data.factorCourier : parseFloat(data.factorCourier) || 0,
        status: data.status || 'active'
      }

    default:
      return data
  }
}

// Debug do backend (apenas em desenvolvimento)
export const DEBUG_CONFIG = {
  LOG_REQUESTS: import.meta.env.DEV,
  LOG_RESPONSES: import.meta.env.DEV,
  LOG_ERRORS: true
}

export function debugLog(message, data = null) {
  if (DEBUG_CONFIG.LOG_REQUESTS || DEBUG_CONFIG.LOG_RESPONSES) {
    console.log(`[BACKEND] ${message}`, data)
  }
}
