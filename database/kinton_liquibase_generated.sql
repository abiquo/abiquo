-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: kinton_master_changelog.xml
-- Ran at: 4/26/12 4:27 PM
-- Against: root@destevez.bcn.abiquo.com@jdbc:mysql://10.60.12.230:3306/kinton
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE `kinton`.`DATABASECHANGELOGLOCK` (`ID` INT NOT NULL, `LOCKED` TINYINT(1) NOT NULL, `LOCKGRANTED` DATETIME, `LOCKEDBY` VARCHAR(255), CONSTRAINT `PK_DATABASECHANGELOGLOCK` PRIMARY KEY (`ID`));

INSERT INTO `kinton`.`DATABASECHANGELOGLOCK` (`ID`, `LOCKED`) VALUES (1, 0);

-- Lock Database
-- Create Database Change Log Table
CREATE TABLE `kinton`.`DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35), `DESCRIPTION` VARCHAR(255), `COMMENTS` VARCHAR(255), `TAG` VARCHAR(255), `LIQUIBASE` VARCHAR(20), CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

-- Changeset kinton2_0_ga.xml::1334562618578-1::destevezg (generated)::(Checksum: 3:c32bbf075db7c5933ca3cce5df660aa9)
CREATE TABLE `kinton`.`alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-1', '2.0.3', '3:c32bbf075db7c5933ca3cce5df660aa9', 1);

-- Changeset kinton2_0_ga.xml::1334562618578-2::destevezg (generated)::(Checksum: 3:b518e45dd85a26cde440580145fcddb4)
CREATE TABLE `kinton`.`apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-2', '2.0.3', '3:b518e45dd85a26cde440580145fcddb4', 2);

-- Changeset kinton2_0_ga.xml::1334562618578-3::destevezg (generated)::(Checksum: 3:966996751618877d8c5c9d810821a619)
CREATE TABLE `kinton`.`auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-3', '2.0.3', '3:966996751618877d8c5c9d810821a619', 3);

-- Changeset kinton2_0_ga.xml::1334562618578-4::destevezg (generated)::(Checksum: 3:447eb654eeabbcb662cb7dad38635820)
CREATE TABLE `kinton`.`auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-4', '2.0.3', '3:447eb654eeabbcb662cb7dad38635820', 4);

-- Changeset kinton2_0_ga.xml::1334562618578-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `kinton`.`auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 5);

-- Changeset kinton2_0_ga.xml::1334562618578-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `kinton`.`category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 6);

-- Changeset kinton2_0_ga.xml::1334562618578-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `kinton`.`chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 7);

-- Changeset kinton2_0_ga.xml::1334562618578-8::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `kinton`.`cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-8', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 8);

-- Changeset kinton2_0_ga.xml::1334562618578-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `kinton`.`costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 9);

-- Changeset kinton2_0_ga.xml::1334562618578-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `kinton`.`costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 10);

-- Changeset kinton2_0_ga.xml::1334562618578-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `kinton`.`currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 11);

-- Changeset kinton2_0_ga.xml::1334562618578-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `kinton`.`datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 12);

-- Changeset kinton2_0_ga.xml::1334562618578-13::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `kinton`.`datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-13', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 13);

-- Changeset kinton2_0_ga.xml::1334562618578-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `kinton`.`datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 14);

-- Changeset kinton2_0_ga.xml::1334562618578-15::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `kinton`.`dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-15', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 15);

-- Changeset kinton2_0_ga.xml::1334562618578-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `kinton`.`dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 16);

-- Changeset kinton2_0_ga.xml::1334562618578-17::destevezg (generated)::(Checksum: 3:ffd62de872535e1f2da1cac582b3c9d5)
CREATE TABLE `kinton`.`disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-17', '2.0.3', '3:ffd62de872535e1f2da1cac582b3c9d5', 17);

-- Changeset kinton2_0_ga.xml::1334562618578-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `kinton`.`diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 18);

-- Changeset kinton2_0_ga.xml::1334562618578-19::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `kinton`.`enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-19', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 19);

-- Changeset kinton2_0_ga.xml::1334562618578-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 20);

-- Changeset kinton2_0_ga.xml::1334562618578-21::destevezg (generated)::(Checksum: 3:3e94390d029bf8e6061698eb5628d573)
CREATE TABLE `kinton`.`enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-21', '2.0.3', '3:3e94390d029bf8e6061698eb5628d573', 21);

-- Changeset kinton2_0_ga.xml::1334562618578-22::destevezg (generated)::(Checksum: 3:be4693925397c572062f1fab8c984362)
CREATE TABLE `kinton`.`enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50), `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-22', '2.0.3', '3:be4693925397c572062f1fab8c984362', 22);

-- Changeset kinton2_0_ga.xml::1334562618578-23::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `kinton`.`enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-23', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 23);

-- Changeset kinton2_0_ga.xml::1334562618578-24::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `kinton`.`enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-24', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 24);

-- Changeset kinton2_0_ga.xml::1334562618578-25::destevezg (generated)::(Checksum: 3:f6211931acdcc03c90d5c6d208a910b9)
CREATE TABLE `kinton`.`heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-25', '2.0.3', '3:f6211931acdcc03c90d5c6d208a910b9', 25);

-- Changeset kinton2_0_ga.xml::1334562618578-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `kinton`.`hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 26);

-- Changeset kinton2_0_ga.xml::1334562618578-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `kinton`.`initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 27);

-- Changeset kinton2_0_ga.xml::1334562618578-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `kinton`.`ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 28);

-- Changeset kinton2_0_ga.xml::1334562618578-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `kinton`.`license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 29);

-- Changeset kinton2_0_ga.xml::1334562618578-30::destevezg (generated)::(Checksum: 3:38e9d9ed33afac96b738855a00109f9c)
CREATE TABLE `kinton`.`log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-30', '2.0.3', '3:38e9d9ed33afac96b738855a00109f9c', 30);

-- Changeset kinton2_0_ga.xml::1334562618578-31::destevezg (generated)::(Checksum: 3:0a23cc6bd4adfbad1eaa59b5b7da2f2e)
CREATE TABLE `kinton`.`metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-31', '2.0.3', '3:0a23cc6bd4adfbad1eaa59b5b7da2f2e', 31);

-- Changeset kinton2_0_ga.xml::1334562618578-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `kinton`.`network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 32);

-- Changeset kinton2_0_ga.xml::1334562618578-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `kinton`.`network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 33);

-- Changeset kinton2_0_ga.xml::1334562618578-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `kinton`.`node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 34);

-- Changeset kinton2_0_ga.xml::1334562618578-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `kinton`.`node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 35);

-- Changeset kinton2_0_ga.xml::1334562618578-36::destevezg (generated)::(Checksum: 3:b6fc7632116240a776aa00853de6bcad)
CREATE TABLE `kinton`.`nodenetwork` (`idNode` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-36', '2.0.3', '3:b6fc7632116240a776aa00853de6bcad', 36);

-- Changeset kinton2_0_ga.xml::1334562618578-37::destevezg (generated)::(Checksum: 3:6952f964ce37833b8144613d3cf11344)
CREATE TABLE `kinton`.`noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-37', '2.0.3', '3:6952f964ce37833b8144613d3cf11344', 37);

-- Changeset kinton2_0_ga.xml::1334562618578-38::destevezg (generated)::(Checksum: 3:72bf3673a02388e2bc0da52ae70e5fce)
CREATE TABLE `kinton`.`nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-38', '2.0.3', '3:72bf3673a02388e2bc0da52ae70e5fce', 38);

-- Changeset kinton2_0_ga.xml::1334562618578-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `kinton`.`nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 39);

-- Changeset kinton2_0_ga.xml::1334562618578-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 40);

-- Changeset kinton2_0_ga.xml::1334562618578-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `kinton`.`ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 41);

-- Changeset kinton2_0_ga.xml::1334562618578-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `kinton`.`ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 42);

-- Changeset kinton2_0_ga.xml::1334562618578-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `kinton`.`ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 43);

-- Changeset kinton2_0_ga.xml::1334562618578-44::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `kinton`.`physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-44', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 44);

-- Changeset kinton2_0_ga.xml::1334562618578-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `kinton`.`pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 45);

-- Changeset kinton2_0_ga.xml::1334562618578-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `kinton`.`pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 46);

-- Changeset kinton2_0_ga.xml::1334562618578-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `kinton`.`pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 47);

-- Changeset kinton2_0_ga.xml::1334562618578-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `kinton`.`privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 48);

-- Changeset kinton2_0_ga.xml::1334562618578-49::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `kinton`.`rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-49', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 49);

-- Changeset kinton2_0_ga.xml::1334562618578-50::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `kinton`.`rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-50', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 50);

-- Changeset kinton2_0_ga.xml::1334562618578-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `kinton`.`rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 51);

-- Changeset kinton2_0_ga.xml::1334562618578-52::destevezg (generated)::(Checksum: 3:ed4ae73f975deb795a4e2fe4980ada26)
CREATE TABLE `kinton`.`register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-52', '2.0.3', '3:ed4ae73f975deb795a4e2fe4980ada26', 52);

-- Changeset kinton2_0_ga.xml::1334562618578-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `kinton`.`remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 53);

-- Changeset kinton2_0_ga.xml::1334562618578-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `kinton`.`repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 54);

-- Changeset kinton2_0_ga.xml::1334562618578-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `kinton`.`role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 55);

-- Changeset kinton2_0_ga.xml::1334562618578-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `kinton`.`role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 56);

-- Changeset kinton2_0_ga.xml::1334562618578-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `kinton`.`roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 57);

-- Changeset kinton2_0_ga.xml::1334562618578-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `kinton`.`session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 58);

-- Changeset kinton2_0_ga.xml::1334562618578-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `kinton`.`storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 59);

-- Changeset kinton2_0_ga.xml::1334562618578-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `kinton`.`storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 60);

-- Changeset kinton2_0_ga.xml::1334562618578-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `kinton`.`system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 61);

-- Changeset kinton2_0_ga.xml::1334562618578-62::destevezg (generated)::(Checksum: 3:31486daf8f610a7250344cb981627a60)
CREATE TABLE `kinton`.`tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-62', '2.0.3', '3:31486daf8f610a7250344cb981627a60', 62);

-- Changeset kinton2_0_ga.xml::1334562618578-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `kinton`.`tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 63);

-- Changeset kinton2_0_ga.xml::1334562618578-64::destevezg (generated)::(Checksum: 3:e5d525478dfcdecb18cc7cad873150c3)
CREATE TABLE `kinton`.`ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-64', '2.0.3', '3:e5d525478dfcdecb18cc7cad873150c3', 64);

-- Changeset kinton2_0_ga.xml::1334562618578-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `kinton`.`user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 65);

-- Changeset kinton2_0_ga.xml::1334562618578-66::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `kinton`.`vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-66', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 66);

-- Changeset kinton2_0_ga.xml::1334562618578-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `kinton`.`vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 67);

-- Changeset kinton2_0_ga.xml::1334562618578-68::destevezg (generated)::(Checksum: 3:aecbcce0078b6d04274190ba65cfca54)
CREATE TABLE `kinton`.`vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-68', '2.0.3', '3:aecbcce0078b6d04274190ba65cfca54', 68);

-- Changeset kinton2_0_ga.xml::1334562618578-69::destevezg (generated)::(Checksum: 3:bc9ba0c28876d849c819915c84e9cd70)
CREATE TABLE `kinton`.`virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-69', '2.0.3', '3:bc9ba0c28876d849c819915c84e9cd70', 69);

-- Changeset kinton2_0_ga.xml::1334562618578-70::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `kinton`.`virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-70', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 70);

-- Changeset kinton2_0_ga.xml::1334562618578-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `kinton`.`virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 71);

-- Changeset kinton2_0_ga.xml::1334562618578-72::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `kinton`.`virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-72', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 72);

-- Changeset kinton2_0_ga.xml::1334562618578-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `kinton`.`virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 73);

-- Changeset kinton2_0_ga.xml::1334562618578-74::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `kinton`.`virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-74', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 74);

-- Changeset kinton2_0_ga.xml::1334562618578-75::destevezg (generated)::(Checksum: 3:62ecd79335be6ba7c6365fb60199052d)
CREATE TABLE `kinton`.`virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-75', '2.0.3', '3:62ecd79335be6ba7c6365fb60199052d', 75);

-- Changeset kinton2_0_ga.xml::1334562618578-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `kinton`.`vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 76);

-- Changeset kinton2_0_ga.xml::1334562618578-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `kinton`.`vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 77);

-- Changeset kinton2_0_ga.xml::1334562618578-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `kinton`.`vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 78);

-- Changeset kinton2_0_ga.xml::1334562618578-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `kinton`.`volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 79);

-- Changeset kinton2_0_ga.xml::1334562618578-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `kinton`.`workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 80);

-- Changeset kinton2_0_ga.xml::1334562618578-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `kinton`.`workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 81);

-- Changeset kinton2_0_ga.xml::1334562618578-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `kinton`.`workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 82);

-- Changeset kinton2_0_ga.xml::1334562618578-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `kinton`.`datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 83);

-- Changeset kinton2_0_ga.xml::1334562618578-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 84);

-- Changeset kinton2_0_ga.xml::1334562618578-85::destevezg (generated)::(Checksum: 3:2dd4badadcd15f6378a42b518d5aab69)
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`, `idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-85', '2.0.3', '3:2dd4badadcd15f6378a42b518d5aab69', 85);

-- Changeset kinton2_0_ga.xml::1334562618578-86::destevezg (generated)::(Checksum: 3:39db06adeb41d3a986d04834d8609781)
ALTER TABLE `kinton`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-86', '2.0.3', '3:39db06adeb41d3a986d04834d8609781', 86);

-- Changeset kinton2_0_ga.xml::1334562618578-87::destevezg (generated)::(Checksum: 3:ef59cbaeca0e42a4ec1583e0a2c37306)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-87', '2.0.3', '3:ef59cbaeca0e42a4ec1583e0a2c37306', 87);

-- Changeset kinton2_0_ga.xml::1334562618578-88::destevezg (generated)::(Checksum: 3:12b2b3f5e6fdee97aa1af071c3ca3129)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-88', '2.0.3', '3:12b2b3f5e6fdee97aa1af071c3ca3129', 88);

-- Changeset kinton2_0_ga.xml::1334562618578-89::destevezg (generated)::(Checksum: 3:aab159bccf255ef411d6f652295aac91)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-89', '2.0.3', '3:aab159bccf255ef411d6f652295aac91', 89);

-- Changeset kinton2_0_ga.xml::1334562618578-90::destevezg (generated)::(Checksum: 3:2c2a3886ab85ac15a24d5b86278cee13)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-90', '2.0.3', '3:2c2a3886ab85ac15a24d5b86278cee13', 90);

-- Changeset kinton2_0_ga.xml::1334562618578-91::destevezg (generated)::(Checksum: 3:7babbcfac31aa94742a0b7c852cbb75c)
ALTER TABLE `kinton`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-91', '2.0.3', '3:7babbcfac31aa94742a0b7c852cbb75c', 91);

-- Changeset kinton2_0_ga.xml::1334562618578-92::destevezg (generated)::(Checksum: 3:e917f98533bb9aef158246f2b9ac3806)
ALTER TABLE `kinton`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-92', '2.0.3', '3:e917f98533bb9aef158246f2b9ac3806', 92);

-- Changeset kinton2_0_ga.xml::1334562618578-93::destevezg (generated)::(Checksum: 3:380b349c2867c97f3069d1ddea7af2dc)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-93', '2.0.3', '3:380b349c2867c97f3069d1ddea7af2dc', 93);

