-- Init Triggers
--	
-- 	

DROP TRIGGER IF EXISTS kinton.datacenter_created;
DROP TRIGGER IF EXISTS kinton.datacenter_deleted;
DROP TRIGGER IF EXISTS kinton.virtualapp_created;
DROP TRIGGER IF EXISTS kinton.virtualapp_deleted;


DROP TRIGGER IF EXISTS kinton.update_virtualapp_update_stats;
DROP TRIGGER IF EXISTS kinton.enterprise_created;
DROP TRIGGER IF EXISTS kinton.enterprise_deleted;
DROP TRIGGER IF EXISTS kinton.enterprise_updated;
DROP TRIGGER IF EXISTS kinton.create_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.create_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.update_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.create_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.virtualdatacenter_created;
DROP TRIGGER IF EXISTS kinton.virtualdatacenter_updated;
DROP TRIGGER IF EXISTS kinton.virtualdatacenter_deleted;
-- DROP TRIGGER IF EXISTS kinton.create_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.create_volume_management_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_volume_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_volume_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_rasd_update_stats;
DROP TRIGGER IF EXISTS kinton.user_created;
DROP TRIGGER IF EXISTS kinton.user_deleted;
DROP TRIGGER IF EXISTS kinton.create_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.create_vlan_network_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_vlan_network_update_stats;
DROP TRIGGER IF EXISTS kinton.update_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_network_configuration_update_stats;
DROP TRIGGER IF EXISTS kinton.dclimit_created;
DROP TRIGGER IF EXISTS kinton.dclimit_updated;
DROP TRIGGER IF EXISTS kinton.dclimit_deleted;
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
CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);
    END IF;
  END;
|
--
CREATE TRIGGER kinton.datacenter_deleted AFTER DELETE ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
	DELETE FROM dc_enterprise_stats WHERE idDataCenter = OLD.idDataCenter;
      	DELETE FROM cloud_usage_stats WHERE idDataCenter = OLD.idDataCenter;
    END IF;
  END;
--
|
CREATE TRIGGER kinton.virtualapp_created AFTER INSERT ON kinton.virtualapp
  FOR EACH ROW BEGIN
    DECLARE vdcNameObj VARCHAR(50);
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vdc.name INTO vdcNameObj
      FROM virtualdatacenter vdc
      WHERE NEW.idVirtualDataCenter = vdc.idVirtualDataCenter;
      INSERT IGNORE INTO vapp_enterprise_stats (idVirtualApp, idEnterprise, idVirtualDataCenter, vappName, vdcName) VALUES(NEW.idVirtualApp, NEW.idEnterprise, NEW.idVirtualDataCenter, NEW.name, vdcNameObj);
    END IF;
  END;
