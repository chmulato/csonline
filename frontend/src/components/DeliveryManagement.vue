<template>
  <div class="delivery-management">
    <h2>Gestão de Entregas</h2>
    <div class="actions">
      <button @click="showForm = true">Nova Entrega</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    
    <div class="filters">
      <select v-model="statusFilter" @change="filterDeliveries">
        <option value="">Todos os Status</option>
        <option value="pending">Pendente</option>
        <option value="received">Recebida</option>
        <option value="completed">Finalizada</option>
      </select>
    </div>

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
          <td>{{ delivery.business.name }}</td>
          <td>{{ delivery.customer.user.name }}</td>
          <td>{{ delivery.courier.user.name }}</td>
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
            <button @click="editDelivery(delivery)">Editar</button>
            <button @click="deleteDelivery(delivery.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingDelivery ? 'Editar Entrega' : 'Nova Entrega' }}</h3>
        <form @submit.prevent="saveDelivery">
          <div class="form-row">
            <select v-model="form.business.id" required>
              <option value="">Selecione a Empresa</option>
              <option v-for="business in businesses" :key="business.id" :value="business.id">
                {{ business.name }}
              </option>
            </select>
            <select v-model="form.customer.id" required>
              <option value="">Selecione o Cliente</option>
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.user.name }}
              </option>
            </select>
          </div>
          
          <div class="form-row">
            <select v-model="form.courier.id" required>
              <option value="">Selecione o Entregador</option>
              <option v-for="courier in couriers" :key="courier.id" :value="courier.id">
                {{ courier.user.name }}
              </option>
            </select>
            <input v-model="form.contact" type="tel" placeholder="Contato WhatsApp" required />
          </div>

          <div class="form-row">
            <input v-model="form.start" type="text" placeholder="Endereço de Origem" required />
            <input v-model="form.destination" type="text" placeholder="Endereço de Destino" required />
          </div>

          <textarea v-model="form.description" placeholder="Descrição da Entrega" rows="3"></textarea>

          <div class="form-row">
            <input v-model="form.volume" type="text" placeholder="Volume (ex: 2m³)" />
            <input v-model="form.weight" type="text" placeholder="Peso (ex: 50kg)" />
            <input v-model="form.km" type="text" placeholder="Distância (km)" />
          </div>

          <div class="form-row">
            <input v-model="form.additionalCost" type="number" step="0.01" min="0" placeholder="Custo Adicional (R$)" />
            <input v-model="form.cost" type="number" step="0.01" min="0" placeholder="Custo Total (R$)" required />
          </div>

          <div class="form-row">
            <label>
              <input v-model="form.received" type="checkbox" />
              Entrega Recebida
            </label>
            <label>
              <input v-model="form.completed" type="checkbox" />
              Entrega Finalizada
            </label>
          </div>

          <div class="form-actions">
            <button type="submit">Salvar</button>
            <button type="button" @click="cancel">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const emit = defineEmits(['back']);

function goBack() {
  emit('back');
}

// Dados simulados
const businesses = ref([
  { id: 1, name: 'CSOnline Delivery' },
  { id: 4, name: 'Gestão Empresarial' }
]);

const customers = ref([
  {
    id: 1,
    user: { id: 3, name: 'Distribuidora Norte' },
    business: { id: 1 }
  },
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
    additionalCost: delivery.additionalCost,
    cost: delivery.cost,
    received: delivery.received,
    completed: delivery.completed
  };
  showForm.value = true;
}

function deleteDelivery(id) {
  if (confirm('Tem certeza que deseja excluir esta entrega?')) {
    deliveries.value = deliveries.value.filter(d => d.id !== id);
  }
}

function saveDelivery() {
  const selectedBusiness = businesses.value.find(b => b.id == form.value.business.id);
  const selectedCustomer = customers.value.find(c => c.id == form.value.customer.id);
  const selectedCourier = couriers.value.find(c => c.id == form.value.courier.id);
  
  if (editingDelivery.value) {
    // Editar entrega existente
    Object.assign(editingDelivery.value, {
      business: selectedBusiness,
      customer: selectedCustomer,
      courier: selectedCourier,
      start: form.value.start,
      destination: form.value.destination,
      contact: form.value.contact,
      description: form.value.description,
      volume: form.value.volume,
      weight: form.value.weight,
      km: form.value.km,
      additionalCost: parseFloat(form.value.additionalCost) || 0,
      cost: parseFloat(form.value.cost),
      received: form.value.received,
      completed: form.value.completed
    });
    editingDelivery.value = null;
  } else {
    // Criar nova entrega
    const newDelivery = {
      id: Date.now(),
      business: selectedBusiness,
      customer: selectedCustomer,
      courier: selectedCourier,
      start: form.value.start,
      destination: form.value.destination,
      contact: form.value.contact,
      description: form.value.description,
      volume: form.value.volume,
      weight: form.value.weight,
      km: form.value.km,
      additionalCost: parseFloat(form.value.additionalCost) || 0,
      cost: parseFloat(form.value.cost),
      received: form.value.received,
      completed: form.value.completed,
      datatime: new Date()
    };
    deliveries.value.push(newDelivery);
  }
  
  showForm.value = false;
  resetForm();
}

function cancel() {
  showForm.value = false;
  editingDelivery.value = null;
  resetForm();
}

function resetForm() {
  form.value = {
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
  };
}

function filterDeliveries() {
  // Função é chamada automaticamente pelo computed filteredDeliveries
}
</script>

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
}

.actions {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.filters {
  margin-bottom: 16px;
}

.back-btn {
  padding: 8px 18px;
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

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 16px;
  font-size: 14px;
}

th, td {
  padding: 8px;
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

button {
  margin-right: 8px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background: #1976d2;
  color: #fff;
  cursor: pointer;
  font-size: 12px;
}

button:hover {
  background: #1565c0;
}

.modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.2);
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
  min-width: 600px;
  max-width: 800px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  margin: 20px;
}

.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.form-row > * {
  flex: 1;
}

.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

select, input[type="text"], input[type="tel"], input[type="number"], textarea {
  width: 100%;
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

select:focus, input:focus, textarea:focus {
  outline: none;
  border-color: #1976d2;
}

label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

input[type="checkbox"] {
  width: auto;
  margin-bottom: 0;
}

textarea {
  resize: vertical;
  min-height: 60px;
}

/* Responsividade para tabela */
@media (max-width: 1200px) {
  table {
    font-size: 12px;
  }
  
  th, td {
    padding: 6px;
  }
}
</style>
