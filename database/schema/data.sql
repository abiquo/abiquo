/*!40000 ALTER TABLE `auth_group` DISABLE KEYS */;
LOCK TABLES `auth_group` WRITE;
INSERT INTO `auth_group` VALUES
 (1,'GENERIC', 'Generic'),
 (2,'MAIN','Flex client main menu group'),
 (3,'USER','Flex and server Users Management'),
 (4,'APPLIANCE_LIBRARY','Flex and server Appliance Library Management');
UNLOCK TABLES;
/*!40000 ALTER TABLE `auth_group` ENABLE KEYS */;

/*!40000 ALTER TABLE `auth_serverresource` DISABLE KEYS */;
LOCK TABLES `auth_serverresource` WRITE;
INSERT INTO `kinton`.`auth_serverresource` VALUES  (1,'LOGIN','Login Service',1,2),
 (2,'ENTERPRISE_GET_ALL_ENTERPRISES','Security to retrieve the whole list of enterprises',3,1),
 (3,'ENTERPRISE_GET_ENTERPRISES','Security to call method getEnterprises in UserCommand',3,3),
 (4,'USER_GETUSERS','Security to call method getUsers in UserCommand',3,3),
 (5,'USER_GET_ALL_USERS','Security to retrieve the whole list of users',3,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `auth_serverresource` ENABLE KEYS */;

/*!40000 ALTER TABLE `category` DISABLE KEYS */;
LOCK TABLES `category` WRITE;
INSERT INTO `kinton`.`category` VALUES  (1,'Others',0,1,0),
 (2,'Database servers',1,0, 0),
 (4,'Applications servers',1,0,0),
 (5,'Web servers',1,0,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;

/*!40000 ALTER TABLE `network` DISABLE KEYS */;
LOCK TABLES `network` WRITE;
INSERT INTO `kinton`.`network` VALUES  (1, "6cd20366-72e5-11df-8f9d-002564aeca80", 1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `network` ENABLE KEYS */;

/*!40000 ALTER TABLE `enterprise` DISABLE KEYS */;
LOCK TABLES `enterprise` WRITE;
INSERT INTO `kinton`.`enterprise` VALUES  (1,'Abiquo',0,0,0,0,0,0,0,0,0,0,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise` ENABLE KEYS */;


/*!40000 ALTER TABLE `enterprise_properties` DISABLE KEYS */;
LOCK TABLES `enterprise_properties` WRITE;
INSERT INTO `kinton`.`enterprise_properties` VALUES  (1,1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise_properties` ENABLE KEYS */;


/*!40000 ALTER TABLE `enterprise_properties_map` DISABLE KEYS */;
LOCK TABLES `enterprise_properties_map` WRITE;
INSERT INTO `kinton`.`enterprise_properties_map` VALUES  (1,'Support e-mail','support@abiquo.com');
UNLOCK TABLES;
/*!40000 ALTER TABLE `enterprise_properties_map` ENABLE KEYS */;

/*!40000 ALTER TABLE `role` DISABLE KEYS */;
LOCK TABLES `role` WRITE;
INSERT INTO `kinton`.`role` (idRole,name,blocked,version_c) VALUES (1,'CLOUD_ADMIN',1,0);
INSERT INTO `kinton`.`role` (idRole,name,version_c) VALUES (2,'USER',0);
INSERT INTO `kinton`.`role` (idRole,name,version_c) VALUES (3,'ENTERPRISE_ADMIN',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;

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
 (44,'EVENTLOG_VIEW_ALL',0),
 (45,'APPLIB_VM_COST_CODE',0),
 (46,'USERS_MANAGE_ENTERPRISE_BRANDING',0),
 (47,'SYSCONFIG_SHOW_REPORTS',0),
 (48,'USERS_DEFINE_AS_MANAGER',0),
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `roles_privileges` DISABLE KEYS */;
LOCK TABLES `roles_privileges` WRITE;
INSERT INTO `roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),(1,23,0),(1,24,0),(1,25,0),
 (1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,47,0),(1,48,0),(1,49,0),(1,50,0),
 (3,3,0),(3,12,0),(3,13,0),(3,14,0),(3,15,0),(3,16,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,24,0),(3,25,0),(3,26,0),(3,27,0),(3,28,0),(3,29,0),(3,30,0),(3,32,0),(3,34,0),(3,43,0),(3,48,0),
(2,12,0),(2,14,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `roles_privileges` ENABLE KEYS */;


/*!40000 ALTER TABLE `user` DISABLE KEYS */;
LOCK TABLES `user` WRITE;
INSERT INTO `kinton`.`user` VALUES  (1,1,1,'admin','Cloud','Administrator','Main administrator','','en_US','c69a39bd64ffb77ea7ee3369dce742f3',null,1, 'ABIQUO', NOW(), 0),
 (2,2,1,'user','Standard','User','Standard user','','en_US','c69a39bd64ffb77ea7ee3369dce742f3',null,1, 'ABIQUO',NOW(), 0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

LOCK TABLES `alerts` WRITE;
INSERT INTO alerts (id, type, value, tstamp) values ("1", "REGISTER", "LATER", date_sub(now(), INTERVAL 4 DAY)), ("2", "HEARTBEAT", "YES", date_sub(now(), INTERVAL 4 DAY));
UNLOCK TABLES;

/*!40000 ALTER TABLE `system_properties` DISABLE KEYS */;
LOCK TABLES `system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.applibrary.ovfpackagesDownloadingProgressUpdateInterval","10","Time interval in seconds"),
 ("client.applibrary.virtualimageUploadProgressUpdateInterval","10","Time interval in seconds"),
 ("client.dashboard.abiquoURL","http://www.abiquo.org","URL of Abiquo web page"),
 ("client.dashboard.allowUsersAccess","1","Allow (1) or deny (0) access to the \'Users\' section"),
 ("client.dashboard.showStartUpAlert","1","Set to 1 to show an Alert with the text found in Startup_Alert.txt file"),
 ("client.infra.googleMapsDefaultLatitude","41.3825","Google Maps will be centered by default at this longitude value"),
 ("client.infra.googleMapsDefaultLongitude","2.176944","Google Maps will be centered by default at this latitude value"),
 ("client.infra.googleMapsDefaultZoom","4","Google Maps will be centered by default with this zoom level value"),
 ("client.infra.googleMapskey","0","The map\'s Google key used in infrastructure section"),
 ("client.infra.googleMapsLadTimeOut","10","Time, in seconds, that applications waits Google Maps to load. After that, application considers that Google Maps service is temporarily unavailable, and is not used"),
 ("client.infra.InfrastructureUpdateInterval","30","Time interval in seconds"),
 ("client.infra.ucsManagerLink","/ucsm/ucsm.jnlp","URL to display UCS Manager Interface"),
 ("client.metering.meteringUpdateInterval","10","Time interval in seconds"),
 ("client.network.numberIpAdressesPerPage","25","Number entries that will appear when listing IP addresses in different parts of the application"),
 ("client.theme.defaultEnterpriseLogoPath","themes/abicloudDefault/logo.png","This is the path to the Enterprise logo used in the app"),
 ("client.user.numberEnterprisesPerPage","25","Number of enterprises per page that will appear in User Management"),
 ("client.user.numberUsersPerPage","25","Number of users per page that will appear in User Management"),
 ("client.virtual.allowVMRemoteAccess","1","Allow (1) or deny (0) virtual machine remote access"),
 ("client.virtual.virtualApplianceDeployingUpdateInterval","5","Time interval in seconds"),
 ("client.virtual.virtualAppliancesUpdateInterval","30","Time interval in seconds"),
 ("client.virtual.moreInfoAboutUploadLimitations","http://wiki.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem","URL of Abiquo virtual image upload limitations web page"),
 ("client.infra.vlanIdMin","2","Minimum value for vlan ID"),
 ("client.infra.vlanIdMax","4094","Maximum value for vlan ID"),
 ("client.dashboard.dashboardUpdateInterval","30","Time interval in seconds"),
 ("client.infra.defaultHypervisorPassword","temporal","Default Hypervisor password used when creating Physical Machines"),
 ("client.infra.defaultHypervisorPort","8889","Default Hypervisor port used when creating Physical Machines"),
 ("client.infra.defaultHypervisorUser","root","Default Hypervisor user used when creating Physical Machines"),
 ("client.storage.volumeMaxSizeValues","1,2,4,8,16,32,64,128,256","Comma separated values, with the allowed sizes when creating or editing a VolumeManagement"),
 ("client.virtual.virtualImagesRefreshConversionsInterval","5","Time interval in seconds to refresh missing virtual image conversions"),
 ("client.main.enterpriseLogoURL","http://www.abiquo.com","URL displayed when the header enterprise logo is clicked"),
 ("client.main.billingUrl","","URL displayed when the report header logo is clicked, if empty the report button will not be displayed"),
 ("client.main.disableChangePassword","1","Allow (1) or deny (0) user to change their password"),
 ("client.logout.url","","Redirect to this URL after logout (empty -> login screen)"),
 ("client.main.allowLogout","1","Allow (1) or deny (0) user to logout"),
 ("client.wiki.showHelp","1","Show (1) or hide (0) the help icon within the plateform"), 
 ("client.wiki.showDefaultHelp","0","Use (1) or not (0) the default help URL within the plateform"), 
 ("client.wiki.defaultURL","http://community.abiquo.com/display/ABI20/Abiquo+Documentation+Home","The default URL opened when not specific help URL is specified"),
 ("client.wiki.infra.createDatacenter","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter","datacenter creation wiki"), 
 ("client.wiki.infra.editDatacenter","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter","datacenter edition wiki"), 
 ("client.wiki.infra.editRemoteService","http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-RemoteServices","remote service edition wiki"), 
 ("client.wiki.infra.createPhysicalMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingPhysicalMachinesonStandardRacks","physical machine creation wiki"),
 ("client.wiki.infra.mailNotification","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-SendingEmailNotifications","mail notification wiki"),
 ("client.wiki.infra.addDatastore","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DatastoreManagement","Datastore manager wiki"),
 ("client.wiki.infra.createRack","http://community.abiquo.com/display/ABI20/Manage+Racks#ManageRacks-CreatingRacks","rack creation wiki"),
 ("client.wiki.infra.createMultiplePhysicalMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingMultiplePhysicalMachines","multiple physical machine creation wiki"),
 ("client.wiki.network.publicVlan","http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-CreateVLANsforPublicNetworks","public vlan creation wiki"),
 ("client.wiki.network.staticRoutes","http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-ConfiguringStaticRoutesUsingDHCP","static routes wiki"),
 ("client.wiki.storage.storageDevice","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-ManagedStorage","storage device creation wiki"),
 ("client.wiki.storage.storagePool","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-StoragePools","storage pool creation wiki"), 
 ("client.wiki.storage.tier","http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-TierManagement","tier edition wiki"),
 ("client.wiki.allocation.global","http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement","global rules wiki"),
 ("client.wiki.allocation.datacenter","http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement","datacenter rules wiki"),
 ("client.wiki.vdc.createVdc","http://community.abiquo.com/display/ABI20/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter","virtual datacenter creation wiki"),
 ("client.wiki.vdc.createVapp","http://community.abiquo.com/display/ABI20/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance","virtual app creation wiki"),
 ("client.wiki.vdc.createPrivateNetwork","http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-CreateaPrivateVLAN","VDC private network creation wiki"),
 ("client.wiki.vdc.createPublicNetwork","http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-PublicIPReservation","VDC public network creation wiki"),
 ("client.wiki.vdc.createVolume","http://community.abiquo.com/display/ABI20/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage","VDC virtual volume creation wiki"),
 ("client.wiki.vm.editVirtualMachine","http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines","Virtual Machine edition wiki"),
 ("client.wiki.vm.bundleVirtualMachine","http://community.abiquo.com/display/ABI20/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-CreateanInstance","Bundles VM wiki"),
 ("client.wiki.vm.createNetworkInterface","http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface","Network Interface creation wiki"),
 ("client.wiki.vm.createInstance","http://community.abiquo.com/display/ABI20/Create+Virtual+Machine+instances","Virtual Machine instance creation wiki"),
 ("client.wiki.vm.createStateful","http://community.abiquo.com/display/ABI20/Create+Persistent+Virtual+Machines","Virtual Machine stateful creation wiki"),
 ("client.wiki.vm.captureVirtualMachine","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-WorkingwithImportedVirtualMachines","Capture Virtual Machine wiki"),
 ("client.wiki.vm.deployInfo","","Show more info when deploying"),
 ("client.wiki.apps.uploadVM","http://community.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem","Virtual Image upload wiki"),
 ("client.wiki.user.createEnterprise","http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise","Enterprise creation wiki"),
 ("client.wiki.user.dataCenterLimits","http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-EdittheEnterprise%27sDatacenters","Datacenter Limits wiki"),
 ("client.wiki.user.createUser","http://community.abiquo.com/display/ABI20/Manage+Users#ManageUsers-CreatingorEditingaUser","User creation wiki"),
 ("client.wiki.user.createRole","http://community.abiquo.com/display/ABI20/Manage+Roles+and+Privileges","Role creation wiki"),
 ("client.wiki.pricing.createCurrency","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CurrenciesTab","Currency creation wiki"),
 ("client.wiki.pricing.createTemplate","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-PricingModelsTab","create pricing template wiki"),
 ("client.wiki.pricing.createCostCode","http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CostCodesTab","create pricing cost code wiki"),
 ("client.wiki.config.general","http://community.abiquo.com/display/ABI20/Configuration+view","Configuration wiki"),
 ("client.wiki.config.heartbeat","http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-Heartbeating","Heartbeat configuration wiki"),
 ("client.wiki.config.licence","http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-LicenseManagement","Licence configuration wiki"),
 ("client.wiki.config.registration","http://community.abiquo.com/display/ABI20/Configuration+view#Configurationview-ProductRegistration","Registration wiki"),
 ("client.wiki.infra.discoverBlades","http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DiscoveringBladesonManagedRacks","discover UCS blades wiki"),
 ("client.network.defaultName","default_private_network","default private vlan name"),
 ("client.network.defaultNetmask","2","index of available netmask"),
 ("client.network.defaultAddress","192.168.0.0","default private vlan address"),
 ("client.network.defaultGateway","192.168.0.1","default private vlan gateway"),
 ("client.network.defaultPrimaryDNS","","default primary DNS"),
 ("client.network.defaultSecondaryDNS","","default secondary DNS"),
 ("client.network.defaultSufixDNS","","default sufix DNS");
UNLOCK TABLES;

/*!40000 ALTER TABLE `system_properties` ENABLE KEYS */;



INSERT IGNORE INTO enterprise_resources_stats (idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed) VALUES (1 ,0,0,0,0,0,0);
--
UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated+1 WHERE idDataCenter = -1;
UPDATE IGNORE cloud_usage_stats SET numUsersCreated = numUsersCreated+2 WHERE idDataCenter = -1;
-- myLocalMachine


--
-- Checks ALL Tables in DB and adds the 'version_c' column required for Hibernate Persistence 
--


