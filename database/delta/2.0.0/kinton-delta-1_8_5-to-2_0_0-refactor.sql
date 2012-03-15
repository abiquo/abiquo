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
SELECT "### APPLYING 1_8_5 TO 2_0_0 PATCH. ###" as " ";

SET @DISABLE_STATS_TRIGGERS = 1;
SELECT "STEP 1 TRIGGERS DISABLED DURING THE UPGRADE" as " ";

DROP PROCEDURE IF EXISTS kinton.delta_1_8_5_to_2_0;

DELIMITER |
CREATE PROCEDURE kinton.delta_1_8_5_to_2_0() 
BEGIN

	-- ##################################### --	
	-- ######## SCHEMA: TABLES ADDED ####### --
	-- ##################################### --		
	SELECT "STEP 2 CREATING NEW TABLES..." as " ";
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
		SELECT "- Table virtualMachineTrackedstate created" as " ";
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

	-- ###################################### --	
        -- ######## SCHEMA: COLUMNS ADDED ####### --
	-- ###################################### --
	SELECT "STEP 3 CREATING NEW COLUMNS..." as " ";
	-- Columns added to ucs_rack
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ucs_rack' AND column_name='defaultTemplate') THEN
		SELECT "Adding defaultTemplate on ucs_rack" as " ";
		ALTER TABLE kinton.ucs_rack ADD COLUMN defaultTemplate varchar(200);
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ucs_rack' AND column_name='maxMachinesOn') THEN
		SELECT "Adding maxMachinesOn on ucs_rack" as " ";
		ALTER TABLE kinton.ucs_rack ADD COLUMN maxMachinesOn int(4) DEFAULT 0;
	END IF;
	-- Columns added to virtualimage
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='creation_date') THEN
		SELECT "Adding creation_date on virtualimage" as " ";
		ALTER TABLE kinton.virtualimage ADD COLUMN creation_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER cost_code;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='creation_user') THEN
		SELECT "Adding creation_user on virtualimage" as " ";
		ALTER TABLE kinton.virtualimage ADD COLUMN creation_user varchar(128) NOT NULL AFTER creation_date;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='chefEnabled') THEN
		SELECT "Adding chefEnabled on virtualimage" as " ";
		ALTER TABLE kinton.virtualimage ADD COLUMN chefEnabled BOOLEAN  NOT NULL DEFAULT false AFTER cost_code;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='ethDriverType') THEN
		SELECT "Adding ethDriverType on virtualimage" as " ";
		ALTER TABLE kinton.virtualimage ADD COLUMN ethDriverType varchar(16) DEFAULT NULL AFTER type;
	END IF;
	-- Columns added to virtualmachine
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='subState') THEN
		SELECT "Adding subState on virtualmachine" as " ";
		ALTER TABLE kinton.virtualmachine ADD COLUMN subState VARCHAR(50)  DEFAULT NULL AFTER state;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='temporal') THEN
		SELECT "Adding temporal on virtualmachine" as " ";
		ALTER TABLE kinton.virtualmachine ADD COLUMN temporal int(10) unsigned default NULL;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='network_configuration_id') THEN
		SELECT "Adding network_configuration on virtualmachine" as " ";
		ALTER TABLE kinton.virtualmachine ADD COLUMN network_configuration_id int(11) unsigned; 
		SELECT "Adding constraint virtualMachine_FK6 on virtualmachine" as " ";
		ALTER TABLE kinton.virtualmachine ADD CONSTRAINT virtualMachine_FK6 FOREIGN KEY (network_configuration_id) REFERENCES network_configuration (network_configuration_id) ON DELETE SET NULL;		
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualmachine' AND column_name='ethDriverType') THEN
		SELECT "Adding ethDriverType on virtualmachine" as " ";
		ALTER TABLE kinton.virtualmachine ADD COLUMN ethDriverType varchar(16) DEFAULT NULL AFTER temporal;
	END IF;
	-- Columns added to enterprise
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_url') THEN
		SELECT "Adding chef_url on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN chef_url VARCHAR(255)  DEFAULT NULL AFTER publicIPHard;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_client') THEN
		SELECT "Adding chef_client on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN chef_client VARCHAR(50)  DEFAULT NULL AFTER chef_url;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_validator') THEN
		SELECT "Adding chef_validator on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN chef_validator VARCHAR(50)  DEFAULT NULL AFTER chef_client;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_client_certificate') THEN
		SELECT "Adding client_certificate on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN chef_client_certificate TEXT  DEFAULT NULL AFTER chef_validator;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='chef_validator_certificate') THEN
		SELECT "Adding chef_validator_certificate on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN chef_validator_certificate TEXT  DEFAULT NULL AFTER chef_client_certificate;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='enterprise' AND column_name='idPricingTemplate') THEN
		SELECT "Adding idPricingTemplate on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD COLUMN idPricingTemplate int(10) unsigned DEFAULT NULL;
		SELECT "Adding constraint enterprise_pricing_FK on enterprise" as " ";
		ALTER TABLE kinton.enterprise ADD CONSTRAINT enterprise_pricing_FK FOREIGN KEY (idPricingTemplate) REFERENCES kinton.pricingTemplate (idPricingTemplate);
	END IF;
	-- Columns added to node_virtual_image_stateful_conversions
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='node_virtual_image_stateful_conversions' AND column_name='state') THEN
		SELECT "Adding state on virtual_image_stateful_conversions" as " ";
		ALTER TABLE kinton.node_virtual_image_stateful_conversions ADD COLUMN state VARCHAR(50)  NOT NULL AFTER idDiskStatefulConversion;
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='node_virtual_image_stateful_conversions' AND column_name='subState') THEN
		SELECT "Adding subState on virtual_image_stateful_conversions" as " ";
		ALTER TABLE kinton.node_virtual_image_stateful_conversions ADD COLUMN subState VARCHAR(50)  DEFAULT NULL AFTER state;
	END IF;
	-- Columns added to datacenter
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='datacenter' AND column_name='uuid') THEN
		SELECT "Adding uuid on datacenter" as " ";
		ALTER TABLE kinton.datacenter ADD COLUMN uuid VARCHAR(40) DEFAULT NULL AFTER idDataCenter;
	END IF;
	-- Columns added to storage_device
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='storage_device' AND column_name='username') THEN
		SELECT "Adding username on storage_device" as " ";
		ALTER TABLE kinton.storage_device ADD COLUMN username varchar(256) DEFAULT NULL;
	END IF;
	-- 
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='storage_device' AND column_name='password') THEN
		SELECT "Adding password on storage_device" as " ";
		ALTER TABLE kinton.storage_device ADD COLUMN password varchar(256) DEFAULT NULL;
	END IF;
	-- Columns added to rasd_management
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='rasd_management' AND column_name='temporal') THEN
		SELECT "Adding temporal on rasd_management" as " ";
		ALTER TABLE kinton.rasd_management ADD COLUMN temporal int(10) unsigned default NULL; 
	END IF;
	--
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='rasd_management' AND column_name='sequence') THEN
		SELECT "Adding sequene on rasd_management" as " ";
		ALTER TABLE kinton.rasd_management ADD COLUMN sequence int(10) unsigned default NULL; 
	END IF;

	-- Adding icon url on virtualImage
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='iconUrl') THEN

		SELECT "Adding iconUrl on virtualImage" as " ";
		ALTER TABLE kinton.virtualimage ADD COLUMN iconUrl VARCHAR(255) DEFAULT NULL AFTER cpu_required;
	END IF;

	-- Adding icon url on ovf_package
	IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ovf_package' AND column_name='iconUrl') THEN

		SELECT "Adding iconUrl on ovf_package" as " ";
		ALTER TABLE kinton.ovf_package ADD COLUMN iconUrl VARCHAR(255) DEFAULT NULL AFTER description;
	END IF;

    #
    # Populate up cost codes from the virtualimage table...
    #
    SELECT COUNT(*) INTO @existsCount FROM kinton.costCode;
    IF @existsCount = 0 THEN
        INSERT INTO kinton.costCode(name, description)
            SELECT DISTINCT(cost_code), 'Automatically populated during upgrade'
            FROM kinton.virtualimage
            WHERE cost_code IS NOT NULL AND cost_code NOT LIKE '' AND NOT EXISTS (SELECT 1 FROM kinton.costCode cc WHERE cc.name=cost_code);
    END IF;

    #
    # Now update the strings to contain numeric values associated with the idCostCode
    # When we subsequently modify the column type they should point to the correct cost code idValues...
    #
    SELECT COUNT(*) INTO @existsCount FROM kinton.costCode;
    IF @existsCount > 0 THEN
        UPDATE kinton.virtualimage, kinton.costCode
            SET kinton.virtualimage.cost_code=kinton.costCode.idCostCode
        WHERE
            kinton.virtualimage.cost_code=kinton.costCode.name;
    END IF;


	-- ######################################## --	
	-- ######## SCHEMA: COLUMNS MODIFIED ###### --
	-- ######################################## --
	SELECT "STEP 4 MODIFIYING EXISTING COLUMNS..." as " ";
	ALTER TABLE kinton.physicalmachine MODIFY COLUMN vswitchName varchar(200) NOT NULL;
	ALTER TABLE kinton.ovf_package MODIFY COLUMN name VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
	ALTER TABLE kinton.ovf_package MODIFY COLUMN productName VARCHAR(255)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
	ALTER TABLE kinton.virtualimage MODIFY COLUMN cost_code int(10) DEFAULT 0;
	-- /* ABICLOUDPREMIUM-2878 - For consistency porpouse, changed vharchar(30) to varchar(256) */
	ALTER TABLE kinton.metering MODIFY COLUMN physicalmachine VARCHAR(256)  CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
	ALTER TABLE kinton.metering MODIFY COLUMN user VARCHAR(128) NOT NULL;
	ALTER TABLE kinton.repository MODIFY COLUMN URL VARCHAR(255) NOT NULL;
	ALTER TABLE kinton.ovf_package_list MODIFY COLUMN name VARCHAR(45) NOT NULL;
	ALTER TABLE kinton.vlan_network MODIFY COLUMN networktype varchar(15) NOT NULL DEFAULT 'INTERNAL';
	ALTER TABLE kinton.user MODIFY COLUMN creationDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

	-- ############################################ --	
	-- ######## SCHEMA: CONSTRAINTS MODIFIED ###### --
	-- ############################################ --	
	SELECT "STEP 5 MODIFIYING CONSTRAINTS..." as " ";
	-- Constraint 'fk_ovf_package_list_has_ovf_package_ovf_package1' is rebuilt	
	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package DROP FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package1;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package ADD CONSTRAINT fk_ovf_package_list_has_ovf_package_ovf_package1 FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package1 (id_ovf_package) REFERENCES ovf_package (id_ovf_package) ON DELETE NO ACTION ON UPDATE NO ACTION;
	END IF;

	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package_list1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package DROP FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package_list1;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list_has_ovf_package' AND constraint_name='fk_ovf_package_list_has_ovf_package_ovf_package_list1') THEN
		ALTER TABLE kinton.ovf_package_list_has_ovf_package ADD CONSTRAINT fk_ovf_package_list_has_ovf_package_ovf_package_list1 FOREIGN KEY fk_ovf_package_list_has_ovf_package_ovf_package_list1 (id_ovf_package_list) REFERENCES ovf_package_list (id_ovf_package_list) ON DELETE CASCADE ON UPDATE NO ACTION;
	END IF;

	-- Constraint 'fk_ovf_package_list_repository' in ovf_package_list
	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list' AND constraint_name='fk_ovf_package_list_repository') THEN
		ALTER TABLE kinton.ovf_package_list DROP FOREIGN KEY fk_ovf_package_list_repository;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='ovf_package_list' AND constraint_name='fk_ovf_package_list_repository') THEN
		ALTER TABLE kinton.ovf_package_list ADD CONSTRAINT fk_ovf_package_list_repository FOREIGN KEY fk_ovf_package_list_repository (id_apps_library) REFERENCES apps_library (id_apps_library) ON DELETE CASCADE ON UPDATE NO ACTION;
	END IF;
	-- Constraint idResource_FK rebuilt
	IF EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='rasd_management' AND constraint_name='idResource_FK') THEN
		ALTER TABLE kinton.rasd_management DROP FOREIGN KEY idResource_FK;
	END IF;
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='rasd_management' AND constraint_name='idResource_FK') THEN
		ALTER TABLE kinton.rasd_management ADD  CONSTRAINT idResource_FK FOREIGN KEY (idResource) REFERENCES rasd (instanceID) ON DELETE SET NULL;
	END IF;
	-- Index for unicity in user table
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='user' AND constraint_name='user_auth_idx') THEN
		ALTER TABLE user ADD UNIQUE KEY user_auth_idx (user, authType); 
	END IF;
	-- Index name on category table
	IF NOT EXISTS (SELECT * FROM information_schema.table_constraints WHERE table_schema= 'kinton' AND table_name='category' AND constraint_name='name') THEN
		ALTER TABLE `kinton`.`category` ADD UNIQUE INDEX `name`(`name`) using BTREE;
	END IF;

	-- ########################################################## --	
        -- ######## DATA: NEW DATA (INSERTS, UPDATES, DELETES ####### --
	-- ########################################################## --
	SELECT "STEP 6 UPDATING DATA..." as " ";
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
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.network.staticRoutes' AND value='http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-ConfiguringStaticRoutesUsingDHCP' AND description='static routes wiki';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.wiki.network.staticRoutes','http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-ConfiguringStaticRoutesUsingDHCP','static routes wiki');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultName' AND value='default_private_network' AND description='default private vlan name';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultName','default_private_network','default private vlan name');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultNetmask' AND value='2' AND description='index of available netmask';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultNetmask','2','index of available netmask');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultAddress' AND value='192.168.0.0' AND description='default private vlan address';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultAddress','192.168.0.0','default private vlan address');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultGateway' AND value='192.168.0.1' AND description='default private vlan gateway';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultGateway','192.168.0.1','default private vlan gateway');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultPrimaryDNS' AND value='' AND description='default primary DNS';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultPrimaryDNS','','default primary DNS');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultSecondaryDNS' AND value='' AND description='default secondary DNS';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultSecondaryDNS','','default secondary DNS');
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.network.defaultSufixDNS' AND value='' AND description='default sufix DNS';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.network.defaultSufixDNS','','default sufix DNS');
	END IF;

	-- Update System Properties
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.defaultURL' AND value='http://community.abiquo.com/display/ABI18/Abiquo+Documentation+Home';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Abiquo+Documentation+Home' WHERE name='client.wiki.defaultURL';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.createDatacenter' AND value='http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter' WHERE name='client.wiki.infra.createDatacenter';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.editDatacenter' AND value='http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter' WHERE name='client.wiki.infra.editDatacenter';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.editRemoteService' AND value='http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-RemoteServices';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-RemoteServices' WHERE name='client.wiki.infra.editRemoteService';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.createPhysicalMachine' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingPhysicalMachinesonStandardRacks';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingPhysicalMachinesonStandardRacks' WHERE name='client.wiki.infra.createPhysicalMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.mailNotification' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-SendingEmailNotifications';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-SendingEmailNotifications' WHERE name='client.wiki.infra.mailNotification';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.addDatastore' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-DatastoresManagement';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DatastoreManagement' WHERE name='client.wiki.infra.addDatastore';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.createRack' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingRacks';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Racks#ManageRacks-CreatingRacks' WHERE name='client.wiki.infra.createRack';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.createMultiplePhysicalMachine' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingMultiplePhysicalMachines';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingMultiplePhysicalMachines' WHERE name='client.wiki.infra.createMultiplePhysicalMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.createMultiplePhysicalMachine' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingMultiplePhysicalMachines';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingMultiplePhysicalMachines' WHERE name='client.wiki.infra.createMultiplePhysicalMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.network.publicVlan' AND value='http://community.abiquo.com/display/ABI18/Manage+Network+Configuration#ManageNetworkConfiguration-ManagePublicVLANs';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-CreateVLANsforPublicNetworks' WHERE name='client.wiki.network.publicVlan';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.storage.storageDevice' AND value='http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-ManagingManagedStorageDevices';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-ManagedStorage' WHERE name='client.wiki.storage.storageDevice';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.storage.storagePool' AND value='http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-StoragePools';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-StoragePools' WHERE name='client.wiki.storage.storagePool';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.storage.tier' AND value='http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-TierManagement';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-TierManagement' WHERE name='client.wiki.storage.tier';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.allocation.global' AND value='http://community.abiquo.com/display/ABI18/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement' WHERE name='client.wiki.allocation.global';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.allocation.datacenter' AND value='http://community.abiquo.com/display/ABI18/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement' WHERE name='client.wiki.allocation.datacenter';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createVdc' AND value='http://community.abiquo.com/display/ABI18/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter' WHERE name='client.wiki.vdc.createVdc';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createVapp' AND value='http://community.abiquo.com/display/ABI18/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance' WHERE name='client.wiki.vdc.createVapp';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createPrivateNetwork' AND value='http://community.abiquo.com/display/ABI18/Manage+Networks#ManageNetworks-PrivateIPAddresses';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-CreateaPrivateVLAN' WHERE name='client.wiki.vdc.createPrivateNetwork';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createPublicNetwork' AND value='http://community.abiquo.com/display/ABI18/Manage+Networks#ManageNetworks-PublicIPReservation';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-PublicIPReservation' WHERE name='client.wiki.vdc.createPublicNetwork';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createVolume' AND value='http://community.abiquo.com/display/ABI18/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage' WHERE name='client.wiki.vdc.createVolume';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vdc.createVolume' AND value='http://community.abiquo.com/display/ABI18/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage' WHERE name='client.wiki.vdc.createVolume';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.editVirtualMachine' AND value='http://community.abiquo.com/display/ABI18/Configure+Virtual+Machines';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines' WHERE name='client.wiki.vm.editVirtualMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.bundleVirtualMachine' AND value='http://community.abiquo.com/display/ABI18/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-Configure';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-CreateanInstance' WHERE name='client.wiki.vm.bundleVirtualMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.createNetworkInterface' AND value='http://community.abiquo.com/display/ABI18/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface' WHERE name='client.wiki.vm.createNetworkInterface';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.createInstance' AND value='http://community.abiquo.com/display/ABI18/Create+Virtual+Machine+instances';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Create+Virtual+Machine+instances' WHERE name='client.wiki.vm.createInstance';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.createStateful' AND value='http://community.abiquo.com/display/ABI18/Create+Persistent+Virtual+Machines';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Create+Persistent+Virtual+Machines' WHERE name='client.wiki.vm.createStateful';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.vm.captureVirtualMachine' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-ImportaRetrievedVirtualMachine';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-WorkingwithImportedVirtualMachines' WHERE name='client.wiki.vm.captureVirtualMachine';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.apps.uploadVM' AND value='http://community.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem' WHERE name='client.wiki.apps.uploadVM';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.user.createEnterprise' AND value='http://community.abiquo.com/display/ABI18/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise' WHERE name='client.wiki.user.createEnterprise';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.user.dataCenterLimits' AND value='http://community.abiquo.com/display/ABI18/Manage+Enterprises#ManageEnterprises-RestrictingDatacenterAccess';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-EdittheEnterprise%27sDatacenters' WHERE name='client.wiki.user.dataCenterLimits';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.user.createUser' AND value='http://community.abiquo.com/display/ABI18/Manage+Users#ManageUsers-CreatingorEditingaUser';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Users#ManageUsers-CreatingorEditingaUser' WHERE name='client.wiki.user.createUser';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.user.createRole' AND value='http://community.abiquo.com/display/ABI18/Manage+Roles+and+Privileges';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Roles+and+Privileges' WHERE name='client.wiki.user.createRole';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.config.general' AND value='http://community.abiquo.com/display/ABI18/Configuration+view';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configuration+view' WHERE name='client.wiki.config.general';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.config.heartbeat' AND value='http://community.abiquo.com/display/ABI18/Configuration+view#Configurationview-Heartbeating';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-Heartbeating' WHERE name='client.wiki.config.heartbeat';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.config.licence' AND value='http://community.abiquo.com/display/ABI18/Configuration+view#Configurationview-Licensemanagement';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-LicenseManagement' WHERE name='client.wiki.config.licence';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.config.registration' AND value='http://community.abiquo.com/display/ABI18/Configuration+view#Configurationview-ProductRegistration';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Configuration+view#Configurationview-ProductRegistration' WHERE name='client.wiki.config.registration';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.wiki.infra.discoverBlades' AND value='http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-DiscoveringBladesonManagedRacks';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DiscoveringBladesonManagedRacks' WHERE name='client.wiki.infra.discoverBlades';
	END IF;
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.virtual.moreInfoAboutUploadLimitations' AND value='http://community.abicloud.org/display/ABI16/Appliance+Library+view#ApplianceLibraryview-Uploadingfromourlocalfilesystem';
	IF @existsCount = 1 THEN 
		UPDATE kinton.system_properties SET value='http://wiki.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem' WHERE name='client.virtual.moreInfoAboutUploadLimitations';
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
	SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='50';
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

	-- new virtualmachine states
	update kinton.virtualmachine set state = "NOT_ALLOCATED" where state = "NOT_DEPLOYED";
	update kinton.virtualmachine set state = "ON" where state = "RUNNING";
	update kinton.virtualmachine set state = "OFF" where state = "POWERED_OFF";
	update kinton.virtualmachine set state = "UNKNOWN" where state = "CRASHED";
	update kinton.virtualmachine set state = "UNKNOWN" where state = "IN_PROGRESS";

	-- iconURL migration
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='idIcon') THEN
		update virtualimage vi, icon i set vi.iconURL = i.path where vi.idIcon = i.idIcon; 
	END IF;

	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ovf_package' AND column_name='idIcon') THEN
		update ovf_package ovf, icon i set ovf.iconURL = i.path where ovf.idIcon = i.idIcon;
	END IF;

	-- costCode
	update virtualimage set cost_code = 0 where cost_code is null;

	-- Enable HeartBeat by default
	UPDATE alerts al SET al.value='YES' where al.type='HEARTBEAT';

        -- Assure the right DC information
        UPDATE datacenter set uuid = null;
	update remote_service r set uri = CONCAT((select REPLACE(REPLACE(r.uri, RIGHT(r.uri, LENGTH(r.uri) - LOCATE(':', r.uri, 5)), '80'), 'tcp', 'http')), '/bpm-async') where r.remoteServiceType = 'BPM_SERVICE';


	-- ######################################## --	
	-- ######## SCHEMA: COLUMNS REMOVED ####### --
	-- ######################################## --
	SELECT "STEP 7 REMOVING DEPRECATED COLUMNS..." as " ";	
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
	--
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='physicalmachine' AND column_name='cpuRatio') THEN
		ALTER TABLE kinton.physicalmachine DROP COLUMN cpuRatio;
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
	-- Columns dropped from virtualimage for urlIcon feature
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='virtualimage' AND column_name='idIcon') THEN
		ALTER TABLE kinton.virtualimage DROP FOREIGN KEY `virtualImage_FK4`;
		ALTER TABLE kinton.virtualimage DROP COLUMN idIcon;
	END IF;
	-- Columns dropped from ovf_package for urlIcon feature
	IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema= 'kinton' AND table_name='ovf_package' AND column_name='idIcon') THEN
		ALTER TABLE kinton.ovf_package DROP FOREIGN KEY `fk_ovf_package_icon`;
		ALTER TABLE kinton.ovf_package DROP COLUMN idIcon;
	END IF;

	-- ######################################## --	
	-- ######## SCHEMA: TABLES REMOVED ######## --
	-- ######################################## --
	--
	-- Definition of table kinton.dhcp_service
	--	
	SELECT "STEP 8 REMOVING DEPRECATED TABLES..." as " ";	
	IF EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='dhcp_service') THEN
		SELECT "Removing table dhcp_service..." as " ";
		DROP  TABLE IF EXISTS kinton.dhcp_service;
	END IF;
	IF EXISTS(SELECT * FROM information_schema.tables WHERE table_schema='kinton' AND table_name='icon') THEN
		SELECT "Removing table icon..." as " ";
		DROP  TABLE IF EXISTS kinton.icon;
	END IF;

