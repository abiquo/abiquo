ALTER TABLE `kinton`.`node` ADD COLUMN `modified` INT(2)  NOT NULL AFTER `idNode`;

INSERT INTO `kinton`.`state` VALUES (7,'APPLY_CHANGES_NEEDED');