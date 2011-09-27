-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --
alter table `kinton`.`user` add creationDate timestamp DEFAULT '1986-02-26 00:00:00' NOT NULL;
ALTER TABLE `kinton`.`vlan_network` ADD COLUMN `networktype` varchar(15) DEFAULT 'INTERNAL';
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`);
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`);
ALTER TABLE `kinton`.`ip_pool_management` ADD COLUMN `available` boolean NOT NULL default 1; 

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

DELIMITER |

DROP PROCEDURE IF EXISTS update_vlans |
DROP PROCEDURE IF EXISTS update_ips |
DROP FUNCTION IF EXISTS number_of_nodes |
DROP FUNCTION IF EXISTS next_ip |

CREATE FUNCTION number_of_nodes(intmask INTEGER) RETURNS INTEGER
    BEGIN 
        IF intmask = 30 THEN RETURN 2; END IF;
        IF intmask = 29 THEN RETURN 6; END IF;
        IF intmask = 28 THEN RETURN 14; END IF;
        IF intmask = 27 THEN RETURN 30; END IF;
        IF intmask = 26 THEN RETURN 62; END IF;
        IF intmask = 25 THEN RETURN 126; END IF;
        IF intmask = 24 THEN RETURN 254; END IF;
        IF intmask = 23 THEN RETURN 510; END IF;
        IF intmask = 22 THEN RETURN 1024; END IF;
        RETURN 0; 
    END |

CREATE FUNCTION next_ip(net_address VARCHAR(40)) RETURNS VARCHAR(40)
    BEGIN
        DECLARE first_octets VARCHAR(40);
        DECLARE last_octet INTEGER;
        SET first_octets = SUBSTRING_INDEX(net_address, '.', 3);
        SET last_octet = CAST(SUBSTRING(net_address, LENGTH(first_octets) + 2) AS UNSIGNED) + 1;
        RETURN CONCAT(first_octets, '.', CAST(last_octet AS CHAR));
    END |

CREATE PROCEDURE update_ips(vlan_id INTEGER, vlan_name VARCHAR(40), net_address VARCHAR(40), mask INTEGER, dhcp_id INTEGER)
    BEGIN
        DECLARE number_of_ips INTEGER;
        DECLARE current_ip VARCHAR(40) DEFAULT net_address;
        DECLARE exists_ip VARCHAR(40);
        DECLARE index_ips INTEGER DEFAULT 0;
        SET number_of_ips = number_of_nodes(mask);
        WHILE index_ips < number_of_ips DO
            SET exists_ip = NULL;
            SET current_ip = next_ip(current_ip);
            SELECT ip.ip INTO exists_ip from ip_pool_management ip where vlan_network_id = vlan_id and ip.ip = current_ip; 
            IF exists_ip IS NULL THEN
                INSERT INTO `kinton`.`rasd_management`(idResourceType, idVirtualDataCenter, idVM, idResource, idVirtualApp) VALUES (10, NULL, NULL, NULL, NULL);
                INSERT INTO `kinton`.`ip_pool_management`(idManagement, dhcp_service_id, mac, name, ip, configureGateway, vlan_network_name, vlan_network_id, quarantine, available) VALUES (LAST_INSERT_ID(), dhcp_id, NULL, NULL, current_ip, 0, vlan_name, vlan_id, 0, 0);
            END IF;
            SET index_ips = index_ips + 1;
        END WHILE;
    END |

CREATE PROCEDURE update_vlans()
    BEGIN
        DECLARE vlan_id INTEGER;
        DECLARE vlan_name VARCHAR(40);
        DECLARE address VARCHAR(40);
        DECLARE intmask INTEGER;
        DECLARE dhcp_id INTEGER;
        DECLARE done TINYINT DEFAULT 0;
        DECLARE cur_vlans CURSOR FOR SELECT vn.vlan_network_id, vn.network_name, nc.network_address, nc.mask, nc.dhcp_service_id FROM `kinton`.`vlan_network` vn, `kinton`.`datacenter` dc, `kinton`.`network_configuration` nc WHERE dc.network_id = vn.network_id AND vn.network_configuration_id = nc.network_configuration_id;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

        OPEN cur_vlans;

        vlan_loop: LOOP
            FETCH cur_vlans INTO vlan_id, vlan_name, address, intmask, dhcp_id;
            IF done THEN LEAVE vlan_loop; END IF;
            UPDATE `kinton`.`vlan_network` SET networktype = 'PUBLIC' WHERE vlan_network_id = vlan_id;
            CALL update_ips(vlan_id, vlan_name, address, intmask, dhcp_id);
        END LOOP;

        CLOSE cur_vlans;
    END |

CALL update_vlans |

DROP PROCEDURE update_vlans |
DROP PROCEDURE update_ips |
DROP FUNCTION number_of_nodes |
DROP FUNCTION next_ip |

DELIMITER ;

-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --


