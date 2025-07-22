# CSOnline Delivery

Aplicação Web para controle de entregas.

## Descrição

CSOnline Delivery é uma aplicação desenvolvida em Java (SDK 17) utilizando Jakarta EE 10, MyFaces 4.0.x e PrimeFaces 14.0.0-jakarta para gerenciamento de entregas. O sistema roda em Tomcat 10.1.23 embedded (Jakarta EE 10 compatível) e utiliza PostgreSQL 15 como banco de dados relacional.

Acesse via navegador: [https://www.caracore.com.br/csonline](https://www.caracore.com.br/csonline)

## Funcionalidades

- Cadastro e controle de entregas
- Perfil de administrador
- Interface web responsiva

## Tecnologias Utilizadas

- **Jakarta EE 10**: Plataforma empresarial Java com namespaces `jakarta.*`
- **PrimeFaces 14.0.0-jakarta**: Biblioteca de componentes JSF para Jakarta EE
- **MyFaces 4.0.x**: Implementação JSF para Jakarta EE
- **Weld 5.1.x**: Implementação CDI (Contexts and Dependency Injection)
- **Tomcat 10.1.23**: Servidor de aplicação compatível com Jakarta EE 10
- **Maven Cargo Plugin**: Para execução do Tomcat embedded durante desenvolvimento
- **PostgreSQL 15**: Banco de dados relacional para produção
- **H2 Database**: Banco de dados em memória para desenvolvimento e testes

## Requisitos

- Java SDK 17
- Jakarta EE 10
- Maven 3.9.x ou superior
- PostgreSQL 15 (para produção) ou H2 (para desenvolvimento)
- Dependências principais (gerenciadas pelo Maven):
  - PrimeFaces 14.0.0-jakarta (Jakarta EE compatível)
  - MyFaces 4.0.x (implementação Jakarta EE)
  - Weld 5.1.x (CDI para Jakarta EE)
  - Tomcat 10.1.23 embedded (via Maven Cargo Plugin)

## Instalação e Execução

### Desenvolvimento (Tomcat Embedded)

1. Clone o repositório:

   ```bash
   git clone <repository-url>
   cd csonline
   ```

2. Compile, empacote e execute com Tomcat embedded:

   ```bash
   mvn clean package cargo:run -DskipTests
   ```

   **Ou execute em etapas separadas:**

   ```bash
   # Primeiro compile e gere o WAR
   mvn clean package -DskipTests
   
   # Depois inicie o servidor
   mvn cargo:run
   ```

3. **Aguarde a mensagem de inicialização:**

   ```text
   [INFO] Tomcat 10.x Embedded started on port [8080]
   [INFO] Press Ctrl-C to stop the container...
   ```

4. **Acesse a aplicação:**
   - URL: <http://localhost:8080/csonline>
   - Usuário: `chmulato`
   - Senha: `admin`

### Logs do Tomcat Embedded

Os logs são exibidos diretamente no terminal onde você executou `mvn cargo:run`. Para logs mais detalhados:

**Localização dos logs:**

```text
target/cargo/
├── configurations/
│   └── tomcat10x/
│       ├── logs/           ← Logs do Tomcat
│       │   ├── catalina.out
│       │   ├── localhost.log
│       │   └── manager.log
│       └── webapps/        ← Aplicações deployadas
│           └── csonline/
```

**Visualizar logs em tempo real:**

```bash
# No Windows (PowerShell)
Get-Content target/cargo/configurations/tomcat10x/logs/catalina.out -Wait

# Ou monitore via terminal onde está executando mvn cargo:run
```

**Configuração de log level:**

O nível de log pode ser ajustado no `pom.xml`:

```xml
<cargo.logging>medium</cargo.logging>  <!-- low, medium, high -->
```

### Configuração do Log4j 2

A aplicação utiliza **Log4j 2.23.1** para logging. A configuração está em `src/main/resources/log4j2.xml`:

- **Logs da aplicação**: `logs/csonline.log`
- **Console output**: Logs também aparecem no terminal
- **Log rotation**: Arquivos rotacionados diariamente (máx. 100MB cada)
- **Levels configurados**:
  - `br.com.mulato.*`: DEBUG (código da aplicação)
  - `jakarta.faces`, `org.apache.myfaces`, `org.primefaces`: INFO
  - `org.jboss.weld`: INFO (CDI)
  - Root logger: INFO

**Ajustar níveis de log:**

Edite `src/main/resources/log4j2.xml` e altere os levels conforme necessário:

```xml
<Logger name="br.com.mulato" level="DEBUG" additivity="false">
<!-- Para mais detalhes, use: level="TRACE" -->
```

### Parar a Aplicação

- Pressione `Ctrl+C` no terminal onde está executando
- Ou force o encerramento: `taskkill /F /IM java.exe` (Windows)

### Solução de Problemas

Se encontrar problemas de cache ou mudanças não aparecerem:

```bash
# Limpe o cache do Cargo e rebuild
taskkill /F /IM java.exe
mvn clean package cargo:run -DskipTests
```

### Verificação da Instalação

Após executar `mvn clean package`, verifique se os arquivos foram criados:

```text
target/
├── csonline.war          ← Arquivo WAR principal
├── csonline/             ← Aplicação expandida
│   ├── WEB-INF/
│   │   ├── lib/          ← Dependências (PrimeFaces, MyFaces, etc.)
│   │   ├── classes/      ← Classes compiladas
│   │   └── web.xml       ← Configuração web
│   └── resources/        ← Recursos estáticos
└── classes/              ← Classes compiladas do projeto
```

**Logs de Sucesso Esperados:**

```text
INFO: MyFaces Core has started
INFO: Running on PrimeFaces 14.0.0
INFO: Tomcat 10.x Embedded started on port [8080]
```

### Produção (Deploy WAR)

1. Gere o arquivo WAR:

   ```bash
   mvn clean package -DskipTests
   ```

2. Configure o banco de dados PostgreSQL 15 conforme os scripts em `doc/dump/`.

3. Faça o deploy do WAR (`target/csonline.war`) em um Tomcat 10.1.x ou superior configurado para Jakarta EE.

## Migração para Jakarta EE

Este projeto foi migrado do Java EE (namespace `javax.*`) para Jakarta EE 10 (namespace `jakarta.*`). As principais mudanças incluem:

- Atualização do PrimeFaces 13.0.7 para 14.0.0-jakarta
- Migração do MyFaces para versão 4.0.x (Jakarta EE)
- Atualização do Weld para versão 5.1.x
- Configuração do Tomcat 10.1.23 embedded para desenvolvimento
- Atualização de todas as dependências para compatibilidade com Jakarta EE 10
- Configuração do Tomcat 10.1.23 embedded para desenvolvimento
- Atualização de todas as dependências para compatibilidade com Jakarta EE 10

O CSOnline **não utiliza JNDI/DataSource do Tomcat**. A conexão com o banco é feita diretamente via JDBC no código Java, utilizando o driver do banco (H2 ou PostgreSQL) e a URL de conexão configurada no projeto.

### Como funciona

- O listener `DatabaseInitializer` realiza a conexão e inicialização do banco H2 via JDBC puro.
- Para PostgreSQL, a conexão também é feita diretamente pelo código Java, sem dependência do Tomcat.
- Não é necessário configurar `<Resource>` no `context.xml` do Tomcat nem referência JNDI no `web.xml`.
- Basta garantir que o driver JDBC (`h2*.jar` ou `postgresql*.jar`) está presente em `WEB-INF/lib` do WAR.

**Resumo:**
> A aplicação é independente do JNDI/DataSource do Tomcat. Toda a gestão de conexão é feita pelo próprio código Java, facilitando o deploy e evitando dependências do servidor.

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
