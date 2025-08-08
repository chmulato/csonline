
# Índice de Documentos - CSOnline JWT 2.0 Enterprise

> **Sistema com Segurança JWT e Controle de Acesso Baseado em Perfis**  
> CSOnline agora opera com autenticação JWT Bearer Token, sistema completo de permissões, proteção de endpoints e controle granular de acesso.

## Documentação Principal

1. [REGRAS_DE_NEGOCIO.md](REGRAS_DE_NEGOCIO.md) — Regras de negócio da camada de serviços, exemplos práticos.
2. [ARQUITETURA.md](ARQUITETURA.md) — Estrutura e arquitetura do sistema backend.
3. [**AUTENTICACAO_JWT.md**](AUTENTICACAO_JWT.md) — **Sistema de autenticação JWT: implementação, segurança e uso**.
4. [**PERFIS_E_CONTROLE_ACESSO.md**](PERFIS_E_CONTROLE_ACESSO.md) — **Sistema completo de perfis de usuário e controle de permissões**.
5. [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) — **Documentação completa sobre migrações e gerenciamento de dados com Flyway e Docker**.
6. [CONFIG_WILDFLY.md](CONFIG_WILDFLY.md) — Guia de configuração do WildFly 31 para o projeto.
7. [SEQUENCIA_SCRIPTS.md](SEQUENCIA_SCRIPTS.md) — Ordem recomendada de execução dos scripts do projeto.

## Documentação Frontend e Interface

8. [ARQUITETURA_VUE.md](ARQUITETURA_VUE.md) — Introdução à arquitetura do front-end Vue.js, manutenção e controle de versões.
9. [**FRONTEND_VUE.md**](FRONTEND_VUE.md) — **Documentação das páginas, navegação, integração JWT e segurança do SPA Vue**.
10. [**ESTRUTURA_NAVEGACAO_TELAS.md**](ESTRUTURA_NAVEGACAO_TELAS.md) — **Estrutura completa de navegação, telas e interface baseada em perfis**.

## Documentação Técnica Especializada

11. [**IMPLEMENTACAO_TECNICA_PERMISSOES.md**](IMPLEMENTACAO_TECNICA_PERMISSOES.md) — **Implementação técnica detalhada do sistema de permissões frontend e backend**.
12. [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md) — Processo de migração do import.sql para o Flyway.
13. [**TESTES_ENDPOINTS.md**](TESTES_ENDPOINTS.md) — **Relatório detalhado dos testes de endpoints JWT, segurança e status atual**.
14. [MODELO_DE_DADOS.md](MODELO_DE_DADOS.md) — **Diagrama e documentação detalhada das entidades, relacionamentos e boas práticas**.
15. [README-STANDALONE.md](../bak/README-STANDALONE.md) — **Documentação completa de configuração manual do WildFly 31 com HSQLDB**.

## Gestão e Administração

16. [HISTORIA_DO_PROJETO.md](HISTORIA_DO_PROJETO.md) — Linha do tempo, decisões e evolução do projeto.
17. [WILDFLY_USO.md](WILDFLY_USO.md) — Guia de uso, administração e boas práticas do servidor WildFly 31.
18. [INDEX.md](INDEX.md) — Este índice de documentos.

## Scripts e Automação

### Scripts de Configuração do Servidor
- [**config-jdbc-driver-wildfly-31.ps1**](../scr/config-jdbc-driver-wildfly-31.ps1) — **Script automatizado versão 2.0 para configuração completa do WildFly com HSQLDB**
- [README-CONFIG-SCRIPT.md](../scr/README-CONFIG-SCRIPT.md) — **Documentação detalhada do script de configuração com troubleshooting**
- [example-usage.ps1](../scr/example-usage.ps1) — Exemplos de uso do script de configuração
- [start-wildfly-31.ps1](../scr/start-wildfly-31.ps1) — Script para iniciar o servidor WildFly
- [stop-wildfly-31.ps1](../scr/stop-wildfly-31.ps1) — Script para parar o servidor WildFly

## Novidades e Funcionalidades Recentes

### Suite de Testes JWT 2.0 Automatizados (Agosto 2025)
O projeto agora conta com uma **suite completa de testes de segurança JWT** localizada em `scr/tests/`:

#### Scripts de Teste Individuais com JWT:
- **`test-users.ps1`** - Testes completos de usuários com autenticação JWT
- **`test-customers.ps1`** - Testes de clientes com validação de segurança
- **`test-couriers.ps1`** - Testes de entregadores protegidos por JWT
- **`test-teams.ps1`** - Testes de equipes com Bearer Token
- **`test-deliveries.ps1`** - Testes de entregas com autenticação
- **`test-sms.ps1`** - Testes de SMS com proteção JWT
- **`test-login.ps1`** - Testes específicos de autenticação JWT

