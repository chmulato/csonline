<template>
  <div class="courier-management">
    <h2>Gestão de Entregadores</h2>
    <div class="actions">
      <button @click="showForm = true">Novo Entregador</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>Nome</th>
          <th>Email</th>
          <th>WhatsApp</th>
          <th>Empresa</th>
          <th>Fator de Comissão</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="courier in couriers" :key="courier.id">
          <td>{{ courier.user.name }}</td>
          <td>{{ courier.user.email }}</td>
          <td>{{ courier.user.mobile }}</td>
          <td>{{ courier.business.name }}</td>
          <td>{{ courier.factorCourier }}%</td>
          <td>
            <button @click="editCourier(courier)">Editar</button>
            <button @click="deleteCourier(courier.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingCourier ? 'Editar Entregador' : 'Novo Entregador' }}</h3>
        <form @submit.prevent="saveCourier">
          <input v-model="form.user.name" type="text" placeholder="Nome do Entregador" required />
          <input v-model="form.user.email" type="email" placeholder="Email" required />
          <input v-model="form.user.mobile" type="tel" placeholder="WhatsApp (ex: 41999887766)" required />
          <select v-model="form.business.id" required>
            <option value="">Selecione a Empresa</option>
            <option v-for="business in businesses" :key="business.id" :value="business.id">
              {{ business.name }}
            </option>
          </select>
          <input v-model="form.factorCourier" type="number" step="0.01" min="0" max="100" placeholder="Fator de Comissão (%)" required />
          <input v-model="form.user.password" type="password" placeholder="Senha" required />
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
import { ref } from 'vue';

const emit = defineEmits(['back']);

function goBack() {
  emit('back');
}

// Dados simulados de empresas (usuários com role 'admin' ou 'user')
const businesses = ref([
  { id: 1, name: 'CSOnline Delivery', role: 'admin' },
  { id: 4, name: 'Gestão Empresarial', role: 'user' }
]);

// Dados simulados de entregadores
const couriers = ref([
  {
    id: 1,
    user: {
      id: 2,
      name: 'João Silva',
      email: 'joao.courier@csonline.com',
      mobile: '41988776655',
      role: 'courier'
    },
    business: { id: 1, name: 'CSOnline Delivery' },
    factorCourier: 15.00
  },
  {
    id: 2,
    user: {
      id: 5,
      name: 'Carlos Santos',
      email: 'carlos.courier@csonline.com',
      mobile: '41977665544',
      role: 'courier'
    },
    business: { id: 1, name: 'CSOnline Delivery' },
    factorCourier: 12.50
  }
]);

const showForm = ref(false);
const editingCourier = ref(null);
const form = ref({
  user: { name: '', email: '', mobile: '', password: '', role: 'courier' },
  business: { id: '' },
  factorCourier: ''
});

function editCourier(courier) {
  editingCourier.value = courier;
  form.value = {
    user: { ...courier.user, password: '' },
    business: { id: courier.business.id },
    factorCourier: courier.factorCourier
  };
  showForm.value = true;
}

function deleteCourier(id) {
  if (confirm('Tem certeza que deseja excluir este entregador?')) {
    couriers.value = couriers.value.filter(c => c.id !== id);
  }
}

function saveCourier() {
  const selectedBusiness = businesses.value.find(b => b.id == form.value.business.id);
  
  if (editingCourier.value) {
    // Editar entregador existente
    Object.assign(editingCourier.value.user, form.value.user);
    editingCourier.value.business = selectedBusiness;
    editingCourier.value.factorCourier = parseFloat(form.value.factorCourier);
    editingCourier.value = null;
  } else {
    // Criar novo entregador
    const newCourier = {
      id: Date.now(),
      user: { ...form.value.user, id: Date.now() + 1 },
      business: selectedBusiness,
      factorCourier: parseFloat(form.value.factorCourier)
    };
    couriers.value.push(newCourier);
  }
  
  showForm.value = false;
  resetForm();
}

function cancel() {
  showForm.value = false;
  editingCourier.value = null;
  resetForm();
}

function resetForm() {
  form.value = {
    user: { name: '', email: '', mobile: '', password: '', role: 'courier' },
    business: { id: '' },
    factorCourier: ''
  };
}
</script>

<style scoped>
.courier-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 1000px;
  margin: 32px auto;
}

.courier-management h2 {
  margin-bottom: 24px;
}

.actions {
  margin-bottom: 16px;
}

.back-btn {
  margin-left: 8px;
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
}

th, td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
}

th {
  background: #f5f5f5;
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
}

.modal-content {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  min-width: 400px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

select, input[type="text"], input[type="email"], input[type="tel"], input[type="password"], input[type="number"] {
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
</style>
