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
    

    ALTER TABLE `kinton`.`costCode` CHANGE COLUMN `idCostCode` `idCostCode` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT;    
    
    IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='pricingTemplate' AND column_name='memoryMB') THEN
    ALTER TABLE kinton.pricingTemplate ADD COLUMN memoryGB DECIMAL(20,5) DEFAULT 0 AFTER vcpu;
    UPDATE  kinton.pricingTemplate SET memoryGB = memoryMB/1024;
         ALTER TABLE kinton.pricingTemplate drop COLUMN memoryMB;
    END IF;

    -- ############################################ --  
    -- ######## SCHEMA: CONSTRAINTS MODIFIED ###### --
    -- ############################################ --  
    SELECT "STEP 5 MODIFIYING CONSTRAINTS..." as " ";
    -- pricing cost code --
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingCostCode' AND constraint_name='pricingCostCode_FK1') THEN
        ALTER TABLE kinton.pricingCostCode DROP FOREIGN KEY pricingCostCode_FK1;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingCostCode' AND constraint_name='pricingCostCode_FK1') THEN
        ALTER TABLE kinton.pricingCostCode ADD CONSTRAINT pricingCostCode_FK1 FOREIGN KEY pricingCostCode_FK1 (idCostCode) REFERENCES costCode (idCostCode) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingCostCode' AND constraint_name='pricingCostCode_FK2') THEN
        ALTER TABLE kinton.pricingCostCode DROP FOREIGN KEY pricingCostCode_FK2;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingCostCode' AND constraint_name='pricingCostCode_FK2') THEN
        ALTER TABLE kinton.pricingCostCode ADD CONSTRAINT pricingCostCode_FK2 FOREIGN KEY pricingCostCode_FK2 (idPricingTemplate) REFERENCES pricingTemplate (idPricingTemplate) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    -- pricing tier --
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingTier' AND constraint_name='pricingTier_FK1') THEN
        ALTER TABLE kinton.pricingTier DROP FOREIGN KEY pricingTier_FK1;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingTier' AND constraint_name='pricingTier_FK1') THEN
        ALTER TABLE kinton.pricingTier ADD CONSTRAINT pricingTier_FK1 FOREIGN KEY pricingTier_FK1 (idTier) REFERENCES tier (id) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingTier' AND constraint_name='pricingTier_FK2') THEN
        ALTER TABLE kinton.pricingTier DROP FOREIGN KEY pricingTier_FK2;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='pricingTier' AND constraint_name='pricingTier_FK2') THEN
        ALTER TABLE kinton.pricingTier ADD CONSTRAINT pricingTier_FK2 FOREIGN KEY pricingTier_FK2 (idPricingTemplate) REFERENCES pricingTemplate (idPricingTemplate) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    -- cost code currency --
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='costCodeCurrency' AND constraint_name='idCostCode_FK') THEN
        ALTER TABLE kinton.costCodeCurrency DROP FOREIGN KEY idCostCode_FK;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='costCodeCurrency' AND constraint_name='idCostCode_FK') THEN
        ALTER TABLE kinton.costCodeCurrency ADD CONSTRAINT idCostCode_FK FOREIGN KEY idCostCode_FK (idCostCode) REFERENCES costCode (idCostCode) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='costCodeCurrency' AND constraint_name='idCurrency_FK') THEN
        ALTER TABLE kinton.costCodeCurrency DROP FOREIGN KEY idCurrency_FK;
    END IF;

    IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='costCodeCurrency' AND constraint_name='idCurrency_FK') THEN
        ALTER TABLE kinton.costCodeCurrency ADD CONSTRAINT idCurrency_FK FOREIGN KEY idCurrency_FK (idCurrency) REFERENCES currency (idCurrency) ON DELETE CASCADE ON UPDATE NO ACTION;
    END IF;
    
    -- ########################################################## --    
    -- ######## DATA: NEW DATA (INSERTS, UPDATES, DELETES ####### --
    -- ########################################################## --
    SELECT "STEP 6 UPDATING DATA..." as " ";
    
    
        -- ######################################## --  
    -- ######## SCHEMA: COLUMNS REMOVED ####### --
    -- ######################################## --
    SELECT "STEP 7 REMOVING DEPRECATED COLUMNS..." as " ";  
    
    IF EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='log') THEN
		SELECT "Removing table log..." as " ";
		DROP  TABLE IF EXISTS kinton.log;
	END IF;

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

-- ############################################# -- 
-- ######## SCHEMA: PROCEDURES RECREATED ####### --
-- ############################################# --
SELECT "STEP 11 UPDATING PROCEDURES FOR THIS RELEASE..." as " ";


-- ############################## --    
-- ######## SCHEMA: VIEWS ####### --
-- ############################## --

# This should not be necessary
CALL kinton.add_version_column_to_all();

SELECT "STEP 12 ENABLING TRIGGERS" as " ";
SET @DISABLE_STATS_TRIGGERS = null;
SELECT "#### UPGRADE COMPLETED ####" as " ";
