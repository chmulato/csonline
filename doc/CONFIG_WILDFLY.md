# Configuração do WildFly 31 para o projeto csonline

Este guia mostra como preparar o WildFly 31 para rodar a aplicação csonline com HSQLDB em memória.

## 1. Adicionar o driver HSQLDB como módulo

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

## 2. Configurar o datasource no standalone.xml

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

## 3. Ajustar o persistence.xml

No arquivo `src/main/resources/META-INF/persistence.xml`, configure:
```xml
<jta-data-source>java:/jdbc/csonlineDS</jta-data-source>
```

## 4. Ajustar application.properties

Remova ou comente as linhas relacionadas a datasource, username, password e dialect.

## 5. Reinicie o WildFly

- Use o script `start-wildfly-31.ps1` ou rode manualmente o `standalone.bat`.
- O WildFly fará o deploy do WAR e criará o banco em memória automaticamente.

## 6. Teste a aplicação

- Acesse: http://localhost:8080/csonline/
- O banco estará em memória, pronto para uso.

---

Se precisar de outros bancos, ajuste o driver e o datasource conforme necessário.
