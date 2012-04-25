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
  `idEnterprise` int(10) unsigned DEFAULT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idCategory`),
  KEY `category_enterprise_FK` (`idEnterprise`),
  CONSTRAINT `category_enterprise_FK` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`),
  UNIQUE KEY (`name`,`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`category`
--

/*!40000 ALTER TABLE `category` DISABLE KEYS */;
LOCK TABLES `category` WRITE;
INSERT INTO `kinton`.`category` VALUES  (1,'Others',0,1,null,0),
 (2,'Database servers',1,0,null, 0),
 (4,'Applications servers',1,0,null,0),
 (5,'Web servers',1,0,null,0);
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
7 - DISABLED_FOR_HA,
8 - HALTED_FOR_SAVE',
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
  `version_c` int(11) DEFAULT '0',
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
  KEY `fk_role_enterprise` (`idEnterprise`),
  CONSTRAINT `fk_role_enterprise` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8; 

--
-- Dumping data for table `kinton`.`role`
--

-- These values are used by the ldap_role table.
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
 (50,'PRICING_MANAGE',0),
 (51,'USERS_MANAGE_CHEF_ENTERPRISE',0),
 (52,'MANAGE_HARD_DISKS',0),
 (53,'APPLIB_MANAGE_GLOBAL_CATEGORIES',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `roles_privileges` DISABLE KEYS */;
LOCK TABLES `roles_privileges` WRITE;
INSERT INTO `roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),(1,23,0),(1,24,0),(1,25,0),
 (1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,47,0),(1,48,0),(1,49,0),(1,50,0),(1,51,0),(1,52,0),(1,53,0),
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
  `user` varchar(128) NOT NULL,
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
 ("client.main.showHardDisk","1","Show (1) or hide (0) hard disk tab"),
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
 ("client.wiki.user.createRole","http://community.abiquo.com/display/ABI20/Manage+Roles+and+ges","Role creation wiki"),
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
  `state` int(11) NOT NULL COMMENT 'possible states: 0 (detached) and 1 (attached).',
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
 
--
-- STATISTICS MODULE TRIGGERS
--

-- Init Triggers
--	
-- 	

DROP TRIGGER IF EXISTS `kinton`.`datacenter_created`;
DROP TRIGGER IF EXISTS `kinton`.`datacenter_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`virtualapp_created`;
DROP TRIGGER IF EXISTS `kinton`.`virtualapp_deleted`;
DROP TRIGGER IF EXISTS kinton.update_virtualapp_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_created`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_updated`;
DROP TRIGGER IF EXISTS `kinton`.`create_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_datastore_update_stats`;
DROP TRIGGER IF EXISTS kinton.create_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_created`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_deleted`;
-- DROP TRIGGER IF EXISTS `kinton`.`create_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS kinton.delete_rasd_management_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`create_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_volume_management_update_stats`;
DROP TRIGGER IF EXISTS kinton.update_rasd_management_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`user_created`;
DROP TRIGGER IF EXISTS `kinton`.`user_deleted`;
DROP TRIGGER IF EXISTS kinton.create_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`create_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS kinton.update_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS `kinton`.`update_network_configuration_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`dclimit_created`;
DROP TRIGGER IF EXISTS `kinton`.`dclimit_updated`;
DROP TRIGGER IF EXISTS `kinton`.`dclimit_deleted`;
--
-- Init Stats
DELIMITER |
-- We can disable Triggers by executing this SET @DISABLE_STATS_TRIGGERS = 1; on each connection opened
-- SET @DISABLE_STATS_TRIGGERS = 1;
--
SET @DISABLE_STATS_TRIGGERS = NULL;
-- Sets a Fake DataCenter to Store enterprises & users not assigned to a DataCenter, but counted as Full Cloud Usage Stats
|
-- ******************************************************************
-- Description:
--
-- Fires:
--
-- ******************************************************************
CREATE TRIGGER `kinton`.`datacenter_created` AFTER INSERT ON `kinton`.`datacenter`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);
    END IF;
  END;
|
--
CREATE TRIGGER `kinton`.`datacenter_deleted` AFTER DELETE ON `kinton`.`datacenter`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
	DELETE FROM dc_enterprise_stats WHERE idDataCenter = OLD.idDataCenter;
      	DELETE FROM cloud_usage_stats WHERE idDataCenter = OLD.idDataCenter;
    END IF;
  END;
--
|
CREATE TRIGGER `kinton`.`virtualapp_created` AFTER INSERT ON `kinton`.`virtualapp`
  FOR EACH ROW BEGIN
    DECLARE vdcNameObj VARCHAR(50) CHARACTER SET utf8;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vdc.name INTO vdcNameObj
      FROM virtualdatacenter vdc
      WHERE NEW.idVirtualDataCenter = vdc.idVirtualDataCenter;
      INSERT IGNORE INTO vapp_enterprise_stats (idVirtualApp, idEnterprise, idVirtualDataCenter, vappName, vdcName) VALUES(NEW.idVirtualApp, NEW.idEnterprise, NEW.idVirtualDataCenter, NEW.name, vdcNameObj);
    END IF;
  END;
--
|
CREATE TRIGGER `kinton`.`virtualapp_deleted` AFTER DELETE ON `kinton`.`virtualapp`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    DELETE FROM vapp_enterprise_stats WHERE idVirtualApp = OLD.idVirtualApp;
  END IF;
  END;
--
|
--
-- ******************************************************************************************
-- Description:
--  * Checksfor V2V operations to update vmCreated stats by VirtualDatacenter
--  * Checks for name changes on virtualapp to update statistics dashboard
--
-- Fires: On an UPDATE for the 'virtualapp' table
-- ******************************************************************************************
CREATE TRIGGER kinton.update_virtualapp_update_stats AFTER UPDATE ON kinton.virtualapp
  FOR EACH ROW BEGIN
    DECLARE numVMachinesCreated INTEGER;
    DECLARE vdcNameObj VARCHAR(45);
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    -- V2V: Vmachines moved between VDC
  IF NEW.idVirtualDataCenter != OLD.idVirtualDataCenter THEN
	-- calculate vmachines total and running in this Vapp
	SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numVMachinesCreated
	FROM nodevirtualimage nvi, virtualmachine v, node n
	WHERE nvi.idNode IS NOT NULL
	AND v.idVM = nvi.idVM
	AND n.idNode = nvi.idNode
	AND n.idVirtualApp = NEW.idVirtualApp
	AND v.state != "NOT_ALLOCATED" AND v.state != "UNKNOWN"
	and v.idType = 1;
	UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated- numVMachinesCreated WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
	UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+ numVMachinesCreated WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
	-- Changing VDC name in VAppStats
	SELECT vdc.name INTO vdcNameObj
	FROM virtualdatacenter vdc
	WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
	UPDATE IGNORE vapp_enterprise_stats SET vdcName = vdcNameObj WHERE idVirtualApp = NEW.idVirtualApp;
    END IF;
    -- Checks for changes
    IF OLD.name != NEW.name THEN
      -- Name changed !!!
      UPDATE IGNORE vapp_enterprise_stats SET vappName = NEW.name
      WHERE idVirtualApp = NEW.idVirtualApp;
    END IF;
  END IF;
  END;
--
-- ******************************************************************************************
-- Description: 
--  * Creates a New row in enterprise_resources_stats to store this enterprise's statistics
--  * Initializes stats for reserved resources (by Enterprise & by DataCenter)
--  * Updates enterprises created (in Fake DataCenter) for Full Cloud Statistics
--
-- Fires: On an INSERT for the enterprise_created table

-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`enterprise_created` AFTER INSERT ON `kinton`.`enterprise`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            --  Creates a New row in enterprise_resources_stats to store this enterprise's statistics
            INSERT IGNORE INTO enterprise_resources_stats (idEnterprise) VALUES (NEW.idEnterprise);
            --  Initializes stats for reserved resources (by Enterprise & by DataCenter)            
            UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated+1 WHERE idDataCenter = -1;
            UPDATE IGNORE enterprise_resources_stats 
                SET     vCpuReserved = vCpuReserved + NEW.cpuHard,
                    memoryReserved = memoryReserved + NEW.ramHard,
                    localStorageReserved = localStorageReserved + NEW.hdHard,
                    extStorageReserved = extStorageReserved + NEW.storageHard,
                    -- repositoryReserved = repositoryReserved + NEW.repositoryHard,
                    -- To be updated when IP is actually reserved/freed
                    -- publicIPsReserved = publicIPsReserved + NEW.publicIPHard, 
                    vlanReserved = vlanReserved + NEW.vlanHard
            WHERE idEnterprise = NEW.idEnterprise;  
            --  Updates enterprises created (in Fake DataCenter) for Full Cloud Statistics
            UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved=vCpuReserved + NEW.cpuHard,
                    vMemoryReserved=vMemoryReserved + NEW.ramHard,
                    vStorageReserved=vStorageReserved + NEW.hdHard,
                    storageReserved = storageReserved + NEW.storageHard,
                    -- repositoryReserved = repositoryReserved + NEW.repositoryHard,
                    publicIPsReserved = publicIPsReserved + NEW.publicIPHard
            WHERE idDataCenter = -1;                        
        END IF;
    END;
-- ******************************************************************************************
-- Description: 
--  * Calculates reserved resources increments if changed
--  * if Deleted/Undeleted logically, updates reserved resources & no. enterprises stats
--
-- Fires: On an UPDATE IGNORE for the enterprise_created table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`enterprise_updated` AFTER UPDATE ON `kinton`.`enterprise`
-- WARN: Enterprises are not deleted, logical delete (delete field) 
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
        -- get DataCenter
        -- calculates resources increase        
        UPDATE IGNORE enterprise_resources_stats 
            SET vCpuReserved = vCpuReserved + NEW.cpuHard - OLD.cpuHard,
                memoryReserved = memoryReserved + NEW.ramHard - OLD.ramHard,
                localStorageReserved = localStorageReserved + NEW.hdHard - OLD.hdHard,
                extStorageReserved = extStorageReserved + NEW.storageHard - OLD.storageHard,
                repositoryReserved = repositoryReserved + NEW.repositoryHard - OLD.repositoryHard,
                -- To be updated when IP is actually reserved/freed
                -- publicIPsReserved = publicIPsReserved + NEW.publicIPHard - OLD.publicIPHard,
                vlanReserved = vlanReserved + NEW.vlanHard - OLD.vlanHard
        WHERE idEnterprise = NEW.idEnterprise;
        UPDATE IGNORE cloud_usage_stats 
        SET vCpuReserved = vCpuReserved  + NEW.cpuHard - OLD.cpuHard,
            vMemoryReserved=vMemoryReserved + NEW.ramHard - OLD.ramHard,            
            vStorageReserved = vStorageReserved + NEW.hdHard - OLD.hdHard,
            storageReserved=storageReserved + NEW.storageHard - OLD.storageHard,
            publicIPsReserved = publicIPsReserved + NEW.publicIPHard - OLD.publicIPHard
        WHERE idDataCenter = -1;
    END IF;
  END;