END;
|
DELIMITER ;

# Now invoke the SP
CALL kinton.delta_1_8_5_to_2_0();

# And on successful completion, remove the SP, so we are not cluttering the DBMS with upgrade code!
DROP PROCEDURE IF EXISTS kinton.delta_1_8_5_to_2_0;

-- ########################################### --	
-- ######## SCHEMA: TRIGGERS REMOVED ####### --
-- ########################################### --

-- THIS TRIGGERS WILL BE REMOVED
SELECT "STEP 9 REMOVING DEPRECATED TRIGGERS..." as " ";
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.create_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_network_configuration_update_stats;

-- ########################################### --	
-- ######## SCHEMA: TRIGGERS RECREATED ####### --
-- ########################################### --
SELECT "STEP 10 UPDATING TRIGGERS..." as " ";
DROP TRIGGER IF EXISTS kinton.virtualapp_created;
DROP TRIGGER IF EXISTS kinton.update_virtualapp_update_stats;
DROP TRIGGER IF EXISTS kinton.create_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.create_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.update_datastore_update_stats;
DROP TRIGGER IF EXISTS kinton.update_physicalmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.create_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_virtualmachine_update_stats;
DROP TRIGGER IF EXISTS kinton.update_virtualmachine_update_stats;
-- NEW TRIGGER (was removed and it's back)
DROP TRIGGER IF EXISTS kinton.create_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_nodevirtualimage_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.virtualdatacenter_updated;
DROP TRIGGER IF EXISTS kinton.virtualdatacenter_deleted;
DROP TRIGGER IF EXISTS kinton.update_rasd_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_rasd_update_stats;
DROP TRIGGER IF EXISTS kinton.create_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.delete_ip_pool_management_update_stats;
DROP TRIGGER IF EXISTS kinton.update_ip_pool_management_update_stats;

-- Brand new one
DROP TRIGGER IF EXISTS kinton.create_volume_management_update_stats;

DELIMITER |
--
SELECT "Recreating trigger virtualapp_created..." as " ";
CREATE TRIGGER kinton.virtualapp_created AFTER INSERT ON kinton.virtualapp
  FOR EACH ROW BEGIN
    DECLARE vdcNameObj VARCHAR(50) CHARACTER SET utf8;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vdc.name INTO vdcNameObj
      FROM virtualdatacenter vdc
      WHERE NEW.idVirtualDataCenter = vdc.idVirtualDataCenter;
      INSERT IGNORE INTO vapp_enterprise_stats (idVirtualApp, idEnterprise, idVirtualDataCenter, vappName, vdcName) VALUES(NEW.idVirtualApp, NEW.idEnterprise, NEW.idVirtualDataCenter, NEW.name, vdcNameObj);
    END IF;
  END;
|
SELECT "Recreating trigger update_virtualapp_update_stats..." as " ";
CREATE TRIGGER kinton.update_virtualapp_update_stats AFTER UPDATE ON kinton.virtualapp
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
|
SELECT "Recreating trigger create_physicalmachine_update_stats..." as " ";
CREATE TRIGGER kinton.create_physicalmachine_update_stats AFTER INSERT ON kinton.physicalmachine
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF NEW.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
               vCpuUsed=vCpuUsed+NEW.cpuUsed, vMemoryUsed=vMemoryUsed+NEW.ramUsed
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
    IF NEW.idState != 2 THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal = serversTotal+1, 
               vCpuTotal=vCpuTotal+NEW.cpu, vMemoryTotal=vMemoryTotal+NEW.ram
        WHERE idDataCenter = NEW.idDataCenter;
    END IF;
END IF;
END;
|
SELECT "Recreating trigger create_datastore_update_stats..." as " ";
CREATE TRIGGER `kinton`.`create_datastore_update_stats` AFTER INSERT ON `kinton`.`datastore_assignment`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
DECLARE enabled INT UNSIGNED;
DECLARE usedSize BIGINT UNSIGNED;
DECLARE size BIGINT UNSIGNED;
DECLARE datastoreuuid VARCHAR(255);
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm WHERE pm.idPhysicalMachine = NEW.idPhysicalmachine;
SELECT d.enabled, d.usedSize, d.size, d.datastoreUUID INTO enabled, usedSize, size, datastoreuuid FROM datastore d WHERE d.idDatastore = NEW.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
        LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
        WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = datastoreuuid AND d.idDatastore != NEW.idDatastore
        AND d.enabled = 1) = 0 THEN
        IF machineState = 3 THEN
            IF enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed + usedSize
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
        IF machineState != 2 THEN
            IF enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + size
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
    END IF;
