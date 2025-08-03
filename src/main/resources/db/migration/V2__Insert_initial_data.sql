-- Inserção de dados iniciais para o sistema

-- 1. Usuários (app_user) - IDs serão gerados automaticamente
INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Rua Central, 100', '11999999999');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador João', 'joao', 'joao123', 'joao@cso.com', NULL, 'Rua das Flores, 300', '11777777777');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Carlos', 'carlos', 'carlos123', 'carlos@email.com', NULL, 'Rua dos Limões, 400', '11666666666');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador Pedro', 'pedro', 'pedro456', 'pedro@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Ana', 'ana', 'ana456', 'ana@email.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('BUSINESS', 'Empresa Y', 'empresay', 'empresay789', 'empresay@cso.com', 'contato@empresay.com', 'Av. Brasil, 700', '11333333333');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador Lucas', 'lucas', 'lucas789', 'lucas@cso.com', NULL, 'Rua dos Pinheiros, 800', '11222222222');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Maria', 'maria', 'maria789', 'maria@email.com', 'maria.contato@email.com', 'Av. Santo Amaro, 900', '11111111111');

-- 2. Clientes (customer) - Usando logins para encontrar IDs
INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'carlos'), 1.1, 'TabelaA');

INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'ana'), 1.2, 'TabelaB');

INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM app_user WHERE login = 'maria'), 1.3, 'TabelaC');

-- 3. Entregadores (courier) - Usando logins para encontrar IDs
INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'joao'), 1.2);

INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'pedro'), 1.3);

INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM app_user WHERE login = 'lucas'), 1.4);

-- 4. Equipes (team)
INSERT INTO team (id, idbusiness, idcourier, factorCourier)
VALUES (1, 2, 1, 1.2);

INSERT INTO team (id, idbusiness, idcourier, factorCourier)
VALUES (2, 2, 2, 1.3);

INSERT INTO team (id, idbusiness, idcourier, factorCourier)
VALUES (3, 7, 3, 1.4);

-- 4. Times (team) - aguardando implementação completa

-- 5. Tabelas de Preço (price)
INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (2, 1, 'TabelaA', 'Carro', 'São Paulo', 50.0);

INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (2, 2, 'TabelaB', 'Moto', 'Rio de Janeiro', 30.0);

INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (7, 3, 'TabelaC', 'Van', 'Curitiba', 70.0);

INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES (2, 1, 'TabelaA', 'Moto', 'São Paulo', 25.0);

-- 6. Entregas (delivery)
INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (2, 1, 1, 'Av. Paulista, 1000', 'Rua Augusta, 500', 'João Silva', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (2, 2, 2, 'Av. Faria Lima, 200', 'Rua Oscar Freire, 300', 'Maria Oliveira', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (2, 1, 2, 'Av. Rebouças, 500', 'Av. Brasil, 300', 'Carlos Santos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (7, 3, 3, 'Rua Consolação, 800', 'Av. Angélica, 400', 'Ana Pereira', 'Entrega frágil', '1 caixa', '2kg', '5', 0.0, 45.0, TRUE, FALSE, CURRENT_TIMESTAMP);

-- 7. SMS (sms)
INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (1, 1, 'INFO', '11666666666', '11777777777', 'Entrega a caminho', CURRENT_TIMESTAMP);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (1, 2, 'INFO', '11777777777', '11666666666', 'Entrega recebida', CURRENT_TIMESTAMP);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (2, 1, 'INFO', '11444444444', '11555555555', 'Saiu para entrega', CURRENT_TIMESTAMP);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (3, 1, 'INFO', '11666666666', '11555555555', 'Entrega especial concluída', CURRENT_TIMESTAMP);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (4, 1, 'INFO', '11111111111', '11222222222', 'Entrega frágil a caminho', CURRENT_TIMESTAMP);
