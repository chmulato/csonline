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
-- Excluir tabela `sms`
--

-- DROP TABLE `sms`;

--
-- Estrutura da tabela `sms`
--

CREATE TABLE IF NOT EXISTS `sms` (
  `ID` int(11) NOT NULL,
  `IDDELIVERY` int(11) DEFAULT 0,
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

INSERT INTO `sms` (`ID`, `IDDELIVERY`, `PIECE`, `TYPE`, `MOBILE_TO`, `MOBILE_FROM`, `MESSAGE`) VALUES
(1, 1, 1, 'S', '04188531130' , '00554199097797', 'RUA PEDRO DA SILVA'),
(2, 2, 2, 'S', '04199097797' , '00554188531130', 'BAIRRO BACACHERI');

INSERT INTO `sms` (`ID`, `PIECE`, `TYPE`, `MOBILE_TO`, `MOBILE_FROM`, `MESSAGE`) VALUES
(3, 1, 'S', '04199097797' , '00554188531130', 'BAIRRO BACACHERI');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;