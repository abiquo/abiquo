-- DELTA 1.7.6 to 1.8.0

DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;

DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;

DELIMITER |
CREATE TRIGGER `kinton`.`update_rasd_management_update_stats` AFTER UPDATE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50);
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE ipAddress VARCHAR(20);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --	   
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
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
			-- Volume removed from a Vapp
			IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    IF idState = 1 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    END IF;                         
			END IF;
			-- Volume added from a Vapp
			IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			    IF idState = 1 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
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
CREATE TRIGGER `kinton`.`update_rasd_update_stats` AFTER UPDATE ON `kinton`.`rasd`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE idThisVirtualDataCenter INTEGER;
        DECLARE isReserved INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --
            IF OLD.limitResource != NEW.limitResource THEN
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisVirtualDataCenter, idThisEnterprise
                FROM rasd_management rm, virtualdatacenter vdc
                WHERE rm.idResource = NEW.instanceID
                AND vdc.idVirtualDataCenter=rm.idVirtualDataCenter;
                -- check if this is reserved
                SELECT count(*) INTO isReserved
                FROM volume_management vm, rasd_management rm
                WHERE vm.idManagement  = rm.idManagement
                AND NEW.instanceID = rm.idResource
                AND (vm.state = 1);
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+ NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;                
                IF isReserved != 0 THEN
                -- si hay volAttached se debe actualizar el storageUsed
                    UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed +  NEW.limitResource - OLD.limitResource WHERE idDataCenter = idDataCenterObj;                    
                    UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idEnterprise = idThisEnterprise;
                    UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idThisEnterprise;
                    UPDATE IGNORE vdc_enterprise_stats 
                    SET     volCreated = volCreated - 1,
                        extStorageUsed = extStorageUsed +  NEW.limitResource - OLD.limitResource 
                    WHERE idVirtualDataCenter = idThisVirtualDataCenter;
                END IF;        
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('UPDATE_STORAGE', NEW.instanceID, NEW.elementName, idThisVirtualDataCenter, idThisEnterprise, NEW.limitResource);
                END IF;
            END IF;
        END IF;
    END;    
|
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
    AND (vm.state = 1)
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
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
    AND state = 1;
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
    AND (vm.state = 1)
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
    AND rm.idManagement = ipm.idManagement
    AND rm.idVM IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id                
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
CREATE PROCEDURE `kinton`.CalculateVappEnterpriseStats()
   BEGIN
  DECLARE idVirtualAppObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE idVirtualDataCenter INTEGER;
  DECLARE vappName VARCHAR(45);
  DECLARE vdcName VARCHAR(45);
  DECLARE vmCreated MEDIUMINT UNSIGNED;
  DECLARE vmActive MEDIUMINT UNSIGNED;
  DECLARE volAssociated MEDIUMINT UNSIGNED;
  DECLARE volAttached MEDIUMINT UNSIGNED;

  DECLARE no_more_vapps INTEGER;

  DECLARE curDC CURSOR FOR SELECT vapp.idVirtualApp, vapp.idEnterprise, vapp.idVirtualDataCenter, vapp.name, vdc.name FROM virtualapp vapp, virtualdatacenter vdc WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vapps = 1;

  SET no_more_vapps = 0;
  SET idVirtualAppObj = -1;

  OPEN curDC;

  TRUNCATE vapp_enterprise_stats;

  dept_loop:WHILE(no_more_vapps = 0) DO
    FETCH curDC INTO idVirtualAppObj, idEnterprise, idVirtualDataCenter, vappName, vdcName;
    IF no_more_vapps=1 THEN
        LEAVE dept_loop;
    END IF;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmCreated
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED"
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state = "RUNNING"
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAssociated
    FROM rasd_management rm
    WHERE rm.idVirtualApp = idVirtualAppObj
    AND rm.idResourceType=8;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO volAttached
    FROM volume_management vm, rasd_management rm
    WHERE rm.idManagement = vm.idManagement
    AND rm.idVirtualApp = idVirtualAppObj
    AND state = 1;

    -- Inserts stats row
    INSERT INTO vapp_enterprise_stats (idVirtualApp,idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached)
    VALUES (idVirtualAppObj, idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached);


  END WHILE dept_loop;
  CLOSE curDC;

   END;
|
DELIMITER ;
