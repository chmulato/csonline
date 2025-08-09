# SISTEMA DE AUTORIZAÇÃO

## Visão Geral
O CSOnline implementa um sistema completo de autorização baseado em roles (RBAC - Role-Based Access Control) utilizando Jakarta EE Security. O sistema protege todos os endpoints REST através de um filtro centralizado que valida tokens JWT e controla acesso por perfis de usuário.

## Arquitetura de Segurança

### Componentes Principais

**AuthorizationFilter**
- Filtro Jakarta EE com prioridade alta (Priorities.AUTHORIZATION)
- Intercepta todas as requisições HTTP antes da execução dos controllers
- Valida tokens JWT e extrai informações do usuário
- Verifica permissões baseadas em anotações @RolesAllowed
- Injeta contexto de segurança para uso nos controllers

**SecurityContextHelper**
- Classe utilitária para acesso ao contexto de segurança
- Métodos para obter informações do usuário logado
- Validação de roles e permissões
- Integração com JwtUtil para processamento de tokens

**Anotações de Segurança**
- @RolesAllowed: Define quais roles podem acessar cada endpoint
- @PermitAll: Permite acesso público (login, health checks)
- @DenyAll: Bloqueia acesso completamente (não utilizado no projeto)

## Perfis de Usuário

### ADMIN
- Acesso total ao sistema
- Pode gerenciar usuários, configurações e dados mestres
- Operações: CRUD completo em todas as entidades

### BUSINESS
- Gestão empresarial de entregas
- Pode gerenciar customers, couriers, deliveries e preços
- Operações: CRUD em entidades relacionadas ao negócio

### COURIER
- Perfil operacional para entregadores
- Pode visualizar e atualizar status de entregas
- Operações: Leitura geral, atualização de deliveries

### CUSTOMER
- Perfil básico para clientes
- Pode visualizar apenas seus próprios dados
- Operações: Leitura restrita aos próprios registros

## Mapeamento de Permissões

### UserController
```
GET    /users          - ADMIN, BUSINESS
GET    /users/{id}     - ADMIN, BUSINESS
POST   /users          - ADMIN
PUT    /users/{id}     - ADMIN, BUSINESS
DELETE /users/{id}     - ADMIN
```

### CourierController
```
GET    /couriers       - ADMIN, BUSINESS, COURIER
GET    /couriers/{id}  - ADMIN, BUSINESS, COURIER
POST   /couriers       - ADMIN, BUSINESS
PUT    /couriers/{id}  - ADMIN, BUSINESS
DELETE /couriers/{id}  - ADMIN, BUSINESS
```

### DeliveryController
```
GET    /deliveries     - ADMIN, BUSINESS, COURIER, CUSTOMER
GET    /deliveries/{id} - ADMIN, BUSINESS, COURIER, CUSTOMER
POST   /deliveries     - ADMIN, BUSINESS
PUT    /deliveries/{id} - ADMIN, BUSINESS, COURIER
DELETE /deliveries/{id} - ADMIN, BUSINESS
```

### CustomerController
```
GET    /customers      - ADMIN, BUSINESS
GET    /customers/{id} - ADMIN, BUSINESS, CUSTOMER
POST   /customers      - ADMIN, BUSINESS
PUT    /customers/{id} - ADMIN, BUSINESS
DELETE /customers/{id} - ADMIN, BUSINESS
```

### PriceController
```
GET    /prices         - ADMIN, BUSINESS
GET    /prices/{id}    - ADMIN, BUSINESS
POST   /prices         - ADMIN, BUSINESS
PUT    /prices/{id}    - ADMIN, BUSINESS
DELETE /prices/{id}    - ADMIN, BUSINESS
```

### SMSController
```
GET    /sms            - ADMIN, BUSINESS
GET    /sms/{id}       - ADMIN, BUSINESS
POST   /sms            - ADMIN, BUSINESS
PUT    /sms/{id}       - ADMIN, BUSINESS
DELETE /sms/{id}       - ADMIN, BUSINESS
```

### TeamController
```
GET    /teams          - ADMIN
GET    /teams/{id}     - ADMIN
POST   /teams          - ADMIN
PUT    /teams/{id}     - ADMIN
DELETE /teams/{id}     - ADMIN
```

## Implementação Técnica

### Fluxo de Autorização

1. **Interceptação da Requisição**
   - AuthorizationFilter intercepta todas as requisições
   - Extrai token JWT do header Authorization
   - Valida formato e expiração do token

2. **Verificação de Permissões**
   - Identifica o método do controller requisitado
   - Verifica se existe anotação @RolesAllowed
   - Compara roles do usuário com roles permitidas

3. **Controle de Acesso**
   - Permite acesso se usuário possui role adequada
   - Retorna HTTP 401 para tokens inválidos
   - Retorna HTTP 403 para usuários sem permissão

4. **Injeção de Contexto**
   - Injeta informações do usuário no contexto da requisição
   - Disponibiliza dados através do SecurityContextHelper

### Integração com JWT

O sistema utiliza a infraestrutura JWT existente:

```java
// Validação de token
Claims claims = jwtUtil.validateToken(token);
String username = claims.getSubject();
String role = claims.get("role", String.class);

// Busca informações completas do usuário
User user = userService.findByLogin(username);
```

### Tratamento de Erros

**Token Ausente ou Inválido**
- Retorna HTTP 401 Unauthorized
- Header Content-Type: application/json
- Body: {"error": "Token JWT inválido ou ausente"}

**Acesso Negado**
- Retorna HTTP 403 Forbidden  
- Header Content-Type: application/json
- Body: {"error": "Acesso negado para este recurso"}

## Configuração e Deploy

### Dependências Maven
```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>2.1.1</version>
</dependency>
```

### Registro do Filtro
O AuthorizationFilter é automaticamente registrado pelo Jakarta EE através da anotação @Provider.

### Configuração de Prioridade
```java
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
```

## Testes e Validação

### Testes de Integração
- Validação de acesso com diferentes roles
- Testes de endpoints protegidos e públicos
- Verificação de headers de resposta em caso de erro

### Cenários de Teste
- Login com usuário ADMIN e acesso a recursos restritos
- Tentativa de acesso com token expirado
- Acesso de CUSTOMER a dados de outros customers
- Operações CRUD com diferentes perfis

### Monitoramento
- Logs de tentativas de acesso negado
- Auditoria de operações por perfil de usuário
- Métricas de uso por endpoint e role

## Considerações de Segurança

### Boas Práticas Implementadas
- Tokens JWT com expiração configurável
- Validação rigorosa de assinatura de tokens
- Controle granular por endpoint e método HTTP
- Logs de segurança para auditoria

### Recomendações Operacionais
- Rotação periódica de chaves JWT
- Monitoramento de tentativas de acesso inválidas
- Revisão regular de permissões por perfil
- Backup e versionamento de configurações de segurança

## Manutenção e Evolução

### Adição de Novos Endpoints
1. Implementar o controller com métodos HTTP
2. Adicionar anotações @RolesAllowed apropriadas
3. Atualizar documentação de permissões
4. Criar testes de autorização

### Novos Perfis de Usuário
1. Definir role na enumeração UserRole
2. Atualizar mapeamento de permissões
3. Modificar anotações @RolesAllowed conforme necessário
4. Implementar testes específicos do novo perfil

### Auditoria e Compliance
- Relatórios de acesso por usuário e período
- Logs estruturados para análise de segurança
- Rastreabilidade completa de operações sensíveis
- Documentação atualizada de permissões e roles
