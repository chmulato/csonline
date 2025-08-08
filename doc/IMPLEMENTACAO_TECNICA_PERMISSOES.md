# CSOnline - Implementação Técnica do Sistema de Permissões

## Arquitetura do Sistema

### Visão Geral
O sistema de permissões do CSOnline implementa uma arquitetura em camadas com validação dupla (frontend e backend), garantindo segurança e experiência do usuário otimizada.

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (Vue.js)      │    │   (Java/JWT)    │    │   (HSQLDB)      │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ PermissionGuard │◄──►│ @RolesAllowed   │◄──►│ user_roles      │
│ Auth Store      │    │ JWT Validation  │    │ permissions     │
│ Route Guards    │    │ Security Filter │    │ audit_log       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Frontend - Vue.js Implementation

### 1. Auth Store (Pinia)

**Arquivo**: `src/stores/auth.js`

```javascript
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token'),
    user: null,
    userRole: null
  }),

  getters: {
    // Verificações de perfil base
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.userRole === 'ADMIN',
    isBusiness: (state) => state.userRole === 'BUSINESS',
    isCourier: (state) => state.userRole === 'COURIER',
    isCustomer: (state) => state.userRole === 'CUSTOMER',

    // Permissões de módulos
    canAccessUsers: (state) => state.userRole === 'ADMIN',
    canAccessBusinesses: (state) => ['ADMIN'].includes(state.userRole),
    canAccessCouriers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canAccessCustomers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canAccessDeliveries: (state) => ['ADMIN', 'BUSINESS', 'COURIER', 'CUSTOMER'].includes(state.userRole),

    // Permissões CRUD - Usuários
    canCreateUsers: (state) => state.userRole === 'ADMIN',
    canEditUsers: (state) => state.userRole === 'ADMIN',
    canDeleteUsers: (state) => state.userRole === 'ADMIN',

    // Permissões CRUD - Empresas
    canCreateBusinesses: (state) => state.userRole === 'ADMIN',
    canEditBusinesses: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canDeleteBusinesses: (state) => state.userRole === 'ADMIN',

    // Permissões CRUD - Entregadores
    canCreateCouriers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canEditCouriers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canDeleteCouriers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),

    // Permissões CRUD - Clientes
    canCreateCustomers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canEditCustomers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canDeleteCustomers: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),

    // Permissões CRUD - Entregas
    canCreateDeliveries: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canEditDeliveries: (state) => ['ADMIN', 'BUSINESS', 'COURIER'].includes(state.userRole),
    canDeleteDeliveries: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),

    // Permissões de relatórios
    canAccessReports: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole),
    canAccessGlobalReports: (state) => state.userRole === 'ADMIN',
    canAccessBusinessReports: (state) => ['ADMIN', 'BUSINESS'].includes(state.userRole)
  },

  actions: {
    async login(credentials) {
      try {
        const response = await fetch('http://localhost:8080/csonline/api/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(credentials)
        })

        if (response.ok) {
          const data = await response.json()
          this.token = data.token
          this.user = data.user
          this.userRole = data.user.role
          localStorage.setItem('token', this.token)
          return true
        }
        return false
      } catch (error) {
        console.error('Erro no login:', error)
        return false
      }
    },

    logout() {
      this.token = null
      this.user = null
      this.userRole = null
      localStorage.removeItem('token')
    },

    // Método auxiliar para verificar múltiplas permissões
    hasAnyPermission(permissions) {
      return permissions.some(permission => this[permission])
    },

    hasAllPermissions(permissions) {
      return permissions.every(permission => this[permission])
    },

    hasRole(roles) {
      return roles.includes(this.userRole)
    }
  }
})
```

### 2. PermissionGuard Component

**Arquivo**: `src/components/PermissionGuard.vue`

