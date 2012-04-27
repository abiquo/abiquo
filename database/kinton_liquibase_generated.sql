-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: kinton_master_changelog.xml
-- Ran at: 4/27/12 12:47 PM
-- Against: root@destevez.bcn.abiquo.com@jdbc:mysql://10.60.12.230:3306/kinton
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE `kinton`.`DATABASECHANGELOGLOCK` (`ID` INT NOT NULL, `LOCKED` TINYINT(1) NOT NULL, `LOCKGRANTED` DATETIME, `LOCKEDBY` VARCHAR(255), CONSTRAINT `PK_DATABASECHANGELOGLOCK` PRIMARY KEY (`ID`));

INSERT INTO `kinton`.`DATABASECHANGELOGLOCK` (`ID`, `LOCKED`) VALUES (1, 0);

-- Lock Database
-- Create Database Change Log Table
CREATE TABLE `kinton`.`DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35), `DESCRIPTION` VARCHAR(255), `COMMENTS` VARCHAR(255), `TAG` VARCHAR(255), `LIQUIBASE` VARCHAR(20), CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

-- Changeset kinton-2.0-ga.xml::1335522742615-1::destevezg (generated)::(Checksum: 3:88726a6f37fe54108519b05ad6fcbff5)
CREATE TABLE `kinton`.`accounting_event_detail` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-1', '2.0.3', '3:88726a6f37fe54108519b05ad6fcbff5', 1);

-- Changeset kinton-2.0-ga.xml::1335522742615-2::destevezg (generated)::(Checksum: 3:7aa4559e931e684780726ced0bdd7f2e)
CREATE TABLE `kinton`.`accounting_event_ips` (`idIPsAccountingEvent` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `startTime` TIMESTAMP, `stopTime` TIMESTAMP, `consolidated` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_IPS` PRIMARY KEY (`idIPsAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-2', '2.0.3', '3:7aa4559e931e684780726ced0bdd7f2e', 2);

-- Changeset kinton-2.0-ga.xml::1335522742615-3::destevezg (generated)::(Checksum: 3:6810486eebc70f182f018d06b3e25a0b)
CREATE TABLE `kinton`.`accounting_event_storage` (`idStorageAccountingEvent` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` VARCHAR(50), `resourceName` VARCHAR(511), `idVM` INT UNSIGNED, `idStorageTier` INT UNSIGNED, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `sizeReserved` BIGINT UNSIGNED NOT NULL, `startTime` TIMESTAMP, `stopTime` TIMESTAMP, `consolidated` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_STORAGE` PRIMARY KEY (`idStorageAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-3', '2.0.3', '3:6810486eebc70f182f018d06b3e25a0b', 3);

-- Changeset kinton-2.0-ga.xml::1335522742615-4::destevezg (generated)::(Checksum: 3:ae699b1d2e21928d5d5379767950f40c)
CREATE TABLE `kinton`.`accounting_event_vlan` (`idVLANAccountingEvent` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `startTime` TIMESTAMP, `stopTime` TIMESTAMP, `consolidated` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_VLAN` PRIMARY KEY (`idVLANAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-4', '2.0.3', '3:ae699b1d2e21928d5d5379767950f40c', 4);

-- Changeset kinton-2.0-ga.xml::1335522742615-5::destevezg (generated)::(Checksum: 3:79a333ca85b35bd9e04a7ed9aecffe72)
CREATE TABLE `kinton`.`accounting_event_vm` (`idVMAccountingEvent` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `cpu` INT UNSIGNED NOT NULL, `ram` INT UNSIGNED NOT NULL, `hd` BIGINT UNSIGNED NOT NULL, `startTime` TIMESTAMP, `stopTime` TIMESTAMP, `consolidated` BIT DEFAULT 0 NOT NULL, `costCode` INT, `hypervisorType` VARCHAR(255), `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_VM` PRIMARY KEY (`idVMAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-5', '2.0.3', '3:79a333ca85b35bd9e04a7ed9aecffe72', 5);

-- Changeset kinton-2.0-ga.xml::1335522742615-6::destevezg (generated)::(Checksum: 3:c32bbf075db7c5933ca3cce5df660aa9)
CREATE TABLE `kinton`.`alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-6', '2.0.3', '3:c32bbf075db7c5933ca3cce5df660aa9', 6);

-- Changeset kinton-2.0-ga.xml::1335522742615-7::destevezg (generated)::(Checksum: 3:b518e45dd85a26cde440580145fcddb4)
CREATE TABLE `kinton`.`apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-7', '2.0.3', '3:b518e45dd85a26cde440580145fcddb4', 7);

-- Changeset kinton-2.0-ga.xml::1335522742615-8::destevezg (generated)::(Checksum: 3:966996751618877d8c5c9d810821a619)
CREATE TABLE `kinton`.`auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-8', '2.0.3', '3:966996751618877d8c5c9d810821a619', 8);

-- Changeset kinton-2.0-ga.xml::1335522742615-9::destevezg (generated)::(Checksum: 3:447eb654eeabbcb662cb7dad38635820)
CREATE TABLE `kinton`.`auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-9', '2.0.3', '3:447eb654eeabbcb662cb7dad38635820', 9);

-- Changeset kinton-2.0-ga.xml::1335522742615-10::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `kinton`.`auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-10', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 10);

-- Changeset kinton-2.0-ga.xml::1335522742615-11::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `kinton`.`category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-11', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 11);

-- Changeset kinton-2.0-ga.xml::1335522742615-12::destevezg (generated)::(Checksum: 3:66bfde02b829cfd6bd7d09dace5bd851)
CREATE TABLE `kinton`.`chargeback_simple` (`idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(20) NOT NULL, `costPerHour` DECIMAL(15,12) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHARGEBACK_SIMPLE` PRIMARY KEY (`idAccountingResourceType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-12', '2.0.3', '3:66bfde02b829cfd6bd7d09dace5bd851', 12);

-- Changeset kinton-2.0-ga.xml::1335522742615-13::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `kinton`.`chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-13', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 13);

-- Changeset kinton-2.0-ga.xml::1335522742615-14::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `kinton`.`cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-14', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 14);

-- Changeset kinton-2.0-ga.xml::1335522742615-15::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `kinton`.`costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-15', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 15);

-- Changeset kinton-2.0-ga.xml::1335522742615-16::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `kinton`.`costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-16', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 16);

-- Changeset kinton-2.0-ga.xml::1335522742615-17::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `kinton`.`currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-17', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 17);

-- Changeset kinton-2.0-ga.xml::1335522742615-18::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `kinton`.`datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-18', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 18);

-- Changeset kinton-2.0-ga.xml::1335522742615-19::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `kinton`.`datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-19', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 19);

-- Changeset kinton-2.0-ga.xml::1335522742615-20::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `kinton`.`datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-20', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 20);

-- Changeset kinton-2.0-ga.xml::1335522742615-21::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `kinton`.`dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-21', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 21);

-- Changeset kinton-2.0-ga.xml::1335522742615-22::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `kinton`.`dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-22', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 22);

-- Changeset kinton-2.0-ga.xml::1335522742615-23::destevezg (generated)::(Checksum: 3:ffd62de872535e1f2da1cac582b3c9d5)
CREATE TABLE `kinton`.`disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-23', '2.0.3', '3:ffd62de872535e1f2da1cac582b3c9d5', 23);

-- Changeset kinton-2.0-ga.xml::1335522742615-24::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `kinton`.`diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-24', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 24);

-- Changeset kinton-2.0-ga.xml::1335522742615-25::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `kinton`.`enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-25', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 25);

-- Changeset kinton-2.0-ga.xml::1335522742615-26::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-26', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 26);

-- Changeset kinton-2.0-ga.xml::1335522742615-27::destevezg (generated)::(Checksum: 3:3e94390d029bf8e6061698eb5628d573)
CREATE TABLE `kinton`.`enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-27', '2.0.3', '3:3e94390d029bf8e6061698eb5628d573', 27);

-- Changeset kinton-2.0-ga.xml::1335522742615-28::destevezg (generated)::(Checksum: 3:be4693925397c572062f1fab8c984362)
CREATE TABLE `kinton`.`enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50), `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-28', '2.0.3', '3:be4693925397c572062f1fab8c984362', 28);

-- Changeset kinton-2.0-ga.xml::1335522742615-29::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `kinton`.`enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-29', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 29);

-- Changeset kinton-2.0-ga.xml::1335522742615-30::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `kinton`.`enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-30', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 30);

-- Changeset kinton-2.0-ga.xml::1335522742615-31::destevezg (generated)::(Checksum: 3:f6211931acdcc03c90d5c6d208a910b9)
CREATE TABLE `kinton`.`heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-31', '2.0.3', '3:f6211931acdcc03c90d5c6d208a910b9', 31);

-- Changeset kinton-2.0-ga.xml::1335522742615-32::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `kinton`.`hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-32', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 32);

-- Changeset kinton-2.0-ga.xml::1335522742615-33::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `kinton`.`initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-33', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 33);

-- Changeset kinton-2.0-ga.xml::1335522742615-34::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `kinton`.`ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-34', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 34);

-- Changeset kinton-2.0-ga.xml::1335522742615-35::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `kinton`.`license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-35', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 35);

-- Changeset kinton-2.0-ga.xml::1335522742615-36::destevezg (generated)::(Checksum: 3:38e9d9ed33afac96b738855a00109f9c)
CREATE TABLE `kinton`.`log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-36', '2.0.3', '3:38e9d9ed33afac96b738855a00109f9c', 36);

-- Changeset kinton-2.0-ga.xml::1335522742615-37::destevezg (generated)::(Checksum: 3:0a23cc6bd4adfbad1eaa59b5b7da2f2e)
CREATE TABLE `kinton`.`metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-37', '2.0.3', '3:0a23cc6bd4adfbad1eaa59b5b7da2f2e', 37);

-- Changeset kinton-2.0-ga.xml::1335522742615-38::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `kinton`.`network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-38', '2.0.3', '3:acc689e893485790d347e737a96a3812', 38);

-- Changeset kinton-2.0-ga.xml::1335522742615-39::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `kinton`.`network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-39', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 39);

-- Changeset kinton-2.0-ga.xml::1335522742615-40::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `kinton`.`node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-40', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 40);

-- Changeset kinton-2.0-ga.xml::1335522742615-41::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `kinton`.`node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-41', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 41);

-- Changeset kinton-2.0-ga.xml::1335522742615-42::destevezg (generated)::(Checksum: 3:b6fc7632116240a776aa00853de6bcad)
CREATE TABLE `kinton`.`nodenetwork` (`idNode` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-42', '2.0.3', '3:b6fc7632116240a776aa00853de6bcad', 42);

-- Changeset kinton-2.0-ga.xml::1335522742615-43::destevezg (generated)::(Checksum: 3:6952f964ce37833b8144613d3cf11344)
CREATE TABLE `kinton`.`noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-43', '2.0.3', '3:6952f964ce37833b8144613d3cf11344', 43);

-- Changeset kinton-2.0-ga.xml::1335522742615-44::destevezg (generated)::(Checksum: 3:72bf3673a02388e2bc0da52ae70e5fce)
CREATE TABLE `kinton`.`nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-44', '2.0.3', '3:72bf3673a02388e2bc0da52ae70e5fce', 44);

-- Changeset kinton-2.0-ga.xml::1335522742615-45::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `kinton`.`nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-45', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 45);

-- Changeset kinton-2.0-ga.xml::1335522742615-46::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-46', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 46);

-- Changeset kinton-2.0-ga.xml::1335522742615-47::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `kinton`.`ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-47', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 47);

-- Changeset kinton-2.0-ga.xml::1335522742615-48::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `kinton`.`ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-48', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 48);

-- Changeset kinton-2.0-ga.xml::1335522742615-49::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `kinton`.`ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-49', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 49);

-- Changeset kinton-2.0-ga.xml::1335522742615-50::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `kinton`.`physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-50', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 50);

-- Changeset kinton-2.0-ga.xml::1335522742615-51::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `kinton`.`pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-51', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 51);

-- Changeset kinton-2.0-ga.xml::1335522742615-52::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `kinton`.`pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-52', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 52);

-- Changeset kinton-2.0-ga.xml::1335522742615-53::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `kinton`.`pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-53', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 53);

-- Changeset kinton-2.0-ga.xml::1335522742615-54::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `kinton`.`privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-54', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 54);

-- Changeset kinton-2.0-ga.xml::1335522742615-55::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `kinton`.`rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-55', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 55);

-- Changeset kinton-2.0-ga.xml::1335522742615-56::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `kinton`.`rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-56', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 56);

-- Changeset kinton-2.0-ga.xml::1335522742615-57::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `kinton`.`rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-57', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 57);

-- Changeset kinton-2.0-ga.xml::1335522742615-58::destevezg (generated)::(Checksum: 3:ed4ae73f975deb795a4e2fe4980ada26)
CREATE TABLE `kinton`.`register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-58', '2.0.3', '3:ed4ae73f975deb795a4e2fe4980ada26', 58);

-- Changeset kinton-2.0-ga.xml::1335522742615-59::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `kinton`.`remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-59', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 59);

-- Changeset kinton-2.0-ga.xml::1335522742615-60::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `kinton`.`repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-60', '2.0.3', '3:71b499bb915394af534df15335b9daed', 60);

-- Changeset kinton-2.0-ga.xml::1335522742615-61::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `kinton`.`role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-61', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 61);

-- Changeset kinton-2.0-ga.xml::1335522742615-62::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `kinton`.`role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-62', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 62);

-- Changeset kinton-2.0-ga.xml::1335522742615-63::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `kinton`.`roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-63', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 63);

-- Changeset kinton-2.0-ga.xml::1335522742615-64::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `kinton`.`session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-64', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 64);

-- Changeset kinton-2.0-ga.xml::1335522742615-65::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `kinton`.`storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-65', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 65);

-- Changeset kinton-2.0-ga.xml::1335522742615-66::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `kinton`.`storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-66', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 66);

-- Changeset kinton-2.0-ga.xml::1335522742615-67::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `kinton`.`system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-67', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 67);

-- Changeset kinton-2.0-ga.xml::1335522742615-68::destevezg (generated)::(Checksum: 3:31486daf8f610a7250344cb981627a60)
CREATE TABLE `kinton`.`tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-68', '2.0.3', '3:31486daf8f610a7250344cb981627a60', 68);

-- Changeset kinton-2.0-ga.xml::1335522742615-69::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `kinton`.`tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-69', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 69);

-- Changeset kinton-2.0-ga.xml::1335522742615-70::destevezg (generated)::(Checksum: 3:e5d525478dfcdecb18cc7cad873150c3)
CREATE TABLE `kinton`.`ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-70', '2.0.3', '3:e5d525478dfcdecb18cc7cad873150c3', 70);

-- Changeset kinton-2.0-ga.xml::1335522742615-71::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `kinton`.`user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-71', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 71);

-- Changeset kinton-2.0-ga.xml::1335522742615-72::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `kinton`.`vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-72', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 72);

-- Changeset kinton-2.0-ga.xml::1335522742615-73::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `kinton`.`vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-73', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 73);

-- Changeset kinton-2.0-ga.xml::1335522742615-74::destevezg (generated)::(Checksum: 3:28fa564c45777149c6b4218b7e631c80)
CREATE TABLE `kinton`.`vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VDC_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-74', '2.0.3', '3:28fa564c45777149c6b4218b7e631c80', 74);

-- Changeset kinton-2.0-ga.xml::1335522742615-75::destevezg (generated)::(Checksum: 3:bc9ba0c28876d849c819915c84e9cd70)
CREATE TABLE `kinton`.`virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-75', '2.0.3', '3:bc9ba0c28876d849c819915c84e9cd70', 75);

-- Changeset kinton-2.0-ga.xml::1335522742615-76::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `kinton`.`virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-76', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 76);

-- Changeset kinton-2.0-ga.xml::1335522742615-77::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `kinton`.`virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-77', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 77);

-- Changeset kinton-2.0-ga.xml::1335522742615-78::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `kinton`.`virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-78', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 78);

-- Changeset kinton-2.0-ga.xml::1335522742615-79::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `kinton`.`virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-79', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 79);

-- Changeset kinton-2.0-ga.xml::1335522742615-80::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `kinton`.`virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-80', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 80);

-- Changeset kinton-2.0-ga.xml::1335522742615-81::destevezg (generated)::(Checksum: 3:62ecd79335be6ba7c6365fb60199052d)
CREATE TABLE `kinton`.`virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-81', '2.0.3', '3:62ecd79335be6ba7c6365fb60199052d', 81);

-- Changeset kinton-2.0-ga.xml::1335522742615-82::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `kinton`.`vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-82', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 82);

-- Changeset kinton-2.0-ga.xml::1335522742615-83::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `kinton`.`vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-83', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 83);

-- Changeset kinton-2.0-ga.xml::1335522742615-84::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `kinton`.`vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-84', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 84);

-- Changeset kinton-2.0-ga.xml::1335522742615-85::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `kinton`.`volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-85', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 85);

-- Changeset kinton-2.0-ga.xml::1335522742615-86::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `kinton`.`workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-86', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 86);

-- Changeset kinton-2.0-ga.xml::1335522742615-87::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `kinton`.`workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-87', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 87);

-- Changeset kinton-2.0-ga.xml::1335522742615-88::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `kinton`.`workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-88', '2.0.3', '3:71036d19125d40af990eb553c437374e', 88);

-- Changeset kinton-2.0-ga.xml::1335522742615-89::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `kinton`.`datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-89', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 89);

-- Changeset kinton-2.0-ga.xml::1335522742615-90::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-90', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 90);

-- Changeset kinton-2.0-ga.xml::1335522742615-92::destevezg (generated)::(Checksum: 3:39db06adeb41d3a986d04834d8609781)
ALTER TABLE `kinton`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-92', '2.0.3', '3:39db06adeb41d3a986d04834d8609781', 91);

-- Changeset kinton-2.0-ga.xml::1335522742615-93::destevezg (generated)::(Checksum: 3:ef59cbaeca0e42a4ec1583e0a2c37306)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-93', '2.0.3', '3:ef59cbaeca0e42a4ec1583e0a2c37306', 92);

-- Changeset kinton-2.0-ga.xml::1335522742615-94::destevezg (generated)::(Checksum: 3:12b2b3f5e6fdee97aa1af071c3ca3129)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-94', '2.0.3', '3:12b2b3f5e6fdee97aa1af071c3ca3129', 93);

-- Changeset kinton-2.0-ga.xml::1335522742615-95::destevezg (generated)::(Checksum: 3:aab159bccf255ef411d6f652295aac91)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-95', '2.0.3', '3:aab159bccf255ef411d6f652295aac91', 94);

-- Changeset kinton-2.0-ga.xml::1335522742615-96::destevezg (generated)::(Checksum: 3:2c2a3886ab85ac15a24d5b86278cee13)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-96', '2.0.3', '3:2c2a3886ab85ac15a24d5b86278cee13', 95);

-- Changeset kinton-2.0-ga.xml::1335522742615-97::destevezg (generated)::(Checksum: 3:7babbcfac31aa94742a0b7c852cbb75c)
ALTER TABLE `kinton`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-97', '2.0.3', '3:7babbcfac31aa94742a0b7c852cbb75c', 96);

-- Changeset kinton-2.0-ga.xml::1335522742615-98::destevezg (generated)::(Checksum: 3:e917f98533bb9aef158246f2b9ac3806)
ALTER TABLE `kinton`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-98', '2.0.3', '3:e917f98533bb9aef158246f2b9ac3806', 97);

-- Changeset kinton-2.0-ga.xml::1335522742615-99::destevezg (generated)::(Checksum: 3:380b349c2867c97f3069d1ddea7af2dc)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-99', '2.0.3', '3:380b349c2867c97f3069d1ddea7af2dc', 98);

-- Changeset kinton-2.0-ga.xml::1335522742615-100::destevezg (generated)::(Checksum: 3:6f74be1ae0f5ca600be744dc575c6b55)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-100', '2.0.3', '3:6f74be1ae0f5ca600be744dc575c6b55', 99);

-- Changeset kinton-2.0-ga.xml::1335522742615-101::destevezg (generated)::(Checksum: 3:7cac3426929736d26932e589efcd2dba)
ALTER TABLE `kinton`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-101', '2.0.3', '3:7cac3426929736d26932e589efcd2dba', 100);

-- Changeset kinton-2.0-ga.xml::1335522742615-102::destevezg (generated)::(Checksum: 3:8743ae41839e4a8c6e13b9b27c7c5100)
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-102', '2.0.3', '3:8743ae41839e4a8c6e13b9b27c7c5100', 101);

-- Changeset kinton-2.0-ga.xml::1335522742615-103::destevezg (generated)::(Checksum: 3:39f1295773e78d4bfc80735d014153c6)
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-103', '2.0.3', '3:39f1295773e78d4bfc80735d014153c6', 102);

-- Changeset kinton-2.0-ga.xml::1335522742615-104::destevezg (generated)::(Checksum: 3:b388d5c13eab7fc4ec7fcf6d82d2517c)
ALTER TABLE `kinton`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-104', '2.0.3', '3:b388d5c13eab7fc4ec7fcf6d82d2517c', 103);

-- Changeset kinton-2.0-ga.xml::1335522742615-105::destevezg (generated)::(Checksum: 3:3e8be0e2f2e71febf08072f5abb2337b)
ALTER TABLE `kinton`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-105', '2.0.3', '3:3e8be0e2f2e71febf08072f5abb2337b', 104);

-- Changeset kinton-2.0-ga.xml::1335522742615-106::destevezg (generated)::(Checksum: 3:9c85972815ba8590587f3e2a7baf8d2e)
ALTER TABLE `kinton`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-106', '2.0.3', '3:9c85972815ba8590587f3e2a7baf8d2e', 105);

-- Changeset kinton-2.0-ga.xml::1335522742615-107::destevezg (generated)::(Checksum: 3:e45f0e33e210d975f95aa06f5a472a31)
ALTER TABLE `kinton`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-107', '2.0.3', '3:e45f0e33e210d975f95aa06f5a472a31', 106);

-- Changeset kinton-2.0-ga.xml::1335522742615-108::destevezg (generated)::(Checksum: 3:685adf52299cb301be40ce79ea068f09)
ALTER TABLE `kinton`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-108', '2.0.3', '3:685adf52299cb301be40ce79ea068f09', 107);

-- Changeset kinton-2.0-ga.xml::1335522742615-109::destevezg (generated)::(Checksum: 3:54e0036e5c4653ab7a70eaa8b7adc969)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-109', '2.0.3', '3:54e0036e5c4653ab7a70eaa8b7adc969', 108);

-- Changeset kinton-2.0-ga.xml::1335522742615-110::destevezg (generated)::(Checksum: 3:c75595ecaf1f61870fe3be4ee1607a58)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-110', '2.0.3', '3:c75595ecaf1f61870fe3be4ee1607a58', 109);

-- Changeset kinton-2.0-ga.xml::1335522742615-111::destevezg (generated)::(Checksum: 3:d0e422554cd4e0db8c124dcdcdc3e861)
ALTER TABLE `kinton`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-111', '2.0.3', '3:d0e422554cd4e0db8c124dcdcdc3e861', 110);

-- Changeset kinton-2.0-ga.xml::1335522742615-112::destevezg (generated)::(Checksum: 3:fbefc45b254ad3dc7c2e08d64deb06e3)
ALTER TABLE `kinton`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-112', '2.0.3', '3:fbefc45b254ad3dc7c2e08d64deb06e3', 111);

-- Changeset kinton-2.0-ga.xml::1335522742615-113::destevezg (generated)::(Checksum: 3:56db749940a3b0de035482dce9f42af3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-113', '2.0.3', '3:56db749940a3b0de035482dce9f42af3', 112);

-- Changeset kinton-2.0-ga.xml::1335522742615-114::destevezg (generated)::(Checksum: 3:dee0fe179f63a7fff9a6a8e7459ef124)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-114', '2.0.3', '3:dee0fe179f63a7fff9a6a8e7459ef124', 113);

-- Changeset kinton-2.0-ga.xml::1335522742615-115::destevezg (generated)::(Checksum: 3:db0c334d194b39f67e8541f4a4c8b31a)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-115', '2.0.3', '3:db0c334d194b39f67e8541f4a4c8b31a', 114);

-- Changeset kinton-2.0-ga.xml::1335522742615-116::destevezg (generated)::(Checksum: 3:7700a7d110854e172f2f3252b1567293)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-116', '2.0.3', '3:7700a7d110854e172f2f3252b1567293', 115);

-- Changeset kinton-2.0-ga.xml::1335522742615-117::destevezg (generated)::(Checksum: 3:350df72b50bbdd1974350647a819ba36)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-117', '2.0.3', '3:350df72b50bbdd1974350647a819ba36', 116);

-- Changeset kinton-2.0-ga.xml::1335522742615-118::destevezg (generated)::(Checksum: 3:bc63c183d18becdad57fdb22ca2279b3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-118', '2.0.3', '3:bc63c183d18becdad57fdb22ca2279b3', 117);

-- Changeset kinton-2.0-ga.xml::1335522742615-119::destevezg (generated)::(Checksum: 3:570a0810c943c0ba338369b35c4facc3)
ALTER TABLE `kinton`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-119', '2.0.3', '3:570a0810c943c0ba338369b35c4facc3', 118);

-- Changeset kinton-2.0-ga.xml::1335522742615-120::destevezg (generated)::(Checksum: 3:36d32fb242d453bc21a77ae64ee5c23c)
ALTER TABLE `kinton`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-120', '2.0.3', '3:36d32fb242d453bc21a77ae64ee5c23c', 119);

-- Changeset kinton-2.0-ga.xml::1335522742615-121::destevezg (generated)::(Checksum: 3:a139f4550368879e9dc8127cf2208b32)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-121', '2.0.3', '3:a139f4550368879e9dc8127cf2208b32', 120);

-- Changeset kinton-2.0-ga.xml::1335522742615-122::destevezg (generated)::(Checksum: 3:14d64e301e24922cdccc4a2e745d788d)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-122', '2.0.3', '3:14d64e301e24922cdccc4a2e745d788d', 121);

-- Changeset kinton-2.0-ga.xml::1335522742615-123::destevezg (generated)::(Checksum: 3:e8ceada3c162ec371d3e31171195c0b2)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-123', '2.0.3', '3:e8ceada3c162ec371d3e31171195c0b2', 122);

-- Changeset kinton-2.0-ga.xml::1335522742615-124::destevezg (generated)::(Checksum: 3:f7d73df5dad5123e4901e04db283185e)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-124', '2.0.3', '3:f7d73df5dad5123e4901e04db283185e', 123);

-- Changeset kinton-2.0-ga.xml::1335522742615-125::destevezg (generated)::(Checksum: 3:4cf48f9241ea2f379f0c8acb839d6818)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-125', '2.0.3', '3:4cf48f9241ea2f379f0c8acb839d6818', 124);

-- Changeset kinton-2.0-ga.xml::1335522742615-126::destevezg (generated)::(Checksum: 3:7f80cb03ad6bfefe6034ca2a75988ee3)
ALTER TABLE `kinton`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-126', '2.0.3', '3:7f80cb03ad6bfefe6034ca2a75988ee3', 125);

-- Changeset kinton-2.0-ga.xml::1335522742615-127::destevezg (generated)::(Checksum: 3:314f329efbdbe1ceffd2b8335ac24754)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-127', '2.0.3', '3:314f329efbdbe1ceffd2b8335ac24754', 126);

-- Changeset kinton-2.0-ga.xml::1335522742615-128::destevezg (generated)::(Checksum: 3:0b8d008edf4729acede17f0436c857b6)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-128', '2.0.3', '3:0b8d008edf4729acede17f0436c857b6', 127);

-- Changeset kinton-2.0-ga.xml::1335522742615-129::destevezg (generated)::(Checksum: 3:40cfac0dcf4c56d309494dcec042d513)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-129', '2.0.3', '3:40cfac0dcf4c56d309494dcec042d513', 128);

-- Changeset kinton-2.0-ga.xml::1335522742615-130::destevezg (generated)::(Checksum: 3:590901f24718ac0ff77f3de502c8bf3f)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-130', '2.0.3', '3:590901f24718ac0ff77f3de502c8bf3f', 129);

-- Changeset kinton-2.0-ga.xml::1335522742615-131::destevezg (generated)::(Checksum: 3:4b4676b5d7cb3f195237d0a5ea3563c1)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-131', '2.0.3', '3:4b4676b5d7cb3f195237d0a5ea3563c1', 130);

-- Changeset kinton-2.0-ga.xml::1335522742615-132::destevezg (generated)::(Checksum: 3:a316da2bf6cfa6eab48b556edbcb1686)
ALTER TABLE `kinton`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-132', '2.0.3', '3:a316da2bf6cfa6eab48b556edbcb1686', 131);

-- Changeset kinton-2.0-ga.xml::1335522742615-133::destevezg (generated)::(Checksum: 3:c75b594b9fa56384d12679e3f3f39844)
ALTER TABLE `kinton`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-133', '2.0.3', '3:c75b594b9fa56384d12679e3f3f39844', 132);

-- Changeset kinton-2.0-ga.xml::1335522742615-134::destevezg (generated)::(Checksum: 3:6c2f073057a45a69c1b7db5f4ee07de1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-134', '2.0.3', '3:6c2f073057a45a69c1b7db5f4ee07de1', 133);

-- Changeset kinton-2.0-ga.xml::1335522742615-135::destevezg (generated)::(Checksum: 3:5ed1e047f733146bb1bb75cfbaa63f8e)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-135', '2.0.3', '3:5ed1e047f733146bb1bb75cfbaa63f8e', 134);

-- Changeset kinton-2.0-ga.xml::1335522742615-136::destevezg (generated)::(Checksum: 3:15014a2695966373e7a6cae113893ff1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-136', '2.0.3', '3:15014a2695966373e7a6cae113893ff1', 135);

-- Changeset kinton-2.0-ga.xml::1335522742615-137::destevezg (generated)::(Checksum: 3:f4ba13ebaac92029c85db4adfd4bb524)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-137', '2.0.3', '3:f4ba13ebaac92029c85db4adfd4bb524', 136);

-- Changeset kinton-2.0-ga.xml::1335522742615-138::destevezg (generated)::(Checksum: 3:99fb777debdd79e43a64df61c8aab9f1)
ALTER TABLE `kinton`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-138', '2.0.3', '3:99fb777debdd79e43a64df61c8aab9f1', 137);

-- Changeset kinton-2.0-ga.xml::1335522742615-139::destevezg (generated)::(Checksum: 3:381a734392ef762c6e4e727db64fdcdc)
ALTER TABLE `kinton`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-139', '2.0.3', '3:381a734392ef762c6e4e727db64fdcdc', 138);

-- Changeset kinton-2.0-ga.xml::1335522742615-140::destevezg (generated)::(Checksum: 3:0afc6b5a509fa965da8109ecf2444522)
ALTER TABLE `kinton`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-140', '2.0.3', '3:0afc6b5a509fa965da8109ecf2444522', 139);

-- Changeset kinton-2.0-ga.xml::1335522742615-141::destevezg (generated)::(Checksum: 3:6e1ac40f00f986ff6827ddffddc4417b)
ALTER TABLE `kinton`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-141', '2.0.3', '3:6e1ac40f00f986ff6827ddffddc4417b', 140);

-- Changeset kinton-2.0-ga.xml::1335522742615-142::destevezg (generated)::(Checksum: 3:0e3df47ebc27a0e2d3d449f673c3436e)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-142', '2.0.3', '3:0e3df47ebc27a0e2d3d449f673c3436e', 141);

-- Changeset kinton-2.0-ga.xml::1335522742615-143::destevezg (generated)::(Checksum: 3:b7d29b45d463a86fe85165ccd981b2a4)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-143', '2.0.3', '3:b7d29b45d463a86fe85165ccd981b2a4', 142);

-- Changeset kinton-2.0-ga.xml::1335522742615-144::destevezg (generated)::(Checksum: 3:0a3a0dce75328a168956b34f2a166124)
ALTER TABLE `kinton`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-144', '2.0.3', '3:0a3a0dce75328a168956b34f2a166124', 143);

-- Changeset kinton-2.0-ga.xml::1335522742615-145::destevezg (generated)::(Checksum: 3:a9f68a95692fd4cb61d1ab7f54a6add0)
ALTER TABLE `kinton`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-145', '2.0.3', '3:a9f68a95692fd4cb61d1ab7f54a6add0', 144);

-- Changeset kinton-2.0-ga.xml::1335522742615-146::destevezg (generated)::(Checksum: 3:51bc92d1f8458a6758f84d5e40c6f88d)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-146', '2.0.3', '3:51bc92d1f8458a6758f84d5e40c6f88d', 145);

-- Changeset kinton-2.0-ga.xml::1335522742615-147::destevezg (generated)::(Checksum: 3:732046a805d961eb44971fd636d52594)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-147', '2.0.3', '3:732046a805d961eb44971fd636d52594', 146);

-- Changeset kinton-2.0-ga.xml::1335522742615-148::destevezg (generated)::(Checksum: 3:31e41feafb066c2be3b1cc2857f49208)
ALTER TABLE `kinton`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-148', '2.0.3', '3:31e41feafb066c2be3b1cc2857f49208', 147);

-- Changeset kinton-2.0-ga.xml::1335522742615-149::destevezg (generated)::(Checksum: 3:466a1d498f0c2740539a49b128d9b6de)
ALTER TABLE `kinton`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-149', '2.0.3', '3:466a1d498f0c2740539a49b128d9b6de', 148);

-- Changeset kinton-2.0-ga.xml::1335522742615-150::destevezg (generated)::(Checksum: 3:2c7e302fae12c8e84f18f3dba9f5c40c)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-150', '2.0.3', '3:2c7e302fae12c8e84f18f3dba9f5c40c', 149);

-- Changeset kinton-2.0-ga.xml::1335522742615-151::destevezg (generated)::(Checksum: 3:b632cd05a5d8ab67cfad62318a6feacd)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-151', '2.0.3', '3:b632cd05a5d8ab67cfad62318a6feacd', 150);

-- Changeset kinton-2.0-ga.xml::1335522742615-152::destevezg (generated)::(Checksum: 3:1361f06e4430e3572388dc130cf3f6ae)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-152', '2.0.3', '3:1361f06e4430e3572388dc130cf3f6ae', 151);

-- Changeset kinton-2.0-ga.xml::1335522742615-153::destevezg (generated)::(Checksum: 3:fa81f7866672e0064abe59ce544d16ee)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-153', '2.0.3', '3:fa81f7866672e0064abe59ce544d16ee', 152);

-- Changeset kinton-2.0-ga.xml::1335522742615-154::destevezg (generated)::(Checksum: 3:21d1330e830eda559d49619da27d4a2d)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-154', '2.0.3', '3:21d1330e830eda559d49619da27d4a2d', 153);

-- Changeset kinton-2.0-ga.xml::1335522742615-155::destevezg (generated)::(Checksum: 3:6d20670c1bdea037fa86029d32e95c4e)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-155', '2.0.3', '3:6d20670c1bdea037fa86029d32e95c4e', 154);

-- Changeset kinton-2.0-ga.xml::1335522742615-156::destevezg (generated)::(Checksum: 3:f93371517d74a4eab3924ee099b26c53)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-156', '2.0.3', '3:f93371517d74a4eab3924ee099b26c53', 155);

-- Changeset kinton-2.0-ga.xml::1335522742615-157::destevezg (generated)::(Checksum: 3:206402c502be46899203f3badd9d8ec7)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-157', '2.0.3', '3:206402c502be46899203f3badd9d8ec7', 156);

-- Changeset kinton-2.0-ga.xml::1335522742615-158::destevezg (generated)::(Checksum: 3:4b1fa941844e12bc85b4a6f54dca3194)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-158', '2.0.3', '3:4b1fa941844e12bc85b4a6f54dca3194', 157);

-- Changeset kinton-2.0-ga.xml::1335522742615-159::destevezg (generated)::(Checksum: 3:72511dad2d82e148a62eadbe7350381a)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-159', '2.0.3', '3:72511dad2d82e148a62eadbe7350381a', 158);

-- Changeset kinton-2.0-ga.xml::1335522742615-160::destevezg (generated)::(Checksum: 3:913ff3701711d54eee00d1fb8b58389d)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-160', '2.0.3', '3:913ff3701711d54eee00d1fb8b58389d', 159);

-- Changeset kinton-2.0-ga.xml::1335522742615-161::destevezg (generated)::(Checksum: 3:082da0be85a69a1ebfc2a09ae9ec94e4)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-161', '2.0.3', '3:082da0be85a69a1ebfc2a09ae9ec94e4', 160);

-- Changeset kinton-2.0-ga.xml::1335522742615-162::destevezg (generated)::(Checksum: 3:17e6766e834c47a60ded2846e28a0374)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-162', '2.0.3', '3:17e6766e834c47a60ded2846e28a0374', 161);

-- Changeset kinton-2.0-ga.xml::1335522742615-163::destevezg (generated)::(Checksum: 3:3887fe4ffa2434ffbe9fdc910afa8538)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-163', '2.0.3', '3:3887fe4ffa2434ffbe9fdc910afa8538', 162);

-- Changeset kinton-2.0-ga.xml::1335522742615-164::destevezg (generated)::(Checksum: 3:4f435a6168a0a18bb1d0714d21361b71)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-164', '2.0.3', '3:4f435a6168a0a18bb1d0714d21361b71', 163);

-- Changeset kinton-2.0-ga.xml::1335522742615-165::destevezg (generated)::(Checksum: 3:1af426b7fdcaf93f1b45d9d5e8aa1bdc)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-165', '2.0.3', '3:1af426b7fdcaf93f1b45d9d5e8aa1bdc', 164);

-- Changeset kinton-2.0-ga.xml::1335522742615-166::destevezg (generated)::(Checksum: 3:f7c4c533470ece45570602545702a16a)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-166', '2.0.3', '3:f7c4c533470ece45570602545702a16a', 165);

-- Changeset kinton-2.0-ga.xml::1335522742615-167::destevezg (generated)::(Checksum: 3:c93821360ad0b67578d33e4371fca936)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-167', '2.0.3', '3:c93821360ad0b67578d33e4371fca936', 166);

-- Changeset kinton-2.0-ga.xml::1335522742615-168::destevezg (generated)::(Checksum: 3:d44c1e70581845fad25b877d07c96182)
ALTER TABLE `kinton`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-168', '2.0.3', '3:d44c1e70581845fad25b877d07c96182', 167);

-- Changeset kinton-2.0-ga.xml::1335522742615-169::destevezg (generated)::(Checksum: 3:dd8fdabb1b5568d9fbbe0342574633c4)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-169', '2.0.3', '3:dd8fdabb1b5568d9fbbe0342574633c4', 168);

-- Changeset kinton-2.0-ga.xml::1335522742615-170::destevezg (generated)::(Checksum: 3:818367047e672790ff3ac9c3d13cec5e)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-170', '2.0.3', '3:818367047e672790ff3ac9c3d13cec5e', 169);

-- Changeset kinton-2.0-ga.xml::1335522742615-171::destevezg (generated)::(Checksum: 3:9424adc44a74d3737f24c3d5b5d812fe)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-171', '2.0.3', '3:9424adc44a74d3737f24c3d5b5d812fe', 170);

-- Changeset kinton-2.0-ga.xml::1335522742615-172::destevezg (generated)::(Checksum: 3:46215ad45eb3e72d8ee65ee7be941e1a)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-172', '2.0.3', '3:46215ad45eb3e72d8ee65ee7be941e1a', 171);

-- Changeset kinton-2.0-ga.xml::1335522742615-173::destevezg (generated)::(Checksum: 3:d09aba94d1f59c8262f5399abec69cc5)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-173', '2.0.3', '3:d09aba94d1f59c8262f5399abec69cc5', 172);

-- Changeset kinton-2.0-ga.xml::1335522742615-174::destevezg (generated)::(Checksum: 3:e717cc8fc89cb02d56bd262979378060)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-174', '2.0.3', '3:e717cc8fc89cb02d56bd262979378060', 173);

-- Changeset kinton-2.0-ga.xml::1335522742615-175::destevezg (generated)::(Checksum: 3:c2d45eafaae6aa722440adfe212203c3)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-175', '2.0.3', '3:c2d45eafaae6aa722440adfe212203c3', 174);

-- Changeset kinton-2.0-ga.xml::1335522742615-176::destevezg (generated)::(Checksum: 3:6ebfd010b6b125ab61846d74710cdb9c)
ALTER TABLE `kinton`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-176', '2.0.3', '3:6ebfd010b6b125ab61846d74710cdb9c', 175);

-- Changeset kinton-2.0-ga.xml::1335522742615-177::destevezg (generated)::(Checksum: 3:6b5855cbae91f21f0eecc23d7d31e7d6)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-177', '2.0.3', '3:6b5855cbae91f21f0eecc23d7d31e7d6', 176);

-- Changeset kinton-2.0-ga.xml::1335522742615-178::destevezg (generated)::(Checksum: 3:8a5267a1f2d46f48e685e311a9a2bb38)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-178', '2.0.3', '3:8a5267a1f2d46f48e685e311a9a2bb38', 177);

-- Changeset kinton-2.0-ga.xml::1335522742615-179::destevezg (generated)::(Checksum: 3:ced9008c983144fe8e36c11ee1d24a81)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-179', '2.0.3', '3:ced9008c983144fe8e36c11ee1d24a81', 178);

-- Changeset kinton-2.0-ga.xml::1335522742615-180::destevezg (generated)::(Checksum: 3:fa97c2e5bfec4084f586a823469f3b1f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-180', '2.0.3', '3:fa97c2e5bfec4084f586a823469f3b1f', 179);

-- Changeset kinton-2.0-ga.xml::1335522742615-181::destevezg (generated)::(Checksum: 3:f57948dcc96f12c0699213820b6b756f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-181', '2.0.3', '3:f57948dcc96f12c0699213820b6b756f', 180);

-- Changeset kinton-2.0-ga.xml::1335522742615-182::destevezg (generated)::(Checksum: 3:e1ca982714c144ed2d5ac20561bc6657)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-182', '2.0.3', '3:e1ca982714c144ed2d5ac20561bc6657', 181);

-- Changeset kinton-2.0-ga.xml::1335522742615-183::destevezg (generated)::(Checksum: 3:746c3e281a036d6ada1b5e3fd95a4696)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-183', '2.0.3', '3:746c3e281a036d6ada1b5e3fd95a4696', 182);

-- Changeset kinton-2.0-ga.xml::1335522742615-184::destevezg (generated)::(Checksum: 3:b3376ebce0a03581c19a96f0c56bbd66)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-184', '2.0.3', '3:b3376ebce0a03581c19a96f0c56bbd66', 183);

-- Changeset kinton-2.0-ga.xml::1335522742615-185::destevezg (generated)::(Checksum: 3:c16c6d833472e00f707818c0d317b44b)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-185', '2.0.3', '3:c16c6d833472e00f707818c0d317b44b', 184);

-- Changeset kinton-2.0-ga.xml::1335522742615-186::destevezg (generated)::(Checksum: 3:420c8225285f8d8e374d50a1ed9e237e)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-186', '2.0.3', '3:420c8225285f8d8e374d50a1ed9e237e', 185);

-- Changeset kinton-2.0-ga.xml::1335522742615-187::destevezg (generated)::(Checksum: 3:8f456a92ce7ba6f2b3625311cb2a47cf)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-187', '2.0.3', '3:8f456a92ce7ba6f2b3625311cb2a47cf', 186);

-- Changeset kinton-2.0-ga.xml::1335522742615-188::destevezg (generated)::(Checksum: 3:c5ab00bc6a57c9809eb1be93120180ba)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-188', '2.0.3', '3:c5ab00bc6a57c9809eb1be93120180ba', 187);

-- Changeset kinton-2.0-ga.xml::1335522742615-189::destevezg (generated)::(Checksum: 3:1035cb414581bebcdacdd2a161d19a41)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-189', '2.0.3', '3:1035cb414581bebcdacdd2a161d19a41', 188);

-- Changeset kinton-2.0-ga.xml::1335522742615-190::destevezg (generated)::(Checksum: 3:f7d0b7bcff44df8f076be460f1172674)
ALTER TABLE `kinton`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-190', '2.0.3', '3:f7d0b7bcff44df8f076be460f1172674', 189);

-- Changeset kinton-2.0-ga.xml::1335522742615-191::destevezg (generated)::(Checksum: 3:2724c06259dee3fa38ec1a3bd14d32b5)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-191', '2.0.3', '3:2724c06259dee3fa38ec1a3bd14d32b5', 190);

-- Changeset kinton-2.0-ga.xml::1335522742615-192::destevezg (generated)::(Checksum: 3:c1c812e559c885292b18196d35ef708e)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-192', '2.0.3', '3:c1c812e559c885292b18196d35ef708e', 191);

-- Changeset kinton-2.0-ga.xml::1335522742615-193::destevezg (generated)::(Checksum: 3:5d48f5fe3bb5c924a4e96180cc3d9790)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-193', '2.0.3', '3:5d48f5fe3bb5c924a4e96180cc3d9790', 192);

-- Changeset kinton-2.0-ga.xml::1335522742615-194::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `kinton`.`category`(`name`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-194', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 193);

-- Changeset kinton-2.0-ga.xml::1335522742615-195::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `kinton`.`user`(`user`, `authType`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-195', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 194);

-- Changeset kinton-2.0-ga.xml::1335522742615-197::destevezg (generated)::(Checksum: 3:032abaa8efe4842be5d8d0ef9dbd0b97)
CREATE VIEW `kinton`.`HOURLY_USAGE_MAX_VW` AS select `kinton`.`accounting_event_detail`.`startTime` AS `startTime`,`kinton`.`accounting_event_detail`.`endTime` AS `endTime`,`kinton`.`accounting_event_detail`.`idAccountingResourceType` AS `idAccountingResourceType`,`kinton`.`accounting_event_detail`.`resourceType` AS `resourceType`,`kinton`.`accounting_event_detail`.`resourceName` AS `resourceName`,max(`kinton`.`accounting_event_detail`.`resourceUnits`) AS `resourceUnits`,`kinton`.`accounting_event_detail`.`idEnterprise` AS `idEnterprise`,`kinton`.`accounting_event_detail`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`kinton`.`accounting_event_detail`.`idVirtualApp` AS `idVirtualApp`,`kinton`.`accounting_event_detail`.`idVirtualMachine` AS `idVirtualMachine`,`kinton`.`accounting_event_detail`.`enterpriseName` AS `enterpriseName`,`kinton`.`accounting_event_detail`.`virtualDataCenter` AS `virtualDataCenter`,`kinton`.`accounting_event_detail`.`virtualApp` AS `virtualApp`,`kinton`.`accounting_event_detail`.`virtualMachine` AS `virtualMachine` from `kinton`.`accounting_event_detail` group by `kinton`.`accounting_event_detail`.`startTime`,`kinton`.`accounting_event_detail`.`endTime`,`kinton`.`accounting_event_detail`.`idAccountingResourceType`,`kinton`.`accounting_event_detail`.`resourceType`,`kinton`.`accounting_event_detail`.`resourceName`,`kinton`.`accounting_event_detail`.`idEnterprise`,`kinton`.`accounting_event_detail`.`idVirtualDataCenter`,`kinton`.`accounting_event_detail`.`idVirtualApp`,`kinton`.`accounting_event_detail`.`idVirtualMachine`,`kinton`.`accounting_event_detail`.`enterpriseName`,`kinton`.`accounting_event_detail`.`virtualDataCenter`,`kinton`.`accounting_event_detail`.`virtualApp`,`kinton`.`accounting_event_detail`.`virtualMachine`;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-197', '2.0.3', '3:032abaa8efe4842be5d8d0ef9dbd0b97', 195);

-- Changeset kinton-2.0-ga.xml::1335522742615-196::destevezg (generated)::(Checksum: 3:1c73e28a1784ca7b3aaeee56e3bead65)
CREATE VIEW `kinton`.`DAILY_USAGE_SUM_VW` AS select cast(`v`.`startTime` as date) AS `startTime`,cast(`v`.`startTime` as date) AS `endTime`,`v`.`idAccountingResourceType` AS `idAccountingResourceType`,`v`.`resourceType` AS `resourceType`,sum(`v`.`resourceUnits`) AS `resourceUnits`,`v`.`idEnterprise` AS `idEnterprise`,`v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`v`.`enterpriseName` AS `enterpriseName`,`v`.`virtualDataCenter` AS `virtualDataCenter` from `kinton`.`HOURLY_USAGE_MAX_VW` `v` group by cast(`v`.`startTime` as date),`v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-196', '2.0.3', '3:1c73e28a1784ca7b3aaeee56e3bead65', 196);

-- Changeset kinton-2.0-ga.xml::1335522742615-198::destevezg (generated)::(Checksum: 3:0ea1a52f54de73b7a8e2caff0fd3c7e2)
CREATE VIEW `kinton`.`HOURLY_USAGE_SUM_VW` AS select `v`.`startTime` AS `startTime`,`v`.`endTime` AS `endTime`,`v`.`idAccountingResourceType` AS `idAccountingResourceType`,`v`.`resourceType` AS `resourceType`,sum(`v`.`resourceUnits`) AS `resourceUnits`,`v`.`idEnterprise` AS `idEnterprise`,`v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`v`.`enterpriseName` AS `enterpriseName`,`v`.`virtualDataCenter` AS `virtualDataCenter` from `kinton`.`HOURLY_USAGE_MAX_VW` `v` group by `v`.`startTime`,`v`.`endTime`,`v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-198', '2.0.3', '3:0ea1a52f54de73b7a8e2caff0fd3c7e2', 197);

-- Changeset kinton-2.0-ga.xml::1335522742615-199::destevezg (generated)::(Checksum: 3:a203e298aa7cc2977d87b568e5ccf833)
CREATE VIEW `kinton`.`LAST_HOUR_USAGE_IPS_VW` AS select `kinton`.`accounting_event_ips`.`idIPsAccountingEvent` AS `idIPsAccountingEvent`,`kinton`.`accounting_event_ips`.`idEnterprise` AS `idEnterprise`,`kinton`.`accounting_event_ips`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`kinton`.`accounting_event_ips`.`ip` AS `ip`,`kinton`.`accounting_event_ips`.`startTime` AS `startTime`,`kinton`.`accounting_event_ips`.`stopTime` AS `stopTime`,(unix_timestamp(`kinton`.`accounting_event_ips`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_ips`.`startTime`)) AS `DELTA_TIME`,from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,`kinton`.`virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,`kinton`.`enterprise`.`name` AS `VIRTUAL_ENTERPRISE` from ((`kinton`.`accounting_event_ips` join `kinton`.`virtualdatacenter` on((`kinton`.`accounting_event_ips`.`idVirtualDataCenter` = `kinton`.`virtualdatacenter`.`idVirtualDataCenter`))) join `kinton`.`enterprise` on((`kinton`.`accounting_event_ips`.`idEnterprise` = `kinton`.`enterprise`.`idEnterprise`))) where (isnull(`kinton`.`accounting_event_ips`.`stopTime`) or ((`kinton`.`accounting_event_ips`.`stopTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_ips`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_ips`.`startTime`)) > 3600)) or ((`kinton`.`accounting_event_ips`.`startTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_ips`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_ips`.`startTime`)) <= 3600)));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-199', '2.0.3', '3:a203e298aa7cc2977d87b568e5ccf833', 198);

-- Changeset kinton-2.0-ga.xml::1335522742615-200::destevezg (generated)::(Checksum: 3:f87734d02534716ba5b0df6716846cd1)
CREATE VIEW `kinton`.`LAST_HOUR_USAGE_STORAGE_VW` AS select `kinton`.`accounting_event_storage`.`idStorageAccountingEvent` AS `idStorageAccountingEvent`,`kinton`.`accounting_event_storage`.`idVM` AS `idVM`,`kinton`.`accounting_event_storage`.`idEnterprise` AS `idEnterprise`,`kinton`.`accounting_event_storage`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`kinton`.`accounting_event_storage`.`idVirtualApp` AS `idVirtualApp`,`kinton`.`accounting_event_storage`.`idResource` AS `idResource`,`kinton`.`accounting_event_storage`.`resourceName` AS `resourceName`,`kinton`.`accounting_event_storage`.`idStorageTier` AS `idStorageTier`,`kinton`.`accounting_event_storage`.`sizeReserved` AS `sizeReserved`,`kinton`.`accounting_event_storage`.`startTime` AS `startTime`,`kinton`.`accounting_event_storage`.`stopTime` AS `stopTime`,(unix_timestamp(`kinton`.`accounting_event_storage`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_storage`.`startTime`)) AS `DELTA_TIME`,from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,`kinton`.`virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,`kinton`.`enterprise`.`name` AS `VIRTUAL_ENTERPRISE` from ((`kinton`.`accounting_event_storage` join `kinton`.`virtualdatacenter` on((`kinton`.`accounting_event_storage`.`idVirtualDataCenter` = `kinton`.`virtualdatacenter`.`idVirtualDataCenter`))) join `kinton`.`enterprise` on((`kinton`.`accounting_event_storage`.`idEnterprise` = `kinton`.`enterprise`.`idEnterprise`))) where (isnull(`kinton`.`accounting_event_storage`.`stopTime`) or ((`kinton`.`accounting_event_storage`.`stopTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_storage`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_storage`.`startTime`)) > 3600)) or ((`kinton`.`accounting_event_storage`.`startTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_storage`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_storage`.`startTime`)) <= 3600)));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-200', '2.0.3', '3:f87734d02534716ba5b0df6716846cd1', 199);

-- Changeset kinton-2.0-ga.xml::1335522742615-201::destevezg (generated)::(Checksum: 3:4e4a37a3792a369035734ef2f54ff479)
CREATE VIEW `kinton`.`LAST_HOUR_USAGE_VLAN_VW` AS select `kinton`.`accounting_event_vlan`.`idVLANAccountingEvent` AS `idVLANAccountingEvent`,`kinton`.`accounting_event_vlan`.`idEnterprise` AS `idEnterprise`,`kinton`.`accounting_event_vlan`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`kinton`.`accounting_event_vlan`.`network_name` AS `networkName`,`kinton`.`accounting_event_vlan`.`startTime` AS `startTime`,`kinton`.`accounting_event_vlan`.`stopTime` AS `stopTime`,(unix_timestamp(`kinton`.`accounting_event_vlan`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vlan`.`startTime`)) AS `DELTA_TIME`,from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,`kinton`.`virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,`kinton`.`enterprise`.`name` AS `VIRTUAL_ENTERPRISE` from ((`kinton`.`accounting_event_vlan` join `kinton`.`virtualdatacenter` on((`kinton`.`accounting_event_vlan`.`idVirtualDataCenter` = `kinton`.`virtualdatacenter`.`idVirtualDataCenter`))) join `kinton`.`enterprise` on((`kinton`.`accounting_event_vlan`.`idEnterprise` = `kinton`.`enterprise`.`idEnterprise`))) where (isnull(`kinton`.`accounting_event_vlan`.`stopTime`) or ((`kinton`.`accounting_event_vlan`.`stopTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_vlan`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vlan`.`startTime`)) > 3600)) or ((`kinton`.`accounting_event_vlan`.`startTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_vlan`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vlan`.`startTime`)) <= 3600)));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-201', '2.0.3', '3:4e4a37a3792a369035734ef2f54ff479', 200);

-- Changeset kinton-2.0-ga.xml::1335522742615-202::destevezg (generated)::(Checksum: 3:677961d7562ace20a0553ba34ff254ed)
CREATE VIEW `kinton`.`LAST_HOUR_USAGE_VM_VW` AS select `kinton`.`accounting_event_vm`.`idVMAccountingEvent` AS `idVMAccountingEvent`,`kinton`.`accounting_event_vm`.`idVM` AS `idVM`,`kinton`.`accounting_event_vm`.`idEnterprise` AS `idEnterprise`,`kinton`.`accounting_event_vm`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`kinton`.`accounting_event_vm`.`idVirtualApp` AS `idVirtualApp`,`kinton`.`accounting_event_vm`.`cpu` AS `cpu`,`kinton`.`accounting_event_vm`.`ram` AS `ram`,`kinton`.`accounting_event_vm`.`hd` AS `hd`,`kinton`.`accounting_event_vm`.`startTime` AS `startTime`,`kinton`.`accounting_event_vm`.`stopTime` AS `stopTime`,`kinton`.`accounting_event_vm`.`costCode` AS `costCode`,`kinton`.`accounting_event_vm`.`hypervisorType` AS `hypervisorType`,(unix_timestamp(`kinton`.`accounting_event_vm`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vm`.`startTime`)) AS `DELTA_TIME`,from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600))) AS `ROUNDED_HOUR`,concat(if(isnull(`kinton`.`virtualmachine`.`description`),'',substr(`kinton`.`virtualmachine`.`description`,1,120)),' - ',`kinton`.`virtualmachine`.`name`) AS `VIRTUAL_MACHINE`,`kinton`.`virtualapp`.`name` AS `VIRTUAL_APP`,`kinton`.`virtualdatacenter`.`name` AS `VIRTUAL_DATACENTER`,`kinton`.`enterprise`.`name` AS `VIRTUAL_ENTERPRISE` from ((((`kinton`.`accounting_event_vm` join `kinton`.`virtualmachine` on((`kinton`.`accounting_event_vm`.`idVM` = `kinton`.`virtualmachine`.`idVM`))) join `kinton`.`virtualapp` on((`kinton`.`accounting_event_vm`.`idVirtualApp` = `kinton`.`virtualapp`.`idVirtualApp`))) join `kinton`.`virtualdatacenter` on((`kinton`.`accounting_event_vm`.`idVirtualDataCenter` = `kinton`.`virtualdatacenter`.`idVirtualDataCenter`))) join `kinton`.`enterprise` on((`kinton`.`accounting_event_vm`.`idEnterprise` = `kinton`.`enterprise`.`idEnterprise`))) where (isnull(`kinton`.`accounting_event_vm`.`stopTime`) or ((`kinton`.`accounting_event_vm`.`stopTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_vm`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vm`.`startTime`)) > 3600)) or ((`kinton`.`accounting_event_vm`.`startTime` > from_unixtime((-(3600) + (truncate((unix_timestamp(now()) / 3600),0) * 3600)))) and ((unix_timestamp(`kinton`.`accounting_event_vm`.`stopTime`) - unix_timestamp(`kinton`.`accounting_event_vm`.`startTime`)) <= 3600)));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-202', '2.0.3', '3:677961d7562ace20a0553ba34ff254ed', 201);

-- Changeset kinton-2.0-ga.xml::1335522742615-203::destevezg (generated)::(Checksum: 3:b594ba104f0734976b7878a1b2ea1384)
CREATE VIEW `kinton`.`MONTHLY_USAGE_SUM_VW` AS select cast((`v`.`startTime` - interval (dayofmonth(`v`.`startTime`) - 1) day) as date) AS `startTime`,last_day(`v`.`startTime`) AS `endTime`,`v`.`idAccountingResourceType` AS `idAccountingResourceType`,`v`.`resourceType` AS `resourceType`,sum(`v`.`resourceUnits`) AS `resourceUnits`,`v`.`idEnterprise` AS `idEnterprise`,`v`.`idVirtualDataCenter` AS `idVirtualDataCenter`,`v`.`enterpriseName` AS `enterpriseName`,`v`.`virtualDataCenter` AS `virtualDataCenter` from `kinton`.`HOURLY_USAGE_MAX_VW` `v` group by cast((`v`.`startTime` - interval (dayofmonth(`v`.`startTime`) - 1) day) as date),`v`.`idAccountingResourceType`,`v`.`resourceType`,`v`.`idEnterprise`,`v`.`idVirtualDataCenter`,`v`.`enterpriseName`,`v`.`virtualDataCenter`;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create View', 'EXECUTED', 'kinton-2.0-ga.xml', '1335522742615-203', '2.0.3', '3:b594ba104f0734976b7878a1b2ea1384', 202);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-1::destevezg (generated)::(Checksum: 3:de8cf3a70531e851e5914cc06037b9b4)
INSERT INTO `kinton`.`enterprise_resources_stats` (`extStorageReserved`, `extStorageUsed`, `idEnterprise`, `localStorageReserved`, `localStorageUsed`, `memoryReserved`, `memoryUsed`, `publicIPsReserved`, `publicIPsUsed`, `repositoryReserved`, `repositoryUsed`, `vCpuReserved`, `vCpuUsed`, `version_c`, `vlanReserved`, `vlanUsed`) VALUES (0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-1', '2.0.3', '3:de8cf3a70531e851e5914cc06037b9b4', 203);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-2::destevezg (generated)::(Checksum: 3:6ee2a7bfbc6e05a148230fba90faca87)
INSERT INTO `kinton`.`apps_library` (`idEnterprise`, `id_apps_library`, `version_c`) VALUES (1, 1, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-2', '2.0.3', '3:6ee2a7bfbc6e05a148230fba90faca87', 204);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-3::destevezg (generated)::(Checksum: 3:88bde47d6d00dbf2776dc0382fb3e34d)
INSERT INTO `kinton`.`license` (`data`, `idLicense`, `version_c`) VALUES ('BAmEs6WKlxr6gOzvjwQtwJah9GCm77qMZPhYC0MHoNkvn/Lu+6d3nkFdJO2ZsgIs/CNhpfV/3OMnnpGvHR668RGjh+dRUN8yDhbMvxGN7hAIYiiL1kl8Gt/uLYtlZOWUBqPdYSMjh9pNY0sKlfH5gIxavNWlu1Tp6fMzmgkioU4TfIixai+BwxYB5kihe1ZGhTR9mZ5HM4Tg6xn96CX1T8eGKiFrc0LBwzUaB4Rt2YNtvSfiyzl2xiBEqc2hhyhiOrvEYfPdmxSdDMt5wH/1pRbohavbTbVK0C+VDFcIKYi4DcbyFRvYWdj1WKQo8UMNuqx5tdyyOHoSoGnMToX94g==', 1, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-3', '2.0.3', '3:88bde47d6d00dbf2776dc0382fb3e34d', 205);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-4::destevezg (generated)::(Checksum: 3:1b6e28cec49ba1fe6aa832e958625b61)
INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (1, 'ENTERPRISE_ENUMERATE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (2, 'ENTERPRISE_ADMINISTER_ALL', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (3, 'ENTERPRISE_RESOURCE_SUMMARY_ENT', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (4, 'PHYS_DC_ENUMERATE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (5, 'PHYS_DC_RETRIEVE_RESOURCE_USAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (6, 'PHYS_DC_MANAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (7, 'PHYS_DC_RETRIEVE_DETAILS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (8, 'PHYS_DC_ALLOW_MODIFY_SERVERS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (9, 'PHYS_DC_ALLOW_MODIFY_NETWORK', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (10, 'PHYS_DC_ALLOW_MODIFY_STORAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (11, 'PHYS_DC_ALLOW_MODIFY_ALLOCATION', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (12, 'VDC_ENUMERATE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (13, 'VDC_MANAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (14, 'VDC_MANAGE_VAPP', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (15, 'VDC_MANAGE_NETWORK', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (16, 'VDC_MANAGE_STORAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (17, 'VAPP_CUSTOMISE_SETTINGS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (18, 'VAPP_DEPLOY_UNDEPLOY', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (19, 'VAPP_ASSIGN_NETWORK', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (20, 'VAPP_ASSIGN_VOLUME', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (21, 'VAPP_PERFORM_ACTIONS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (22, 'VAPP_CREATE_STATEFUL', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (23, 'VAPP_CREATE_INSTANCE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (24, 'APPLIB_VIEW', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (25, 'APPLIB_ALLOW_MODIFY', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (26, 'APPLIB_UPLOAD_IMAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (27, 'APPLIB_MANAGE_REPOSITORY', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (28, 'APPLIB_DOWNLOAD_IMAGE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (29, 'APPLIB_MANAGE_CATEGORIES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (30, 'USERS_VIEW', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (31, 'USERS_MANAGE_ENTERPRISE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (32, 'USERS_MANAGE_USERS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (33, 'USERS_MANAGE_OTHER_ENTERPRISES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (34, 'USERS_PROHIBIT_VDC_RESTRICTION', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (35, 'USERS_VIEW_PRIVILEGES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (36, 'USERS_MANAGE_ROLES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (37, 'USERS_MANAGE_ROLES_OTHER_ENTERPRISES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (38, 'USERS_MANAGE_SYSTEM_ROLES', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (39, 'USERS_MANAGE_LDAP_GROUP', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (40, 'USERS_ENUMERATE_CONNECTED', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (41, 'SYSCONFIG_VIEW', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (42, 'SYSCONFIG_ALLOW_MODIFY', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (43, 'EVENTLOG_VIEW_ENTERPRISE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (44, 'EVENTLOG_VIEW_ALL', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (45, 'APPLIB_VM_COST_CODE', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (46, 'USERS_MANAGE_ENTERPRISE_BRANDING', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (47, 'SYSCONFIG_SHOW_REPORTS', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (48, 'USERS_DEFINE_AS_MANAGER', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (49, 'PRICING_VIEW', 0);

INSERT INTO `kinton`.`privilege` (`idPrivilege`, `name`, `version_c`) VALUES (50, 'PRICING_MANAGE', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x50)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-4', '2.0.3', '3:1b6e28cec49ba1fe6aa832e958625b61', 206);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-5::destevezg (generated)::(Checksum: 3:b3f0c67b2f6f4cac3086fefc627fe152)
INSERT INTO `kinton`.`enterprise` (`chef_client`, `chef_client_certificate`, `chef_url`, `chef_validator`, `chef_validator_certificate`, `cpuHard`, `cpuSoft`, `hdHard`, `hdSoft`, `idEnterprise`, `idPricingTemplate`, `isReservationRestricted`, `name`, `publicIPHard`, `publicIPSoft`, `ramHard`, `ramSoft`, `repositoryHard`, `repositorySoft`, `storageHard`, `storageSoft`, `version_c`, `vlanHard`, `vlanSoft`) VALUES (NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 1, NULL, 0, 'Abiquo', 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-5', '2.0.3', '3:b3f0c67b2f6f4cac3086fefc627fe152', 207);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-6::destevezg (generated)::(Checksum: 3:d8fe6ecf9187dc7c3fb35d05bdb8ef4b)
INSERT INTO `kinton`.`workload_fit_policy_rule` (`fitPolicy`, `id`, `idDatacenter`, `version_c`) VALUES ('PROGRESSIVE', 0, NULL, 1);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-6', '2.0.3', '3:d8fe6ecf9187dc7c3fb35d05bdb8ef4b', 208);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-7::destevezg (generated)::(Checksum: 3:6bee14978f59c4c0246e57db5acd1f54)
INSERT INTO `kinton`.`cloud_usage_stats` (`idDataCenter`, `numEnterprisesCreated`, `numUsersCreated`, `numVDCCreated`, `publicIPsReserved`, `publicIPsTotal`, `publicIPsUsed`, `serversRunning`, `serversTotal`, `storageReserved`, `storageTotal`, `storageUsed`, `vCpuReserved`, `vCpuTotal`, `vCpuUsed`, `vMachinesRunning`, `vMachinesTotal`, `vMemoryReserved`, `vMemoryTotal`, `vMemoryUsed`, `vStorageReserved`, `vStorageTotal`, `vStorageUsed`, `version_c`, `vlanReserved`, `vlanUsed`) VALUES (-1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-7', '2.0.3', '3:6bee14978f59c4c0246e57db5acd1f54', 209);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-8::destevezg (generated)::(Checksum: 3:cd8857111c2fccaff489ea361fc63b31)
INSERT INTO `kinton`.`auth_group` (`description`, `id`, `name`, `version_c`) VALUES ('Generic', 1, 'GENERIC', 0);

INSERT INTO `kinton`.`auth_group` (`description`, `id`, `name`, `version_c`) VALUES ('Flex client main menu group', 2, 'MAIN', 0);

INSERT INTO `kinton`.`auth_group` (`description`, `id`, `name`, `version_c`) VALUES ('Flex and server Users Management', 3, 'USER', 0);

INSERT INTO `kinton`.`auth_group` (`description`, `id`, `name`, `version_c`) VALUES ('Flex and server Appliance Library Management', 4, 'APPLIANCE_LIBRARY', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x4)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-8', '2.0.3', '3:cd8857111c2fccaff489ea361fc63b31', 210);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-9::destevezg (generated)::(Checksum: 3:fb31e2ba074d9a7bc9ba7688e9706dc8)
INSERT INTO `kinton`.`auth_serverresource` (`description`, `id`, `idGroup`, `idRole`, `name`, `version_c`) VALUES ('Login Service', 1, 1, 2, 'LOGIN', 0);

INSERT INTO `kinton`.`auth_serverresource` (`description`, `id`, `idGroup`, `idRole`, `name`, `version_c`) VALUES ('Security to retrieve the whole list of enterprises', 2, 3, 1, 'ENTERPRISE_GET_ALL_ENTERPRISES', 0);

INSERT INTO `kinton`.`auth_serverresource` (`description`, `id`, `idGroup`, `idRole`, `name`, `version_c`) VALUES ('Security to call method getEnterprises in UserCommand', 3, 3, 3, 'ENTERPRISE_GET_ENTERPRISES', 0);

INSERT INTO `kinton`.`auth_serverresource` (`description`, `id`, `idGroup`, `idRole`, `name`, `version_c`) VALUES ('Security to call method getUsers in UserCommand', 4, 3, 3, 'USER_GETUSERS', 0);

INSERT INTO `kinton`.`auth_serverresource` (`description`, `id`, `idGroup`, `idRole`, `name`, `version_c`) VALUES ('Security to retrieve the whole list of users', 5, 3, 1, 'USER_GET_ALL_USERS', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x5)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-9', '2.0.3', '3:fb31e2ba074d9a7bc9ba7688e9706dc8', 211);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-10::destevezg (generated)::(Checksum: 3:01a0899358975c7e845845aaf5fb68c0)
INSERT INTO `kinton`.`enterprise_properties_map` (`enterprise_properties`, `map_key`, `value`, `version_c`) VALUES (1, 'Support e-mail', 'support@abiquo.com', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-10', '2.0.3', '3:01a0899358975c7e845845aaf5fb68c0', 212);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-11::destevezg (generated)::(Checksum: 3:4e1226a6a36ae74e4ed982725b2d4c54)
INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (1, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (2, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (3, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (4, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (5, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (6, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (7, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (8, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (9, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (10, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (11, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (12, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (13, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (14, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (15, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (16, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (17, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (18, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (19, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (20, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (21, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (22, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (23, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (24, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (25, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (26, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (27, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (28, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (29, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (30, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (31, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (32, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (33, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (34, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (35, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (36, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (37, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (38, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (39, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (40, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (41, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (42, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (43, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (44, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (45, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (47, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (48, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (49, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (50, 1, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (3, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (12, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (13, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (14, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (15, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (16, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (17, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (18, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (19, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (20, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (21, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (22, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (23, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (24, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (25, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (26, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (27, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (28, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (29, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (30, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (32, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (34, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (43, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (48, 3, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (12, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (14, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (17, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (18, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (19, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (20, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (21, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (22, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (23, 2, 0);

INSERT INTO `kinton`.`roles_privileges` (`idPrivilege`, `idRole`, `version_c`) VALUES (43, 2, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x83)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-11', '2.0.3', '3:4e1226a6a36ae74e4ed982725b2d4c54', 213);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-12::destevezg (generated)::(Checksum: 3:038b8f4afc535e869fd3474162ffdb8b)
INSERT INTO `kinton`.`role` (`blocked`, `idEnterprise`, `idRole`, `name`, `version_c`) VALUES (1, NULL, 1, 'CLOUD_ADMIN', 0);

INSERT INTO `kinton`.`role` (`blocked`, `idEnterprise`, `idRole`, `name`, `version_c`) VALUES (0, NULL, 2, 'USER', 0);

INSERT INTO `kinton`.`role` (`blocked`, `idEnterprise`, `idRole`, `name`, `version_c`) VALUES (0, NULL, 3, 'ENTERPRISE_ADMIN', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x3)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-12', '2.0.3', '3:038b8f4afc535e869fd3474162ffdb8b', 214);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-13::destevezg (generated)::(Checksum: 3:089c056314989f6686309ead50938bcb)
INSERT INTO `kinton`.`enterprise_properties` (`enterprise`, `idProperties`, `version_c`) VALUES (1, 1, 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-13', '2.0.3', '3:089c056314989f6686309ead50938bcb', 215);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-14::destevezg (generated)::(Checksum: 3:488515bb737e87d54efc39654c6bc22a)
INSERT INTO `kinton`.`category` (`idCategory`, `isDefault`, `isErasable`, `name`, `version_c`) VALUES (1, 1, 0, 'Others', 0);

INSERT INTO `kinton`.`category` (`idCategory`, `isDefault`, `isErasable`, `name`, `version_c`) VALUES (2, 0, 1, 'Database servers', 0);

INSERT INTO `kinton`.`category` (`idCategory`, `isDefault`, `isErasable`, `name`, `version_c`) VALUES (4, 0, 1, 'Applications servers', 0);

INSERT INTO `kinton`.`category` (`idCategory`, `isDefault`, `isErasable`, `name`, `version_c`) VALUES (5, 0, 1, 'Web servers', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x4)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-14', '2.0.3', '3:488515bb737e87d54efc39654c6bc22a', 216);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-15::destevezg (generated)::(Checksum: 3:f01c1cb4f6bf52b684078287b4b72312)
INSERT INTO `kinton`.`network` (`network_id`, `uuid`, `version_c`) VALUES (1, '6cd20366-72e5-11df-8f9d-002564aeca80', 1);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-15', '2.0.3', '3:f01c1cb4f6bf52b684078287b4b72312', 217);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-16::destevezg (generated)::(Checksum: 3:82bf294985b44cab8f8c9ce2114366c7)
INSERT INTO `kinton`.`alerts` (`description`, `id`, `tstamp`, `type`, `value`, `version_c`) VALUES (NULL, '1', '2012-04-23 10:25:41.0', 'REGISTER', 'LATER', 0);

INSERT INTO `kinton`.`alerts` (`description`, `id`, `tstamp`, `type`, `value`, `version_c`) VALUES (NULL, '2', '2012-04-23 10:25:41.0', 'HEARTBEAT', 'YES', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x2)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-16', '2.0.3', '3:82bf294985b44cab8f8c9ce2114366c7', 218);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-17::destevezg (generated)::(Checksum: 3:1671992947b92f05f598e92f29b3de10)
INSERT INTO `kinton`.`user` (`active`, `authType`, `availableVirtualDatacenters`, `creationDate`, `description`, `email`, `idEnterprise`, `idRole`, `idUser`, `locale`, `name`, `password`, `surname`, `user`, `version_c`) VALUES (1, 'ABIQUO', NULL, '2012-04-27 10:25:34.0', 'Main administrator', '', 1, 1, 1, 'en_US', 'Cloud', 'c69a39bd64ffb77ea7ee3369dce742f3', 'Administrator', 'admin', 0);

INSERT INTO `kinton`.`user` (`active`, `authType`, `availableVirtualDatacenters`, `creationDate`, `description`, `email`, `idEnterprise`, `idRole`, `idUser`, `locale`, `name`, `password`, `surname`, `user`, `version_c`) VALUES (1, 'ABIQUO', NULL, '2012-04-27 10:25:34.0', 'Standard user', '', 1, 2, 2, 'en_US', 'Standard', 'c69a39bd64ffb77ea7ee3369dce742f3', 'User', 'user', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x2)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-17', '2.0.3', '3:1671992947b92f05f598e92f29b3de10', 219);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-18::destevezg (generated)::(Checksum: 3:2b477b3eddc17cd40dc60c24f7c11e64)
INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.applibrary.ovfpackagesDownloadingProgressUpdateInterval', 1, '10', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.applibrary.virtualimageUploadProgressUpdateInterval', 2, '10', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('URL of Abiquo web page', 'client.dashboard.abiquoURL', 3, 'http://www.abiquo.org', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Allow (1) or deny (0) access to the ''Users'' section', 'client.dashboard.allowUsersAccess', 4, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Set to 1 to show an Alert with the text found in Startup_Alert.txt file', 'client.dashboard.showStartUpAlert', 5, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Google Maps will be centered by default at this longitude value', 'client.infra.googleMapsDefaultLatitude', 6, '41.3825', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Google Maps will be centered by default at this latitude value', 'client.infra.googleMapsDefaultLongitude', 7, '2.176944', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Google Maps will be centered by default with this zoom level value', 'client.infra.googleMapsDefaultZoom', 8, '4', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('The map''s Google key used in infrastructure section', 'client.infra.googleMapskey', 9, '0', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time, in seconds, that applications waits Google Maps to load. After that, application considers that Google Maps service is temporarily unavailable, and is not used', 'client.infra.googleMapsLadTimeOut', 10, '10', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.infra.InfrastructureUpdateInterval', 11, '30', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('URL to display UCS Manager Interface', 'client.infra.ucsManagerLink', 12, '/ucsm/ucsm.jnlp', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.metering.meteringUpdateInterval', 13, '10', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Number entries that will appear when listing IP addresses in different parts of the application', 'client.network.numberIpAdressesPerPage', 14, '25', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('This is the path to the Enterprise logo used in the app', 'client.theme.defaultEnterpriseLogoPath', 15, 'themes/abicloudDefault/logo.png', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Number of enterprises per page that will appear in User Management', 'client.user.numberEnterprisesPerPage', 16, '25', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Number of users per page that will appear in User Management', 'client.user.numberUsersPerPage', 17, '25', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Allow (1) or deny (0) virtual machine remote access', 'client.virtual.allowVMRemoteAccess', 18, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.virtual.virtualApplianceDeployingUpdateInterval', 19, '5', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.virtual.virtualAppliancesUpdateInterval', 20, '30', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('URL of Abiquo virtual image upload limitations web page', 'client.virtual.moreInfoAboutUploadLimitations', 21, 'http://wiki.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Minimum value for vlan ID', 'client.infra.vlanIdMin', 22, '2', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Maximum value for vlan ID', 'client.infra.vlanIdMax', 23, '4094', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds', 'client.dashboard.dashboardUpdateInterval', 24, '30', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Default Hypervisor password used when creating Physical Machines', 'client.infra.defaultHypervisorPassword', 25, 'temporal', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Default Hypervisor port used when creating Physical Machines', 'client.infra.defaultHypervisorPort', 26, '8889', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Default Hypervisor user used when creating Physical Machines', 'client.infra.defaultHypervisorUser', 27, 'root', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Comma separated values, with the allowed sizes when creating or editing a VolumeManagement', 'client.storage.volumeMaxSizeValues', 28, '1,2,4,8,16,32,64,128,256', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Time interval in seconds to refresh missing virtual image conversions', 'client.virtual.virtualImagesRefreshConversionsInterval', 29, '5', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('URL displayed when the header enterprise logo is clicked', 'client.main.enterpriseLogoURL', 30, 'http://www.abiquo.com', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('URL displayed when the report header logo is clicked, if empty the report button will not be displayed', 'client.main.billingUrl', 31, '', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Allow (1) or deny (0) user to change their password', 'client.main.disableChangePassword', 32, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Redirect to this URL after logout (empty -> login screen)', 'client.logout.url', 33, '', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Allow (1) or deny (0) user to logout', 'client.main.allowLogout', 34, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Show (1) or hide (0) the help icon within the plateform', 'client.wiki.showHelp', 35, '1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Use (1) or not (0) the default help URL within the plateform', 'client.wiki.showDefaultHelp', 36, '0', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('The default URL opened when not specific help URL is specified', 'client.wiki.defaultURL', 37, 'http://community.abiquo.com/display/ABI20/Abiquo+Documentation+Home', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('datacenter creation wiki', 'client.wiki.infra.createDatacenter', 38, 'http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('datacenter edition wiki', 'client.wiki.infra.editDatacenter', 39, 'http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('remote service edition wiki', 'client.wiki.infra.editRemoteService', 40, 'http://community.abiquo.com/display/ABI20/Managing+Datacenters#ManagingDatacenters-RemoteServices', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('physical machine creation wiki', 'client.wiki.infra.createPhysicalMachine', 41, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingPhysicalMachinesonStandardRacks', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('mail notification wiki', 'client.wiki.infra.mailNotification', 42, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-SendingEmailNotifications', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Datastore manager wiki', 'client.wiki.infra.addDatastore', 43, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DatastoreManagement', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('rack creation wiki', 'client.wiki.infra.createRack', 44, 'http://community.abiquo.com/display/ABI20/Manage+Racks#ManageRacks-CreatingRacks', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('multiple physical machine creation wiki', 'client.wiki.infra.createMultiplePhysicalMachine', 45, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-CreatingMultiplePhysicalMachines', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('public vlan creation wiki', 'client.wiki.network.publicVlan', 46, 'http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-CreateVLANsforPublicNetworks', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('static routes wiki', 'client.wiki.network.staticRoutes', 47, 'http://community.abiquo.com/display/ABI20/Manage+Network+Configuration#ManageNetworkConfiguration-ConfiguringStaticRoutesUsingDHCP', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('storage device creation wiki', 'client.wiki.storage.storageDevice', 48, 'http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-ManagedStorage', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('storage pool creation wiki', 'client.wiki.storage.storagePool', 49, 'http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-StoragePools', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('tier edition wiki', 'client.wiki.storage.tier', 50, 'http://community.abiquo.com/display/ABI20/Managing+External+Storage#ManagingExternalStorage-TierManagement', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('global rules wiki', 'client.wiki.allocation.global', 51, 'http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('datacenter rules wiki', 'client.wiki.allocation.datacenter', 52, 'http://community.abiquo.com/display/ABI20/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('virtual datacenter creation wiki', 'client.wiki.vdc.createVdc', 53, 'http://community.abiquo.com/display/ABI20/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('virtual app creation wiki', 'client.wiki.vdc.createVapp', 54, 'http://community.abiquo.com/display/ABI20/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('VDC private network creation wiki', 'client.wiki.vdc.createPrivateNetwork', 55, 'http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-CreateaPrivateVLAN', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('VDC public network creation wiki', 'client.wiki.vdc.createPublicNetwork', 56, 'http://community.abiquo.com/display/ABI20/Manage+Networks#ManageNetworks-PublicIPReservation', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('VDC virtual volume creation wiki', 'client.wiki.vdc.createVolume', 57, 'http://community.abiquo.com/display/ABI20/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Virtual Machine edition wiki', 'client.wiki.vm.editVirtualMachine', 58, 'http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Bundles VM wiki', 'client.wiki.vm.bundleVirtualMachine', 59, 'http://community.abiquo.com/display/ABI20/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-CreateanInstance', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Network Interface creation wiki', 'client.wiki.vm.createNetworkInterface', 60, 'http://community.abiquo.com/display/ABI20/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Virtual Machine instance creation wiki', 'client.wiki.vm.createInstance', 61, 'http://community.abiquo.com/display/ABI20/Create+Virtual+Machine+instances', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Virtual Machine stateful creation wiki', 'client.wiki.vm.createStateful', 62, 'http://community.abiquo.com/display/ABI20/Create+Persistent+Virtual+Machines', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Capture Virtual Machine wiki', 'client.wiki.vm.captureVirtualMachine', 63, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-WorkingwithImportedVirtualMachines', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Show more info when deploying', 'client.wiki.vm.deployInfo', 64, '', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Virtual Image upload wiki', 'client.wiki.apps.uploadVM', 65, 'http://community.abiquo.com/display/ABI20/Adding+VM+Templates+to+the+Appliance+Library#AddingVMTemplatestotheApplianceLibrary-UploadingfromtheLocalFilesystem', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Enterprise creation wiki', 'client.wiki.user.createEnterprise', 66, 'http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Datacenter Limits wiki', 'client.wiki.user.dataCenterLimits', 67, 'http://community.abiquo.com/display/ABI20/Manage+Enterprises#ManageEnterprises-EdittheEnterprise%27sDatacenters', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('User creation wiki', 'client.wiki.user.createUser', 68, 'http://community.abiquo.com/display/ABI20/Manage+Users#ManageUsers-CreatingorEditingaUser', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Role creation wiki', 'client.wiki.user.createRole', 69, 'http://community.abiquo.com/display/ABI20/Manage+Roles+and+Privileges', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Currency creation wiki', 'client.wiki.pricing.createCurrency', 70, 'http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CurrenciesTab', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('create pricing template wiki', 'client.wiki.pricing.createTemplate', 71, 'http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-PricingModelsTab', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('create pricing cost code wiki', 'client.wiki.pricing.createCostCode', 72, 'http://community.abiquo.com/display/ABI20/Pricing+View#PricingView-CostCodesTab', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Configuration wiki', 'client.wiki.config.general', 73, 'http://community.abiquo.com/display/ABI20/Configuration+view', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Heartbeat configuration wiki', 'client.wiki.config.heartbeat', 74, 'http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-Heartbeating', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Licence configuration wiki', 'client.wiki.config.licence', 75, 'http://community.abiquo.com/display/ABI20/Configuration+view#ConfigurationView-LicenseManagement', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('Registration wiki', 'client.wiki.config.registration', 76, 'http://community.abiquo.com/display/ABI20/Configuration+view#Configurationview-ProductRegistration', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('discover UCS blades wiki', 'client.wiki.infra.discoverBlades', 77, 'http://community.abiquo.com/display/ABI20/Manage+Physical+Machines#ManagePhysicalMachines-DiscoveringBladesonManagedRacks', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default private vlan name', 'client.network.defaultName', 78, 'default_private_network', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('index of available netmask', 'client.network.defaultNetmask', 79, '2', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default private vlan address', 'client.network.defaultAddress', 80, '192.168.0.0', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default private vlan gateway', 'client.network.defaultGateway', 81, '192.168.0.1', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default primary DNS', 'client.network.defaultPrimaryDNS', 82, '', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default secondary DNS', 'client.network.defaultSecondaryDNS', 83, '', 0);

INSERT INTO `kinton`.`system_properties` (`description`, `name`, `systemPropertyId`, `value`, `version_c`) VALUES ('default sufix DNS', 'client.network.defaultSufixDNS', 84, '', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x84)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-18', '2.0.3', '3:2b477b3eddc17cd40dc60c24f7c11e64', 220);

-- Changeset kinton-2.0-ga-data.xml::1335522749701-19::destevezg (generated)::(Checksum: 3:ecb84847f2d39cdd0f7b1ae70946b2d1)
INSERT INTO `kinton`.`currency` (`digits`, `idCurrency`, `name`, `symbol`, `version_c`) VALUES (2, 1, 'Dollar - $', 'USD', 0);

INSERT INTO `kinton`.`currency` (`digits`, `idCurrency`, `name`, `symbol`, `version_c`) VALUES (2, 2, 'Euro - €', 'EUR', 0);

INSERT INTO `kinton`.`currency` (`digits`, `idCurrency`, `name`, `symbol`, `version_c`) VALUES (0, 3, 'Yen - ¥', 'JPY', 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Insert Row (x3)', 'EXECUTED', 'kinton-2.0-ga-data.xml', '1335522749701-19', '2.0.3', '3:ecb84847f2d39cdd0f7b1ae70946b2d1', 221);

