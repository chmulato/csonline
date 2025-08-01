<template>
  <div class="sms-management">
    <h2>Gestão de Mensagens WhatsApp</h2>
    <div class="actions">
      <button @click="showForm = true">Nova Mensagem</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    
    <div class="filters">
      <select v-model="deliveryFilter" @change="filterSMS">
        <option value="">Todas as Entregas</option>
        <option v-for="delivery in deliveries" :key="delivery.id" :value="delivery.id">
          Entrega #{{ delivery.id }} - {{ delivery.customer.user.name }}
        </option>
      </select>
      <select v-model="typeFilter" @change="filterSMS">
        <option value="">Todos os Tipos</option>
        <option value="pickup">Coleta</option>
        <option value="delivery">Entrega</option>
        <option value="update">Atualização</option>
        <option value="problem">Problema</option>
        <option value="completion">Finalização</option>
      </select>
      <input v-model="dateFilter" type="date" @change="filterSMS" placeholder="Filtrar por data" />
    </div>

    <div class="stats">
      <div class="stat-card">
        <h4>Total de Mensagens</h4>
        <span class="stat-number">{{ filteredSMS.length }}</span>
      </div>
      <div class="stat-card">
        <h4>Mensagens Hoje</h4>
        <span class="stat-number">{{ getTodayMessages() }}</span>
      </div>
      <div class="stat-card">
        <h4>Entregas Ativas</h4>
        <span class="stat-number">{{ getActiveDeliveries() }}</span>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Entrega</th>
          <th>Cliente</th>
          <th>Tipo</th>
          <th>Peça</th>
          <th>De</th>
          <th>Para</th>
          <th>Mensagem</th>
          <th>Data/Hora</th>
          <th>Status</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sms in filteredSMS" :key="sms.id">
          <td>{{ sms.id }}</td>
          <td>
            <div class="delivery-info">
              <strong>#{{ sms.delivery.id }}</strong>
              <small>{{ sms.delivery.destination }}</small>
            </div>
          </td>
          <td>{{ sms.delivery.customer.user.name }}</td>
          <td>
            <span :class="getTypeClass(sms.type)">{{ getTypeText(sms.type) }}</span>
          </td>
          <td>{{ sms.piece }}/{{ sms.delivery.totalPieces || 1 }}</td>
          <td>
            <div class="contact-info">
              <strong>{{ getContactName(sms.mobileFrom) }}</strong>
              <small>{{ formatPhone(sms.mobileFrom) }}</small>
            </div>
          </td>
          <td>
            <div class="contact-info">
              <strong>{{ getContactName(sms.mobileTo) }}</strong>
              <small>{{ formatPhone(sms.mobileTo) }}</small>
            </div>
          </td>
          <td>
            <div class="message-preview" :title="sms.message">
              {{ sms.message.length > 50 ? sms.message.substring(0, 50) + '...' : sms.message }}
            </div>
          </td>
          <td>{{ formatDateTime(sms.datetime) }}</td>
          <td>
            <span :class="getStatusClass(sms)">{{ getStatusText(sms) }}</span>
          </td>
          <td>
            <button @click="viewSMS(sms)" class="btn-view">Ver</button>
            <button @click="editSMS(sms)" class="btn-edit">Editar</button>
            <button @click="deleteSMS(sms.id)" class="btn-delete">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal para Nova/Editar Mensagem -->
    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingSMS ? 'Editar Mensagem' : 'Nova Mensagem WhatsApp' }}</h3>
        <form @submit.prevent="saveSMS">
          <div class="form-row">
            <select v-model="form.delivery.id" @change="onDeliveryChange" required>
              <option value="">Selecione a Entrega</option>
              <option v-for="delivery in deliveries" :key="delivery.id" :value="delivery.id">
                #{{ delivery.id }} - {{ delivery.customer.user.name }} ({{ delivery.destination }})
              </option>
            </select>
            <select v-model="form.type" required>
              <option value="">Tipo de Mensagem</option>
              <option value="pickup">Coleta</option>
              <option value="delivery">Entrega</option>
              <option value="update">Atualização</option>
              <option value="problem">Problema</option>
              <option value="completion">Finalização</option>
            </select>
          </div>

          <div class="form-row">
            <input v-model="form.piece" type="number" min="1" placeholder="Peça (ex: 1)" required />
            <input v-model="form.mobileFrom" type="tel" placeholder="WhatsApp Remetente" required />
            <input v-model="form.mobileTo" type="tel" placeholder="WhatsApp Destinatário" required />
          </div>

          <div class="delivery-context" v-if="selectedDelivery">
            <h4>Contexto da Entrega:</h4>
            <p><strong>Cliente:</strong> {{ selectedDelivery.customer.user.name }}</p>
            <p><strong>Entregador:</strong> {{ selectedDelivery.courier.user.name }}</p>
            <p><strong>Origem:</strong> {{ selectedDelivery.start }}</p>
            <p><strong>Destino:</strong> {{ selectedDelivery.destination }}</p>
            <p><strong>Contato:</strong> {{ formatPhone(selectedDelivery.contact) }}</p>
          </div>

          <div class="message-templates" v-if="form.type">
            <h4>Templates Sugeridos:</h4>
            <div class="template-buttons">
              <button type="button" v-for="template in getTemplates(form.type)" :key="template.id" 
                      @click="useTemplate(template)" class="template-btn">
                {{ template.name }}
              </button>
            </div>
          </div>

          <textarea v-model="form.message" placeholder="Mensagem WhatsApp" rows="4" required maxlength="500"></textarea>
          <small class="char-count">{{ form.message.length }}/500 caracteres</small>

          <div class="form-actions">
            <button type="submit">{{ editingSMS ? 'Atualizar' : 'Enviar Mensagem' }}</button>
            <button type="button" @click="cancel">Cancelar</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal para Visualizar Mensagem -->
    <div v-if="showViewModal" class="modal">
      <div class="modal-content">
        <h3>Detalhes da Mensagem WhatsApp</h3>
        <div class="sms-details">
          <div class="detail-section">
            <h4>Informações da Entrega</h4>
            <p><strong>ID:</strong> #{{ viewingSMS.delivery.id }}</p>
            <p><strong>Cliente:</strong> {{ viewingSMS.delivery.customer.user.name }}</p>
            <p><strong>Destino:</strong> {{ viewingSMS.delivery.destination }}</p>
          </div>
          
          <div class="detail-section">
            <h4>Informações da Mensagem</h4>
            <p><strong>Tipo:</strong> {{ getTypeText(viewingSMS.type) }}</p>
            <p><strong>Peça:</strong> {{ viewingSMS.piece }}</p>
            <p><strong>Data/Hora:</strong> {{ formatDateTime(viewingSMS.datetime) }}</p>
          </div>

          <div class="detail-section">
            <h4>Contatos</h4>
            <p><strong>De:</strong> {{ getContactName(viewingSMS.mobileFrom) }} ({{ formatPhone(viewingSMS.mobileFrom) }})</p>
            <p><strong>Para:</strong> {{ getContactName(viewingSMS.mobileTo) }} ({{ formatPhone(viewingSMS.mobileTo) }})</p>
          </div>

          <div class="detail-section">
            <h4>Mensagem</h4>
            <div class="message-full">{{ viewingSMS.message }}</div>
          </div>
        </div>
        
        <div class="form-actions">
          <button @click="closeViewModal">Fechar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const emit = defineEmits(['back']);

