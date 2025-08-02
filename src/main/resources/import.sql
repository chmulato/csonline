-- Limpa dados em ordem correta para evitar constraint violations
DELETE FROM sms;
DELETE FROM delivery;
DELETE FROM price;
DELETE FROM courier;
DELETE FROM team;
DELETE FROM customer;
DELETE FROM app_user;

-- ****************************************************
-- ATENÇÃO: ORDEM DE INSERÇÃO DE DADOS MUITO IMPORTANTE!
-- ****************************************************
-- 1. Primeiro insira app_user (tabela base)
-- 2. Depois insira customer (referencia app_user)
-- 3. Depois insira courier (referencia app_user)
-- 4. Depois insira team (referencia app_user)
-- 5. Depois insira price (referencia customer)
-- 6. Depois insira delivery (referencia customer e courier)
-- 7. Por fim insira sms (referencia delivery)

-- 1. Usuários - App_user é a tabela base, sem dependências
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (1, 'ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Rua Central, 100', '11999999999');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (2, 'BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (3, 'COURIER', 'Entregador Y', 'courier', 'courier123', 'courier@cso.com', NULL, 'Rua das Flores, 300', '11777777777');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (4, 'CUSTOMER', 'Cliente Z', 'cliente', 'cliente123', 'cliente@cso.com', NULL, 'Rua dos Limões, 400', '11666666666');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (5, 'COURIER', 'Entregador W', 'courier2', 'courier456', 'courier2@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile)
VALUES (6, 'CUSTOMER', 'Cliente Y', 'cliente2', 'cliente456', 'cliente2@cso.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');

-- 2. Clientes (Customer) - Referencia app_user (ID 2 como business, ID 4 e 6 como users)
-- iduser = referência à tabela app_user, idbusiness = referência à empresa (app_user com role=BUSINESS)
INSERT INTO customer (id, idbusiness, iduser, factorCustomer, priceTable)
VALUES (1, 2, 4, 1.1, 'TabelaA');
INSERT INTO customer (id, idbusiness, iduser, factorCustomer, priceTable)
VALUES (2, 2, 6, 1.2, 'TabelaB');

-- 3. Entregadores (Courier) - Referencia app_user (ID 2 como business, ID 3 e 5 como users)
-- idcourier = referência à tabela app_user (entregadores)
INSERT INTO courier (id, idbusiness, idcourier, factorCourier)
VALUES (1, 2, 3, 1.2);
INSERT INTO courier (id, idbusiness, idcourier, factorCourier)
VALUES (2, 2, 5, 1.3);

-- 4. Equipes (Team) - Referencia app_user (ID 2 como business, ID 3 e 5 como couriers)
INSERT INTO team (id, idbusiness, idcourier, factorCourier)
VALUES (1, 2, 3, 1.2);
INSERT INTO team (id, idbusiness, idcourier, factorCourier)
VALUES (2, 2, 5, 1.3);

-- 5. Tabelas de Preço - Referencia customer (ID 1 e 2)
-- idcustomer = referência à tabela customer, não à tabela app_user!
INSERT INTO price (id, idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (1, 2, 1, 'TabelaA', 'Carro', 'SP', 50.0);
INSERT INTO price (id, idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (2, 2, 2, 'TabelaB', 'Moto', 'RJ', 30.0);

-- 6. Entregas - Referencia customer (ID 1 e 2) e courier (ID 1 e 2)
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (1, 2, 1, 1, 'Origem A', 'Destino B', 'João', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP);
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (2, 2, 2, 2, 'Origem C', 'Destino D', 'Maria', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP);
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (3, 2, 1, 2, 'Origem E', 'Destino F', 'Carlos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP);

-- 7. SMS - Referencia delivery (ID 1, 2 e 3)
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (1, 1, 1, 'INFO', '11666666666', '11777777777', 'Entrega a caminho', CURRENT_TIMESTAMP);
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (2, 1, 2, 'INFO', '11777777777', '11666666666', 'Entrega recebida', CURRENT_TIMESTAMP);
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (3, 2, 1, 'INFO', '11444444444', '11555555555', 'Saiu para entrega', CURRENT_TIMESTAMP);
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (4, 3, 1, 'INFO', '11666666666', '11555555555', 'Entrega especial concluída', CURRENT_TIMESTAMP);