--
-- ******************************************************************************************
-- Description: 
--  * Destroys all statistics for this enterprise
--
-- Fires: On an DELETE for the enterprise_created table
--
-- ************************************************************************************
|
CREATE TRIGGER `kinton`.`enterprise_deleted` AFTER DELETE ON `kinton`.`enterprise`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
        DELETE FROM enterprise_resources_stats WHERE idEnterprise = OLD.idEnterprise;           
        UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated-1 WHERE idDataCenter = -1;
        -- init reserved stats
        UPDATE IGNORE cloud_usage_stats 
            SET     vCpuReserved=vCpuReserved - OLD.cpuHard,
                vMemoryReserved=vMemoryReserved - OLD.ramHard,
                vStorageReserved = vStorageReserved - OLD.hdHard,
                storageReserved=storageReserved - OLD.storageHard,                
                -- repositoryReserved = repositoryReserved - OLD.repositoryHard
                -- To be updated when IP is actually reserved/freed
                publicIPsReserved = publicIPsReserved - OLD.publicIPHard
        WHERE idDataCenter = -1;
    END IF;
    END;
--
--
-- Triggers ON Physical Machine
-- ******************************************************************************************
-- Description:
--  * Updates cloud_usage_stats: server totals/running, virtualcpu (total/used), virtualmemory (total/used), virtualstorage (total/used)
--   when a physicalmachines is created
--
-- Fires: On an INSERT for the physicalmachine table
--
-- ************************************************************************************
--
|
CREATE TRIGGER `kinton`.`create_physicalmachine_update_stats` AFTER INSERT ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
               vCpuUsed=vCpuUsed+NEW.cpuUsed, vMemoryUsed=vMemoryUsed+NEW.ramUsed
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
    IF NEW.idState != 2 THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal = serversTotal+1, 
               vCpuTotal=vCpuTotal+NEW.cpu, vMemoryTotal=vMemoryTotal+NEW.ram
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
END IF;
END
|
CREATE TRIGGER `kinton`.`create_datastore_update_stats` AFTER INSERT ON `kinton`.`datastore_assignment`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
DECLARE enabled INT UNSIGNED;
DECLARE usedSize BIGINT UNSIGNED;
DECLARE size BIGINT UNSIGNED;
DECLARE datastoreuuid VARCHAR(255);
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm WHERE pm.idPhysicalMachine = NEW.idPhysicalmachine;
SELECT d.enabled, d.usedSize, d.size, d.datastoreUUID INTO enabled, usedSize, size, datastoreuuid FROM datastore d WHERE d.idDatastore = NEW.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
        LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
        WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = datastoreuuid AND d.idDatastore != NEW.idDatastore
        AND d.enabled = 1) = 0 THEN
        IF machineState = 3 THEN
            IF enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed + usedSize
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
        IF machineState != 2 THEN
            IF enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + size
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
    END IF;
END IF;
END

--
-- ******************************************************************************************
-- Description:
--  * Updates cloud_usage_stats: server totals/running, virtualcpu (total/used), virtualmemory (total/used), virtualstorage (total/used)
--   when a physicalmachines is deleted
--
-- Fires: On an DELETE for the physicalmachine table
--
-- ************************************************************************************
|
CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
               vCpuUsed=vCpuUsed-OLD.cpuUsed, vMemoryUsed=vMemoryUsed-OLD.ramUsed
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
               vCpuTotal=vCpuTotal-OLD.cpu, vMemoryTotal=vMemoryTotal-OLD.ram
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
END IF;
END;
|
CREATE TRIGGER `kinton`.`delete_datastore_update_stats` BEFORE DELETE ON `kinton`.`datastore`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = OLD.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
        LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
        WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = OLD.datastoreuuid AND d.idDatastore != OLD.idDatastore
        AND d.enabled = 1) = 0 THEN
        IF machineState = 3 THEN
            IF OLD.enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
        IF machineState NOT IN (2, 6, 7) THEN
            IF OLD.enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
    END IF;
END IF;
END
--
|
-- ******************************************************************************************
-- Description:
--  * Updates cloud_usage_stats: server totals/running, virtualcpu (total/used), virtualmemory (total/used), virtualstorage (total/used)
--   when a physicalmachines is updated
--
-- Fires: On an UPDATE IGNORE for the physicalmachine table
--
-- ************************************************************************************
CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreSize BIGINT UNSIGNED;
DECLARE oldDatastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
            -- Machine not managed changes into managed; or disabled_by_ha to Managed
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1,
                   vCpuTotal=vCpuTotal + NEW.cpu,
                   vMemoryTotal=vMemoryTotal + NEW.ram
            WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
            -- Machine managed changes into not managed or DisabledByHA
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
                   vCpuTotal=vCpuTotal-OLD.cpu,
                   vMemoryTotal=vMemoryTotal-OLD.ram
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
            -- Stopped / Halted / Not provisioned passes to Managed (Running)
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
                   vCpuUsed=vCpuUsed+NEW.cpuUsed,
                   vMemoryUsed=vMemoryUsed+NEW.ramUsed
            WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
            -- Managed (Running) passes to Stopped / Halted / Not provisioned
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
                   vCpuUsed=vCpuUsed-OLD.cpuUsed,
                   vMemoryUsed=vMemoryUsed-OLD.ramUsed
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    ELSE
        -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
            -- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
            UPDATE IGNORE cloud_usage_stats SET vCpuTotal=vCpuTotal+(NEW.cpu-OLD.cpu),
                   vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
            UPDATE IGNORE cloud_usage_stats SET vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
                   vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    END IF;
END IF;
END;
|
CREATE TRIGGER `kinton`.`update_datastore_update_stats` AFTER UPDATE ON `kinton`.`datastore`
    FOR EACH ROW BEGIN
	DECLARE idDatacenter INT UNSIGNED;
	DECLARE machineState INT UNSIGNED;
	SELECT pm.idDatacenter, pm.idState INTO idDatacenter, machineState FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
	WHERE da.idDatastore = NEW.idDatastore;
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
            IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
                LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
                WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = NEW.datastoreUUID AND d.idDatastore != NEW.idDatastore 
                AND d.enabled = 1) = 0 THEN
	        IF OLD.enabled = 1 THEN
		    IF NEW.enabled = 1 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size,
		            cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
	            ELSEIF NEW.enabled = 0 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size,
		            cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
		    END IF;
	        ELSE
		    IF NEW.enabled = 1 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size,
		            cus.vStorageUsed = cus.vStorageUsed + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
		    END IF;
	        END IF;
            ELSEIF NEW.usedSize NOT IN (SELECT d.usedSize FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
                LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
                WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = NEW.datastoreUUID AND d.idDatastore != NEW.idDatastore 
                AND d.enabled = 1) THEN
                -- repeated code to update only the first shared datastore
	        IF OLD.enabled = 1 THEN
		    IF NEW.enabled = 1 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size,
		            cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
	            ELSEIF NEW.enabled = 0 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size,
		            cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
		    END IF;
	        ELSE
		    IF NEW.enabled = 1 THEN
		        IF machineState IN (2, 6, 7) THEN
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size
		            WHERE cus.idDatacenter = idDatacenter;
		        ELSE
		            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size,
		            cus.vStorageUsed = cus.vStorageUsed + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		        END IF;
		    END IF;
	        END IF;
	    END IF;
        END IF;
    END;
--
--
-- ******************************************************************************************
-- Description: 
--  * Keeps track of previous states for virtualmachines
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`create_virtualmachine_update_stats` AFTER INSERT ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		INSERT INTO virtualmachinetrackedstate (idVM) VALUES (NEW.idVM);
	END IF;
    END;
--
|
--
-- ******************************************************************************************
-- Description: 
--  * Keeps track of previous states for virtualmachines
--
-- ******************************************************************************************	
CREATE TRIGGER `kinton`.`delete_virtualmachine_update_stats` AFTER DELETE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		DELETE FROM virtualmachinetrackedstate WHERE idVM = OLD.idVM;
	END IF;
    END;
--
|
--	
-- ******************************************************************************************
-- Description: 
--  * Updates resources (cpu, ram, hd) used by Enterprise
--  * Updates vMachinesRunning for cloud Usage Stats
--  * Keeps track of previous states for virtualmachines
--
-- Fires: ON UPDATE IGNORE an virtualmachine for the virtualmachine table
--
--
-- ******************************************************************************************
CREATE TRIGGER kinton.update_virtualmachine_update_stats AFTER UPDATE ON kinton.virtualmachine
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;	
        DECLARE costCodeObj int(4);
	DECLARE previousState VARCHAR(50);
	DECLARE extraHDSize BIGINT DEFAULT 0;
	-- For debugging purposes only        
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
	-- We always store previous state when starting a transaction
	IF NEW.state != OLD.state AND OLD.state NOT IN ('LOCKED') THEN
		UPDATE virtualmachinetrackedstate SET previousState=OLD.state WHERE idVM=NEW.idVM;
	END IF;
	--
	SELECT vmts.previousState INTO previousState
        FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = NEW.idVM;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVM ', NEW.idVM, ' - ', OLD.idType, ' - ', NEW.idType, ' - ', OLD.state, ' - NEW.state: ', NEW.state, ' - previousState: ', previousState));	
        --  Updating enterprise_resources_stats: VCPU Used, Memory Used, Local Storage Used
        IF OLD.idHypervisor IS NULL OR (OLD.idHypervisor != NEW.idHypervisor) THEN
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE NEW.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        ELSE 
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE OLD.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        END IF;     
        --
        SELECT n.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM nodevirtualimage nvi, node n, virtualapp vapp
        WHERE NEW.idVM = nvi.idVM
        AND nvi.idNode = n.idNode
        AND vapp.idVirtualApp = n.idVirtualApp;   
