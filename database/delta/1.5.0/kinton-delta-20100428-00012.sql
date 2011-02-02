--
-- Adding Service IP (VRDP access)
--

ALTER TABLE `kinton`.`hypervisor` ADD COLUMN `ipService` VARCHAR(39)  NOT NULL AFTER `ip`;

-- By default the Service IP is the same Managment IP
UPDATE `kinton`.`hypervisor` SET `ipService`=`ip`;