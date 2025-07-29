# História do Projeto CSOnline (Resumo do Git)

## Início e Primeiros Passos
- O projeto começou com a estruturação inicial, entidades básicas, testes e configuração do branch `service_restfull`.
- Primeiros commits focaram em criar a base do sistema, scripts de banco de dados, configuração Maven e documentação inicial.

## Evolução Arquitetural
- Migração para Jakarta EE 10, atualização de dependências, namespaces e adaptação de arquivos de configuração.
- Implementação de controllers e serviços RESTful para entidades principais: Courier, Customer, Delivery, SMS, User e Team.
- Inclusão de testes unitários e de integração para garantir a qualidade das APIs.
- Refino das entidades e scripts SQL para garantir integridade referencial, removendo cascatas indesejadas e validando constraints de banco.
- Evolução dos endpoints REST com documentação automática via Swagger/OpenAPI.

## Testes e Qualidade
- Refatoração dos testes para garantir isolamento total: uso de `TestDataFactory` para dados únicos, limpeza do banco antes de cada teste com `TestDatabaseUtil`, e padronização dos métodos de setup.
- Melhoria da cobertura de testes, incluindo casos de integridade referencial, unicidade de entidades e tratamento de erros.
- Implementação de factories para entidades de teste, evitando conflitos de dados e facilitando a manutenção dos testes.
- Adoção de práticas de unicidade (UUID/timestamp) para dados de teste, evitando colisões em execuções paralelas.

## Manutenção e Refino
- Diversas refatorações para melhorar legibilidade, confiabilidade e isolamento dos testes.
- Ajustes em configurações de logging, scripts SQL, e documentação para facilitar o desenvolvimento e a manutenção.
- Inclusão de novas entidades (ex: Team), controllers, serviços e testes relacionados.
- Remoção de arquivos e configurações obsoletas para simplificar o setup do projeto.
- Melhoria contínua do tratamento de erros, logging e mensagens de exceção para rastreabilidade.

## Integração Contínua e Boas Práticas
- Adoção de práticas como uso de UUID/timestamp para unicidade de dados de teste.
- Configuração do Maven Surefire para evitar concorrência indesejada nos testes.
- Melhoria do tratamento de erros e logging em toda a aplicação.
- Documentação dos endpoints REST e regras de negócio centralizadas para facilitar onboarding e manutenção.

## Conclusão
O projeto evoluiu de uma base simples para uma arquitetura robusta, com forte foco em testes automatizados, isolamento de dados, unicidade de informações e boas práticas de desenvolvimento. Cada etapa do git mostra o compromisso com qualidade, refino contínuo, rastreabilidade e adaptação às melhores tecnologias do ecossistema Java/Jakarta EE.

---


## Dificuldades e Desafios Superados

Ao longo da jornada do CSOnline, enfrentamos muitos desafios típicos de projetos de software robustos:

- **Migração de tecnologias:** A transição do Java EE para Jakarta EE exigiu adaptações profundas em dependências, namespaces e configurações, além de ajustes em bibliotecas de terceiros.
- **Isolamento de testes:** Garantir que cada teste rodasse de forma independente, sem "sujar" o banco ou depender de dados de outros testes, foi um desafio constante. Isso exigiu a criação de utilitários de limpeza e fábricas de dados.
- **Integridade referencial:** Lidar com constraints do banco, especialmente em deleções em cascata e entidades relacionadas, gerou muitos erros difíceis de rastrear, exigindo refino nas entidades, scripts SQL e regras explícitas na camada de serviço para garantir mensagens claras ao usuário.
- **Concorrência em testes:** Problemas de concorrência e dados compartilhados em execuções paralelas de testes levaram à configuração cuidadosa do Maven Surefire e à revisão de todos os métodos de setup.
- **Evolução de requisitos:** Mudanças de escopo, inclusão de novas entidades e endpoints REST exigiram refatorações frequentes, sempre mantendo a compatibilidade e a cobertura de testes.
- **Logging e rastreabilidade:** Garantir logs claros e úteis para depuração, sem poluir o ambiente de produção, foi um ajuste fino entre configurações e boas práticas.

Cada obstáculo trouxe aprendizados e fortaleceu a arquitetura do projeto. O resultado é um sistema mais resiliente, testável e pronto para evoluir.

---

## Conto: A Jornada do CSOnline

No início, o CSOnline era apenas uma ideia: conectar entregadores, clientes e empresas em uma plataforma simples, mas robusta. Os primeiros commits trouxeram vida às entidades básicas e aos testes, mas logo vieram os desafios: migração para Jakarta EE, adaptação de scripts, e a busca incessante por isolamento e unicidade nos dados de teste.

Cada refatoração foi um passo rumo à maturidade: factories para dados únicos, limpeza automática do banco, e testes que cobriam não só o sucesso, mas também os erros e as exceções. A integridade referencial, antes fonte de bugs silenciosos, virou regra explícita e documentada, protegendo o sistema de exclusões indevidas.

Com o tempo, a arquitetura se fortaleceu: logging detalhado, documentação automatizada, e uma cultura de rastreabilidade via git e commits descritivos. O CSOnline tornou-se exemplo de evolução contínua, onde cada obstáculo virou aprendizado e cada teste, um guardião da qualidade.

Hoje, o projeto segue pronto para crescer, com base sólida, regras claras e uma história de superação escrita commit a commit.

---

*Gerado automaticamente a partir do histórico do git na cidade de Campo Largo, Paraná, em 29 de julho de 2025.*
