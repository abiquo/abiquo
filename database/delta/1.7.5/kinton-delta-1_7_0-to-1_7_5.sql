--
-- Modify virtualapp to change nodeconnections column
--
alter table virtualapp modify nodeconnections text;

alter table hypervisor drop column description;

-- Update the leases name
update ip_pool_management set name=replace(name,':','');

-- DELETE THE OBSOLETE PUBLIC IP TABLE --
DROP TABLE IF EXISTS `kinton`.`publicip`;

--
-- STATEFUL REFRACTOR TABLES MIGRATION
--

-- 
-- Definition of table `kinton`.`tier`.
--
DROP TABLE IF EXISTS `kinton`.`tier`;
CREATE TABLE `kinton`.`tier` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(40) NOT NULL,
    `description` varchar(255) NOT NULL,
    `idDataCenter` int(10) unsigned NOT NULL,
    `isEnabled` tinyint(1) unsigned NOT NULL default '1',
    `version_c` integer NOT NULL DEFAULT 1,
     PRIMARY KEY  (`id`),
     CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

INSERT INTO `kinton`.`tier` (name, description, idDataCenter, isEnabled)
SELECT "Default tier 1" , "Description of default tier 1", idDataCenter, 1
FROM datacenter;

INSERT INTO `kinton`.`tier` (name, description, idDataCenter, isEnabled)
SELECT "Default tier 2" , "Description of default tier 2", idDataCenter, 1
FROM datacenter;

INSERT INTO `kinton`.`tier` (name, description, idDataCenter, isEnabled)
SELECT "Default tier 3" , "Description of default tier 3", idDataCenter, 1
FROM datacenter;

INSERT INTO `kinton`.`tier` (name, description, idDataCenter, isEnabled)
SELECT "Default tier 4" , "Description of default tier 4", idDataCenter, 1
FROM datacenter;

--
-- Definition of table `kinton`.`cabin`
--

DROP TABLE IF EXISTS `kinton`.`storage_device`;
CREATE TABLE `kinton`.`storage_device` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `idDataCenter` int(10) unsigned NOT NULL,
  `management_ip` varchar(256) NOT NULL,
  `management_port` int(5) unsigned NOT NULL DEFAULT '0',
  `iscsi_ip` varchar(256) NOT NULL,
  `iscsi_port` int(5) unsigned NOT NULL DEFAULT '0',
  `storage_technology` varchar(256) DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`id`),
  CONSTRAINT `cabinet_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Update `storage_device` table with previous `storage_pool` table
INSERT INTO `kinton`.`storage_device` (name, idDataCenter, management_ip, management_port, iscsi_ip, iscsi_port, storage_technology)
SELECT CONCAT(s.name, '_device') , 
       r.idDatacenter,substring_index(substring_index(s.url_management,':',2),'//',-1), 
       substring_index(substring_index(s.url_management,':',-1),'/',1), 
       s.host_ip, 
       s.host_port, 
       s.storage_technology 
FROM `storage_pool` s, `remote_service` r 
WHERE s.idRemoteService = r.idRemoteService;

-- REESTRUCTURE THE STORAGE_POOL TABLE
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `idStorageDevice` int(10) unsigned NOT NULL;
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `idTier` int(10) unsigned NOT NULL;
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `isEnabled` tinyint(1) unsigned NOT NULL default '1';
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `totalSizeInMb` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `usedSizeInMb` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE `kinton`.`storage_pool` ADD COLUMN `availableSizeInMb` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0;

/*!40000 ALTER TABLE `storage_pool` DISABLE KEYS */;
UPDATE `storage_pool` s, `remote_service` r, `storage_device` c
SET s.idStorageDevice = c.id, s.idTier = 1, s.isEnabled = 1, s.name = 'abiquo'
WHERE r.idRemoteService = s.idRemoteService 
  AND r.idDatacenter = r.idDatacenter;
/*!40000 ALTER TABLE `storage_pool` ENABLE KEYS */;

ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON DELETE CASCADE;
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON DELETE RESTRICT;
ALTER TABLE `kinton`.`storage_pool` DROP FOREIGN KEY `idRemoteServiceFK_1`;
ALTER TABLE `kinton`.`storage_pool` DROP KEY `idRemoteServiceFK_1`;
ALTER TABLE `kinton`.`storage_pool` DROP COLUMN `idRemoteService`;
ALTER TABLE `kinton`.`storage_pool` DROP COLUMN `url_management`;
ALTER TABLE `kinton`.`storage_pool` DROP COLUMN `host_ip`;
ALTER TABLE `kinton`.`storage_pool` DROP COLUMN `host_port`;
ALTER TABLE `kinton`.`storage_pool` DROP COLUMN `storage_technology`;

--
-- System properties
--

/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.main.enterpriseLogoURL","http://www.abiquo.com","URL displayed when the header enterprise logo is clicked");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

/*!40000 ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` DISABLE KEYS */;
-- LOCK TABLES `kinton`.`node_virtual_image_stateful_conversions` WRITE;
-- UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ENABLE KEYS */;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `idTier` int(10) unsigned NOT NULL;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `tier` (`id`);

-- STATISTICS TABLES 
ALTER TABLE `kinton`.`vapp_enterprise_stats` ADD COLUMN `idVirtualDataCenter` INTEGER NOT NULL DEFAULT 0; 


-- STATISTICS TRIGGERS 

-- Fixes PublicIPs Total, Reserved for Infrastructure View
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;
DROP TRIGGER IF EXISTS `kinton`.`update_network_configuration_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_ip_pool_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_ip_pool_management_update_stats`;
DROP TRIGGER if exists `kinton`.`dclimit_created`;
DROP TRIGGER if exists `kinton`.`dclimit_updated`;    
DROP TRIGGER if exists `kinton`.`dclimit_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`create_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_volume_management_update_stats`;

DROP TRIGGER IF EXISTS `kinton`.`virtualapp_created`;

DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;
DROP PROCEDURE IF EXISTS  `kinton`.`UpdateVappEnterpriseStatsWithVDCIds`;

DELIMITER |
CREATE TRIGGER `kinton`.`virtualdatacenter_updated` AFTER UPDATE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
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
    END;
|
-- ******************************************************************************************
-- Description: 
--  * Registers/Unregister new IPS defined for a datacenter's network
--
-- Fires: On IP Creation / Deletion in a Datacenter
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`update_network_configuration_update_stats` AFTER UPDATE ON  `network_configuration`
FOR EACH ROW BEGIN
	DECLARE newPublicIps INTEGER;
	DECLARE idDataCenterObj INTEGER;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE network_configuration with dhcp_service_id OLD ',IFNULL(OLD.dhcp_service_id,'NULL'),' and NEW ', IFNULL(NEW.dhcp_service_id,'NULL')));
	 IF OLD.dhcp_service_id IS NULL AND NEW.dhcp_service_id IS NOT NULL THEN
	 	-- New Public IPs added
	 	SELECT count(*), dc.idDataCenter INTO newPublicIps, idDataCenterObj
	      FROM vlan_network vn, datacenter dc, ip_pool_management ipm
	      WHERE ipm.dhcp_service_id = NEW.dhcp_service_id
	      AND vn.network_configuration_id = NEW.network_configuration_id
	      AND vn.network_id = dc.network_id;	 	
	      -- INSERT INTO debug_msg (msg) VALUES (CONCAT('New Public Ips Detected ',IFNULL(newPublicIps,'NULL'),' for DC ', IFNULL(idDataCenterObj,'NULL')));
	      UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal+newPublicIps WHERE idDataCenter = idDataCenterObj;
	 END IF;
	IF NEW.dhcp_service_id IS NULL AND OLD.dhcp_service_id IS NOT NULL THEN
	 	-- New Public IPs deleted 
	 	SELECT count(*), dc.idDataCenter INTO newPublicIps, idDataCenterObj
	      FROM vlan_network vn, datacenter dc, ip_pool_management ipm
	      WHERE ipm.dhcp_service_id = OLD.dhcp_service_id
	      AND vn.network_configuration_id = OLD.network_configuration_id
	      AND vn.network_id = dc.network_id;	 	
	      -- INSERT INTO debug_msg (msg) VALUES (CONCAT('New Public Ips Deleted ',IFNULL(newPublicIps,'NULL'),' for DC ', IFNULL(idDataCenterObj,'NULL')));
	      UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal - newPublicIps WHERE idDataCenter = idDataCenterObj;	
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
      WHERE OLD.dhcp_service_id = nc.dhcp_service_id
      AND vn.network_configuration_id = nc.network_configuration_id
      AND vn.network_id = dc.network_id;
      IF idDataCenterObj IS NOT NULL THEN
	-- detects IP disabled/enabled at Edit Public Ips
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
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
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
CREATE TRIGGER `kinton`.`update_rasd_management_update_stats` AFTER UPDATE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50);
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE ipAddress VARCHAR(20);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --	   
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
		-- Detectamos cambios de VDC: V2V
		IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter AND OLD.idVirtualApp = NEW.idVirtualApp THEN
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			IF idState = 2 THEN
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			END IF;
		ELSE 			
		        IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
				-- Volume was changed to another VDC not in a V2V operation (cold move)
		            UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			END IF;
			-- Volume removed from a Vapp
			IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    IF idState = 2 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    END IF;                         
			END IF;
			-- Volume added from a Vapp
			IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			    IF idState = 2 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			    END IF;                 
			END IF;
			-- Volume added to VDC
			IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN        
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached 
			    IF idState = 2 THEN
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
			    END IF;
			END IF;
			-- Volume removed from VDC
			IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached
			    IF idState = 2 THEN
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
CREATE TRIGGER `kinton`.`update_ip_pool_management_update_stats` AFTER UPDATE ON `kinton`.`ip_pool_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- Checks for reserved IPs
            IF OLD.mac IS NULL AND NEW.mac IS NOT NULL THEN
                -- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise  INTO idDataCenterObj, idVirtualDataCenterObj, idEnterpriseObj
                FROM rasd_management rm, virtualdatacenter vdc
                WHERE NEW.idManagement = rm.idManagement
                AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter;
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
CREATE TRIGGER `kinton`.`dclimit_updated` AFTER UPDATE ON `kinton`.`enterprise_limits_by_datacenter`
FOR EACH ROW BEGIN     
	 IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
                -- Limit is not used anymore. Statistics are removed
                DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;                
                INSERT IGNORE INTO dc_enterprise_stats 
                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
            	VALUES 
                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);       
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
CREATE TRIGGER `kinton`.`dclimit_created` AFTER INSERT ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            --  Creates a New row in dc_enterprise_stats to store this enterprise's statistics
            INSERT IGNORE INTO dc_enterprise_stats 
                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
            VALUES 
                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);
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
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
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
        IF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            -- Activates if state changes or machines are captured
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
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
            CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd);
        END IF;              
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
        IF NEW.state != OLD.state THEN
            IF NEW.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed+reservedSize WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = 2 THEN
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
            IF OLD.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
            ELSEIF OLD.state = 2 THEN
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;
        END IF;
    END IF;
    END;
