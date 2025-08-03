
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
11. [TESTES_ENDPOINTS.md](TESTES_ENDPOINTS.md) — **Relatório detalhado dos testes de endpoints, status atual e correções implementadas**.
12. [MODELO_DE_DADOS.md](MODELO_DE_DADOS.md) — **Diagrama e documentação detalhada das entidades, relacionamentos e boas práticas**.
13. [README-STANDALONE.md](../bak/README-STANDALONE.md) — **Documentação completa de configuração manual do WildFly 31 com HSQLDB**.
14. [INDEX.md](INDEX.md) — Este índice de documentos.

## Scripts e Automação

### Scripts de Configuração do Servidor
- [**config-jdbc-driver-wildfly-31.ps1**](../scr/config-jdbc-driver-wildfly-31.ps1) — **Script automatizado versão 2.0 para configuração completa do WildFly com HSQLDB**
- [README-CONFIG-SCRIPT.md](../scr/README-CONFIG-SCRIPT.md) — **Documentação detalhada do script de configuração com troubleshooting**
- [example-usage.ps1](../scr/example-usage.ps1) — Exemplos de uso do script de configuração
- [start-wildfly-31.ps1](../scr/start-wildfly-31.ps1) — Script para iniciar o servidor WildFly
- [stop-wildfly-31.ps1](../scr/stop-wildfly-31.ps1) — Script para parar o servidor WildFly

## Novidades e Funcionalidades Recentes

### Suite de Testes Automatizados (Agosto 2025)
O projeto agora conta com uma **suite completa de testes automatizados** localizada em `scr/tests/`:

- **Scripts individuais:** `test-users.ps1`, `test-customers.ps1`, `test-couriers.ps1`, `test-teams.ps1`, `test-deliveries.ps1`, `test-sms.ps1`, `test-login.ps1`
- **Ferramentas de automação:** `test-all-endpoints.ps1`, `health-check-endpoints.ps1`, `run-tests.ps1`
- **Documentação dos testes:** `README-TESTES.ps1` e `README.md` na pasta de testes

### Automação de Configuração do Servidor (Agosto 2025)
Script de configuração WildFly completamente reescrito com sistema avançado de logging:

- **Script principal:** `config-jdbc-driver-wildfly-31.ps1` (versão 2.0) com 7 etapas bem definidas
- **Sistema de logging:** Logs detalhados com timestamp salvos em `logs/wildfly-config-*.log`
- **Parâmetros flexíveis:** `-Verbose`, `-CleanStart`, `-SkipBackup` para diferentes cenários
- **Configuração baseada em módulos:** Evita conflitos de deployment, usa módulos WildFly nativos
- **Download automático:** Baixa driver HSQLDB automaticamente se não encontrado
- **Validação completa:** Testes de conectividade e verificação de cada etapa
- **Documentação completa:** [README-CONFIG-SCRIPT.md](../scr/README-CONFIG-SCRIPT.md) com troubleshooting

### Containerização com Docker (Agosto 2025)
O banco de dados HSQLDB agora executa em container Docker:

- **Arquivo de configuração:** `docker-compose.yml` na raiz do projeto
- **Persistência de dados:** Volume `hsqldb-data/` para manter dados entre reinicializações
- **Configuração simplificada:** Um comando para ter o banco funcionando
- **Documentação atualizada:** [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) inclui instruções Docker

### Status Atual dos Endpoints (Agosto 2025)
Através dos testes automatizados e diagnóstico detalhado, foi identificado:
- **80% dos endpoints funcionando** perfeitamente (8/10 endpoints)
- **20% dos endpoints com problemas** catalogados e em correção
- **Deliveries endpoint recuperado** com implementação de DTO
- **Problemas de serialização resolvidos** com anotações Jackson
- **Deploy 100% funcional** no WildFly 31.0.1.Final
- **Documentação completa** disponível em [TESTES_ENDPOINTS.md](TESTES_ENDPOINTS.md)

 > **Observação:** Os documentos IMPORT_SQL.md e MIGRATIONS.md foram fundidos no novo arquivo [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para fornecer uma documentação mais completa e unificada sobre o gerenciamento de banco de dados com Flyway e Docker. O documento [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md) permanece ativo como referência histórica e técnica sobre o processo de migração.

## Como Usar Esta Documentação

### Para Novos Desenvolvedores
1. Comece com [ARQUITETURA.md](ARQUITETURA.md) para entender a estrutura geral
2. Leia [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md) para configurar Docker e banco de dados
3. Siga [SEQUENCIA_SCRIPTS.md](SEQUENCIA_SCRIPTS.md) para setup do ambiente
4. **Use o script automatizado:** Execute `pwsh .\scr\config-jdbc-driver-wildfly-31.ps1` para configurar WildFly
5. Use a suite de testes em `scr/tests/` para verificar se tudo está funcionando

### Para Configuração do Servidor
- **Configuração automática:** Use `scr/config-jdbc-driver-wildfly-31.ps1` com logging completo
- **Configuração manual:** Siga [README-STANDALONE.md](../bak/README-STANDALONE.md) para processo passo-a-passo
- **Troubleshooting:** Consulte [README-CONFIG-SCRIPT.md](README-CONFIG-SCRIPT.md) para resolver problemas
- **Logs detalhados:** Verifique `logs/wildfly-config-*.log` para diagnóstico

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

**Última atualização:** Agosto 2025 - Inclusão da suite de testes automatizados, script de configuração WildFly 2.0 com sistema avançado de logging, e containerização com Docker.
