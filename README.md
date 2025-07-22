# CSOnline Delivery

Aplicação Web para controle de entregas.

## Descrição

CSOnline Deliv### Logs do Jetty Embedded

Os logs são exibidos diretamente no terminal onde você executou `mvn jetty:run`. Os logs incluem:

- **Inicialização do H2**: `Banco H2 inicializado com sucesso.`
- **MyFaces/JSF**: `MyFaces Core has started`
- **PrimeFaces**: `Running on PrimeFaces 14.0.0`
- **CDI/Weld**: `Weld initialization`
- **Log4j 2**: Logs estruturados com timestamp e níveis

**Logs da aplicação:**

```text
logs/
├── csonline.log          ← Log principal da aplicação
├── csonline.2025-07-22.1.gz  ← Logs arquivados (rotação diária)
└── archived/             ← Logs antigos (máx. 10 arquivos)
```ão desenvolvida em Java (SDK 17) utilizando Jakarta EE 10, MyFaces 4.0.x e PrimeFaces 14.0.0-jakarta para gerenciamento de entregas. O sistema roda em Tomcat 10.1.23 embedded (Jakarta EE 10 compatível) e utiliza PostgreSQL 15 como banco de dados relacional.

Acesse via navegador: [https://www.caracore.com.br/csonline](https://www.caracore.com.br/csonline)

## Funcionalidades

- Cadastro e controle de entregas
- Perfil de administrador
- Interface web responsiva

## Tecnologias Utilizadas

- **Jakarta EE 10**: Plataforma empresarial Java com namespaces `jakarta.*`
- **PrimeFaces 14.0.0-jakarta**: Biblioteca de componentes JSF para Jakarta EE
- **MyFaces 4.0.x**: Implementação JSF para Jakarta EE (Apache MyFaces)
- **Weld 5.1.2.Final**: Implementação CDI (Contexts and Dependency Injection)
- **Jetty 11.0.17**: Servidor de aplicação para desenvolvimento (Maven Plugin)
- **Tomcat 10.1.23**: Servidor de aplicação para produção compatível com Jakarta EE 10
- **Log4j 2.23.1**: Sistema de logging estruturado com rotação de arquivos
- **PostgreSQL 15**: Banco de dados relacional para produção
- **H2 Database 2.3.232**: Banco de dados em memória para desenvolvimento e testes

## Requisitos

- Java SDK 17+
- Jakarta EE 10
- Maven 3.9.x ou superior
- PostgreSQL 15 (para produção) ou H2 2.3.232 (para desenvolvimento)
- Dependências principais (gerenciadas pelo Maven):
  - PrimeFaces 14.0.0-jakarta (Jakarta EE compatível)
  - Apache MyFaces 4.0.x (implementação Jakarta EE)
  - Weld 5.1.2.Final (CDI para Jakarta EE)
  - Jetty 11.0.17 (desenvolvimento via Maven Plugin)
  - Log4j 2.23.1 (logging estruturado)

## Instalação e Execução

### Desenvolvimento (Jetty Embedded)

1. Clone o repositório:

   ```bash
   git clone <repository-url>
   cd csonline
   ```

2. Compile, empacote e execute com Jetty embedded:

   ```bash
   mvn clean package jetty:run -DskipTests
   ```

   **Ou execute em etapas separadas:**

   ```bash
   # Primeiro compile e gere o WAR
   mvn clean package -DskipTests
   
   # Depois inicie o servidor
   mvn jetty:run
   ```

3. **Aguarde a inicialização completa:**

   ```text
   [INFO] MyFaces Core has started, it took [3921] ms.
   [INFO] Running on PrimeFaces 14.0.0
   [INFO] Banco H2 inicializado com sucesso.
   ```

4. **Acesse a aplicação:**
   - URL: <http://localhost:8080/csonline>
   - Usuário: `chmulato`
   - Senha: `admin`

### Base de Dados H2 (Desenvolvimento)

A aplicação utiliza H2 Database em modo PostgreSQL para desenvolvimento:

- **URL de conexão**: `jdbc:h2:~/csonline;MODE=PostgreSQL;DATABASE_TO_UPPER=false`
- **Localização**: `~/csonline.mv.db` (diretório home do usuário)
- **Inicialização automática**: Script SQL executado via `DatabaseInitializer`
- **Dados de teste incluídos**: Usuários, clientes, entregadores e preços

**Usuários disponíveis para teste:**

- **Admin**: `chmulato` / `admin`
- **Business**: `pmulato` / `admin`, `joao` / `admin`
- **Customer**: `mariosa` / `admin`, `santasa` / `admin`
- **Courier**: `mane` / `admin`

### Logs do Jetty Embedded

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
Get-Content logs/csonline.log -Wait

# Ou monitore via terminal onde está executando mvn jetty:run
```

**Configuração de log level:**

Edite `src/main/resources/log4j2.xml` para ajustar níveis:

```xml
<Logger name="br.com.mulato" level="DEBUG" additivity="false">
<!-- Para mais detalhes: level="TRACE" -->
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

Se encontrar problemas com a aplicação:

```bash
# Limpe e reinicie completamente
taskkill /F /IM java.exe
mvn clean package jetty:run -DskipTests
```

**Problemas comuns:**

1. **Porta 8080 ocupada**: Verificar se outra aplicação está usando a porta
2. **H2 Database lock**: Fechar conexões existentes antes de reiniciar
3. **Dependências**: Executar `mvn clean` para limpar cache Maven

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
INFO: Banco H2 inicializado com sucesso.
INFO: MyFaces Core has started, it took [3921] ms.
INFO: Running on PrimeFaces 14.0.0
INFO: Weld initialization completed successfully
```

### Produção (Deploy WAR)

1. Gere o arquivo WAR:

   ```bash
   mvn clean package -DskipTests
   ```

2. Configure o banco de dados PostgreSQL 15 conforme os scripts em `doc/dump/`.

3. Faça o deploy do WAR (`target/csonline.war`) em um Tomcat 10.1.x ou superior configurado para Jakarta EE.

## Migração para Jakarta EE

Este projeto foi **completamente migrado** do Java EE (namespace `javax.*`) para Jakarta EE 10 (namespace `jakarta.*`). As principais mudanças incluem:

### Mudanças Críticas Realizadas

- **PrimeFaces**: Atualizado de 13.0.7 para **14.0.0-jakarta** (versão Jakarta EE compatível)
- **MyFaces**: Migrado para versão 4.0.x (Jakarta EE) - Apache MyFaces
- **Weld**: Atualizado para versão 5.1.2.Final (CDI Jakarta EE)
- **H2 Database**: Configurado script de inicialização corrigindo palavra reservada `user` → `users`
- **Log4j**: Atualizado para 2.23.1 com configuração Jakarta EE compatível
- **Servidor**: Migrado de Tomcat Cargo para Jetty Maven Plugin (desenvolvimento)
- **Namespaces**: Todas as dependências e configurações atualizadas para `jakarta.*`

### Descobertas Importantes da Migração

1. **PrimeFaces 13.x não é Jakarta EE**: Era necessário usar versão 14.0.0 específica
2. **H2 Database**: Palavra `user` é reservada, substituída por `users`
3. **Log4j**: Necessária migração completa da configuração v1.x para v2.x
4. **Cargo Plugin**: Problemas de cache resolvidos com migração para Jetty

### Arquivos Principais Criados/Atualizados

- `src/main/resources/log4j2.xml` - Configuração completa Log4j 2
- `src/main/resources/data-h2.sql` - Script inicialização H2 compatível
- `src/main/java/.../DatabaseInitializer.java` - Listener inicialização banco
- `pom.xml` - Dependências Jakarta EE 10 completas

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