|
CREATE TRIGGER `kinton`.`virtualapp_created` AFTER INSERT ON `kinton`.`virtualapp`
  FOR EACH ROW BEGIN
    DECLARE vdcNameObj VARCHAR(50);
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vdc.name INTO vdcNameObj
      FROM virtualdatacenter vdc
      WHERE NEW.idVirtualDataCenter = vdc.idVirtualDataCenter;
      INSERT IGNORE INTO vapp_enterprise_stats (idVirtualApp, idEnterprise, idVirtualDataCenter, vappName, vdcName) VALUES(NEW.idVirtualApp, NEW.idEnterprise, NEW.idVirtualDataCenter, NEW.name, vdcNameObj);
    END IF;
  END;
|
CREATE PROCEDURE `kinton`.CalculateVappEnterpriseStats()
   BEGIN
  DECLARE idVirtualAppObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE idVirtualDataCenter INTEGER;
  DECLARE vappName VARCHAR(45);
  DECLARE vdcName VARCHAR(45);
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
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED"
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state = "RUNNING"
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
    AND state = 2;

    -- Inserts stats row
    INSERT INTO vapp_enterprise_stats (idVirtualApp,idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached)
    VALUES (idVirtualAppObj, idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached);


  END WHILE dept_loop;
  CLOSE curDC;

END;
|
CREATE PROCEDURE `kinton`.`UpdateVappEnterpriseStatsWithVDCIds`()
BEGIN
    DECLARE currentIdVirtualApp INTEGER;
    DECLARE idVirtualDatacenterObj INTEGER;
    DECLARE no_more_vappst INTEGER;
    DECLARE curVappSt CURSOR FOR SELECT vappst.idVirtualApp FROM vapp_enterprise_stats vappst;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vappst=1;
    SET no_more_vappst = 0;
    SET currentIdVirtualApp = -1;
    OPEN curVappSt;  
    my_loop:WHILE(no_more_vappst = 0) DO
	FETCH curVappSt INTO currentIdVirtualApp;
	SELECT idVirtualDataCenter INTO idVirtualDatacenterObj FROM virtualapp WHERE idVirtualApp = currentIdVirtualApp;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Iteracion vapp_enterprise_stats: ',currentIdVirtualApp));
	UPDATE vapp_enterprise_stats SET idVirtualDataCenter = idVirtualDatacenterObj WHERE idVirtualApp=currentIdVirtualApp;
    END WHILE my_loop;
    CLOSE curVappSt;
END;
|
DELIMITER ;

-- We need to update Vapp Enterprise Stats with the right idVirtualDatacenter Values
CALL `kinton`.`UpdateVappEnterpriseStatsWithVDCIds`();

--



