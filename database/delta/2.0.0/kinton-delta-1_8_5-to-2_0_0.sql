-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures

-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --

--
-- Definition of table `kinton`.`dhcpOption`
--
CREATE TABLE `kinton`.`dhcpOption` (
  `idDhcpOption` int(10) unsigned NOT NULL AUTO_INCREMENT ,
  `option` varchar(20) NOT NULL ,
  `description` varchar(100) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idDhcpOption`)
  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`vlans_dhcp`
--

CREATE  TABLE `kinton`.`vlans_dhcp` (
  `idVlan` INT(10) UNSIGNED NOT NULL ,
  `idDhcp` INT(10) UNSIGNED NOT NULL ,
  `version_c` INT(11) default 0,
  INDEX `fk_vlans_dhcp_vlan` (`idVlan` ASC) ,
  INDEX `fk_vlans_dhcp_dhcp` (`idDhcp` ASC) ,
  CONSTRAINT `fk_vlans_dhcp_vlan`
    FOREIGN KEY (`idVlan` )
    REFERENCES `kinton`.`vlan_network` (`vlan_network_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vlans_dhcp_dhcp`
    FOREIGN KEY (`idDhcp` )
    REFERENCES `kinton`.`dhcpOption` (`idDhcpOption` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage, DROP COLUMN hd, DROP COLUMN hdUsed;
ALTER TABLE `kinton`.`vlan_network` ADD COLUMN `networktype` varchar(15) DEFAULT 'internal';
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD COLUMN `default_vlan_network_id` int(11) unsigned DEFAULT NULL; 
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`),
ALTER TABLE `kinton`.`ip_pool_management` ADD COLUMN `available` boolean NOT NULL default 1; 


-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
("client.logout.url","","Redirect to this URL after logout (empty -> login screen)");

-- First I need to update some rows before to delete the `default_network` field
UPDATE `kinton`.`virtualdatacenter` vdc, `kinton`.`vlan_network` v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
ALTER TABLE `kinton`.`vlan_network` DROP COLUMN `default_network`;

INSERT INTO `kinton`.`dhcpOption` (`option`, `description`) VALUES
("121","");
INSERT INTO `kinton`.`dhcpOption` (`option`, `description`) VALUES
("249","");
-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

-- *************************************************
--
--  Procedures to calculate datastore size
--
-- *************************************************
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;

DELIMITER |

