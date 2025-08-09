
# Configuração do WildFly 31 para o projeto CSOnline

Guia para preparar o WildFly 31 com HSQLDB (modo arquivo) e migrações Flyway.

## Configuração Atual

O projeto está configurado para operar com:
- **WildFly 31.0.1.Final** - Servidor de aplicação enterprise
- **HSQLDB 2.7** - Banco de dados em modo arquivo
- **Flyway 8.5.13** - Controle de migrações de banco
- **Deploy automático** - Scripts PowerShell para build e deploy
- **URLs funcionais** - Todas as APIs e interfaces testadas e operacionais


## 1. Deploy Rápido (Sistema já Configurado)

**Para usar o sistema já configurado:**

```powershell
# 1. Inicie o WildFly
cd C:\dev\csonline
.\scr\start-wildfly-31.ps1

# 2. Faça o deploy da aplicação
.\scr\deploy-wildfly-31.ps1

# 3. Acesse o sistema
# Aplicação: http://localhost:8080/csonline/
# APIs: http://localhost:8080/csonline/api/users
# Swagger: http://localhost:8080/csonline/swagger-ui/
```

**O sistema inclui:**
- Driver HSQLDB configurado como módulo
- Datasource `java:/HSQLDBDatasource`
- Migrações Flyway V1 (schema) e V2 (dados)
- JTA para transações

## 2. Configuração Completa do Ambiente (Se Necessário)

**Caso precise configurar um ambiente novo, use o processo automatizado:**

```powershell
# Configuração completa automática
cd C:\dev\csonline
.\scr\config-wildfly-31.ps1
```

Este script realiza:
1. Download do driver HSQLDB 2.7.2
2. Criação do módulo WildFly para HSQLDB
3. Configuração do datasource `java:/HSQLDBDatasource`
4. Configuração de logging e SSL (opcional)
5. Testes de conectividade e validação

---

## 3. (Alternativo) Configuração Manual do Módulo HSQLDB

Se preferir configurar manualmente, siga os passos abaixo:

1. Crie a pasta do módulo:

   ```
   C:\dev\csonline\server\wildfly-31.0.1.Final\modules\system\layers\base\org\hsqldb\main
   ```

2. Baixe o JAR do HSQLDB 2.7.2 e coloque nessa pasta:
   ```
   hsqldb-2.7.2.jar
   ```

3. Crie o arquivo `module.xml` nessa mesma pasta com o conteúdo:

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

## 4. Configurar o datasource no standalone.xml (manual)

1. Abra:

   ```
   C:\dev\csonline\server\wildfly-31.0.1.Final\standalone\configuration\standalone.xml
   ```

2. Dentro da tag `<subsystem xmlns="...datasources...">`, adicione:

   ```xml
   <datasource jndi-name="java:/HSQLDBDatasource" pool-name="HSQLDBDatasource" enabled="true">
       <connection-url>jdbc:hsqldb:file:csonline</connection-url>
       <driver>hsqldb</driver>
       <pool>
           <min-pool-size>2</min-pool-size>
           <max-pool-size>10</max-pool-size>
       </pool>
       <security>
           <user-name>sa</user-name>
           <password></password>
       </security>
   </datasource>
   ```

3. Dentro da seção `<drivers>`, adicione:

   ```xml
   <driver name="hsqldb" module="org.hsqldb">
       <xa-datasource-class>org.hsqldb.jdbc.pool.JDBCXADataSource</xa-datasource-class>
   </driver>
   ```

## 5. Configuração do persistence.xml (Produção)

O sistema CSOnline está configurado para produção usando JTA (Java Transaction API) para integração completa com WildFly. A configuração atual utiliza HSQLDB como banco de dados principal com gerenciamento de transações pelo servidor.

### 5.1 Configuração de Produção Atual (JTA)

A configuração de produção utiliza JTA para gerenciamento automático de transações pelo WildFly:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="csonlinePU" transaction-type="JTA">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <jta-data-source>java:/HSQLDBDatasource</jta-data-source>
        
        <!-- Classes de entidade -->
        <class>com.caracore.cso.entity.User</class>
        <class>com.caracore.cso.entity.Customer</class>
        <class>com.caracore.cso.entity.Courier</class>
        <class>com.caracore.cso.entity.Delivery</class>
        <class>com.caracore.cso.entity.Team</class>
        
        <properties>
            <!-- Configurações do EclipseLink -->
            <property name="eclipselink.logging.level" value="INFO"/>
            <property name="eclipselink.ddl-generation" value="create-or-extend-tables"/>
            <property name="eclipselink.ddl-generation.output-mode" value="database"/>
            
            <!-- Configurações específicas para HSQLDB -->
            <property name="eclipselink.target-database" value="HSQL"/>
            
            <!-- Flyway cuida das migrações de schema e dados iniciais -->
        </properties>
    </persistence-unit>
</persistence>
```

**Importante:** No modo RESOURCE_LOCAL, você não pode usar `@PersistenceContext` para injetar o EntityManager. Em vez disso, você deve:

1. Criar um `EntityManagerFactory` usando `Persistence.createEntityManagerFactory()`
2. Criar instâncias de `EntityManager` conforme necessário
3. Gerenciar transações manualmente (`begin()`, `commit()`, `rollback()`)

Exemplo:

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("csonlinePU");
EntityManager em = emf.createEntityManager();
try {
    em.getTransaction().begin();
    // operações com o banco de dados
    em.getTransaction().commit();
} catch (Exception e) {
    if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
} finally {
    em.close();
    emf.close();
}
```

Com JTA, você pode injetar o `EntityManager` diretamente nos seus serviços:

