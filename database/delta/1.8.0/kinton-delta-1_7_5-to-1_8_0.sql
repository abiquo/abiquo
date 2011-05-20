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

-- ipmi
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `ipmiIP` VARCHAR(39)  DEFAULT NULL AFTER `version_c`,
 ADD COLUMN `ipmiPort` INT(5) UNSIGNED DEFAULT NULL AFTER `ipmiIP`,
 ADD COLUMN `ipmiUser` VARCHAR(255)  DEFAULT NULL AFTER `ipmiPort`,
 ADD COLUMN `ipmiPassword` VARCHAR(255)  DEFAULT NULL AFTER `ipmiUser`;
