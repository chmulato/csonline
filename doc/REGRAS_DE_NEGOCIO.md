

# Regras de Negócio - CSOnline

## Visão Geral

O sistema CSOnline implementa regras de negócio robustas para gestão de entregas, com controle de acesso baseado em perfis de usuário e operações seguras entre todas as camadas da aplicação.

## Interface Frontend (Vue 3 SPA)

**Gestão de Usuários:**
A interface permite operações completas de CRUD para usuários, com controle de acesso baseado em perfis específicos.

**Perfis de Usuário:**
- **Administrador (admin):** Acesso total ao sistema, gerenciamento completo de usuários, entregas, preços e mensagens
- **Usuário (user):** Gestão administrativa de entregas com acesso às funcionalidades operacionais
- **Entregador (courier):** Acesso restrito às próprias entregas, histórico e mensagens
- **Cliente (customer):** Acesso restrito às próprias entregas, histórico e comunicações

**Funcionalidades Principais:**
- Listagem paginada de usuários com filtros por nome, email e perfil
- Formulários modais para cadastro e edição de usuários
- Validação em tempo real de dados de entrada
- Navegação protegida com autenticação baseada em JWT
- Menu responsivo com acesso às funcionalidades por perfil
## APIs REST e Documentação

**Documentação Automática:**
Todos os endpoints REST são documentados automaticamente via OpenAPI/Swagger, proporcionando uma interface interativa para testes e integração.

**Acesso à Documentação:**
- Especificação OpenAPI: `/api/openapi.json`
- Interface Swagger UI: `/csonline/swagger-ui/index.html`
- Swagger Editor Online para análise externa

**Padrões de API:**
- Endpoints RESTful seguindo convenções HTTP
- Respostas padronizadas em JSON
- Tratamento de erros com códigos HTTP apropriados
- Versionamento de API para compatibilidade

## Gestão de Usuários (UserService)

**Controle de Acesso por Perfil:**

**Administrador (ADMIN):**
- Acesso irrestrito a todas as funcionalidades do sistema
- Gerenciamento completo de usuários, entregas, preços e mensagens
- Visualização e modificação de dados de todos os perfis
- Acesso a relatórios e métricas do sistema

**Entregador (COURIER):**
- Acesso limitado às entregas vinculadas ao próprio usuário
- Consulta de histórico pessoal de entregas e mensagens
- Atualização do próprio perfil e configurações
- Bloqueio de acesso a dados de outros usuários

**Cliente (CUSTOMER):**
- Acesso restrito às próprias entregas e histórico
- Consulta de mensagens e status das entregas pessoais
- Atualização de dados pessoais e configurações de conta
- Isolamento total de dados de outros clientes

**Sistema de Autenticação:**
- Autenticação baseada em credenciais (login/senha)
- Validação de identidade com tokens JWT
- Controle de sessão e expiração automática
- Atribuição dinâmica de permissões por perfil

## Gestão de Entregadores (CourierService)

**Controle de Acesso a Entregas:**
- Entregadores acessam exclusivamente entregas vinculadas ao próprio perfil
- Bloqueio automático de tentativas de acesso não autorizadas
- Registro de auditoria para tentativas de acesso indevido

**Operações Permitidas:**
- Consulta de entregas pessoais com filtros por período e status
- Visualização detalhada de entregas atribuídas
- Atualização do próprio fator de entrega (factorCourier)
- Acesso ao histórico de mensagens relacionadas às entregas

**Restrições de Segurança:**
- Impossibilidade de alterar fatores de outros entregadores
- Isolamento total de dados de entregas de terceiros
- Validação rigorosa de propriedade antes de qualquer operação

## Gestão de Clientes (CustomerService)

**Acesso Controlado a Entregas:**
- Clientes visualizam apenas entregas vinculadas ao próprio perfil
- Proteção total contra acesso a dados de outros clientes
- Auditoria de tentativas de acesso não autorizadas

