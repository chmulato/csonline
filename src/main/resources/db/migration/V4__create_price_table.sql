-- MySQL 8.0 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS price;
CREATE TABLE price (
  ID int NOT NULL AUTO_INCREMENT,
  VALUE decimal(10,2) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO price (VALUE) VALUES
  (10.00),
  (20.00);
