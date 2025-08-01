
# História do Projeto CSOnline

_Linha do tempo, marcos e aprendizados do desenvolvimento do sistema._

## Início e Primeiros Passos

**Início e Primeiros Passos**

- Estruturação inicial do projeto, criação das entidades básicas, primeiros testes e configuração do branch `main`.
- Commits iniciais focados na base do sistema, scripts de banco de dados, configuração Maven e documentação.

## Evolução Arquitetural

**Evolução Arquitetural**

- Migração para Jakarta EE 10, atualização de dependências, namespaces e arquivos de configuração.
- Implementação de controllers e serviços RESTful para Courier, Customer, Delivery, SMS, User e Team.
- Testes unitários e de integração para garantir qualidade das APIs.
- Refino das entidades e scripts SQL para integridade referencial, remoção de cascatas indesejadas e validação de constraints.
- Endpoints REST evoluídos com documentação automática via Swagger/OpenAPI.

## Testes e Qualidade

**Testes e Qualidade**

- Refatoração dos testes para isolamento total: uso de `TestDataFactory` para dados únicos, limpeza do banco antes de cada teste com `TestDatabaseUtil`, padronização dos métodos de setup.
- Melhoria da cobertura de testes: integridade referencial, unicidade de entidades, tratamento de erros.
- Factories para entidades de teste, evitando conflitos e facilitando manutenção.
- Práticas de unicidade (UUID/timestamp) para dados de teste, evitando colisões em execuções paralelas.

## Manutenção e Refino

**Manutenção e Refino**

- Refatorações para legibilidade, confiabilidade e isolamento dos testes.
- Ajustes em logging, scripts SQL e documentação para facilitar desenvolvimento e manutenção.
- Novas entidades (ex: Team), controllers, serviços e testes relacionados.
- Remoção de arquivos/configurações obsoletas para simplificar o setup.
- Melhoria contínua do tratamento de erros, logging e mensagens de exceção para rastreabilidade.

## Integração Contínua e Boas Práticas

**Integração Contínua e Boas Práticas**

- Uso de UUID/timestamp para unicidade de dados de teste.
- Configuração do Maven Surefire para evitar concorrência indesejada nos testes.
- Melhoria do tratamento de erros e logging em toda a aplicação.
- Documentação dos endpoints REST e regras de negócio centralizadas para facilitar onboarding e manutenção.

## Conclusão

## Novos Marcos (Julho/2025)


**Novos Marcos (Julho/2025 a Agosto/2025)**

- Front-end Vue SPA: Implementação do SPA moderno em Vue 3 + Vite, experiência responsiva, navegação dinâmica (login, menu principal, gestão de usuários, logout) e integração futura com autenticação JWT. Estrutura modular, scripts de build automatizados e documentação viva.
- Navegação e usabilidade: Fluxo refinado entre telas, estado reativo, eventos e feedback visual. Menu lateral para acesso rápido às funcionalidades, gestão de usuários com cadastro, edição e exclusão simulados.
- Automação e integração: Scripts PowerShell aprimorados para build/deploy do front-end, logs detalhados e checagem automática do ambiente. Integração contínua backend/frontend facilitando onboarding e manutenção.
- Segurança planejada: Arquitetura do SPA prevê autenticação JWT, proteção de rotas e controle de acesso por perfil, com adendo de segurança documentado e pronto para implementação.
- Documentação ampliada: Evolução do front-end, navegação, integração e segurança registrada nos arquivos técnicos, facilitando entendimento e evolução futura.

O projeto está estável, com deploy automatizado, documentação viva e APIs REST testáveis via Swagger. O acesso ao index.html, Swagger UI e SPA Vue marca o fechamento de um ciclo de evolução e maturidade.

---

## Dificuldades e Desafios Superados

**Dificuldades e Desafios Superados**

- Front-end SPA e integração: Estruturação de componentes, navegação reativa, comunicação entre telas, automação de build/deploy e integração futura com autenticação. Ajuste do fluxo de navegação, eventos e feedback visual exigiu refino e testes constantes.
- Automação multiplataforma: Scripts de build/deploy adaptados para diferentes ambientes (Windows, Linux), logs detalhados e paths relativos para facilitar setup e manutenção.
- Segurança e controle de acesso: Planejamento da autenticação JWT, proteção de rotas e controle de perfis no SPA, estudo e documentação para futuras integrações seguras.
- Documentação viva: Manutenção da documentação técnica atualizada, cobrindo backend e frontend, garantindo rastreabilidade, onboarding rápido e evolução contínua.

Cada obstáculo trouxe aprendizados e fortaleceu a arquitetura do projeto. O resultado é um sistema mais resiliente, testável e pronto para evoluir.

---

## Conto: A Jornada do CSOnline

---

## Conto: A Jornada do CSOnline

No início, o CSOnline era apenas uma ideia: conectar entregadores, clientes e empresas em uma plataforma simples, mas robusta. Os primeiros commits trouxeram vida às entidades básicas e aos testes, mas logo vieram os desafios: migração para Jakarta EE, adaptação de scripts, e a busca incessante por isolamento e unicidade nos dados de teste.

Cada refatoração foi um passo rumo à maturidade: factories para dados únicos, limpeza automática do banco, e testes que cobriam não só o sucesso, mas também os erros e as exceções. A integridade referencial, antes fonte de bugs silenciosos, virou regra explícita e documentada, protegendo o sistema de exclusões indevidas.

Com o tempo, a arquitetura se fortaleceu: logging detalhado, documentação automatizada, e uma cultura de rastreabilidade via git e commits descritivos. O CSOnline tornou-se exemplo de evolução contínua, onde cada obstáculo virou aprendizado e cada teste, um guardião da qualidade.

Hoje, o projeto segue pronto para crescer, com base sólida, regras claras e uma história de superação escrita commit a commit.

---

_Gerado automaticamente a partir do histórico do git na cidade de Campo Largo, PR, sexta-feira, 01 de agosto de 2025._
