<template>
  <div class="logout-container">
    <h2>Você saiu do CSOnline</h2>
    <p>Obrigado por utilizar o sistema!</p>
    <button @click="goToLogin">Voltar ao Login</button>
    <div v-if="showConfirm" class="confirm-msg">Redirecionando para o login...</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const emit = defineEmits(['backToLogin']);
const showConfirm = ref(false);
function goToLogin() {
  showConfirm.value = true;
  setTimeout(() => {
    emit('backToLogin');
    showConfirm.value = false;
  }, 600);
}
</script>

<style scoped>
.logout-container {
  max-width: 400px;
  margin: 80px auto;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background: #fff;
  text-align: center;
}
.logout-container h2 {
  margin-bottom: 16px;
}
.logout-container button {
  margin-top: 24px;
  padding: 10px 24px;
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}
.logout-container button:hover {
  background: #1565c0;
}
 .confirm-msg {
   margin-top: 18px;
   color: #388e3c;
   font-weight: bold;
   animation: fadeIn 0.5s;
 }
 @keyframes fadeIn {
   from { opacity: 0; }
   to { opacity: 1; }
 }
</style>
