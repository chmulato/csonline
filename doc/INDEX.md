
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

### Status Atual do Sistema (3 de Agosto/2025)
**Sistema CSOnline 100% Operacional em Produção**

- **WildFly 31.0.1.Final:** Servidor de aplicação configurado e funcionando
- **HSQLDB 2.7:** Banco de dados integrado com migrações Flyway completas
- **Aplicação WAR:** Deploy realizado com sucesso, todas as APIs funcionais
- **URLs Ativas:**
  - Aplicação: http://localhost:8080/csonline/
  - APIs REST: http://localhost:8080/csonline/api/{users|customers|couriers|deliveries|teams|sms}
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

### Para Testes e Qualidade (Sistema Operacional)
- **Acesso direto:** Visite http://localhost:8080/csonline/ para interface principal
- **APIs funcionais:** Teste http://localhost:8080/csonline/api/users para verificar APIs
- **Swagger UI:** Acesse http://localhost:8080/csonline/swagger-ui/ para documentação interativa
- **Scripts de teste:** Use `scr/tests/` para validação automatizada (opcional)

### Para Ambiente de Produção Enterprise
- **Sistema operacional:** WildFly + HSQLDB + Flyway totalmente configurados
- **URLs de produção:** Todas as URLs funcionais e testadas
- **Migrações aplicadas:** V1 (schema) e V2 (dados iniciais) executadas com sucesso
- **Monitoramento:** Logs estruturados e console de administração disponíveis

### Para Evolução do Projeto
- Consulte [HISTORIA_DO_PROJETO.md](HISTORIA_DO_PROJETO.md) para entender decisões técnicas
- Novos documentos e funcionalidades devem ser adicionados à pasta `doc/` e este índice atualizado conforme o projeto avança

---

**Última atualização:** 3 de Agosto de 2025 - Sistema CSOnline 100% operacional em produção enterprise com WildFly 31.0.1.Final, HSQLDB 2.7, migrações Flyway completas e todas as APIs funcionais. Deploy enterprise consolidado e testado.
