


DROP PROCEDURE IF EXISTS `kinton`.`AccountingVMRegisterEvents`;
DROP PROCEDURE IF EXISTS `kinton`.`AccountingStorageRegisterEvents`;
DROP PROCEDURE IF EXISTS `kinton`.`AccountingIPsRegisterEvents`;
DROP PROCEDURE IF EXISTS `kinton`.`UpdateAccounting`;
DROP PROCEDURE IF EXISTS `kinton`.`AccountingVLANRegisterEvents`;

DELIMITER |
-- 
-- AccountingVMRegisterEvents: Registers Events related to DEPLOY or UNDEPLOY virtualmachines for Accounting
-- Inserts new rows with startTime=NOW() for each new DEPLOY_VM event
-- Updates existing rows with stopTime=NOW() for each new UNDEPLOY_VM event
-- 
CREATE PROCEDURE `kinton`.AccountingVMRegisterEvents(
	IN idVirtualMachine INT(10) UNSIGNED, 
	IN idType INT(1) UNSIGNED, 
	IN oldState VARCHAR(50), 
	IN newState VARCHAR(50), 
	IN ramValue INT(7) unsigned,  
	IN cpuValue INT(10) unsigned,
	IN hdValue BIGINT(20) unsigned)
BEGIN
	IF idType = 1 AND (oldState != newState) AND (newState = "RUNNING") THEN
	-- Deploy Event Detected
		INSERT INTO accounting_event_vm (idVM,idEnterprise,idVirtualDataCenter,idVirtualApp,cpu,ram,hd,startTime,stopTime) 
		SELECT
			vm.idVM, vapp.idEnterprise, vapp.idVirtualDataCenter, n.idVirtualApp,  
			cpuValue,
			ramValue,			
			hdValue,
			now(),
			null
		  FROM nodevirtualimage nvi, node n, virtualapp vapp, virtualmachine vm
		WHERE vm.idVM = nvi.idVM
		AND nvi.idNode = n.idNode
		AND vapp.idVirtualApp = n.idVirtualApp
		AND vm.idVM = idVirtualMachine;
	END IF;
	--	
	IF idType = 1 AND newState = "NOT_DEPLOYED" THEN			
	-- Undeploy Event Detected
		UPDATE
		  accounting_event_vm
		SET
		  stopTime=now()
		WHERE
		  accounting_event_vm.idVM = idVirtualMachine
		  and
		  accounting_event_vm.stopTime is null;
	END IF;
END;
|
-- 
-- AccountingStorageRegisterEvents
--
-- Triggered when user creates, updates or deletes a volume in a VirtualDataCenter. All this events are stored in 'accounting_event_storage' with its timestamps.
-- This procedure performs different actions managed by 'action' parameter values:'CREATE_STORAGE','UPDATE_STORAGE','
-- 
CREATE PROCEDURE `kinton`.AccountingStorageRegisterEvents(
	IN action VARCHAR(15),
	IN idThisResource VARCHAR(50),
	IN thisResourceName VARCHAR(255),
	IN idThisVirtualDataCenter INT(10) UNSIGNED,
	IN idThisEnterprise INT(10) UNSIGNED,  
	IN sizeReserved BIGINT(20))
