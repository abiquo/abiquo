-- -----------------------------------------------------
-- Add enterprise limits
-- -----------------------------------------------------
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `storageSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `repositorySoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `vlanSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `publicIPSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `storageHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `repositoryHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `vlanHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` ADD COLUMN `publicIPHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `ramSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `cpuSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `hdSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `ramHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `cpuHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`enterprise` MODIFY COLUMN `hdHard` bigint(20) NOT NULL default 0;

UPDATE `kinton`.`enterprise`
SET storageSoft = 0, repositorySoft = 0, vlanSoft = 0, publicIPSoft = 0, 
    storageHard = 0, repositoryHard = 0, vlanHard = 0, publicIPHard = 0;

-- -----------------------------------------------------
-- Add virtual datacenter limits
-- -----------------------------------------------------
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `hdSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `ramSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `cpuSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `storageSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `vlanSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `publicIPSoft` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `hdHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `ramHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `cpuHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `storageHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `vlanHard` bigint(20) NOT NULL default 0;
ALTER TABLE `kinton`.`virtualdatacenter` ADD COLUMN `publicIPHard` bigint(20) NOT NULL default 0;

UPDATE `kinton`.`virtualdatacenter`
SET storageSoft = 0, vlanSoft = 0, publicIPSoft = 0, 
    storageHard = 0, vlanHard = 0, publicIPHard = 0;

-- --------------------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_enterprise_exclusion_rule`
-- --------------------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_enterprise_exclusion_rule`;
CREATE TABLE  `kinton`.`workload_enterprise_exclusion_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idEnterprise1` int(10) unsigned NOT NULL,
  `idEnterprise2` int(10) unsigned NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,  
  PRIMARY KEY (`id`),
  KEY `FK_eerule_enterprise_1` (`idEnterprise1`),
  KEY `FK_eerule_enterprise_2` (`idEnterprise2`),
  CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE,
  CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ------------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_machine_load_rule`
-- ------------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_machine_load_rule`;
CREATE TABLE  `kinton`.`workload_machine_load_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ramLoadPercentage` int(10) unsigned NOT NULL,
  `cpuLoadPercentage` int(10) unsigned NOT NULL,
  `idDatacenter` int(10) unsigned DEFAULT NULL,
  `idRack` int(15) unsigned DEFAULT NULL,
  `idMachine` int(20) unsigned DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,  
  PRIMARY KEY (`id`),
  KEY `FK_mlrule_datacenter` (`idDatacenter`),
  KEY `FK_mlrule_rack` (`idRack`),
  KEY `FK_mlrule_machine` (`idMachine`),
  CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `datacenter` (`idDataCenter`),
  CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `physicalmachine` (`idPhysicalMachine`),
  CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------------------------------------
-- Workload Engine: Table `kinton`.`workload_fit_policy_rule`
-- ----------------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`workload_fit_policy_rule`;
CREATE TABLE  `kinton`.`workload_fit_policy_rule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fitPolicy` varchar(20) NOT NULL,
  `idDatacenter` int(10) unsigned DEFAULT NULL,
  `version_c` integer NOT NULL DEFAULT 1,  
  PRIMARY KEY (`id`),
  KEY `FK_fprule_datacenter` (`idDatacenter`),
  CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `datacenter` (`idDataCenter`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `kinton`.`workload_fit_policy_rule` DISABLE KEYS */;
LOCK TABLES `kinton`.`workload_fit_policy_rule` WRITE;
INSERT INTO `kinton`.`workload_fit_policy_rule` (id,fitPolicy) VALUES (0, 'PROGRESSIVE');
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`workload_fit_policy_rule` ENABLE KEYS */;

-- -----------------------------------------------------
-- Table `kinton`.`enterprise_limits_by_datacenter`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `kinton`.`enterprise_limits_by_datacenter`;
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (
  `idDatacenterLimit` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idEnterprise` int(10) unsigned,
  `idDataCenter` int(10) unsigned,
  `ramSoft` bigint(20) NOT NULL,
  `cpuSoft` bigint(20) NOT NULL,
  `hdSoft` bigint(20)  NOT NULL,
  `storageSoft` bigint(20)  NOT NULL,
  `repositorySoft` bigint(20)  NOT NULL,
  `vlanSoft` bigint(20)  NOT NULL,
  `publicIPSoft` bigint(20)  NOT NULL,
  `ramHard` bigint(20) NOT NULL,
  `cpuHard` bigint(20) NOT NULL,
  `hdHard` bigint(20)  NOT NULL,
  `storageHard` bigint(20)  NOT NULL,
  `repositoryHard` bigint(20)  NOT NULL,
  `vlanHard` bigint(20)  NOT NULL,
  `publicIPHard` bigint(20)  NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,  
  PRIMARY KEY (`idDatacenterLimit`)
--  CONSTRAINT `idDataCenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `datacenter` (`idDataCenter`),
--  CONSTRAINT `idEnterprise_FK` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Asyncronous tasks
--
DROP TABLE IF EXISTS `kinton`.`tasks`;
CREATE TABLE `kinton`.`tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL,
  `component` varchar(20) NOT NULL,
  `action` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Add idEnterprise column to PhysicalMachine
--
ALTER TABLE `kinton`.`physicalmachine` ADD `idEnterprise` int(10) unsigned DEFAULT NULL;

-- -----------------------------------------------------
-- Add rack vlan networking parameters
-- -----------------------------------------------------
ALTER TABLE `kinton`.`rack` ADD COLUMN `vlan_id_min` int(15) unsigned default 2;
ALTER TABLE `kinton`.`rack` ADD COLUMN `vlan_id_max` int(15) unsigned default 4094;
ALTER TABLE `kinton`.`rack` ADD COLUMN `vlans_id_avoided` varchar(255) default '';
ALTER TABLE `kinton`.`rack` ADD COLUMN `vlan_per_vdc_expected` int(15) unsigned default 8;
ALTER TABLE `kinton`.`rack` ADD COLUMN `nrsq` int(15) unsigned default 10;

--
-- System properties
--

