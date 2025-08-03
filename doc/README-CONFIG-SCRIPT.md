# Script de Configuração do WildFly 31 - Documentação

## Visão Geral

O script `config-jdbc-driver-wildfly-31.ps1` foi completamente reescrito para fornecer uma configuração robusta e bem documentada do servidor WildFly 31 com HSQLDB.

## Melhorias da Versão 2.0

### Novo Sistema de Logging
- **Logs Detalhados**: Cada operação é registrada com timestamp
- **Arquivo de Log**: Logs salvos em `logs/wildfly-config-YYYYMMDD-HHMMSS.log`
- **Cores no Console**: Diferentes níveis de log com cores distintas
- **Rastreamento Completo**: Histórico completo de todas as operações

### Etapas Claramente Definidas
1. **Pré-requisitos**: Verificação completa do ambiente
2. **Backup**: Backup automático da configuração
3. **Limpeza**: Remoção de deployments conflitantes
4. **Módulo HSQLDB**: Configuração correta usando módulos
5. **Datasource**: Configuração via CLI com validação
6. **Deploy**: Deploy automático da aplicação
7. **Verificação**: Testes de conectividade e status

### Configuração Baseada em Módulos
- **Não Usa Deployments**: Evita conflitos de deployment duplicado
- **Módulo Dedicado**: Cria módulo `org.hsqldb` adequadamente
- **Download Automático**: Baixa driver se não encontrado
- **Configuração Limpa**: Remove configurações antigas antes de aplicar novas

### Parâmetros Opcionais
```powershell
# Uso básico
pwsh .\config-jdbc-driver-wildfly-31.ps1

# Com verbose (mostra saída detalhada dos comandos)
pwsh .\config-jdbc-driver-wildfly-31.ps1 -Verbose

# Com limpeza completa (remove cache)
pwsh .\config-jdbc-driver-wildfly-31.ps1 -CleanStart

# Sem backup (para testes)
pwsh .\config-jdbc-driver-wildfly-31.ps1 -SkipBackup

# Combinando parâmetros
pwsh .\config-jdbc-driver-wildfly-31.ps1 -Verbose -CleanStart
```

## Pré-requisitos

### Obrigatórios
- **WildFly 31.0.1.Final** em `server/wildfly-31.0.1.Final/`
- **Java JDK 11+** configurado no PATH
- **WildFly em execução** (o script precisa do CLI ativo)

### Opcionais
- **Aplicação WAR** em `target/csonline.war` (deploy automático)
- **Driver HSQLDB** em `target/csonline/WEB-INF/lib/` (senão baixa automaticamente)

## Como Usar

### 1. Iniciar o WildFly
```powershell
# Primeiro inicie o WildFly
pwsh .\scr\start-wildfly-31.ps1
```

### 2. Executar Configuração
```powershell
# Navegar para o diretório do script
cd scr

# Executar configuração básica
pwsh .\config-jdbc-driver-wildfly-31.ps1

# Ou com verbose para debug
pwsh .\config-jdbc-driver-wildfly-31.ps1 -Verbose
```

### 3. Verificar Resultado
O script mostrará um resumo final com:
- Status de cada etapa
- URLs de acesso
- Localizações dos logs
- Avisos ou próximas ações

## Estrutura de Logs

### Console Output
```
[2025-08-03 14:30:15] [STEP] ==========================================
[2025-08-03 14:30:15] [STEP] PASSO 1: Criando Backup da Configuração
[2025-08-03 14:30:15] [STEP] ==========================================
[2025-08-03 14:30:15] [SUCESSO] Backup criado: bak/standalone-20250803-143015.xml
```

### Arquivo de Log
Logs detalhados salvos em `logs/wildfly-config-YYYYMMDD-HHMMSS.log`:
```
[2025-08-03 14:30:15] [INFO] Iniciando configuração do WildFly 31 com HSQLDB
[2025-08-03 14:30:15] [START] Timestamp: 20250803-143015
[2025-08-03 14:30:15] [SUCESSO] WildFly encontrado: C:\dev\csonline\server\wildfly-31.0.1.Final
```

