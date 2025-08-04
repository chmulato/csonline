# Configuração do Servidor WildFly 31.0.1.Final

Este documento descreve como configurar o servidor WildFly 31 para a aplicação CSOnline.

## Índice
- [Pré-requisitos](#pré-requisitos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Configuração do Driver HSQLDB](#configuração-do-driver-hsqldb)
- [Configuração do Datasource](#configuração-do-datasource)
- [Configuração de Deployment](#configuração-de-deployment)
- [Inicialização do Servidor](#inicialização-do-servidor)
- [Troubleshooting](#troubleshooting)

## Pré-requisitos

- Java JDK 11 ou superior
- WildFly 31.0.1.Final baixado e extraído
- Maven 3.6 ou superior para build da aplicação

## Estrutura do Projeto

```
csonline/
├── server/
│   └── wildfly-31.0.1.Final/
│       ├── bin/
│       ├── modules/
│       └── standalone/
│           ├── configuration/
│           │   └── standalone.xml
│           ├── deployments/
│           └── log/
├── target/
│   └── csonline.war
└── scr/
    ├── start-wildfly-31.ps1
    └── stop-wildfly-31.ps1
```

## Configuração do Driver HSQLDB

### 1. Criar Módulo HSQLDB

Crie a estrutura de diretórios para o módulo:

```
server/wildfly-31.0.1.Final/modules/system/layers/base/org/hsqldb/main/
```

### 2. Baixar o Driver

Baixe o driver HSQLDB 2.7.2:
```bash
wget https://repo1.maven.org/maven2/org/hsqldb/hsqldb/2.7.2/hsqldb-2.7.2.jar
```

Coloque o arquivo em:
```
server/wildfly-31.0.1.Final/modules/system/layers/base/org/hsqldb/main/hsqldb-2.7.2.jar
```

### 3. Criar module.xml

Crie o arquivo `module.xml` no diretório `main`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.hsqldb">
    <resources>
        <resource-root path="hsqldb-2.7.2.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
        <module name="javax.servlet.api" optional="true"/>
    </dependencies>
</module>
```

## Configuração do Datasource

### 1. Editar standalone.xml

Localize a seção `<subsystem xmlns="urn:jboss:domain:datasources:7.1">` no arquivo:
```
server/wildfly-31.0.1.Final/standalone/configuration/standalone.xml
```

### 2. Adicionar Driver HSQLDB

Na seção `<drivers>`, adicione:

```xml
<driver name="hsqldb" module="org.hsqldb">
    <xa-datasource-class>org.hsqldb.jdbc.pool.JDBCXADataSource</xa-datasource-class>
</driver>
```

### 3. Configurar Datasource

Na seção `<datasources>`, adicione:

```xml
<datasource jndi-name="java:/HSQLDBDatasource" pool-name="HSQLDBDatasource" enabled="true">
    <connection-url>jdbc:hsqldb:file:csonline</connection-url>
    <driver>hsqldb</driver>
    <pool>
        <min-pool-size>2</min-pool-size>
        <max-pool-size>10</max-pool-size>
    </pool>
    <security user-name="sa" password="password"/>
</datasource>
```

### Exemplo Completo da Seção Datasources

```xml
<subsystem xmlns="urn:jboss:domain:datasources:7.1">
    <datasources>
        <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
            <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
            <driver>h2</driver>
            <security user-name="sa" password="sa"/>
        </datasource>
        <datasource jndi-name="java:/HSQLDBDatasource" pool-name="HSQLDBDatasource" enabled="true">
            <connection-url>jdbc:hsqldb:file:csonline</connection-url>
            <driver>hsqldb</driver>
            <pool>
                <min-pool-size>2</min-pool-size>
                <max-pool-size>10</max-pool-size>
            </pool>
            <security user-name="sa" password="password"/>
        </datasource>
        <drivers>
            <driver name="h2" module="com.h2database.h2">
                <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
            </driver>
            <driver name="hsqldb" module="org.hsqldb">
                <xa-datasource-class>org.hsqldb.jdbc.pool.JDBCXADataSource</xa-datasource-class>
            </driver>
        </drivers>
    </datasources>
</subsystem>
```

## Configuração de Deployment

### 1. Limpar Diretório deployments

Certifique-se de que não há conflitos no diretório `deployments`:

```bash
# Remover deployments duplicados se existirem
rm -f server/wildfly-31.0.1.Final/standalone/deployments/hsqldb-2.7.2.jar*
```

### 2. Deploy da Aplicação

Copie o WAR para o diretório de deployments:

```bash
cp target/csonline.war server/wildfly-31.0.1.Final/standalone/deployments/
```

## Inicialização do Servidor

### 1. Script de Inicialização

Use o script PowerShell (Windows):

```powershell
# scr/start-wildfly-31.ps1
cd "server\wildfly-31.0.1.Final\bin"
.\standalone.bat
```

### 2. Inicialização Manual

```bash
cd server/wildfly-31.0.1.Final/bin
./standalone.sh  # Linux/Mac
# ou
standalone.bat   # Windows
```

### 3. Verificar Inicialização

O servidor deve apresentar as seguintes mensagens de sucesso:

```
INFO  [org.jboss.as.connector.deployers.jdbc] Started Driver service with driver-name = hsqldb
INFO  [org.jboss.as.connector.subsystems.datasources] Bound data source [java:/HSQLDBDatasource]
INFO  [org.wildfly.extension.undertow] Undertow HTTP listener default listening on 127.0.0.1:8080
INFO  [org.jboss.as.server] Deployed "csonline.war"
INFO  [org.jboss.as] WildFly Full 31.0.1.Final started
```

## URLs de Acesso

Após a inicialização bem-sucedida:

- Aplicação: http://127.0.0.1:8080/csonline
- Console Admin: http://127.0.0.1:9990
- Management HTTP: http://127.0.0.1:9990/management

## Troubleshooting

### Erro: Deployment Duplicado

Sintoma: `WFLYCTL0212: Duplicate resource [("deployment" => "hsqldb-2.7.2.jar")]`

Solução:
1. Remover driver da pasta deployments
2. Limpar cache do servidor

```bash
rm -f server/wildfly-31.0.1.Final/standalone/deployments/hsqldb*
rm -rf server/wildfly-31.0.1.Final/standalone/data/content
rm -rf server/wildfly-31.0.1.Final/standalone/tmp/*
```

### Erro: Conteúdo de Deployment Não Encontrado

Sintoma: `No deployment content with hash [...] is available`

Solução:
1. Remover seção de deployments do standalone.xml
2. Usar apenas módulos para drivers

### Erro: Constraint de Chave Estrangeira

Sintoma: `integrity constraint violation: foreign key no parent`

Solução:
1. Verificar ordem dos dados no import.sql
2. Verificar dependências entre tabelas
3. Usar Flyway para gerenciar migrações

### Verificar Logs

Logs do servidor estão em:
```
server/wildfly-31.0.1.Final/standalone/log/server.log
```

### Comandos Úteis de Diagnóstico

```bash
# Verificar se o driver está carregado
grep "hsqldb" server/wildfly-31.0.1.Final/standalone/log/server.log

# Verificar se o datasource foi vinculado
grep "HSQLDBDatasource" server/wildfly-31.0.1.Final/standalone/log/server.log

# Verificar deployment da aplicação
grep "csonline.war" server/wildfly-31.0.1.Final/standalone/log/server.log
```

## Configuração de Desenvolvimento

Para desenvolvimento, considere:

1. Hot Deployment: Habilitar scanning automático
2. Debug Mode: Iniciar com debug habilitado
3. Logging: Configurar nível de log adequado

### Habilitar Debug

```bash
# Linux/Mac
./standalone.sh --debug

# Windows
standalone.bat --debug
```

### Configurar Logging

Edite `standalone/configuration/logging.properties` para ajustar níveis de log.

## Configuração de Produção

Para produção:

1. SSL/TLS: Configurar certificados
2. Security: Configurar autenticação
3. Performance: Ajustar pools de conexão
4. Monitoring: Configurar métricas

### Exemplo de Pool de Produção

```xml
<pool>
    <min-pool-size>10</min-pool-size>
    <max-pool-size>50</max-pool-size>
    <prefill>true</prefill>
</pool>
```

---

Autor: GitHub Copilot  
Data: 03/08/2025  
Versão: WildFly 31.0.1.Final
