
--
-- System properties
--

/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.main.enterpriseLogoURL","http://www.abiquo.com","URL displayed when the header enterprise logo is clicked");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

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
                IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
                    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    IF idState = 2 THEN
                        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    END IF;                         
                END IF;
                IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
                    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    IF idState = 2 THEN
                        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    END IF;                 
                END IF;
                -- Detects Stateful added: 
                IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN                         
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- Stateful are always Attached 
                    IF idState = 2 THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
                    END IF;
                END IF;
                -- Detects Stateful added: 
                IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- Stateful are always Attached
                    IF idState = 2 THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;                     
                    END IF;
                END IF;         
                -- Detectamos cambios de VDC: V2V
                IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- vol Attached ??
                    SELECT IF(count(*) = 0, "", vm.state) INTO state
                    FROM volume_management vm
                    WHERE vm.idManagement = OLD.idManagement;
                    IF state = "PAUSED" THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    END IF;
                END IF;
            END IF;
            -- From old `autoDetachVolume`
            UPDATE IGNORE volume_management v set v.state = 0
            WHERE v.idManagement = OLD.idManagement;
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
    -- Creation : idEnterprise and idDatacenter are NOT NULL: update with NEW.stat
    -- Deletion: idEnterprise is NULL -> update with  - NEW.stat
    -- INSERT or DELETE Triggers are used when deleting Enterprises or inserting new Enterprises
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            IF OLD.idEnterprise IS NOT NULL AND NEW.idEnterprise IS NULL THEN
                -- Limit is not used anymore. Statistics are removed
                DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;                
                UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved - OLD.cpuHard,
                    vMemoryReserved = vMemoryReserved - OLD.ramHard,
                    vStorageReserved = vStorageReserved - OLD.hdHard,
                    storageReserved = storageReserved - OLD.storageHard,
                    publicIPsReserved = publicIPsReserved - OLD.publicIPHard,
                    vlanReserved = vlanReserved - OLD.vlanHard
                WHERE idDataCenter = OLD.idDataCenter;                
            ELSEIF  OLD.idEnterprise IS NULL AND NEW.idEnterprise IS NOT NULL THEN
                -- We got a new limit defined (or updated)
            	INSERT IGNORE INTO dc_enterprise_stats 
                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
            	VALUES 
                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);                                
                -- Update cloud usage
                UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved  + NEW.cpuHard,
                    vMemoryReserved = vMemoryReserved + NEW.ramHard,
                    vStorageReserved = vStorageReserved  + NEW.hdHard,
                    storageReserved = storageReserved  + NEW.storageHard,
                    publicIPsReserved = publicIPsReserved  + NEW.publicIPHard,
                    vlanReserved = vlanReserved  + NEW.vlanHard
                WHERE idDataCenter = NEW.idDataCenter;
            END IF;             
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
DELIMITER ;

alter table hypervisor drop column description;
