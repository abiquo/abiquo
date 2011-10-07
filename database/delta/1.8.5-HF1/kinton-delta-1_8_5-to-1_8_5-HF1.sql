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

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

DELIMITER |

DROP PROCEDURE IF EXISTS update_vlans |

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
            UPDATE `kinton`.`vlan_network` SET networktype = 'EXTERNAL' WHERE vlan_network_id = vlan_id AND enterprise_id is not null;
        END LOOP;

        CLOSE cur_vlans;
    END |

CALL update_vlans |

DROP PROCEDURE update_vlans |


DELIMITER ;

-- remove a bug introduced in 1.6.8 and increased in 1.8.5
update ip_pool_management ip1, ip_pool_management ip2 set ip1.vlan_network_id = ip2.vlan_network_id where ip1.vlan_network_id is null and ip2.vlan_network_id is not null and ip1.ip = ip2.ip and ip1.dhcp_service_id = ip2.dhcp_service_id and ip1.idManagement != ip2.idManagement;

delete ip1 from ip_pool_management ip1, ip_pool_management ip2 where ip1.ip = ip2.ip and ip1.dhcp_service_id = ip2.dhcp_service_id and ip1.available = 0 and ip1.idManagement != ip2.idManagement;