function goBack() {
  emit('back');
}

// Dados simulados de entregas
const deliveries = ref([
  {
    id: 1,
    customer: { user: { name: 'Distribuidora Norte' } },
    courier: { user: { name: 'João Silva' } },
    start: 'Rua das Flores, 123 - Centro, Curitiba/PR',
    destination: 'Av. Brasil, 456 - Batel, Curitiba/PR',
    contact: '41987654321',
    totalPieces: 2
  },
  {
    id: 2,
    customer: { user: { name: 'Logística Sul' } },
    courier: { user: { name: 'Carlos Santos' } },
    start: 'Av. Industrial, 789 - CIC, Curitiba/PR',
    destination: 'Rua Comercial, 321 - Centro, São José dos Pinhais/PR',
    contact: '41976543210',
    totalPieces: 1
  },
  {
    id: 3,
    customer: { user: { name: 'Centro de Distribuição ABC' } },
    courier: { user: { name: 'Maria Oliveira' } },
    start: 'Rua Logística, 555 - Araucária/PR',
    destination: 'Av. das Indústrias, 888 - Fazenda Rio Grande/PR',
    contact: '41965432109',
    totalPieces: 3
  }
]);

// Dados simulados de SMS
const smsMessages = ref([
  {
    id: 1,
    delivery: deliveries.value[0],
    piece: 1,
    type: 'pickup',
    mobileFrom: '41988776655',
    mobileTo: '41987654321',
    message: 'Olá! Sou o João, entregador da CSOnline. Estou a caminho para coletar sua encomenda. Previsão: 15 minutos.',
    datetime: new Date().toISOString()
  },
  {
    id: 2,
    delivery: deliveries.value[0],
    piece: 1,
    type: 'delivery',
    mobileFrom: '41988776655',
    mobileTo: '41987654321',
    message: 'Sua encomenda foi coletada com sucesso! Agora seguimos para o destino. Acompanhe pelo link: bit.ly/track123',
    datetime: new Date(Date.now() - 30 * 60000).toISOString()
  },
  {
    id: 3,
    delivery: deliveries.value[1],
    piece: 1,
    type: 'completion',
    mobileFrom: '41977665544',
    mobileTo: '41976543210',
    message: 'Entrega realizada com sucesso! Recebido por: Maria Silva às 14:30. Obrigado por escolher a CSOnline!',
    datetime: new Date(Date.now() - 2 * 60 * 60000).toISOString()
  },
  {
    id: 4,
    delivery: deliveries.value[2],
    piece: 2,
    type: 'problem',
    mobileFrom: '41966554433',
    mobileTo: '41965432109',
    message: 'Atenção: Endereço não localizado. Por favor, confirme o endereço de entrega ou entre em contato.',
    datetime: new Date(Date.now() - 60 * 60000).toISOString()
  }
]);

