/** Virtual Image changes */
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `stateful` int(1) unsigned NOT NULL after `ovfid`;
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `diskFileSize` BIGINT(20) UNSIGNED NOT NULL AFTER `stateful`;