## Configuração Aplicada

### Módulo HSQLDB
```
server/wildfly-31.0.1.Final/modules/system/layers/base/org/hsqldb/main/
├── hsqldb-2.7.2.jar
└── module.xml
```

### Driver Configuration
```xml
<jdbc-driver name="hsqldb" 
             driver-module-name="org.hsqldb"
             driver-xa-datasource-class-name="org.hsqldb.jdbc.pool.JDBCXADataSource"/>
```

### Datasource Configuration
```xml
<datasource jndi-name="java:/HSQLDBDatasource" 
            pool-name="HSQLDBDatasource" 
            enabled="true">
    <connection-url>jdbc:hsqldb:file:csonline</connection-url>
    <driver>hsqldb</driver>
    <pool>
        <min-pool-size>2</min-pool-size>
        <max-pool-size>10</max-pool-size>
    </pool>
    <security user-name="sa" password="password"/>
</datasource>
```

## Solução de Problemas

### WildFly não está rodando
```
[ERRO] WildFly não está rodando ou não está acessível
```
**Solução**: Inicie o WildFly primeiro com `scr\start-wildfly-31.ps1`

### Java não encontrado
```
[ERRO] Java não encontrado ou não configurado no PATH
```
**Solução**: Configure a variável JAVA_HOME e adicione ao PATH

### Driver não encontrado
```
[AVISO] Driver não encontrado no WAR, tentando baixar...
```
**Solução**: O script baixa automaticamente, mas pode executar `mvn clean package` primeiro

### Falha na conexão CLI
```
[ERRO] Falha na configuração do datasource
```
**Solução**: Verifique se o WildFly está rodando e acessível em 127.0.0.1:9990

## Verificação Manual

### Comandos CLI Úteis
```bash
# Verificar driver carregado
./jboss-cli.bat --connect --command="/subsystem=datasources/jdbc-driver=hsqldb:read-resource"

# Verificar datasource
./jboss-cli.bat --connect --command="/subsystem=datasources/data-source=HSQLDBDatasource:read-resource"

# Testar conexão
./jboss-cli.bat --connect --command="/subsystem=datasources/data-source=HSQLDBDatasource:test-connection-in-pool"
```

### Logs do Servidor
```bash
# Monitor logs em tempo real
Get-Content "server\wildfly-31.0.1.Final\standalone\log\server.log" -Wait

# Buscar por HSQLDB
Select-String "hsqldb" "server\wildfly-31.0.1.Final\standalone\log\server.log"
```

## URLs de Acesso Pós-Configuração

```
|----------------|-------------------------------------------|--------------------------|
| Serviço        | URL                                       | Descrição                |
|----------------|-------------------------------------------|--------------------------|
| **Aplicação**  | http://127.0.0.1:8080/csonline            | Interface da aplicação   |
| **Management** | http://127.0.0.1:9990                     | Console de administração |
| **Swagger**    | http://127.0.0.1:8080/csonline/swagger-ui | Documentação da API      |
|----------------|-------------------------------------------|--------------------------|
```

## Arquivos Relacionados

```
|-----------------------------------------|----------------------------------|
| Arquivo                                 | Descrição                        |
|-----------------------------------------|----------------------------------|
| `scr/config-jdbc-driver-wildfly-31.ps1` | **Script principal**             |
| `scr/start-wildfly-31.ps1`              | Script para iniciar servidor     |
| `scr/stop-wildfly-31.ps1`               | Script para parar servidor       |
| `bak/README-STANDALONE.md`              | Documentação completa do WildFly |
| `logs/wildfly-config-*.log`             | Logs de execução do script       |
|-----------------------------------------|----------------------------------|
```

---

**Versão**: 2.0  
**Data**: 03/08/2025  
**Autor**: GitHub Copilot  
**Compatível**: WildFly 31.0.1.Final, HSQLDB 2.7.2, PowerShell 5.1+
