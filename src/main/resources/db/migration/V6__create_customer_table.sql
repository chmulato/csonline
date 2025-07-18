
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
  ID SERIAL PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL
);

INSERT INTO customer (NAME) VALUES
  ('Cliente 1'),
  ('Cliente 2');
