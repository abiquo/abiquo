-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --

DROP TABLE IF EXISTS `kinton`.`chefrecipe`;

-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

--
-- Definition of table `kinton`.`chef_recipe`
--

DROP TABLE IF EXISTS `kinton`.`chef_recipe`;
CREATE TABLE  `kinton`.`chef_recipe` (
  `idRecipe` int(10) unsigned NOT NULL auto_increment,
  `idVM` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255),
  `version_c` int(11) default 0,
  PRIMARY KEY  (`idRecipe`),
  KEY `chef_recipe_FK1` (`idVM`),
  CONSTRAINT `chef_recipe_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `subState` VARCHAR(50)  DEFAULT NULL AFTER `state`;
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `chefEnabled` BOOLEAN  NOT NULL DEFAULT false AFTER `cost_code`;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `chef_url` VARCHAR(255)  DEFAULT NULL AFTER `publicIPHard`,
 ADD COLUMN `chef_validator` VARCHAR(50)  DEFAULT NULL AFTER `chef_url`,
 ADD COLUMN `chef_client_certificate` TEXT  DEFAULT NULL AFTER `chef_validator`,
 ADD COLUMN `chef_validator_certificate` TEXT  DEFAULT NULL AFTER `chef_client_certificate`;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --


