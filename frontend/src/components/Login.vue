<template>
  <div class="login-container">
    <h2>CSOnline - Login</h2>
    <form @submit.prevent="login">
      <input 
        v-model="username" 
        type="text" 
        placeholder="Usuário" 
        required 
        :disabled="loading"
      />
      <input 
        v-model="password" 
        type="password" 
        placeholder="Senha" 
        required 
        :disabled="loading"
      />
      <button type="submit" :disabled="loading">
        {{ loading ? 'Entrando...' : 'Entrar' }}
      </button>
    </form>
    
    <!-- Mensagens de erro/sucesso -->
    <div v-if="message" :class="messageType">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';

const username = ref('');
const password = ref('');
const loading = ref(false);
const message = ref('');
const messageType = ref('');

const authStore = useAuthStore();
const emit = defineEmits(['login']);

function devLog(...args) {
  if (import.meta.env.MODE === 'development') {
    console.log('[DEV]', ...args);
  }
}

async function login() {
  if (!username.value || !password.value) {
    showMessage('Por favor, preencha todos os campos', 'error');
    return;
  }

  loading.value = true;
  message.value = '';

  try {
    devLog('Tentativa de login', { username: username.value });
    
    const response = await fetch('/csonline/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        login: username.value,
        password: password.value
      })
    });

    if (response.ok) {
      const data = await response.json();
      devLog('Login bem-sucedido:', data);
      
      // Armazenar token e dados do usuário
      authStore.setAuth(data);
      
      showMessage(`Bem-vindo, ${data.name}!`, 'success');
      
      // Emit login event
      emit('login', data);
      
    } else {
      const errorData = await response.json().catch(() => ({ error: 'Erro de autenticação' }));
      devLog('Erro de login:', errorData);
      showMessage(errorData.error || 'Usuário ou senha inválidos', 'error');
    }
    
  } catch (error) {
    devLog('Erro na requisição:', error);
    showMessage('Erro de conexão com o servidor', 'error');
  } finally {
    loading.value = false;
  }
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
  
  // Limpar mensagem após 5 segundos
  setTimeout(() => {
    message.value = '';
    messageType.value = '';
  }, 5000);
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
  color: #1976d2;
}
.login-container input {
  display: block;
  width: 100%;
  margin-bottom: 16px;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 14px;
}
.login-container input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.login-container button {
  width: 100%;
  padding: 12px;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}
.login-container button:hover:not(:disabled) {
  background: #1565c0;
}
.login-container button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.success {
  margin-top: 16px;
  padding: 12px;
  background: #e8f5e8;
  color: #2e7d32;
  border-radius: 4px;
  border: 1px solid #c8e6c9;
}

.error {
  margin-top: 16px;
  padding: 12px;
  background: #ffeaea;
  color: #c62828;
  border-radius: 4px;
  border: 1px solid #ffcdd2;
}
</style>
