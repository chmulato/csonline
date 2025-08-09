<template>
  <div class="delivery-management">
    <h2>Gestão de Entregas</h2>
    
    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <p>Carregando entregas...</p>
    </div>
    
    <!-- Error state -->
    <div v-else-if="error" class="error">
      <p>Erro ao carregar entregas: {{ error }}</p>
      <button @click="loadDeliveries">Tentar novamente</button>
    </div>
    
    <!-- Main content -->
    <div v-else>
      <div class="actions">
        <button @click="showForm = true" class="primary-btn">Nova Entrega</button>
        <button class="back-btn" @click="goBack">Voltar</button>
      </div>
      
      <div class="filters">
        <div class="filter-group">
          <select v-model="statusFilter" @change="filterDeliveries">
            <option value="">Todos os Status</option>
            <option value="pending">Pendente</option>
            <option value="received">Recebida</option>
            <option value="completed">Finalizada</option>
          </select>
          
          <input 
            v-model="searchText" 
            type="text" 
            placeholder="Buscar por cliente, entregador..."
            @input="filterDeliveries"
            class="search-input"
          />
        </div>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Empresa</th>
              <th>Cliente</th>
              <th>Entregador</th>
              <th>Origem</th>
              <th>Destino</th>
              <th>Contato</th>
              <th>Volume</th>
              <th>Peso</th>
              <th>KM</th>
              <th>Custo</th>
              <th>Status</th>
              <th>Data</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="delivery in filteredDeliveries" :key="delivery.id">
              <td>{{ delivery.id }}</td>
              <td>{{ getBusinessName(delivery) }}</td>
              <td>{{ getCustomerName(delivery) }}</td>
              <td>{{ getCourierName(delivery) }}</td>
              <td>{{ delivery.start }}</td>
              <td>{{ delivery.destination }}</td>
              <td>{{ delivery.contact }}</td>
              <td>{{ delivery.volume }}</td>
              <td>{{ delivery.weight }}</td>
              <td>{{ delivery.km }}km</td>
              <td>R$ {{ delivery.cost?.toFixed(2) }}</td>
              <td>
                <span :class="getStatusClass(delivery)">{{ getStatusText(delivery) }}</span>
              </td>
              <td>{{ formatDate(delivery.datatime) }}</td>
              <td>
                <button @click="editDelivery(delivery)" class="edit-btn">Editar</button>
                <button @click="deleteDelivery(delivery.id)" class="delete-btn">Excluir</button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-if="filteredDeliveries.length === 0" class="empty-state">
          <p>Nenhuma entrega encontrada.</p>
        </div>
      </div>
    </div>

    <!-- Modal Form -->
    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingDelivery ? 'Editar Entrega' : 'Nova Entrega' }}</h3>
        
        <div v-if="formLoading" class="form-loading">
          <p>Carregando dados...</p>
        </div>
        
        <form v-else @submit.prevent="saveDelivery">
          <div class="form-row">
            <div class="form-group">
              <label>Empresa *</label>
              <select v-model="form.businessId" required>
                <option value="">Selecione a Empresa</option>
                <option v-for="business in businesses" :key="business.id" :value="business.id">
                  {{ business.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Cliente *</label>
              <select v-model="form.customerId" required>
                <option value="">Selecione o Cliente</option>
                <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                  {{ customer.user?.name || customer.name }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Entregador *</label>
              <select v-model="form.courierId" required>
                <option value="">Selecione o Entregador</option>
                <option v-for="courier in couriers" :key="courier.id" :value="courier.id">
                  {{ courier.user?.name || courier.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Contato WhatsApp *</label>
              <input v-model="form.contact" type="tel" placeholder="(11) 99999-9999" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Endereço de Origem *</label>
              <input v-model="form.start" type="text" placeholder="Endereço completo de origem" required />
            </div>
            
            <div class="form-group">
              <label>Endereço de Destino *</label>
              <input v-model="form.destination" type="text" placeholder="Endereço completo de destino" required />
            </div>
          </div>

          <div class="form-group">
            <label>Descrição da Entrega</label>
            <textarea v-model="form.description" placeholder="Descrição detalhada da entrega..." rows="3"></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Volume</label>
              <input v-model="form.volume" type="text" placeholder="ex: 2m³, 10 caixas" />
            </div>
            
            <div class="form-group">
              <label>Peso</label>
              <input v-model="form.weight" type="text" placeholder="ex: 50kg, 100kg" />
            </div>
            
            <div class="form-group">
              <label>Distância (km)</label>
              <input v-model="form.km" type="number" step="0.1" min="0" placeholder="0.0" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Custo Adicional (R$)</label>
              <input v-model="form.additionalCost" type="number" step="0.01" min="0" placeholder="0.00" />
            </div>
            
            <div class="form-group">
              <label>Custo Total (R$) *</label>
              <input v-model="form.cost" type="number" step="0.01" min="0" placeholder="0.00" required />
            </div>
          </div>

          <div class="form-row checkbox-row">
            <label class="checkbox-label">
              <input v-model="form.received" type="checkbox" />
              Entrega Recebida
            </label>
            
            <label class="checkbox-label">
              <input v-model="form.completed" type="checkbox" />
              Entrega Finalizada
            </label>
          </div>

          <div class="form-actions">
            <button type="submit" :disabled="saving" class="save-btn">
              {{ saving ? 'Salvando...' : 'Salvar' }}
            </button>
            <button type="button" @click="cancel" class="cancel-btn">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { backendService } from '../services/backend.js'

const emit = defineEmits(['back'])

// Reactive state
const deliveries = ref([])
const businesses = ref([])
const customers = ref([])
const couriers = ref([])
const loading = ref(false)
const error = ref(null)
const formLoading = ref(false)
const saving = ref(false)

// Form state
const showForm = ref(false)
const editingDelivery = ref(null)

// Filters
const statusFilter = ref('')
const searchText = ref('')

// Form data
const form = ref({
  businessId: '',
  customerId: '',
  courierId: '',
  start: '',
  destination: '',
  contact: '',
  description: '',
  volume: '',
  weight: '',
  km: '',
  additionalCost: '',
  cost: '',
  received: false,
  completed: false
})

// Computed
const filteredDeliveries = computed(() => {
  let filtered = deliveries.value

  // Filter by status
  if (statusFilter.value) {
    filtered = filtered.filter(delivery => {
      const status = getDeliveryStatus(delivery)
      return status === statusFilter.value
    })
  }

  // Filter by search text
  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    filtered = filtered.filter(delivery => {
      return getCustomerName(delivery).toLowerCase().includes(search) ||
             getCourierName(delivery).toLowerCase().includes(search) ||
             getBusinessName(delivery).toLowerCase().includes(search) ||
             delivery.start?.toLowerCase().includes(search) ||
             delivery.destination?.toLowerCase().includes(search)
    })
  }

  return filtered
})

// Methods
function goBack() {
  emit('back')
}

async function loadDeliveries() {
  loading.value = true
  error.value = null
  
  try {
    const [deliveriesData, businessesData, customersData, couriersData] = await Promise.all([
      backendService.getDeliveries(),
      backendService.getUsers(), // Buscar usuários para obter empresas
      backendService.getCustomers(),
      backendService.getCouriers()
    ])
    
    deliveries.value = deliveriesData || []
    
    // Filtrar apenas usuários do tipo BUSINESS para dropdown
    businesses.value = (businessesData || []).filter(user => user.profile === 'BUSINESS')
    customers.value = customersData || []
    couriers.value = couriersData || []
    
    console.log('Entregas carregadas:', deliveries.value.length)
    console.log('Empresas disponíveis:', businesses.value.length)
    console.log('Clientes disponíveis:', customers.value.length)
    console.log('Entregadores disponíveis:', couriers.value.length)
    
  } catch (err) {
    console.error('Erro ao carregar entregas:', err)
    error.value = err.message || 'Erro ao conectar com o servidor'
  } finally {
    loading.value = false
  }
}

function getDeliveryStatus(delivery) {
  if (delivery.completed) return 'completed'
  if (delivery.received) return 'received' 
  return 'pending'
}

function getBusinessName(delivery) {
  return delivery.business?.name || delivery.business?.user?.name || 'N/A'
}

function getCustomerName(delivery) {
  return delivery.customer?.user?.name || delivery.customer?.name || 'N/A'
}

function getCourierName(delivery) {
  return delivery.courier?.user?.name || delivery.courier?.name || 'N/A'
}

function getStatusClass(delivery) {
  const status = getDeliveryStatus(delivery)
  return `status-${status}`
}

function getStatusText(delivery) {
  const status = getDeliveryStatus(delivery)
  const statusMap = {
    pending: 'Pendente',
    received: 'Recebida',
    completed: 'Finalizada'
  }
  return statusMap[status] || 'Desconhecido'
}

function formatDate(dateStr) {
  if (!dateStr) return 'N/A'
  
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return 'Data inválida'
  }
}

async function editDelivery(delivery) {
  editingDelivery.value = delivery
  
  // Mapear os dados para o formulário
  form.value = {
    businessId: delivery.business?.id || '',
    customerId: delivery.customer?.id || '',
    courierId: delivery.courier?.id || '',
    start: delivery.start || '',
    destination: delivery.destination || '',
    contact: delivery.contact || '',
    description: delivery.description || '',
    volume: delivery.volume || '',
    weight: delivery.weight || '',
    km: delivery.km || '',
    additionalCost: delivery.additionalCost || '',
    cost: delivery.cost || '',
    received: delivery.received || false,
    completed: delivery.completed || false
  }
  
  showForm.value = true
}

async function deleteDelivery(id) {
  if (!confirm('Tem certeza que deseja excluir esta entrega?')) {
    return
  }
  
  try {
    await backendService.deleteDelivery(id)
    
    // Remover da lista local
    deliveries.value = deliveries.value.filter(d => d.id !== id)
    
    console.log('Entrega excluída com sucesso')
  } catch (err) {
    console.error('Erro ao excluir entrega:', err)
    alert('Erro ao excluir entrega: ' + (err.message || 'Erro desconhecido'))
  }
}

async function saveDelivery() {
  saving.value = true
  
  try {
    // Preparar dados para envio
    const deliveryData = {
      business: { id: parseInt(form.value.businessId) },
      customer: { id: parseInt(form.value.customerId) },
      courier: { id: parseInt(form.value.courierId) },
      start: form.value.start,
      destination: form.value.destination,
      contact: form.value.contact,
      description: form.value.description,
      volume: form.value.volume,
      weight: form.value.weight,
      km: parseFloat(form.value.km) || 0,
      additionalCost: parseFloat(form.value.additionalCost) || 0,
      cost: parseFloat(form.value.cost),
      received: form.value.received,
      completed: form.value.completed
    }
    
    let savedDelivery
    
    if (editingDelivery.value) {
      // Editar entrega existente
      savedDelivery = await backendService.updateDelivery(editingDelivery.value.id, deliveryData)
      
      // Atualizar na lista local
      const index = deliveries.value.findIndex(d => d.id === editingDelivery.value.id)
      if (index !== -1) {
        deliveries.value[index] = { ...editingDelivery.value, ...savedDelivery }
      }
      
      console.log('Entrega atualizada com sucesso')
    } else {
      // Criar nova entrega
      savedDelivery = await backendService.createDelivery(deliveryData)
      
      // Adicionar à lista local
      deliveries.value.push(savedDelivery)
      
      console.log('Entrega criada com sucesso')
    }
    
    showForm.value = false
    resetForm()
    
  } catch (err) {
    console.error('Erro ao salvar entrega:', err)
    alert('Erro ao salvar entrega: ' + (err.message || 'Erro desconhecido'))
  } finally {
    saving.value = false
  }
}

function cancel() {
  showForm.value = false
  editingDelivery.value = null
  resetForm()
}

function resetForm() {
  form.value = {
    businessId: '',
    customerId: '',
    courierId: '',
    start: '',
    destination: '',
    contact: '',
    description: '',
    volume: '',
    weight: '',
    km: '',
    additionalCost: '',
    cost: '',
    received: false,
    completed: false
  }
}

function filterDeliveries() {
  // Esta função é chamada automaticamente pelo computed filteredDeliveries
  // Não precisa fazer nada aqui
}

// Lifecycle
onMounted(() => {
  loadDeliveries()
})
</script>
  {
    id: 2,
    user: { id: 6, name: 'Logística Sul' },
    business: { id: 1 }
  },
  {
    id: 3,
    user: { id: 7, name: 'Centro de Distribuição ABC' },
    business: { id: 4 }
  }
]);

const couriers = ref([
  {
    id: 1,
    user: { id: 2, name: 'João Silva' },
    business: { id: 1 }
  },
  {
    id: 2,
    user: { id: 5, name: 'Carlos Santos' },
    business: { id: 1 }
  }
]);

const deliveries = ref([
  {
    id: 1,
    business: { id: 1, name: 'CSOnline Delivery' },
    customer: { id: 1, user: { name: 'Distribuidora Norte' } },
    courier: { id: 1, user: { name: 'João Silva' } },
    start: 'Rua das Flores, 123 - Centro, Curitiba/PR',
    destination: 'Av. Brasil, 456 - Batel, Curitiba/PR',
    contact: '41987654321',
    description: 'Entrega de documentos urgentes',
    volume: '1m³',
    weight: '5kg',
    km: '12',
    additionalCost: 0.00,
    cost: 25.00,
    received: true,
    completed: false,
    datatime: new Date('2025-08-01T08:30:00')
  },
  {
    id: 2,
    business: { id: 1, name: 'CSOnline Delivery' },
    customer: { id: 2, user: { name: 'Logística Sul' } },
    courier: { id: 2, user: { name: 'Carlos Santos' } },
    start: 'Av. Industrial, 789 - CIC, Curitiba/PR',
    destination: 'Rua Comercial, 321 - Centro, São José dos Pinhais/PR',
    contact: '41976543210',
    description: 'Entrega de equipamentos eletrônicos',
    volume: '3m³',
    weight: '25kg',
    km: '18',
    additionalCost: 5.00,
    cost: 45.00,
    received: true,
    completed: true,
    datatime: new Date('2025-08-01T10:15:00')
  },
  {
    id: 3,
    business: { id: 4, name: 'Gestão Empresarial' },
    customer: { id: 3, user: { name: 'Centro de Distribuição ABC' } },
    courier: { id: 1, user: { name: 'João Silva' } },
    start: 'Rua Logística, 555 - Araucária/PR',
    destination: 'Av. das Indústrias, 888 - Fazenda Rio Grande/PR',
    contact: '41965432109',
    description: 'Transporte de mercadorias diversas',
    volume: '5m³',
    weight: '80kg',
    km: '25',
    additionalCost: 10.00,
    cost: 75.00,
    received: false,
    completed: false,
    datatime: new Date('2025-08-01T14:20:00')
  }
]);

const statusFilter = ref('');
const showForm = ref(false);
const editingDelivery = ref(null);
const form = ref({
  business: { id: '' },
  customer: { id: '' },
  courier: { id: '' },
  start: '',
  destination: '',
  contact: '',
  description: '',
  volume: '',
  weight: '',
  km: '',
  additionalCost: '',
  cost: '',
  received: false,
  completed: false
});

const filteredDeliveries = computed(() => {
  if (!statusFilter.value) return deliveries.value;
  
  return deliveries.value.filter(delivery => {
    switch (statusFilter.value) {
      case 'pending':
        return !delivery.received && !delivery.completed;
      case 'received':
        return delivery.received && !delivery.completed;
      case 'completed':
        return delivery.completed;
      default:
        return true;
    }
  });
});

function getStatusClass(delivery) {
  if (delivery.completed) return 'status-completed';
  if (delivery.received) return 'status-received';
  return 'status-pending';
}

function getStatusText(delivery) {
  if (delivery.completed) return 'Finalizada';
  if (delivery.received) return 'Recebida';
  return 'Pendente';
}

function formatDate(date) {
  if (!date) return '';
  return new Date(date).toLocaleString('pt-BR');
}

function editDelivery(delivery) {
  editingDelivery.value = delivery;
  form.value = {
    business: { id: delivery.business.id },
    customer: { id: delivery.customer.id },
    courier: { id: delivery.courier.id },
    start: delivery.start,
    destination: delivery.destination,
    contact: delivery.contact,
    description: delivery.description,
    volume: delivery.volume,
    weight: delivery.weight,
    km: delivery.km,
<style scoped>
.delivery-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 1400px;
  margin: 32px auto;
}

.delivery-management h2 {
  margin-bottom: 24px;
  color: #1976d2;
}

.loading, .error {
  text-align: center;
  padding: 40px;
}

.error p {
  color: #d32f2f;
  margin-bottom: 16px;
}

.actions {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.primary-btn {
  padding: 10px 20px;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.primary-btn:hover {
  background: #1565c0;
}

.back-btn {
  padding: 10px 20px;
  background: #888;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.back-btn:hover {
  background: #555;
}

.filters {
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 300px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 16px;
  font-size: 14px;
}

th, td {
  padding: 12px 8px;
  border-bottom: 1px solid #eee;
  text-align: left;
}

th {
  background: #f5f5f5;
  font-weight: bold;
  position: sticky;
  top: 0;
}

.status-pending {
  color: #f57c00;
  font-weight: bold;
}

.status-received {
  color: #1976d2;
  font-weight: bold;
}

.status-completed {
  color: #388e3c;
  font-weight: bold;
}

.edit-btn, .delete-btn {
  margin-right: 8px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.edit-btn {
  background: #1976d2;
  color: #fff;
}

.edit-btn:hover {
  background: #1565c0;
}

.delete-btn {
  background: #d32f2f;
  color: #fff;
}

.delete-btn:hover {
  background: #c62828;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
}

.modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  overflow-y: auto;
}

.modal-content {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  min-width: 700px;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
  margin: 20px;
}

.modal-content h3 {
  margin-bottom: 24px;
  color: #1976d2;
}

.form-loading {
  text-align: center;
  padding: 40px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row > * {
  flex: 1;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 500;
  color: #333;
}

.checkbox-row {
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 0;
  font-weight: normal;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.save-btn {
  padding: 10px 24px;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.save-btn:hover:not(:disabled) {
  background: #1565c0;
}

.save-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 10px 24px;
  background: #666;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}

.cancel-btn:hover {
  background: #555;
}

select, input[type="text"], input[type="tel"], input[type="number"], textarea {
  width: 100%;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  font-size: 14px;
}

select:focus, input:focus, textarea:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

input[type="checkbox"] {
  width: auto;
  margin: 0;
}

textarea {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

/* Responsividade */
@media (max-width: 1200px) {
  .delivery-management {
    padding: 20px;
    margin: 20px auto;
  }
  
  table {
    font-size: 12px;
  }
  
  th, td {
    padding: 8px 6px;
  }
}

@media (max-width: 768px) {
  .modal-content {
    min-width: auto;
    width: 95vw;
    padding: 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    max-width: none;
  }
}
</style>
