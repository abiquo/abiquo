/* This script add the new State of the physicalMachine */
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `idState` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - STOPPED
1 - NOT PROVISIONED
2 - NOT MANAGED
3 - MANAGED
4 - HALTED' AFTER `hdUsed`;


/* By default all the physicalMachine has the state 3 - Managed */
update physicalmachine set idState = 3;