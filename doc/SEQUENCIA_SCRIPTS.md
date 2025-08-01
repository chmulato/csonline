
# Sequência Recomendada de Execução dos Scripts (WildFly 31)

Este documento apresenta a ordem real e recomendada dos scripts PowerShell utilizados para build, deploy, configuração e troubleshooting do ambiente da aplicação csonline no WildFly 31. A sequência foi validada com sucesso no deploy de julho/2025 e atualizada em agosto/2025.

## Fluxo Principal (Backend + Frontend)

### 1. Preparar o Front-end Vue SPA

```powershell
cd frontend
npm install
npm run build
cd ..
pwsh ./src/build-frontend.ps1
```

Instala dependências, gera o build do SPA Vue e copia para `src/main/webapp/`.

### 2. Preparar o artefato WAR e copiar para o WildFly

```powershell
pwsh ./prepare-artifact-wildfly.ps1 [-DskipTests]
```

Gera o arquivo `target/csonline.war` (incluindo o front-end) e copia para `server/wildfly-31.0.1.Final/standalone/deployments`.

### 3. Configurar o Driver JDBC e DataSource

```powershell
pwsh ./config-wildfly-31.ps1
```

O script copia o driver JDBC e configura o DataSource padrão (exemplo: HSQLDB). Para configurar apenas o driver, use o parâmetro `-SomenteDriver`.

### 4. Configurar o log customizado da aplicação

```powershell
pwsh ./config-log-wildfly-31.ps1
```

Cria o diretório de logs e configura o handler para gravar logs da aplicação em `logs/app.log`.

### 5. Configurar HTTPS/SSL (opcional)

```powershell
pwsh ./config-ssl-wildfly-31.ps1
```

Gera um certificado autoassinado, configura o HTTPS no WildFly (porta 8443) e orienta sobre reinício do servidor.

### 6. Iniciar o WildFly

```powershell
pwsh ./start-wildfly-31.ps1
```

Inicia o WildFly 31 em http://localhost:8080/.

## Scripts Auxiliares

### Deploy manual do WAR (opcional)

```powershell
pwsh ./deploy-wildfly-31.ps1
```

Copia o WAR para a pasta deployments do WildFly a qualquer momento (útil para hot deploy sem rebuild).

### Parar o WildFly

```powershell
pwsh ./stop-wildfly-31.ps1
```

Para o WildFly 31.

## Desenvolvimento Front-end (apenas)

Para desenvolvimento isolado do SPA Vue:

```powershell
cd frontend
npm run dev
```

Acesse em http://localhost:5173 (porta padrão Vite).

---

## Troubleshooting e Acesso

- Se ocorrer erro de deploy, verifique o log em `server/wildfly-31.0.1.Final/standalone/log/server.log`.
- O acesso ao sistema é feito por:
  - Página principal (SPA Vue): http://localhost:8080/csonline/index.html
  - Swagger UI: http://localhost:8080/csonline/swagger-ui/index.html
  - OpenAPI JSON: http://localhost:8080/csonline/api/openapi.json
- Scripts agora usam paths relativos, logs detalhados e checagem automática do Java.
- Para desenvolvimento front-end isolado, use `npm run dev` na pasta `frontend/`.

---

**Observação:**

- Execute os scripts sempre a partir da raiz do projeto.
- Scripts opcionais só são necessários se você quiser customizar o ambiente além do padrão.
- Para projetos com front-end, sempre execute o build do Vue antes do WAR.
- Consulte o README e os demais documentos da pasta `doc` para detalhes de cada etapa.
- Para troubleshooting detalhado, consulte também o histórico do git e o arquivo `HISTORIA_DO_PROJETO.md`.
