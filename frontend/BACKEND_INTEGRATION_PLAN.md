# Plano de Integração Frontend-Backend

## Overview
Este documento descreve o plano para integrar o frontend Vue.js com o backend JAX-RS (CSOnline).

## Objetivos
- [x] Criar serviço de backend abstrato (`BackendService`)
- [x] Configurar endpoints e URLs da API
- [x] Atualizar store de autenticação para usar backend real
- [ ] Migrar componentes de gerenciamento
- [ ] Implementar tratamento de erros robusto
- [ ] Adicionar loading states e feedback visual
- [ ] Testar integração completa

## Arquitetura da Integração

### Backend API Endpoints
```
Base URL: http://localhost:8080/csonline/api

Autenticação:
POST /login - Login de usuário

Couriers:
GET /couriers - Listar entregadores
GET /couriers/{id} - Buscar entregador
POST /couriers - Criar entregador
PUT /couriers/{id} - Atualizar entregador
DELETE /couriers/{id} - Deletar entregador

Customers:
GET /customers - Listar clientes
GET /customers/{id} - Buscar cliente
POST /customers - Criar cliente
PUT /customers/{id} - Atualizar cliente
DELETE /customers/{id} - Deletar cliente

Users:
GET /users - Listar usuários (para dropdowns)
GET /users/{id} - Buscar usuário

Teams:
GET /teams - Listar equipes
POST /teams - Criar equipe
PUT /teams/{id} - Atualizar equipe
DELETE /teams/{id} - Deletar equipe

Deliveries:
GET /deliveries - Listar entregas
POST /deliveries - Criar entrega
PUT /deliveries/{id} - Atualizar entrega
DELETE /deliveries/{id} - Deletar entrega
```

### Frontend Services
```
src/
├── config/
│   └── backend.js ✅ (Configurações da API)
├── services/
│   ├── api.js (Existente - para manter compatibilidade)
│   └── backend.js ✅ (Novo serviço principal)
└── stores/
    └── auth.js ✅ (Atualizado para backend real)
```

## Migração por Componente

### 1. Login Component ✅
- [x] Store de auth atualizado
- [x] Integração com `/login` endpoint
- [x] Tratamento de erros de autenticação

### 2. CourierManagement Component
- [ ] Substituir fetch direto por `backendService.getCouriers()`
- [ ] Implementar CRUD completo
- [ ] Adicionar loading states
- [ ] Tratamento de erros específicos

### 3. CustomerManagement Component  
- [ ] Migrar para `backendService.getCustomers()`
- [ ] Validação de formulários
- [ ] Feedback visual de operações

### 4. UserManagement Component
- [ ] Criar componente se não existir
- [ ] Implementar gestão de usuários

### 5. DeliveryManagement Component
- [ ] Implementar gestão de entregas
- [ ] Status tracking
- [ ] Filtros avançados

### 6. TeamManagement Component
- [ ] Gestão de equipes
- [ ] Associação de entregadores

## Implementação por Fases

### Fase 1: Infraestrutura ✅
- [x] BackendService class
- [x] Configuração de endpoints
- [x] Auth store atualizado
- [x] Estrutura de tratamento de erros

### Fase 2: Componentes Básicos (Em Progresso)
- [ ] CourierManagement integração
- [ ] CustomerManagement integração  
- [ ] Testes de integração básicos

### Fase 3: Funcionalidades Avançadas
- [ ] Filtros e busca server-side
- [ ] Paginação
- [ ] Upload de arquivos
- [ ] Exportação de dados

### Fase 4: Polimento e Otimização
- [ ] Loading skeletons
- [ ] Cache de dados
- [ ] Retry automático
- [ ] Offline support

## Configurações Específicas

### Desenvolvimento
```javascript
BASE_URL: 'http://localhost:8080/csonline/api'
DEBUG: true
TIMEOUT: 30000ms
```

### Produção
```javascript
BASE_URL: '/csonline/api'
DEBUG: false  
TIMEOUT: 10000ms
```

## Estratégia de Testes

### Testes Unitários
- [ ] BackendService métodos
- [ ] Error handling
- [ ] Data formatting

### Testes de Integração
- [ ] Login flow completo
- [ ] CRUD operations
- [ ] Error scenarios

### Testes E2E
- [ ] User workflows
- [ ] Cross-browser compatibility

## Checklist de Migração

### Para cada componente:
- [ ] Substituir fetch direto por BackendService
- [ ] Implementar loading states
- [ ] Adicionar error handling
- [ ] Validar formato de dados
- [ ] Testar operações CRUD
- [ ] Verificar permissões
- [ ] Documentar mudanças

## Possíveis Problemas e Soluções

### CORS Issues
```javascript
// Backend: Configurar headers CORS no WildFly
// Frontend: Proxy no vite.config.js se necessário
```

### Token Expiration
```javascript
// Implementado: Auto-refresh ou redirect para login
// Interceptor global para 401 responses
```

### Network Errors
```javascript
// Implementado: Retry automático
// Fallback para dados cached
```

### Data Format Mismatches
```javascript
// Implementado: formatDataForBackend()
// Validação de response do backend
```

## Métricas de Sucesso

- [ ] 100% dos componentes migrados
- [ ] 0 chamadas fetch diretas restantes
- [ ] Tempo de resposta < 2s para operações normais
- [ ] Error rate < 1%
- [ ] Cobertura de testes > 80%

## Monitoramento

### Logs de Desenvolvimento
```javascript
[BACKEND] GET /couriers - 200ms
[BACKEND] POST /customers - 150ms  
[BACKEND] ERROR DELETE /couriers/123 - 500ms
```

### Métricas de Produção
- Response times
- Error rates
- Success rates por endpoint
- User experience metrics

## Documentação

- [ ] API endpoints documentation
- [ ] Frontend service documentation  
- [ ] Error codes reference
- [ ] Developer guidelines
- [ ] Deployment procedures

---

**Status Atual**: Em Progresso
- Infraestrutura base criada
- Migração de componentes principais em andamento
- Testes de integração pendentes

**Próximos Passos**:
1. Migrar CourierManagement e CustomerManagement
2. Implementar testes de integração
3. Adicionar loading states e feedback visual
4. Documentar padrões e guidelines
