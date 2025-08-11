<template>
  <div class="courier-management">
    <PermissionGuard :require-any="['canAccessCouriers']">
      <h2>Gestão de Entregadores</h2>
      
      <div class="role-indicator">
        <span class="role-badge" :class="authStore.userRole.toLowerCase()">
          {{ authStore.userRole }} - {{ getRoleDescription() }}
        </span>
      </div>

      <div class="actions">
        <PermissionGuard :require-any="['canCreateCouriers']" :show-denied="false">
          <button @click="showForm = true" class="btn-primary">Novo Entregador</button>
        </PermissionGuard>
        <button class="back-btn" @click="goBack">Voltar</button>
      </div>

      <div class="filters">
        <input v-model="searchTerm" type="text" placeholder="Buscar entregadores..." class="search-input" />
        <select v-model="businessFilter" class="business-filter">
          <option value="">Todas as empresas</option>
          <option v-for="business in businesses" :key="business.id" :value="business.id">
            {{ business.name }}
          </option>
        </select>
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>WhatsApp</th>
            <th>Empresa</th>
            <th>Fator de Comissão</th>
            <th v-if="authStore.canEditCouriers || authStore.canDeleteCouriers">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="courier in filteredCouriers" :key="courier.id">
            <td>{{ courier.id }}</td>
            <td>{{ courier.user.name }}</td>
            <td>{{ courier.user.email }}</td>
            <td>{{ courier.user.mobile }}</td>
            <td>{{ courier.business.name }}</td>
            <td>{{ courier.factorCourier }}%</td>
            <td v-if="authStore.canEditCouriers || authStore.canDeleteCouriers">
              <PermissionGuard :require-any="['canEditCouriers']" :show-denied="false">
                <button @click="editCourier(courier)" class="btn-edit">Editar</button>
              </PermissionGuard>
              <PermissionGuard :require-any="['canDeleteCouriers']" :show-denied="false">
                <button @click="deleteCourier(courier.id)" class="btn-delete">Excluir</button>
              </PermissionGuard>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="filteredCouriers.length === 0" class="no-data">
        <p>Nenhum entregador encontrado com os filtros aplicados.</p>
      </div>

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
            <input v-model="form.user.password" type="password" :placeholder="editingCourier ? 'Nova senha (deixe vazio para manter)' : 'Senha'" :required="!editingCourier" />
            <div class="form-actions">
              <button type="submit" class="btn-primary">Salvar</button>
              <button type="button" @click="cancel" class="btn-secondary">Cancelar</button>
            </div>
          </form>
        </div>
      </div>
    </PermissionGuard>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { backendService } from '../services/backend.js'
import { formatDataForBackend } from '../config/backend.js'
import PermissionGuard from './PermissionGuard.vue'

