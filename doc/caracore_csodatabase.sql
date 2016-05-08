-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 08-Maio-2016 às 17:48
-- Versão do servidor: 10.1.9-MariaDB
-- PHP Version: 5.5.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `caracore_csodatabase`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `customer`
--

CREATE TABLE `customer` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `IDCUSTOMER` int(11) NOT NULL,
  `FACTOR_CUSTOMER` decimal(13,2) NOT NULL,
  `PRICE_TABLE` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `customer`
--

INSERT INTO `customer` (`ID`, `IDBUSINESS`, `IDCUSTOMER`, `FACTOR_CUSTOMER`, `PRICE_TABLE`) VALUES
(1, 4, 5, '1.00', NULL),
(2, 3, 6, '1.00', NULL);

-- --------------------------------------------------------

--
-- Estrutura da tabela `delivery`
--

CREATE TABLE `delivery` (
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
  `RECEIVED` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `delivery`
--

INSERT INTO `delivery` (`ID`, `IDBUSINESS`, `IDCUSTOMER`, `IDCOURIER`, `DATETIME`, `START`, `DESTINATION`, `CONTACT`, `DESCRIPTION`, `WEIGHT`, `VOLUME`, `KM`, `ADDITIONAL_COST`, `COST`, `COMPLETED`, `RECEIVED`) VALUES
(1, 3, 6, NULL, '2013-11-22 22:12:05', 'RUA ABC', 'RUA DEF', 'SR. PEDRO', 'PACOTAO', '1.500', '0.0500', '21.000', '0.00', '20.00', 0, 0);

-- --------------------------------------------------------

--
-- Estrutura da tabela `price`
--

CREATE TABLE `price` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `TABLE_NAME` varchar(255) NOT NULL,
  `VEHICLE` varchar(255) NOT NULL,
  `LOCAL` varchar(255) DEFAULT NULL,
  `PRICE` decimal(13,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `price`
--

INSERT INTO `price` (`ID`, `IDBUSINESS`, `TABLE_NAME`, `VEHICLE`, `LOCAL`, `PRICE`) VALUES
(1, 3, 'BASICO', 'MOTO', 'BIGORRILHO', '10.00'),
(2, 3, 'BASICO', 'MOTO', 'VILA HAUER', '15.00');

-- --------------------------------------------------------

--
-- Estrutura da tabela `sms`
--

CREATE TABLE `sms` (
  `ID` int(11) NOT NULL,
  `IDDELIVERY` int(11) DEFAULT '0',
  `PIECE` smallint(6) NOT NULL DEFAULT '1',
  `TYPE` char(1) NOT NULL DEFAULT 'S',
  `TO` varchar(255) NOT NULL,
  `FROM` varchar(255) NOT NULL,
  `DATETIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `MESSAGE` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `sms`
--

INSERT INTO `sms` (`ID`, `IDDELIVERY`, `PIECE`, `TYPE`, `TO`, `FROM`, `DATETIME`, `MESSAGE`) VALUES
(1, 1, 1, 'S', '04188531130', '00554199097797', '2015-12-31 19:17:32', 'RUA PEDRO DA SILVA'),
(2, 2, 2, 'S', '04199097797', '00554188531130', '2015-12-31 19:17:32', 'BAIRRO BACACHERI'),
(3, 0, 1, 'S', '04199097797', '00554188531130', '2015-12-31 19:17:32', 'BAIRRO BACACHERI');

-- --------------------------------------------------------

--
-- Estrutura da tabela `team`
--

CREATE TABLE `team` (
  `ID` int(11) NOT NULL,
  `IDBUSINESS` int(11) NOT NULL,
  `IDCOURIER` int(11) NOT NULL,
  `FACTOR_COURIER` decimal(13,2) NOT NULL
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

CREATE TABLE `user` (
  `ID` int(11) NOT NULL,
  `ROLE` varchar(15) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `LOGIN` varchar(15) NOT NULL,
  `PASSWORD` varchar(15) NOT NULL,
  `EMAIL` varchar(30) NOT NULL,
  `EMAIL2` varchar(30) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL,
  `MOBILE` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `user`
--

INSERT INTO `user` (`ID`, `ROLE`, `NAME`, `LOGIN`, `PASSWORD`, `EMAIL`, `EMAIL2`, `ADDRESS`, `MOBILE`) VALUES
(1, 'ADMINISTRATOR', 'CHRISTIAN MULATO', 'chmulato', 'admin', 'chmulato@hotmail.com', '', 'Rua Ari Manfron, 123 - CIC - Curitiba - PR', '00554199097797'),
(3, 'BUSINESS', 'PAULO MULATO', 'pmulato', 'admin', 'chmulato@hotmail.com', '', 'RUA ABC PAULISTA', '00554190909090'),
(4, 'BUSINESS', 'JOAO DA SILVA', 'joao', 'admin', 'joao@hotmail.com', '', 'RUA ABC PAULISTA, 123', '00554190909088'),
(5, 'CUSTOMER', 'MARIO DA SILVA SA', 'mariosa', 'admin', 'mariosa@hotmail.com', '', 'RUA TATU', '00554180808080'),
(6, 'CUSTOMER', 'SANTA CLARA SA', 'santasa', 'admin', 'chmulato@hotmail.com', '', 'RUA TATU, 3030', '00554180808787'),
(7, 'COURIER', 'MANE GARRINCHA', 'mane', 'admin', 'chmulato@hotmail.com', '', 'RUA ABC, 123 CIC', '00554180808081');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `delivery`
--
ALTER TABLE `delivery`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `price`
--
ALTER TABLE `price`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `sms`
--
ALTER TABLE `sms`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `team`
--
ALTER TABLE `team`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
