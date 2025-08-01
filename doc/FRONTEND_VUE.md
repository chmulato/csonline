# Documentação do Front-End SPA (Vue.js)

## Estrutura do Projeto

- Localização: `frontend/`
- Framework: Vue 3 + Vite
- Build final: `frontend/dist` (copiar para `src/main/webapp` para deploy junto ao backend)

## Páginas e Navegação

### 1. Login

- **Arquivo:** `src/components/Login.vue`
- **Descrição:** Tela inicial de autenticação. Usuário informa login e senha.
- **Fluxo:** Após login bem-sucedido, navega para a tela principal.

### 2. Tela Principal (Layout)

- **Arquivo:** `src/components/MainLayout.vue`
- **Descrição:** Layout principal do sistema após login.
- **Elementos:**
  - Header com título e botão menu sanduíche (☰)
  - Menu lateral (drawer) com links para funcionalidades:
    - Entregas
    - Clientes
    - Usuários
    - Preços
    - SMS
    - Sair
  - Área principal com mensagem de boas-vindas
- **Fluxo:** Selecionando uma opção do menu, navega para a respectiva funcionalidade (a implementar).

## Navegação

- Inicial: `/` → Login
- Após login: `/main` (simulado via estado, SPA)
- Menu sanduíche abre/fecha o drawer lateral
- Futuras rotas podem ser adicionadas com Vue Router

## Integração com Backend

- Endpoints REST disponíveis em `/api/*` (ex: `/api/couriers`, `/api/customers`)
- Autenticação e navegação protegida podem ser implementadas via chamada à API

## Build e Deploy

- `npm install` para instalar dependências
- `npm run dev` para desenvolvimento local
- `npm run build` para gerar arquivos finais em `dist/`
- Copiar conteúdo de `dist/` para `src/main/webapp/` para servir via WildFly

## Customização

- Estilos podem ser ajustados nos arquivos `.vue`
- Novas páginas podem ser criadas em `src/components/`
- Para navegação avançada, adicionar Vue Router

## Screenshots

- Adicione imagens da tela de login, tela principal e menu sanduíche conforme evolução

## Segurança da Aplicação (Adendo)

A segurança do front-end Vue será implementada ao final do desenvolvimento das páginas principais. Os principais pontos a serem considerados:

- Autenticação baseada em token (JWT) ou sessão, validada pelo backend.
- Proteção de rotas e menus conforme perfil do usuário (admin, user, courier, customer).
- Uso de HTTPS obrigatório em produção.
- Controle de acesso sempre validado pelo backend.
- Logs restritos ao ambiente de desenvolvimento (devLog).
- Proteção contra XSS e CSRF (evitar v-html, tokens seguros).
- Nunca armazenar dados sensíveis no front-end.

*A implementação da segurança será detalhada e aplicada após o término das páginas principais do sistema.*

---

*Este documento deve ser atualizado conforme novas páginas, funcionalidades e camadas de segurança forem implementadas no front-end.*
