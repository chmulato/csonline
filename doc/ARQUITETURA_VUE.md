# Introdução à Arquitetura do Front-End Vue.js

Este documento apresenta os conceitos fundamentais, estrutura, boas práticas e orientações para manutenção e controle de versões do front-end SPA do CSOnline, desenvolvido em Vue 3 + Vite.

## 1. Conceitos Fundamentais

- **SPA (Single Page Application):** Aplicação de página única, navegação dinâmica sem recarregar o navegador.
- **Vue.js:** Framework progressivo para construção de interfaces reativas e modulares.
- **Vite:** Ferramenta moderna de build e desenvolvimento rápido para projetos Vue.

## 2. Estrutura de Pastas

```
frontend/
  ├─ src/
  │   ├─ main.js         # Ponto de entrada da aplicação
  │   ├─ App.vue         # Componente raiz
  │   └─ components/     # Componentes reutilizáveis (Login, MainLayout, UserManagement, etc)
  ├─ index.html          # HTML principal
  ├─ package.json        # Dependências e scripts
  ├─ vite.config.js      # Configuração do Vite
  └─ dist/               # Arquivos finais gerados pelo build
```

## 3. Componentização

- Cada funcionalidade é um componente Vue (`.vue`), com template, script e estilo isolados.
- Componentes são reutilizáveis e organizados em `src/components`.
- O estado global pode ser gerenciado com Vuex ou Pinia (a implementar).

## 4. Navegação

- Inicialmente controlada por estado (login → tela principal).
- Para rotas avançadas, recomenda-se uso do Vue Router.
- Menu sanduíche para navegação entre funcionalidades.

## 5. Comunicação com Backend

- Requisições HTTP via `fetch` ou bibliotecas como Axios.
- Endpoints REST expostos pelo backend Java/Jakarta EE.
- Autenticação e controle de acesso por perfil (admin, user, courier, customer).

## 6. Controle de Versões

- O front-end está versionado junto ao backend no mesmo repositório Git.
- Recomenda-se criar branches para novas features ou correções:
  - `feature/nome-da-feature`
  - `fix/nome-do-bug`
- Commits devem ser claros e descritivos.
- Pull Requests para revisão e integração.

## 7. Build e Deploy

- `npm install` para instalar dependências.
- `npm run dev` para desenvolvimento local.
- `npm run build` para gerar arquivos finais em `dist/`.
- Use o script `build-frontend.ps1` para distribuir o front-end no WAR Java.

## 8. Boas Práticas de Manutenção

- Componentes pequenos e focados em uma responsabilidade.
- Uso de logs apenas em ambiente de desenvolvimento (função devLog).
- Documentação dos componentes e fluxo de navegação.
- Atualize o arquivo `FRONTEND_VUE.md` a cada nova página ou funcionalidade.

## 9. Referências

- [Documentação Vue.js](https://vuejs.org/)
- [Documentação Vite](https://vitejs.dev/)
- [Guia SPA](https://developer.mozilla.org/en-US/docs/Glossary/SPA)

---

*Este documento deve ser revisado e expandido conforme o front-end evoluir.*
