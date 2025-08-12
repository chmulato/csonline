<template>
  <header class="app-header">
    <h1>CSOnline</h1>
    <div v-if="authStore.isAuthenticated" class="user-info">
      Bem-vindo, {{ authStore.userName }} ({{ authStore.userRole }})
    </div>
    <!-- Debug info -->
    <div v-if="authStore.isAuthenticated" style="font-size: 10px; background: rgba(255,255,255,0.1); padding: 4px; border-radius: 2px;">
      Debug: SMS={{ isSMSMgmt }}, Users={{ isUserMgmt }}, Teams={{ isTeamMgmt }}
    </div>
  </header>
  
  <Login v-if="!authStore.isAuthenticated && !isUserMgmt && !isCourierMgmt && !isCustomerMgmt && !isDeliveryMgmt && !isTeamMgmt && !isSMSMgmt && !isPriceMgmt && !isLogout" @login="onLogin" />
  <MainLayout v-else-if="authStore.isAuthenticated && !isLogout && !isUserMgmt && !isCourierMgmt && !isCustomerMgmt && !isDeliveryMgmt && !isTeamMgmt && !isSMSMgmt && !isPriceMgmt" @logout="onLogout" @userMgmt="onUserMgmt" @courierMgmt="onCourierMgmt" @customerMgmt="onCustomerMgmt" @deliveryMgmt="onDeliveryMgmt" @teamMgmt="onTeamMgmt" @smsMgmt="onSMSMgmt" @priceMgmt="onPriceMgmt" />
  
  <UserManagement v-if="isUserMgmt" @back="onBackFromUserMgmt" />
  <CourierManagement v-if="isCourierMgmt" @back="onBackFromCourierMgmt" />
  <CustomerManagement v-if="isCustomerMgmt" @back="onBackFromCustomerMgmt" />
  <DeliveryManagement v-if="isDeliveryMgmt" @back="onBackFromDeliveryMgmt" />
  <TeamManagement v-if="isTeamMgmt" @back="onBackFromTeamMgmt" />
  <SMSManagement v-if="isSMSMgmt" @back="onBackFromSMSMgmt" />
  <PriceManagement v-if="isPriceMgmt" @back="onBackFromPriceMgmt" />
  <Logout v-if="isLogout" @backToLogin="onBackToLogin" />
  
  <footer class="app-footer">
    Cara Core Informática &copy; 2025
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from './stores/auth';
import Login from './components/Login.vue';
import MainLayout from './components/MainLayout.vue';
import Logout from './components/Logout.vue';
import UserManagement from './components/UserManagement.vue';
import CourierManagement from './components/CourierManagement.vue';
import CustomerManagement from './components/CustomerManagement.vue';
import DeliveryManagement from './components/DeliveryManagement.vue';
import TeamManagement from './components/TeamManagement.vue';
import SMSManagement from './components/SMSManagement.vue';
import PriceManagement from './components/PriceManagement.vue';

const authStore = useAuthStore();

const isLogout = ref(false);
const isUserMgmt = ref(false);
const isCourierMgmt = ref(false);
const isCustomerMgmt = ref(false);
const isDeliveryMgmt = ref(false);
const isTeamMgmt = ref(false);
const isSMSMgmt = ref(false);
const isPriceMgmt = ref(false);

// Check auth on app start
onMounted(() => {
  authStore.validateAuth();
});

function onLogin(userData) {
  console.log('[APP] User logged in:', userData);
  resetAllViews();
}

function onLogout() {
  authStore.clearAuth();
  isLogout.value = true;
  resetAllViews();
}

function resetAllViews() {
  console.log('[App] resetAllViews called');
  isLogout.value = false;
  isUserMgmt.value = false;
  isCourierMgmt.value = false;
  isCustomerMgmt.value = false;
  isDeliveryMgmt.value = false;
  isTeamMgmt.value = false;
  isSMSMgmt.value = false;
  isPriceMgmt.value = false;
}

function onUserMgmt() {
  console.log('[App] onUserMgmt triggered');
  resetAllViews();
  isUserMgmt.value = true;
}

function onCourierMgmt() {
  console.log('[App] onCourierMgmt triggered');
  resetAllViews();
  isCourierMgmt.value = true;
}

function onCustomerMgmt() {
  console.log('[App] onCustomerMgmt triggered');
  resetAllViews();
  isCustomerMgmt.value = true;
}

function onDeliveryMgmt() {
  console.log('[App] onDeliveryMgmt triggered');
  resetAllViews();
  isDeliveryMgmt.value = true;
}

function onTeamMgmt() {
  console.log('[App] onTeamMgmt triggered');
  resetAllViews();
  isTeamMgmt.value = true;
}

function onSMSMgmt() {
  console.log('[App] onSMSMgmt triggered - setting isSMSMgmt to true');
  resetAllViews();
  isSMSMgmt.value = true;
  console.log('[App] isSMSMgmt now set to:', isSMSMgmt.value);
}

function onPriceMgmt() {
  console.log('[App] onPriceMgmt triggered');
  resetAllViews();
  isPriceMgmt.value = true;
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

function onBackFromDeliveryMgmt() {
  isDeliveryMgmt.value = false;
}

function onBackFromTeamMgmt() {
  isTeamMgmt.value = false;
}

function onBackFromSMSMgmt() {
  isSMSMgmt.value = false;
}

function onBackFromPriceMgmt() {
  isPriceMgmt.value = false;
}

function onBackToLogin() {
  authStore.clearAuth();
  resetAllViews();
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  margin: 0;
  padding: 0;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.app-header {
  background: var(--primary-700);
  color: var(--text-white);
  padding: var(--spacing-md);
  text-align: center;
  font-size: 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-md);
}

.user-info {
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.1);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.app-footer {
  background: var(--bg-paper);
  color: var(--text-secondary);
  text-align: center;
  padding: var(--spacing-md);
  font-size: 0.9rem;
  position: fixed;
  left: 0; 
  right: 0; 
  bottom: 0;
  border-top: 1px solid var(--divider);
}
</style>
