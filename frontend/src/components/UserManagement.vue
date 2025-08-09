<template>
  <div class="user-management">
    <PermissionGuard :require-any="['canAccessUsers']">
      <h2>Gestão de Usuários</h2>
      
      <div class="role-indicator">
        <span class="role-badge" :class="authStore.userRole.toLowerCase()">
          {{ authStore.userRole }} - {{ getRoleDescription() }}
        </span>
      </div>

      <div class="actions">
        <PermissionGuard :require-any="['canCreateUsers']" :show-denied="false">
          <button @click="showForm = true" class="btn-primary">Novo Usuário</button>
        </PermissionGuard>
        <button class="back-btn" @click="goBack">Voltar</button>
      </div>

      <div class="filters">
        <input v-model="searchTerm" type="text" placeholder="Buscar usuários..." class="search-input" />
        <select v-model="roleFilter" class="role-filter">
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
            <th v-if="authStore.canEditUsers || authStore.canDeleteUsers">Ações</th>
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
            <td>{{ user.address || 'Não informado' }}</td>
            <td v-if="authStore.canEditUsers || authStore.canDeleteUsers">
              <PermissionGuard :require-any="['canEditUsers']" :show-denied="false">
                <button @click="editUser(user)" class="btn-edit">Editar</button>
              </PermissionGuard>
              <PermissionGuard :require-any="['canDeleteUsers']" :show-denied="false">
                <button @click="deleteUser(user.id)" class="btn-delete">Excluir</button>
              </PermissionGuard>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="filteredUsers.length === 0" class="no-data">
        <p>Nenhum usuário encontrado com os filtros aplicados.</p>
      </div>

      <div v-if="showForm" class="modal">
        <div class="modal-content">
          <h3>{{ editingUser ? 'Editar Usuário' : 'Novo Usuário' }}</h3>
          <form @submit.prevent="saveUser">
            <input v-model="form.name" type="text" placeholder="Nome completo" required />
            <input v-model="form.login" type="text" placeholder="Login" required />
            <input v-model="form.email" type="email" placeholder="Email" required />
            <input v-model="form.address" type="text" placeholder="Endereço" />
            <input v-model="form.mobile" type="tel" placeholder="Telefone" />
            
            <select v-model="form.role" required>
              <option value="">Selecione o perfil</option>
              <PermissionGuard :require-any="['isAdmin']" :show-denied="false">
                <option value="ADMIN">Administrador</option>
              </PermissionGuard>
              <option value="BUSINESS">Centro de Distribuição</option>
              <option value="COURIER">Entregador</option>
              <option value="CUSTOMER">Cliente</option>
            </select>
            
            <input v-model="form.password" type="password" :placeholder="editingUser ? 'Nova senha (deixe vazio para manter)' : 'Senha'" :required="!editingUser" />
            
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

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth.js';
import { backendService } from '../services/backend.js';
import PermissionGuard from './PermissionGuard.vue';

const router = useRouter();
const authStore = useAuthStore();

// Estado reativo
const users = ref([]);
const showForm = ref(false);
const editingUser = ref(null);
const searchTerm = ref('');
const roleFilter = ref('');
const loading = ref(false);
const error = ref(null);

// Formulário
const form = ref({
  name: '',
  login: '',
  email: '',
  address: '',
  mobile: '',
  role: '',
  password: ''
});

    const filteredUsers = computed(() => {
      let filtered = users.value

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

    const getRoleDescription = () => {
      const descriptions = {
        ADMIN: 'Controle total do sistema',
        BUSINESS: 'Gestão de distribuição',
        COURIER: 'Entregador/Transportador',
        CUSTOMER: 'Cliente final'
      }
      return descriptions[authStore.userRole] || 'Papel indefinido'
    }

    const getRoleLabel = (role) => {
      const labels = {
        ADMIN: 'Administrador',
        BUSINESS: 'Centro de Distribuição',
        COURIER: 'Entregador',
        CUSTOMER: 'Cliente'
      }
      return labels[role] || role
    }

    const loadUsers = async () => {
      try {
        const response = await fetch('http://localhost:8080/csonline/api/users', {
          headers: {
            'Authorization': `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.ok) {
          users.value = await response.json()
        } else {
          console.error('Erro ao carregar usuários:', response.statusText)
        }
      } catch (error) {
        console.error('Erro na requisição:', error)
      }
    }

    const saveUser = async () => {
      try {
        const url = editingUser.value 
          ? `http://localhost:8080/csonline/api/users/${editingUser.value.id}`
          : 'http://localhost:8080/csonline/api/users'
        
        const method = editingUser.value ? 'PUT' : 'POST'
        
        // Se estamos editando e a senha está vazia, não enviar password
        const userData = { ...form }
        if (editingUser.value && !userData.password) {
          delete userData.password
        }
        
        const response = await fetch(url, {
          method,
          headers: {
            'Authorization': `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(userData)
        })

        if (response.ok) {
          await loadUsers()
          cancel()
          console.log(editingUser.value ? 'Usuário atualizado com sucesso!' : 'Usuário criado com sucesso!')
        } else {
          console.error('Erro ao salvar usuário:', response.statusText)
        }
      } catch (error) {
        console.error('Erro na requisição:', error)
      }
    }

    const editUser = (user) => {
      editingUser.value = user
      Object.assign(form, {
        name: user.name,
        login: user.login,
        email: user.email,
        address: user.address || '',
        mobile: user.mobile || '',
        role: user.role,
        password: ''
      })
      showForm.value = true
    }

    const deleteUser = async (userId) => {
      if (!confirm('Tem certeza que deseja excluir este usuário?')) {
        return
      }

      try {
        const response = await fetch(`http://localhost:8080/csonline/api/users/${userId}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          }
        })

        if (response.ok) {
          await loadUsers()
          console.log('Usuário excluído com sucesso!')
        } else {
          console.error('Erro ao excluir usuário:', response.statusText)
        }
      } catch (error) {
        console.error('Erro na requisição:', error)
      }
    }

    const cancel = () => {
      showForm.value = false
      editingUser.value = null
      Object.assign(form, {
        name: '',
        login: '',
        email: '',
        address: '',
        mobile: '',
        role: '',
        password: ''
      })
    }

    const goBack = () => {
      router.push('/dashboard')
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      authStore,
      users,
      filteredUsers,
      showForm,
      editingUser,
      searchTerm,
      roleFilter,
      form,
      getRoleDescription,
      getRoleLabel,
      saveUser,
      editUser,
      deleteUser,
      cancel,
      goBack
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.role-indicator {
  margin-bottom: 20px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
  border-left: 4px solid #2196F3;
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

.search-input, .role-filter {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.role-filter {
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

.role-tag {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.role-tag.admin { background: #e74c3c; }
.role-tag.business { background: #2196F3; }
.role-tag.courier { background: #f39c12; }
.role-tag.customer { background: #27ae60; }

.btn-primary {
  background: #2196F3;
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

.btn-primary:hover { background: #1976d2; }
.btn-secondary:hover { background: #5a6268; }
.btn-edit:hover { background: #218838; }
.btn-delete:hover { background: #c82333; }
.back-btn:hover { background: #5a6268; }
</style>
