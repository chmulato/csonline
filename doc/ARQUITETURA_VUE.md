# Arquitetura do Front-End Vue.js

Este documento apresenta os conceitos fundamentais, estrutura implementada, boas práticas e orientações para manutenção e controle de versões do front-end SPA do CSOnline, desenvolvido em Vue 3 + Vite.

## 1. Conceitos Fundamentais

- **SPA (Single Page Application):** Aplicação de página única com navegação dinâmica entre telas sem recarregar o navegador.
- **Vue 3:** Framework progressivo para construção de interfaces reativas e modulares, usando Composition API.
- **Vite:** Ferramenta moderna de build e desenvolvimento rápido com hot-reload para projetos Vue.
- **Estado Reativo:** Gerenciamento de navegação via estado local usando refs do Vue 3.

## 2. Estrutura de Pastas

```
frontend/
  ├─ src/
  │   ├─ main.js           # Ponto de entrada da aplicação
  │   ├─ App.vue           # Componente raiz com gerenciamento de navegação
  │   └─ components/       # Componentes Vue implementados
  │       ├─ Login.vue     # Tela de autenticação
  │       ├─ MainLayout.vue# Layout principal com menu sanduíche
  │       ├─ UserManagement.vue # Gestão de usuários (CRUD)
  │       └─ Logout.vue    # Tela de saída com feedback visual
  ├─ public/               # Arquivos estáticos públicos
  ├─ index.html            # HTML principal da aplicação
  ├─ package.json          # Dependências npm e scripts
  ├─ package-lock.json     # Lock das versões exatas das dependências
  ├─ vite.config.js        # Configuração do Vite para build e dev
  └─ dist/                 # Arquivos finais gerados pelo build (ignorado no git)

Integração com Backend:
  src/main/webapp/         # Destino dos arquivos do build Vue (copiados pelo script)
  src/build-frontend.ps1   # Script de automação para copiar dist/ → webapp/
```

## 3. Componentização

- Cada funcionalidade é um componente Vue (`.vue`) com template, script setup e estilo scoped isolados.
- Componentes implementados: `Login.vue`, `MainLayout.vue`, `UserManagement.vue`, `Logout.vue`.
- Componentes organizados em `src/components/` e importados no `App.vue`.
- Comunicação entre componentes via eventos (`emit`) e props.
- Estado global gerenciado no componente raiz `App.vue` para navegação.

## 4. Navegação Implementada

- Navegação controlada por estado reativo no `App.vue` (isLogged, isLogout, isUserMgmt).
- Fluxo: Login → Tela Principal → Gestão de Usuários / Logout → Login.
- Menu sanduíche lateral com navegação entre funcionalidades via eventos.
- Feedback visual com mensagens de confirmação e animações CSS.
- Estrutura preparada para futura integração com Vue Router.

## 5. Comunicação com Backend

- Requisições HTTP via `fetch` API nativa (preparado para Axios se necessário).
- Endpoints REST expostos pelo backend Java/Jakarta EE em `/api/*`.
- Autenticação planejada via JWT com controle de acesso por perfil (admin, user, courier, customer).
- Gestão de usuários simulada localmente, preparada para integração com API real.
- Logs de desenvolvimento restritos ao ambiente dev (função `devLog`).

## 6. Integração e Deploy

- Front-end integrado ao WAR do backend via script `build-frontend.ps1`.
- Build do Vue (`npm run build`) gera arquivos estáticos otimizados em `dist/`.
- Script PowerShell copia automaticamente `dist/` para `src/main/webapp/`.
- Servido pelo WildFly junto ao backend em `http://localhost:8080/csonline/`.
- Desenvolvimento isolado possível via `npm run dev` em `http://localhost:5173`.
- Hot-reload e debugging via Vite durante desenvolvimento.
- Estrutura de pastas mantém separação clara entre frontend e backend.

## 7. Controle de Versões

- Front-end versionado junto ao backend no repositório Git (branch `main`).
- Commits descritivos com prefixos: `feat:`, `fix:`, `docs:`, `style:`.
- Estrutura de branches recomendada:
  - `feature/nome-da-feature` para novas funcionalidades
  - `fix/nome-do-bug` para correções
  - `docs/atualizacao` para documentação
- Pull Requests para revisão e integração na branch principal.

## 8. Build e Deploy Automatizado

- `npm install` para instalar dependências Vue/Vite.
- `npm run dev` para desenvolvimento local com hot-reload.
- `npm run build` para gerar arquivos finais otimizados em `dist/`.
- `pwsh ./src/build-frontend.ps1` para integração automática com o backend WAR.
- Deploy conjunto backend+frontend via scripts PowerShell automatizados.

## 9. Boas Práticas Implementadas

- **Componentes modulares:** Cada tela é um componente focado em uma responsabilidade específica.
- **Logging controlado:** Uso da função `devLog` para logs apenas em ambiente de desenvolvimento.
- **Estilo scoped:** CSS isolado por componente evitando conflitos de estilo.
- **Eventos e comunicação clara:** Uso de `emit` para comunicação entre componentes pai/filho.
- **Navegação consistente:** Estado centralizado no `App.vue` para fluxo previsível.
- **Documentação viva:** Atualização contínua dos arquivos técnicos a cada nova funcionalidade.

## 10. Segurança Planejada

- **Autenticação JWT:** Integração futura com backend para tokens seguros.
- **Proteção de rotas:** Controle de acesso baseado em perfil do usuário.
- **Validação client-side:** Nunca confiar apenas no front-end para segurança.
- **HTTPS obrigatório:** Uso de SSL/TLS em produção.
- **Sanitização de dados:** Evitar XSS e injeção de código malicioso.

## 11. Próximos Passos

- Integração com autenticação JWT do backend.
- Implementação de proteção de rotas via Vue Router.
- Evolução da gestão de usuários para operações reais via API.
- Adição de novas funcionalidades: entregas, clientes, preços, SMS.
- Testes automatizados para componentes Vue.

## 12. Referências

- [Documentação Vue 3](https://vuejs.org/)
- [Documentação Vite](https://vitejs.dev/)
- [Composition API](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Guia SPA](https://developer.mozilla.org/en-US/docs/Glossary/SPA)

---

*Documento atualizado em agosto/2025. Revisado conforme implementação atual do SPA e preparado para evoluções futuras.*