BEGIN	
	-- Storage Creation Event Detected (table rasd_management). Storage is converted to Bytes
	IF action = "CREATE_STORAGE" THEN
		INSERT INTO accounting_event_storage (idResource, resourceName, idVM,idEnterprise,idVirtualDataCenter,idVirtualApp,sizeReserved,startTime, stopTime)
		SELECT idThisResource, thisResourceName, null, idThisEnterprise, idThisVirtualDataCenter, null, sizeReserved * 1048576, now(), null; 
	END IF;
	-- Storage Delete Event Detected (table rasd_management)
	IF action = "DELETE_STORAGE" THEN	
		UPDATE
		  accounting_event_storage
		SET
		  stopTime=now()
		WHERE
		  accounting_event_storage.idResource = idThisResource
		  AND
		  accounting_event_storage.stopTime is null;
	END IF;
	-- Storage Update Event Detected: update and insert a new one (table rasd)
	IF action = "UPDATE_STORAGE" THEN	
		UPDATE
		  accounting_event_storage
		SET
		  stopTime=now()
		WHERE
		  accounting_event_storage.idResource = idThisResource
		  AND
		  accounting_event_storage.stopTime is null;
		INSERT INTO accounting_event_storage (idResource, resourceName, idVM,idEnterprise,idVirtualDataCenter,idVirtualApp,sizeReserved,startTime, stopTime)
			SELECT idThisResource,thisResourceName, null, idThisEnterprise, idThisVirtualDataCenter, null, sizeReserved * 1048576, now(), null; 
	END IF;
END;
|
-- 
-- AccountingIPsRegisterEvents
--
-- Triggered when user creates, updates or deletes a volume in a VirtualDataCenter. All this events are stored in 'accounting_event_storage' with its timestamps.
-- This procedure performs different actions managed by 'action' parameter values:'CREATE_STORAGE','UPDATE_STORAGE','
-- 
CREATE PROCEDURE `kinton`.AccountingIPsRegisterEvents(
	IN action VARCHAR(15),
	IN idManagement INT(10) UNSIGNED,
	IN ipAddress VARCHAR(20),
	IN idThisVirtualDataCenter INT(10) UNSIGNED,
	IN idThisEnterprise INT(10) UNSIGNED)
BEGIN	
	--	
	-- IP Reserved Event Detected (table ip_pool_management)
	IF action = "IP_RESERVED" THEN
		INSERT INTO accounting_event_ips (idManagement,idEnterprise,idVirtualDataCenter,ip,startTime,stopTime)
		SELECT idManagement, idThisEnterprise, idThisVirtualDataCenter, ipAddress, now(), null; 
	END IF;
	-- IP Freed Event Detected (table rasd_management)
	IF action = "IP_FREED" THEN	
		UPDATE
		  accounting_event_ips
		SET
		  stopTime=now()
		WHERE
		  accounting_event_ips.idManagement = idManagement
		  AND
		  accounting_event_ips.stopTime is null;
	END IF;	
END;
|
-- 
-- AccountingVLANRegisterEvents
--
-- Triggered when user creates or deletes a VLAN in a VirtualDataCenter. All this events are stored in 'accounting_event_vlan' with its timestamps.
-- This procedure performs different actions managed by 'action' parameter values:'CREATE_VLAN','DELETE_VLAN','
-- 
CREATE PROCEDURE `kinton`.AccountingVLANRegisterEvents(
	IN action VARCHAR(15),
	IN vlan_network_id INT(11) UNSIGNED,
  	IN network_name VARCHAR(40),
	IN idThisVirtualDataCenter INT(10) UNSIGNED,
	IN idThisEnterprise INT(10) UNSIGNED)
BEGIN	
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('PROCEDURE AccountingVLANRegisterEvents Activated: ',IFNULL(vlan_network_id,'NULL'),'-',IFNULL(network_name,'NULL'),'-',IFNULL(idThisVirtualDataCenter,'NULL'),'-',idThisEnterprise,'-',action,'-',now()));	
	--	
	-- VLAN Created Event Detected
	IF action = "CREATE_VLAN" THEN
		INSERT INTO accounting_event_vlan (vlan_network_id,idEnterprise,idVirtualDataCenter,network_name,startTime,stopTime) 
		SELECT vlan_network_id, idThisEnterprise, idThisVirtualDataCenter, network_name, now(), null; 
	END IF;
	-- VLAN Deleted Event Detected
	IF action = "DELETE_VLAN" THEN	
		UPDATE
		  accounting_event_vlan
		SET
		  stopTime=now()
		WHERE
		  accounting_event_vlan.vlan_network_id = vlan_network_id
		  AND
		  accounting_event_vlan.stopTime is null;
	END IF;	
