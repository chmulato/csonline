<template>
  <div class="main-layout">
    <header>
      <button class="menu-btn" @click="drawer = !drawer">☰</button>
      <h1>CSOnline - Gestão CD</h1>
      <div class="user-info">
        {{ authStore.userName }} ({{ authStore.userRole }})
      </div>
    </header>
    <nav v-if="drawer" class="drawer">
      <ul>
        <li v-if="authStore.canAccessDeliveries">
          <a href="#" @click.prevent="deliveryMgmt">Entregas</a>
        </li>
        <li v-if="authStore.canAccessCustomers">
          <a href="#" @click.prevent="customerMgmt">Centros de Distribuição</a>
        </li>
        <li v-if="authStore.canAccessCouriers">
          <a href="#" @click.prevent="courierMgmt">Entregadores</a>
        </li>
        <li v-if="authStore.canAccessTeams">
          <a href="#" @click.prevent="teamMgmt">Times</a>
        </li>
        <li v-if="authStore.canAccessUsers">
          <a href="#" @click.prevent="userMgmt">Usuários</a>
        </li>
        <li v-if="authStore.canAccessPrices">
          <a href="#" @click.prevent="priceMgmt">Preços</a>
        </li>
        <li v-if="authStore.canAccessSMS">
          <a href="#" @click.prevent="smsMgmt">SMS/WhatsApp</a>
        </li>
        <li class="separator"></li>
        <li>
          <a href="#" @click.prevent="logout" class="logout-link">Sair</a>
        </li>
      </ul>
    </nav>
    <main>
      <div class="dashboard">
        <h2>Bem-vindo ao CSOnline!</h2>
        <p>Sistema de Gestão de Centros de Distribuição</p>
        
        <div class="dashboard-cards">
          <div v-if="authStore.canAccessDeliveries" class="card" @click="deliveryMgmt">
            <h3>Entregas</h3>
            <p>Gerenciar entregas e status</p>
          </div>
          
          <div v-if="authStore.canAccessCustomers" class="card" @click="customerMgmt">
            <h3>Centros de Distribuição</h3>
            <p>Cadastro de empresas/CDs</p>
          </div>
          
          <div v-if="authStore.canAccessCouriers" class="card" @click="courierMgmt">
            <h3>Entregadores</h3>
            <p>Gestão de couriers</p>
          </div>
          
          <div v-if="authStore.canAccessUsers && authStore.isAdmin" class="card" @click="userMgmt">
            <h3>Usuários</h3>
            <p>Administração do sistema</p>
          </div>
          
          <div v-if="authStore.canAccessTeams" class="card" @click="teamMgmt">
            <h3>Equipes</h3>
            <p>Organização de times</p>
          </div>
          
          <div v-if="authStore.canAccessPrices" class="card" @click="priceMgmt">
            <h3>Preços</h3>
            <p>Tabelas de valores</p>
          </div>
          
          <div v-if="authStore.canAccessSMS" class="card" @click="smsMgmt">
            <h3>SMS/WhatsApp</h3>
            <p>Mensagens para entregas</p>
          </div>
        </div>
        
        <div v-if="authStore.userRole" class="role-info">
          <h3>Suas Permissões ({{ authStore.userRole }}):</h3>
          <ul>
            <li v-if="authStore.isAdmin">Administrador - Acesso total ao sistema</li>
            <li v-if="authStore.isBusiness">Centro de Distribuição - Gestão operacional</li>
            <li v-if="authStore.isCourier">Entregador - Visualização e atualização de entregas</li>
            <li v-if="authStore.isCustomer">Cliente - Consulta de entregas</li>
          </ul>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';

const drawer = ref(false);
const authStore = useAuthStore();

const emit = defineEmits(['logout', 'userMgmt', 'courierMgmt', 'customerMgmt', 'deliveryMgmt', 'teamMgmt', 'smsMgmt', 'priceMgmt']);

function logout() {
  console.log('[MainLayout] Logout clicked');
  drawer.value = false;
  emit('logout');
}

function userMgmt() {
  console.log('[MainLayout] User Management clicked');
  drawer.value = false;
  emit('userMgmt');
}

function courierMgmt() {
  console.log('[MainLayout] Courier Management clicked');
  drawer.value = false;
  emit('courierMgmt');
}

function customerMgmt() {
  console.log('[MainLayout] Customer Management clicked');
  drawer.value = false;
  emit('customerMgmt');
}

function deliveryMgmt() {
  console.log('[MainLayout] Delivery Management clicked');
  drawer.value = false;
  emit('deliveryMgmt');
}

function teamMgmt() {
  console.log('[MainLayout] Team Management clicked');
  drawer.value = false;
  emit('teamMgmt');
}

function smsMgmt() {
  console.log('[MainLayout] SMS Management clicked - emitting smsMgmt event');
  drawer.value = false;
  emit('smsMgmt');
}

function priceMgmt() {
  console.log('[MainLayout] Price Management clicked');
  drawer.value = false;
  emit('priceMgmt');
}
</script>

<style scoped>
.main-layout {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: var(--bg-secondary);
  min-height: 100vh;
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--primary-700);
  color: var(--text-white);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-md);
}

.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: var(--text-white);
  margin-right: var(--spacing-md);
  cursor: pointer;
  padding: var(--spacing-sm);
  border-radius: var(--radius-sm);
  transition: background-color var(--transition-fast);
}

.menu-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-info {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
}

.drawer {
  background: var(--bg-paper);
  box-shadow: var(--shadow-lg);
  position: absolute;
  top: 64px;
  left: 0;
  width: 280px;
  z-index: 10;
  max-height: 500px;
  overflow-y: auto;
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
}

.drawer ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.drawer li {
  border-bottom: 1px solid var(--divider);
}

.drawer li.separator {
  border-bottom: 2px solid var(--border-medium);
  margin: var(--spacing-sm) 0;
}

.drawer a {
  display: block;
  padding: var(--spacing-md);
  color: var(--primary-700);
  text-decoration: none;
  font-weight: 500;
  transition: background-color var(--transition-fast);
}

.drawer a:hover {
  background: var(--primary-50);
}

.drawer a.logout-link {
  color: var(--error-500);
  font-weight: 600;
}

.drawer a.logout-link:hover {
  background: var(--error-50);
}

main {
  padding: var(--spacing-xl);
}

.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard h2 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  font-size: 28px;
  font-weight: 600;
}

.dashboard p {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
  font-size: 16px;
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

.card {
  background: var(--bg-paper);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  border-left: 4px solid var(--primary-500);
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.card h3 {
  color: var(--primary-700);
  margin: 0 0 var(--spacing-sm) 0;
  font-size: 18px;
  font-weight: 600;
}

.card p {
  color: var(--text-secondary);
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

.role-info {
  background: var(--bg-paper);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  border-left: 4px solid var(--success-500);
}

.role-info h3 {
  color: var(--success-700);
  margin: 0 0 var(--spacing-md) 0;
  font-size: 18px;
  font-weight: 600;
}

.role-info ul {
  margin: 0;
  padding-left: 20px;
}

.role-info li {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.5;
}

@media (max-width: 768px) {
  .dashboard-cards {
    grid-template-columns: 1fr;
  }
  
  .drawer {
    width: 240px;
  }
  
  main {
    padding: var(--spacing-md);
  }
}
</style>
