# CSOnline Delivery

Aplicação Web para controle de entregas.

## Descrição

CSOnline Delivery é uma aplicação desenvolvida em Java (SDK 11) utilizando Jakarta EE 10, MyFaces e PrimeFaces para gerenciamento de entregas. O sistema roda em Jakarta EE 10 (Tomcat compatível) e utiliza PostgreSQL 15 como banco de dados relacional.

Acesse via navegador: [https://www.caracore.com.br/csonline](https://www.caracore.com.br/csonline)

## Funcionalidades

- Cadastro e controle de entregas
- Perfil de administrador
- Interface web responsiva

## Requisitos


- Java SDK 11
- Jakarta EE 10 (compatível com Tomcat 10.1.x ou superior)
- PostgreSQL 15
- Tomcat 10.1.x (ou superior) configurado para Jakarta EE
- Jars obrigatórios no WAR (WEB-INF/lib):
  - primefaces-13.0.4-jakarta.jar (baixe manualmente se necessário)
  - myfaces-api-4.0.0.jar (MyFaces)
  - myfaces-impl-4.0.0.jar (MyFaces)
  - weld-servlet-shaded-4.0.3.Final.jar (CDI Weld)
  - Demais dependências do projeto (jsoup, log4j, postgresql, etc.)

## Instalação

1. Clone o repositório.
2. Configure o banco de dados PostgreSQL 15 conforme o padrão do projeto (consulte os scripts em `doc/dump`).
3. Faça o build do projeto e gere o arquivo WAR.
4. Certifique-se que os jars obrigatórios estão presentes em WEB-INF/lib do WAR.
5. Faça o deploy do WAR em um Tomcat 10.1.x (ou superior) configurado para Jakarta EE.

## Configuração de JNDI (DataSource) no Tomcat

O CSOnline utiliza um DataSource configurado via JNDI para acesso ao banco de dados. O recomendado é configurar o DataSource diretamente no servidor Tomcat, centralizando a gestão de conexões e facilitando a administração.

### Exemplo de configuração no Tomcat (`conf/context.xml`):

```xml
<Resource
    name="jdbc/db_cso"
    auth="Container"
    type="javax.sql.DataSource"
    driverClassName="org.h2.Driver"
    url="jdbc:h2:~/csonline;MODE=PostgreSQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE"
    username="sa"
    password=""
    maxTotal="20"
    maxIdle="10"
    maxWaitMillis="10000"
    initialSize="5"
    logAbandoned="true"
    removeAbandonedOnBorrow="true"
    removeAbandonedTimeout="1800"
/>
```

> **Importante:**
> - O nome do recurso (`jdbc/db_cso`) deve ser igual ao referenciado no `web.xml` da aplicação.
> - O driver JDBC correspondente (ex: `h2*.jar` ou `postgresql*.jar`) deve estar em `lib` do Tomcat.
> - Não é necessário (nem recomendado) definir o mesmo DataSource no arquivo `META-INF/context.xml` do WAR quando já está configurado no servidor.

### Diferença entre configuração no servidor e na aplicação

- **Configuração no servidor (recomendado):**
  - O DataSource é definido no Tomcat (`conf/context.xml` ou `conf/[engine]/[host]/[app].xml`).
  - Todas as aplicações podem compartilhar a mesma configuração.
  - Facilita a administração, troca de banco e credenciais sem rebuild do WAR.
  - Evita conflitos e duplicidade de recursos.

- **Configuração na aplicação:**
  - O DataSource é definido em `src/main/webapp/META-INF/context.xml` e empacotado no WAR.
  - Útil apenas para testes/desenvolvimento local, ou quando não se tem acesso ao Tomcat.
  - Pode sobrescrever a configuração do servidor, causando confusão.
  - Não recomendado para produção.

**Resumo:**
> Para produção, configure o JNDI/DataSource no Tomcat e mantenha apenas a referência no `web.xml` da aplicação. Remova ou comente o bloco `<Resource>` do `META-INF/context.xml` do projeto para evitar conflitos.

## Perfil de Administrador

- Usuário: `chmulato`
- Senha: `admin`

## Demonstração

Veja no Youtube: [https://youtu.be/vAMd647anMA](https://youtu.be/vAMd647anMA)

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo LICENSE para mais detalhes.

## Autor

Christian Vladimir Uhdre Mulato

## Contato

Para dúvidas ou sugestões, entre em contato pelo site ou pelo e-mail disponível no perfil do autor.
