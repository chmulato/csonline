# Documentação do Front-End SPA (Vue.js)
#
# Consulte também o arquivo [ARQUITETURA_VUE.md](ARQUITETURA_VUE.md) para uma visão geral da arquitetura, boas práticas e controle de versões do front-end.

## Estrutura do Projeto

- Localização: `frontend/`
- Framework: Vue 3 + Vite
- Componentes em `src/components/` (Login, MainLayout, UserManagement, etc)
- Build final: `frontend/dist` (copiar para `src/main/webapp` para deploy junto ao backend)
- Scripts de build e deploy: `src/build-frontend.ps1`

## Páginas e Navegação

### 1. Login

- **Arquivo:** `src/components/Login.vue`
- **Descrição:** Tela inicial de autenticação. Usuário informa login e senha.
- **Fluxo:** Após login bem-sucedido, navega para a tela principal. Logs de tentativa de login são exibidos apenas em ambiente de desenvolvimento.

### 2. Tela Principal (Layout)

- **Arquivo:** `src/components/MainLayout.vue`
- **Descrição:** Layout principal do sistema após login.
- **Elementos:**
  - Header fixo com nome da aplicação
  - Footer fixo com copyright "Cara Core Informática © 2025"
  - Botão menu sanduíche (☰) para abrir menu lateral
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
- Futuras rotas podem ser adicionadas com Vue Router para navegação avançada

## Integração com Backend

- Endpoints REST disponíveis em `/api/*` (ex: `/api/couriers`, `/api/customers`)
- Autenticação e navegação protegida podem ser implementadas via chamada à API
- O backend controla o acesso por perfil; o front-end oculta menus conforme o perfil, mas nunca confia só nisso

## Build e Deploy

- `npm install` para instalar dependências
- `npm run dev` para desenvolvimento local
- `npm run build` para gerar arquivos finais em `dist/`

---

- Use o script `src/build-frontend.ps1` para copiar o conteúdo de `dist/` para `src/main/webapp/` e servir via WildFly

---

## Customização

- Estilos podem ser ajustados nos arquivos `.vue`
- Novas páginas podem ser criadas em `src/components/`
- Para navegação avançada, adicionar Vue Router
- Logs de console devem ser feitos apenas com devLog e restritos ao ambiente de desenvolvimento

## Screenshots

- Adicione imagens da tela de login, tela principal, menu sanduíche e gestão de usuários conforme evolução

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
