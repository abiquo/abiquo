--
-- Add size column to virtualimage_conversions
--

ALTER TABLE `kinton`.`virtualimage_conversions`
    ADD COLUMN `size` BIGINT default NULL AFTER `timestamp`;
    
UPDATE `kinton`.`virtualimage_conversions` SET `size` = 0;
