# csonline

Projeto Java para gestão de entregas, clientes, usuários, preços e SMS.

## Principais Tecnologias

- Jakarta EE 10
- JPA (Jakarta Persistence API)
- EclipseLink (JPA Provider)
- Jersey (JAX-RS)
- HSQLDB (HyperSQL Database)
- Log4j 2
- Swagger/OpenAPI
- JUnit 5, Mockito

## Como executar



## Como executar

### Ambiente recomendado: Payara Server 6 (Windows/PowerShell)

Scripts automatizados estão disponíveis na raiz do projeto:

1. **Preparar o artefato WAR:**
   ```powershell
   pwsh ./prepare-artifact.ps1 [-DskipTests]
   ```
   Gera o arquivo `target/csonline-1.0-SNAPSHOT.war`.


2. **Iniciar o Payara Server:**
   ```powershell
   pwsh ./start-payara.ps1 [dominio]
   ```
   Inicia o domínio informado (padrão: domain1) do Payara em http://localhost:8080/.

3. **Deploy do WAR:**
   ```powershell
   pwsh ./deploy-payara.ps1 [dominio]
   ```
   Copia o WAR para a pasta de autodeploy do domínio informado (padrão: domain1). O deploy é feito automaticamente.

4. **Parar o Payara Server:**
   ```powershell
   pwsh ./stop-payara.ps1 [dominio]
   ```
   Para o domínio informado (padrão: domain1) do Payara.

> **Dica:**
> Se você criou um novo domínio (ex: domain2), basta passar o nome como argumento nos scripts acima:
> ```powershell
> pwsh ./start-payara.ps1 domain2
> pwsh ./deploy-payara.ps1 domain2
> pwsh ./stop-payara.ps1 domain2
> ```

> Para criar um novo domínio com senha mestre salva, use:
> ```powershell
> server\payara6\bin\asadmin create-domain --savemasterpassword=true domain2
> ```
> Depois, utilize normalmente nos scripts.

---

### Alternativas

#### Tomcat 10+
1. Baixe o Tomcat 10 em: https://tomcat.apache.org/download-10.cgi
2. Extraia o Tomcat em uma pasta.
3. Copie o arquivo `target/csonline-1.0-SNAPSHOT.war` para a pasta `webapps` do Tomcat.
4. Inicie o Tomcat com `bin/startup.bat` (Windows) ou `bin/startup.sh` (Linux/Mac).
5. Acesse: http://localhost:8080/

#### Payara Micro (Jakarta EE 10)
1. Baixe o Payara Micro: https://www.payara.fish/downloads/payara-micro/
2. Execute:
   ```bash
   java -jar payara-micro*.jar --deploy target/csonline-1.0-SNAPSHOT.war
   ```
3. Acesse: http://localhost:8080/

---

## Logging

Os logs são gravados em `logs/app.log` (configurável via `log4j2.xml`).

## Documentação da API

Swagger disponível em `/api/openapi.json`.

Acesse a interface Swagger UI em:  
`http://localhost:8080/api/openapi.json`
(ajuste a porta conforme sua configuração Tomcat)

Os endpoints REST estão disponíveis em:  
- `/api/users`
- `/api/customers`
- `/api/couriers`
- `/api/deliveries`
- `/api/sms`
- `/api/login`

## Documentação Completa

Consulte o arquivo [INDEX.md](doc/INDEX.md) para uma documentação detalhada do projeto.

## Estrutura de Pastas

- `src/main/java/com/caracore/cso/controller` - Controllers REST
- `src/main/java/com/caracore/cso/service` - Serviços de negócio
- `src/main/java/com/caracore/cso/repository` - Repositórios JPA/EclipseLink
- `src/main/java/com/caracore/cso/entity` - Entidades JPA
- `src/main/resources` - Configurações (ex: `log4j2.xml`, `persistence.xml`)
- `src/test/java` - Testes unitários

## Configuração

- Edite `src/main/resources/log4j2.xml` para ajustar o log.
- Edite `src/main/resources/persistence.xml` para configurar JPA/EclipseLink.
- Banco de dados HSQLDB em memória por padrão (configurável em `src/main/resources/persistence.xml` e `application.properties`).

## Contato

Para dúvidas ou sugestões, abra uma issue.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
