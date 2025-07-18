-- MySQL 8.0 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS sms;
CREATE TABLE sms (
  ID int NOT NULL AUTO_INCREMENT,
  MESSAGE varchar(255) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO sms (MESSAGE) VALUES
  ('Mensagem 1'),
  ('Mensagem 2');
