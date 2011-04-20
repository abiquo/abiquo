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

-- Temporal relationship between virtual machine and the old assigned machine

CREATE TABLE  `kinton`.`virtualmachine_for_ha` (
    `id` int(10) unsigned NOT NULL auto_increment,
    `idVirtualMachine` int(10) unsigned NOT NULL
    `idMachine` int(20) unsigned NOT NULL,
    `version_c` int(11) default 0,
    PRIMARY KEY (`id`),
    KEY `virtualmachine_for_ha_FK1` (`idVirtualMachine`),
    KEY `virtualmachine_for_ha_FK2` (`idMachine`),
    CONSTRAINT `virtualmachine_for_ha_FK1` FOREIGN KEY (`idVirtualMachine`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE,
    CONSTRAINT `virtualmachine_for_ha_FK2` FOREIGN KEY (`idMachine`) REFERENCES `physicalmachine` (`idPhysicalMachine`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