const deliveryFilter = ref('');
const typeFilter = ref('');
const dateFilter = ref('');
const showForm = ref(false);
const showViewModal = ref(false);
const editingSMS = ref(null);
const viewingSMS = ref(null);
const form = ref({
  delivery: { id: '' },
  piece: 1,
  type: '',
  mobileFrom: '',
  mobileTo: '',
  message: ''
});

const filteredSMS = computed(() => {
  let filtered = smsMessages.value;
  
  if (deliveryFilter.value) {
    filtered = filtered.filter(sms => sms.delivery.id == deliveryFilter.value);
  }
  
  if (typeFilter.value) {
    filtered = filtered.filter(sms => sms.type === typeFilter.value);
  }
  
  if (dateFilter.value) {
    const filterDate = new Date(dateFilter.value).toDateString();
    filtered = filtered.filter(sms => new Date(sms.datetime).toDateString() === filterDate);
  }
  
  return filtered.sort((a, b) => new Date(b.datetime) - new Date(a.datetime));
});

const selectedDelivery = computed(() => {
  if (!form.value.delivery.id) return null;
  return deliveries.value.find(d => d.id == form.value.delivery.id);
});

// Templates de mensagem por tipo
const messageTemplates = {
  pickup: [
    { id: 1, name: 'Saindo para coleta', text: 'Olá! Sou o entregador da CSOnline. Estou a caminho para coletar sua encomenda. Previsão: 15 minutos.' },
    { id: 2, name: 'Chegando', text: 'Estou chegando no local de coleta. Por favor, tenha a encomenda pronta.' }
  ],
  delivery: [
    { id: 1, name: 'A caminho', text: 'Sua encomenda foi coletada! Agora seguimos para o destino. Tempo estimado: 30 minutos.' },
    { id: 2, name: 'Próximo destino', text: 'Estou próximo ao local de entrega. Por favor, confirme se alguém estará disponível para receber.' }
  ],
  update: [
    { id: 1, name: 'Atualização status', text: 'Atualização da sua entrega: Em trânsito. Previsão de chegada: ' },
    { id: 2, name: 'Reagendamento', text: 'Precisamos reagendar sua entrega. Por favor, informe um novo horário de sua preferência.' }
  ],
  problem: [
    { id: 1, name: 'Endereço não encontrado', text: 'Endereço não localizado. Por favor, confirme o endereço de entrega.' },
    { id: 2, name: 'Destinatário ausente', text: 'Ninguém foi encontrado no local de entrega. Tentaremos novamente em 30 minutos.' }
  ],
  completion: [
    { id: 1, name: 'Entrega concluída', text: 'Entrega realizada com sucesso! Obrigado por escolher a CSOnline!' },
    { id: 2, name: 'Coleta concluída', text: 'Coleta realizada com sucesso! Sua encomenda está sendo transportada.' }
  ]
};

