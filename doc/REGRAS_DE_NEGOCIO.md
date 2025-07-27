

# Regras de Negócio - Camada de Serviços

## Usuário (UserService)
- Usuário ADMIN:
  - Tem acesso total a todas as funcionalidades do sistema, incluindo cadastro, consulta, atualização e remoção de usuários, entregas, preços e mensagens.
  - Pode visualizar e gerenciar dados de todos os clientes, couriers e entregas.
- Usuário COURIER:
  - Só pode acessar entregas vinculadas ao seu próprio usuário.
  - Não pode visualizar ou alterar dados de outros couriers ou clientes.
  - Pode consultar seu histórico de entregas e mensagens.
- Usuário CUSTOMER:
  - Só pode acessar entregas vinculadas ao seu próprio usuário.
  - Não pode visualizar ou alterar dados de outros clientes ou couriers.
  - Pode consultar seu histórico de entregas e mensagens.
- Autenticação:
  - Realizada por login e senha, com validação de credenciais e atribuição de perfil (ADMIN, COURIER, CUSTOMER).
  - O acesso a funcionalidades é controlado por roles (perfis) definidos no cadastro do usuário.

## Entregador (CourierService)
- Consulta de entregas:
  - O courier pode consultar apenas entregas que estejam vinculadas ao seu usuário.
  - Não é permitido acessar entregas de outros couriers.
- Acesso à entrega:
  - Só pode acessar detalhes de uma entrega se for o courier vinculado àquela entrega.
  - Tentativas de acesso não autorizadas são bloqueadas e registradas.
- Atualização de fator:
  - O courier pode atualizar o seu próprio fator de entrega (factorCourier), que impacta o cálculo do preço da entrega.
  - Não pode alterar fatores de outros couriers.

## Cliente (CustomerService)
- Consulta de entregas:
  - O cliente pode consultar apenas entregas vinculadas ao seu usuário.
  - Não é permitido acessar entregas de outros clientes.
- Acesso à entrega:
  - Só pode acessar detalhes de uma entrega se for o customer vinculado àquela entrega.
  - Tentativas de acesso não autorizadas são bloqueadas e registradas.
- Atualização de fator e tabela de preço:
  - O cliente pode atualizar seu próprio fator e tabela de preço, impactando o valor das entregas.
  - Não pode alterar dados de outros clientes.

## Entrega (DeliveryService)
- Consulta de entregas:
  - Permite consultar entregas por cliente ou courier, respeitando as regras de autorização.
  - ADMIN pode consultar todas as entregas.
- Atualização de status:
  - Permite atualizar o status da entrega (ex: pendente, em trânsito, entregue, cancelada).
  - Apenas o courier vinculado ou o ADMIN pode alterar o status.
- Rastreamento de mensagens:
  - Permite consultar o histórico de mensagens (SMS) associadas à entrega.
  - Apenas envolvidos (courier, customer, ADMIN) podem acessar o histórico.

## SMS (SMSService)
- Registro de mensagens:
  - Permite registrar mensagens entre courier e customer para rastreamento da entrega.
  - Mensagens podem ser enviadas via WhatsApp, SMS ou outros canais definidos.
- Consulta de histórico:
  - Permite consultar o histórico de mensagens de uma entrega, acessível apenas aos envolvidos e ao ADMIN.
- Campos:
  - `mobileTo` e `mobileFrom` representam os telefones dos envolvidos na comunicação.

## Preço (PriceService)
- Consulta de preços:
  - Permite consultar preços vinculados ao cliente ou courier.
  - ADMIN pode consultar e atualizar qualquer preço.
- Atualização de preços:
  - Clientes e couriers podem atualizar seus próprios preços, conforme regras de negócio.

## Regras Gerais
- Todas as operações sensíveis lançam DAOException em caso de erro de persistência ou violação de regra de negócio.
- O acesso a dados e funcionalidades é controlado por regras de autorização centralizadas na camada de serviço.
- Tentativas de acesso não autorizado são bloqueadas e podem ser registradas para auditoria.
