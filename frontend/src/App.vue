<template>
  <header class="app-header">
    <h1>CSOnline</h1>
  </header>
  <Login v-if="!isLogged && !isUserMgmt && !isLogout" @login="onLogin" />
  <MainLayout v-else-if="isLogged && !isLogout && !isUserMgmt" @logout="onLogout" @userMgmt="onUserMgmt" />
  <UserManagement v-if="isUserMgmt" @back="onBackFromUserMgmt" />
  <Logout v-if="isLogout" @backToLogin="onBackToLogin" />
  <footer class="app-footer">
    Cara Core Informática &copy; 2025
  </footer>
</template>

<script setup>
import { ref } from 'vue';
import Login from './components/Login.vue';
import MainLayout from './components/MainLayout.vue';
import Logout from './components/Logout.vue';
import UserManagement from './components/UserManagement.vue';

const isLogged = ref(false);
const isLogout = ref(false);
const isUserMgmt = ref(false);

function onLogin() {
  isLogged.value = true;
  isLogout.value = false;
  isUserMgmt.value = false;
}
function onLogout() {
  isLogged.value = false;
  isLogout.value = true;
  isUserMgmt.value = false;
}
function onUserMgmt() {
  isUserMgmt.value = true;
}
function onBackFromUserMgmt() {
  isUserMgmt.value = false;
}
function onBackToLogin() {
  isLogged.value = false;
  isLogout.value = false;
  isUserMgmt.value = false;
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
