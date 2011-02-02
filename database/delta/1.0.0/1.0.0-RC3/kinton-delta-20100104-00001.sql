/**This script updates the virtualMachine length**/
ALTER TABLE `kinton`.`metering` MODIFY COLUMN `virtualmachine` VARCHAR(256)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;