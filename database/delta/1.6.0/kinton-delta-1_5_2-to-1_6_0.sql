--
-- REMOTE SERVICES MIGRATION
--
ALTER TABLE remote_service ADD remoteServiceType varchar(50) NOT NULL;
ALTER TABLE remote_service DROP FOREIGN KEY idRemoteServiceType_FK1;
ALTER TABLE remote_service DROP COLUMN idRemoteServiceType;

DROP TABLE remote_service_type;

--
-- APPLIANCE MANAGER MIGRATION
--

-- -----------------------------------------------------
-- Table `kinton`.`apps_library`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`apps_library` (
  `id_apps_library` INT UNSIGNED NOT NULL ,
  `idEnterprise` INT UNSIGNED NOT NULL , 
  PRIMARY KEY (`id_apps_library`),
  CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY ( `idEnterprise` ) REFERENCES `enterprise` ( `idEnterprise` ) ON DELETE CASCADE 
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `kinton`.`ovf_package_list`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package_list` (
  `id_ovf_package_list` INT NOT NULL ,
  `name` VARCHAR(45) NULL ,
  `id_apps_library` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`id_ovf_package_list`) ,
  CONSTRAINT `fk_ovf_package_list_repository`
    FOREIGN KEY (`id_apps_library`)
    REFERENCES `kinton`.`apps_library` (`id_apps_library`)
    ON DELETE NO ACTION   
    ON UPDATE NO ACTION
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `kinton`.`ovf_package`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package` (
  `id_ovf_package` INT NOT NULL ,
  `id_apps_library` INT UNSIGNED NOT NULL ,
  `url` VARCHAR(45) NULL NOT NULL,
  `name` VARCHAR(45) NULL ,
  `description` VARCHAR(45) NULL , 
  `productName` VARCHAR(45) NULL ,
  `productUrl` VARCHAR(45) NULL ,
  `productVersion` VARCHAR(45) NULL ,
  `productVendor` VARCHAR(45) NULL ,
 
   `idCategory` int(3) unsigned NOT NULL default 1,
   `idIcon` int(4) unsigned default NULL,
   `idFormat` int(10) unsigned NOT NULL,
 
  PRIMARY KEY (`id_ovf_package`),
  CONSTRAINT `fk_ovf_package_repository`
    FOREIGN KEY (`id_apps_library`)
    REFERENCES `kinton`.`apps_library` (`id_apps_library`)
    ON DELETE NO ACTION,   
    -- ON UPDATE NO ACTION   
    CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `category` (`idCategory`),     
      CONSTRAINT `fk_ovf_package_icon`      FOREIGN KEY (`idIcon`)     REFERENCES `icon` (`idIcon`) ON DELETE SET NULL,
      CONSTRAINT `fk_ovf_package_format`      FOREIGN KEY (`idFormat`)     REFERENCES `disk_format_type` (`id`)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `kinton`.`ovf_package_list_has_ovf_package`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `kinton`.`ovf_package_list_has_ovf_package` (
  `id_ovf_package_list` INT NOT NULL ,
  `id_ovf_package` INT NOT NULL ,
  PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`) ,
  INDEX `fk_ovf_package_list_has_ovf_package_ovf_package_list1` (`id_ovf_package_list` ASC) ,
  INDEX `fk_ovf_package_list_has_ovf_package_ovf_package1` (`id_ovf_package` ASC) ,
  CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1`
    FOREIGN KEY (`id_ovf_package_list` )
    REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1`
    FOREIGN KEY (`id_ovf_package` )
    REFERENCES `kinton`.`ovf_package` (`id_ovf_package` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- NETWORKING MIGRATION
-- Steps:
--   1. Creation of the tables: 
--      * network
--      * dhcp_service
--      * network_configuration
--      * vlan_network
--      * vlan_network_assignment
--   2. Migration of data.
--   3. Alter contraints.
--   4. Delete the old tables:
--      * hosttype
--      * dhcptype
--      * networktype
--      * bridgetype
--      * forwardtype
--      * rangetype

--
-- 1. CREATION OF THE TABLES
--

--
-- Definition of table `kinton`.`network`
--
CREATE TABLE  `kinton`.`network` (
  `network_id` int(11) unsigned NOT NULL auto_increment,  
  `uuid` varchar(40) NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`network_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Definition of table `kinton`.`dhcp_service`
--
CREATE TABLE `kinton`.`dhcp_service` (
  `dhcp_service_id` int(11) unsigned NOT NULL auto_increment,
  `dhcp_remote_service` int(10) unsigned,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY (`dhcp_service_id`),
  KEY `dhcp_remote_service_FK` (`dhcp_remote_service`),
  CONSTRAINT `dhcp_remote_service_FK` FOREIGN KEY (`dhcp_remote_service`) REFERENCES `remote_service` (`idRemoteService`) ON DELETE SET NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`network_configuration`
--
CREATE TABLE `kinton`.`network_configuration` (
  `network_configuration_id` int(11) unsigned NOT NULL auto_increment,
  `dhcp_service_id` int(11) unsigned,
  `gateway` varchar(40),
  `network_address` varchar(40) NOT NULL,
  `mask` int(4) NOT NULL,
  `netmask` varchar(20) NOT NULL,
  `primary_dns` varchar(20),
  `secondary_dns` varchar(20),
  `sufix_dns` varchar(40),
  `fence_mode` varchar(20) NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`network_configuration_id`),
  KEY `configuration_dhcp_FK` (`dhcp_service_id`),
  CONSTRAINT `configuration_dhcp_FK` FOREIGN KEY (`dhcp_service_id`) REFERENCES `dhcp_service` (`dhcp_service_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`vlan_network`
--
CREATE TABLE  `kinton`.`vlan_network` (
  `vlan_network_id` int(11) unsigned NOT NULL auto_increment,
  `network_id` int(11) unsigned NOT NULL,
  `network_configuration_id` int(11) unsigned NOT NULL,
  `network_name` varchar(40) NOT NULL,
  `vlan_tag` int(4) unsigned DEFAULT NULL,
  `default_network` boolean NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`vlan_network_id`),
  KEY `vlannetwork_network_FK` (`network_id`),
  KEY `vlannetwork_configuration_FK` (`network_configuration_id`),
  CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `network` (`network_id`) ON DELETE CASCADE,
  CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `network_configuration` (`network_configuration_id`) ON DELETE RESTRICT 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`vlan_network_assignment`
--
CREATE TABLE `kinton`.`vlan_network_assignment` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `vlan_network_id` INTEGER UNSIGNED NOT NULL,
  `idRack` INT(15) UNSIGNED NOT NULL,
  `idVirtualDataCenter` int(10) UNSIGNED default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`id`),
  INDEX `vlan_network_assignment_networkid_FK`(`vlan_network_id`),
  INDEX `vlan_network_assignment_idRack_FK`(`idRack`),
  CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY `vlan_network_assignment_networkid_FK` (`vlan_network_id`)
    REFERENCES `vlan_network` (`vlan_network_id`)    ON DELETE CASCADE,
  CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `virtualdatacenter` (`idVirtualDataCenter`) ON DELETE SET NULL,
  CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY `vlan_network_assignment_idRack_FK` (`idRack`)
    REFERENCES `rack` (`idRack`)
    ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`ip_pool`
--
CREATE TABLE  `kinton`.`ip_pool_management` (
  `idManagement` int(10) unsigned NOT NULL auto_increment,
  `dhcp_service_id` int(11) unsigned NOT NULL,
  `mac` varchar(20),
  `name` varchar(30),
  `ip` varchar(20) NOT NULL,
  `configureGateway` boolean NOT NULL default 0,
  `vlan_network_name` varchar(40),
  `vlan_network_id` int(11) unsigned,
  `quarantine` boolean NOT NULL default 0,
  `version_c` integer NOT NULL DEFAULT 1,
  KEY `id_management_FK` (`idManagement`),
  KEY `ippool_dhcpservice_FK` (`dhcp_service_id`),
  KEY `ippool_vlan_network_FK` (`vlan_network_id`),
--  CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE,
  CONSTRAINT `ippool_dhcpservice_FK` FOREIGN KEY (`dhcp_service_id`) REFERENCES `dhcp_service` (`dhcp_service_id`)  ON DELETE RESTRICT,
  CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `vlan_network` (`vlan_network_id`)  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

ALTER TABLE `rasd_management` MODIFY COLUMN `idResource` varchar(50); 

--
-- 2. MIGRATION OF DATA
--

-- Migrate `kinton`.`network`
INSERT INTO network (network_id, uuid)
SELECT networktypeID, uuid FROM networktype;

-- Migrate `kinton`.`dhcp_service`
INSERT INTO dhcp_service (dhcp_service_id, dhcp_remote_service)
SELECT d.dhcptypeID, rs.idRemoteService
FROM dhcptype d, virtualdatacenter vdc, remote_service rs
WHERE d.networktypeID = vdc.networktypeID
  AND vdc.idDataCenter = rs.idDataCenter
  AND rs.remoteServiceType = "DHCP_SERVICE";
   
-- Migrate `kinton`.`network_configuration`
INSERT INTO network_configuration (network_configuration_id, dhcp_service_id, gateway, network_address, mask, netmask, fence_mode)
SELECT nt.networktypeID, d.dhcptypeID, d.ipgateway, r.first_ip, r.mask, d.netmask, 'bridge' 
  FROM dhcptype d, rangetype r, networktype nt
 WHERE nt.rangetypeID = r.rangetypeID 
   AND d.networktypeID = nt.networktypeID;

   -- Migrate `kinton`.`vlan_network`
INSERT INTO vlan_network (network_id, network_configuration_id, network_name, default_network)
SELECT vdc.networktypeID, vdc.networktypeID, concat(vdc.name, '_network'), 1 from virtualdatacenter vdc;

-- Migrate `kinton`.`ip_pool`.
-- First, all the IP address that is stored by a machine
INSERT INTO ip_pool_management (idManagement, dhcp_service_id, mac, name, ip, configureGateway, vlan_network_name, vlan_network_id)
SELECT rm.idManagement, h.dhcptypeID, h.mac, concat(h.mac,'_name'), h.ip, h.configureGateway, vn.network_name, vn.network_id
  FROM rasd_management rm, vlan_network vn, virtualdatacenter vdc, rasd r, hosttype h 
WHERE rm.idResourceType = 10 
  AND vdc.networktypeID = vn.network_id 
  AND rm.idVirtualDataCenter = vdc.idVirtualDataCenter 
  AND rm.idResource = r.instanceID 
  AND h.mac = r.address;  

-- Insert all the ip_pool_management values that not belong to a machine
INSERT INTO ip_pool_management (dhcp_service_id, mac, name, ip, configureGateway, vlan_network_name, vlan_network_id)
SELECT dt.dhcptypeID, h.mac, concat(h.mac,'_name'), h.ip, h.configureGateway, vn.network_name, vn.network_id
FROM vlan_network vn, dhcptype dt, hosttype h
WHERE dt.networktypeID = vn.vlan_network_id
  AND h.dhcptypeID = dt.dhcptypeID
  AND h.ip NOT IN (select ip from ip_pool_management);
  
-- Insert the registers of rasd_management related to the previous IPPOOLMANAGEMENT inserts
INSERT INTO rasd_management (idManagement, idResourceType, idVirtualDataCenter, idVM, idResource, idVirtualApp)
SELECT im.idManagement, 10, vdc.idVirtualDataCenter, null, null, null
FROM virtualdatacenter vdc, vlan_network vn, ip_pool_management im
WHERE vdc.networktypeID = vn.network_id
  AND im.vlan_network_id = vn.vlan_network_id
  AND im.idManagement NOT IN (SELECT idManagement FROM rasd_management);
  
--
-- 3. MODIFY THE NEW CONSTRAINTS
--
ALTER TABLE `ip_pool_management` MODIFY COLUMN `idManagement` int(10) unsigned NOT NULL;
ALTER TABLE `ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `rasd_management` (`idManagement`) ON DELETE CASCADE;

ALTER TABLE `virtualdatacenter` DROP FOREIGN KEY `virtualDataCenter_FK4`; 
ALTER TABLE `virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `network` (`network_id`);

ALTER TABLE `datacenter` ADD COLUMN `network_id` int(11) unsigned default NULL;
ALTER TABLE `datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `network` (`network_id`);

ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `vswitchName` VARCHAR(30)  NOT NULL AFTER `idState`;

ALTER TABLE `kinton`.`hypervisor` ADD COLUMN `type` VARCHAR(255) NOT NULL;
update hypervisor h set `type` = (select replace(upper(name), '-', '_') from hypervisortype t where t.id = h.idType);

--
-- 4. DROP OLD TABLES
--
-- DROP TABLE IF EXISTS `kinton`.`hosttype`;
-- DROP TABLE IF EXISTS `kinton`.`dhcptype`;
-- DROP TABLE IF EXISTS `kinton`.`networktype`;
-- DROP TABLE IF EXISTS `kinton`.`bridgetype`;
-- DROP TABLE IF EXISTS `kinton`.`forwardtype`;
-- DROP TABLE IF EXISTS `kinton`.`rangetype`;


-- ESXi is not compatible with VMDK SPARSE disk format
delete from `kinton`.hypervisor_disk_compatibilities where idHypervisor = 4 and idFormat = 5;

-- Add version_c to virtualdatacenter
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `version_c` integer NOT NULL DEFAULT 1;

-- Add hypervisorType to virtualdatacenter
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `hypervisorType` varchar(255) NOT NULL;
UPDATE virtualdatacenter v set `hypervisorType` = (select replace(upper(name), '-', '_') from hypervisortype t where t.id = v.idHypervisorType);
