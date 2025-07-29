# História do Projeto CSOnline (Resumo do Git)

## Início e Primeiros Passos
- O projeto começou com a estruturação inicial, entidades básicas, testes e configuração do branch `service_restfull`.
- Primeiros commits focaram em criar a base do sistema, scripts de banco de dados, configuração Maven e documentação inicial.

## Evolução Arquitetural
- Migração para Jakarta EE 10, atualização de dependências, namespaces e adaptação de arquivos de configuração.
- Implementação de controllers e serviços RESTful para entidades principais: Courier, Customer, Delivery, SMS e User.
- Inclusão de testes unitários e de integração para garantir a qualidade das APIs.

## Testes e Qualidade
- Refatoração dos testes para garantir isolamento total: uso de `TestDataFactory` para dados únicos, limpeza do banco antes de cada teste com `TestDatabaseUtil`, e padronização dos métodos de setup.
- Melhoria da cobertura de testes, incluindo casos de integridade referencial e tratamento de erros.
- Implementação de factories para entidades de teste, evitando conflitos de dados e facilitando a manutenção dos testes.

## Manutenção e Refino
- Diversas refatorações para melhorar legibilidade, confiabilidade e isolamento dos testes.
- Ajustes em configurações de logging, scripts SQL, e documentação para facilitar o desenvolvimento e a manutenção.
- Inclusão de novas entidades (ex: Team), controllers, serviços e testes relacionados.

## Integração Contínua e Boas Práticas
- Adoção de práticas como uso de UUID/timestamp para unicidade de dados de teste.
- Configuração do Maven Surefire para evitar concorrência indesejada nos testes.
- Melhoria do tratamento de erros e logging em toda a aplicação.

## Conclusão
O projeto evoluiu de uma base simples para uma arquitetura robusta, com forte foco em testes automatizados, isolamento de dados e boas práticas de desenvolvimento. Cada etapa do git mostra o compromisso com qualidade, refino contínuo e adaptação às melhores tecnologias do ecossistema Java/Jakarta EE.

---


## Dificuldades e Desafios Superados

Ao longo da jornada do CSOnline, enfrentamos muitos desafios típicos de projetos de software robustos:

- **Migração de tecnologias:** A transição do Java EE para Jakarta EE exigiu adaptações profundas em dependências, namespaces e configurações, além de ajustes em bibliotecas de terceiros.
- **Isolamento de testes:** Garantir que cada teste rodasse de forma independente, sem "sujar" o banco ou depender de dados de outros testes, foi um desafio constante. Isso exigiu a criação de utilitários de limpeza e fábricas de dados.
- **Integridade referencial:** Lidar com constraints do banco, especialmente em deleções em cascata e entidades relacionadas, gerou muitos erros difíceis de rastrear, exigindo refino nas entidades e scripts SQL.
- **Concorrência em testes:** Problemas de concorrência e dados compartilhados em execuções paralelas de testes levaram à configuração cuidadosa do Maven Surefire e à revisão de todos os métodos de setup.
- **Evolução de requisitos:** Mudanças de escopo, inclusão de novas entidades e endpoints REST exigiram refatorações frequentes, sempre mantendo a compatibilidade e a cobertura de testes.
- **Logging e rastreabilidade:** Garantir logs claros e úteis para depuração, sem poluir o ambiente de produção, foi um ajuste fino entre configurações e boas práticas.

Cada obstáculo trouxe aprendizados e fortaleceu a arquitetura do projeto. O resultado é um sistema mais resiliente, testável e pronto para evoluir.

---

*Gerado automaticamente a partir do histórico do git em julho de 2025.*
