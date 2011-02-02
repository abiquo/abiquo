-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.4


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

------------------------------------------------
-- Two new columns in virtualdatacenter table --
------------------------------------------------


--
-- Add idNetwork and idHypervisor columns
--

ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `idDataCenter` INT(10) UNSIGNED NOT NULL AFTER `name`, 
ADD COLUMN `idHypervisorType` INT(5) NOT NULL AFTER `idDataCenter`,
ADD COLUMN `networktypeID` int(11) UNSIGNED NOT NULL AFTER `idHypervisorType`;


--
-- Add foreign key named 'virtualDataCenter_FK4' referencing table 'networktype'
--

ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY `virtualDataCenter_FK4` (`networktypeID`)
REFERENCES `networktype` (`networktypeID`)
ON UPDATE RESTRICT;


--
-- Add foreign key named 'virtualDataCenter_FK5' referencing table 'hypervisortype'
--

ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK5` FOREIGN KEY `virtualDataCenter_FK5` (`idHypervisorType`)
REFERENCES `hypervisortype` (`id`)
ON DELETE RESTRICT
ON UPDATE RESTRICT;