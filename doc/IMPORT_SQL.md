## Exemplos de Cenários
- Usuário ADMIN para administração geral.
- Empresa BUSINESS vinculada a clientes e couriers.
- Dois clientes e dois couriers para simular múltiplas entregas e interações.
- Entregas com diferentes status (pendente, concluída) e dados variados.
- Tabela de preços para diferentes clientes e veículos.
- SMS simulando troca de mensagens entre courier e customer.

## Exemplos de Consultas para Testes
```sql
-- Buscar entregas pendentes
SELECT * FROM delivery WHERE completed = FALSE;

-- Buscar todas as mensagens de uma entrega
SELECT * FROM sms WHERE iddelivery = 1;

-- Buscar couriers de uma empresa
SELECT * FROM courier WHERE idbusiness = 2;
```

## Dicas para Testes
- Utilize os dados do import.sql para validar regras de negócio e persistência.
- Teste cenários de atualização, deleção e consulta por diferentes atributos.
- Simule operações de rastreamento e autorização usando os vínculos entre entidades.
# IMPORT_SQL.md

## Estrutura do import.sql
O arquivo `import.sql` contém comandos SQL para popular o banco H2 com dados iniciais das principais entidades do sistema.

### Exemplo de conteúdo:
```sql
-- Usuários iniciais
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES
  (1, 'ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Rua Central, 100', '11999999999'),
  (2, 'BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888'),
  (3, 'COURIER', 'Entregador Y', 'courier', 'courier123', 'courier@cso.com', NULL, 'Rua das Flores, 300', '11777777777'),
  (4, 'CUSTOMER', 'Cliente Z', 'cliente', 'cliente123', 'cliente@cso.com', NULL, 'Rua dos Limões, 400', '11666666666'),
  (5, 'COURIER', 'Entregador W', 'courier2', 'courier456', 'courier2@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555'),
  (6, 'CUSTOMER', 'Cliente Y', 'cliente2', 'cliente456', 'cliente2@cso.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');

-- Clientes
INSERT INTO customer (id, idbusiness, iduser, factorCustomer, priceTable) VALUES
  (1, 2, 4, 1.1, 'TabelaA'),
  (2, 2, 6, 1.2, 'TabelaB');

-- Couriers
INSERT INTO courier (id, idbusiness, iduser, factorCourier) VALUES
  (1, 2, 3, 1.2),
  (2, 2, 5, 1.3);

-- Entregas
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime) VALUES
  (1, 2, 1, 1, 'Origem A', 'Destino B', 'João', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP()),
  (2, 2, 2, 2, 'Origem C', 'Destino D', 'Maria', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP()),
  (3, 2, 1, 2, 'Origem E', 'Destino F', 'Carlos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP());

-- Tabela de preços
INSERT INTO price (id, idbusiness, idcustomer, tableName, vehicle, local, price) VALUES
  (1, 2, 1, 'TabelaA', 'Carro', 'SP', 50.0),
  (2, 2, 2, 'TabelaB', 'Moto', 'RJ', 30.0);

-- SMS
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime) VALUES
  (1, 1, 1, 'INFO', '11666666666', '11777777777', 'Entrega a caminho', CURRENT_TIMESTAMP()),
  (2, 1, 2, 'INFO', '11777777777', '11666666666', 'Entrega recebida', CURRENT_TIMESTAMP()),
  (3, 2, 1, 'INFO', '11444444444', '11555555555', 'Saiu para entrega', CURRENT_TIMESTAMP()),
  (4, 3, 1, 'INFO', '11666666666', '11555555555', 'Entrega especial concluída', CURRENT_TIMESTAMP());
```

## Observações
- Os dados podem ser ajustados conforme necessidade do ambiente ou testes.
- O arquivo deve ser colocado em `src/main/resources/import.sql` para ser carregado automaticamente pelo Hibernate/H2.
- Consulte as regras de negócio em [REGRAS_DE_NEGOCIO.md](REGRAS_DE_NEGOCIO.md) para entender os vínculos entre entidades.