function getTodayMessages() {
  const today = new Date().toDateString();
  return smsMessages.value.filter(sms => new Date(sms.datetime).toDateString() === today).length;
}

function getActiveDeliveries() {
  return deliveries.value.length;
}

function getTypeClass(type) {
  switch (type) {
    case 'pickup': return 'type-pickup';
    case 'delivery': return 'type-delivery';
    case 'update': return 'type-update';
    case 'problem': return 'type-problem';
    case 'completion': return 'type-completion';
    default: return '';
  }
}

function getTypeText(type) {
  switch (type) {
    case 'pickup': return 'Coleta';
    case 'delivery': return 'Entrega';
    case 'update': return 'Atualização';
    case 'problem': return 'Problema';
    case 'completion': return 'Finalização';
    default: return type;
  }
}

function getStatusClass(sms) {
  const now = new Date();
  const smsDate = new Date(sms.datetime);
  const diffMinutes = (now - smsDate) / (1000 * 60);
  
  if (sms.type === 'completion') return 'status-completed';
  if (sms.type === 'problem') return 'status-problem';
  if (diffMinutes < 30) return 'status-recent';
  return 'status-normal';
}

function getStatusText(sms) {
  const now = new Date();
  const smsDate = new Date(sms.datetime);
  const diffMinutes = (now - smsDate) / (1000 * 60);
  
  if (sms.type === 'completion') return 'Finalizada';
  if (sms.type === 'problem') return 'Problema';
  if (diffMinutes < 30) return 'Recente';
  return 'Enviada';
}

function getContactName(mobile) {
  // Simula busca de nome por telefone
  const contacts = {
    '41988776655': 'João Silva (Entregador)',
    '41977665544': 'Carlos Santos (Entregador)',
    '41966554433': 'Maria Oliveira (Entregador)',
    '41987654321': 'Distribuidora Norte',
    '41976543210': 'Logística Sul',
    '41965432109': 'Centro ABC'
  };
  return contacts[mobile] || 'Contato';
}

function formatPhone(phone) {
  if (!phone) return '';
  return phone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
}

function formatDateTime(datetime) {
  if (!datetime) return '';
  return new Date(datetime).toLocaleString('pt-BR');
}

function getTemplates(type) {
  return messageTemplates[type] || [];
}

function useTemplate(template) {
  form.value.message = template.text;
}

function onDeliveryChange() {
  if (selectedDelivery.value) {
    form.value.mobileTo = selectedDelivery.value.contact;
  }
}

function viewSMS(sms) {
  viewingSMS.value = sms;
  showViewModal.value = true;
}

function closeViewModal() {
  showViewModal.value = false;
  viewingSMS.value = null;
}

function editSMS(sms) {
  editingSMS.value = sms;
  form.value = {
    delivery: { id: sms.delivery.id },
    piece: sms.piece,
    type: sms.type,
    mobileFrom: sms.mobileFrom,
    mobileTo: sms.mobileTo,
    message: sms.message
  };
  showForm.value = true;
}

function deleteSMS(id) {
  if (confirm('Tem certeza que deseja excluir esta mensagem?')) {
    smsMessages.value = smsMessages.value.filter(s => s.id !== id);
  }
}

function saveSMS() {
  const selectedDeliveryData = deliveries.value.find(d => d.id == form.value.delivery.id);
  
  if (editingSMS.value) {
    // Editar SMS existente
    Object.assign(editingSMS.value, {
      delivery: selectedDeliveryData,
      piece: parseInt(form.value.piece),
      type: form.value.type,
      mobileFrom: form.value.mobileFrom,
      mobileTo: form.value.mobileTo,
      message: form.value.message
    });
    editingSMS.value = null;
  } else {
    // Criar nova SMS
    const newSMS = {
      id: Date.now(),
      delivery: selectedDeliveryData,
      piece: parseInt(form.value.piece),
      type: form.value.type,
      mobileFrom: form.value.mobileFrom,
      mobileTo: form.value.mobileTo,
      message: form.value.message,
      datetime: new Date().toISOString()
    };
    smsMessages.value.push(newSMS);
  }
  
  showForm.value = false;
  resetForm();
}

