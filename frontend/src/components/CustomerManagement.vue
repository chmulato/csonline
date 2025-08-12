<template>
  <div class="customer-management">
    <div class="page-header">
      <h2 class="h2">Gestão de Empresas (Centro de Distribuições)</h2>
    </div>
    
    <div class="actions">
      <button @click="openNewCustomerModal" class="btn btn-primary">Nova Empresa</button>
      <button class="btn btn-secondary" @click="goBack">Voltar</button>
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
            <div class="action-buttons">
              <button class="btn-primary" @click="editCustomer(customer)">Editar</button>
              <button class="btn-danger" @click="deleteCustomer(customer.id)">Excluir</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

  <div v-if="showModal" class="modal">
      <div class="modal-content">
        <h3>{{ editingCustomer ? 'Editar Empresa' : 'Nova Empresa' }}</h3>
        <form @submit.prevent="saveCustomer">
      <input v-model="form.user.name" type="text" placeholder="Nome da Empresa" required />
      <input v-model="form.user.email" type="email" placeholder="Email" required />
      <input v-model="form.user.phone" type="tel" placeholder="WhatsApp (ex: 41999887766)" required />
      <input v-model="form.user.address" type="text" placeholder="Endereço Completo" required />
      <input v-model="form.factorCustomer" type="number" step="0.01" min="0" max="100" placeholder="Fator de Comissão (%)" required />
      <input v-model="form.priceTable" type="text" placeholder="Tabela de Preço (ex: STANDARD, PREMIUM)" required />
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
import { useAuthStore } from '../stores/auth.js'
import { backendService } from '../services/backend.js'

const authStore = useAuthStore()

const emit = defineEmits(['back'])

// State
const customers = ref([])
const businesses = ref([])
const showModal = ref(false)
const editingCustomer = ref(null)
const isLoading = ref(false)
const error = ref(null)

// Form data
const form = ref({
  user: {
    name: '',
    email: '',
    phone: '',
    address: ''
  },
  factorCustomer: '',
  priceTable: '',
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
    
    const rawData = {
      id: editingCustomer.value?.id || null,
      factorCustomer: parseFloat(form.value.factorCustomer),
      priceTable: form.value.priceTable || null,
      user: {
        id: editingCustomer.value?.user?.id || null,
        name: form.value.user.name,
        email: form.value.user.email,
        phone: form.value.user.phone,
        address: form.value.user.address,
        role: 'CUSTOMER'
      }
    }
    const customerData = formatDataForBackend(rawData, 'customer')

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
      phone: customer.user?.phone || customer.user?.mobile || '',
      address: customer.user?.address || ''
    },
    factorCustomer: customer.factorCustomer || '',
    priceTable: customer.priceTable || '',
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
    priceTable: '',
    businessId: ''
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingCustomer.value = null
}

function goBack() {
  console.log('[CUSTOMER] Emitting back event')
  emit('back')
}

// Load data on mount
onMounted(() => {
  loadCustomers()
  loadBusinesses()
})

import { formatDataForBackend } from '../config/backend.js'
</script>

<style scoped>
.customer-management {
  padding: var(--spacing-lg);
  background: var(--bg-secondary);
  min-height: 100vh;
}

.page-header {
  margin-bottom: var(--spacing-xl);
}

.actions {
  margin: var(--spacing-lg) 0;
  display: flex;
  gap: var(--spacing-md);
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: var(--spacing-lg);
  background: var(--bg-paper);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

th, td {
  border: 1px solid var(--border-light);
  padding: var(--spacing-md);
  text-align: left;
  font-size: var(--font-size-sm);
}

th {
  background-color: var(--gray-100);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-start;
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
  background: var(--bg-paper);
  padding: var(--spacing-2xl);
  border-radius: var(--radius-lg);
  min-width: 500px;
  box-shadow: var(--shadow-lg);
}

.modal-content h3 {
  margin-bottom: var(--spacing-lg);
}

.form-actions {
  margin-top: var(--spacing-md);
  display: flex;
  gap: var(--spacing-sm);
}

select, input[type="text"], input[type="email"], input[type="tel"], input[type="password"], input[type="number"] {
  width: 100%;
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-light);
  font-family: var(--font-primary);
  font-size: var(--font-size-sm);
  transition: border-color var(--transition-fast);
}

select:focus, input:focus {
  outline: none;
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px var(--primary-50);
}
</style>