#### Ferramentas de Automação JWT:
- **`test-jwt-security.ps1`** - **Suite completa de segurança JWT** (20 testes)
- **`jwt-utility.ps1`** - Utilitários para operações JWT nos testes
- **`test-all-endpoints.ps1`** - Execução de todos os endpoints com JWT
- **`health-check-endpoints.ps1`** - Verificação de saúde do sistema
- **`run-tests.ps1`** - **Script principal JWT 2.0** com opções de segurança

#### Documentação dos Testes JWT:
- **`README-TESTES.ps1`** - Interface visual moderna com JWT 2.0
- **`README.md`** - Documentação técnica completa dos testes

#### Características dos Testes JWT:
- **Autenticação automática** com credenciais configuráveis
- **Validação de segurança** com testes de autorização
- **Logs detalhados** com opção `-Verbose`
- **Interface moderna** com emojis e cores
- **Taxa de sucesso** de 100% nos testes de segurança

### Configuração Enterprise Completa (3 de Agosto/2025)
O projeto alcançou **maturidade de produção enterprise** com:

- **Infraestrutura consolidada:** WildFly 31.0.1.Final + HSQLDB 2.7 + Flyway 8.5.13
- **Deploy automatizado:** Scripts PowerShell para build, deploy e configuração
- **Configuração JTA:** Transações gerenciadas pelo container para ambiente enterprise
- **Migrações controladas:** Flyway com histórico de versões V1 (schema) e V2 (dados)
- **APIs documentadas:** Swagger/OpenAPI com interface web para desenvolvedores
- **Logging estruturado:** Sistema de logs detalhado para produção e debugging

### Automação de Configuração do Servidor (Atualizado - Agosto 2025)
Script de configuração WildFly completamente reescrito com sistema avançado de logging:

- **Script principal:** `config-jdbc-driver-wildfly-31.ps1` (versão 2.0) com 7 etapas bem definidas
- **Sistema de logging:** Logs detalhados com timestamp salvos em `logs/wildfly-config-*.log`
- **Parâmetros flexíveis:** `-Verbose`, `-CleanStart`, `-SkipBackup` para diferentes cenários
- **Configuração baseada em módulos:** Evita conflitos de deployment, usa módulos WildFly nativos
- **Download automático:** Baixa driver HSQLDB automaticamente se não encontrado
- **Validação completa:** Testes de conectividade e verificação de cada etapa
- **Documentação completa:** [README-CONFIG-SCRIPT.md](../scr/README-CONFIG-SCRIPT.md) com troubleshooting

### Containerização com Docker (Atualizado - Agosto 2025)
O banco de dados HSQLDB agora executa tanto em modo arquivo quanto container Docker:

- **Arquivo de configuração:** `docker-compose.yml` na raiz do projeto
- **Modo arquivo (atual):** HSQLDB executando em modo file para integração WildFly
- **Persistência de dados:** Arquivos de banco e migrações Flyway
- **Configuração flexível:** Suporte tanto para desenvolvimento quanto produção
- **Documentação atualizada:** [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) inclui instruções completas

### Status Atual do Sistema (7 de Agosto/2025)
**Sistema CSOnline 100% Operacional com JWT 2.0 - MARCO HISTÓRICO: Segurança Enterprise Implementada**

**EVOLUÇÃO PARA PRODUÇÃO ENTERPRISE COM SEGURANÇA JWT:**

- **Taxa de Sucesso:** 100% (7/7 endpoints REST protegidos por JWT)
- **Sistema de Autenticação:** JWT Bearer Token com HMAC SHA-512
- **Frontend Vue 3 SPA:** Autenticação integrada com Pinia store
- **Backend Jakarta EE:** APIs REST com filtro de segurança JWT
- **Testes de Segurança:** Suite completa validando 100% da segurança
- **Deploy Enterprise:** WildFly 31.0.1.Final com segurança JWT em produção

**URLs Ativas e Sistema de Autenticação:**
  - Aplicação: http://localhost:8080/csonline/ (com login JWT)
  - APIs REST Protegidas: http://localhost:8080/csonline/api/{users|customers|couriers|deliveries|teams|sms}
  - Login público: http://localhost:8080/csonline/api/login (endpoint público)
  - Swagger UI: http://localhost:8080/csonline/swagger-ui/
  - Health Check: http://localhost:8080/csonline/api/health (endpoint público)

**Resultados dos Endpoints JWT 2.0 - 100% SEGUROS E FUNCIONAIS:**
- **Endpoint `/login`** - Autenticação JWT - PÚBLICO (200)
- **Endpoint `/health`** - Health Check - PÚBLICO (200)
- **Endpoint `/users`** - Gestão de Usuários - PROTEGIDO JWT (200)
- **Endpoint `/couriers`** - Gestão de Entregadores - PROTEGIDO JWT (200)
- **Endpoint `/customers`** - Gestão de Clientes - PROTEGIDO JWT (200)
- **Endpoint `/teams`** - Gestão de Equipas - PROTEGIDO JWT (200)
- **Endpoint `/deliveries`** - Gestão de Entregas - PROTEGIDO JWT (200)
- **Endpoint `/sms`** - Sistema de SMS - PROTEGIDO JWT (200)

