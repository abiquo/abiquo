-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures

-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --

DROP TABLE IF EXISTS `kinton`.`approval`;
DROP TABLE IF EXISTS `kinton`.`approval_manager`;


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

-- APPROVAL --
-- Definition of table `kinton`.`approval`
CREATE TABLE `kinton`.`approval` (
  `idApproval` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `token` varchar(255) NOT NULL ,
  `approvalType` varchar(255) NOT NULL ,
  `status` varchar(255) NOT NULL ,
  `timeRequested` timestamp NOT NULL ,
  `timeResponse` timestamp NOT NULL ,
  `reason` text DEFAULT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idApproval`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- APPROVAL --
-- Definition of table `kinton`.`approval_manager`
CREATE TABLE `kinton`.`approval_manager` (
  `idApprovalManager` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `idEnterprise` int(10) UNSIGNED,
  `idUser` int(10) UNSIGNED,
  `approvalMail` varchar(255) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idApprovalManager`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`virtualapp` ADD COLUMN `idApproval` int(11) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `virtualAppliance_FK7` FOREIGN KEY (`idApproval`) REFERENCES `approval` (`idApproval`);


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (49,'APPROVAL_MANAGE',0),
 (50,'APPROVAL_REQUIRED',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;


/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,49,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;

 
-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --