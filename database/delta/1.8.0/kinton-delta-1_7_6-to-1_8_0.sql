-- Fix int precision
ALTER TABLE `kinton`.`vappstateful_conversions` MODIFY COLUMN `idUser` int(10) unsigned NOT NULL;
