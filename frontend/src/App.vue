<template>
  <header class="app-header">
    <h1>CSOnline</h1>
  </header>
  <Login v-if="!isLogged" @login="onLogin" />
  <MainLayout v-else />
  <footer class="app-footer">
    Cara Core Informática &copy; 2025
  </footer>
</template>

<script setup>
import { ref } from 'vue';
import Login from './components/Login.vue';
import MainLayout from './components/MainLayout.vue';

const isLogged = ref(false);
function onLogin() {
  isLogged.value = true;
}
</script>

<style scoped>
.app-header {
  background: #1976d2;
  color: #fff;
  padding: 16px;
  text-align: center;
  font-size: 2rem;
  font-family: Arial, sans-serif;
}
.app-footer {
  background: #f5f5f5;
  color: #444;
  text-align: center;
  padding: 16px;
  font-size: 1rem;
  position: fixed;
  left: 0; right: 0; bottom: 0;
  font-family: Arial, sans-serif;
}
</style>
