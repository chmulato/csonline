# 📁 Organização dos Scripts CSOnline

## 🗂️ **Estrutura de Diretórios**

### `scr/` - **Scripts de Gestão e Deploy**
Scripts para configuração, build e deploy do sistema:

- **Deploy e Servidor:**
  - `deploy-wildfly-31.ps1` - Deploy da aplicação no WildFly
  - `start-wildfly-31.ps1` - Iniciar servidor WildFly
  - `stop-wildfly-31.ps1` - Parar servidor WildFly

- **Configuração WildFly:**
  - `config-wildfly-31.ps1` - Configuração principal do WildFly
  - `config-jdbc-driver-wildfly-31.ps1` - Driver JDBC HSQLDB
  - `config-log-wildfly-31.ps1` - Configuração de logs
  - `config-ssl-wildfly-31.ps1` - Configuração SSL/HTTPS

- **Build e Artefatos:**
  - `prepare-artifact-wildfly.ps1` - Preparação de artefatos
  - `build-frontend.ps1` - Build do frontend Vue.js

- **Banco de Dados:**
  - `flyway-manage.ps1` - Gestão de migrações Flyway

### `scr/tests/` - **Scripts de Teste** 🧪
**TODOS OS TESTES FORAM MOVIDOS PARA ESTA PASTA**

Ver documentação completa em: [`scr/tests/README.md`](tests/README.md)

#### **Scripts Principais:**
- `test-all-profiles.ps1` - **⭐ Teste completo de todos os perfis**
- `test-all-endpoints.ps1` - Teste de todos os endpoints
- `test-frontend-scenarios.ps1` - Cenários frontend + backend
- `quick-test.ps1` - Teste rápido de conectividade

#### **Testes por Módulo:**
- `test-users.ps1`, `test-couriers.ps1`, `test-customers.ps1`
- `test-deliveries.ps1`, `test-teams.ps1`, `test-sms.ps1`
- `test-login.ps1`, `test-jwt-security.ps1`

#### **Utilitários:**
- `health-check-endpoints.ps1` - Verificação de saúde
- `jwt-utility.ps1` - Manipulação de tokens JWT

## 🚀 **Como Executar**

### **Para Testes Completos:**
```powershell
cd scr/tests
.\test-all-profiles.ps1 -Verbose
```

### **Para Deploy:**
```powershell
cd scr
.\deploy-wildfly-31.ps1
```

### **Para Configuração Inicial:**
```powershell
cd scr
.\config-wildfly-31.ps1
.\start-wildfly-31.ps1
```

## 🔐 **Sistema de Autorização**

Todos os testes validam o **sistema completo de autorização** implementado:

- **JWT Authentication Filter** - Validação de tokens
- **Authorization Filter** - Controle por roles (@RolesAllowed)
- **4 Perfis**: ADMIN, BUSINESS, COURIER, CUSTOMER
- **Proteção completa** de todos os endpoints REST

## 📚 **Documentação Adicional**

- **Testes**: [`scr/tests/README.md`](tests/README.md)
- **Arquitetura**: [`doc/SISTEMA_AUTORIZACAO.md`](../doc/SISTEMA_AUTORIZACAO.md)
- **Configuração**: [`doc/CONFIG_WILDFLY.md`](../doc/CONFIG_WILDFLY.md)