```vue
<template>
  <div>
    <!-- Conteúdo permitido -->
    <slot v-if="hasAccess" />
    
    <!-- Mensagem de acesso negado -->
    <div v-else-if="showDenied" class="access-denied">
      <p>{{ deniedMessage }}</p>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'PermissionGuard',
  props: {
    // Lista de permissões - precisa de pelo menos uma
    requireAny: {
      type: Array,
      default: () => []
    },
    // Lista de permissões - precisa de todas
    requireAll: {
      type: Array,
      default: () => []
    },
    // Lista de perfis - precisa de pelo menos um
    requireRole: {
      type: Array,
      default: () => []
    },
    // Mostrar mensagem quando acesso negado
    showDenied: {
      type: Boolean,
      default: true
    },
    // Mensagem customizada
    deniedMessage: {
      type: String,
      default: 'Você não tem permissão para acessar esta funcionalidade.'
    }
  },
  setup(props) {
    const authStore = useAuthStore()

    const hasAccess = computed(() => {
      // Verificar se está autenticado
      if (!authStore.isAuthenticated) {
        return false
      }

      // Verificar perfis específicos
      if (props.requireRole.length > 0) {
        if (!authStore.hasRole(props.requireRole)) {
          return false
        }
      }

      // Verificar permissões - precisa de todas
      if (props.requireAll.length > 0) {
        if (!authStore.hasAllPermissions(props.requireAll)) {
          return false
        }
      }

      // Verificar permissões - precisa de pelo menos uma
      if (props.requireAny.length > 0) {
        if (!authStore.hasAnyPermission(props.requireAny)) {
          return false
        }
      }

      return true
    })

    return {
      hasAccess
    }
  }
}
</script>

<style scoped>
.access-denied {
  padding: 20px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  text-align: center;
  color: #6c757d;
  margin: 20px 0;
}

.access-denied p {
  margin: 0;
  font-style: italic;
}
</style>
```

### 3. Router Guards

