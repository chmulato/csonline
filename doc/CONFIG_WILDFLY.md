
# Configuração do WildFly 31 para o projeto csonline

Este guia mostra como preparar o WildFly 31 para rodar a aplicação csonline com HSQLDB em memória.

> **Dica:** Para automatizar a configuração do driver JDBC e do DataSource, utilize os scripts PowerShell já prontos no projeto. Veja exemplos abaixo.


## 1. Configurar driver JDBC e DataSource via script (recomendado)

Execute na raiz do projeto:

Para configurar apenas o driver JDBC:

```powershell
pwsh ./config-wildfly-31.ps1 -SomenteDriver
```

Para configurar driver JDBC e DataSource:

```powershell
pwsh ./config-wildfly-31.ps1
```

Esses scripts copiam o JAR do driver para o WildFly e executam os comandos necessários via jboss-cli. Não é preciso criar módulos manualmente.

---

## 2. (Alternativo) Adicionar o driver HSQLDB como módulo manualmente

Se preferir configurar manualmente, siga os passos abaixo:

1. Crie a pasta do módulo:

   ```
   C:\dev\csonline\server\wildfly-31.0.1.Final\modules\org\hsqldb\main
   ```

2. Baixe o JAR do HSQLDB (ex: `hsqldb-2.7.2.jar`) e coloque nessa pasta.

3. Crie o arquivo `module.xml` nessa mesma pasta com o conteúdo:

   ```xml
   <module name="org.hsqldb">
       <resources>
           <resource-root path="hsqldb-2.7.2.jar"/>
       </resources>
       <dependencies>
           <module name="javax.api"/>
           <module name="javax.transaction.api"/>
       </dependencies>
   </module>
   ```

## 3. Configurar o datasource no standalone.xml (manual)

1. Abra:

   ```
   C:\dev\csonline\server\wildfly-31.0.1.Final\standalone\configuration\standalone.xml
   ```

2. Dentro da tag `<subsystem xmlns="...datasources...">`, adicione:

   ```xml
   <datasource jndi-name="java:/jdbc/csonlineDS" pool-name="csonlineDS" enabled="true">
       <connection-url>jdbc:hsqldb:mem:testdb</connection-url>
       <driver>hsqldb</driver>
       <security>
           <user-name>sa</user-name>
           <password></password>
       </security>
   </datasource>
   <drivers>
       <driver name="hsqldb" module="org.hsqldb">
           <xa-datasource-class>org.hsqldb.jdbcDriver</xa-datasource-class>
       </driver>
   </drivers>
   ```

## 4. Configuração do persistence.xml

Existem duas abordagens de configuração, dependendo do ambiente:

### 4.1 Ambiente de Desenvolvimento (RESOURCE_LOCAL)

Para desenvolvimento, use a configuração RESOURCE_LOCAL, que permite conexão direta com o banco de dados sem depender de datasources do servidor:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="csonlinePU" transaction-type="RESOURCE_LOCAL">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <!-- Classes de entidade -->
        <class>com.caracore.cso.entity.User</class>
        <!-- outras classes aqui... -->
        <properties>
            <!-- Conexão direta com o banco de dados -->
            <property name="jakarta.persistence.jdbc.driver" value="org.hsqldb.jdbcDriver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:hsqldb:mem:testdb"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            
            <!-- Configurações do EclipseLink -->
            <property name="eclipselink.logging.level" value="FINE"/>
            <property name="eclipselink.ddl-generation" value="drop-and-create-tables"/>
            <property name="eclipselink.ddl-generation.output-mode" value="database"/>
            
            <!-- Carga de dados inicial -->
            <property name="jakarta.persistence.sql-load-script-source" value="import.sql"/>
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

### 4.2 Ambiente de Produção (JTA)

Para produção, use a configuração JTA, que utiliza o gerenciamento de transações do servidor:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="csonlinePU" transaction-type="JTA">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <jta-data-source>java:/jdbc/csonlineDS</jta-data-source>
        <!-- Classes de entidade -->
        <class>com.caracore.cso.entity.User</class>
        <!-- outras classes aqui... -->
        <properties>
            <!-- Configurações do EclipseLink -->
            <property name="eclipselink.logging.level" value="FINE"/>
            <property name="eclipselink.ddl-generation" value="drop-and-create-tables"/>
            <property name="eclipselink.ddl-generation.output-mode" value="database"/>
            
            <!-- Carga de dados inicial -->
            <property name="jakarta.persistence.sql-load-script-source" value="import.sql"/>
        </properties>
    </persistence-unit>
</persistence>
```

Com JTA, você pode injetar o `EntityManager` diretamente:

```java
@PersistenceContext(unitName = "csonlinePU")
private EntityManager entityManager;
```

Não é necessário gerenciar transações manualmente, pois elas são gerenciadas pelo container. Você pode usar anotações como `@Transactional` ou métodos EJB para definir limites de transação.

## 5. Ajustar application.properties

Remova ou comente as linhas relacionadas a datasource, username, password e dialect, pois essas configurações já estão no persistence.xml.

## 6. Criar o arquivo beans.xml

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

## 7. Reinicie o WildFly

- Use o script `start-wildfly-31.ps1` ou rode manualmente o `standalone.bat`.
- O WildFly fará o deploy do WAR e criará o banco em memória automaticamente.

## 8. Teste a aplicação

- Acesse: http://localhost:8080/csonline/
- O banco estará em memória, pronto para uso.

---

## Diferenças entre RESOURCE_LOCAL e JTA

### RESOURCE_LOCAL (Desenvolvimento)
- Conexão direta com o banco via JDBC
- Transações gerenciadas manualmente
- Mais simples para ambiente de desenvolvimento
- Não suporta transações distribuídas
- Requer criação manual do EntityManager

### JTA (Produção)
- Usa datasources configurados no servidor de aplicação
- Transações gerenciadas pelo container
- Suporta transações distribuídas
- Permite injeção direta de EntityManager
- Melhor para ambientes de produção com muitas conexões simultâneas

Escolha a configuração mais adequada ao seu ambiente e necessidades.

---

Se precisar de outros bancos, ajuste o driver e o datasource conforme necessário.
