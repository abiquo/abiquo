-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers 
-- 5th -> SQL Procedures

-- [ABICLOUDPREMIUM-1502]
-- Fix int precision
ALTER TABLE `kinton`.`vappstateful_conversions` MODIFY COLUMN `idUser` int(10) unsigned NOT NULL;

-- [ABICLOUDPREMIUM-1598]
ALTER TABLE `kinton`.`enterprise` ADD `isReservationRestricted` tinyint(1) DEFAULT 0;

-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update `kinton`.`volume_management` set state = 1 where state = 2;

-- [ABICLOUDPREMIUM-1616]
ALTER TABLE `kinton`.`virtualimage` ADD cost_code VARCHAR(50);

DELETE FROM `kinton`.`system_properties` WHERE name = 'client.infra.useVirtualBox';

-- [ABICLOUDPREMIUM-1476] Changes to fit the LDAP integration.
alter table kinton.user modify user varchar(128) NOT NULL;
alter table kinton.user add authType varchar(20) NOT NULL;
alter table kinton.user modify column password varchar(32);
alter table kinton.user modify column email varchar(200);
update kinton.user set authType = 'ABIQUO';
alter table kinton.session modify user varchar(128) NOT NULL;
alter table kinton.user modify name varchar(128) NOT NULL;
alter table kinton.metering modify user varchar(128) NOT NULL;
alter table kinton.session add authType varchar(20) NOT NULL;

--
-- Datastore rootPath longer
--

alter table kinton.datastore modify rootPath varchar(42) NOT NULL;

--
-- [ABICLOUDPREMIUM 1615]  Accounting changes --
--

alter table kinton.accounting_event_vm add costCode varchar(50) DEFAULT NULL;
alter table kinton.accounting_event_detail add costCode varchar(50) DEFAULT NULL;

--
-- Drop table `kinton`.`auth_clientresource_exception`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource_exception`;

--
-- Drop table `kinton`.`auth_clientresource`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource`;


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
) ENGINE=InnoDB AUTO_INCREMENT=4  DEFAULT CHARSET=utf8;


UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_PROCESS_START" WHERE actionperformed="STATEFUL_PROCESS_START";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_RAW_FINISHED" WHERE actionperformed="STATEFUL_RAW_FINISHED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_VOLUME_CREATED" WHERE actionperformed="STATEFUL_VOLUME_CREATED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_DUMP_ENQUEUED" WHERE actionperformed="STATEFUL_DUMP_ENQUEUED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_DUMP_FINISHED" WHERE actionperformed="STATEFUL_DUMP_FINISHED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_PROCESS_FINISHED" WHERE actionperformed="STATEFUL_PROCESS_FINISHED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_PROCESS_FAILED" WHERE actionperformed="STATEFUL_PROCESS_FAILED";
UPDATE `kinton`.`metering` SET actionperformed="PERSISTENT_INITIATOR_ADDED" WHERE actionperformed="STATEFUL_INITIATOR_ADDED";


--
-- Definition of table `kinton`.`role`
--

ALTER TABLE `kinton`.`role` DROP COLUMN `securityLevel` , 
DROP COLUMN `largeDescription` , DROP COLUMN `shortDescription` , 
DROP COLUMN `type` , ADD COLUMN `name` VARCHAR(40) NOT NULL DEFAULT 'auto_name'  AFTER `version_c` , 
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

