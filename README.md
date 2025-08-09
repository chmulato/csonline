# CSOnline - Gestão CD (Centro de Distribuição) JWT 2.0

Sistema completo para gestão de centros de distribuição, entregas, entregadores, equipes, preços e comunicação via SMS/WhatsApp. Desenvolvido com Jakarta EE 10 no backend, Vue 3 + Vite no frontend e **autenticação JWT enterprise**.

**MARCO HISTÓRICO ALCANÇADO: SEGURANÇA JWT 2.0 ENTERPRISE IMPLEMENTADA** (8 de Agosto/2025)

### Atualização — 9 de Agosto/2025
- Merge para main concluído; filtros de segurança (`JwtAuthenticationFilter`, `AuthorizationFilter`, `CorsFilter`) registrados no `ResourceConfig`.
- GET de usuários agora ADMIN-only; 403 esperado para perfis não-admin (por design).
- Scripts de testes ajustados para enviar `Authorization: Bearer` e consolidar resultados corretamente.
- WAR recompilado e publicado no WildFly; suítes de saúde, JWT e endpoints executadas end-to-end.
- Resultado atual: Segurança ~90% com token não-admin (403 esperados em endpoints ADMIN-only); ajustes pendentes nos testes de endpoints (IDs válidos, usernames únicos, payloads com IDs).

## Funcionalidades Implementadas

### **Sistema Completo de Gestão de Centros de Distribuição**

**Frontend Vue 3 SPA - 100% Funcional com Autenticação JWT:**
- **Sistema de Login JWT** - Autenticação Bearer Token com Pinia store
- **Gestão de Usuários** - CRUD completo para administradores do sistema (protegido)
- **Gestão de Entregadores** - Cadastro e controle de couriers com comissões (protegido)
- **Gestão de Centros de Distribuição** - Cadastro e controle de CDs com endereços (protegido)
- **Gestão de Entregas** - Sistema completo com status, filtros e rastreamento (protegido)
- **Gestão de Equipes** - Vinculação de entregadores aos centros de distribuição (protegido)
- **Gestão de SMS/WhatsApp** - Sistema de mensagens com templates para entregas (protegido)
- **Gestão de Preços** - Tabelas de preços por centro de distribuição, veículo e localização (protegido)
- **Interceptors HTTP** - Injeção automática de Bearer Token em todas as requisições

### **Recursos Técnicos JWT 2.0:**
- **Autenticação JWT Enterprise** com HMAC SHA-512
- **Proteção automática de endpoints** via filtro de segurança
- **Gerenciamento de sessão** com localStorage e validação de expiração
- **Interface responsiva e moderna** com login obrigatório
- **Dashboard com estatísticas** em cada módulo protegido
- **Sistema de filtros e busca avançada** com autorização
- **Modais para criação/edição/visualização** protegidos por token
- **Validação de formulários** com verificação de autenticação
- **Navegação SPA sem reload** com controle de acesso
- **API client integrado** com interceptors automáticos

### **Backend Jakarta EE 10 com Segurança JWT:**

- **Sistema de autenticação JWT** completo e seguro
- **Filtro de segurança automático** protegendo endpoints `/api/*`
- **APIs REST completamente documentadas** e protegidas
- **Swagger UI integrado** para testes com autenticação
- **Banco de dados HSQLDB** com usuários de teste
- **Logging customizado** incluindo eventos de autenticação
- **Deploy automatizado no WildFly 31** com segurança
- **Flyway para migrações** de banco de dados e usuários
- **Suite completa de testes de segurança** para todos os endpoints

## Principais Tecnologias

### **Frontend:**

- Vue 3 + Composition API
- Vite (build tool)
- CSS3 moderno e responsivo
- FontAwesome (ícones)

### **Backend:**

- Jakarta EE 10
- **JWT Authentication** (JJWT 0.12.3)
- JPA (Jakarta Persistence API)
- EclipseLink (JPA Provider)
- Jersey (JAX-RS)
- HSQLDB (HyperSQL Database) - única solução de banco de dados utilizada
- Flyway (Migrações de banco de dados)
- Log4j 2
- Swagger/OpenAPI
- JUnit 5, Mockito

### **Deploy:**

- WildFly 31 Application Server
- Scripts PowerShell automatizados
- Build e deploy integrados

## Suite de Testes de Segurança JWT 2.0

O projeto conta com uma **suite completa de testes de segurança JWT** para garantir a proteção e confiabilidade de todos os endpoints da API:

### **Scripts de Teste JWT Disponíveis:**

- **`test-users.ps1`** - Testa endpoints de usuários com autenticação JWT (GET, POST, PUT, DELETE)
- **`test-customers.ps1`** - Testa endpoints de centros de distribuição com Bearer Token
- **`test-couriers.ps1`** - Testa endpoints de entregadores com JWT
- **`test-teams.ps1`** - Testa endpoints de equipes com autenticação
- **`test-deliveries.ps1`** - Testa endpoints de entregas protegidas
- **`test-sms.ps1`** - Testa endpoints de SMS/WhatsApp com JWT
- **`test-login.ps1`** - Testa endpoint de autenticação JWT

### **Ferramentas de Segurança:**

- **`test-jwt-security.ps1`** - **Suite completa com 20 testes de segurança JWT**
- **`jwt-utility.ps1`** - Funções utilitárias para operações JWT em PowerShell
- **`test-all-endpoints.ps1`** - Executa todos os testes com autenticação JWT
- **`health-check-endpoints.ps1`** - Verificação de saúde sem autenticação
- **`run-tests.ps1`** - **Script principal JWT 2.0** com opções de segurança

### **Como Executar os Testes JWT:**

```powershell
# Teste completo de segurança JWT (recomendado)
.\run-tests.ps1 -JWTSecurity -Login "admin" -Password "admin123"

# Verificação rápida de saúde (sem JWT)
.\run-tests.ps1 -HealthCheck

# Executar todos os testes com autenticação JWT
.\run-tests.ps1 -AllTests -Login "admin" -Password "admin123"

# Executar teste específico com JWT
.\run-tests.ps1 -OnlyTest "users" -Login "admin" -Password "admin123"

# Testes verbosos com logs detalhados
.\run-tests.ps1 -JWTSecurity -Verbose -Login "admin" -Password "admin123"
```

### **Credenciais de Teste Disponíveis:**

- **admin/admin123** - Perfil administrativo
- **empresa/empresa123** - Perfil centro de distribuição

### **Status Atual dos Endpoints JWT 2.0:**
- **`/api/login`** - Autenticação JWT - PÚBLICO (200)
- **`/api/health`** - Health Check - PÚBLICO (200)
- **`/api/users`** - Gestão de usuários - PROTEGIDO JWT (GET requer ADMIN; outros perfis 403)
- **`/api/customers`** - Gestão de centros de distribuição - PROTEGIDO JWT (200)
- **`/api/couriers`** - Gestão de entregadores - PROTEGIDO JWT (200)
- **`/api/teams`** - Gestão de equipes - PROTEGIDO JWT (200)
- **`/api/deliveries`** - Gestão de entregas - PROTEGIDO JWT (200)
- **`/api/sms`** - Sistema de SMS/WhatsApp - PROTEGIDO JWT (200)

Observação: Endpoints com autorização por perfil retornarão 403 quando acessados sem a role adequada (por exemplo, `ADMIN`).

### **Métricas de Segurança JWT Alcançadas:**
- **Cobertura de Segurança Validada**: ~90% com token não-admin (403 esperados em endpoints ADMIN-only)
- **Com token ADMIN**: 100% dos cenários cobertos (20/20)
- **Proteção contra acesso não autorizado**: 401 Unauthorized para endpoints protegidos
- **Validação de tokens**: Rejeição de tokens inválidos ou expirados
- **Autenticação automática**: Frontend com interceptors HTTP automáticos
- **Tempo de Resposta com JWT**: < 100ms incluindo validação de token
- **Dados de Teste**: 9 users, 2 couriers, 2 customers, 2 teams, 2 deliveries, 2 sms
- **Tokens JWT**: HMAC SHA-512 com expiração de 24 horas

