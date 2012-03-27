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