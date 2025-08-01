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
      <button @click="openCreateModal" class="btn-primary">
        <i class="fas fa-plus"></i> Novo Preço
      </button>
    </div>

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
              {{ customer.name }}
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
            <option value="Moto">Moto</option>
            <option value="Carro">Carro</option>
            <option value="Van">Van</option>
            <option value="Caminhão">Caminhão</option>
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
            >
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
                {{ price.customer?.name || 'N/A' }}
              </div>
            </td>
            <td>
              <div class="user-info">
                <i class="fas fa-user-tie"></i>
                {{ price.business?.name || 'N/A' }}
              </div>
            </td>
            <td>
              <span class="vehicle-badge" :class="getVehicleClass(price.vehicle)">
                <i :class="getVehicleIcon(price.vehicle)"></i>
                {{ price.vehicle }}
              </span>
            </td>
            <td>{{ price.local }}</td>
            <td class="price-cell">
              R$ {{ price.price?.toFixed(2) || '0.00' }}
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewPrice(price)" class="btn-action btn-view" title="Visualizar">
                  <i class="fas fa-eye"></i>
                </button>
                <button @click="editPrice(price)" class="btn-action btn-edit" title="Editar">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="deletePrice(price)" class="btn-action btn-delete" title="Excluir">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>
            <i class="fas fa-dollar-sign"></i>
            {{ isEditing ? 'Editar Preço' : 'Novo Preço' }}
          </h3>
          <button @click="closeModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="savePrice">
            <div class="form-grid">
              <div class="form-group">
                <label for="tableName">Nome da Tabela <span class="required">*</span></label>
                <input 
                  type="text" 
                  id="tableName"
                  v-model="currentPrice.tableName" 
                  required
                  placeholder="Ex: Tabela Padrão 2025"
                >
              </div>
              <div class="form-group">
                <label for="customer">Cliente <span class="required">*</span></label>
                <select 
                  id="customer"
                  v-model="currentPrice.customerId" 
                  required
                >
                  <option value="">Selecione um cliente</option>
                  <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                    {{ customer.name }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="business">Empresa <span class="required">*</span></label>
                <select 
                  id="business"
                  v-model="currentPrice.businessId" 
                  required
                >
                  <option value="">Selecione uma empresa</option>
                  <option v-for="business in businesses" :key="business.id" :value="business.id">
                    {{ business.name }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="vehicle">Tipo de Veículo <span class="required">*</span></label>
                <select 
                  id="vehicle"
                  v-model="currentPrice.vehicle" 
                  required
                >
                  <option value="">Selecione um veículo</option>
                  <option value="Moto">Moto</option>
                  <option value="Carro">Carro</option>
                  <option value="Van">Van</option>
                  <option value="Caminhão">Caminhão</option>
                </select>
              </div>
              <div class="form-group">
                <label for="local">Local <span class="required">*</span></label>
                <input 
                  type="text" 
                  id="local"
                  v-model="currentPrice.local" 
                  required
                  placeholder="Ex: São Paulo - Centro"
                >
              </div>
              <div class="form-group">
                <label for="price">Preço (R$) <span class="required">*</span></label>
                <input 
                  type="number" 
                  id="price"
                  v-model="currentPrice.price" 
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                >
              </div>
            </div>
            <div class="modal-actions">
              <button type="button" @click="closeModal" class="btn-secondary">
                <i class="fas fa-times"></i> Cancelar
              </button>
              <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i> 
                {{ isEditing ? 'Atualizar' : 'Salvar' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- View Modal -->
    <div v-if="showViewModal" class="modal-overlay" @click="closeViewModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>
            <i class="fas fa-eye"></i>
            Detalhes do Preço
          </h3>
          <button @click="closeViewModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="view-grid" v-if="viewingPrice">
            <div class="view-item">
              <label>ID:</label>
              <span>{{ viewingPrice.id }}</span>
            </div>
            <div class="view-item">
              <label>Nome da Tabela:</label>
              <span class="table-badge">{{ viewingPrice.tableName }}</span>
            </div>
            <div class="view-item">
              <label>Cliente:</label>
              <span class="customer-info">
                <i class="fas fa-building"></i>
                {{ viewingPrice.customer?.name || 'N/A' }}
              </span>
            </div>
            <div class="view-item">
              <label>Empresa:</label>
              <span class="business-info">
                <i class="fas fa-user-tie"></i>
                {{ viewingPrice.business?.name || 'N/A' }}
              </span>
            </div>
            <div class="view-item">
              <label>Tipo de Veículo:</label>
              <span class="vehicle-badge" :class="getVehicleClass(viewingPrice.vehicle)">
                <i :class="getVehicleIcon(viewingPrice.vehicle)"></i>
                {{ viewingPrice.vehicle }}
              </span>
            </div>
            <div class="view-item">
              <label>Local:</label>
              <span>{{ viewingPrice.local }}</span>
            </div>
            <div class="view-item">
              <label>Preço:</label>
              <span class="price-display">R$ {{ viewingPrice.price?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>
          <div class="modal-actions">
            <button @click="closeViewModal" class="btn-secondary">
              <i class="fas fa-times"></i> Fechar
            </button>
            <button @click="editFromView" class="btn-primary">
              <i class="fas fa-edit"></i> Editar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// Emits
const emit = defineEmits(['back']);

// Reactive data
const prices = ref([]);
const customers = ref([]);
const businesses = ref([]);
const showModal = ref(false);
const showViewModal = ref(false);
const isEditing = ref(false);
const viewingPrice = ref(null);

const currentPrice = ref({
  id: null,
  tableName: '',
  customerId: '',
  businessId: '',
  vehicle: '',
  local: '',
  price: null
});

const filters = ref({
  customer: '',
  business: '',
  vehicle: '',
  search: ''
});

// Computed properties
const filteredPrices = computed(() => {
  let filtered = prices.value;

  if (filters.value.customer) {
    filtered = filtered.filter(price => price.customer?.id == filters.value.customer);
  }

  if (filters.value.business) {
    filtered = filtered.filter(price => price.business?.id == filters.value.business);
  }

  if (filters.value.vehicle) {
    filtered = filtered.filter(price => price.vehicle === filters.value.vehicle);
  }

  if (filters.value.search) {
    const search = filters.value.search.toLowerCase();
    filtered = filtered.filter(price => 
      price.tableName?.toLowerCase().includes(search) ||
      price.local?.toLowerCase().includes(search) ||
      price.customer?.name?.toLowerCase().includes(search) ||
      price.business?.name?.toLowerCase().includes(search)
    );
  }

  return filtered;
});

const uniqueVehicles = computed(() => {
  const vehicles = new Set(prices.value.map(price => price.vehicle).filter(Boolean));
  return vehicles.size;
});

const uniqueCustomers = computed(() => {
  const customers = new Set(prices.value.map(price => price.customer?.id).filter(Boolean));
  return customers.size;
});

const averagePrice = computed(() => {
  const validPrices = prices.value.filter(price => price.price && price.price > 0);
  if (validPrices.length === 0) return 0;
  const sum = validPrices.reduce((acc, price) => acc + price.price, 0);
  return sum / validPrices.length;
});

// Methods
function goBack() {
  emit('back');
}

function openCreateModal() {
  isEditing.value = false;
  currentPrice.value = {
    id: null,
    tableName: '',
    customerId: '',
    businessId: '',
    vehicle: '',
    local: '',
    price: null
  };
  showModal.value = true;
}

function editPrice(price) {
  isEditing.value = true;
  currentPrice.value = {
    id: price.id,
    tableName: price.tableName || '',
    customerId: price.customer?.id || '',
    businessId: price.business?.id || '',
    vehicle: price.vehicle || '',
    local: price.local || '',
    price: price.price || null
  };
  showModal.value = true;
}

function viewPrice(price) {
  viewingPrice.value = price;
  showViewModal.value = true;
}

function editFromView() {
  closeViewModal();
  editPrice(viewingPrice.value);
}

function closeModal() {
  showModal.value = false;
  currentPrice.value = {
    id: null,
    tableName: '',
    customerId: '',
    businessId: '',
    vehicle: '',
    local: '',
    price: null
  };
}

function closeViewModal() {
  showViewModal.value = false;
  viewingPrice.value = null;
}

function savePrice() {
  if (isEditing.value) {
    // Update existing price
    const index = prices.value.findIndex(p => p.id === currentPrice.value.id);
    if (index !== -1) {
      const customer = customers.value.find(c => c.id == currentPrice.value.customerId);
      const business = businesses.value.find(b => b.id == currentPrice.value.businessId);
      
      prices.value[index] = {
        ...currentPrice.value,
        customer: customer,
        business: business
      };
    }
  } else {
    // Create new price
    const customer = customers.value.find(c => c.id == currentPrice.value.customerId);
    const business = businesses.value.find(b => b.id == currentPrice.value.businessId);
    
    const newPrice = {
      ...currentPrice.value,
      id: Date.now(), // Temporary ID
      customer: customer,
      business: business
    };
    prices.value.push(newPrice);
  }
  closeModal();
}

function deletePrice(price) {
  if (confirm(`Tem certeza que deseja excluir o preço "${price.tableName}" - ${price.local}?`)) {
    const index = prices.value.findIndex(p => p.id === price.id);
    if (index !== -1) {
      prices.value.splice(index, 1);
    }
  }
}

function applyFilters() {
  // Filters are automatically applied via computed property
}

function clearFilters() {
  filters.value = {
    customer: '',
    business: '',
    vehicle: '',
    search: ''
  };
}

function exportPrices() {
  // Mock export functionality
  const data = filteredPrices.value.map(price => ({
    ID: price.id,
    Tabela: price.tableName,
    Cliente: price.customer?.name || '',
    Empresa: price.business?.name || '',
    Veiculo: price.vehicle,
    Local: price.local,
    Preco: price.price
  }));
  
  console.log('Exportando preços:', data);
  alert('Funcionalidade de exportação será implementada na integração com o backend.');
}

function getVehicleClass(vehicle) {
  switch (vehicle) {
    case 'Moto': return 'vehicle-moto';
    case 'Carro': return 'vehicle-car';
    case 'Van': return 'vehicle-van';
    case 'Caminhão': return 'vehicle-truck';
    default: return 'vehicle-default';
  }
}

function getVehicleIcon(vehicle) {
  switch (vehicle) {
    case 'Moto': return 'fas fa-motorcycle';
    case 'Carro': return 'fas fa-car';
    case 'Van': return 'fas fa-shuttle-van';
    case 'Caminhão': return 'fas fa-truck';
    default: return 'fas fa-shipping-fast';
  }
}

// Mock data loading
onMounted(() => {
  // Mock customers data
  customers.value = [
    { id: 1, name: 'Centro de Distribuição São Paulo' },
    { id: 2, name: 'Centro de Distribuição Rio de Janeiro' },
    { id: 3, name: 'Centro de Distribuição Belo Horizonte' },
    { id: 4, name: 'Centro de Distribuição Porto Alegre' },
    { id: 5, name: 'Centro de Distribuição Salvador' }
  ];

  // Mock businesses data
  businesses.value = [
    { id: 1, name: 'Transportadora Express LTDA' },
    { id: 2, name: 'Logística Rápida S.A.' },
    { id: 3, name: 'Entregas Ágeis EIRELI' },
    { id: 4, name: 'Distribuidora Nacional LTDA' },
    { id: 5, name: 'Courier Premium S.A.' }
  ];

  // Mock prices data
  prices.value = [
    {
      id: 1,
      tableName: 'Tabela Padrão 2025',
      customer: customers.value[0],
      business: businesses.value[0],
      vehicle: 'Moto',
      local: 'São Paulo - Centro',
      price: 15.50
    },
    {
      id: 2,
      tableName: 'Tabela Express',
      customer: customers.value[1],
      business: businesses.value[1],
      vehicle: 'Carro',
      local: 'Rio de Janeiro - Zona Sul',
      price: 25.00
    },
    {
      id: 3,
      tableName: 'Tabela Premium',
      customer: customers.value[2],
      business: businesses.value[2],
      vehicle: 'Van',
      local: 'Belo Horizonte - Centro',
      price: 45.75
    },
    {
      id: 4,
      tableName: 'Tabela Corporativa',
      customer: customers.value[3],
      business: businesses.value[3],
      vehicle: 'Caminhão',
      local: 'Porto Alegre - Industrial',
      price: 85.00
    },
    {
      id: 5,
      tableName: 'Tabela Regional',
      customer: customers.value[4],
      business: businesses.value[4],
      vehicle: 'Moto',
      local: 'Salvador - Pelourinho',
      price: 12.25
    },
    {
      id: 6,
      tableName: 'Tabela Noturna',
      customer: customers.value[0],
      business: businesses.value[1],
      vehicle: 'Carro',
      local: 'São Paulo - Zona Norte',
      price: 32.50
    }
  ];
});
</script>

<style scoped>
.price-management {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e0e0e0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-header h2 {
  color: #2c3e50;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header h2 i {
  color: #27ae60;
  margin-right: 10px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 25px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card:nth-child(3) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card:nth-child(4) {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon {
  font-size: 40px;
  opacity: 0.9;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.filters-section {
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.filter-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
}

.filter-group select,
.filter-group input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.filter-group select:focus,
.filter-group input:focus {
  outline: none;
  border-color: #3498db;
}

.search-input {
  position: relative;
}

.search-input i {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #7f8c8d;
}

.filter-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.table-container {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 15px;
  text-align: left;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.data-table td {
  padding: 18px 15px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.no-data {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.no-data i {
  font-size: 48px;
  margin-bottom: 15px;
  display: block;
}

.no-data p {
  font-size: 18px;
  margin: 0;
}

.table-badge {
  background: #e3f2fd;
  color: #1976d2;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.user-info i {
  color: #7f8c8d;
}

.vehicle-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.vehicle-moto {
  background: #fff3e0;
  color: #f57c00;
}

.vehicle-car {
  background: #e8f5e8;
  color: #4caf50;
}

.vehicle-van {
  background: #e3f2fd;
  color: #2196f3;
}

.vehicle-truck {
  background: #fce4ec;
  color: #e91e63;
}

.vehicle-default {
  background: #f5f5f5;
  color: #757575;
}

.price-cell {
  font-weight: 600;
  color: #27ae60;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-view {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-view:hover {
  background: #1976d2;
  color: white;
}

.btn-edit {
  background: #fff3e0;
  color: #f57c00;
}

.btn-edit:hover {
  background: #f57c00;
  color: white;
}

.btn-delete {
  background: #ffebee;
  color: #d32f2f;
}

.btn-delete:hover {
  background: #d32f2f;
  color: white;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.btn-secondary {
  background: #6c757d;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-outline {
  background: transparent;
  color: #667eea;
  padding: 10px 20px;
  border: 2px solid #667eea;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
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
  backdrop-filter: blur(5px);
}

.modal {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px;
  border-bottom: 1px solid #e0e0e0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px 15px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.btn-close:hover {
  background: rgba(255,255,255,0.2);
}

.modal-body {
  padding: 25px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group select {
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3498db;
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.view-grid {
  display: grid;
  gap: 20px;
  margin-bottom: 30px;
}

.view-item {
  display: grid;
  grid-template-columns: 150px 1fr;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.view-item label {
  font-weight: 600;
  color: #2c3e50;
}

.customer-info,
.business-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.customer-info i,
.business-info i {
  color: #7f8c8d;
}

.price-display {
  font-weight: 600;
  color: #27ae60;
  font-size: 18px;
}

@media (max-width: 768px) {
  .price-management {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .header-left {
    justify-content: space-between;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .filters-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-actions {
    justify-content: stretch;
  }
  
  .filter-actions button {
    flex: 1;
  }
  
  .data-table {
    font-size: 12px;
  }
  
  .data-table th,
  .data-table td {
    padding: 10px 8px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .view-item {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>