-- INSERT INTO debug_msg (msg) VALUES (CONCAT('update values ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
	--
	-- Imported VMs will be updated on create_node_virtual_image
	-- Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from delete_nodevirtualimage_update_stats ON DELETE nodevirtualimage when updating the VApp
	-- Main case: an imported VM changes its state (from LOCKED to ...)
	-- TODO: Create SQLProcedures to update stats. Code is repeated here
	IF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
		IF previousState IN ("NOT_ALLOCATED") AND NEW.state IN ("OFF", "ON") THEN
			-- Machine has been deployed or re-captured
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats DEPLOY+POWERON event detected for Running machine. Updating (+1) VMachinesTotal from Stats', NEW.idVM));
			UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
	           WHERE idVirtualApp = idVirtualAppObj;
	           UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
	           WHERE idVirtualDataCenter = idVirtualDataCenterObj;
	           UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
	           WHERE idDataCenter = idDataCenterObj;
			IF NEW.state IN ("ON") THEN
				-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats DEPLOY+POWERON event detected for Running machine. Updating (+1) VMachinesRunning VM from Stats', NEW.idVM));
				UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
	                WHERE idVirtualApp = idVirtualAppObj;
	                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
	                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
	                WHERE idDataCenter = idDataCenterObj;       
				SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
				FROM rasd_management rm, rasd r 
				WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs added ', extraHDSize));
	                UPDATE IGNORE enterprise_resources_stats 
	                    SET vCpuUsed = vCpuUsed + NEW.cpu,
	                        memoryUsed = memoryUsed + NEW.ram,
	                        localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise;
	                UPDATE IGNORE dc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed + NEW.cpu,
	                    memoryUsed = memoryUsed + NEW.ram,
	                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
	                UPDATE IGNORE vdc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed + NEW.cpu,
	                    memoryUsed = memoryUsed + NEW.ram,
	                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj;						
			END IF;
		ELSEIF previousState IN ("ON","PAUSED","OFF") AND NEW.state IN ("NOT_ALLOCATED") THEN
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats UNDEPLOY event detected. Updating (-1) VMachinesTotal VM from Stats', NEW.idVM));
			UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
                WHERE idDataCenter = idDataCenterObj;
			IF previousState IN ("ON") THEN
				-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats UNDEPLOY event detected for Running machine. Updating (-1) VMachinesRunning VM from Stats', NEW.idVM));
				UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
	                WHERE idVirtualApp = idVirtualAppObj;
	                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
	                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
	                WHERE idDataCenter = idDataCenterObj;
				SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
				FROM rasd_management rm, rasd r 
				WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs removed ', extraHDSize));
	                UPDATE IGNORE enterprise_resources_stats 
	                    SET vCpuUsed = vCpuUsed - NEW.cpu,
	                        memoryUsed = memoryUsed - NEW.ram,
	                        localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise;
	                UPDATE IGNORE dc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed - NEW.cpu,
	                    memoryUsed = memoryUsed - NEW.ram,
	                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
	                UPDATE IGNORE vdc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed - NEW.cpu,
	                    memoryUsed = memoryUsed - NEW.ram,
	                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 		
			END IF;
		ELSEIF previousState IN ("OFF","PAUSED","UNKNOWN") AND NEW.state IN ("ON") THEN
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats POWERON/RESUME/UNKNOWN event detected. Updating (+1) VMachinesRunning VM from Stats', NEW.idVM));
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
                WHERE idDataCenter = idDataCenterObj;       
			SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
			FROM rasd_management rm, rasd r 
			WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs added ', extraHDSize));
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;		
		ELSEIF previousState IN ("ON")  AND NEW.state IN ("OFF","PAUSED","UNKNOWN") THEN
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVMStats POWEROFF/PAUSE/UNKNOWN event detected. Updating (-1) VMachinesRunning VM from Stats', NEW.idVM));
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
	                WHERE idVirtualApp = idVirtualAppObj;
	                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
	                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
	                WHERE idDataCenter = idDataCenterObj;
				SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
				FROM rasd_management rm, rasd r 
				WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs removed ', extraHDSize));
	                UPDATE IGNORE enterprise_resources_stats 
	                    SET vCpuUsed = vCpuUsed - NEW.cpu,
	                        memoryUsed = memoryUsed - NEW.ram,
	                        localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise;
	                UPDATE IGNORE dc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed - NEW.cpu,
	                    memoryUsed = memoryUsed - NEW.ram,
	                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
	                UPDATE IGNORE vdc_enterprise_stats 
	                SET     vCpuUsed = vCpuUsed - NEW.cpu,
	                    memoryUsed = memoryUsed - NEW.ram,
	                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
	                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		END IF;
        END IF;
        --
        SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, previousState, NEW.ram, NEW.cpu, NEW.hd + extraHDSize, costCodeObj);
       		 END IF;              
	    END IF;
      END IF;
    END;
--
-- ******************************************************************************************
-- Description:
--  * Updates counter for imported virtual machines: total && created for cloud_usage_stats
--  * Updates counter for imported virtual machines: total && created for vdc_enterprise_stats
--  * Updates counter for imported virtual machines: total && created for vapp_enterprise_stats
--
-- Fires: ON INSERT for the nodevirtualimage table
--
-- ******************************************************************************************
--
|
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
CREATE TRIGGER kinton.create_nodevirtualimage_update_stats AFTER INSERT ON kinton.nodevirtualimage
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    DECLARE costCodeObj int(4);
    DECLARE type INTEGER;
    DECLARE state VARCHAR(50);
    DECLARE ram INTEGER;
    DECLARE cpu INTEGER;
    DECLARE hd bigint;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter, vdc.idEnterprise  INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj, idEnterpriseObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = NEW.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.idType, vm.state, vm.cpu, vm.ram, vm.hd INTO type, state, cpu, ram, hd
     FROM virtualmachine vm
	WHERE vm.idVM = NEW.idVM;
     -- INSERT INTO debug_msg (msg) VALUES (CONCAT('createNVI ', type, ' - ', state, ' - ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL')));
    IF type=1 THEN
    	-- Imported !!!
		IF state NOT IN ("NOT_ALLOCATED","UNKNOWN") THEN
			-- INSERT INTO debug_msg (msg) VALUES (CONCAT('CreateNVI deploy detected. Adding VM ', NEW.idVM));
			UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                END IF;
          IF state = "ON" THEN 	
          	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('CreateNVI deploy runningVM detected. Adding RUnning VM ', NEW.idVM));
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualApp = idVirtualAppObj;
		        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
		        WHERE idDataCenter = idDataCenterObj;       
		        UPDATE IGNORE enterprise_resources_stats 
		            SET vCpuUsed = vCpuUsed + cpu,
		                memoryUsed = memoryUsed + ram,
		                localStorageUsed = localStorageUsed + hd
		        WHERE idEnterprise = idEnterpriseObj;
		        UPDATE IGNORE dc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + cpu,
		            memoryUsed = memoryUsed + ram,
		            localStorageUsed = localStorageUsed + hd
		        WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
		        UPDATE IGNORE vdc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + cpu,
		            memoryUsed = memoryUsed + ram,
		            localStorageUsed = localStorageUsed + hd
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;	
		END IF;
    END IF;    
    SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, type, "NOT_ALLOCATED", state, "NOT_ALLOCATED", ram, cpu, hd, costCodeObj);
        END IF;              
     END IF;
    END IF;
  END;
-- ******************************************************************************************
-- Description:
--  * Updates counter for virtual machines: total && created for cloud_usage_stats
--  * Updates counter for virtual machines: total && created for vdc_enterprise_stats
--  * Updates counter for virtual machines: total && created for vapp_enterprise_stats
--
-- Fires: ON DELETE for the nodevirtualimage table
--
-- ******************************************************************************************
--
|
CREATE TRIGGER kinton.delete_nodevirtualimage_update_stats AFTER DELETE ON kinton.nodevirtualimage
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;   
    DECLARE costCodeObj int(4); 
    DECLARE previousState VARCHAR(50);
    DECLARE state VARCHAR(50);
    DECLARE ram INTEGER;
    DECLARE cpu INTEGER;
    DECLARE hd bigint;
    DECLARE type INTEGER;
    DECLARE isUsingIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter, vdc.idEnterprise INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj, idEnterpriseObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = OLD.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.idType, vm.cpu, vm.ram, vm.hd, vm.state INTO type, cpu, ram, hd, state
     FROM virtualmachine vm
	WHERE vm.idVM = OLD.idVM;
    SELECT vmts.previousState INTO previousState
     FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = OLD.idVM;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('deleteNVI ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
-- INSERT INTO debug_msg (msg) VALUES (CONCAT('deleteNVI values', IFNULL(cpu,'NULL'), ' - ',IFNULL(ram,'NULL'), ' - ',IFNULL(hd,'NULL')));						
    --
    IF type = 1 THEN
      IF previousState NOT IN ("NOT_ALLOCATED","UNKNOWN") THEN      
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
           UPDATE IGNORE enterprise_resources_stats 
               SET vCpuUsed = vCpuUsed - cpu,
                   memoryUsed = memoryUsed - ram,
                   localStorageUsed = localStorageUsed - hd
           WHERE idEnterprise = idEnterpriseObj;
           UPDATE IGNORE dc_enterprise_stats 
           SET     vCpuUsed = vCpuUsed - cpu,
               memoryUsed = memoryUsed - ram,
               localStorageUsed = localStorageUsed - hd
           WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
           UPDATE IGNORE vdc_enterprise_stats 
           SET     vCpuUsed = vCpuUsed - cpu,
               memoryUsed = memoryUsed - ram,
               localStorageUsed = localStorageUsed - hd
           WHERE idVirtualDataCenter = idVirtualDataCenterObj;                 
      END IF;
      --
      IF previousState = "ON" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
    SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = OLD.idImage;
    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=OLD.idImage) THEN 
	          CALL AccountingVMRegisterEvents(OLD.idVM, type, "-", "NOT_ALLOCATED", previousState, ram, cpu, hd, costCodeObj);
        END IF;              
     END IF;
  END IF;
  END;
