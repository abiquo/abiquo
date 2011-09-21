-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --


ALTER TABLE `kinton`.`vlan_network` ADD COLUMN `networktype` varchar(15) DEFAULT 'internal';
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`ip_pool_management` ADD COLUMN `available` boolean NOT NULL default 1; 


alter table `kinton`.`user` add creationDate timestamp DEFAULT '1986-02-26 00:00:00' NOT NULL;


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --



/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES (51,'APPLIB_ALLOW_MODIFY_SHARED',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES (1,51,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;

-- First I need to update some rows before to delete the `default_network` field
UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;



-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --


