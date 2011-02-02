DROP TABLE IF EXISTS `kinton`.`imagetype`;

DROP TABLE IF EXISTS `kinton`.`disk_format_type`;
CREATE TABLE  `kinton`.`disk_format_type` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `uri` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `magicnumber` integer,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `kinton`.`disk_format_type` VALUES  (0, 'http://unknown', 'Unknown format');

ALTER TABLE `kinton`.`virtualimage` DROP FOREIGN KEY `virtualImage_FK7`;

ALTER TABLE `kinton`.`virtualimage` CHANGE COLUMN `imageType` `idFormat` INT(10) UNSIGNED NOT NULL DEFAULT 1;

ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `idFormat_FK` FOREIGN KEY `idFormat_FK` (`idFormat`)
    REFERENCES `disk_format_type` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;
    
DROP TABLE IF EXISTS `kinton`.`state_conversion`;
CREATE TABLE  `kinton`.`state_conversion` (
  `idState` int(1) unsigned NOT NULL auto_increment,
  `description` varchar(20) NOT NULL,
  PRIMARY KEY  (`idState`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `kinton`.`state_conversion` VALUES  (1, 'ENQUEUED'), (2, 'FINISHED'), (3,'FAILED');

DROP TABLE IF EXISTS `kinton`.`virtualimage_conversions`;
CREATE TABLE  `kinton`.`virtualimage_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment, 
  `idImage` int(10) unsigned NOT NULL,
  `idSourceFormat` int(10) unsigned,
  `idTargetFormat` int(10) unsigned NOT NULL,
  `sourcePath` varchar(255),
  `targetPath` varchar(255) NOT NULL,
  `idState` int(1) unsigned NOT NULL,
  `timestamp` timestamp NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `idImage_FK` (`idImage`),
  KEY `image_conversions_source_format_FK` (`idSourceFormat`),
  KEY `image_conversions_target_format_FK` (`idTargetFormat`),
  KEY `idState_FK` (`idState`),
  CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `virtualimage` (`idImage`) ON DELETE CASCADE,
  CONSTRAINT `image_conversions_source_format_FK` FOREIGN KEY (`idSourceFormat`) REFERENCES `disk_format_type` (`id`) ON DELETE CASCADE,  
  CONSTRAINT `image_conversions_target_format_FK` FOREIGN KEY (`idTargetFormat`) REFERENCES `disk_format_type` (`id`) ON DELETE CASCADE,  
  CONSTRAINT `idState_FK` FOREIGN KEY (`idState`) REFERENCES `state_conversion` (`idState`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `kinton`.`hypervisor_disk_compatibilities`;
CREATE TABLE  `kinton`.`hypervisor_disk_compatibilities` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idFormat` int(10) unsigned NOT NULL,
  `idHypervisor` int(5) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `disk_compatibilities_format_FK` (`idFormat`),
  KEY `idHypervisor_FK` (`idHypervisor`),
  CONSTRAINT `disk_compatibilities_format_FK` FOREIGN KEY (`idFormat`) REFERENCES `disk_format_type` (`id`) ON DELETE CASCADE,
  CONSTRAINT `idHypervisor_FK` FOREIGN KEY (`idHypervisor`) REFERENCES `hypervisortype` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `kinton`.`hypervisor_disk_compatibilities` WRITE;
insert into `kinton`.`hypervisor_disk_compatibilities` (idFormat, idHypervisor) values (1, 1);
insert into `kinton`.`hypervisor_disk_compatibilities` (idFormat, idHypervisor) values (1, 2);
insert into `kinton`.`hypervisor_disk_compatibilities` (idFormat, idHypervisor) values (1, 3);
UNLOCK TABLES;

DROP TABLE IF EXISTS `kinton`.`virtual_appliance_conversions`;
CREATE TABLE `kinton`.`virtual_appliance_conversions` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idConversion` int(10) unsigned NOT NULL,
  `idVirtualAppliance` int(10) unsigned NOT NULL,
  `idUser` int(10) unsigned NOT NULL,
  `forceLimits` boolean,
  PRIMARY KEY (`id`),
  KEY `idConversion_K` (`idConversion`),
  KEY `idVirtualAppliance_K` (`idVirtualAppliance`),
  KEY `idUser_K` (`idUser`),
  CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `virtualimage_conversions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `virtualapp` (`idVirtualApp`) ON DELETE CASCADE,
  CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bpm_event_status` (
  `process_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `enterprise_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `event_type` int(11) NOT NULL,
  `process_name` varchar(255) NOT NULL,
  `creation_date` timestamp NULL DEFAULT NULL,
  `last_update` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bpm_event_status_history` (
  `event_status_history_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `process_id` bigint(20) unsigned NOT NULL,
  `enterprise_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `event_type` int(11) NOT NULL,
  `process_name` varchar(255) NOT NULL,
  `tstamp` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`event_status_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `SYSTEM_PROPERTIES` (
  `ID` varchar(255) NOT NULL,
  `VALUE` varchar(255) NOT NULL,
  `DESCRIPTION` varchar(255) NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER |

CREATE TRIGGER `bpm_event_status_after_ins_tr` AFTER INSERT ON `bpm_event_status`
  FOR EACH ROW
BEGIN
     insert into bpm_event_status_history (process_id,enterprise_id, user_id, event_type, process_name, tstamp)
     values (NEW.process_id,NEW.enterprise_id, NEW.user_id, NEW.event_type, NEW.process_name, NOW());
END;

CREATE TRIGGER `bpm_event_status_after_upd_tr` AFTER UPDATE ON `bpm_event_status`
  FOR EACH ROW
BEGIN
     insert into bpm_event_status_history (process_id,enterprise_id, user_id, event_type, process_name, tstamp)
     values (NEW.process_id,NEW.enterprise_id, NEW.user_id, NEW.event_type, NEW.process_name, NOW());
END;

|   

DELIMITER ;

ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `idConversion` INT(10) UNSIGNED AFTER `high_disponibility`,
 ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY `virtualmachine_conversion_FK` (`idConversion`)
    REFERENCES `virtualimage_conversions` (`id`);

DELETE FROM `kinton`.`hypervisortype`;

ALTER TABLE `kinton`.`hypervisortype` ADD COLUMN `idBaseFormat` INT(10) UNSIGNED NOT NULL AFTER `defaultPort`,
 ADD CONSTRAINT `hypervisor_disk_format_FK` FOREIGN KEY `hypervisor_disk_format_FK` (`idBaseFormat`)
    REFERENCES `disk_format_type` (`id`);

LOCK TABLES `hypervisortype` WRITE;
INSERT INTO `kinton`.`hypervisortype` VALUES  
 (1,'vBox',18083, 5),
 (2,'KVM',8887, 5),
 (3,'xen-3',8887, 4);

UNLOCK TABLES; 
