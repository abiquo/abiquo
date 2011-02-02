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

--
-- Definition of table `kinton`.`remote_service`
--

DROP TABLE IF EXISTS `kinton`.`remote_service`;
CREATE TABLE  `kinton`.`remote_service` (
  `idRemoteService` int(10) unsigned NOT NULL auto_increment,
  `idRemoteServiceType` int(2) unsigned NOT NULL,
  `uri` varchar(256) character set utf8 NOT NULL,
  `idUserCreation` int(11) default NULL,
  `idUser_lastModification` int(11) default NULL,
  `name` varchar(256) character set utf8 default NULL,
  `creationDate` timestamp NOT NULL default '0000-00-00 00:00:00',
  `lastModificationDate` timestamp NOT NULL default '0000-00-00 00:00:00',
  `uuid` varchar(40) NOT NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idRemoteService`),
  KEY `idRemoteServiceType_FK1` (`idRemoteServiceType`),
  KEY `idRemoteServiceType_FK2` (`idDataCenter`),
  CONSTRAINT `idRemoteServiceType_FK2` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE,
  CONSTRAINT `idRemoteServiceType_FK1` FOREIGN KEY (`idRemoteServiceType`) REFERENCES `remote_service_type` (`idRemoteServiceType`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kinton`.`remote_service`
--

/*!40000 ALTER TABLE `remote_service` DISABLE KEYS */;
LOCK TABLES `remote_service` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `remote_service` ENABLE KEYS */;

--
-- Definition of table `kinton`.`remote_service_type`
--

DROP TABLE IF EXISTS `kinton`.`remote_service_type`;
CREATE TABLE  `kinton`.`remote_service_type` (
  `idRemoteServiceType` int(2) unsigned NOT NULL,
  `name` varchar(40) character set utf8 NOT NULL,
  PRIMARY KEY  (`idRemoteServiceType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kinton`.`remote_service_type`
--

/*!40000 ALTER TABLE `remote_service_type` DISABLE KEYS */;
LOCK TABLES `remote_service_type` WRITE;
INSERT INTO `kinton`.`remote_service_type` VALUES  (1,'VirtualFactory');
UNLOCK TABLES;
/*!40000 ALTER TABLE `remote_service_type` ENABLE KEYS */;

--
-- Definition of table `kinton`.`virtualfactory`
--

DROP TABLE IF EXISTS `kinton`.`virtualfactory`;
CREATE TABLE  `kinton`.`virtualfactory` (
  `idRemoteService` int(10) unsigned NOT NULL,
  KEY `virtualfactory_FK1` (`idRemoteService`),
  CONSTRAINT `virtualfactory_FK1` FOREIGN KEY (`idRemoteService`) REFERENCES `remote_service` (`idRemoteService`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kinton`.`virtualfactory`
--

/*!40000 ALTER TABLE `virtualfactory` DISABLE KEYS */;
LOCK TABLES `virtualfactory` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualfactory` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
