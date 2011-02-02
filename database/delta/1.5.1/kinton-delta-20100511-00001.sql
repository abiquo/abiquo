--
-- Add serviceMapping column
--

ALTER TABLE `kinton`.`remote_service_type` ADD COLUMN `protocol` VARCHAR(15)  NOT NULL AFTER `serviceMapping`,
 ADD COLUMN `port` INT(5)  NOT NULL AFTER `protocol`;
    
UPDATE `kinton`.`remote_service_type` SET
    protocol = 'http://',
    port = 8080
WHERE idRemoteServiceType = 1;

UPDATE `kinton`.`remote_service_type` SET
    protocol = 'http://',
    port = 8080
WHERE idRemoteServiceType = 3;