**Funcionalidades Disponíveis:**
- Consulta de entregas pessoais com histórico completo
- Visualização de detalhes e status de entregas
- Atualização do fator pessoal e tabela de preços
- Configuração de preferências de comunicação

**Controles de Integridade:**
- Validação de propriedade antes de qualquer operação
- Isolamento de dados entre diferentes clientes
- Impossibilidade de alteração de dados de terceiros

## Gestão de Entregas (DeliveryService)

**Integridade Referencial:**
- Proteção contra exclusão de entregas com mensagens (SMS) associadas
- Lançamento de `ReferentialIntegrityException` em violações de integridade
- Validação automática de dependências antes de operações críticas

**Operações por Perfil:**
- **Administradores:** Acesso completo a todas as entregas do sistema
- **Entregadores:** Consulta e atualização apenas de entregas atribuídas
- **Clientes:** Visualização restrita às próprias entregas

**Controle de Status:**
- Atualização de status controlada por permissões (pendente, em trânsito, entregue, cancelada)
- Apenas entregadores vinculados ou administradores podem alterar status
- Rastreamento completo de mudanças de status com timestamps

**Histórico e Rastreamento:**
- Consulta de histórico de mensagens (SMS) por entrega
- Acesso restrito aos envolvidos (entregador, cliente, administrador)
- Auditoria completa de operações realizadas

## Sistema de Mensagens (SMSService)

**Proteção de Integridade:**
- Impossibilidade de exclusão de mensagens vinculadas a entregas ativas
- Exceções `ReferentialIntegrityException` com mensagens informativas
- Validação automática de dependências antes de operações de exclusão

**Comunicação Entre Partes:**
- Registro de mensagens entre entregadores e clientes
- Suporte a múltiplos canais (WhatsApp, SMS, outros)
- Rastreamento completo de comunicações por entrega

**Controles de Acesso:**
- Histórico acessível apenas aos envolvidos na entrega
- Administradores têm acesso completo ao histórico de mensagens
- Isolamento total entre conversas de diferentes entregas

**Campos de Comunicação:**
- `mobileTo` e `mobileFrom`: telefones dos participantes
- Timestamps automáticos para rastreamento temporal
- Status de entrega e leitura das mensagens

## Gestão de Preços (PriceService)

**Controle de Acesso por Perfil:**
- **Administradores:** Consulta e atualização irrestrita de qualquer tabela de preços
- **Clientes e Entregadores:** Acesso limitado aos próprios preços e fatores
- Validação rigorosa de propriedade antes de qualquer modificação

**Operações Permitidas:**
- Consulta de preços vinculados ao usuário autenticado
- Atualização de fatores pessoais conforme regras de negócio
- Configuração de tabelas de preço personalizadas
- Histórico de alterações para auditoria

**Regras de Negócio:**
- Fatores de entregadores impactam diretamente no cálculo final
- Tabelas de preço de clientes definem valores base
- Validação automática de valores dentro de faixas permitidas
- Aplicação imediata de mudanças em novas entregas

## Princípios Gerais de Segurança

**Tratamento de Exceções:**
- Todas as operações críticas utilizam `DAOException` para erros de persistência
- Mensagens de erro padronizadas e informativas para o usuário
- Logging detalhado para análise e debug de problemas

**Controle de Autorização:**
- Regras de autorização centralizadas na camada de serviço
- Validação automática de permissões antes de qualquer operação
- Isolamento total de dados entre diferentes usuários e perfis

**Auditoria e Monitoramento:**
- Registro automático de tentativas de acesso não autorizado
- Logs estruturados para rastreamento de operações sensíveis
- Alertas para atividades suspeitas ou violações de segurança

**Integridade de Dados:**
- Validação de integridade referencial em todas as operações
- Proteção contra corrupção de dados através de transações
- Backup automático e recuperação de dados em caso de falhas
