<template>
  <header class="app-header">
    <h1>CSOnline</h1>
  </header>
  <Login v-if="!isLogged && !isUserMgmt && !isCourierMgmt && !isCustomerMgmt && !isLogout" @login="onLogin" />
  <MainLayout v-else-if="isLogged && !isLogout && !isUserMgmt && !isCourierMgmt && !isCustomerMgmt" @logout="onLogout" @userMgmt="onUserMgmt" @courierMgmt="onCourierMgmt" @customerMgmt="onCustomerMgmt" />
  <UserManagement v-if="isUserMgmt" @back="onBackFromUserMgmt" />
  <CourierManagement v-if="isCourierMgmt" @back="onBackFromCourierMgmt" />
  <CustomerManagement v-if="isCustomerMgmt" @back="onBackFromCustomerMgmt" />
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
import CourierManagement from './components/CourierManagement.vue';
import CustomerManagement from './components/CustomerManagement.vue';

const isLogged = ref(false);
const isLogout = ref(false);
const isUserMgmt = ref(false);
const isCourierMgmt = ref(false);
const isCustomerMgmt = ref(false);

function onLogin() {
  isLogged.value = true;
  isLogout.value = false;
  isUserMgmt.value = false;
  isCourierMgmt.value = false;
  isCustomerMgmt.value = false;
}
function onLogout() {
  isLogged.value = false;
  isLogout.value = true;
  isUserMgmt.value = false;
  isCourierMgmt.value = false;
  isCustomerMgmt.value = false;
}
function onUserMgmt() {
  isUserMgmt.value = true;
  isCourierMgmt.value = false;
  isCustomerMgmt.value = false;
}
function onCourierMgmt() {
  isCourierMgmt.value = true;
  isUserMgmt.value = false;
  isCustomerMgmt.value = false;
}
function onCustomerMgmt() {
  isCustomerMgmt.value = true;
  isUserMgmt.value = false;
  isCourierMgmt.value = false;
}
function onBackFromUserMgmt() {
  isUserMgmt.value = false;
}
function onBackFromCourierMgmt() {
  isCourierMgmt.value = false;
}
function onBackFromCustomerMgmt() {
  isCustomerMgmt.value = false;
}
function onBackToLogin() {
  isLogged.value = false;
  isLogout.value = false;
  isUserMgmt.value = false;
  isCourierMgmt.value = false;
  isCustomerMgmt.value = false;
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
