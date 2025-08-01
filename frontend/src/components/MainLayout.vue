<template>
  <div class="main-layout">
    <header>
      <button class="menu-btn" @click="drawer = !drawer">☰</button>
      <h1>CSOnline</h1>
    </header>
    <nav v-if="drawer" class="drawer">
      <ul>
        <li><a href="#">Entregas</a></li>
        <li><a href="#" @click.prevent="customerMgmt">Empresas</a></li>
        <li><a href="#" @click.prevent="courierMgmt">Entregadores</a></li>
        <li><a href="#" @click.prevent="userMgmt">Usuários</a></li>
        <li><a href="#">Preços</a></li>
        <li><a href="#">SMS</a></li>
        <li><a href="#" @click.prevent="logout">Sair</a></li>
      </ul>
    </nav>
    <main>
      <h2>Bem-vindo ao CSOnline!</h2>
      <p>Selecione uma funcionalidade no menu.</p>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const drawer = ref(false);
const emit = defineEmits(['logout', 'userMgmt', 'courierMgmt', 'customerMgmt']);
function logout() {
  emit('logout');
}
function userMgmt() {
  emit('userMgmt');
}
function courierMgmt() {
  emit('courierMgmt');
}
function customerMgmt() {
  emit('customerMgmt');
}
</script>

<style scoped>
.main-layout {
  font-family: Arial, sans-serif;
  background: #f5f5f5;
  min-height: 100vh;
}
header {
  display: flex;
  align-items: center;
  background: #1976d2;
  color: #fff;
  padding: 16px;
}
.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: #fff;
  margin-right: 16px;
  cursor: pointer;
}
.drawer {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: absolute;
  top: 64px;
  left: 0;
  width: 220px;
  z-index: 10;
}
.drawer ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.drawer li {
  border-bottom: 1px solid #eee;
}
.drawer a {
  display: block;
  padding: 16px;
  color: #1976d2;
  text-decoration: none;
}
.drawer a:hover {
  background: #e3f2fd;
}
main {
  padding: 32px;
}
</style>