**Arquivo**: `src/router/index.js`

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../components/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../components/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: () => import('../components/UserManagement.vue'),
    meta: { 
      requiresAuth: true,
      permissions: ['canAccessUsers']
    }
  },
  {
    path: '/couriers',
    name: 'CourierManagement',
    component: () => import('../components/CourierManagement.vue'),
    meta: { 
      requiresAuth: true,
      permissions: ['canAccessCouriers']
    }
  },
  {
    path: '/customers',
    name: 'CustomerManagement',
    component: () => import('../components/CustomerManagement.vue'),
    meta: { 
      requiresAuth: true,
      permissions: ['canAccessCustomers']
    }
  },
  {
    path: '/deliveries',
    name: 'DeliveryManagement',
    component: () => import('../components/DeliveryManagement.vue'),
    meta: { 
      requiresAuth: true,
      permissions: ['canAccessDeliveries']
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Guard global para verificar autenticação e permissões
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Verificar se a rota requer autenticação
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // Verificar permissões específicas
  if (to.meta.permissions) {
    if (!authStore.hasAnyPermission(to.meta.permissions)) {
      // Redirecionar para página de acesso negado ou dashboard
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
```

### 4. Uso em Componentes

**Exemplo**: `UserManagement.vue`

```vue
<template>
  <div class="user-management">
    <PermissionGuard :require-any="['canAccessUsers']">
      <h2>Gestão de Usuários</h2>
      
      <!-- Indicador de perfil -->
      <div class="role-indicator">
        <span class="role-badge" :class="authStore.userRole.toLowerCase()">
          {{ authStore.userRole }} - {{ getRoleDescription() }}
        </span>
      </div>

      <!-- Ações com permissões -->
      <div class="actions">
        <PermissionGuard :require-any="['canCreateUsers']" :show-denied="false">
          <button @click="showForm = true" class="btn-primary">
            Novo Usuário
          </button>
        </PermissionGuard>
        <button class="back-btn" @click="goBack">Voltar</button>
      </div>

      <!-- Tabela com colunas condicionais -->
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>Perfil</th>
            <th v-if="authStore.canEditUsers || authStore.canDeleteUsers">
              Ações
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.role }}</td>
            <td v-if="authStore.canEditUsers || authStore.canDeleteUsers">
              <PermissionGuard :require-any="['canEditUsers']" :show-denied="false">
                <button @click="editUser(user)" class="btn-edit">
                  Editar
                </button>
              </PermissionGuard>
              <PermissionGuard :require-any="['canDeleteUsers']" :show-denied="false">
                <button @click="deleteUser(user.id)" class="btn-delete">
                  Excluir
                </button>
              </PermissionGuard>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Formulário com permissões para criação de perfis -->
      <div v-if="showForm" class="modal">
        <div class="modal-content">
          <form @submit.prevent="saveUser">
            <select v-model="form.role" required>
              <option value="">Selecione o perfil</option>
              <PermissionGuard :require-any="['isAdmin']" :show-denied="false">
                <option value="ADMIN">Administrador</option>
              </PermissionGuard>
              <option value="BUSINESS">Centro de Distribuição</option>
              <option value="COURIER">Entregador</option>
              <option value="CUSTOMER">Cliente</option>
            </select>
          </form>
        </div>
      </div>
    </PermissionGuard>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import PermissionGuard from './PermissionGuard.vue'

export default {
  components: { PermissionGuard },
  setup() {
    const authStore = useAuthStore()
    
    const getRoleDescription = () => {
      const descriptions = {
        ADMIN: 'Controle total do sistema',
        BUSINESS: 'Gestão de distribuição',
        COURIER: 'Entregador/Transportador',
        CUSTOMER: 'Cliente final'
      }
      return descriptions[authStore.userRole] || 'Papel indefinido'
    }

    return {
      authStore,
      getRoleDescription
    }
  }
}
</script>
```

## Backend - Java Implementation

### 1. Security Configuration

**Arquivo**: `SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/businesses/**").hasAnyRole("ADMIN", "BUSINESS")
                .requestMatchers("/api/couriers/**").hasAnyRole("ADMIN", "BUSINESS")
                .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "BUSINESS")
                .requestMatchers("/api/deliveries/**").hasAnyRole("ADMIN", "BUSINESS", "COURIER", "CUSTOMER")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }
}
```

### 2. Controller Annotations

**Arquivo**: `UserController.java`

```java
@RestController
@RequestMapping("/api/users")
@RolesAllowed("ADMIN")
public class UserController {

    @GetMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<User>> getAllUsers() {
        // Apenas administradores podem listar todos os usuários
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        // Apenas administradores podem criar usuários
        return ResponseEntity.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        // Apenas administradores podem editar usuários
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Apenas administradores podem excluir usuários
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Arquivo**: `CourierController.java`

```java
@RestController
@RequestMapping("/api/couriers")
@RolesAllowed({"ADMIN", "BUSINESS"})
public class CourierController {

    @GetMapping
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public ResponseEntity<List<Courier>> getCouriers(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        if ("ROLE_ADMIN".equals(role)) {
            // Administrador vê todos os entregadores
            return ResponseEntity.ok(courierService.findAll());
        } else {
            // Empresa vê apenas seus entregadores
            User currentUser = getCurrentUser(auth);
            return ResponseEntity.ok(courierService.findByBusiness(currentUser.getBusiness()));
        }
    }

    @PostMapping
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public ResponseEntity<Courier> createCourier(@RequestBody CreateCourierRequest request, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        if ("ROLE_BUSINESS".equals(role)) {
            // Empresas só podem criar entregadores para sua própria empresa
            User currentUser = getCurrentUser(auth);
            request.setBusinessId(currentUser.getBusiness().getId());
        }
        
        return ResponseEntity.ok(courierService.create(request));
    }
}
```

### 3. JWT Token Generation

**Arquivo**: `AuthController.java`

```java
@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.authenticate(request.getLogin(), request.getPassword());
            
            String token = Jwts.builder()
                .setSubject(user.getLogin())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("businessId", user.getBusiness() != null ? user.getBusiness().getId() : null)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUser(user);
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
```

### 4. Service Layer Security

**Arquivo**: `CourierService.java`

```java
@Service
@Transactional
public class CourierService {

    public List<Courier> findByCurrentUser(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        switch (role) {
            case "ROLE_ADMIN":
                return courierRepository.findAll();
            case "ROLE_BUSINESS":
                User currentUser = getCurrentUser(auth);
                return courierRepository.findByBusinessId(currentUser.getBusiness().getId());
            default:
                throw new AccessDeniedException("Acesso negado para visualizar entregadores");
        }
    }

    public Courier create(CreateCourierRequest request, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        // Validar se empresa pode criar entregador para o business especificado
        if ("ROLE_BUSINESS".equals(role)) {
            User currentUser = getCurrentUser(auth);
            if (!currentUser.getBusiness().getId().equals(request.getBusinessId())) {
                throw new AccessDeniedException("Não é possível criar entregador para outra empresa");
            }
        }
        
        return courierRepository.save(mapToEntity(request));
    }
}
```

## Database Schema

### Tabela de Usuários
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mobile VARCHAR(20),
    address VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'BUSINESS', 'COURIER', 'CUSTOMER')),
    business_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);
```

### Tabela de Auditoria
```sql
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    resource_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Testes de Segurança

### Testes de Permissão Frontend
```javascript
// tests/permissions.test.js
import { describe, it, expect } from 'vitest'
import { useAuthStore } from '../src/stores/auth'

describe('Permission System', () => {
  it('should allow admin to access all functions', () => {
    const store = useAuthStore()
    store.userRole = 'ADMIN'
    
    expect(store.canAccessUsers).toBe(true)
    expect(store.canCreateCouriers).toBe(true)
    expect(store.canDeleteDeliveries).toBe(true)
  })

  it('should restrict business user appropriately', () => {
    const store = useAuthStore()
    store.userRole = 'BUSINESS'
    
    expect(store.canAccessUsers).toBe(false)
    expect(store.canCreateCouriers).toBe(true)
    expect(store.canAccessDeliveries).toBe(true)
  })

  it('should limit courier to delivery operations', () => {
    const store = useAuthStore()
    store.userRole = 'COURIER'
    
    expect(store.canAccessUsers).toBe(false)
    expect(store.canCreateCouriers).toBe(false)
    expect(store.canEditDeliveries).toBe(true)
  })
})
```

### Testes de API Backend
```java
// CourierControllerTest.java
@SpringBootTest
@AutoConfigureTestDatabase
public class CourierControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminCanAccessAllCouriers() throws Exception {
        mockMvc.perform(get("/api/couriers"))
               .andExpect(status().isOk())
               .andExpected(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(roles = "BUSINESS")
    public void businessCanAccessOwnCouriers() throws Exception {
        mockMvc.perform(get("/api/couriers"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void customerCannotAccessCouriers() throws Exception {
        mockMvc.perform(get("/api/couriers"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUSINESS")
    public void businessCannotCreateCourierForOtherBusiness() throws Exception {
        CreateCourierRequest request = new CreateCourierRequest();
        request.setBusinessId(999L); // ID de outra empresa
        
        mockMvc.perform(post("/api/couriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isForbidden());
    }
}
```

## Monitoramento e Logs

### Log de Acesso
```java
@Component
@Slf4j
public class SecurityAuditLogger {

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String role = event.getAuthentication().getAuthorities().toString();
        
        log.info("Login successful - User: {}, Role: {}, IP: {}", 
                username, role, getClientIP());
                
        // Salvar no banco para auditoria
        auditService.logAccess(username, "LOGIN_SUCCESS", getClientIP());
    }

    @EventListener
    public void handleAccessDenied(AuthorizationDeniedEvent event) {
        Authentication auth = event.getAuthentication();
        String resource = event.getAuthorizationDecision().toString();
        
        log.warn("Access denied - User: {}, Resource: {}, IP: {}", 
                auth.getName(), resource, getClientIP());
                
        auditService.logAccess(auth.getName(), "ACCESS_DENIED", getClientIP());
    }
}
```

## Deployment e Configuração

### Variáveis de Ambiente
```bash
# JWT Configuration
JWT_SECRET=your-super-secret-key-here
JWT_EXPIRATION=86400000

# Security Configuration
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:8080
ENABLE_CSRF=false
SESSION_TIMEOUT=3600

# Database Configuration
DB_URL=jdbc:hsqldb:file:./data/csonline
DB_USERNAME=sa
DB_PASSWORD=

# Logging Configuration
LOG_LEVEL=INFO
AUDIT_LOG_ENABLED=true
```

### Configuração do WildFly
```xml
<!-- standalone.xml -->
<security-domain name="csonline-security">
    <authentication>
        <login-module code="Database" flag="required">
            <module-option name="dsJndiName" value="java:jboss/datasources/csonlineDS"/>
            <module-option name="principalsQuery" value="SELECT password FROM users WHERE login=?"/>
            <module-option name="rolesQuery" value="SELECT role, 'Roles' FROM users WHERE login=?"/>
        </login-module>
    </authentication>
</security-domain>
```

---

**Documento atualizado em**: Agosto 2025  
**Versão do Sistema**: CSOnline JWT 2.0  
**Responsável**: Equipe de Desenvolvimento CSOnline