DROP TABLE IF EXISTS `kinton`.`SYSTEM_PROPERTIES`;
DROP TABLE IF EXISTS `kinton`.`system_properties`;
CREATE TABLE `kinton`.`system_properties` (
  `systemPropertyId` int(10) unsigned NOT NULL auto_increment,
  `version_c` int(11) default 0,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `description` varchar(255) NULL,
  PRIMARY KEY (`systemPropertyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.applibrary.ovfpackagesDownloadingProgressUpdateInterval","10","Time interval in seconds"),
 ("client.applibrary.virtualimageUploadProgressUpdateInterval","10","Time interval in seconds"),
 ("client.dashboard.abiquoURL","http://www.abiquo.org","URL of Abiquo web page"),
 ("client.dashboard.allowUsersAccess","1","Allow (1) or deny (0) access to the \'Users\' section"),
 ("client.dashboard.showStartUpAlert","0","Set to 1 to show an Alert with the text found in Startup_Alert.txt file"),
 ("client.infra.googleMapsDefaultLatitude","90","Google Maps will be centered by default at this longitude value"),
 ("client.infra.googleMapsDefaultLongitude","42","Google Maps will be centered by default at this latitude value"),
 ("client.infra.googleMapsDefaultZoom","4","Google Maps will be centered by default with this zoom level value"),
 ("client.infra.googleMapskey","0","The map\'s Google key used in infrastructure section"),
 ("client.infra.googleMapsLadTimeOut","10","Time, in seconds, that applications waits Google Maps to load. After that, application considers that Google Maps service is temporarily unavailable, and is not used"),
 ("client.infra.InfrastructureUpdateInterval","30","Time interval in seconds"),
 ("client.metering.meteringUpdateInterval","10","Time interval in seconds"),
 ("client.network.numberIpAdressesPerPage","25","Number entries that will appear when listing IP addresses in different parts of the application"),
 ("client.theme.defaultEnterpriseLogoPath","themes/abicloudDefault/logo.png","This is the path to the Enterprise logo used in the app"),
 ("client.user.numberEnterprisesPerPage","25","Number of enterprises per page that will appear in User Management"),
 ("client.user.numberUsersPerPage","25","Number of users per page that will appear in User Management"),
 ("client.virtual.allowVMRemoteAccess","1","Allow (1) or deny (0) virtual machine remote access"),
 ("client.virtual.virtualApplianceDeployingUpdateInterval","5","Time interval in seconds"),
 ("client.virtual.virtualAppliancesUpdateInterval","30","Time interval in seconds"),
 ("client.virtual.moreInfoAboutUploadLimitations","http://community.abicloud.org/display/ABI16/Appliance+Library+view#ApplianceLibraryview-Uploadingfromourlocalfilesystem","URL of Abiquo virtual image upload limitations web page"),
 ("client.infra.vlanIdMin","2","Minimum value for vlan ID"),
 ("client.infra.vlanIdMax","4094","Maximum value for vlan ID"),
 ("client.infra.useVirtualBox","0","Support (1) or not (0) Virtual Box"),
 ("client.dashboard.dashboardUpdateInterval","30","Time interval in seconds"),
 ("client.infra.defaultHypervisorPassword","voycruzand0elrio","Default Hypervisor password used when creating Physical Machines"),
 ("client.infra.defaultHypervisorPort","8889","Default Hypervisor port used when creating Physical Machines"),
 ("client.infra.defaultHypervisorUser","root","Default Hypervisor user used when creating Physical Machines"),
 ("client.storage.volumeMaxSizeValues","1,2,4,8,16,32,64,128,256","Comma separated values, with the allowed sizes when creating or editing a VolumeManagement"),
 ("client.virtual.virtualImagesRefreshConversionsInterval","5","Time interval in seconds to refresh missing virtual image conversions");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

-- ADD THE COLUMN ID_ENTERPRISE TO VLAN_NETWORK --
ALTER TABLE `kinton`.`vlan_network` ADD COLUMN `enterprise_id` int(10) unsigned DEFAULT NULL;
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `enterprise` (`idEnterprise`);

ALTER TABLE `kinton`.`user` add `availableVirtualDatacenters` varchar(255);

--
-- Remove obsolete tables
--

alter table `kinton`.`virtualimage_conversions` add sourceType varchar(50) default null;
alter table `kinton`.`virtualimage_conversions` add targetType varchar(50) not null;
update `kinton`.`virtualimage_conversions` set sourceType = 'UNKNOWN' where idSourceFormat = 0;
update `kinton`.`virtualimage_conversions` set targetType = 'UNKNOWN' where idTargetFormat = 0;
update `kinton`.`virtualimage_conversions` set sourceType = 'RAW' where idSourceFormat = 1;
update `kinton`.`virtualimage_conversions` set targetType = 'RAW' where idTargetFormat = 1;
update `kinton`.`virtualimage_conversions` set sourceType = 'INCOMPATIBLE' where idSourceFormat = 2;
update `kinton`.`virtualimage_conversions` set targetType = 'INCOMPATIBLE' where idTargetFormat = 2;
update `kinton`.`virtualimage_conversions` set sourceType = 'VMDK_STREAM_OPTIMIZED' where idSourceFormat = 3;
update `kinton`.`virtualimage_conversions` set targetType = 'VMDK_STREAM_OPTIMIZED' where idTargetFormat = 3;
update `kinton`.`virtualimage_conversions` set sourceType = 'VMDK_FLAT' where idSourceFormat = 4;
update `kinton`.`virtualimage_conversions` set targetType = 'VMDK_FLAT' where idTargetFormat = 4;
update `kinton`.`virtualimage_conversions` set sourceType = 'VMDK_SPARSE' where idSourceFormat = 5;
update `kinton`.`virtualimage_conversions` set targetType = 'VMDK_SPARSE' where idTargetFormat = 5;
update `kinton`.`virtualimage_conversions` set sourceType = 'VHD_FLAT' where idSourceFormat = 6;
update `kinton`.`virtualimage_conversions` set targetType = 'VHD_FLAT' where idTargetFormat = 6;
update `kinton`.`virtualimage_conversions` set targetType = 'VHD_SPARSE' where idTargetFormat = 7;
update `kinton`.`virtualimage_conversions` set sourceType = 'VHD_SPARSE' where idSourceFormat = 7;
update `kinton`.`virtualimage_conversions` set targetType = 'VDI_FLAT' where idTargetFormat = 8;
update `kinton`.`virtualimage_conversions` set sourceType = 'VDI_FLAT' where idSourceFormat = 8;
update `kinton`.`virtualimage_conversions` set targetType = 'VDI_SPARSE' where idTargetFormat = 9;
update `kinton`.`virtualimage_conversions` set sourceType = 'VDI_SPARSE' where idSourceFormat = 9;
update `kinton`.`virtualimage_conversions` set targetType = 'QCOW2_FLAT' where idTargetFormat = 10;
update `kinton`.`virtualimage_conversions` set sourceType = 'QCOW2_FLAT' where idSourceFormat = 10;
update `kinton`.`virtualimage_conversions` set sourceType = 'QCOW2_SPARSE' where idSourceFormat = 11;
update `kinton`.`virtualimage_conversions` set targetType = 'QCOW2_SPARSE' where idTargetFormat = 11;
-- update `kinton`.`virtualimage_conversions` set sourceType = NULL where idSourceFormat IS NULL OR idSourceFormat = '';
-- update `kinton`.`virtualimage_conversions` set targetType = NULL where idTargetFormat IS NULL OR idTargetFormat = '';
alter table `kinton`.`virtualimage_conversions` drop foreign key image_conversions_source_format_FK;
alter table `kinton`.`virtualimage_conversions` drop foreign key image_conversions_target_format_FK;
alter table `kinton`.`virtualimage_conversions` drop column idSourceFormat;
alter table `kinton`.`virtualimage_conversions` drop column idTargetFormat;

alter table `kinton`.`virtualimage` add `type` varchar(50) not null;
update `kinton`.`virtualimage` set `type` = 'UNKNOWN' where idFormat = 0;
update `kinton`.`virtualimage` set `type` = 'RAW' where idFormat = 1;
update `kinton`.`virtualimage` set `type` = 'INCOMPATIBLE' where idFormat = 2;
update `kinton`.`virtualimage` set `type` = 'VMDK_STREAM_OPTIMIZED' where idFormat = 3;
update `kinton`.`virtualimage` set `type` = 'VMDK_FLAT' where idFormat = 4;
update `kinton`.`virtualimage` set `type` = 'VMDK_SPARSE' where idFormat = 5;
update `kinton`.`virtualimage` set `type` = 'VHD_FLAT' where idFormat = 6;
update `kinton`.`virtualimage` set `type` = 'VHD_SPARSE' where idFormat = 7;
update `kinton`.`virtualimage` set `type` = 'VDI_FLAT' where idFormat = 8;
update `kinton`.`virtualimage` set `type` = 'VDI_SPARSE' where idFormat = 9;
update `kinton`.`virtualimage` set `type` = 'QCOW2_FLAT' where idFormat = 10;
update `kinton`.`virtualimage` set `type` = 'QCOW2_SPARSE' where idFormat = 11;
alter table `kinton`.`virtualimage` drop foreign key idFormat_FK;
alter table `kinton`.`virtualimage` drop column idFormat;
ALTER TABLE `kinton`.`virtualapp` MODIFY COLUMN `nodeconnections` varchar(255);
ALTER TABLE `kinton`.`node` MODIFY COLUMN `posX` int(3) NOT NULL DEFAULT 0;
ALTER TABLE `kinton`.`node` MODIFY COLUMN `posY` int(3) NOT NULL DEFAULT 0;
ALTER TABLE `kinton`.`repository` MODIFY COLUMN `name` varchar(30) DEFAULT NULL;
ALTER TABLE `kinton`.`virtualimage_conversions` MODIFY COLUMN `sourceType` varchar(50) DEFAULT NULL;
alter table `kinton`.`virtualdatacenter` drop foreign key virtualDataCenter_FK5;
alter table `kinton`.`virtualdatacenter` drop column idHypervisorType;
ALTER TABLE `kinton`.`volume_management` MODIFY COLUMN `usedSize` BIGINT(20) unsigned not null default 0;
ALTER TABLE `kinton`.`datastore` MODIFY COLUMN `size` BIGINT(40) UNSIGNED NOT NULL;
ALTER TABLE `kinton`.`datastore` MODIFY COLUMN `usedSize` BIGINT(40) UNSIGNED NOT NULL;
alter table `kinton`.`hypervisor` drop foreign key Hypervisor_FK2;
alter table `kinton`.`hypervisor` drop column idType;

alter table `kinton`.`ovf_package` add `type` varchar(50) not null;
update `kinton`.`ovf_package` set `type` = 'UNKNOWN' where idFormat = 0;
update `kinton`.`ovf_package` set `type` = 'RAW' where idFormat = 1;
update `kinton`.`ovf_package` set `type` = 'INCOMPATIBLE' where idFormat = 2;
update `kinton`.`ovf_package` set `type` = 'VMDK_STREAM_OPTIMIZED' where idFormat = 3;
update `kinton`.`ovf_package` set `type` = 'VMDK_FLAT' where idFormat = 4;
update `kinton`.`ovf_package` set `type` = 'VMDK_SPARSE' where idFormat = 5;
update `kinton`.`ovf_package` set `type` = 'VHD_FLAT' where idFormat = 6;
update `kinton`.`ovf_package` set `type` = 'VHD_SPARSE' where idFormat = 7;
update `kinton`.`ovf_package` set `type` = 'VDI_FLAT' where idFormat = 8;
update `kinton`.`ovf_package` set `type` = 'VDI_SPARSE' where idFormat = 9;
update `kinton`.`ovf_package` set `type` = 'QCOW2_FLAT' where idFormat = 10;
update `kinton`.`ovf_package` set `type` = 'QCOW2_SPARSE' where idFormat = 11;
alter table `kinton`.`ovf_package` drop foreign key fk_ovf_package_format;
alter table `kinton`.`ovf_package` drop column idFormat;

drop table `kinton`.`hypervisor_disk_compatibilities`;
drop table `kinton`.`hypervisortype`;
drop table `kinton`.`disk_format_type`;
drop table `kinton`.`disk_format_alias`;

--
-- drop deleted columns
--
alter table `kinton`.`user` drop column deleted;
alter table `kinton`.`enterprise` drop column deleted;

--
-- Change column size in ovf_package disk size
--
ALTER TABLE `kinton`.`ovf_package` modify `diskSizeMb` bigint(20) NULL;

--
-- Adds the chared property to virtual image
--
ALTER TABLE `kinton`.`virtualimage` ADD COLUMN `shared` int(1) unsigned NOT NULL default 0 COMMENT '0-No 1-Yes';

--
-- Enterprise theme table
--
DROP  TABLE IF EXISTS `kinton`.`enterprise_theme`;
CREATE  TABLE IF NOT EXISTS `kinton`.`enterprise_theme` (
  `idEnterprise` int(10) unsigned NOT NULL,
  `company_logo_path` text NULL, 
  `theme` text NULL , 
  PRIMARY KEY (`idEnterprise`), 
  KEY (`idEnterprise`),
  CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE CASCADE
)ENGINE = InnoDB DEFAULT CHARSET=utf8;

--
-- Add the name column for stateful conversions
--
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `newName` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL AFTER `id`;

--
-- Checks ALL Tables in DB and adds the 'version_c' column required for Hibernate Persistence 
--

DROP PROCEDURE IF EXISTS `kinton`.`add_version_column_to_all`;

DELIMITER |
CREATE PROCEDURE `kinton`.`add_version_column_to_all`()
BEGIN
    DECLARE currentTableName VARCHAR(64);
    DECLARE no_more_tables INTEGER;
    DECLARE curAllTables CURSOR FOR SELECT TABLE_NAME from `information_schema`.TABLES where TABLE_SCHEMA = "kinton" AND TABLE_TYPE="BASE TABLE";
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_tables=1;
    SET no_more_tables = 0;
    OPEN curAllTables;  
    dept_loop:WHILE(no_more_tables = 0) DO
        FETCH curAllTables INTO currentTableName;
        IF no_more_tables = 1 THEN
           LEAVE dept_loop;
        END IF;
        IF NOT EXISTS( (SELECT * FROM `information_schema`.COLUMNS WHERE TABLE_SCHEMA= "kinton" AND TABLE_NAME=currentTableName AND COLUMN_NAME='version_c') ) THEN
            SET @alter_sql=CONCAT('ALTER IGNORE TABLE ', currentTableName,' ADD COLUMN version_c int default 0;');
            PREPARE stmt from @alter_sql;
            EXECUTE stmt;
        END IF;
    END WHILE dept_loop;
    CLOSE curAllTables;
END;
|
DELIMITER ;


CALL `kinton`.`add_version_column_to_all`();

LOCK TABLES `kinton`.`auth_clientresource` WRITE;
INSERT INTO `kinton`.`auth_clientresource` VALUES (13,'VDC_ALLOCATION_LIMITS_TAB','Allocation resources for Virtual Datacenter',1,3,0),
(14, 'THEMES_MANAGEMENT', 'Allows to handle the application themes', 1, 1, 0),
(15,'MANAGE_CATEGORIES_BUTTONS','Allows to manage virtual image categories',4,1,0),
(16,'STATS_BASIC_CONTROL','Allows to see basics statistics',1,2,0),
(17,'STATS_ENTERPRISE_CONTROL','Allows to filter statistics  by enterprise',1,1,0),
(18,'ALLOW_VDC_ACTIONS','Permissions to allow/deny actions in VDC',1,2,0);
UNLOCK TABLES;


--
-- ********************************************************* STATISTICS SCHEMA UPDATED **************************************************************************** 
--
ALTER TABLE `kinton`.`rasd` MODIFY COLUMN `generation` BIGINT DEFAULT NULL;
ALTER TABLE `kinton`.`rasd` MODIFY COLUMN `reservation` BIGINT DEFAULT NULL;

ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `idDataCenter` INT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `serversTotal` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Total number of Physical machines managed.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `serversRunning` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Total number of Physical Machines running currently.';
ALTER TABLE `kinton`.`cloud_usage_stats` ADD COLUMN `storageReserved` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0  COMMENT 'External storage reserved by Enterprises in Datacenters (in Megabytes)';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `publicIPsTotal` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Total number of IPs defined (managed).';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `publicIPsReserved` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved)';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `publicIPsUsed` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines';
ALTER TABLE `kinton`.`cloud_usage_stats` ADD COLUMN `vlanReserved` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0  COMMENT 'Total maximum number of VLANs reserved by all enterprises. (enterprise.vlanHard)';
ALTER TABLE `kinton`.`cloud_usage_stats` ADD COLUMN `vlanUsed` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by any datacenter managed.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `vMachinesTotal` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Machines managed by Abiquo.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `vMachinesRunning` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Machines currently in a running state.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `numUsersCreated` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of users in system.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `numVDCCreated` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of Virtual Data Centers in system.';
ALTER TABLE `kinton`.`cloud_usage_stats` MODIFY COLUMN `numEnterprisesCreated` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of enterprises in system.';


ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum external storage size reserved by all enterprises in Megabytes.';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes)';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `repositoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum repository size reserved by all enterprises in Megabytes.';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `repositoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total repository size currently used by all enterprises in Megabytes.';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `publicIPsReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved)';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `publicIPsUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `vlanReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved by all enterprises. (enterprise.vlanHard)';
ALTER TABLE `kinton`.`enterprise_resources_stats` ADD COLUMN `vlanUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by any datacenter managed.';

ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `vCpuReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved for this enterprise in this virtual datacenter.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `vCpuUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `memoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved for this enterprise in this virtual datacenter in Megabytes.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `memoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `localStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum storage size reserved for this enterprise in this virtual datacenter for Virtual Machines  in Megabytes.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `localStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT ' Maximum external storage size reserved for this enterprise in this virtual datacenter in Megabytes.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes).';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `publicIPsReserved` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to this virtualDatacenter (Reserved).';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `publicIPsUsed` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines.';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `vlanReserved` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved for this enterprise in this virtual datacenter. (enterprise.vlanHard)';
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD COLUMN `vlanUsed` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by this virtual datacenter.';

DROP  TABLE IF EXISTS `kinton`.`dc_enterprise_stats`;
CREATE  TABLE IF NOT EXISTS `kinton`.`dc_enterprise_stats` (
  `idDCEnterpriseStats` INT NOT NULL AUTO_INCREMENT,
  `idDataCenter` INT NOT NULL ,
  `idEnterprise` INT NOT NULL ,
  `vCpuReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum number of Virtual CPUs reserved for this enterprise in this datacenter.',
  `vCpuUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total number of Virtual CPUs in Virtual Machines currently in a running state.',
  `memoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum amount of RAM reserved for this enterprise in this datacenter in Megabytes.',
  `memoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total amount of RAM used by Virtual Machines currently in a running state in Megabytes.',
  `localStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum storage size reserved for this enterprise in this datacenter for Virtual Machines  in Megabytes.',
  `localStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total storage size used by Virtual Machines currently in a running state  in Megabytes.',
  `extStorageReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT ' Maximum external storage size reserved for this enterprise in this datacenter in Megabytes.',
  `extStorageUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'External storage used (attached or mounted) by any virtual machines (in Megabytes).',
  `repositoryReserved` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Maximum repository size reserved for this enterprise in this datacenter in Megabytes.',
  `repositoryUsed` BIGINT UNSIGNED NULL DEFAULT 0 COMMENT 'Total repository size currently used for this enterprise in this datacenter in Megabytes.',
  `publicIPsReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs assigned to a VirtualDatacenter (Reserved).',
  `publicIPsUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of IPs used by virtual machines.',
  `vlanReserved` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total maximum number of VLANs reserved for this enterprise in this datacenter. (enterprise.vlanHard)',
  `vlanUsed` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of VLANs in use by this datacenter.',
  `version_c` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`idDCEnterpriseStats`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci;
  
-- Changing charset
ALTER TABLE `kinton`.`cloud_usage_stats` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `kinton`.`enterprise_resources_stats` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `kinton`.`vapp_enterprise_stats` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `kinton`.`vapp_enterprise_stats` MODIFY COLUMN `idVirtualApp` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `kinton`.`vapp_enterprise_stats` MODIFY COLUMN `vappName` varchar(45) DEFAULT NULL;
ALTER TABLE `kinton`.`vapp_enterprise_stats` MODIFY COLUMN `vdcName` varchar(45) DEFAULT NULL;
ALTER TABLE `kinton`.`vdc_enterprise_stats` CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE `kinton`.`vdc_enterprise_stats` MODIFY COLUMN `idEnterprise` int(11) NOT NULL;
ALTER TABLE `kinton`.`vdc_enterprise_stats` MODIFY COLUMN `vdcName` varchar(45) DEFAULT NULL;
ALTER TABLE `kinton`.`vdc_enterprise_stats` DROP PRIMARY KEY;
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`,`idEnterprise`);
--
-- ********************************************************* TRIGGERS UPDATED **************************************************************************** 
--
-- Deleted triggers
DROP TRIGGER IF EXISTS `kinton`.`user_deleted_logically`;

-- Modified Triggers
DROP TRIGGER IF EXISTS `kinton`.`update_virtualapp_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_created`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_updated`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_nodevirtualimage_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_nodevirtualimage_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`create_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_created`;
DROP TRIGGER IF EXISTS `kinton`.`update_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`delete_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_ip_pool_management_update_stats`;

-- Added Triggers
DROP TRIGGER IF EXISTS `kinton`.`dclimit_created`;
DROP TRIGGER IF EXISTS `kinton`.`dclimit_updated`;
DROP TRIGGER IF EXISTS `kinton`.`dclimit_deleted`;
DROP TRIGGER IF EXISTS `kinton`.`create_vlan_network_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;
DELIMITER |
SET @DISABLE_STATS_TRIGGERS = NULL;
|
CREATE TRIGGER `kinton`.`update_virtualapp_update_stats` AFTER UPDATE ON `kinton`.`virtualapp`
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
      AND v.state != "NOT_DEPLOYED" AND v.state != "UNKNOWN" AND v.state != "CRASHED"
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
CREATE TRIGGER `kinton`.`enterprise_created` AFTER INSERT ON `kinton`.`enterprise`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            --  Creates a New row in enterprise_resources_stats to store this enterprise's statistics
            INSERT IGNORE INTO enterprise_resources_stats (idEnterprise) VALUES (NEW.idEnterprise);
            --  Initializes stats for reserved resources (by Enterprise & by DataCenter)            
            UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated+1 WHERE idDataCenter = -1;
            UPDATE IGNORE enterprise_resources_stats 
                SET     vCpuReserved = vCpuReserved + NEW.cpuHard,
                    memoryReserved = memoryReserved + NEW.ramHard,
                    localStorageReserved = localStorageReserved + NEW.hdHard,
                    extStorageReserved = extStorageReserved + NEW.storageHard,
                    -- repositoryReserved = repositoryReserved + NEW.repositoryHard,
                    -- To be updated when IP is actually reserved/freed
                    -- publicIPsReserved = publicIPsReserved + NEW.publicIPHard, 
                    vlanReserved = vlanReserved + NEW.vlanHard
            WHERE idEnterprise = NEW.idEnterprise;  
            --  Updates enterprises created (in Fake DataCenter) for Full Cloud Statistics
            UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved=vCpuReserved + NEW.cpuHard,
                    vMemoryReserved=vMemoryReserved + NEW.ramHard,
                    vStorageReserved=vStorageReserved + NEW.hdHard,
                    storageReserved = storageReserved + NEW.storageHard,
                    -- repositoryReserved = repositoryReserved + NEW.repositoryHard,
                    publicIPsReserved = publicIPsReserved + NEW.publicIPHard
            WHERE idDataCenter = -1;                        
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`enterprise_updated` AFTER UPDATE ON `kinton`.`enterprise`
-- WARN: Enterprises are not deleted, logical delete (delete field) 
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
        -- get DataCenter
        -- calculates resources increase        
        UPDATE IGNORE enterprise_resources_stats 
            SET vCpuReserved = vCpuReserved + NEW.cpuHard - OLD.cpuHard,
                memoryReserved = memoryReserved + NEW.ramHard - OLD.ramHard,
                localStorageReserved = localStorageReserved + NEW.hdHard - OLD.hdHard,
                extStorageReserved = extStorageReserved + NEW.storageHard - OLD.storageHard,
                repositoryReserved = repositoryReserved + NEW.repositoryHard - OLD.repositoryHard,
                -- To be updated when IP is actually reserved/freed
                -- publicIPsReserved = publicIPsReserved + NEW.publicIPHard - OLD.publicIPHard,
                vlanReserved = vlanReserved + NEW.vlanHard - OLD.vlanHard
        WHERE idEnterprise = NEW.idEnterprise;
        UPDATE IGNORE cloud_usage_stats 
        SET vCpuReserved = vCpuReserved  + NEW.cpuHard - OLD.cpuHard,
            vMemoryReserved=vMemoryReserved + NEW.ramHard - OLD.ramHard,            
            vStorageReserved = vStorageReserved + NEW.hdHard - OLD.hdHard,
            storageReserved=storageReserved + NEW.storageHard - OLD.storageHard,
            publicIPsReserved = publicIPsReserved + NEW.publicIPHard - OLD.publicIPHard
        WHERE idDataCenter = -1;
    END IF;
  END;
|
CREATE TRIGGER `kinton`.`enterprise_deleted` AFTER DELETE ON `kinton`.`enterprise`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
        DELETE FROM enterprise_resources_stats WHERE idEnterprise = OLD.idEnterprise;           
        UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated-1 WHERE idDataCenter = -1;
        -- init reserved stats
        UPDATE IGNORE cloud_usage_stats 
            SET     vCpuReserved=vCpuReserved - OLD.cpuHard,
                vMemoryReserved=vMemoryReserved - OLD.ramHard,
                vStorageReserved = vStorageReserved - OLD.hdHard,
                storageReserved=storageReserved - OLD.storageHard,               
                -- repositoryReserved = repositoryReserved - OLD.repositoryHard
                -- To be updated when IP is actually reserved/freed
                publicIPsReserved = publicIPsReserved - OLD.publicIPHard
        WHERE idDataCenter = -1;
    END IF;
    END;
|
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
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
        IF NEW.idType = 1 AND (NEW.state != OLD.state OR OLD.idType != NEW.idType) THEN
            -- Activates if state changes or machines are captured
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
            IF OLD.state = "NOT_DEPLOYED" OR OLD.state = "UNKNOWN" OR OLD.idType != NEW.idType THEN
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
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
            CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd);
        END IF;              
    END IF;
    END;    
|
CREATE TRIGGER `kinton`.`create_nodevirtualimage_update_stats` AFTER INSERT ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE state VARCHAR(50);
    DECLARE type INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = NEW.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
      SELECT vm.state, vm.idType INTO state, type FROM virtualmachine vm WHERE vm.idVM = NEW.idVM;
      --
      IF state != "NOT_DEPLOYED" AND state != "UNKNOWN" AND state != "CRASHED"  AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF state = "RUNNING" AND type = 1 THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END;
|
CREATE TRIGGER `kinton`.`delete_nodevirtualimage_update_stats` AFTER DELETE ON `kinton`.`nodevirtualimage`
  FOR EACH ROW BEGIN
    DECLARE idDataCenterObj INTEGER;
    DECLARE idVirtualAppObj INTEGER;
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE oldState VARCHAR(50);
    DECLARE type INTEGER;
    DECLARE isUsingIP INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter, vdc.idDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj, idDataCenterObj
      FROM node n, virtualapp vapp, virtualdatacenter vdc
      WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter
      AND n.idNode = OLD.idNode
      AND n.idVirtualApp = vapp.idVirtualApp;
    SELECT state, idType INTO oldState, type FROM virtualmachine WHERE idVM = OLD.idVM;
    --
    IF type = 1 THEN
      IF oldState != "NOT_DEPLOYED" AND oldState != "UNKNOWN" AND oldState != "CRASHED" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
          WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
          WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
      --
      IF oldState = "RUNNING" THEN
        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
        WHERE idDataCenter = idDataCenterObj;
        UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualApp = idVirtualAppObj;
        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
      END IF;
    END IF;
  END IF;
  END;
|
CREATE TRIGGER `kinton`.`create_rasd_management_update_stats` AFTER INSERT ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;
        DECLARE resourceName VARCHAR(255);
        SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
        SELECT elementName, limitResource INTO resourceName, limitResourceObj
        FROM rasd r
        WHERE r.instanceID = NEW.idResource;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF NEW.idResourceType='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal+limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('CREATE_STORAGE', NEW.idResource, resourceName, NEW.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
                END IF;               
            END IF;
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`delete_rasd_management_update_stats` AFTER DELETE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idThisEnterprise INTEGER;
        DECLARE limitResourceObj BIGINT;    
        DECLARE resourceName VARCHAR(255);  
        SELECT vdc.idDataCenter, vdc.idEnterprise INTO idDataCenterObj, idThisEnterprise
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
        SELECT elementName, limitResource INTO resourceName, limitResourceObj
        FROM rasd r
        WHERE r.instanceID = OLD.idResource;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN           
            IF OLD.idResourceType='8' THEN 
                UPDATE IGNORE cloud_usage_stats SET storageTotal = storageTotal-limitResourceObj WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingStorageRegisterEvents' ) THEN
                    CALL AccountingStorageRegisterEvents('DELETE_STORAGE', OLD.idResource, resourceName, OLD.idVirtualDataCenter, idThisEnterprise, limitResourceObj);
                END IF;                  
            END IF;
        END IF;
    END;    
|
CREATE TRIGGER `kinton`.`virtualdatacenter_created` AFTER INSERT ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
        UPDATE IGNORE cloud_usage_stats SET numVDCCreated = numVDCCreated + 1 WHERE idDataCenter = NEW.idDataCenter;    
        -- Init Stats
        INSERT IGNORE INTO vdc_enterprise_stats 
            (idVirtualDataCenter,idEnterprise,vdcName,
            vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
            extStorageReserved, extStorageUsed, publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed) 
        VALUES 
            (NEW.idVirtualDataCenter, NEW.idEnterprise, NEW.name,
            NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
            NEW.storageHard, 0, 0, 0, NEW.vlanHard, 0);          
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
        SELECT rs.idDataCenter INTO idDataCenterObj
        FROM storage_pool sp, remote_service rs
        WHERE OLD.idStorage = sp.idStorage
        AND sp.idRemoteService = rs.idRemoteService;
        --      
        SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM rasd_management rasd, virtualapp vapp
        WHERE OLD.idManagement = rasd.idManagement
        AND rasd.idVirtualApp = vapp.idVirtualApp;
        --
        SELECT vdc.idEnterprise INTO idEnterpriseObj
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = idVirtualDataCenterObj;
        --
        SELECT r.limitResource INTO reservedSize
        FROM rasd_management rm, rasd r
        WHERE rm.idManagement = NEW.idManagement
        AND r.instanceID = rm.idResource;
        --
        IF NEW.state != OLD.state THEN
            IF NEW.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed+reservedSize WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = 2 THEN
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
            ELSEIF OLD.state = 2 THEN
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
                IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
                    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    IF idState = 2 THEN
                        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    END IF;                         
                END IF;
                IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
                    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    IF idState = 2 THEN
                        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    END IF;                 
                END IF;
                -- Detects Stateful added: 
                IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN                         
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- Stateful are always Attached 
                    IF idState = 2 THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
                    END IF;
                END IF;
                -- Detects Stateful added: 
                IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
                    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- Stateful are always Attached
                    IF idState = 2 THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;                     
                    END IF;
                END IF;         
                -- Detectamos cambios de VDC: V2V
                IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- vol Attached ??
                    SELECT IF(count(*) = 0, "", vm.state) INTO state
                    FROM volume_management vm
                    WHERE vm.idManagement = OLD.idManagement;
                    IF state = "PAUSED" THEN
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                    END IF;
                END IF;
            END IF;
            -- From old `autoDetachVolume`
            UPDATE IGNORE volume_management v set v.state = 0
            WHERE v.idManagement = OLD.idManagement;
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
                    UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed + 1 WHERE idDataCenter = idDataCenterObj;
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
                    UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
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
                    UPDATE IGNORE cloud_usage_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = idDataCenterObj;
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
                AND (vm.state = 1 OR vm.state = 2);
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
CREATE TRIGGER `kinton`.`delete_vlan_network_update_stats` AFTER DELETE ON `kinton`.`vlan_network`
FOR EACH ROW
BEGIN
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    DECLARE numPublicIpsDeleted INTEGER;
    DECLARE idDataCenterObj INTEGER;
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- Query for new Public Ips created
            SELECT COUNT(*) INTO numPublicIpsDeleted
            FROM ip_pool_management ipm, network_configuration nc
            WHERE ipm.dhcp_service_id=nc.dhcp_service_id
            AND OLD.network_configuration_id = nc.network_configuration_id
            AND OLD.network_id in (select distinct network_id from datacenter);
            -- Query for Datacenter
            SELECT dc.idDataCenter INTO idDataCenterObj
            FROM datacenter dc
            WHERE dc.network_id = OLD.network_id;
            --
            IF numPublicIpsDeleted > 0 THEN
                UPDATE IGNORE cloud_usage_stats SET publicIPsTotal = publicIPsTotal-numPublicIpsDeleted WHERE idDataCenter = idDataCenterObj;
            END IF;
            -- VLAN Accounting  
            SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
            FROM virtualdatacenter vdc, enterprise e
            WHERE vdc.networktypeID=OLD.network_id
            AND vdc.idEnterprise=e.idEnterprise;
            IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
                CALL AccountingVLANRegisterEvents('DELETE_VLAN',OLD.vlan_network_id, OLD.network_name, idVirtualDataCenterObj,idEnterpriseObj);
            END IF;
            -- Statistics
            UPDATE IGNORE cloud_usage_stats
                SET     vlanUsed = vlanUsed - 1
                WHERE idDataCenter = -1;
            UPDATE IGNORE enterprise_resources_stats 
                SET     vlanUsed = vlanUsed - 1
                WHERE idEnterprise = idEnterpriseObj;
            UPDATE IGNORE dc_enterprise_stats 
                SET     vlanUsed = vlanUsed - 1
                WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
            UPDATE IGNORE vdc_enterprise_stats 
                SET     vlanUsed = vlanUsed - 1
            WHERE idVirtualDataCenter = idVirtualDataCenterObj;
        END IF;
END;    
|
CREATE TRIGGER `kinton`.`update_ip_pool_management_update_stats` AFTER UPDATE ON `kinton`.`ip_pool_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- Checks for reserved IPs
            IF OLD.mac IS NULL AND NEW.mac IS NOT NULL THEN
                -- Query for datacenter
                SELECT vdc.idDataCenter, vdc.idVirtualDataCenter, vdc.idEnterprise  INTO idDataCenterObj, idVirtualDataCenterObj, idEnterpriseObj
                FROM rasd_management rm, virtualdatacenter vdc
                WHERE NEW.idManagement = rm.idManagement
                AND vdc.idVirtualDataCenter = rm.idVirtualDataCenter;
                -- New Public IP assignment for a VDC ---> Reserved
                UPDATE IGNORE cloud_usage_stats SET publicIPsReserved = publicIPsReserved+1 WHERE idDataCenter = idDataCenterObj;
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
CREATE TRIGGER `kinton`.`dclimit_created` AFTER INSERT ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            --  Creates a New row in dc_enterprise_stats to store this enterprise's statistics
            INSERT IGNORE INTO dc_enterprise_stats 
                (idDataCenter,idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed,
                extStorageReserved,extStorageUsed,repositoryReserved,repositoryUsed,publicIPsReserved,publicIPsUsed,vlanReserved,vlanUsed)
            VALUES 
                (NEW.idDataCenter, NEW.idEnterprise, NEW.cpuHard, 0, NEW.ramHard, 0, NEW.hdHard, 0,
                NEW.storageHard, 0, NEW.repositoryHard, 0, NEW.publicIPHard, 0, NEW.vlanHard, 0);
            -- cloud_usage_stats
            UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved + NEW.cpuHard,
                    vMemoryReserved = vMemoryReserved + NEW.ramHard,
                    vStorageReserved = vStorageReserved + NEW.hdHard,
                    storageReserved = storageReserved + NEW.storageHard,
                    publicIPsReserved = publicIPsReserved + NEW.publicIPHard,
                    vlanReserved = vlanReserved + NEW.vlanHard
                WHERE idDataCenter = NEW.idDataCenter;
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`dclimit_updated` AFTER UPDATE ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN      
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
            IF NEW.idEnterprise IS NULL OR NEW.idDataCenter IS NULL THEN
                -- Limit is not used anymore. Statistics are removed
                DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;
                UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved - OLD.cpuHard,
                    vMemoryReserved = vMemoryReserved - OLD.ramHard,
                    vStorageReserved = vStorageReserved - OLD.hdHard,
                    storageReserved = storageReserved - OLD.storageHard,
                    -- repositoryReserved = repositoryReserved - OLD.repositoryHard + NEW.repositoryHard,
                    publicIPsReserved = publicIPsReserved - OLD.publicIPHard,
                    vlanReserved = vlanReserved - OLD.vlanHard
                WHERE idDataCenter = OLD.idDataCenter;
            ELSE 
                -- Update statistics fields
                UPDATE IGNORE dc_enterprise_stats 
                SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                    memoryReserved = memoryReserved - OLD.ramHard + NEW.ramHard,
                    localStorageReserved = localStorageReserved - OLD.hdHard + NEW.hdHard,
                    extStorageReserved = extStorageReserved - OLD.storageHard + NEW.storageHard,
                    repositoryReserved = repositoryReserved - OLD.repositoryHard + NEW.repositoryHard,
                    -- publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                    vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = NEW.idDataCenter;
                -- Update cloud usage
                UPDATE IGNORE cloud_usage_stats 
                SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                    vMemoryReserved = vMemoryReserved - OLD.ramHard + NEW.ramHard,
                    vStorageReserved = vStorageReserved - OLD.hdHard + NEW.hdHard,
                    storageReserved = storageReserved - OLD.storageHard + NEW.storageHard,
                    -- repositoryReserved = repositoryReserved - OLD.repositoryHard + NEW.repositoryHard,
                    publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                    vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
                WHERE idDataCenter = NEW.idDataCenter;
            END IF;             
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`dclimit_deleted` AFTER DELETE ON `kinton`.`enterprise_limits_by_datacenter`
    FOR EACH ROW BEGIN
        DELETE FROM dc_enterprise_stats WHERE idEnterprise = OLD.idEnterprise AND idDataCenter = OLD.idDataCenter;
        UPDATE IGNORE cloud_usage_stats 
        SET vCpuReserved = vCpuReserved - OLD.cpuHard,
            vMemoryReserved = vMemoryReserved - OLD.ramHard,
            vStorageReserved = vStorageReserved - OLD.hdHard,
            storageReserved = storageReserved - OLD.storageHard,
            -- repositoryReserved = repositoryReserved - OLD.repositoryHard + NEW.repositoryHard,
            publicIPsReserved = publicIPsReserved - OLD.publicIPHard,
            vlanReserved = vlanReserved - OLD.vlanHard
        WHERE idDataCenter = OLD.idDataCenter;
    END;
|
CREATE TRIGGER `kinton`.`create_vlan_network_update_stats` AFTER INSERT ON `kinton`.`vlan_network`
FOR EACH ROW BEGIN
    DECLARE idVirtualDataCenterObj INTEGER;
    DECLARE idDataCenterObj INTEGER;
    DECLARE idEnterpriseObj INTEGER;
    SELECT vdc.idVirtualDataCenter, e.idEnterprise INTO idVirtualDataCenterObj, idEnterpriseObj
    FROM virtualdatacenter vdc, enterprise e
    WHERE vdc.networktypeID=NEW.network_id
    AND vdc.idEnterprise=e.idEnterprise;
    -- Query for Datacenter
    SELECT dc.idDataCenter INTO idDataCenterObj
    FROM datacenter dc
    WHERE dc.network_id = NEW.network_id;
    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('PROCEDURE AccountingVLANRegisterEvents Activated: ',IFNULL(NEW.vlan_network_id,'NULL'),'-',IFNULL(NEW.network_name,'NULL'),'-',IFNULL(idVirtualDataCenterObj,'NULL'),'-',idEnterpriseObj,'-','CREATE_VLAN','-',now()));
    IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
            CALL AccountingVLANRegisterEvents('CREATE_VLAN',NEW.vlan_network_id, NEW.network_name, idVirtualDataCenterObj,idEnterpriseObj);
        END IF;    
    -- Statistics
    UPDATE IGNORE cloud_usage_stats
        SET     vlanUsed = vlanUsed + 1
        WHERE idDataCenter = -1;
    UPDATE IGNORE enterprise_resources_stats 
        SET     vlanUsed = vlanUsed + 1
        WHERE idEnterprise = idEnterpriseObj;
    UPDATE IGNORE dc_enterprise_stats 
        SET     vlanUsed = vlanUsed + 1
        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
    UPDATE IGNORE vdc_enterprise_stats 
        SET     vlanUsed = vlanUsed + 1
    WHERE idVirtualDataCenter = idVirtualDataCenterObj;
END;
|
CREATE TRIGGER `kinton`.`virtualdatacenter_updated` AFTER UPDATE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- Checks for changes
            IF OLD.name != NEW.name THEN
                -- Name changed !!!
                UPDATE IGNORE vdc_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
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
    END;
|

-- ******************************************************************************************
--
--  Checks statistics miscalculations and corrects them to zero
--
-- ****************************************************************************************
DROP TRIGGER IF EXISTS `kinton`.`cloud_usage_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`enterprise_resources_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`vdc_enterprise_stats_negative_check`;
DROP TRIGGER IF EXISTS `kinton`.`dc_enterprise_stats_negative_check`;

CREATE TRIGGER `kinton`.`cloud_usage_stats_negative_check` BEFORE UPDATE ON `kinton`.`cloud_usage_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.serversTotal > 16700000)  THEN SET NEW.serversTotal = 0; END IF;
      IF (NEW.serversRunning > 16700000)  THEN SET NEW.serversRunning = 0; END IF;
      IF (NEW.storageTotal > 18446000000000000000)  THEN SET NEW.storageTotal = 0; END IF;
      IF (NEW.storageReserved > 18446000000000000000) THEN SET NEW.storageReserved = 0; END IF;
      IF (NEW.storageUsed > 18446000000000000000)  THEN SET NEW.storageUsed = 0; END IF;
      IF (NEW.publicIPsTotal > 16700000)  THEN SET NEW.publicIPsTotal = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vMachinesTotal > 16700000)  THEN SET NEW.vMachinesTotal = 0; END IF;
      IF (NEW.vMachinesRunning > 16700000)  THEN SET NEW.vMachinesRunning = 0; END IF;
      IF (NEW.vCpuTotal > 18446000000000000000)  THEN SET NEW.vCpuTotal = 0; END IF;
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.vMemoryTotal > 18446000000000000000)  THEN SET NEW.vMemoryTotal = 0; END IF;
      IF (NEW.vMemoryReserved > 18446000000000000000)  THEN SET NEW.vMemoryReserved = 0; END IF;
      IF (NEW.vMemoryUsed > 18446000000000000000)  THEN SET NEW.vMemoryUsed = 0; END IF;
      IF (NEW.vStorageTotal > 18446000000000000000)  THEN SET NEW.vStorageTotal = 0; END IF;
      IF (NEW.vStorageReserved > 18446000000000000000)  THEN SET NEW.vStorageReserved = 0; END IF;
      IF (NEW.vStorageUsed > 18446000000000000000)  THEN SET NEW.vStorageUsed = 0; END IF;
      IF (NEW.numUsersCreated > 16700000)  THEN SET NEW.numUsersCreated = 0; END IF;
      IF (NEW.numVDCCreated > 16700000)  THEN SET NEW.numVDCCreated = 0; END IF;
      IF (NEW.numEnterprisesCreated > 16700000)  THEN SET NEW.numEnterprisesCreated = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;


CREATE TRIGGER `kinton`.`enterprise_resources_stats_negative_check` BEFORE UPDATE ON `kinton`.`enterprise_resources_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.repositoryReserved > 18446000000000000000)  THEN SET NEW.repositoryReserved = 0; END IF;
      IF (NEW.repositoryUsed > 18446000000000000000)  THEN SET NEW.repositoryUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;

CREATE TRIGGER `kinton`.`dc_enterprise_stats_negative_check` BEFORE UPDATE ON `kinton`.`dc_enterprise_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.repositoryReserved > 18446000000000000000)  THEN SET NEW.repositoryReserved = 0; END IF;
      IF (NEW.repositoryUsed > 18446000000000000000)  THEN SET NEW.repositoryUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;

CREATE TRIGGER `kinton`.`vdc_enterprise_stats_negative_check` BEFORE UPDATE ON `kinton`.`vdc_enterprise_stats`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF (NEW.vmCreated > 16700000)  THEN SET NEW.vmCreated = 0; END IF;
      IF (NEW.vmActive > 16700000)  THEN SET NEW.vmActive = 0; END IF;
      IF (NEW.volCreated > 16700000)  THEN SET NEW.volCreated = 0; END IF;
      IF (NEW.volAssociated > 16700000)  THEN SET NEW.volAssociated = 0; END IF;
      IF (NEW.volAttached > 16700000)  THEN SET NEW.volAttached = 0; END IF;
      IF (NEW.vCpuReserved > 18446000000000000000)  THEN SET NEW.vCpuReserved = 0; END IF;
      IF (NEW.vCpuUsed > 18446000000000000000)  THEN SET NEW.vCpuUsed = 0; END IF;
      IF (NEW.memoryReserved > 18446000000000000000)  THEN SET NEW.memoryReserved = 0; END IF;
      IF (NEW.memoryUsed > 18446000000000000000)  THEN SET NEW.memoryUsed = 0; END IF;
      IF (NEW.localStorageReserved > 18446000000000000000)  THEN SET NEW.localStorageReserved = 0; END IF;
      IF (NEW.localStorageUsed > 18446000000000000000)  THEN SET NEW.localStorageUsed = 0; END IF;                                 
      IF (NEW.extStorageReserved > 18446000000000000000)  THEN SET NEW.extStorageReserved = 0; END IF;
      IF (NEW.extStorageUsed > 18446000000000000000)  THEN SET NEW.extStorageUsed = 0; END IF;
      IF (NEW.publicIPsReserved > 16700000)  THEN SET NEW.publicIPsReserved = 0; END IF;
      IF (NEW.publicIPsUsed > 16700000)  THEN SET NEW.publicIPsUsed = 0; END IF;
      IF (NEW.vlanReserved > 16700000)  THEN SET NEW.vlanReserved = 0; END IF;
      IF (NEW.vlanUsed > 16700000)  THEN SET NEW.vlanUsed = 0; END IF;
    END IF;
  END;
|  
DELIMITER ;

--
-- Change Role types and descriptions
--
alter table `kinton`.`role` add `type` varchar(20) NOT NULL;
alter table `kinton`.`role` modify securityLevel float(10,1) unsigned NOT NULL default '0.0';
update `kinton`.`role` set `type` = 'SYS_ADMIN', shortDescription = 'Cloud Admin', largeDescription = 'IT Cloud Administrator' where idRole = 1;
update `kinton`.`role` set `type` = 'USER' where idRole = 2;
update `kinton`.`role` set `type` = 'ENTERPRISE_ADMIN' where idRole = 3;
update `kinton`.`user` set name = 'Cloud' where idUser = 1;

--
-- session foreign key
--
delete from `kinton`.`session`;
alter table `kinton`.`session` add column idUser int(10) unsigned;
alter table `kinton`.`session` add constraint `fk_session_user` foreign key (`idUser`) references `user` (`idUser`);


-- add user constraint to user 

ALTER TABLE `kinton`.`virtualmachine` ADD  KEY `virtualMachine_FK4` (`idUser`);
ALTER TABLE `kinton`.`virtualmachine` ADD  CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE SET NULL;
ALTER TABLE `kinton`.`virtualmachine` ADD  KEY `virtualMachine_FK5` (`idEnterprise`);
ALTER TABLE `kinton`.`virtualmachine` ADD  CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `enterprise` (`idEnterprise`) ON DELETE SET NULL;


--
-- SanityChecks for Statistics should be executed to update data.
--
-- CALL `kinton`.`CalculateCloudUsageStats`;
-- CALL `kinton`.`CalculateEnterpriseResourcesStats`;
-- CALL `kinton`.`CalculateVappEnterpriseStats`;
-- CALL `kinton`.`CalculateVdcEnterpriseStats`;

-- Enterprise Limits insert

INSERT INTO `kinton`.`enterprise_limits_by_datacenter` (idEnterprise, idDataCenter, ramSoft, cpuSoft, hdSoft, storageSoft, repositorySoft, vlanSoft, publicIPSoft, ramHard, cpuHard, hdHard, storageHard, repositoryHard, vlanHard, publicIPHard, version_c)
SELECT distinct e.idEnterprise, d.idDatacenter, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1
  FROM enterprise e, datacenter d;

-- Update vlan_network values for private vlans
UPDATE vlan_network v, dhcp_service d, network_configuration c, ip_pool_management i, network n, virtualdatacenter vdc
   SET i.vlan_network_id = v.vlan_network_id, i.vlan_network_name = v.network_name
   WHERE i.dhcp_service_id = d.dhcp_service_id 
     AND c.dhcp_service_id = d.dhcp_service_id 
     AND v.network_configuration_id = c.network_configuration_id
     AND vdc.networktypeId = n.network_id
     AND v.network_id = n.network_id;

     
-- ******************************************************************************************
--
--  Stats Sanity: Procedures to recalculate stats on demand
--
-- ****************************************************************************************

DROP PROCEDURE IF EXISTS `kinton`.`CalculateCloudUsageStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateEnterpriseResourcesStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVappEnterpriseStats`;
DROP PROCEDURE IF EXISTS `kinton`.`CalculateVdcEnterpriseStats`;
-- DROP PROCEDURE IF EXISTS `kinton`.`CalculateDcEnterpriseStats`;

DELIMITER |
|
-- Statistics INFO Update
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
    FROM storage_pool sp, remote_service rs, volume_management vm, rasd_management rm, rasd r
    WHERE vm.idStorage = sp.idStorage
    AND sp.idRemoteService = rs.idRemoteService
    AND vm.idManagement = rm.idManagement
    AND r.instanceID = rm.idResource
    AND rm.idResourceType = 8
    AND (vm.state = 1 OR vm.state = 2)
    AND rs.idDataCenter = idDataCenterObj;
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
--
--
--
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
    AND (vm.state = 1 OR vm.state = 2)
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
--
--
--
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
  DECLARE vCpuUsed BIGINT UNSIGNED; -- TBD
  DECLARE memoryReserved BIGINT UNSIGNED;
  DECLARE memoryUsed BIGINT UNSIGNED; -- TBD
  DECLARE localStorageReserved BIGINT UNSIGNED; 
  DECLARE localStorageUsed BIGINT UNSIGNED; -- TBD
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
    AND state = 2;
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
    AND (vm.state = 1 OR vm.state = 2)
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
--
-- To be DONE when showing Datacenter Stats by Enterprise
-- CREATE PROCEDURE `kinton`.CalculateDcEnterpriseStats()
--   BEGIN
--   END;
--
--
CREATE PROCEDURE `kinton`.CalculateVappEnterpriseStats()
   BEGIN
  DECLARE idVirtualAppObj INTEGER;
  DECLARE idEnterprise INTEGER;
  DECLARE vappName VARCHAR(45);
  DECLARE vdcName VARCHAR(45);
  DECLARE vmCreated MEDIUMINT UNSIGNED;
  DECLARE vmActive MEDIUMINT UNSIGNED;
  DECLARE volAssociated MEDIUMINT UNSIGNED;
  DECLARE volAttached MEDIUMINT UNSIGNED;

  DECLARE no_more_vapps INTEGER;

  DECLARE curDC CURSOR FOR SELECT vapp.idVirtualApp, vapp.idEnterprise, vapp.name, vdc.name FROM virtualapp vapp, virtualdatacenter vdc WHERE vdc.idVirtualDataCenter = vapp.idVirtualDataCenter;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_vapps = 1;

  SET no_more_vapps = 0;
  SET idVirtualAppObj = -1;

  OPEN curDC;

  TRUNCATE vapp_enterprise_stats;

  dept_loop:WHILE(no_more_vapps = 0) DO
    FETCH curDC INTO idVirtualAppObj, idEnterprise, vappName, vdcName;
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
    AND state = 2;

    -- Inserts stats row
    INSERT INTO vapp_enterprise_stats (idVirtualApp,idEnterprise,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached)
    VALUES (idVirtualAppObj, idEnterprise,vappName,vdcName,vmCreated,vmActive,volAssociated,volAttached);


  END WHILE dept_loop;
  CLOSE curDC;

   END;

|
--
DELIMITER ;     

CALL `kinton`.`CalculateCloudUsageStats`();
CALL `kinton`.`CalculateEnterpriseResourcesStats`();
CALL `kinton`.`CalculateVappEnterpriseStats`();
CALL `kinton`.`CalculateVdcEnterpriseStats`();
-- To be DONE when showing Datacenter Stats by Enterprise
-- CALL `kinton`.`CalculateDcEnterpriseStats`(); 
