# CSOnline Delivery

Aplicação Web para controle de entregas.

## Descrição

CSOnline Delivery é uma aplicação desenvolvida em Java (SDK 17) utilizando Jakarta EE 10, Eclipse Mojarra 4.0.8 e PrimeFaces 14.0.0-jakarta para gerenciamento de entregas. O sistema roda em Jetty 11.0.17 embedded (desenvolvimento) ou Tomcat 10.1.x (produção) compatível com Jakarta EE 10, e utiliza H2 Database 2.3.232 (desenvolvimento) ou PostgreSQL 15 (produção) como banco de dados relacional.

Acesse via navegador: [https://www.caracore.com.br/csonline](https://www.caracore.com.br/csonline)

## Funcionalidades

- Cadastro e controle de entregas
- Perfil de administrador
- Interface web responsiva

## Tecnologias Utilizadas

- **Jakarta EE 10**: Plataforma empresarial Java com namespaces `jakarta.*`
- **PrimeFaces 14.0.0-jakarta**: Biblioteca de componentes JSF para Jakarta EE
- **Eclipse Mojarra 4.0.8**: Implementação de referência JSF para Jakarta EE
- **Weld 5.1.2.Final**: Implementação CDI (Contexts and Dependency Injection)
- **Jetty 11.0.17**: Servidor de aplicação para desenvolvimento (Maven Plugin)
- **Log4j 2.23.1**: Sistema de logging estruturado com rotação de arquivos
- **H2 Database 2.3.232**: Banco de dados em memória para desenvolvimento (modo PostgreSQL)
- **PostgreSQL 15**: Banco de dados relacional para produção
- **jQuery 3.6.0**: Biblioteca JavaScript para interações client-side

## Requisitos

- Java SDK 17+
- Jakarta EE 10
- Maven 3.9.x ou superior
- H2 2.3.232 (para desenvolvimento) ou PostgreSQL 15 (para produção)
- Dependências principais (gerenciadas pelo Maven):
  - PrimeFaces 14.0.0-jakarta (Jakarta EE compatível)
  - Eclipse Mojarra 4.0.8 (implementação de referência JSF)
  - Weld 5.1.2.Final (CDI para Jakarta EE)
  - Jetty 11.0.17 (desenvolvimento via Maven Plugin)
  - Log4j 2.23.1 (logging estruturado)
  - H2 Database 2.3.232 (desenvolvimento com modo PostgreSQL)
  - jQuery 3.6.0 (client-side scripting)

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
   [INFO] Eclipse Mojarra JSF implementation started successfully.
   [INFO] Running on PrimeFaces 14.0.0
   [INFO] Banco H2 inicializado com sucesso.
   [INFO] Weld initialization completed successfully
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

**Logs da aplicação:**

```text
logs/
├── csonline.log          ← Log principal da aplicação
├── csonline.2025-07-22.1.gz  ← Logs arquivados (rotação diária)
└── archived/             ← Logs antigos (máx. 10 arquivos)
```

**Localização dos logs do servidor:**

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
  - `jakarta.faces`, `org.primefaces`: INFO
  - `org.jboss.weld`: INFO (CDI)
  - Root logger: INFO

**Ajustar níveis de log:**

Edite `src/main/resources/log4j2.xml` e altere os levels conforme necessário:

```xml
<Logger name="br.com.mulato" level="DEBUG" additivity="false">
<!-- Para mais detalhes, use: level="TRACE" -->
```

### Parar a Aplicação

**Scripts de gerenciamento disponíveis:**

```powershell
# Parar o servidor (Windows PowerShell)
.\stop-csonline.ps1

# Iniciar o servidor (Windows PowerShell)  
.\start-csonline.ps1
```

**Manualmente:**

- Pressione `Ctrl+C` no terminal onde está executando
- Ou force o encerramento: `taskkill /F /IM java.exe` (Windows)

### Solução de Problemas

Se encontrar problemas com a aplicação:

```powershell
# Usando scripts PowerShell (Windows)
.\stop-csonline.ps1
.\start-csonline.ps1

# Ou limpe e reinicie completamente
taskkill /F /IM java.exe
mvn clean package jetty:run -DskipTests
```

**Problemas comuns:**

1. **Porta 8080 ocupada**: Verificar se outra aplicação está usando a porta
2. **H2 Database lock**: Fechar conexões existentes antes de reiniciar
3. **Dependências**: Executar `mvn clean` para limpar cache Maven
4. **Recursos JSF não carregam**: Verificar configuração `jakarta.faces.RESOURCE_EXCLUDES` no web.xml
5. **Temas PrimeFaces**: Verificar se `nova-light` está disponível nos recursos
6. **Seletor de temas não funciona**: O sistema possui dois gerenciadores de temas:
   - ThemeBean (#{themeMB}) - Usado no login.xhtml
   - ThemeSwitcherBean (#{themeSwitcherBean}) - Usado no theme.xhtml
   - Verificar parâmetro `themeDefault` no web.xml e propriedade `client.theme_application` no config.properties

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
INFO: Eclipse Mojarra JSF implementation started successfully.
INFO: Running on PrimeFaces 14.0.0
INFO: Weld initialization completed successfully
INFO: Started Server@... HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
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
- **JSF Implementation**: Migrado do Apache MyFaces 4.0.2 para **Eclipse Mojarra 4.0.8** (implementação de referência)
- **Weld**: Atualizado para versão 5.1.2.Final (CDI Jakarta EE)
- **H2 Database**: Configurado script de inicialização corrigindo palavra reservada `user` → `users`
- **Log4j**: Atualizado para 2.23.1 com configuração Jakarta EE compatível
- **Servidor**: Migrado de Tomcat Cargo para Jetty Maven Plugin (desenvolvimento)
- **Namespaces**: Todas as dependências e configurações atualizadas para `jakarta.*`
- **Recursos JSF**: Configuração `jakarta.faces.RESOURCE_EXCLUDES` otimizada para servir CSS, JS e imagens
- **Scripts de gerenciamento**: Criados scripts PowerShell para iniciar/parar servidor
- **Recursos customizados**: Removidos todos os CSS e jQuery personalizados para usar apenas recursos do PrimeFaces

### Sistema de Temas

O CSOnline utiliza temas PrimeFaces para personalizar a aparência da aplicação:

- **Configuração global**: Parâmetro `primefaces.THEME=nova-light` no web.xml
- **Gerenciamento**: Dois sistemas de controle de tema implementados:
  - **ThemeBean (#{themeMB})**: Usado na tela de login
  - **ThemeSwitcherBean (#{themeSwitcherBean})**: Usado na página theme.xhtml
- **Temas disponíveis**: O sistema oferece 32 temas, incluindo:
  - ui-lightness, nova-light, aristo, vader, cupertino, home, etc.
- **Tema padrão**: Configurado via parâmetro `themeDefault` no web.xml
- **Tema do usuário**: Pode ser selecionado na tela de login ou na página de temas

A implementação atual está sendo unificada para usar apenas o ThemeSwitcherBean como gerenciador único de temas.

### Descobertas Importantes da Migração

1. **PrimeFaces 13.x não é Jakarta EE**: Era necessário usar versão 14.0.0 específica
2. **H2 Database**: Palavra `user` é reservada, substituída por `users`
3. **Log4j**: Necessária migração completa da configuração v1.x para v2.x
4. **Jetty Plugin**: Resolveu problemas de cache do Cargo Plugin
5. **Recursos JSF**: Configuração específica para servir corretamente CSS, JS e imagens do PrimeFaces
6. **Sistema de Temas**: Identificada duplicidade de gerenciadores (ThemeBean e ThemeSwitcherBean)

### Arquivos Principais Criados/Atualizados

- `src/main/resources/log4j2.xml` - Configuração completa Log4j 2
- `src/main/resources/data-h2.sql` - Script inicialização H2 compatível
- `src/main/java/.../DatabaseInitializer.java` - Listener inicialização banco
- `src/main/webapp/WEB-INF/web.xml` - Configuração Jakarta EE com recursos JSF e temas
- `src/main/java/br/com/mulato/cso/view/beans/ThemeBean.java` - Gerenciador de temas (login)
- `src/main/java/br/com/mulato/cso/view/bean/ThemeSwitcherBean.java` - Gerenciador de temas (theme.xhtml)
- `pom.xml` - Dependências Jakarta EE 10 e temas PrimeFaces
- `MIGRACAO.md` - Documentação detalhada do processo de migração e problemas identificados
- `start-csonline.ps1` - Script PowerShell para iniciar servidor
- `stop-csonline.ps1` - Script PowerShell para parar servidor

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
