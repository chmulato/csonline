-- Arquivo de inicialização do banco H2 para testes
DROP TABLE IF EXISTS CSO_USER;
DROP TABLE IF EXISTS CSO_DELIVERY; 
DROP TABLE IF EXISTS CSO_CUSTOMER;

-- Criação da tabela de usuários para testes
CREATE TABLE CSO_USER (
    id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- Inserção de dados de teste
INSERT INTO CSO_USER (id, username, password, active) VALUES 
(1, 'admin', 'admin123', true),
(2, 'user', 'user123', true),
(3, 'test', 'test123', true);

-- Criação de outras tabelas básicas para testes
CREATE TABLE CSO_DELIVERY (
    id BIGINT NOT NULL PRIMARY KEY,
    description VARCHAR(255),
    status VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CSO_CUSTOMER (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE
);
