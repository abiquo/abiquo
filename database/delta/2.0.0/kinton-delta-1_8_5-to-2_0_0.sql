
-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures



-- ---------------------------------------------- --
--                 TABLE DROP                     --
-- ---------------------------------------------- --

ALTER TABLE `kinton`.`ip_pool_management` DROP FOREIGN KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP KEY `ippool_dhcpservice_FK`;
ALTER TABLE `kinton`.`ip_pool_management` DROP COLUMN dhcp_service_id;
ALTER TABLE `kinton`.`network_configuration` DROP FOREIGN KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP KEY `configuration_dhcp_FK`;
ALTER TABLE `kinton`.`network_configuration` DROP COLUMN `dhcp_service_id`;

DROP TABLE IF EXISTS `kinton`.`dhcp_service`;


-- ---------------------------------------------- --
--                  TABLE CREATION                --
-- ---------------------------------------------- --



-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --





-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --

DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_ip_pool_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_network_configuration_update_stats`;

--
--
--
DELIMITER |
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) DEFAULT '';
    DECLARE no_more_ipsfreed INT;
    DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement   
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.vlan_network_id = vn.vlan_network_id
           AND vn.network_configuration_id = nc.network_configuration_id
           AND vn.network_id = dc.network_id
       AND vn.networktype = 'PUBLIC'
           AND ra.idManagement = ipm.idManagement
           AND ra.idVirtualDataCenter = OLD.idVirtualDataCenter;
       DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_ipsfreed = 1;   
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
            UPDATE IGNORE cloud_usage_stats SET numVDCCreated = numVDCCreated-1 WHERE idDataCenter = OLD.idDataCenter;  
            -- Remove Stats
            DELETE FROM vdc_enterprise_stats WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
           --   
    SET no_more_ipsfreed = 0;       
        OPEN curIpFreed;            
        my_loop:WHILE(no_more_ipsfreed=0) DO 
        FETCH curIpFreed INTO currentDataCenter, currentIpAddress, currentIdManagement;
        IF no_more_ipsfreed=1 THEN
                    LEAVE my_loop;
             END IF;
--      INSERT INTO debug_msg (msg) VALUES (CONCAT('IP_FREED: ',currentIpAddress, ' - idManagement: ', currentIdManagement, ' - OLD.idVirtualDataCenter: ', OLD.idVirtualDataCenter, ' - idEnterpriseObj: ', OLD.idEnterprise));
        -- We reset MAC and NAME for the reserved IPs. Java code should do this!
        UPDATE ip_pool_management set mac=NULL, name=NULL WHERE idManagement = currentIdManagement;
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                    CALL AccountingIPsRegisterEvents('IP_FREED',currentIdManagement,currentIpAddress,OLD.idVirtualDataCenter, OLD.idEnterprise);
            END IF;                    
        UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = OLD.idEnterprise; 
        END WHILE my_loop;         
        CLOSE curIpFreed;
        END IF;
    END;

--
--
--

CREATE TRIGGER `kinton`.`update_rasd_management_update_stats` AFTER UPDATE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50);
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE ipAddress VARCHAR(20);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --     
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateRASD: ',idState,' - ', IFNULL(OLD.idVirtualApp, 'OLD.idVirtualApp es NULL'), IFNULL(NEW.idVirtualApp, 'NEW.idVirtualApp es NULL')));   
        -- Detectamos cambios de VDC: V2V
        IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter AND OLD.idVirtualApp = NEW.idVirtualApp THEN
            UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
            UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
            IF idState = 1 THEN
                UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
            END IF;
        ELSE            
                IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
                -- Volume was changed to another VDC not in a V2V operation (cold move)
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
            END IF;
            -- Volume added from a Vapp
            IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
                UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
                UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                IF idState = 1 THEN
                    UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
                    UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;                         
            END IF;
            -- Volume removed from a Vapp
            IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
                UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
                UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                IF idState = 1 THEN
                SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
                FROM virtualdatacenter vdc
                WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
                SELECT r.limitResource INTO reservedSize
                FROM rasd r
                WHERE r.instanceID = OLD.idResource;
                -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));    
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed - reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed - reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed - reservedSize
                WHERE idVirtualDataCenter = OLD.idVirtualDatacenter;
                    UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
                END IF;                 
            END IF;
            -- Volume added to VDC
            IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN        
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                -- Stateful are always Attached 
                IF idState = 1 THEN
                    UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
                END IF;
            END IF;
            -- Volume removed from VDC
            IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
                UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                -- Stateful are always Attached
                IF idState = 1 THEN
                    UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;                     
                END IF;
            END IF;                         
                END IF;
            END IF;
            -- From old `autoDetachVolume`
            -- UPDATE IGNORE volume_management v set v.state = 0
            -- WHERE v.idManagement = OLD.idManagement;
            -- Checks for used IPs
            IF OLD.idVM IS NULL AND NEW.idVM IS NOT NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed + 1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
            END IF;
            -- Checks for unused IPs
            IF OLD.idVM IS NOT NULL AND NEW.idVM IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
            END IF;
            -- Checks for unreserved IPs
            IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter, ipm.ip INTO idDataCenterObj, ipAddress
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
                AND OLD.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPReserved
                IF idDataCenterObj IS NOT NULL THEN
                    UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    -- Registers Accounting Event
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;                    
                    UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = idDataCenterObj;
                    IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                        CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
                    END IF;                    
                END IF;
            END IF;
        END IF;
    END;
    
|  

CREATE TRIGGER `kinton`.`delete_ip_pool_management_update_stats` AFTER DELETE ON `kinton`.`ip_pool_management`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      -- Query for Public Ips deleted (disabled)
      SELECT distinct dc.idDataCenter INTO idDataCenterObj
      FROM vlan_network vn, network_configuration nc, datacenter dc
       WHERE OLD.vlan_network_id = vn.vlan_network_id
      AND vn.network_id = dc.network_id;
      IF idDataCenterObj IS NOT NULL THEN
    -- detects IP disabled/enabled at Edit Public Ips
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|
DELIMITER ;

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --

DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;

--
--
--
DELIMITER |
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
    AND (vm.state = 1)
    AND sd.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsTotal
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND vn.networktype = 'PUBLIC'             
    AND ipm.mac IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id
    AND vn.networktype = 'PUBLIC'             
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
    SELECT IF (SUM(cpu*cpuRatio) IS NULL,0,SUM(cpu*cpuRatio)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(hd) IS NULL,0,SUM(hd)) , IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)), IF (SUM(hdUsed) IS NULL,0,SUM(hdUsed)) INTO vCpuTotal, vMemoryTotal, vStorageTotal, vCpuUsed, vMemoryUsed, vStorageUsed
    FROM physicalmachine
    WHERE idDataCenter = idDataCenterObj
    AND idState = 3; 
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
|
CREATE PROCEDURE `kinton`.CalculateEnterpriseResourcesStats()
   BEGIN
  DECLARE idEnterpriseObj INTEGER;
  DECLARE vCpuReserved BIGINT UNSIGNED;
  DECLARE vCpuUsed BIGINT UNSIGNED;
  DECLARE memoryReserved BIGINT UNSIGNED;
  DECLARE memoryUsed BIGINT UNSIGNED;
  DECLARE localStorageReserved BIGINT UNSIGNED;
  DECLARE localStorageUsed BIGINT UNSIGNED;
  DECLARE extStorageReserved BIGINT UNSIGNED; 
  DECLARE extStorageUsed BIGINT UNSIGNED; 
  DECLARE publicIPsReserved BIGINT UNSIGNED;
  DECLARE publicIPsUsed BIGINT UNSIGNED;
  DECLARE vlanReserved BIGINT UNSIGNED; 
  DECLARE vlanUsed BIGINT UNSIGNED; 
  -- DECLARE repositoryReserved BIGINT UNSIGNED; -- TBD
  -- DECLARE repositoryUsed BIGINT UNSIGNED; -- TBD

  DECLARE no_more_enterprises INTEGER;

  DECLARE curDC CURSOR FOR SELECT idEnterprise FROM enterprise;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_enterprises=1;

  SET no_more_enterprises = 0;
  SET idEnterpriseObj = -1;

  OPEN curDC;

  TRUNCATE enterprise_resources_stats;

  dept_loop:WHILE(no_more_enterprises = 0) DO
    FETCH curDC INTO idEnterpriseObj;
    IF no_more_enterprises=1 THEN
        LEAVE dept_loop;
    END IF;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Iteracion Enterprise: ',idEnterpriseObj));
    --
    SELECT cpuHard, ramHard, hdHard, storageHard, vlanHard INTO vCpuReserved, memoryReserved, localStorageReserved, extStorageReserved, vlanReserved
    FROM enterprise e
    WHERE e.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (SUM(vm.cpu) IS NULL, 0, SUM(vm.cpu)), IF (SUM(vm.ram) IS NULL, 0, SUM(vm.ram)), IF (SUM(vm.hd) IS NULL, 0, SUM(vm.hd)) INTO vCpuUsed, memoryUsed, localStorageUsed
    FROM virtualmachine vm
    WHERE vm.state = "RUNNING"
    AND vm.idType = 1
    AND vm.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
    FROM virtualdatacenter vdc, vlan_network vn
    WHERE vdc.networktypeID=vn.network_id
    AND vdc.idEnterprise=idEnterpriseObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO extStorageUsed
    FROM rasd_management rm, rasd r, volume_management vm, virtualdatacenter vdc
    WHERE rm.idManagement = vm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND r.instanceID = rm.idResource
    AND (vm.state = 1 OR vm.state = 2)
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id   
    AND vn.networktype = 'PUBLIC'             
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id            
    AND vn.networktype = 'PUBLIC'    
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND rm.idVM IS NOT NULL
    AND vdc.idEnterprise = idEnterpriseObj;


    -- Inserts stats row
    INSERT INTO enterprise_resources_stats (idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed)
     VALUES (idEnterpriseObj,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed);

  END WHILE dept_loop;
  CLOSE curDC;

   END;

|

CREATE PROCEDURE `kinton`.CalculateVdcEnterpriseStats()
   BEGIN
  DECLARE idVirtualDataCenterObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE vdcName VARCHAR(45);
  DECLARE vmCreated MEDIUMINT UNSIGNED;
  DECLARE vmActive MEDIUMINT UNSIGNED;
  DECLARE volCreated MEDIUMINT UNSIGNED;
  DECLARE volAssociated MEDIUMINT UNSIGNED;
  DECLARE volAttached MEDIUMINT UNSIGNED;
  DECLARE vCpuReserved BIGINT UNSIGNED; 
  DECLARE vCpuUsed BIGINT UNSIGNED; 
  DECLARE memoryReserved BIGINT UNSIGNED;
  DECLARE memoryUsed BIGINT UNSIGNED; 
  DECLARE localStorageReserved BIGINT UNSIGNED; 
  DECLARE localStorageUsed BIGINT UNSIGNED; 
  DECLARE extStorageReserved BIGINT UNSIGNED; 
  DECLARE extStorageUsed BIGINT UNSIGNED; 
  DECLARE publicIPsReserved MEDIUMINT UNSIGNED;
  DECLARE publicIPsUsed MEDIUMINT UNSIGNED;
  DECLARE vlanReserved MEDIUMINT UNSIGNED; 
  DECLARE vlanUsed MEDIUMINT UNSIGNED; 

  DECLARE no_more_vdcs INTEGER;

  DECLARE curDC CURSOR FOR SELECT vdc.idVirtualDataCenter, vdc.idEnterprise, vdc.name FROM virtualdatacenter vdc;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vdcs = 1;

  SET no_more_vdcs = 0;
  SET idVirtualDataCenterObj = -1;

  OPEN curDC;

  TRUNCATE vdc_enterprise_stats;

  dept_loop:WHILE(no_more_vdcs = 0) DO
    FETCH curDC INTO idVirtualDataCenterObj, idEnterprise, vdcName;
    IF no_more_vdcs=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmCreated
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state = "RUNNING";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volCreated
    FROM rasd_management rm
    WHERE rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAssociated
    FROM rasd_management rm
    WHERE rm.idVirtualApp IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAttached
    FROM volume_management vm, rasd_management rm
    WHERE rm.idManagement = vm.idManagement
    AND rm.idVirtualApp IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj
    AND state = 2;
    --
    SELECT IF (SUM(cpuHard) IS NULL, 0, SUM(cpuHard)), IF (SUM(ramHard) IS NULL, 0, SUM(ramHard)), IF (SUM(hdHard) IS NULL, 0, SUM(hdHard)), IF (SUM(storageHard) IS NULL, 0, SUM(storageHard)), IF (SUM(vlanHard) IS NULL, 0, SUM(vlanHard)) INTO vCpuReserved, memoryReserved, localStorageReserved, extStorageReserved, vlanReserved
    FROM virtualdatacenter 
    WHERE idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (SUM(vm.cpu) IS NULL, 0, SUM(vm.cpu)), IF (SUM(vm.ram) IS NULL, 0, SUM(vm.ram)), IF (SUM(vm.hd) IS NULL, 0, SUM(vm.hd)) INTO vCpuUsed, memoryUsed, localStorageUsed
    FROM virtualmachine vm, nodevirtualimage nvi, node n, virtualapp vapp
    WHERE vm.idVM = nvi.idVM
    AND nvi.idNode = n.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vm.state = "RUNNING"
    AND vm.idType = 1
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (SUM(r.limitResource) IS NULL, 0, SUM(r.limitResource)) INTO extStorageUsed
    FROM rasd_management rm, rasd r, volume_management vm
    WHERE rm.idManagement = vm.idManagement    
    AND r.instanceID = rm.idResource
    AND (vm.state = 1 OR vm.state = 2)
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id           
    AND vn.networktype = 'PUBLIC'     
    AND rm.idManagement = ipm.idManagement
    AND rm.idVM IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.vlan_network_id = vn.vlan_network_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
    AND vn.networktype = 'PUBLIC'
    AND rm.idManagement = ipm.idManagement
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vlanUsed
    FROM virtualdatacenter vdc, vlan_network vn
    WHERE vdc.networktypeID = vn.network_id
    AND vdc.idVirtualDataCenter = idVirtualDataCenterObj;
   -- 


    -- Inserts stats row
    INSERT INTO vdc_enterprise_stats (idVirtualDataCenter,idEnterprise,vdcName,vmCreated,vmActive,volCreated,volAssociated,volAttached, vCpuReserved, vCpuUsed, memoryReserved, memoryUsed, localStorageReserved, localStorageUsed, extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed)
    VALUES (idVirtualDataCenterObj,idEnterprise,vdcName,vmCreated,vmActive,volCreated,volAssociated,volAttached, vCpuReserved, vCpuUsed, memoryReserved, memoryUsed, localStorageReserved, localStorageUsed, extStorageReserved, extStorageUsed, publicIPsReserved, publicIPsUsed, vlanReserved, vlanUsed );


  END WHILE dept_loop;
  CLOSE curDC;

   END;
| 
DELIMITER ; 
CALL `kinton`.`CalculateCloudUsageStats`();
CALL `kinton`.`CalculateEnterpriseResourcesStats`();
CALL `kinton`.`CalculateVdcEnterpriseStats`();

  

  

