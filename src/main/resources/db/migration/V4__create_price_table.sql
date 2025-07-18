
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS price;
CREATE TABLE price (
  ID SERIAL PRIMARY KEY,
  VALUE NUMERIC(10,2) NOT NULL
);

INSERT INTO price (VALUE) VALUES
  (10.00),
  (20.00);