--
-- ******************************************************************************************
-- Description:
--  * Updates storageTotal
--  * Register Storage Created Event for Accounting
--
-- Fires: On an INSERT for the volume_managment table
--
--
-- ******************************************************************************************
|
-- This Trigger was deleted in 2.0-> CREATE TRIGGER `kinton`.`create_rasd_management_update_stats` AFTER INSERT ON `kinton`.`rasd_management`
CREATE TRIGGER `kinton`.`create_volume_management_update_stats` AFTER INSERT ON `kinton`.`volume_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;
        DECLARE idResourceObj VARCHAR(50);
        DECLARE idResourceTypeObj VARCHAR(5);
	DECLARE idStorageTier INTEGER;
        DECLARE resourceName VARCHAR(255);
        SELECT vdc.idDataCenter, vdc.idEnterprise, vdc.idVirtualDataCenter INTO idDataCenterObj, idThisEnterprise, idVirtualDataCenterObj
        FROM virtualdatacenter vdc, rasd_management rm
        WHERE vdc.idVirtualDataCenter = rm.idVirtualDataCenter
        AND NEW.idManagement = rm.idManagement;
        --
        SELECT r.elementName, r.limitResource, rm.idResource, rm.idResourceType INTO resourceName, limitResourceObj, idResourceObj, idResourceTypeObj
        FROM rasd r, rasd_management rm
        WHERE r.instanceID = rm.idResource
        AND NEW.idManagement = rm.idManagement;
        --
        SELECT sp.idTier INTO idStorageTier
        FROM storage_pool sp
        WHERE sp.idStorage = NEW.idStorage;
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idDataCenterObj ',IFNULL(idDataCenterObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL limitResourceObj ',IFNULL(limitResourceObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idResourceObj ',IFNULL(idResourceObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idStorageTier ',IFNULL(idStorageTier,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL resourceName: ',IFNULL(resourceName,'-')));
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF idResourceTypeObj='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('CREATE_STORAGE', idResourceObj, resourceName, idStorageTier, idVirtualDataCenterObj, idThisEnterprise, limitResourceObj);
                END IF;               
            END IF;
        END IF;
    END;
--
-- ******************************************************************************************
-- Description: 
--  * Updates storageTotal
--  * Register Storage Deleted Event for Accounting
--
-- Fires: On an DELETE for the rasd_management table
--
-- ******************************************************************************************
|
CREATE TRIGGER kinton.delete_rasd_management_update_stats AFTER DELETE ON kinton.rasd_management
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;    
        DECLARE resourceName VARCHAR(255);  
	DECLARE currentState VARCHAR(50);
	DECLARE previousState VARCHAR(50);
	DECLARE extraHDSize BIGINT DEFAULT 0;
	SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
	SELECT vm.state INTO currentState
        FROM virtualmachine vm
        WHERE vm.idVM = OLD.idVM;
	SELECT vmts.previousState INTO previousState
        FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = OLD.idVM;
        SELECT elementName, limitResource INTO resourceName, limitResourceObj
        FROM rasd r
        WHERE r.instanceID = OLD.idResource;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF OLD.idResourceType='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal-limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('DELETE_STORAGE', OLD.idResource, resourceName, 0, OLD.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
                END IF;                  
            END IF;
            IF OLD.idResourceType='17' AND previousState = 'ON' THEN
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Removed ExtraHDs ', limitResourceObj, ' for idVM ', OLD.idVM, ' with state ', previousState));  
		SELECT limitResourceObj * 1048576 INTO extraHDSize;
		UPDATE IGNORE enterprise_resources_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize 
                WHERE idEnterprise = idThisEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize
                WHERE idEnterprise = idThisEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize
                WHERE idVirtualDataCenter = OLD.idVirtualDataCenter; 		
	    END IF;
        END IF;
    END;      
--
-- Triggers on virtualdatacenter
-- ******************************************************************************************
-- Description: 
--  * Updates no. VDCs created
--  * Initializes vdc_enterprise_stats
--
-- Fires: On an INSERT for the virtualdatacenter table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`virtualdatacenter_created` AFTER INSERT ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
        UPDATE IGNORE cloud_usage_stats SET numVDCCreated = numVDCCreated + 1 WHERE idDataCenter = NEW.idDataCenter;    
        -- Init Stats
        INSERT IGNORE INTO vdc_enterprise_stats 
            (idVirtualDataCenter,idEnterprise,vdcName,
            vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
            extStorageReserved, extStorageUsed, publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed) 
        VALUES 
            (NEW.idVirtualDataCenter, NEW.idEnterprise, NEW.name,
            NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
            NEW.storageHard, 0, 0, 0, NEW.vlanHard, 0);          
    END IF;
    END;
--
--
-- ******************************************************************************************
-- Description: 
--  * Checks for name changes on virtualapp to update statistics dashboard
--
-- Fires: On an UPDATE for the 'virtualdatacenter' table
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`virtualdatacenter_updated` AFTER UPDATE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE vlanNetworkIdObj INTEGER;    
        	  DECLARE networkNameObj VARCHAR(40) CHARACTER SET utf8;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- INSERT INTO debug_msg (msg) VALUES (CONCAT('OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'),'NEW.networktypeID ', IFNULL(NEW.networktypeID,'NULL')));
            -- Checks for changes
            IF OLD.name != NEW.name THEN
                -- Name changed !!!
                UPDATE IGNORE vdc_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                -- Changes also in Vapp stats
                UPDATE IGNORE vapp_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualApp IN (SELECT idVirtualApp FROM virtualapp WHERE idVirtualDataCenter=NEW.idVirtualDataCenter);
            END IF; 
            UPDATE IGNORE vdc_enterprise_stats 
            SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                memoryReserved = memoryReserved - OLD.ramHard + NEW.ramHard,
                localStorageReserved = localStorageReserved - OLD.hdHard + NEW.hdHard,
                -- publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                extStorageReserved = extStorageReserved - OLD.storageHard + NEW.storageHard,
                vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
            WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;            
        END IF;
        IF OLD.networktypeID IS NOT NULL AND NEW.networktypeID IS NULL THEN
        -- Remove VlanUsed
	    BEGIN
		DECLARE done INTEGER DEFAULT 0;
		DECLARE cursorVlan CURSOR FOR SELECT DISTINCT vn.network_id, vn.network_name FROM vlan_network vn WHERE vn.network_id = OLD.networktypeID;
		DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
		    
		OPEN cursorVlan;
		    
		REPEAT
		   FETCH cursorVlan into vlanNetworkIdObj, networkNameObj;
		   IF NOT done THEN

		    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('VDC UPDATED -> OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'), 'Enterprise: ',IFNULL(OLD.idEnterprise,'NULL'),' VDC: ',IFNULL(OLD.idVirtualDataCenter,'NULL'),IFNULL(vlanNetworkIdObj,'NULL'),IFNULL(networkNameObj,'NULL')));
			IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
				CALL AccountingVLANRegisterEvents('DELETE_VLAN',vlanNetworkIdObj, networkNameObj, OLD.idVirtualDataCenter,OLD.idEnterprise);
			END IF;
			-- Statistics
			UPDATE IGNORE cloud_usage_stats
				SET     vlanUsed = vlanUsed - 1
				WHERE idDataCenter = -1;
			UPDATE IGNORE enterprise_resources_stats 
				SET     vlanUsed = vlanUsed - 1
				WHERE idEnterprise = OLD.idEnterprise;
			UPDATE IGNORE vdc_enterprise_stats 
				SET     vlanUsed = vlanUsed - 1
			    WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
		   END IF;    
		UNTIL done END REPEAT;
		CLOSE cursorVlan;
	    END;
        END IF;
    END;
|
-- ******************************************************************************************
-- Description: 
--  * Updates no. VDCs created
--  * Removes vdc_enterprise_stats
--
-- Fires: On an DELETE for the virtualdatacenter table
--
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) CHARACTER SET utf8 DEFAULT '';
    DECLARE no_more_ipsfreed INT;
    DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement   
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.vlan_network_id = vn.vlan_network_id
           AND vn.network_configuration_id = nc.network_configuration_id
           AND vn.network_id = dc.network_id
       AND vn.networktype = 'PUBLIC'
           AND ra.idManagement = ipm.idManagement
           AND ra.idVirtualDataCenter = OLD.idVirtualDataCenter;
       DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_ipsfreed = 1;   
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
            UPDATE IGNORE cloud_usage_stats SET numVDCCreated = numVDCCreated-1 WHERE idDataCenter = OLD.idDataCenter;  
            -- Remove Stats
            DELETE FROM vdc_enterprise_stats WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
           --   
    SET no_more_ipsfreed = 0;       
        OPEN curIpFreed;            
        my_loop:WHILE(no_more_ipsfreed=0) DO 
        FETCH curIpFreed INTO currentDataCenter, currentIpAddress, currentIdManagement;
        IF no_more_ipsfreed=1 THEN
                    LEAVE my_loop;
             END IF;
--      INSERT INTO debug_msg (msg) VALUES (CONCAT('IP_FREED: ',currentIpAddress, ' - idManagement: ', currentIdManagement, ' - OLD.idVirtualDataCenter: ', OLD.idVirtualDataCenter, ' - idEnterpriseObj: ', OLD.idEnterprise));
        -- We reset MAC and NAME for the reserved IPs. Java code should do this!
        UPDATE ip_pool_management set mac=NULL, name=NULL WHERE idManagement = currentIdManagement;
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                    CALL AccountingIPsRegisterEvents('IP_FREED',currentIdManagement,currentIpAddress,OLD.idVirtualDataCenter, OLD.idEnterprise);
            END IF;                    
        UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = OLD.idEnterprise; 
        END WHILE my_loop;         
        CLOSE curIpFreed;
        END IF;
    END;
--
-- Triggers on user
-- ******************************************************************************************
-- Description:
--  * Updates no. users created (in Fake DataCenter) for Full Cloud Statistics
--
-- Fires: On an DELETE for the user table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`user_created` AFTER INSERT ON `kinton`.`user`
  FOR EACH ROW BEGIN
    -- DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    -- SELECT vc.idDataCenter INTO idDataCenterObj
    -- FROM virtualdatacenter vc, enterprise e
    -- WHERE vc.idEnterprise = e.idEnterprise
    -- AND NEW.idEnterprise = e.idEnterprise;
    UPDATE IGNORE cloud_usage_stats SET numUsersCreated = numUsersCreated+1 WHERE idDataCenter = -1;
   END IF;
  END;
--
-- ******************************************************************************************
-- Description:
--  * Updates no. users created (in Fake DataCenter) for Full Cloud Statistics
--
-- Fires: On an DELETE for the user table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`user_deleted` AFTER DELETE ON `kinton`.`user`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    UPDATE IGNORE cloud_usage_stats SET numUsersCreated = numUsersCreated-1 WHERE idDataCenter = -1;
  END IF;
  END;
-- ******************************************************************************************
-- * Updatess Storage Total / Reserved / Used for cloud
-- * Updates volCreated, volAttached
-- * Updates storageUsed for enterprise, dc, vdc
-- * Register Updated Storage Event for statistics

--  State defined at com.abiquo.abiserver.storage.StorageState
--      NOT_MOUNTED_NOT_RESERVED(0),
--      NOT_MOUNTED_RESERVED(1),
--      MOUNTED_RESERVED(2);
--
-- Fires: On an UPDATE for the volume_management table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`update_volume_management_update_stats` AFTER UPDATE ON `kinton`.`volume_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE incr INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
        SET incr = NEW.usedSize-OLD.usedSize;
        SELECT sd.idDataCenter INTO idDataCenterObj
        FROM storage_pool sp, storage_device sd
        WHERE OLD.idStorage = sp.idStorage
        AND sp.idStorageDevice = sd.id;
        --      
        SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM rasd_management rasd, virtualapp vapp
        WHERE OLD.idManagement = rasd.idManagement
        AND rasd.idVirtualApp = vapp.idVirtualApp;
        --
        SELECT vdc.idEnterprise INTO idEnterpriseObj
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = idVirtualDataCenterObj;
        --
        SELECT r.limitResource INTO reservedSize
        FROM rasd_management rm, rasd r
        WHERE rm.idManagement = NEW.idManagement
        AND r.instanceID = rm.idResource;
        --
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVol: ',IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), IFNULL(idVirtualDataCenterObj, 'idVirtualDataCenterObj es NULL'), IFNULL(idDataCenterObj, 'idDataCenterObj es NULL')));
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVol: ',OLD.state, NEW.state, reservedSize));
	-- 
        IF NEW.state != OLD.state THEN
            IF NEW.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed+reservedSize WHERE idDataCenter = idDataCenterObj;
		UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached + 1, extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;     
	-- IF OLD.state = 1 ====> This is done in update_rasd_management_update_stats
        END IF;
    END IF;
    END;
