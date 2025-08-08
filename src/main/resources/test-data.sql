-- Dados de teste para testes unitários
-- CSOnline - Sistema de Gestão de Centro de Distribuição

-- Usuários de teste
INSERT INTO TB_USER (ID, LOGIN, PASSWORD, NAME, ROLE, CREATION_DATE) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Administrador Teste', 'ADMIN', CURRENT_TIMESTAMP),
(2, 'business', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Usuário Business', 'BUSINESS', CURRENT_TIMESTAMP),
(3, 'courier', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Entregador Teste', 'COURIER', CURRENT_TIMESTAMP),
(4, 'customer', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Cliente Teste', 'CUSTOMER', CURRENT_TIMESTAMP);

-- Times de teste
INSERT INTO TB_TEAM (ID, NAME, DESCRIPTION, CREATION_DATE) VALUES
(1, 'Team Alpha', 'Time de teste Alpha', CURRENT_TIMESTAMP),
(2, 'Team Beta', 'Time de teste Beta', CURRENT_TIMESTAMP);

-- Entregadores de teste
INSERT INTO TB_COURIER (ID, NAME, CPF, PHONE, EMAIL, VEHICLE_TYPE, LICENSE_PLATE, TEAM_ID, CREATION_DATE) VALUES
(1, 'João Silva', '12345678901', '11999888777', 'joao@test.com', 'MOTO', 'ABC1234', 1, CURRENT_TIMESTAMP),
(2, 'Maria Santos', '09876543210', '11888777666', 'maria@test.com', 'BICICLETA', 'BIC001', 2, CURRENT_TIMESTAMP);

-- Clientes de teste
INSERT INTO TB_CUSTOMER (ID, NAME, CPF, PHONE, EMAIL, ADDRESS, CREATION_DATE) VALUES
(1, 'Empresa ABC', '11222333000144', '1133334444', 'contato@abc.com', 'Rua A, 123', CURRENT_TIMESTAMP),
(2, 'Loja XYZ', '55666777000155', '1155556666', 'contato@xyz.com', 'Av. B, 456', CURRENT_TIMESTAMP);

-- Preços de teste
INSERT INTO TB_PRICE (ID, DISTANCE_KM, PRICE, CREATION_DATE) VALUES
(1, 5.0, 10.50, CURRENT_TIMESTAMP),
(2, 10.0, 18.00, CURRENT_TIMESTAMP),
(3, 15.0, 25.50, CURRENT_TIMESTAMP);

-- Entregas de teste
INSERT INTO TB_DELIVERY (ID, CUSTOMER_ID, COURIER_ID, PICKUP_ADDRESS, DELIVERY_ADDRESS, DISTANCE_KM, TOTAL_PRICE, STATUS, CREATION_DATE) VALUES
(1, 1, 1, 'Rua Origem, 100', 'Rua Destino, 200', 8.5, 15.75, 'PENDING', CURRENT_TIMESTAMP),
(2, 2, 2, 'Av. Partida, 300', 'Rua Chegada, 400', 12.0, 20.00, 'DELIVERED', CURRENT_TIMESTAMP);

-- SMS de teste
INSERT INTO TB_SMS (ID, PHONE_NUMBER, MESSAGE, STATUS, CREATION_DATE) VALUES
(1, '11999888777', 'Sua entrega foi confirmada', 'SENT', CURRENT_TIMESTAMP),
(2, '11888777666', 'Entrega realizada com sucesso', 'SENT', CURRENT_TIMESTAMP);
