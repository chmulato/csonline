<template>
  <div class="price-management">
    <!-- Header -->
    <div class="page-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          <i class="fas fa-arrow-left"></i> Voltar
        </button>
        <h2><i class="fas fa-dollar-sign"></i> Gestão de Preços</h2>
      </div>
      <button @click="openCreateModal" class="btn-primary" :disabled="loading">
        <i class="fas fa-plus"></i> Novo Preço
      </button>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Carregando preços...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">
        <i class="fas fa-exclamation-triangle"></i>
      </div>
      <h3>Erro ao carregar preços</h3>
      <p>{{ error }}</p>
      <button @click="loadPrices" class="btn-primary">
        <i class="fas fa-redo"></i> Tentar novamente
      </button>
    </div>

    <!-- Main content -->
    <div v-else>
      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-dollar-sign"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ prices.length }}</div>
            <div class="stat-label">Total de Preços</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-truck"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ uniqueVehicles }}</div>
            <div class="stat-label">Tipos de Veículos</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-building"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ uniqueCustomers }}</div>
            <div class="stat-label">Clientes Ativos</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-chart-line"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">R$ {{ averagePrice.toFixed(2) }}</div>
            <div class="stat-label">Preço Médio</div>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="filters-section">
        <div class="filters-grid">
          <div class="filter-group">
            <label>Cliente:</label>
            <select v-model="filters.customer" @change="applyFilters">
              <option value="">Todos os Clientes</option>
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.user?.name || customer.name }}
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>Empresa:</label>
            <select v-model="filters.business" @change="applyFilters">
              <option value="">Todas as Empresas</option>
              <option v-for="business in businesses" :key="business.id" :value="business.id">
                {{ business.name }}
              </option>
            </select>
          </div>
          <div class="filter-group">
            <label>Veículo:</label>
            <select v-model="filters.vehicle" @change="applyFilters">
              <option value="">Todos os Veículos</option>
              <!-- Use lowercase values to ensure consistent filtering with test data -->
              <option value="moto">Moto</option>
              <option value="carro">Carro</option>
              <option value="van">Van</option>
              <option value="caminhão">Caminhão</option>
            </select>
          </div>
          <div class="filter-group">
            <label>Buscar:</label>
            <div class="search-input">
              <input 
                type="text" 
                v-model="filters.search" 
                @input="applyFilters"
                placeholder="Buscar por tabela, local..."
              />
              <i class="fas fa-search"></i>
            </div>
          </div>
        </div>
        <div class="filter-actions">
          <button @click="clearFilters" class="btn-secondary">
            <i class="fas fa-times"></i> Limpar Filtros
          </button>
          <button @click="exportPrices" class="btn-outline">
            <i class="fas fa-download"></i> Exportar
          </button>
        </div>
      </div>

      <!-- Prices Table -->
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tabela</th>
              <th>Cliente</th>
              <th>Empresa</th>
              <th>Veículo</th>
              <th>Local</th>
              <th>Preço</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredPrices.length === 0">
              <td colspan="8" class="no-data">
                <i class="fas fa-dollar-sign"></i>
                <p>Nenhum preço encontrado</p>
              </td>
            </tr>
            <tr v-for="price in filteredPrices" :key="price.id">
              <td>{{ price.id }}</td>
              <td>
                <span class="table-badge">{{ price.tableName }}</span>
              </td>
              <td>
                <div class="user-info">
                  <i class="fas fa-building"></i>
                  {{ getCustomerName(price) }}
                </div>
              </td>
              <td>
                <div class="user-info">
                  <i class="fas fa-user-tie"></i>
                  {{ getBusinessName(price) }}
                </div>
              </td>
              <td>
                <span class="vehicle-badge" :class="getVehicleClass(price.vehicle)">
                  <i :class="getVehicleIcon(price.vehicle)"></i>
                  {{ (price.vehicle && price.vehicle.charAt(0).toUpperCase() + price.vehicle.slice(1)) || '' }}
                </span>
              </td>
              <td>{{ price.local }}</td>
              <td>
                <span class="price-value">R$ {{ formatPrice(price.price) }}</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="viewPrice(price)" class="btn-action btn-view">
                    <i class="fas fa-eye"></i>
                  </button>
                  <button @click="editPrice(price)" class="btn-action btn-edit">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deletePrice(price.id)" class="btn-action btn-delete">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>
            <i class="fas fa-dollar-sign"></i>
            {{ isEditing ? 'Editar Preço' : 'Novo Preço' }}
          </h3>
          <button @click="closeModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="savePrice" class="modal-form">
          <div class="form-grid">
            <div class="form-group">
              <label for="tableName">Nome da Tabela *</label>
              <input 
                id="tableName"
                v-model="currentPrice.tableName" 
                type="text" 
                required 
                placeholder="Ex: Tabela Padrão 2025"
              />
            </div>

            <div class="form-group">
              <label for="customer">Cliente *</label>
              <select id="customer" v-model="currentPrice.customerId" required>
                <option value="">Selecione o cliente</option>
                <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                  {{ customer.user?.name || customer.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="business">Empresa *</label>
              <select id="business" v-model="currentPrice.businessId" required>
                <option value="">Selecione a empresa</option>
                <option v-for="business in businesses" :key="business.id" :value="business.id">
                  {{ business.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="vehicle">Tipo de Veículo *</label>
              <select id="vehicle" v-model="currentPrice.vehicle" required>
                <option value="">Selecione o veículo</option>
                <option value="moto">Moto</option>
                <option value="carro">Carro</option>
                <option value="van">Van</option>
                <option value="caminhão">Caminhão</option>
              </select>
            </div>

            <div class="form-group">
              <label for="local">Local/Rota *</label>
              <input 
                id="local"
                v-model="currentPrice.local" 
                type="text" 
                required 
                placeholder="Ex: São Paulo - Santos"
              />
            </div>

            <div class="form-group">
              <label for="price">Preço (R$) *</label>
              <input 
                id="price"
                v-model="currentPrice.price" 
                type="number" 
                step="0.01" 
                min="0" 
                required 
                placeholder="0.00"
              />
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" @click="closeModal" class="btn-secondary">
              <i class="fas fa-times"></i> Cancelar
            </button>
            <button type="submit" class="btn-primary" :disabled="saving">
              <i class="fas fa-save"></i>
              {{ saving ? 'Salvando...' : 'Salvar' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- View Modal -->
    <div v-if="showViewModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>
            <i class="fas fa-eye"></i>
            Detalhes do Preço
          </h3>
          <button @click="closeViewModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="price-details">
          <div class="detail-group">
            <label>ID:</label>
            <span>{{ viewingPrice?.id }}</span>
          </div>
          <div class="detail-group">
            <label>Tabela:</label>
            <span class="table-badge">{{ viewingPrice?.tableName }}</span>
          </div>
          <div class="detail-group">
            <label>Cliente:</label>
            <span>{{ getCustomerName(viewingPrice) }}</span>
          </div>
          <div class="detail-group">
            <label>Empresa:</label>
            <span>{{ getBusinessName(viewingPrice) }}</span>
          </div>
          <div class="detail-group">
            <label>Veículo:</label>
            <span class="vehicle-badge" :class="getVehicleClass(viewingPrice?.vehicle)">
              <i :class="getVehicleIcon(viewingPrice?.vehicle)"></i>
              {{ viewingPrice?.vehicle && viewingPrice.vehicle.charAt(0).toUpperCase() + viewingPrice.vehicle.slice(1) }}
            </span>
          </div>
          <div class="detail-group">
            <label>Local/Rota:</label>
            <span>{{ viewingPrice?.local }}</span>
          </div>
          <div class="detail-group">
            <label>Preço:</label>
            <span class="price-value large">R$ {{ formatPrice(viewingPrice?.price) }}</span>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="closeViewModal" class="btn-secondary">
            <i class="fas fa-times"></i> Fechar
          </button>
          <button @click="editPriceFromView" class="btn-primary">
            <i class="fas fa-edit"></i> Editar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { backendService } from '../services/backend.js'

// Emits
const emit = defineEmits(['back'])

// Reactive state
const prices = ref([])
const customers = ref([])
const businesses = ref([])
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

// Legacy/form compatibility for validation tests
const form = ref({
  customerId: '',
  businessId: '',
  courierPricePerKm: '',
  customerPricePerKm: '',
  vehicle: '',
  distance: '',
  weight: ''
})
const errors = ref({})

// Modal state
const showModal = ref(false)
const showViewModal = ref(false)
const isEditing = ref(false)
const viewingPrice = ref(null)

// Form data
const currentPrice = ref({
  id: null,
  tableName: '',
  customerId: '',
  businessId: '',
  vehicle: '',
  local: '',
  price: null
})

// Filters
const filters = ref({
  customer: '',
  business: '',
  vehicle: '',
  search: ''
})

// Computed properties
const filteredPrices = computed(() => {
  let filtered = prices.value

  if (filters.value.customer) {
    filtered = filtered.filter(price => 
      price.customer?.id == filters.value.customer ||
      price.customerId == filters.value.customer
    )
  }

  if (filters.value.business) {
    filtered = filtered.filter(price => 
      price.business?.id == filters.value.business ||
      price.businessId == filters.value.business
    )
  }

  if (filters.value.vehicle) {
    const sel = filters.value.vehicle.toLowerCase()
    filtered = filtered.filter(price => (price.vehicle || '').toLowerCase() === sel)
  }

  if (filters.value.search) {
    const search = filters.value.search.toLowerCase()
    filtered = filtered.filter(price => 
      price.tableName?.toLowerCase().includes(search) ||
      price.local?.toLowerCase().includes(search) ||
      getCustomerName(price).toLowerCase().includes(search) ||
      getBusinessName(price).toLowerCase().includes(search)
    )
  }

  return filtered
})

const uniqueVehicles = computed(() => {
  const vehicles = new Set(prices.value.map(price => price.vehicle).filter(Boolean))
  return vehicles.size
})

const uniqueCustomers = computed(() => {
  const customerIds = new Set(
    prices.value.map(price => price.customer?.id || price.customerId).filter(Boolean)
  )
  return customerIds.size
})

const averagePrice = computed(() => {
  const validPrices = prices.value.filter(price => price.price && price.price > 0)
  if (validPrices.length === 0) return 0
  const sum = validPrices.reduce((acc, price) => acc + price.price, 0)
  return sum / validPrices.length
})

// Methods
function goBack() {
  emit('back')
}

async function loadPrices() {
  loading.value = true
  error.value = null
  
  try {
    const [pricesData, customersData, businessesData] = await Promise.all([
      backendService.getPrices(),
      backendService.getCustomers(),
      backendService.getUsers() // Para buscar empresas (users com role BUSINESS)
    ])
    
    prices.value = pricesData || []
    customers.value = customersData || []
    
    // Filtrar apenas usuários do tipo BUSINESS
    businesses.value = (businessesData || []).filter(user => user.profile === 'BUSINESS')
    
    console.log('Preços carregados:', prices.value.length)
    console.log('Clientes carregados:', customers.value.length)
    console.log('Empresas carregadas:', businesses.value.length)
    
  } catch (err) {
    console.error('Erro ao carregar preços:', err)
    error.value = err.message || 'Erro ao conectar com o servidor'
  } finally {
    loading.value = false
  }
}

function getCustomerName(price) {
  if (!price) return 'N/A'
  return price.customer?.user?.name || 
         price.customer?.name || 
         'Cliente não informado'
}

function getBusinessName(price) {
  if (!price) return 'N/A'
  return price.business?.name || 
         price.business?.user?.name ||
         'Empresa não informada'
}

function getVehicleClass(vehicle) {
  if (!vehicle) return 'vehicle-default'
  const v = vehicle.toLowerCase()
  const classes = {
    'moto': 'vehicle-moto',
    'carro': 'vehicle-car',
    'van': 'vehicle-van',
    'caminhão': 'vehicle-truck'
  }
  return classes[v] || 'vehicle-default'
}

function getVehicleIcon(vehicle) {
  if (!vehicle) return 'fas fa-vehicle'
  const v = vehicle.toLowerCase()
  const icons = {
    'moto': 'fas fa-motorcycle',
    'carro': 'fas fa-car',
    'van': 'fas fa-shuttle-van',
    'caminhão': 'fas fa-truck'
  }
  return icons[v] || 'fas fa-vehicle'
}

function formatPrice(price) {
  if (!price) return '0,00'
  return parseFloat(price).toLocaleString('pt-BR', { 
    minimumFractionDigits: 2, 
    maximumFractionDigits: 2 
  })
}

// Currency formatter used by legacy tests
function formatCurrency(value) {
  const num = parseFloat(value)
  if (isNaN(num)) return 'R$ 0,00'
  return 'R$ ' + num.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

function openCreateModal() {
  isEditing.value = false
  currentPrice.value = {
    id: null,
    tableName: '',
    customerId: '',
    businessId: '',
    vehicle: '',
    local: '',
    price: null
  }
  showModal.value = true
}

function editPrice(price) {
  isEditing.value = true
  currentPrice.value = {
    id: price.id,
    tableName: price.tableName || '',
    customerId: price.customer?.id || price.customerId || '',
    businessId: price.business?.id || price.businessId || '',
    vehicle: price.vehicle || '',
    local: price.local || '',
    price: price.price || null
  }
  showModal.value = true
}

function viewPrice(price) {
  viewingPrice.value = price
  showViewModal.value = true
}

function editPriceFromView() {
  closeViewModal()
  editPrice(viewingPrice.value)
}

async function savePrice() {
  saving.value = true
  
  try {
    // Preparar dados para envio
    const priceData = {
      tableName: currentPrice.value.tableName,
      customer: { id: parseInt(currentPrice.value.customerId) },
      business: { id: parseInt(currentPrice.value.businessId) },
      vehicle: currentPrice.value.vehicle,
      local: currentPrice.value.local,
      price: parseFloat(currentPrice.value.price)
    }
    
    let savedPrice
    
    if (isEditing.value) {
      // Editar preço existente
      savedPrice = await backendService.updatePrice(currentPrice.value.id, priceData)
      
      // Atualizar na lista local
      const index = prices.value.findIndex(p => p.id === currentPrice.value.id)
      if (index !== -1) {
        prices.value[index] = { ...prices.value[index], ...savedPrice }
      }
      
      console.log('Preço atualizado com sucesso')
    } else {
      // Criar novo preço
      savedPrice = await backendService.createPrice(priceData)
      
      // Adicionar à lista local
      prices.value.push(savedPrice)
      
      console.log('Preço criado com sucesso')
    }
    
    closeModal()
    
  } catch (err) {
    console.error('Erro ao salvar preço:', err)
    alert('Erro ao salvar preço: ' + (err.message || 'Erro desconhecido'))
  } finally {
    saving.value = false
  }
}

async function deletePrice(id) {
  if (!confirm('Tem certeza que deseja excluir este preço?')) {
    return
  }
  
  try {
    await backendService.deletePrice(id)
    
    // Remover da lista local
    prices.value = prices.value.filter(p => p.id !== id)
    
    console.log('Preço excluído com sucesso')
  } catch (err) {
    console.error('Erro ao excluir preço:', err)
    alert('Erro ao excluir preço: ' + (err.message || 'Erro desconhecido'))
  }
}

function closeModal() {
  showModal.value = false
  isEditing.value = false
  currentPrice.value = {
    id: null,
    tableName: '',
    customerId: '',
    businessId: '',
    vehicle: '',
    local: '',
    price: null
  }
}

function closeViewModal() {
  showViewModal.value = false
  viewingPrice.value = null
}

function applyFilters() {
  // Esta função é chamada automaticamente pelo computed filteredPrices
}

function clearFilters() {
  filters.value = {
    customer: '',
    business: '',
    vehicle: '',
    search: ''
  }
}

function validateForm() {
  errors.value = {}
  const f = form.value

  // Required fields
  if (!f.customerId) errors.value.customerId = 'Cliente obrigatório'
  if (!f.businessId) errors.value.businessId = 'Empresa obrigatória'
  if (!f.vehicle) errors.value.vehicle = 'Veículo obrigatório'

  // Numeric validations
  const numericFields = ['courierPricePerKm', 'customerPricePerKm']
  numericFields.forEach(field => {
    if (f[field] === '' || f[field] === null || f[field] === undefined) return
    const val = parseFloat(f[field])
    if (isNaN(val)) errors.value[field] = 'Valor numérico inválido'
    else if (val < 0) errors.value[field] = 'Valor deve ser positivo'
  })

  return Object.keys(errors.value).length === 0
}

function exportPrices() {
  try {
    const csvContent = generateCSV()
    downloadCSV(csvContent, 'precos-csonline.csv')
  } catch (err) {
    console.error('Erro ao exportar preços:', err)
    alert('Erro ao exportar preços')
  }
}

function generateCSV() {
  const headers = ['ID', 'Tabela', 'Cliente', 'Empresa', 'Veículo', 'Local', 'Preço']
  const rows = filteredPrices.value.map(price => [
    price.id,
    price.tableName,
    getCustomerName(price),
    getBusinessName(price),
    price.vehicle,
    price.local,
    formatPrice(price.price)
  ])
  
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.map(field => `"${field}"`).join(','))
  ].join('\n')
  
  return csvContent
}

function downloadCSV(content, filename) {
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// Lifecycle
onMounted(() => {
  loadPrices()
})
</script>

<style scoped>
.price-management {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.btn-back {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
}

.btn-primary {
  padding: 10px 20px;
  background: #fff;
  color: #1976d2;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #f5f5f5;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-state, .error-state {
  text-align: center;
  padding: 60px 32px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1976d2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  color: #d32f2f;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 32px;
  background: #f8f9fa;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.stat-card:nth-child(1) .stat-icon {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
}

.stat-card:nth-child(2) .stat-icon {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
}

.stat-card:nth-child(3) .stat-icon {
  background: linear-gradient(135deg, #2196f3, #1976d2);
  color: white;
}

.stat-card:nth-child(4) .stat-icon {
  background: linear-gradient(135deg, #9c27b0, #7b1fa2);
  color: white;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.filters-section {
  padding: 32px;
  background: white;
  border-bottom: 1px solid #eee;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label {
  margin-bottom: 4px;
  font-weight: 500;
  color: #333;
}

.filter-group select,
.search-input input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  position: relative;
}

.search-input i {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.btn-secondary {
  padding: 8px 16px;
  background: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-outline {
  padding: 8px 16px;
  background: transparent;
  color: #1976d2;
  border: 1px solid #1976d2;
  border-radius: 4px;
  cursor: pointer;
}

.table-container {
  padding: 32px;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #666;
}

.no-data i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.table-badge {
  padding: 4px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.vehicle-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.vehicle-moto {
  background: #fff3e0;
  color: #f57c00;
}

.vehicle-car {
  background: #e8f5e8;
  color: #388e3c;
}

.vehicle-van {
  background: #e3f2fd;
  color: #1976d2;
}

.vehicle-truck {
  background: #fce4ec;
  color: #c2185b;
}

.price-value {
  font-weight: 600;
  color: #4caf50;
}

.price-value.large {
  font-size: 18px;
}

.action-buttons {
  display: flex;
  gap: 4px;
}

.btn-action {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-view {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-edit {
  background: #fff3e0;
  color: #f57c00;
}

.btn-delete {
  background: #ffebee;
  color: #d32f2f;
}

.btn-action:hover {
  transform: scale(1.1);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 600px;
  width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.btn-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-form {
  padding: 32px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 24px 32px;
  border-top: 1px solid #eee;
}

.price-details {
  padding: 32px;
}

.detail-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-group:last-child {
  border-bottom: none;
}

.detail-group label {
  font-weight: 500;
  color: #666;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filters-grid {
    grid-template-columns: 1fr;
  }

  .filter-actions {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .modal-actions {
    flex-direction: column;
  }
}
</style>
