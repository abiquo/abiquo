DELETE FROM `kinton`.`system_properties` WHERE name = 'client.infra.useVirtualBox';
-- [ABICLOUDPREMIUM-1476] Changes to fit the LDAP integration.
alter table kinton.user modify user varchar(128) NOT NULL;
alter table kinton.user add authType varchar(20) NOT NULL;
alter table kinton.user modify column password varchar(32);
update kinton.user set authtype = 'ABIQUO';
alter table kinton.session modify user varchar(128) NOT NULL;
alter table kinton.user modify name varchar(128) NOT NULL;
alter table kinton.metering modify user varchar(128) NOT NULL;
alter table kinton.session add authType varchar(20) NOT NULL;
--
-- Drop table `kinton`.`auth_clientresource_exception`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource_exception`;

--
-- Drop table `kinton`.`auth_clientresource`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource`;

--
-- Definition of table `kinton`.`role`
--

ALTER TABLE `kinton`.`role` DROP COLUMN `securityLevel` , 
DROP COLUMN `largeDescription` , DROP COLUMN `shortDescription` , 
DROP COLUMN `type` , ADD COLUMN `name` VARCHAR(40) NOT NULL  AFTER `version_c` , 
ADD COLUMN `idEnterprise` INT(10) UNSIGNED NULL DEFAULT NULL  AFTER `name` ,
ADD COLUMN `blocked` TINYINT(1)  NOT NULL DEFAULT 0  AFTER `idEnterprise` , 
  ADD CONSTRAINT `fk_role_enterprise`
  FOREIGN KEY (`idEnterprise` )
  REFERENCES `kinton`.`enterprise` (`idEnterprise` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_role_enterprise` (`idEnterprise` ASC) ;

UPDATE  `kinton`.`role`  Set name ='CLOUD_ADMIN', blocked=1 where idRole=1;
UPDATE  `kinton`.`role`  Set name ='USER' where idRole=2;
UPDATE  `kinton`.`role`  Set name ='ENTERPRISE_ADMIN'where idRole=3;

--
-- Definition of table `kinton`.`privilege`
--

CREATE TABLE `kinton`.`privilege` (
  `idPrivilege` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPrivilege`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (1,'ENTERPRISE_ENUMERATE',0),
 (2,'ENTERPRISE_ADMINISTER_ALL',0),
 (3,'ENTERPRISE_RESOURCE_SUMMARY_ENT',0),
 (4,'PHYS_DC_ENUMERATE',0),
 (5,'PHYS_DC_RETRIEVE_RESOURCE_USAGE',0),
 (6,'PHYS_DC_MANAGE',0),
 (7,'PHYS_DC_RETRIEVE_DETAILS',0),
 (8,'PHYS_DC_ALLOW_MODIFY_SERVERS',0),
 (9,'PHYS_DC_ALLOW_MODIFY_NETWORK',0),
 (10,'PHYS_DC_ALLOW_MODIFY_STORAGE',0),
 (11,'PHYS_DC_ALLOW_MODIFY_ALLOCATION',0),
 (12,'VDC_ENUMERATE',0),
 (13,'VDC_MANAGE',0),
 (14,'VDC_MANAGE_VAPP',0),
 (15,'VDC_MANAGE_NETWORK',0),
 (16,'VDC_MANAGE_STORAGE',0),
 (17,'VAPP_CUSTOMISE_SETTINGS',0),
 (18,'VAPP_DEPLOY_UNDEPLOY',0),
 (19,'VAPP_ASSIGN_NETWORK',0),
 (20,'VAPP_ASSIGN_VOLUME',0),
 (21,'VAPP_PERFORM_ACTIONS',0),
 (22,'VAPP_CREATE_STATEFUL',0),
 (23,'VAPP_CREATE_INSTANCE',0),
 (24,'APPLIB_VIEW',0),
 (25,'APPLIB_ALLOW_MODIFY',0),
 (26,'APPLIB_UPLOAD_IMAGE',0),
 (27,'APPLIB_MANAGE_REPOSITORY',0),
 (28,'APPLIB_DOWNLOAD_IMAGE',0),
 (29,'APPLIB_MANAGE_CATEGORIES',0),
 (30,'USERS_VIEW',0),
 (31,'USERS_MANAGE_ENTERPRISE',0),
 (32,'USERS_MANAGE_USERS',0),
 (33,'USERS_MANAGE_OTHER_ENTERPRISES',0),
 (34,'USERS_PROHIBIT_VDC_RESTRICTION',0),
 (35,'USERS_VIEW_PRIVILEGES',0),
 (36,'USERS_MANAGE_ROLES',0),
 (37,'USERS_MANAGE_ROLES_OTHER_ENTERPRISES',0),
 (38,'USERS_MANAGE_SYSTEM_ROLES',0),
 (39,'USERS_MANAGE_LDAP_GROUP',0),
 (40,'USERS_ENUMERATE_CONNECTED',0),
 (41,'SYSCONFIG_VIEW',0),
 (42,'SYSCONFIG_ALLOW_MODIFY',0),
 (43,'EVENTLOG_VIEW_ENTERPRISE',0),
 (44,'EVENTLOG_VIEW_ALL',0),
 (45,'APPLIB_VM_COST_CODE',0),
 (46,'USERS_MANAGE_ENTERPRISE_BRANDING',0),
 (47,'SYSCONFIG_SHOW_REPORTS',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

--
-- Definition of table `kinton`.`roles_privileges`
--

CREATE  TABLE `kinton`.`roles_privileges` (
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `idPrivilege` INT(10) UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  INDEX `fk_roles_privileges_role` (`idRole` ASC) ,
  INDEX `fk_roles_privileges_privileges` (`idPrivilege` ASC) ,
  CONSTRAINT `fk_roles_privileges_role`
    FOREIGN KEY (`idRole` )
    REFERENCES `kinton`.`role` (`idRole` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roles_privileges_privileges`
    FOREIGN KEY (`idPrivilege` )
    REFERENCES `kinton`.`privilege` (`idPrivilege` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),
 (1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),
 (2,3,0),(2,12,0),(2,13,0),(2,14,0),(2,15,0),(2,16,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,24,0),(2,25,0),(2,26,0),(2,27,0),(2,28,0),(2,29,0),(2,30,0),(2,32,0),(2,34,0),
 (2,43,0),(3,12,0),(3,14,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;


--
-- Definition of table `kinton`.`role_ldap`
--
DROP TABLE IF EXISTS `kinton`.`role_ldap`;

CREATE  TABLE `kinton`.`role_ldap` (
  `idRole_ldap` INT(3) NOT NULL AUTO_INCREMENT ,
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `role_ldap` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idRole_ldap`) ,
  KEY `fk_role_ldap_role` (`idRole`) ,
  CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole` ) REFERENCES `kinton`.`role` (`idRole` ) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into kinton.role_ldap(idRole, role_ldap,  version_c) values ((select idRole from kinton.role where type = 'SYS_ADMIN'), 'LDAP_SYS_ADMIN', 0);
insert into kinton.role_ldap(idRole, role_ldap, version_c) values ((select idRole from kinton.role where type = 'USER'), 'LDAP_USER', 0);
insert into kinton.role_ldap(idRole, role_ldap, version_c) values ((select idRole from kinton.role where type = 'ENTERPRISE_ADMIN'), 'LDAP_ENTERPRISE_ADMIN', 0);

-- [ABICLOUDPREMIUM-1487] The stateful can be done to a pre-selected volume
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `idManagement` int(10) unsigned;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`);


DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;

DELIMITER |
=======
ALTER TABLE kinton.virtualimage ADD cost_code VARCHAR(50);

-- ABICLOUDPREMIUM 1615  Accounting changes --

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
        
        SELECT IF(vi.cost_code IS NULL, "", vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
         IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN     
      	  CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
        END IF;              
    END IF;
    END IF
    END;
        
|
CREATE TRIGGER `kinton`.`virtualdatacenter_updated` AFTER UPDATE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE vlanNetworkIdObj INTEGER;    
        	  DECLARE networkNameObj VARCHAR(40);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- INSERT INTO debug_msg (msg) VALUES (CONCAT('OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'),'NEW.networktypeID ', IFNULL(NEW.networktypeID,'NULL')));
            -- Checks for changes
            IF OLD.name != NEW.name THEN
                -- Name changed !!!
                UPDATE IGNORE vdc_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                -- Changes also in Vapp stats
                UPDATE IGNORE vapp_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualApp IN (SELECT idVirtualApp FROM virtualapp WHERE idVirtualDataCenter=NEW.idVirtualDataCenter);
            END IF; 
            UPDATE IGNORE vdc_enterprise_stats 
            SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                memoryReserved = memoryReserved - OLD.ramHard + NEW.ramHard,
                localStorageReserved = localStorageReserved - OLD.hdHard + NEW.hdHard,
                -- publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                extStorageReserved = extStorageReserved - OLD.storageHard + NEW.storageHard,
                vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
            WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;            
        END IF;
        IF OLD.networktypeID IS NOT NULL AND NEW.networktypeID IS NULL THEN
        -- Remove VlanUsed
	    BEGIN
		DECLARE done INTEGER DEFAULT 0;
		DECLARE cursorVlan CURSOR FOR SELECT DISTINCT vn.network_id, vn.network_name FROM vlan_network vn WHERE vn.network_id = OLD.networktypeID;
		DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
		    
		OPEN cursorVlan;
		    
		REPEAT
		   FETCH cursorVlan into vlanNetworkIdObj, networkNameObj;
		   IF NOT done THEN

		    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('VDC UPDATED -> OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'), 'Enterprise: ',IFNULL(OLD.idEnterprise,'NULL'),' VDC: ',IFNULL(OLD.idVirtualDataCenter,'NULL'),IFNULL(vlanNetworkIdObj,'NULL'),IFNULL(networkNameObj,'NULL')));
			IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
				CALL AccountingVLANRegisterEvents('DELETE_VLAN',vlanNetworkIdObj, networkNameObj, OLD.idVirtualDataCenter,OLD.idEnterprise);
			END IF;
			-- Statistics
			UPDATE IGNORE cloud_usage_stats
				SET     vlanUsed = vlanUsed - 1
				WHERE idDataCenter = -1;
			UPDATE IGNORE enterprise_resources_stats 
				SET     vlanUsed = vlanUsed - 1
				WHERE idEnterprise = OLD.idEnterprise;
			UPDATE IGNORE vdc_enterprise_stats 
				SET     vlanUsed = vlanUsed - 1
			    WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
		   END IF;    
		UNTIL done END REPEAT;
		CLOSE cursorVlan;
	    END;
        END IF;
    END;
|
DELIMITER ;

--
-- Datastore rootPath longer
--

alter table kinton.datastore modify rootPath varchar(42) NOT NULL;

--
-- System properties
--


/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.showHelp","1","Show (1) or hide (0) the help icon within the plateform"), 
 ("client.wiki.showDefaultHelp","0","Use (1) or not (0) the default help URL within the plateform"), 
 ("client.wiki.defaultURL","http://community.abiquo.com/display/ABI17/Abiquo+Documentation+Home","The default URL opened when not specific help URL is specified"),
 ("client.wiki.infra.createDatacenter","http://community.abiquo.com/display/ABI17/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter","datacenter creation wiki"), 
 ("client.wiki.infra.editDatacenter","http://community.abiquo.com/display/ABI17/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter","datacenter edition wiki"), 
 ("client.wiki.infra.editRemoteService","http://community.abiquo.com/display/ABI17/Managing+Datacenters#ManagingDatacenters-RemoteServices","remote service edition wiki"), 
 ("client.wiki.infra.createPhysicalMachine","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingPhysicalMachines","physical machine creation wiki"),
 ("client.wiki.infra.mailNotification","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-Sendingemailnotifications","mail notification wiki"),
 ("client.wiki.infra.addDatastore","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-Datastoresmanagement","Datastore manager wiki"),
 ("client.wiki.infra.createRack","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingRacks","rack creation wiki"),
 ("client.wiki.infra.createMultiplePhysicalMachine","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-Createmultiplesphysicalmachines.","multiple physical machine creation wiki"),
 ("client.wiki.network.publicVlan","http://community.abiquo.com/display/ABI17/Manage+Networking+Configuration#ManageNetworkingConfiguration-PublicVLANManagement","public vlan creation wiki"),
 ("client.wiki.storage.storageDevice","http://community.abiquo.com/display/ABI17/Manage+External+Storage+%281.7.5%29#ManageExternalStorage%281.7.5%29-StorageDevicemanagement","storage device creation wiki"),
 ("client.wiki.storage.storagePool","http://community.abiquo.com/display/ABI17/Manage+External+Storage+%281.7.5%29#ManageExternalStorage%281.7.5%29-StoragePoolmanagement","storage pool creation wiki"), 
 ("client.wiki.storage.tier","http://community.abiquo.com/display/ABI17/Manage+External+Storage+%281.7.5%29#ManageExternalStorage%281.7.5%29-TierManagement","tier edition wiki"),
 ("client.wiki.allocation.global","http://community.abiquo.com/display/ABI17/Manage+Allocation+Rules#ManageAllocationRules-Globalrulesmanagement","global rules wiki"),
 ("client.wiki.allocation.datacenter","http://community.abiquo.com/display/ABI17/Manage+Allocation+Rules#ManageAllocationRules-Datacenterrulesmanagement","datacenter rules wiki"),
 ("client.wiki.vdc.createVdc","http://community.abiquo.com/display/ABI17/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter","virtual datacenter creation wiki"),
 ("client.wiki.vdc.createVapp","http://community.abiquo.com/display/ABI17/Basic+operations#Basicoperations-CreatinganewVirtualAppliance","virtual app creation wiki"),
 ("client.wiki.vdc.createPrivateNetwork","http://community.abiquo.com/display/ABI17/Manage+Networks#ManageNetworks-PrivateIPaddresses","VDC private network creation wiki"),
 ("client.wiki.vdc.createPublicNetwork","http://community.abiquo.com/display/ABI17/Manage+Networks#ManageNetworks-PublicIPreservation","VDC public network creation wiki"),
 ("client.wiki.vdc.createVolume","http://community.abiquo.com/display/ABI17/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolume","VDC virtual volume creation wiki"),
 ("client.wiki.vm.editVirtualMachine","http://community.abiquo.com/display/ABI17/Configure+Virtual+Machines","Virtual Machine edition wiki"),
 ("client.wiki.vm.bundleVirtualMachine","http://community.abiquo.com/display/ABI17/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-Configure","Bundles VM wiki"),
 ("client.wiki.vm.createNetworkInterface","http://community.abiquo.com/display/ABI17/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatinganewNetworkInterface","Network Interface creation wiki"),
 ("client.wiki.vm.createInstance","http://community.abiquo.com/display/ABI17/Create+Virtual+Machine+instances","Virtual Machine instance creation wiki"),
 ("client.wiki.vm.createStateful","http://community.abiquo.com/display/ABI17/Create+Stateful+Virtual+Machines","Virtual Machine stateful creation wiki"),
 ("client.wiki.vm.captureVirtualMachine","http://community.abiquo.com/display/ABI17/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-Importaretrievedmachine.","Capture Virtual Machine wiki"),
 ("client.wiki.apps.uploadVM","http://community.abiquo.com/display/ABI17/Adding+virtual+images+into+the+repository#Addingvirtualimagesintotherepository-Uploadingfromourlocalfilesystem","Virtual Image upload wiki"),
 ("client.wiki.user.createEnterprise","http://community.abiquo.com/display/ABI17/Manage+Enterprises#ManageEnterprises-CreatingoreditinganEnterprise","Enterprise creation wiki"),
 ("client.wiki.user.dataCenterLimits","http://community.abiquo.com/display/ABI17/Manage+Enterprises#ManageEnterprises-Datacenters","Datacenter Limits wiki"),
 ("client.wiki.user.createUser","http://community.abiquo.com/display/ABI17/Manage+Users#ManageUsers-Creatingoreditinganuser","User creation wiki"),
 ("client.wiki.user.createRole","http://community.abiquo.com/display/ABI18/Manage+Roles","Role creation wiki"),
 ("client.wiki.config.general","http://community.abiquo.com/display/ABI17/Configuration+view","Configuration wiki"),
 ("client.wiki.config.heartbeat","http://community.abiquo.com/display/ABI17/Configuration+view#Configurationview-Heartbeating","Heartbeat configuration wiki"),
 ("client.wiki.config.licence","http://community.abiquo.com/display/ABI17/Configuration+view#Configurationview-Licensemanagement","Licence configuration wiki"),
 ("client.wiki.config.registration","http://community.abiquo.com/display/ABI17/Configuration+view#Configurationview-ProductRegistration","Registration wiki"),
 ("client.main.billingUrl","","URL displayed when the report header logo is clicked, if empty the report button will not be displayed");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

ALTER TABLE `kinton`.`enterprise` ADD `isReservationRestricted` tinyint(1) DEFAULT 0

-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update volume_management set state = 1 where state = 2;

-- [ABICLOUDPREMIUM-1933] Change the default value
UPDATE  `kinton`.`system_properties`  Set value ='1' where name='client.dashboard.showStartUpAlert';
