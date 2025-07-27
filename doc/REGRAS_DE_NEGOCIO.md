# Exemplos de Uso das Regras de Negócio

## Exemplo 1: Autorização de acesso à entrega
```java
// Usuário courier tentando acessar uma entrega
User courier = ...;
Delivery delivery = ...;
boolean autorizado = courierService.canAccessDelivery(courier, delivery);
if (autorizado) {
    // Permitir acesso aos detalhes da entrega
}
```

## Exemplo 2: Registro de mensagem de rastreamento
```java
// Registrar mensagem WhatsApp entre courier e customer
smsService.sendDeliverySMS(
    delivery.getId(),
    courier.getMobile(),
    customer.getMobile(),
    "WHATSAPP",
    "Entrega saiu para entrega",
    1,
    LocalDateTime.now().toString()
);
```

## Exemplo 3: Consulta de histórico de mensagens
```java
List<SMS> historico = smsService.getDeliverySMSHistory(delivery.getId());
for (SMS sms : historico) {
    System.out.println(sms.getDatetime() + ": " + sms.getMessage());
}
```

## Exemplo 4: Atualização de fator do courier
```java
courierService.updateFactor(courier.getId(), 1.15);
```

## Exemplo 5: Autenticação de usuário
```java
User usuario = userService.findByLoginAndPassword("admin", "senha123");
if (usuario != null && userService.isAdmin(usuario)) {
    // Usuário autenticado como admin
}
```
    
# Regras de Negócio - Camada de Serviços

## Usuário (UserService)
- Usuário ADMIN tem acesso total ao sistema.
- Usuário COURIER pode acessar entregas vinculadas ao seu usuário.
- Usuário CUSTOMER pode acessar entregas vinculadas ao seu usuário.
- Autenticação por login e senha.

## Entregador (CourierService)
- Pode consultar entregas vinculadas ao seu usuário.
- Só pode acessar uma entrega se for o courier vinculado à entrega.
- Pode atualizar o fator de entrega (factorCourier).

## Cliente (CustomerService)
- Pode consultar entregas vinculadas ao seu usuário.
- Só pode acessar uma entrega se for o customer vinculado à entrega.
- Pode atualizar o fator e tabela de preço.

## Entrega (DeliveryService)
- Permite consultar entregas por cliente ou courier.
- Permite atualizar o status da entrega.
- Permite rastrear o histórico de mensagens (SMS) da entrega.

## SMS (SMSService)
- Permite registrar mensagens entre courier e customer para rastreamento da entrega.
- Permite consultar o histórico de mensagens de uma entrega.
- Os campos `mobileTo` e `mobileFrom` representam os telefones WhatsApp dos envolvidos.

## Preço (PriceService)
- Permite consultar e atualizar preços vinculados ao cliente ou courier.

## Regras Gerais
- Todas as operações sensíveis lançam DAOException em caso de erro de persistência.
- Acesso a dados é controlado por regras de autorização na camada de serviço.
