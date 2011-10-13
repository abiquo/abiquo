
-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures


-- [ABICLOUDPREMIUM-2057]
UPDATE kinton.metering SET actionperformed="VAPP_INSTANCE" WHERE actionperformed="VAPP_BUNDLE";

-- ---------------------------------------------- --
--                 TABLE DROP                     --
-- ---------------------------------------------- --

-- PRICING --
-- DROP THE TABLES RELATED TO PRICING --
DROP TABLE IF EXISTS `kinton`.`pricing_template`;
DROP TABLE IF EXISTS `kinton`.`costCode`;
DROP TABLE IF EXISTS `kinton`.`pricingCostCode`;
DROP TABLE IF EXISTS `kinton`.`pricingTier`;
DROP TABLE IF EXISTS `kinton`.`currency`;
DROP TABLE IF EXISTS `kinton`.`costCodeCurrency`;
ALTER TABLE `kinton`.`ip_pool_management` DROP FOREIGN KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP COLUMN dhcp_service_id;
ALTER TABLE `kinton`.`network_configuration` DROP FOREIGN KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP COLUMN `dhcp_service_id`;

DROP TABLE IF EXISTS `kinton`.`dhcp_service`;


-- ---------------------------------------------- --
--                  TABLE CREATION                --
-- ---------------------------------------------- --

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
-- Definition of table `kinton`.`pricing`
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
alter table repository modify URL varchar(255);
-- [UCS]
ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `vswitchName` varchar(200) NOT NULL;
ALTER TABLE `kinton`.`vlan_network` ADD COLUMN `networktype` varchar(15) DEFAULT 'internal';
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`ip_pool_management` ADD COLUMN `available` boolean NOT NULL default 1; 

-- PRICING --
-- ADD THE COLUMN ID_PRICING TO ENTERPRISE --
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idPricingTemplate` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricing_template` (`idPricingTemplate`);

ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage;

ALTER TABLE `kinton`.`virtualimage` MODIFY COLUMN `cost_code` int(4) DEFAULT 0;

-- PHYSICAL MACHINE --
ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage, DROP COLUMN hd, DROP COLUMN hdUsed;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");

-- First I need to update some rows before to delete the `default_network` field
UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;

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
 ("client.wiki.pricing.createCurrency","","Currency creation wiki");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_ip_pool_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_network_configuration_update_stats`;

--
--
--
DELIMITER |
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) DEFAULT '';
    DECLARE no_more_ipsfreed INT;
    DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement   
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.vlan_network_id = vn.vlan_network_id
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
--
--

CREATE TRIGGER `kinton`.`update_rasd_management_update_stats` AFTER UPDATE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50);
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE ipAddress VARCHAR(20);
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
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed - reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed - reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed - reservedSize
                WHERE idVirtualDataCenter = OLD.idVirtualDatacenter;
                    UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
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
            -- From old `autoDetachVolume`
            -- UPDATE IGNORE volume_management v set v.state = 0
            -- WHERE v.idManagement = OLD.idManagement;
            -- Checks for used IPs
            IF OLD.idVM IS NULL AND NEW.idVM IS NOT NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
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
            END IF;
            -- Checks for unused IPs
            IF OLD.idVM IS NOT NULL AND NEW.idVM IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
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
                    IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                        CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
                    END IF;                    
                END IF;
            END IF;
        END IF;
    END;
    
|  

CREATE TRIGGER `kinton`.`delete_ip_pool_management_update_stats` AFTER DELETE ON `kinton`.`ip_pool_management`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      -- Query for Public Ips deleted (disabled)
      SELECT distinct dc.idDataCenter INTO idDataCenterObj
      FROM vlan_network vn, network_configuration nc, datacenter dc
       WHERE OLD.vlan_network_id = vn.vlan_network_id
      AND vn.network_id = dc.network_id;
      IF idDataCenterObj IS NOT NULL THEN
    -- detects IP disabled/enabled at Edit Public Ips
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|

/* cost code has changed from string to int */
DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE costCodeObj int(4);
    -- For debugging purposes only
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', OLD.idType, NEW.idType, OLD.state, NEW.state));   
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
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

    IF NEW.idType = 1 AND OLD.idType = 0 THEN
        -- Imported !!!
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
        IF NEW.state = "RUNNING" THEN   
            UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
                WHERE idDataCenter = idDataCenterObj;       
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 
        END IF;
    ELSEIF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "RUNNING" THEN 
                -- New Active
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
                WHERE idDataCenter = idDataCenterObj;       
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            ELSEIF OLD.state = "RUNNING" THEN           
                -- Active Out
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed - NEW.cpu,
                        memoryUsed = memoryUsed - NEW.ram,
                        localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            END IF;     
            IF OLD.state = "NOT_DEPLOYED" OR OLD.state = "UNKNOWN"  THEN -- OR OLD.idType != NEW.idType
                -- VMachine Deployed or VMachine imported
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = "NOT_DEPLOYED" OR NEW.state = "CRASHED" OR (NEW.state = "UNKNOWN" AND OLD.state != "CRASHED") THEN 
                -- VMachine Undeployed
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;         
        END IF;
        --
        SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
             IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN 
              CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
             END IF;              
        END IF;
      END IF;
    END;
|
DELIMITER ;

-- *************************************************
-- Triggers ON Physical Machine
-- *************************************************

DROP TRIGGER IF EXISTS `kinton`.`create_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`; 
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_datastore_update_stats`;


