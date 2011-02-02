/**
 *  This script creates the metering table
 */

DROP TABLE IF EXISTS `kinton`.`metering`;
CREATE TABLE  `kinton`.`metering` (
  `idMeter` BIGINT unsigned NOT NULL auto_increment,
  `idDatacenter` int(10) unsigned default null,
  `datacenter` varchar(20) default null,
  `idRack` int(15) unsigned default NULL,
  `rack` varchar(20) default NULL,
  `idPhysicalMachine` int(20) unsigned default NULL,
  `physicalmachine` varchar(30) default NULL,
  `idStorageSystem` int(10) unsigned default NULL,
  `storageSystem` varchar(256) default NULL,
  `idStoragePool` varchar(40) default NULL,
  `storagePool` varchar(256) default NULL,
  `idVolume` varchar(50) default NULL,
  `volume` varchar(256) default NULL,
  `idNetwork` int(11) unsigned default NULL,
  `network` varchar(256) default NULL,
  `idSubnet` int(11) unsigned default NULL,
  `subnet` varchar(256) default NULL, 
  `idEnterprise` int(10) unsigned default NULL,
  `enterprise` varchar(40) default NULL, 
  `idUser` int(10) unsigned default NULL,
  `user` varchar(20) default NULL, 
  `idVirtualDataCenter` int(10) unsigned default NULL,
  `virtualDataCenter` varchar(40) default NULL, 
  `idVirtualApp` int(10) unsigned default NULL,
  `virtualApp` varchar(30) default NULL,   
  `idVirtualMachine` int(10) unsigned default NULL,
  `virtualmachine` varchar(100) default NULL, 
  `severity` varchar(100) NOT NULL, 
  `timestamp` timestamp NOT NULL,
  `performedby` varchar(255) NOT NULL,
  `actionperformed` varchar(100) NOT NULL,  
  `component` varchar(255) default NULL, 
  `stacktrace` text default NULL,
  PRIMARY KEY  (`idMeter`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;