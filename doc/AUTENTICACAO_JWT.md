# Autenticação JWT - CSOnline

## Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Implementação Backend](#implementação-backend)
- [Implementação Frontend](#implementação-frontend)
- [Segurança](#segurança)
- [Endpoints](#endpoints)
- [Testes](#testes)
- [Troubleshooting](#troubleshooting)

## Visão Geral

O CSOnline implementa autenticação baseada em **JWT (JSON Web Tokens)** para garantir a segurança da aplicação. Esta implementação fornece:

- **Autenticação stateless** - Não requer sessões no servidor
- **Tokens seguros** - Assinados com HMAC SHA-512
- **Expiração automática** - Tokens válidos por 24 horas
- **Informações do usuário** - Username, role e ID embarcados no token
- **Proteção automática** - Filtro que protege todos os endpoints sensíveis

## Arquitetura

```
┌─────────────────┐    JWT Token    ┌─────────────────┐
│   Frontend      │ ◄─────────────► │    Backend      │
│   (Vue.js)      │                 │   (WildFly)     │
└─────────────────┘                 └─────────────────┘
         │                                   │
         ▼                                   ▼
┌─────────────────┐                 ┌─────────────────┐
│ Pinia Store     │                 │ JWT Filter      │
│ (auth.js)       │                 │ + JWT Util      │
│ - Token storage │                 │ - Validação     │
│ - Auto refresh  │                 │ - Extração      │
│ - Validation    │                 │ - Proteção      │
└─────────────────┘                 └─────────────────┘
```

## Implementação Backend

### 1. JwtUtil.java

Utilitário para geração e validação de tokens JWT.

**Localização:** `src/main/java/com/caracore/cso/util/JwtUtil.java`

**Principais métodos:**

```java
// Geração de token
public static String generateToken(String login, String role, Long userId)

// Validação
public static boolean validateToken(String token)
public static boolean isTokenExpired(String token)

// Extração de dados
public static String getLoginFromToken(String token)
public static String getRoleFromToken(String token)
public static Long getUserIdFromToken(String token)
```

**Configurações:**

- **Algoritmo:** HMAC SHA-512
- **Expiração:** 24 horas (86400000ms)
- **Chave secreta:** Configurável via variável de ambiente

### 2. JwtAuthenticationFilter.java

Filtro servlet que intercepta todas as requisições para `/api/*`.

**Localização:** `src/main/java/com/caracore/cso/security/JwtAuthenticationFilter.java`

**Funcionalidades:**

- Intercepta todas as requisições para `/api/*`
- Permite endpoints públicos (login, health, docs)
- Valida tokens JWT automaticamente
- Injeta informações do usuário no HttpServletRequest
- Retorna erros 401 para tokens inválidos

### 3. LoginController.java

Controller responsável pela autenticação e geração de tokens.

**Endpoint:** `POST /api/login`

**Resposta (200):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 2,
  "name": "Empresa",
  "login": "empresa",
  "role": "BUSINESS"
}
```

## Implementação Frontend

### 1. Auth Store (Pinia)

Gerenciamento de estado de autenticação.

**Localização:** `frontend/src/stores/auth.js`

**Funcionalidades:**

```javascript
// Estado (persistência)
const token = ref(localStorage.getItem('token') || '')
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

// Ações
async function login(credentials) { /* chama backendService.login e salva { token, id, name, login, role } */ }
function logout() { /* limpa token/user do estado e localStorage */ }
function clearAuth() { /* utilitário para limpar estado */ }
function validateAuth() { /* checa claim exp do JWT e limpa se expirado */ }

// Getters
const isAuthenticated = computed(() => !!token.value)
function getAuthHeaders() { return { Authorization: `Bearer ${token.value}` } }
```

### 2. Serviço de Backend

Cliente HTTP com autenticação/retries centralizados.

**Localização:** `frontend/src/services/backend.js`

**Funcionalidades:**

- Adiciona automaticamente Authorization quando autenticado
- Em 401, limpa sessão (token expirado)
- Mapeia 403/404/409 para mensagens claras
- Métodos convenientes (get, post, put, delete)

### 3. Login Component

Componente de interface para autenticação.

**Localização:** `frontend/src/components/Login.vue`

**Funcionalidades:**

- Formulário de login responsivo
- Validação em tempo real
- Estados de loading e erro
- Integração com auth store

## Segurança

### Endpoints Protegidos

Todos os endpoints que requerem autenticação JWT:

```
/api/users/*      - Gerenciamento de usuários
/api/couriers/*   - Gerenciamento de entregadores
/api/customers/*  - Gerenciamento de clientes
/api/deliveries/* - Gerenciamento de entregas
/api/teams/*      - Gerenciamento de equipes
/api/sms/*        - Gerenciamento de SMS
/api/prices/*     - Gerenciamento de preços
```

### Endpoints Públicos

Endpoints que NÃO requerem autenticação:

```
/api/login        - Autenticação
/api/health       - Health check
/api/docs         - Documentação
/api/swagger-ui   - Interface Swagger
/api/swagger      - Recursos Swagger
/api/openapi      - Documento OpenAPI (JSON)
```

### Configurações de Segurança

| Configuração | Valor         | Descrição              |
| -------------- | ------------- | ------------------------ |
| Algoritmo      | HS512         | HMAC SHA-512             |
| Expiração    | 24h           | 86400 segundos           |
| Header         | Authorization | Bearer token             |
| Storage        | localStorage  | Persistência no browser |

## Endpoints

### POST /api/login

Autentica usuário e retorna token JWT.

**Request:**

```json
{
  "login": "empresa",
  "password": "empresa123"
}
```

**Response (200):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlbXByZXNhIiwicm9sZSI6IkJVU0lORVNTIiwidXNlcklkIjoyLCJpYXQiOjE3NTQ2MDQ0NjQsImV4cCI6MTc1NDY5MDg2NH0...",
  "id": 2,
  "login": "empresa",
  "name": "Empresa",
  "role": "BUSINESS"
}
```

**Response (401):**

```json
{
  "error": "Unauthorized",
  "message": "Credenciais inválidas"
}
```

### Usando Token em Requisições

Todas as requisições para endpoints protegidos devem incluir o header:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Testes

### Teste Manual via cURL

**1. Login:**

```bash
curl -X POST "http://localhost:8080/csonline/api/login" \
  -H "Content-Type: application/json" \
  -d '{"login":"empresa","password":"empresa123"}'
```

**2. Usando Token:**

```bash
curl -X GET "http://localhost:8080/csonline/api/users" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Observação: com um token que não seja ADMIN, o acesso a `/api/users` deve retornar 403 Forbidden.

### Teste Frontend

1. Acesse `http://localhost:5173/`
2. Faça login com:
   - Login: `empresa`
   - Senha: `empresa123`
3. Verifique se é redirecionado para a tela principal
4. Abra as DevTools e verifique o localStorage

### Teste de Segurança

**Teste sem token:**

```bash
curl -X GET "http://localhost:8080/csonline/api/users"
# Deve retornar 401 Unauthorized
```

**Teste com token inválido:**

```bash
curl -X GET "http://localhost:8080/csonline/api/users" \
  -H "Authorization: Bearer invalid_token"
# Deve retornar 401 Unauthorized
```

## Troubleshooting

### Problema: "Token JWT não fornecido"

**Causa:** Header Authorization ausente
**Solução:** Verificar se o frontend está enviando o header correto

### Problema: "Token JWT inválido"

**Causas possíveis:**

1. Token expirado (24h)
2. Token malformado
3. Chave secreta alterada

**Soluções:**

1. Fazer novo login
2. Verificar formato do token
3. Verificar configurações do servidor

### Problema: "403 Forbidden" ao acessar um endpoint

**Causa:** Token é válido, mas a role do usuário não possui permissão para o recurso (@RolesAllowed)

**Solução:** Use um usuário com a role necessária (ex.: GET /api/users requer ADMIN)

### Problema: "CORS Error"

**Causa:** Configuração de CORS no WildFly
**Solução:** Verificar se o filtro permite OPTIONS requests

### Problema: Frontend não consegue fazer login

**Verificações:**

1. Backend rodando em `http://localhost:8080`
2. Proxy Vite configurado corretamente
3. Credenciais corretas (`empresa/empresa123`)

### Debug Mode

Para habilitar logs detalhados, adicione no `application.properties`:

```properties
# JWT Debug
jwt.debug=true
logging.level.com.caracore.cso.security=DEBUG
```

## Logs Úteis

**Login bem-sucedido:**

```
INFO: Usuario empresa autenticado com sucesso
DEBUG: Token JWT gerado para usuario empresa
```

**Acesso negado:**

```
WARN: Tentativa de acesso sem token para /api/users
DEBUG: Token inválido ou expirado
```

**Filtro JWT:**

```
JwtAuthenticationFilter - Path: users, Method: GET
JwtAuthenticationFilter - Token válido para usuário: empresa, role: BUSINESS
```

## Atualizações Futuras

### Melhorias Planejadas:

1. **Refresh Tokens** - Renovação automática
2. **Logout Global** - Invalidação de tokens
3. **Rate Limiting** - Proteção contra ataques
4. **Audit Log** - Log de acessos e alterações
5. **Role-based Access** - Controle granular por perfil

### Configurações Avançadas:

1. **Multiple Secrets** - Rotação de chaves
2. **Token Blacklist** - Lista de tokens invalidados
3. **Session Management** - Controle de sessões ativas
4. **OAuth Integration** - Login com terceiros

---

## Suporte

Para dúvidas sobre a implementação JWT:

1. **Documentação técnica:** Este arquivo
2. **Código fonte:** Verificar comentários nos arquivos
3. **Logs:** Consultar logs do WildFly e browser
4. **Testes:** Executar testes automatizados

---

*Documentação atualizada em: 9 de agosto de 2025*
*Versão: 1.0*
*Autor: CSOnline Team*