function cancel() {
  showForm.value = false;
  editingSMS.value = null;
  resetForm();
}

function resetForm() {
  form.value = {
    delivery: { id: '' },
    piece: 1,
    type: '',
    mobileFrom: '',
    mobileTo: '',
    message: ''
  };
}

function filterSMS() {
  // Função é chamada automaticamente pelo computed filteredSMS
}
</script>

<style scoped>
.sms-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 1600px;
  margin: 32px auto;
}

.sms-management h2 {
  margin-bottom: 24px;
}

.actions {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.filters {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filters select, .filters input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  min-width: 200px;
}

.stats {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  min-width: 120px;
}

.stat-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #1976d2;
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
  font-size: 13px;
}

th, td {
  padding: 8px;
  border-bottom: 1px solid #eee;
  text-align: left;
  vertical-align: top;
}

th {
  background: #f5f5f5;
  font-weight: bold;
  position: sticky;
  top: 0;
}

.delivery-info, .contact-info {
  display: flex;
  flex-direction: column;
}

.delivery-info small, .contact-info small {
  color: #666;
  font-size: 11px;
}

.message-preview {
  max-width: 200px;
  font-size: 12px;
  line-height: 1.3;
}

.type-pickup { color: #1976d2; font-weight: bold; }
.type-delivery { color: #388e3c; font-weight: bold; }
.type-update { color: #f57c00; font-weight: bold; }
.type-problem { color: #d32f2f; font-weight: bold; }
.type-completion { color: #7b1fa2; font-weight: bold; }

.status-recent { color: #4caf50; font-weight: bold; }
.status-completed { color: #9c27b0; font-weight: bold; }
.status-problem { color: #f44336; font-weight: bold; }
.status-normal { color: #666; }

.btn-view, .btn-edit, .btn-delete {
  margin-right: 4px;
  padding: 4px 8px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 11px;
}

.btn-view { background: #2196f3; color: white; }
.btn-edit { background: #ff9800; color: white; }
.btn-delete { background: #f44336; color: white; }

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
  min-width: 700px;
  max-width: 900px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  margin: 20px;
  max-height: 90vh;
  overflow-y: auto;
}

.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.form-row > * {
  flex: 1;
}

.delivery-context {
  background: #e3f2fd;
  padding: 16px;
  border-radius: 4px;
  margin: 16px 0;
  border-left: 4px solid #2196f3;
}

.delivery-context h4 {
  margin: 0 0 12px 0;
  color: #1976d2;
}

.delivery-context p {
  margin: 4px 0;
  font-size: 14px;
}

.message-templates {
  margin: 16px 0;
}

.message-templates h4 {
  margin: 0 0 8px 0;
  color: #1976d2;
}

.template-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.template-btn {
  padding: 6px 12px;
  background: #e3f2fd;
  color: #1976d2;
  border: 1px solid #2196f3;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.template-btn:hover {
  background: #2196f3;
  color: white;
}

.char-count {
  color: #666;
  font-size: 12px;
  margin-top: -8px;
  margin-bottom: 12px;
  display: block;
}

.sms-details {
  display: grid;
  gap: 16px;
}

.detail-section {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 4px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #1976d2;
  border-bottom: 1px solid #ddd;
  padding-bottom: 8px;
}

.detail-section p {
  margin: 4px 0;
}

.message-full {
  background: #fff;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
  white-space: pre-wrap;
  line-height: 1.4;
}

.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

select, input, textarea {
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

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background: #1976d2;
  color: #fff;
  cursor: pointer;
  font-weight: bold;
}

button:hover {
  background: #1565c0;
}

/* Responsividade */
@media (max-width: 1400px) {
  table { font-size: 12px; }
  th, td { padding: 6px; }
}

@media (max-width: 768px) {
  .stats { flex-direction: column; }
  .filters { flex-direction: column; }
  .modal-content { min-width: 95vw; margin: 10px; }
}
</style>
