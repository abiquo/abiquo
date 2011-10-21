
-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures

-- ---------------------------------------------- --
--                 TABLE DROP                     --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                  TABLE CREATION                --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`virtualimage` DROP COLUMN `treaty`;
ALTER TABLE `kinton`.`virtualimage` DROP COLUMN `deleted`;

ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package`
 DROP FOREIGN KEY `fk_ovf_package_list_has_ovf_package_ovf_package1`;

ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY `fk_ovf_package_list_has_ovf_package_ovf_package1` (`id_ovf_package`)
    REFERENCES `ovf_package` (`id_ovf_package`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

