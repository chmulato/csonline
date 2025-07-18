-- MySQL 8.0 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS delivery;
CREATE TABLE delivery (
  ID int NOT NULL AUTO_INCREMENT,
  DESCRIPTION varchar(255) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO delivery (DESCRIPTION) VALUES
  ('Entrega 1'),
  ('Entrega 2');
