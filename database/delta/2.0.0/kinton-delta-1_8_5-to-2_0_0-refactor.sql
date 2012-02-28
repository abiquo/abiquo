-- ############################################################################################################### --	
-- ############################################################################################################### --	
-- INDEX:
-- 	SCHEMA: TABLES ADDED
--	SCHEMA: TABLES REMOVED
--	SCHEMA: COLUMNS ADDED 
--	SCHEMA: COLUMNS REMOVED 
--	SCHEMA: COLUMNS MODIFIED
--	DATA: NEW DATA
--	SCHEMA: TRIGGERS RECREATED
--	SCHEMA: PROCEDURES RECREATED
-- 	SCHEMA: VIEWS
-- ############################################################################################################### --	
-- ############################################################################################################### --	


DROP PROCEDURE IF EXISTS kinton.delta_1_8_5_to_2_0;

DELIMITER |
CREATE PROCEDURE kinton.delta_1_8_5_to_2_0() 
BEGIN
	SELECT "Applying 1_8_5 to 2_0_0 patch." as " ";
	-- ##################################### --	
	-- ######## SCHEMA: TABLES ADDED ####### --
	-- ##################################### --		
	SELECT "Creating new tables..." as " ";
	-- Definition of table kinton.virtualmachinetrackedstate
	-- 
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='virtualmachinetrackedstate') THEN
		SELECT "Adding new table virtualmachinetrackedstate..." as " ";
		CREATE TABLE  kinton.virtualmachinetrackedstate (
		  idVM int(10) unsigned NOT NULL,
		  previousState varchar(50) NOT NULL,
		  PRIMARY KEY  (idVM),
		  KEY VirtualMachineTrackedState_FK1 (idVM),
		  CONSTRAINT VirtualMachineTrackedState_FK1 FOREIGN KEY (idVM) REFERENCES virtualmachine (idVM) ON DELETE CASCADE
		  )
		 ENGINE=InnoDB DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.pricingCostCode
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='pricingCostCode') THEN
		SELECT "Adding new table pricingCostCode..." as " ";
		CREATE TABLE kinton.pricingCostCode (
		idPricingCostCode int(10) unsigned NOT NULL AUTO_INCREMENT,
		  idPricingTemplate int(10) UNSIGNED NOT NULL,
		  idCostCode int(10) UNSIGNED NOT NULL,
		  price DECIMAL(20,5) NOT NULL default 0,
		  version_c int(11) default 0,
		  PRIMARY KEY (idPricingCostCode) 
		  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.currency
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='currency') THEN
		SELECT "Adding new table currency..." as " ";
		CREATE TABLE kinton.currency (
		  idCurrency int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
		  symbol varchar(10) NOT NULL ,
		  name varchar(20) NOT NULL,
		  digits int(1)  NOT NULL default 2,
		  version_c int(11) default 0,
		  PRIMARY KEY (idCurrency)
		  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.costCode
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='costCode') THEN
		SELECT "Adding new table costCode..." as " ";
		CREATE TABLE kinton.costCode (
		  idCostCode int(10) NOT NULL AUTO_INCREMENT ,
		   name varchar(20) NOT NULL ,
		  description varchar(100) NOT NULL ,
		  version_c int(11) default 0,
		  PRIMARY KEY (idCostCode)
		  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.pricingTier
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='pricingTier') THEN
		SELECT "Adding new table pricingTier..." as " ";
		CREATE TABLE kinton.pricingTier (
		  idPricingTier int(10) unsigned NOT NULL AUTO_INCREMENT,
		  idPricingTemplate int(10) UNSIGNED NOT NULL,
		  idTier int(10) UNSIGNED NOT NULL,
		  price  DECIMAL(20,5) NOT NULL default 0,
		  version_c int(11) default 0,
		  PRIMARY KEY (idPricingTier) 
		  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8; 
	END IF;
	--
	-- Definition of table kinton.costCodeCurrency
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='costCodeCurrency') THEN  
		SELECT "Adding new table costCodeCurrency..." as " ";
		CREATE TABLE  kinton.costCodeCurrency (
		  idCostCodeCurrency int(10) unsigned NOT NULL AUTO_INCREMENT,
		  idCostCode int(10) unsigned,
		  idCurrency int(10) unsigned,
		  price DECIMAL(20,5) NOT NULL default 0,
		  version_c integer NOT NULL DEFAULT 1,
		  PRIMARY KEY (idCostCodeCurrency)
		) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.pricingTemplate
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='pricingTemplate') THEN
		SELECT "Adding new table pricingTemplate..." as " ";
		CREATE TABLE kinton.pricingTemplate (
		  idPricingTemplate int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
		  idCurrency int(10) UNSIGNED NOT NULL ,
		  name varchar(256) NOT NULL ,
		  chargingPeriod  int(10) UNSIGNED NOT NULL ,
		  minimumCharge int(10) UNSIGNED NOT NULL ,
		  showChangesBefore boolean NOT NULL default 0,
		  standingChargePeriod DECIMAL(20,5) NOT NULL default 0,
		  minimumChargePeriod DECIMAL(20,5) NOT NULL default 0,
		  vcpu DECIMAL(20,5) NOT NULL default 0,
		  memoryMB DECIMAL(20,5) NOT NULL default 0,
		  hdGB DECIMAL(20,5) NOT NULL default 0,
		  vlan DECIMAL(20,5) NOT NULL default 0,
		  publicIp DECIMAL(20,5) NOT NULL default 0,
		  defaultTemplate boolean NOT NULL default 0,
		  description varchar(1000)  NOT NULL,
		  last_update timestamp NOT NULL,
		  version_c int(11) default 0,
		  PRIMARY KEY (idPricingTemplate) ,
		  KEY Pricing_FK2_Currency (idCurrency),
		  CONSTRAINT Pricing_FK2_Currency FOREIGN KEY (idCurrency ) REFERENCES kinton.currency (idCurrency ) ON DELETE NO ACTION
		  ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.chef_runlist
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='chef_runlist') THEN
		SELECT "Adding new table chef_runlist..." as " "; 
		CREATE TABLE  kinton.chef_runlist (
		  id int(10) unsigned NOT NULL auto_increment,
		  idVM int(10) unsigned NOT NULL,
		  name varchar(100) NOT NULL,
		  description varchar(255),
		  priority int(10) NOT NULL default 0,
		  version_c int(11) default 0,
		  PRIMARY KEY  (id),
		  KEY chef_runlist_FK1 (idVM),
		  CONSTRAINT chef_runlist_FK1 FOREIGN KEY (idVM) REFERENCES virtualmachine (idVM) ON DELETE CASCADE
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.enterprise_properties
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='enterprise_properties') THEN
		SELECT "Adding new table enterprise_properties..." as " ";
		CREATE TABLE  kinton.enterprise_properties (
		  idProperties int(11) unsigned NOT NULL auto_increment,
		  enterprise int(11) unsigned DEFAULT NULL,
		  PRIMARY KEY  (idProperties),
 		  CONSTRAINT FK_enterprise FOREIGN KEY (enterprise) REFERENCES enterprise (idEnterprise)
		) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.enterprise_properties_map
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='enterprise_properties_map') THEN
		SELECT "Adding new table enterprise_properties_map..." as " ";
		CREATE TABLE  kinton.enterprise_properties_map (
		  enterprise_properties int(11) unsigned NOT NULL,
		  map_key varchar(30) NOT NULL,
		  value varchar(50) default NULL, 
		  CONSTRAINT FK2_enterprise_properties FOREIGN KEY (enterprise_properties) REFERENCES enterprise_properties (idProperties) ON DELETE CASCADE
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	END IF;	
	--
	-- Definition of table kinton.dhcpOption
	--  
	IF NOT EXISTS ( SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='dhcpOption') THEN
		SELECT "Adding new table dhcpOption..." as " ";
		CREATE TABLE kinton.dhcpOption (
		  idDhcpOption int(10) unsigned NOT NULL AUTO_INCREMENT ,
		  dhcp_opt int(20) NOT NULL ,
		   gateway varchar(40),
		  network_address varchar(40) NOT NULL,
		  mask int(4) NOT NULL,
		  netmask varchar(20) NOT NULL,
		  version_c int(11) default 0,
		  PRIMARY KEY (idDhcpOption)
		  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8; 
	END IF;
	--
	-- Definition of table kinton.vlans_dhcpOption
	--  
	IF NOT EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='vlans_dhcpOption') THEN
		SELECT "Adding new table vlans_dhcpOption..." as " ";
		CREATE  TABLE kinton.vlans_dhcpOption (
		  idVlan INT(10) UNSIGNED NOT NULL ,
		  idDhcpOption INT(10) UNSIGNED NOT NULL ,
		  version_c INT(11) default 0,
		  INDEX fk_vlans_dhcp_vlan (idVlan ASC) ,
		  INDEX fk_vlans_dhcp_dhcp (idDhcpOption ASC) ,
		  CONSTRAINT fk_vlans_dhcp_vlan
		    FOREIGN KEY (idVlan )
		    REFERENCES kinton.vlan_network (vlan_network_id )
		    ON DELETE NO ACTION
		    ON UPDATE NO ACTION,
		  CONSTRAINT fk_vlans_dhcp_dhcp
		    FOREIGN KEY (idDhcpOption )
		    REFERENCES kinton.dhcpOption (idDhcpOption )
		    ON DELETE NO ACTION
		    ON UPDATE NO ACTION
		) ENGINE=InnoDB DEFAULT CHARSET=utf8; 
	END IF;	
	--
	-- Definition of table kinton.disk_management
	--
	IF NOT EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='disk_management') THEN
		SELECT "Adding new table disk_management..." as " ";
		CREATE TABLE  kinton.disk_management (
		  idManagement int(10) unsigned NOT NULL,
		  idDatastore int(10) unsigned default NULL,
		  KEY disk_idManagement_FK (idManagement),
		  KEY disk_management_datastore_FK (idDatastore),
		  CONSTRAINT disk_idManagement_FK FOREIGN KEY (idManagement) REFERENCES rasd_management (idManagement) ON DELETE CASCADE,
		  CONSTRAINT disk_datastore_FK FOREIGN KEY (idDatastore) REFERENCES datastore (idDatastore)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	END IF;
	--
	-- Definition of table kinton.one_time_token
	--
	IF NOT EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='one_time_token') THEN
		SELECT "Adding new table one_time_token..." as " ";
		CREATE  TABLE kinton.one_time_token (idOneTimeTokenSession int(3) unsigned NOT NULL AUTO_INCREMENT,
		  token VARCHAR(128) NOT NULL ,
		  version_c int(11) default 0,
		  PRIMARY KEY (idOneTimeTokenSession)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
	END IF;
	-- ######################################## --	
	-- ######## SCHEMA: TABLES REMOVED ######## --
	-- ######################################## --
	--
	-- Definition of table kinton.dhcp_service
	--	
	IF EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='dhcp_service') THEN
		SELECT "Removing table dhcp_service..." as " ";
		DROP  TABLE IF EXISTS kinton.dhcp_service;
	END IF;
	-- ###################################### --	
        -- ######## SCHEMA: COLUMNS ADDED ####### --
	-- ###################################### --
	SELECT "Creating new columns..." as " ";
	-- Columns added to ucs_rack
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ucs_rack' AND column_name='defaultTemplate') THEN
		ALTER TABLE kinton.ucs_rack ADD COLUMN defaultTemplate varchar(200);
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ucs_rack' AND column_name='maxMachinesOn') THEN
		ALTER TABLE kinton.ucs_rack ADD COLUMN maxMachinesOn int(4) DEFAULT 0;
	END IF;
	-- Columns added to virtualimage
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='creation_date') THEN
		ALTER TABLE kinton.virtualimage ADD COLUMN creation_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER cost_code;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='creation_user') THEN
		ALTER TABLE kinton.virtualimage ADD COLUMN creation_user varchar(128) NOT NULL AFTER creation_date;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='chefEnabled') THEN
		ALTER TABLE kinton.virtualimage ADD COLUMN chefEnabled BOOLEAN  NOT NULL DEFAULT false AFTER cost_code;
	END IF;
	-- Columns added to virtualmachine
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='subState') THEN
		ALTER TABLE kinton.virtualmachine ADD COLUMN subState VARCHAR(50)  DEFAULT NULL AFTER state;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='temporal') THEN
		ALTER TABLE kinton.virtualmachine ADD COLUMN temporal int(10) unsigned default NULL;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='network_configuration_id') THEN
		ALTER TABLE kinton.virtualmachine ADD COLUMN network_configuration_id int(11) unsigned; 
		ALTER TABLE kinton.virtualmachine ADD CONSTRAINT virtualMachine_FK6 FOREIGN KEY (network_configuration_id) REFERENCES network_configuration (network_configuration_id) ON DELETE SET NULL;		
	END IF;
	-- Columns added to enterprise
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_url') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN chef_url VARCHAR(255)  DEFAULT NULL AFTER publicIPHard;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_client') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN chef_client VARCHAR(50)  DEFAULT NULL AFTER chef_url;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_validator') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN chef_validator VARCHAR(50)  DEFAULT NULL AFTER chef_client;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_client_certificate') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN chef_client_certificate TEXT  DEFAULT NULL AFTER chef_validator;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_validator_certificate') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN chef_validator_certificate TEXT  DEFAULT NULL AFTER chef_client_certificate;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='idPricingTemplate') THEN
		ALTER TABLE kinton.enterprise ADD COLUMN idPricingTemplate int(10) unsigned DEFAULT NULL;
		ALTER TABLE kinton.enterprise ADD CONSTRAINT enterprise_pricing_FK FOREIGN KEY (idPricingTemplate) REFERENCES kinton.pricingTemplate (idPricingTemplate);
	END IF;
	-- Columns added to node_virtual_image_stateful_conversions
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='node_virtual_image_stateful_conversions' AND column_name='state') THEN
		ALTER TABLE kinton.node_virtual_image_stateful_conversions ADD COLUMN state VARCHAR(50)  NOT NULL AFTER idDiskStatefulConversion;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='node_virtual_image_stateful_conversions' AND column_name='subState') THEN
		ALTER TABLE kinton.node_virtual_image_stateful_conversions ADD COLUMN subState VARCHAR(50)  DEFAULT NULL AFTER state;
	END IF;
	-- Columns added to datacenter
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='datacenter' AND column_name='uuid') THEN
		ALTER TABLE kinton.datacenter ADD COLUMN uuid VARCHAR(40) DEFAULT NULL AFTER idDataCenter;
	END IF;
	-- Columns added to storage_device
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='storage_device' AND column_name='username') THEN
		ALTER TABLE kinton.storage_device ADD COLUMN username varchar(256) DEFAULT NULL;
	END IF;
	-- 
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='storage_device' AND column_name='password') THEN
		ALTER TABLE kinton.storage_device ADD COLUMN password varchar(256) DEFAULT NULL;
	END IF;
	-- Columns added to rasd_management
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='rasd_management' AND column_name='temporal') THEN
		ALTER TABLE kinton.rasd_management ADD COLUMN temporal int(10) unsigned default NULL; 
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='rasd_management' AND column_name='sequence') THEN
		ALTER TABLE kinton.rasd_management ADD COLUMN sequence int(10) unsigned default NULL; 
	END IF;

	-- ######################################## --	
	-- ######## SCHEMA: COLUMNS REMOVED ####### --
	-- ######################################## --
	SELECT "Removing deprecated columns..." as " ";	
	-- Columns dropped from virtualimage
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='treaty') THEN
		ALTER TABLE kinton.virtualimage DROP COLUMN treaty;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='deleted') THEN
		ALTER TABLE kinton.virtualimage DROP COLUMN deleted;
	END IF;
	-- Columns dropped from physicalmachine
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='realram') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN realram;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='realcpu') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN realcpu;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='realStorage') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN realStorage;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='hd') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN hd;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='hdUsed') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN hdUsed;
	END IF;
	-- Columns dropped from virtualapp
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualapp' AND column_name='state') THEN
		ALTER TABLE kinton.virtualapp DROP COLUMN state;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualapp' AND column_name='substate') THEN
		ALTER TABLE kinton.virtualapp DROP COLUMN substate; 
	END IF;
	-- Columns dropped from vappstateful_conversions
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='vappstateful_conversions' AND column_name='state') THEN
		ALTER TABLE kinton.vappstateful_conversions DROP COLUMN state;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='vappstateful_conversions' AND column_name='substate') THEN
		ALTER TABLE kinton.vappstateful_conversions DROP COLUMN substate; 
	END IF;
	-- Columns dropped from ip_pool_management
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ip_pool_management' AND column_name='configureGateway') THEN
		-- Updating data is needed to remove this column/FK
		UPDATE vlan_network vl, ip_pool_management ip, rasd_management rm, virtualmachine vm SET vm.network_configuration_id = vl.network_configuration_id WHERE ip.vlan_network_id = vl.vlan_network_id AND ip.idManagement = rm.idManagement and configureGateway = 1 AND rm.idvm = vm.idvm;	
		ALTER TABLE kinton.ip_pool_management DROP COLUMN configureGateway;
	END IF;
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ip_pool_management' AND column_name='dhcp_service_id') THEN
		ALTER TABLE kinton.ip_pool_management DROP FOREIGN KEY ippool_dhcpservice_FK;
		ALTER TABLE kinton.ip_pool_management DROP KEY ippool_dhcpservice_FK;
		ALTER TABLE kinton.ip_pool_management DROP COLUMN dhcp_service_id;
	END IF;
	-- Columns dropped from network_configuration
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='network_configuration' AND column_name='dhcp_service_id') THEN
		ALTER TABLE kinton.network_configuration DROP FOREIGN KEY configuration_dhcp_FK;
		ALTER TABLE kinton.network_configuration DROP KEY configuration_dhcp_FK;
		ALTER TABLE kinton.network_configuration DROP COLUMN dhcp_service_id;
	END IF;
	-- ######################################## --	
	-- ######## SCHEMA: COLUMNS MODIFIED ###### --
	-- ######################################## --
	SELECT "Modifying existing columns..." as " ";
	ALTER TABLE kinton.physicalmachine MODIFY COLUMN vswitchName varchar(200) NOT NULL;
	ALTER TABLE kinton.ovf_package MODIFY COLUMN name VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
	ALTER TABLE kinton.virtualimage MODIFY COLUMN cost_code int(4) DEFAULT 0;
	-- /* ABICLOUDPREMIUM-2878 - For consistency porpouse, changed vharchar(30) to varchar(256) */
	ALTER TABLE kinton.metering MODIFY COLUMN physicalmachine VARCHAR(256)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
	ALTER TABLE kinton.repository MODIFY COLUMN URL VARCHAR(255) NOT NULL;
	-- ############################################ --	
	-- ######## SCHEMA: CONSTRAINTS MODIFIED ###### --
	-- ############################################ --	
	SELECT "Modifying constraints..." as " ";
	-- Constraint 'fk_ovf_package_list_has_ovf_package_ovf_package1' is rebuilt	
	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package DROP FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package1;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package ADD CONSTRAINT fk_ovf_package_list_has_ovf_package_ovf_package1 FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package1 (id_ovf_package) REFERENCES ovf_package (id_ovf_package) ON DELETE CASCADE ON UPDATE NO ACTION;
	END IF;
	-- Constraint idResource_FK rebuilt
	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='rasd_management' AND constraint_name='idResource_FK') THEN
		ALTER TABLE kinton.rasd_management DROP FOREIGN KEY idResource_FK;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='rasd_management' AND constraint_name='idResource_FK') THEN
		ALTER TABLE kinton.rasd_management ADD  CONSTRAINT idResource_FK FOREIGN KEY (idResource) REFERENCES rasd (instanceID) ON DELETE SET NULL;
	END IF;
	
	-- ########################################################## --	
        -- ######## DATA: NEW DATA (INSERTS, UPDATES, DELETES ####### --
	-- ########################################################## --
	SELECT "Updating data..." as " ";
	-- New System Properties
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.infra.ucsManagerLink' AND value='/ucsm/ucsm.jnlp' AND description='URL to display UCS Manager Interface';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.infra.ucsManagerLink','/ucsm/ucsm.jnlp','URL to display UCS Manager Interface');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE  name='client.wiki.pricing.createCurrency' AND value='http://community.abiquo.com/display/ABI20/Pricing+View' AND description='Currency creation wiki';
	IF @existsCount = 0 THEN
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.wiki.pricing.createCurrency','http://community.abiquo.com/display/ABI20/Pricing+View','Currency creation wiki');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.pricing.createTemplate' AND value='http://community.abiquo.com/display/ABI20/Pricing+View' AND description='create pricing template wiki';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.wiki.pricing.createTemplate','http://community.abiquo.com/display/ABI20/Pricing+View','create pricing template wiki');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.pricing.createCostCode' AND value='http://community.abiquo.com/display/ABI20/Pricing+View' AND description='create pricing cost code wiki';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.wiki.pricing.createCostCode','http://community.abiquo.com/display/ABI20/Pricing+View','create pricing cost code wiki');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.logout.url' AND value='' AND description='Redirect to this URL after logout (empty -> login screen)';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.logout.url','','Redirect to this URL after logout (empty -> login screen)');
	END IF;
	
	-- PRICING --
	-- Dumping data for table kinton.privilege
	SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='49' AND name='PRICING_VIEW';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.privilege VALUES (49,'PRICING_VIEW',0);
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='50' AND name='PRICING_MANAGE';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.privilege VALUES (50,'PRICING_MANAGE',0);
	END IF;

	-- PRICING --
	-- Dumping data for table kinton.currency
	SELECT COUNT(*) INTO @existsCount FROM kinton.currency WHERE idCurrency='1' AND symbol='USD';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.currency values (1, "USD", "Dollar - $", 2, 0);
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.currency WHERE idCurrency='2' AND symbol='EUR';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.currency values (2, "EUR", CONCAT("Euro - " ,0xE282AC), 2,  0);
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.currency WHERE idCurrency='3' AND symbol='JPY';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.currency values (3, "JPY", CONCAT("Yen - " , 0xc2a5), 0, 0);
	END IF;
	
	-- PRICING --
	-- Dumping data for table kinton.roles_privileges
	--
	SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='49';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.roles_privileges VALUES (1,49,0);
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='' AND idPrivilege='50';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.roles_privileges VALUES (1,50,0);
	END IF;

	-- CHECK: Is this needed?
	-- First I need to update some rows before to delete the default_network field
	-- UPDATE kinton.virtualdatacenter vdc, kinton.vlan_network v set vdc.default_vlan_network_id = v.vlan_network_id WHERE vdc.networktypeID = v.network_id and v.default_network = 1;
	-- ALTER TABLE kinton.vlan_network DROP COLUMN default_network;

	-- Updating virtualimages
	UPDATE kinton.virtualimage set creation_user = 'ABIQUO-BEFORE-2.0', creation_date = CURRENT_TIMESTAMP;
	UPDATE kinton.virtualimage set idRepository = null where stateful = 1;
	UPDATE ip_pool_management im, rasd_management rm, rasd r SET rm.sequence = r.configurationname WHERE rm.idresource=r.instanceid AND im.idmanagement = rm.idmanagement AND rm.idvm IS NOT NULL;
	UPDATE volume_management vm, rasd_management rm, rasd r SET rm.sequence=IF(r.generation IS NULL, 0, r.generation +1) WHERE rm.idResource=r.instanceID AND vm.idManagement = rm.idManagement AND rm.idVM IS NOT NULL;

	-- New enterprise properties
	SELECT COUNT(*) INTO @existsCount FROM kinton.enterprise_properties WHERE idProperties='1' AND enterprise='1';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.enterprise_properties VALUES (1,1);
	END IF;
	-- New enterprise properties
	SELECT COUNT(*) INTO @existsCount FROM kinton.enterprise_properties_map WHERE enterprise_properties='1' AND map_key='Support e-mail' AND value='support@abiquo.com';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.enterprise_properties_map VALUES  (1,'Support e-mail','support@abiquo.com');
	END IF;	

	--
	SELECT "Updated succesfully from 1.8.5 to 2.0.0..." as " ";
END;
|
DELIMITER ;

# Now invoke the SP
CALL kinton.delta_1_8_5_to_2_0();

# And on successful completion, remove the SP, so we are not cluttering the DBMS with upgrade code!
DROP PROCEDURE IF EXISTS kinton.delta_1_8_5_to_2_0;

-- ########################################### --	
-- ######## SCHEMA: TRIGGERS RECREATED ####### --
-- ########################################### --

-- NEW TRIGGER (was removed and it's back)
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;

DROP TRIGGER IF EXISTS kinton.delete_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_rasd_management_update_stats;


DELIMITER |
SELECT "Recreating trigger create_nodevirtualimage_update_stats..." as " ";
CREATE TRIGGER kinton.create_nodevirtualimage_update_stats AFTER INSERT ON kinton.nodevirtualimage
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    DECLARE costCodeObj int(4);
    DECLARE type INTEGER;
    DECLARE state VARCHAR(50);
    DECLARE ram INTEGER;
    DECLARE cpu INTEGER;
    DECLARE hd bigint;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter, vdc.idEnterprise  INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj, idEnterpriseObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = NEW.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.idType, vm.state, vm.cpu, vm.ram, vm.hd INTO type, state, cpu, ram, hd
     FROM virtualmachine vm
	WHERE vm.idVM = NEW.idVM;
      --  INSERT INTO debug_msg (msg) VALUES (CONCAT('createNVI ', type, ' - ', state, ' - ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL')));
    IF type=1 THEN
    	-- Imported !!!
		UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
          IF state = "ON" THEN 	
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualApp = idVirtualAppObj;
		        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
		        WHERE idDataCenter = idDataCenterObj;       
		        UPDATE IGNORE enterprise_resources_stats 
		            SET vCpuUsed = vCpuUsed + cpu,
		                memoryUsed = memoryUsed + ram,
		                localStorageUsed = localStorageUsed + hd
		        WHERE idEnterprise = idEnterpriseObj;
		        UPDATE IGNORE dc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + cpu,
		            memoryUsed = memoryUsed + ram,
		            localStorageUsed = localStorageUsed + hd
		        WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
		        UPDATE IGNORE vdc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + cpu,
		            memoryUsed = memoryUsed + ram,
		            localStorageUsed = localStorageUsed + hd
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;	
		END IF;
    END IF;    
    SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, type, "NOT_ALLOCATED", state, "NOT_ALLOCATED", ram, cpu, hd, costCodeObj);
        END IF;              
     END IF;
    END IF;
  END;
