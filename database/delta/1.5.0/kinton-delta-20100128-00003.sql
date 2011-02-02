/* This script add the changes for infrastructure features */

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `idType` INT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - NOT MANAGED BY ABICLOUD  1 - MANAGED BY ABICLOUD' AFTER `high_disponibility`;
ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `idUser` INT(10) unsigned default NULL COMMENT 'User who creates the VM' AFTER `idType`;
ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `idEnterprise` int(10) unsigned default NULL COMMENT 'Enterprise of the user' AFTER `idUser`;

ALTER TABLE `kinton`.`virtualmachine` MODIFY COLUMN `idImage` INT(4) UNSIGNED DEFAULT NULL;


