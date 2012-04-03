-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version  5.0.51a-3ubuntu5.4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema kinton
--
DROP DATABASE IF EXISTS kinton;
CREATE DATABASE IF NOT EXISTS kinton;
USE kinton;

--
-- Definition of table `kinton`.`appliancemanagernotification`
--

DROP TABLE IF EXISTS `kinton`.`appliancemanagernotification`;

--
-- Definition of table `kinton`.`auth_group`
--

DROP TABLE IF EXISTS `kinton`.`auth_group`;
CREATE TABLE  `kinton`.`auth_group` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(20) default NULL,
  `description` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`auth_group`
--

/*!40000 ALTER TABLE `auth_group` DISABLE KEYS */;
LOCK TABLES `auth_group` WRITE;
INSERT INTO `auth_group` VALUES
 (1,'GENERIC', 'Generic'),
 (2,'MAIN','Flex client main menu group'),
 (3,'USER','Flex and server Users Management'),
 (4,'APPLIANCE_LIBRARY','Flex and server Appliance Library Management');
UNLOCK TABLES;
/*!40000 ALTER TABLE `auth_group` ENABLE KEYS */;


--
-- Definition of table `kinton`.`auth_serverresource`
--

DROP TABLE IF EXISTS `kinton`.`auth_serverresource`;
CREATE TABLE  `kinton`.`auth_serverresource` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(50) default NULL,
  `description` varchar(100) default NULL,
  `idGroup` int(11) unsigned default NULL,
  `idRole` int(3) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `auth_serverresourceFK1` (`idGroup`),
  KEY `auth_serverresourceFK2` (`idRole`),
  CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `auth_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `role` (`idRole`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`auth_serverresource`
--

/*!40000 ALTER TABLE `auth_serverresource` DISABLE KEYS */;
LOCK TABLES `auth_serverresource` WRITE;
INSERT INTO `kinton`.`auth_serverresource` VALUES  (1,'LOGIN','Login Service',1,2),
 (2,'ENTERPRISE_GET_ALL_ENTERPRISES','Security to retrieve the whole list of enterprises',3,1),
 (3,'ENTERPRISE_GET_ENTERPRISES','Security to call method getEnterprises in UserCommand',3,3),
 (4,'USER_GETUSERS','Security to call method getUsers in UserCommand',3,3),
 (5,'USER_GET_ALL_USERS','Security to retrieve the whole list of users',3,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `auth_serverresource` ENABLE KEYS */;


--
-- Definition of table `kinton`.`auth_serverresource_exception`
--

DROP TABLE IF EXISTS `kinton`.`auth_serverresource_exception`;
CREATE TABLE  `kinton`.`auth_serverresource_exception` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `idResource` int(11) unsigned NOT NULL,
  `idUser` int(10) unsigned NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `auth_serverresource_exceptionFK1` (`idResource`),
  KEY `auth_serverresource_exceptionFK2` (`idUser`),
  CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `auth_serverresource` (`id`) ON DELETE CASCADE,
  CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`auth_serverresource_exception`
--

/*!40000 ALTER TABLE `auth_serverresource_exception` DISABLE KEYS */;
LOCK TABLES `auth_serverresource_exception` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `auth_serverresource_exception` ENABLE KEYS */;

--
-- Definition of table `kinton`.`category`
--

DROP TABLE IF EXISTS `kinton`.`category`;
CREATE TABLE  `kinton`.`category` (
  `idCategory` int(3) unsigned NOT NULL auto_increment,
  `name` varchar(30) NOT NULL,
  `isErasable` int(1) unsigned NOT NULL default '1',
  `isDefault` int(1) unsigned NOT NULL default '0',
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idCategory`),
  UNIQUE KEY (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`category`
--

/*!40000 ALTER TABLE `category` DISABLE KEYS */;
LOCK TABLES `category` WRITE;
INSERT INTO `kinton`.`category` VALUES  (1,'Others',0,1,0),
 (2,'Database servers',1,0, 0),
 (4,'Applications servers',1,0,0),
 (5,'Web servers',1,0,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;

--
-- NETWORK TABLES
--
-- DROP THE TABLES RELATED TO NETWORK --
DROP TABLE IF EXISTS `kinton`.`vlan_network`;
DROP TABLE IF EXISTS `kinton`.`network_configuration`;
DROP TABLE IF EXISTS `kinton`.`network`;
DROP TABLE IF EXISTS `kinton`.`vlan_network_assignment`;

--
-- Definition of table `kinton`.`network`
--
CREATE TABLE  `kinton`.`network` (
  `network_id` int(11) unsigned NOT NULL auto_increment,
  `uuid` varchar(40) NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`network_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`network_configuration`
--
CREATE TABLE `kinton`.`network_configuration` (
  `network_configuration_id` int(11) unsigned NOT NULL auto_increment,
  `gateway` varchar(40),
  `network_address` varchar(40) NOT NULL,
  `mask` int(4) NOT NULL,
  `netmask` varchar(20) NOT NULL,
  `primary_dns` varchar(20),
  `secondary_dns` varchar(20),
  `sufix_dns` varchar(40),
  `fence_mode` varchar(20) NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`network_configuration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`vlan_network`
--
CREATE TABLE  `kinton`.`vlan_network` (
  `vlan_network_id` int(11) unsigned NOT NULL auto_increment,
  `network_id` int(11) unsigned NOT NULL,
  `network_configuration_id` int(11) unsigned NOT NULL, 
  `network_name` varchar(40) NOT NULL,
  `vlan_tag` int(4) unsigned DEFAULT NULL,
  `networktype` varchar(15) NOT NULL DEFAULT 'INTERNAL',
  `version_c` integer NOT NULL DEFAULT 1,
  `enterprise_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY  (`vlan_network_id`),
  KEY `vlannetwork_network_FK` (`network_id`),
  KEY `vlannetwork_configuration_FK` (`network_configuration_id`),
  KEY `vlannetwork_enterprise_FK` (`enterprise_id`),
  CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `enterprise` (`idEnterprise`),
  CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `network` (`network_id`) ON DELETE CASCADE,
  CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `network_configuration` (`network_configuration_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `kinton`.`vlan_network_assignment` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `vlan_network_id` INTEGER UNSIGNED NOT NULL,
  `idRack` INT(15) UNSIGNED NOT NULL,
  `idVirtualDataCenter` int(10) UNSIGNED default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`id`),
  INDEX `vlan_network_assignment_networkid_FK`(`vlan_network_id`),
  INDEX `vlan_network_assignment_idRack_FK`(`idRack`),
  CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY `vlan_network_assignment_networkid_FK` (`vlan_network_id`)
    REFERENCES `vlan_network` (`vlan_network_id`)    ON DELETE CASCADE,
  CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `virtualdatacenter` (`idVirtualDataCenter`) ON DELETE SET NULL,
  CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY `vlan_network_assignment_idRack_FK` (`idRack`)
    REFERENCES `rack` (`idRack`)
    ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `network` DISABLE KEYS */;
LOCK TABLES `network` WRITE;
INSERT INTO `kinton`.`network` VALUES  (1, "6cd20366-72e5-11df-8f9d-002564aeca80", 1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `network` ENABLE KEYS */;

-- NETWORK TABLES END!!!

--
-- Definition of table `kinton`.`datacenter`
--

DROP TABLE IF EXISTS `kinton`.`datacenter`;
CREATE TABLE  `kinton`.`datacenter` (
  `idDataCenter` int(10) unsigned NOT NULL auto_increment,
  `uuid` varchar(40) default NULL,
  `name` varchar(20) NOT NULL,
  `situation` varchar(100) default NULL,
  `network_id` int(11) unsigned default NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idDataCenter`),
  KEY `datacenternetwork_FK1` (`network_id`),
  CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `network` (`network_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`datacenter`
--

/*!40000 ALTER TABLE `datacenter` DISABLE KEYS */;
LOCK TABLES `datacenter` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `datacenter` ENABLE KEYS */;

--
-- Definition of table `kinton`.`enterprise`
--

DROP TABLE IF EXISTS `kinton`.`enterprise`;
CREATE TABLE  `kinton`.`enterprise` (
  `idEnterprise` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(40) NOT NULL,
  `ramSoft` bigint(20) NOT NULL default 0,
  `cpuSoft` bigint(20) NOT NULL default 0,
  `hdSoft` bigint(20)  NOT NULL default 0,
  `storageSoft` bigint(20)  NOT NULL default 0,
  `repositorySoft` bigint(20)  NOT NULL default 0,
  `vlanSoft` bigint(20)  NOT NULL default 0,
  `publicIPSoft` bigint(20)  NOT NULL default 0,
  `ramHard` bigint(20) NOT NULL default 0,
  `cpuHard` bigint(20) NOT NULL default 0,
  `hdHard` bigint(20)  NOT NULL default 0,
  `storageHard` bigint(20)  NOT NULL default 0,
  `repositoryHard` bigint(20)  NOT NULL default 0,
  `vlanHard` bigint(20)  NOT NULL default 0,
  `publicIPHard` bigint(20)  NOT NULL default 0,
  `chef_url` varchar(255) default NULL,
  `chef_client` varchar(50) default NULL,
  `chef_validator` varchar(50) default NULL,
  `chef_client_certificate` text default NULL,
  `chef_validator_certificate` text default NULL,
  `isReservationRestricted` tinyint(1) default '0',
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`enterprise`
--

/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
LOCK TABLES `enterprise` WRITE;
INSERT INTO `kinton`.`enterprise` VALUES  (1,'Abiquo',0,0,0,0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;

--
-- Definition of table `kinton`.`enterprise_properties`
--

DROP TABLE IF EXISTS `kinton`.`enterprise_properties`;
CREATE TABLE  `kinton`.`enterprise_properties` (
  `idProperties` int(11) unsigned NOT NULL auto_increment,
  `enterprise` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY  (`idProperties`),
  CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`enterprise_properties`
--

/*!40000 ALTER TABLE `enterprise_properties` DISABLE KEYS */;
LOCK TABLES `enterprise_properties` WRITE;
INSERT INTO `kinton`.`enterprise_properties` VALUES  (1,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise_properties` ENABLE KEYS */;

--
-- Definition of table `kinton`.`enterprise_properties_map`
--

DROP TABLE IF EXISTS `kinton`.`enterprise_properties_map`;
CREATE TABLE  `kinton`.`enterprise_properties_map` (
 `enterprise_properties` int(11) unsigned NOT NULL,
  `map_key` varchar(30) NOT NULL,
  `value` varchar(50) default NULL, 
  CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `enterprise_properties` (`idProperties`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`enterprise_properties_map`
--

/*!40000 ALTER TABLE `enterprise_properties_map` DISABLE KEYS */;
LOCK TABLES `enterprise_properties_map` WRITE;
INSERT INTO `kinton`.`enterprise_properties_map` VALUES  (1,'Support e-mail','support@abiquo.com');
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise_properties_map` ENABLE KEYS */;

--
-- Definition of table `kinton`.`hypervisor`
--

DROP TABLE IF EXISTS `kinton`.`hypervisor`;
CREATE TABLE  `kinton`.`hypervisor` (
  `id` int(20) unsigned NOT NULL auto_increment,
  `idPhysicalMachine` int(20) unsigned NOT NULL,
  `ip` varchar(39) NOT NULL,
  `ipService` varchar(39) NOT NULL,
  `port` int(5) NOT NULL,
  `user` varchar(255) NOT NULL DEFAULT 'user',
  `password` varchar(255) NOT NULL DEFAULT 'password',
  `version_c` int(11) default 0,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `Hypervisor_FK1` (`idPhysicalMachine`),
  CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `physicalmachine` (`idPhysicalMachine`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`log`
--

DROP TABLE IF EXISTS `kinton`.`log`;
CREATE TABLE  `kinton`.`log` (
  `idLog` int(10) unsigned NOT NULL auto_increment,
  `idVirtualApp` int(10) unsigned NOT NULL,
  `description` varchar(250) NOT NULL,
  `logDate` timestamp NOT NULL,
  `deleted` tinyint(1) unsigned DEFAULT '0',
  PRIMARY KEY  (`idLog`),
  KEY `log_FK1` (`idVirtualApp`),
  CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Dumping data for table `kinton`.`log`
--

/*!40000 ALTER TABLE `log` DISABLE KEYS */;
LOCK TABLES `log` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `log` ENABLE KEYS */;

--
-- Definition of table `kinton`.`node`
--

DROP TABLE IF EXISTS `kinton`.`node`;
CREATE TABLE  `kinton`.`node` (
  `idVirtualApp` int(10) unsigned NOT NULL,
  `idNode` int(10) unsigned NOT NULL auto_increment,
  `modified` int(2) NOT NULL,
  `posX` int(3) NOT NULL DEFAULT 0,
  `posY` int(3) NOT NULL DEFAULT 0, 
  `type` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `ip` varchar(15) default NULL,
  `mac` varchar(17) default NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idNode`),
  KEY `Nodes_FK4` (`idVirtualApp`),
  CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`node`
--

/*!40000 ALTER TABLE `node` DISABLE KEYS */;
LOCK TABLES `node` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `node` ENABLE KEYS */;


--
-- Definition of table `kinton`.`nodenetwork`
--

DROP TABLE IF EXISTS `kinton`.`nodenetwork`;
CREATE TABLE  `kinton`.`nodenetwork` (
  `idNode` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idNode`),
  CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `node` (`idNode`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`nodenetwork`
--

/*!40000 ALTER TABLE `nodenetwork` DISABLE KEYS */;
LOCK TABLES `nodenetwork` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `nodenetwork` ENABLE KEYS */;


--
-- Definition of table `kinton`.`noderelationtype`
--

DROP TABLE IF EXISTS `kinton`.`noderelationtype`;
CREATE TABLE  `kinton`.`noderelationtype` (
  `idNodeRelationType` int(2) unsigned NOT NULL auto_increment,
  `name` varchar(20) default NULL,
  PRIMARY KEY  (`idNodeRelationType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`noderelationtype`
--

/*!40000 ALTER TABLE `noderelationtype` DISABLE KEYS */;
LOCK TABLES `noderelationtype` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `noderelationtype` ENABLE KEYS */;


--
-- Definition of table `kinton`.`nodestorage`
--

DROP TABLE IF EXISTS `kinton`.`nodestorage`;
CREATE TABLE  `kinton`.`nodestorage` (
  `idNode` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`idNode`),
  CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `node` (`idNode`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`nodestorage`
--

/*!40000 ALTER TABLE `nodestorage` DISABLE KEYS */;
LOCK TABLES `nodestorage` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `nodestorage` ENABLE KEYS */;

--
-- Definition of table `kinton`.`nodevirtualimage`
--

DROP TABLE IF EXISTS `kinton`.`nodevirtualimage`;
CREATE TABLE  `kinton`.`nodevirtualimage` (
  `idNode` int(10) unsigned NOT NULL,
  `idVM` int(10) unsigned default NULL,
  `idImage` int(10) unsigned NOT NULL,
  `version_c` int(11) default 0,
  KEY `nodevirtualImage_FK1` (`idImage`),
  KEY `nodevirtualImage_FK2` (`idVM`),
  KEY `nodevirtualimage_FK3` (`idNode`),
  CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `virtualimage` (`idImage`),
  CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE SET NULL,
  CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `node` (`idNode`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`nodevirtualimage`
--

/*!40000 ALTER TABLE `nodevirtualimage` DISABLE KEYS */;
LOCK TABLES `nodevirtualimage` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `nodevirtualimage` ENABLE KEYS */;


--
-- Definition of table `kinton`.`physicalmachine`
--

DROP TABLE IF EXISTS `kinton`.`physicalmachine`;
CREATE TABLE  `kinton`.`physicalmachine` (
  `idPhysicalMachine` int(20) unsigned NOT NULL auto_increment,
  `idRack` int(15) unsigned default NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  `name` varchar(256) NOT NULL,
  `description` varchar(100) default NULL,
  `ram` int(7) NOT NULL,
  `cpu` int(11) NOT NULL,
  `ramUsed` int(7) NOT NULL,
  `cpuUsed` int(11) NOT NULL,
  `idState` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - STOPPED
1 - NOT PROVISIONED
2 - NOT MANAGED
3 - MANAGED
4 - HALTED
5 - UNLICENSED
6 - HA_IN_PROGRESS
7 - DISABLED_FOR_HA',
  `vswitchName` VARCHAR(200)  NOT NULL,
  `idEnterprise` int(10) unsigned default NULL,
  `initiatorIQN` VARCHAR(256) DEFAULT NULL,
  `version_c` int(11) default 0,
  `ipmiIP` varchar(39) default NULL,
  `ipmiPort` int(5) unsigned default NULL,
  `ipmiUser` varchar(255) default NULL,
  `ipmiPassword` varchar(255) default NULL,
  PRIMARY KEY  (`idPhysicalMachine`),
  KEY `PhysicalMachine_FK1` (`idRack`),
  KEY `PhysicalMachine_FK5` (`idDataCenter`),
  KEY `PhysicalMachine_FK6` (`idEnterprise`),
  CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`) ON DELETE CASCADE,  
  CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE,
  CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`physicalmachine`
--
LOCK TABLES `physicalmachine` WRITE;
UNLOCK TABLES;

/*!40000 ALTER TABLE `physicalmachine` DISABLE KEYS */;
-- XXX LOCK TABLES `physicalmachine` WRITE;
-- XXX INSERT INTO `kinton`.`physicalmachine` VALUES  (1,1,1,'myMachine','My local machine',1024,2,42949672960,2,1,'2009-02-03 00:00:00',1,'2009-03-30 21:00:51',0,0,0);
-- XXX UNLOCK TABLES;
/*!40000 ALTER TABLE `physicalmachine` ENABLE KEYS */;


--
-- Definition of table `kinton`.`rack`
--

DROP TABLE IF EXISTS `kinton`.`rack`;
CREATE TABLE  `kinton`.`rack` (
  `idRack` int(15) unsigned NOT NULL auto_increment,
  `idDataCenter` int(10) unsigned NOT NULL,
  `name` varchar(20) NOT NULL,
  `shortDescription` varchar(30) default NULL,
  `largeDescription` varchar(100) default NULL,
  `vlan_id_min` int(15) unsigned default 2,
  `vlan_id_max` int(15) unsigned default 4094,
  `vlans_id_avoided` varchar(255) default '',
  `vlan_per_vdc_expected` int(15) unsigned default 8,
  `nrsq` int(15) unsigned default 10,
  `haEnabled` boolean default false COMMENT 'TRUE - This rack is enabled for the HA functionality',
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idRack`),
  KEY `Rack_FK1` (`idDataCenter`),
  CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `rack` DISABLE KEYS */;
LOCK TABLES `rack` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rack` ENABLE KEYS */;

--
-- Definition of table `kinton`.`ucs_rack`
--
DROP TABLE IF EXISTS `kinton`.`ucs_rack`;
CREATE TABLE  `kinton`.`ucs_rack` (
  `idRack` int(15) unsigned NOT NULL,
  `ip` varchar(20) NOT NULL,
  `port` int(5) NOT NULL,
  `user_rack` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `defaultTemplate` varchar(200),
  `maxMachinesOn` int(4) DEFAULT 0,
  KEY `id_rack_FK` (`idRack`),
  CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`rack`
--

--
-- Definition of table `kinton`.`datastore`
--
CREATE TABLE  `kinton`.`datastore` (
  `idDatastore` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `rootPath` varchar(42) NOT NULL,
  `directory` varchar(255) NOT NULL,
  `enabled` boolean NOT NULL default 0,
  `size` bigint(40) unsigned NOT NULL,
  `usedSize` bigint(40) unsigned NOT NULL,
  `datastoreUuid` VARCHAR(255) default NULL COMMENT 'Datastore UUID set by Abiquo to identify shared datastores.',
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idDatastore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `datastore` DISABLE KEYS */;
LOCK TABLES `datastore` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `datastore` ENABLE KEYS */;

--
-- Definition of table `kinton`.`datastore_assignment`
--

CREATE TABLE `kinton`.`datastore_assignment` (
  `idDatastore` INT(10) UNSIGNED NOT NULL,
  `idPhysicalMachine` int(20) UNSIGNED default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idDatastore`,`idPhysicalMachine`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `datastore_assignment` DISABLE KEYS */;
LOCK TABLES `datastore_assignment` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `datastore` ENABLE KEYS */;


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
  `description` varchar(255) default NULL,
  `elementName` varchar(255) NOT NULL,
  `generation` BIGINT default NULL,
  `hostResource` varchar(256) default NULL,
  `instanceID` varchar(50) NOT NULL,
  `limitResource` BIGINT default NULL,
  `mappingBehaviour` int(5) default NULL,
  `otherResourceType` varchar(50) default NULL,
  `parent` varchar(50) default NULL,
  `poolID` varchar(50) default NULL,
  `reservation` BIGINT default NULL,
  `resourceSubType` varchar(15) default NULL COMMENT 'For IPs: 0 = private, 1 = public, 2 = external',
  `resourceType` int(5) NOT NULL,
  `virtualQuantity` int(20) default NULL,
  `weight` int(5) default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`instanceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`rasd`
--

/*!40000 ALTER TABLE `rasd` DISABLE KEYS */;
LOCK TABLES `rasd` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rasd` ENABLE KEYS */;


--
-- Definition of table `kinton`.`rasd_management`
--

DROP TABLE IF EXISTS `kinton`.`rasd_management`;
CREATE TABLE  `kinton`.`rasd_management` (
  `idManagement` int(10) unsigned NOT NULL auto_increment,
  `idResourceType` varchar(5) NOT NULL,
  `idVirtualDataCenter` int(10) unsigned default NULL,
  `idVM` int(10) unsigned default NULL,
  `idResource` varchar(50),
  `idVirtualApp` int(10) unsigned default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  `temporal` int(10) unsigned DEFAULT NULL,
  `sequence` int(10) unsigned default NULL,
  PRIMARY KEY  (`idManagement`),
  KEY `idVirtualApp_FK` (`idVirtualApp`),
  KEY `idVM_FK` (`idVM`),
  KEY `idVirtualDataCenter_FK` (`idVirtualDataCenter`),
  KEY `idResource_FK` (`idResource`),
  CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE SET NULL,
  CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `virtualdatacenter` (`idVirtualDataCenter`) ON DELETE SET NULL,
  CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE SET NULL,
  CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `rasd` (`instanceID`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`rasd_management`
--

/*!40000 ALTER TABLE `rasd_management` DISABLE KEYS */;
LOCK TABLES `rasd_management` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rasd_management` ENABLE KEYS */;

--
-- Definition of table `kinton`.`ip_pool`
--
DROP TABLE IF EXISTS `kinton`.`ip_pool_management`;
CREATE TABLE  `kinton`.`ip_pool_management` (
  `idManagement` int(10) unsigned NOT NULL,
  `mac` varchar(20),
  `name` varchar(30),
  `ip` varchar(20) NOT NULL,
  `vlan_network_name` varchar(40),
  `vlan_network_id` int(11) unsigned,
  `quarantine` boolean NOT NULL default 0,
  `available` boolean NOT NULL default 1,
  `version_c` integer NOT NULL DEFAULT 1,
  KEY `id_management_FK` (`idManagement`),
  KEY `ippool_vlan_network_FK` (`vlan_network_id`),
  CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE,
  CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`)  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `kinton`.`repository`;
CREATE TABLE  `kinton`.`repository` (
  `idRepository` int(3) unsigned NOT NULL auto_increment,
  `idDataCenter` INT UNSIGNED NOT NULL ,
  `name` varchar(30),
  `URL` varchar(255) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idRepository`),
  CONSTRAINT `fk_idDataCenter` FOREIGN KEY ( `idDataCenter` ) REFERENCES `datacenter` ( `idDataCenter` ) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
--
-- Dumping data for table `kinton`.`repository`
--

--
-- Definition of table `kinton`.`role`
--


DROP TABLE IF EXISTS `kinton`.`role`;

CREATE TABLE  `kinton`.`role` (
  `idRole` int(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL DEFAULT 'auto_name',
  `idEnterprise` int(10) unsigned DEFAULT NULL,
  `blocked` tinyint(1) NOT NULL DEFAULT '0',
  `version_c` int(11) DEFAULT '0',
  PRIMARY KEY (`idRole`),
  KEY `fk_role_1` (`idEnterprise`),
  CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8; 

--
-- Dumping data for table `kinton`.`role`
--

/*!40000 ALTER TABLE `role` DISABLE KEYS */;
LOCK TABLES `role` WRITE;
	INSERT INTO `kinton`.`role` (idRole,name,blocked,version_c) VALUES (1,'CLOUD_ADMIN',1,0);
	INSERT INTO `kinton`.`role` (idRole,name,version_c) VALUES (2,'USER',0);
	INSERT INTO `kinton`.`role` (idRole,name,version_c) VALUES (3,'ENTERPRISE_ADMIN',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;

--
-- Definition of table `kinton`.`roles_privileges`
--

CREATE  TABLE `kinton`.`roles_privileges` (
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `idPrivilege` INT(10) UNSIGNED NOT NULL ,
  `version_c` INT(11) default 0,
  INDEX `fk_roles_privileges_role` (`idRole` ASC) ,
  INDEX `fk_roles_privileges_privileges` (`idPrivilege` ASC) ,
  CONSTRAINT `fk_roles_privileges_role`
    FOREIGN KEY (`idRole` )
    REFERENCES `kinton`.`role` (`idRole` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roles_privileges_privileges`
    FOREIGN KEY (`idPrivilege` )
    REFERENCES `kinton`.`privilege` (`idPrivilege` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`privilege`
--

CREATE TABLE `privilege` (
  `idPrivilege` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPrivilege`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
LOCK TABLES `privilege` WRITE;
INSERT INTO `privilege` VALUES
 (1,'ENTERPRISE_ENUMERATE',0),
 (2,'ENTERPRISE_ADMINISTER_ALL',0),
 (3,'ENTERPRISE_RESOURCE_SUMMARY_ENT',0),
 (4,'PHYS_DC_ENUMERATE',0),
 (5,'PHYS_DC_RETRIEVE_RESOURCE_USAGE',0),
 (6,'PHYS_DC_MANAGE',0),
 (7,'PHYS_DC_RETRIEVE_DETAILS',0),
 (8,'PHYS_DC_ALLOW_MODIFY_SERVERS',0),
 (9,'PHYS_DC_ALLOW_MODIFY_NETWORK',0),
 (10,'PHYS_DC_ALLOW_MODIFY_STORAGE',0),
 (11,'PHYS_DC_ALLOW_MODIFY_ALLOCATION',0),
 (12,'VDC_ENUMERATE',0),
 (13,'VDC_MANAGE',0),
 (14,'VDC_MANAGE_VAPP',0),
 (15,'VDC_MANAGE_NETWORK',0),
 (16,'VDC_MANAGE_STORAGE',0),
 (17,'VAPP_CUSTOMISE_SETTINGS',0),
 (18,'VAPP_DEPLOY_UNDEPLOY',0),
 (19,'VAPP_ASSIGN_NETWORK',0),
 (20,'VAPP_ASSIGN_VOLUME',0),
 (21,'VAPP_PERFORM_ACTIONS',0),
 (22,'VAPP_CREATE_STATEFUL',0),
 (23,'VAPP_CREATE_INSTANCE',0),
 (24,'APPLIB_VIEW',0),
 (25,'APPLIB_ALLOW_MODIFY',0),
 (26,'APPLIB_UPLOAD_IMAGE',0),
 (27,'APPLIB_MANAGE_REPOSITORY',0),
 (28,'APPLIB_DOWNLOAD_IMAGE',0),
 (29,'APPLIB_MANAGE_CATEGORIES',0),
 (30,'USERS_VIEW',0),
 (31,'USERS_MANAGE_ENTERPRISE',0),
 (32,'USERS_MANAGE_USERS',0),
 (33,'USERS_MANAGE_OTHER_ENTERPRISES',0),
 (34,'USERS_PROHIBIT_VDC_RESTRICTION',0),
 (35,'USERS_VIEW_PRIVILEGES',0),
 (36,'USERS_MANAGE_ROLES',0),
 (37,'USERS_MANAGE_ROLES_OTHER_ENTERPRISES',0),
 (38,'USERS_MANAGE_SYSTEM_ROLES',0),
 (39,'USERS_MANAGE_LDAP_GROUP',0),
 (40,'USERS_ENUMERATE_CONNECTED',0),
 (41,'SYSCONFIG_VIEW',0),
 (42,'SYSCONFIG_ALLOW_MODIFY',0),
 (43,'EVENTLOG_VIEW_ENTERPRISE',0),
 (44,'EVENTLOG_VIEW_ALL',0),
 (45,'APPLIB_VM_COST_CODE',0),
 (46,'USERS_MANAGE_ENTERPRISE_BRANDING',0),
 (47,'SYSCONFIG_SHOW_REPORTS',0),
 (48,'USERS_DEFINE_AS_MANAGER',0),
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `roles_privileges` DISABLE KEYS */;
LOCK TABLES `roles_privileges` WRITE;
INSERT INTO `roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),(1,23,0),(1,24,0),(1,25,0),
 (1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,47,0),(1,48,0),(1,49,0),(1,50,0),
 (3,3,0),(3,12,0),(3,13,0),(3,14,0),(3,15,0),(3,16,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,24,0),(3,25,0),(3,26,0),(3,27,0),(3,28,0),(3,29,0),(3,30,0),(3,32,0),(3,34,0),(3,43,0),(3,48,0),
(2,12,0),(2,14,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `roles_privileges` ENABLE KEYS */;

--
-- Definition of table `kinton`.`role_ldap`
--
DROP TABLE IF EXISTS `kinton`.`role_ldap`;

CREATE  TABLE `kinton`.`role_ldap` (
  `idRole_ldap` INT(3) NOT NULL AUTO_INCREMENT ,
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `role_ldap` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idRole_ldap`) ,
  KEY `fk_role_ldap_role` (`idRole`) ,
  CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole` ) REFERENCES `kinton`.`role` (`idRole` ) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`user`
--

DROP TABLE IF EXISTS `kinton`.`user`;
CREATE TABLE  `kinton`.`user` (
  `idUser` int(10) unsigned NOT NULL auto_increment,
  `idRole` int(3) unsigned NOT NULL,
  `idEnterprise` int(10) unsigned default NULL,
  `user` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `surname` varchar(50) default NULL,
  `description` varchar(100) default NULL,
  `email` varchar(200),
  `locale` varchar(10) NOT NULL,
  `password` varchar(32),
  `availableVirtualDatacenters` varchar(255),
  `active` int(1) unsigned NOT NULL default '0',
  `authType` varchar(20) NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idUser`),
  KEY `User_FK1` (`idRole`),
  KEY `FK1_user` (`idEnterprise`),
  CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`),
  CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `role` (`idRole`),
  UNIQUE KEY user_auth_idx (user, authType)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
LOCK TABLES `user` WRITE;
INSERT INTO `kinton`.`user` VALUES  (1,1,1,'admin','Cloud','Administrator','Main administrator','','en_US','c69a39bd64ffb77ea7ee3369dce742f3',null,1, 'ABIQUO', NOW(), 0),
 (2,2,1,'user','Standard','User','Standard user','','en_US','c69a39bd64ffb77ea7ee3369dce742f3',null,1, 'ABIQUO',NOW(), 0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Definition of table `kinton`.`session`
--

DROP TABLE IF EXISTS `kinton`.`session`;
CREATE TABLE  `kinton`.`session` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user` varchar(128) NOT NULL,
  `key` varchar(100) NOT NULL,
  `expireDate` timestamp NOT NULL,
  `idUser` int(10) unsigned default null,
  `authType` varchar(20) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  CONSTRAINT `fk_session_user` foreign key (`idUser`) references `user` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`virtualapp`
--

DROP TABLE IF EXISTS `kinton`.`virtualapp`;
CREATE TABLE  `kinton`.`virtualapp` (
  `idVirtualApp` int(10) unsigned NOT NULL auto_increment,
  `idVirtualDataCenter` int(10) unsigned NOT NULL,
  `idEnterprise` int(10) unsigned default NULL,
  `name` varchar(30) NOT NULL,
  `public` int(1) unsigned NOT NULL COMMENT '0-No 1-Yes',
  `high_disponibility` int(1) unsigned NOT NULL COMMENT '0-No 1-Yes',
  `error` int(1) unsigned NOT NULL,
  `nodeconnections` text,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idVirtualApp`),
  KEY `VirtualApp_FK4` (`idVirtualDataCenter`),
  KEY `VirtualApp_FK5` (`idEnterprise`),
  CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `virtualdatacenter` (`idVirtualDataCenter`) ON DELETE CASCADE,
  CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`virtualapp`
--

/*!40000 ALTER TABLE `virtualapp` DISABLE KEYS */;
LOCK TABLES `virtualapp` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualapp` ENABLE KEYS */;


--
-- Definition of table `kinton`.`virtualdatacenter`
--

DROP TABLE IF EXISTS `kinton`.`virtualdatacenter`;
CREATE TABLE  `kinton`.`virtualdatacenter` (
  `idVirtualDataCenter` int(10) unsigned NOT NULL auto_increment,
  `idEnterprise` int(10) unsigned NOT NULL,
  `name` varchar(40) default NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  `networktypeID` int(11) unsigned,
  `hypervisorType` varchar(255) NOT NULL,
  `ramSoft` bigint(20) NOT NULL default 0,
  `cpuSoft` bigint(20) NOT NULL default 0,
  `hdSoft` bigint(20)  NOT NULL default 0,
  `storageSoft` bigint(20)  NOT NULL default 0,
  `vlanSoft` bigint(20)  NOT NULL default 0,
  `publicIPSoft` bigint(20)  NOT NULL default 0,
  `ramHard` bigint(20) NOT NULL default 0,
  `cpuHard` bigint(20) NOT NULL default 0,
  `hdHard` bigint(20)  NOT NULL default 0,
  `storageHard` bigint(20)  NOT NULL default 0,
  `vlanHard` bigint(20)  NOT NULL default 0,
  `publicIPHard` bigint(20)  NOT NULL default 0,
  `default_vlan_network_id` int(11) unsigned default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idVirtualDataCenter`),
  KEY `virtualDataCenter_FK1` (`idEnterprise`),
  KEY `virtualDataCenter_FK6` (`idDataCenter`),
  CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`),
  CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `network` (`network_id`),
  CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE RESTRICT,
  CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`virtualdatacenter`
--

/*!40000 ALTER TABLE `virtualdatacenter` DISABLE KEYS */;
LOCK TABLES `virtualdatacenter` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualdatacenter` ENABLE KEYS */;


--
-- Definition of table `kinton`.`virtualimage`
--





DROP TABLE IF EXISTS `kinton`.`virtualimage`;
CREATE TABLE  `kinton`.`virtualimage` (
  `idImage` int(4) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `pathName` varchar(255) NOT NULL,
  `hd_required` bigint(20) default NULL,
  `ram_required` int(7) unsigned default NULL,
  `cpu_required` int(11) default NULL,
  `iconUrl` varchar(255) default NULL,
  `idCategory` int(3) unsigned NOT NULL,
  `idRepository` int(3) unsigned default NULL,
  `type` varchar(50) NOT NULL,
  `ethDriverType` varchar(16) default NULL,
  `idMaster` int(4) unsigned default NULL,
  `idEnterprise` int(10) unsigned default null,
  `shared` int(1) unsigned NOT NULL default 0 COMMENT '0-No 1-Yes',
  `ovfid` varchar(255),
  `stateful` int(1) unsigned NOT NULL,
  `diskFileSize` BIGINT(20) UNSIGNED NOT NULL,
  `chefEnabled` boolean NOT NULL default false,
  `cost_code` int(10) DEFAULT 0,
  `creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creation_user` varchar(128) NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idImage`),
  KEY `fk_virtualimage_category` (`idCategory`),
  KEY `virtualImage_FK3` (`idRepository`),
  CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `category` (`idCategory`),
  CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `repository` (`idRepository`) ON DELETE SET NULL,
  CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `virtualimage` (`idImage`) ON DELETE SET NULL,
  CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`virtualimage`
--

/*!40000 ALTER TABLE `virtualimage` DISABLE KEYS */;
LOCK TABLES `virtualimage` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualimage` ENABLE KEYS */;


--
-- Definition of table `kinton`.`virtualmachine`
--

DROP TABLE IF EXISTS `kinton`.`virtualmachine`;
CREATE TABLE  `kinton`.`virtualmachine` (
  `idVM` int(10) unsigned NOT NULL auto_increment,
  `idHypervisor` int(2) unsigned default NULL,
  `idImage` int(4) unsigned DEFAULT NULL,
  `UUID` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `ram` int(7) unsigned default NULL,
  `cpu` int(10) unsigned default NULL,
  `hd` bigint(20) unsigned default NULL,
  `vdrpPort` int(5) unsigned default NULL,
  `vdrpIP` varchar(39) default NULL,
  `state` varchar(50) NOT NULL,
  `subState` varchar(50) DEFAULT NULL,
  `high_disponibility` int(1) unsigned NOT NULL,
  `idConversion` INT(10) UNSIGNED,
  `idType` INT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - NOT MANAGED BY ABICLOUD  1 - MANAGED BY ABICLOUD',
  `idUser` INT(10) unsigned default NULL COMMENT 'User who creates the VM',
  `idEnterprise` int(10) unsigned default NULL COMMENT 'Enterprise of the user',
  `idDatastore` int(10) unsigned default NULL,
  `password` varchar(32) default NULL,
  `network_configuration_id` int(11) unsigned, 
  `temporal` int(10) unsigned default NULL,
  `ethDriverType` varchar(16) default NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idVM`),
  KEY `VirtualMachine_FK1` (`idHypervisor`),
  KEY `virtualMachine_datastore_FK` (`idDatastore`),
  KEY `virtualMachine_FK3` (`idImage`),
  KEY `virtualMachine_FK4` (`idUser`),
  KEY `virtualMachine_FK5` (`idEnterprise`),
  KEY `virtualMachine_FK6` (`network_configuration_id`),
  CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `hypervisor` (`id`) ON DELETE CASCADE,
  CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `datastore` (`idDatastore`),
  CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `virtualimage` (`idImage`),
  CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY `virtualmachine_conversion_FK` (`idConversion`) REFERENCES `virtualimage_conversions` (`id`),
  CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE SET NULL,
  CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE SET NULL,
  CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `network_configuration` (`network_configuration_id`) ON DELETE SET NULL  
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`virtualmachine`
--

/*!40000 ALTER TABLE `virtualmachine` DISABLE KEYS */;
LOCK TABLES `virtualmachine` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualmachine` ENABLE KEYS */;

--
-- Definition of table `kinton`.`virtualmachinetrackedstate`
-- This table is kept only for tracking VM states and updating statistics/accounting information
--
DROP TABLE IF EXISTS `kinton`.`virtualmachinetrackedstate`;
CREATE TABLE  `kinton`.`virtualmachinetrackedstate` (
  `idVM` int(10) unsigned NOT NULL,
  `previousState` varchar(50) NOT NULL,
  PRIMARY KEY  (`idVM`),
  KEY `VirtualMachineTrackedState_FK1` (`idVM`),
  CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
  )
 ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`remote_service`
--

DROP TABLE IF EXISTS `kinton`.`remote_service`;
CREATE TABLE  `kinton`.`remote_service` (
  `idRemoteService` int(10) unsigned NOT NULL auto_increment,
  `uri` varchar(255) NOT NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  `status` INT(1) unsigned NOT NULL DEFAULT 0,
  `remoteServiceType` varchar(255) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idRemoteService`),
  KEY `idDatecenter_FK` (`idDataCenter`),
  CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`chef_runlist`
--

DROP TABLE IF EXISTS `kinton`.`chef_runlist`;
CREATE TABLE  `kinton`.`chef_runlist` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idVM` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255),
  `priority` int(10) NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `chef_runlist_FK1` (`idVM`),
  CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 *  Definition of table `kinton`.`metering`
 */

DROP TABLE IF EXISTS `kinton`.`metering`;
CREATE TABLE  `kinton`.`metering` (
  `idMeter` BIGINT unsigned NOT NULL auto_increment,
  `idDatacenter` int(10) unsigned default null,
  `datacenter` varchar(20) default null,
  `idRack` int(15) unsigned default NULL,
  `rack` varchar(20) default NULL,
  `idPhysicalMachine` int(20) unsigned default NULL,
  `physicalmachine` varchar(256) default NULL,
  `idStorageSystem` int(10) unsigned default NULL,
  `storageSystem` varchar(256) default NULL,
  `idStoragePool` varchar(40) default NULL,
  `storagePool` varchar(256) default NULL,
  `idVolume` varchar(50) default NULL,
  `volume` varchar(256) default NULL,
  `idNetwork` int(11) unsigned default NULL,
  `network` varchar(256) default NULL,
  `idSubnet` int(11) unsigned default NULL,
  `subnet` varchar(256) default NULL,
  `idEnterprise` int(10) unsigned default NULL,
  `enterprise` varchar(40) default NULL,
  `idUser` int(10) unsigned default NULL,
  `user` varchar(128) default NULL,
  `idVirtualDataCenter` int(10) unsigned default NULL,
  `virtualDataCenter` varchar(40) default NULL,
  `idVirtualApp` int(10) unsigned default NULL,
  `virtualApp` varchar(30) default NULL,
  `idVirtualMachine` int(10) unsigned default NULL,
  `virtualmachine` varchar(256) default NULL,
  `severity` varchar(100) NOT NULL,
  `timestamp` timestamp NOT NULL,
  `performedby` varchar(255) NOT NULL,
  `actionperformed` varchar(100) NOT NULL,
  `component` varchar(255) default NULL,
  `stacktrace` text default NULL,
  PRIMARY KEY  (`idMeter`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `kinton`.`virtualimage_conversions`;
CREATE TABLE  `kinton`.`virtualimage_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idImage` int(10) unsigned NOT NULL,
  `sourceType` varchar(50),
  `targetType` varchar(50) NOT NULL,
  `sourcePath` varchar(255),
  `targetPath` varchar(255) NOT NULL,
  `state` varchar(50) NOT NULL,
  `timestamp` timestamp NOT NULL,
  `size` BIGINT default NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `idImage_FK` (`idImage`),
  CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `virtualimage` (`idImage`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `kinton`.`virtual_appliance_conversions`;
CREATE TABLE `kinton`.`virtual_appliance_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idConversion` int(10) unsigned NOT NULL,
  `idVirtualAppliance` int(10) unsigned NOT NULL,
  `idUser` int(10) unsigned,
  `forceLimits` boolean,
  `idNode` int(10) unsigned,
  PRIMARY KEY (`id`),
  KEY `idConversion_K` (`idConversion`),
  KEY `idVirtualAppliance_K` (`idVirtualAppliance`),
  KEY `idUser_K` (`idUser`),
  CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `virtualimage_conversions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE CASCADE,
  CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`),
  constraint `virtual_appliance_conversions_node_FK` foreign key (`idNode`) references `nodevirtualimage` (`idNode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS alerts;
CREATE TABLE IF NOT EXISTS alerts (
  id char(36) NOT NULL,
  `type` varchar(60) NOT NULL,
  `value` varchar(60) NOT NULL,
  description varchar(240) DEFAULT NULL,
  tstamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `alerts` WRITE;
insert into alerts (id, type, value, tstamp) values ("1", "REGISTER", "LATER", date_sub(now(), INTERVAL 4 DAY)), ("2", "HEARTBEAT", "YES", date_sub(now(), INTERVAL 4 DAY));
UNLOCK TABLES;

DROP TABLE IF EXISTS heartbeatlog;
CREATE TABLE IF NOT EXISTS heartbeatlog (
  id char(36) NOT NULL,
  abicloud_id varchar(60),
  client_ip varchar(16) NOT NULL,
  physical_servers int(11) NOT NULL,
  virtual_machines int(11) NOT NULL,
  volumes int(11) NOT NULL,
  virtual_datacenters int(11) NOT NULL,
  virtual_appliances int(11) NOT NULL,
  organizations int(11) NOT NULL,
  total_virtual_cores_allocated bigint(20) NOT NULL,
  total_virtual_cores_used bigint(20) NOT NULL,
  total_virtual_cores bigint(20) NOT NULL default 0,
  total_virtual_memory_allocated bigint(20) NOT NULL,
  total_virtual_memory_used bigint(20) NOT NULL,
  total_virtual_memory bigint(20) NOT NULL default 0,
  total_volume_space_allocated bigint(20) NOT NULL,
  total_volume_space_used bigint(20) NOT NULL,
  total_volume_space bigint(20) NOT NULL default 0,
  virtual_images bigint(20) NOT NULL,
  operating_system_name varchar(60) NOT NULL,
  operating_system_version varchar(60) NOT NULL,
  database_name varchar(60) NOT NULL,
  database_version varchar(60) NOT NULL,
  application_server_name varchar(60) NOT NULL,
  application_server_version varchar(60) NOT NULL,
  java_version varchar(60) NOT NULL,
  abicloud_version varchar(60) NOT NULL,
  abicloud_distribution varchar(60) NOT NULL,
  tstamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS register;
CREATE TABLE IF NOT EXISTS register (
  id char(36) NOT NULL,
  company_name varchar(60) NOT NULL,
  company_address varchar(240) NOT NULL,
  company_state varchar(60) NOT NULL,
  company_country_code varchar(2) NOT NULL,
  company_industry varchar(255),
  contact_title varchar(60) NOT NULL,
  contact_name varchar(60) NOT NULL,
  contact_email varchar(60) NOT NULL,
  contact_phone varchar(60) NOT NULL,
  company_size_revenue varchar(60) NOT NULL,
  company_size_employees varchar(60) NOT NULL,
  subscribe_development_news tinyint(1) NOT NULL DEFAULT '0',
  subscribe_commercial_news tinyint(1) NOT NULL DEFAULT '0',
  allow_commercial_contact tinyint(1) NOT NULL DEFAULT '0',
  creation_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  last_updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




--
-- System properties
--

DROP TABLE IF EXISTS `kinton`.`system_properties`;
CREATE TABLE `kinton`.`system_properties` (
  `systemPropertyId` int(10) unsigned NOT NULL auto_increment,
  `version_c` int(11) default 0,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `description` varchar(255) NULL,
  PRIMARY KEY (`systemPropertyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `system_properties` DISABLE KEYS */;
LOCK TABLES `system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.applibrary.ovfpackagesDownloadingProgressUpdateInterval","10","Time interval in seconds"),
 ("client.applibrary.virtualimageUploadProgressUpdateInterval","10","Time interval in seconds"),
 ("client.dashboard.abiquoURL","http://www.abiquo.org","URL of Abiquo web page"),
 ("client.dashboard.allowUsersAccess","1","Allow (1) or deny (0) access to the \'Users\' section"),
 ("client.dashboard.showStartUpAlert","1","Set to 1 to show an Alert with the text found in Startup_Alert.txt file"),
 ("client.infra.googleMapsDefaultLatitude","41.3825","Google Maps will be centered by default at this longitude value"),
 ("client.infra.googleMapsDefaultLongitude","2.176944","Google Maps will be centered by default at this latitude value"),
 ("client.infra.googleMapsDefaultZoom","4","Google Maps will be centered by default with this zoom level value"),
 ("client.infra.googleMapskey","0","The map\'s Google key used in infrastructure section"),
 ("client.infra.googleMapsLadTimeOut","10","Time, in seconds, that applications waits Google Maps to load. After that, application considers that Google Maps service is temporarily unavailable, and is not used"),
 ("client.infra.InfrastructureUpdateInterval","30","Time interval in seconds"),
 ("client.infra.ucsManagerLink","/ucsm/ucsm.jnlp","URL to display UCS Manager Interface"),
 ("client.metering.meteringUpdateInterval","10","Time interval in seconds"),
 ("client.network.numberIpAdressesPerPage","25","Number entries that will appear when listing IP addresses in different parts of the application"),
 ("client.theme.defaultEnterpriseLogoPath","themes/abicloudDefault/logo.png","This is the path to the Enterprise logo used in the app"),
 ("client.user.numberEnterprisesPerPage","25","Number of enterprises per page that will appear in User Management"),
 ("client.user.numberUsersPerPage","25","Number of users per page that will appear in User Management"),
 ("client.virtual.allowVMRemoteAccess","1","Allow (1) or deny (0) virtual machine remote access"),
 ("client.virtual.virtualApplianceDeployingUpdateInterval","5","Time interval in seconds"),
 ("client.virtual.virtualAppliancesUpdateInterval","30","Time interval in seconds"),
 ("client.virtual.moreInfoAboutUploadLimitations","http://wiki.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem","URL of Abiquo virtual image upload limitations web page"),
 ("client.infra.vlanIdMin","2","Minimum value for vlan ID"),
 ("client.infra.vlanIdMax","4094","Maximum value for vlan ID"),
 ("client.dashboard.dashboardUpdateInterval","30","Time interval in seconds"),
 ("client.infra.defaultHypervisorPassword","temporal","Default Hypervisor password used when creating Physical Machines"),
 ("client.infra.defaultHypervisorPort","8889","Default Hypervisor port used when creating Physical Machines"),
 ("client.infra.defaultHypervisorUser","root","Default Hypervisor user used when creating Physical Machines"),
 ("client.storage.volumeMaxSizeValues","1,2,4,8,16,32,64,128,256","Comma separated values, with the allowed sizes when creating or editing a VolumeManagement"),
 ("client.virtual.virtualImagesRefreshConversionsInterval","5","Time interval in seconds to refresh missing virtual image conversions"),
 ("client.main.enterpriseLogoURL","http://www.abiquo.com","URL displayed when the header enterprise logo is clicked"),
 ("client.main.billingUrl","","URL displayed when the report header logo is clicked, if empty the report button will not be displayed"),
 ("client.main.disableChangePassword","1","Allow (1) or deny (0) user to change their password"),
 ("client.logout.url","","Redirect to this URL after logout (empty -> login screen)"),
 ("client.main.allowLogout","1","Allow (1) or deny (0) user to logout"),
 ("client.wiki.showHelp","1","Show (1) or hide (0) the help icon within the plateform"), 
 ("client.wiki.showDefaultHelp","0","Use (1) or not (0) the default help URL within the plateform"), 
 ("client.wiki.defaultURL","http://community.abiquo.com/display/ABI20/Abiquo+Documentation+Home","The default URL opened when not specific help URL is specified"),
 ("client.wiki.infra.createDatacenter","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter","datacenter creation wiki"), 
 ("client.wiki.infra.editDatacenter","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter","datacenter edition wiki"), 
 ("client.wiki.infra.editRemoteService","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-RemoteServices","remote service edition wiki"), 
 ("client.wiki.infra.createPhysicalMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingPhysicalMachinesonStandardRacks","physical machine creation wiki"),
 ("client.wiki.infra.mailNotification","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-SendingEmailNotifications","mail notification wiki"),
 ("client.wiki.infra.addDatastore","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DatastoreManagement","Datastore manager wiki"),
 ("client.wiki.infra.createRack","http://community.abiquo.com/display/ABI20/Manage+Racks#ManageRacks-CreatingRacks","rack creation wiki"),
 ("client.wiki.infra.createMultiplePhysicalMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingMultiplePhysicalMachines","multiple physical machine creation wiki"),
 ("client.wiki.network.publicVlan","http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-CreateVLANsforPublicNetworks","public vlan creation wiki"),
 ("client.wiki.network.staticRoutes","http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-ConfiguringStaticRoutesUsingDHCP","static routes wiki"),
 ("client.wiki.storage.storageDevice","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-ManagedStorage","storage device creation wiki"),
 ("client.wiki.storage.storagePool","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-StoragePools","storage pool creation wiki"), 
 ("client.wiki.storage.tier","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-TierManagement","tier edition wiki"),
 ("client.wiki.allocation.global","http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement","global rules wiki"),
 ("client.wiki.allocation.datacenter","http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement","datacenter rules wiki"),
 ("client.wiki.vdc.createVdc","http://community.abiquo.com/display/ABI20/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter","virtual datacenter creation wiki"),
 ("client.wiki.vdc.createVapp","http://community.abiquo.com/display/ABI20/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance","virtual app creation wiki"),
 ("client.wiki.vdc.createPrivateNetwork","http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-CreateaPrivateVLAN","VDC private network creation wiki"),
 ("client.wiki.vdc.createPublicNetwork","http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-PublicIPReservation","VDC public network creation wiki"),
 ("client.wiki.vdc.createVolume","http://community.abiquo.com/display/ABI20/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage","VDC virtual volume creation wiki"),
 ("client.wiki.vm.editVirtualMachine","http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines","Virtual Machine edition wiki"),
 ("client.wiki.vm.bundleVirtualMachine","http://community.abiquo.com/display/ABI20/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-CreateanInstance","Bundles VM wiki"),
 ("client.wiki.vm.createNetworkInterface","http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface","Network Interface creation wiki"),
 ("client.wiki.vm.createInstance","http://community.abiquo.com/display/ABI20/Create+Virtual+Machine+instances","Virtual Machine instance creation wiki"),
 ("client.wiki.vm.createStateful","http://community.abiquo.com/display/ABI20/Create+Persistent+Virtual+Machines","Virtual Machine stateful creation wiki"),
 ("client.wiki.vm.captureVirtualMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-WorkingwithImportedVirtualMachines","Capture Virtual Machine wiki"),
 ("client.wiki.vm.deployInfo","","Show more info when deploying"),
 ("client.wiki.apps.uploadVM","http://community.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem","Virtual Image upload wiki"),
 ("client.wiki.user.createEnterprise","http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise","Enterprise creation wiki"),
 ("client.wiki.user.dataCenterLimits","http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-EdittheEnterprise%27sDatacenters","Datacenter Limits wiki"),
 ("client.wiki.user.createUser","http://community.abiquo.com/display/ABI20/Manage+Users#ManageUsers-CreatingorEditingaUser","User creation wiki"),
 ("client.wiki.user.createRole","http://community.abiquo.com/display/ABI20/Manage+Roles+and+Privileges","Role creation wiki"),
 ("client.wiki.pricing.createCurrency","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CurrenciesTab","Currency creation wiki"),
 ("client.wiki.pricing.createTemplate","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-PricingModelsTab","create pricing template wiki"),
 ("client.wiki.pricing.createCostCode","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CostCodesTab","create pricing cost code wiki"),
 ("client.wiki.config.general","http://community.abiquo.com/display/ABI20/Configuration+view","Configuration wiki"),
 ("client.wiki.config.heartbeat","http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-Heartbeating","Heartbeat configuration wiki"),
 ("client.wiki.config.licence","http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-LicenseManagement","Licence configuration wiki"),
 ("client.wiki.config.registration","http://community.abiquo.com/display/ABI20/Configuration+view#Configurationview-ProductRegistration","Registration wiki"),
 ("client.wiki.infra.discoverBlades","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DiscoveringBladesonManagedRacks","discover UCS blades wiki"),
 ("client.network.defaultName","default_private_network","default private vlan name"),
 ("client.network.defaultNetmask","2","index of available netmask"),
 ("client.network.defaultAddress","192.168.0.0","default private vlan address"),
 ("client.network.defaultGateway","192.168.0.1","default private vlan gateway"),
 ("client.network.defaultPrimaryDNS","","default primary DNS"),
 ("client.network.defaultSecondaryDNS","","default secondary DNS"),
 ("client.network.defaultSufixDNS","","default sufix DNS");
UNLOCK TABLES;

/*!40000 ALTER TABLE `system_properties` ENABLE KEYS */;


-- -----------------------------------------------------
-- Table `kinton`.`apps_library`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`apps_library` (
  `id_apps_library` INT UNSIGNED NOT NULL auto_increment,
  `idEnterprise` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`id_apps_library`),
  CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY ( `idEnterprise` ) REFERENCES `enterprise` ( `idEnterprise` ) ON DELETE CASCADE
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `apps_library` WRITE;
insert into `kinton`.`apps_library` (id_apps_library, idEnterprise) values (1, 1);
UNLOCK TABLES;


-- -----------------------------------------------------
-- Table `kinton`.`ovf_package_list`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package_list` (
  `id_ovf_package_list` INT NOT NULL auto_increment,
  `name` VARCHAR(45) NOT NULL,
  `url` VARCHAR(255) NULL,
  `id_apps_library` INT UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`id_ovf_package_list`) ,
  CONSTRAINT `fk_ovf_package_list_repository`
    FOREIGN KEY (`id_apps_library`)
    REFERENCES `kinton`.`apps_library` (`id_apps_library`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
-- -----------------------------------------------------
-- Table `kinton`.`ovf_package`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package` (
  `id_ovf_package` INT NOT NULL auto_increment,
  `id_apps_library` INT UNSIGNED NOT NULL ,
  `url` VARCHAR(255) NULL NOT NULL,
  `name` VARCHAR(255) NULL ,
  `description` VARCHAR(255) NULL ,
  `iconUrl` varchar(255) default NULL,
  `productName` VARCHAR(255) NULL ,
  `productUrl` VARCHAR(45) NULL ,
  `productVersion` VARCHAR(45) NULL ,
  `productVendor` VARCHAR(45) NULL ,
   `idCategory` int(3) unsigned NULL,  -- NOT NULL default 1,
   `diskSizeMb` bigint(20) NULL,
  `version_c` int(11) default 0,
  `type` varchar(50) not null,

  PRIMARY KEY (`id_ovf_package`),
  CONSTRAINT `fk_ovf_package_repository`
    FOREIGN KEY (`id_apps_library`)
    REFERENCES `kinton`.`apps_library` (`id_apps_library`)
    ON DELETE CASCADE,
    -- ON UPDATE NO ACTION
  CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `category` (`idCategory`) ON DELETE SET NULL
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
-- -----------------------------------------------------
-- Table `kinton`.`ovf_package_list_has_ovf_package`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package_list_has_ovf_package` (
  `id_ovf_package_list` INT NOT NULL ,
  `id_ovf_package` INT NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`) ,
  INDEX `fk_ovf_package_list_has_ovf_package_ovf_package_list1` (`id_ovf_package_list` ASC) ,
  INDEX `fk_ovf_package_list_has_ovf_package_ovf_package1` (`id_ovf_package` ASC) ,
  CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1`
    FOREIGN KEY (`id_ovf_package_list` )
    REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1`
    FOREIGN KEY (`id_ovf_package` )
    REFERENCES `kinton`.`ovf_package` (`id_ovf_package` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_enterprise_exclusion_rule`
-- --------------------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_enterprise_exclusion_rule`;
CREATE TABLE  `kinton`.`workload_enterprise_exclusion_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idEnterprise1` int(10) unsigned NOT NULL,
  `idEnterprise2` int(10) unsigned NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `FK_eerule_enterprise_1` (`idEnterprise1`),
  KEY `FK_eerule_enterprise_2` (`idEnterprise2`),
  CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE,
  CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ------------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_machine_load_rule`
-- ------------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_machine_load_rule`;
CREATE TABLE  `kinton`.`workload_machine_load_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ramLoadPercentage` int(10) unsigned NOT NULL,
  `cpuLoadPercentage` int(10) unsigned NOT NULL,
  `idDatacenter` int(10) unsigned DEFAULT NULL,
  `idRack` int(15) unsigned DEFAULT NULL,
  `idMachine` int(20) unsigned DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `FK_mlrule_datacenter` (`idDatacenter`),
  KEY `FK_mlrule_rack` (`idRack`),
  KEY `FK_mlrule_machine` (`idMachine`),
  CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `datacenter` (`idDataCenter`),
  CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `physicalmachine` (`idPhysicalMachine`),
  CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_fit_policy_rule`
-- ----------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_fit_policy_rule`;
CREATE TABLE  `kinton`.`workload_fit_policy_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fitPolicy` varchar(20) NOT NULL,
  `idDatacenter` int(10) unsigned DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `FK_fprule_datacenter` (`idDatacenter`),
  CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `workload_fit_policy_rule` DISABLE KEYS */;
LOCK TABLES `workload_fit_policy_rule` WRITE;
INSERT INTO `kinton`.`workload_fit_policy_rule` (id,fitPolicy) VALUES (0, 'PROGRESSIVE');
UNLOCK TABLES;
/*!40000 ALTER TABLE `workload_fit_policy_rule` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


-- -----------------------------------------------------
-- Table `kinton`.`enterprise_limits_by_datacenter`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`enterprise_limits_by_datacenter`;
CREATE TABLE  `kinton`.`enterprise_limits_by_datacenter` (
  `idDatacenterLimit` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idEnterprise` int(10) unsigned,
  `idDataCenter` int(10) unsigned,
  `ramSoft` bigint(20) NOT NULL,
  `cpuSoft` bigint(20) NOT NULL,
  `hdSoft` bigint(20)  NOT NULL,
  `storageSoft` bigint(20)  NOT NULL,
  `repositorySoft` bigint(20)  NOT NULL,
  `vlanSoft` bigint(20)  NOT NULL,
  `publicIPSoft` bigint(20)  NOT NULL,
  `ramHard` bigint(20) NOT NULL,
  `cpuHard` bigint(20) NOT NULL,
  `hdHard` bigint(20)  NOT NULL,
  `storageHard` bigint(20)  NOT NULL,
  `repositoryHard` bigint(20)  NOT NULL,
  `vlanHard` bigint(20)  NOT NULL,
  `publicIPHard` bigint(20)  NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  `default_vlan_network_id` int(11) unsigned default NULL,
  PRIMARY KEY (`idDatacenterLimit`),
  CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version  5.0.51a-3ubuntu5.4


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

USE kinton;

-- 
-- Definition of table `kinton`.`tier`.
--
DROP TABLE IF EXISTS `kinton`.`tier`;
CREATE TABLE `kinton`.`tier` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(40) NOT NULL,
    `description` varchar(255) NOT NULL,
    `isEnabled` tinyint(1) unsigned NOT NULL default '1',
    `idDataCenter` int(10) unsigned NOT NULL,
    `version_c` integer NOT NULL DEFAULT 1,
     PRIMARY KEY  (`id`),
     CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

/*!40000 ALTER TABLE `tier` ENABLE KEYS */;
--
-- Definition of table `kinton`.`cabin`
--

CREATE TABLE `kinton`.`storage_device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  `management_ip` varchar(256) NOT NULL,
  `management_port` int(5) unsigned NOT NULL DEFAULT '0',
  `iscsi_ip` varchar(256) NOT NULL,
  `iscsi_port` int(5) unsigned NOT NULL DEFAULT '0',
  `storage_technology` varchar(256) DEFAULT NULL,
  `username` varchar(256) DEFAULT NULL,
  `password` varchar(256) DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`id`),
  CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `storage_device` DISABLE KEYS */;
LOCK TABLES `kinton`.`storage_device` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `storage_device` ENABLE KEYS */;

--
-- Definition of table `kinton`.`storage_pool`
--

DROP TABLE IF EXISTS `kinton`.`storage_pool`;
CREATE TABLE  `kinton`.`storage_pool` (
  `idStorage` varchar(40) NOT NULL,
  `idStorageDevice` int(10) unsigned NOT NULL,
  `idTier` int(10) unsigned NOT NULL,
  `isEnabled` tinyint(1) unsigned NOT NULL default '1',
  `version_c` integer NOT NULL DEFAULT 1,
  `totalSizeInMb` bigint(20) unsigned NOT NULL default 0,
  `usedSizeInMb` bigint(20) unsigned NOT NULL default 0,
  `availableSizeInMb` bigint(20) unsigned NOT NULL default 0,
  `name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`idStorage`),
  CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON DELETE CASCADE,
  CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`storage_pool`
--

/*!40000 ALTER TABLE `storage_pool` DISABLE KEYS */;
LOCK TABLES `storage_pool` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `storage_pool` ENABLE KEYS */;

--
-- Definition of table `kinton`.`volume_management`
--
DROP TABLE IF EXISTS `kinton`.`volume_management`;
CREATE TABLE  `kinton`.`volume_management` (
  `idManagement` int(10) unsigned NOT NULL,
  `usedSize` bigint(20) unsigned NOT NULL default 0,
  `idSCSI` varchar(256) NOT NULL,
  `state` int(11) NOT NULL,
  `idStorage` varchar(40) NOT NULL,
  `idImage` int(4) unsigned default NULL,
  `version_c` int(11) default 0,
  KEY `idStorage_FK` (`idStorage`),
  KEY `idManagement_FK` (`idManagement`),
  KEY `volumemanagement_FK3` (`idImage`),
  CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `storage_pool` (`idStorage`) ON DELETE CASCADE,
  CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE,
  CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `virtualimage` (`idImage`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `volume_management` DISABLE KEYS */;
LOCK TABLES `volume_management` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `volume_management` ENABLE KEYS */;
--
-- Definition of table `kinton`.`disk_management`
--
DROP TABLE IF EXISTS `kinton`.`disk_management`;
CREATE TABLE  `kinton`.`disk_management` (
  `idManagement` int(10) unsigned NOT NULL,
  `idDatastore` int(10) unsigned default NULL,
  KEY `disk_idManagement_FK` (`idManagement`),
  KEY `disk_management_datastore_FK` (`idDatastore`),
  CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE,
  CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `datastore` (`idDatastore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`volume_management`
--


--
-- Definition of table `kinton`.`initiator_mapping`
--

DROP TABLE IF EXISTS `kinton`.`initiator_mapping`;
CREATE TABLE  `kinton`.`initiator_mapping` (
  `idInitiatorMapping` int(10) unsigned NOT NULL auto_increment,
  `idManagement`int(10) unsigned NOT NULL,
  `initiatorIqn` varchar(256) NOT NULL,
  `targetIqn` varchar(256) NOT NULL,
  `targetLun` int(10) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idInitiatorMapping`),
  KEY `volume_managementFK_1` (`idManagement`),
  CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`initiator_mapping`
--

/*!40000 ALTER TABLE `initiator_mapping` DISABLE KEYS */;
LOCK TABLES `initiator_mapping` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `initiator_mapping` ENABLE KEYS */;


--
-- Definition of table `kinton`.`diskstateful_conversions`
--
DROP TABLE IF EXISTS `kinton`.`diskstateful_conversions`;
CREATE TABLE  `kinton`.`diskstateful_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `imagePath` varchar(256) NOT NULL,
  `idManagement` int(10) unsigned NOT NULL,
  `state` varchar(50) NOT NULL,
  `convertionTimestamp` timestamp NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `idManagement_FK2` (`idManagement`),
  CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`vappstateful_conversions`
--
DROP TABLE IF EXISTS `kinton`.`vappstateful_conversions`;
CREATE TABLE  `kinton`.`vappstateful_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idVirtualApp` int(10) unsigned NOT NULL,
  `idUser` int(1) unsigned NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `idVirtualApp_FK3` (`idVirtualApp`),
  KEY `idUser_FK3` (`idUser`),
  CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE CASCADE,
  CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`node_virtual_image_stateful_conversions`
--
DROP TABLE IF EXISTS `kinton`.`node_virtual_image_stateful_conversions`;
CREATE TABLE  `kinton`.`node_virtual_image_stateful_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `newName` varchar(255) NOT NULL,
  `idVirtualApplianceStatefulConversion` int(10) unsigned NOT NULL,
  `idNodeVirtualImage` int(10) unsigned NOT NULL,
  `idVirtualImageConversion` int(1) unsigned,
  `idDiskStatefulConversion` int(1) unsigned,
  `state` varchar(50) NOT NULL,
  `subState` varchar(50) DEFAULT NULL,
  `version_c` int(11) default 0,
  `idTier` int(10) unsigned NOT NULL,
  `idManagement` int(10) unsigned,
  PRIMARY KEY  (`id`),
  KEY `idVirtualApplianceStatefulConversion_FK4` (`idVirtualApplianceStatefulConversion`),
  KEY `idNodeVirtualImage_FK4` (`idNodeVirtualImage`),
  KEY `idVirtualImageConversion_FK4` (`idVirtualImageConversion`),
  KEY `idDiskStatefulConversion_FK4` (`idDiskStatefulConversion`),
  KEY `idTier_FK4` (`idTier`),
  KEY `idManagement_FK4` (`idManagement`),
  CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `vappstateful_conversions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `nodevirtualimage` (`idNode`) ON DELETE CASCADE,
  CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `virtualimage_conversions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `diskstateful_conversions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `tier` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`license`
--
DROP TABLE IF EXISTS `kinton`.`license`;
CREATE TABLE  `kinton`.`license` (
  `idLicense` int(11) NOT NULL auto_increment,
  `data` varchar(1000) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idLicense`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`dhcpOption`
--
CREATE TABLE `kinton`.`dhcpOption` (
  `idDhcpOption` int(10) unsigned NOT NULL AUTO_INCREMENT ,
  `dhcp_opt` int(20) NOT NULL ,
   `gateway` varchar(40),
  `network_address` varchar(40) NOT NULL,
  `mask` int(4) NOT NULL,
  `netmask` varchar(20) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idDhcpOption`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`vlans_dhcpOption`
--

CREATE  TABLE `kinton`.`vlans_dhcpOption` (
  `idVlan` INT(10) UNSIGNED NOT NULL ,
  `idDhcpOption` INT(10) UNSIGNED NOT NULL ,
  `version_c` INT(11) default 0,
  INDEX `fk_vlans_dhcp_vlan` (`idVlan` ASC) ,
  INDEX `fk_vlans_dhcp_dhcp` (`idDhcpOption` ASC) ,
  CONSTRAINT `fk_vlans_dhcp_vlan`
    FOREIGN KEY (`idVlan` )
    REFERENCES `kinton`.`vlan_network` (`vlan_network_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vlans_dhcp_dhcp`
    FOREIGN KEY (`idDhcpOption` )
    REFERENCES `kinton`.`dhcpOption` (`idDhcpOption` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- THE WONDERFUL WORLD OF TRIGGERS
--




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;



--
-- STATISTICS MODULE TABLES BEGIN
--

--
-- Definition of table `kinton`.`cloud_usage_stats`
--
DROP  TABLE IF EXISTS `kinton`.`cloud_usage_stats`;
CREATE  TABLE `kinton`.`cloud_usage_stats` (
  `idDataCenter` INT NOT NULL AUTO_INCREMENT,
  `serversTotal`  BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Physical machines managed.',
  `serversRunning` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Physical Machines running currently.',  
  `storageTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'External storage managed by Abiquo (in Megabytes)',
  `storageReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'External storage reserved by Enterprises in Datacenters (in Megabytes)',
  `storageUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes)',
  `publicIPsTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs defined (managed).',
  `publicIPsReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved)',
  `publicIPsUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines',
  `vlanReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0  COMMENT 'Total maximum number of VLANs reserved by all enterprises. (enterprise.vlanHard)',
  `vlanUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by any datacenter managed.',
  `vMachinesTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Machines managed by Abiquo.',
  `vMachinesRunning` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Machines currently in a running state.',
  `vCpuTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines managed (can be used for VMs).',
  `vCpuReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved by all enterprises.',
  `vCpuUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.',
  `vMemoryTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0  COMMENT 'Total amount of RAM managed (can be used for VMs) in Megabytes.',
  `vMemoryReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved by all enterprises in Megabytes.',
  `vMemoryUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.',
  `vStorageReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Maximum storage size reserved by all enterprises for Virtual Machines  in Megabytes.',
  `vStorageUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.',
  `vStorageTotal` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total storage size managed to be used by Virtual Machines  in Megabytes.',
  `numUsersCreated` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of users in system.',
  `numVDCCreated` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Data Centers in system.',
  `numEnterprisesCreated` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of enterprises in system.',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idDataCenter`) )
ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- Creating Fake DataCenter (for retrieving ALL Cloud Usage Stats)
LOCK TABLES `cloud_usage_stats` WRITE;
INSERT INTO cloud_usage_stats (idDataCenter,serversTotal,serversRunning,storageTotal,storageReserved,storageUsed,publicIPsTotal,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed,vMachinesTotal,vMachinesRunning,vCpuTotal,vCpuReserved,vCpuUsed,vMemoryTotal,vMemoryReserved,vMemoryUsed,vStorageReserved,vStorageUsed,vStorageTotal,numUsersCreated,numVDCCreated,numEnterprisesCreated) VALUES (-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
UNLOCK TABLES;

DROP  TABLE IF EXISTS `kinton`.`enterprise_resources_stats`;
CREATE  TABLE IF NOT EXISTS `kinton`.`enterprise_resources_stats` (
  `idEnterprise` INT NOT NULL AUTO_INCREMENT ,
  `vCpuReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved by all enterprises.',
  `vCpuUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.',
  `memoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved by all enterprises in Megabytes.',
  `memoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.',
  `localStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum storage size reserved by all enterprises for Virtual Machines  in Megabytes.',
  `localStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.',
  `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum external storage size reserved by all enterprises in Bytes.',
  `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes)',
  `repositoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum repository size reserved by all enterprises in Megabytes.',
  `repositoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total repository size currently used by all enterprises in Megabytes.',
  `publicIPsReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved)',
  `publicIPsUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines',
  `vlanReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved by all enterprises. (enterprise.vlanHard)',
  `vlanUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by any datacenter managed.',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idEnterprise`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
--
DROP  TABLE IF EXISTS `kinton`.`dc_enterprise_stats`;
CREATE  TABLE IF NOT EXISTS `kinton`.`dc_enterprise_stats` (
  `idDCEnterpriseStats` INT NOT NULL AUTO_INCREMENT,
  `idDataCenter` INT NOT NULL ,
  `idEnterprise` INT NOT NULL ,
  `vCpuReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved for this enterprise in this datacenter.',
  `vCpuUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.',
  `memoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved for this enterprise in this datacenter in Megabytes.',
  `memoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.',
  `localStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum storage size reserved for this enterprise in this datacenter for Virtual Machines  in Megabytes.',
  `localStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.',
  `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT ' Maximum external storage size reserved for this enterprise in this datacenter in Megabytes.',
  `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes).',
  `repositoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum repository size reserved for this enterprise in this datacenter in Megabytes.',
  `repositoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total repository size currently used for this enterprise in this datacenter in Megabytes.',
  `publicIPsReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved).',
  `publicIPsUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines.',
  `vlanReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved for this enterprise in this datacenter. (enterprise.vlanHard)',
  `vlanUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by this datacenter.',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idDCEnterpriseStats`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
--
--
DROP  TABLE IF EXISTS `kinton`.`vdc_enterprise_stats`;
CREATE  TABLE IF NOT EXISTS `kinton`.`vdc_enterprise_stats` (
  `idVirtualDataCenter` INT NOT NULL AUTO_INCREMENT,
  `idEnterprise` INT NOT NULL ,
  `vdcName` VARCHAR(45) NULL COMMENT 'Name for the virtualdatacenter',
  `vmCreated` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of virtual machines created in this virtual datacenter',
  `vmActive` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of virtual machines currently running in this virtual datacenter',
  `volCreated` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of volumes created in this virtual datacenter',
  `volAssociated` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of volumes associated to a virtual machine in this virtual datacenter', 
  `volAttached` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of volumes currently attached to a virtual machine in this virtual datacenter', 
  `vCpuReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved for this enterprise in this virtual datacenter.',
  `vCpuUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.',
  `memoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved for this enterprise in this virtual datacenter in Megabytes.',
  `memoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.',
  `localStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum storage size reserved for this enterprise in this virtual datacenter for Virtual Machines  in Megabytes.',
  `localStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.',
  `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT ' Maximum external storage size reserved for this enterprise in this virtual datacenter in Megabytes.',
  `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes).',
  `publicIPsReserved` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to this virtualDatacenter (Reserved).',
  `publicIPsUsed` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines.',
  `vlanReserved` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved for this enterprise in this virtual datacenter. (enterprise.vlanHard)',
  `vlanUsed` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by this virtual datacenter.',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idVirtualDataCenter`,`idEnterprise`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
--
--
DROP  TABLE IF EXISTS `kinton`.`vapp_enterprise_stats`;
CREATE  TABLE IF NOT EXISTS `kinton`.`vapp_enterprise_stats` (
  `idVirtualApp` INT NOT NULL AUTO_INCREMENT,
  `idEnterprise` INT NOT NULL ,
  `idVirtualDataCenter` INT NOT NULL,
  `vappName` VARCHAR(45) NULL COMMENT 'Name for this virtual appliance',
  `vdcName` VARCHAR(45) NULL COMMENT 'Name for the virtualdatacenter',
  `vmCreated` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of virtual machines created in this virtual appliance',
  `vmActive` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of virtual machines currently running in this virtual appliance',
  `volAssociated` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of volumes associated to this virtual appliance',
  `volAttached` MEDIUMINT UNSIGNED NULL DEFAULT 0 COMMENT 'Number of volumes currently attached to a virtual machine in this virtual appliance',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idVirtualApp`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
--
--
-- STATISTICS MODULE TABLES END
--
--
DROP  TABLE IF EXISTS `kinton`.`enterprise_theme`;
CREATE  TABLE IF NOT EXISTS `kinton`.`enterprise_theme` (
  `idEnterprise` int(10) unsigned NOT NULL,
  `company_logo_path` text NULL,
  `theme` text NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idEnterprise`),
  KEY (`idEnterprise`),
  CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE
)ENGINE = InnoDB DEFAULT CHARSET=utf8;

--
-- ONETIMETOKEN TABLE
--
DROP  TABLE IF EXISTS `kinton`.`one_time_token`;
CREATE  TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` int(3) unsigned NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idOneTimeTokenSession`)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
 

CREATE TABLE `tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL,
  `component` varchar(20) NOT NULL,
  `action` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ******************************************************************************************
-- PRICING RELATED TABLES
-- ******************************************************************************************

-- DROP THE TABLES RELATED TO PRICING --
DROP TABLE IF EXISTS `kinton`.`pricing_template`;
DROP TABLE IF EXISTS `kinton`.`costCode`;
DROP TABLE IF EXISTS `kinton`.`pricingCostCode`;
DROP TABLE IF EXISTS `kinton`.`pricingTier`;
DROP TABLE IF EXISTS `kinton`.`currency`;
DROP TABLE IF EXISTS `kinton`.`costCodeCurrency`;

--
-- Definition of table `kinton`.`currency`
--

CREATE TABLE `kinton`.`currency` (
  `idCurrency` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `symbol` varchar(10) NOT NULL ,
  `name` varchar(20) NOT NULL,
  `digits` int(1)  NOT NULL default 2,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCurrency`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
    

--
-- Dumping data for table `kinton`.`currency`
--

/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
LOCK TABLES `currency` WRITE;
INSERT INTO `kinton`.`currency` values (1, "USD", "Dollar - $", 2,  0);
INSERT INTO `kinton`.`currency` values (2, "EUR", CONCAT("Euro - " ,0xE282AC), 2,0);
INSERT INTO `kinton`.`currency` values (3, "JPY", CONCAT("Yen - " , 0xc2a5), 0,  0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;  
  
--
-- Definition of table `kinton`.`costCode`
--  

CREATE TABLE `kinton`.`costCode` (
  `idCostCode` int(10) NOT NULL AUTO_INCREMENT ,
   `name` varchar(20) NOT NULL ,
  `description` varchar(100) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCostCode`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`pricing`
--
  

CREATE TABLE `kinton`.`pricingTemplate` (
  `idPricingTemplate` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idCurrency` int(10) UNSIGNED NOT NULL ,
  `name` varchar(256) NOT NULL ,
  `chargingPeriod`  int(10) UNSIGNED NOT NULL ,
  `minimumCharge` int(10) UNSIGNED NOT NULL ,
  `showChangesBefore` boolean NOT NULL default 0,
  `standingChargePeriod` DECIMAL(20,5) NOT NULL default 0,
  `minimumChargePeriod` DECIMAL(20,5) NOT NULL default 0,
  `vcpu` DECIMAL(20,5) NOT NULL default 0,
  `memoryMB` DECIMAL(20,5) NOT NULL default 0,
  `hdGB` DECIMAL(20,5) NOT NULL default 0,
  `vlan` DECIMAL(20,5) NOT NULL default 0,
  `publicIp` DECIMAL(20,5) NOT NULL default 0,
  `defaultTemplate` boolean NOT NULL default 0,
  `description` varchar(1000)  NOT NULL,
  `last_update` timestamp NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTemplate`) ,
  KEY `Pricing_FK2_Currency` (`idCurrency`),
  CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency` ) REFERENCES `kinton`.`currency` (`idCurrency` ) ON DELETE NO ACTION
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
  

--
-- Definition of table `kinton`.`pricingCostCode`
--  
  

CREATE TABLE `kinton`.`pricingCostCode` (
`idPricingCostCode` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idPricingTemplate` int(10) UNSIGNED NOT NULL,
  `idCostCode` int(10) UNSIGNED NOT NULL,
  `price` DECIMAL(20,5) NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingCostCode`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;  
  
 
--
-- Table `kinton`.`costCodeCurrency`
-- 

DROP TABLE IF EXISTS `kinton`.`costCodeCurrency`;
CREATE TABLE  `kinton`.`costCodeCurrency` (
  `idCostCodeCurrency` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idCostCode` int(10) unsigned,
  `idCurrency` int(10) unsigned,
  `price` DECIMAL(20,5) NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`idCostCodeCurrency`)
  -- CONSTRAINT `idCostCode_FK` FOREIGN KEY (`idCostCode`) REFERENCES `costCode` (`idCostCode`),
  -- CONSTRAINT `idCurrency_FK` FOREIGN KEY (`idCurrency`) REFERENCES `currency` (`idCurrency`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
 
 
--
-- Definition of table `kinton`.`pricingTemplate_tier`
--  


CREATE TABLE `kinton`.`pricingTier` (
  `idPricingTier` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idPricingTemplate` int(10) UNSIGNED NOT NULL,
  `idTier` int(10) UNSIGNED NOT NULL,
  `price`  DECIMAL(20,5) NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTier`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;    
  
-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricingTemplate` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`);



CALL `kinton`.`add_version_column_to_all`();
