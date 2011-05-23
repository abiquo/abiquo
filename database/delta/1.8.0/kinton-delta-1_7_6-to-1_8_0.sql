--
-- Drop table `kinton`.`auth_clientresource`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource`;

--
-- Drop table `kinton`.`auth_clientresource_exception`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource_exception`;



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

UPDATE  `kinton`.`role`  Set name ='SYS_ADMIN', blocked=1 where idRole=1;
UPDATE  `kinton`.`role`  Set name ='USER' where idRole=2;
UPDATE  `kinton`.`role`  Set name ='ENTERPRISE_ADMIN'where idRole=3;

--
-- Definition of table `kinton`.`privilege`
--

CREATE TABLE `privilege` (
  `idPrivilege` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPrivilege`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
LOCK TABLES `privilege` WRITE;
INSERT INTO `privilege` VALUES
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
 (44,'EVENTLOG_VIEW_ALL',0)
 (45,'APPLIB_VM_COST_CODE',0),
 (46,'USERS_MANAGE_ENTERPRISE_BRANDING',0);
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
 (1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),
 (2,3,0),(2,12,0),(2,13,0),(2,14,0),(2,15,0),(2,16,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,24,0),(2,25,0),(2,26,0),(2,27,0),(2,28,0),(2,29,0),(2,30,0),(2,32,0),(2,34,0),
 (2,43,0),(3,12,0),(3,14,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `roles_privileges` ENABLE KEYS */;


--
-- Definition of table `kinton`.`role_ldap`
--

CREATE  TABLE `kinton`.`role_ldap` (
  `idRole_ldap` INT(3) NOT NULL AUTO_INCREMENT ,
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `role_ldap` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idRole_ldap`) ,
  KEY `fk_role_ldap_role` (`idRole`) ,
  CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole` ) REFERENCES `kinton`.`role` (`idRole` ) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
 ("client.wiki.config.registration","http://community.abiquo.com/display/ABI17/Configuration+view#Configurationview-ProductRegistration","Registration wiki");
UNLOCK TABLES;
/*!40000 ALTER TABLE `system_properties` ENABLE KEYS */;