export default {
  name: 'CourierManagement',
  components: {
    PermissionGuard
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const couriers = ref([])
    const businesses = ref([])
    const showForm = ref(false)
    const editingCourier = ref(null)
    const searchTerm = ref('')
    const businessFilter = ref('')
    
    const form = reactive({
      user: {
        name: '',
        email: '',
        mobile: '',
        password: ''
      },
      business: {
        id: ''
      },
      factorCourier: ''
    })

    const filteredCouriers = computed(() => {
      let filtered = couriers.value

      if (searchTerm.value) {
        const search = searchTerm.value.toLowerCase()
        filtered = filtered.filter(courier =>
          courier.user.name.toLowerCase().includes(search) ||
          courier.user.email.toLowerCase().includes(search) ||
          courier.business.name.toLowerCase().includes(search)
        )
      }

      if (businessFilter.value) {
        filtered = filtered.filter(courier => courier.business.id == businessFilter.value)
      }

      return filtered
    })

    const getRoleDescription = () => {
      const descriptions = {
        ADMIN: 'Controle total do sistema',
        BUSINESS: 'Gestão de distribuição',
        COURIER: 'Entregador/Transportador',
        CUSTOMER: 'Cliente final'
      }
      return descriptions[authStore.userRole] || 'Papel indefinido'
    }

    const loadCouriers = async () => {
      try {
        console.log('[COURIER] Loading couriers from backend...')
        couriers.value = await backendService.getCouriers()
        console.log('[COURIER] Loaded couriers:', couriers.value.length)
      } catch (error) {
        console.error('[COURIER] Error loading couriers:', error.message)
        alert('Erro ao carregar entregadores: ' + error.message)
      }
    }

    const loadBusinesses = async () => {
      try {
        console.log('[COURIER] Loading businesses from backend...')
        const users = await backendService.getUsers()
        businesses.value = users.filter(user => user.role === 'BUSINESS')
        console.log('[COURIER] Loaded businesses:', businesses.value.length)
      } catch (error) {
        console.error('[COURIER] Error loading businesses:', error.message)
      }
    }

    const saveCourier = async () => {
      try {
        console.log('[COURIER] Saving courier...')
        const baseData = {
          id: editingCourier.value?.id || null,
          factorCourier: parseFloat(form.factorCourier),
          businessId: form.business.id,
          userId: editingCourier.value?.user?.id || null,
          user: !editingCourier.value ? {
            id: null,
            name: form.user.name,
            email: form.user.email,
            login: form.user.email,
            phone: form.user.mobile,
            mobile: form.user.mobile,
            role: 'COURIER',
            password: form.user.password
          } : undefined
        }
        const courierData = formatDataForBackend(baseData, 'courier')

        if (editingCourier.value) {
          await backendService.updateCourier(editingCourier.value.id, courierData)
          console.log('[COURIER] Courier updated successfully')
          alert('Entregador atualizado com sucesso!')
        } else {
          await backendService.createCourier(courierData)
          console.log('[COURIER] Courier created successfully')
          alert('Entregador criado com sucesso!')
        }
        
        await loadCouriers()
        cancel()
      } catch (error) {
        console.error('[COURIER] Error saving courier:', error.message)
        alert('Erro ao salvar entregador: ' + error.message)
      }
    }

  const editCourier = (courier) => {
      editingCourier.value = courier
      Object.assign(form, {
        user: {
          name: courier.user.name,
          email: courier.user.email,
      mobile: courier.user.mobile || courier.user.phone || '',
          password: ''
        },
        business: {
          id: courier.business.id
        },
        factorCourier: courier.factorCourier
      })
      showForm.value = true
    }

    const deleteCourier = async (courierId) => {
      if (!confirm('Tem certeza que deseja excluir este entregador?')) {
        return
      }

      try {
        console.log('[COURIER] Deleting courier:', courierId)
        await backendService.deleteCourier(courierId)
        await loadCouriers()
        console.log('[COURIER] Courier deleted successfully')
        alert('Entregador excluído com sucesso!')
      } catch (error) {
        console.error('[COURIER] Error deleting courier:', error.message)
        alert('Erro ao excluir entregador: ' + error.message)
      }
    }

    const cancel = () => {
      showForm.value = false
      editingCourier.value = null
      Object.assign(form, {
        user: {
          name: '',
          email: '',
          mobile: '',
          password: ''
        },
        business: {
          id: ''
        },
        factorCourier: ''
      })
    }

    const goBack = () => {
      router.push('/dashboard')
    }

    onMounted(() => {
      loadCouriers()
      loadBusinesses()
    })

    return {
      authStore,
      couriers,
      filteredCouriers,
      businesses,
      showForm,
      editingCourier,
      searchTerm,
      businessFilter,
      form,
      getRoleDescription,
      saveCourier,
      editCourier,
      deleteCourier,
      cancel,
      goBack
    }
  }
}
</script>

<style scoped>
.courier-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.role-indicator {
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
  border-left: 4px solid #f39c12;
}

.role-badge {
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 20px;
  color: white;
}

.role-badge.admin { background: #e74c3c; }
.role-badge.business { background: #2196F3; }
.role-badge.courier { background: #f39c12; }
.role-badge.customer { background: #27ae60; }

.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-input, .business-filter {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.business-filter {
  min-width: 180px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.btn-primary {
  background: #f39c12;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.btn-secondary {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.btn-edit {
  background: #28a745;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 5px;
}

.btn-delete {
  background: #dc3545;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.back-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #666;
  background: #f8f9fa;
  border-radius: 8px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.modal-content input,
.modal-content select {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-primary:hover { background: #e67e22; }
.btn-secondary:hover { background: #5a6268; }
.btn-edit:hover { background: #218838; }
.btn-delete:hover { background: #c82333; }
.back-btn:hover { background: #5a6268; }
</style>
