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
  drawer.value = false;
  emit('logout');
}

function userMgmt() {
  drawer.value = false;
  emit('userMgmt');
}

function courierMgmt() {
  drawer.value = false;
  emit('courierMgmt');
}

function customerMgmt() {
  drawer.value = false;
  emit('customerMgmt');
}

function deliveryMgmt() {
  drawer.value = false;
  emit('deliveryMgmt');
}

function teamMgmt() {
  drawer.value = false;
  emit('teamMgmt');
}

function smsMgmt() {
  drawer.value = false;
  emit('smsMgmt');
}

function priceMgmt() {
  drawer.value = false;
  emit('priceMgmt');
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
  justify-content: space-between;
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

.user-info {
  font-size: 14px;
  opacity: 0.9;
}

.drawer {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: absolute;
  top: 64px;
  left: 0;
  width: 250px;
  z-index: 10;
  max-height: 400px;
  overflow-y: auto;
}

.drawer ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.drawer li {
  border-bottom: 1px solid #eee;
}

.drawer li.separator {
  border-bottom: 2px solid #ddd;
  margin: 8px 0;
}

.drawer a {
  display: block;
  padding: 16px;
  color: #1976d2;
  text-decoration: none;
  transition: background-color 0.2s;
}

.drawer a:hover {
  background: #e3f2fd;
}

.drawer a.logout-link {
  color: #d32f2f;
  font-weight: bold;
}

.drawer a.logout-link:hover {
  background: #ffebee;
}

main {
  padding: 32px;
}

.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard h2 {
  color: #1976d2;
  margin-bottom: 8px;
}

.dashboard p {
  color: #666;
  margin-bottom: 32px;
}

.dashboard-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-left: 4px solid #1976d2;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.card h3 {
  color: #1976d2;
  margin: 0 0 8px 0;
  font-size: 18px;
}

.card p {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.role-info {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-left: 4px solid #4caf50;
}

.role-info h3 {
  color: #4caf50;
  margin: 0 0 16px 0;
}

.role-info ul {
  margin: 0;
  padding-left: 20px;
}

.role-info li {
  color: #666;
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .dashboard-cards {
    grid-template-columns: 1fr;
  }
  
  .drawer {
    width: 200px;
  }
  
  main {
    padding: 16px;
  }
}
</style>
