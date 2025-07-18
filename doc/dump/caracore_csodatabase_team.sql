
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS team;
CREATE TABLE team (
  ID SERIAL PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL
);

INSERT INTO team (NAME) VALUES
  ('Equipe A'),
  ('Equipe B');