END IF;
END;
|
SELECT "Recreating trigger delete_physicalmachine_update_stats..." as " ";
CREATE TRIGGER kinton.delete_physicalmachine_update_stats AFTER DELETE ON kinton.physicalmachine
FOR EACH ROW BEGIN
DECLARE datastoreUsedSize BIGINT UNSIGNED;
DECLARE datastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
        UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
               vCpuUsed=vCpuUsed-OLD.cpuUsed, vMemoryUsed=vMemoryUsed-OLD.ramUsed
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
        UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
               vCpuTotal=vCpuTotal-OLD.cpu, vMemoryTotal=vMemoryTotal-OLD.ram
        WHERE idDataCenter = OLD.idDataCenter;
    END IF;
END IF;
END;
|
SELECT "Recreating trigger delete_datastore_update_stats..." as " ";
CREATE TRIGGER `kinton`.`delete_datastore_update_stats` BEFORE DELETE ON `kinton`.`datastore`
FOR EACH ROW BEGIN
DECLARE machineState INT UNSIGNED;
DECLARE idDatacenter INT UNSIGNED;
SELECT pm.idState, pm.idDatacenter INTO machineState, idDatacenter FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = OLD.idDatastore;
IF (@DISABLED_STATS_TRIGGERS IS NULL) THEN
    IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
        LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
        WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = OLD.datastoreuuid AND d.idDatastore != OLD.idDatastore
        AND d.enabled = 1) = 0 THEN
        IF machineState = 3 THEN
            IF OLD.enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
        IF machineState NOT IN (2, 6, 7) THEN
            IF OLD.enabled = 1 THEN
                UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
                WHERE cus.idDataCenter = idDatacenter;
            END IF;
        END IF;
    END IF;
