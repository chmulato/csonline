<template>
  <div>
    <div v-if="hasPermission">
      <slot />
    </div>
    <div v-else-if="showDenied" class="permission-denied">
      <h3>Acesso Negado</h3>
      <p>{{ deniedMessage || 'Você não tem permissão para acessar esta funcionalidade.' }}</p>
      <p><strong>Seu perfil:</strong> {{ authStore.userRole }}</p>
      <p><strong>Permissão necessária:</strong> {{ requiredRole || requiredPermission }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';

const props = defineProps({
  requiredRole: {
    type: String,
    default: null
  },
  requiredPermission: {
    type: String,
    default: null
  },
  requireAny: {
    type: Array,
    default: () => []
  },
  showDenied: {
    type: Boolean,
    default: true
  },
  deniedMessage: {
    type: String,
    default: null
  }
});

const authStore = useAuthStore();

const hasPermission = computed(() => {
  // Se não está autenticado, negar acesso
  if (!authStore.isAuthenticated) {
    return false;
  }

  // Se não há requisitos, sempre permite
  if (!props.requiredRole && !props.requiredPermission && props.requireAny.length === 0) {
    return true;
  }

  // Verificar role específico
  if (props.requiredRole) {
    return authStore.userRole === props.requiredRole;
  }

  // Verificar permissão específica
  if (props.requiredPermission) {
    return authStore[props.requiredPermission] || false;
  }

  // Verificar qualquer uma das opções (OR)
  if (props.requireAny.length > 0) {
    return props.requireAny.some(permission => {
      if (permission.startsWith('role:')) {
        return authStore.userRole === permission.replace('role:', '');
      }
      return authStore[permission] || false;
    });
  }

  return false;
});
</script>

<style scoped>
.permission-denied {
  background: #ffebee;
  border: 1px solid #f44336;
  border-radius: 8px;
  padding: 24px;
  margin: 16px 0;
  text-align: center;
}

.permission-denied h3 {
  color: #f44336;
  margin: 0 0 16px 0;
}

.permission-denied p {
  color: #666;
  margin: 8px 0;
}
</style>
