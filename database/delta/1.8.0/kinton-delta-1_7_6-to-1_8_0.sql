
-- [ABICLOUDPREMIUM-1598]
ALTER TABLE `kinton`.`enterprise` ADD `isReservationRestricted` tinyint(1) DEFAULT 0

-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update volume_management set state = 1 where state = 2;

-- [ABICLOUDPREMIUM-1616]
ALTER TABLE kinton.virtualimage ADD cost_code VARCHAR(50);

UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_START" WHERE actionperformed="STATEFUL_PROCESS_START";
UPDATE kinton.metering SET actionperformed="PERSISTENT_RAW_FINISHED" WHERE actionperformed="STATEFUL_RAW_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_VOLUME_CREATED" WHERE actionperformed="STATEFUL_VOLUME_CREATED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_ENQUEUED" WHERE actionperformed="STATEFUL_DUMP_ENQUEUED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_FINISHED" WHERE actionperformed="STATEFUL_DUMP_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FINISHED" WHERE actionperformed="STATEFUL_PROCESS_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FAILED" WHERE actionperformed="STATEFUL_PROCESS_FAILED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_INITIATOR_ADDED" WHERE actionperformed="STATEFUL_INITIATOR_ADDED";

-- [ABICLOUDPREMIUM 1615]  Accounting changes --

