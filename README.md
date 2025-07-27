# csonline

Projeto Java para gestão de entregas, clientes, usuários, preços e SMS.

## Principais Tecnologias

- Jakarta EE 10
- Hibernate ORM 6
- Jersey (JAX-RS)
- H2 Database
- Log4j 2
- Swagger/OpenAPI
- JUnit 5, Mockito

## Como executar

```bash
mvn clean install
mvn tomcat10:run
```

## Testes

```bash
mvn test
```

## Logging

Os logs são gravados em `logs/app.log` (configurável via `log4j2.xml`).

## Documentação da API

Swagger disponível em `/api/openapi.json`.

Acesse a interface Swagger UI em:  
`http://localhost:8080/api/openapi.json`  
(ou ajuste a porta conforme sua configuração Tomcat/Jetty)

Os endpoints REST estão disponíveis em:  
- `/api/users`
- `/api/customers`
- `/api/couriers`
- `/api/deliveries`
- `/api/sms`
- `/api/login`

## Estrutura de Pastas

- `src/main/java/com/caracore/cso/controller` - Controllers REST
- `src/main/java/com/caracore/cso/service` - Serviços de negócio
- `src/main/java/com/caracore/cso/repository` - Repositórios de acesso a dados
- `src/main/java/com/caracore/cso/entity` - Entidades JPA
- `src/main/resources` - Configurações (ex: `log4j2.xml`)
- `src/test/java` - Testes unitários

## Configuração

- Edite `src/main/resources/log4j2.xml` para ajustar o log.
- Banco de dados H2 em memória por padrão.

## Contato

Para dúvidas ou sugestões, abra uma issue.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
