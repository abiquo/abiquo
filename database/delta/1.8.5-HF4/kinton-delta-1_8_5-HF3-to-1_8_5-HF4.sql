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

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --
