-- V3__Add_complete_test_data.sql - Dados completos para testes
-- Adiciona mais dados de teste para todos os módulos

-- Mais usuários para testes completos
INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador Pedro', 'pedro', 'pedro456', 'pedro@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Ana', 'ana', 'ana456', 'ana@email.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('BUSINESS', 'Empresa Y', 'empresay', 'empresay789', 'empresay@cso.com', 'contato@empresay.com', 'Av. Faria Lima, 300', '11333333333');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador Lucas', 'lucas', 'lucas789', 'lucas@cso.com', NULL, 'Rua das Acácias, 700', '11222222222');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Maria', 'maria', 'maria789', 'maria@email.com', 'maria.contato@email.com', 'Av. Rebouças, 800', '11111111111');

-- Mais clientes para testes
INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'ana'), 1.2, 'TabelaB');

INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM app_user WHERE login = 'maria'), 1.3, 'TabelaC');

-- Mais entregadores para testes
INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'pedro'), 1.3);

INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM app_user WHERE login = 'lucas'), 1.4);

-- Teams (equipes) - usando relacionamento correto
INSERT INTO team (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'joao')), 1.2);

INSERT INTO team (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'pedro')), 1.3);

INSERT INTO team (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'lucas')), 1.4);

-- Entregas para testes
INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (
    (SELECT id FROM app_user WHERE login = 'empresa'),
    (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'carlos')),
    (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'joao')),
    'Av. Paulista, 1000', 'Rua Augusta, 500', 'João Silva', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP
);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (
    (SELECT id FROM app_user WHERE login = 'empresa'),
    (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'ana')),
    (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'pedro')),
    'Av. Faria Lima, 200', 'Rua Oscar Freire, 300', 'Maria Oliveira', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP
);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (
    (SELECT id FROM app_user WHERE login = 'empresa'),
    (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'carlos')),
    (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'pedro')),
    'Av. Rebouças, 500', 'Av. Brasil, 300', 'Carlos Santos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP
);

INSERT INTO delivery (idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime)
VALUES (
    (SELECT id FROM app_user WHERE login = 'empresay'),
    (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'maria')),
    (SELECT id FROM courier WHERE idcourier = (SELECT id FROM app_user WHERE login = 'lucas')),
    'Rua Consolação, 800', 'Av. Angélica, 400', 'Pedro Lima', 'Entrega frágil', '1 caixa', '2kg', '5', 2.0, 45.0, FALSE, FALSE, CURRENT_TIMESTAMP
);

-- SMS para testes
INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (
    (SELECT id FROM delivery WHERE description = 'Entrega urgente'),
    1, 'INFO', '11666666666', '11777777777', 'Saiu para entrega', CURRENT_TIMESTAMP
);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (
    (SELECT id FROM delivery WHERE description = 'Entrega normal'),
    1, 'INFO', '11444444444', '11555555555', 'Saiu para entrega', CURRENT_TIMESTAMP
);

INSERT INTO sms (iddelivery, piece, type, mobileTo, mobileFrom, message, datetime)
VALUES (
    (SELECT id FROM delivery WHERE description = 'Entrega frágil'),
    1, 'INFO', '11111111111', '11222222222', 'Entrega frágil a caminho', CURRENT_TIMESTAMP
);

-- Mais preços para testes
INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'ana')), 'TabelaB', 'Moto', 'São Paulo', 30.0);

INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES ((SELECT id FROM app_user WHERE login = 'empresay'), (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'maria')), 'TabelaC', 'Van', 'São Paulo', 80.0);

INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'carlos')), 'TabelaA', 'Caminhão', 'São Paulo', 120.0);
