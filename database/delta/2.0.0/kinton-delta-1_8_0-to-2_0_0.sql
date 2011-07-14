-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures

-- PRICING --

-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricing` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricing`) REFERENCES `kinton`.`pricing` (`idPricing`);

-- DROP THE TABLES RELATED TO PRICING --
DROP TABLE IF EXISTS `kinton`.`pricing`;
DROP TABLE IF EXISTS `kinton`.`costCode`;
DROP TABLE IF EXISTS `kinton`.`pricing_costcode`;
DROP TABLE IF EXISTS `kinton`.`pricing_tier`;
DROP TABLE IF EXISTS `kinton`.`currency`;

--
-- Definition of table `kinton`.`currency`
--

CREATE TABLE `kinton`.`currency` (
  `idCurrency` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `symbol` varchar(256) NOT NULL ,
  `name` varchar(256) NOT NULL ,
  `blocked` boolean default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCurrency`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`costCode`
--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE_PRICING',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,49,0),(1,50,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;


/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.pricing.createCurrency","","Currency creation wiki");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;
--  
  

CREATE TABLE `kinton`.`costCode` (
  `idCostCode` INT(10) NOT NULL AUTO_INCREMENT ,
  `variable` varchar(256) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCostCode`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`pricing`
--
  

CREATE TABLE `kinton`.`pricing` (
  `idPricing` INT(10) NOT NULL AUTO_INCREMENT ,
  `idEnterprise` INT(10) UNSIGNED NOT NULL ,
  `idCurrency` INT(10) UNSIGNED NOT NULL ,
  `chargingPeriod`  INT(10) UNSIGNED NOT NULL ,
  `minimumCharge` INT(10) UNSIGNED NOT NULL ,
  `showChangesBefore` boolean NOT NULL default 0,
  `showMinimumCharge` boolean NOT NULL default 0,
  `limitMaximumDeployedCharged` INT(10) UNSIGNED NOT NULL ,
  `standingChargePeriod` INT(10) UNSIGNED NOT NULL ,
  `minimumChargePeriod` INT(10) UNSIGNED NOT NULL ,
  `vCPU` INT(10) UNSIGNED NOT NULL ,
  `memoryMb` INT(10) UNSIGNED NOT NULL ,
  `hdGB` INT(10) UNSIGNED NOT NULL ,
  `vlan` varchar(256) NOT NULL ,
  `publicIp` varchar(256) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricing`) ,
  KEY `Pricing_FK1_Enterprise` (`idEnterprise`),
  KEY `Pricing_FK2_Currency` (`idCurrency`),
  CONSTRAINT `Pricing_FK1_Enterprise` FOREIGN KEY (`idEnterprise` ) REFERENCES `kinton`.`enterprise` (`idEnterprise` ) ON DELETE NO ACTION,
  CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency` ) REFERENCES `kinton`.`currency` (`idCurrency` ) ON DELETE NO ACTION
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
  

--
-- Definition of table `kinton`.`pricing_costCode`
--  
  
CREATE TABLE `kinton`.`pricing_costcode` (
  `idPricing` INT(10) UNSIGNED NOT NULL,
  `idCostCode` INT(10) UNSIGNED NOT NULL,
  `price` INT(10) UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricing`, `idCostCode`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;  
  
  
--
-- Definition of table `kinton`.`pricing_tier`
--  

CREATE TABLE `kinton`.`pricing_tier` (
  `idPricing` INT(10) UNSIGNED NOT NULL,
  `idTier` INT(10) UNSIGNED NOT NULL,
  `price` INT(10) UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricing`, `idTier`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;    
  
