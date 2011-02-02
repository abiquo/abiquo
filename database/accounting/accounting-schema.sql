DROP TABLE IF EXISTS `kinton`.`accounting_event_vm`;
DROP TABLE IF EXISTS `kinton`.`accounting_event_storage`;
DROP TABLE IF EXISTS `kinton`.`accounting_event_ips`;
DROP TABLE IF EXISTS `kinton`.`accounting_event_vlan`;
DROP TABLE IF EXISTS `kinton`.`accounting_event_detail`;

-- Events for VM
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
   PRIMARY KEY (`idVMAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

-- Events for Storage
CREATE TABLE `kinton`.`accounting_event_storage` (
  `idStorageAccountingEvent` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `idResource` VARCHAR(50) DEFAULT NULL,
  `resourceName` VARCHAR(511) DEFAULT NULL,
  -- idManagement is necessary?
  `idVM` INTEGER(10) unsigned NULL,
  `idEnterprise` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualDataCenter` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualApp` INTEGER(10) UNSIGNED NULL,
  `sizeReserved` BIGINT UNSIGNED NOT NULL, -- SELECT limitResource INTO limitResourceObj FROM rasd r
  `startTime` TIMESTAMP NULL,
  `stopTime` TIMESTAMP NULL,
  `consolidated` BOOLEAN NOT NULL default 0,
   PRIMARY KEY (`idStorageAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

-- Events for IPs Reserved
CREATE TABLE `kinton`.`accounting_event_ips` (
  `idIPsAccountingEvent` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  -- `idResource` INTEGER(10) UNSIGNED NOT NULL, no idResource if IP is not assigned to a VM
  `idManagement`  INTEGER(10) UNSIGNED NOT NULL,
  -- `idVM` INTEGER(10) unsigned NOT NULL,
  `idEnterprise` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualDataCenter` INTEGER(10) UNSIGNED NOT NULL,
  -- `idVirtualApp` INTEGER(10) UNSIGNED NULL,
  `ip` VARCHAR(20) NOT NULL,
  `startTime` TIMESTAMP NULL,
  `stopTime` TIMESTAMP NULL,
  `consolidated` BOOLEAN NOT NULL default 0,
   PRIMARY KEY (`idIPsAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 ;

-- Events for VLANs created
CREATE TABLE `kinton`.`accounting_event_vlan` (
  `idVLANAccountingEvent` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `vlan_network_id` INT(11) UNSIGNED NOT NULL,
  -- `network_id` INT(11) UNSIGNED NOT NULL,
  `idEnterprise` INTEGER(10) UNSIGNED NOT NULL,
  `idVirtualDataCenter` INTEGER(10) UNSIGNED NOT NULL,
  `network_name` VARCHAR(40) NOT NULL,
  `startTime` TIMESTAMP NULL,
  `stopTime` TIMESTAMP NULL,
  `consolidated` BOOLEAN NOT NULL default 0,
   PRIMARY KEY (`idVLANAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8 ;

-- Consolidated data
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
  PRIMARY KEY (`idAccountingEvent`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;


DROP VIEW IF EXISTS LAST_HOUR_USAGE_VM_VW;
DROP VIEW IF EXISTS LAST_HOUR_USAGE_STORAGE_VW;
DROP VIEW IF EXISTS LAST_HOUR_USAGE_IPS_VW;
DROP VIEW IF EXISTS LAST_HOUR_USAGE_VLAN_VW;

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

-- VIEW to calculate Event_Detail for Storage Accounting
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `LAST_HOUR_USAGE_STORAGE_VW` AS 
select 
    `accounting_event_storage`.idStorageAccountingEvent AS idStorageAccountingEvent,
    `accounting_event_storage`.idVM AS idVM,
    `accounting_event_storage`.idEnterprise AS idEnterprise,
    `accounting_event_storage`.idVirtualDataCenter AS idVirtualDataCenter,
    `accounting_event_storage`.idVirtualApp AS idVirtualApp,
    `accounting_event_storage`.idResource AS idResource,
    `accounting_event_storage`.resourceName AS resourceName,
    `accounting_event_storage`.sizeReserved AS sizeReserved,    
    `accounting_event_storage`.startTime AS startTime,
    `accounting_event_storage`.stopTime AS stopTime,
    (unix_timestamp(`accounting_event_storage`.stopTime) - unix_timestamp(`accounting_event_storage`.startTime)) AS `DELTA_TIME`,
    from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,
	-- `virtualmachine`.`name` AS `VIRTUAL_MACHINE`,
	-- `virtualapp`.`name` AS `VIRTUAL_APP`,
    `virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,
    `enterprise`.`name` AS `VIRTUAL_ENTERPRISE` 
  from 
    (((`accounting_event_storage` join `virtualdatacenter` on(`accounting_event_storage`.idVirtualDataCenter = `virtualdatacenter`.`idVirtualDataCenter`))
    join `enterprise` on(`accounting_event_storage`.idEnterprise = `enterprise`.`idEnterprise`))) 
  where 
  	-- Storage volume is still ON
    ((`accounting_event_storage`.stopTime is null)
    -- Storage volume was ON for less than 60 seconds
    or ((`accounting_event_storage`.stopTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_storage`.stopTime) - unix_timestamp(`accounting_event_storage`.startTime)) > 3600)) or ((`accounting_event_storage`.startTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_storage`.stopTime) - unix_timestamp(`accounting_event_storage`.startTime)) <= 3600)));

-- VIEW to calculate Event_Detail for IPs Accounting
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `LAST_HOUR_USAGE_IPS_VW` AS 
select 
    `accounting_event_ips`.idIPsAccountingEvent AS idIPsAccountingEvent,
    `accounting_event_ips`.idEnterprise AS idEnterprise,
    `accounting_event_ips`.idVirtualDataCenter AS idVirtualDataCenter,
    `accounting_event_ips`.ip AS ip,    
    `accounting_event_ips`.startTime AS startTime,
    `accounting_event_ips`.stopTime AS stopTime,
    (unix_timestamp(`accounting_event_ips`.stopTime) - unix_timestamp(`accounting_event_ips`.startTime)) AS `DELTA_TIME`,
    from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,
	-- `virtualmachine`.`name` AS `VIRTUAL_MACHINE`,
	-- `virtualapp`.`name` AS `VIRTUAL_APP`,
    `virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,
    `enterprise`.`name` AS `VIRTUAL_ENTERPRISE` 
  from 
    (((`accounting_event_ips` join `virtualdatacenter` on(`accounting_event_ips`.idVirtualDataCenter = `virtualdatacenter`.`idVirtualDataCenter`))
    join `enterprise` on(`accounting_event_ips`.idEnterprise = `enterprise`.`idEnterprise`))) 
  where 
  	-- IP is still Reserved
    ((`accounting_event_ips`.stopTime is null)
    -- IP was reserved for less than 60 seconds
    or ((`accounting_event_ips`.stopTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_ips`.stopTime) - unix_timestamp(`accounting_event_ips`.startTime)) > 3600)) or ((`accounting_event_ips`.startTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_ips`.stopTime) - unix_timestamp(`accounting_event_ips`.startTime)) <= 3600)));


    -- VIEW to calculate Event_Detail for VLAN Accounting
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `LAST_HOUR_USAGE_VLAN_VW` AS 
select 
    `accounting_event_vlan`.idVLANAccountingEvent AS idVLANAccountingEvent,
    `accounting_event_vlan`.idEnterprise AS idEnterprise,
    `accounting_event_vlan`.idVirtualDataCenter AS idVirtualDataCenter,
    `accounting_event_vlan`.network_name AS networkName,    
    `accounting_event_vlan`.startTime AS startTime,
    `accounting_event_vlan`.stopTime AS stopTime,
    (unix_timestamp(`accounting_event_vlan`.stopTime) - unix_timestamp(`accounting_event_vlan`.startTime)) AS `DELTA_TIME`,
    from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,
    `virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,
    `enterprise`.`name` AS `VIRTUAL_ENTERPRISE` 
  from 
    (((`accounting_event_vlan` join `virtualdatacenter` on(`accounting_event_vlan`.idVirtualDataCenter = `virtualdatacenter`.`idVirtualDataCenter`))
    join `enterprise` on(`accounting_event_vlan`.idEnterprise = `enterprise`.`idEnterprise`))) 
  where 
  	-- IP is still Reserved
    ((`accounting_event_vlan`.stopTime is null)
    -- IP was reserved for less than 60 seconds
    or ((`accounting_event_vlan`.stopTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_vlan`.stopTime) - unix_timestamp(`accounting_event_vlan`.startTime)) > 3600)) or ((`accounting_event_vlan`.startTime > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`accounting_event_vlan`.stopTime) - unix_timestamp(`accounting_event_vlan`.startTime)) <= 3600)));
    
-- Only for debugging at development stage
-- DROP  TABLE IF EXISTS `kinton`.`debug_msg`;
-- CREATE TABLE `kinton`.`debug_msg` (
--   `msg` varchar(255) NOT NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Trigger Activated: ',idVirtualMachine,'-',idType,'-',oldState,'-',newState,'-', ramValue,'-',cpuValue,'-',hdValue));



