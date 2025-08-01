<template>
  <div class="login-container">
    <h2>CSOnline - Login</h2>
    <form @submit.prevent="login">
      <input v-model="username" type="text" placeholder="Usuário" required />
      <input v-model="password" type="password" placeholder="Senha" required />
      <button type="submit">Entrar</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const username = ref('');
const password = ref('');
const emit = defineEmits(['login']);

function devLog(...args) {
  if (import.meta.env.MODE === 'development') {
    console.log('[DEV]', ...args);
  }
}

function login() {
  devLog('Tentativa de login', { username: username.value });
  // Aqui você pode adicionar chamada à API de autenticação
  emit('login');
}
</script>

<style scoped>
.login-container {
  max-width: 320px;
  margin: 80px auto;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background: #fff;
  text-align: center;
}
.login-container h2 {
  margin-bottom: 24px;
}
.login-container input {
  display: block;
  width: 100%;
  margin-bottom: 16px;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
.login-container button {
  width: 100%;
  padding: 10px;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}
.login-container button:hover {
  background: #1565c0;
}
</style>