END IF;
END;
|
SELECT "Recreating trigger update_datastore_update_stats..." as " ";
CREATE TRIGGER `kinton`.`update_datastore_update_stats` AFTER UPDATE ON `kinton`.`datastore`
    FOR EACH ROW BEGIN
DECLARE idDatacenter INT UNSIGNED;
DECLARE machineState INT UNSIGNED;
SELECT pm.idDatacenter, pm.idState INTO idDatacenter, machineState FROM physicalmachine pm LEFT OUTER JOIN datastore_assignment da ON pm.idPhysicalMachine = da.idPhysicalMachine
WHERE da.idDatastore = NEW.idDatastore;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
            IF (SELECT count(*) FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
                LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
                WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = NEW.datastoreUUID AND d.idDatastore != NEW.idDatastore 
                AND d.enabled = 1) = 0 THEN
       IF OLD.enabled = 1 THEN
   IF NEW.enabled = 1 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size,
           cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
           ELSEIF NEW.enabled = 0 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size,
           cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
   END IF;
       ELSE
   IF NEW.enabled = 1 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size,
           cus.vStorageUsed = cus.vStorageUsed + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
   END IF;
       END IF;
            ELSEIF NEW.usedSize NOT IN (SELECT d.usedSize FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
                LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
                WHERE pm.idDatacenter = idDatacenter AND d.datastoreUUID = NEW.datastoreUUID AND d.idDatastore != NEW.idDatastore 
                AND d.enabled = 1) THEN
                -- repeated code to update only the first shared datastore
       IF OLD.enabled = 1 THEN
   IF NEW.enabled = 1 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size + NEW.size,
           cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
           ELSEIF NEW.enabled = 0 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal - OLD.size,
           cus.vStorageUsed = cus.vStorageUsed - OLD.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
   END IF;
       ELSE
   IF NEW.enabled = 1 THEN
       IF machineState IN (2, 6, 7) THEN
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size
           WHERE cus.idDatacenter = idDatacenter;
       ELSE
           UPDATE IGNORE cloud_usage_stats cus SET cus.vStorageTotal = cus.vStorageTotal + NEW.size,
           cus.vStorageUsed = cus.vStorageUsed + NEW.usedSize WHERE cus.idDatacenter = idDatacenter;
       END IF;
   END IF;
       END IF;
   END IF;
        END IF;
    END;
