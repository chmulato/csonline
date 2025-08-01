# Documentação do Front-End SPA (Vue.js)
#
# Consulte também o arquivo [ARQUITETURA_VUE.md](ARQUITETURA_VUE.md) para uma visão geral da arquitetura, boas práticas e controle de versões do front-end.

## Estrutura do Projeto

- **Localização:** `frontend/`
- **Framework:** Vue 3 + Vite
- **Componentes principais em `src/components/`:**
  - `App.vue` - Componente raiz com controle de navegação
  - `Login.vue` - Tela de autenticação
  - `MainLayout.vue` - Layout principal com menu
  - `UserManagement.vue` - CRUD de usuários
  - `CourierManagement.vue` - CRUD de entregadores
  - `CustomerManagement.vue` - CRUD de empresas/centros de distribuição
  - `DeliveryManagement.vue` - CRUD de entregas
  - `Logout.vue` - Tela de logout
- **Build final:** `frontend/dist` (copiar para `src/main/webapp` para deploy junto ao backend)
- **Scripts de build e deploy:** `src/build-frontend.ps1`

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
    - **Entregas** → `DeliveryManagement.vue`
    - **Empresas** → `CustomerManagement.vue`
    - **Entregadores** → `CourierManagement.vue`
    - **Usuários** → `UserManagement.vue`
    - Preços (a implementar)
    - SMS (a implementar)
    - Sair → `Logout.vue`
  - Área principal com mensagem de boas-vindas
- **Fluxo:** Selecionando uma opção do menu, navega para a respectiva funcionalidade.

### 3. Gestão de Usuários

- **Arquivo:** `src/components/UserManagement.vue`
- **Descrição:** CRUD completo para gestão de usuários do sistema.
- **Funcionalidades:**
  - Listagem de usuários com nome, email, WhatsApp, role
  - Formulário modal para criar/editar usuários
  - Campo WhatsApp obrigatório para utilização do sistema
  - Validação de campos obrigatórios (nome, email, WhatsApp, senha)
  - Exclusão com confirmação
  - Botão "Voltar" para retornar ao menu principal
- **Roles suportados:** admin, user, courier, customer

### 4. Gestão de Entregadores

- **Arquivo:** `src/components/CourierManagement.vue`
- **Descrição:** CRUD completo para gestão de entregadores (couriers).
- **Funcionalidades:**
  - Listagem de entregadores com dados do usuário e fator de comissão
  - Formulário modal para criar/editar entregadores
  - Seleção de empresa (business) via dropdown
  - Fator de comissão configurável (0-100%)
  - Integração com entidade User (relacionamento ManyToOne)
  - Exclusão com confirmação
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** Courier + User associado (role: 'courier')

### 5. Gestão de Empresas (Centro de Distribuições)

- **Arquivo:** `src/components/CustomerManagement.vue`
- **Descrição:** CRUD completo para gestão de empresas/centros de distribuição (customers).
- **Funcionalidades:**
  - Listagem de empresas com dados completos e tabela de preços
  - Formulário modal para criar/editar empresas
  - Seleção de empresa controladora via dropdown
  - Fator de comissão e tabela de preços configuráveis
  - Campo endereço completo obrigatório
  - Integração com entidade User (relacionamento OneToOne)
  - Exclusão com confirmação
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** Customer + User associado (role: 'customer')

### 6. Gestão de Entregas

- **Arquivo:** `src/components/DeliveryManagement.vue`
- **Descrição:** CRUD completo para gestão de entregas do sistema.
- **Funcionalidades:**
  - Listagem de entregas com todas as informações relevantes
  - Sistema de filtros por status (Pendente, Recebida, Finalizada)
  - Status visuais com cores diferenciadas
  - Formulário modal completo para criar/editar entregas
  - Seleção de empresa, cliente e entregador via dropdowns
  - Campos para origem, destino, contato, descrição
  - Dados físicos: volume, peso, distância (km)
  - Controle de custos (adicional e total)
  - Checkboxes para status (recebida/finalizada)
  - Formatação de data/hora e valores monetários
  - Exclusão com confirmação
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** Delivery com relacionamentos para Business, Customer e Courier

### 6. Gestão de Equipes (Teams)

- **Arquivo:** `src/components/TeamManagement.vue`
- **Descrição:** CRUD completo para gestão de equipes de entregadores por centro de distribuição.
- **Funcionalidades:**
  - Listagem de equipes com informações do centro de distribuição
  - Criação, edição e exclusão de equipes
  - Vinculação de entregadores às equipes
  - Filtros por centro de distribuição e status
  - Modal de visualização com detalhes completos da equipe
  - Dashboard com estatísticas: total de equipes, entregadores ativos, centros ativos, taxa de aproveitamento
  - Campos para nome da equipe, centro de distribuição, líder, status, descrição
  - Controle de membros ativos/inativos por equipe
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** Team com relacionamentos para Customer (centro de distribuição) e Couriers

### 7. Gestão de SMS/WhatsApp