--
|
CREATE TRIGGER kinton.virtualapp_deleted AFTER DELETE ON kinton.virtualapp
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
CREATE TRIGGER kinton.enterprise_created AFTER INSERT ON kinton.enterprise
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
CREATE TRIGGER kinton.enterprise_updated AFTER UPDATE ON kinton.enterprise
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
CREATE TRIGGER kinton.enterprise_deleted AFTER DELETE ON kinton.enterprise
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
CREATE TRIGGER kinton.create_physicalmachine_update_stats AFTER INSERT ON kinton.physicalmachine
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
CREATE TRIGGER kinton.create_datastore_update_stats AFTER INSERT ON kinton.datastore_assignment
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
CREATE TRIGGER kinton.delete_physicalmachine_update_stats AFTER DELETE ON kinton.physicalmachine
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
CREATE TRIGGER kinton.delete_datastore_update_stats` BEFORE DELETE ON kinton.datastore
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
CREATE TRIGGER kinton.update_physicalmachine_update_stats AFTER UPDATE ON kinton.physicalmachine
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
--
--
-- ******************************************************************************************
-- Description: 
--  * Keeps track of previous states for virtualmachines
--
-- ******************************************************************************************
|
CREATE TRIGGER kinton.create_virtualmachine_update_stats AFTER INSERT ON kinton.virtualmachine
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
CREATE TRIGGER kinton.delete_virtualmachine_update_stats AFTER DELETE ON kinton.virtualmachine
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
	IF NEW.state != OLD.state AND NEW.state='LOCKED' THEN
		UPDATE virtualmachinetrackedstate SET previousState=OLD.state WHERE idVM=NEW.idVM;
	END IF;
	--
	SELECT vmts.previousState INTO previousState
        FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = NEW.idVM;
	-- -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', NEW.idVM, ' - ', OLD.idType, ' - ', NEW.idType, ' - ', OLD.state, ' - ', NEW.state, ' - ', previousState));	
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
-- -- INSERT INTO debug_msg (msg) VALUES (CONCAT('update values ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
	--
	-- Imported VMs will be updated on create_node_virtual_image
	-- Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from delete_nodevirtualimage_update_stats ON DELETE nodevirtualimage when updating the VApp
	-- Main case: an imported VM changes its state (from LOCKED to ...)
	IF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "ON" AND previousState != "ON" THEN 
                -- New Active		
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
	    ELSEIF (NEW.state IN ("PAUSED","OFF","NOT_ALLOCATED") AND previousState = "ON") THEN
                -- When Undeploying a full Vapp
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
      --  INSERT INTO debug_msg (msg) VALUES (CONCAT('createNVI ', type, ' - ', state, ' - ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL')));
    IF type=1 THEN
    	-- Imported !!!
		UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
          IF state = "ON" THEN 	
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
      IF previousState != "NOT_ALLOCATED" OR previousState != "UNKNOWN" THEN      
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
-- This Trigger was deleted in 2.0-> CREATE TRIGGER kinton.create_rasd_management_update_stats AFTER INSERT ON kinton.rasd_management
CREATE TRIGGER kinton.create_volume_management_update_stats AFTER INSERT ON kinton.volume_management
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
CREATE TRIGGER kinton.virtualdatacenter_created AFTER INSERT ON kinton.virtualdatacenter
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
CREATE TRIGGER kinton.virtualdatacenter_updated AFTER UPDATE ON kinton.virtualdatacenter
    FOR EACH ROW BEGIN
    DECLARE vlanNetworkIdObj INTEGER;    
        	  DECLARE networkNameObj VARCHAR(40);
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
CREATE TRIGGER kinton.virtualdatacenter_deleted` BEFORE DELETE ON kinton.virtualdatacenter
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) DEFAULT '';
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
CREATE TRIGGER kinton.user_created AFTER INSERT ON kinton.user
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
CREATE TRIGGER kinton.user_deleted AFTER DELETE ON kinton.user
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
CREATE TRIGGER kinton.update_volume_management_update_stats AFTER UPDATE ON kinton.volume_management
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
		            UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    IF idState = 1 THEN
				SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
				FROM virtualdatacenter vdc
				WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
				SELECT r.limitResource INTO reservedSize
				FROM rasd r
				WHERE r.instanceID = OLD.idResource;
				--  INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));	
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
CREATE TRIGGER kinton.update_rasd_update_stats AFTER UPDATE ON kinton.rasd
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
-- Fires: On an INSERT ON kinton.vlan_network
--
-- ******************************************************************************************
CREATE TRIGGER kinton.create_vlan_network_update_stats AFTER INSERT ON kinton.vlan_network
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
-- Fires: On an DELETE ON kinton.vlan_network
--
-- ******************************************************************************************
|
CREATE TRIGGER kinton.delete_vlan_network_update_stats AFTER DELETE ON kinton.vlan_network
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
CREATE TRIGGER kinton.dclimit_created AFTER INSERT ON kinton.enterprise_limits_by_datacenter
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
CREATE TRIGGER kinton.dclimit_updated AFTER UPDATE ON kinton.enterprise_limits_by_datacenter
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
CREATE TRIGGER kinton.dclimit_deleted AFTER DELETE ON kinton.enterprise_limits_by_datacenter
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
CREATE TRIGGER kinton.update_datastore_update_stats AFTER UPDATE ON kinton.datastore
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
|
-- ******************************************************************************************
--
--  Checks statistics miscalculations and corrects them to zero
--
-- ****************************************************************************************
DROP TRIGGER IF EXISTS kinton.cloud_usage_stats_negative_check;
DROP TRIGGER IF EXISTS kinton.enterprise_resources_stats_negative_check;
DROP TRIGGER IF EXISTS kinton.vapp_enterprise_stats_negative_check;
DROP TRIGGER IF EXISTS kinton.vdc_enterprise_stats_negative_check;
DROP TRIGGER IF EXISTS kinton.dc_enterprise_stats_negative_check;

CREATE TRIGGER kinton.cloud_usage_stats_negative_check` BEFORE UPDATE ON kinton.cloud_usage_stats
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


CREATE TRIGGER kinton.enterprise_resources_stats_negative_check` BEFORE UPDATE ON kinton.enterprise_resources_stats
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

CREATE TRIGGER kinton.dc_enterprise_stats_negative_check` BEFORE UPDATE ON kinton.dc_enterprise_stats
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


CREATE TRIGGER kinton.vapp_enterprise_stats_negative_check` BEFORE UPDATE ON kinton.vapp_enterprise_stats
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vmCreated > 16700000)  THEN SET NEW.vmCreated = 0; END IF;
      IF (NEW.vmActive > 16700000)  THEN SET NEW.vmActive = 0; END IF;
      IF (NEW.volAssociated > 16700000)  THEN SET NEW.volAssociated = 0; END IF;
      IF (NEW.volAttached > 16700000)  THEN SET NEW.volAttached = 0; END IF;
    END IF;
  END;



CREATE TRIGGER kinton.vdc_enterprise_stats_negative_check` BEFORE UPDATE ON kinton.vdc_enterprise_stats
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
