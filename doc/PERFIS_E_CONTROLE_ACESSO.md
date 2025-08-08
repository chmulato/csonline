# CSOnline - Perfis e Controle de Acesso

## Visão Geral

O CSOnline implementa um sistema de controle de acesso baseado em perfis (roles) que define as permissões e funcionalidades disponíveis para cada tipo de usuário no sistema de gestão de centros de distribuição.

## Arquitetura de Segurança

### Sistema de Autenticação
- **Protocolo**: JWT (JSON Web Token)
- **Tempo de Expiração**: Configurável no backend
- **Validação**: Servidor WildFly 31.0.1.Final
- **Armazenamento**: Pinia Store (frontend) + HttpOnly cookies

### Hierarquia de Perfis

```
ADMIN (Nível 4)
├── Controle total do sistema
├── Gestão de todos os usuários
└── Acesso a todas as funcionalidades

BUSINESS (Nível 3)
├── Gestão do centro de distribuição
├── Controle de entregadores e clientes
└── Acesso às operações da empresa

COURIER (Nível 2)
├── Acesso às entregas designadas
├── Atualização de status
└── Gestão de rotas

CUSTOMER (Nível 1)
├── Acompanhamento de pedidos
├── Histórico de entregas
└── Dados pessoais
```

## Perfis de Usuário

### 1. ADMIN (Administrador)
**Descrição**: Controle total do sistema  
**Código do Perfil**: `ADMIN`

**Permissões Completas:**
- Gestão de usuários (criar, editar, excluir todos os perfis)
- Gestão de empresas/centros de distribuição
- Gestão de entregadores
- Gestão de clientes
- Gestão de entregas
- Configurações do sistema
- Relatórios gerenciais completos

**Telas Acessíveis:**
- Dashboard Administrativo
- Gestão de Usuários
- Gestão de Empresas
- Gestão de Entregadores
- Gestão de Clientes
- Gestão de Entregas
- Relatórios e Analytics
- Configurações do Sistema

### 2. BUSINESS (Centro de Distribuição)
**Descrição**: Gestão de distribuição  
**Código do Perfil**: `BUSINESS`

**Permissões Limitadas ao Escopo:**
- Gestão de entregadores da própria empresa
- Gestão de clientes da própria empresa
- Gestão de entregas da própria empresa
- Relatórios da própria operação

**Restrições:**
- Não pode criar usuários ADMIN
- Não pode gerenciar outras empresas
- Não pode acessar dados de outras empresas

**Telas Acessíveis:**
- Dashboard Empresarial
- Gestão de Entregadores (próprios)
- Gestão de Clientes (próprios)
- Gestão de Entregas (próprias)
- Relatórios Empresariais

### 3. COURIER (Entregador)
**Descrição**: Entregador/Transportador  
**Código do Perfil**: `COURIER`

**Permissões Operacionais:**
- Visualização de entregas designadas
- Atualização de status de entrega
- Gestão de rotas
- Comunicação com clientes

**Restrições:**
- Não pode criar ou editar usuários
- Não pode gerenciar outros entregadores
- Acesso limitado às próprias entregas

**Telas Acessíveis:**
- Dashboard do Entregador
- Minhas Entregas
- Atualização de Status
- Gestão de Rotas
- Perfil Pessoal

### 4. CUSTOMER (Cliente)
**Descrição**: Cliente final  
**Código do Perfil**: `CUSTOMER`

**Permissões Básicas:**
- Acompanhamento de pedidos próprios
- Histórico de entregas
- Gestão de dados pessoais

**Restrições:**
- Não pode acessar dados administrativos
- Não pode gerenciar outros usuários
- Acesso limitado aos próprios pedidos

**Telas Acessíveis:**
- Dashboard do Cliente
- Meus Pedidos
- Histórico de Entregas
- Perfil Pessoal

## Matriz de Permissões

| Funcionalidade | ADMIN | BUSINESS | COURIER | CUSTOMER |
|----------------|-------|----------|---------|----------|
| **Gestão de Usuários** |
| Criar usuários | ✅ Todos | ❌ | ❌ | ❌ |
| Editar usuários | ✅ Todos | ❌ | ❌ | ❌ |
| Excluir usuários | ✅ Todos | ❌ | ❌ | ❌ |
| Visualizar usuários | ✅ Todos | ❌ | ❌ | ❌ |
| **Gestão de Empresas** |
| Criar empresas | ✅ | ❌ | ❌ | ❌ |
| Editar empresas | ✅ | ✅ Própria | ❌ | ❌ |
| Excluir empresas | ✅ | ❌ | ❌ | ❌ |
| Visualizar empresas | ✅ Todas | ✅ Própria | ❌ | ❌ |
| **Gestão de Entregadores** |
| Criar entregadores | ✅ | ✅ Próprios | ❌ | ❌ |
| Editar entregadores | ✅ | ✅ Próprios | ❌ | ❌ |
| Excluir entregadores | ✅ | ✅ Próprios | ❌ | ❌ |
| Visualizar entregadores | ✅ Todos | ✅ Próprios | ❌ | ❌ |
| **Gestão de Clientes** |
| Criar clientes | ✅ | ✅ Próprios | ❌ | ❌ |
| Editar clientes | ✅ | ✅ Próprios | ❌ | ❌ |
| Excluir clientes | ✅ | ✅ Próprios | ❌ | ❌ |
| Visualizar clientes | ✅ Todos | ✅ Próprios | ❌ | ❌ |
| **Gestão de Entregas** |
| Criar entregas | ✅ | ✅ Próprias | ❌ | ❌ |
| Editar entregas | ✅ | ✅ Próprias | ✅ Designadas | ❌ |
| Excluir entregas | ✅ | ✅ Próprias | ❌ | ❌ |
| Visualizar entregas | ✅ Todas | ✅ Próprias | ✅ Designadas | ✅ Próprias |
| **Relatórios** |
| Relatórios gerais | ✅ | ❌ | ❌ | ❌ |
| Relatórios empresariais | ✅ | ✅ Próprios | ❌ | ❌ |
| Relatórios de entrega | ✅ | ✅ Próprios | ✅ Próprios | ✅ Próprios |

