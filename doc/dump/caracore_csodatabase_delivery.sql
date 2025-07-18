
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS delivery;
CREATE TABLE delivery (
  ID SERIAL PRIMARY KEY,
  DESCRIPTION VARCHAR(255) NOT NULL
);

INSERT INTO delivery (DESCRIPTION) VALUES
  ('Entrega 1'),
  ('Entrega 2');