```java
@PersistenceContext(unitName = "csonlinePU")
private EntityManager entityManager;
```

As transações são gerenciadas automaticamente pelo WildFly. Use anotações como `@Transactional` para definir limites de transação quando necessário.

### 5.2 Vantagens da Configuração JTA Atual

- **Gerenciamento automático de transações**: WildFly gerencia begin/commit/rollback
- **Pool de conexões**: Otimização automática de recursos de banco de dados
- **Integração com Jakarta EE**: Funciona perfeitamente com CDI, EJB e outros componentes
- **Monitoramento**: Estatísticas de conexão disponíveis via console administrativo
- **Escalabilidade**: Preparado para ambientes de produção enterprise

## 6. Configuração do Flyway (Sistema de Migração)

O CSOnline utiliza Flyway 8.5.13 para gerenciamento de migrações de banco de dados. O sistema está configurado para funcionar com HSQLDB em produção.

### 6.1 Script de Gerenciamento

Use o script PowerShell `flyway-manage.ps1` para executar migrações:

```powershell
# Aplicar todas as migrações
.\scr\flyway-manage.ps1 migrate

# Verificar status das migrações
.\scr\flyway-manage.ps1 info

# Limpar banco de dados (usar com cuidado!)
.\scr\flyway-manage.ps1 clean
```

### 6.2 Estrutura de Migrações

As migrações estão localizadas em `src/main/resources/db/migration/` seguindo o padrão:
- `V1__Initial_schema.sql`
- `V2__Add_delivery_status.sql`
- `V3__Update_user_roles.sql`

### 6.3 Configuração Flyway

O arquivo `flyway.conf` está configurado para HSQLDB:

```properties
flyway.url=jdbc:hsqldb:file:./data/csonline;shutdown=true
flyway.user=sa
flyway.password=
flyway.locations=filesystem:src/main/resources/db/migration
```

## 7. Configuração de Logs

O sistema utiliza Log4j2 para gerenciamento de logs, com configuração otimizada para produção.

### 7.1 Arquivo log4j2.xml Atual

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <File name="FileAppender" fileName="logs/app.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Logger name="com.caracore.cso" level="INFO"/>
        <Logger name="org.flywaydb" level="INFO"/>
        <Root level="WARN">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

## 8. Ajustar application.properties

As configurações de conexão com banco de dados foram movidas para o persistence.xml e datasource do WildFly. O arquivo `application.properties` atual contém apenas configurações específicas da aplicação.

### 8.1 Arquivo application.properties Atual

```properties
# Configurações da aplicação
app.name=CSOnline
app.version=1.0.0
app.environment=production

# Configurações de SMS (exemplo)
sms.api.key=your-api-key-here
sms.provider.url=https://api.sms-provider.com

# Configurações de segurança
jwt.secret=your-jwt-secret-key
jwt.expiration=3600000

# Configurações de upload
upload.max.file.size=10MB
upload.directory=./uploads
```

## 9. Criar o arquivo beans.xml

Para garantir que o CDI (Contexts and Dependency Injection) funcione corretamente, crie um arquivo `beans.xml` em `src/main/webapp/WEB-INF/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       version="4.0"
       bean-discovery-mode="all">
</beans>
```

## 10. Deploy e Inicialização

### 10.1 Scripts de Deploy Automático

Use os scripts PowerShell para gerenciar o WildFly:

```powershell
# Iniciar WildFly
.\scr\start-wildfly-31.ps1

# Fazer deploy da aplicação
.\scr\deploy-wildfly-31.ps1

# Parar WildFly
.\scr\stop-wildfly-31.ps1
```

### 10.2 Verificação do Deploy

Após o deploy, verifique:

1. **Console do WildFly**: http://localhost:9990/console
2. **Aplicação Principal**: http://localhost:8080/csonline/
3. **API Swagger**: http://localhost:8080/csonline/swagger-ui/
4. **Health Check**: http://localhost:8080/csonline/api/health

## 11. Configuração de Produção Atual

O sistema está configurado com os seguintes componentes em produção:

- **WildFly 31.0.1.Final**: Servidor de aplicação Jakarta EE
- **HSQLDB 2.7**: Banco de dados em arquivo persistente
- **Flyway 8.5.13**: Sistema de migração de banco de dados
- **EclipseLink**: Implementação JPA
- **Vue 3**: Frontend SPA completo
- **Swagger/OpenAPI**: Documentação automática da API

### 11.1 URLs de Produção

- **Frontend Principal**: http://localhost:8080/csonline/
- **API REST**: http://localhost:8080/csonline/api/
- **Documentação Swagger**: http://localhost:8080/csonline/swagger-ui/
- **Console Administrativo**: http://localhost:9990/console

---

## Resumo da Configuração

### Ambiente de Produção Atual (JTA)
- ✅ Conexão via datasource configurado no WildFly
- ✅ Transações gerenciadas automaticamente pelo container
- ✅ Pool de conexões otimizado
- ✅ Suporte completo a Jakarta EE
- ✅ Injeção direta de EntityManager
- ✅ Sistema de logs configurado
- ✅ Migrações Flyway funcionais

### Vantagens da Configuração Atual
- **Performance**: Pool de conexões otimizado
- **Escalabilidade**: Preparado para múltiplos usuários simultâneos
- **Manutenibilidade**: Configuração centralizada no servidor
- **Monitoramento**: Métricas disponíveis via console administrativo
- **Robustez**: Gerenciamento automático de transações e recursos

A configuração atual está otimizada para produção enterprise e oferece todas as funcionalidades necessárias para o sistema CSOnline.