## Implementação Técnica

### Frontend (Vue.js)

#### PermissionGuard Component
```vue
<!-- Exemplo de uso do controle de acesso -->
<PermissionGuard :require-any="['canCreateUsers']">
  <button @click="createUser">Novo Usuário</button>
</PermissionGuard>

<PermissionGuard :require-role="['ADMIN', 'BUSINESS']">
  <router-link to="/couriers">Gestão de Entregadores</router-link>
</PermissionGuard>
```

#### Auth Store (Pinia)
```javascript
// Getters de permissão disponíveis
const permissions = {
  // Verificações de perfil
  isAdmin: () => userRole === 'ADMIN',
  isBusiness: () => userRole === 'BUSINESS',
  isCourier: () => userRole === 'COURIER',
  isCustomer: () => userRole === 'CUSTOMER',
  
  // Permissões de usuários
  canAccessUsers: () => isAdmin,
  canCreateUsers: () => isAdmin,
  canEditUsers: () => isAdmin,
  canDeleteUsers: () => isAdmin,
  
  // Permissões de entregadores
  canAccessCouriers: () => isAdmin || isBusiness,
  canCreateCouriers: () => isAdmin || isBusiness,
  canEditCouriers: () => isAdmin || isBusiness,
  canDeleteCouriers: () => isAdmin || isBusiness,
  
  // Permissões de entregas
  canAccessDeliveries: () => true, // Todos podem ver suas entregas
  canCreateDeliveries: () => isAdmin || isBusiness,
  canEditDeliveries: () => isAdmin || isBusiness || isCourier,
  canDeleteDeliveries: () => isAdmin || isBusiness
}
```

### Backend (Java/WildFly)

#### Anotações de Segurança
```java
@RolesAllowed({"ADMIN"})
public class UserController {
    // Apenas administradores
}

@RolesAllowed({"ADMIN", "BUSINESS"})
public class CourierController {
    // Administradores e empresas
}

@RolesAllowed({"ADMIN", "BUSINESS", "COURIER"})
public class DeliveryController {
    // Administradores, empresas e entregadores
}
```

## Fluxo de Autenticação

### 1. Login
```
Cliente → Frontend → Backend
       ← JWT Token ←
```

### 2. Validação de Acesso
```
Requisição + JWT → Backend → Validação
                ← Resposta ← (200/403)
```

### 3. Controle no Frontend
```
Componente → Auth Store → Verificação de Perfil
          ← Permitido/Negado ←
```

## Segurança e Boas Práticas

### Validação Dupla
- **Frontend**: UX/UI responsiva baseada em permissões
- **Backend**: Validação de segurança obrigatória

### Princípio do Menor Privilégio
- Cada perfil possui apenas as permissões mínimas necessárias
- Acesso granular por funcionalidade
- Separação clara de responsabilidades

### Auditoria e Logs
- Registro de todas as ações sensíveis
- Rastreamento de alterações por usuário
- Logs de acesso e tentativas negadas

## Configuração e Manutenção

### Adição de Novos Perfis
1. Definir novo enum no backend
2. Configurar anotações de segurança
3. Atualizar Auth Store no frontend
4. Implementar PermissionGuard nos componentes
5. Atualizar documentação

### Modificação de Permissões
1. Revisar matriz de permissões
2. Atualizar backend (anotações)
3. Atualizar frontend (store + componentes)
4. Testar todas as combinações
5. Atualizar documentação

## Casos de Uso Comuns

### Cenário 1: Empresa Multifilial
- Administrador central (ADMIN)
- Gerentes de filial (BUSINESS)
- Entregadores por filial (COURIER)
- Clientes compartilhados (CUSTOMER)

### Cenário 2: Franquia
- Franqueador (ADMIN)
- Franqueados (BUSINESS)
- Entregadores próprios (COURIER)
- Base de clientes local (CUSTOMER)

### Cenário 3: Operação Terceirizada
- Empresa principal (ADMIN)
- Parceiros logísticos (BUSINESS)
- Entregadores terceiros (COURIER)
- Clientes finais (CUSTOMER)

## Troubleshooting

### Problemas Comuns

**Erro 403 - Acesso Negado**
- Verificar token JWT válido
- Confirmar perfil do usuário
- Validar permissões no backend

**Interface Não Atualiza**
- Verificar Auth Store
- Confirmar reactive updates
- Validar PermissionGuard

**Permissões Inconsistentes**
- Sincronizar frontend/backend
- Verificar cache do navegador
- Confirmar configuração do servidor

---

**Documento atualizado em**: Agosto 2025  
**Versão do Sistema**: CSOnline JWT 2.0  
**Responsável**: Equipe de Desenvolvimento CSOnline
