DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;
|
--
--
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
    SELECT IF (SUM(cpu) IS NULL,0,SUM(cpu)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(hd) IS NULL,0,SUM(hd)) , IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)), IF (SUM(hdUsed) IS NULL,0,SUM(hdUsed)) INTO vCpuTotal, vMemoryTotal, vStorageTotal, vCpuUsed, vMemoryUsed, vStorageUsed
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

|
--
--
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
--
--
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

|
--
DELIMITER ;


-- ******************************************************************************************
--
--  Procedures to calculate datastore size
--
-- ****************************************************************************************
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_size_by_dc`;
DROP PROCEDURE IF EXISTS `kinton`.`get_datastore_used_size_by_dc`;

DELIMITER |
--
CREATE PROCEDURE `kinton`.`get_datastore_size_by_dc`(IN idDC INT, OUT size BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.size) IS NULL,0,SUM(ds_view.size)) INTO size
    FROM (SELECT d.size as size FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END
--
|
--
CREATE PROCEDURE `kinton`.`get_datastore_used_size_by_dc`(IN idDC INT, OUT usedSize BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.usedSize) IS NULL,0,SUM(ds_view.usedSize)) INTO usedSize
    FROM (SELECT d.usedSize as usedSize FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicialMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END
--
|
--
DELIMITER ;


DROP PROCEDURE IF EXISTS `kinton`.`add_version_column_to_all`;

DELIMITER |
CREATE PROCEDURE `kinton`.`add_version_column_to_all`()
BEGIN
    DECLARE currentTableName VARCHAR(64);
    DECLARE no_more_tables INTEGER;
    DECLARE curAllTables CURSOR FOR SELECT TABLE_NAME from `information_schema`.TABLES where TABLE_SCHEMA = "kinton" AND TABLE_TYPE="BASE TABLE";
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_tables=1;
    SET no_more_tables = 0;
    OPEN curAllTables;  
    dept_loop:WHILE(no_more_tables = 0) DO
        FETCH curAllTables INTO currentTableName;
        IF no_more_tables = 1 THEN
           LEAVE dept_loop;
        END IF;
        IF NOT EXISTS( (SELECT * FROM `information_schema`.COLUMNS WHERE TABLE_SCHEMA= "kinton" AND TABLE_NAME=currentTableName AND COLUMN_NAME='version_c') ) THEN
            SET @alter_sql=CONCAT('ALTER IGNORE TABLE ', currentTableName,' ADD COLUMN version_c int default 0;');
            PREPARE stmt from @alter_sql;
            EXECUTE stmt;
        END IF;
    END WHILE dept_loop;
    CLOSE curAllTables;
END;
|
DELIMITER ;


