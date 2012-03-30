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

    -- New System Properties
    SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.main.showHardDisk' AND value='1' AND description='Show (1) or hide (0) hard disk tab';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.main.showHardDisk','1','Show (1) or hide (0) hard disk tab');
    END IF;

    -- New Privilege
    SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='52' AND name='MANAGE_HARD_DISKS';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.privilege VALUES (52,'MANAGE_HARD_DISKS',0);
    END IF;

    -- Assign New Privilege to Cloud Admin
    SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='52';
    IF @existsCount = 0 THEN 
        INSERT INTO kinton.roles_privileges VALUES (1,52,0);
    END IF;


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

DROP TRIGGER IF EXISTS kinton.update_virtualapp_update_stats;
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
