--
-- Creates disk_format_type and disk_format_alias tables
--

DROP TABLE IF EXISTS `kinton`.`disk_format_alias`;
CREATE TABLE  `kinton`.`disk_format_alias` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `disk_format_alias` WRITE;
INSERT INTO `kinton`.`disk_format_alias` VALUES
(0, 'UNKNWON'),
(1, 'RAW'),
(2, 'INCOMPATIBLE'),
(3, 'VMDK_STREAM_OPTIMIZED'),
(4, 'VMDK_FLAT'),
(5, 'VMDK_SPARSE'),
(6, 'VHD'),
(7, 'VDI'),
(8, 'QCOW2');
UNLOCK TABLES;


DROP TABLE IF EXISTS `kinton`.`disk_format_type`;
CREATE TABLE  `kinton`.`disk_format_type` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `uri` varchar(255),
  `description` varchar(255) NOT NULL,
  `magicnumber` integer,
  `alias` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `Alias_FK` (`alias`),
  CONSTRAINT `Alias_FK` FOREIGN KEY (`alias`) REFERENCES `disk_format_alias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `disk_format_type` WRITE;
INSERT INTO `kinton`.`disk_format_type` VALUES
(0, 'http://unknown', 'Unknown format', null, 0),
(1, 'http://raw', 'Disk from device', null, 1),
(2, 'http://incompatible', 'Incompatible disk type', null, 2),
(3, 'http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized', 'VMWare streamOptimized format', null, 3),
(4, 'http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat', 'VMWare Fixed Disk', null, 4),
(5, 'http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse', 'VMWare Sparse Disk', null, 5),
(6, 'http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat', 'VHD Fixed Disk', null, 6),
(7, 'http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse', 'VHD Fixed Disk', null, 6),
(8, 'http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat', 'VDI Fixed Disk', null, 7),
(9, 'http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse', 'VDI Sparse Disk', null, 7),
(10, 'http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat', 'QCOW2 Fixed Disk' ,null, 8),
(11, 'http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse', 'QCOW2 Sparse Disk' ,null, 8);
UNLOCK TABLES;

--
-- Update column length to allow generated values
--
ALTER TABLE `kinton`.`rasd`
  MODIFY COLUMN `description` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  MODIFY COLUMN `elementName` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  MODIFY COLUMN `limitResource` BIGINT DEFAULT NULL;
  
--
-- Add RAW disk compatibilities
--
LOCK TABLES `hypervisor_disk_compatibilities` WRITE;
INSERT INTO `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) VALUES (1, 1);
INSERT INTO `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) VALUES (2, 1);
UNLOCK TABLES;
