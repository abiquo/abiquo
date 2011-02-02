--
-- Add serviceMapping column
--

ALTER TABLE `kinton`.`remote_service_type`
    ADD COLUMN `serviceMapping` varchar(40) character set utf8 NOT NULL AFTER `name`;
    
UPDATE `kinton`.`remote_service_type` SET
    serviceMapping = 'virtualfactory',
    name = 'Virtualization Manager'
WHERE idRemoteServiceType = 1;

UPDATE `kinton`.`remote_service_type` SET
    serviceMapping = 'vsm',
    name = 'Monitor Manager'
WHERE idRemoteServiceType = 3;
