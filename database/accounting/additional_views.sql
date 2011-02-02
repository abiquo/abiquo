# SQL Manager 2005 for MySQL 3.7.0.1
# ---------------------------------------
# Host     : 10.60.1.80
# Port     : 3306
# Database : kinton

USE kinton;

SET FOREIGN_KEY_CHECKS=0;

#
# Structure for the `chargeback_simple` table : 
#

DROP TABLE IF EXISTS `chargeback_simple`;

CREATE TABLE `chargeback_simple` (
  `idAccountingResourceType` tinyint(4) NOT NULL,
  `resourceType` varchar(20) NOT NULL,
  `costPerHour` decimal(15,12) NOT NULL,
  PRIMARY KEY  (`idAccountingResourceType`),
  UNIQUE KEY `idAccountingResourceType` (`idAccountingResourceType`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


#
# Definition for the `HOURLY_USAGE_MAX_VW` view : 
#

DROP VIEW IF EXISTS `HOURLY_USAGE_MAX_VW`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `HOURLY_USAGE_MAX_VW` AS 
  select 
    `accounting_event_detail`.`startTime` AS `startTime`,
    `accounting_event_detail`.`endTime` AS `endTime`,
    `accounting_event_detail`.`idAccountingResourceType` AS `idAccountingResourceType`,
    `accounting_event_detail`.`resourceType` AS `resourceType`,
    `accounting_event_detail`.`resourceName` AS `resourceName`,
    max(`accounting_event_detail`.`resourceUnits`) AS `resourceUnits`,
    `accounting_event_detail`.`idEnterprise` AS `idEnterprise`,
    `accounting_event_detail`.`idVirtualDataCenter` AS `idVirtualDataCenter`,
    `accounting_event_detail`.`idVirtualApp` AS `idVirtualApp`,
    `accounting_event_detail`.`idVirtualMachine` AS `idVirtualMachine`,
    `accounting_event_detail`.`enterpriseName` AS `enterpriseName`,
    `accounting_event_detail`.`virtualDataCenter` AS `virtualDataCenter`,
    `accounting_event_detail`.`virtualApp` AS `virtualApp`,
    `accounting_event_detail`.`virtualMachine` AS `virtualMachine` 
  from 
    `accounting_event_detail` 
  group by 
    `accounting_event_detail`.`startTime`,`accounting_event_detail`.`endTime`,`accounting_event_detail`.`idAccountingResourceType`,`accounting_event_detail`.`resourceType`,`accounting_event_detail`.`resourceName`,`accounting_event_detail`.`idEnterprise`,`accounting_event_detail`.`idVirtualDataCenter`,`accounting_event_detail`.`idVirtualApp`,`accounting_event_detail`.`idVirtualMachine`,`accounting_event_detail`.`enterpriseName`,`accounting_event_detail`.`virtualDataCenter`,`accounting_event_detail`.`virtualApp`,`accounting_event_detail`.`virtualMachine`;


#
# Definition for the `DAILY_USAGE_SUM_VW` view : 
#

DROP VIEW IF EXISTS `DAILY_USAGE_SUM_VW`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `DAILY_USAGE_SUM_VW` AS 
  select 
    cast(`v`.`startTime` as date) AS `startTime`,
    cast(`v`.`startTime` as date) AS `endTime`,
    `v`.`idAccountingResourceType` AS `idAccountingResourceType`,
    `v`.`resourceType` AS `resourceType`,
    sum(`v`.`resourceUnits`) AS `resourceUnits`,
    `v`.`idEnterprise` AS `idEnterprise`,
    `v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,
    `v`.`enterpriseName` AS `enterpriseName`,
    `v`.`virtualDataCenter` AS `virtualDataCenter` 
  from 
    `HOURLY_USAGE_MAX_VW` `v` 
  group by 
    `v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

#
# Definition for the `HOURLY_USAGE_SUM_VW` view : 
#

DROP VIEW IF EXISTS `HOURLY_USAGE_SUM_VW`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `HOURLY_USAGE_SUM_VW` AS 
  select 
    `v`.`startTime` AS `startTime`,
    `v`.`endTime` AS `endTime`,
    `v`.`idAccountingResourceType` AS `idAccountingResourceType`,
    `v`.`resourceType` AS `resourceType`,
    sum(`v`.`resourceUnits`) AS `resourceUnits`,
    `v`.`idEnterprise` AS `idEnterprise`,
    `v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,
    `v`.`enterpriseName` AS `enterpriseName`,
    `v`.`virtualDataCenter` AS `virtualDataCenter` 
  from 
    `HOURLY_USAGE_MAX_VW` `v` 
  group by 
    `v`.`startTime`,`v`.`endTime`,`v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

#
# Definition for the `MONTHLY_USAGE_SUM_VW` view : 
#

DROP VIEW IF EXISTS `MONTHLY_USAGE_SUM_VW`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `MONTHLY_USAGE_SUM_VW` AS 
  select 
    cast((`v`.`startTime` - interval (dayofmonth(`v`.`startTime`) - 1) day) as date) AS `startTime`,
    last_day(`v`.`startTime`) AS `endTime`,
    `v`.`idAccountingResourceType` AS `idAccountingResourceType`,
    `v`.`resourceType` AS `resourceType`,
    sum(`v`.`resourceUnits`) AS `resourceUnits`,
    `v`.`idEnterprise` AS `idEnterprise`,
    `v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,
    `v`.`enterpriseName` AS `enterpriseName`,
    `v`.`virtualDataCenter` AS `virtualDataCenter` 
  from 
    `HOURLY_USAGE_MAX_VW` `v` 
  group by 
    `v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

#
# Data for the `chargeback_simple` table  (LIMIT 0,500)
#

# INSERT INTO `chargeback_simple` (`idAccountingResourceType`, `resourceType`, `costPerHour`) VALUES 
#   (1,'cpu',0.03),
#   (2,'ram',5E-5),
#   (3,'hd',2.5E-11),
#   (4,'externalstorage',5E-11),
#   (5,'ipaddress',0.01);

COMMIT;

