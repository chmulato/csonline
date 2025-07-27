

INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (1, 'ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Rua Central, 100', '11999999999');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (2, 'BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (3, 'COURIER', 'Entregador Y', 'courier', 'courier123', 'courier@cso.com', NULL, 'Rua das Flores, 300', '11777777777');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (4, 'CUSTOMER', 'Cliente Z', 'cliente', 'cliente123', 'cliente@cso.com', NULL, 'Rua dos Limões, 400', '11666666666');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (5, 'COURIER', 'Entregador W', 'courier2', 'courier456', 'courier2@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555');
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES (6, 'CUSTOMER', 'Cliente Y', 'cliente2', 'cliente456', 'cliente2@cso.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');


-- Corrigido: customer usa idcustomer (referência ao app_user CUSTOMER)

INSERT INTO customer (id, idbusiness, idcustomer, factorCustomer, priceTable) VALUES (1, 2, 4, 1.1, 'TabelaA');
INSERT INTO customer (id, idbusiness, idcustomer, factorCustomer, priceTable) VALUES (2, 2, 6, 1.2, 'TabelaB');




INSERT INTO team (id, idbusiness, idcourier, factorCourier) VALUES (1, 2, 3, 1.2);
INSERT INTO team (id, idbusiness, idcourier, factorCourier) VALUES (2, 2, 5, 1.3);



INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime) VALUES (1, 2, 1, 1, 'Origem A', 'Destino B', 'João', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP());
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime) VALUES (2, 2, 2, 2, 'Origem C', 'Destino D', 'Maria', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP());
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime) VALUES (3, 2, 1, 2, 'Origem E', 'Destino F', 'Carlos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP());



INSERT INTO price (id, idbusiness, idcustomer, tableName, vehicle, local, price) VALUES (1, 2, 1, 'TabelaA', 'Carro', 'SP', 50.0);
INSERT INTO price (id, idbusiness, idcustomer, tableName, vehicle, local, price) VALUES (2, 2, 2, 'TabelaB', 'Moto', 'RJ', 30.0);



INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime) VALUES (1, 1, 1, 'INFO', '11666666666', '11777777777', 'Entrega a caminho', CURRENT_TIMESTAMP());
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime) VALUES (2, 1, 2, 'INFO', '11777777777', '11666666666', 'Entrega recebida', CURRENT_TIMESTAMP());
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime) VALUES (3, 2, 1, 'INFO', '11444444444', '11555555555', 'Saiu para entrega', CURRENT_TIMESTAMP());
INSERT INTO sms (id, iddelivery, piece, type, mobileTo, mobileFrom, message, datetime) VALUES (4, 3, 1, 'INFO', '11666666666', '11555555555', 'Entrega especial concluída', CURRENT_TIMESTAMP());
