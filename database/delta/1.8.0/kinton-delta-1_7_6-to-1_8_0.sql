-- TODO: Rename to kinton-delta-1_7_5-to-1_8_0.sql


-- Racks can be HA enabled
ALTER TABLE `kinton`.`rack` ADD COLUMN `haEnabled` boolean default false COMMENT 'TRUE - This rack is enabled for the HA functionality';

-- PhysicalMachine can have 2 new states
ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `idState` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - STOPPED
1 - NOT PROVISIONED
2 - NOT MANAGED
3 - MANAGED
4 - HALTED
5 - UNLICENSED
6 - HA_IN_PROGRESS
7 - DISABLED_FOR_HA';


-- Racks can be HA enabled
ALTER TABLE `kinton`.`datastore` ADD COLUMN `datastoreUuid` VARCHAR(255) default NULL COMMENT 'Datastore UUID set by Abiquo to identify shared datastores.';
ALTER TABLE `kinton`.`datastore` DROP COLUMN `shared`;

-- ipmi
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `ipmiIP` VARCHAR(39)  DEFAULT NULL AFTER `version_c`,
 ADD COLUMN `ipmiPort` INT(5) UNSIGNED DEFAULT NULL AFTER `ipmiIP`,
 ADD COLUMN `ipmiUser` VARCHAR(255)  DEFAULT NULL AFTER `ipmiPort`,
 ADD COLUMN `ipmiPassword` VARCHAR(255)  DEFAULT NULL AFTER `ipmiUser`;


-- Statistics trigger updates

DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;

DELIMITER |
CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
      UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1 WHERE idDataCenter = OLD.idDataCenter;
      UPDATE IGNORE cloud_usage_stats
        SET vCpuUsed=vCpuUsed-OLD.cpuUsed,
          vMemoryUsed=vMemoryUsed-OLD.ramUsed,
          vStorageUsed=vStorageUsed-OLD.hdUsed
      WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
      UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1 WHERE idDataCenter = OLD.idDataCenter;
      UPDATE IGNORE cloud_usage_stats
        SET vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
          vMemoryTotal=vMemoryTotal-OLD.ram,
          vStorageTotal=vStorageTotal-OLD.hd
      WHERE idDataCenter = OLD.idDataCenter;
    END IF;
  END IF;
  END;
|
CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
          -- Machine not managed changes into managed; or disabled_by_ha to Managed
          UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
          SET vCpuTotal=vCpuTotal + (NEW.cpu*NEW.cpuRatio),
            vMemoryTotal=vMemoryTotal + NEW.ram,
            vStorageTotal=vStorageTotal + NEW.hd
          WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
          -- Machine managed changes into not managed or DisabledByHA
          UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
          SET vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
            vMemoryTotal=vMemoryTotal-OLD.ram,
            vStorageTotal=vStorageTotal-OLD.hd
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
        -- Stopped / Halted / Not provisioned passes to Managed (Running)
          UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed+NEW.cpuUsed,
              vMemoryUsed=vMemoryUsed+NEW.ramUsed,
              vStorageUsed=vStorageUsed+NEW.hdUsed
          WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
        -- Managed (Running) passes to Stopped / Halted / Not provisioned
          UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed-OLD.cpuUsed,
              vMemoryUsed=vMemoryUsed-OLD.ramUsed,
              vStorageUsed=vStorageUsed-OLD.hdUsed
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
      ELSE
      -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
	-- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
          UPDATE IGNORE cloud_usage_stats
            SET vCpuTotal=vCpuTotal+((NEW.cpu-OLD.cpu)*NEW.cpuRatio),
              vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram),
              vStorageTotal=vStorageTotal + (NEW.hd-OLD.hd)
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
              vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed),
              vStorageUsed=vStorageUsed + (NEW.hdUsed-OLD.hdUsed)
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
      END IF;
    END IF;
  END;
|
DELIMITER ;
