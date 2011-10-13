use kinton;
-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --
DROP TRIGGER IF EXISTS  `kinton`.`update_datastore_update_stats`;

DELIMITER |
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
|
DELIMITER ;
