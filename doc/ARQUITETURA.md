# ARQUITETURA.md

## Visão Geral
O CSOnline é um sistema modular para gestão de entregas, desenvolvido em Java 11, utilizando Jakarta EE 10, Hibernate ORM, H2 Database e JSF. O projeto segue boas práticas de separação de camadas e responsabilidade.


## Camadas do Sistema

- **Entidades (Entity):**
  - Representam os dados persistidos no banco (User, Customer, Courier, Delivery, Price, SMS).
  - Utilizam JPA para mapeamento objeto-relacional.

- **Repositórios (Repository):**
  - Responsáveis pelo acesso e manipulação dos dados via Hibernate.
  - Utilizam SessionFactory para operações CRUD.

- **Serviços (Service):**
  - Implementam regras de negócio, controle de acesso e segurança (LoginService).
  - Validam permissões e encapsulam lógica de autorização centralizada.

- **RESTful (Controller/DTO):**
  - Exposição dos serviços via endpoints REST usando Jakarta EE 10/JAX-RS.
  - Controllers para autenticação e acesso aos serviços.
  - DTOs para transporte seguro de dados entre camadas e APIs.
  - Documentação automática dos endpoints REST via Swagger/OpenAPI.
  - Especificação OpenAPI disponível em `/api/openapi.json`.
  - Pode ser visualizada via Swagger UI ou [Swagger Editor Online](https://editor.swagger.io/).

- **Testes (Test):**
  - Testes unitários para cada serviço, repositório e controller REST.
  - Cobertura de regras de negócio, persistência e endpoints REST.
  - Uso de Jersey como provedor JAX-RS para testes REST.

- **Documentação (doc):**
  - Documentos de regras, arquitetura, dados iniciais e índice.


## Fluxo de Dados
1. Usuário interage via interface JSF ou via API REST.
2. JSF ou REST aciona serviços, que validam regras, segurança e acessam repositórios.
3. Repositórios manipulam entidades e persistem dados no H2 via Hibernate.
4. Respostas e exceções são tratadas na camada de serviço ou controller REST.


## Tecnologias
- Java 11
- Jakarta EE 10
- Hibernate ORM 6.4.4.Final
- H2 Database
- JSF (Mojarra)
- JUnit 5
- JaCoCo
- Jetty Embedded
- Tomcat Embedded 10.1.x
- Jersey (JAX-RS provider para testes REST)


## Diagrama Simplificado

```
[JSF] → [Service] → [Repository] → [Entity] → [H2 Database]
[REST Controller] → [Service] → [Repository] → [Entity] → [H2 Database]
```


## Observações
- Todas as operações sensíveis lançam DAOException.
- Regras de autorização centralizadas na camada de serviço.
- Segurança implementada via LoginService e controle de acesso por roles.
- Testes REST utilizam Jersey como provedor JAX-RS.
- Dados iniciais são carregados via import.sql (um insert por linha).
- Testes unitários utilizam IDs altos para evitar conflitos com dados iniciais.
- Documentação e exemplos disponíveis em `doc/INDEX.md`.