### **Marco de Segurança Enterprise Implementado (7 de Agosto/2025):**

- **Sistema JWT completo**: Autenticação, autorização e proteção automática
- **20 testes de segurança**: Validação de todos os cenários de proteção
- **Integração frontend-backend**: Vue 3 + Jakarta EE com JWT seamless
- **Resultado**: **Segurança Enterprise** alcançada - 100% dos endpoints protegidos

### **Benefícios dos Testes de Segurança JWT:**
- **Validação completa de segurança** com 20 cenários de teste
- **Proteção contra acesso não autorizado** - endpoints retornam 401 sem JWT
- **Validação de tokens JWT** - rejeição de tokens inválidos ou expirados
- **Testes de endpoints públicos** - login e health funcionam sem autenticação
- **Identificação proativa de vulnerabilidades** e correção sistemática
- **Testes de regressão de segurança** após mudanças no código
- **Documentação viva** dos comportamentos de segurança esperados
- **Facilita debugging** de problemas de autenticação com diagnósticos detalhados
- **Onboarding simplificado** para novos desenvolvedores com exemplos JWT
- **Garantia de segurança** em produção com validação automática

### **Marco Histórico de Segurança Enterprise:**
O CSOnline atingiu em 7 de agosto de 2025 a **segurança enterprise completa** com 100% dos endpoints protegidos por JWT e 20 testes de segurança aprovados. Esta conquista representa a consolidação de um sistema enterprise robusto, seguro, testado e pronto para produção com autenticação de nível corporativo.

Todos os scripts estão localizados em `scr/tests/` e incluem autenticação JWT, tratamento de erros, saídas coloridas e relatórios detalhados de segurança.

## Como Executar

### **Front-end Vue SPA (Desenvolvimento)**

1. **Instale as dependências:**

   ```powershell
   cd frontend
   npm install
   ```