-- Changeset kinton2_0_ga.xml::1334562618578-94::destevezg (generated)::(Checksum: 3:6f74be1ae0f5ca600be744dc575c6b55)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-94', '2.0.3', '3:6f74be1ae0f5ca600be744dc575c6b55', 94);

-- Changeset kinton2_0_ga.xml::1334562618578-95::destevezg (generated)::(Checksum: 3:7cac3426929736d26932e589efcd2dba)
ALTER TABLE `kinton`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-95', '2.0.3', '3:7cac3426929736d26932e589efcd2dba', 95);

-- Changeset kinton2_0_ga.xml::1334562618578-96::destevezg (generated)::(Checksum: 3:8743ae41839e4a8c6e13b9b27c7c5100)
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-96', '2.0.3', '3:8743ae41839e4a8c6e13b9b27c7c5100', 96);

-- Changeset kinton2_0_ga.xml::1334562618578-97::destevezg (generated)::(Checksum: 3:39f1295773e78d4bfc80735d014153c6)
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-97', '2.0.3', '3:39f1295773e78d4bfc80735d014153c6', 97);

-- Changeset kinton2_0_ga.xml::1334562618578-98::destevezg (generated)::(Checksum: 3:b388d5c13eab7fc4ec7fcf6d82d2517c)
ALTER TABLE `kinton`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-98', '2.0.3', '3:b388d5c13eab7fc4ec7fcf6d82d2517c', 98);

-- Changeset kinton2_0_ga.xml::1334562618578-99::destevezg (generated)::(Checksum: 3:3e8be0e2f2e71febf08072f5abb2337b)
ALTER TABLE `kinton`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-99', '2.0.3', '3:3e8be0e2f2e71febf08072f5abb2337b', 99);

-- Changeset kinton2_0_ga.xml::1334562618578-100::destevezg (generated)::(Checksum: 3:9c85972815ba8590587f3e2a7baf8d2e)
ALTER TABLE `kinton`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-100', '2.0.3', '3:9c85972815ba8590587f3e2a7baf8d2e', 100);

-- Changeset kinton2_0_ga.xml::1334562618578-101::destevezg (generated)::(Checksum: 3:e45f0e33e210d975f95aa06f5a472a31)
ALTER TABLE `kinton`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-101', '2.0.3', '3:e45f0e33e210d975f95aa06f5a472a31', 101);

-- Changeset kinton2_0_ga.xml::1334562618578-102::destevezg (generated)::(Checksum: 3:685adf52299cb301be40ce79ea068f09)
ALTER TABLE `kinton`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-102', '2.0.3', '3:685adf52299cb301be40ce79ea068f09', 102);

-- Changeset kinton2_0_ga.xml::1334562618578-103::destevezg (generated)::(Checksum: 3:54e0036e5c4653ab7a70eaa8b7adc969)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-103', '2.0.3', '3:54e0036e5c4653ab7a70eaa8b7adc969', 103);

-- Changeset kinton2_0_ga.xml::1334562618578-104::destevezg (generated)::(Checksum: 3:c75595ecaf1f61870fe3be4ee1607a58)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-104', '2.0.3', '3:c75595ecaf1f61870fe3be4ee1607a58', 104);

-- Changeset kinton2_0_ga.xml::1334562618578-105::destevezg (generated)::(Checksum: 3:d0e422554cd4e0db8c124dcdcdc3e861)
ALTER TABLE `kinton`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-105', '2.0.3', '3:d0e422554cd4e0db8c124dcdcdc3e861', 105);

-- Changeset kinton2_0_ga.xml::1334562618578-106::destevezg (generated)::(Checksum: 3:fbefc45b254ad3dc7c2e08d64deb06e3)
ALTER TABLE `kinton`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-106', '2.0.3', '3:fbefc45b254ad3dc7c2e08d64deb06e3', 106);

