-- MySQL 8.0 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  ID int NOT NULL AUTO_INCREMENT,
  ROLE varchar(15) NOT NULL,
  NAME varchar(255) NOT NULL,
  LOGIN varchar(15) NOT NULL,
  PASSWORD varchar(15) NOT NULL,
  EMAIL varchar(30) NOT NULL,
  EMAIL2 varchar(30) DEFAULT NULL,
  ADDRESS varchar(255) DEFAULT NULL,
  MOBILE varchar(30) DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user (ROLE, NAME, LOGIN, PASSWORD, EMAIL, EMAIL2, ADDRESS, MOBILE) VALUES
  ('ADMINISTRATOR','CHRISTIAN MULATO','chmulato','admin','chmulato@hotmail.com','','Rua Ari Manfron, 123 - CIC - Curitiba - PR','00554199097797'),
  ('BUSINESS','PAULO MULATO','pmulato','admin','chmulato@hotmail.com','','RUA ABC PAULISTA','00554190909090'),
  ('BUSINESS','JOAO DA SILVA','joao','admin','joao@hotmail.com','','RUA ABC PAULISTA','00554190909088'),
  ('CUSTOMER','MARIO DA SILVA SA','mariosa','admin','mariosa@hotmail.com','','RUA TATU','00554180808080'),
  ('CUSTOMER','SANTA CLARA SA','santasa','admin','chmulato@hotmail.com','','RUA TATU, 3030','00554180808787'),
  ('COURIER','MANE GARRINCHA','mane','admin','chmulato@hotmail.com','','RUA ABC, 123 CIC','04188531130');
