<template>
  <div class="user-management">
    <h2>Gestão de Usuários</h2>
    
    <div v-if="loading" class="loading">Carregando usuários...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <div class="actions">
  <button @click="showForm = true">Novo Usuário</button>
  <button class="back-btn" data-test="back-btn" type="button" @click="goBack">Voltar</button>
    </div>
    
    <div class="filters">
      <input v-model="searchTerm" type="text" placeholder="Buscar usuários..." />
      <select v-model="roleFilter">
        <option value="">Todos os perfis</option>
        <option value="ADMIN">Administradores</option>
        <option value="BUSINESS">Centros de Distribuição</option>
        <option value="COURIER">Entregadores</option>
        <option value="CUSTOMER">Clientes</option>
      </select>
    </div>
    
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>Login</th>
          <th>Email</th>
          <th>Perfil</th>
          <th>Endereço</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in filteredUsers" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.name }}</td>
          <td>{{ user.login }}</td>
          <td>{{ user.email }}</td>
          <td>
            <span class="role-tag" :class="user.role.toLowerCase()">
              {{ getRoleLabel(user.role) }}
            </span>
          </td>
          <td>{{ user.address || 'N/A' }}</td>
          <td>
            <button class="edit-btn" @click="editUser(user)">Editar</button>
            <button class="delete-btn" @click="deleteUser(user.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingUser ? 'Editar Usuário' : 'Novo Usuário' }}</h3>
        <form @submit.prevent="saveUser">
          <input v-model="form.name" type="text" placeholder="Nome completo" required />
          <input v-model="form.login" type="text" placeholder="Login/Usuário" required />
          <input v-model="form.email" type="email" placeholder="Email" required />
          <select v-model="form.role" required>
            <option value="">Selecione o perfil</option>
            <option value="ADMIN">Administrador</option>
            <option value="BUSINESS">Centro de Distribuição</option>
            <option value="COURIER">Entregador</option>
            <option value="CUSTOMER">Cliente</option>
          </select>
          <input v-model="form.address" type="text" placeholder="Endereço" />
          <input v-model="form.mobile" type="text" placeholder="WhatsApp" />
          <input 
            v-model="form.password" 
            type="password" 
            :placeholder="editingUser ? 'Nova senha (deixe vazio para manter)' : 'Senha'" 
            :required="!editingUser" 
          />
          <div class="form-actions">
            <button type="submit" :disabled="loading">
              {{ loading ? 'Salvando...' : 'Salvar' }}
            </button>
            <button type="button" @click="cancel">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, getCurrentInstance } from 'vue'
// define emit for legacy tests expecting 'back' event
const emit = defineEmits(['back'])
import { backendService as backendServiceSingleton } from '../services/backend.js'

// router será resolvido dinamicamente em goBack via getCurrentInstance / globalThis

// Allow backend service injection (tests can provide their own mock)
const backendService = inject('backendService', backendServiceSingleton)

const users = ref([])
const showForm = ref(false)
const editingUser = ref(null)
const loading = ref(false)
const error = ref(null)
const searchTerm = ref('')
const roleFilter = ref('')

const form = ref({
  name: '',
  login: '',
  email: '',
  address: '',
  mobile: '',
  role: '',
  password: ''
})

const filteredUsers = computed(() => {
  let filtered = Array.isArray(users.value) ? users.value : []
  
  if (searchTerm.value) {
    const search = searchTerm.value.toLowerCase()
    filtered = filtered.filter(user =>
      user.name.toLowerCase().includes(search) ||
      user.login.toLowerCase().includes(search) ||
      user.email.toLowerCase().includes(search)
    )
  }
  
  if (roleFilter.value) {
    filtered = filtered.filter(user => user.role === roleFilter.value)
  }
  
  return filtered
})

function getRoleLabel(role) {
  const labels = {
    'ADMIN': 'Administrador',
    'BUSINESS': 'Centro de Distribuição',
    'COURIER': 'Entregador',
    'CUSTOMER': 'Cliente'
  }
  return labels[role] || role
}

async function loadUsers() {
  try {
    loading.value = true
    error.value = null
  users.value = await backendService.getUsers()
  } catch (err) {
  // Mensagem genérica para corresponder aos testes
  error.value = 'Erro ao carregar usuários. Tente novamente.'
  console.error('Erro ao carregar usuários:', err)
  } finally {
    loading.value = false
  }
}

async function saveUser() {
  try {
    loading.value = true
    error.value = null
    
  const userData = { ...form.value }
  if (!userData.password) delete userData.password
    
    // Permitir modo edição também quando form possui id (alguns testes não setam editingUser)
    const isEditMode = !!editingUser.value || !!userData.id
    const targetId = editingUser.value?.id || userData.id

    if (isEditMode) {
      const updatedUser = await backendService.updateUser(targetId, userData)
      const index = users.value.findIndex(u => u.id === targetId)
      if (index !== -1) {
        users.value[index] = updatedUser
      }
      editingUser.value = null
    } else {
      const newUser = await backendService.createUser(userData)
      users.value.push(newUser)
    }
    
    showForm.value = false
    resetForm()
  } catch (err) {
    error.value = 'Erro ao salvar usuário: ' + err.message
    console.error('Erro ao salvar usuário:', err)
  } finally {
    loading.value = false
  }
}

function editUser(user) {
  editingUser.value = user
  form.value = {
    name: user.name,
    login: user.login,
    email: user.email,
    address: user.address || '',
    mobile: user.mobile || '',
    role: user.role,
    password: ''
  }
  showForm.value = true
}

async function deleteUser(id) {
  if (confirm('Tem certeza que deseja excluir este usuário?')) {
    try {
      loading.value = true
      error.value = null
      await backendService.deleteUser(id)
      users.value = users.value.filter(u => u.id !== id)
    } catch (err) {
      error.value = 'Erro ao excluir usuário: ' + err.message
      console.error('Erro ao excluir usuário:', err)
    } finally {
      loading.value = false
    }
  }
}

function cancel() {
  showForm.value = false
  editingUser.value = null
  resetForm()
}

function resetForm() {
  form.value = {
    name: '',
    login: '',
    email: '',
    address: '',
    mobile: '',
    role: '',
    password: ''
  }
}

function goBack() {
  try { emit('back') } catch(_) {}
  const inst = getCurrentInstance()
  const candidates = [inst?.proxy?.$router, globalThis?.$router]
  candidates.forEach(r => {
    if (r && typeof r.push === 'function') {
      try { r.push('/') } catch(e) { /* ignore */ }
    }
  })
}

onMounted(async () => {
  await loadUsers()
})
</script>

<style scoped>
.user-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 1400px;
  margin: 32px auto;
}

.loading {
  background: #e3f2fd;
  color: #1976d2;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  margin-bottom: 16px;
}

.error {
  background: #ffebee;
  color: #d32f2f;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
  border-left: 4px solid #d32f2f;
}

.actions {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.filters {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

.filters input, .filters select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background: #f5f5f5;
  font-weight: 600;
}

.role-tag {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.role-tag.admin { background: #d32f2f; }
.role-tag.business { background: #1976d2; }
.role-tag.courier { background: #388e3c; }
.role-tag.customer { background: #f57c00; }

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
}

button[type="submit"] {
  background: #1976d2;
  color: white;
}

button[type="button"] {
  background: #6c757d;
  color: white;
}

.back-btn {
  background: #6c757d;
  color: white;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.modal-content input, .modal-content select {
  width: 100%;
  padding: 8px;
  margin-bottom: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}
</style>
