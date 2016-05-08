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
-- Estrutura da tabela `delivery`
--

CREATE TABLE IF NOT EXISTS `delivery` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `IDCUSTOMER` int(11) NOT NULL,
  `IDCOURIER` int(11) DEFAULT NULL,
  `DATETIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `START` varchar(255) NOT NULL,
  `DESTINATION` varchar(255) NOT NULL,
  `CONTACT` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `WEIGHT` decimal(13,3) DEFAULT NULL,
  `VOLUME` decimal(13,4) DEFAULT NULL,
  `KM` decimal(13,3) NOT NULL,
  `ADDITIONAL_COST` decimal(13,2) NOT NULL,
  `COST` decimal(13,2) NOT NULL,
  `COMPLETED` tinyint(1) NOT NULL,
  `RECEIVED` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `delivery`
--

INSERT INTO `delivery` (`ID`, `IDBUSINESS`, `IDCUSTOMER`, `IDCOURIER`, `DATETIME`, `START`, `DESTINATION`, `CONTACT`, `DESCRIPTION`, `WEIGHT`, `VOLUME`, `KM`, `ADDITIONAL_COST`, `COST`, `COMPLETED`, `RECEIVED`) VALUES
(1, 3, 6, NULL, '2013-11-22 22:12:05', 'RUA ABC', 'RUA DEF', 'SR. PEDRO', 'PACOTAO', '1.500', '0.0500', '21.000', '0.00', '20.00', 0, 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;