-- Tabela de usuários
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    email2 VARCHAR(100),
    address VARCHAR(200),
    mobile VARCHAR(20)
);

-- Tabela de clientes
CREATE TABLE customer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idbusiness INT NOT NULL,
    idcustomer INT NOT NULL,
    factor_customer DECIMAL(10,2),
    price_table VARCHAR(50),
    FOREIGN KEY (idbusiness) REFERENCES user(id),
    FOREIGN KEY (idcustomer) REFERENCES user(id)
);

-- Tabela de entregadores (team)
CREATE TABLE team (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idbusiness INT NOT NULL,
    idcourier INT NOT NULL,
    factor_courier DECIMAL(10,2),
    FOREIGN KEY (idbusiness) REFERENCES user(id),
    FOREIGN KEY (idcourier) REFERENCES user(id)
);

-- Tabela de entregas (delivery)
CREATE TABLE delivery (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idbusiness INT NOT NULL,
    idcustomer INT NOT NULL,
    idcourier INT NOT NULL,
    start VARCHAR(200),
    destination VARCHAR(200),
    contact VARCHAR(100),
    description VARCHAR(200),
    volume VARCHAR(50),
    weight DECIMAL(10,2),
    km DECIMAL(10,2),
    additional_cost DECIMAL(10,2),
    cost DECIMAL(10,2),
    received BOOLEAN DEFAULT FALSE,
    completed BOOLEAN DEFAULT FALSE,
    datatime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idbusiness) REFERENCES user(id),
    FOREIGN KEY (idcustomer) REFERENCES user(id),
    FOREIGN KEY (idcourier) REFERENCES user(id)
);

-- Tabela de preços (price)
CREATE TABLE price (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idbusiness INT NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    vehicle VARCHAR(50),
    local VARCHAR(100),
    price DECIMAL(10,2),
    idcustomer INT,
    FOREIGN KEY (idbusiness) REFERENCES user(id),
    FOREIGN KEY (idcustomer) REFERENCES user(id)
);

-- Tabela de SMS
CREATE TABLE sms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    iddelivery INT NOT NULL,
    piece INT,
    type CHAR(1),
    mobile_to VARCHAR(20),
    mobile_from VARCHAR(20),
    message VARCHAR(500),
    datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (iddelivery) REFERENCES delivery(id)
);

-- Índices e constraints extras podem ser adicionados conforme necessário.
