# Gerenciamento de Banco de Dados CSOnline HSQLDB

**Versão:** 1.0  
**Data:** 07 de agosto de 2025  
**Sistema:** CSOnline JWT 2.0  
**Banco:** HSQLDB (In-Memory)  

## Visão Geral

Este diretório contém scripts PowerShell para gerenciamento completo do banco de dados HSQLDB do sistema CSOnline, incluindo operações de backup, restauração e reset através da API REST com autenticação JWT.

## Arquivos do Sistema

### Scripts Principais

| Script | Função | Descrição |
|--------|--------|-----------|
| `export-database.ps1` | Backup | Exporta todos os dados via API REST |
| `import-database-advanced.ps1` | Restauração | Importa dados com parse avançado |
| `reset-and-import.ps1` | Reset Completo | Limpa e restaura banco de dados |
| `README-SCRIPTS.ps1` | Documentação | Guia de uso e exemplos |

### Arquivos de Dados

| Arquivo | Função | Descrição |
|---------|--------|-----------|
| `database-export.sql` | Backup SQL | Arquivo gerado pelo export |
| `*.log` | Logs | Histórico de operações |

## Características dos Scripts

### Funcionalidades Avançadas

- **Autenticação JWT**: Integração completa com sistema de segurança
- **Modo Dry-Run**: Simulação segura antes da execução
- **Logging Detalhado**: Rastreamento completo de operações
- **Validação de Dados**: Verificação de integridade antes/depois
- **Tratamento de Erros**: Gestão robusta de exceções
- **Backup Automático**: Proteção de dados antes de operações destrutivas

### Segurança

- **Tokens JWT**: Autenticação obrigatória para todas as operações
- **Validação de Servidor**: Verificação de conectividade antes da execução
- **Confirmações**: Proteção contra execução acidental
- **Logs de Auditoria**: Registro de todas as ações realizadas

## Casos de Uso

### 1. Backup Regular de Dados

```powershell
# Exportar todos os dados atuais
.\export-database.ps1 -Verbose

# Resultado: arquivo database-export.sql
```

**Cenário**: Backup antes de atualizações ou manutenção  
**Frequência**: Recomendado antes de mudanças no sistema  
**Dados**: Todos os registros de todas as tabelas  

### 2. Análise de Dados

```powershell
# Analisar estrutura do backup sem modificar dados
.\import-database-advanced.ps1 -ExportFile "database-export.sql" -DryRun -Verbose
```

**Cenário**: Auditoria e análise de dados  
**Resultado**: Relatório detalhado sem alterações  
**Uso**: Verificação de integridade e estrutura  

### 3. Reset para Desenvolvimento

```powershell
# Reset completo com simulação
.\reset-and-import.ps1 -DryRun -Verbose

# Reset real após confirmação
.\reset-and-import.ps1 -ForceReset -Verbose
```

**Cenário**: Ambiente de desenvolvimento  
**Cuidado**: Operação destrutiva  
**Recomendação**: Sempre usar DryRun primeiro  

### 4. Migração de Ambiente

```powershell
# Origem: Exportar dados
.\export-database.ps1

# Destino: Importar dados
.\import-database-advanced.ps1 -ExportFile "database-export.sql"
```

**Cenário**: Transferência entre ambientes  
**Pré-requisito**: Banco destino vazio ou compatível  
**Validação**: Verificação automática de dados  

## Estrutura do Banco de Dados

### Tabelas Gerenciadas

| Tabela | Registros Típicos | Dependências |
|--------|------------------|--------------|
| `users` | 8+ | Base do sistema |
| `teams` | 2+ | Independente |
| `customers` | 2+ | Referencia users |
| `couriers` | 2+ | Referencia users |
| `deliveries` | 2+ | Referencia customers/couriers |
| `sms` | 2+ | Referencia users |

### Relacionamentos