CREATE PROCEDURE `kinton`.`get_datastore_size_by_dc`(IN idDC INT, OUT size BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(d.size) IS NULL,0,SUM(d.size)) INTO size
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size_by_dc`(IN idDC INT, OUT usedSize BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(d.usedSize) IS NULL,0,SUM(d.usedSize)) INTO usedSize
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1;
END
--
|
--
CREATE PROCEDURE `kinton`.CalculateCloudUsageStats()
   BEGIN
  DECLARE idDataCenterObj INTEGER;
  DECLARE serversTotal BIGINT UNSIGNED;
  DECLARE serversRunning BIGINT UNSIGNED;
  DECLARE storageTotal BIGINT UNSIGNED;
  DECLARE storageUsed BIGINT UNSIGNED;
  DECLARE publicIPsTotal BIGINT UNSIGNED;
  DECLARE publicIPsReserved BIGINT UNSIGNED;
  DECLARE publicIPsUsed BIGINT UNSIGNED;
  DECLARE vMachinesTotal BIGINT UNSIGNED;
  DECLARE vMachinesRunning BIGINT UNSIGNED;
  DECLARE vCpuTotal BIGINT UNSIGNED;
  DECLARE vCpuReserved BIGINT UNSIGNED;
  DECLARE vCpuUsed BIGINT UNSIGNED;
  DECLARE vMemoryTotal BIGINT UNSIGNED;
  DECLARE vMemoryReserved BIGINT UNSIGNED;
  DECLARE vMemoryUsed BIGINT UNSIGNED;
  DECLARE vStorageReserved BIGINT UNSIGNED;
  DECLARE vStorageUsed BIGINT UNSIGNED;
  DECLARE vStorageTotal BIGINT UNSIGNED;
  DECLARE numUsersCreated BIGINT UNSIGNED;
  DECLARE numVDCCreated BIGINT UNSIGNED;
  DECLARE numEnterprisesCreated BIGINT UNSIGNED;
  DECLARE storageReserved BIGINT UNSIGNED; 
  DECLARE vlanReserved BIGINT UNSIGNED; 
  DECLARE vlanUsed BIGINT UNSIGNED; 

  DECLARE no_more_dcs INTEGER;

  DECLARE curDC CURSOR FOR SELECT idDataCenter FROM datacenter;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_dcs=1;

  SET no_more_dcs=0;
  SET idDataCenterObj = -1;

  OPEN curDC;

  TRUNCATE cloud_usage_stats;

  dept_loop:WHILE(no_more_dcs=0) DO
    FETCH curDC INTO idDataCenterObj;
    IF no_more_dcs=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO serversTotal
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState!=2;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO serversRunning
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState=3;
    --
    SELECT IF (SUM(limitResource) IS NULL, 0, SUM(limitResource))   INTO storageTotal
    FROM rasd r, rasd_management rm, virtualdatacenter vdc
    WHERE rm.idResource = r.instanceID
    AND vdc.idVirtualDataCenter=rm.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO storageUsed
    FROM storage_pool sp, storage_device sd, volume_management vm, rasd_management rm, rasd r
    WHERE vm.idStorage = sp.idStorage
    AND sp.idStorageDevice = sd.id
    AND vm.idManagement = rm.idManagement
    AND r.instanceID = rm.idResource
    AND rm.idResourceType = 8
    AND (vm.state = 1 OR vm.state = 2)
    AND sd.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsTotal
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND ipm.mac IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND rm.idManagement = ipm.idManagement
    AND ipm.mac IS NOT NULL
    AND rm.idVM IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesTotal
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED"
    and v.idType = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesRunning
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state = "RUNNING"
    and v.idType = 1;
    --
    SELECT IF (SUM(cpu*cpuRatio) IS NULL,0,SUM(cpu*cpuRatio)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)) INTO vCpuTotal, vMemoryTotal, vCpuUsed, vMemoryUsed
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState = 3; 
    --
    CALL get_datastore_size_by_dc(idDataCenterObj,vStorageTotal);
    CALL get_datastore_used_size_by_dc(idDataCenterObj,vStorageUsed);
    --
    SELECT IF (SUM(vlanHard) IS NULL, 0, SUM(vlanHard))  INTO vlanReserved
    FROM enterprise_limits_by_datacenter 
    WHERE idDataCenter = idDataCenterObj AND idEnterprise IS NOT NULL;

    -- Inserts stats row
    INSERT INTO cloud_usage_stats
    (idDataCenter,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,vCpuReserved,vCpuUsed,
    vMemoryTotal,vMemoryReserved,vMemoryUsed,
    vStorageReserved,vStorageUsed,vStorageTotal,
    vlanReserved,
    numUsersCreated,numVDCCreated,numEnterprisesCreated)
    VALUES
    (idDataCenterObj,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,0,vCpuUsed,
    vMemoryTotal,0,vMemoryUsed,
    0,vStorageUsed,vStorageTotal,
    vlanReserved,
    0,0,0);

  END WHILE dept_loop;
  CLOSE curDC;

  -- All Cloud Stats (idDataCenter -1): vCpuReserved, VMemoryReserved, VStorageReserved, NumUsersCreated, NumVDCCreated, NumEnterprisesCreated
  SELECT IF (SUM(cpuHard) IS NULL,0,SUM(cpuHard)), IF (SUM(ramHard) IS NULL,0,SUM(ramHard)), IF (SUM(hdHard) IS NULL,0,SUM(hdHard)), IF (SUM(storageHard) IS NULL,0,SUM(storageHard)) INTO vCpuReserved, vMemoryReserved, vStorageReserved, storageReserved
  FROM enterprise e;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numUsersCreated
  FROM user;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numVDCCreated
  FROM virtualdatacenter vdc;
  --
  SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numEnterprisesCreated
  FROM enterprise e;
  --
  SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
  FROM vlan_network;

  -- Inserts stats row
    INSERT INTO cloud_usage_stats
    (idDataCenter,
    serversTotal,serversRunning,
    storageTotal,storageUsed,
    publicIPsTotal,publicIPsReserved,publicIPsUsed,
    vMachinesTotal,vMachinesRunning,
    vCpuTotal,vCpuReserved,vCpuUsed,
    vMemoryTotal,vMemoryReserved,vMemoryUsed,
    vStorageReserved,vStorageUsed,vStorageTotal,
    vlanUsed,
    numUsersCreated,numVDCCreated,numEnterprisesCreated)
    VALUES
    (-1,
    0,0,
    0,0,
    0,0,0,
    0,0,
    0,vCpuReserved,0,
    0,vMemoryReserved,0,
    vStorageReserved,0,0,
    vlanUsed,
    numUsersCreated,numVDCCreated,numEnterprisesCreated);
   END;
--
|
--
DELIMITER ;

-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --
DROP TRIGGER IF EXISTS `kinton`.`create_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`; 
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_datastore_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_datastore_update_stats`;


DELIMITER |

-- *************************************************
-- Triggers ON Physical Machine
-- *************************************************