-- Changeset kinton2_0_ga.xml::1334562618578-107::destevezg (generated)::(Checksum: 3:56db749940a3b0de035482dce9f42af3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-107', '2.0.3', '3:56db749940a3b0de035482dce9f42af3', 107);

-- Changeset kinton2_0_ga.xml::1334562618578-108::destevezg (generated)::(Checksum: 3:dee0fe179f63a7fff9a6a8e7459ef124)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-108', '2.0.3', '3:dee0fe179f63a7fff9a6a8e7459ef124', 108);

-- Changeset kinton2_0_ga.xml::1334562618578-109::destevezg (generated)::(Checksum: 3:db0c334d194b39f67e8541f4a4c8b31a)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-109', '2.0.3', '3:db0c334d194b39f67e8541f4a4c8b31a', 109);

-- Changeset kinton2_0_ga.xml::1334562618578-110::destevezg (generated)::(Checksum: 3:7700a7d110854e172f2f3252b1567293)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-110', '2.0.3', '3:7700a7d110854e172f2f3252b1567293', 110);

-- Changeset kinton2_0_ga.xml::1334562618578-111::destevezg (generated)::(Checksum: 3:350df72b50bbdd1974350647a819ba36)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-111', '2.0.3', '3:350df72b50bbdd1974350647a819ba36', 111);

-- Changeset kinton2_0_ga.xml::1334562618578-112::destevezg (generated)::(Checksum: 3:bc63c183d18becdad57fdb22ca2279b3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-112', '2.0.3', '3:bc63c183d18becdad57fdb22ca2279b3', 112);

-- Changeset kinton2_0_ga.xml::1334562618578-113::destevezg (generated)::(Checksum: 3:570a0810c943c0ba338369b35c4facc3)
ALTER TABLE `kinton`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-113', '2.0.3', '3:570a0810c943c0ba338369b35c4facc3', 113);

-- Changeset kinton2_0_ga.xml::1334562618578-114::destevezg (generated)::(Checksum: 3:36d32fb242d453bc21a77ae64ee5c23c)
ALTER TABLE `kinton`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-114', '2.0.3', '3:36d32fb242d453bc21a77ae64ee5c23c', 114);

-- Changeset kinton2_0_ga.xml::1334562618578-115::destevezg (generated)::(Checksum: 3:a139f4550368879e9dc8127cf2208b32)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-115', '2.0.3', '3:a139f4550368879e9dc8127cf2208b32', 115);

-- Changeset kinton2_0_ga.xml::1334562618578-116::destevezg (generated)::(Checksum: 3:14d64e301e24922cdccc4a2e745d788d)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-116', '2.0.3', '3:14d64e301e24922cdccc4a2e745d788d', 116);

-- Changeset kinton2_0_ga.xml::1334562618578-117::destevezg (generated)::(Checksum: 3:e8ceada3c162ec371d3e31171195c0b2)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-117', '2.0.3', '3:e8ceada3c162ec371d3e31171195c0b2', 117);

-- Changeset kinton2_0_ga.xml::1334562618578-118::destevezg (generated)::(Checksum: 3:f7d73df5dad5123e4901e04db283185e)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-118', '2.0.3', '3:f7d73df5dad5123e4901e04db283185e', 118);

-- Changeset kinton2_0_ga.xml::1334562618578-119::destevezg (generated)::(Checksum: 3:4cf48f9241ea2f379f0c8acb839d6818)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-119', '2.0.3', '3:4cf48f9241ea2f379f0c8acb839d6818', 119);

-- Changeset kinton2_0_ga.xml::1334562618578-120::destevezg (generated)::(Checksum: 3:7f80cb03ad6bfefe6034ca2a75988ee3)
ALTER TABLE `kinton`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-120', '2.0.3', '3:7f80cb03ad6bfefe6034ca2a75988ee3', 120);

-- Changeset kinton2_0_ga.xml::1334562618578-121::destevezg (generated)::(Checksum: 3:314f329efbdbe1ceffd2b8335ac24754)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-121', '2.0.3', '3:314f329efbdbe1ceffd2b8335ac24754', 121);

-- Changeset kinton2_0_ga.xml::1334562618578-122::destevezg (generated)::(Checksum: 3:0b8d008edf4729acede17f0436c857b6)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-122', '2.0.3', '3:0b8d008edf4729acede17f0436c857b6', 122);

-- Changeset kinton2_0_ga.xml::1334562618578-123::destevezg (generated)::(Checksum: 3:40cfac0dcf4c56d309494dcec042d513)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-123', '2.0.3', '3:40cfac0dcf4c56d309494dcec042d513', 123);

-- Changeset kinton2_0_ga.xml::1334562618578-124::destevezg (generated)::(Checksum: 3:590901f24718ac0ff77f3de502c8bf3f)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-124', '2.0.3', '3:590901f24718ac0ff77f3de502c8bf3f', 124);

-- Changeset kinton2_0_ga.xml::1334562618578-125::destevezg (generated)::(Checksum: 3:4b4676b5d7cb3f195237d0a5ea3563c1)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-125', '2.0.3', '3:4b4676b5d7cb3f195237d0a5ea3563c1', 125);

-- Changeset kinton2_0_ga.xml::1334562618578-126::destevezg (generated)::(Checksum: 3:a316da2bf6cfa6eab48b556edbcb1686)
ALTER TABLE `kinton`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-126', '2.0.3', '3:a316da2bf6cfa6eab48b556edbcb1686', 126);

-- Changeset kinton2_0_ga.xml::1334562618578-127::destevezg (generated)::(Checksum: 3:c75b594b9fa56384d12679e3f3f39844)
ALTER TABLE `kinton`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-127', '2.0.3', '3:c75b594b9fa56384d12679e3f3f39844', 127);

-- Changeset kinton2_0_ga.xml::1334562618578-128::destevezg (generated)::(Checksum: 3:6c2f073057a45a69c1b7db5f4ee07de1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-128', '2.0.3', '3:6c2f073057a45a69c1b7db5f4ee07de1', 128);

-- Changeset kinton2_0_ga.xml::1334562618578-129::destevezg (generated)::(Checksum: 3:5ed1e047f733146bb1bb75cfbaa63f8e)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-129', '2.0.3', '3:5ed1e047f733146bb1bb75cfbaa63f8e', 129);

-- Changeset kinton2_0_ga.xml::1334562618578-130::destevezg (generated)::(Checksum: 3:15014a2695966373e7a6cae113893ff1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-130', '2.0.3', '3:15014a2695966373e7a6cae113893ff1', 130);

-- Changeset kinton2_0_ga.xml::1334562618578-131::destevezg (generated)::(Checksum: 3:f4ba13ebaac92029c85db4adfd4bb524)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-131', '2.0.3', '3:f4ba13ebaac92029c85db4adfd4bb524', 131);

-- Changeset kinton2_0_ga.xml::1334562618578-132::destevezg (generated)::(Checksum: 3:99fb777debdd79e43a64df61c8aab9f1)
ALTER TABLE `kinton`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-132', '2.0.3', '3:99fb777debdd79e43a64df61c8aab9f1', 132);

-- Changeset kinton2_0_ga.xml::1334562618578-133::destevezg (generated)::(Checksum: 3:381a734392ef762c6e4e727db64fdcdc)
ALTER TABLE `kinton`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-133', '2.0.3', '3:381a734392ef762c6e4e727db64fdcdc', 133);

-- Changeset kinton2_0_ga.xml::1334562618578-134::destevezg (generated)::(Checksum: 3:0afc6b5a509fa965da8109ecf2444522)
ALTER TABLE `kinton`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-134', '2.0.3', '3:0afc6b5a509fa965da8109ecf2444522', 134);

-- Changeset kinton2_0_ga.xml::1334562618578-135::destevezg (generated)::(Checksum: 3:6e1ac40f00f986ff6827ddffddc4417b)
ALTER TABLE `kinton`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-135', '2.0.3', '3:6e1ac40f00f986ff6827ddffddc4417b', 135);

-- Changeset kinton2_0_ga.xml::1334562618578-136::destevezg (generated)::(Checksum: 3:0e3df47ebc27a0e2d3d449f673c3436e)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-136', '2.0.3', '3:0e3df47ebc27a0e2d3d449f673c3436e', 136);

-- Changeset kinton2_0_ga.xml::1334562618578-137::destevezg (generated)::(Checksum: 3:b7d29b45d463a86fe85165ccd981b2a4)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-137', '2.0.3', '3:b7d29b45d463a86fe85165ccd981b2a4', 137);

-- Changeset kinton2_0_ga.xml::1334562618578-138::destevezg (generated)::(Checksum: 3:0a3a0dce75328a168956b34f2a166124)
ALTER TABLE `kinton`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-138', '2.0.3', '3:0a3a0dce75328a168956b34f2a166124', 138);

-- Changeset kinton2_0_ga.xml::1334562618578-139::destevezg (generated)::(Checksum: 3:a9f68a95692fd4cb61d1ab7f54a6add0)
ALTER TABLE `kinton`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-139', '2.0.3', '3:a9f68a95692fd4cb61d1ab7f54a6add0', 139);

-- Changeset kinton2_0_ga.xml::1334562618578-140::destevezg (generated)::(Checksum: 3:51bc92d1f8458a6758f84d5e40c6f88d)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-140', '2.0.3', '3:51bc92d1f8458a6758f84d5e40c6f88d', 140);

-- Changeset kinton2_0_ga.xml::1334562618578-141::destevezg (generated)::(Checksum: 3:732046a805d961eb44971fd636d52594)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-141', '2.0.3', '3:732046a805d961eb44971fd636d52594', 141);

-- Changeset kinton2_0_ga.xml::1334562618578-142::destevezg (generated)::(Checksum: 3:31e41feafb066c2be3b1cc2857f49208)
ALTER TABLE `kinton`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-142', '2.0.3', '3:31e41feafb066c2be3b1cc2857f49208', 142);

-- Changeset kinton2_0_ga.xml::1334562618578-143::destevezg (generated)::(Checksum: 3:466a1d498f0c2740539a49b128d9b6de)
ALTER TABLE `kinton`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-143', '2.0.3', '3:466a1d498f0c2740539a49b128d9b6de', 143);

-- Changeset kinton2_0_ga.xml::1334562618578-144::destevezg (generated)::(Checksum: 3:2c7e302fae12c8e84f18f3dba9f5c40c)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-144', '2.0.3', '3:2c7e302fae12c8e84f18f3dba9f5c40c', 144);

-- Changeset kinton2_0_ga.xml::1334562618578-145::destevezg (generated)::(Checksum: 3:b632cd05a5d8ab67cfad62318a6feacd)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-145', '2.0.3', '3:b632cd05a5d8ab67cfad62318a6feacd', 145);

-- Changeset kinton2_0_ga.xml::1334562618578-146::destevezg (generated)::(Checksum: 3:1361f06e4430e3572388dc130cf3f6ae)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-146', '2.0.3', '3:1361f06e4430e3572388dc130cf3f6ae', 146);

-- Changeset kinton2_0_ga.xml::1334562618578-147::destevezg (generated)::(Checksum: 3:fa81f7866672e0064abe59ce544d16ee)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-147', '2.0.3', '3:fa81f7866672e0064abe59ce544d16ee', 147);

-- Changeset kinton2_0_ga.xml::1334562618578-148::destevezg (generated)::(Checksum: 3:21d1330e830eda559d49619da27d4a2d)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-148', '2.0.3', '3:21d1330e830eda559d49619da27d4a2d', 148);

-- Changeset kinton2_0_ga.xml::1334562618578-149::destevezg (generated)::(Checksum: 3:6d20670c1bdea037fa86029d32e95c4e)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-149', '2.0.3', '3:6d20670c1bdea037fa86029d32e95c4e', 149);

-- Changeset kinton2_0_ga.xml::1334562618578-150::destevezg (generated)::(Checksum: 3:f93371517d74a4eab3924ee099b26c53)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-150', '2.0.3', '3:f93371517d74a4eab3924ee099b26c53', 150);

-- Changeset kinton2_0_ga.xml::1334562618578-151::destevezg (generated)::(Checksum: 3:206402c502be46899203f3badd9d8ec7)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-151', '2.0.3', '3:206402c502be46899203f3badd9d8ec7', 151);

-- Changeset kinton2_0_ga.xml::1334562618578-152::destevezg (generated)::(Checksum: 3:4b1fa941844e12bc85b4a6f54dca3194)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-152', '2.0.3', '3:4b1fa941844e12bc85b4a6f54dca3194', 152);

-- Changeset kinton2_0_ga.xml::1334562618578-153::destevezg (generated)::(Checksum: 3:72511dad2d82e148a62eadbe7350381a)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-153', '2.0.3', '3:72511dad2d82e148a62eadbe7350381a', 153);

-- Changeset kinton2_0_ga.xml::1334562618578-154::destevezg (generated)::(Checksum: 3:913ff3701711d54eee00d1fb8b58389d)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-154', '2.0.3', '3:913ff3701711d54eee00d1fb8b58389d', 154);

-- Changeset kinton2_0_ga.xml::1334562618578-155::destevezg (generated)::(Checksum: 3:082da0be85a69a1ebfc2a09ae9ec94e4)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-155', '2.0.3', '3:082da0be85a69a1ebfc2a09ae9ec94e4', 155);

-- Changeset kinton2_0_ga.xml::1334562618578-156::destevezg (generated)::(Checksum: 3:17e6766e834c47a60ded2846e28a0374)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-156', '2.0.3', '3:17e6766e834c47a60ded2846e28a0374', 156);

-- Changeset kinton2_0_ga.xml::1334562618578-157::destevezg (generated)::(Checksum: 3:3887fe4ffa2434ffbe9fdc910afa8538)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-157', '2.0.3', '3:3887fe4ffa2434ffbe9fdc910afa8538', 157);

-- Changeset kinton2_0_ga.xml::1334562618578-158::destevezg (generated)::(Checksum: 3:4f435a6168a0a18bb1d0714d21361b71)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-158', '2.0.3', '3:4f435a6168a0a18bb1d0714d21361b71', 158);

-- Changeset kinton2_0_ga.xml::1334562618578-159::destevezg (generated)::(Checksum: 3:1af426b7fdcaf93f1b45d9d5e8aa1bdc)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-159', '2.0.3', '3:1af426b7fdcaf93f1b45d9d5e8aa1bdc', 159);

-- Changeset kinton2_0_ga.xml::1334562618578-160::destevezg (generated)::(Checksum: 3:f7c4c533470ece45570602545702a16a)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-160', '2.0.3', '3:f7c4c533470ece45570602545702a16a', 160);

-- Changeset kinton2_0_ga.xml::1334562618578-161::destevezg (generated)::(Checksum: 3:c93821360ad0b67578d33e4371fca936)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-161', '2.0.3', '3:c93821360ad0b67578d33e4371fca936', 161);

-- Changeset kinton2_0_ga.xml::1334562618578-162::destevezg (generated)::(Checksum: 3:d44c1e70581845fad25b877d07c96182)
ALTER TABLE `kinton`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-162', '2.0.3', '3:d44c1e70581845fad25b877d07c96182', 162);

-- Changeset kinton2_0_ga.xml::1334562618578-163::destevezg (generated)::(Checksum: 3:dd8fdabb1b5568d9fbbe0342574633c4)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-163', '2.0.3', '3:dd8fdabb1b5568d9fbbe0342574633c4', 163);

-- Changeset kinton2_0_ga.xml::1334562618578-164::destevezg (generated)::(Checksum: 3:818367047e672790ff3ac9c3d13cec5e)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-164', '2.0.3', '3:818367047e672790ff3ac9c3d13cec5e', 164);

-- Changeset kinton2_0_ga.xml::1334562618578-165::destevezg (generated)::(Checksum: 3:9424adc44a74d3737f24c3d5b5d812fe)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-165', '2.0.3', '3:9424adc44a74d3737f24c3d5b5d812fe', 165);

-- Changeset kinton2_0_ga.xml::1334562618578-166::destevezg (generated)::(Checksum: 3:46215ad45eb3e72d8ee65ee7be941e1a)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-166', '2.0.3', '3:46215ad45eb3e72d8ee65ee7be941e1a', 166);

-- Changeset kinton2_0_ga.xml::1334562618578-167::destevezg (generated)::(Checksum: 3:d09aba94d1f59c8262f5399abec69cc5)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-167', '2.0.3', '3:d09aba94d1f59c8262f5399abec69cc5', 167);

-- Changeset kinton2_0_ga.xml::1334562618578-168::destevezg (generated)::(Checksum: 3:e717cc8fc89cb02d56bd262979378060)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-168', '2.0.3', '3:e717cc8fc89cb02d56bd262979378060', 168);

-- Changeset kinton2_0_ga.xml::1334562618578-169::destevezg (generated)::(Checksum: 3:c2d45eafaae6aa722440adfe212203c3)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-169', '2.0.3', '3:c2d45eafaae6aa722440adfe212203c3', 169);

-- Changeset kinton2_0_ga.xml::1334562618578-170::destevezg (generated)::(Checksum: 3:6ebfd010b6b125ab61846d74710cdb9c)
ALTER TABLE `kinton`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-170', '2.0.3', '3:6ebfd010b6b125ab61846d74710cdb9c', 170);

-- Changeset kinton2_0_ga.xml::1334562618578-171::destevezg (generated)::(Checksum: 3:6b5855cbae91f21f0eecc23d7d31e7d6)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-171', '2.0.3', '3:6b5855cbae91f21f0eecc23d7d31e7d6', 171);

-- Changeset kinton2_0_ga.xml::1334562618578-172::destevezg (generated)::(Checksum: 3:8a5267a1f2d46f48e685e311a9a2bb38)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-172', '2.0.3', '3:8a5267a1f2d46f48e685e311a9a2bb38', 172);

-- Changeset kinton2_0_ga.xml::1334562618578-173::destevezg (generated)::(Checksum: 3:ced9008c983144fe8e36c11ee1d24a81)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-173', '2.0.3', '3:ced9008c983144fe8e36c11ee1d24a81', 173);

-- Changeset kinton2_0_ga.xml::1334562618578-174::destevezg (generated)::(Checksum: 3:fa97c2e5bfec4084f586a823469f3b1f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-174', '2.0.3', '3:fa97c2e5bfec4084f586a823469f3b1f', 174);

-- Changeset kinton2_0_ga.xml::1334562618578-175::destevezg (generated)::(Checksum: 3:f57948dcc96f12c0699213820b6b756f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-175', '2.0.3', '3:f57948dcc96f12c0699213820b6b756f', 175);

-- Changeset kinton2_0_ga.xml::1334562618578-176::destevezg (generated)::(Checksum: 3:e1ca982714c144ed2d5ac20561bc6657)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-176', '2.0.3', '3:e1ca982714c144ed2d5ac20561bc6657', 176);

-- Changeset kinton2_0_ga.xml::1334562618578-177::destevezg (generated)::(Checksum: 3:746c3e281a036d6ada1b5e3fd95a4696)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-177', '2.0.3', '3:746c3e281a036d6ada1b5e3fd95a4696', 177);

-- Changeset kinton2_0_ga.xml::1334562618578-178::destevezg (generated)::(Checksum: 3:b3376ebce0a03581c19a96f0c56bbd66)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-178', '2.0.3', '3:b3376ebce0a03581c19a96f0c56bbd66', 178);

-- Changeset kinton2_0_ga.xml::1334562618578-179::destevezg (generated)::(Checksum: 3:c16c6d833472e00f707818c0d317b44b)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-179', '2.0.3', '3:c16c6d833472e00f707818c0d317b44b', 179);

-- Changeset kinton2_0_ga.xml::1334562618578-180::destevezg (generated)::(Checksum: 3:420c8225285f8d8e374d50a1ed9e237e)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-180', '2.0.3', '3:420c8225285f8d8e374d50a1ed9e237e', 180);

-- Changeset kinton2_0_ga.xml::1334562618578-181::destevezg (generated)::(Checksum: 3:8f456a92ce7ba6f2b3625311cb2a47cf)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-181', '2.0.3', '3:8f456a92ce7ba6f2b3625311cb2a47cf', 181);

-- Changeset kinton2_0_ga.xml::1334562618578-182::destevezg (generated)::(Checksum: 3:c5ab00bc6a57c9809eb1be93120180ba)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-182', '2.0.3', '3:c5ab00bc6a57c9809eb1be93120180ba', 182);

-- Changeset kinton2_0_ga.xml::1334562618578-183::destevezg (generated)::(Checksum: 3:1035cb414581bebcdacdd2a161d19a41)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-183', '2.0.3', '3:1035cb414581bebcdacdd2a161d19a41', 183);

-- Changeset kinton2_0_ga.xml::1334562618578-184::destevezg (generated)::(Checksum: 3:f7d0b7bcff44df8f076be460f1172674)
ALTER TABLE `kinton`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-184', '2.0.3', '3:f7d0b7bcff44df8f076be460f1172674', 184);

-- Changeset kinton2_0_ga.xml::1334562618578-185::destevezg (generated)::(Checksum: 3:2724c06259dee3fa38ec1a3bd14d32b5)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-185', '2.0.3', '3:2724c06259dee3fa38ec1a3bd14d32b5', 185);

-- Changeset kinton2_0_ga.xml::1334562618578-186::destevezg (generated)::(Checksum: 3:c1c812e559c885292b18196d35ef708e)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-186', '2.0.3', '3:c1c812e559c885292b18196d35ef708e', 186);

-- Changeset kinton2_0_ga.xml::1334562618578-187::destevezg (generated)::(Checksum: 3:5d48f5fe3bb5c924a4e96180cc3d9790)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-187', '2.0.3', '3:5d48f5fe3bb5c924a4e96180cc3d9790', 187);

-- Changeset kinton2_0_ga.xml::1334562618578-188::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `kinton`.`category`(`name`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-188', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 188);

-- Changeset kinton2_0_ga.xml::1334562618578-189::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `kinton`.`user`(`user`, `authType`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334562618578-189', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 189);

-- Changeset kinton2_0_ga.xml::1334584506393-1::destevezg (generated)::(Checksum: 3:4cc953d671ea64d307e8d8ff11bd6220)
CREATE TABLE `kinton`.`alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-1', '2.0.3', '3:4cc953d671ea64d307e8d8ff11bd6220', 190);

-- Changeset kinton2_0_ga.xml::1334584506393-2::destevezg (generated)::(Checksum: 3:8e919ec45e59bcb22749fbbb8f8e7731)
CREATE TABLE `kinton`.`apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-2', '2.0.3', '3:8e919ec45e59bcb22749fbbb8f8e7731', 191);

-- Changeset kinton2_0_ga.xml::1334584506393-3::destevezg (generated)::(Checksum: 3:3a64a5ee5cd7e25bfab74647244666c9)
CREATE TABLE `kinton`.`auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-3', '2.0.3', '3:3a64a5ee5cd7e25bfab74647244666c9', 192);

-- Changeset kinton2_0_ga.xml::1334584506393-4::destevezg (generated)::(Checksum: 3:d5a57e91c407bb3e4286f207929d13ce)
CREATE TABLE `kinton`.`auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-4', '2.0.3', '3:d5a57e91c407bb3e4286f207929d13ce', 193);

-- Changeset kinton2_0_ga.xml::1334584506393-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `kinton`.`auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 194);

-- Changeset kinton2_0_ga.xml::1334584506393-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `kinton`.`category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 195);

-- Changeset kinton2_0_ga.xml::1334584506393-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `kinton`.`chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 196);

-- Changeset kinton2_0_ga.xml::1334584506393-8::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `kinton`.`cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-8', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 197);

-- Changeset kinton2_0_ga.xml::1334584506393-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `kinton`.`costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 198);

-- Changeset kinton2_0_ga.xml::1334584506393-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `kinton`.`costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 199);

-- Changeset kinton2_0_ga.xml::1334584506393-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `kinton`.`currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 200);

-- Changeset kinton2_0_ga.xml::1334584506393-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `kinton`.`datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 201);

-- Changeset kinton2_0_ga.xml::1334584506393-13::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `kinton`.`datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-13', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 202);

-- Changeset kinton2_0_ga.xml::1334584506393-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `kinton`.`datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 203);

-- Changeset kinton2_0_ga.xml::1334584506393-15::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `kinton`.`dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-15', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 204);

-- Changeset kinton2_0_ga.xml::1334584506393-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `kinton`.`dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 205);

-- Changeset kinton2_0_ga.xml::1334584506393-17::destevezg (generated)::(Checksum: 3:945b273b2813740dd085b21b2aa00bdb)
CREATE TABLE `kinton`.`disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-17', '2.0.3', '3:945b273b2813740dd085b21b2aa00bdb', 206);

-- Changeset kinton2_0_ga.xml::1334584506393-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `kinton`.`diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 207);

-- Changeset kinton2_0_ga.xml::1334584506393-19::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `kinton`.`enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-19', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 208);

-- Changeset kinton2_0_ga.xml::1334584506393-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 209);

-- Changeset kinton2_0_ga.xml::1334584506393-21::destevezg (generated)::(Checksum: 3:c67606071cfc197cd0d312b346c48f46)
CREATE TABLE `kinton`.`enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-21', '2.0.3', '3:c67606071cfc197cd0d312b346c48f46', 210);

-- Changeset kinton2_0_ga.xml::1334584506393-22::destevezg (generated)::(Checksum: 3:501eb9f341a105a7a8c396cf25b447ce)
CREATE TABLE `kinton`.`enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-22', '2.0.3', '3:501eb9f341a105a7a8c396cf25b447ce', 211);

-- Changeset kinton2_0_ga.xml::1334584506393-23::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `kinton`.`enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-23', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 212);

-- Changeset kinton2_0_ga.xml::1334584506393-24::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `kinton`.`enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-24', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 213);

-- Changeset kinton2_0_ga.xml::1334584506393-25::destevezg (generated)::(Checksum: 3:14d5f0dd484bd102fdbd77db70853048)
CREATE TABLE `kinton`.`heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-25', '2.0.3', '3:14d5f0dd484bd102fdbd77db70853048', 214);

-- Changeset kinton2_0_ga.xml::1334584506393-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `kinton`.`hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 215);

-- Changeset kinton2_0_ga.xml::1334584506393-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `kinton`.`initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 216);

-- Changeset kinton2_0_ga.xml::1334584506393-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `kinton`.`ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 217);

-- Changeset kinton2_0_ga.xml::1334584506393-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `kinton`.`license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 218);

-- Changeset kinton2_0_ga.xml::1334584506393-30::destevezg (generated)::(Checksum: 3:cba5489b99643adbaca75913c0f65003)
CREATE TABLE `kinton`.`log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-30', '2.0.3', '3:cba5489b99643adbaca75913c0f65003', 219);

-- Changeset kinton2_0_ga.xml::1334584506393-31::destevezg (generated)::(Checksum: 3:610191e4c6c085272041ab93b7a4bd88)
CREATE TABLE `kinton`.`metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-31', '2.0.3', '3:610191e4c6c085272041ab93b7a4bd88', 220);

-- Changeset kinton2_0_ga.xml::1334584506393-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `kinton`.`network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 221);

-- Changeset kinton2_0_ga.xml::1334584506393-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `kinton`.`network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 222);

-- Changeset kinton2_0_ga.xml::1334584506393-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `kinton`.`node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 223);

-- Changeset kinton2_0_ga.xml::1334584506393-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `kinton`.`node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 224);