/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
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
 (47,'SYSCONFIG_SHOW_REPORTS',0),
 (48,'USERS_DEFINE_AS_MANAGER',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;

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

/*!40000 ALTER TABLE `roles_privileges` DISABLE KEYS */;
LOCK TABLES `roles_privileges` WRITE;
INSERT INTO `roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),
 (1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,48,0),
 (3,3,0),(3,12,0),(3,13,0),(3,14,0),(3,15,0),(3,16,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,24,0),(3,25,0),(3,26,0),(3,27,0),(3,28,0),(3,29,0),(3,30,0),(3,32,0),(3,34,0),(3,43,0),(3,48,0),
 (2,12,0),(2,14,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `roles_privileges` ENABLE KEYS */;

/*!40000 ALTER TABLE `roles_privileges` DISABLE KEYS */;
LOCK TABLES `roles_privileges` WRITE;
INSERT INTO `roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),
 (1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),
 (2,3,0),(2,12,0),(2,13,0),(2,14,0),(2,15,0),(2,16,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,24,0),(2,25,0),(2,26,0),(2,27,0),(2,28,0),(2,29,0),(2,30,0),(2,32,0),(2,34,0),
 (2,43,0),(3,12,0),(3,14,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `roles_privileges` ENABLE KEYS */;


--
-- System properties
--

/*!40000 ALTER TABLE `system_properties` DISABLE KEYS */;
LOCK TABLES `system_properties` WRITE;
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
/*!40000 ALTER TABLE `system_properties` ENABLE KEYS */;

-- Racks can be HA enabled
ALTER TABLE `kinton`.`rack` ADD COLUMN `haEnabled` boolean default false COMMENT 'TRUE - This rack is enabled for the HA functionality';

-- PhysicalMachine can have 2 new states
ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `idState` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - STOPPED
1 - NOT PROVISIONED
2 - NOT MANAGED
3 - MANAGED
4 - HALTED
5 - UNLICENSED
6 - HA_IN_PROGRESS
7 - DISABLED_FOR_HA';


-- Racks can be HA enabled
ALTER TABLE `kinton`.`datastore` ADD COLUMN `datastoreUuid` VARCHAR(255) default NULL COMMENT 'Datastore UUID set by Abiquo to identify shared datastores.';
ALTER TABLE `kinton`.`datastore` DROP COLUMN `shared`;

-- ipmi
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `ipmiIP` VARCHAR(39)  DEFAULT NULL AFTER `version_c`,
 ADD COLUMN `ipmiPort` INT(5) UNSIGNED DEFAULT NULL AFTER `ipmiIP`,
 ADD COLUMN `ipmiUser` VARCHAR(255)  DEFAULT NULL AFTER `ipmiPort`,
 ADD COLUMN `ipmiPassword` VARCHAR(255)  DEFAULT NULL AFTER `ipmiUser`;

-- Adding the foreign key over idEnterprise
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` 
  FOREIGN KEY (`idEnterprise`) 
  REFERENCES `kinton`.`enterprise` (`idEnterprise`) 
  ON DELETE SET NULL;

DROP PROCEDURE IF EXISTS `kinton`.`AccountingVMRegisterEvents`;
DROP PROCEDURE IF EXISTS `kinton`.`UpdateAccounting`;
DELIMITER |
CREATE PROCEDURE `kinton`.`AccountingVMRegisterEvents`(
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
|
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
      T.`costCode`
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
      T.`costCode`
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
|
DELIMITER ;

DROP VIEW IF EXISTS `kinton`.LAST_HOUR_USAGE_VM_VW;
-- VIEW to calculate Event_Detail for VM Accounting
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `kinton`.`LAST_HOUR_USAGE_VM_VW` AS 
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



-- DELTA 1.7.6 to 1.8.0
-- [ABICLOUDPREMIUM-1487] The stateful can be done to a pre-selected volume
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `idManagement` int(10) unsigned;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`);

DELETE FROM `kinton`.`system_properties` WHERE name = 'client.infra.useVirtualBox';

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `password` VARCHAR(32) DEFAULT NULL;

--
-- Definition of table `kinton`.`ucs_rack`
--
DROP TABLE IF EXISTS `kinton`.`ucs_rack`;
CREATE TABLE  `kinton`.`ucs_rack` (
  `idRack` int(15) unsigned NOT NULL,
  `ip` varchar(20) NOT NULL,
  `port` int(5) NOT NULL,
  `user_rack` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `version_c` int(11) DEFAULT '0',
  KEY `id_rack_FK` (`idRack`),
  CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_deleted`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;

DELIMITER |
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
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);      
       		 END IF;          
       	END IF;	 
    END IF;
    END;
|
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
CREATE TRIGGER `kinton`.`update_volume_management_update_stats` AFTER UPDATE ON `kinton`.`volume_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE incr INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
        SET incr = NEW.usedSize-OLD.usedSize;
        SELECT sd.idDataCenter INTO idDataCenterObj
        FROM storage_pool sp, storage_device sd
        WHERE OLD.idStorage = sp.idStorage
        AND sp.idStorageDevice = sd.id;
        
        SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM rasd_management rasd, virtualapp vapp
        WHERE OLD.idManagement = rasd.idManagement
        AND rasd.idVirtualApp = vapp.idVirtualApp;
        
        SELECT vdc.idEnterprise INTO idEnterpriseObj
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = idVirtualDataCenterObj;
        
        SELECT r.limitResource INTO reservedSize
        FROM rasd_management rm, rasd r
        WHERE rm.idManagement = NEW.idManagement
        AND r.instanceID = rm.idResource;
        
        IF NEW.state != OLD.state THEN
            IF NEW.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed+reservedSize WHERE idDataCenter = idDataCenterObj;
    UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached + 1, extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;     
            IF OLD.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
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
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
	DECLARE currentIdManagement INTEGER DEFAULT -1;
	DECLARE currentDataCenter INTEGER DEFAULT -1;
	DECLARE currentIpAddress VARCHAR(20) DEFAULT '';
	DECLARE no_more_ipsfreed INT;
	DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement	
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.dhcp_service_id=nc.dhcp_service_id
           AND vn.network_configuration_id = nc.network_configuration_id
           AND vn.network_id = dc.network_id
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
--		INSERT INTO debug_msg (msg) VALUES (CONCAT('IP_FREED: ',currentIpAddress, ' - idManagement: ', currentIdManagement, ' - OLD.idVirtualDataCenter: ', OLD.idVirtualDataCenter, ' - idEnterpriseObj: ', OLD.idEnterprise));
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
