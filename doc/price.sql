-- phpMyAdmin SQL Dump
-- version 3.5.3
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tempo de Geração: 
-- Versão do Servidor: 5.5.28
-- Versão do PHP: 5.3.18

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Banco de Dados: `csodatabase`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `price`
--

CREATE TABLE IF NOT EXISTS `price` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `TABLE_NAME` varchar(255) NOT NULL,
  `VEHICLE` varchar(255) NOT NULL,
  `LOCAL` varchar(255) DEFAULT NULL,
  `PRICE` decimal(13,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `price`
--

INSERT INTO `price` (`ID`,`IDBUSINESS`,`TABLE_NAME`, `VEHICLE`, `LOCAL`, `PRICE`) VALUES
(1, 3, 'BASICO', 'MOTO', 'BIGORRILHO', '10.00'),
(2, 3, 'BASICO', 'MOTO', 'VILA HAUER', '15.00');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
