use kinton;
-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --
DROP TABLE IF EXISTS `kinton`.`disk_management`;

-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --
CREATE TABLE  `kinton`.`disk_management` (
  `idManagement` int(10) unsigned NOT NULL,
  `idDatastore` int(10) unsigned default NULL,
  KEY `disk_idManagement_FK` (`idManagement`),
  KEY `disk_management_datastore_FK` (`idDatastore`),
  CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE,
  CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `datastore` (`idDatastore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

