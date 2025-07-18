-- MySQL 8.0 compatible dump
-- Database: caracore_csodatabase

DROP TABLE IF EXISTS team;
CREATE TABLE team (
  ID int NOT NULL AUTO_INCREMENT,
  NAME varchar(255) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO team (NAME) VALUES
  ('Equipe A'),
  ('Equipe B');