DROP TABLE IF EXISTS `kinton`.`accounting_event_vm`;
CREATE TABLE `kinton`.`accounting_event_vm` (
  `idVMAccountingEvent` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `idVM` INTEGER(10) UNSIGNED NOT NULL,
  `idEnterprise` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualDataCenter` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualApp` INTEGER(10) UNSIGNED NOT NULL,
  `cpu` INTEGER(10) UNSIGNED NOT NULL,
  `ram` INTEGER(10) UNSIGNED NOT NULL,
  `hd` BIGINT(20) UNSIGNED NOT NULL,
  `startTime` TIMESTAMP NULL,
  `stopTime` TIMESTAMP NULL,
  `consolidated` BOOLEAN NOT NULL default 0,
  `costCode` VARCHAR(50) DEFAULT NULL,
  `version_c` int(11) DEFAULT '0',
   PRIMARY KEY (`idVMAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `kinton`.`accounting_event_detail`;
CREATE TABLE `kinton`.`accounting_event_detail` (
  `idAccountingEvent` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `startTime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `endTime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  `idAccountingResourceType` TINYINT(4) NOT NULL COMMENT '1 - VirtualMachine-vcpu; 2 - VirtualMachine-vram; 3 - VirtualMachine-vhd; 4 - ExternalStorage; 5 - IPAddress;', 
  `resourceType` VARCHAR(255)  NOT NULL,
  `resourceUnits` BIGINT(20) NOT NULL,
  `resourceName` VARCHAR(511)  NOT NULL,
  `idEnterprise` INTEGER(11) UNSIGNED NOT NULL,
  `idVirtualDataCenter` INTEGER(11) UNSIGNED NOT NULL,
  `idVirtualApp` INTEGER(11) UNSIGNED,
  `idVirtualMachine` INTEGER(11) UNSIGNED,
  `enterpriseName` VARCHAR(255)  NOT NULL,
  `virtualDataCenter` VARCHAR(255)  NOT NULL,
  `virtualApp` VARCHAR(255) ,
  `virtualMachine` VARCHAR(255) ,
  `costCode` VARCHAR(50) DEFAULT NULL,
  `version_c` int(11) DEFAULT '0',
  PRIMARY KEY (`idAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

DROP PROCEDURE IF EXISTS `kinton`.`AccountingVMRegisterEvents`;
CREATE PROCEDURE `kinton`.AccountingVMRegisterEvents(
    IN idVirtualMachine INT(10) UNSIGNED, 
    IN idType INT(1) UNSIGNED, 
    IN oldState VARCHAR(50), 
    IN newState VARCHAR(50), 
    IN ramValue INT(7) unsigned,  
    IN cpuValue INT(10) unsigned,
    IN hdValue BIGINT(20) unsigned,
    IN costCode VARCHAR(50))
BEGIN
    IF idType = 1 AND (oldState != newState) AND (newState = "RUNNING") THEN
    -- Deploy Event Detected
    
    
        INSERT INTO accounting_event_vm (idVM,idEnterprise,idVirtualDataCenter,idVirtualApp,cpu,ram,hd,startTime,stopTime,costCode) 
        SELECT
            vm.idVM, vapp.idEnterprise, vapp.idVirtualDataCenter, n.idVirtualApp,
            cpuValue,
            ramValue,           
            hdValue,
            now(),
            null,
            costCode
          FROM nodevirtualimage nvi, node n, virtualapp vapp, virtualmachine vm
        WHERE vm.idVM = nvi.idVM
        AND nvi.idNode = n.idNode
        AND vapp.idVirtualApp = n.idVirtualApp
        AND vm.idVM = idVirtualMachine;
    END IF;
    --  
    IF idType = 1 AND (newState = "NOT_DEPLOYED" OR newState = "UNKNOWN" OR (newState = "CRASHED" AND oldState != "UNKNOWN")) THEN          
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


DROP PROCEDURE IF EXISTS `kinton`.`UpdateAccounting`;
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
  `virtualMachine`,
  `costCode`)
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
      T.`VIRTUAL_MACHINE`,
      ''
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
      T.`VIRTUAL_MACHINE`,
      ''
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
      T.`VIRTUAL_MACHINE`,
      T.`costCode`
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
      '',
      ''
FROM `LAST_HOUR_USAGE_VLAN_VW` T;
END;


DROP VIEW IF EXISTS LAST_HOUR_USAGE_VM_VW;
-- VIEW to calculate Event_Detail for VM Accounting
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `LAST_HOUR_USAGE_VM_VW` AS 
  select 
    `accounting_event_vm`.idVMAccountingEvent AS idVMAccountingEvent,
    `accounting_event_vm`.idVM AS idVM,
    `accounting_event_vm`.idEnterprise AS idEnterprise,
    `accounting_event_vm`.idVirtualDataCenter AS idVirtualDataCenter,
    `accounting_event_vm`.idVirtualApp AS idVirtualApp,
    `accounting_event_vm`.cpu AS cpu,
    `accounting_event_vm`.ram AS ram,
    `accounting_event_vm`.hd AS hd,
    `accounting_event_vm`.startTime AS startTime,
    `accounting_event_vm`.stopTime AS stopTime,
    `accounting_event_vm`.costCode AS costCode,
    (unix_timestamp(`accounting_event_vm`.stopTime) - unix_timestamp(`accounting_event_vm`.startTime)) AS `DELTA_TIME`,
    from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,
    CONCAT(IF (`virtualmachine`.`description` IS NULL, '', `virtualmachine`.`description`),' - ', `virtualmachine`.`name`) AS `VIRTUAL_MACHINE`,
    `virtualapp`.`name` AS `VIRTUAL_APP`,
    `virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,
    `enterprise`.`name` AS `VIRTUAL_ENTERPRISE` 
  from 
    ((((`accounting_event_vm` join `virtualmachine` on((`accounting_event_vm`.idVM = `virtualmachine`.`idVM`))) join `virtualapp` on((`accounting_event_vm`.idVirtualApp = `virtualapp`.`idVirtualApp`))) join `virtualdatacenter` on((`accounting_event_vm`.idVirtualDataCenter = `virtualdatacenter`.`idVirtualDataCenter`))) join `enterprise` on((`accounting_event_vm`.idEnterprise = `enterprise`.`idEnterprise`))) 
  where 
    -- Machine is still ON
    ((`accounting_event_vm`.stopTime is null)
    -- Machine was ON for less than 60 seconds
    or ((`accounting_event_vm`.stopTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_vm`.stopTime) - unix_timestamp(`accounting_event_vm`.startTime)) > 3600)) or ((`accounting_event_vm`.startTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_vm`.stopTime) - unix_timestamp(`accounting_event_vm`.startTime)) <= 3600)));




DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;

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
        IF NEW.state = "RUNNING" THEN   
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
    ELSEIF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "RUNNING" THEN 
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
            ELSEIF OLD.state = "RUNNING" THEN           
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
            IF OLD.state = "NOT_DEPLOYED" OR OLD.state = "UNKNOWN"  THEN -- OR OLD.idType != NEW.idType
                -- VMachine Deployed or VMachine imported
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = "NOT_DEPLOYED" OR NEW.state = "CRASHED" OR (NEW.state = "UNKNOWN" AND OLD.state != "CRASHED") THEN 
                -- VMachine Undeployed
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
            CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
        END IF;              
    END IF;
    END;

-- [ABICLOUDPREMIUM-1476] Changes to fit the LDAP integration.
alter table kinton.user modify user varchar(128) NOT NULL;
alter table kinton.user add authType varchar(20) NOT NULL;
alter table kinton.user modify column password varchar(32);
update kinton.user set authtype = 'ABIQUO';