-- Changeset kinton2_0_ga.xml::1334584506393-36::destevezg (generated)::(Checksum: 3:5cecdb934194d6b6c4c52d5ddafab8a4)
CREATE TABLE `kinton`.`nodenetwork` (`idNode` INT UNSIGNED NOT NULL, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-36', '2.0.3', '3:5cecdb934194d6b6c4c52d5ddafab8a4', 225);

-- Changeset kinton2_0_ga.xml::1334584506393-37::destevezg (generated)::(Checksum: 3:98d35e5d1c7727e5a3a97a39ba856315)
CREATE TABLE `kinton`.`noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-37', '2.0.3', '3:98d35e5d1c7727e5a3a97a39ba856315', 226);

-- Changeset kinton2_0_ga.xml::1334584506393-38::destevezg (generated)::(Checksum: 3:9874aabd5a932cf4ac5e4c3c2a8518fb)
CREATE TABLE `kinton`.`nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-38', '2.0.3', '3:9874aabd5a932cf4ac5e4c3c2a8518fb', 227);

-- Changeset kinton2_0_ga.xml::1334584506393-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `kinton`.`nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 228);

-- Changeset kinton2_0_ga.xml::1334584506393-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 229);

-- Changeset kinton2_0_ga.xml::1334584506393-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `kinton`.`ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 230);

-- Changeset kinton2_0_ga.xml::1334584506393-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `kinton`.`ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 231);

-- Changeset kinton2_0_ga.xml::1334584506393-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `kinton`.`ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 232);

-- Changeset kinton2_0_ga.xml::1334584506393-44::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `kinton`.`physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-44', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 233);

-- Changeset kinton2_0_ga.xml::1334584506393-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `kinton`.`pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 234);

-- Changeset kinton2_0_ga.xml::1334584506393-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `kinton`.`pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 235);

-- Changeset kinton2_0_ga.xml::1334584506393-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `kinton`.`pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 236);

-- Changeset kinton2_0_ga.xml::1334584506393-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `kinton`.`privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 237);

-- Changeset kinton2_0_ga.xml::1334584506393-49::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `kinton`.`rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-49', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 238);

-- Changeset kinton2_0_ga.xml::1334584506393-50::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `kinton`.`rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-50', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 239);

-- Changeset kinton2_0_ga.xml::1334584506393-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `kinton`.`rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 240);

-- Changeset kinton2_0_ga.xml::1334584506393-52::destevezg (generated)::(Checksum: 3:e007dec4c46888665dd0bc6d5b5fbfe9)
CREATE TABLE `kinton`.`register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-52', '2.0.3', '3:e007dec4c46888665dd0bc6d5b5fbfe9', 241);

-- Changeset kinton2_0_ga.xml::1334584506393-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `kinton`.`remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 242);

-- Changeset kinton2_0_ga.xml::1334584506393-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `kinton`.`repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 243);

-- Changeset kinton2_0_ga.xml::1334584506393-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `kinton`.`role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 244);

-- Changeset kinton2_0_ga.xml::1334584506393-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `kinton`.`role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 245);

-- Changeset kinton2_0_ga.xml::1334584506393-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `kinton`.`roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 246);

-- Changeset kinton2_0_ga.xml::1334584506393-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `kinton`.`session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 247);

-- Changeset kinton2_0_ga.xml::1334584506393-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `kinton`.`storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 248);

-- Changeset kinton2_0_ga.xml::1334584506393-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `kinton`.`storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 249);

-- Changeset kinton2_0_ga.xml::1334584506393-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `kinton`.`system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 250);

-- Changeset kinton2_0_ga.xml::1334584506393-62::destevezg (generated)::(Checksum: 3:fd64da3920543e4ceaf993a73f88d28e)
CREATE TABLE `kinton`.`tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-62', '2.0.3', '3:fd64da3920543e4ceaf993a73f88d28e', 251);

-- Changeset kinton2_0_ga.xml::1334584506393-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `kinton`.`tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 252);

-- Changeset kinton2_0_ga.xml::1334584506393-64::destevezg (generated)::(Checksum: 3:1b0a3cb74ec9cb7c8117dd68a60414b3)
CREATE TABLE `kinton`.`ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-64', '2.0.3', '3:1b0a3cb74ec9cb7c8117dd68a60414b3', 253);

-- Changeset kinton2_0_ga.xml::1334584506393-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `kinton`.`user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 254);

-- Changeset kinton2_0_ga.xml::1334584506393-66::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `kinton`.`vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-66', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 255);

-- Changeset kinton2_0_ga.xml::1334584506393-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `kinton`.`vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 256);

-- Changeset kinton2_0_ga.xml::1334584506393-68::destevezg (generated)::(Checksum: 3:aecbcce0078b6d04274190ba65cfca54)
CREATE TABLE `kinton`.`vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-68', '2.0.3', '3:aecbcce0078b6d04274190ba65cfca54', 257);

-- Changeset kinton2_0_ga.xml::1334584506393-69::destevezg (generated)::(Checksum: 3:030a2622524d2284c305f928bb82368b)
CREATE TABLE `kinton`.`virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-69', '2.0.3', '3:030a2622524d2284c305f928bb82368b', 258);

-- Changeset kinton2_0_ga.xml::1334584506393-70::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `kinton`.`virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-70', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 259);

-- Changeset kinton2_0_ga.xml::1334584506393-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `kinton`.`virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 260);

-- Changeset kinton2_0_ga.xml::1334584506393-72::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `kinton`.`virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-72', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 261);

-- Changeset kinton2_0_ga.xml::1334584506393-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `kinton`.`virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 262);

-- Changeset kinton2_0_ga.xml::1334584506393-74::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `kinton`.`virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-74', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 263);

-- Changeset kinton2_0_ga.xml::1334584506393-75::destevezg (generated)::(Checksum: 3:a7be54650882a268059c959a6a5ff8bd)
CREATE TABLE `kinton`.`virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-75', '2.0.3', '3:a7be54650882a268059c959a6a5ff8bd', 264);

-- Changeset kinton2_0_ga.xml::1334584506393-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `kinton`.`vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 265);

-- Changeset kinton2_0_ga.xml::1334584506393-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `kinton`.`vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 266);

-- Changeset kinton2_0_ga.xml::1334584506393-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `kinton`.`vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 267);

-- Changeset kinton2_0_ga.xml::1334584506393-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `kinton`.`volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 268);

-- Changeset kinton2_0_ga.xml::1334584506393-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `kinton`.`workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 269);

-- Changeset kinton2_0_ga.xml::1334584506393-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `kinton`.`workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 270);

-- Changeset kinton2_0_ga.xml::1334584506393-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `kinton`.`workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 271);

-- Changeset kinton2_0_ga.xml::1334584506393-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `kinton`.`datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 272);

-- Changeset kinton2_0_ga.xml::1334584506393-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 273);

-- Changeset kinton2_0_ga.xml::1334584506393-85::destevezg (generated)::(Checksum: 3:2dd4badadcd15f6378a42b518d5aab69)
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`, `idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-85', '2.0.3', '3:2dd4badadcd15f6378a42b518d5aab69', 274);

-- Changeset kinton2_0_ga.xml::1334584506393-86::destevezg (generated)::(Checksum: 3:39db06adeb41d3a986d04834d8609781)
ALTER TABLE `kinton`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-86', '2.0.3', '3:39db06adeb41d3a986d04834d8609781', 275);

-- Changeset kinton2_0_ga.xml::1334584506393-87::destevezg (generated)::(Checksum: 3:ef59cbaeca0e42a4ec1583e0a2c37306)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-87', '2.0.3', '3:ef59cbaeca0e42a4ec1583e0a2c37306', 276);

-- Changeset kinton2_0_ga.xml::1334584506393-88::destevezg (generated)::(Checksum: 3:12b2b3f5e6fdee97aa1af071c3ca3129)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-88', '2.0.3', '3:12b2b3f5e6fdee97aa1af071c3ca3129', 277);

-- Changeset kinton2_0_ga.xml::1334584506393-89::destevezg (generated)::(Checksum: 3:aab159bccf255ef411d6f652295aac91)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-89', '2.0.3', '3:aab159bccf255ef411d6f652295aac91', 278);

-- Changeset kinton2_0_ga.xml::1334584506393-90::destevezg (generated)::(Checksum: 3:2c2a3886ab85ac15a24d5b86278cee13)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-90', '2.0.3', '3:2c2a3886ab85ac15a24d5b86278cee13', 279);

-- Changeset kinton2_0_ga.xml::1334584506393-91::destevezg (generated)::(Checksum: 3:7babbcfac31aa94742a0b7c852cbb75c)
ALTER TABLE `kinton`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-91', '2.0.3', '3:7babbcfac31aa94742a0b7c852cbb75c', 280);

-- Changeset kinton2_0_ga.xml::1334584506393-92::destevezg (generated)::(Checksum: 3:e917f98533bb9aef158246f2b9ac3806)
ALTER TABLE `kinton`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-92', '2.0.3', '3:e917f98533bb9aef158246f2b9ac3806', 281);

-- Changeset kinton2_0_ga.xml::1334584506393-93::destevezg (generated)::(Checksum: 3:380b349c2867c97f3069d1ddea7af2dc)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-93', '2.0.3', '3:380b349c2867c97f3069d1ddea7af2dc', 282);

-- Changeset kinton2_0_ga.xml::1334584506393-94::destevezg (generated)::(Checksum: 3:6f74be1ae0f5ca600be744dc575c6b55)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-94', '2.0.3', '3:6f74be1ae0f5ca600be744dc575c6b55', 283);

-- Changeset kinton2_0_ga.xml::1334584506393-95::destevezg (generated)::(Checksum: 3:7cac3426929736d26932e589efcd2dba)
ALTER TABLE `kinton`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-95', '2.0.3', '3:7cac3426929736d26932e589efcd2dba', 284);

-- Changeset kinton2_0_ga.xml::1334584506393-96::destevezg (generated)::(Checksum: 3:8743ae41839e4a8c6e13b9b27c7c5100)
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-96', '2.0.3', '3:8743ae41839e4a8c6e13b9b27c7c5100', 285);

-- Changeset kinton2_0_ga.xml::1334584506393-97::destevezg (generated)::(Checksum: 3:39f1295773e78d4bfc80735d014153c6)
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-97', '2.0.3', '3:39f1295773e78d4bfc80735d014153c6', 286);

-- Changeset kinton2_0_ga.xml::1334584506393-98::destevezg (generated)::(Checksum: 3:b388d5c13eab7fc4ec7fcf6d82d2517c)
ALTER TABLE `kinton`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-98', '2.0.3', '3:b388d5c13eab7fc4ec7fcf6d82d2517c', 287);

-- Changeset kinton2_0_ga.xml::1334584506393-99::destevezg (generated)::(Checksum: 3:3e8be0e2f2e71febf08072f5abb2337b)
ALTER TABLE `kinton`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-99', '2.0.3', '3:3e8be0e2f2e71febf08072f5abb2337b', 288);

-- Changeset kinton2_0_ga.xml::1334584506393-100::destevezg (generated)::(Checksum: 3:9c85972815ba8590587f3e2a7baf8d2e)
ALTER TABLE `kinton`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-100', '2.0.3', '3:9c85972815ba8590587f3e2a7baf8d2e', 289);

-- Changeset kinton2_0_ga.xml::1334584506393-101::destevezg (generated)::(Checksum: 3:e45f0e33e210d975f95aa06f5a472a31)
ALTER TABLE `kinton`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-101', '2.0.3', '3:e45f0e33e210d975f95aa06f5a472a31', 290);

-- Changeset kinton2_0_ga.xml::1334584506393-102::destevezg (generated)::(Checksum: 3:685adf52299cb301be40ce79ea068f09)
ALTER TABLE `kinton`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-102', '2.0.3', '3:685adf52299cb301be40ce79ea068f09', 291);

-- Changeset kinton2_0_ga.xml::1334584506393-103::destevezg (generated)::(Checksum: 3:54e0036e5c4653ab7a70eaa8b7adc969)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-103', '2.0.3', '3:54e0036e5c4653ab7a70eaa8b7adc969', 292);

-- Changeset kinton2_0_ga.xml::1334584506393-104::destevezg (generated)::(Checksum: 3:c75595ecaf1f61870fe3be4ee1607a58)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-104', '2.0.3', '3:c75595ecaf1f61870fe3be4ee1607a58', 293);

-- Changeset kinton2_0_ga.xml::1334584506393-105::destevezg (generated)::(Checksum: 3:d0e422554cd4e0db8c124dcdcdc3e861)
ALTER TABLE `kinton`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-105', '2.0.3', '3:d0e422554cd4e0db8c124dcdcdc3e861', 294);

-- Changeset kinton2_0_ga.xml::1334584506393-106::destevezg (generated)::(Checksum: 3:fbefc45b254ad3dc7c2e08d64deb06e3)
ALTER TABLE `kinton`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-106', '2.0.3', '3:fbefc45b254ad3dc7c2e08d64deb06e3', 295);

-- Changeset kinton2_0_ga.xml::1334584506393-107::destevezg (generated)::(Checksum: 3:56db749940a3b0de035482dce9f42af3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-107', '2.0.3', '3:56db749940a3b0de035482dce9f42af3', 296);

-- Changeset kinton2_0_ga.xml::1334584506393-108::destevezg (generated)::(Checksum: 3:dee0fe179f63a7fff9a6a8e7459ef124)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-108', '2.0.3', '3:dee0fe179f63a7fff9a6a8e7459ef124', 297);

-- Changeset kinton2_0_ga.xml::1334584506393-109::destevezg (generated)::(Checksum: 3:db0c334d194b39f67e8541f4a4c8b31a)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-109', '2.0.3', '3:db0c334d194b39f67e8541f4a4c8b31a', 298);

-- Changeset kinton2_0_ga.xml::1334584506393-110::destevezg (generated)::(Checksum: 3:7700a7d110854e172f2f3252b1567293)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-110', '2.0.3', '3:7700a7d110854e172f2f3252b1567293', 299);

-- Changeset kinton2_0_ga.xml::1334584506393-111::destevezg (generated)::(Checksum: 3:350df72b50bbdd1974350647a819ba36)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-111', '2.0.3', '3:350df72b50bbdd1974350647a819ba36', 300);

-- Changeset kinton2_0_ga.xml::1334584506393-112::destevezg (generated)::(Checksum: 3:bc63c183d18becdad57fdb22ca2279b3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-112', '2.0.3', '3:bc63c183d18becdad57fdb22ca2279b3', 301);

-- Changeset kinton2_0_ga.xml::1334584506393-113::destevezg (generated)::(Checksum: 3:570a0810c943c0ba338369b35c4facc3)
ALTER TABLE `kinton`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-113', '2.0.3', '3:570a0810c943c0ba338369b35c4facc3', 302);

-- Changeset kinton2_0_ga.xml::1334584506393-114::destevezg (generated)::(Checksum: 3:36d32fb242d453bc21a77ae64ee5c23c)
ALTER TABLE `kinton`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-114', '2.0.3', '3:36d32fb242d453bc21a77ae64ee5c23c', 303);

-- Changeset kinton2_0_ga.xml::1334584506393-115::destevezg (generated)::(Checksum: 3:a139f4550368879e9dc8127cf2208b32)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-115', '2.0.3', '3:a139f4550368879e9dc8127cf2208b32', 304);

-- Changeset kinton2_0_ga.xml::1334584506393-116::destevezg (generated)::(Checksum: 3:14d64e301e24922cdccc4a2e745d788d)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-116', '2.0.3', '3:14d64e301e24922cdccc4a2e745d788d', 305);

-- Changeset kinton2_0_ga.xml::1334584506393-117::destevezg (generated)::(Checksum: 3:e8ceada3c162ec371d3e31171195c0b2)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-117', '2.0.3', '3:e8ceada3c162ec371d3e31171195c0b2', 306);

-- Changeset kinton2_0_ga.xml::1334584506393-118::destevezg (generated)::(Checksum: 3:f7d73df5dad5123e4901e04db283185e)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-118', '2.0.3', '3:f7d73df5dad5123e4901e04db283185e', 307);

-- Changeset kinton2_0_ga.xml::1334584506393-119::destevezg (generated)::(Checksum: 3:4cf48f9241ea2f379f0c8acb839d6818)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-119', '2.0.3', '3:4cf48f9241ea2f379f0c8acb839d6818', 308);

-- Changeset kinton2_0_ga.xml::1334584506393-120::destevezg (generated)::(Checksum: 3:7f80cb03ad6bfefe6034ca2a75988ee3)
ALTER TABLE `kinton`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-120', '2.0.3', '3:7f80cb03ad6bfefe6034ca2a75988ee3', 309);

-- Changeset kinton2_0_ga.xml::1334584506393-121::destevezg (generated)::(Checksum: 3:314f329efbdbe1ceffd2b8335ac24754)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-121', '2.0.3', '3:314f329efbdbe1ceffd2b8335ac24754', 310);

-- Changeset kinton2_0_ga.xml::1334584506393-122::destevezg (generated)::(Checksum: 3:0b8d008edf4729acede17f0436c857b6)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-122', '2.0.3', '3:0b8d008edf4729acede17f0436c857b6', 311);

-- Changeset kinton2_0_ga.xml::1334584506393-123::destevezg (generated)::(Checksum: 3:40cfac0dcf4c56d309494dcec042d513)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-123', '2.0.3', '3:40cfac0dcf4c56d309494dcec042d513', 312);

-- Changeset kinton2_0_ga.xml::1334584506393-124::destevezg (generated)::(Checksum: 3:590901f24718ac0ff77f3de502c8bf3f)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-124', '2.0.3', '3:590901f24718ac0ff77f3de502c8bf3f', 313);

-- Changeset kinton2_0_ga.xml::1334584506393-125::destevezg (generated)::(Checksum: 3:4b4676b5d7cb3f195237d0a5ea3563c1)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-125', '2.0.3', '3:4b4676b5d7cb3f195237d0a5ea3563c1', 314);

-- Changeset kinton2_0_ga.xml::1334584506393-126::destevezg (generated)::(Checksum: 3:a316da2bf6cfa6eab48b556edbcb1686)
ALTER TABLE `kinton`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-126', '2.0.3', '3:a316da2bf6cfa6eab48b556edbcb1686', 315);

-- Changeset kinton2_0_ga.xml::1334584506393-127::destevezg (generated)::(Checksum: 3:c75b594b9fa56384d12679e3f3f39844)
ALTER TABLE `kinton`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-127', '2.0.3', '3:c75b594b9fa56384d12679e3f3f39844', 316);

-- Changeset kinton2_0_ga.xml::1334584506393-128::destevezg (generated)::(Checksum: 3:6c2f073057a45a69c1b7db5f4ee07de1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-128', '2.0.3', '3:6c2f073057a45a69c1b7db5f4ee07de1', 317);

-- Changeset kinton2_0_ga.xml::1334584506393-129::destevezg (generated)::(Checksum: 3:5ed1e047f733146bb1bb75cfbaa63f8e)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-129', '2.0.3', '3:5ed1e047f733146bb1bb75cfbaa63f8e', 318);

-- Changeset kinton2_0_ga.xml::1334584506393-130::destevezg (generated)::(Checksum: 3:15014a2695966373e7a6cae113893ff1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-130', '2.0.3', '3:15014a2695966373e7a6cae113893ff1', 319);

-- Changeset kinton2_0_ga.xml::1334584506393-131::destevezg (generated)::(Checksum: 3:f4ba13ebaac92029c85db4adfd4bb524)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-131', '2.0.3', '3:f4ba13ebaac92029c85db4adfd4bb524', 320);

-- Changeset kinton2_0_ga.xml::1334584506393-132::destevezg (generated)::(Checksum: 3:99fb777debdd79e43a64df61c8aab9f1)
ALTER TABLE `kinton`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-132', '2.0.3', '3:99fb777debdd79e43a64df61c8aab9f1', 321);

-- Changeset kinton2_0_ga.xml::1334584506393-133::destevezg (generated)::(Checksum: 3:381a734392ef762c6e4e727db64fdcdc)
ALTER TABLE `kinton`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-133', '2.0.3', '3:381a734392ef762c6e4e727db64fdcdc', 322);

-- Changeset kinton2_0_ga.xml::1334584506393-134::destevezg (generated)::(Checksum: 3:0afc6b5a509fa965da8109ecf2444522)
ALTER TABLE `kinton`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-134', '2.0.3', '3:0afc6b5a509fa965da8109ecf2444522', 323);

-- Changeset kinton2_0_ga.xml::1334584506393-135::destevezg (generated)::(Checksum: 3:6e1ac40f00f986ff6827ddffddc4417b)
ALTER TABLE `kinton`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-135', '2.0.3', '3:6e1ac40f00f986ff6827ddffddc4417b', 324);

-- Changeset kinton2_0_ga.xml::1334584506393-136::destevezg (generated)::(Checksum: 3:0e3df47ebc27a0e2d3d449f673c3436e)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-136', '2.0.3', '3:0e3df47ebc27a0e2d3d449f673c3436e', 325);

-- Changeset kinton2_0_ga.xml::1334584506393-137::destevezg (generated)::(Checksum: 3:b7d29b45d463a86fe85165ccd981b2a4)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-137', '2.0.3', '3:b7d29b45d463a86fe85165ccd981b2a4', 326);

-- Changeset kinton2_0_ga.xml::1334584506393-138::destevezg (generated)::(Checksum: 3:0a3a0dce75328a168956b34f2a166124)
ALTER TABLE `kinton`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-138', '2.0.3', '3:0a3a0dce75328a168956b34f2a166124', 327);

-- Changeset kinton2_0_ga.xml::1334584506393-139::destevezg (generated)::(Checksum: 3:a9f68a95692fd4cb61d1ab7f54a6add0)
ALTER TABLE `kinton`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-139', '2.0.3', '3:a9f68a95692fd4cb61d1ab7f54a6add0', 328);

-- Changeset kinton2_0_ga.xml::1334584506393-140::destevezg (generated)::(Checksum: 3:51bc92d1f8458a6758f84d5e40c6f88d)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-140', '2.0.3', '3:51bc92d1f8458a6758f84d5e40c6f88d', 329);

-- Changeset kinton2_0_ga.xml::1334584506393-141::destevezg (generated)::(Checksum: 3:732046a805d961eb44971fd636d52594)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-141', '2.0.3', '3:732046a805d961eb44971fd636d52594', 330);

-- Changeset kinton2_0_ga.xml::1334584506393-142::destevezg (generated)::(Checksum: 3:31e41feafb066c2be3b1cc2857f49208)
ALTER TABLE `kinton`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-142', '2.0.3', '3:31e41feafb066c2be3b1cc2857f49208', 331);

-- Changeset kinton2_0_ga.xml::1334584506393-143::destevezg (generated)::(Checksum: 3:466a1d498f0c2740539a49b128d9b6de)
ALTER TABLE `kinton`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-143', '2.0.3', '3:466a1d498f0c2740539a49b128d9b6de', 332);

-- Changeset kinton2_0_ga.xml::1334584506393-144::destevezg (generated)::(Checksum: 3:2c7e302fae12c8e84f18f3dba9f5c40c)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-144', '2.0.3', '3:2c7e302fae12c8e84f18f3dba9f5c40c', 333);

-- Changeset kinton2_0_ga.xml::1334584506393-145::destevezg (generated)::(Checksum: 3:b632cd05a5d8ab67cfad62318a6feacd)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-145', '2.0.3', '3:b632cd05a5d8ab67cfad62318a6feacd', 334);

-- Changeset kinton2_0_ga.xml::1334584506393-146::destevezg (generated)::(Checksum: 3:1361f06e4430e3572388dc130cf3f6ae)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-146', '2.0.3', '3:1361f06e4430e3572388dc130cf3f6ae', 335);

-- Changeset kinton2_0_ga.xml::1334584506393-147::destevezg (generated)::(Checksum: 3:fa81f7866672e0064abe59ce544d16ee)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-147', '2.0.3', '3:fa81f7866672e0064abe59ce544d16ee', 336);

-- Changeset kinton2_0_ga.xml::1334584506393-148::destevezg (generated)::(Checksum: 3:21d1330e830eda559d49619da27d4a2d)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-148', '2.0.3', '3:21d1330e830eda559d49619da27d4a2d', 337);

-- Changeset kinton2_0_ga.xml::1334584506393-149::destevezg (generated)::(Checksum: 3:6d20670c1bdea037fa86029d32e95c4e)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-149', '2.0.3', '3:6d20670c1bdea037fa86029d32e95c4e', 338);

-- Changeset kinton2_0_ga.xml::1334584506393-150::destevezg (generated)::(Checksum: 3:f93371517d74a4eab3924ee099b26c53)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-150', '2.0.3', '3:f93371517d74a4eab3924ee099b26c53', 339);

-- Changeset kinton2_0_ga.xml::1334584506393-151::destevezg (generated)::(Checksum: 3:206402c502be46899203f3badd9d8ec7)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-151', '2.0.3', '3:206402c502be46899203f3badd9d8ec7', 340);

-- Changeset kinton2_0_ga.xml::1334584506393-152::destevezg (generated)::(Checksum: 3:4b1fa941844e12bc85b4a6f54dca3194)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-152', '2.0.3', '3:4b1fa941844e12bc85b4a6f54dca3194', 341);

-- Changeset kinton2_0_ga.xml::1334584506393-153::destevezg (generated)::(Checksum: 3:72511dad2d82e148a62eadbe7350381a)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-153', '2.0.3', '3:72511dad2d82e148a62eadbe7350381a', 342);

-- Changeset kinton2_0_ga.xml::1334584506393-154::destevezg (generated)::(Checksum: 3:913ff3701711d54eee00d1fb8b58389d)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-154', '2.0.3', '3:913ff3701711d54eee00d1fb8b58389d', 343);

-- Changeset kinton2_0_ga.xml::1334584506393-155::destevezg (generated)::(Checksum: 3:082da0be85a69a1ebfc2a09ae9ec94e4)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-155', '2.0.3', '3:082da0be85a69a1ebfc2a09ae9ec94e4', 344);

-- Changeset kinton2_0_ga.xml::1334584506393-156::destevezg (generated)::(Checksum: 3:17e6766e834c47a60ded2846e28a0374)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-156', '2.0.3', '3:17e6766e834c47a60ded2846e28a0374', 345);

-- Changeset kinton2_0_ga.xml::1334584506393-157::destevezg (generated)::(Checksum: 3:3887fe4ffa2434ffbe9fdc910afa8538)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-157', '2.0.3', '3:3887fe4ffa2434ffbe9fdc910afa8538', 346);

-- Changeset kinton2_0_ga.xml::1334584506393-158::destevezg (generated)::(Checksum: 3:4f435a6168a0a18bb1d0714d21361b71)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-158', '2.0.3', '3:4f435a6168a0a18bb1d0714d21361b71', 347);

-- Changeset kinton2_0_ga.xml::1334584506393-159::destevezg (generated)::(Checksum: 3:1af426b7fdcaf93f1b45d9d5e8aa1bdc)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-159', '2.0.3', '3:1af426b7fdcaf93f1b45d9d5e8aa1bdc', 348);

-- Changeset kinton2_0_ga.xml::1334584506393-160::destevezg (generated)::(Checksum: 3:f7c4c533470ece45570602545702a16a)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-160', '2.0.3', '3:f7c4c533470ece45570602545702a16a', 349);

-- Changeset kinton2_0_ga.xml::1334584506393-161::destevezg (generated)::(Checksum: 3:c93821360ad0b67578d33e4371fca936)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-161', '2.0.3', '3:c93821360ad0b67578d33e4371fca936', 350);

-- Changeset kinton2_0_ga.xml::1334584506393-162::destevezg (generated)::(Checksum: 3:d44c1e70581845fad25b877d07c96182)
ALTER TABLE `kinton`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-162', '2.0.3', '3:d44c1e70581845fad25b877d07c96182', 351);

-- Changeset kinton2_0_ga.xml::1334584506393-163::destevezg (generated)::(Checksum: 3:dd8fdabb1b5568d9fbbe0342574633c4)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-163', '2.0.3', '3:dd8fdabb1b5568d9fbbe0342574633c4', 352);

-- Changeset kinton2_0_ga.xml::1334584506393-164::destevezg (generated)::(Checksum: 3:818367047e672790ff3ac9c3d13cec5e)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-164', '2.0.3', '3:818367047e672790ff3ac9c3d13cec5e', 353);

-- Changeset kinton2_0_ga.xml::1334584506393-165::destevezg (generated)::(Checksum: 3:9424adc44a74d3737f24c3d5b5d812fe)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-165', '2.0.3', '3:9424adc44a74d3737f24c3d5b5d812fe', 354);

-- Changeset kinton2_0_ga.xml::1334584506393-166::destevezg (generated)::(Checksum: 3:46215ad45eb3e72d8ee65ee7be941e1a)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-166', '2.0.3', '3:46215ad45eb3e72d8ee65ee7be941e1a', 355);

-- Changeset kinton2_0_ga.xml::1334584506393-167::destevezg (generated)::(Checksum: 3:d09aba94d1f59c8262f5399abec69cc5)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-167', '2.0.3', '3:d09aba94d1f59c8262f5399abec69cc5', 356);

-- Changeset kinton2_0_ga.xml::1334584506393-168::destevezg (generated)::(Checksum: 3:e717cc8fc89cb02d56bd262979378060)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-168', '2.0.3', '3:e717cc8fc89cb02d56bd262979378060', 357);

-- Changeset kinton2_0_ga.xml::1334584506393-169::destevezg (generated)::(Checksum: 3:c2d45eafaae6aa722440adfe212203c3)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-169', '2.0.3', '3:c2d45eafaae6aa722440adfe212203c3', 358);

-- Changeset kinton2_0_ga.xml::1334584506393-170::destevezg (generated)::(Checksum: 3:6ebfd010b6b125ab61846d74710cdb9c)
ALTER TABLE `kinton`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-170', '2.0.3', '3:6ebfd010b6b125ab61846d74710cdb9c', 359);

-- Changeset kinton2_0_ga.xml::1334584506393-171::destevezg (generated)::(Checksum: 3:6b5855cbae91f21f0eecc23d7d31e7d6)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-171', '2.0.3', '3:6b5855cbae91f21f0eecc23d7d31e7d6', 360);

-- Changeset kinton2_0_ga.xml::1334584506393-172::destevezg (generated)::(Checksum: 3:8a5267a1f2d46f48e685e311a9a2bb38)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-172', '2.0.3', '3:8a5267a1f2d46f48e685e311a9a2bb38', 361);

-- Changeset kinton2_0_ga.xml::1334584506393-173::destevezg (generated)::(Checksum: 3:ced9008c983144fe8e36c11ee1d24a81)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-173', '2.0.3', '3:ced9008c983144fe8e36c11ee1d24a81', 362);

-- Changeset kinton2_0_ga.xml::1334584506393-174::destevezg (generated)::(Checksum: 3:fa97c2e5bfec4084f586a823469f3b1f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-174', '2.0.3', '3:fa97c2e5bfec4084f586a823469f3b1f', 363);

-- Changeset kinton2_0_ga.xml::1334584506393-175::destevezg (generated)::(Checksum: 3:f57948dcc96f12c0699213820b6b756f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-175', '2.0.3', '3:f57948dcc96f12c0699213820b6b756f', 364);

-- Changeset kinton2_0_ga.xml::1334584506393-176::destevezg (generated)::(Checksum: 3:e1ca982714c144ed2d5ac20561bc6657)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-176', '2.0.3', '3:e1ca982714c144ed2d5ac20561bc6657', 365);

-- Changeset kinton2_0_ga.xml::1334584506393-177::destevezg (generated)::(Checksum: 3:746c3e281a036d6ada1b5e3fd95a4696)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-177', '2.0.3', '3:746c3e281a036d6ada1b5e3fd95a4696', 366);

-- Changeset kinton2_0_ga.xml::1334584506393-178::destevezg (generated)::(Checksum: 3:b3376ebce0a03581c19a96f0c56bbd66)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-178', '2.0.3', '3:b3376ebce0a03581c19a96f0c56bbd66', 367);

-- Changeset kinton2_0_ga.xml::1334584506393-179::destevezg (generated)::(Checksum: 3:c16c6d833472e00f707818c0d317b44b)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-179', '2.0.3', '3:c16c6d833472e00f707818c0d317b44b', 368);

-- Changeset kinton2_0_ga.xml::1334584506393-180::destevezg (generated)::(Checksum: 3:420c8225285f8d8e374d50a1ed9e237e)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-180', '2.0.3', '3:420c8225285f8d8e374d50a1ed9e237e', 369);

-- Changeset kinton2_0_ga.xml::1334584506393-181::destevezg (generated)::(Checksum: 3:8f456a92ce7ba6f2b3625311cb2a47cf)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-181', '2.0.3', '3:8f456a92ce7ba6f2b3625311cb2a47cf', 370);

-- Changeset kinton2_0_ga.xml::1334584506393-182::destevezg (generated)::(Checksum: 3:c5ab00bc6a57c9809eb1be93120180ba)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-182', '2.0.3', '3:c5ab00bc6a57c9809eb1be93120180ba', 371);

-- Changeset kinton2_0_ga.xml::1334584506393-183::destevezg (generated)::(Checksum: 3:1035cb414581bebcdacdd2a161d19a41)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-183', '2.0.3', '3:1035cb414581bebcdacdd2a161d19a41', 372);

-- Changeset kinton2_0_ga.xml::1334584506393-184::destevezg (generated)::(Checksum: 3:f7d0b7bcff44df8f076be460f1172674)
ALTER TABLE `kinton`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-184', '2.0.3', '3:f7d0b7bcff44df8f076be460f1172674', 373);

-- Changeset kinton2_0_ga.xml::1334584506393-185::destevezg (generated)::(Checksum: 3:2724c06259dee3fa38ec1a3bd14d32b5)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-185', '2.0.3', '3:2724c06259dee3fa38ec1a3bd14d32b5', 374);

-- Changeset kinton2_0_ga.xml::1334584506393-186::destevezg (generated)::(Checksum: 3:c1c812e559c885292b18196d35ef708e)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-186', '2.0.3', '3:c1c812e559c885292b18196d35ef708e', 375);

-- Changeset kinton2_0_ga.xml::1334584506393-187::destevezg (generated)::(Checksum: 3:5d48f5fe3bb5c924a4e96180cc3d9790)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-187', '2.0.3', '3:5d48f5fe3bb5c924a4e96180cc3d9790', 376);

-- Changeset kinton2_0_ga.xml::1334584506393-188::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `kinton`.`category`(`name`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-188', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 377);

-- Changeset kinton2_0_ga.xml::1334584506393-189::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `kinton`.`user`(`user`, `authType`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334584506393-189', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 378);

-- Changeset kinton2_0_ga.xml::1334595170508-1::destevezg (generated)::(Checksum: 3:4cc953d671ea64d307e8d8ff11bd6220)
CREATE TABLE `kinton`.`alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-1', '2.0.3', '3:4cc953d671ea64d307e8d8ff11bd6220', 379);

-- Changeset kinton2_0_ga.xml::1334595170508-2::destevezg (generated)::(Checksum: 3:8e919ec45e59bcb22749fbbb8f8e7731)
CREATE TABLE `kinton`.`apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-2', '2.0.3', '3:8e919ec45e59bcb22749fbbb8f8e7731', 380);

-- Changeset kinton2_0_ga.xml::1334595170508-3::destevezg (generated)::(Checksum: 3:3a64a5ee5cd7e25bfab74647244666c9)
CREATE TABLE `kinton`.`auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-3', '2.0.3', '3:3a64a5ee5cd7e25bfab74647244666c9', 381);

-- Changeset kinton2_0_ga.xml::1334595170508-4::destevezg (generated)::(Checksum: 3:d5a57e91c407bb3e4286f207929d13ce)
CREATE TABLE `kinton`.`auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-4', '2.0.3', '3:d5a57e91c407bb3e4286f207929d13ce', 382);

-- Changeset kinton2_0_ga.xml::1334595170508-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `kinton`.`auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 383);

-- Changeset kinton2_0_ga.xml::1334595170508-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `kinton`.`category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 384);

-- Changeset kinton2_0_ga.xml::1334595170508-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `kinton`.`chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 385);

-- Changeset kinton2_0_ga.xml::1334595170508-8::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `kinton`.`cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-8', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 386);

-- Changeset kinton2_0_ga.xml::1334595170508-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `kinton`.`costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 387);

-- Changeset kinton2_0_ga.xml::1334595170508-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `kinton`.`costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 388);

-- Changeset kinton2_0_ga.xml::1334595170508-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `kinton`.`currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 389);

-- Changeset kinton2_0_ga.xml::1334595170508-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `kinton`.`datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 390);

-- Changeset kinton2_0_ga.xml::1334595170508-13::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `kinton`.`datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-13', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 391);

-- Changeset kinton2_0_ga.xml::1334595170508-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `kinton`.`datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 392);

-- Changeset kinton2_0_ga.xml::1334595170508-15::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `kinton`.`dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-15', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 393);

-- Changeset kinton2_0_ga.xml::1334595170508-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `kinton`.`dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 394);

-- Changeset kinton2_0_ga.xml::1334595170508-17::destevezg (generated)::(Checksum: 3:945b273b2813740dd085b21b2aa00bdb)
CREATE TABLE `kinton`.`disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-17', '2.0.3', '3:945b273b2813740dd085b21b2aa00bdb', 395);

-- Changeset kinton2_0_ga.xml::1334595170508-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `kinton`.`diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 396);

-- Changeset kinton2_0_ga.xml::1334595170508-19::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `kinton`.`enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-19', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 397);

-- Changeset kinton2_0_ga.xml::1334595170508-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `kinton`.`enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 398);

-- Changeset kinton2_0_ga.xml::1334595170508-21::destevezg (generated)::(Checksum: 3:c67606071cfc197cd0d312b346c48f46)
CREATE TABLE `kinton`.`enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-21', '2.0.3', '3:c67606071cfc197cd0d312b346c48f46', 399);

-- Changeset kinton2_0_ga.xml::1334595170508-22::destevezg (generated)::(Checksum: 3:501eb9f341a105a7a8c396cf25b447ce)
CREATE TABLE `kinton`.`enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-22', '2.0.3', '3:501eb9f341a105a7a8c396cf25b447ce', 400);

-- Changeset kinton2_0_ga.xml::1334595170508-23::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `kinton`.`enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-23', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 401);

-- Changeset kinton2_0_ga.xml::1334595170508-24::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `kinton`.`enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-24', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 402);

-- Changeset kinton2_0_ga.xml::1334595170508-25::destevezg (generated)::(Checksum: 3:14d5f0dd484bd102fdbd77db70853048)
CREATE TABLE `kinton`.`heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-25', '2.0.3', '3:14d5f0dd484bd102fdbd77db70853048', 403);

-- Changeset kinton2_0_ga.xml::1334595170508-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `kinton`.`hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 404);

-- Changeset kinton2_0_ga.xml::1334595170508-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `kinton`.`initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 405);

-- Changeset kinton2_0_ga.xml::1334595170508-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `kinton`.`ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 406);

-- Changeset kinton2_0_ga.xml::1334595170508-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `kinton`.`license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 407);

-- Changeset kinton2_0_ga.xml::1334595170508-30::destevezg (generated)::(Checksum: 3:cba5489b99643adbaca75913c0f65003)
CREATE TABLE `kinton`.`log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-30', '2.0.3', '3:cba5489b99643adbaca75913c0f65003', 408);

-- Changeset kinton2_0_ga.xml::1334595170508-31::destevezg (generated)::(Checksum: 3:610191e4c6c085272041ab93b7a4bd88)
CREATE TABLE `kinton`.`metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-31', '2.0.3', '3:610191e4c6c085272041ab93b7a4bd88', 409);

-- Changeset kinton2_0_ga.xml::1334595170508-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `kinton`.`network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 410);

-- Changeset kinton2_0_ga.xml::1334595170508-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `kinton`.`network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 411);

-- Changeset kinton2_0_ga.xml::1334595170508-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `kinton`.`node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 412);

-- Changeset kinton2_0_ga.xml::1334595170508-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `kinton`.`node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 413);

-- Changeset kinton2_0_ga.xml::1334595170508-36::destevezg (generated)::(Checksum: 3:5cecdb934194d6b6c4c52d5ddafab8a4)
CREATE TABLE `kinton`.`nodenetwork` (`idNode` INT UNSIGNED NOT NULL, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-36', '2.0.3', '3:5cecdb934194d6b6c4c52d5ddafab8a4', 414);

-- Changeset kinton2_0_ga.xml::1334595170508-37::destevezg (generated)::(Checksum: 3:98d35e5d1c7727e5a3a97a39ba856315)
CREATE TABLE `kinton`.`noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-37', '2.0.3', '3:98d35e5d1c7727e5a3a97a39ba856315', 415);

-- Changeset kinton2_0_ga.xml::1334595170508-38::destevezg (generated)::(Checksum: 3:9874aabd5a932cf4ac5e4c3c2a8518fb)
CREATE TABLE `kinton`.`nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-38', '2.0.3', '3:9874aabd5a932cf4ac5e4c3c2a8518fb', 416);

-- Changeset kinton2_0_ga.xml::1334595170508-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `kinton`.`nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 417);

-- Changeset kinton2_0_ga.xml::1334595170508-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 418);

-- Changeset kinton2_0_ga.xml::1334595170508-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `kinton`.`ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 419);

-- Changeset kinton2_0_ga.xml::1334595170508-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `kinton`.`ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 420);

-- Changeset kinton2_0_ga.xml::1334595170508-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `kinton`.`ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 421);

-- Changeset kinton2_0_ga.xml::1334595170508-44::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `kinton`.`physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-44', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 422);

-- Changeset kinton2_0_ga.xml::1334595170508-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `kinton`.`pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 423);

-- Changeset kinton2_0_ga.xml::1334595170508-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `kinton`.`pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 424);

-- Changeset kinton2_0_ga.xml::1334595170508-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `kinton`.`pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 425);

-- Changeset kinton2_0_ga.xml::1334595170508-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `kinton`.`privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 426);

-- Changeset kinton2_0_ga.xml::1334595170508-49::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `kinton`.`rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-49', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 427);

-- Changeset kinton2_0_ga.xml::1334595170508-50::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `kinton`.`rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-50', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 428);

-- Changeset kinton2_0_ga.xml::1334595170508-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `kinton`.`rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 429);

-- Changeset kinton2_0_ga.xml::1334595170508-52::destevezg (generated)::(Checksum: 3:e007dec4c46888665dd0bc6d5b5fbfe9)
CREATE TABLE `kinton`.`register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-52', '2.0.3', '3:e007dec4c46888665dd0bc6d5b5fbfe9', 430);

-- Changeset kinton2_0_ga.xml::1334595170508-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `kinton`.`remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 431);

-- Changeset kinton2_0_ga.xml::1334595170508-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `kinton`.`repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 432);

-- Changeset kinton2_0_ga.xml::1334595170508-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `kinton`.`role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 433);

-- Changeset kinton2_0_ga.xml::1334595170508-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `kinton`.`role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 434);

-- Changeset kinton2_0_ga.xml::1334595170508-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `kinton`.`roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 435);

-- Changeset kinton2_0_ga.xml::1334595170508-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `kinton`.`session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 436);

-- Changeset kinton2_0_ga.xml::1334595170508-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `kinton`.`storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 437);

-- Changeset kinton2_0_ga.xml::1334595170508-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `kinton`.`storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 438);

-- Changeset kinton2_0_ga.xml::1334595170508-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `kinton`.`system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 439);

-- Changeset kinton2_0_ga.xml::1334595170508-62::destevezg (generated)::(Checksum: 3:fd64da3920543e4ceaf993a73f88d28e)
CREATE TABLE `kinton`.`tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-62', '2.0.3', '3:fd64da3920543e4ceaf993a73f88d28e', 440);

-- Changeset kinton2_0_ga.xml::1334595170508-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `kinton`.`tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 441);

-- Changeset kinton2_0_ga.xml::1334595170508-64::destevezg (generated)::(Checksum: 3:1b0a3cb74ec9cb7c8117dd68a60414b3)
CREATE TABLE `kinton`.`ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-64', '2.0.3', '3:1b0a3cb74ec9cb7c8117dd68a60414b3', 442);

-- Changeset kinton2_0_ga.xml::1334595170508-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `kinton`.`user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 443);

-- Changeset kinton2_0_ga.xml::1334595170508-66::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `kinton`.`vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-66', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 444);

-- Changeset kinton2_0_ga.xml::1334595170508-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `kinton`.`vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 445);

-- Changeset kinton2_0_ga.xml::1334595170508-68::destevezg (generated)::(Checksum: 3:aecbcce0078b6d04274190ba65cfca54)
CREATE TABLE `kinton`.`vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-68', '2.0.3', '3:aecbcce0078b6d04274190ba65cfca54', 446);

-- Changeset kinton2_0_ga.xml::1334595170508-69::destevezg (generated)::(Checksum: 3:030a2622524d2284c305f928bb82368b)
CREATE TABLE `kinton`.`virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-69', '2.0.3', '3:030a2622524d2284c305f928bb82368b', 447);

-- Changeset kinton2_0_ga.xml::1334595170508-70::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `kinton`.`virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-70', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 448);

-- Changeset kinton2_0_ga.xml::1334595170508-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `kinton`.`virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 449);

-- Changeset kinton2_0_ga.xml::1334595170508-72::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `kinton`.`virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-72', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 450);

-- Changeset kinton2_0_ga.xml::1334595170508-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `kinton`.`virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 451);

-- Changeset kinton2_0_ga.xml::1334595170508-74::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `kinton`.`virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-74', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 452);

-- Changeset kinton2_0_ga.xml::1334595170508-75::destevezg (generated)::(Checksum: 3:a7be54650882a268059c959a6a5ff8bd)
CREATE TABLE `kinton`.`virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-75', '2.0.3', '3:a7be54650882a268059c959a6a5ff8bd', 453);

-- Changeset kinton2_0_ga.xml::1334595170508-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `kinton`.`vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 454);

-- Changeset kinton2_0_ga.xml::1334595170508-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `kinton`.`vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 455);

-- Changeset kinton2_0_ga.xml::1334595170508-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `kinton`.`vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 456);

-- Changeset kinton2_0_ga.xml::1334595170508-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `kinton`.`volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 457);

-- Changeset kinton2_0_ga.xml::1334595170508-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `kinton`.`workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 458);

-- Changeset kinton2_0_ga.xml::1334595170508-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `kinton`.`workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 459);

-- Changeset kinton2_0_ga.xml::1334595170508-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `kinton`.`workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 460);

-- Changeset kinton2_0_ga.xml::1334595170508-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `kinton`.`datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 461);

-- Changeset kinton2_0_ga.xml::1334595170508-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 462);

-- Changeset kinton2_0_ga.xml::1334595170508-85::destevezg (generated)::(Checksum: 3:2dd4badadcd15f6378a42b518d5aab69)
ALTER TABLE `kinton`.`vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`, `idEnterprise`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-85', '2.0.3', '3:2dd4badadcd15f6378a42b518d5aab69', 463);

-- Changeset kinton2_0_ga.xml::1334595170508-86::destevezg (generated)::(Checksum: 3:39db06adeb41d3a986d04834d8609781)
ALTER TABLE `kinton`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-86', '2.0.3', '3:39db06adeb41d3a986d04834d8609781', 464);

-- Changeset kinton2_0_ga.xml::1334595170508-87::destevezg (generated)::(Checksum: 3:ef59cbaeca0e42a4ec1583e0a2c37306)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-87', '2.0.3', '3:ef59cbaeca0e42a4ec1583e0a2c37306', 465);

-- Changeset kinton2_0_ga.xml::1334595170508-88::destevezg (generated)::(Checksum: 3:12b2b3f5e6fdee97aa1af071c3ca3129)
ALTER TABLE `kinton`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-88', '2.0.3', '3:12b2b3f5e6fdee97aa1af071c3ca3129', 466);

-- Changeset kinton2_0_ga.xml::1334595170508-89::destevezg (generated)::(Checksum: 3:aab159bccf255ef411d6f652295aac91)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-89', '2.0.3', '3:aab159bccf255ef411d6f652295aac91', 467);

-- Changeset kinton2_0_ga.xml::1334595170508-90::destevezg (generated)::(Checksum: 3:2c2a3886ab85ac15a24d5b86278cee13)
ALTER TABLE `kinton`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-90', '2.0.3', '3:2c2a3886ab85ac15a24d5b86278cee13', 468);

-- Changeset kinton2_0_ga.xml::1334595170508-91::destevezg (generated)::(Checksum: 3:7babbcfac31aa94742a0b7c852cbb75c)
ALTER TABLE `kinton`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-91', '2.0.3', '3:7babbcfac31aa94742a0b7c852cbb75c', 469);

-- Changeset kinton2_0_ga.xml::1334595170508-92::destevezg (generated)::(Checksum: 3:e917f98533bb9aef158246f2b9ac3806)
ALTER TABLE `kinton`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-92', '2.0.3', '3:e917f98533bb9aef158246f2b9ac3806', 470);

-- Changeset kinton2_0_ga.xml::1334595170508-93::destevezg (generated)::(Checksum: 3:380b349c2867c97f3069d1ddea7af2dc)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-93', '2.0.3', '3:380b349c2867c97f3069d1ddea7af2dc', 471);

-- Changeset kinton2_0_ga.xml::1334595170508-94::destevezg (generated)::(Checksum: 3:6f74be1ae0f5ca600be744dc575c6b55)
ALTER TABLE `kinton`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-94', '2.0.3', '3:6f74be1ae0f5ca600be744dc575c6b55', 472);

-- Changeset kinton2_0_ga.xml::1334595170508-95::destevezg (generated)::(Checksum: 3:7cac3426929736d26932e589efcd2dba)
ALTER TABLE `kinton`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-95', '2.0.3', '3:7cac3426929736d26932e589efcd2dba', 473);

-- Changeset kinton2_0_ga.xml::1334595170508-96::destevezg (generated)::(Checksum: 3:8743ae41839e4a8c6e13b9b27c7c5100)
ALTER TABLE `kinton`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-96', '2.0.3', '3:8743ae41839e4a8c6e13b9b27c7c5100', 474);

-- Changeset kinton2_0_ga.xml::1334595170508-97::destevezg (generated)::(Checksum: 3:39f1295773e78d4bfc80735d014153c6)
ALTER TABLE `kinton`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-97', '2.0.3', '3:39f1295773e78d4bfc80735d014153c6', 475);

-- Changeset kinton2_0_ga.xml::1334595170508-98::destevezg (generated)::(Checksum: 3:b388d5c13eab7fc4ec7fcf6d82d2517c)
ALTER TABLE `kinton`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-98', '2.0.3', '3:b388d5c13eab7fc4ec7fcf6d82d2517c', 476);

-- Changeset kinton2_0_ga.xml::1334595170508-99::destevezg (generated)::(Checksum: 3:3e8be0e2f2e71febf08072f5abb2337b)
ALTER TABLE `kinton`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-99', '2.0.3', '3:3e8be0e2f2e71febf08072f5abb2337b', 477);

-- Changeset kinton2_0_ga.xml::1334595170508-100::destevezg (generated)::(Checksum: 3:9c85972815ba8590587f3e2a7baf8d2e)
ALTER TABLE `kinton`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-100', '2.0.3', '3:9c85972815ba8590587f3e2a7baf8d2e', 478);

-- Changeset kinton2_0_ga.xml::1334595170508-101::destevezg (generated)::(Checksum: 3:e45f0e33e210d975f95aa06f5a472a31)
ALTER TABLE `kinton`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-101', '2.0.3', '3:e45f0e33e210d975f95aa06f5a472a31', 479);

-- Changeset kinton2_0_ga.xml::1334595170508-102::destevezg (generated)::(Checksum: 3:685adf52299cb301be40ce79ea068f09)
ALTER TABLE `kinton`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-102', '2.0.3', '3:685adf52299cb301be40ce79ea068f09', 480);

-- Changeset kinton2_0_ga.xml::1334595170508-103::destevezg (generated)::(Checksum: 3:54e0036e5c4653ab7a70eaa8b7adc969)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-103', '2.0.3', '3:54e0036e5c4653ab7a70eaa8b7adc969', 481);

-- Changeset kinton2_0_ga.xml::1334595170508-104::destevezg (generated)::(Checksum: 3:c75595ecaf1f61870fe3be4ee1607a58)
ALTER TABLE `kinton`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-104', '2.0.3', '3:c75595ecaf1f61870fe3be4ee1607a58', 482);

-- Changeset kinton2_0_ga.xml::1334595170508-105::destevezg (generated)::(Checksum: 3:d0e422554cd4e0db8c124dcdcdc3e861)
ALTER TABLE `kinton`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-105', '2.0.3', '3:d0e422554cd4e0db8c124dcdcdc3e861', 483);

-- Changeset kinton2_0_ga.xml::1334595170508-106::destevezg (generated)::(Checksum: 3:fbefc45b254ad3dc7c2e08d64deb06e3)
ALTER TABLE `kinton`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-106', '2.0.3', '3:fbefc45b254ad3dc7c2e08d64deb06e3', 484);

-- Changeset kinton2_0_ga.xml::1334595170508-107::destevezg (generated)::(Checksum: 3:56db749940a3b0de035482dce9f42af3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-107', '2.0.3', '3:56db749940a3b0de035482dce9f42af3', 485);

-- Changeset kinton2_0_ga.xml::1334595170508-108::destevezg (generated)::(Checksum: 3:dee0fe179f63a7fff9a6a8e7459ef124)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-108', '2.0.3', '3:dee0fe179f63a7fff9a6a8e7459ef124', 486);

-- Changeset kinton2_0_ga.xml::1334595170508-109::destevezg (generated)::(Checksum: 3:db0c334d194b39f67e8541f4a4c8b31a)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-109', '2.0.3', '3:db0c334d194b39f67e8541f4a4c8b31a', 487);

-- Changeset kinton2_0_ga.xml::1334595170508-110::destevezg (generated)::(Checksum: 3:7700a7d110854e172f2f3252b1567293)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-110', '2.0.3', '3:7700a7d110854e172f2f3252b1567293', 488);

-- Changeset kinton2_0_ga.xml::1334595170508-111::destevezg (generated)::(Checksum: 3:350df72b50bbdd1974350647a819ba36)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-111', '2.0.3', '3:350df72b50bbdd1974350647a819ba36', 489);

-- Changeset kinton2_0_ga.xml::1334595170508-112::destevezg (generated)::(Checksum: 3:bc63c183d18becdad57fdb22ca2279b3)
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-112', '2.0.3', '3:bc63c183d18becdad57fdb22ca2279b3', 490);

-- Changeset kinton2_0_ga.xml::1334595170508-113::destevezg (generated)::(Checksum: 3:570a0810c943c0ba338369b35c4facc3)
ALTER TABLE `kinton`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-113', '2.0.3', '3:570a0810c943c0ba338369b35c4facc3', 491);

-- Changeset kinton2_0_ga.xml::1334595170508-114::destevezg (generated)::(Checksum: 3:36d32fb242d453bc21a77ae64ee5c23c)
ALTER TABLE `kinton`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-114', '2.0.3', '3:36d32fb242d453bc21a77ae64ee5c23c', 492);

-- Changeset kinton2_0_ga.xml::1334595170508-115::destevezg (generated)::(Checksum: 3:a139f4550368879e9dc8127cf2208b32)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-115', '2.0.3', '3:a139f4550368879e9dc8127cf2208b32', 493);

-- Changeset kinton2_0_ga.xml::1334595170508-116::destevezg (generated)::(Checksum: 3:14d64e301e24922cdccc4a2e745d788d)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-116', '2.0.3', '3:14d64e301e24922cdccc4a2e745d788d', 494);

-- Changeset kinton2_0_ga.xml::1334595170508-117::destevezg (generated)::(Checksum: 3:e8ceada3c162ec371d3e31171195c0b2)
ALTER TABLE `kinton`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-117', '2.0.3', '3:e8ceada3c162ec371d3e31171195c0b2', 495);

-- Changeset kinton2_0_ga.xml::1334595170508-118::destevezg (generated)::(Checksum: 3:f7d73df5dad5123e4901e04db283185e)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-118', '2.0.3', '3:f7d73df5dad5123e4901e04db283185e', 496);

-- Changeset kinton2_0_ga.xml::1334595170508-119::destevezg (generated)::(Checksum: 3:4cf48f9241ea2f379f0c8acb839d6818)
ALTER TABLE `kinton`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-119', '2.0.3', '3:4cf48f9241ea2f379f0c8acb839d6818', 497);

-- Changeset kinton2_0_ga.xml::1334595170508-120::destevezg (generated)::(Checksum: 3:7f80cb03ad6bfefe6034ca2a75988ee3)
ALTER TABLE `kinton`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-120', '2.0.3', '3:7f80cb03ad6bfefe6034ca2a75988ee3', 498);

-- Changeset kinton2_0_ga.xml::1334595170508-121::destevezg (generated)::(Checksum: 3:314f329efbdbe1ceffd2b8335ac24754)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-121', '2.0.3', '3:314f329efbdbe1ceffd2b8335ac24754', 499);

-- Changeset kinton2_0_ga.xml::1334595170508-122::destevezg (generated)::(Checksum: 3:0b8d008edf4729acede17f0436c857b6)
ALTER TABLE `kinton`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-122', '2.0.3', '3:0b8d008edf4729acede17f0436c857b6', 500);

-- Changeset kinton2_0_ga.xml::1334595170508-123::destevezg (generated)::(Checksum: 3:40cfac0dcf4c56d309494dcec042d513)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-123', '2.0.3', '3:40cfac0dcf4c56d309494dcec042d513', 501);

-- Changeset kinton2_0_ga.xml::1334595170508-124::destevezg (generated)::(Checksum: 3:590901f24718ac0ff77f3de502c8bf3f)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-124', '2.0.3', '3:590901f24718ac0ff77f3de502c8bf3f', 502);

-- Changeset kinton2_0_ga.xml::1334595170508-125::destevezg (generated)::(Checksum: 3:4b4676b5d7cb3f195237d0a5ea3563c1)
ALTER TABLE `kinton`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-125', '2.0.3', '3:4b4676b5d7cb3f195237d0a5ea3563c1', 503);

-- Changeset kinton2_0_ga.xml::1334595170508-126::destevezg (generated)::(Checksum: 3:a316da2bf6cfa6eab48b556edbcb1686)
ALTER TABLE `kinton`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-126', '2.0.3', '3:a316da2bf6cfa6eab48b556edbcb1686', 504);

-- Changeset kinton2_0_ga.xml::1334595170508-127::destevezg (generated)::(Checksum: 3:c75b594b9fa56384d12679e3f3f39844)
ALTER TABLE `kinton`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-127', '2.0.3', '3:c75b594b9fa56384d12679e3f3f39844', 505);

-- Changeset kinton2_0_ga.xml::1334595170508-128::destevezg (generated)::(Checksum: 3:6c2f073057a45a69c1b7db5f4ee07de1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-128', '2.0.3', '3:6c2f073057a45a69c1b7db5f4ee07de1', 506);

-- Changeset kinton2_0_ga.xml::1334595170508-129::destevezg (generated)::(Checksum: 3:5ed1e047f733146bb1bb75cfbaa63f8e)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-129', '2.0.3', '3:5ed1e047f733146bb1bb75cfbaa63f8e', 507);

-- Changeset kinton2_0_ga.xml::1334595170508-130::destevezg (generated)::(Checksum: 3:15014a2695966373e7a6cae113893ff1)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-130', '2.0.3', '3:15014a2695966373e7a6cae113893ff1', 508);

-- Changeset kinton2_0_ga.xml::1334595170508-131::destevezg (generated)::(Checksum: 3:f4ba13ebaac92029c85db4adfd4bb524)
ALTER TABLE `kinton`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-131', '2.0.3', '3:f4ba13ebaac92029c85db4adfd4bb524', 509);

-- Changeset kinton2_0_ga.xml::1334595170508-132::destevezg (generated)::(Checksum: 3:99fb777debdd79e43a64df61c8aab9f1)
ALTER TABLE `kinton`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-132', '2.0.3', '3:99fb777debdd79e43a64df61c8aab9f1', 510);

-- Changeset kinton2_0_ga.xml::1334595170508-133::destevezg (generated)::(Checksum: 3:381a734392ef762c6e4e727db64fdcdc)
ALTER TABLE `kinton`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-133', '2.0.3', '3:381a734392ef762c6e4e727db64fdcdc', 511);

-- Changeset kinton2_0_ga.xml::1334595170508-134::destevezg (generated)::(Checksum: 3:0afc6b5a509fa965da8109ecf2444522)
ALTER TABLE `kinton`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-134', '2.0.3', '3:0afc6b5a509fa965da8109ecf2444522', 512);

-- Changeset kinton2_0_ga.xml::1334595170508-135::destevezg (generated)::(Checksum: 3:6e1ac40f00f986ff6827ddffddc4417b)
ALTER TABLE `kinton`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-135', '2.0.3', '3:6e1ac40f00f986ff6827ddffddc4417b', 513);

-- Changeset kinton2_0_ga.xml::1334595170508-136::destevezg (generated)::(Checksum: 3:0e3df47ebc27a0e2d3d449f673c3436e)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-136', '2.0.3', '3:0e3df47ebc27a0e2d3d449f673c3436e', 514);

-- Changeset kinton2_0_ga.xml::1334595170508-137::destevezg (generated)::(Checksum: 3:b7d29b45d463a86fe85165ccd981b2a4)
ALTER TABLE `kinton`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-137', '2.0.3', '3:b7d29b45d463a86fe85165ccd981b2a4', 515);

-- Changeset kinton2_0_ga.xml::1334595170508-138::destevezg (generated)::(Checksum: 3:0a3a0dce75328a168956b34f2a166124)
ALTER TABLE `kinton`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-138', '2.0.3', '3:0a3a0dce75328a168956b34f2a166124', 516);

-- Changeset kinton2_0_ga.xml::1334595170508-139::destevezg (generated)::(Checksum: 3:a9f68a95692fd4cb61d1ab7f54a6add0)
ALTER TABLE `kinton`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-139', '2.0.3', '3:a9f68a95692fd4cb61d1ab7f54a6add0', 517);

-- Changeset kinton2_0_ga.xml::1334595170508-140::destevezg (generated)::(Checksum: 3:51bc92d1f8458a6758f84d5e40c6f88d)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-140', '2.0.3', '3:51bc92d1f8458a6758f84d5e40c6f88d', 518);

-- Changeset kinton2_0_ga.xml::1334595170508-141::destevezg (generated)::(Checksum: 3:732046a805d961eb44971fd636d52594)
ALTER TABLE `kinton`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-141', '2.0.3', '3:732046a805d961eb44971fd636d52594', 519);

-- Changeset kinton2_0_ga.xml::1334595170508-142::destevezg (generated)::(Checksum: 3:31e41feafb066c2be3b1cc2857f49208)
ALTER TABLE `kinton`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-142', '2.0.3', '3:31e41feafb066c2be3b1cc2857f49208', 520);

-- Changeset kinton2_0_ga.xml::1334595170508-143::destevezg (generated)::(Checksum: 3:466a1d498f0c2740539a49b128d9b6de)
ALTER TABLE `kinton`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-143', '2.0.3', '3:466a1d498f0c2740539a49b128d9b6de', 521);

-- Changeset kinton2_0_ga.xml::1334595170508-144::destevezg (generated)::(Checksum: 3:2c7e302fae12c8e84f18f3dba9f5c40c)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-144', '2.0.3', '3:2c7e302fae12c8e84f18f3dba9f5c40c', 522);

-- Changeset kinton2_0_ga.xml::1334595170508-145::destevezg (generated)::(Checksum: 3:b632cd05a5d8ab67cfad62318a6feacd)
ALTER TABLE `kinton`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-145', '2.0.3', '3:b632cd05a5d8ab67cfad62318a6feacd', 523);

-- Changeset kinton2_0_ga.xml::1334595170508-146::destevezg (generated)::(Checksum: 3:1361f06e4430e3572388dc130cf3f6ae)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-146', '2.0.3', '3:1361f06e4430e3572388dc130cf3f6ae', 524);

-- Changeset kinton2_0_ga.xml::1334595170508-147::destevezg (generated)::(Checksum: 3:fa81f7866672e0064abe59ce544d16ee)
ALTER TABLE `kinton`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-147', '2.0.3', '3:fa81f7866672e0064abe59ce544d16ee', 525);

-- Changeset kinton2_0_ga.xml::1334595170508-148::destevezg (generated)::(Checksum: 3:21d1330e830eda559d49619da27d4a2d)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-148', '2.0.3', '3:21d1330e830eda559d49619da27d4a2d', 526);

-- Changeset kinton2_0_ga.xml::1334595170508-149::destevezg (generated)::(Checksum: 3:6d20670c1bdea037fa86029d32e95c4e)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-149', '2.0.3', '3:6d20670c1bdea037fa86029d32e95c4e', 527);

-- Changeset kinton2_0_ga.xml::1334595170508-150::destevezg (generated)::(Checksum: 3:f93371517d74a4eab3924ee099b26c53)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-150', '2.0.3', '3:f93371517d74a4eab3924ee099b26c53', 528);

-- Changeset kinton2_0_ga.xml::1334595170508-151::destevezg (generated)::(Checksum: 3:206402c502be46899203f3badd9d8ec7)
ALTER TABLE `kinton`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-151', '2.0.3', '3:206402c502be46899203f3badd9d8ec7', 529);

-- Changeset kinton2_0_ga.xml::1334595170508-152::destevezg (generated)::(Checksum: 3:4b1fa941844e12bc85b4a6f54dca3194)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-152', '2.0.3', '3:4b1fa941844e12bc85b4a6f54dca3194', 530);

-- Changeset kinton2_0_ga.xml::1334595170508-153::destevezg (generated)::(Checksum: 3:72511dad2d82e148a62eadbe7350381a)
ALTER TABLE `kinton`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-153', '2.0.3', '3:72511dad2d82e148a62eadbe7350381a', 531);

-- Changeset kinton2_0_ga.xml::1334595170508-154::destevezg (generated)::(Checksum: 3:913ff3701711d54eee00d1fb8b58389d)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-154', '2.0.3', '3:913ff3701711d54eee00d1fb8b58389d', 532);

-- Changeset kinton2_0_ga.xml::1334595170508-155::destevezg (generated)::(Checksum: 3:082da0be85a69a1ebfc2a09ae9ec94e4)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-155', '2.0.3', '3:082da0be85a69a1ebfc2a09ae9ec94e4', 533);

-- Changeset kinton2_0_ga.xml::1334595170508-156::destevezg (generated)::(Checksum: 3:17e6766e834c47a60ded2846e28a0374)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-156', '2.0.3', '3:17e6766e834c47a60ded2846e28a0374', 534);

-- Changeset kinton2_0_ga.xml::1334595170508-157::destevezg (generated)::(Checksum: 3:3887fe4ffa2434ffbe9fdc910afa8538)
ALTER TABLE `kinton`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-157', '2.0.3', '3:3887fe4ffa2434ffbe9fdc910afa8538', 535);

-- Changeset kinton2_0_ga.xml::1334595170508-158::destevezg (generated)::(Checksum: 3:4f435a6168a0a18bb1d0714d21361b71)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-158', '2.0.3', '3:4f435a6168a0a18bb1d0714d21361b71', 536);

-- Changeset kinton2_0_ga.xml::1334595170508-159::destevezg (generated)::(Checksum: 3:1af426b7fdcaf93f1b45d9d5e8aa1bdc)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-159', '2.0.3', '3:1af426b7fdcaf93f1b45d9d5e8aa1bdc', 537);

-- Changeset kinton2_0_ga.xml::1334595170508-160::destevezg (generated)::(Checksum: 3:f7c4c533470ece45570602545702a16a)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-160', '2.0.3', '3:f7c4c533470ece45570602545702a16a', 538);

-- Changeset kinton2_0_ga.xml::1334595170508-161::destevezg (generated)::(Checksum: 3:c93821360ad0b67578d33e4371fca936)
ALTER TABLE `kinton`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-161', '2.0.3', '3:c93821360ad0b67578d33e4371fca936', 539);

-- Changeset kinton2_0_ga.xml::1334595170508-162::destevezg (generated)::(Checksum: 3:d44c1e70581845fad25b877d07c96182)
ALTER TABLE `kinton`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-162', '2.0.3', '3:d44c1e70581845fad25b877d07c96182', 540);

-- Changeset kinton2_0_ga.xml::1334595170508-163::destevezg (generated)::(Checksum: 3:dd8fdabb1b5568d9fbbe0342574633c4)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-163', '2.0.3', '3:dd8fdabb1b5568d9fbbe0342574633c4', 541);

-- Changeset kinton2_0_ga.xml::1334595170508-164::destevezg (generated)::(Checksum: 3:818367047e672790ff3ac9c3d13cec5e)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-164', '2.0.3', '3:818367047e672790ff3ac9c3d13cec5e', 542);

-- Changeset kinton2_0_ga.xml::1334595170508-165::destevezg (generated)::(Checksum: 3:9424adc44a74d3737f24c3d5b5d812fe)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-165', '2.0.3', '3:9424adc44a74d3737f24c3d5b5d812fe', 543);

-- Changeset kinton2_0_ga.xml::1334595170508-166::destevezg (generated)::(Checksum: 3:46215ad45eb3e72d8ee65ee7be941e1a)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-166', '2.0.3', '3:46215ad45eb3e72d8ee65ee7be941e1a', 544);

-- Changeset kinton2_0_ga.xml::1334595170508-167::destevezg (generated)::(Checksum: 3:d09aba94d1f59c8262f5399abec69cc5)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-167', '2.0.3', '3:d09aba94d1f59c8262f5399abec69cc5', 545);

-- Changeset kinton2_0_ga.xml::1334595170508-168::destevezg (generated)::(Checksum: 3:e717cc8fc89cb02d56bd262979378060)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-168', '2.0.3', '3:e717cc8fc89cb02d56bd262979378060', 546);

-- Changeset kinton2_0_ga.xml::1334595170508-169::destevezg (generated)::(Checksum: 3:c2d45eafaae6aa722440adfe212203c3)
ALTER TABLE `kinton`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-169', '2.0.3', '3:c2d45eafaae6aa722440adfe212203c3', 547);

-- Changeset kinton2_0_ga.xml::1334595170508-170::destevezg (generated)::(Checksum: 3:6ebfd010b6b125ab61846d74710cdb9c)
ALTER TABLE `kinton`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-170', '2.0.3', '3:6ebfd010b6b125ab61846d74710cdb9c', 548);

-- Changeset kinton2_0_ga.xml::1334595170508-171::destevezg (generated)::(Checksum: 3:6b5855cbae91f21f0eecc23d7d31e7d6)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-171', '2.0.3', '3:6b5855cbae91f21f0eecc23d7d31e7d6', 549);

-- Changeset kinton2_0_ga.xml::1334595170508-172::destevezg (generated)::(Checksum: 3:8a5267a1f2d46f48e685e311a9a2bb38)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-172', '2.0.3', '3:8a5267a1f2d46f48e685e311a9a2bb38', 550);

-- Changeset kinton2_0_ga.xml::1334595170508-173::destevezg (generated)::(Checksum: 3:ced9008c983144fe8e36c11ee1d24a81)
ALTER TABLE `kinton`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-173', '2.0.3', '3:ced9008c983144fe8e36c11ee1d24a81', 551);

-- Changeset kinton2_0_ga.xml::1334595170508-174::destevezg (generated)::(Checksum: 3:fa97c2e5bfec4084f586a823469f3b1f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-174', '2.0.3', '3:fa97c2e5bfec4084f586a823469f3b1f', 552);

-- Changeset kinton2_0_ga.xml::1334595170508-175::destevezg (generated)::(Checksum: 3:f57948dcc96f12c0699213820b6b756f)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-175', '2.0.3', '3:f57948dcc96f12c0699213820b6b756f', 553);

-- Changeset kinton2_0_ga.xml::1334595170508-176::destevezg (generated)::(Checksum: 3:e1ca982714c144ed2d5ac20561bc6657)
ALTER TABLE `kinton`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-176', '2.0.3', '3:e1ca982714c144ed2d5ac20561bc6657', 554);

-- Changeset kinton2_0_ga.xml::1334595170508-177::destevezg (generated)::(Checksum: 3:746c3e281a036d6ada1b5e3fd95a4696)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-177', '2.0.3', '3:746c3e281a036d6ada1b5e3fd95a4696', 555);

-- Changeset kinton2_0_ga.xml::1334595170508-178::destevezg (generated)::(Checksum: 3:b3376ebce0a03581c19a96f0c56bbd66)
ALTER TABLE `kinton`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-178', '2.0.3', '3:b3376ebce0a03581c19a96f0c56bbd66', 556);

-- Changeset kinton2_0_ga.xml::1334595170508-179::destevezg (generated)::(Checksum: 3:c16c6d833472e00f707818c0d317b44b)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-179', '2.0.3', '3:c16c6d833472e00f707818c0d317b44b', 557);

-- Changeset kinton2_0_ga.xml::1334595170508-180::destevezg (generated)::(Checksum: 3:420c8225285f8d8e374d50a1ed9e237e)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-180', '2.0.3', '3:420c8225285f8d8e374d50a1ed9e237e', 558);

-- Changeset kinton2_0_ga.xml::1334595170508-181::destevezg (generated)::(Checksum: 3:8f456a92ce7ba6f2b3625311cb2a47cf)
ALTER TABLE `kinton`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-181', '2.0.3', '3:8f456a92ce7ba6f2b3625311cb2a47cf', 559);

-- Changeset kinton2_0_ga.xml::1334595170508-182::destevezg (generated)::(Checksum: 3:c5ab00bc6a57c9809eb1be93120180ba)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-182', '2.0.3', '3:c5ab00bc6a57c9809eb1be93120180ba', 560);

-- Changeset kinton2_0_ga.xml::1334595170508-183::destevezg (generated)::(Checksum: 3:1035cb414581bebcdacdd2a161d19a41)
ALTER TABLE `kinton`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-183', '2.0.3', '3:1035cb414581bebcdacdd2a161d19a41', 561);

-- Changeset kinton2_0_ga.xml::1334595170508-184::destevezg (generated)::(Checksum: 3:f7d0b7bcff44df8f076be460f1172674)
ALTER TABLE `kinton`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-184', '2.0.3', '3:f7d0b7bcff44df8f076be460f1172674', 562);

-- Changeset kinton2_0_ga.xml::1334595170508-185::destevezg (generated)::(Checksum: 3:2724c06259dee3fa38ec1a3bd14d32b5)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-185', '2.0.3', '3:2724c06259dee3fa38ec1a3bd14d32b5', 563);

-- Changeset kinton2_0_ga.xml::1334595170508-186::destevezg (generated)::(Checksum: 3:c1c812e559c885292b18196d35ef708e)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-186', '2.0.3', '3:c1c812e559c885292b18196d35ef708e', 564);

-- Changeset kinton2_0_ga.xml::1334595170508-187::destevezg (generated)::(Checksum: 3:5d48f5fe3bb5c924a4e96180cc3d9790)
ALTER TABLE `kinton`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-187', '2.0.3', '3:5d48f5fe3bb5c924a4e96180cc3d9790', 565);

-- Changeset kinton2_0_ga.xml::1334595170508-188::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `kinton`.`category`(`name`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-188', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 566);

-- Changeset kinton2_0_ga.xml::1334595170508-189::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `kinton`.`user`(`user`, `authType`);

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', 'kinton2_0_ga.xml', '1334595170508-189', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 567);

-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::1335280201177-1::destevezg (generated)::(Checksum: 3:fd4f14896387e0fd842ac1cbc5b89239)
CREATE TABLE `kinton`.`accounting_event_detail_HF1` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL_HF1` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', '1335280201177-1', '2.0.3', '3:fd4f14896387e0fd842ac1cbc5b89239', 568);

-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::ABICLOUDPREMIUM-432432::destevez::(Checksum: 3:1fd743ccdf084124370692129205fcd8)
DROP TRIGGER IF EXISTS kinton.datacenter_created;

CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);

END IF;

END;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevez', '', NOW(), 'SQL From File', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', 'ABICLOUDPREMIUM-432432', '2.0.3', '3:1fd743ccdf084124370692129205fcd8', 569);

-- Changeset delta/2.0.0-HF2_new/deltachangelog.xml::1335280201177-1::destevezg (generated)::(Checksum: 3:822052e1ee0bbf2727e909744cdbbd48)
CREATE TABLE `kinton`.`accounting_event_detail_HF2` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL_HF2` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'delta/2.0.0-HF2_new/deltachangelog.xml', '1335280201177-1', '2.0.3', '3:822052e1ee0bbf2727e909744cdbbd48', 570);

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

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevez', '', NOW(), 'SQL From File (x2)', 'EXECUTED', 'delta/2.0.0-HF2_new/deltachangelog.xml', 'ABICLOUDPREMIUM-9899999', '2.0.3', '3:cb1689ce5579b918d949bf48f2c9aa9a', 571);

