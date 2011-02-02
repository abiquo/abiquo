/* Modify name and description length */
ALTER TABLE `kinton`.`virtualmachine` MODIFY COLUMN `description` VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
ALTER TABLE `kinton`.`virtualmachine` MODIFY COLUMN `name` VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT NULL;