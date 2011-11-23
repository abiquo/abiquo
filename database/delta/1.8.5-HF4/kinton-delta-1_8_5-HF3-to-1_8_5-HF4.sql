-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.main.disableChangePassword","1","Allow (1) or deny (0) user to change their password"),
("client.main.allowLogout","1","Allow (1) or deny (0) user to logout");

ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `vswitchName` varchar(200) NOT NULL;

UPDATE `kinton`.`virtualmachine` SET vdrpIP = NULL, vdrpPort = 0 WHERE state = 'NOT_DEPLOYED';

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --
