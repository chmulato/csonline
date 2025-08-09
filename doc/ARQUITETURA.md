# ARQUITETURA.md

## Visão Geral
O CSOnline é um sistema completo para gestão de entregas, desenvolvido com frontend Vue 3 SPA e backend Jakarta EE 10. O sistema utiliza HSQLDB em modo arquivo (com opção de execução via Docker Compose), migrações controladas pelo Flyway e inclui suite completa de testes automatizados. O projeto segue boas práticas de separação de camadas, responsabilidades bem definidas e arquitetura moderna.


## Camadas do Sistema

**Frontend (Vue 3 SPA):**
- Interface moderna e responsiva desenvolvida em Vue 3
- Navegação SPA fluida entre 7 módulos principais
- Componentes reutilizáveis e gerenciamento de estado centralizado
- Build otimizado com Vite e integração automática com o backend

**Entidades (Entity):**
- Representam os dados persistidos no banco (User, Customer, Courier, Delivery, Price, SMS, Team)
- Utilizam JPA para mapeamento objeto-relacional
- Relacionamentos tratados com anotações Jackson para evitar referências circulares

**Repositórios (Repository):**
- Responsáveis pelo acesso e manipulação dos dados via JPA/EclipseLink
- Utilizam EntityManager para operações CRUD
- Integração com transações JTA gerenciadas pelo WildFly

**Serviços (Service):**
- Implementam regras de negócio, controle de acesso e segurança
- Validam permissões e encapsulam lógica de autorização centralizada
- Gerenciamento de transações e tratamento de exceções

**Controllers REST:**
- Exposição dos serviços via endpoints REST usando Jakarta EE 10/JAX-RS
- Controllers para autenticação e acesso aos serviços de todas as entidades
- DTOs para transporte seguro de dados entre camadas e APIs
- Documentação automática dos endpoints REST via Swagger/OpenAPI
- Especificação OpenAPI disponível em `/api/openapi.json`
- Interface Swagger UI acessível em `/csonline/swagger-ui/index.html`

**Banco de Dados e Migrações:**
- HSQLDB em modo arquivo integrado ao WildFly (opção Docker disponível)
- Migrações de banco controladas pelo Flyway com versionamento automático
- Configuração flexível para desenvolvimento e produção

**Testes Automatizados:**
- Suite completa de testes para todos os endpoints REST
- Scripts individuais para cada controller (`test-users.ps1`, `test-customers.ps1`, etc.)
- Ferramentas de automação (`test-all-endpoints.ps1`, `health-check-endpoints.ps1`)
- Verificação de saúde e diagnóstico de problemas
- Cobertura de operações CRUD completas

**Documentação:**
- Documentos técnicos organizados na pasta `doc/`
- Índice centralizado e categorizado
- Documentação de APIs, arquitetura e regras de negócio


## Fluxo de Dados e Operações

A aplicação CSOnline segue um fluxo clássico de arquitetura em camadas onde os dados transitam de forma controlada entre as diferentes camadas do sistema.

**Entrada de Dados:**
O frontend Vue 3 SPA envia requisições HTTP para os endpoints REST da aplicação. Cada requisição é recebida pelos controllers específicos que validam os dados de entrada.

**Processamento:**
Os controllers delegam a lógica de negócio para os services, que aplicam as regras específicas do domínio. Os services utilizam os repositories para acesso aos dados, que por sua vez interagem com o banco HSQLDB via JPA.

**Persistência:**
O banco de dados está containerizado com Docker e utiliza volumes persistentes. As migrações são gerenciadas automaticamente pelo Flyway, garantindo versionamento e integridade estrutural.

**Resposta:**
Os dados processados retornam através das mesmas camadas até chegarem ao frontend como respostas JSON, proporcionando uma interface moderna e responsiva ao usuário.


## Tecnologias Utilizadas

O CSOnline é um sistema moderno para gestão de entregas construído com tecnologias enterprise consolidadas e ferramentas de desenvolvimento atuais.

**Backend:**
- **Jakarta EE 10** - Framework enterprise para desenvolvimento Java
- **WildFly 31** - Servidor de aplicação robusto e otimizado
- **EclipseLink (JPA)** - Mapeamento objeto-relacional
- **JAX-RS (Jersey)** - APIs REST padronizadas
- **CDI (Contexts and Dependency Injection)** - Injeção de dependências

**Frontend:**
- **Vue 3 SPA** - Interface de usuário moderna e responsiva
- **JavaScript ES6+** - Desenvolvimento frontend atual

**Banco de Dados:**
- **HSQLDB** - Banco de dados relacional em memória/arquivo
- **Docker** - Containerização para isolamento e portabilidade
- **Flyway 8.5.13** - Controle de versão e migrações de banco

**Ferramentas de Desenvolvimento:**
- **Maven** - Gerenciamento de dependências e build
- **JaCoCo** - Cobertura de código e relatórios de teste
- **OpenAPI/Swagger** - Documentação automática de APIs
- **PowerShell** - Scripts de automação e testes
## Diagrama da Arquitetura

A arquitetura do CSOnline segue o padrão de camadas bem definidas, proporcionando separação clara de responsabilidades:

```
[Vue 3 SPA Frontend] 
        ↓ HTTP/REST
[JAX-RS Controllers] 
        ↓ Business Logic
[Services Layer] 
        ↓ Data Access
[Repository Layer] 
        ↓ JPA/EclipseLink
[HSQLDB Database in Docker]
```

**Fluxo de Dados:**
- Frontend Vue 3 → Controllers REST → Services → Repositories → Banco HSQLDB
- Respostas seguem o caminho inverso com dados JSON estruturados

**Integração:**
- APIs REST documentadas via OpenAPI/Swagger
- Containerização completa com Docker Compose
- Migrações automáticas via Flyway

## Considerações Técnicas

**Tratamento de Erros:**
- Todas as operações críticas utilizam exceções personalizadas (DAOException)
- Tratamento centralizado de erros nos controllers REST
- Logs estruturados para monitoramento e debug

**Inicialização de Dados:**
- Migrações com Flyway (V1/V2) aplicadas no ambiente
- `import.sql` opcional para cenários de desenvolvimento
- Ambiente containerizado (opcional) garante consistência entre deploys

**APIs e Documentação:**
- Especificação OpenAPI disponível em `/api/openapi.json`
- Interface Swagger UI em `/csonline/swagger-ui/index.html`
- Documentação técnica centralizada em `doc/INDEX.md`

**Testes e Qualidade:**
- Suite completa de testes automatizados via PowerShell
- Cobertura de código monitorada pelo JaCoCo
- Testes de integração para todos os endpoints REST
- Verificação contínua de saúde da aplicação
