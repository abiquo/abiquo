use kinton;
-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --
CREATE TABLE  `kinton`.`enterprise_properties` (
  `idProperties` int(11) unsigned NOT NULL auto_increment,
  `enterprise` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY  (`idProperties`),
  CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE  `kinton`.`enterprise_properties_map` (
 `enterprise_properties` int(11) unsigned NOT NULL,
  `map_key` varchar(30) NOT NULL,
  `value` varchar(50) default NULL, 
  CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `enterprise_properties` (`idProperties`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");

INSERT INTO `kinton`.`enterprise_properties` VALUES  (1,1);

INSERT INTO `kinton`.`enterprise_properties_map` VALUES  (1,'Support e-mail','support@abiquo.com');

/*!40000 ALTER TABLE `enterprise_properties` ENABLE KEYS */;

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --
