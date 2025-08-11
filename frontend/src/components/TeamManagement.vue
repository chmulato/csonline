<template>
  <div class="team-management">
    <h2>Gestão de Times</h2>
    
    <!-- Loading indicator -->
    <div v-if="loading" class="loading">
      Carregando...
    </div>
    
    <!-- Error message -->
    <div v-if="error" class="error">
      {{ error }}
    </div>
    
    <div class="actions">
      <button @click="showForm = true">Novo Time</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    
    <div class="filters">
      <select v-model="businessFilter" @change="filterTeams">
        <option value="">Todas as Empresas</option>
        <option v-for="business in businesses" :key="business.id" :value="business.id">
          {{ business.name }}
        </option>
      </select>
    </div>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Empresa (CD)</th>
          <th>Entregador</th>
          <th>Email</th>
          <th>WhatsApp</th>
          <th>Fator (%)</th>
          <th>Status</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="team in filteredTeams" :key="team.id">
          <td>{{ team.id }}</td>
          <td>{{ team.business.name }}</td>
          <td>{{ team.courier.name }}</td>
          <td>{{ team.courier.email }}</td>
          <td>{{ team.courier.mobile }}</td>
          <td>{{ team.factorCourier }}%</td>
          <td>
            <span :class="getStatusClass(team)">{{ getStatusText(team) }}</span>
          </td>
          <td>
            <button @click="editTeam(team)">Editar</button>
            <button @click="deleteTeam(team.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingTeam ? 'Editar Time' : 'Novo Time' }}</h3>
        <form @submit.prevent="saveTeam">
          <div class="form-row">
            <select v-model="form.business.id" @change="onBusinessChange" required>
              <option value="">Selecione a Empresa (CD)</option>
              <option v-for="business in customerBusinesses" :key="business.id" :value="business.id">
                {{ business.name }}
              </option>
            </select>
            <select v-model="form.courier.id" required>
              <option value="">Selecione o Entregador</option>
              <option v-for="courier in availableCouriers" :key="courier.id" :value="courier.id">
                {{ courier.name }} ({{ courier.email }})
              </option>
            </select>
          </div>

          <div class="form-row">
            <input v-model="form.factorCourier" type="number" step="0.01" min="0" max="100" placeholder="Fator de Comissão (%)" required />
            <select v-model="form.status">
              <option value="active">Ativo</option>
              <option value="inactive">Inativo</option>
              <option value="suspended">Suspenso</option>
            </select>
          </div>

          <div class="info-section" v-if="selectedCourier">
            <h4>Informações do Entregador Selecionado:</h4>
            <p><strong>Nome:</strong> {{ selectedCourier.name }}</p>
            <p><strong>Email:</strong> {{ selectedCourier.email }}</p>
            <p><strong>WhatsApp:</strong> {{ selectedCourier.mobile }}</p>
            <p><strong>Empresa Principal:</strong> {{ getOriginalBusiness(selectedCourier.id) }}</p>
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
import { ref, computed, watch, onMounted } from 'vue';
import { backendService } from '../services/backend.js';
import { formatDataForBackend } from '../config/backend.js';

const emit = defineEmits(['back']);

// Estado reativo
const showForm = ref(false);
const editingTeam = ref(null);
const businessFilter = ref('');
const loading = ref(false);
const error = ref(null);

function goBack() {
  emit('back');
}

// Dados simulados de empresas (incluindo CDs)
const businesses = ref([
  { id: 1, name: 'CSOnline Delivery', role: 'admin' },
  { id: 4, name: 'Gestão Empresarial', role: 'user' }
]);

// Empresas que são centros de distribuição (customers)
const customerBusinesses = ref([
  { id: 3, name: 'Distribuidora Norte' },
  { id: 6, name: 'Logística Sul' },
  { id: 7, name: 'Centro de Distribuição ABC' }
]);

