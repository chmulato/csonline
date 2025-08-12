<template>
  <div class="login-container">
    <h2 class="h2">CSOnline - Login</h2>
    <form @submit.prevent="login">
      <input 
        v-model="username" 
        type="text" 
        placeholder="Usuário" 
        required 
        :disabled="loading"
        class="input-field"
      />
      <input 
        v-model="password" 
        type="password" 
        placeholder="Senha" 
        required 
        :disabled="loading"
        class="input-field"
      />
      <button type="submit" :disabled="loading" class="btn btn-primary btn-lg">
        {{ loading ? 'Entrando...' : 'Entrar' }}
      </button>
    </form>
    
    <!-- Mensagens de erro/sucesso -->
    <div v-if="message" :class="messageType" class="message">
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
  max-width: 400px;
  margin: 80px auto;
  padding: var(--spacing-2xl);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  background: var(--bg-paper);
  text-align: center;
}

.input-field {
  display: block;
  width: 100%;
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-light);
  font-family: var(--font-primary);
  font-size: var(--font-size-sm);
  transition: border-color var(--transition-fast);
}

.input-field:focus {
  outline: none;
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px var(--primary-50);
}

.input-field:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: var(--bg-disabled);
}

.message {
  margin-top: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.success {
  background: var(--success-50);
  color: var(--success-700);
  border: 1px solid var(--success-500);
}

.error {
  background: var(--error-50);
  color: var(--error-700);
  border: 1px solid var(--error-500);
}
</style>