DELIMITER |

CREATE TRIGGER `kinton`.`create_physicalmachine_update_stats` AFTER INSERT ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
               vCpuUsed=vCpuUsed+NEW.cpuUsed, vMemoryUsed=vMemoryUsed+NEW.ramUsed
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
    IF NEW.idState != 2 THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal = serversTotal+1, 
               vCpuTotal=vCpuTotal+(NEW.cpu*NEW.cpuRatio), vMemoryTotal=vMemoryTotal+NEW.ram
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `kinton`.`create_datastore_update_stats` AFTER INSERT ON `kinton`.`datastore_assignment`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
DECLARE enabled INT UNSIGNED;
DECLARE usedSize BIGINT UNSIGNED;
DECLARE size BIGINT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm WHERE pm.idPhysicalMachine = NEW.idPhysicalmachine;
SELECT d.enabled, d.usedSize, d.size INTO enabled, usedSize, size FROM datastore d WHERE d.idDatastore = NEW.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
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
END

--
|
--

CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
               vCpuUsed=vCpuUsed-OLD.cpuUsed, vMemoryUsed=vMemoryUsed-OLD.ramUsed
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
               vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio), vMemoryTotal=vMemoryTotal-OLD.ram
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
END IF;
END;

--
|
--

CREATE TRIGGER `kinton`.`delete_datastore_update_stats` BEFORE DELETE ON `kinton`.`datastore`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = OLD.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
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
END

--
|
--

CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreSize BIGINT UNSIGNED;
DECLARE oldDatastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
            -- Machine not managed changes into managed; or disabled_by_ha to Managed
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1,
                   vCpuTotal=vCpuTotal + (NEW.cpu*NEW.cpuRatio),
                   vMemoryTotal=vMemoryTotal + NEW.ram
            WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
            -- Machine managed changes into not managed or DisabledByHA
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
                   vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
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
            UPDATE IGNORE cloud_usage_stats SET vCpuTotal=vCpuTotal+((NEW.cpu-OLD.cpu)*NEW.cpuRatio),
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

--
|
--

CREATE TRIGGER `kinton`.`update_datastore_update_stats` AFTER UPDATE ON `kinton`.`datastore`
FOR EACH ROW BEGIN
DECLARE idDatacenter INT UNSIGNED;
DECLARE machineState INT UNSIGNED;
SELECT pm.idDatacenter, pm.idState INTO idDatacenter, machineState FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = NEW.idDatastore;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
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
END;

--
|
--

DELIMITER ;

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

-- *************************************************
--
--  Procedures to calculate datastore size
--
-- *************************************************
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;

DELIMITER |

CREATE PROCEDURE `kinton`.`get_datastore_size_by_dc`(IN idDC INT, OUT size BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(d.size) IS NULL,0,SUM(d.size)) INTO size
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size_by_dc`(IN idDC INT, OUT usedSize BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(d.usedSize) IS NULL,0,SUM(d.usedSize)) INTO usedSize
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1;
END
--
|
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
    AND (vm.state = 1 OR vm.state = 2)
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
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED"
    and v.idType = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesRunning
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state = "RUNNING"
    and v.idType = 1;
    --
    SELECT IF (SUM(cpu*cpuRatio) IS NULL,0,SUM(cpu*cpuRatio)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)) INTO vCpuTotal, vMemoryTotal, vCpuUsed, vMemoryUsed
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
--
|
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
    WHERE vm.state = "RUNNING"
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
    AND (vm.state = 1 OR vm.state = 2)
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

CREATE PROCEDURE `kinton`.CalculateVdcEnterpriseStats()
   BEGIN
  DECLARE idVirtualDataCenterObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE vdcName VARCHAR(45);
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
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state = "RUNNING";
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
    AND state = 2;
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
    AND vm.state = "RUNNING"
    AND vm.idType = 1
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO extStorageUsed
    FROM rasd_management rm, rasd r, volume_management vm
    WHERE rm.idManagement = vm.idManagement    
    AND r.instanceID = rm.idResource
    AND (vm.state = 1 OR vm.state = 2)
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
DELIMITER ; 
CALL `kinton`.`CalculateCloudUsageStats`();
CALL `kinton`.`CalculateEnterpriseResourcesStats`();
CALL `kinton`.`CalculateVdcEnterpriseStats`();

  

  