|
SELECT "Recreating trigger update_physicalmachine_update_stats..." as " ";
CREATE TRIGGER kinton.update_physicalmachine_update_stats AFTER UPDATE ON kinton.physicalmachine
FOR EACH ROW BEGIN
DECLARE datastoreSize BIGINT UNSIGNED;
DECLARE oldDatastoreSize BIGINT UNSIGNED;
IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
            -- Machine not managed changes into managed; or disabled_by_ha to Managed
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1,
                   vCpuTotal=vCpuTotal + NEW.cpu,
                   vMemoryTotal=vMemoryTotal + NEW.ram
            WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
            -- Machine managed changes into not managed or DisabledByHA
            UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1,
                   vCpuTotal=vCpuTotal-OLD.cpu,
                   vMemoryTotal=vMemoryTotal-OLD.ram
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
            -- Stopped / Halted / Not provisioned passes to Managed (Running)
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1,
                   vCpuUsed=vCpuUsed+NEW.cpuUsed,
                   vMemoryUsed=vMemoryUsed+NEW.ramUsed
            WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
            -- Managed (Running) passes to Stopped / Halted / Not provisioned
            UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1,
                   vCpuUsed=vCpuUsed-OLD.cpuUsed,
                   vMemoryUsed=vMemoryUsed-OLD.ramUsed
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    ELSE
        -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
            -- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
            UPDATE IGNORE cloud_usage_stats SET vCpuTotal=vCpuTotal+(NEW.cpu-OLD.cpu),
                   vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
            UPDATE IGNORE cloud_usage_stats SET vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
                   vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed)
            WHERE idDataCenter = OLD.idDataCenter;
        END IF;
    END IF;
