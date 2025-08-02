
# Índice de Documentos

## Documentação Principal

1. [REGRAS_DE_NEGOCIO.md](REGRAS_DE_NEGOCIO.md) — Regras de negócio da camada de serviços, exemplos práticos.
2. [ARQUITETURA.md](ARQUITETURA.md) — Estrutura e arquitetura do sistema backend.
3. [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) — **Documentação completa sobre migrações e gerenciamento de dados com Flyway e Docker**.
4. [CONFIG_WILDFLY.md](CONFIG_WILDFLY.md) — Guia de configuração do WildFly 31 para o projeto.
5. [SEQUENCIA_SCRIPTS.md](SEQUENCIA_SCRIPTS.md) — Ordem recomendada de execução dos scripts do projeto.

## Documentação Frontend

6. [ARQUITETURA_VUE.md](ARQUITETURA_VUE.md) — Introdução à arquitetura do front-end Vue.js, manutenção e controle de versões.
7. [FRONTEND_VUE.md](FRONTEND_VUE.md) — Documentação das páginas, navegação, integração e segurança do SPA Vue.

## Gestão e Administração

8. [HISTORIA_DO_PROJETO.md](HISTORIA_DO_PROJETO.md) — Linha do tempo, decisões e evolução do projeto.
9. [WILDFLY_USO.md](WILDFLY_USO.md) — Guia de uso, administração e boas práticas do servidor WildFly 31.

## Documentação Técnica Especializada

10. [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md) — Processo de migração do import.sql para o Flyway.
11. [INDEX.md](INDEX.md) — Este índice de documentos.

## Novidades e Funcionalidades Recentes

### Suite de Testes Automatizados (Agosto 2025)
O projeto agora conta com uma **suite completa de testes automatizados** localizada em `scr/tests/`:

- **Scripts individuais:** `test-users.ps1`, `test-customers.ps1`, `test-couriers.ps1`, `test-teams.ps1`, `test-deliveries.ps1`, `test-sms.ps1`, `test-login.ps1`
- **Ferramentas de automação:** `test-all-endpoints.ps1`, `health-check-endpoints.ps1`, `run-tests.ps1`
- **Documentação dos testes:** `README-TESTES.ps1` e `README.md` na pasta de testes

### Containerização com Docker (Agosto 2025)
O banco de dados HSQLDB agora executa em container Docker:

- **Arquivo de configuração:** `docker-compose.yml` na raiz do projeto
- **Persistência de dados:** Volume `hsqldb-data/` para manter dados entre reinicializações
- **Configuração simplificada:** Um comando para ter o banco funcionando
- **Documentação atualizada:** [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) inclui instruções Docker

### Status Atual dos Endpoints
Através dos testes automatizados, foi identificado:
- ✅ **50% dos endpoints funcionando** perfeitamente
- ❌ **50% dos endpoints com problemas** catalogados e prontos para correção
- **Diagnóstico detalhado** disponível via scripts de verificação de saúde

 > **Observação:** Os documentos IMPORT_SQL.md e MIGRATIONS.md foram fundidos no novo arquivo [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para fornecer uma documentação mais completa e unificada sobre o gerenciamento de banco de dados com Flyway e Docker. O documento [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md) permanece ativo como referência histórica e técnica sobre o processo de migração.

## Como Usar Esta Documentação

### Para Novos Desenvolvedores
1. Comece com [ARQUITETURA.md](ARQUITETURA.md) para entender a estrutura geral
2. Leia [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para configurar Docker e banco de dados
3. Siga [SEQUENCIA_SCRIPTS.md](SEQUENCIA_SCRIPTS.md) para setup do ambiente
4. Use a suite de testes em `scr/tests/` para verificar se tudo está funcionando

### Para Testes e Qualidade
- Execute `.\run-tests.ps1 -HealthCheck` para verificação rápida
- Use `.\run-tests.ps1` para testes completos
- Consulte `scr/tests/README.md` para documentação detalhada dos testes

### Para Ambiente Docker
- Execute `docker-compose up -d` para iniciar o banco de dados
- Siga as instruções em [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para configuração completa

### Para Evolução do Projeto
- Consulte [HISTORIA_DO_PROJETO.md](HISTORIA_DO_PROJETO.md) para entender decisões técnicas
- Novos documentos e funcionalidades devem ser adicionados à pasta `doc/` e este índice atualizado conforme o projeto avança

---

**Última atualização:** Agosto 2025 - Inclusão da suite de testes automatizados e containerização com Docker.
