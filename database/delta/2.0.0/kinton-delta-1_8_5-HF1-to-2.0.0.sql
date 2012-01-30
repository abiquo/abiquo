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

DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;

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
    AND vn.networktype = 'PUBLIC'             
    AND ipm.mac IS NOT NULL
    AND dc.idDataCenter = idDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
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
    AND v.state != "NOT_ALLOCATED" AND v.state != "UNKNOWN" 
    and v.idType = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesRunning
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state = "ON"
    and v.idType = 1;
    --
    SELECT IF (SUM(cpu*cpuRatio) IS NULL,0,SUM(cpu*cpuRatio)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(hd) IS NULL,0,SUM(hd)) , IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)), IF (SUM(hdUsed) IS NULL,0,SUM(hdUsed)) INTO vCpuTotal, vMemoryTotal, vStorageTotal, vCpuUsed, vMemoryUsed, vStorageUsed
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
    WHERE vm.state = "ON"
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
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id   
    AND vn.networktype = 'PUBLIC'             
    AND rm.idManagement = ipm.idManagement
    AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter
    AND vdc.idEnterprise = idEnterpriseObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsUsed
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
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
--
|
--
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
    AND v.state != "NOT_ALLOCATED" AND v.state != "UNKNOWN";
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state = "ON";
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
    AND vm.state = "ON"
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
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
    AND vn.network_configuration_id = nc.network_configuration_id
    AND vn.network_id = dc.network_id           
    AND vn.networktype = 'PUBLIC'     
    AND rm.idManagement = ipm.idManagement
    AND rm.idVM IS NOT NULL
    AND rm.idVirtualDataCenter = idVirtualDataCenterObj;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO publicIPsReserved
    FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm
    WHERE ipm.dhcp_service_id=nc.dhcp_service_id
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
--
|
--
-- To be DONE when showing Datacenter Stats by Enterprise
-- CREATE PROCEDURE `kinton`.CalculateDcEnterpriseStats()
--   BEGIN
--   END;
--
--
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
    AND v.state != "NOT_ALLOCATED" AND v.state != "UNKNOWN"
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state = "ON"
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
    AND state = 2;

    -- Inserts stats row
    INSERT INTO vapp_enterprise_stats (idVirtualApp,idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached)
    VALUES (idVirtualAppObj, idEnterprise,idVirtualDataCenter,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached);


  END WHILE dept_loop;
  CLOSE curDC;

   END;
--
|
--
DELIMITER ;

-- ---------------------------------------------- --
--                   TRIGGERS                     --
-- ---------------------------------------------- --
DROP TRIGGER IF EXISTS `kinton`.`update_virtualapp_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_nodevirtualimage_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_nodevirtualimage_update_stats`;

DELIMITER |
CREATE TRIGGER `kinton`.`update_virtualapp_update_stats` AFTER UPDATE ON `kinton`.`virtualapp`
  FOR EACH ROW BEGIN
    DECLARE numVMachinesCreated INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    -- V2V: Vmachines moved between VDC
  IF NEW.idVirtualDataCenter != OLD.idVirtualDataCenter THEN
      -- calculate vmachines total and running in this Vapp
      SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO numVMachinesCreated
      FROM nodevirtualimage nvi, virtualmachine v, node n
      WHERE nvi.idNode IS NOT NULL
      AND v.idVM = nvi.idVM
      AND n.idNode = nvi.idNode
      AND n.idVirtualApp = NEW.idVirtualApp
      AND v.state != "NOT_ALLOCATED" AND v.state != "UNKNOWN"
      and v.idType = 1;
      UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated- numVMachinesCreated WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
      UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+ numVMachinesCreated WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
    END IF;
    -- Checks for changes
    IF OLD.name != NEW.name THEN
      -- Name changed !!!
      UPDATE IGNORE vapp_enterprise_stats SET vappName = NEW.name
      WHERE idVirtualApp = NEW.idVirtualApp;
    END IF;
  END IF;
  END;
--
|
--
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE costCodeObj VARCHAR(50);
	-- For debugging purposes only
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', OLD.idType, NEW.idType, OLD.state, NEW.state));	
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
        --  Updating enterprise_resources_stats: VCPU Used, Memory Used, Local Storage Used
        IF OLD.idHypervisor IS NULL OR (OLD.idHypervisor != NEW.idHypervisor) THEN
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE NEW.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        ELSE 
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE OLD.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        END IF;     
        --
        SELECT n.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM nodevirtualimage nvi, node n, virtualapp vapp
        WHERE NEW.idVM = nvi.idVM
        AND nvi.idNode = n.idNode
        AND vapp.idVirtualApp = n.idVirtualApp;   

	IF NEW.idType = 1 AND OLD.idType = 0 THEN
		-- Imported !!!
		UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		IF NEW.state = "ON" THEN 	
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualApp = idVirtualAppObj;
		        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
		        WHERE idDataCenter = idDataCenterObj;       
		        UPDATE IGNORE enterprise_resources_stats 
		            SET vCpuUsed = vCpuUsed + NEW.cpu,
		                memoryUsed = memoryUsed + NEW.ram,
		                localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idEnterprise = NEW.idEnterprise;
		        UPDATE IGNORE dc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + NEW.cpu,
		            memoryUsed = memoryUsed + NEW.ram,
		            localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
		        UPDATE IGNORE vdc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + NEW.cpu,
		            memoryUsed = memoryUsed + NEW.ram,
		            localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;	
		END IF;
	-- Main case: an imported VM changes its state (from LOCKED to ...)
	ELSEIF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "ON" THEN 
                -- New Active
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
                WHERE idDataCenter = idDataCenterObj;       
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            -- ELSEIF OLD.state = "ON" THEN           * This has to change, OLD.state is always LOCKED
		ELSEIF NEW.state = "OFF" OR NEW.state = "PAUSED" THEN
                -- Active Out
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed - NEW.cpu,
                        memoryUsed = memoryUsed - NEW.ram,
                        localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            END IF;     	    
            IF NEW.state = "CONFIGURED" THEN -- OR OLD.idType != NEW.idType
                -- VMachine Deployed or VMachine imported
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = "NOT_ALLOCATED" THEN 
                -- VMachine was deconfigured (still allocated)
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;         
        END IF;
        --
        SELECT IF(vi.cost_code IS NULL, "", vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
       		 END IF;              
	    END IF;
      END IF;
    END;
--
|
--
CREATE TRIGGER `kinton`.`create_nodevirtualimage_update_stats` AFTER INSERT ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE state VARCHAR(50);
    DECLARE type INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = NEW.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.state, vm.idType INTO state, type FROM virtualmachine vm WHERE vm.idVM = NEW.idVM;
      --
      IF state != "NOT_ALLOCATED" AND state != "UNKNOWN" AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF state = "ON" AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END;
--
|
--
CREATE TRIGGER `kinton`.`delete_nodevirtualimage_update_stats` AFTER DELETE ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE oldState VARCHAR(50);
    DECLARE type INTEGER;
    DECLARE isUsingIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = OLD.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
    SELECT state, idType INTO oldState, type FROM virtualmachine WHERE idVM = OLD.idVM;
    --
    IF type = 1 THEN
      IF oldState != "NOT_ALLOCATED" AND oldState != "UNKNOWN" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF oldState = "ON" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END IF;
  END;
--
|
--
DELIMITER ;

