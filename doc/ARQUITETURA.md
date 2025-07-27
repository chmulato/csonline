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
  - Implementam regras de negócio e controle de acesso.
  - Validam permissões e encapsulam lógica de autorização.

- **Testes (Test):**
  - Testes unitários para cada serviço e repositório.
  - Cobertura de regras de negócio e persistência.

- **Documentação (doc):**
  - Documentos de regras, arquitetura, dados iniciais e índice.

## Fluxo de Dados
1. Usuário interage via interface JSF.
2. JSF aciona serviços, que validam regras e acessam repositórios.
3. Repositórios manipulam entidades e persistem dados no H2 via Hibernate.
4. Respostas e exceções são tratadas na camada de serviço.

## Tecnologias
- Java 11
- Jakarta EE 10
- Hibernate ORM 6.4.4.Final
- H2 Database
- JSF (Mojarra)
- JUnit 5
- JaCoCo
- Jetty Embedded

## Diagrama Simplificado

```
[JSF] → [Service] → [Repository] → [Entity] → [H2 Database]
```

## Observações
- Todas as operações sensíveis lançam DAOException.
- Regras de autorização centralizadas na camada de serviço.
- Documentação e exemplos disponíveis em `doc/INDEX.md`.