--
--
--
-- ******************************************************************************************
-- Description: 
-- * volCreated, volAttached
-- * Checks new idVM assignments to update publicIPsUsed stats
-- * Checks idVirtualDataCenter unassignments -> publicIPsReserved stats decrease
-- * Updates publicIpsUsed for Enterprise, dc, vdc
-- * Registers Deleted Public IP for Accounting
--
-- Fires: On an UPDATE IGNORE for the rasd_management table
--
-- ******************************************************************************************
|
CREATE TRIGGER kinton.update_rasd_management_update_stats AFTER UPDATE ON kinton.rasd_management
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50) CHARACTER SET utf8;
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE ipAddress VARCHAR(20) CHARACTER SET utf8;
	DECLARE type INTEGER;
	DECLARE currentVMState VARCHAR(50);
	DECLARE extraHDSize BIGINT;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --     
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateRASD: ',idState,' - ', IFNULL(OLD.idVirtualApp, 'OLD.idVirtualApp es NULL'), IFNULL(NEW.idVirtualApp, 'NEW.idVirtualApp es NULL')));	
		-- Detectamos cambios de VDC: V2V
		IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter AND OLD.idVirtualApp = NEW.idVirtualApp THEN
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			IF idState = 1 THEN
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			END IF;
		ELSE 			
		        IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
				-- Volume was changed to another VDC not in a V2V operation (cold move)
		            UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			END IF;
			-- Volume added from a Vapp
			IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    IF idState = 1 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    END IF;                         
			END IF;
			-- Volume removed from a Vapp
			IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			    IF idState = 1 THEN
				SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
				FROM virtualdatacenter vdc
				WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
				SELECT r.limitResource INTO reservedSize
				FROM rasd r
				WHERE r.instanceID = OLD.idResource;
				-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));	
				UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
				UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
				UPDATE IGNORE enterprise_resources_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idEnterprise = idEnterpriseObj;
				UPDATE IGNORE dc_enterprise_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
				UPDATE IGNORE vdc_enterprise_stats 
				    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed - reservedSize
				WHERE idVirtualDataCenter = OLD.idVirtualDatacenter;
			    END IF;                 
			END IF;
			-- Volume added to VDC
			IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN        
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached 
			    IF idState = 1 THEN
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
			    END IF;
			END IF;
			-- Volume removed from VDC
			IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached
			    IF idState = 1 THEN
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;                     
			    END IF;
			END IF;                         
                END IF;
            END IF;
            -- From old autoDetachVolume
            -- UPDATE IGNORE volume_management v set v.state = 0
            -- WHERE v.idManagement = OLD.idManagement;
            -- Checks for used IPs
            IF OLD.idVM IS NULL AND NEW.idVM IS NOT NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        	AND vn.networktype = 'PUBLIC'
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed + 1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
		-- Added ExtraHD for Imported VM
		-- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idEnterpriseObj
                FROM virtualdatacenter vdc
                WHERE vdc.idVirtualDatacenter = NEW.idVirtualDataCenter;
		SELECT vm.state, vm.idType INTO currentVMState, type
		FROM virtualmachine vm
		WHERE vm.idVM = NEW.idVM;
		SELECT IFNULL(r.limitResource,0) * 1048576 INTO extraHDSize
		FROM rasd r
		WHERE NEW.idResourceType=17 AND r.instanceID = NEW.idResource;
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Added ExtraHDs UpdateRASDMana ', IFNULL(extraHDSize,'NULL'), ' for idVM ', IFNULL(NEW.idVM,'NULL'), ' with state ', IFNULL(currentVMState,'NULL'), ' type ', IFNULL(type,'NULL')));  
		IF extraHDSize IS NOT NULL  AND currentVMState = 'ON' THEN -- this is an imported machine
		UPDATE IGNORE enterprise_resources_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter; 
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('INSERTED ExtraHDs stats ', IFNULL(extraHDSize,'NULL'), ' for idEnterpriseObj ', IFNULL(idEnterpriseObj,'NULL'), ' with idDataCenterObj ', IFNULL(idDataCenterObj,'NULL'), ' and NEW.idVirtualDataCenter ', IFNULL(NEW.idVirtualDataCenter,'NULL')));	
		END IF;
            END IF;
            -- Checks for unused IPs
            IF OLD.idVM IS NOT NULL AND NEW.idVM IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
            END IF;
            -- Checks for unreserved IPs
            IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter, ipm.ip INTO idDataCenterObj, ipAddress
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
                AND OLD.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPReserved
                IF idDataCenterObj IS NOT NULL THEN
                    UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    -- Registers Accounting Event
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;                    
                    UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = idDataCenterObj;
                    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                        CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
                    END IF;                    
                END IF;
            END IF;
        END IF;
    END;
--
-- ******************************************************************************************
-- Description: 
--  * Updates volCreated, volAttached
--  * Updates storageUsed for enterprise, cloud, dc, vdc
--  * Register Updated Storage Event for statistics
--
-- Fires: On an UPDATE IGNORE for the rasd_management table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`update_rasd_update_stats` AFTER UPDATE ON `kinton`.`rasd`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE idThisVirtualDataCenter INTEGER;
        DECLARE isReserved INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --
            IF OLD.limitResource != NEW.limitResource THEN
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisVirtualDataCenter, idThisEnterprise
                FROM rasd_management rm, virtualdatacenter vdc
                WHERE rm.idResource = NEW.instanceID
                AND vdc.idVirtualDataCenter=rm.idVirtualDataCenter;
                -- check if this is reserved
                SELECT count(*) INTO isReserved
                FROM volume_management vm, rasd_management rm
                WHERE vm.idManagement  = rm.idManagement
                AND NEW.instanceID = rm.idResource
                AND (vm.state = 1);
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+ NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;                
                IF isReserved != 0 THEN
                -- si hay volAttached se debe actualizar el storageUsed
                    UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed +  NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;                    
                    UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idEnterprise = idThisEnterprise;
                    UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idThisEnterprise;
                    UPDATE IGNORE vdc_enterprise_stats 
                    SET     volCreated = volCreated - 1,
                        extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idVirtualDataCenter = idThisVirtualDataCenter;
                END IF;        
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('UPDATE_STORAGE', NEW.instanceID, NEW.elementName, 0, idThisVirtualDataCenter, idThisEnterprise, NEW.limitResource);
                END IF;
            END IF;
        END IF;
    END;    
-- ******************************************************************************************
-- Description:
--  * When a new IP is created, trigger checks if it belongs to a public VLAN and updates publicIPsTotal Stat
-- It's necessary to update IPs one by one => Slows VLAN creation operation
--
-- Fires: On an INSERT for the ip_pool_management
-- ******************************************************************************************
CREATE TRIGGER kinton.create_ip_pool_management_update_stats AFTER INSERT ON kinton.ip_pool_management
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT dc.idDataCenter INTO idDataCenterObj
	FROM rasd_management rm, vlan_network vn, network_configuration nc, datacenter dc
	WHERE NEW.vlan_network_id = vn.vlan_network_id
	AND vn.networktype = 'PUBLIC'
	AND vn.network_configuration_id = nc.network_configuration_id
	AND dc.network_id = vn.network_id
	AND NEW.idManagement = rm.idManagement;
      IF idDataCenterObj IS NOT NULL THEN
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('create_ip_pool_management_update_stats +1 ', IFNULL(idDataCenterObj,'NULL')));
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal+1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|
-- ******************************************************************************************
-- Description:
--  * When a new IP is deleted from a datacenter at VLAN Creation
--
-- Fires: On an DELETE for the ip_pool_management
-- ******************************************************************************************
CREATE TRIGGER kinton.delete_ip_pool_management_update_stats AFTER DELETE ON kinton.ip_pool_management
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      -- Query for Public Ips deleted (disabled)
      SELECT dc.idDataCenter INTO idDataCenterObj
	FROM rasd_management rm, vlan_network vn, network_configuration nc, datacenter dc
	WHERE OLD.vlan_network_id = vn.vlan_network_id
	AND vn.networktype = 'PUBLIC'
	AND vn.network_configuration_id = nc.network_configuration_id
	AND dc.network_id = vn.network_id
	AND OLD.idManagement = rm.idManagement;
      IF idDataCenterObj IS NOT NULL THEN
    -- detects IP disabled/enabled at Edit Public Ips
   	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('delete_ip_pool_management_update_stats -1 ', IFNULL(idDataCenterObj,'NULL')));
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|
-- ******************************************************************************************
-- Description: 
-- * Registers Created VLAN for Accounting for enterprise, dc, vdc
--
-- Fires: On an INSERT ON `kinton`.`vlan_network`
--
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`create_vlan_network_update_stats` AFTER INSERT ON `kinton`.`vlan_network`
FOR EACH ROW BEGIN
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
    FROM virtualdatacenter vdc, enterprise e
    WHERE vdc.networktypeID=NEW.network_id
    AND vdc.idEnterprise=e.idEnterprise;
    -- Query for Datacenter
    SELECT dc.idDataCenter INTO idDataCenterObj
    FROM datacenter dc
    WHERE dc.network_id = NEW.network_id;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('PROCEDURE AccountingVLANRegisterEvents Activated: ',IFNULL(NEW.vlan_network_id,'NULL'),'-',IFNULL(NEW.network_name,'NULL'),'-',IFNULL(idVirtualDataCenterObj,'NULL'),'-',idEnterpriseObj,'-','CREATE_VLAN','-',now()));
    IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
            CALL AccountingVLANRegisterEvents('CREATE_VLAN',NEW.vlan_network_id, NEW.network_name, idVirtualDataCenterObj,idEnterpriseObj);
        END IF;    
    -- Statistics
    UPDATE IGNORE cloud_usage_stats
        SET     vlanUsed = vlanUsed + 1
        WHERE idDataCenter = -1;
    UPDATE IGNORE enterprise_resources_stats 
        SET     vlanUsed = vlanUsed + 1
        WHERE idEnterprise = idEnterpriseObj;
    UPDATE IGNORE dc_enterprise_stats 
        SET     vlanUsed = vlanUsed + 1
        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
    UPDATE IGNORE vdc_enterprise_stats 
        SET     vlanUsed = vlanUsed + 1
    WHERE idVirtualDataCenter = idVirtualDataCenterObj;
