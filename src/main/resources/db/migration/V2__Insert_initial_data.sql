-- V2__Insert_initial_data.sql - Dados básicos para testes
-- Apenas dados essenciais para permitir o deploy e testes básicos

-- 1. Usuários básicos (usando estrutura correta da tabela app_user)
INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Av. Paulista, 100', '11999999999');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('COURIER', 'Entregador João', 'joao', 'joao123', 'joao@cso.com', NULL, 'Rua das Flores, 300', '11777777777');

INSERT INTO app_user (role, name, login, password, email, email2, address, mobile)
VALUES ('CUSTOMER', 'Cliente Carlos', 'carlos', 'carlos456', 'carlos@cso.com', 'cliente@empresa.com', 'Rua das Palmeiras, 400', '11666666666');

-- 2. Clientes básicos usando subselects
INSERT INTO customer (idbusiness, iduser, factorCustomer, priceTable)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'carlos'), 1.1, 'TabelaA');

-- 3. Entregadores básicos usando subselects  
INSERT INTO courier (idbusiness, idcourier, factorCourier)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM app_user WHERE login = 'joao'), 1.2);

-- 4. Preço básico
INSERT INTO price (idbusiness, idcustomer, tableName, vehicle, local, price)
VALUES ((SELECT id FROM app_user WHERE login = 'empresa'), (SELECT id FROM customer WHERE iduser = (SELECT id FROM app_user WHERE login = 'carlos')), 'TabelaA', 'Carro', 'São Paulo', 50.0);

-- Fim do script básico
