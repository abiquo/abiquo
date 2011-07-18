-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --

DROP TABLE IF EXISTS `kinton`.`chefcookbook`;

-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

CREATE TABLE  `kinton`.`chefcookbook` (
  `chefCookbookId` int(10) unsigned NOT NULL auto_increment,
  `idVM` int(10) unsigned NOT NULL,
  `cookbook` varchar(255) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`chefCookbookId`),
  KEY `chefcookbook_FK1` (`idVM`),
  CONSTRAINT `chefcookbook_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `subState` VARCHAR(50)  DEFAULT NULL AFTER `state`;
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `chefEnabled` BOOLEAN  NOT NULL DEFAULT false AFTER `cost_code`;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `chef_url` VARCHAR(255)  DEFAULT NULL AFTER `publicIPHard`,
 ADD COLUMN `chef_client_certificate` TEXT  DEFAULT NULL AFTER `chef_url`,
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