END;
-- ******************************************************************************************
-- Description: 
-- * Registers Deleted VLAN for Accounting for enterprise, dc, vdc
--
-- Fires: On an DELETE ON `kinton`.`vlan_network`
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`delete_vlan_network_update_stats` AFTER DELETE ON `kinton`.`vlan_network`
FOR EACH ROW
BEGIN
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- Query for Datacenter
            SELECT dc.idDataCenter INTO idDataCenterObj
            FROM datacenter dc
            WHERE dc.network_id = OLD.network_id;
            -- Deleted PublicIps are deteceted in network_configuration
            -- VLAN Accounting  
            SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
            FROM virtualdatacenter vdc, enterprise e
            WHERE vdc.networktypeID=OLD.network_id
            AND vdc.idEnterprise=e.idEnterprise;
            IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
                CALL AccountingVLANRegisterEvents('DELETE_VLAN',OLD.vlan_network_id, OLD.network_name, idVirtualDataCenterObj,idEnterpriseObj);
            END IF;
            -- Statistics
            UPDATE IGNORE cloud_usage_stats
                SET     vlanUsed = vlanUsed - 1
                WHERE idDataCenter = -1;
            UPDATE IGNORE enterprise_resources_stats 
                SET     vlanUsed = vlanUsed - 1
                WHERE idEnterprise = idEnterpriseObj;
            UPDATE IGNORE dc_enterprise_stats 
                SET     vlanUsed = vlanUsed - 1
                WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
            UPDATE IGNORE vdc_enterprise_stats 
                SET     vlanUsed = vlanUsed - 1
            WHERE idVirtualDataCenter = idVirtualDataCenterObj;
        END IF;
END;
|
--
-- ******************************************************************************************
-- Description: 
--  * Checks for new Reserved or unreserved IPs
--  * Reserved IPs have a valid 'mac' address; trigger checks this field to increase publicIPsReserved Stat
--  * Registers Created Public IP for Accounting
--
-- Fires: On an UPDATE for the ip_pool_management
-- ******************************************************************************************
CREATE TRIGGER kinton.update_ip_pool_management_update_stats AFTER UPDATE ON kinton.ip_pool_management
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
	   DECLARE networkTypeObj VARCHAR(15);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
		SELECT vn.networktype, dc.idDataCenter INTO networkTypeObj, idDataCenterObj
		FROM vlan_network vn, datacenter dc
		WHERE dc.network_id = vn.network_id
		AND OLD.vlan_network_id = vn.vlan_network_id;
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('update_ip_pool_management_update_stats', '-', OLD.ip, '-',OLD.available,'-', NEW.available,'-', IFNULL(networkTypeObj,'NULL'), '-', IFNULL(idDataCenterObj,'NULL')));
		IF networkTypeObj = 'PUBLIC' THEN		
			IF OLD.available=FALSE AND NEW.available=TRUE THEN
				UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal+1 WHERE idDataCenter = idDataCenterObj;
			END IF;
			IF OLD.available=TRUE AND NEW.available=FALSE THEN
				UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
			END IF;
		END IF;
	    -- Checks for public available 
            -- Checks for reserved IPs		
            IF OLD.mac IS NULL AND NEW.mac IS NOT NULL THEN
                -- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise  INTO idDataCenterObj, idVirtualDataCenterObj, idEnterpriseObj
                FROM rasd_management rm, virtualdatacenter vdc, vlan_network vn
                WHERE vdc.idVirtualDataCenter = rm.idVirtualDataCenter
		AND NEW.vlan_network_id = vn.vlan_network_id
		AND vn.networktype = 'PUBLIC'
		AND NEW.idManagement = rm.idManagement;
                -- New Public IP assignment for a VDC ---> Reserved
                UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed+1 WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idDataCenter = idDataCenterObj;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                    CALL AccountingIPsRegisterEvents('IP_RESERVED',NEW.idManagement,NEW.ip,idVirtualDataCenterObj, idEnterpriseObj);
                END IF;
            END IF;
        END IF;
    END;
|
-- ******************************************************************************************
-- Description: 
--  * Registers new limits created for datacenter by enterprise, so they show in statistics
--
-- Fires: On an INSERT for the enterprise_limits_by_datacenter
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`dclimit_created` AFTER INSERT ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                   
        		 IF (NEW.idEnterprise != 0 AND NEW.idDataCenter != 0) THEN
        INSERT IGNORE INTO dc_enterprise_stats 
                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
            VALUES 
                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);
                END IF;
            -- cloud_usage_stats
            UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved + NEW.cpuHard,
                    vMemoryReserved = vMemoryReserved + NEW.ramHard,
                    vStorageReserved = vStorageReserved + NEW.hdHard,
                    storageReserved = storageReserved + NEW.storageHard,
                    publicIPsReserved = publicIPsReserved + NEW.publicIPHard,
                    vlanReserved = vlanReserved + NEW.vlanHard
                WHERE idDataCenter = NEW.idDataCenter;
        END IF;
    END;
|
-- ******************************************************************************************
-- Description: 
--  * Registers changes in limits created for datacenter by enterprise, so they show in statistics
--
-- Fires: On an UPDATE for the enterprise_limits_by_datacenter
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`dclimit_updated` AFTER UPDATE ON `kinton`.`enterprise_limits_by_datacenter`
FOR EACH ROW BEGIN     
	 IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
                -- Limit is not used anymore. Statistics are removed
                DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;
                IF (NEW.idEnterprise != 0 AND NEW.idDataCenter != 0) THEN
                INSERT IGNORE INTO dc_enterprise_stats 
	                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
	                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
	            	VALUES 
	                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
	                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);       
                END IF;
		-- 
                UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                    vMemoryReserved = vMemoryReserved - OLD.ramHard + NEW.ramHard,
                    vStorageReserved = vStorageReserved - OLD.hdHard + NEW.hdHard,
                    storageReserved = storageReserved - OLD.storageHard + NEW.storageHard,
                    publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                    vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
                WHERE idDataCenter = NEW.idDataCenter;                            
        END IF;
END;
|
-- ******************************************************************************************
-- Description: 
--  * Removes statistics for the limit
--
-- Fires: On an UPDATE for the enterprise_limits_by_datacenter
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`dclimit_deleted` AFTER DELETE ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN
        DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;
        UPDATE IGNORE cloud_usage_stats 
        SET vCpuReserved = vCpuReserved - OLD.cpuHard,
            vMemoryReserved = vMemoryReserved - OLD.ramHard,
            vStorageReserved = vStorageReserved - OLD.hdHard,
            storageReserved = storageReserved - OLD.storageHard,
            -- repositoryReserved = repositoryReserved - OLD.repositoryHard + NEW.repositoryHard,
            publicIPsReserved = publicIPsReserved - OLD.publicIPHard,
            vlanReserved = vlanReserved - OLD.vlanHard
        WHERE idDataCenter = OLD.idDataCenter;
    END;
|
-- ******************************************************************************************
--
--  Checks statistics miscalculations and corrects them to zero
--
-- ****************************************************************************************
DROP TRIGGER IF EXISTS `kinton`.`cloud_usage_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_resources_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`vapp_enterprise_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`vdc_enterprise_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`dc_enterprise_stats_negative_check`;

CREATE TRIGGER `kinton`.`cloud_usage_stats_negative_check` BEFORE UPDATE ON `kinton`.`cloud_usage_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.serversTotal > 16700000)  THEN SET NEW.serversTotal = 0; END IF;
      IF (NEW.serversRunning > 16700000)  THEN SET NEW.serversRunning = 0; END IF;
      IF (NEW.storageTotal > 18446000000000000000)  THEN SET NEW.storageTotal = 0; END IF;
      IF (NEW.storageReserved > 18446000000000000000) THEN SET NEW.storageReserved = 0; END IF;
      IF (NEW.storageUsed > 18446000000000000000)  THEN SET NEW.storageUsed = 0; END IF;
      IF (NEW.publicIPsTotal > 16700000)  THEN SET NEW.publicIPsTotal = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vMachinesTotal > 16700000)  THEN SET NEW.vMachinesTotal = 0; END IF;
      IF (NEW.vMachinesRunning > 16700000)  THEN SET NEW.vMachinesRunning = 0; END IF;
      IF (NEW.vCpuTotal > 18446000000000000000)  THEN SET NEW.vCpuTotal = 0; END IF;
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.vMemoryTotal > 18446000000000000000)  THEN SET NEW.vMemoryTotal = 0; END IF;
      IF (NEW.vMemoryReserved > 18446000000000000000)  THEN SET NEW.vMemoryReserved = 0; END IF;
      IF (NEW.vMemoryUsed > 18446000000000000000)  THEN SET NEW.vMemoryUsed = 0; END IF;
      IF (NEW.vStorageTotal > 18446000000000000000)  THEN SET NEW.vStorageTotal = 0; END IF;
      IF (NEW.vStorageReserved > 18446000000000000000)  THEN SET NEW.vStorageReserved = 0; END IF;
      IF (NEW.vStorageUsed > 18446000000000000000)  THEN SET NEW.vStorageUsed = 0; END IF;
      IF (NEW.numUsersCreated > 16700000)  THEN SET NEW.numUsersCreated = 0; END IF;
      IF (NEW.numVDCCreated > 16700000)  THEN SET NEW.numVDCCreated = 0; END IF;
      IF (NEW.numEnterprisesCreated > 16700000)  THEN SET NEW.numEnterprisesCreated = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;


CREATE TRIGGER `kinton`.`enterprise_resources_stats_negative_check` BEFORE UPDATE ON `kinton`.`enterprise_resources_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.repositoryReserved > 18446000000000000000)  THEN SET NEW.repositoryReserved = 0; END IF;
      IF (NEW.repositoryUsed > 18446000000000000000)  THEN SET NEW.repositoryUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;

CREATE TRIGGER `kinton`.`dc_enterprise_stats_negative_check` BEFORE UPDATE ON `kinton`.`dc_enterprise_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.repositoryReserved > 18446000000000000000)  THEN SET NEW.repositoryReserved = 0; END IF;
      IF (NEW.repositoryUsed > 18446000000000000000)  THEN SET NEW.repositoryUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;


CREATE TRIGGER `kinton`.`vapp_enterprise_stats_negative_check` BEFORE UPDATE ON `kinton`.`vapp_enterprise_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vmCreated > 16700000)  THEN SET NEW.vmCreated = 0; END IF;
      IF (NEW.vmActive > 16700000)  THEN SET NEW.vmActive = 0; END IF;
      IF (NEW.volAssociated > 16700000)  THEN SET NEW.volAssociated = 0; END IF;
      IF (NEW.volAttached > 16700000)  THEN SET NEW.volAttached = 0; END IF;
    END IF;
  END;



CREATE TRIGGER `kinton`.`vdc_enterprise_stats_negative_check` BEFORE UPDATE ON `kinton`.`vdc_enterprise_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vmCreated > 16700000)  THEN SET NEW.vmCreated = 0; END IF;
      IF (NEW.vmActive > 16700000)  THEN SET NEW.vmActive = 0; END IF;
      IF (NEW.volCreated > 16700000)  THEN SET NEW.volCreated = 0; END IF;
      IF (NEW.volAssociated > 16700000)  THEN SET NEW.volAssociated = 0; END IF;
      IF (NEW.volAttached > 16700000)  THEN SET NEW.volAttached = 0; END IF;
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;                                 
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;
|
--


-- ******************************************************************************************
--
--  Stats Sanity: Procedures to recalculate stats on demand
--
-- ****************************************************************************************

DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;
|
--
--
--
CREATE PROCEDURE `kinton`.CalculateCloudUsageStats()
   BEGIN
  DECLARE idDataCenterObj INTEGER;
  DECLARE serversTotal BIGINT UNSIGNED;
  DECLARE serversRunning BIGINT UNSIGNED;
  DECLARE storageTotal BIGINT UNSIGNED;
  DECLARE storageUsed BIGINT UNSIGNED;
  DECLARE publicIPsTotal BIGINT UNSIGNED;
  DECLARE publicIPsReserved BIGINT UNSIGNED;
  DECLARE publicIPsUsed BIGINT UNSIGNED;
  DECLARE vMachinesTotal BIGINT UNSIGNED;
  DECLARE vMachinesRunning BIGINT UNSIGNED;
  DECLARE vCpuTotal BIGINT UNSIGNED;
  DECLARE vCpuReserved BIGINT UNSIGNED;
  DECLARE vCpuUsed BIGINT UNSIGNED;
  DECLARE vMemoryTotal BIGINT UNSIGNED;
  DECLARE vMemoryReserved BIGINT UNSIGNED;
  DECLARE vMemoryUsed BIGINT UNSIGNED;
  DECLARE vStorageReserved BIGINT UNSIGNED;
  DECLARE vStorageUsed BIGINT UNSIGNED;
  DECLARE vStorageTotal BIGINT UNSIGNED;
  DECLARE numUsersCreated BIGINT UNSIGNED;
  DECLARE numVDCCreated BIGINT UNSIGNED;
  DECLARE numEnterprisesCreated BIGINT UNSIGNED;
  DECLARE storageReserved BIGINT UNSIGNED; 
  DECLARE vlanReserved BIGINT UNSIGNED; 
  DECLARE vlanUsed BIGINT UNSIGNED; 

  DECLARE no_more_dcs INTEGER;

  DECLARE curDC CURSOR FOR SELECT idDataCenter FROM datacenter;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_dcs=1;

  SET no_more_dcs=0;
  SET idDataCenterObj = -1;

  OPEN curDC;

  TRUNCATE cloud_usage_stats;

  dept_loop:WHILE(no_more_dcs=0) DO
    FETCH curDC INTO idDataCenterObj;
    IF no_more_dcs=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO serversTotal
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState!=2;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO serversRunning
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState=3;
    --
    SELECT IF (SUM(limitResource) IS NULL, 0, SUM(limitResource))   INTO storageTotal
    FROM rasd r, rasd_management rm, virtualdatacenter vdc
    WHERE rm.idResource = r.instanceID
    AND vdc.idVirtualDataCenter=rm.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO storageUsed
    FROM storage_pool sp, storage_device sd, volume_management vm, rasd_management rm, rasd r
    WHERE vm.idStorage = sp.idStorage
    AND sp.idStorageDevice = sd.id
    AND vm.idManagement = rm.idManagement
    AND r.instanceID = rm.idResource
    AND rm.idResourceType = 8
    AND (vm.state = 1)
    AND sd.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsTotal
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND vn.networktype = 'PUBLIC'             
    AND ipm.mac IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND vn.networktype = 'PUBLIC'             
    AND rm.idManagement = ipm.idManagement
    AND ipm.mac IS NOT NULL
    AND rm.idVM IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesTotal
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN'
    and v.idType = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesRunning
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state = 'ON'
    and v.idType = 1;
    --
    SELECT IF (SUM(cpu) IS NULL,0,SUM(cpu)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)) INTO vCpuTotal, vMemoryTotal, vCpuUsed, vMemoryUsed
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState = 3; 
    --
    CALL get_datastore_size_by_dc(idDataCenterObj,vStorageTotal);
    CALL get_datastore_used_size_by_dc(idDataCenterObj,vStorageUsed);
    --
    SELECT IF (SUM(vlanHard) IS NULL, 0, SUM(vlanHard))  INTO vlanReserved
    FROM enterprise_limits_by_datacenter 
    WHERE idDataCenter = idDataCenterObj AND idEnterprise IS NOT NULL;

    -- Inserts stats row
    INSERT INTO cloud_usage_stats
    (idDataCenter,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,vCpuReserved,vCpuUsed,
    vMemoryTotal,vMemoryReserved,vMemoryUsed,
    vStorageReserved,vStorageUsed,vStorageTotal,
    vlanReserved,
    numUsersCreated,numVDCCreated,numEnterprisesCreated)
    VALUES
    (idDataCenterObj,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,0,vCpuUsed,
    vMemoryTotal,0,vMemoryUsed,
    0,vStorageUsed,vStorageTotal,
    vlanReserved,
    0,0,0);

  END WHILE dept_loop;
  CLOSE curDC;

  -- All Cloud Stats (idDataCenter -1): vCpuReserved, VMemoryReserved, VStorageReserved, NumUsersCreated, NumVDCCreated, NumEnterprisesCreated
  SELECT IF (SUM(cpuHard) IS NULL,0,SUM(cpuHard)), IF (SUM(ramHard) IS NULL,0,SUM(ramHard)), IF (SUM(hdHard) IS NULL,0,SUM(hdHard)), IF (SUM(storageHard) IS NULL,0,SUM(storageHard)) INTO vCpuReserved, vMemoryReserved, vStorageReserved, storageReserved
  FROM enterprise e;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numUsersCreated
  FROM user;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numVDCCreated
  FROM virtualdatacenter vdc;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numEnterprisesCreated
  FROM enterprise e;
  --
  SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
  FROM vlan_network;

  -- Inserts stats row
    INSERT INTO cloud_usage_stats
    (idDataCenter,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,vCpuReserved,vCpuUsed,
    vMemoryTotal,vMemoryReserved,vMemoryUsed,
    vStorageReserved,vStorageUsed,vStorageTotal,
    vlanUsed,
    numUsersCreated,numVDCCreated,numEnterprisesCreated)
    VALUES
    (-1,
    0,0,
    0,0,
    0,0,0,
    0,0,
    0,vCpuReserved,0,
    0,vMemoryReserved,0,
    vStorageReserved,0,0,
    vlanUsed,
    numUsersCreated,numVDCCreated,numEnterprisesCreated);
   END;

|
--
--
--
CREATE PROCEDURE `kinton`.CalculateEnterpriseResourcesStats()
   BEGIN
  DECLARE idEnterpriseObj INTEGER;
  DECLARE vCpuReserved BIGINT UNSIGNED;
  DECLARE vCpuUsed BIGINT UNSIGNED;
  DECLARE memoryReserved BIGINT UNSIGNED;
  DECLARE memoryUsed BIGINT UNSIGNED;
  DECLARE localStorageReserved BIGINT UNSIGNED;
  DECLARE localStorageUsed BIGINT UNSIGNED;
  DECLARE extStorageReserved BIGINT UNSIGNED; 
  DECLARE extStorageUsed BIGINT UNSIGNED; 
  DECLARE publicIPsReserved BIGINT UNSIGNED;
  DECLARE publicIPsUsed BIGINT UNSIGNED;
  DECLARE vlanReserved BIGINT UNSIGNED; 
  DECLARE vlanUsed BIGINT UNSIGNED; 
  -- DECLARE repositoryReserved BIGINT UNSIGNED; -- TBD
  -- DECLARE repositoryUsed BIGINT UNSIGNED; -- TBD

  DECLARE no_more_enterprises INTEGER;

  DECLARE curDC CURSOR FOR SELECT idEnterprise FROM enterprise;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_enterprises=1;

  SET no_more_enterprises = 0;
  SET idEnterpriseObj = -1;

  OPEN curDC;

  TRUNCATE enterprise_resources_stats;

  dept_loop:WHILE(no_more_enterprises = 0) DO
    FETCH curDC INTO idEnterpriseObj;
    IF no_more_enterprises=1 THEN
        LEAVE dept_loop;
    END IF;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Iteracion Enterprise: ',idEnterpriseObj));
    --
    SELECT cpuHard, ramHard, hdHard, storageHard, vlanHard INTO vCpuReserved, memoryReserved, localStorageReserved, extStorageReserved, vlanReserved
    FROM enterprise e
    WHERE e.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (SUM(vm.cpu) IS NULL, 0, SUM(vm.cpu)), IF (SUM(vm.ram) IS NULL, 0, SUM(vm.ram)), IF (SUM(vm.hd) IS NULL, 0, SUM(vm.hd)) INTO vCpuUsed, memoryUsed, localStorageUsed
    FROM virtualmachine vm
    WHERE vm.state = 'ON'
    AND vm.idType = 1
    AND vm.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
    FROM virtualdatacenter vdc, vlan_network vn
    WHERE vdc.networktypeID=vn.network_id
    AND vdc.idEnterprise=idEnterpriseObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO extStorageUsed
    FROM rasd_management rm, rasd r, volume_management vm, virtualdatacenter vdc
    WHERE rm.idManagement = vm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND r.instanceID = rm.idResource
    AND (vm.state = 1)
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id   
    AND vn.networktype = 'PUBLIC'             
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id            
    AND vn.networktype = 'PUBLIC'    
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND rm.idVM IS NOT NULL
    AND vdc.idEnterprise = idEnterpriseObj;


    -- Inserts stats row
    INSERT INTO enterprise_resources_stats (idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed)
     VALUES (idEnterpriseObj,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed);

  END WHILE dept_loop;
  CLOSE curDC;

   END;