- **Arquivo:** `src/components/SMSManagement.vue`
- **Descrição:** CRUD completo para gestão de mensagens WhatsApp para entregas.
- **Funcionalidades:**
  - Listagem de mensagens com filtros por entrega, tipo e data
  - Sistema de templates de mensagens (coleta, entrega, atualização, problema, finalização)
  - Criação, edição e exclusão de mensagens
  - Dashboard com estatísticas: total de mensagens, templates ativos, taxa de entrega
  - Modal de visualização com detalhes da mensagem e contexto da entrega
  - Campos para tipo, template, entrega associada, destinatário, conteúdo
  - Contador de caracteres para limite de SMS
  - Integração com dados de entregas para contexto
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** SMS com relacionamentos para Delivery e templates de mensagens

### 8. Gestão de Preços

- **Arquivo:** `src/components/PriceManagement.vue`
- **Descrição:** CRUD completo para gestão de tabelas de preços por empresa e localização.
- **Funcionalidades:**
  - Listagem de preços com informações de cliente, empresa, veículo e local
  - Criação, edição e exclusão de preços
  - Filtros por cliente, empresa, tipo de veículo e busca textual
  - Modal de visualização com detalhes completos do preço
  - Dashboard com estatísticas: total de preços, tipos de veículos, clientes ativos, preço médio
  - Campos para nome da tabela, cliente, empresa, veículo, local, valor
  - Categorização por tipo de veículo (Moto, Carro, Van, Caminhão)
  - Formatação monetária em Reais (R$)
  - Funcionalidade de exportação de dados
  - Botão "Voltar" para retornar ao menu principal
- **Estrutura:** Price com relacionamentos para User (business) e Customer

### 9. Logout

- **Arquivo:** `src/components/Logout.vue`
- **Descrição:** Tela de saída do sistema com feedback visual.
- **Funcionalidades:**
  - Confirmação de logout
  - Opção para retornar ao login
  - Limpeza do estado da aplicação

## Navegação

- **Inicial:** `/` → Login (`Login.vue`)
- **Após login:** Tela principal (`MainLayout.vue`) com menu de navegação
- **Gestão de usuários:** `UserManagement.vue` (via menu "Usuários")
- **Gestão de entregadores:** `CourierManagement.vue` (via menu "Entregadores") 
- **Gestão de empresas:** `CustomerManagement.vue` (via menu "Empresas")
- **Gestão de entregas:** `DeliveryManagement.vue` (via menu "Entregas")
- **Gestão de equipes:** `TeamManagement.vue` (via menu "Times")
- **Gestão de SMS:** `SMSManagement.vue` (via menu "SMS")
- **Gestão de preços:** `PriceManagement.vue` (via menu "Preços")
- **Logout:** `Logout.vue` (via menu "Sair")

### Sistema de Navegação SPA

- Navegação baseada em estado reativo (Vue 3 Composition API)
- Controle de visibilidade via variáveis reativas (`isLogged`, `isUserMgmt`, etc.)
- Menu sanduíche abre/fecha o drawer lateral
- Botão "Voltar" em todas as telas de gestão para retornar ao menu principal
- Futuras rotas podem ser adicionadas com Vue Router para navegação avançada

### Estados de Navegação

```javascript
// Estados principais de navegação no App.vue
const isLogged = ref(false);
const isLogout = ref(false);
const isUserMgmt = ref(false);
const isCourierMgmt = ref(false);
const isCustomerMgmt = ref(false);
const isDeliveryMgmt = ref(false);
const isTeamMgmt = ref(false);
const isSMSMgmt = ref(false);
const isPriceMgmt = ref(false);
```

## Integração com Backend

- **Endpoints REST disponíveis:** `/api/*` (ex: `/api/couriers`, `/api/customers`, `/api/deliveries`)
- **Estruturas de dados:** Baseadas nas entidades JPA do backend
  - `User` → Usuários do sistema (admin, user, courier, customer)
  - `Courier` → Entregadores (relacionamento com User e Business)
  - `Customer` → Empresas/Centros de distribuição (relacionamento com User e Business)
  - `Delivery` → Entregas (relacionamentos com Business, Customer e Courier)
- **Autenticação:** A ser implementada via JWT com controle de sessão
- **Autorização:** Backend controla acesso por perfil; front-end adapta interface conforme perfil
- **Validação:** Validações básicas no front-end, validações de negócio no backend

### Dados Simulados (Desenvolvimento)

Atualmente todos os componentes utilizam dados simulados para desenvolvimento:
- Empresas controladoras: CSOnline Delivery, Gestão Empresarial
- Usuários de exemplo com diferentes roles
- Entregadores com fatores de comissão
- Empresas com endereços da região de Curitiba/PR
- Entregas com diferentes status e valores realistas

## Build e Deploy

- `npm install` para instalar dependências
- `npm run dev` para desenvolvimento local
- `npm run build` para gerar arquivos finais em `dist/`

---

- Use o script `src/build-frontend.ps1` para copiar o conteúdo de `dist/` para `src/main/webapp/` e servir via WildFly

---

## Customização

- **Estilos:** Podem ser ajustados nos arquivos `.vue` individuais (scoped styles)
- **Novas páginas:** Criar em `src/components/` seguindo o padrão existente
- **Navegação:** Adicionar estado no `App.vue` e função no `MainLayout.vue`
- **Formulários:** Seguir padrão modal com validações e botão "Voltar"
- **Dados:** Substituir dados simulados por chamadas de API quando backend estiver pronto
- **Roteamento avançado:** Adicionar Vue Router para URLs amigáveis
- **Logs:** Usar `devLog` restrito ao ambiente de desenvolvimento

### Padrões de Desenvolvimento

1. **Componentes:** Vue 3 Composition API com `<script setup>`
2. **Estado:** Refs reativos para controle de navegação
3. **Formulários:** Modais com validação HTML5 e confirmações
4. **Tabelas:** Responsivas com scroll horizontal quando necessário
5. **Estilos:** Scoped CSS com design consistente
6. **Navegação:** Estado centralizado no App.vue
7. **Dados:** Estrutura baseada nas entidades JPA do backend

## Screenshots

- **Telas implementadas até agosto/2025:**
  - **Login:** Autenticação simulada, logs restritos ao ambiente de desenvolvimento
  - **Tela principal:** Menu sanduíche, navegação entre funcionalidades, mensagem de boas-vindas
  - **Gestão de usuários:** CRUD completo com WhatsApp obrigatório, botão "Voltar"
  - **Gestão de entregadores:** CRUD completo com seleção de empresa e fator de comissão
  - **Gestão de empresas:** CRUD completo para centros de distribuição com endereços e tabelas de preço
  - **Gestão de entregas:** CRUD completo com filtros por status, dados físicos e financeiros
  - **Gestão de equipes:** CRUD completo para times de entregadores por centro de distribuição
  - **Gestão de SMS:** CRUD completo para mensagens WhatsApp com templates e filtros
  - **Gestão de preços:** CRUD completo para tabelas de preços por empresa, veículo e localização
  - **Logout:** Tela de saída com feedback visual e opção de retornar ao login

### Funcionalidades Implementadas

**Sistema de navegação SPA completo**
**CRUD de usuários com validações**
**CRUD de entregadores com relacionamentos**
**CRUD de empresas/centros de distribuição**
**CRUD de entregas com filtros e status**
**CRUD de equipes com vinculação de entregadores**
**CRUD de SMS/WhatsApp com sistema de templates**
**CRUD de preços com categorização por veículo**
**Interface responsiva e consistente**
**Formulários modais com validação**
**Dados simulados para desenvolvimento**
**Navegação com botão "Voltar"**
**Dashboard com estatísticas em cada módulo**

**Em desenvolvimento:**
- Integração com backend via API REST
- Autenticação JWT
- Controle de acesso por perfil

Imagens podem ser adicionadas conforme evolução visual e integração com backend.

## Segurança da Aplicação (Adendo)


A segurança do front-end Vue está planejada para ser implementada após a conclusão das principais telas e integração com o backend. Pontos já considerados e documentados:

- Autenticação baseada em token (JWT) ou sessão, validada pelo backend.
- Proteção de rotas e menus conforme perfil do usuário (admin, user, courier, customer).
- Uso de HTTPS obrigatório em produção.
- Controle de acesso sempre validado pelo backend.
- Logs restritos ao ambiente de desenvolvimento (devLog).
- Proteção contra XSS e CSRF (evitar v-html, tokens seguros).
- Nunca armazenar dados sensíveis no front-end.


*A implementação da segurança será detalhada e aplicada após o término das páginas principais do sistema e início da integração com autenticação JWT e controle de perfis.*

---

## Próximos Passos

### Funcionalidades Pendentes
- **Dashboard:** Tela inicial com métricas e resumos gerais do sistema
- **Relatórios:** Telas de relatórios de entregas, custos e performance
- **Configurações:** Tela de configurações gerais do sistema

### Integração e Backend
- **Autenticação JWT:** Implementar login real com token de sessão
- **API REST:** Substituir dados simulados por chamadas aos endpoints do backend
- **Controle de acesso:** Implementar proteção de rotas conforme perfil do usuário
- **Tratamento de erros:** Implementar feedback visual para erros de API
- **Loading states:** Implementar indicadores de carregamento

### Melhorias de UX
- **Vue Router:** Implementar roteamento com URLs amigáveis
- **Paginação:** Implementar paginação nas listagens com muitos registros
- **Busca:** Implementar filtros de busca nas listagens
- **Notificações:** Sistema de notificações toast para feedback de ações
- **Tema:** Implementar tema claro/escuro

### Performance e Qualidade
- **Testes:** Implementar testes unitários com Vitest
- **TypeScript:** Migrar para TypeScript para maior segurança de tipos
- **PWA:** Implementar Service Worker para funcionalidade offline
- **Otimização:** Code splitting e lazy loading de componentes

*Este documento deve ser atualizado conforme novas páginas, funcionalidades, integrações e camadas de segurança forem implementadas no front-end.*
