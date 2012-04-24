-- ############################################################################################################### --   
-- ############################################################################################################### --   
-- INDEX:
--  SCHEMA: TABLES ADDED
--  SCHEMA: TABLES REMOVED
--  SCHEMA: COLUMNS ADDED 
--  SCHEMA: COLUMNS REMOVED 
--  SCHEMA: COLUMNS MODIFIED
--  DATA: NEW DATA
--  SCHEMA: TRIGGERS RECREATED
--  SCHEMA: PROCEDURES RECREATED
--  SCHEMA: VIEWS
-- ############################################################################################################### --   
-- ############################################################################################################### --   
SELECT "### APPLYING 2_0_0 TO 2_0_0-HF1 PATCH. ###" as " ";

SET @DISABLE_STATS_TRIGGERS = 1;
SELECT "STEP 1 TRIGGERS DISABLED DURING THE UPGRADE" as " ";

DROP PROCEDURE IF EXISTS kinton.delta_2_0_0_to_2_0_0HF1;

DELIMITER |
CREATE PROCEDURE kinton.delta_2_0_0_to_2_0_0HF1() 
BEGIN

    -- ##################################### -- 
    -- ######## SCHEMA: TABLES ADDED ####### --
    -- ##################################### --     
    SELECT "STEP 2 CREATING NEW TABLES..." as " ";
    IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='category' AND column_name='idEnterprise') THEN
        SELECT "Adding idEnterprise on category" as " ";
        ALTER TABLE kinton.category ADD COLUMN idEnterprise int(10) unsigned default NULL;
    END IF;

    -- ###################################### --    
        -- ######## SCHEMA: COLUMNS ADDED ####### --
    -- ###################################### --
    SELECT "STEP 3 CREATING NEW COLUMNS..." as " ";
    


    -- ######################################## --  
    -- ######## SCHEMA: COLUMNS MODIFIED ###### --
    -- ######################################## --
    SELECT "STEP 4 MODIFIYING EXISTING COLUMNS..." as " ";

    -- ############################################ --  
    -- ######## SCHEMA: CONSTRAINTS MODIFIED ###### --
    -- ############################################ --  
    SELECT "STEP 5 MODIFIYING CONSTRAINTS..." as " ";
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='category' AND constraint_name='category_enterprise_FK') THEN
        ALTER TABLE kinton.category DROP FOREIGN KEY category_enterprise_FK;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='category' AND constraint_name='category_enterprise_FK') THEN
        ALTER TABLE kinton.category ADD CONSTRAINT category_enterprise_FK FOREIGN KEY category_enterprise_FK (idEnterprise) REFERENCES enterprise (idEnterprise) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;

    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='category' AND constraint_name='name') THEN
		ALTER TABLE `kinton`.`category` DROP INDEX `name`;
	END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='category' AND constraint_name='name') THEN
		ALTER TABLE `kinton`.`category` ADD UNIQUE INDEX `name`(`name`, `idEnterprise`) using BTREE;
	END IF;

    -- ########################################################## --    
        -- ######## DATA: NEW DATA (INSERTS, UPDATES, DELETES ####### --
    -- ########################################################## --
    SELECT "STEP 6 UPDATING DATA..." as " ";
    
    -- CHEF --
    -- Dumping data for table kinton.privilege
    SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='51' AND name='USERS_MANAGE_CHEF_ENTERPRISE';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.privilege VALUES (51,'USERS_MANAGE_CHEF_ENTERPRISE',0);
    END IF;

    -- CHEF --
    -- Dumping data for table kinton.roles_privileges
    --
    SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='51';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.roles_privileges VALUES (1,51,0);
    END IF;

    -- GLOBAL CATEGORY --
    SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='53' AND name='APPLIB_MANAGE_GLOBAL_CATEGORIES';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.privilege VALUES (53,'APPLIB_MANAGE_GLOBAL_CATEGORIES',0);
    END IF;

    -- GLOBAL CATEGORY Privilege for cloud admin
    SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='53';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.roles_privileges VALUES (1,53,0);
    END IF;

    -- New System Properties
    SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.main.showHardDisk' AND value='1' AND description='Show (1) or hide (0) hard disk tab';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.main.showHardDisk','1','Show (1) or hide (0) hard disk tab');
    END IF;

    -- New Privileges
    SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='52' AND name='MANAGE_HARD_DISKS';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.privilege VALUES (52,'MANAGE_HARD_DISKS',0);
    END IF;
    SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='53' AND name='APPLIB_MANAGE_GLOBAL_CATEGORIES';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.privilege VALUES (53,'APPLIB_MANAGE_GLOBAL_CATEGORIES',0);
    END IF;

    -- Assign New Privileges to Cloud Admin
    SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='52';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.roles_privileges VALUES (1,52,0);
    END IF;
     SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='53';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.roles_privileges VALUES (1,53,0);
    END IF;
    
    -- Change to NULL to avoid an empty string for the property availableVirtualDatacenters --
    UPDATE IGNORE user SET availableVirtualDatacenters = NULL WHERE availableVirtualDatacenters = "";


    -- ######################################## --  
    -- ######## SCHEMA: COLUMNS REMOVED ####### --
    -- ######################################## --
    SELECT "STEP 7 REMOVING DEPRECATED COLUMNS..." as " ";  
    

    -- ######################################## --  
    -- ######## SCHEMA: TABLES REMOVED ######## --
    -- ######################################## --
    
