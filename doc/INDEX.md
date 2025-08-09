
# Índice de Documentos - CSOnline

Guia prático de configuração, operação e testes. Sistema protegido por JWT com controle de acesso baseado em perfis.

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
13. [TESTES.md](TESTES.md) — Relatório unificado de testes (unitários, repositórios, segurança JWT e endpoints) substituindo documentos anteriores.
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

## Referências Rápidas

### Suite de Testes (JWT e Endpoints)
Local: `scr/tests/`
- `test-users.ps1`, `test-customers.ps1`, `test-couriers.ps1`, `test-teams.ps1`, `test-deliveries.ps1`, `test-sms.ps1`, `test-login.ps1`
- Ferramentas: `test-jwt-security.ps1` (20 testes), `jwt-utility.ps1`, `test-all-endpoints.ps1`, `health-check-endpoints.ps1`, `run-tests.ps1`
- Documento unificado de testes: [TESTES.md](TESTES.md)

### URLs Úteis
- Aplicação: http://localhost:8080/csonline/
- APIs REST: http://localhost:8080/csonline/api/*
- Login (público): http://localhost:8080/csonline/api/login
- Health (público): http://localhost:8080/csonline/api/health
- Swagger UI: http://localhost:8080/csonline/swagger-ui/
- Console WildFly: http://localhost:9990

Observação: Endpoints protegidos exigem JWT. Algumas rotas exigem roles específicas (por exemplo, GET de usuários requer ADMIN).

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

**Última atualização:** 9 de Agosto de 2025. Sistema com autenticação JWT ativa, RBAC aplicado em endpoints específicos e suíte de testes automatizados disponível em `scr/tests/`.
