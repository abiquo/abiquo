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


-- TARANTINO --
-- Previous state Tracking for VirtualMachines --
DROP TABLE IF EXISTS `kinton`.`virtualmachinetrackedstate`;
-- TARANTINO --

-- PRICING --
-- DROP THE TABLES RELATED TO PRICING --

DROP TABLE IF EXISTS `kinton`.`pricingTemplate`;
DROP TABLE IF EXISTS `kinton`.`pricingCostCode`;
DROP TABLE IF EXISTS `kinton`.`costCode`;
DROP TABLE IF EXISTS `kinton`.`pricingTier`;
DROP TABLE IF EXISTS `kinton`.`costCodeCurrency`;
DROP TABLE IF EXISTS `kinton`.`currency`;

ALTER TABLE `kinton`.`ip_pool_management` DROP FOREIGN KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP COLUMN dhcp_service_id;
ALTER TABLE `kinton`.`network_configuration` DROP FOREIGN KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP COLUMN `dhcp_service_id`;

DROP TABLE IF EXISTS `kinton`.`dhcp_service`;


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

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

-- TARANTINO --
-- Definition of table `kinton`.`virtualmachinetrackedstate`
CREATE TABLE  `kinton`.`virtualmachinetrackedstate` (
  `idVM` int(10) unsigned NOT NULL,
  `previousState` varchar(50) NOT NULL,
  PRIMARY KEY  (`idVM`),
  KEY `VirtualMachineTrackedState_FK1` (`idVM`),
  CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
  )
 ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- TARANTINO --