END IF;
END;
|
SELECT "Recreating trigger create_virtualmachine_update_stats..." as " ";
CREATE TRIGGER kinton.create_virtualmachine_update_stats AFTER INSERT ON kinton.virtualmachine
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		INSERT INTO virtualmachinetrackedstate (idVM) VALUES (NEW.idVM);
	END IF;
    END;
|
SELECT "Recreating trigger delete_virtualmachine_update_stats..." as " ";
CREATE TRIGGER kinton.delete_virtualmachine_update_stats AFTER DELETE ON kinton.virtualmachine
    FOR EACH ROW BEGIN
	IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
		DELETE FROM virtualmachinetrackedstate WHERE idVM = OLD.idVM;
	END IF;
    END;
|
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
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage) THEN 
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
	DECLARE extraHDSize BIGINT DEFAULT 0;
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
	-- -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', NEW.idVM, ' - ', OLD.idType, ' - ', NEW.idType, ' - ', OLD.state, ' - ', NEW.state, ' - ', previousState));	
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
-- -- INSERT INTO debug_msg (msg) VALUES (CONCAT('update values ', IFNULL(idDataCenterObj,'NULL'), ' - ',IFNULL(idVirtualAppObj,'NULL'), ' - ',IFNULL(idVirtualDataCenterObj,'NULL'), ' - ',IFNULL(previousState,'NULL')));
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
		SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
		FROM rasd_management rm, rasd r 
		WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs added ', extraHDSize));
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd + extraHDSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;		
	    ELSEIF (NEW.state IN ("PAUSED","OFF","NOT_ALLOCATED") AND previousState = "ON") THEN
                -- When Undeploying a full Vapp
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
                WHERE idDataCenter = idDataCenterObj;
		SELECT IFNULL(SUM(limitResource),0) * 1048576 INTO extraHDSize 
		FROM rasd_management rm, rasd r 
		WHERE rm.idResource = r.instanceID AND rm.idVM = NEW.idVM AND rm.idResourceType=17;    
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('NEW ExtraHDs removed ', extraHDSize));
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed - NEW.cpu,
                        memoryUsed = memoryUsed - NEW.ram,
                        localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd - extraHDSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 		
            END IF;
        END IF;
        --
        SELECT IF(vi.cost_code IS NULL, 0, vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage) THEN 
	          CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, previousState, NEW.ram, NEW.cpu, NEW.hd + extraHDSize, costCodeObj);
       		 END IF;              
	    END IF;
      END IF;
    END;
|
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
      IF previousState != "NOT_ALLOCATED" OR previousState != "UNKNOWN" THEN      
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
       IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=OLD.idImage) THEN 
	          CALL AccountingVMRegisterEvents(OLD.idVM, type, "-", "NOT_ALLOCATED", previousState, ram, cpu, hd, costCodeObj);
        END IF;              
     END IF;
  END IF;
  END;
--
|
--
SELECT "Recreating trigger delete_rasd_management_update_stats..." as " ";
CREATE TRIGGER kinton.delete_rasd_management_update_stats AFTER DELETE ON kinton.rasd_management
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;    
        DECLARE resourceName VARCHAR(255);  
	DECLARE currentState VARCHAR(50);
	DECLARE previousState VARCHAR(50);
	DECLARE extraHDSize BIGINT DEFAULT 0;
	SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
	SELECT vm.state INTO currentState
        FROM virtualmachine vm
        WHERE vm.idVM = OLD.idVM;
	SELECT vmts.previousState INTO previousState
        FROM virtualmachinetrackedstate vmts
	WHERE vmts.idVM = OLD.idVM;
        SELECT elementName, limitResource INTO resourceName, limitResourceObj
        FROM rasd r
        WHERE r.instanceID = OLD.idResource;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF OLD.idResourceType='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal-limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('DELETE_STORAGE', OLD.idResource, resourceName, 0, OLD.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
                END IF;                  
            END IF;
            IF OLD.idResourceType='17' AND previousState = 'ON' THEN
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Removed ExtraHDs ', limitResourceObj, ' for idVM ', OLD.idVM, ' with state ', previousState));  
		SELECT limitResourceObj * 1048576 INTO extraHDSize;
		UPDATE IGNORE enterprise_resources_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize 
                WHERE idEnterprise = idThisEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize
                WHERE idEnterprise = idThisEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET localStorageUsed = localStorageUsed - extraHDSize
                WHERE idVirtualDataCenter = OLD.idVirtualDataCenter; 		
	    END IF;
        END IF;
    END;      
|
SELECT "Recreating trigger virtualdatacenter_updated..." as " ";
CREATE TRIGGER kinton.virtualdatacenter_updated AFTER UPDATE ON kinton.virtualdatacenter
    FOR EACH ROW BEGIN
    DECLARE vlanNetworkIdObj INTEGER;    
        	  DECLARE networkNameObj VARCHAR(40) CHARACTER SET utf8;
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
			IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
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
SELECT "Recreating trigger virtualdatacenter_deleted..." as " ";
CREATE TRIGGER kinton.virtualdatacenter_deleted BEFORE DELETE ON kinton.virtualdatacenter
    FOR EACH ROW BEGIN
    DECLARE currentIdManagement INTEGER DEFAULT -1;
    DECLARE currentDataCenter INTEGER DEFAULT -1;
    DECLARE currentIpAddress VARCHAR(20) CHARACTER SET utf8 DEFAULT '';
    DECLARE no_more_ipsfreed INT;
    DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement   
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.vlan_network_id = vn.vlan_network_id
           AND vn.network_configuration_id = nc.network_configuration_id
           AND vn.network_id = dc.network_id
       AND vn.networktype = 'PUBLIC'
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
--      INSERT INTO debug_msg (msg) VALUES (CONCAT('IP_FREED: ',currentIpAddress, ' - idManagement: ', currentIdManagement, ' - OLD.idVirtualDataCenter: ', OLD.idVirtualDataCenter, ' - idEnterpriseObj: ', OLD.idEnterprise));
        -- We reset MAC and NAME for the reserved IPs. Java code should do this!
        UPDATE ip_pool_management set mac=NULL, name=NULL WHERE idManagement = currentIdManagement;
        IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                    CALL AccountingIPsRegisterEvents('IP_FREED',currentIdManagement,currentIpAddress,OLD.idVirtualDataCenter, OLD.idEnterprise);
            END IF;                    
        UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = currentDataCenter;
        UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = OLD.idEnterprise; 
        END WHILE my_loop;         
        CLOSE curIpFreed;
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
	DECLARE type INTEGER;
	DECLARE currentVMState VARCHAR(50);
	DECLARE extraHDSize BIGINT;
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
			    IF idState = 1 THEN
				SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
				FROM virtualdatacenter vdc
				WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
				SELECT r.limitResource INTO reservedSize
				FROM rasd r
				WHERE r.instanceID = OLD.idResource;
				-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));	
				UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
				UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
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
		-- Added ExtraHD for Imported VM
		-- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idEnterpriseObj
                FROM virtualdatacenter vdc
                WHERE vdc.idVirtualDatacenter = NEW.idVirtualDataCenter;
		SELECT vm.state, vm.idType INTO currentVMState, type
		FROM virtualmachine vm
		WHERE vm.idVM = NEW.idVM;
		SELECT IFNULL(r.limitResource,0) * 1048576 INTO extraHDSize
		FROM rasd r
		WHERE NEW.idResourceType=17 AND r.instanceID = NEW.idResource;
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Added ExtraHDs UpdateRASDMana ', IFNULL(extraHDSize,'NULL'), ' for idVM ', IFNULL(NEW.idVM,'NULL'), ' with state ', IFNULL(currentVMState,'NULL'), ' type ', IFNULL(type,'NULL')));  
		IF extraHDSize IS NOT NULL  AND currentVMState = 'ON' THEN -- this is an imported machine
		UPDATE IGNORE enterprise_resources_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idEnterprise = idEnterpriseObj AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET localStorageUsed = localStorageUsed + extraHDSize
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter; 
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('INSERTED ExtraHDs stats ', IFNULL(extraHDSize,'NULL'), ' for idEnterpriseObj ', IFNULL(idEnterpriseObj,'NULL'), ' with idDataCenterObj ', IFNULL(idDataCenterObj,'NULL'), ' and NEW.idVirtualDataCenter ', IFNULL(NEW.idVirtualDataCenter,'NULL')));	
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
SELECT "Recreating trigger update_rasd_update_stats..." as " ";
CREATE TRIGGER kinton.update_rasd_update_stats AFTER UPDATE ON kinton.rasd
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
                IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('UPDATE_STORAGE', NEW.instanceID, NEW.elementName, 0, idThisVirtualDataCenter, idThisEnterprise, NEW.limitResource);
                END IF;
            END IF;
        END IF;
    END;    
|
SELECT "Recreating trigger create_ip_pool_management_update_stats..." as " ";
CREATE TRIGGER kinton.create_ip_pool_management_update_stats AFTER INSERT ON kinton.ip_pool_management
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT dc.idDataCenter INTO idDataCenterObj
	FROM rasd_management rm, vlan_network vn, network_configuration nc, datacenter dc
	WHERE NEW.vlan_network_id = vn.vlan_network_id
	AND vn.networktype = 'PUBLIC'
	AND vn.network_configuration_id = nc.network_configuration_id
	AND dc.network_id = vn.network_id
	AND NEW.idManagement = rm.idManagement;
      IF idDataCenterObj IS NOT NULL THEN
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('create_ip_pool_management_update_stats +1 ', IFNULL(idDataCenterObj,'NULL')));
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal+1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|
SELECT "Recreating trigger delete_ip_pool_management_update_stats..." as " ";
CREATE TRIGGER kinton.delete_ip_pool_management_update_stats AFTER DELETE ON kinton.ip_pool_management
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      -- Query for Public Ips deleted (disabled)
      SELECT dc.idDataCenter INTO idDataCenterObj
	FROM rasd_management rm, vlan_network vn, network_configuration nc, datacenter dc
	WHERE OLD.vlan_network_id = vn.vlan_network_id
	AND vn.networktype = 'PUBLIC'
	AND vn.network_configuration_id = nc.network_configuration_id
	AND dc.network_id = vn.network_id
	AND OLD.idManagement = rm.idManagement;
      IF idDataCenterObj IS NOT NULL THEN
    -- detects IP disabled/enabled at Edit Public Ips
   	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('delete_ip_pool_management_update_stats -1 ', IFNULL(idDataCenterObj,'NULL')));
        UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
      END IF;
    END IF;
  END;
|
SELECT "Recreating trigger update_ip_pool_management_update_stats..." as " ";
CREATE TRIGGER kinton.update_ip_pool_management_update_stats AFTER UPDATE ON kinton.ip_pool_management
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
	   DECLARE networkTypeObj VARCHAR(15);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
		SELECT vn.networktype, dc.idDataCenter INTO networkTypeObj, idDataCenterObj
		FROM vlan_network vn, datacenter dc
		WHERE dc.network_id = vn.network_id
		AND OLD.vlan_network_id = vn.vlan_network_id;
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('update_ip_pool_management_update_stats', '-', OLD.ip, '-',OLD.available,'-', NEW.available,'-', IFNULL(networkTypeObj,'NULL'), '-', IFNULL(idDataCenterObj,'NULL')));
		IF networkTypeObj = 'PUBLIC' THEN		
			IF OLD.available=FALSE AND NEW.available=TRUE THEN
				UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal+1 WHERE idDataCenter = idDataCenterObj;
			END IF;
			IF OLD.available=TRUE AND NEW.available=FALSE THEN
				UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-1 WHERE idDataCenter = idDataCenterObj;
			END IF;
		END IF;
	    -- Checks for public available 
            -- Checks for reserved IPs		
            IF OLD.mac IS NULL AND NEW.mac IS NOT NULL THEN
                -- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise  INTO idDataCenterObj, idVirtualDataCenterObj, idEnterpriseObj
                FROM rasd_management rm, virtualdatacenter vdc, vlan_network vn
                WHERE vdc.idVirtualDataCenter = rm.idVirtualDataCenter
		AND NEW.vlan_network_id = vn.vlan_network_id
		AND vn.networktype = 'PUBLIC'
		AND NEW.idManagement = rm.idManagement;
                -- New Public IP assignment for a VDC ---> Reserved
                UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed+1 WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idDataCenter = idDataCenterObj;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                    CALL AccountingIPsRegisterEvents('IP_RESERVED',NEW.idManagement,NEW.ip,idVirtualDataCenterObj, idEnterpriseObj);
                END IF;
            END IF;
        END IF;
    END;
