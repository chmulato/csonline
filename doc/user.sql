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
(7, 'COURIER', 'MANE GARRINCHA', 'mane', 'admin', 'chmulato@hotmail.com', '', 'RUA ABC, 123 CIC', '00554180808081');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
