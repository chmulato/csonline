<template>
  <div class="customer-management">
    <h2>Gestão de Empresas (Centro de Distribuições)</h2>
    <div class="actions">
      <button @click="showForm = true">Nova Empresa</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>Nome</th>
          <th>Email</th>
          <th>WhatsApp</th>
          <th>Endereço</th>
          <th>Fator (%)</th>
          <th>Tabela de Preço</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="customer in customers" :key="customer.id">
          <td>{{ customer.user.name }}</td>
          <td>{{ customer.user.email }}</td>
          <td>{{ customer.user.mobile }}</td>
          <td>{{ customer.user.address }}</td>
          <td>{{ customer.factorCustomer }}%</td>
          <td>{{ customer.priceTable }}</td>
          <td>
            <button @click="editCustomer(customer)">Editar</button>
            <button @click="deleteCustomer(customer.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingCustomer ? 'Editar Empresa' : 'Nova Empresa' }}</h3>
        <form @submit.prevent="saveCustomer">
          <input v-model="form.user.name" type="text" placeholder="Nome da Empresa" required />
          <input v-model="form.user.email" type="email" placeholder="Email" required />
          <input v-model="form.user.mobile" type="tel" placeholder="WhatsApp (ex: 41999887766)" required />
          <input v-model="form.user.address" type="text" placeholder="Endereço Completo" required />
          <select v-model="form.business.id" required>
            <option value="">Selecione a Empresa Controladora</option>
            <option v-for="business in businesses" :key="business.id" :value="business.id">
              {{ business.name }}
            </option>
          </select>
          <input v-model="form.factorCustomer" type="number" step="0.01" min="0" max="100" placeholder="Fator de Comissão (%)" required />
          <input v-model="form.priceTable" type="text" placeholder="Tabela de Preço (ex: STANDARD, PREMIUM)" required />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { backendService } from '../services/backend.js'

const router = useRouter()
const authStore = useAuthStore()

const emit = defineEmits(['back'])

// State
const customers = ref([])
const businesses = ref([])
const showModal = ref(false)
const editingCustomer = ref(null)
const isLoading = ref(false)

// Form data
const form = ref({
  user: {
    name: '',
    email: '',
    phone: '',
    address: ''
  },
  factorCustomer: '',
  businessId: ''
})

// Load data from backend
const loadCustomers = async () => {
  try {
    console.log('[CUSTOMER] Loading customers from backend...')
    isLoading.value = true
    customers.value = await backendService.getCustomers()
    console.log('[CUSTOMER] Loaded customers:', customers.value.length)
  } catch (error) {
    console.error('[CUSTOMER] Error loading customers:', error.message)
    alert('Erro ao carregar clientes: ' + error.message)
  } finally {
    isLoading.value = false
  }
}

const loadBusinesses = async () => {
  try {
    console.log('[CUSTOMER] Loading businesses from backend...')
    const users = await backendService.getUsers()
    businesses.value = users.filter(user => user.role === 'BUSINESS')
    console.log('[CUSTOMER] Loaded businesses:', businesses.value.length)
  } catch (error) {
    console.error('[CUSTOMER] Error loading businesses:', error.message)
  }
}

// CRUD operations
const saveCustomer = async () => {
  try {
    console.log('[CUSTOMER] Saving customer...')
    
    const customerData = {
      factorCustomer: parseFloat(form.value.factorCustomer),
      user: {
        name: form.value.user.name,
        email: form.value.user.email,
        phone: form.value.user.phone,
        address: form.value.user.address,
        role: 'CUSTOMER'
      }
    }

    if (editingCustomer.value) {
      await backendService.updateCustomer(editingCustomer.value.id, customerData)
      console.log('[CUSTOMER] Customer updated successfully')
      alert('Cliente atualizado com sucesso!')
    } else {
      await backendService.createCustomer(customerData)
      console.log('[CUSTOMER] Customer created successfully')
      alert('Cliente criado com sucesso!')
    }
    
    await loadCustomers()
    closeModal()
    
  } catch (error) {
    console.error('[CUSTOMER] Error saving customer:', error.message)
    alert('Erro ao salvar cliente: ' + error.message)
  }
}

const editCustomer = (customer) => {
  editingCustomer.value = customer
  form.value = {
    user: {
      name: customer.user?.name || '',
      email: customer.user?.email || '',
      phone: customer.user?.phone || '',
      address: customer.user?.address || ''
    },
    factorCustomer: customer.factorCustomer || '',
    businessId: customer.businessId || ''
  }
  showModal.value = true
}

const deleteCustomer = async (customerId) => {
  if (!confirm('Tem certeza que deseja excluir este cliente?')) {
    return
  }

  try {
    console.log('[CUSTOMER] Deleting customer:', customerId)
    await backendService.deleteCustomer(customerId)
    await loadCustomers()
    console.log('[CUSTOMER] Customer deleted successfully')
    alert('Cliente excluído com sucesso!')
  } catch (error) {
    console.error('[CUSTOMER] Error deleting customer:', error.message)
    alert('Erro ao excluir cliente: ' + error.message)
  }
}

const openNewCustomerModal = () => {
  editingCustomer.value = null
  form.value = {
    user: {
      name: '',
      email: '',
      phone: '',
      address: ''
    },
    factorCustomer: '',
    businessId: ''
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingCustomer.value = null
}

function goBack() {
  router.push('/dashboard')
}

// Load data on mount
onMounted(() => {
  loadCustomers()
  loadBusinesses()
})
</script>

<style scoped>
.customer-management {
  padding: 20px;
}

.actions {
  margin: 20px 0;
  display: flex;
  gap: 12px;
}

.btn-primary {
  background: #1976d2;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn {
  background: #424242;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

th {
  background-color: #f5f5f5;
  font-weight: bold;
}

.modal-overlay {
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
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  min-width: 500px;
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
