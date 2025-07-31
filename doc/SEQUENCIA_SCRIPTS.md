# Sequência Recomendada de Execução dos Scripts (WildFly 31)

Este documento orienta a ordem correta de execução dos scripts PowerShell para build, deploy e configuração do ambiente da aplicação csonline no WildFly 31.

## 1. Preparar o artefato WAR e copiar para o WildFly

```powershell
pwsh ./prepare-artifact-wildfly.ps1 [-DskipTests]
```

Gera o arquivo `target/csonline.war` e copia para `server/wildfly-31.0.1.Final/standalone/deployments`.

## 2. Configurar o DataSource JDBC (opcional, apenas se usar banco externo ou customizado)

```powershell
pwsh ./config-wildfly-31.ps1
```

Copia o driver JDBC e configura o DataSource no WildFly (exemplo para HSQLDB).

## 3. Configurar o log customizado da aplicação (opcional)

```powershell
pwsh ./config-log-wildfly-31.ps1
```

Cria o diretório de logs e configura o handler para gravar logs da aplicação em `logs/app.log`.

## 4. Configurar HTTPS/SSL (opcional)

```powershell
pwsh ./config-ssl-wildfly-31.ps1
```

Gera um certificado autoassinado, configura o HTTPS no WildFly (porta 8443) e orienta sobre reinício do servidor.

## 5. Iniciar o WildFly

```powershell
pwsh ./start-wildfly-31.ps1
```

Inicia o WildFly 31 em http://localhost:8080/.

## 6. Parar o WildFly

```powershell
pwsh ./stop-wildfly-31.ps1
```

Para o WildFly 31.

## 7. Deploy manual do WAR (opcional)

```powershell
pwsh ./deploy-wildfly-31.ps1
```

Copia o WAR para a pasta deployments do WildFly a qualquer momento.

---

**Observação:**

- Execute os scripts sempre a partir da raiz do projeto.
- Scripts opcionais só são necessários se você quiser customizar o ambiente além do padrão.
- Consulte o README e os demais documentos da pasta `doc` para detalhes de cada etapa.
