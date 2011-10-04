ALTER TABLE `kinton`.`virtualimage` MODIFY COLUMN `name` VARCHAR(255) NOT NULL;
ALTER TABLE `kinton`.`virtualimage` MODIFY COLUMN `description` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `kinton`.`virtualimage` MODIFY COLUMN `pathName` VARCHAR(255) NOT NULL;