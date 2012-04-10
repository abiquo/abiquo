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
SELECT "### APPLYING 2_0_0-HF1 TO 2_0_0-HF2 PATCH. ###" as " ";

SET @DISABLE_STATS_TRIGGERS = 1;
SELECT "STEP 1 TRIGGERS DISABLED DURING THE UPGRADE" as " ";

DROP PROCEDURE IF EXISTS kinton.delta_2_0_0HF1_to_2_0_0HF2;

DELIMITER |
CREATE PROCEDURE kinton.delta_2_0_0HF1_to_2_0_0HF2() 
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

    IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='pricingTemplate' AND column_name='memoryMB') THEN
    ALTER TABLE kinton.pricingTemplate ADD COLUMN memoryGB DECIMAL(20,5) DEFAULT 0 AFTER vcpu;
    UPDATE  kinton.pricingTemplate SET memoryGB = memoryMB/1024;
         ALTER TABLE kinton.pricingTemplate drop COLUMN memoryMB;
    END IF;

    -- ############################################ --  
    -- ######## SCHEMA: CONSTRAINTS MODIFIED ###### --
    -- ############################################ --  
    SELECT "STEP 5 MODIFIYING CONSTRAINTS..." as " ";
    

    -- ########################################################## --    
        -- ######## DATA: NEW DATA (INSERTS, UPDATES, DELETES ####### --
    -- ########################################################## --
    SELECT "STEP 6 UPDATING DATA..." as " ";
    
    


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
CALL kinton.delta_2_0_0HF1_to_2_0_0HF2();

# And on successful completion, remove the SP, so we are not cluttering the DBMS with upgrade code!
DROP PROCEDURE IF EXISTS kinton.delta_2_0_0HF1_to_2_0_0HF2;

-- ########################################### --   
-- ######## SCHEMA: TRIGGERS REMOVED ####### --
-- ########################################### --

-- THIS TRIGGERS WILL BE REMOVED
SELECT "STEP 9 REMOVING DEPRECATED TRIGGERS..." as " ";

-- ########################################### --   
-- ######## SCHEMA: TRIGGERS RECREATED ####### --
-- ########################################### --
SELECT "STEP 10 UPDATING TRIGGERS..." as " ";

DELIMITER |
--
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