// Entregadores disponíveis
const couriers = ref([
  {
    id: 2,
    name: 'João Silva',
    email: 'joao.courier@csonline.com',
    mobile: '41988776655',
    role: 'courier',
    originalBusiness: 1 // CSOnline Delivery
  },
  {
    id: 5,
    name: 'Carlos Santos',
    email: 'carlos.courier@csonline.com',
    mobile: '41977665544',
    role: 'courier',
    originalBusiness: 1 // CSOnline Delivery
  },
  {
    id: 8,
    name: 'Maria Oliveira',
    email: 'maria.courier@csonline.com',
    mobile: '41966554433',
    role: 'courier',
    originalBusiness: 4 // Gestão Empresarial
  },
  {
    id: 9,
    name: 'Pedro Costa',
    email: 'pedro.courier@csonline.com',
    mobile: '41955443322',
    role: 'courier',
    originalBusiness: 1 // CSOnline Delivery
  }
]);

// Times cadastrados
const teams = ref([
  {
    id: 1,
    business: { id: 3, name: 'Distribuidora Norte' },
    courier: { id: 2, name: 'João Silva', email: 'joao.courier@csonline.com', mobile: '41988776655' },
    factorCourier: 12.5,
    status: 'active'
  },
  {
    id: 2,
    business: { id: 6, name: 'Logística Sul' },
    courier: { id: 5, name: 'Carlos Santos', email: 'carlos.courier@csonline.com', mobile: '41977665544' },
    factorCourier: 15.0,
    status: 'active'
  },
  {
    id: 3,
    business: { id: 7, name: 'Centro de Distribuição ABC' },
    courier: { id: 8, name: 'Maria Oliveira', email: 'maria.courier@csonline.com', mobile: '41966554433' },
    factorCourier: 10.0,
    status: 'inactive'
  },
  {
    id: 4,
    business: { id: 3, name: 'Distribuidora Norte' },
    courier: { id: 9, name: 'Pedro Costa', email: 'pedro.courier@csonline.com', mobile: '41955443322' },
    factorCourier: 14.0,
    status: 'active'
  }
]);

// Formulário para criar/editar time
const form = ref({
  business: { id: '' },
  courier: { id: '' },
  factorCourier: '',
  status: 'active'
});

// Funções para carregar dados da API
async function loadTeams() {
  try {
    loading.value = true;
    error.value = null;
    teams.value = await backendService.getTeams();
  } catch (err) {
    error.value = 'Erro ao carregar times: ' + err.message;
    console.error('Erro ao carregar times:', err);
  } finally {
    loading.value = false;
  }
}

async function loadBusinesses() {
  try {
    const allCustomers = await backendService.getCustomers();
    // Separar businesses que são customers (CDs) das que são do sistema
    customerBusinesses.value = allCustomers.filter(c => c.role === 'customer' || !c.role);
    businesses.value = allCustomers; // Para o filtro mostrar todas
  } catch (err) {
    error.value = 'Erro ao carregar empresas: ' + err.message;
    console.error('Erro ao carregar empresas:', err);
  }
}

async function loadCouriers() {
  try {
    couriers.value = await backendService.getCouriers();
  } catch (err) {
    error.value = 'Erro ao carregar entregadores: ' + err.message;
    console.error('Erro ao carregar entregadores:', err);
  }
}

const filteredTeams = computed(() => {
  if (!businessFilter.value) return teams.value;
  return teams.value.filter(team => team.business.id == businessFilter.value);
});

const availableCouriers = computed(() => {
  // Mostra todos os entregadores, mas indica se já estão em um time
  return couriers.value.map(courier => {
    const inTeam = teams.value.find(team => 
      team.courier.id === courier.id && 
      team.business.id == form.value.business.id &&
      (!editingTeam.value || team.id !== editingTeam.value.id)
    );
    return {
      ...courier,
      inTeam: !!inTeam
    };
  });
});

const selectedCourier = computed(() => {
  if (!form.value.courier.id) return null;
  return couriers.value.find(c => c.id == form.value.courier.id);
});

function getOriginalBusiness(courierId) {
  const courier = couriers.value.find(c => c.id === courierId);
  if (!courier) return 'N/A';
  const business = businesses.value.find(b => b.id === courier.originalBusiness);
  return business ? business.name : 'N/A';
}