CREATE TABLE  `kinton`.`enterprise_properties` (
  `idProperties` int(11) unsigned NOT NULL auto_increment,
  `enterprise` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY  (`idProperties`),
  CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE  `kinton`.`enterprise_properties_map` (
 `enterprise_properties` int(11) unsigned NOT NULL,
  `map_key` varchar(30) NOT NULL,
  `value` varchar(50) default NULL, 
  CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `enterprise_properties` (`idProperties`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8

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

-- PRICING --
-- Definition of table `kinton`.`currency`
CREATE TABLE `kinton`.`currency` (
  `idCurrency` int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `symbol` varchar(10) NOT NULL ,
  `name` varchar(20) NOT NULL ,
   `digits` int(1)  NOT NULL default 2,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCurrency`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
    
-- PRICING --
-- Definition of table `kinton`.`costCode`
CREATE TABLE `kinton`.`costCode` (
  `idCostCode` int(10) NOT NULL AUTO_INCREMENT ,
  `name` varchar(20) NOT NULL ,
  `description` varchar(100) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idCostCode`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- PRICING --
-- Definition of table `kinton`.`pricingTemplate`
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
  `description` varchar(1000) NOT NULL,
  `last_update` timestamp NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTemplate`) ,
  KEY `Pricing_FK2_Currency` (`idCurrency`),
  CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency` ) REFERENCES `kinton`.`currency` (`idCurrency` ) ON DELETE NO ACTION
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
  

-- PRICING --
-- Definition of table `kinton`.`pricingCostCode`
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
CREATE TABLE  `kinton`.`costCodeCurrency` (
  `idCostCodeCurrency` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idCostCode` int(10) unsigned,
  `idCurrency` int(10) unsigned,
  `price` DECIMAL(20,5) NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 0,
  PRIMARY KEY (`idCostCodeCurrency`)
  -- CONSTRAINT `idCostCode_FK` FOREIGN KEY (`idCostCode`) REFERENCES `costCode` (`idCostCode`),
  -- CONSTRAINT `idCurrency_FK` FOREIGN KEY (`idCurrency`) REFERENCES `currency` (`idCurrency`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- PRICING --
-- Definition of table `kinton`.`pricingTier`
CREATE TABLE `kinton`.`pricingTier` (
  `idPricingTemplate` int(10) UNSIGNED NOT NULL,
  `idTier` int(10) UNSIGNED NOT NULL,
  `price` DECIMAL(20,5) UNSIGNED NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPricingTemplate`, `idTier`) 
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `vswitchName` varchar(200) NOT NULL;

-- UCS default template
ALTER TABLE `kinton`.`ucs_rack` ADD COLUMN `defaultTemplate` varchar(200);
ALTER TABLE `kinton`.`ucs_rack` ADD COLUMN `maxMachinesOn` int(4) DEFAULT 0;

ALTER TABLE `kinton`.`virtualimage` DROP COLUMN `treaty`;
ALTER TABLE `kinton`.`virtualimage` DROP COLUMN `deleted`;

ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `cost_code`,
 ADD COLUMN `creation_user` varchar(128) NOT NULL AFTER `creation_date`;

ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package`
 DROP FOREIGN KEY `fk_ovf_package_list_has_ovf_package_ovf_package1`;

ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY `fk_ovf_package_list_has_ovf_package_ovf_package1` (`id_ovf_package`)
    REFERENCES `ovf_package` (`id_ovf_package`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage, DROP COLUMN hd, DROP COLUMN hdUsed;

ALTER TABLE `kinton`.`virtualappliance` DROP COLUMN `state`;
ALTER TABLE `kinton`.`virtualappliance` DROP COLUMN `substate`; 
ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `subState` VARCHAR(50)  DEFAULT NULL AFTER `state`;
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `chefEnabled` BOOLEAN  NOT NULL DEFAULT false AFTER `cost_code`;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `chef_url` VARCHAR(255)  DEFAULT NULL AFTER `publicIPHard`,
 ADD COLUMN `chef_client` VARCHAR(50)  DEFAULT NULL AFTER `chef_url`,
 ADD COLUMN `chef_validator` VARCHAR(50)  DEFAULT NULL AFTER `chef_client`,
 ADD COLUMN `chef_client_certificate` TEXT  DEFAULT NULL AFTER `chef_validator`,
 ADD COLUMN `chef_validator_certificate` TEXT  DEFAULT NULL AFTER `chef_client_certificate`;

ALTER TABLE `kinton`.`vappstateful_conversions` DROP COLUMN `state`;
ALTER TABLE `kinton`.`vappstateful_conversions` DROP COLUMN `substate`; 
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `state` VARCHAR(50)  DEFAULT NULL AFTER `idDiskStatefulConversion`;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `subState` VARCHAR(50)  DEFAULT NULL AFTER `state`;
ALTER TABLE `kinton`.`ovf_package` MODIFY COLUMN `name` VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `kinton`.`datacenter` ADD COLUMN `uuid` VARCHAR(40) DEFAULT NULL AFTER `idDataCenter`;

-- PRICING --
-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricingTemplate` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`);

-- PHYSICAL MACHINE --
ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage, DROP COLUMN hd, DROP COLUMN hdUsed;

-- VFILER --
ALTER TABLE `kinton`.`storage_device` ADD COLUMN `username` varchar(256) DEFAULT NULL;
ALTER TABLE `kinton`.`storage_device` ADD COLUMN `password` varchar(256) DEFAULT NULL;

-- reconfigures --
ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `temporal` int(10) unsigned default NULL;
ALTER TABLE `kinton`.`rasd_management` ADD COLUMN `temporal` int(10) unsigned default NULL; 
ALTER TABLE `kinton`.`rasd_management` ADD COLUMN `sequence` int(10) unsigned default NULL; 
-- Modify constraint --
ALTER TABLE `kinton`.`rasd_management` DROP CONSTRAINT `idResource_FK`;
ALTER TABLE `kinton`.`rasd_management` ADD  CONSTRAINT `idResource_FK2` FOREIGN KEY (`idResource`) REFERENCES `rasd` (`instanceID`) ON DELETE SET NULL


ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `network_configuration_id` int(11) unsigned; 
ALTER TABLE `kinton`.`virtualmachine` ADD KEY `virtualMachine_FK6`;
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `network_configuration` (`network_configuration_id`) ON DELETE SET NULL; 

update vlan_network vl, ip_pool_management ip, rasd_management rm, virtualmachine vm set vm.network_configuration_id = vl.network_configuration_id where ip.vlan_network_id = vl.vlan_network_id and ip.idManagement = rm.idManagement and configureGateway = 1 and rm.idvm = vm.idvm;

ALTER TABLE `ip_pool_management` DROP COLUMN `configureGateway`;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");

-- First I need to update some rows before to delete the `default_network` field
-- UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
-- ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;

 ("client.infra.ucsManagerLink","/ucsm/ucsm.jnlp","URL to display UCS Manager Interface");
 
-- PRICING --
-- Dumping data for table `kinton`.`privilege`

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE_PRICING',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

-- PRICING --
-- Dumping data for table `kinton`.`currency`
LOCK TABLES `kinton`.`currency` WRITE;
INSERT INTO `kinton`.`currency` values (1, "USD", "Dollar - $", 2, 0);
INSERT INTO `kinton`.`currency` values (2, "EUR", CONCAT("Euro - " ,0xE282AC), 2,  0);
INSERT INTO `kinton`.`currency` values (3, "JPY", CONCAT("Yen - " , 0xc2a5), 0, 0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;  

-- PRICING --
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
 ("client.main.disableChangePassword","1","Allow (1) or deny (0) user to change their password"),
 ("client.wiki.pricing.createCurrency","http://community.abiquo.com/display/ABI20/Pricing+View","Currency creation wiki"),
 ("client.wiki.pricing.createTemplate","http://community.abiquo.com/display/ABI20/Pricing+View","create pricing template wiki"),
 ("client.wiki.pricing.createCostCode","http://community.abiquo.com/display/ABI20/Pricing+View","create pricing cost code wiki"),
 ("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

-- First I need to update some rows before to delete the `default_network` field
-- UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
-- ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;

UPDATE `kinton`.`virtualimage` set creation_user = 'ABIQUO-BEFORE-2.0', creation_date = CURRENT_TIMESTAMP;
UPDATE `kinton`.`virtualimage` set idRepository = null where stateful = 1;

/* ABICLOUDPREMIUM-2878 - For consistency porpouse, changed vharchar(30) to varchar(256) */
ALTER TABLE `kinton`.`metering` MODIFY COLUMN `physicalmachine` VARCHAR(256)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;


UPDATE ip_pool_management im, rasd_management rm, rasd r SET rm.sequence = r.configurationname WHERE rm.idresource=r.instanceid AND Im.idmanagement = rm.idmanagement AND Rm.idvm IS NOT NULL;

UPDATE Volume_management vm, rasd_management rm, rasd r SET Rm.sequence=IF(r.generation IS NULL, 0, r.generation +1) WHERE Rm.idResource=r.instanceID AND Vm.idManagement = rm.idManagement AND Rm.idVM IS NOT NULL;

LOCK TABLES `kinton`.`enterprise_properties` WRITE;
INSERT INTO `kinton`.`enterprise_properties` VALUES  (1,1);
/*!40000 ALTER TABLE `kinton`.`enterprise_properties` ENABLE KEYS */;

/*!40000 ALTER TABLE `kinton`.`enterprise_properties` DISABLE KEYS */;
INSERT INTO `kinton`.`enterprise_properties_map` VALUES  (1,'Support e-mail','support@abiquo.com');
/*!40000 ALTER TABLE `kinton`.`enterprise_properties` ENABLE KEYS */;

-- First I need to update some rows before to delete the `default_network` field
UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_ip_pool_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_network_configuration_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`; 
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_datastore_update_stats`;
-- 
DROP TRIGGER IF EXISTS `kinton`.`create_rasd_management_update_stats`; -- Trigger removed in 2.0
--
--  PROCEDURES TO CALCULATE DATASTORE SIZE
--
-- *************************************************
DROP PROCEDURE IF EXISTS `KINTON`.`GET_DATASTORE_SIZE_BY_DC`;
DROP PROCEDURE IF EXISTS `KINTON`.`GET_DATASTORE_USED_SIZE_BY_DC`;
DROP PROCEDURE IF EXISTS `KINTON`.`CALCULATECLOUDUSAGESTATS`;
DROP PROCEDURE IF EXISTS `KINTON`.`CALCULATEENTERPRISERESOURCESSTATS`;
DROP PROCEDURE IF EXISTS `KINTON`.`CALCULATEVAPPENTERPRISESTATS`;
DROP PROCEDURE IF EXISTS `KINTON`.`CALCULATEVDCENTERPRISESTATS`;

DELIMITER |
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) DEFAULT CHARACTER SET utf8 '';
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


CREATE PROCEDURE `KINTON`.`GET_DATASTORE_SIZE_BY_DC`(IN IDDC INT, OUT SIZE BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(D.SIZE) IS NULL,0,SUM(D.SIZE)) INTO SIZE
    FROM DATASTORE D LEFT OUTER JOIN DATASTORE_ASSIGNMENT DA ON D.IDDATASTORE = DA.IDDATASTORE 
    LEFT OUTER JOIN PHYSICALMACHINE PM ON DA.IDPHYSICALMACHINE = PM.IDPHYSICIALMACHINE
    WHERE PM.IDDATACENTER = IDDC AND D.ENABLED = 1;
END
--
|
--
CREATE PROCEDURE `KINTON`.`GET_DATASTORE_USED_SIZE_BY_DC`(IN IDDC INT, OUT USEDSIZE BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(D.USEDSIZE) IS NULL,0,SUM(D.USEDSIZE)) INTO USEDSIZE
    FROM DATASTORE D LEFT OUTER JOIN DATASTORE_ASSIGNMENT DA ON D.IDDATASTORE = DA.IDDATASTORE
    LEFT OUTER JOIN PHYSICALMACHINE PM ON DA.IDPHYSICALMACHINE = PM.IDPHYSICIALMACHINE
    WHERE PM.IDDATACENTER = IDDC AND D.ENABLED = 1;
END
--
|
--

CREATE PROCEDURE `KINTON`.CALCULATECLOUDUSAGESTATS()
   BEGIN
  DECLARE IDDATACENTEROBJ INTEGER;
  DECLARE SERVERSTOTAL BIGINT UNSIGNED;
  DECLARE SERVERSRUNNING BIGINT UNSIGNED;
  DECLARE STORAGETOTAL BIGINT UNSIGNED;
  DECLARE STORAGEUSED BIGINT UNSIGNED;
  DECLARE PUBLICIPSTOTAL BIGINT UNSIGNED;
  DECLARE PUBLICIPSRESERVED BIGINT UNSIGNED;
  DECLARE PUBLICIPSUSED BIGINT UNSIGNED;
  DECLARE VMACHINESTOTAL BIGINT UNSIGNED;
  DECLARE VMACHINESRUNNING BIGINT UNSIGNED;
  DECLARE VCPUTOTAL BIGINT UNSIGNED;
  DECLARE VCPURESERVED BIGINT UNSIGNED;
  DECLARE VCPUUSED BIGINT UNSIGNED;
  DECLARE VMEMORYTOTAL BIGINT UNSIGNED;
  DECLARE VMEMORYRESERVED BIGINT UNSIGNED;
  DECLARE VMEMORYUSED BIGINT UNSIGNED;
  DECLARE VSTORAGERESERVED BIGINT UNSIGNED;
  DECLARE VSTORAGEUSED BIGINT UNSIGNED;
  DECLARE VSTORAGETOTAL BIGINT UNSIGNED;
  DECLARE NUMUSERSCREATED BIGINT UNSIGNED;
  DECLARE NUMVDCCREATED BIGINT UNSIGNED;
  DECLARE NUMENTERPRISESCREATED BIGINT UNSIGNED;
  DECLARE STORAGERESERVED BIGINT UNSIGNED; 
  DECLARE VLANRESERVED BIGINT UNSIGNED; 
  DECLARE VLANUSED BIGINT UNSIGNED; 

  DECLARE NO_MORE_DCS INTEGER;

  DECLARE CURDC CURSOR FOR SELECT IDDATACENTER FROM DATACENTER;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_DCS=1;

  SET NO_MORE_DCS=0;
  SET IDDATACENTEROBJ = -1;

  OPEN CURDC;

  TRUNCATE CLOUD_USAGE_STATS;

  DEPT_LOOP:WHILE(NO_MORE_DCS=0) DO
    FETCH CURDC INTO IDDATACENTEROBJ;
    IF NO_MORE_DCS=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO SERVERSTOTAL
    FROM PHYSICALMACHINE
    WHERE IDDATACENTER = IDDATACENTEROBJ
    AND IDSTATE!=2;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO SERVERSRUNNING
    FROM PHYSICALMACHINE
    WHERE IDDATACENTER = IDDATACENTEROBJ
    AND IDSTATE=3;
    --
    SELECT IF (SUM(LIMITRESOURCE) IS NULL, 0, SUM(LIMITRESOURCE))   INTO STORAGETOTAL
    FROM RASD R, RASD_MANAGEMENT RM, VIRTUALDATACENTER VDC
    WHERE RM.IDRESOURCE = R.INSTANCEID
    AND VDC.IDVIRTUALDATACENTER=RM.IDVIRTUALDATACENTER
    AND VDC.IDDATACENTER = IDDATACENTEROBJ;
    --
    SELECT IF (SUM(R.LIMITRESOURCE) IS NULL, 0, SUM(R.LIMITRESOURCE)) INTO STORAGEUSED
    FROM STORAGE_POOL SP, STORAGE_DEVICE SD, VOLUME_MANAGEMENT VM, RASD_MANAGEMENT RM, RASD R
    WHERE VM.IDSTORAGE = SP.IDSTORAGE
    AND SP.IDSTORAGEDEVICE = SD.ID
    AND VM.IDMANAGEMENT = RM.IDMANAGEMENT
    AND R.INSTANCEID = RM.IDRESOURCE
    AND RM.IDRESOURCETYPE = 8
    AND (VM.STATE = 1)
    AND SD.IDDATACENTER = IDDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSTOTAL
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID
    AND DC.IDDATACENTER = IDDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSRESERVED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID
    AND VN.NETWORKTYPE = 'PUBLIC'             
    AND IPM.MAC IS NOT NULL
    AND DC.IDDATACENTER = IDDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSUSED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID
    AND VN.NETWORKTYPE = 'PUBLIC'             
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND IPM.MAC IS NOT NULL
    AND RM.IDVM IS NOT NULL
    AND DC.IDDATACENTER = IDDATACENTEROBJ;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMACHINESTOTAL
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP, VIRTUALDATACENTER VDC
    WHERE V.IDVM = NVI.IDVM
    AND N.IDNODE=NVI.IDNODE
    AND VAPP.IDVIRTUALAPP = N.IDVIRTUALAPP
    AND VDC.IDVIRTUALDATACENTER = VAPP.IDVIRTUALDATACENTER
    AND VDC.IDDATACENTER = IDDATACENTEROBJ
    AND V.STATE != "NOT_ALLOCATED" AND V.STATE != "UNKNOWN" 
    AND V.IDTYPE = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMACHINESRUNNING
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP, VIRTUALDATACENTER VDC
    WHERE V.IDVM = NVI.IDVM
    AND N.IDNODE=NVI.IDNODE
    AND VAPP.IDVIRTUALAPP = N.IDVIRTUALAPP
    AND VDC.IDVIRTUALDATACENTER = VAPP.IDVIRTUALDATACENTER
    AND VDC.IDDATACENTER = IDDATACENTEROBJ
    AND V.STATE = "ON"
    AND V.IDTYPE = 1;
    --
    SELECT IF (SUM(CPU*CPURATIO) IS NULL,0,SUM(CPU*CPURATIO)), IF (SUM(RAM) IS NULL,0,SUM(RAM)), IF (SUM(HD) IS NULL,0,SUM(HD)) , IF (SUM(CPUUSED) IS NULL,0,SUM(CPUUSED)), IF (SUM(RAMUSED) IS NULL,0,SUM(RAMUSED)), IF (SUM(HDUSED) IS NULL,0,SUM(HDUSED)) INTO VCPUTOTAL, VMEMORYTOTAL, VSTORAGETOTAL, VCPUUSED, VMEMORYUSED, VSTORAGEUSED
    FROM PHYSICALMACHINE
    WHERE IDDATACENTER = IDDATACENTEROBJ
    AND IDSTATE = 3; 
    --
    CALL GET_DATASTORE_SIZE_BY_DC(IDDATACENTEROBJ,VSTORAGETOTAL);
    CALL GET_DATASTORE_USED_SIZE_BY_DC(IDDATACENTEROBJ,VSTORAGEUSED);
    --
    SELECT IF (SUM(VLANHARD) IS NULL, 0, SUM(VLANHARD))  INTO VLANRESERVED
    FROM ENTERPRISE_LIMITS_BY_DATACENTER 
    WHERE IDDATACENTER = IDDATACENTEROBJ AND IDENTERPRISE IS NOT NULL;

    -- INSERTS STATS ROW
    INSERT INTO CLOUD_USAGE_STATS
    (IDDATACENTER,
    SERVERSTOTAL,SERVERSRUNNING,
    STORAGETOTAL,STORAGEUSED,
    PUBLICIPSTOTAL,PUBLICIPSRESERVED,PUBLICIPSUSED,
    VMACHINESTOTAL,VMACHINESRUNNING,
    VCPUTOTAL,VCPURESERVED,VCPUUSED,
    VMEMORYTOTAL,VMEMORYRESERVED,VMEMORYUSED,
    VSTORAGERESERVED,VSTORAGEUSED,VSTORAGETOTAL,
    VLANRESERVED,
    NUMUSERSCREATED,NUMVDCCREATED,NUMENTERPRISESCREATED)
    VALUES
    (IDDATACENTEROBJ,
    SERVERSTOTAL,SERVERSRUNNING,
    STORAGETOTAL,STORAGEUSED,
    PUBLICIPSTOTAL,PUBLICIPSRESERVED,PUBLICIPSUSED,
    VMACHINESTOTAL,VMACHINESRUNNING,
    VCPUTOTAL,0,VCPUUSED,
    VMEMORYTOTAL,0,VMEMORYUSED,
    0,VSTORAGEUSED,VSTORAGETOTAL,
    VLANRESERVED,
    0,0,0);

  END WHILE DEPT_LOOP;
  CLOSE CURDC;

  -- ALL CLOUD STATS (IDDATACENTER -1): VCPURESERVED, VMEMORYRESERVED, VSTORAGERESERVED, NUMUSERSCREATED, NUMVDCCREATED, NUMENTERPRISESCREATED
  SELECT IF (SUM(CPUHARD) IS NULL,0,SUM(CPUHARD)), IF (SUM(RAMHARD) IS NULL,0,SUM(RAMHARD)), IF (SUM(HDHARD) IS NULL,0,SUM(HDHARD)), IF (SUM(STORAGEHARD) IS NULL,0,SUM(STORAGEHARD)) INTO VCPURESERVED, VMEMORYRESERVED, VSTORAGERESERVED, STORAGERESERVED
  FROM ENTERPRISE E;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO NUMUSERSCREATED
  FROM USER;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO NUMVDCCREATED
  FROM VIRTUALDATACENTER VDC;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO NUMENTERPRISESCREATED
  FROM ENTERPRISE E;
  --
  SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VLANUSED
  FROM VLAN_NETWORK;

  -- INSERTS STATS ROW
    INSERT INTO CLOUD_USAGE_STATS
    (IDDATACENTER,
    SERVERSTOTAL,SERVERSRUNNING,
    STORAGETOTAL,STORAGEUSED,
    PUBLICIPSTOTAL,PUBLICIPSRESERVED,PUBLICIPSUSED,
    VMACHINESTOTAL,VMACHINESRUNNING,
    VCPUTOTAL,VCPURESERVED,VCPUUSED,
    VMEMORYTOTAL,VMEMORYRESERVED,VMEMORYUSED,
    VSTORAGERESERVED,VSTORAGEUSED,VSTORAGETOTAL,
    VLANUSED,
    NUMUSERSCREATED,NUMVDCCREATED,NUMENTERPRISESCREATED)
    VALUES
    (-1,
    0,0,
    0,0,
    0,0,0,
    0,0,
    0,VCPURESERVED,0,
    0,VMEMORYRESERVED,0,
    VSTORAGERESERVED,0,0,
    VLANUSED,
    NUMUSERSCREATED,NUMVDCCREATED,NUMENTERPRISESCREATED);
   END;

--
|
--
CREATE PROCEDURE `KINTON`.CALCULATEENTERPRISERESOURCESSTATS()
   BEGIN
  DECLARE IDENTERPRISEOBJ INTEGER;
  DECLARE VCPURESERVED BIGINT UNSIGNED;
  DECLARE VCPUUSED BIGINT UNSIGNED;
  DECLARE MEMORYRESERVED BIGINT UNSIGNED;
  DECLARE MEMORYUSED BIGINT UNSIGNED;
  DECLARE LOCALSTORAGERESERVED BIGINT UNSIGNED;
  DECLARE LOCALSTORAGEUSED BIGINT UNSIGNED;
  DECLARE EXTSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE PUBLICIPSRESERVED BIGINT UNSIGNED;
  DECLARE PUBLICIPSUSED BIGINT UNSIGNED;
  DECLARE VLANRESERVED BIGINT UNSIGNED; 
  DECLARE VLANUSED BIGINT UNSIGNED; 
  -- DECLARE REPOSITORYRESERVED BIGINT UNSIGNED; -- TBD
  -- DECLARE REPOSITORYUSED BIGINT UNSIGNED; -- TBD

  DECLARE NO_MORE_ENTERPRISES INTEGER;

  DECLARE CURDC CURSOR FOR SELECT IDENTERPRISE FROM ENTERPRISE;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_ENTERPRISES=1;

  SET NO_MORE_ENTERPRISES = 0;
  SET IDENTERPRISEOBJ = -1;

  OPEN CURDC;

  TRUNCATE ENTERPRISE_RESOURCES_STATS;

  DEPT_LOOP:WHILE(NO_MORE_ENTERPRISES = 0) DO
    FETCH CURDC INTO IDENTERPRISEOBJ;
    IF NO_MORE_ENTERPRISES=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    -- INSERT INTO DEBUG_MSG (MSG) VALUES (CONCAT('ITERACION ENTERPRISE: ',IDENTERPRISEOBJ));
    --
    SELECT CPUHARD, RAMHARD, HDHARD, STORAGEHARD, VLANHARD INTO VCPURESERVED, MEMORYRESERVED, LOCALSTORAGERESERVED, EXTSTORAGERESERVED, VLANRESERVED
    FROM ENTERPRISE E
    WHERE E.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (SUM(VM.CPU) IS NULL, 0, SUM(VM.CPU)), IF (SUM(VM.RAM) IS NULL, 0, SUM(VM.RAM)), IF (SUM(VM.HD) IS NULL, 0, SUM(VM.HD)) INTO VCPUUSED, MEMORYUSED, LOCALSTORAGEUSED
    FROM VIRTUALMACHINE VM
    WHERE VM.STATE = "ON"
    AND VM.IDTYPE = 1
    AND VM.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VLANUSED
    FROM VIRTUALDATACENTER VDC, VLAN_NETWORK VN
    WHERE VDC.NETWORKTYPEID=VN.NETWORK_ID
    AND VDC.IDENTERPRISE=IDENTERPRISEOBJ;
    --
    SELECT IF (SUM(R.LIMITRESOURCE) IS NULL, 0, SUM(R.LIMITRESOURCE)) INTO EXTSTORAGEUSED
    FROM RASD_MANAGEMENT RM, RASD R, VOLUME_MANAGEMENT VM, VIRTUALDATACENTER VDC
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND R.INSTANCEID = RM.IDRESOURCE
    AND (VM.STATE = 1 OR VM.STATE = 2)
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSRESERVED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM, VIRTUALDATACENTER VDC
    WHERE IPM.DHCP_SERVICE_ID=NC.DHCP_SERVICE_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID   
    AND VN.NETWORKTYPE = 'PUBLIC'             
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSUSED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM, VIRTUALDATACENTER VDC
    WHERE IPM.DHCP_SERVICE_ID=NC.DHCP_SERVICE_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID            
    AND VN.NETWORKTYPE = 'PUBLIC'    
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND RM.IDVM IS NOT NULL
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;


    -- INSERTS STATS ROW
    INSERT INTO ENTERPRISE_RESOURCES_STATS (IDENTERPRISE,VCPURESERVED,VCPUUSED,MEMORYRESERVED,MEMORYUSED,LOCALSTORAGERESERVED,LOCALSTORAGEUSED,EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED)
     VALUES (IDENTERPRISEOBJ,VCPURESERVED,VCPUUSED,MEMORYRESERVED,MEMORYUSED,LOCALSTORAGERESERVED,LOCALSTORAGEUSED,EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED);

  END WHILE DEPT_LOOP;
  CLOSE CURDC;

   END;
--
|
--
CREATE PROCEDURE `KINTON`.CALCULATEVDCENTERPRISESTATS()
   BEGIN
  DECLARE IDVIRTUALDATACENTEROBJ INTEGER;
  DECLARE IDENTERPRISE INTEGER;
  DECLARE VDCNAME VARCHAR(45);
  DECLARE VMCREATED MEDIUMINT UNSIGNED;
  DECLARE VMACTIVE MEDIUMINT UNSIGNED;
  DECLARE VOLCREATED MEDIUMINT UNSIGNED;
  DECLARE VOLASSOCIATED MEDIUMINT UNSIGNED;
  DECLARE VOLATTACHED MEDIUMINT UNSIGNED;
  DECLARE VCPURESERVED BIGINT UNSIGNED; 
  DECLARE VCPUUSED BIGINT UNSIGNED; 
  DECLARE MEMORYRESERVED BIGINT UNSIGNED;
  DECLARE MEMORYUSED BIGINT UNSIGNED; 
  DECLARE LOCALSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE LOCALSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE PUBLICIPSRESERVED MEDIUMINT UNSIGNED;
  DECLARE PUBLICIPSUSED MEDIUMINT UNSIGNED;
  DECLARE VLANRESERVED MEDIUMINT UNSIGNED; 
  DECLARE VLANUSED MEDIUMINT UNSIGNED; 

  DECLARE NO_MORE_VDCS INTEGER;

  DECLARE CURDC CURSOR FOR SELECT VDC.IDVIRTUALDATACENTER, VDC.IDENTERPRISE, VDC.NAME FROM VIRTUALDATACENTER VDC;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_VDCS = 1;

  SET NO_MORE_VDCS = 0;
  SET IDVIRTUALDATACENTEROBJ = -1;

  OPEN CURDC;

  TRUNCATE VDC_ENTERPRISE_STATS;

  DEPT_LOOP:WHILE(NO_MORE_VDCS = 0) DO
    FETCH CURDC INTO IDVIRTUALDATACENTEROBJ, IDENTERPRISE, VDCNAME;
    IF NO_MORE_VDCS=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMCREATED
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND V.STATE != "NOT_ALLOCATED" AND V.STATE != "UNKNOWN";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMACTIVE
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND V.STATE = "ON";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLCREATED
    FROM RASD_MANAGEMENT RM
    WHERE RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND RM.IDRESOURCETYPE=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLASSOCIATED
    FROM RASD_MANAGEMENT RM
    WHERE RM.IDVIRTUALAPP IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND RM.IDRESOURCETYPE=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLATTACHED
    FROM VOLUME_MANAGEMENT VM, RASD_MANAGEMENT RM
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT
    AND RM.IDVIRTUALAPP IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND STATE = 2;
    --
    SELECT IF (SUM(CPUHARD) IS NULL, 0, SUM(CPUHARD)), IF (SUM(RAMHARD) IS NULL, 0, SUM(RAMHARD)), IF (SUM(HDHARD) IS NULL, 0, SUM(HDHARD)), IF (SUM(STORAGEHARD) IS NULL, 0, SUM(STORAGEHARD)), IF (SUM(VLANHARD) IS NULL, 0, SUM(VLANHARD)) INTO VCPURESERVED, MEMORYRESERVED, LOCALSTORAGERESERVED, EXTSTORAGERESERVED, VLANRESERVED
    FROM VIRTUALDATACENTER 
    WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (SUM(VM.CPU) IS NULL, 0, SUM(VM.CPU)), IF (SUM(VM.RAM) IS NULL, 0, SUM(VM.RAM)), IF (SUM(VM.HD) IS NULL, 0, SUM(VM.HD)) INTO VCPUUSED, MEMORYUSED, LOCALSTORAGEUSED
    FROM VIRTUALMACHINE VM, NODEVIRTUALIMAGE NVI, NODE N, VIRTUALAPP VAPP
    WHERE VM.IDVM = NVI.IDVM
    AND NVI.IDNODE = N.IDNODE
    AND VAPP.IDVIRTUALAPP = N.IDVIRTUALAPP
    AND VM.STATE = "ON"
    AND VM.IDTYPE = 1
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (SUM(R.LIMITRESOURCE) IS NULL, 0, SUM(R.LIMITRESOURCE)) INTO EXTSTORAGEUSED
    FROM RASD_MANAGEMENT RM, RASD R, VOLUME_MANAGEMENT VM
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT    
    AND R.INSTANCEID = RM.IDRESOURCE
    AND (VM.STATE = 1 OR VM.STATE = 2)
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSUSED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM
    WHERE IPM.DHCP_SERVICE_ID=NC.DHCP_SERVICE_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID           
    AND VN.NETWORKTYPE = 'PUBLIC'     
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND RM.IDVM IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSRESERVED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM
    WHERE IPM.DHCP_SERVICE_ID=NC.DHCP_SERVICE_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID                
    AND VN.NETWORKTYPE = 'PUBLIC'
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VLANUSED
    FROM VIRTUALDATACENTER VDC, VLAN_NETWORK VN
    WHERE VDC.NETWORKTYPEID = VN.NETWORK_ID
    AND VDC.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
   -- 


    -- INSERTS STATS ROW
    INSERT INTO VDC_ENTERPRISE_STATS (IDVIRTUALDATACENTER,IDENTERPRISE,VDCNAME,VMCREATED,VMACTIVE,VOLCREATED,VOLASSOCIATED,VOLATTACHED, VCPURESERVED, VCPUUSED, MEMORYRESERVED, MEMORYUSED, LOCALSTORAGERESERVED, LOCALSTORAGEUSED, EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED)
    VALUES (IDVIRTUALDATACENTEROBJ,IDENTERPRISE,VDCNAME,VMCREATED,VMACTIVE,VOLCREATED,VOLASSOCIATED,VOLATTACHED, VCPURESERVED, VCPUUSED, MEMORYRESERVED, MEMORYUSED, LOCALSTORAGERESERVED, LOCALSTORAGEUSED, EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED );


  END WHILE DEPT_LOOP;
  CLOSE CURDC;

   END;
--
|
--
-- TO BE DONE WHEN SHOWING DATACENTER STATS BY ENTERPRISE
-- CREATE PROCEDURE `KINTON`.CALCULATEDCENTERPRISESTATS()
--   BEGIN
--   END;
--
--
CREATE PROCEDURE `KINTON`.CALCULATEVAPPENTERPRISESTATS()
   BEGIN
  DECLARE IDVIRTUALAPPOBJ INTEGER;
  DECLARE IDENTERPRISE INTEGER;
  DECLARE IDVIRTUALDATACENTER INTEGER;
  DECLARE VAPPNAME VARCHAR(45);
  DECLARE VDCNAME VARCHAR(45);
  DECLARE VMCREATED MEDIUMINT UNSIGNED;
  DECLARE VMACTIVE MEDIUMINT UNSIGNED;
  DECLARE VOLASSOCIATED MEDIUMINT UNSIGNED;
  DECLARE VOLATTACHED MEDIUMINT UNSIGNED;

  DECLARE NO_MORE_VAPPS INTEGER;

  DECLARE CURDC CURSOR FOR SELECT VAPP.IDVIRTUALAPP, VAPP.IDENTERPRISE, VAPP.IDVIRTUALDATACENTER, VAPP.NAME, VDC.NAME FROM VIRTUALAPP VAPP, VIRTUALDATACENTER VDC WHERE VDC.IDVIRTUALDATACENTER = VAPP.IDVIRTUALDATACENTER;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_VAPPS = 1;

  SET NO_MORE_VAPPS = 0;
  SET IDVIRTUALAPPOBJ = -1;

  OPEN CURDC;

  TRUNCATE VAPP_ENTERPRISE_STATS;

  DEPT_LOOP:WHILE(NO_MORE_VAPPS = 0) DO
    FETCH CURDC INTO IDVIRTUALAPPOBJ, IDENTERPRISE, IDVIRTUALDATACENTER, VAPPNAME, VDCNAME;
    IF NO_MORE_VAPPS=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMCREATED
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALAPP = IDVIRTUALAPPOBJ
    AND V.STATE != "NOT_ALLOCATED" AND V.STATE != "UNKNOWN"
    AND V.IDTYPE = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMACTIVE
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALAPP = IDVIRTUALAPPOBJ
    AND V.STATE = "ON"
    AND V.IDTYPE = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLASSOCIATED
    FROM RASD_MANAGEMENT RM
    WHERE RM.IDVIRTUALAPP = IDVIRTUALAPPOBJ
    AND RM.IDRESOURCETYPE=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLATTACHED
    FROM VOLUME_MANAGEMENT VM, RASD_MANAGEMENT RM
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT
    AND RM.IDVIRTUALAPP = IDVIRTUALAPPOBJ
    AND STATE = 2;

    -- INSERTS STATS ROW
    INSERT INTO VAPP_ENTERPRISE_STATS (IDVIRTUALAPP,IDENTERPRISE,IDVIRTUALDATACENTER,VAPPNAME,VDCNAME,VMCREATED,VMACTIVE,VOLASSOCIATED,VOLATTACHED)
    VALUES (IDVIRTUALAPPOBJ, IDENTERPRISE,IDVIRTUALDATACENTER,VAPPNAME,VDCNAME,VMCREATED,VMACTIVE,VOLASSOCIATED,VOLATTACHED);


  END WHILE DEPT_LOOP;
  CLOSE CURDC;

   END;
--
|
--
CREATE PROCEDURE `KINTON`.CALCULATEENTERPRISERESOURCESSTATS()
   BEGIN
  DECLARE IDENTERPRISEOBJ INTEGER;
  DECLARE VCPURESERVED BIGINT UNSIGNED;
  DECLARE VCPUUSED BIGINT UNSIGNED;
  DECLARE MEMORYRESERVED BIGINT UNSIGNED;
  DECLARE MEMORYUSED BIGINT UNSIGNED;
  DECLARE LOCALSTORAGERESERVED BIGINT UNSIGNED;
  DECLARE LOCALSTORAGEUSED BIGINT UNSIGNED;
  DECLARE EXTSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE PUBLICIPSRESERVED BIGINT UNSIGNED;
  DECLARE PUBLICIPSUSED BIGINT UNSIGNED;
  DECLARE VLANRESERVED BIGINT UNSIGNED; 
  DECLARE VLANUSED BIGINT UNSIGNED; 
  -- DECLARE REPOSITORYRESERVED BIGINT UNSIGNED; -- TBD
  -- DECLARE REPOSITORYUSED BIGINT UNSIGNED; -- TBD

  DECLARE NO_MORE_ENTERPRISES INTEGER;

  DECLARE CURDC CURSOR FOR SELECT IDENTERPRISE FROM ENTERPRISE;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_ENTERPRISES=1;

  SET NO_MORE_ENTERPRISES = 0;
  SET IDENTERPRISEOBJ = -1;

  OPEN CURDC;

  TRUNCATE ENTERPRISE_RESOURCES_STATS;

  DEPT_LOOP:WHILE(NO_MORE_ENTERPRISES = 0) DO
    FETCH CURDC INTO IDENTERPRISEOBJ;
    IF NO_MORE_ENTERPRISES=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    -- INSERT INTO DEBUG_MSG (MSG) VALUES (CONCAT('ITERACION ENTERPRISE: ',IDENTERPRISEOBJ));
    --
    SELECT CPUHARD, RAMHARD, HDHARD, STORAGEHARD, VLANHARD INTO VCPURESERVED, MEMORYRESERVED, LOCALSTORAGERESERVED, EXTSTORAGERESERVED, VLANRESERVED
    FROM ENTERPRISE E
    WHERE E.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (SUM(VM.CPU) IS NULL, 0, SUM(VM.CPU)), IF (SUM(VM.RAM) IS NULL, 0, SUM(VM.RAM)), IF (SUM(VM.HD) IS NULL, 0, SUM(VM.HD)) INTO VCPUUSED, MEMORYUSED, LOCALSTORAGEUSED
    FROM VIRTUALMACHINE VM
    WHERE VM.STATE = "RUNNING"
    AND VM.IDTYPE = 1
    AND VM.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VLANUSED
    FROM VIRTUALDATACENTER VDC, VLAN_NETWORK VN
    WHERE VDC.NETWORKTYPEID=VN.NETWORK_ID
    AND VDC.IDENTERPRISE=IDENTERPRISEOBJ;
    --
    SELECT IF (SUM(R.LIMITRESOURCE) IS NULL, 0, SUM(R.LIMITRESOURCE)) INTO EXTSTORAGEUSED
    FROM RASD_MANAGEMENT RM, RASD R, VOLUME_MANAGEMENT VM, VIRTUALDATACENTER VDC
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND R.INSTANCEID = RM.IDRESOURCE
    AND (VM.STATE = 1 OR VM.STATE = 2)
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSRESERVED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM, VIRTUALDATACENTER VDC
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID   
    AND VN.NETWORKTYPE = 'PUBLIC'             
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSUSED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM, VIRTUALDATACENTER VDC
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID            
    AND VN.NETWORKTYPE = 'PUBLIC'    
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND VDC.IDVIRTUALDATACENTER = RM.IDVIRTUALDATACENTER
    AND RM.IDVM IS NOT NULL
    AND VDC.IDENTERPRISE = IDENTERPRISEOBJ;


    -- INSERTS STATS ROW
    INSERT INTO ENTERPRISE_RESOURCES_STATS (IDENTERPRISE,VCPURESERVED,VCPUUSED,MEMORYRESERVED,MEMORYUSED,LOCALSTORAGERESERVED,LOCALSTORAGEUSED,EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED)
     VALUES (IDENTERPRISEOBJ,VCPURESERVED,VCPUUSED,MEMORYRESERVED,MEMORYUSED,LOCALSTORAGERESERVED,LOCALSTORAGEUSED,EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED);

  END WHILE DEPT_LOOP;
  CLOSE CURDC;

   END;

|

CREATE PROCEDURE `KINTON`.CALCULATEVDCENTERPRISESTATS()
   BEGIN
  DECLARE IDVIRTUALDATACENTEROBJ INTEGER;
  DECLARE IDENTERPRISE INTEGER;
  DECLARE VDCNAME VARCHAR(45);
  DECLARE VMCREATED MEDIUMINT UNSIGNED;
  DECLARE VMACTIVE MEDIUMINT UNSIGNED;
  DECLARE VOLCREATED MEDIUMINT UNSIGNED;
  DECLARE VOLASSOCIATED MEDIUMINT UNSIGNED;
  DECLARE VOLATTACHED MEDIUMINT UNSIGNED;
  DECLARE VCPURESERVED BIGINT UNSIGNED; 
  DECLARE VCPUUSED BIGINT UNSIGNED; 
  DECLARE MEMORYRESERVED BIGINT UNSIGNED;
  DECLARE MEMORYUSED BIGINT UNSIGNED; 
  DECLARE LOCALSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE LOCALSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGERESERVED BIGINT UNSIGNED; 
  DECLARE EXTSTORAGEUSED BIGINT UNSIGNED; 
  DECLARE PUBLICIPSRESERVED MEDIUMINT UNSIGNED;
  DECLARE PUBLICIPSUSED MEDIUMINT UNSIGNED;
  DECLARE VLANRESERVED MEDIUMINT UNSIGNED; 
  DECLARE VLANUSED MEDIUMINT UNSIGNED; 

  DECLARE NO_MORE_VDCS INTEGER;

  DECLARE CURDC CURSOR FOR SELECT VDC.IDVIRTUALDATACENTER, VDC.IDENTERPRISE, VDC.NAME FROM VIRTUALDATACENTER VDC;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET NO_MORE_VDCS = 1;

  SET NO_MORE_VDCS = 0;
  SET IDVIRTUALDATACENTEROBJ = -1;

  OPEN CURDC;

  TRUNCATE VDC_ENTERPRISE_STATS;

  DEPT_LOOP:WHILE(NO_MORE_VDCS = 0) DO
    FETCH CURDC INTO IDVIRTUALDATACENTEROBJ, IDENTERPRISE, VDCNAME;
    IF NO_MORE_VDCS=1 THEN
        LEAVE DEPT_LOOP;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMCREATED
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND V.STATE != "NOT_DEPLOYED" AND V.STATE != "UNKNOWN" AND V.STATE != "CRASHED";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VMACTIVE
    FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N, VIRTUALAPP VAPP
    WHERE NVI.IDNODE IS NOT NULL
    AND V.IDVM = NVI.IDVM
    AND N.IDNODE = NVI.IDNODE
    AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND V.STATE = "RUNNING";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLCREATED
    FROM RASD_MANAGEMENT RM
    WHERE RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND RM.IDRESOURCETYPE=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLASSOCIATED
    FROM RASD_MANAGEMENT RM
    WHERE RM.IDVIRTUALAPP IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND RM.IDRESOURCETYPE=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VOLATTACHED
    FROM VOLUME_MANAGEMENT VM, RASD_MANAGEMENT RM
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT
    AND RM.IDVIRTUALAPP IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ
    AND STATE = 2;
    --
    SELECT IF (SUM(CPUHARD) IS NULL, 0, SUM(CPUHARD)), IF (SUM(RAMHARD) IS NULL, 0, SUM(RAMHARD)), IF (SUM(HDHARD) IS NULL, 0, SUM(HDHARD)), IF (SUM(STORAGEHARD) IS NULL, 0, SUM(STORAGEHARD)), IF (SUM(VLANHARD) IS NULL, 0, SUM(VLANHARD)) INTO VCPURESERVED, MEMORYRESERVED, LOCALSTORAGERESERVED, EXTSTORAGERESERVED, VLANRESERVED
    FROM VIRTUALDATACENTER 
    WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (SUM(VM.CPU) IS NULL, 0, SUM(VM.CPU)), IF (SUM(VM.RAM) IS NULL, 0, SUM(VM.RAM)), IF (SUM(VM.HD) IS NULL, 0, SUM(VM.HD)) INTO VCPUUSED, MEMORYUSED, LOCALSTORAGEUSED
    FROM VIRTUALMACHINE VM, NODEVIRTUALIMAGE NVI, NODE N, VIRTUALAPP VAPP
    WHERE VM.IDVM = NVI.IDVM
    AND NVI.IDNODE = N.IDNODE
    AND VAPP.IDVIRTUALAPP = N.IDVIRTUALAPP
    AND VM.STATE = "RUNNING"
    AND VM.IDTYPE = 1
    AND VAPP.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (SUM(R.LIMITRESOURCE) IS NULL, 0, SUM(R.LIMITRESOURCE)) INTO EXTSTORAGEUSED
    FROM RASD_MANAGEMENT RM, RASD R, VOLUME_MANAGEMENT VM
    WHERE RM.IDMANAGEMENT = VM.IDMANAGEMENT    
    AND R.INSTANCEID = RM.IDRESOURCE
    AND (VM.STATE = 1 OR VM.STATE = 2)
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSUSED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID           
    AND VN.NETWORKTYPE = 'PUBLIC'     
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND RM.IDVM IS NOT NULL
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO PUBLICIPSRESERVED
    FROM IP_POOL_MANAGEMENT IPM, NETWORK_CONFIGURATION NC, VLAN_NETWORK VN, DATACENTER DC, RASD_MANAGEMENT RM
    WHERE IPM.VLAN_NETWORK_ID = VN.VLAN_NETWORK_ID
    AND VN.NETWORK_CONFIGURATION_ID = NC.NETWORK_CONFIGURATION_ID
    AND VN.NETWORK_ID = DC.NETWORK_ID                
    AND VN.NETWORKTYPE = 'PUBLIC'
    AND RM.IDMANAGEMENT = IPM.IDMANAGEMENT
    AND RM.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO VLANUSED
    FROM VIRTUALDATACENTER VDC, VLAN_NETWORK VN
    WHERE VDC.NETWORKTYPEID = VN.NETWORK_ID
    AND VDC.IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
   -- 


    -- INSERTS STATS ROW
    INSERT INTO VDC_ENTERPRISE_STATS (IDVIRTUALDATACENTER,IDENTERPRISE,VDCNAME,VMCREATED,VMACTIVE,VOLCREATED,VOLASSOCIATED,VOLATTACHED, VCPURESERVED, VCPUUSED, MEMORYRESERVED, MEMORYUSED, LOCALSTORAGERESERVED, LOCALSTORAGEUSED, EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED)
    VALUES (IDVIRTUALDATACENTEROBJ,IDENTERPRISE,VDCNAME,VMCREATED,VMACTIVE,VOLCREATED,VOLASSOCIATED,VOLATTACHED, VCPURESERVED, VCPUUSED, MEMORYRESERVED, MEMORYUSED, LOCALSTORAGERESERVED, LOCALSTORAGEUSED, EXTSTORAGERESERVED, EXTSTORAGEUSED, PUBLICIPSRESERVED, PUBLICIPSUSED, VLANRESERVED, VLANUSED );


  END WHILE DEPT_LOOP;
  CLOSE CURDC;

   END;
| 
DELIMITER ; 
CALL `KINTON`.`CALCULATECLOUDUSAGESTATS`();
CALL `KINTON`.`CALCULATEENTERPRISERESOURCESSTATS`();
CALL `KINTON`.`CALCULATEVDCENTERPRISESTATS`();
-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

DROP TRIGGER IF EXISTS `KINTON`.`CREATE_PHYSICALMACHINE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`DELETE_PHYSICALMACHINE_UPDATE_STATS`; 
DROP TRIGGER IF EXISTS `KINTON`.`UPDATE_PHYSICALMACHINE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`CREATE_DATASTORE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`UPDATE_DATASTORE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`DELETE_DATASTORE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`UPDATE_VIRTUALAPP_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`CREATE_VIRTUALMACHINE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`DELETE_VIRTUALMACHINE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`UPDATE_VIRTUALMACHINE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`CREATE_NODEVIRTUALIMAGE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `KINTON`.`DELETE_NODEVIRTUALIMAGE_UPDATE_STATS`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;

DELIMITER |

-- *************************************************
-- TRIGGERS ON PHYSICAL MACHINE
-- *************************************************

CREATE TRIGGER `KINTON`.`CREATE_PHYSICALMACHINE_UPDATE_STATS` AFTER INSERT ON `KINTON`.`PHYSICALMACHINE`
FOR EACH ROW BEGIN
DECLARE DATASTOREUSEDSIZE BIGINT UNSIGNED;
DECLARE DATASTORESIZE BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.IDSTATE = 3 THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSRUNNING = SERVERSRUNNING+1,
               VCPUUSED=VCPUUSED+NEW.CPUUSED, VMEMORYUSED=VMEMORYUSED+NEW.RAMUSED
        WHERE IDDATACENTER = NEW.IDDATACENTER;
    END IF;
    IF NEW.IDSTATE != 2 THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSTOTAL = SERVERSTOTAL+1, 
               VCPUTOTAL=VCPUTOTAL+(NEW.CPU*NEW.CPURATIO), VMEMORYTOTAL=VMEMORYTOTAL+NEW.RAM
        WHERE IDDATACENTER = NEW.IDDATACENTER;
    END IF;
END IF;
END

--
|
--
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

--
CREATE TRIGGER `kinton`.`create_nodevirtualimage_update_stats` AFTER INSERT ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE state VARCHAR(50) CHARACTER SET utf8;
    DECLARE type INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = NEW.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.state, vm.idType INTO state, type FROM virtualmachine vm WHERE vm.idVM = NEW.idVM;
      --
      IF state != "NOT_DEPLOYED" AND state != "UNKNOWN" AND state != "CRASHED"  AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF state = "RUNNING" AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END;
--

--
CREATE TRIGGER `kinton`.`delete_nodevirtualimage_update_stats` AFTER DELETE ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE oldState VARCHAR(50) CHARACTER SET utf8;
    DECLARE type INTEGER;
    DECLARE isUsingIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = OLD.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
    SELECT state, idType INTO oldState, type FROM virtualmachine WHERE idVM = OLD.idVM;
    --
    IF type = 1 THEN
      IF oldState != "NOT_DEPLOYED" AND oldState != "UNKNOWN" AND oldState != "CRASHED" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF oldState = "RUNNING" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END IF;
  END;
--

--
CREATE TRIGGER `kinton`.`create_rasd_management_update_stats` AFTER INSERT ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;
        DECLARE resourceName VARCHAR(255) CHARACTER SET utf8;
        SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
        SELECT elementName, limitResource INTO resourceName, limitResourceObj
        FROM rasd r
        WHERE r.instanceID = NEW.idResource;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF NEW.idResourceType='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('CREATE_STORAGE', NEW.idResource, resourceName, NEW.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
                END IF;               
            END IF;
        END IF;
    END;
--

--
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
--
|
--
CREATE TRIGGER `kinton`.`create_volume_management_update_stats` AFTER INSERT ON `kinton`.`volume_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;
        DECLARE idResourceObj VARCHAR(50);
        DECLARE idResourceTypeObj VARCHAR(5);
       DECLARE idStorageTier INTEGER;
        DECLARE resourceName VARCHAR(255) CHARACTER SET utf8;
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
|
--

CREATE TRIGGER `KINTON`.`CREATE_DATASTORE_UPDATE_STATS` AFTER INSERT ON `KINTON`.`DATASTORE_ASSIGNMENT`
FOR EACH ROW BEGIN
DECLARE MACHINESTATE INT UNSIGNED;
DECLARE IDDATACENTER INT UNSIGNED;
DECLARE ENABLED INT UNSIGNED;
DECLARE USEDSIZE BIGINT UNSIGNED;
DECLARE SIZE BIGINT UNSIGNED;
SELECT PM.IDSTATE, PM.IDDATACENTER INTO MACHINESTATE, IDDATACENTER FROM PHYSICALMACHINE PM WHERE PM.IDPHYSICALMACHINE = NEW.IDPHYSICALMACHINE;
SELECT D.ENABLED, D.USEDSIZE, D.SIZE INTO ENABLED, USEDSIZE, SIZE FROM DATASTORE D WHERE D.IDDATASTORE = NEW.IDDATASTORE;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF MACHINESTATE = 3 THEN
        IF ENABLED = 1 THEN
            UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGEUSED = CUS.VSTORAGEUSED + USEDSIZE
            WHERE CUS.IDDATACENTER = IDDATACENTER;
        END IF;
    END IF;
    IF MACHINESTATE != 2 THEN
        IF ENABLED = 1 THEN
            UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL + SIZE
            WHERE CUS.IDDATACENTER = IDDATACENTER;
        END IF;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `KINTON`.`DELETE_PHYSICALMACHINE_UPDATE_STATS` AFTER DELETE ON `KINTON`.`PHYSICALMACHINE`
FOR EACH ROW BEGIN
DECLARE DATASTOREUSEDSIZE BIGINT UNSIGNED;
DECLARE DATASTORESIZE BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.IDSTATE = 3 THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSRUNNING = SERVERSRUNNING-1,
               VCPUUSED=VCPUUSED-OLD.CPUUSED, VMEMORYUSED=VMEMORYUSED-OLD.RAMUSED
        WHERE IDDATACENTER = OLD.IDDATACENTER;
    END IF;
    IF OLD.IDSTATE NOT IN (2, 6, 7) THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSTOTAL=SERVERSTOTAL-1,
               VCPUTOTAL=VCPUTOTAL-(OLD.CPU*OLD.CPURATIO), VMEMORYTOTAL=VMEMORYTOTAL-OLD.RAM
        WHERE IDDATACENTER = OLD.IDDATACENTER;
    END IF;
END IF;
END;

--
|
--

CREATE TRIGGER `KINTON`.`DELETE_DATASTORE_UPDATE_STATS` BEFORE DELETE ON `KINTON`.`DATASTORE`
FOR EACH ROW BEGIN
DECLARE MACHINESTATE INT UNSIGNED;
DECLARE IDDATACENTER INT UNSIGNED;
SELECT PM.IDSTATE, PM.IDDATACENTER INTO MACHINESTATE, IDDATACENTER FROM PHYSICALMACHINE PM LEFT OUTER JOIN DATASTORE_ASSIGNMENT DA ON PM.IDPHYSICALMACHINE = DA.IDPHYSICALMACHINE
WHERE DA.IDDATASTORE = OLD.IDDATASTORE;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF MACHINESTATE = 3 THEN
        IF OLD.ENABLED = 1 THEN
            UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGEUSED = CUS.VSTORAGEUSED - OLD.USEDSIZE
            WHERE CUS.IDDATACENTER = IDDATACENTER;
        END IF;
    END IF;
    IF MACHINESTATE NOT IN (2, 6, 7) THEN
        IF OLD.ENABLED = 1 THEN
            UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL - OLD.SIZE
            WHERE CUS.IDDATACENTER = IDDATACENTER;
        END IF;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `KINTON`.`UPDATE_PHYSICALMACHINE_UPDATE_STATS` AFTER UPDATE ON `KINTON`.`PHYSICALMACHINE`
FOR EACH ROW BEGIN
DECLARE DATASTORESIZE BIGINT UNSIGNED;
DECLARE OLDDATASTORESIZE BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.IDSTATE != NEW.IDSTATE THEN
        IF OLD.IDSTATE IN (2, 7) THEN
            -- MACHINE NOT MANAGED CHANGES INTO MANAGED; OR DISABLED_BY_HA TO MANAGED
            UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSTOTAL=SERVERSTOTAL+1,
                   VCPUTOTAL=VCPUTOTAL + (NEW.CPU*NEW.CPURATIO),
                   VMEMORYTOTAL=VMEMORYTOTAL + NEW.RAM
            WHERE IDDATACENTER = NEW.IDDATACENTER;
        END IF;
        IF NEW.IDSTATE IN (2,7) THEN
            -- MACHINE MANAGED CHANGES INTO NOT MANAGED OR DISABLEDBYHA
            UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSTOTAL=SERVERSTOTAL-1,
                   VCPUTOTAL=VCPUTOTAL-(OLD.CPU*OLD.CPURATIO),
                   VMEMORYTOTAL=VMEMORYTOTAL-OLD.RAM
            WHERE IDDATACENTER = OLD.IDDATACENTER;
        END IF;
        IF NEW.IDSTATE = 3 THEN
            -- STOPPED / HALTED / NOT PROVISIONED PASSES TO MANAGED (RUNNING)
            UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSRUNNING = SERVERSRUNNING+1,
                   VCPUUSED=VCPUUSED+NEW.CPUUSED,
                   VMEMORYUSED=VMEMORYUSED+NEW.RAMUSED
            WHERE IDDATACENTER = NEW.IDDATACENTER;
        ELSEIF OLD.IDSTATE = 3 THEN
            -- MANAGED (RUNNING) PASSES TO STOPPED / HALTED / NOT PROVISIONED
            UPDATE IGNORE CLOUD_USAGE_STATS SET SERVERSRUNNING = SERVERSRUNNING-1,
                   VCPUUSED=VCPUUSED-OLD.CPUUSED,
                   VMEMORYUSED=VMEMORYUSED-OLD.RAMUSED
            WHERE IDDATACENTER = OLD.IDDATACENTER;
        END IF;
    ELSE
        -- NO STATE CHANGES
        IF NEW.IDSTATE NOT IN (2, 6, 7) THEN
            -- IF MACHINE IS IN A NOT MANAGED STATE, CHANGES INTO RESOURCES ARE IGNORED, SHOULD WE ADD 'DISABLED' STATE TO THIS CONDITION?
            UPDATE IGNORE CLOUD_USAGE_STATS SET VCPUTOTAL=VCPUTOTAL+((NEW.CPU-OLD.CPU)*NEW.CPURATIO),
                   VMEMORYTOTAL=VMEMORYTOTAL + (NEW.RAM-OLD.RAM)
            WHERE IDDATACENTER = OLD.IDDATACENTER;
        END IF;
        --
        IF NEW.IDSTATE = 3 THEN
            UPDATE IGNORE CLOUD_USAGE_STATS SET VCPUUSED=VCPUUSED + (NEW.CPUUSED-OLD.CPUUSED),
                   VMEMORYUSED=VMEMORYUSED + (NEW.RAMUSED-OLD.RAMUSED)
            WHERE IDDATACENTER = OLD.IDDATACENTER;
        END IF;
    END IF;
END IF;
END;

--
|
--

CREATE TRIGGER `KINTON`.`UPDATE_DATASTORE_UPDATE_STATS` AFTER UPDATE ON `KINTON`.`DATASTORE`
    FOR EACH ROW BEGIN
    DECLARE IDDATACENTER INT UNSIGNED;
    DECLARE MACHINESTATE INT UNSIGNED;
    SELECT PM.IDDATACENTER, PM.IDSTATE INTO IDDATACENTER, MACHINESTATE FROM PHYSICALMACHINE PM LEFT OUTER JOIN DATASTORE_ASSIGNMENT DA ON PM.IDPHYSICALMACHINE = DA.IDPHYSICALMACHINE
    WHERE DA.IDDATASTORE = NEW.IDDATASTORE;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
        IF OLD.ENABLED = 1 THEN
        IF NEW.ENABLED = 1 THEN
            IF MACHINESTATE IN (2, 6, 7) THEN
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL - OLD.SIZE + NEW.SIZE
                WHERE CUS.IDDATACENTER = IDDATACENTER;
            ELSE
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL - OLD.SIZE + NEW.SIZE,
                CUS.VSTORAGEUSED = CUS.VSTORAGEUSED - OLD.USEDSIZE + NEW.USEDSIZE WHERE CUS.IDDATACENTER = IDDATACENTER;
            END IF;
        ELSEIF NEW.ENABLED = 0 THEN
            IF MACHINESTATE IN (2, 6, 7) THEN
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL - OLD.SIZE
                WHERE CUS.IDDATACENTER = IDDATACENTER;
            ELSE
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL - OLD.SIZE,
                CUS.VSTORAGEUSED = CUS.VSTORAGEUSED - OLD.USEDSIZE WHERE CUS.IDDATACENTER = IDDATACENTER;
            END IF;
        END IF;
        ELSE
        IF NEW.ENABLED = 1 THEN
            IF MACHINESTATE IN (2, 6, 7) THEN
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL + NEW.SIZE
                WHERE CUS.IDDATACENTER = IDDATACENTER;
            ELSE
                UPDATE IGNORE CLOUD_USAGE_STATS CUS SET CUS.VSTORAGETOTAL = CUS.VSTORAGETOTAL + NEW.SIZE,
                CUS.VSTORAGEUSED = CUS.VSTORAGEUSED + NEW.USEDSIZE WHERE CUS.IDDATACENTER = IDDATACENTER;
            END IF;
        END IF;
        END IF;
    END IF;
    END;
--
|
--

CREATE TRIGGER `KINTON`.`UPDATE_VIRTUALAPP_UPDATE_STATS` AFTER UPDATE ON `KINTON`.`VIRTUALAPP`
  FOR EACH ROW BEGIN
    DECLARE NUMVMACHINESCREATED INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    -- V2V: VMACHINES MOVED BETWEEN VDC
  IF NEW.IDVIRTUALDATACENTER != OLD.IDVIRTUALDATACENTER THEN
      -- CALCULATE VMACHINES TOTAL AND RUNNING IN THIS VAPP
      SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO NUMVMACHINESCREATED
      FROM NODEVIRTUALIMAGE NVI, VIRTUALMACHINE V, NODE N
      WHERE NVI.IDNODE IS NOT NULL
      AND V.IDVM = NVI.IDVM
      AND N.IDNODE = NVI.IDNODE
      AND N.IDVIRTUALAPP = NEW.IDVIRTUALAPP
      AND V.STATE != "NOT_ALLOCATED" AND V.STATE != "UNKNOWN"
      AND V.IDTYPE = 1;
      UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED- NUMVMACHINESCREATED WHERE IDVIRTUALDATACENTER = OLD.IDVIRTUALDATACENTER;
      UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED+ NUMVMACHINESCREATED WHERE IDVIRTUALDATACENTER = NEW.IDVIRTUALDATACENTER;
    END IF;
    -- CHECKS FOR CHANGES
    IF OLD.NAME != NEW.NAME THEN
      -- NAME CHANGED !!!
      UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VAPPNAME = NEW.NAME
      WHERE IDVIRTUALAPP = NEW.IDVIRTUALAPP;
    END IF;
  END IF;
  END;
--
|
--
CREATE TRIGGER `KINTON`.`CREATE_VIRTUALMACHINE_UPDATE_STATS` AFTER INSERT ON `KINTON`.`VIRTUALMACHINE`
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		INSERT INTO VIRTUALMACHINETRACKEDSTATE (IDVM) VALUES (NEW.IDVM);
	END IF;
    END;
--
|
--	
CREATE TRIGGER `KINTON`.`DELETE_VIRTUALMACHINE_UPDATE_STATS` AFTER DELETE ON `KINTON`.`VIRTUALMACHINE`
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		DELETE FROM VIRTUALMACHINETRACKEDSTATE WHERE IDVM = OLD.IDVM;
	END IF;
    END;
--
|
--	

/* COST CODE HAS CHANGED FROM STRING TO INT */
DROP TRIGGER IF EXISTS `KINTON`.`UPDATE_VIRTUALMACHINE_UPDATE_STATS`;
CREATE TRIGGER `KINTON`.`UPDATE_VIRTUALMACHINE_UPDATE_STATS` AFTER UPDATE ON `KINTON`.`VIRTUALMACHINE`
    FOR EACH ROW BEGIN
        DECLARE IDDATACENTEROBJ INTEGER;
        DECLARE IDVIRTUALAPPOBJ INTEGER;
        DECLARE IDVIRTUALDATACENTEROBJ INTEGER;
        DECLARE COSTCODEOBJ INT(4);
	DECLARE PREVIOUSSTATE VARCHAR(50);
	-- FOR DEBUGGING PURPOSES ONLY
        -- INSERT INTO DEBUG_MSG (MSG) VALUES (CONCAT('UPDATE: ', OLD.IDTYPE, NEW.IDTYPE, OLD.STATE, NEW.STATE));	
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
	-- WE ALWAYS STORE PREVIOUS STATE WHEN STARTING A TRANSACTION
	IF NEW.STATE != OLD.STATE AND NEW.STATE='LOCKED' THEN
		UPDATE VIRTUALMACHINETRACKEDSTATE SET PREVIOUSSTATE=OLD.STATE WHERE IDVM=NEW.IDVM;
	END IF;
	--
	SELECT VMTS.PREVIOUSSTATE INTO PREVIOUSSTATE
        FROM VIRTUALMACHINETRACKEDSTATE VMTS
	WHERE VMTS.IDVM = NEW.IDVM;
        --  UPDATING ENTERPRISE_RESOURCES_STATS: VCPU USED, MEMORY USED, LOCAL STORAGE USED
        IF OLD.IDHYPERVISOR IS NULL OR (OLD.IDHYPERVISOR != NEW.IDHYPERVISOR) THEN
            SELECT PM.IDDATACENTER INTO IDDATACENTEROBJ
            FROM HYPERVISOR HY, PHYSICALMACHINE PM
            WHERE NEW.IDHYPERVISOR=HY.ID
            AND HY.IDPHYSICALMACHINE=PM.IDPHYSICALMACHINE;
        ELSE 
            SELECT PM.IDDATACENTER INTO IDDATACENTEROBJ
            FROM HYPERVISOR HY, PHYSICALMACHINE PM
            WHERE OLD.IDHYPERVISOR=HY.ID
            AND HY.IDPHYSICALMACHINE=PM.IDPHYSICALMACHINE;
        END IF;     
        --
        SELECT N.IDVIRTUALAPP, VAPP.IDVIRTUALDATACENTER INTO IDVIRTUALAPPOBJ, IDVIRTUALDATACENTEROBJ
        FROM NODEVIRTUALIMAGE NVI, NODE N, VIRTUALAPP VAPP
        WHERE NEW.IDVM = NVI.IDVM
        AND NVI.IDNODE = N.IDNODE
        AND VAPP.IDVIRTUALAPP = N.IDVIRTUALAPP;   
	--
	IF NEW.IDTYPE = 1 AND OLD.IDTYPE = 0 THEN
		-- IMPORTED !!!
		UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESTOTAL = VMACHINESTOTAL+1
                WHERE IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
                WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
		IF NEW.STATE = "ON" AND PREVIOUSSTATE != "ON" THEN 	
			UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
		        WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
		        UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
		        WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
		        UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESRUNNING = VMACHINESRUNNING+1
		        WHERE IDDATACENTER = IDDATACENTEROBJ;       
		        UPDATE IGNORE ENTERPRISE_RESOURCES_STATS 
		            SET VCPUUSED = VCPUUSED + NEW.CPU,
		                MEMORYUSED = MEMORYUSED + NEW.RAM,
		                LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
		        WHERE IDENTERPRISE = NEW.IDENTERPRISE;
		        UPDATE IGNORE DC_ENTERPRISE_STATS 
		        SET     VCPUUSED = VCPUUSED + NEW.CPU,
		            MEMORYUSED = MEMORYUSED + NEW.RAM,
		            LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
		        WHERE IDENTERPRISE = NEW.IDENTERPRISE AND IDDATACENTER = IDDATACENTEROBJ;
		        UPDATE IGNORE VDC_ENTERPRISE_STATS 
		        SET     VCPUUSED = VCPUUSED + NEW.CPU,
		            MEMORYUSED = MEMORYUSED + NEW.RAM,
		            LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
		        WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;	
		END IF;
	-- MAIN CASE: AN IMPORTED VM CHANGES ITS STATE (FROM LOCKED TO ...)
	ELSEIF NEW.IDTYPE = 1 AND (NEW.STATE != OLD.STATE) THEN
            IF NEW.STATE = "ON" AND PREVIOUSSTATE != "ON" THEN 
                -- NEW ACTIVE
                UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
                WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
                UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESRUNNING = VMACHINESRUNNING+1
                WHERE IDDATACENTER = IDDATACENTEROBJ;       
                UPDATE IGNORE ENTERPRISE_RESOURCES_STATS 
                    SET VCPUUSED = VCPUUSED + NEW.CPU,
                        MEMORYUSED = MEMORYUSED + NEW.RAM,
                        LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
                WHERE IDENTERPRISE = NEW.IDENTERPRISE;
                UPDATE IGNORE DC_ENTERPRISE_STATS 
                SET     VCPUUSED = VCPUUSED + NEW.CPU,
                    MEMORYUSED = MEMORYUSED + NEW.RAM,
                    LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
                WHERE IDENTERPRISE = NEW.IDENTERPRISE AND IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS 
                SET     VCPUUSED = VCPUUSED + NEW.CPU,
                    MEMORYUSED = MEMORYUSED + NEW.RAM,
                    LOCALSTORAGEUSED = LOCALSTORAGEUSED + NEW.HD
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
-- CLOUD_USAGE_STATS USED STATS (VCPUUSED, VMEMORYUSED, VSTORAGEUSED) ARE UPDATED FROM UPDATE_PHYSICAL_MACHINE_UPDATE_STATS TRIGGER
            -- ELSEIF OLD.STATE = "ON" THEN           * THIS HAS TO CHANGE, OLD.STATE IS ALWAYS LOCKED
		ELSEIF (NEW.STATE = "OFF" AND PREVIOUSSTATE != "OFF") OR (NEW.STATE = "PAUSED" AND PREVIOUSSTATE != "OFF") THEN
                -- ACTIVE OUT
                UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE-1
                WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE-1
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
                UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESRUNNING = VMACHINESRUNNING-1
                WHERE IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE ENTERPRISE_RESOURCES_STATS 
                    SET VCPUUSED = VCPUUSED - NEW.CPU,
                        MEMORYUSED = MEMORYUSED - NEW.RAM,
                        LOCALSTORAGEUSED = LOCALSTORAGEUSED - NEW.HD
                WHERE IDENTERPRISE = NEW.IDENTERPRISE;
                UPDATE IGNORE DC_ENTERPRISE_STATS 
                SET     VCPUUSED = VCPUUSED - NEW.CPU,
                    MEMORYUSED = MEMORYUSED - NEW.RAM,
                    LOCALSTORAGEUSED = LOCALSTORAGEUSED - NEW.HD
                WHERE IDENTERPRISE = NEW.IDENTERPRISE AND IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS 
                SET     VCPUUSED = VCPUUSED - NEW.CPU,
                    MEMORYUSED = MEMORYUSED - NEW.RAM,
                    LOCALSTORAGEUSED = LOCALSTORAGEUSED - NEW.HD
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ; 
-- CLOUD_USAGE_STATS USED STATS (VCPUUSED, VMEMORYUSED, VSTORAGEUSED) ARE UPDATED FROM UPDATE_PHYSICAL_MACHINE_UPDATE_STATS TRIGGER
            END IF;     	    
            IF NEW.STATE = "CONFIGURED" AND PREVIOUSSTATE != "CONFIGURED" THEN -- OR OLD.IDTYPE != NEW.IDTYPE
                -- VMACHINE DEPLOYED OR VMACHINE IMPORTED
                UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESTOTAL = VMACHINESTOTAL+1
                WHERE IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
                WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
            ELSEIF NEW.STATE = "NOT_ALLOCATED"  AND PREVIOUSSTATE != "NOT_ALLOCATED" THEN 
                -- VMACHINE WAS DECONFIGURED (STILL ALLOCATED)
                UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESTOTAL = VMACHINESTOTAL-1
                WHERE IDDATACENTER = IDDATACENTEROBJ;
                UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMCREATED = VMCREATED-1
                WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
                UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED-1
                WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
            END IF;         
        END IF;
        --
        SELECT IF(VI.COST_CODE IS NULL, 0, VI.COST_CODE) INTO COSTCODEOBJ
        FROM VIRTUALIMAGE VI
        WHERE VI.IDIMAGE = NEW.IDIMAGE;
        -- REGISTER ACCOUNTING EVENTS
        IF EXISTS( SELECT * FROM `INFORMATION_SCHEMA`.ROUTINES WHERE ROUTINE_SCHEMA='KINTON' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='ACCOUNTINGVMREGISTEREVENTS' ) THEN
       		 IF EXISTS(SELECT * FROM VIRTUALIMAGE VI WHERE VI.IDIMAGE=NEW.IDIMAGE AND VI.IDREPOSITORY IS NOT NULL) THEN 
	          CALL ACCOUNTINGVMREGISTEREVENTS(NEW.IDVM, NEW.IDTYPE, OLD.STATE, NEW.STATE, PREVIOUSSTATE, NEW.RAM, NEW.CPU, NEW.HD, COSTCODEOBJ);
       		 END IF;              
	    END IF;
      END IF;
    END;
--
|
--
CREATE TRIGGER `KINTON`.`CREATE_NODEVIRTUALIMAGE_UPDATE_STATS` AFTER INSERT ON `KINTON`.`NODEVIRTUALIMAGE`
  FOR EACH ROW BEGIN
    DECLARE IDDATACENTEROBJ INTEGER;
    DECLARE IDVIRTUALAPPOBJ INTEGER;
    DECLARE IDVIRTUALDATACENTEROBJ INTEGER;
    DECLARE STATE VARCHAR(50);
    DECLARE TYPE INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT VAPP.IDVIRTUALAPP, VAPP.IDVIRTUALDATACENTER, VDC.IDDATACENTER INTO IDVIRTUALAPPOBJ, IDVIRTUALDATACENTEROBJ, IDDATACENTEROBJ
      FROM NODE N, VIRTUALAPP VAPP, VIRTUALDATACENTER VDC
      WHERE VDC.IDVIRTUALDATACENTER = VAPP.IDVIRTUALDATACENTER
      AND N.IDNODE = NEW.IDNODE
      AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP;
      SELECT VM.STATE, VM.IDTYPE INTO STATE, TYPE FROM VIRTUALMACHINE VM WHERE VM.IDVM = NEW.IDVM;
      --
      IF STATE != "NOT_ALLOCATED" AND STATE != "UNKNOWN" AND TYPE = 1 THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESTOTAL = VMACHINESTOTAL+1
        WHERE IDDATACENTER = IDDATACENTEROBJ;
        UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
        WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
        UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED+1
        WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
      END IF;
      --
      IF STATE = "ON" AND TYPE = 1 THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESRUNNING = VMACHINESRUNNING+1
        WHERE IDDATACENTER = IDDATACENTEROBJ;
        UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
        WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
        UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE+1
        WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
      END IF;
    END IF;
  END;
--
|
--
CREATE TRIGGER `KINTON`.`DELETE_NODEVIRTUALIMAGE_UPDATE_STATS` AFTER DELETE ON `KINTON`.`NODEVIRTUALIMAGE`
  FOR EACH ROW BEGIN
    DECLARE IDDATACENTEROBJ INTEGER;
    DECLARE IDVIRTUALAPPOBJ INTEGER;
    DECLARE IDVIRTUALDATACENTEROBJ INTEGER;
    DECLARE OLDSTATE VARCHAR(50);
    DECLARE TYPE INTEGER;
    DECLARE ISUSINGIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT VAPP.IDVIRTUALAPP, VAPP.IDVIRTUALDATACENTER, VDC.IDDATACENTER INTO IDVIRTUALAPPOBJ, IDVIRTUALDATACENTEROBJ, IDDATACENTEROBJ
      FROM NODE N, VIRTUALAPP VAPP, VIRTUALDATACENTER VDC
      WHERE VDC.IDVIRTUALDATACENTER = VAPP.IDVIRTUALDATACENTER
      AND N.IDNODE = OLD.IDNODE
      AND N.IDVIRTUALAPP = VAPP.IDVIRTUALAPP;
    SELECT STATE, IDTYPE INTO OLDSTATE, TYPE FROM VIRTUALMACHINE WHERE IDVM = OLD.IDVM;
    --
    IF TYPE = 1 THEN
      IF OLDSTATE != "NOT_ALLOCATED" AND OLDSTATE != "UNKNOWN" THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESTOTAL = VMACHINESTOTAL-1
          WHERE IDDATACENTER = IDDATACENTEROBJ;
        UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMCREATED = VMCREATED-1
          WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
        UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMCREATED = VMCREATED-1
          WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
      END IF;
      --
      IF OLDSTATE = "ON" THEN
        UPDATE IGNORE CLOUD_USAGE_STATS SET VMACHINESRUNNING = VMACHINESRUNNING-1
        WHERE IDDATACENTER = IDDATACENTEROBJ;
        UPDATE IGNORE VAPP_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE-1
        WHERE IDVIRTUALAPP = IDVIRTUALAPPOBJ;
        UPDATE IGNORE VDC_ENTERPRISE_STATS SET VMACTIVE = VMACTIVE-1
        WHERE IDVIRTUALDATACENTER = IDVIRTUALDATACENTEROBJ;
      END IF;
    END IF;
  END IF;
  END;
--
|
--
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
                AND (vm.state = 1 OR vm.state = 2);
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
--
|
--
DELIMITER ;
