-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --
ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `vswitchName` varchar(200) NOT NULL;

-- UCS default template
ALTER TABLE `kinton`.`ucs_rack` ADD COLUMN `defaultTemplate` varchar(200);
ALTER TABLE `kinton`.`ucs_rack` ADD COLUMN `maxMachinesOn` int(4) DEFAULT 0;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.infra.ucsManagerLink","/ucsm/ucsm.jnlp","URL to display UCS Manager Interface");

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --


