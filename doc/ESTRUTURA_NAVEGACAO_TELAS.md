# CSOnline - Estrutura de Navegação e Telas

## Visão Geral da Interface

O CSOnline utiliza uma arquitetura de Single Page Application (SPA) com navegação baseada em perfis de usuário, garantindo que cada tipo de usuário acesse apenas as funcionalidades adequadas ao seu nível de autorização.

## Estrutura de Navegação

### Layout Principal (MainLayout.vue)

#### Cabeçalho
- **Logo da Empresa**: CSOnline - Gestão CD
- **Indicador de Perfil**: Badge visual mostrando o perfil atual
- **Informações do Usuário**: Nome e função
- **Botão Logout**: Encerramento seguro da sessão

#### Menu Lateral Adaptativo

**Menu para ADMIN**
```
Dashboard
Gestão de Usuários
Gestão de Empresas  
Gestão de Entregadores
Gestão de Clientes
Gestão de Entregas
Relatórios
Configurações
```

**Menu para BUSINESS**
```
Dashboard
Gestão de Entregadores
Gestão de Clientes  
Gestão de Entregas
Relatórios Empresariais
```

**Menu para COURIER**
```
Dashboard
Minhas Entregas
Rotas
Meu Perfil
```

**Menu para CUSTOMER**
```
Dashboard
Meus Pedidos
Histórico
Meu Perfil
```

## Telas por Perfil

### 1. Telas do ADMIN

#### Dashboard Administrativo
**Rota**: `/dashboard`  
**Componente**: `Dashboard.vue`

**Widgets Disponíveis:**
- Resumo geral do sistema
- Estatísticas de usuários por perfil
- Métricas de entregas globais
- Empresas ativas
- Performance geral

**Cards de Acesso Rápido:**
- Gestão de Usuários
- Gestão de Empresas
- Gestão de Entregadores
- Gestão de Clientes
- Gestão de Entregas
- Relatórios

#### Gestão de Usuários
**Rota**: `/users`  
**Componente**: `UserManagement.vue`

**Funcionalidades:**
- Listagem de todos os usuários
- Filtros por perfil (ADMIN, BUSINESS, COURIER, CUSTOMER)
- Busca por nome, login ou email
- Criação de novos usuários (todos os perfis)
- Edição de usuários existentes
- Exclusão de usuários
- Indicadores visuais de perfil

**Campos do Formulário:**
- Nome completo
- Login único
- Email
- Endereço
- Telefone
- Perfil (dropdown com todos os tipos)
- Senha (obrigatória para novos, opcional para edição)

#### Gestão de Empresas
**Rota**: `/businesses`  
**Componente**: `BusinessManagement.vue`

**Funcionalidades:**
- Listagem de todas as empresas/centros de distribuição
- Criação de novas empresas
- Edição de dados empresariais
- Desativação/ativação de empresas
- Visualização de estatísticas por empresa

### 2. Telas do BUSINESS

#### Dashboard Empresarial
**Rota**: `/dashboard`  
**Componente**: `Dashboard.vue`

**Widgets Específicos:**
- Métricas da própria empresa
- Entregadores ativos
- Entregas do dia/semana/mês
- Clientes ativos
- Performance da operação

**Cards Limitados:**
- Gestão de Entregadores (próprios)
- Gestão de Clientes (próprios)
- Gestão de Entregas (próprias)
- Relatórios Empresariais

#### Gestão de Entregadores
**Rota**: `/couriers`  
**Componente**: `CourierManagement.vue`

**Funcionalidades Específicas:**
- Listagem apenas dos entregadores da própria empresa
- Filtro por empresa (limitado à própria)
- Criação de novos entregadores vinculados à empresa
- Edição de entregadores próprios
- Definição de fatores de comissão
- Histórico de performance

**Campos do Formulário:**
- Nome do entregador
- Email
- WhatsApp
- Empresa (pré-selecionada, não editável)
- Fator de comissão (%)
- Senha (para acesso do entregador)

#### Gestão de Clientes
**Rota**: `/customers`  
**Componente**: `CustomerManagement.vue`

**Funcionalidades:**
- Listagem de clientes da empresa
- Cadastro de novos clientes
- Edição de dados de clientes
- Histórico de entregas por cliente
- Comunicação com clientes

### 3. Telas do COURIER

#### Dashboard do Entregador
**Rota**: `/dashboard`  
**Componente**: `Dashboard.vue`

**Widgets Personalizados:**
- Entregas do dia
- Próximas entregas
- Status das rotas
- Comissões acumuladas
- Métricas pessoais

#### Minhas Entregas
**Rota**: `/my-deliveries`  
**Componente**: `MyDeliveries.vue`

**Funcionalidades:**
- Listagem de entregas designadas
- Filtros por status (pendente, em rota, entregue)
- Atualização de status de entrega
- Upload de comprovantes
- Comunicação com cliente
- GPS e navegação

**Status Disponíveis:**
- Pendente
- Coletado
- Em Rota
- Tentativa de Entrega
- Entregue
- Problema na Entrega

#### Gestão de Rotas
**Rota**: `/routes`  
**Componente**: `RouteManagement.vue`