|
SELECT "Recreating trigger create_volume_management_update_stats..." as " ";
CREATE TRIGGER kinton.create_volume_management_update_stats AFTER INSERT ON kinton.volume_management
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;
        DECLARE idResourceObj VARCHAR(50);
        DECLARE idResourceTypeObj VARCHAR(5);
	DECLARE idStorageTier INTEGER;
        DECLARE resourceName VARCHAR(255);
        SELECT vdc.idDataCenter, vdc.idEnterprise, vdc.idVirtualDataCenter INTO idDataCenterObj, idThisEnterprise, idVirtualDataCenterObj
        FROM virtualdatacenter vdc, rasd_management rm
        WHERE vdc.idVirtualDataCenter = rm.idVirtualDataCenter
        AND NEW.idManagement = rm.idManagement;
        --
        SELECT r.elementName, r.limitResource, rm.idResource, rm.idResourceType INTO resourceName, limitResourceObj, idResourceObj, idResourceTypeObj
        FROM rasd r, rasd_management rm
        WHERE r.instanceID = rm.idResource
        AND NEW.idManagement = rm.idManagement;
        --
        SELECT sp.idTier INTO idStorageTier
        FROM storage_pool sp
        WHERE sp.idStorage = NEW.idStorage;
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idDataCenterObj ',IFNULL(idDataCenterObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL limitResourceObj ',IFNULL(limitResourceObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idResourceObj ',IFNULL(idResourceObj,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL idStorageTier ',IFNULL(idStorageTier,'-')));
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('Create VOL resourceName: ',IFNULL(resourceName,'-')));
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF idResourceTypeObj='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                IF EXISTS( SELECT * FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('CREATE_STORAGE', idResourceObj, resourceName, idStorageTier, idVirtualDataCenterObj, idThisEnterprise, limitResourceObj);
                END IF;               
            END IF;
        END IF;
    END;
|
DELIMITER ;


-- ############################################# --	
-- ######## SCHEMA: PROCEDURES RECREATED ####### --
-- ############################################# --
SELECT "STEP 11 UPDATING PROCEDURES FOR THIS RELEASE..." as " ";
DROP PROCEDURE IF EXISTS kinton.CalculateCloudUsageStats;
DROP PROCEDURE IF EXISTS kinton.CalculateEnterpriseResourcesStats;
DROP PROCEDURE IF EXISTS kinton.CalculateVappEnterpriseStats;
DROP PROCEDURE IF EXISTS kinton.CalculateVdcEnterpriseStats;

--  New Procedures to calculate datastore size
DROP PROCEDURE IF EXISTS kinton.get_datastore_size_by_dc;
DROP PROCEDURE IF EXISTS kinton.get_datastore_used_size_by_dc;

DELIMITER |

SELECT "Recreating PROCEDURE CalculateCloudUsageStats..." as " ";
CREATE PROCEDURE kinton.CalculateCloudUsageStats()
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
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN' 
    and v.idType = 1;
    --
    SELECT  IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vMachinesRunning
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp, virtualdatacenter vdc
    WHERE v.idVM = nvi.idVM
    AND n.idNode=nvi.idNode
    AND vapp.idVirtualApp = n.idVirtualApp
    AND vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
    AND vdc.idDataCenter = idDataCenterObj
    AND v.state = 'ON'
    and v.idType = 1;
    --
    SELECT IF (SUM(cpu) IS NULL,0,SUM(cpu)), IF (SUM(ram) IS NULL,0,SUM(ram)), IF (SUM(cpuUsed) IS NULL,0,SUM(cpuUsed)), IF (SUM(ramUsed) IS NULL,0,SUM(ramUsed)) INTO vCpuTotal, vMemoryTotal, vCpuUsed, vMemoryUsed
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
SELECT "Recreating PROCEDURE CalculateEnterpriseResourcesStats..." as " ";
CREATE PROCEDURE kinton.CalculateEnterpriseResourcesStats()
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
    WHERE vm.state = 'ON'
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
SELECT "Recreating PROCEDURE CalculateVdcEnterpriseStats..." as " ";
CREATE PROCEDURE kinton.CalculateVdcEnterpriseStats()
   BEGIN
  DECLARE idVirtualDataCenterObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE vdcName VARCHAR(45) CHARACTER SET utf8;
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
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN';
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualDataCenter = idVirtualDataCenterObj
    AND v.state = 'ON';
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
    AND vm.state = 'ON'
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
-- CREATE PROCEDURE kinton.CalculateDcEnterpriseStats()
--   BEGIN
--   END;
--
--
SELECT "Recreating PROCEDURE CalculateVappEnterpriseStats..." as " ";
CREATE PROCEDURE kinton.CalculateVappEnterpriseStats()
   BEGIN
  DECLARE idVirtualAppObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE idVirtualDataCenter INTEGER;
  DECLARE vappName VARCHAR(45) CHARACTER SET utf8;
  DECLARE vdcName VARCHAR(45) CHARACTER SET utf8;
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
    AND v.state != 'NOT_ALLOCATED' AND v.state != 'UNKNOWN'
    and v.idType = 1;
    --
    SELECT IF (COUNT(*) IS NULL, 0, COUNT(*)) INTO vmActive
    FROM nodevirtualimage nvi, virtualmachine v, node n, virtualapp vapp
    WHERE nvi.idNode IS NOT NULL
    AND v.idVM = nvi.idVM
    AND n.idNode = nvi.idNode
    AND n.idVirtualApp = vapp.idVirtualApp
    AND vapp.idVirtualApp = idVirtualAppObj
    AND v.state = 'ON'
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
--
|
SELECT "Recreating PROCEDURE get_datastore_size_by_dc..." as " ";
CREATE PROCEDURE kinton.get_datastore_size_by_dc(IN idDC INT, OUT size BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.size) IS NULL,0,SUM(ds_view.size)) INTO size
    FROM (SELECT d.size as size FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore 
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END;
--
|
--
SELECT "Recreating PROCEDURE get_datastore_used_size_by_dc..." as " ";
CREATE PROCEDURE kinton.get_datastore_used_size_by_dc(IN idDC INT, OUT usedSize BIGINT UNSIGNED)
BEGIN
    SELECT IF (SUM(ds_view.usedSize) IS NULL,0,SUM(ds_view.usedSize)) INTO usedSize
    FROM (SELECT d.usedSize as usedSize FROM datastore d LEFT OUTER JOIN datastore_assignment da ON d.idDatastore = da.idDatastore
    LEFT OUTER JOIN physicalmachine pm ON da.idPhysicalMachine = pm.idPhysicalMachine
    WHERE pm.idDataCenter = idDC AND d.enabled = 1 GROUP BY d.datastoreUuid) ds_view;
END;
--
|
--
DELIMITER ;

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

SELECT "STEP 12 ENABLING TRIGGERS" as " ";
SET @DISABLE_STATS_TRIGGERS = null;
SELECT "#### UPGRADE COMPLETED ####" as " ";
