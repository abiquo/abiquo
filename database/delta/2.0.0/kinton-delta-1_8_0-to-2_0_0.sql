-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`physicalmachine` DROP COLUMN realram, DROP COLUMN realcpu, DROP COLUMN realStorage, DROP COLUMN hd, DROP COLUMN hdUsed;

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

-- *************************************************
--
--  Procedures to calculate datastore size
--
-- *************************************************
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;

DELIMITER |
--
CREATE PROCEDURE `kinton`.`get_datastore_size`(IN idPM INT, OUT size BIGINT(20))
BEGIN
    SELECT IF (SUM(d.size) IS NULL,0,SUM(d.size)) INTO size
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    WHERE da.idPhysicalMachine = idPM AND d.enabled = 1;
END
--
| 
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size`(IN idPM INT, OUT usedSize BIGINT(20))
BEGIN
    SELECT IF (SUM(d.size) IS NULL,0,SUM(d.usedSize)) INTO usedSize
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    WHERE da.idPhysicalMachine = idPM AND d.enabled = 1;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_size_by_dc`(IN idDC INT, OUT size BIGINT)
BEGIN
    SELECT IF (SUM(d.size) IS NULL,0,SUM(d.size)) INTO size
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN phyisicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size_by_dc`(IN idDC INT, OUT usedSize BIGINT)
BEGIN
    SELECT IF (SUM(d.usedSize) IS NULL,0,SUM(d.usedSize)) INTO usedSize
    FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN phyisicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
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


DELIMITER |

-- *************************************************
-- Triggers ON Physical Machine
-- *************************************************

CREATE TRIGGER `kinton`.`create_physicalmachine_update_stats` AFTER INSERT ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT(20);
DECLARE datastoreSize BIGINT(20);
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.idState = 3 THEN
        CALL get_datastore_used_size(NEW.idPhysicalMachine,datastoreUsedSize);
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
               vStorageUsed = vStorageUsed + datastoreUsedSize,
               vCpuUsed=vCpuUsed+NEW.cpuUsed, vMemoryUsed=vMemoryUsed+NEW.ramUsed
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
    IF NEW.idState != 2 THEN
        CALL get_datastore_size(NEW.idPhysicalMachine,datastoreSize);
        UPDATE IGNORE cloud_usage_stats SET serversTotal = serversTotal+1, 
               vStorageTotal = vStorageTotal + datastoreSize,
               vCpuTotal=vCpuTotal+(NEW.cpu*NEW.cpuRatio), vMemoryTotal=vMemoryTotal+NEW.ram
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
END IF;
END

--
|
--

CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT(20);
DECLARE datastoreSize BIGINT(20);
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
        CALL get_datastore_used_size(OLD.idPhysicalMachine,datastoreUsedSize);
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
               vStorageUsed = vStorageUsed - datastoreUsedSize,
               vCpuUsed=vCpuUsed-OLD.cpuUsed, vMemoryUsed=vMemoryUsed-OLD.ramUsed
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
        CALL get_datastore_size(OLD.idPhysicalMachine,datastoreSize);
        UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
               vStorageTotal = vStorageTotal - datastoreSize,
               vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio), vMemoryTotal=vMemoryTotal-OLD.ram
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
END IF;
END;

--
|
--

CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
FOR EACH ROW BEGIN
DECLARE datastoreSize BIGINT(20);
DECLARE oldDatastoreSize BIGINT(20);
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
            -- Machine not managed changes into managed; or disabled_by_ha to Managed
            CALL get_datastore_size(NEW.idPhysicalMachine,datastoreSize);
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1,
                   vStorageTotal = vStorageTotal + datastoreSize,
                   vCpuTotal=vCpuTotal + (NEW.cpu*NEW.cpuRatio),
                   vMemoryTotal=vMemoryTotal + NEW.ram
            WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
            -- Machine managed changes into not managed or DisabledByHA
            CALL get_datastore_size(OLD.idPhysicalMachine,datastoreSize);
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
                   vStorageTotal = vStorageTotal - datastoreSize,
                   vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
                   vMemoryTotal=vMemoryTotal-OLD.ram
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
            -- Stopped / Halted / Not provisioned passes to Managed (Running)
            CALL get_datastore_used_size(NEW.idPhysicalMachine,datastoreSize);
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
                   vStorageUsed = vStorageUsed + datastoreSize,
                   vCpuUsed=vCpuUsed+NEW.cpuUsed,
                   vMemoryUsed=vMemoryUsed+NEW.ramUsed
            WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
            -- Managed (Running) passes to Stopped / Halted / Not provisioned
            CALL get_datastore_used_size(OLD.idPhysicalMachine,datastoreSize);
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
                   vStorageUsed = vStorageUsed - datastoreSize,
                   vCpuUsed=vCpuUsed-OLD.cpuUsed,
                   vMemoryUsed=vMemoryUsed-OLD.ramUsed
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    ELSE
        -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
            -- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
            CALL get_datastore_size(NEW.idPhysicalMachine,datastoreSize);
            CALL get_datastore_size(OLD.idPhysicalMachine,oldDatastoreSize);
            UPDATE IGNORE cloud_usage_stats SET vStorageTotal = vStorageTotal + (datastoreSize - oldDatastoreSize),
                   vCpuTotal=vCpuTotal+((NEW.cpu-OLD.cpu)*NEW.cpuRatio),
                   vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
            CALL get_datastore_used_size(NEW.idPhysicalMachine,datastoreSize);
            CALL get_datastore_used_size(OLD.idPhysicalMachine,oldDatastoreSize);
            UPDATE IGNORE cloud_usage_stats SET vStorageUsed = vStorageUsed + (datastoreSize - oldDatastoreSize),
                   vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
                   vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    END IF;
END IF;
END;

--
|
--
DELIMITER ;