|
--
--
--
CREATE PROCEDURE `kinton`.CalculateVdcEnterpriseStats()
   BEGIN
  DECLARE idVirtualDataCenterObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE vdcName VARCHAR(45) CHARACTER SET utf8;
  DECLARE vmCreated MEDIUMINT UNSIGNED;
  DECLARE vmActive MEDIUMINT UNSIGNED;
  DECLARE volCreated MEDIUMINT UNSIGNED;
  DECLARE volAssociated MEDIUMINT UNSIGNED;
  DECLARE volAttached MEDIUMINT UNSIGNED;
  DECLARE vCpuReserved BIGINT UNSIGNED; 
  DECLARE vCpuUsed BIGINT UNSIGNED; 
  DECLARE memoryReserved BIGINT UNSIGNED;
  DECLARE memoryUsed BIGINT UNSIGNED; 
  DECLARE localStorageReserved BIGINT UNSIGNED; 
  DECLARE localStorageUsed BIGINT UNSIGNED; 
  DECLARE extStorageReserved BIGINT UNSIGNED; 
  DECLARE extStorageUsed BIGINT UNSIGNED; 
  DECLARE publicIPsReserved MEDIUMINT UNSIGNED;
  DECLARE publicIPsUsed MEDIUMINT UNSIGNED;
  DECLARE vlanReserved MEDIUMINT UNSIGNED; 
  DECLARE vlanUsed MEDIUMINT UNSIGNED; 

  DECLARE no_more_vdcs INTEGER;

  DECLARE curDC CURSOR FOR SELECT vdc.idVirtualDataCenter, vdc.idEnterprise, vdc.name FROM virtualdatacenter vdc;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vdcs = 1;

  SET no_more_vdcs = 0;
  SET idVirtualDataCenterObj = -1;

  OPEN curDC;

  TRUNCATE vdc_enterprise_stats;

  dept_loop:WHILE(no_more_vdcs = 0) DO
    FETCH curDC INTO idVirtualDataCenterObj, idEnterprise, vdcName;
    IF no_more_vdcs=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmCreated
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN';
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state = 'ON';
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volCreated
    FROM rasd_management rm
    WHERE rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAssociated
    FROM rasd_management rm
    WHERE rm.idVirtualApp IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAttached
    FROM volume_management vm, rasd_management rm
    WHERE rm.idManagement = vm.idManagement
    AND rm.idVirtualApp IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND state = 1;
    --
    SELECT IF (SUM(cpuHard) IS NULL, 0, SUM(cpuHard)), IF (SUM(ramHard) IS NULL, 0, SUM(ramHard)), IF (SUM(hdHard) IS NULL, 0, SUM(hdHard)), IF (SUM(storageHard) IS NULL, 0, SUM(storageHard)), IF (SUM(vlanHard) IS NULL, 0, SUM(vlanHard)) INTO vCpuReserved, memoryReserved, localStorageReserved, extStorageReserved, vlanReserved
    FROM virtualdatacenter 
    WHERE idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (SUM(vm.cpu) IS NULL, 0, SUM(vm.cpu)), IF (SUM(vm.ram) IS NULL, 0, SUM(vm.ram)), IF (SUM(vm.hd) IS NULL, 0, SUM(vm.hd)) INTO vCpuUsed, memoryUsed, localStorageUsed
    FROM virtualmachine vm, nodevirtualimage nvi, node n, virtualapp vapp
    WHERE vm.idVM = nvi.idVM
    AND nvi.idNode = n.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vm.state = 'ON'
    AND vm.idType = 1
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO extStorageUsed
    FROM rasd_management rm, rasd r, volume_management vm
    WHERE rm.idManagement = vm.idManagement    
    AND r.instanceID = rm.idResource
    AND (vm.state = 1)
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id           
    AND vn.networktype = 'PUBLIC'     
    AND rm.idManagement = ipm.idManagement
    AND rm.idVM IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
    AND vn.networktype = 'PUBLIC'
    AND rm.idManagement = ipm.idManagement
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
    FROM virtualdatacenter vdc, vlan_network vn
    WHERE vdc.networktypeID = vn.network_id
    AND vdc.idVirtualDataCenter = idVirtualDataCenterObj;
   -- 


    -- Inserts stats row
    INSERT INTO vdc_enterprise_stats (idVirtualDataCenter,idEnterprise,vdcName,vmCreated,vmActive,volCreated,volAssociated,volAttached, vCpuReserved, vCpuUsed, memoryReserved, memoryUsed, localStorageReserved, localStorageUsed, extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed)
    VALUES (idVirtualDataCenterObj,idEnterprise,vdcName,vmCreated,vmActive,volCreated,volAssociated,volAttached, vCpuReserved, vCpuUsed, memoryReserved, memoryUsed, localStorageReserved, localStorageUsed, extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed );


  END WHILE dept_loop;
  CLOSE curDC;

   END;
|
--
-- To be DONE when showing Datacenter Stats by Enterprise
-- CREATE PROCEDURE `kinton`.CalculateDcEnterpriseStats()
--   BEGIN
--   END;
--
--
CREATE PROCEDURE `kinton`.CalculateVappEnterpriseStats()
   BEGIN
  DECLARE idVirtualAppObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE idVirtualDataCenter INTEGER;
  DECLARE vappName VARCHAR(45) CHARACTER SET utf8;
  DECLARE vdcName VARCHAR(45) CHARACTER SET utf8;
  DECLARE vmCreated MEDIUMINT UNSIGNED;
  DECLARE vmActive MEDIUMINT UNSIGNED;
  DECLARE volAssociated MEDIUMINT UNSIGNED;
  DECLARE volAttached MEDIUMINT UNSIGNED;

  DECLARE no_more_vapps INTEGER;

  DECLARE curDC CURSOR FOR SELECT vapp.idVirtualApp, vapp.idEnterprise, vapp.idVirtualDataCenter, vapp.name, vdc.name FROM virtualapp vapp, virtualdatacenter vdc WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vapps = 1;

  SET no_more_vapps = 0;
  SET idVirtualAppObj = -1;

  OPEN curDC;

  TRUNCATE vapp_enterprise_stats;

  dept_loop:WHILE(no_more_vapps = 0) DO
    FETCH curDC INTO idVirtualAppObj, idEnterprise, idVirtualDataCenter, vappName, vdcName;
    IF no_more_vapps=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmCreated
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN'
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state = 'ON'
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAssociated
    FROM rasd_management rm
    WHERE rm.idVirtualApp = idVirtualAppObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAttached
    FROM volume_management vm, rasd_management rm
    WHERE rm.idManagement = vm.idManagement
    AND rm.idVirtualApp = idVirtualAppObj
    AND state = 1;

    -- Inserts stats row
    INSERT INTO vapp_enterprise_stats (idVirtualApp,idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached)
    VALUES (idVirtualAppObj, idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached);


  END WHILE dept_loop;
  CLOSE curDC;

   END;

|
--
DELIMITER ;


-- ******************************************************************************************
--
--  Procedures to calculate datastore size
--
-- ****************************************************************************************
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size_by_dc`;

DELIMITER |
--
CREATE PROCEDURE `kinton`.`get_datastore_size_by_dc`(IN idDC INT, OUT size BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.size) IS NULL,0,SUM(ds_view.size)) INTO size
    FROM (SELECT d.size as size FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size_by_dc`(IN idDC INT, OUT usedSize BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.usedSize) IS NULL,0,SUM(ds_view.usedSize)) INTO usedSize
    FROM (SELECT d.usedSize as usedSize FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END
--
|
--
DELIMITER ;


--
-- STATISTICS INITIALIZATION (uncomment for Default datacenter (id=1))
--
INSERT IGNORE INTO enterprise_resources_stats (idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed) VALUES (1 ,0,0,0,0,0,0);
--
UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated+1 WHERE idDataCenter = -1;
UPDATE IGNORE cloud_usage_stats SET numUsersCreated = numUsersCreated+2 WHERE idDataCenter = -1;
-- myLocalMachine


--
-- Checks ALL Tables in DB and adds the 'version_c' column required for Hibernate Persistence 
--

DROP PROCEDURE IF EXISTS `kinton`.`add_version_column_to_all`;

DELIMITER |
CREATE PROCEDURE `kinton`.`add_version_column_to_all`()
BEGIN
    DECLARE currentTableName VARCHAR(64) CHARACTER SET utf8;
    DECLARE no_more_tables INTEGER;
    DECLARE curAllTables CURSOR FOR SELECT TABLE_NAME from `information_schema`.TABLES where TABLE_SCHEMA = "kinton" AND TABLE_TYPE="BASE TABLE";
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_tables=1;
    SET no_more_tables = 0;
    OPEN curAllTables;  
    dept_loop:WHILE(no_more_tables = 0) DO
        FETCH curAllTables INTO currentTableName;
        IF no_more_tables = 1 THEN
           LEAVE dept_loop;
        END IF;
        IF NOT EXISTS( (SELECT * FROM `information_schema`.COLUMNS WHERE TABLE_SCHEMA= "kinton" AND TABLE_NAME=currentTableName AND COLUMN_NAME='version_c') ) THEN
            SET @alter_sql=CONCAT('ALTER IGNORE TABLE ', currentTableName,' ADD COLUMN version_c int default 0;');
            PREPARE stmt from @alter_sql;
            EXECUTE stmt;
        END IF;
    END WHILE dept_loop;
    CLOSE curAllTables;
END;
|
DELIMITER ;

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
  `idCostCode` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
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
  `memoryGB` DECIMAL(20,5) NOT NULL default 0,
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
  PRIMARY KEY (`idPricingCostCode`),
  KEY `pricingCostCode_FK1` (`idCostCode`),
  KEY `pricingCostCode_FK2` (`idPricingTemplate`),
  CONSTRAINT `pricingCostCode_FK1` FOREIGN KEY (`idCostCode`) REFERENCES `costCode` (`idCostCode`) ON DELETE CASCADE,
  CONSTRAINT `pricingCostCode_FK2` FOREIGN KEY (`idPricingTemplate`) REFERENCES `pricingTemplate` (`idPricingTemplate`) ON DELETE CASCADE
  )ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;  
  
 
--
-- Table `kinton`.`costCodeCurrency`
-- 

CREATE TABLE  `kinton`.`costCodeCurrency` (
  `idCostCodeCurrency` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idCostCode` int(10) unsigned,
  `idCurrency` int(10) unsigned,
  `price` DECIMAL(20,5) NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`idCostCodeCurrency`),
  KEY `idCostCode_FK` (`idCostCode`),
  KEY `idCurrency_FK`  (`idCurrency`),
  CONSTRAINT `idCostCode_FK` FOREIGN KEY (`idCostCode`) REFERENCES `kinton`.`costCode` (`idCostCode`) ON DELETE CASCADE,
  CONSTRAINT `idCurrency_FK` FOREIGN KEY (`idCurrency`) REFERENCES `kinton`.`currency` (`idCurrency`) ON DELETE CASCADE
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
  PRIMARY KEY (`idPricingTier`),
  KEY `pricingTier_FK1` (`idTier`),
  KEY `pricingTier_FK2`  (`idPricingTemplate`),
  CONSTRAINT `pricingTier_FK1` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON DELETE CASCADE,
  CONSTRAINT `pricingTier_FK2` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`) ON DELETE CASCADE
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;    
  
-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricingTemplate` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`);



CALL `kinton`.`add_version_column_to_all`();
