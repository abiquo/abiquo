
-- 
-- TRIGGER DEFINITIONS
-- 
-- WARNING: This Triggers overwrite some statistics functionality so 
-- synchronization MUST BE DONE MANUALLY if there are changes in statistics functionality
--

DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_ip_pool_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_vlan_network_update_stats`;


DELIMITER |
--
-- ******************************************************************************************
-- Description: 
--  * Updates storageTotal
--  * Register Storage Created Event for Accounting
--
-- Fires: On an INSERT for the rasd_managment table
--
-- ******************************************************************************************
CREATE TRIGGER `kinton`.`create_rasd_management_update_stats` AFTER INSERT ON `kinton`.`rasd_management`
	FOR EACH ROW BEGIN
		DECLARE idDataCenterObj INTEGER;
		DECLARE idThisEnterprise INTEGER;
		DECLARE limitResourceObj BIGINT;
		DECLARE resourceName VARCHAR(255);
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
				CALL AccountingStorageRegisterEvents('CREATE_STORAGE', NEW.idResource, resourceName, NEW.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
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
CREATE TRIGGER `kinton`.`delete_rasd_management_update_stats` AFTER DELETE ON `kinton`.`rasd_management`
	FOR EACH ROW BEGIN
		DECLARE idDataCenterObj INTEGER;
		DECLARE idThisEnterprise INTEGER;
		DECLARE limitResourceObj BIGINT;	
		DECLARE resourceName VARCHAR(255);	
		SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
		FROM virtualdatacenter vdc
		WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
		SELECT elementName, limitResource INTO resourceName, limitResourceObj
		FROM rasd r
		WHERE r.instanceID = OLD.idResource;
		IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN			
			IF OLD.idResourceType='8' THEN 
				UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal-limitResourceObj WHERE idDataCenter = idDataCenterObj;
				UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
				CALL AccountingStorageRegisterEvents('DELETE_STORAGE', OLD.idResource, resourceName, OLD.idVirtualDataCenter, idThisEnterprise, limitResourceObj);	
			END IF;
		END IF;
	END;	
-- ******************************************************************************************
-- Description: 
--  * volCreated, volAttached
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
				AND (vm.state = 1 OR vm.state = 2);
				UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+ NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;				
				IF isReserved != 0 THEN
				-- si hay volAttached se debe actualizar el storageUsed
					UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed +  NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;					
				END IF;				
				CALL AccountingStorageRegisterEvents('UPDATE_STORAGE', NEW.instanceID, NEW.elementName, idThisVirtualDataCenter, idThisEnterprise, NEW.limitResource);
			END IF;
		END IF;
	END;	
--
-- ******************************************************************************************
-- Description: 
--  * Checks for new Reserved or unreserved IPs
--  * Reserved IPs have a valid 'mac' address; trigger checks this field to increase publicIPsReserved Stat
--  * Registers Created Public IP for Accounting
--
-- Fires: On an UPDATE for the ip_pool_management
-- ******************************************************************************************
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
				UPDATE IGNORE cloud_usage_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idDataCenter = idDataCenterObj;
				CALL AccountingIPsRegisterEvents('IP_RESERVED',NEW.idManagement,NEW.ip,idVirtualDataCenterObj, idEnterpriseObj);
			END IF;
		END IF;
	END;
-- ******************************************************************************************
-- Description: 
-- * volCreated, volAttached
-- * Checks new idVM assignments to update publicIPsUsed stats
-- * Checks idVirtualDataCenter unassignments -> publicIPsReserved stats decrease
-- * Registers Deleted Public IP for Accounting
--
-- Fires: On an UPDATE IGNORE for the rasd_management table
--
-- ******************************************************************************************
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
					UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed+1 WHERE idDataCenter = idDataCenterObj;
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
					UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
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
					UPDATE IGNORE cloud_usage_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = idDataCenterObj;
					-- Registers Accounting Event
					SELECT vdc.idEnterprise INTO idEnterpriseObj
					FROM virtualdatacenter vdc
					WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
					CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
				END IF;
			END IF;
		END IF;
	END;
--
-- ******************************************************************************************
-- Description: 
--  * Updates resources (cpu, ram, hd) used by Enterprise
--  * Updates vMachinesRunning for cloud Usage Stats
--  * TODO: Updates vmCreated for enterprise stats (vapp, vdc)
--  * Registers Accounting Events for this VM Resources
--
-- Fires: ON UPDATE IGNORE an virtualmachine for the virtualmachine table
--
-- ******************************************************************************************
|
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
	FOR EACH ROW BEGIN
		DECLARE idDataCenterObj INTEGER;
		DECLARE idVirtualAppObj INTEGER;
		DECLARE idVirtualDataCenterObj INTEGER;
		IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN	
		-- 	Updating enterprise_resources_stats: VCPU Used, Memory Used, Local Storage Used
		IF OLD.idHypervisor IS NULL THEN
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
		IF NEW.idType = 1 AND (NEW.state != OLD.state OR OLD.idType != NEW.idType) THEN
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
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
			END IF;		
			IF OLD.state = "NOT_DEPLOYED" OR OLD.idType != NEW.idType THEN
				-- VMachine Deployed or VMachine captured
				UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
				WHERE idDataCenter = idDataCenterObj;
				UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
				WHERE idVirtualApp = idVirtualAppObj;
				UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
				WHERE idVirtualDataCenter = idVirtualDataCenterObj;
			ELSEIF NEW.state = "NOT_DEPLOYED" THEN 
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
		CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd);		
	END IF;
	END;
|
-- For VLAN Accounting
CREATE TRIGGER `kinton`.`create_vlan_network_update_stats` AFTER INSERT ON `kinton`.`vlan_network`
FOR EACH ROW BEGIN
	DECLARE idVirtualDataCenterObj INTEGER;
	DECLARE idEnterpriseObj INTEGER;
	SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
	FROM virtualdatacenter vdc, enterprise e
	WHERE vdc.networktypeID=NEW.network_id
	AND vdc.idEnterprise=e.idEnterprise;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('PROCEDURE AccountingVLANRegisterEvents Activated: ',IFNULL(NEW.vlan_network_id,'NULL'),'-',IFNULL(NEW.network_name,'NULL'),'-',IFNULL(idVirtualDataCenterObj,'NULL'),'-',idEnterpriseObj,'-','CREATE_VLAN','-',now()));
	CALL AccountingVLANRegisterEvents('CREATE_VLAN',NEW.vlan_network_id, NEW.network_name, idVirtualDataCenterObj,idEnterpriseObj);
END;
|
CREATE TRIGGER `kinton`.`delete_vlan_network_update_stats` AFTER DELETE ON `kinton`.`vlan_network`
FOR EACH ROW
BEGIN
	DECLARE idVirtualDataCenterObj INTEGER;
	DECLARE idEnterpriseObj INTEGER;
	SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
	FROM virtualdatacenter vdc, enterprise e
	WHERE vdc.networktypeID=OLD.network_id
	AND vdc.idEnterprise=e.idEnterprise;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('PROCEDURE AccountingVLANRegisterEvents Activated: ',IFNULL(OLD.vlan_network_id,'NULL'),'-',IFNULL(OLD.network_name,'NULL'),'-',IFNULL(idVirtualDataCenterObj,'NULL'),'-',IFNULL(idEnterpriseObj,'NULL'),'-','DELETE_VLAN','-',now()));
	CALL AccountingVLANRegisterEvents('DELETE_VLAN',OLD.vlan_network_id, OLD.network_name, idVirtualDataCenterObj,idEnterpriseObj);
END;
|
DELIMITER ;
