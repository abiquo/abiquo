use kinton;
-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

--
-- Definition of table `kinton`.`chef_runlist`
--

DROP TABLE IF EXISTS `kinton`.`chef_runlist`;
CREATE TABLE  `kinton`.`chef_runlist` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idVM` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255),
  `priority` int(10) NOT NULL default 0,
  `version_c` int(11) default 0,
  PRIMARY KEY  (`id`),
  KEY `chef_runlist_FK1` (`idVM`),
  CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `virtualmachine` (`idVM`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `subState` VARCHAR(50)  DEFAULT NULL AFTER `state`;
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `chefEnabled` BOOLEAN  NOT NULL DEFAULT false AFTER `cost_code`;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `chef_url` VARCHAR(255)  DEFAULT NULL AFTER `publicIPHard`,
 ADD COLUMN `chef_client` VARCHAR(50)  DEFAULT NULL AFTER `chef_url`,
 ADD COLUMN `chef_validator` VARCHAR(50)  DEFAULT NULL AFTER `chef_client`,
 ADD COLUMN `chef_client_certificate` TEXT  DEFAULT NULL AFTER `chef_validator`,
 ADD COLUMN `chef_validator_certificate` TEXT  DEFAULT NULL AFTER `chef_client_certificate`;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --
