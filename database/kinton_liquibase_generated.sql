-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: kinton_master_changelog.xml
-- Ran at: 4/27/12 12:18 PM
-- Against: root@destevez.bcn.abiquo.com@jdbc:mysql://10.60.12.230:3306/kinton
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE `kinton`.`DATABASECHANGELOGLOCK` (`ID` INT NOT NULL, `LOCKED` TINYINT(1) NOT NULL, `LOCKGRANTED` DATETIME, `LOCKEDBY` VARCHAR(255), CONSTRAINT `PK_DATABASECHANGELOGLOCK` PRIMARY KEY (`ID`));

INSERT INTO `kinton`.`DATABASECHANGELOGLOCK` (`ID`, `LOCKED`) VALUES (1, 0);

-- Lock Database
-- Create Database Change Log Table
CREATE TABLE `kinton`.`DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35), `DESCRIPTION` VARCHAR(255), `COMMENTS` VARCHAR(255), `TAG` VARCHAR(255), `LIQUIBASE` VARCHAR(20), CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

-- Changeset kinton-2.0-ga.xml::1335521716699-1::destevezg (generated)::(Checksum: 3:c32bbf075db7c5933ca3cce5df660aa9)
CREATE TABLE `kinton`.`alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-1', '2.0.3', '3:c32bbf075db7c5933ca3cce5df660aa9', 1);

-- Changeset kinton-2.0-ga.xml::1335521716699-2::destevezg (generated)::(Checksum: 3:b518e45dd85a26cde440580145fcddb4)
CREATE TABLE `kinton`.`apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-2', '2.0.3', '3:b518e45dd85a26cde440580145fcddb4', 2);

-- Changeset kinton-2.0-ga.xml::1335521716699-3::destevezg (generated)::(Checksum: 3:966996751618877d8c5c9d810821a619)
CREATE TABLE `kinton`.`auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-3', '2.0.3', '3:966996751618877d8c5c9d810821a619', 3);

-- Changeset kinton-2.0-ga.xml::1335521716699-4::destevezg (generated)::(Checksum: 3:447eb654eeabbcb662cb7dad38635820)
CREATE TABLE `kinton`.`auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-4', '2.0.3', '3:447eb654eeabbcb662cb7dad38635820', 4);

-- Changeset kinton-2.0-ga.xml::1335521716699-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `kinton`.`auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 5);

-- Changeset kinton-2.0-ga.xml::1335521716699-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `kinton`.`category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 6);

-- Changeset kinton-2.0-ga.xml::1335521716699-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `kinton`.`chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 7);

-- Changeset kinton-2.0-ga.xml::1335521716699-8::destevezg (generated)::(Checksum: 3:58cbbf341c225e42abbddb19152d80cf)
CREATE TABLE `kinton`.`cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-8', '2.0.3', '3:58cbbf341c225e42abbddb19152d80cf', 8);

-- Changeset kinton-2.0-ga.xml::1335521716699-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `kinton`.`costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 9);

-- Changeset kinton-2.0-ga.xml::1335521716699-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `kinton`.`costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 10);

-- Changeset kinton-2.0-ga.xml::1335521716699-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `kinton`.`currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 11);

-- Changeset kinton-2.0-ga.xml::1335521716699-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `kinton`.`datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 12);

-- Changeset kinton-2.0-ga.xml::1335521716699-13::destevezg (generated)::(Checksum: 3:012f13e36a785d4e1cbc919807a3b446)
CREATE TABLE `kinton`.`datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-13', '2.0.3', '3:012f13e36a785d4e1cbc919807a3b446', 13);

-- Changeset kinton-2.0-ga.xml::1335521716699-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `kinton`.`datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 14);

-- Changeset kinton-2.0-ga.xml::1335521716699-15::destevezg (generated)::(Checksum: 3:5f9c424e9fbb0017629e5cbc44589037)
CREATE TABLE `kinton`.`dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-15', '2.0.3', '3:5f9c424e9fbb0017629e5cbc44589037', 15);

-- Changeset kinton-2.0-ga.xml::1335521716699-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `kinton`.`dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 16);

-- Changeset kinton-2.0-ga.xml::1335521716699-17::destevezg (generated)::(Checksum: 3:ffd62de872535e1f2da1cac582b3c9d5)
CREATE TABLE `kinton`.`disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-17', '2.0.3', '3:ffd62de872535e1f2da1cac582b3c9d5', 17);

-- Changeset kinton-2.0-ga.xml::1335521716699-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `kinton`.`diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 18);

-- Changeset kinton-2.0-ga.xml::1335521716699-19::destevezg (generated)::(Checksum: 3:21d26101befd3c8262ef45a09b22a60c)
CREATE TABLE `kinton`.`enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-19', '2.0.3', '3:21d26101befd3c8262ef45a09b22a60c', 19);

-- Changeset kinton-2.0-ga.xml::1335521716699-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 20);

-- Changeset kinton-2.0-ga.xml::1335521716699-21::destevezg (generated)::(Checksum: 3:3e94390d029bf8e6061698eb5628d573)
CREATE TABLE `kinton`.`enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-21', '2.0.3', '3:3e94390d029bf8e6061698eb5628d573', 21);

-- Changeset kinton-2.0-ga.xml::1335521716699-22::destevezg (generated)::(Checksum: 3:be4693925397c572062f1fab8c984362)
CREATE TABLE `kinton`.`enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50), `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-22', '2.0.3', '3:be4693925397c572062f1fab8c984362', 22);

-- Changeset kinton-2.0-ga.xml::1335521716699-23::destevezg (generated)::(Checksum: 3:85cbe91890b5c71b94383ffacfa6e990)
CREATE TABLE `kinton`.`enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-23', '2.0.3', '3:85cbe91890b5c71b94383ffacfa6e990', 23);

-- Changeset kinton-2.0-ga.xml::1335521716699-24::destevezg (generated)::(Checksum: 3:e8f2ebf6beb25a439ac657f77720f809)
CREATE TABLE `kinton`.`enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-24', '2.0.3', '3:e8f2ebf6beb25a439ac657f77720f809', 24);

-- Changeset kinton-2.0-ga.xml::1335521716699-25::destevezg (generated)::(Checksum: 3:f6211931acdcc03c90d5c6d208a910b9)
CREATE TABLE `kinton`.`heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-25', '2.0.3', '3:f6211931acdcc03c90d5c6d208a910b9', 25);

-- Changeset kinton-2.0-ga.xml::1335521716699-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `kinton`.`hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 26);

-- Changeset kinton-2.0-ga.xml::1335521716699-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `kinton`.`initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 27);

-- Changeset kinton-2.0-ga.xml::1335521716699-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `kinton`.`ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 28);

-- Changeset kinton-2.0-ga.xml::1335521716699-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `kinton`.`license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 29);

-- Changeset kinton-2.0-ga.xml::1335521716699-30::destevezg (generated)::(Checksum: 3:38e9d9ed33afac96b738855a00109f9c)
CREATE TABLE `kinton`.`log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-30', '2.0.3', '3:38e9d9ed33afac96b738855a00109f9c', 30);

-- Changeset kinton-2.0-ga.xml::1335521716699-31::destevezg (generated)::(Checksum: 3:5d99f5c76459a8742c2a7903abff8ab6)
CREATE TABLE `kinton`.`metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-31', '2.0.3', '3:5d99f5c76459a8742c2a7903abff8ab6', 31);

-- Changeset kinton-2.0-ga.xml::1335521716699-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `kinton`.`network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 32);

-- Changeset kinton-2.0-ga.xml::1335521716699-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `kinton`.`network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 33);

-- Changeset kinton-2.0-ga.xml::1335521716699-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `kinton`.`node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 34);

-- Changeset kinton-2.0-ga.xml::1335521716699-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `kinton`.`node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 35);

-- Changeset kinton-2.0-ga.xml::1335521716699-36::destevezg (generated)::(Checksum: 3:b6fc7632116240a776aa00853de6bcad)
CREATE TABLE `kinton`.`nodenetwork` (`idNode` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-36', '2.0.3', '3:b6fc7632116240a776aa00853de6bcad', 36);

-- Changeset kinton-2.0-ga.xml::1335521716699-37::destevezg (generated)::(Checksum: 3:6952f964ce37833b8144613d3cf11344)
CREATE TABLE `kinton`.`noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-37', '2.0.3', '3:6952f964ce37833b8144613d3cf11344', 37);

-- Changeset kinton-2.0-ga.xml::1335521716699-38::destevezg (generated)::(Checksum: 3:72bf3673a02388e2bc0da52ae70e5fce)
CREATE TABLE `kinton`.`nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-38', '2.0.3', '3:72bf3673a02388e2bc0da52ae70e5fce', 38);

-- Changeset kinton-2.0-ga.xml::1335521716699-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `kinton`.`nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 39);

-- Changeset kinton-2.0-ga.xml::1335521716699-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 40);

-- Changeset kinton-2.0-ga.xml::1335521716699-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `kinton`.`ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 41);

-- Changeset kinton-2.0-ga.xml::1335521716699-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `kinton`.`ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 42);

-- Changeset kinton-2.0-ga.xml::1335521716699-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `kinton`.`ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 43);

-- Changeset kinton-2.0-ga.xml::1335521716699-44::destevezg (generated)::(Checksum: 3:1c7f0f2d49a40fc8acf31b2623be1e19)
CREATE TABLE `kinton`.`physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-44', '2.0.3', '3:1c7f0f2d49a40fc8acf31b2623be1e19', 44);

-- Changeset kinton-2.0-ga.xml::1335521716699-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `kinton`.`pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 45);

-- Changeset kinton-2.0-ga.xml::1335521716699-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `kinton`.`pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 46);

-- Changeset kinton-2.0-ga.xml::1335521716699-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `kinton`.`pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 47);

-- Changeset kinton-2.0-ga.xml::1335521716699-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `kinton`.`privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 48);

-- Changeset kinton-2.0-ga.xml::1335521716699-49::destevezg (generated)::(Checksum: 3:f9e93f3ea715dc50d0c7b98249ff885d)
CREATE TABLE `kinton`.`rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-49', '2.0.3', '3:f9e93f3ea715dc50d0c7b98249ff885d', 49);

-- Changeset kinton-2.0-ga.xml::1335521716699-50::destevezg (generated)::(Checksum: 3:6ad3c61b145a97899b2b3470720682ac)
CREATE TABLE `kinton`.`rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-50', '2.0.3', '3:6ad3c61b145a97899b2b3470720682ac', 50);

-- Changeset kinton-2.0-ga.xml::1335521716699-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `kinton`.`rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 51);

-- Changeset kinton-2.0-ga.xml::1335521716699-52::destevezg (generated)::(Checksum: 3:ed4ae73f975deb795a4e2fe4980ada26)
CREATE TABLE `kinton`.`register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-52', '2.0.3', '3:ed4ae73f975deb795a4e2fe4980ada26', 52);

-- Changeset kinton-2.0-ga.xml::1335521716699-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `kinton`.`remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 53);

-- Changeset kinton-2.0-ga.xml::1335521716699-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `kinton`.`repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 54);

-- Changeset kinton-2.0-ga.xml::1335521716699-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `kinton`.`role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 55);

-- Changeset kinton-2.0-ga.xml::1335521716699-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `kinton`.`role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 56);

-- Changeset kinton-2.0-ga.xml::1335521716699-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `kinton`.`roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 57);

-- Changeset kinton-2.0-ga.xml::1335521716699-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `kinton`.`session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 58);

-- Changeset kinton-2.0-ga.xml::1335521716699-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `kinton`.`storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 59);

-- Changeset kinton-2.0-ga.xml::1335521716699-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `kinton`.`storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 60);

-- Changeset kinton-2.0-ga.xml::1335521716699-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `kinton`.`system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 61);

-- Changeset kinton-2.0-ga.xml::1335521716699-62::destevezg (generated)::(Checksum: 3:31486daf8f610a7250344cb981627a60)
CREATE TABLE `kinton`.`tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-62', '2.0.3', '3:31486daf8f610a7250344cb981627a60', 62);

-- Changeset kinton-2.0-ga.xml::1335521716699-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `kinton`.`tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 63);

-- Changeset kinton-2.0-ga.xml::1335521716699-64::destevezg (generated)::(Checksum: 3:e5d525478dfcdecb18cc7cad873150c3)
CREATE TABLE `kinton`.`ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-64', '2.0.3', '3:e5d525478dfcdecb18cc7cad873150c3', 64);

-- Changeset kinton-2.0-ga.xml::1335521716699-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `kinton`.`user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 65);

-- Changeset kinton-2.0-ga.xml::1335521716699-66::destevezg (generated)::(Checksum: 3:488437b5ee6644bec1be95122ea619bd)
CREATE TABLE `kinton`.`vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-66', '2.0.3', '3:488437b5ee6644bec1be95122ea619bd', 66);

-- Changeset kinton-2.0-ga.xml::1335521716699-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `kinton`.`vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 67);

-- Changeset kinton-2.0-ga.xml::1335521716699-68::destevezg (generated)::(Checksum: 3:3d0b7f09c47388eefd55f27095ae19fa)
CREATE TABLE `kinton`.`vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VDC_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-68', '2.0.3', '3:3d0b7f09c47388eefd55f27095ae19fa', 68);

-- Changeset kinton-2.0-ga.xml::1335521716699-69::destevezg (generated)::(Checksum: 3:bc9ba0c28876d849c819915c84e9cd70)
CREATE TABLE `kinton`.`virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-69', '2.0.3', '3:bc9ba0c28876d849c819915c84e9cd70', 69);

-- Changeset kinton-2.0-ga.xml::1335521716699-70::destevezg (generated)::(Checksum: 3:a986c6a58a6a04c09cec7bab3476d00f)
CREATE TABLE `kinton`.`virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-70', '2.0.3', '3:a986c6a58a6a04c09cec7bab3476d00f', 70);

-- Changeset kinton-2.0-ga.xml::1335521716699-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `kinton`.`virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 71);

-- Changeset kinton-2.0-ga.xml::1335521716699-72::destevezg (generated)::(Checksum: 3:0be3e819a67bb0b75b9b764750439bc5)
CREATE TABLE `kinton`.`virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-72', '2.0.3', '3:0be3e819a67bb0b75b9b764750439bc5', 72);

-- Changeset kinton-2.0-ga.xml::1335521716699-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `kinton`.`virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 73);

-- Changeset kinton-2.0-ga.xml::1335521716699-74::destevezg (generated)::(Checksum: 3:3adc3c4bbc9e83860f010937c576ae62)
CREATE TABLE `kinton`.`virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-74', '2.0.3', '3:3adc3c4bbc9e83860f010937c576ae62', 74);

-- Changeset kinton-2.0-ga.xml::1335521716699-75::destevezg (generated)::(Checksum: 3:62ecd79335be6ba7c6365fb60199052d)
CREATE TABLE `kinton`.`virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-75', '2.0.3', '3:62ecd79335be6ba7c6365fb60199052d', 75);

-- Changeset kinton-2.0-ga.xml::1335521716699-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `kinton`.`vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 76);

-- Changeset kinton-2.0-ga.xml::1335521716699-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `kinton`.`vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 77);

-- Changeset kinton-2.0-ga.xml::1335521716699-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `kinton`.`vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 78);

-- Changeset kinton-2.0-ga.xml::1335521716699-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `kinton`.`volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 79);

-- Changeset kinton-2.0-ga.xml::1335521716699-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `kinton`.`workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 80);

-- Changeset kinton-2.0-ga.xml::1335521716699-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `kinton`.`workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 81);

-- Changeset kinton-2.0-ga.xml::1335521716699-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `kinton`.`workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 82);

-- Changeset kinton-2.0-ga.xml::1335521716699-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `kinton`.`datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 83);

-- Changeset kinton-2.0-ga.xml::1335521716699-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 84);

-- Changeset kinton-2.0-ga.xml::1335521716699-85::destevezg (generated)::(Checksum: 3:30c70dfc222810e526301420db90cd33)
CREATE INDEX `fk_idEnterpriseApps` ON `kinton`.`apps_library`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-85', '2.0.3', '3:30c70dfc222810e526301420db90cd33', 85);

-- Changeset kinton-2.0-ga.xml::1335521716699-86::destevezg (generated)::(Checksum: 3:7a90efd5a16a24fb9333931757ae8a73)
CREATE INDEX `auth_serverresourceFK1` ON `kinton`.`auth_serverresource`(`idGroup`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-86', '2.0.3', '3:7a90efd5a16a24fb9333931757ae8a73', 86);

-- Changeset kinton-2.0-ga.xml::1335521716699-87::destevezg (generated)::(Checksum: 3:f3eee0e863774fa09e64bccfec41349a)
CREATE INDEX `auth_serverresourceFK2` ON `kinton`.`auth_serverresource`(`idRole`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-87', '2.0.3', '3:f3eee0e863774fa09e64bccfec41349a', 87);

-- Changeset kinton-2.0-ga.xml::1335521716699-88::destevezg (generated)::(Checksum: 3:e3577da38d7414cb4c6fc320a22dace2)
CREATE INDEX `auth_serverresource_exceptionFK1` ON `kinton`.`auth_serverresource_exception`(`idResource`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-88', '2.0.3', '3:e3577da38d7414cb4c6fc320a22dace2', 88);

-- Changeset kinton-2.0-ga.xml::1335521716699-89::destevezg (generated)::(Checksum: 3:cfa86aba3e137a9971d874ac9d2ee47d)
CREATE INDEX `auth_serverresource_exceptionFK2` ON `kinton`.`auth_serverresource_exception`(`idUser`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-89', '2.0.3', '3:cfa86aba3e137a9971d874ac9d2ee47d', 89);

-- Changeset kinton-2.0-ga.xml::1335521716699-90::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `kinton`.`category`(`name`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-90', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 90);

-- Changeset kinton-2.0-ga.xml::1335521716699-91::destevezg (generated)::(Checksum: 3:c68116819dcca698178ca973a709573c)
CREATE INDEX `chef_runlist_FK1` ON `kinton`.`chef_runlist`(`idVM`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-91', '2.0.3', '3:c68116819dcca698178ca973a709573c', 91);

-- Changeset kinton-2.0-ga.xml::1335521716699-92::destevezg (generated)::(Checksum: 3:1ed15b1b9341397be9408ac7bd6eb550)
CREATE INDEX `datacenternetwork_FK1` ON `kinton`.`datacenter`(`network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-92', '2.0.3', '3:1ed15b1b9341397be9408ac7bd6eb550', 92);

-- Changeset kinton-2.0-ga.xml::1335521716699-93::destevezg (generated)::(Checksum: 3:85658e02a3a965082a6361833ffb1b45)
CREATE INDEX `disk_datastore_FK` ON `kinton`.`disk_management`(`idDatastore`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-93', '2.0.3', '3:85658e02a3a965082a6361833ffb1b45', 93);

-- Changeset kinton-2.0-ga.xml::1335521716699-94::destevezg (generated)::(Checksum: 3:ff2518714cdcd882909be832ee0e718e)
CREATE INDEX `disk_idManagement_FK` ON `kinton`.`disk_management`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-94', '2.0.3', '3:ff2518714cdcd882909be832ee0e718e', 94);

-- Changeset kinton-2.0-ga.xml::1335521716699-95::destevezg (generated)::(Checksum: 3:1acddc4f15dfa434779ad8ca2f194235)
CREATE INDEX `idManagement_FK2` ON `kinton`.`diskstateful_conversions`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-95', '2.0.3', '3:1acddc4f15dfa434779ad8ca2f194235', 95);

-- Changeset kinton-2.0-ga.xml::1335521716699-96::destevezg (generated)::(Checksum: 3:45086fe079a6d7f02eee026abe90530a)
CREATE INDEX `enterprise_pricing_FK` ON `kinton`.`enterprise`(`idPricingTemplate`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-96', '2.0.3', '3:45086fe079a6d7f02eee026abe90530a', 96);

-- Changeset kinton-2.0-ga.xml::1335521716699-97::destevezg (generated)::(Checksum: 3:85d784013f3bfef97a1e94c94a058be1)
CREATE INDEX `enterprise_FK7` ON `kinton`.`enterprise_limits_by_datacenter`(`default_vlan_network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-97', '2.0.3', '3:85d784013f3bfef97a1e94c94a058be1', 97);

-- Changeset kinton-2.0-ga.xml::1335521716699-98::destevezg (generated)::(Checksum: 3:24e87cf15bc6a0d5d0d250fcdb5fb146)
CREATE INDEX `FK_enterprise` ON `kinton`.`enterprise_properties`(`enterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-98', '2.0.3', '3:24e87cf15bc6a0d5d0d250fcdb5fb146', 98);

-- Changeset kinton-2.0-ga.xml::1335521716699-99::destevezg (generated)::(Checksum: 3:e003c73fb152bee6ddc52f03f73ed041)
CREATE INDEX `FK2_enterprise_properties` ON `kinton`.`enterprise_properties_map`(`enterprise_properties`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-99', '2.0.3', '3:e003c73fb152bee6ddc52f03f73ed041', 99);

-- Changeset kinton-2.0-ga.xml::1335521716699-100::destevezg (generated)::(Checksum: 3:d77dd6dc395cb73fb357c3bfff04b70c)
CREATE INDEX `Hypervisor_FK1` ON `kinton`.`hypervisor`(`idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-100', '2.0.3', '3:d77dd6dc395cb73fb357c3bfff04b70c', 100);

-- Changeset kinton-2.0-ga.xml::1335521716699-101::destevezg (generated)::(Checksum: 3:2dca3406de9c18666b5e25b374441e8f)
CREATE INDEX `volume_managementFK_1` ON `kinton`.`initiator_mapping`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-101', '2.0.3', '3:2dca3406de9c18666b5e25b374441e8f', 101);

-- Changeset kinton-2.0-ga.xml::1335521716699-102::destevezg (generated)::(Checksum: 3:f6fa0350a694985d7642ce97c62d94e2)
CREATE INDEX `id_management_FK` ON `kinton`.`ip_pool_management`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-102', '2.0.3', '3:f6fa0350a694985d7642ce97c62d94e2', 102);

-- Changeset kinton-2.0-ga.xml::1335521716699-103::destevezg (generated)::(Checksum: 3:1fdecbf82fbf48cd3b6d7fd2d5fd25e6)
CREATE INDEX `ippool_vlan_network_FK` ON `kinton`.`ip_pool_management`(`vlan_network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-103', '2.0.3', '3:1fdecbf82fbf48cd3b6d7fd2d5fd25e6', 103);

-- Changeset kinton-2.0-ga.xml::1335521716699-104::destevezg (generated)::(Checksum: 3:25445a966da6ae01a7278ce8118d3a91)
CREATE INDEX `log_FK1` ON `kinton`.`log`(`idVirtualApp`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-104', '2.0.3', '3:25445a966da6ae01a7278ce8118d3a91', 104);

-- Changeset kinton-2.0-ga.xml::1335521716699-105::destevezg (generated)::(Checksum: 3:3335e9ea364499c4cc1c4b36bf14febd)
CREATE INDEX `node_FK2` ON `kinton`.`node`(`idVirtualApp`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-105', '2.0.3', '3:3335e9ea364499c4cc1c4b36bf14febd', 105);

-- Changeset kinton-2.0-ga.xml::1335521716699-106::destevezg (generated)::(Checksum: 3:0934a68a139026df1e40df89958e6055)
CREATE INDEX `idDiskStatefulConversion_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idDiskStatefulConversion`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-106', '2.0.3', '3:0934a68a139026df1e40df89958e6055', 106);

-- Changeset kinton-2.0-ga.xml::1335521716699-107::destevezg (generated)::(Checksum: 3:b9acb404463ab08d98e7d3329ab04042)
CREATE INDEX `idManagement_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-107', '2.0.3', '3:b9acb404463ab08d98e7d3329ab04042', 107);

-- Changeset kinton-2.0-ga.xml::1335521716699-108::destevezg (generated)::(Checksum: 3:e453c5cd9e5745be7e494edc27e53a1b)
CREATE INDEX `idNodeVirtualImage_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idNodeVirtualImage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-108', '2.0.3', '3:e453c5cd9e5745be7e494edc27e53a1b', 108);

-- Changeset kinton-2.0-ga.xml::1335521716699-109::destevezg (generated)::(Checksum: 3:2b71c0ed455411c9625b4d2d265ff610)
CREATE INDEX `idTier_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idTier`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-109', '2.0.3', '3:2b71c0ed455411c9625b4d2d265ff610', 109);

-- Changeset kinton-2.0-ga.xml::1335521716699-110::destevezg (generated)::(Checksum: 3:addedff8814e720852eb641443f7239b)
CREATE INDEX `idVirtualApplianceStatefulConversion_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idVirtualApplianceStatefulConversion`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-110', '2.0.3', '3:addedff8814e720852eb641443f7239b', 110);

-- Changeset kinton-2.0-ga.xml::1335521716699-111::destevezg (generated)::(Checksum: 3:9ebeabf9dc4f8199539a7cec543f5c50)
CREATE INDEX `idVirtualImageConversion_FK4` ON `kinton`.`node_virtual_image_stateful_conversions`(`idVirtualImageConversion`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-111', '2.0.3', '3:9ebeabf9dc4f8199539a7cec543f5c50', 111);

-- Changeset kinton-2.0-ga.xml::1335521716699-112::destevezg (generated)::(Checksum: 3:7ac5b43c4a77e4bb96d46eec9371b4c8)
CREATE INDEX `nodevirtualImage_FK1` ON `kinton`.`nodevirtualimage`(`idImage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-112', '2.0.3', '3:7ac5b43c4a77e4bb96d46eec9371b4c8', 112);

-- Changeset kinton-2.0-ga.xml::1335521716699-113::destevezg (generated)::(Checksum: 3:a7f57247f4020d1c12366b804e3f3b2e)
CREATE INDEX `nodevirtualImage_FK2` ON `kinton`.`nodevirtualimage`(`idVM`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-113', '2.0.3', '3:a7f57247f4020d1c12366b804e3f3b2e', 113);

-- Changeset kinton-2.0-ga.xml::1335521716699-114::destevezg (generated)::(Checksum: 3:f17ada6b630fa3f2229572368fac6821)
CREATE INDEX `nodevirtualimage_FK3` ON `kinton`.`nodevirtualimage`(`idNode`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-114', '2.0.3', '3:f17ada6b630fa3f2229572368fac6821', 114);

-- Changeset kinton-2.0-ga.xml::1335521716699-115::destevezg (generated)::(Checksum: 3:7d9798eb8f86bebbb8927d5ecfa26abc)
CREATE INDEX `fk_ovf_package_category` ON `kinton`.`ovf_package`(`idCategory`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-115', '2.0.3', '3:7d9798eb8f86bebbb8927d5ecfa26abc', 115);

-- Changeset kinton-2.0-ga.xml::1335521716699-116::destevezg (generated)::(Checksum: 3:a4873c3f8b2683371ec5ab41040925be)
CREATE INDEX `fk_ovf_package_repository` ON `kinton`.`ovf_package`(`id_apps_library`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-116', '2.0.3', '3:a4873c3f8b2683371ec5ab41040925be', 116);

-- Changeset kinton-2.0-ga.xml::1335521716699-117::destevezg (generated)::(Checksum: 3:f5b7aea6c52530f6863f2f2a6a8b01fd)
CREATE INDEX `fk_ovf_package_list_repository` ON `kinton`.`ovf_package_list`(`id_apps_library`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-117', '2.0.3', '3:f5b7aea6c52530f6863f2f2a6a8b01fd', 117);

-- Changeset kinton-2.0-ga.xml::1335521716699-118::destevezg (generated)::(Checksum: 3:5830c9d996373b44386c802ba5ce8bbe)
CREATE INDEX `fk_ovf_package_list_has_ovf_package_ovf_package1` ON `kinton`.`ovf_package_list_has_ovf_package`(`id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-118', '2.0.3', '3:5830c9d996373b44386c802ba5ce8bbe', 118);

-- Changeset kinton-2.0-ga.xml::1335521716699-119::destevezg (generated)::(Checksum: 3:95d82f341711b1cb7cbb7ddd55f05213)
CREATE INDEX `PhysicalMachine_FK1` ON `kinton`.`physicalmachine`(`idRack`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-119', '2.0.3', '3:95d82f341711b1cb7cbb7ddd55f05213', 119);

-- Changeset kinton-2.0-ga.xml::1335521716699-120::destevezg (generated)::(Checksum: 3:60ff4e872aed3e8961ea2a7bdd06c632)
CREATE INDEX `PhysicalMachine_FK5` ON `kinton`.`physicalmachine`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-120', '2.0.3', '3:60ff4e872aed3e8961ea2a7bdd06c632', 120);

-- Changeset kinton-2.0-ga.xml::1335521716699-121::destevezg (generated)::(Checksum: 3:086ce9e533a0c04e058486cbde41107a)
CREATE INDEX `PhysicalMachine_FK6` ON `kinton`.`physicalmachine`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-121', '2.0.3', '3:086ce9e533a0c04e058486cbde41107a', 121);

-- Changeset kinton-2.0-ga.xml::1335521716699-122::destevezg (generated)::(Checksum: 3:1ebd68d6f3d610a25719056f59156369)
CREATE INDEX `Pricing_FK2_Currency` ON `kinton`.`pricingTemplate`(`idCurrency`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-122', '2.0.3', '3:1ebd68d6f3d610a25719056f59156369', 122);

-- Changeset kinton-2.0-ga.xml::1335521716699-123::destevezg (generated)::(Checksum: 3:c4cb6bc30c794d31d1a3b56edbf4438d)
CREATE INDEX `Rack_FK1` ON `kinton`.`rack`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-123', '2.0.3', '3:c4cb6bc30c794d31d1a3b56edbf4438d', 123);

-- Changeset kinton-2.0-ga.xml::1335521716699-124::destevezg (generated)::(Checksum: 3:71b388a7886eeed3441ba35b63de890c)
CREATE INDEX `idResource_FK` ON `kinton`.`rasd_management`(`idResource`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-124', '2.0.3', '3:71b388a7886eeed3441ba35b63de890c', 124);

-- Changeset kinton-2.0-ga.xml::1335521716699-125::destevezg (generated)::(Checksum: 3:aee22e5b1288c67eebd1a66e01e56070)
CREATE INDEX `idVM_FK` ON `kinton`.`rasd_management`(`idVM`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-125', '2.0.3', '3:aee22e5b1288c67eebd1a66e01e56070', 125);

-- Changeset kinton-2.0-ga.xml::1335521716699-126::destevezg (generated)::(Checksum: 3:cc9fabf559fcfd16b4010dd511e741af)
CREATE INDEX `idVirtualApp_FK` ON `kinton`.`rasd_management`(`idVirtualApp`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-126', '2.0.3', '3:cc9fabf559fcfd16b4010dd511e741af', 126);

-- Changeset kinton-2.0-ga.xml::1335521716699-127::destevezg (generated)::(Checksum: 3:0c609e50fc27710b76b01fe8294de8ce)
CREATE INDEX `idVirtualDataCenter_FK` ON `kinton`.`rasd_management`(`idVirtualDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-127', '2.0.3', '3:0c609e50fc27710b76b01fe8294de8ce', 127);

-- Changeset kinton-2.0-ga.xml::1335521716699-128::destevezg (generated)::(Checksum: 3:ec1c403dc5eed91df658a5f84d04836c)
CREATE INDEX `idDatecenter_FK` ON `kinton`.`remote_service`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-128', '2.0.3', '3:ec1c403dc5eed91df658a5f84d04836c', 128);

-- Changeset kinton-2.0-ga.xml::1335521716699-129::destevezg (generated)::(Checksum: 3:52a515c750a74f0992f89931c1164a68)
CREATE INDEX `fk_idDataCenter` ON `kinton`.`repository`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-129', '2.0.3', '3:52a515c750a74f0992f89931c1164a68', 129);

-- Changeset kinton-2.0-ga.xml::1335521716699-130::destevezg (generated)::(Checksum: 3:440ea1a6aae6b729225c8291333fec8b)
CREATE INDEX `fk_role_1` ON `kinton`.`role`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-130', '2.0.3', '3:440ea1a6aae6b729225c8291333fec8b', 130);

-- Changeset kinton-2.0-ga.xml::1335521716699-131::destevezg (generated)::(Checksum: 3:5c2739e2a924c658af9662a99f211d2a)
CREATE INDEX `fk_role_ldap_role` ON `kinton`.`role_ldap`(`idRole`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-131', '2.0.3', '3:5c2739e2a924c658af9662a99f211d2a', 131);

-- Changeset kinton-2.0-ga.xml::1335521716699-132::destevezg (generated)::(Checksum: 3:0b336891d54a7b7b638cbe7db604b913)
CREATE INDEX `fk_roles_privileges_privileges` ON `kinton`.`roles_privileges`(`idPrivilege`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-132', '2.0.3', '3:0b336891d54a7b7b638cbe7db604b913', 132);

-- Changeset kinton-2.0-ga.xml::1335521716699-133::destevezg (generated)::(Checksum: 3:a0886e0bc4b76c0eeafb600374da088b)
CREATE INDEX `fk_roles_privileges_role` ON `kinton`.`roles_privileges`(`idRole`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-133', '2.0.3', '3:a0886e0bc4b76c0eeafb600374da088b', 133);

-- Changeset kinton-2.0-ga.xml::1335521716699-134::destevezg (generated)::(Checksum: 3:7b8a5469eede3054add03fac4c2ca817)
CREATE INDEX `fk_session_user` ON `kinton`.`session`(`idUser`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-134', '2.0.3', '3:7b8a5469eede3054add03fac4c2ca817', 134);

-- Changeset kinton-2.0-ga.xml::1335521716699-135::destevezg (generated)::(Checksum: 3:38899f11ceca0b52334e3da53b3eda64)
CREATE INDEX `storage_device_FK_1` ON `kinton`.`storage_device`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-135', '2.0.3', '3:38899f11ceca0b52334e3da53b3eda64', 135);

-- Changeset kinton-2.0-ga.xml::1335521716699-136::destevezg (generated)::(Checksum: 3:e4ecfd61713adefd33141c8f39cdf455)
CREATE INDEX `storage_pool_FK1` ON `kinton`.`storage_pool`(`idStorageDevice`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-136', '2.0.3', '3:e4ecfd61713adefd33141c8f39cdf455', 136);

-- Changeset kinton-2.0-ga.xml::1335521716699-137::destevezg (generated)::(Checksum: 3:0602c34efbe266893b9504091db3d68f)
CREATE INDEX `storage_pool_FK2` ON `kinton`.`storage_pool`(`idTier`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-137', '2.0.3', '3:0602c34efbe266893b9504091db3d68f', 137);

-- Changeset kinton-2.0-ga.xml::1335521716699-138::destevezg (generated)::(Checksum: 3:cf84b078db70a4eba3134a2bb861849b)
CREATE INDEX `tier_FK_1` ON `kinton`.`tier`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-138', '2.0.3', '3:cf84b078db70a4eba3134a2bb861849b', 138);

-- Changeset kinton-2.0-ga.xml::1335521716699-139::destevezg (generated)::(Checksum: 3:0e59fd00587b808b395bf3284e19c3aa)
CREATE INDEX `id_rack_FK` ON `kinton`.`ucs_rack`(`idRack`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-139', '2.0.3', '3:0e59fd00587b808b395bf3284e19c3aa', 139);

-- Changeset kinton-2.0-ga.xml::1335521716699-140::destevezg (generated)::(Checksum: 3:1b2ca05cc4d58cd6d3cc4740d2371708)
CREATE INDEX `FK1_user` ON `kinton`.`user`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-140', '2.0.3', '3:1b2ca05cc4d58cd6d3cc4740d2371708', 140);

-- Changeset kinton-2.0-ga.xml::1335521716699-141::destevezg (generated)::(Checksum: 3:82857174b11df5083cd55a51e15bd438)
CREATE INDEX `User_FK1` ON `kinton`.`user`(`idRole`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-141', '2.0.3', '3:82857174b11df5083cd55a51e15bd438', 141);

-- Changeset kinton-2.0-ga.xml::1335521716699-142::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `kinton`.`user`(`user`, `authType`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-142', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 142);

-- Changeset kinton-2.0-ga.xml::1335521716699-143::destevezg (generated)::(Checksum: 3:465746ce2e0ff97c2070278a68cb01e7)
CREATE INDEX `idUser_FK3` ON `kinton`.`vappstateful_conversions`(`idUser`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-143', '2.0.3', '3:465746ce2e0ff97c2070278a68cb01e7', 143);

-- Changeset kinton-2.0-ga.xml::1335521716699-144::destevezg (generated)::(Checksum: 3:9d1e3a8a82d6c0f5adb3a38ad9f7d591)
CREATE INDEX `idVirtualApp_FK3` ON `kinton`.`vappstateful_conversions`(`idVirtualApp`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-144', '2.0.3', '3:9d1e3a8a82d6c0f5adb3a38ad9f7d591', 144);

-- Changeset kinton-2.0-ga.xml::1335521716699-145::destevezg (generated)::(Checksum: 3:e3863123a176a952c835535dd92e08a8)
CREATE INDEX `user_FK` ON `kinton`.`virtual_appliance_conversions`(`idUser`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-145', '2.0.3', '3:e3863123a176a952c835535dd92e08a8', 145);

-- Changeset kinton-2.0-ga.xml::1335521716699-146::destevezg (generated)::(Checksum: 3:1d2cd575b9011b2537902776e9246da1)
CREATE INDEX `virtual_appliance_conversions_node_FK` ON `kinton`.`virtual_appliance_conversions`(`idNode`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-146', '2.0.3', '3:1d2cd575b9011b2537902776e9246da1', 146);

-- Changeset kinton-2.0-ga.xml::1335521716699-147::destevezg (generated)::(Checksum: 3:503f1aea0a0d7ec98d54aed6cec468ce)
CREATE INDEX `virtualapp_FK` ON `kinton`.`virtual_appliance_conversions`(`idVirtualAppliance`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-147', '2.0.3', '3:503f1aea0a0d7ec98d54aed6cec468ce', 147);

-- Changeset kinton-2.0-ga.xml::1335521716699-148::destevezg (generated)::(Checksum: 3:e969e5e720ee691fb5ff34980f98193e)
CREATE INDEX `virtualimage_conversions_FK` ON `kinton`.`virtual_appliance_conversions`(`idConversion`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-148', '2.0.3', '3:e969e5e720ee691fb5ff34980f98193e', 148);

-- Changeset kinton-2.0-ga.xml::1335521716699-149::destevezg (generated)::(Checksum: 3:76aa8b764458447ecd42758fed64206e)
CREATE INDEX `VirtualApp_FK4` ON `kinton`.`virtualapp`(`idVirtualDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-149', '2.0.3', '3:76aa8b764458447ecd42758fed64206e', 149);

-- Changeset kinton-2.0-ga.xml::1335521716699-150::destevezg (generated)::(Checksum: 3:1ad815b29e4db2211001f2c05611e74c)
CREATE INDEX `VirtualApp_FK5` ON `kinton`.`virtualapp`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-150', '2.0.3', '3:1ad815b29e4db2211001f2c05611e74c', 150);

-- Changeset kinton-2.0-ga.xml::1335521716699-151::destevezg (generated)::(Checksum: 3:e7b3b94b57d5d201b25eee4cb825bf94)
CREATE INDEX `virtualDataCenter_FK1` ON `kinton`.`virtualdatacenter`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-151', '2.0.3', '3:e7b3b94b57d5d201b25eee4cb825bf94', 151);

-- Changeset kinton-2.0-ga.xml::1335521716699-152::destevezg (generated)::(Checksum: 3:24c53d1c7c19cf7ccc599167b1bdb60e)
CREATE INDEX `virtualDataCenter_FK4` ON `kinton`.`virtualdatacenter`(`networktypeID`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-152', '2.0.3', '3:24c53d1c7c19cf7ccc599167b1bdb60e', 152);

-- Changeset kinton-2.0-ga.xml::1335521716699-153::destevezg (generated)::(Checksum: 3:d34c6bd3794befcce41a9b6a504058b7)
CREATE INDEX `virtualDataCenter_FK6` ON `kinton`.`virtualdatacenter`(`idDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-153', '2.0.3', '3:d34c6bd3794befcce41a9b6a504058b7', 153);

-- Changeset kinton-2.0-ga.xml::1335521716699-154::destevezg (generated)::(Checksum: 3:9464a82614e3c23778a4e63d481f892e)
CREATE INDEX `virtualDataCenter_FK7` ON `kinton`.`virtualdatacenter`(`default_vlan_network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-154', '2.0.3', '3:9464a82614e3c23778a4e63d481f892e', 154);

-- Changeset kinton-2.0-ga.xml::1335521716699-155::destevezg (generated)::(Checksum: 3:efeddfe2ad3252753cd3f45d06a3af61)
CREATE INDEX `fk_virtualimage_category` ON `kinton`.`virtualimage`(`idCategory`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-155', '2.0.3', '3:efeddfe2ad3252753cd3f45d06a3af61', 155);

-- Changeset kinton-2.0-ga.xml::1335521716699-156::destevezg (generated)::(Checksum: 3:03e3fe5a344196d09f873c1f7b66611c)
CREATE INDEX `virtualImage_FK3` ON `kinton`.`virtualimage`(`idRepository`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-156', '2.0.3', '3:03e3fe5a344196d09f873c1f7b66611c', 156);

-- Changeset kinton-2.0-ga.xml::1335521716699-157::destevezg (generated)::(Checksum: 3:d8543342f599e0cc8b961164fddad63a)
CREATE INDEX `virtualImage_FK8` ON `kinton`.`virtualimage`(`idMaster`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-157', '2.0.3', '3:d8543342f599e0cc8b961164fddad63a', 157);

-- Changeset kinton-2.0-ga.xml::1335521716699-158::destevezg (generated)::(Checksum: 3:6e4c35bc8ba88dab91e6020bc6b1013b)
CREATE INDEX `virtualImage_FK9` ON `kinton`.`virtualimage`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-158', '2.0.3', '3:6e4c35bc8ba88dab91e6020bc6b1013b', 158);

-- Changeset kinton-2.0-ga.xml::1335521716699-159::destevezg (generated)::(Checksum: 3:6fe9ff507d0117b19348d3ab7192d5fd)
CREATE INDEX `idImage_FK` ON `kinton`.`virtualimage_conversions`(`idImage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-159', '2.0.3', '3:6fe9ff507d0117b19348d3ab7192d5fd', 159);

-- Changeset kinton-2.0-ga.xml::1335521716699-160::destevezg (generated)::(Checksum: 3:11e32d08be6ea89b32dca4de80adc3f2)
CREATE INDEX `virtualMachine_FK1` ON `kinton`.`virtualmachine`(`idHypervisor`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-160', '2.0.3', '3:11e32d08be6ea89b32dca4de80adc3f2', 160);

-- Changeset kinton-2.0-ga.xml::1335521716699-161::destevezg (generated)::(Checksum: 3:17b61219656c7af357c88336abd86476)
CREATE INDEX `virtualMachine_FK3` ON `kinton`.`virtualmachine`(`idImage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-161', '2.0.3', '3:17b61219656c7af357c88336abd86476', 161);

-- Changeset kinton-2.0-ga.xml::1335521716699-162::destevezg (generated)::(Checksum: 3:85bf7405d3303d0e7bf9a062d311b138)
CREATE INDEX `virtualMachine_FK4` ON `kinton`.`virtualmachine`(`idUser`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-162', '2.0.3', '3:85bf7405d3303d0e7bf9a062d311b138', 162);

-- Changeset kinton-2.0-ga.xml::1335521716699-163::destevezg (generated)::(Checksum: 3:0d884a67c00d36a5bdfeeb1565136882)
CREATE INDEX `virtualMachine_FK5` ON `kinton`.`virtualmachine`(`idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-163', '2.0.3', '3:0d884a67c00d36a5bdfeeb1565136882', 163);

-- Changeset kinton-2.0-ga.xml::1335521716699-164::destevezg (generated)::(Checksum: 3:a2114b2a9131cb00cd05140e61d2c7f8)
CREATE INDEX `virtualMachine_FK6` ON `kinton`.`virtualmachine`(`network_configuration_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-164', '2.0.3', '3:a2114b2a9131cb00cd05140e61d2c7f8', 164);

-- Changeset kinton-2.0-ga.xml::1335521716699-165::destevezg (generated)::(Checksum: 3:713a2f42351415a29c0f8c0db0777550)
CREATE INDEX `virtualMachine_datastore_FK` ON `kinton`.`virtualmachine`(`idDatastore`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-165', '2.0.3', '3:713a2f42351415a29c0f8c0db0777550', 165);

-- Changeset kinton-2.0-ga.xml::1335521716699-166::destevezg (generated)::(Checksum: 3:c4468c470dba9e50ed0a8747c4fa0bcb)
CREATE INDEX `virtualmachine_conversion_FK` ON `kinton`.`virtualmachine`(`idConversion`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-166', '2.0.3', '3:c4468c470dba9e50ed0a8747c4fa0bcb', 166);

-- Changeset kinton-2.0-ga.xml::1335521716699-167::destevezg (generated)::(Checksum: 3:31f3812a67c39459a01d696889e63bd0)
CREATE INDEX `vlannetwork_configuration_FK` ON `kinton`.`vlan_network`(`network_configuration_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-167', '2.0.3', '3:31f3812a67c39459a01d696889e63bd0', 167);

-- Changeset kinton-2.0-ga.xml::1335521716699-168::destevezg (generated)::(Checksum: 3:a45a1a998214b3284f085499bb95328d)
CREATE INDEX `vlannetwork_enterprise_FK` ON `kinton`.`vlan_network`(`enterprise_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-168', '2.0.3', '3:a45a1a998214b3284f085499bb95328d', 168);

-- Changeset kinton-2.0-ga.xml::1335521716699-169::destevezg (generated)::(Checksum: 3:3c72f3c890d8bd8502e67df51bd65592)
CREATE INDEX `vlannetwork_network_FK` ON `kinton`.`vlan_network`(`network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-169', '2.0.3', '3:3c72f3c890d8bd8502e67df51bd65592', 169);

-- Changeset kinton-2.0-ga.xml::1335521716699-170::destevezg (generated)::(Checksum: 3:549338c3584a2f3d141b4d02093efc17)
CREATE INDEX `vlan_network_assignment_idRack_FK` ON `kinton`.`vlan_network_assignment`(`idRack`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-170', '2.0.3', '3:549338c3584a2f3d141b4d02093efc17', 170);

-- Changeset kinton-2.0-ga.xml::1335521716699-171::destevezg (generated)::(Checksum: 3:aa1dda0294bb173385aabb63952f4053)
CREATE INDEX `vlan_network_assignment_idVirtualDataCenter_FK` ON `kinton`.`vlan_network_assignment`(`idVirtualDataCenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-171', '2.0.3', '3:aa1dda0294bb173385aabb63952f4053', 171);

-- Changeset kinton-2.0-ga.xml::1335521716699-172::destevezg (generated)::(Checksum: 3:85f02fb7a8848fea7d1ce3c0bded4ada)
CREATE INDEX `vlan_network_assignment_networkid_FK` ON `kinton`.`vlan_network_assignment`(`vlan_network_id`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-172', '2.0.3', '3:85f02fb7a8848fea7d1ce3c0bded4ada', 172);

-- Changeset kinton-2.0-ga.xml::1335521716699-173::destevezg (generated)::(Checksum: 3:379bfe819f05378a04da486b0c7bc937)
CREATE INDEX `fk_vlans_dhcp_dhcp` ON `kinton`.`vlans_dhcpOption`(`idDhcpOption`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-173', '2.0.3', '3:379bfe819f05378a04da486b0c7bc937', 173);

-- Changeset kinton-2.0-ga.xml::1335521716699-174::destevezg (generated)::(Checksum: 3:21ce6f179a028699cffbd76cc009815c)
CREATE INDEX `fk_vlans_dhcp_vlan` ON `kinton`.`vlans_dhcpOption`(`idVlan`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-174', '2.0.3', '3:21ce6f179a028699cffbd76cc009815c', 174);

-- Changeset kinton-2.0-ga.xml::1335521716699-175::destevezg (generated)::(Checksum: 3:9b9c250ca12d4074b268b3ad6416a4ea)
CREATE INDEX `idManagement_FK` ON `kinton`.`volume_management`(`idManagement`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-175', '2.0.3', '3:9b9c250ca12d4074b268b3ad6416a4ea', 175);

-- Changeset kinton-2.0-ga.xml::1335521716699-176::destevezg (generated)::(Checksum: 3:997cd7819b3051a5eb4417b6be9cd604)
CREATE INDEX `idStorage_FK` ON `kinton`.`volume_management`(`idStorage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-176', '2.0.3', '3:997cd7819b3051a5eb4417b6be9cd604', 176);

-- Changeset kinton-2.0-ga.xml::1335521716699-177::destevezg (generated)::(Checksum: 3:11ae9e79c502d853bddb6550593f9bb7)
CREATE INDEX `volumemanagement_FK3` ON `kinton`.`volume_management`(`idImage`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-177', '2.0.3', '3:11ae9e79c502d853bddb6550593f9bb7', 177);

-- Changeset kinton-2.0-ga.xml::1335521716699-178::destevezg (generated)::(Checksum: 3:1825caf487f628d6783e513eb5464f9f)
CREATE INDEX `FK_eerule_enterprise_1` ON `kinton`.`workload_enterprise_exclusion_rule`(`idEnterprise1`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-178', '2.0.3', '3:1825caf487f628d6783e513eb5464f9f', 178);

-- Changeset kinton-2.0-ga.xml::1335521716699-179::destevezg (generated)::(Checksum: 3:818660d5b039fd2edf6baba5ed83ca09)
CREATE INDEX `FK_eerule_enterprise_2` ON `kinton`.`workload_enterprise_exclusion_rule`(`idEnterprise2`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-179', '2.0.3', '3:818660d5b039fd2edf6baba5ed83ca09', 179);

-- Changeset kinton-2.0-ga.xml::1335521716699-180::destevezg (generated)::(Checksum: 3:c5b877993e4778c53222940b25035cc9)
CREATE INDEX `FK_fprule_datacenter` ON `kinton`.`workload_fit_policy_rule`(`idDatacenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-180', '2.0.3', '3:c5b877993e4778c53222940b25035cc9', 180);

-- Changeset kinton-2.0-ga.xml::1335521716699-181::destevezg (generated)::(Checksum: 3:266307ce4ad4dfe6bd68c23e40d64183)
CREATE INDEX `FK_mlrule_datacenter` ON `kinton`.`workload_machine_load_rule`(`idDatacenter`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-181', '2.0.3', '3:266307ce4ad4dfe6bd68c23e40d64183', 181);

-- Changeset kinton-2.0-ga.xml::1335521716699-182::destevezg (generated)::(Checksum: 3:bf0ef2abaa7122ed820fcbd2be7489f6)
CREATE INDEX `FK_mlrule_machine` ON `kinton`.`workload_machine_load_rule`(`idMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-182', '2.0.3', '3:bf0ef2abaa7122ed820fcbd2be7489f6', 182);

-- Changeset kinton-2.0-ga.xml::1335521716699-183::destevezg (generated)::(Checksum: 3:0786291ebb06628564916b8d1134eeec)
CREATE INDEX `FK_mlrule_rack` ON `kinton`.`workload_machine_load_rule`(`idRack`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton-2.0-ga.xml', '1335521716699-183', '2.0.3', '3:0786291ebb06628564916b8d1134eeec', 183);

-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::1335280201177-1::destevezg (generated)::(Checksum: 3:fd4f14896387e0fd842ac1cbc5b89239)
CREATE TABLE `kinton`.`accounting_event_detail_HF1` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL_HF1` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', '1335280201177-1', '2.0.3', '3:fd4f14896387e0fd842ac1cbc5b89239', 184);

-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::ABICLOUDPREMIUM-432432::destevez::(Checksum: 3:1fd743ccdf084124370692129205fcd8)
DROP TRIGGER IF EXISTS kinton.datacenter_created;

CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);

END IF;

END;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevez', '', NOW(), 'SQL From File', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', 'ABICLOUDPREMIUM-432432', '2.0.3', '3:1fd743ccdf084124370692129205fcd8', 185);

-- Changeset delta/2.0.0-HF2_new/deltachangelog.xml::1335280201177-1::destevezg (generated)::(Checksum: 3:822052e1ee0bbf2727e909744cdbbd48)
CREATE TABLE `kinton`.`accounting_event_detail_HF2` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL_HF2` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'delta/2.0.0-HF2_new/deltachangelog.xml', '1335280201177-1', '2.0.3', '3:822052e1ee0bbf2727e909744cdbbd48', 186);

-- Changeset delta/2.0.0-HF2_new/deltachangelog.xml::ABICLOUDPREMIUM-9899999::destevez::(Checksum: 3:cb1689ce5579b918d949bf48f2c9aa9a)
DROP TRIGGER IF EXISTS kinton.datacenter_created;

CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);

END IF;

END;

DROP TRIGGER IF EXISTS kinton.datacenter_deleted;

CREATE TRIGGER kinton.datacenter_deleted AFTER DELETE ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
	DELETE FROM dc_enterprise_stats WHERE idDataCenter = OLD.idDataCenter;

DELETE FROM cloud_usage_stats WHERE idDataCenter = OLD.idDataCenter;

END IF;

END;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevez', '', NOW(), 'SQL From File (x2)', 'EXECUTED', 'delta/2.0.0-HF2_new/deltachangelog.xml', 'ABICLOUDPREMIUM-9899999', '2.0.3', '3:cb1689ce5579b918d949bf48f2c9aa9a', 187);

