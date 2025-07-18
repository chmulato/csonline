
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
  ID SERIAL PRIMARY KEY,
  ROLE VARCHAR(15) NOT NULL,
  NAME VARCHAR(255) NOT NULL,
  LOGIN VARCHAR(15) NOT NULL,
  PASSWORD VARCHAR(15) NOT NULL,
  EMAIL VARCHAR(30) NOT NULL,
  EMAIL2 VARCHAR(30),
  ADDRESS VARCHAR(255),
  MOBILE VARCHAR(30)
);

INSERT INTO "user" (ROLE, NAME, LOGIN, PASSWORD, EMAIL, EMAIL2, ADDRESS, MOBILE) VALUES
  ('ADMINISTRATOR','CHRISTIAN MULATO','chmulato','admin','chmulato@hotmail.com',NULL,'Rua Ari Manfron, 123 - CIC - Curitiba - PR','00554199097797'),
  ('BUSINESS','PAULO MULATO','pmulato','admin','chmulato@hotmail.com',NULL,'RUA ABC PAULISTA','00554190909090'),
  ('BUSINESS','JOAO DA SILVA','joao','admin','joao@hotmail.com',NULL,'RUA ABC PAULISTA','00554190909088'),
  ('CUSTOMER','MARIO DA SILVA SA','mariosa','admin','mariosa@hotmail.com',NULL,'RUA TATU','00554180808080'),
  ('CUSTOMER','SANTA CLARA SA','santasa','admin','chmulato@hotmail.com',NULL,'RUA TATU, 3030','00554180808787'),
  ('COURIER','MANE GARRINCHA','mane','admin','chmulato@hotmail.com',NULL,'RUA ABC, 123 CIC','04188531130');