CREATE TRIGGER `kinton`.`create_physicalmachine_update_stats` AFTER INSERT ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
               vCpuUsed=vCpuUsed+NEW.cpuUsed, vMemoryUsed=vMemoryUsed+NEW.ramUsed
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
    IF NEW.idState != 2 THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal = serversTotal+1, 
               vCpuTotal=vCpuTotal+(NEW.cpu*NEW.cpuRatio), vMemoryTotal=vMemoryTotal+NEW.ram
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `kinton`.`create_datastore_update_stats` AFTER INSERT ON `kinton`.`datastore_assignment`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
DECLARE enabled INT UNSIGNED;
DECLARE usedSize BIGINT UNSIGNED;
DECLARE size BIGINT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm WHERE pm.idPhysicalMachine = NEW.idPhysicalmachine;
SELECT d.enabled, d.usedSize, d.size INTO enabled, usedSize, size FROM datastore d WHERE d.idDatastore = NEW.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF machineState = 3 THEN
        IF enabled = 1 THEN
            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed + usedSize
            WHERE cus.idDataCenter = idDatacenter;
        END IF;
    END IF;
    IF machineState != 2 THEN
        IF enabled = 1 THEN
            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + size
            WHERE cus.idDataCenter = idDatacenter;
        END IF;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
               vCpuUsed=vCpuUsed-OLD.cpuUsed, vMemoryUsed=vMemoryUsed-OLD.ramUsed
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
               vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio), vMemoryTotal=vMemoryTotal-OLD.ram
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
END IF;
END;

--
|
--

CREATE TRIGGER `kinton`.`delete_datastore_update_stats` BEFORE DELETE ON `kinton`.`datastore`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = OLD.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF machineState = 3 THEN
        IF OLD.enabled = 1 THEN
            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize
            WHERE cus.idDataCenter = idDatacenter;
        END IF;
    END IF;
    IF machineState NOT IN (2, 6, 7) THEN
        IF OLD.enabled = 1 THEN
            UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
            WHERE cus.idDataCenter = idDatacenter;
        END IF;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreSize BIGINT UNSIGNED;
DECLARE oldDatastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
            -- Machine not managed changes into managed; or disabled_by_ha to Managed
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1,
                   vCpuTotal=vCpuTotal + (NEW.cpu*NEW.cpuRatio),
                   vMemoryTotal=vMemoryTotal + NEW.ram
            WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
            -- Machine managed changes into not managed or DisabledByHA
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
                   vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
                   vMemoryTotal=vMemoryTotal-OLD.ram
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
            -- Stopped / Halted / Not provisioned passes to Managed (Running)
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
                   vCpuUsed=vCpuUsed+NEW.cpuUsed,
                   vMemoryUsed=vMemoryUsed+NEW.ramUsed
            WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
            -- Managed (Running) passes to Stopped / Halted / Not provisioned
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
                   vCpuUsed=vCpuUsed-OLD.cpuUsed,
                   vMemoryUsed=vMemoryUsed-OLD.ramUsed
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    ELSE
        -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
            -- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
            UPDATE IGNORE cloud_usage_stats SET vCpuTotal=vCpuTotal+((NEW.cpu-OLD.cpu)*NEW.cpuRatio),
                   vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
            UPDATE IGNORE cloud_usage_stats SET vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
                   vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    END IF;
END IF;
END;
--
|
--
CREATE TRIGGER `kinton`.`update_datastore_update_stats` AFTER UPDATE ON `kinton`.`datastore`
    FOR EACH ROW BEGIN
	DECLARE idDatacenter INT UNSIGNED;
	DECLARE machineState INT UNSIGNED;
	SELECT pm.idDatacenter, pm.idState INTO idDatacenter, machineState FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
	WHERE da.idDatastore = NEW.idDatastore;
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
	    IF OLD.enabled = 1 THEN
		IF NEW.enabled = 1 THEN
		    IF machineState IN (2, 6, 7) THEN
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size
		        WHERE cus.idDatacenter = idDatacenter;
		    ELSE
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size,
		        cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		    END IF;
		ELSEIF NEW.enabled = 0 THEN
		    IF machineState IN (2, 6, 7) THEN
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
		        WHERE cus.idDatacenter = idDatacenter;
		    ELSE
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size,
		        cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize WHERE cus.idDatacenter = idDatacenter;
		    END IF;
		END IF;
	    ELSE
		IF NEW.enabled = 1 THEN
		    IF machineState IN (2, 6, 7) THEN
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size
		        WHERE cus.idDatacenter = idDatacenter;
		    ELSE
		        UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size,
		        cus.vStorageUsed = cus.vStorageUsed + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
		    END IF;
		END IF;
	    END IF;
	END IF;
    END;
--
|
--
DELIMITER ;