**Funcionalidades:**
- Visualização de rotas otimizadas
- Sequência de entregas
- Estimativas de tempo
- Integração com GPS
- Relatório de quilometragem

### 4. Telas do CUSTOMER

#### Dashboard do Cliente
**Rota**: `/dashboard`  
**Componente**: `Dashboard.vue`

**Widgets Básicos:**
- Pedidos ativos
- Próximas entregas
- Histórico recente
- Status de acompanhamento

#### Meus Pedidos
**Rota**: `/my-orders`  
**Componente**: `MyOrders.vue`

**Funcionalidades:**
- Visualização de pedidos ativos
- Rastreamento em tempo real
- Previsão de entrega
- Comunicação com entregador
- Avaliação de serviço

#### Histórico de Entregas
**Rota**: `/order-history`  
**Componente**: `OrderHistory.vue`

**Funcionalidades:**
- Histórico completo de pedidos
- Filtros por período
- Download de comprovantes
- Reemissão de notas
- Estatísticas pessoais

## Componentes de Interface

### PermissionGuard
**Arquivo**: `PermissionGuard.vue`

**Propriedades:**
```vue
<PermissionGuard 
  :require-any="['permissao1', 'permissao2']"
  :require-all="['permissao1', 'permissao2']"
  :require-role="['ADMIN', 'BUSINESS']"
  :show-denied="true"
>
  <!-- Conteúdo protegido -->
</PermissionGuard>
```

**Comportamentos:**
- `require-any`: Exige pelo menos uma das permissões
- `require-all`: Exige todas as permissões
- `require-role`: Exige um dos perfis específicos
- `show-denied`: Mostra mensagem quando acesso negado

### Indicadores Visuais

#### Badges de Perfil
```css
.role-badge.admin { 
  background: #e74c3c; /* Vermelho - Administrador */
}
.role-badge.business { 
  background: #2196F3; /* Azul - Empresa */
}
.role-badge.courier { 
  background: #f39c12; /* Laranja - Entregador */
}
.role-badge.customer { 
  background: #27ae60; /* Verde - Cliente */
}
```

#### Tags de Status
- **Pendente**: Cinza
- **Em Andamento**: Azul
- **Concluído**: Verde
- **Problema**: Vermelho
- **Cancelado**: Laranja

## Fluxos de Navegação

### Fluxo de Login
```
1. Tela de Login → Validação → Dashboard
2. Redirecionamento baseado no perfil
3. Menu adaptativo carregado
4. Permissões aplicadas automaticamente
```

### Fluxo de Gestão (ADMIN/BUSINESS)
```
1. Dashboard → Cards de Gestão
2. Seleção da área (Usuários/Entregadores/etc)
3. Listagem com filtros
4. Ações (Criar/Editar/Excluir)
5. Confirmação → Atualização da lista
```

### Fluxo Operacional (COURIER)
```
1. Dashboard → Entregas do Dia
2. Seleção da entrega
3. Navegação → Atualização de Status
4. Comprovante → Conclusão
5. Próxima entrega
```

### Fluxo de Acompanhamento (CUSTOMER)
```
1. Dashboard → Meus Pedidos
2. Seleção do pedido
3. Rastreamento em tempo real
4. Comunicação com entregador
5. Avaliação do serviço
```

## Responsividade e UX

### Breakpoints
- **Desktop**: > 1024px (layout completo)
- **Tablet**: 768px - 1024px (menu colapsável)
- **Mobile**: < 768px (menu hambúrguer)

### Adaptações por Dispositivo
- **Desktop**: Sidebar fixa, múltiplas colunas
- **Tablet**: Sidebar colapsável, layout adaptativo
- **Mobile**: Menu overlay, layout empilhado

### Acessibilidade
- Contraste adequado para daltônicos
- Suporte a leitores de tela
- Navegação por teclado
- Indicadores visuais claros
- Textos alternativos

## Padrões de Interface

### Cores do Sistema
```css
:root {
  --primary: #2196F3;     /* Azul principal */
  --secondary: #6c757d;   /* Cinza secundário */
  --success: #28a745;     /* Verde sucesso */
  --danger: #dc3545;      /* Vermelho erro */
  --warning: #f39c12;     /* Laranja alerta */
  --info: #17a2b8;        /* Azul informativo */
}
```

### Tipografia
- **Títulos**: Source Sans Pro, Bold
- **Texto**: Source Sans Pro, Regular
- **Monospace**: JetBrains Mono (códigos/logs)

### Espaçamentos
- **Pequeno**: 8px
- **Médio**: 16px
- **Grande**: 24px
- **Extra Grande**: 32px

## Manutenção e Evolução

### Adição de Novas Telas
1. Criar componente Vue.js
2. Definir rota no router
3. Adicionar item no menu (se aplicável)
4. Implementar PermissionGuard
5. Testar com todos os perfis
6. Atualizar documentação

### Modificação de Permissões
1. Atualizar Auth Store
2. Revisar PermissionGuard nos componentes
3. Testar fluxos de navegação
4. Validar responsividade
5. Atualizar documentação

---

**Documento atualizado em**: Agosto 2025  
**Versão do Sistema**: CSOnline JWT 2.0  
**Responsável**: Equipe de Desenvolvimento CSOnline