END;
|
DELIMITER ;

# Now invoke the SP
CALL kinton.delta_2_0_0_to_2_0_0HF1();

# And on successful completion, remove the SP, so we are not cluttering the DBMS with upgrade code!
DROP PROCEDURE IF EXISTS kinton.delta_2_0_0_to_2_0_0HF1;

-- ########################################### --   
-- ######## SCHEMA: TRIGGERS REMOVED ####### --
-- ########################################### --

-- THIS TRIGGERS WILL BE REMOVED
SELECT "STEP 9 REMOVING DEPRECATED TRIGGERS..." as " ";

-- ########################################### --   
-- ######## SCHEMA: TRIGGERS RECREATED ####### --
-- ########################################### --
SELECT "STEP 10 UPDATING TRIGGERS..." as " ";
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualapp_update_stats;

DELIMITER |
--
SELECT "Recreating trigger update_virtualapp_update_stats..." as " ";
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
|
--
SELECT "Recreating trigger create_nodevirtualimage_update_stats..." as " ";
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
|
SELECT "Recreating trigger delete_nodevirtualimage_update_stats..." as " ";
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
|
SELECT "Recreating trigger update_virtualmachine_update_stats..." as " ";
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
|
DELIMITER ;

-- ############################################# -- 
-- ######## SCHEMA: PROCEDURES RECREATED ####### --
-- ############################################# --
SELECT "STEP 11 UPDATING PROCEDURES FOR THIS RELEASE..." as " ";


-- ############################## --    
-- ######## SCHEMA: VIEWS ####### --
-- ############################## --

-- ############################## --    
-- ######## SCHEMA: VIEWS ####### --
-- ############################## --


-- ############################################# -- 
-- ######## STATISTICS SANITY PROCEDURES ####### --
-- ############################################# --
-- This should be included in EVERY delta
-- FIX and Uncomment THIS!
-- CALL PROCEDURE kinton.CalculateCloudUsageStats();
-- CALL PROCEDURE kinton.CalculateEnterpriseResourcesStats();
-- CALL PROCEDURE kinton.CalculateVappEnterpriseStats();
-- CALL PROCEDURE kinton.CalculateVdcEnterpriseStats();


# This should not be necessary
CALL kinton.add_version_column_to_all();

SELECT "STEP 12 ENABLING TRIGGERS" as " ";
SET @DISABLE_STATS_TRIGGERS = null;
SELECT "#### UPGRADE COMPLETED ####" as " ";