function getStatusClass(team) {
  switch (team.status) {
    case 'active': return 'status-active';
    case 'inactive': return 'status-inactive';
    case 'suspended': return 'status-suspended';
    default: return '';
  }
}

function getStatusText(team) {
  switch (team.status) {
    case 'active': return 'Ativo';
    case 'inactive': return 'Inativo';
    case 'suspended': return 'Suspenso';
    default: return team.status;
  }
}

function onBusinessChange() {
  // Reset courier selection when business changes
  form.value.courier.id = '';
}

function editTeam(team) {
  editingTeam.value = team;
  form.value = {
    business: { id: team.business.id },
    courier: { id: team.courier.id },
    factorCourier: team.factorCourier,
    status: team.status
  };
  showForm.value = true;
}

async function deleteTeam(id) {
  if (confirm('Tem certeza que deseja excluir este time?')) {
    try {
      loading.value = true;
      error.value = null;
      await backendService.deleteTeam(id);
      teams.value = teams.value.filter(t => t.id !== id);
    } catch (err) {
      error.value = 'Erro ao excluir time: ' + err.message;
      console.error('Erro ao excluir time:', err);
    } finally {
      loading.value = false;
    }
  }
}

async function saveTeam() {
  try {
    loading.value = true;
    error.value = null;

    const selectedBusiness = customerBusinesses.value.find(b => b.id == form.value.business.id);
    const selectedCourierData = couriers.value.find(c => c.id == form.value.courier.id);

    const raw = {
      id: editingTeam.value?.id || null,
      business: selectedBusiness,
      courier: selectedCourierData,
      factorCourier: form.value.factorCourier,
      status: form.value.status
    }
    const formatted = formatDataForBackend(raw, 'team')
    const teamData = {
      business: formatted.business,
      courier: formatted.courier,
      factorCourier: formatted.factorCourier,
      status: formatted.status
    }

    if (editingTeam.value) {
      const updatedTeam = await backendService.updateTeam(editingTeam.value.id, teamData);
      const index = teams.value.findIndex(t => t.id === editingTeam.value.id);
      if (index !== -1) teams.value[index] = updatedTeam;
      editingTeam.value = null;
    } else {
      const newTeam = await backendService.createTeam(teamData);
      teams.value.push(newTeam);
    }

    showForm.value = false;
    resetForm();
  } catch (err) {
    error.value = 'Erro ao salvar time: ' + err.message;
    console.error('Erro ao salvar time:', err);
  } finally {
    loading.value = false;
  }
}

function cancel() {
  showForm.value = false;
  editingTeam.value = null;
  resetForm();
}

function resetForm() {
  form.value = {
    business: { id: '' },
    courier: { id: '' },
    factorCourier: '',
    status: 'active'
  };
}

function filterTeams() {
  // Função é chamada automaticamente pelo computed filteredTeams
}

// Carregar dados quando o componente for montado
onMounted(async () => {
  await Promise.all([
    loadTeams(),
    loadBusinesses(),
    loadCouriers()
  ]);
});
</script>

<style scoped>
.team-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 1400px;
  margin: 32px auto;
}

.team-management h2 {
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

.status-active {
  color: #388e3c;
  font-weight: bold;
}

.status-inactive {
  color: #f57c00;
  font-weight: bold;
}

.status-suspended {
  color: #d32f2f;
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

.info-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 4px;
  margin: 16px 0;
  border-left: 4px solid #1976d2;
}

.info-section h4 {
  margin: 0 0 12px 0;
  color: #1976d2;
}

.info-section p {
  margin: 4px 0;
  font-size: 14px;
}

.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

select, input[type="number"] {
  width: 100%;
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

select:focus, input:focus {
  outline: none;
  border-color: #1976d2;
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

/* Loading e Error States */
.loading {
  background: #e3f2fd;
  color: #1976d2;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  margin-bottom: 16px;
  font-weight: 500;
}

.error {
  background: #ffebee;
  color: #d32f2f;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
  border-left: 4px solid #d32f2f;
}
</style>
