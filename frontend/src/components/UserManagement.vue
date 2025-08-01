<template>
  <div class="user-management">
    <h2>Gestão de Usuários</h2>
    <div class="actions">
      <button @click="showForm = true">Novo Usuário</button>
      <button class="back-btn" @click="goBack">Voltar</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>Nome</th>
          <th>Email</th>
          <th>Perfil</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.name }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role }}</td>
          <td>
            <button @click="editUser(user)">Editar</button>
            <button @click="deleteUser(user.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showForm" class="modal">
      <div class="modal-content">
        <h3>{{ editingUser ? 'Editar Usuário' : 'Novo Usuário' }}</h3>
        <form @submit.prevent="saveUser">
          <input v-model="form.name" type="text" placeholder="Nome" required />
          <input v-model="form.email" type="email" placeholder="Email" required />
          <select v-model="form.role" required>
            <option value="admin">Administrador</option>
            <option value="user">Usuário</option>
            <option value="courier">Entregador</option>
            <option value="customer">Cliente</option>
          </select>
          <input v-model="form.password" type="password" placeholder="Senha" required />
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
const users = ref([
  { id: 1, name: 'Admin', email: 'admin@csonline.com', role: 'admin' },
  { id: 2, name: 'João Courier', email: 'courier@csonline.com', role: 'courier' },
  { id: 3, name: 'Maria Cliente', email: 'customer@csonline.com', role: 'customer' },
  { id: 4, name: 'Gestor', email: 'user@csonline.com', role: 'user' }
]);
const showForm = ref(false);
const editingUser = ref(null);
const form = ref({ name: '', email: '', role: 'user', password: '' });

function editUser(user) {
  editingUser.value = user;
  form.value = { ...user, password: '' };
  showForm.value = true;
}
function deleteUser(id) {
  users.value = users.value.filter(u => u.id !== id);
}
function saveUser() {
  if (editingUser.value) {
    Object.assign(editingUser.value, form.value);
    editingUser.value = null;
  } else {
    users.value.push({ ...form.value, id: Date.now() });
  }
  showForm.value = false;
  form.value = { name: '', email: '', role: 'user', password: '' };
}
function cancel() {
  showForm.value = false;
  editingUser.value = null;
  form.value = { name: '', email: '', role: 'user', password: '' };
}
</script>

<style scoped>
.user-management {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  max-width: 800px;
  margin: 32px auto;
}
.user-management h2 {
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
  min-width: 320px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.form-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}
select, input[type="text"], input[type="email"], input[type="password"] {
  width: 100%;
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>
