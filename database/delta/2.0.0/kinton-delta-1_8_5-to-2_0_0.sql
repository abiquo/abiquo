
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

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

