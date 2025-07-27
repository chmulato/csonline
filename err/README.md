# CSOnline - Sistema de Gestão de Entregas
# Consulte o índice de documentação em [doc/INDEX.md](../doc/INDEX.md)

## Descrição
CSOnline é um sistema de gestão de entregas desenvolvido em Java 11, utilizando Jakarta EE 10, Hibernate ORM, H2 Database e JSF (Mojarra). O projeto é modular, com camada de entidades, persistência, serviços e testes automatizados.

## Tecnologias
- Java 11
- Jakarta EE 10 (jakarta.jakartaee-api)
- Hibernate ORM 6.4.4.Final
- H2 Database
- JSF (Mojarra)
- JUnit 5
- JaCoCo
- Jetty Embedded

## Estrutura do Projeto
- `src/main/java/com/caracore/cso/entity` — Entidades JPA
- `src/main/java/com/caracore/cso/repository` — Repositórios e utilitários Hibernate
- `src/main/java/com/caracore/cso/service` — Camada de serviços e regras de negócio
- `src/test/java/com/caracore/cso/service` — Testes unitários dos serviços
- `doc/` — Documentação técnica e de negócio

## Como Executar
1. Instale o Java 11 e Maven.
2. Execute `mvn clean install` para compilar e rodar os testes.
3. O projeto gera o arquivo WAR em `target/csonline.war`.
4. Faça o deploy do WAR em um servidor Jakarta EE 10 (Payara, WildFly, GlassFish, etc).
5. O banco H2 é configurado automaticamente e os dados iniciais são carregados via `import.sql`.
  
## Documentação dos Endpoints REST (Swagger/OpenAPI)
Todos os endpoints REST estão documentados automaticamente via Swagger/OpenAPI.
- Acesse a especificação OpenAPI em: `http://localhost:8080/api/openapi.json`
- Para visualizar via interface gráfica, utilize o Swagger UI apontando para essa URL.
  - Exemplo: [Swagger Editor Online](https://editor.swagger.io/) → "File > Import URL" → cole o link acima.

### Endpoints REST disponíveis
- `/api/team` — Equipes
- `/api/couriers` — Entregadores
- `/api/customers` — Clientes
- `/api/deliveries` — Entregas
- `/api/users` — Usuários
- `/api/sms` — Mensagens SMS
- `/api/login` — Autenticação

Todos aceitam e retornam JSON.

### Endpoints REST
- Exemplo de login: `POST /api/login` (JSON)
  ```json
  {
    "login": "usuario",
    "password": "senha"
  }
  ```
- Retorno: dados do usuário autenticado ou erro 401.

## Documentação
- [doc/INDEX.md](../doc/INDEX.md) — Índice dos documentos técnicos
- [doc/REGRAS_DE_NEGOCIO.md](../doc/REGRAS_DE_NEGOCIO.md) — Regras de negócio e exemplos

## Contribuição
Sugestões e melhorias são bem-vindas! Abra uma issue ou envie um pull request.

## Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](../LICENSE) para mais detalhes.
