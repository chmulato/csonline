
-- PostgreSQL 15 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS sms;
CREATE TABLE sms (
  ID SERIAL PRIMARY KEY,
  MESSAGE VARCHAR(255) NOT NULL
);

INSERT INTO sms (MESSAGE) VALUES
  ('Mensagem 1'),
  ('Mensagem 2');