--
|
--
SELECT "Recreating trigger update_virtualmachine_update_stats ..." as " ";
CREATE TRIGGER kinton.update_virtualmachine_update_stats AFTER UPDATE ON kinton.virtualmachine
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE costCodeObj int(4);
	DECLARE previousState VARCHAR(50);
	-- For debugging purposes only        
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
	-- We always store previous state when starting a transaction
	IF NEW.state != OLD.state AND NEW.state='LOCKED' THEN
		UPDATE virtualmachinetrackedstate SET previousState=OLD.state WHERE idVM=NEW.idVM;
	END IF;
	--
	SELECT vmts.previousState INTO previousState
        FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = NEW.idVM;
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', NEW.idVM, ' - ', OLD.idType, ' - ', NEW.idType, ' - ', OLD.state, ' - ', NEW.state, ' - ', previousState));	
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
-- INSERT INTO debug_msg (msg) VALUES (CONCAT('update values ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
	--
	-- Imported VMs will be updated on create_node_virtual_image
	-- Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from delete_nodevirtualimage_update_stats ON DELETE nodevirtualimage when updating the VApp
	-- Main case: an imported VM changes its state (from LOCKED to ...)
	IF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "ON" AND previousState != "ON" THEN 
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
	    ELSEIF (NEW.state IN ("PAUSED","OFF","NOT_ALLOCATED") AND previousState = "ON") THEN
                -- When Undeploying a full Vapp
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
            END IF;
            IF NEW.state = "ON" AND previousState = "NOT_ALLOCATED" THEN 
                -- VMachine Deployed
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = "NOT_ALLOCATED"  AND previousState IN ("ON","OFF") THEN 
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
        SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, previousState, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
       		 END IF;              
	    END IF;
      END IF;
    END;
--
|
-- 
SELECT "Recreating trigger delete_nodevirtualimage_update_stats..." as " ";
CREATE TRIGGER kinton.delete_nodevirtualimage_update_stats AFTER DELETE ON kinton.nodevirtualimage
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;   
    DECLARE costCodeObj int(4); 
    DECLARE previousState VARCHAR(50);
    DECLARE state VARCHAR(50);
    DECLARE ram INTEGER;
    DECLARE cpu INTEGER;
    DECLARE hd bigint;
    DECLARE type INTEGER;
    DECLARE isUsingIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter, vdc.idEnterprise INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj, idEnterpriseObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = OLD.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.idType, vm.cpu, vm.ram, vm.hd, vm.state INTO type, cpu, ram, hd, state
     FROM virtualmachine vm
	WHERE vm.idVM = OLD.idVM;
    SELECT vmts.previousState INTO previousState
     FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = OLD.idVM;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('deleteNVI ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
-- INSERT INTO debug_msg (msg) VALUES (CONCAT('deleteNVI values', IFNULL(cpu,'NULL'), ' - ',IFNULL(ram,'NULL'), ' - ',IFNULL(hd,'NULL')));						
    --
    IF type = 1 THEN
      IF previousState != "NOT_ALLOCATED" AND previousState != "UNKNOWN" THEN      	
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
           UPDATE IGNORE enterprise_resources_stats 
               SET vCpuUsed = vCpuUsed - cpu,
                   memoryUsed = memoryUsed - ram,
                   localStorageUsed = localStorageUsed - hd
           WHERE idEnterprise = idEnterpriseObj;
           UPDATE IGNORE dc_enterprise_stats 
           SET     vCpuUsed = vCpuUsed - cpu,
               memoryUsed = memoryUsed - ram,
               localStorageUsed = localStorageUsed - hd
           WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
           UPDATE IGNORE vdc_enterprise_stats 
           SET     vCpuUsed = vCpuUsed - cpu,
               memoryUsed = memoryUsed - ram,
               localStorageUsed = localStorageUsed - hd
           WHERE idVirtualDataCenter = idVirtualDataCenterObj;                 
      END IF;
      --
      IF previousState != "NOT_ALLOCATED" AND previousState != "UNKNOWN" THEN      	
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF previousState = "ON" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
    SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = OLD.idImage;
    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=OLD.idImage AND vi.idRepository IS NOT NULL) THEN 
	          CALL AccountingVMRegisterEvents(OLD.idVM, type, "-", "NOT_ALLOCATED", previousState, ram, cpu, hd, costCodeObj);
        END IF;              
     END IF;
  END IF;
  END;
--
|
--
SELECT "Recreating trigger update_rasd_management_update_stats..." as " ";
CREATE TRIGGER kinton.update_rasd_management_update_stats AFTER UPDATE ON kinton.rasd_management
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50) CHARACTER SET utf8;
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE ipAddress VARCHAR(20) CHARACTER SET utf8;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --     
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateRASD: ',idState,' - ', IFNULL(OLD.idVirtualApp, 'OLD.idVirtualApp es NULL'), IFNULL(NEW.idVirtualApp, 'NEW.idVirtualApp es NULL')));	
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
			-- Volume added from a Vapp
			IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    IF idState = 1 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    END IF;                         
			END IF;
			-- Volume removed from a Vapp
			IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
		            UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    IF idState = 1 THEN
				SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
				FROM virtualdatacenter vdc
				WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
				SELECT r.limitResource INTO reservedSize
				FROM rasd r
				WHERE r.instanceID = OLD.idResource;
				--  INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));	
				UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;			
				UPDATE IGNORE enterprise_resources_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idEnterprise = idEnterpriseObj;
				UPDATE IGNORE dc_enterprise_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
				UPDATE IGNORE vdc_enterprise_stats 
				    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed - reservedSize
				WHERE idVirtualDataCenter = OLD.idVirtualDatacenter;
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
            -- From old autoDetachVolume
            -- UPDATE IGNORE volume_management v set v.state = 0
            -- WHERE v.idManagement = OLD.idManagement;
            -- Checks for used IPs
            IF OLD.idVM IS NULL AND NEW.idVM IS NOT NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
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
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
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
                WHERE ipm.vlan_network_id = vn.vlan_network_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
        AND vn.networktype = 'PUBLIC'
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
                    IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                        CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
                    END IF;                    
                END IF;
            END IF;
        END IF;
    END;
| 
DELIMITER ;


-- ############################################# --	
-- ######## SCHEMA: PROCEDURES RECREATED ####### --
-- ############################################# --

-- ############################## --	
-- ######## SCHEMA: VIEWS ####### --
-- ############################## --

-- ############################## --	
-- ######## SCHEMA: VIEWS ####### --
-- ############################## --


-- ############################################# --	
-- ######## STATISTICS SANITY PROCEDURES ####### --
-- ############################################# --
-- This should be included in EVERY delta
-- FIX and Uncomment THIS!
-- CALL PROCEDURE kinton.CalculateCloudUsageStats();
-- CALL PROCEDURE kinton.CalculateEnterpriseResourcesStats();
-- CALL PROCEDURE kinton.CalculateVappEnterpriseStats();
-- CALL PROCEDURE kinton.CalculateVdcEnterpriseStats();


# This should not be necessary
CALL kinton.add_version_column_to_all();