END;
|
-- 
-- UpdateAccounting
--
-- Inserts rows at accounting_event_detail based on Views defined for VMs, Storage and IPs events
-- 
CREATE PROCEDURE `kinton`.`UpdateAccounting`()
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
-- For VM Resources Accounting
INSERT INTO accounting_event_detail(
  `startTime`,
  `endTime`, 
  `idAccountingResourceType`,
  `resourceType`,
  `resourceUnits`,
  `resourceName`, 
  `idEnterprise`, 
  `idVirtualDataCenter`, 
  `idVirtualApp`, 
  `idVirtualMachine`, 
  `enterpriseName`, 
  `virtualDataCenter`, 
  `virtualApp`, 
  `virtualMachine`)
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      1,
      'VirtualMachine-vcpu',
      T.cpu,
      T.`VIRTUAL_MACHINE`,
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      T.`idVirtualApp`,
      T.`idVM`,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      T.`VIRTUAL_APP`,
      T.`VIRTUAL_MACHINE`
FROM `LAST_HOUR_USAGE_VM_VW` T
UNION ALL
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      2,
      'VirtualMachine-vram',
      T.`ram`,
      T.`VIRTUAL_MACHINE`,
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      T.`idVirtualApp`,
      T.`idVM`,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      T.`VIRTUAL_APP`,
      T.`VIRTUAL_MACHINE`
FROM `LAST_HOUR_USAGE_VM_VW` T
UNION ALL
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      3,
      'VirtualMachine-vhd',
      T.`hd`,
      T.`VIRTUAL_MACHINE`,
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      T.`idVirtualApp`,
      T.`idVM`,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      T.`VIRTUAL_APP`,
      T.`VIRTUAL_MACHINE`
FROM `LAST_HOUR_USAGE_VM_VW` T
-- Storage
UNION ALL
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      4,
      'ExternalStorage',
      T.`sizeReserved`,
      CONCAT(IF (T.`resourceName` IS NULL, '', T.`resourceName`), ' - ', T.`idResource`),
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      '',
      NULL, -- T.`idVM`,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      '',
      ''      
FROM `LAST_HOUR_USAGE_STORAGE_VW` T
-- IPs
UNION ALL
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      5,
      'IPAddress',
      1,
      T.`ip`,
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      '',
      NULL, -- idVM,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      '',
      ''      
FROM `LAST_HOUR_USAGE_IPS_VW` T
-- VLANs
UNION ALL
SELECT DISTINCT
      T.`ROUNDED_HOUR`,
      from_unixtime(3600 + unix_timestamp(T.`ROUNDED_HOUR`)),
      6,
      'VLAN',
      1,
      T.`networkName`,
      T.`idEnterprise`,
      T.`idVirtualDataCenter`,
      '',
      NULL, -- idVM,
      T.`VIRTUAL_ENTERPRISE`,
      T.`VIRTUAL_DATACENTER`,
      '',
      ''      
FROM `LAST_HOUR_USAGE_VLAN_VW` T;
END;
|
-- 
-- DeleteOldRegisteredEvents
--
-- Auxiliar procedure to delete old rows from event registering tables
-- All events registered older than 'hours' parameter from now will be deleted
-- 
DROP PROCEDURE IF EXISTS `kinton`.DeleteOldRegisteredEvents;
CREATE PROCEDURE `kinton`.DeleteOldRegisteredEvents(	
IN hours INT(2) UNSIGNED)
BEGIN	
	DELETE FROM accounting_event_vm  WHERE stopTime < date_sub(NOW(), INTERVAL hours HOUR);
	DELETE FROM accounting_event_storage  WHERE stopTime < date_sub(NOW(), INTERVAL hours HOUR);
	DELETE FROM accounting_event_ips  WHERE stopTime < date_sub(NOW(), INTERVAL hours HOUR);
	DELETE FROM accounting_event_vlan  WHERE stopTime < date_sub(NOW(), INTERVAL hours HOUR);
END;
|
DELIMITER ;
