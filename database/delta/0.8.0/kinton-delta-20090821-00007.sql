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

---------------------------------------------------------------------
-- 'resource_allocation_limit' and 'resource_type' tables created. --
-- Data inserted into 'resource_type'.                             --
---------------------------------------------------------------------

--
-- Definition of table `kinton`.`resource_allocation_limit`
--

DROP TABLE IF EXISTS `kinton`.`resource_allocation_limit`;
CREATE TABLE  `kinton`.`resource_allocation_limit` (
  `idRAL` int(11) unsigned NOT NULL auto_increment,
  
  `ramSoft` bigint(20) NOT NULL,
  `cpuSoft` bigint(20) NOT NULL,
  `hdSoft` bigint(20)  NOT NULL,
   
  `ramHard` bigint(20) NOT NULL,
  `cpuHard` bigint(20) NOT NULL,
  `hdHard` bigint(20)  NOT NULL,
  
  PRIMARY KEY  (`idRAL`)
  -- -KEY `RAL_FK1` (`idEnterprise`),
  -- -CONSTRAINT `RAL_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE,    
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`resource_type`
--

DROP TABLE IF EXISTS `kinton`.`resource_type`;
CREATE TABLE  `kinton`.`resource_type` (
  `idResource` int(1) unsigned NOT NULL,
  `name` varchar(25) NOT NULL,
  PRIMARY KEY  (`idResource`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Dumping data for table `kinton`.`resource_type`
--


/*!40000 ALTER TABLE `resource_type` DISABLE KEYS */;
LOCK TABLES `resource_type` WRITE;
INSERT INTO `kinton`.`resource_type` VALUES  (1,'Other'),
 (2,'Computer_System'),
 (3,'Processor'),
 (4,'Memory'),
 (5,'IDE_Controller'),
 (6,'Parallel_SCSI_HBA'),
 (7,'FC_HBA'),
 (8,'iSCSI_HBA'),
 (9,'IB_HCA'),
 (10,'Ethernet_Adapter'),
 (11,'Other_Network_Adapter'),
 (12,'IO_Slot'),
 (13,'IO_Device'),
 (14,'Floppy_Drive'),
 (15,'CD_Drive'),
 (16,'DVD_drive'),
 (17,'Disk_Drive'),
 (18,'Tape_Drive'),
 (19,'Storage_Extent'),
 (20,'Other_storage_device'),
 (21,'Serial_port'),
 (22,'Parallel_port'),
 (23,'USB_Controller'),
 (24,'Graphics_controller'),
 (25,'IEEE_1394_Controller'),
 (26,'Partitionable_Unit'),
 (27,'Base_Partitionable_Unit'),
 (28,'Power'),
 (29,'Cooling_Capacity'),
 (30,'Ethernet_Switch_Port'),
 (31,'DMTF_reserved'),
 (32,'Vendor_Reserved');
UNLOCK TABLES;
/*!40000 ALTER TABLE `resource_type` ENABLE KEYS */;


--
-- Dumping data for table `kinton`.`resource_allocation_limit`
--

INSERT INTO `kinton`.`resource_allocation_limit` VALUES  (1,5,5,5,25,25,25);