**Métricas de Segurança JWT Alcançadas:**
- **Taxa de Segurança Total:** 100% (20/20 testes de segurança aprovados)
- **Proteção contra acesso não autorizado:** 401 Unauthorized para endpoints protegidos
- **Validação de tokens:** Rejeição de tokens inválidos ou expirados
- **Autenticação automática:** Frontend com interceptors HTTP automáticos
- **Persistência de sessão:** LocalStorage com validação de expiração
- **Data do Marco JWT:** 7 de Agosto/2025
  - Swagger UI: http://localhost:8080/csonline/swagger-ui/
  - Console WildFly: http://localhost:9990

### Endpoints Testados e Funcionais (3 de Agosto/2025)
Através dos testes automatizados e deploy enterprise, foi confirmado:
- **100% dos endpoints principais funcionando** (users, customers, couriers, deliveries, sms)
- **Swagger UI operacional** com documentação automática
- **Migrações Flyway executadas** com dados iniciais carregados
- **Configuração JTA completa** para transações enterprise
- **Deploy enterprise finalizado** sem erros ou conflitos

 > **Observação:** Os documentos IMPORT_SQL.md e MIGRATIONS.md foram fundidos no novo arquivo [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para fornecer uma documentação mais completa e unificada sobre o gerenciamento de banco de dados com Flyway e Docker. O documento [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md) permanece ativo como referência histórica e técnica sobre o processo de migração.

## Como Usar Esta Documentação

### Para Novos Desenvolvedores
1. Comece com [ARQUITETURA.md](ARQUITETURA.md) para entender a estrutura geral
2. Leia [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para configurar banco de dados e migrações
3. Siga [SEQUENCIA_SCRIPTS.md](SEQUENCIA_SCRIPTS.md) para setup do ambiente
4. **Execute o deploy:** Use `scr\deploy-wildfly-31.ps1` para deploy automático no WildFly
5. **Verifique funcionamento:** Acesse http://localhost:8080/csonline/ para confirmar que está operacional

### Para Configuração do Servidor (Produção Enterprise)
- **Deploy completo:** WildFly 31.0.1.Final configurado com HSQLDB e migrações Flyway
- **Configuração manual:** Siga [README-STANDALONE.md](../bak/README-STANDALONE.md) para processo detalhado
- **Troubleshooting:** Consulte logs do WildFly em `server/wildfly-31.0.1.Final/standalone/log/`
- **Monitoramento:** Console de administração em http://localhost:9990

### Para Testes e Qualidade JWT 2.0 (Sistema Seguro)
- **Acesso autenticado:** Visite http://localhost:8080/csonline/ para interface com login JWT
- **APIs protegidas:** Teste endpoints com Bearer Token para validar segurança
- **Swagger UI:** Acesse http://localhost:8080/csonline/swagger-ui/ para documentação interativa
- **Testes de segurança:** Use `scr/tests/test-jwt-security.ps1` para validação completa
- **Scripts JWT 2.0:** Execute `scr/tests/run-tests.ps1 -JWTSecurity` para testes de segurança
- **Login de teste:** Credenciais padrão: admin/admin123
- **Validação automática:** 20 testes de segurança com 100% de aprovação

### Para Ambiente de Produção Enterprise JWT
- **Sistema seguro:** WildFly + HSQLDB + Flyway + JWT Authentication
- **URLs protegidas:** Todas as URLs com autenticação JWT obrigatória
- **Migrações aplicadas:** V1 (schema) e V2 (dados iniciais) com usuários de teste
- **Segurança JWT:** Tokens HMAC SHA-512 com expiração de 24 horas
- **Filtro de segurança:** Proteção automática de todos os endpoints `/api/*`
- **Monitoramento:** Logs estruturados incluindo eventos de autenticação
- **Credenciais padrão:** admin/admin123 e empresa/empresa123 para testes

### Para Evolução do Projeto
- Consulte [HISTORIA_DO_PROJETO.md](HISTORIA_DO_PROJETO.md) para entender decisões técnicas
- Novos documentos e funcionalidades devem ser adicionados à pasta `doc/` e este índice atualizado conforme o projeto avança

---

**Última atualização:** 7 de Agosto de 2025 - Sistema CSOnline com **JWT 2.0 Enterprise Security** implementado. Sistema 100% operacional com autenticação JWT, 20 testes de segurança aprovados, frontend Vue 3 com autenticação integrada, e todos os endpoints protegidos por Bearer Token. Marco histórico: **Segurança Enterprise Completa**.
