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
-- Estrutura da tabela `customer`
--

CREATE TABLE IF NOT EXISTS `customer` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `IDCUSTOMER` int(11) NOT NULL,
  `FACTOR_CUSTOMER` decimal(13,2) NOT NULL,
  `PRICE_TABLE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `customer`
--

INSERT INTO `customer` (`ID`, `IDBUSINESS`, `IDCUSTOMER`, `FACTOR_CUSTOMER`, `PRICE_TABLE`) VALUES
(1, 4, 5, '1.00', NULL),
(2, 3, 6, '1.00', 'BÁSICO');

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
(1, 3, 6, 7, '2013-12-13 00:53:55', 'RUA ABC', 'RUA DEF', 'SR. PEDRO', 'PACOTAO', '1.500', '0.0500', '21.000', '0.00', '20.00', 0, 0),
(2, 3, 6, 7, '2013-12-13 00:53:55', 'RUA ABC', 'RUA DEF', 'SR. PEDRO', 'PACOTAO', '1.500', '0.0500', '21.000', '0.00', '20.00', 0, 0);

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

INSERT INTO `price` (`ID`, `IDBUSINESS`, `TABLE_NAME`, `VEHICLE`, `LOCAL`, `PRICE`) VALUES
(1, 3, 'BÁSICO', 'MOTO', 'BIGORRILHO', '10.00'),
(2, 3, 'BASICO', 'MOTO', 'VILA HAUER', '15.00'),
(3, 3, 'BÁSICO', 'MOTO', 'VILA VERDE', '15.00');

-- --------------------------------------------------------

--
-- Estrutura da tabela `sms`
--

CREATE TABLE IF NOT EXISTS `sms` (
  `ID` int(11) NOT NULL,
  `IDDELIVERY` int(11) DEFAULT '0',
  `PIECE` smallint(6) NOT NULL DEFAULT '1',
  `TYPE` char(1) NOT NULL DEFAULT 'S',
  `MOBILE_TO` varchar(255) NOT NULL,
  `MOBILE_FROM` varchar(255) NOT NULL,
  `DATETIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MESSAGE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `sms`
--

INSERT INTO `sms` (`ID`, `IDDELIVERY`, `PIECE`, `TYPE`, `MOBILE_TO`, `MOBILE_FROM`, `DATETIME`, `MESSAGE`) VALUES
(1, 1, 1, 'S', '04188531130', '04199097797', '2013-12-14 00:31:37', 'RUA PEDRO DA SILVA'),
(2, 0, 1, 'R', '04199097797', '04188531130', '2013-12-14 00:31:22', 'BAIRRO BACACHERI'),
(3, 0, 1, 'R', '04199097797', '04188531130', '2013-12-13 22:31:32', 'BAIRRO BACACHERI');

-- --------------------------------------------------------

--
-- Estrutura da tabela `team`
--

CREATE TABLE IF NOT EXISTS `team` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `IDCOURIER` int(11) NOT NULL,
  `FACTOR_COURIER` decimal(13,2) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `team`
--

INSERT INTO `team` (`ID`, `IDBUSINESS`, `IDCOURIER`, `FACTOR_COURIER`) VALUES
(1, 3, 7, '1.20');

-- --------------------------------------------------------

--
-- Estrutura da tabela `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `ID` int(11) NOT NULL,
  `ROLE` varchar(15) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `LOGIN` varchar(15) NOT NULL,
  `PASSWORD` varchar(15) NOT NULL,
  `EMAIL` varchar(30) NOT NULL,
  `EMAIL2` varchar(30) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `MOBILE` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `user`
--

INSERT INTO `user` (`ID`, `ROLE`, `NAME`, `LOGIN`, `PASSWORD`, `EMAIL`, `EMAIL2`, `ADDRESS`, `MOBILE`) VALUES
(1, 'ADMINISTRATOR', 'CHRISTIAN MULATO', 'chmulato', 'admin', 'chmulato@hotmail.com', '', 'Rua Ari Manfron, 123 - CIC - Curitiba - PR', '00554199097797'),
(3, 'BUSINESS', 'PAULO MULATO', 'pmulato', 'admin', 'chmulato@hotmail.com', '', 'RUA ABC PAULISTA', '00554190909090'),
(4, 'BUSINESS', 'JOAO DA SILVA', 'joao', 'admin', 'joao@hotmail.com', '', 'RUA ABC PAULISTA', '00554190909088'),
(5, 'CUSTOMER', 'MARIO DA SILVA SA', 'mariosa', 'admin', 'mariosa@hotmail.com', '', 'RUA TATU', '00554180808080'),
(6, 'CUSTOMER', 'SANTA CLARA SA', 'santasa', 'admin', 'chmulato@hotmail.com', '', 'RUA TATU, 3030', '00554180808787'),
(7, 'COURIER', 'MANE GARRINCHA', 'mane', 'admin', 'chmulato@hotmail.com', '', 'RUA ABC, 123 CIC', '04188531130');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