- **Users**: Tabela central do sistema
- **Customers/Couriers**: Extendem users com perfis específicos
- **Deliveries**: Conecta customers e couriers
- **SMS**: Log de comunicações
- **Teams**: Agrupamento de usuários

## Resultados dos Testes

### Teste Completo Realizado em 07/08/2025

**Estado Inicial**: 18 registros (8 users, 2 couriers, 2 customers, 2 deliveries, 2 teams, 2 sms)

#### Resultados por Script

| Operação | Status | Resultado |
|----------|--------|-----------|
| Export Database | Sucesso | 18 registros exportados (5.69 KB) |
| Import Dry-Run | Sucesso | Parse completo, 6 tabelas identificadas |
| Reset Parcial | Parcial | 2 registros deletados (teams) |
| Import Advanced | Limitado | Conflitos de duplicação detectados |

#### Limitações Identificadas

1. **Integridade Referencial**: Dependências impedem reset completo
2. **IDs Sequenciais**: Sistema não permite reset com dados existentes
3. **Conflitos de Duplicação**: Usuários existentes bloqueiam reimportação

#### Casos de Sucesso

- **Backup via API**: 100% funcional
- **Parse de dados**: Análise completa
- **Simulação**: Dry-run modes funcionais
- **Autenticação JWT**: Integração perfeita

## Configuração do Ambiente

### Pré-requisitos

- **PowerShell 5.1+**: Para execução dos scripts
- **WildFly 31.0.1**: Servidor de aplicação em execução
- **CSOnline API**: Sistema rodando em localhost:8080
- **Credenciais**: Login 'empresa' com permissões adequadas

### Variáveis de Ambiente

```powershell
$serverUrl = "http://localhost:8080/csonline/api"
$defaultLogin = "empresa"
$defaultPassword = "empresa123"
```

### Verificação do Sistema

```powershell
# Testar conectividade
curl http://localhost:8080/csonline/api/login -Method POST

# Verificar autenticação JWT
cd ..\scr\tests
.\test-login.ps1
```

## Troubleshooting

### Problemas Comuns

**Erro 401 (Unauthorized)**
- Verificar credenciais de login
- Confirmar que o servidor está rodando
- Validar token JWT não expirado

**Erro 409 (Conflict)**
- Dados já existem no banco
- Usar reset antes da importação
- Verificar IDs duplicados

**Erro 500 (Internal Server Error)**
- Verificar logs do WildFly
- Confirmar estrutura dos dados
- Validar dependências de tabelas

### Logs e Debugging

- **Logs dos Scripts**: Consultar arquivos .log gerados
- **Logs do WildFly**: `server/wildfly-31.0.1.Final/standalone/log/`
- **Modo Verbose**: Usar -Verbose para mais detalhes
- **Dry-Run**: Sempre testar antes da execução real

## Recomendações de Uso

### Para Produção

1. **Sempre fazer backup** antes de operações destrutivas
2. **Usar modo dry-run** para validar operações
3. **Verificar logs** após cada execução
4. **Testar em ambiente** de desenvolvimento primeiro

### Para Desenvolvimento

1. **Reset regular** para testes limpos
2. **Backup de estados** conhecidos bons
3. **Simulação** antes de mudanças estruturais
4. **Validação** de dados após importação

### Para Migração

1. **Export completo** do ambiente origem
2. **Preparar ambiente** destino vazio
3. **Import com validação** de integridade
4. **Testes funcionais** após migração

## Suporte e Manutenção

### Contato

- **Projeto**: CSOnline JWT 2.0
- **Repositório**: GitHub - chmulato/csonline
- **Branch**: main

### Atualizações

- **Versão dos Scripts**: Verificar cabeçalhos dos arquivos .ps1
- **Compatibilidade**: Testado com WildFly 31.0.1.Final
- **Última Atualização**: 07 de agosto de 2025

---

**Documento gerado automaticamente pelo sistema CSOnline**  
**Para mais informações, consulte README-SCRIPTS.ps1**
