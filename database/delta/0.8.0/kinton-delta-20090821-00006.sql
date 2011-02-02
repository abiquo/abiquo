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

--------------------------------------------------
-- 'rasd' and 'rasd_management' tables created. --
--------------------------------------------------


--
-- Definition of table `kinton`.`rasd`
--

DROP TABLE IF EXISTS `kinton`.`rasd`;
CREATE TABLE  `kinton`.`rasd` (
  `address` varchar(256) default NULL,
  `addressOnParent` varchar(25) default NULL,
  `allocationUnits` varchar(15) default NULL,
  `automaticAllocation` int(1) default NULL,
  `automaticDeallocation` int(1) default NULL,
  `caption` varchar(15) default NULL,
  `changeableType` int(1) default NULL,
  `configurationName` varchar(15) default NULL,
  `connectionResource` varchar(256) default NULL,
  `consumerVisibility` int(5) default NULL,
  `description` varchar(50) default NULL,
  `elementName` varchar(15) NOT NULL,
  `generation` int(20) default NULL,
  `hostResource` varchar(256) default NULL,
  `instanceID` varchar(50) NOT NULL,
  `limitResource` int(20) default NULL,
  `mappingBehaviour` int(5) default NULL,
  `otherResourceType` varchar(50) default NULL,
  `parent` varchar(50) default NULL,
  `poolID` varchar(50) default NULL,
  `reservation` int(20) default NULL,
  `resourceSubType` varchar(15) default NULL,
  `resourceType` int(5) NOT NULL,
  `virtualQuantity` int(20) default NULL,
  `weight` int(5) default NULL,
  PRIMARY KEY  (`instanceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`rasd_management`
--

DROP TABLE IF EXISTS `kinton`.`rasd_management`;
CREATE TABLE  `kinton`.`rasd_management` (
  `idManagement` int(10) unsigned NOT NULL auto_increment,
  `idResourceType` varchar(5) NOT NULL,
  `idVirtualDataCenter` int(10) unsigned default NULL,
  `idVM` int(10) unsigned default NULL,
  `idResource` varchar(50) NOT NULL,
  `idVirtualApp` int(10) unsigned default NULL,
  `user_creation` varchar(40) default NULL,
  `data_creation` timestamp NOT NULL default '0000-00-00 00:00:00',
  `user_last_modification` varchar(40) default NULL,
  `data_last_modification` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`idManagement`),
  KEY `idVirtualApp_FK` (`idVirtualApp`),
  KEY `idVM_FK` (`idVM`),
  KEY `idVirtualDataCenter_FK` (`idVirtualDataCenter`),
  KEY `idResource_FK` (`idResource`),
  CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE SET NULL,
  CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `virtualdatacenter` (`idVirtualDataCenter`) ON DELETE SET NULL,
  CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE SET NULL,
  CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `rasd` (`instanceID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