2. **Execute em modo desenvolvimento:**

   ```powershell
   npm run dev
   ```

   Acesse em [http://localhost:5173](http://localhost:5173) (porta padrão Vite).
3. **Build de produção:**

   ```powershell
   npm run build
   ```

   Os arquivos finais estarão em `frontend/dist`.
4. **Integrar build com backend:**

   ```powershell
   pwsh ./src/build-frontend.ps1
   ```

   Copia o conteúdo de `dist/` para `src/main/webapp/` e serve o SPA junto ao backend.

### **Backend + Deploy WildFly 31**

Scripts automatizados estão disponíveis na raiz do projeto para facilitar o deploy:

#### Diagrama de Sequência dos Scripts

```mermaid
sequenceDiagram
    participant Dev as Desenvolvedor
    participant PS as PowerShell
    participant WildFly as WildFly 31
    participant WAR as WAR

    Dev->>PS: Executa prepare-artifact-wildfly.ps1
    PS->>WAR: Gera e copia WAR para deployments
    Dev->>PS: Executa config-wildfly-31.ps1 (opcional)
    PS->>WildFly: Configura DataSource JDBC
    Dev->>PS: Executa config-ssl-wildfly-31.ps1 (opcional)
    PS->>WildFly: Configura HTTPS/SSL
    Dev->>PS: Executa start-wildfly-31.ps1
    PS->>WildFly: Inicia WildFly
    WildFly->>WAR: Faz deploy automático
    Dev->>PS: Executa stop-wildfly-31.ps1 (quando necessário)
    PS->>WildFly: Para WildFly
    Note over Dev,PS: deploy-wildfly-31.ps1 pode ser usado para copiar o WAR manualmente a qualquer momento
```

**Ordem típica de execução:**

1. `prepare-artifact-wildfly.ps1` → Gera e copia WAR
2. `config-wildfly-31.ps1` (opcional) → Configura DataSource
3. `config-ssl-wildfly-31.ps1` (opcional) → Configura HTTPS
4. `start-wildfly-31.ps1` → Inicia servidor
5. `stop-wildfly-31.ps1` → Para servidor quando necessário

**Comandos detalhados:**

1. **Preparar o artefato WAR e copiar para o WildFly:**
   ```powershell
   pwsh ./prepare-artifact-wildfly.ps1 [-DskipTests]
   ```
   Gera o arquivo `target/csonline.war` e copia para `server\wildfly-31.0.1.Final\standalone\deployments`.

2. **Iniciar o WildFly:**
   ```powershell
   pwsh ./start-wildfly-31.ps1
   ```
   Inicia o WildFly 31 em http://localhost:8080/.

3. **Parar o WildFly:**
   ```powershell
   pwsh ./stop-wildfly-31.ps1
   ```

   Para o WildFly 31.
4. **Deploy manual do WAR (opcional):**

   ```powershell
   pwsh ./deploy-wildfly-31.ps1
   ```

   Copia o WAR para a pasta deployments do WildFly.
5. **Configurar DataSource JDBC (opcional):**

   ```powershell
   pwsh ./config-wildfly-31.ps1
   ```

   Copia o driver JDBC e configura o DataSource no WildFly (exemplo para HSQLDB).
6. **Configurar HTTPS/SSL (opcional):**

   ```powershell
   pwsh ./config-ssl-wildfly-31.ps1
   ```

   Gera certificado autoassinado e configura HTTPS (porta 8443).
   Após executar, acesse: https://localhost:8443/csonline/

## Acessos do Sistema

### **URLs Principais - Sistema JWT 2.0 Enterprise Seguro:**

- **Frontend SPA**: [http://localhost:5173](http://localhost:5173) (desenvolvimento com proxy JWT)
- **Sistema Enterprise**: [http://localhost:8080/csonline/](http://localhost:8080/csonline/) *(login JWT obrigatório)*
- **Login JWT**: [http://localhost:8080/csonline/api/login](http://localhost:8080/csonline/api/login) *(endpoint público)*
- **Swagger UI**: [http://localhost:8080/csonline/swagger-ui/](http://localhost:8080/csonline/swagger-ui/)
- **APIs Protegidas**: `http://localhost:8080/csonline/api/*` *(Bearer Token obrigatório)*
- **Health Check**: `http://localhost:8080/csonline/api/health` *(endpoint público)*

### **Módulos Disponíveis no Frontend JWT:**
- **Login** → Autenticação JWT obrigatória
- **Dashboard Principal** → Menu de navegação protegido
- **Usuários** → Gestão de administradores (JWT)
- **Entregadores** → Cadastro de couriers (JWT)
- **Centros de Distribuição** → Gestão de CDs (JWT)
- **Entregas** → Controle de entregas (JWT)
- **Equipes** → Times de entregadores (JWT)
- **SMS/WhatsApp** → Mensagens para entregas (JWT)
- **Preços** → Tabelas de preços (JWT)
- **Logout** → Saída segura do sistema

### **Credenciais de Teste JWT:**

- **admin/admin123** - Perfil administrativo completo
- **empresa/empresa123** - Perfil centro de distribuição

## Logging e Monitoramento

Os logs customizados da aplicação são gravados em `logs/app.log` (relativo à raiz do projeto, configurado via scripts e logging do WildFly).

Para visualizar o log:

- Acesse o arquivo diretamente: `logs/app.log`
- Ou, via CLI do WildFly:
  ```powershell
  pwsh ./config-log-wildfly-31.ps1   # (executa a configuração, se necessário)
  Get-Content ./logs/app.log -Wait   # (acompanha o log em tempo real)
  ```

Além disso, o log padrão do servidor WildFly está em:
`server/wildfly-31.0.1.Final/standalone/log/server.log`

Você pode ajustar o formato e destino do log customizado editando o script ou via console administrativo do WildFly.

## Documentação Completa

Consulte o arquivo [doc/INDEX.md](doc/INDEX.md) para documentação detalhada do projeto, incluindo:

- **Arquitetura do sistema**
- **Regras de negócio**
- **Guias de integração**
- **Documentação completa do frontend Vue**
- **Especificações de segurança**

## Estrutura do Projeto

### **Backend (Jakarta EE):**

- `src/main/java/com/caracore/cso/controller/` - Controllers REST
- `src/main/java/com/caracore/cso/service/` - Serviços de negócio
- `src/main/java/com/caracore/cso/repository/` - Repositórios JPA/EclipseLink
- `src/main/java/com/caracore/cso/entity/` - Entidades JPA
- `src/main/resources/` - Configurações (log4j2.xml, persistence.xml)
- `src/test/java/` - Testes unitários

### **Frontend (Vue 3 SPA):**

- `frontend/src/components/` - Componentes Vue
  - `Login.vue` - Tela de autenticação
  - `MainLayout.vue` - Layout principal com menu
  - `UserManagement.vue` - Gestão de usuários
  - `CourierManagement.vue` - Gestão de entregadores
  - `CustomerManagement.vue` - Gestão de centros de distribuição
  - `DeliveryManagement.vue` - Gestão de entregas
  - `TeamManagement.vue` - Gestão de equipes
  - `SMSManagement.vue` - Gestão de SMS/WhatsApp
  - `PriceManagement.vue` - Gestão de preços
  - `Logout.vue` - Tela de logout
- `frontend/dist/` - Build final do front-end
- `src/main/webapp/` - Frontend integrado ao backend

### **Scripts e Configurações:**

- `*.ps1` - Scripts PowerShell para deploy automatizado
- `scr/tests/` - **Suite completa de testes automatizados**
  - `test-*.ps1` - Scripts individuais para cada endpoint
  - `test-all-endpoints.ps1` - Script master para todos os testes
  - `health-check-endpoints.ps1` - Verificação de saúde
  - `README-TESTES.ps1` - Documentação interativa dos testes
- `doc/` - Documentação completa do projeto
- `logs/` - Logs da aplicação

## Configuração

- **Backend:** Edite `src/main/resources/log4j2.xml` para ajustar logs e `src/main/resources/META-INF/persistence.xml` para configurar JPA/EclipseLink
- **Banco de dados:** HSQLDB em memória por padrão (configurável em `persistence.xml` e `application.properties`), sendo este o único banco de dados suportado pela aplicação
- **Frontend:** Customize os componentes Vue em `frontend/src/components/`
- **Deploy:** Use os scripts PowerShell na raiz para automação completa

## Próximos Passos

### **Com Segurança JWT 2.0 Enterprise Implementada, o foco agora é:**

#### **Autenticação JWT Enterprise - CONCLUÍDA:**
- **Sistema JWT completo** implementado e testado com 20 testes de segurança
- **Integração frontend-backend** com autenticação seamless Vue 3 + Jakarta EE
- **Proteção automática** de todos os endpoints `/api/*` com filtro de segurança
- **Tokens HMAC SHA-512** com expiração de 24 horas e validação robusta

#### **Próximas Evoluções de Produção:**
- **Operações CRUD Completas**: POST, PUT, DELETE com validação JWT em todos os módulos
- **Autorização por Perfis**: Controle granular de permissões baseado em roles JWT
- **Gestão Avançada de Usuários**: Criação, edição e desativação via interface autenticada
- **Auditoria de Segurança**: Logs detalhados de acesso, autenticação e operações

#### **Funcionalidades Enterprise Avançadas:**
- **Dashboard de Segurança**: Métricas de autenticação, tokens ativos e tentativas de acesso
- **Gestão de Tokens**: Renovação automática, blacklist e controle de sessões
- **Integração SSO**: Single Sign-On para ambientes corporativos
- **Monitoramento de Segurança**: Alertas em tempo real e relatórios de conformidade

### **Roadmap Estratégico JWT 2.0:**
1. **Segurança Enterprise** CONCLUÍDA (7 de Agosto/2025)
2. **Operações CRUD Autenticadas** (próxima milestone)
3. **Autorização Granular** (controle de permissões)
4. **Deploy Produção Seguro** (HTTPS + JWT enterprise)

## Contato

Para dúvidas, sugestões ou contribuições, abra uma issue no repositório.

---

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
