-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: /home/destevezg/abiws/abiquo/database/changelog.xml
-- Ran at: 4/16/12 3:57 PM
-- Against: root@localhost@jdbc:mysql://localhost:3306/kinton_liquibase
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE `DATABASECHANGELOGLOCK` (`ID` INT NOT NULL, `LOCKED` TINYINT(1) NOT NULL, `LOCKGRANTED` DATETIME, `LOCKEDBY` VARCHAR(255), CONSTRAINT `PK_DATABASECHANGELOGLOCK` PRIMARY KEY (`ID`));

INSERT INTO `DATABASECHANGELOGLOCK` (`ID`, `LOCKED`) VALUES (1, 0);

-- Lock Database
-- Create Database Change Log Table
CREATE TABLE `DATABASECHANGELOG` (`ID` VARCHAR(63) NOT NULL, `AUTHOR` VARCHAR(63) NOT NULL, `FILENAME` VARCHAR(200) NOT NULL, `DATEEXECUTED` DATETIME NOT NULL, `ORDEREXECUTED` INT NOT NULL, `EXECTYPE` VARCHAR(10) NOT NULL, `MD5SUM` VARCHAR(35), `DESCRIPTION` VARCHAR(255), `COMMENTS` VARCHAR(255), `TAG` VARCHAR(255), `LIQUIBASE` VARCHAR(20), CONSTRAINT `PK_DATABASECHANGELOG` PRIMARY KEY (`ID`, `AUTHOR`, `FILENAME`));

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-1::destevezg (generated)::(Checksum: 3:c32bbf075db7c5933ca3cce5df660aa9)
CREATE TABLE `alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-1', '2.0.3', '3:c32bbf075db7c5933ca3cce5df660aa9', 1);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-2::destevezg (generated)::(Checksum: 3:b518e45dd85a26cde440580145fcddb4)
CREATE TABLE `apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-2', '2.0.3', '3:b518e45dd85a26cde440580145fcddb4', 2);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-3::destevezg (generated)::(Checksum: 3:966996751618877d8c5c9d810821a619)
CREATE TABLE `auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-3', '2.0.3', '3:966996751618877d8c5c9d810821a619', 3);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-4::destevezg (generated)::(Checksum: 3:447eb654eeabbcb662cb7dad38635820)
CREATE TABLE `auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-4', '2.0.3', '3:447eb654eeabbcb662cb7dad38635820', 4);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 5);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 6);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 7);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-8::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-8', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 8);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 9);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 10);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 11);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 12);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-13::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-13', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 13);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 14);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-15::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-15', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 15);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 16);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-17::destevezg (generated)::(Checksum: 3:ffd62de872535e1f2da1cac582b3c9d5)
CREATE TABLE `disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-17', '2.0.3', '3:ffd62de872535e1f2da1cac582b3c9d5', 17);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 18);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-19::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-19', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 19);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 20);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-21::destevezg (generated)::(Checksum: 3:3e94390d029bf8e6061698eb5628d573)
CREATE TABLE `enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-21', '2.0.3', '3:3e94390d029bf8e6061698eb5628d573', 21);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-22::destevezg (generated)::(Checksum: 3:be4693925397c572062f1fab8c984362)
CREATE TABLE `enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50), `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-22', '2.0.3', '3:be4693925397c572062f1fab8c984362', 22);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-23::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-23', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 23);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-24::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-24', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 24);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-25::destevezg (generated)::(Checksum: 3:f6211931acdcc03c90d5c6d208a910b9)
CREATE TABLE `heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-25', '2.0.3', '3:f6211931acdcc03c90d5c6d208a910b9', 25);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 26);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 27);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 28);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 29);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-30::destevezg (generated)::(Checksum: 3:38e9d9ed33afac96b738855a00109f9c)
CREATE TABLE `log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-30', '2.0.3', '3:38e9d9ed33afac96b738855a00109f9c', 30);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-31::destevezg (generated)::(Checksum: 3:0a23cc6bd4adfbad1eaa59b5b7da2f2e)
CREATE TABLE `metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-31', '2.0.3', '3:0a23cc6bd4adfbad1eaa59b5b7da2f2e', 31);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 32);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 33);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 34);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 35);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-36::destevezg (generated)::(Checksum: 3:b6fc7632116240a776aa00853de6bcad)
CREATE TABLE `nodenetwork` (`idNode` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-36', '2.0.3', '3:b6fc7632116240a776aa00853de6bcad', 36);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-37::destevezg (generated)::(Checksum: 3:6952f964ce37833b8144613d3cf11344)
CREATE TABLE `noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-37', '2.0.3', '3:6952f964ce37833b8144613d3cf11344', 37);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-38::destevezg (generated)::(Checksum: 3:72bf3673a02388e2bc0da52ae70e5fce)
CREATE TABLE `nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-38', '2.0.3', '3:72bf3673a02388e2bc0da52ae70e5fce', 38);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 39);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 40);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 41);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 42);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 43);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-44::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-44', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 44);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 45);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 46);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 47);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 48);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-49::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-49', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 49);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-50::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-50', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 50);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 51);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-52::destevezg (generated)::(Checksum: 3:ed4ae73f975deb795a4e2fe4980ada26)
CREATE TABLE `register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-52', '2.0.3', '3:ed4ae73f975deb795a4e2fe4980ada26', 52);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 53);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 54);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 55);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 56);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 57);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 58);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 59);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 60);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 61);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-62::destevezg (generated)::(Checksum: 3:31486daf8f610a7250344cb981627a60)
CREATE TABLE `tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-62', '2.0.3', '3:31486daf8f610a7250344cb981627a60', 62);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 63);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-64::destevezg (generated)::(Checksum: 3:e5d525478dfcdecb18cc7cad873150c3)
CREATE TABLE `ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-64', '2.0.3', '3:e5d525478dfcdecb18cc7cad873150c3', 64);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 65);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-66::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-66', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 66);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 67);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-68::destevezg (generated)::(Checksum: 3:aecbcce0078b6d04274190ba65cfca54)
CREATE TABLE `vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-68', '2.0.3', '3:aecbcce0078b6d04274190ba65cfca54', 68);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-69::destevezg (generated)::(Checksum: 3:bc9ba0c28876d849c819915c84e9cd70)
CREATE TABLE `virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-69', '2.0.3', '3:bc9ba0c28876d849c819915c84e9cd70', 69);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-70::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-70', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 70);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 71);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-72::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-72', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 72);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 73);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-74::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-74', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 74);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-75::destevezg (generated)::(Checksum: 3:62ecd79335be6ba7c6365fb60199052d)
CREATE TABLE `virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-75', '2.0.3', '3:62ecd79335be6ba7c6365fb60199052d', 75);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 76);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 77);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 78);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 79);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 80);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 81);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 82);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 83);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 84);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-85::destevezg (generated)::(Checksum: 3:2dd4badadcd15f6378a42b518d5aab69)
ALTER TABLE `vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`, `idEnterprise`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-85', '2.0.3', '3:2dd4badadcd15f6378a42b518d5aab69', 85);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-86::destevezg (generated)::(Checksum: 3:c99e6608c0f45bf70433a743f80d8992)
ALTER TABLE `kinton_liquibase`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-86', '2.0.3', '3:c99e6608c0f45bf70433a743f80d8992', 86);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-87::destevezg (generated)::(Checksum: 3:76f80741e3a196a8595c4df2a2cb1a4a)
ALTER TABLE `kinton_liquibase`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton_liquibase`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-87', '2.0.3', '3:76f80741e3a196a8595c4df2a2cb1a4a', 87);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-88::destevezg (generated)::(Checksum: 3:3b937104469a886e54beafe1459cb772)
ALTER TABLE `kinton_liquibase`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-88', '2.0.3', '3:3b937104469a886e54beafe1459cb772', 88);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-89::destevezg (generated)::(Checksum: 3:212f38afd8fe18b74c7196e14ce66a28)
ALTER TABLE `kinton_liquibase`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton_liquibase`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-89', '2.0.3', '3:212f38afd8fe18b74c7196e14ce66a28', 89);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-90::destevezg (generated)::(Checksum: 3:8409beebcfd4a6398dc4c64a6beaa2bb)
ALTER TABLE `kinton_liquibase`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-90', '2.0.3', '3:8409beebcfd4a6398dc4c64a6beaa2bb', 90);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-91::destevezg (generated)::(Checksum: 3:f3fcdeb5a6948b40acdd42ffaa2b9ca2)
ALTER TABLE `kinton_liquibase`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-91', '2.0.3', '3:f3fcdeb5a6948b40acdd42ffaa2b9ca2', 91);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-92::destevezg (generated)::(Checksum: 3:f3d14adb1e350c51997a1e6844ab9940)
ALTER TABLE `kinton_liquibase`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-92', '2.0.3', '3:f3d14adb1e350c51997a1e6844ab9940', 92);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-93::destevezg (generated)::(Checksum: 3:9fe0560ad10c16c6447e1ed6885f76f4)
ALTER TABLE `kinton_liquibase`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton_liquibase`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-93', '2.0.3', '3:9fe0560ad10c16c6447e1ed6885f76f4', 93);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-94::destevezg (generated)::(Checksum: 3:0032907cbce09c87640b9ff6a764c480)
ALTER TABLE `kinton_liquibase`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-94', '2.0.3', '3:0032907cbce09c87640b9ff6a764c480', 94);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-95::destevezg (generated)::(Checksum: 3:dd62c0e68f4129aac905b4057dc1d099)
ALTER TABLE `kinton_liquibase`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-95', '2.0.3', '3:dd62c0e68f4129aac905b4057dc1d099', 95);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-96::destevezg (generated)::(Checksum: 3:270162361562e2f4bfc315790e4c2436)
ALTER TABLE `kinton_liquibase`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton_liquibase`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-96', '2.0.3', '3:270162361562e2f4bfc315790e4c2436', 96);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-97::destevezg (generated)::(Checksum: 3:551bb6624a1bbf377d366698131eca46)
ALTER TABLE `kinton_liquibase`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-97', '2.0.3', '3:551bb6624a1bbf377d366698131eca46', 97);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-98::destevezg (generated)::(Checksum: 3:0e34a5a7111441531665633a6d7b9f72)
ALTER TABLE `kinton_liquibase`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-98', '2.0.3', '3:0e34a5a7111441531665633a6d7b9f72', 98);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-99::destevezg (generated)::(Checksum: 3:04a1d1e90ad4469a47e9708c8089627b)
ALTER TABLE `kinton_liquibase`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton_liquibase`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-99', '2.0.3', '3:04a1d1e90ad4469a47e9708c8089627b', 99);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-100::destevezg (generated)::(Checksum: 3:f91ad4d8c5060389553f3d306358f05b)
ALTER TABLE `kinton_liquibase`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-100', '2.0.3', '3:f91ad4d8c5060389553f3d306358f05b', 100);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-101::destevezg (generated)::(Checksum: 3:08431dad180a0d22381e81da51437d58)
ALTER TABLE `kinton_liquibase`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton_liquibase`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-101', '2.0.3', '3:08431dad180a0d22381e81da51437d58', 101);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-102::destevezg (generated)::(Checksum: 3:ec18fe708288c3727c059f70fec0592d)
ALTER TABLE `kinton_liquibase`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-102', '2.0.3', '3:ec18fe708288c3727c059f70fec0592d', 102);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-103::destevezg (generated)::(Checksum: 3:32ea224b9e146eb40936f76867ff7b14)
ALTER TABLE `kinton_liquibase`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-103', '2.0.3', '3:32ea224b9e146eb40936f76867ff7b14', 103);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-104::destevezg (generated)::(Checksum: 3:16385e2e83f9ad14bf2d01931632dcf8)
ALTER TABLE `kinton_liquibase`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-104', '2.0.3', '3:16385e2e83f9ad14bf2d01931632dcf8', 104);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-105::destevezg (generated)::(Checksum: 3:a3889810f74b3504ba455f5b92503a17)
ALTER TABLE `kinton_liquibase`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-105', '2.0.3', '3:a3889810f74b3504ba455f5b92503a17', 105);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-106::destevezg (generated)::(Checksum: 3:1c2a45f18c2c9e0205cec68c5f15c8df)
ALTER TABLE `kinton_liquibase`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-106', '2.0.3', '3:1c2a45f18c2c9e0205cec68c5f15c8df', 106);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-107::destevezg (generated)::(Checksum: 3:3b2d585cddafb8dd8b487c79a25ec210)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton_liquibase`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-107', '2.0.3', '3:3b2d585cddafb8dd8b487c79a25ec210', 107);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-108::destevezg (generated)::(Checksum: 3:aca4613cd7b924079341eb1d6e53d4b6)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-108', '2.0.3', '3:aca4613cd7b924079341eb1d6e53d4b6', 108);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-109::destevezg (generated)::(Checksum: 3:20c32675c8298f6834a07abae0b131d5)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton_liquibase`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-109', '2.0.3', '3:20c32675c8298f6834a07abae0b131d5', 109);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-110::destevezg (generated)::(Checksum: 3:38e3bcff643d714af308c044fba52bbf)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton_liquibase`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-110', '2.0.3', '3:38e3bcff643d714af308c044fba52bbf', 110);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-111::destevezg (generated)::(Checksum: 3:b443894ec4ff12050a84044d1bcd1e4e)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton_liquibase`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-111', '2.0.3', '3:b443894ec4ff12050a84044d1bcd1e4e', 111);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-112::destevezg (generated)::(Checksum: 3:ba6bbf2f30da240d0ee6984a73d03e19)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-112', '2.0.3', '3:ba6bbf2f30da240d0ee6984a73d03e19', 112);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-113::destevezg (generated)::(Checksum: 3:82d3f1fd372f88015e18d54632a7b55d)
ALTER TABLE `kinton_liquibase`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-113', '2.0.3', '3:82d3f1fd372f88015e18d54632a7b55d', 113);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-114::destevezg (generated)::(Checksum: 3:ededbf117cea566d26248a2eed8d500d)
ALTER TABLE `kinton_liquibase`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-114', '2.0.3', '3:ededbf117cea566d26248a2eed8d500d', 114);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-115::destevezg (generated)::(Checksum: 3:9abeafe76cb303b75da2529d2ed49f33)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-115', '2.0.3', '3:9abeafe76cb303b75da2529d2ed49f33', 115);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-116::destevezg (generated)::(Checksum: 3:6f31fcadde625681d0780e5bdb930ab1)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-116', '2.0.3', '3:6f31fcadde625681d0780e5bdb930ab1', 116);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-117::destevezg (generated)::(Checksum: 3:5c05b8f81a9c49441952ac6e288ca4ed)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-117', '2.0.3', '3:5c05b8f81a9c49441952ac6e288ca4ed', 117);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-118::destevezg (generated)::(Checksum: 3:d7fcd283a8430116755eadb98871038e)
ALTER TABLE `kinton_liquibase`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton_liquibase`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-118', '2.0.3', '3:d7fcd283a8430116755eadb98871038e', 118);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-119::destevezg (generated)::(Checksum: 3:65e4efa31cdcb216c11fbc35119ff4b3)
ALTER TABLE `kinton_liquibase`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton_liquibase`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-119', '2.0.3', '3:65e4efa31cdcb216c11fbc35119ff4b3', 119);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-120::destevezg (generated)::(Checksum: 3:6c3d167564dbddcbc4b5da95b4c989cf)
ALTER TABLE `kinton_liquibase`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton_liquibase`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-120', '2.0.3', '3:6c3d167564dbddcbc4b5da95b4c989cf', 120);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-121::destevezg (generated)::(Checksum: 3:6eef486fa27a9271b265750cf5822329)
ALTER TABLE `kinton_liquibase`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton_liquibase`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-121', '2.0.3', '3:6eef486fa27a9271b265750cf5822329', 121);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-122::destevezg (generated)::(Checksum: 3:ba1257e710d03eedabb07d7df82f28ed)
ALTER TABLE `kinton_liquibase`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton_liquibase`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-122', '2.0.3', '3:ba1257e710d03eedabb07d7df82f28ed', 122);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-123::destevezg (generated)::(Checksum: 3:40587fdbdc0987f94dc8870d5433a648)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-123', '2.0.3', '3:40587fdbdc0987f94dc8870d5433a648', 123);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-124::destevezg (generated)::(Checksum: 3:b36b0910dee590233354610d2a82e84c)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-124', '2.0.3', '3:b36b0910dee590233354610d2a82e84c', 124);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-125::destevezg (generated)::(Checksum: 3:713a6bc8106c0385f397d8d3f5519f89)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-125', '2.0.3', '3:713a6bc8106c0385f397d8d3f5519f89', 125);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-126::destevezg (generated)::(Checksum: 3:195b0c68ac2f0abc8a08de9bb1f7d42c)
ALTER TABLE `kinton_liquibase`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton_liquibase`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-126', '2.0.3', '3:195b0c68ac2f0abc8a08de9bb1f7d42c', 126);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-127::destevezg (generated)::(Checksum: 3:01a3b1335b2708d52cf2054388db2ed2)
ALTER TABLE `kinton_liquibase`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-127', '2.0.3', '3:01a3b1335b2708d52cf2054388db2ed2', 127);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-128::destevezg (generated)::(Checksum: 3:26fa047add1a70737553b68179e23149)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton_liquibase`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-128', '2.0.3', '3:26fa047add1a70737553b68179e23149', 128);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-129::destevezg (generated)::(Checksum: 3:316c6413e16c240a9536d31cac1a009d)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-129', '2.0.3', '3:316c6413e16c240a9536d31cac1a009d', 129);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-130::destevezg (generated)::(Checksum: 3:98547d9da510f98a04525bb1da899fd2)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-130', '2.0.3', '3:98547d9da510f98a04525bb1da899fd2', 130);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-131::destevezg (generated)::(Checksum: 3:9a2fd8be4e660b66984438a523889b99)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-131', '2.0.3', '3:9a2fd8be4e660b66984438a523889b99', 131);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-132::destevezg (generated)::(Checksum: 3:e4ef52980015af94bdd498643e2c99a9)
ALTER TABLE `kinton_liquibase`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-132', '2.0.3', '3:e4ef52980015af94bdd498643e2c99a9', 132);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-133::destevezg (generated)::(Checksum: 3:7dc67ac46da69cc5700b0beb5be00d36)
ALTER TABLE `kinton_liquibase`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-133', '2.0.3', '3:7dc67ac46da69cc5700b0beb5be00d36', 133);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-134::destevezg (generated)::(Checksum: 3:3bfd4d3d0292b3fccd17b7071128b3f6)
ALTER TABLE `kinton_liquibase`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-134', '2.0.3', '3:3bfd4d3d0292b3fccd17b7071128b3f6', 134);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-135::destevezg (generated)::(Checksum: 3:ff7766e265801918ad4cdf7ecd46c560)
ALTER TABLE `kinton_liquibase`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-135', '2.0.3', '3:ff7766e265801918ad4cdf7ecd46c560', 135);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-136::destevezg (generated)::(Checksum: 3:ee2a223fb099e67cf6fc18b87ae7f591)
ALTER TABLE `kinton_liquibase`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton_liquibase`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-136', '2.0.3', '3:ee2a223fb099e67cf6fc18b87ae7f591', 136);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-137::destevezg (generated)::(Checksum: 3:ac64bed93201f4481d726403fcf64066)
ALTER TABLE `kinton_liquibase`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-137', '2.0.3', '3:ac64bed93201f4481d726403fcf64066', 137);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-138::destevezg (generated)::(Checksum: 3:73100cbb3f5a942160cc64a8424abd5e)
ALTER TABLE `kinton_liquibase`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-138', '2.0.3', '3:73100cbb3f5a942160cc64a8424abd5e', 138);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-139::destevezg (generated)::(Checksum: 3:793c781707bced3d1654c84b7c76e77d)
ALTER TABLE `kinton_liquibase`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-139', '2.0.3', '3:793c781707bced3d1654c84b7c76e77d', 139);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-140::destevezg (generated)::(Checksum: 3:9055467ff7c5054137b4f90ebe876da7)
ALTER TABLE `kinton_liquibase`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton_liquibase`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-140', '2.0.3', '3:9055467ff7c5054137b4f90ebe876da7', 140);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-141::destevezg (generated)::(Checksum: 3:fd7e4dd4737d5370f20271be9fdd7eb2)
ALTER TABLE `kinton_liquibase`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton_liquibase`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-141', '2.0.3', '3:fd7e4dd4737d5370f20271be9fdd7eb2', 141);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-142::destevezg (generated)::(Checksum: 3:09c3d29a363519b3cac07f4202c646fe)
ALTER TABLE `kinton_liquibase`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-142', '2.0.3', '3:09c3d29a363519b3cac07f4202c646fe', 142);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-143::destevezg (generated)::(Checksum: 3:50b7c3f1e04b85602e866f583a16b66e)
ALTER TABLE `kinton_liquibase`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-143', '2.0.3', '3:50b7c3f1e04b85602e866f583a16b66e', 143);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-144::destevezg (generated)::(Checksum: 3:ace1843a6d783d646d969ccd823db671)
ALTER TABLE `kinton_liquibase`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-144', '2.0.3', '3:ace1843a6d783d646d969ccd823db671', 144);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-145::destevezg (generated)::(Checksum: 3:a7e93675d1197b63062e6448fef41df5)
ALTER TABLE `kinton_liquibase`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-145', '2.0.3', '3:a7e93675d1197b63062e6448fef41df5', 145);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-146::destevezg (generated)::(Checksum: 3:e51d638bc4415d31dbde4fa70d70850c)
ALTER TABLE `kinton_liquibase`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-146', '2.0.3', '3:e51d638bc4415d31dbde4fa70d70850c', 146);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-147::destevezg (generated)::(Checksum: 3:d555b66bdf90f7a7030d0f68de9f9352)
ALTER TABLE `kinton_liquibase`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-147', '2.0.3', '3:d555b66bdf90f7a7030d0f68de9f9352', 147);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-148::destevezg (generated)::(Checksum: 3:426ea6a8d43fbfa8dbf1e3042100d680)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-148', '2.0.3', '3:426ea6a8d43fbfa8dbf1e3042100d680', 148);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-149::destevezg (generated)::(Checksum: 3:aa7373c0c3f49483376a4f81f701eac1)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-149', '2.0.3', '3:aa7373c0c3f49483376a4f81f701eac1', 149);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-150::destevezg (generated)::(Checksum: 3:da2c3e52f06964c57ef3d2271a9b6a0e)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-150', '2.0.3', '3:da2c3e52f06964c57ef3d2271a9b6a0e', 150);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-151::destevezg (generated)::(Checksum: 3:222c34869af778782ab817dc95cc5b2e)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-151', '2.0.3', '3:222c34869af778782ab817dc95cc5b2e', 151);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-152::destevezg (generated)::(Checksum: 3:512ca0846b9cc8eaf543619fe37c2467)
ALTER TABLE `kinton_liquibase`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-152', '2.0.3', '3:512ca0846b9cc8eaf543619fe37c2467', 152);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-153::destevezg (generated)::(Checksum: 3:6ff1055b34ea718a053fff2b8df4c8a5)
ALTER TABLE `kinton_liquibase`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-153', '2.0.3', '3:6ff1055b34ea718a053fff2b8df4c8a5', 153);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-154::destevezg (generated)::(Checksum: 3:d5d8e8618ab9866e9f9cfb448ae33969)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-154', '2.0.3', '3:d5d8e8618ab9866e9f9cfb448ae33969', 154);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-155::destevezg (generated)::(Checksum: 3:b122233bd5e4bb430579f5f814d36397)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-155', '2.0.3', '3:b122233bd5e4bb430579f5f814d36397', 155);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-156::destevezg (generated)::(Checksum: 3:dfbf9f81255b3cd3a3161dcc17511110)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-156', '2.0.3', '3:dfbf9f81255b3cd3a3161dcc17511110', 156);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-157::destevezg (generated)::(Checksum: 3:c9fba8f99d3108f2bd3b9c9f390eeec7)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-157', '2.0.3', '3:c9fba8f99d3108f2bd3b9c9f390eeec7', 157);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-158::destevezg (generated)::(Checksum: 3:f31789791e81e7135e8124295ee327d2)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton_liquibase`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-158', '2.0.3', '3:f31789791e81e7135e8124295ee327d2', 158);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-159::destevezg (generated)::(Checksum: 3:0e6a4917ed52ee38daa466c66c3f9cd9)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-159', '2.0.3', '3:0e6a4917ed52ee38daa466c66c3f9cd9', 159);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-160::destevezg (generated)::(Checksum: 3:3cfe3848114a9224ffa33b2dfb64f115)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-160', '2.0.3', '3:3cfe3848114a9224ffa33b2dfb64f115', 160);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-161::destevezg (generated)::(Checksum: 3:733f8240f9c747fbb0f87feef304343c)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton_liquibase`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-161', '2.0.3', '3:733f8240f9c747fbb0f87feef304343c', 161);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-162::destevezg (generated)::(Checksum: 3:bf92f9b5a97addff1277e5ff780845a9)
ALTER TABLE `kinton_liquibase`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-162', '2.0.3', '3:bf92f9b5a97addff1277e5ff780845a9', 162);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-163::destevezg (generated)::(Checksum: 3:9f3ba6cbc69c86e618fe2874a791fa06)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-163', '2.0.3', '3:9f3ba6cbc69c86e618fe2874a791fa06', 163);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-164::destevezg (generated)::(Checksum: 3:67b88aa30df28d3201c7ceebcb52bc94)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton_liquibase`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-164', '2.0.3', '3:67b88aa30df28d3201c7ceebcb52bc94', 164);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-165::destevezg (generated)::(Checksum: 3:e12866d3a5ede591cd3c87977d048b4e)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-165', '2.0.3', '3:e12866d3a5ede591cd3c87977d048b4e', 165);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-166::destevezg (generated)::(Checksum: 3:4f2175c2f4541349202628e309765132)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton_liquibase`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-166', '2.0.3', '3:4f2175c2f4541349202628e309765132', 166);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-167::destevezg (generated)::(Checksum: 3:fedaa460d66535f899c7529f1149ebae)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-167', '2.0.3', '3:fedaa460d66535f899c7529f1149ebae', 167);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-168::destevezg (generated)::(Checksum: 3:c675f4be2a72820110a984d8c47c662c)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-168', '2.0.3', '3:c675f4be2a72820110a984d8c47c662c', 168);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-169::destevezg (generated)::(Checksum: 3:27a4887055b31cd11ff095d1cfa85916)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton_liquibase`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-169', '2.0.3', '3:27a4887055b31cd11ff095d1cfa85916', 169);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-170::destevezg (generated)::(Checksum: 3:560374d4b47a9b6a771044148b282e70)
ALTER TABLE `kinton_liquibase`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-170', '2.0.3', '3:560374d4b47a9b6a771044148b282e70', 170);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-171::destevezg (generated)::(Checksum: 3:68d7563ef38aead2c95fdaac0e888719)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-171', '2.0.3', '3:68d7563ef38aead2c95fdaac0e888719', 171);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-172::destevezg (generated)::(Checksum: 3:312c8e0fff7774e3d6e5524f517e0b82)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton_liquibase`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-172', '2.0.3', '3:312c8e0fff7774e3d6e5524f517e0b82', 172);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-173::destevezg (generated)::(Checksum: 3:bef7a80f32d6231b651ba0c87aa0fde1)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-173', '2.0.3', '3:bef7a80f32d6231b651ba0c87aa0fde1', 173);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-174::destevezg (generated)::(Checksum: 3:d41c0e29185d5d92dc0aea3d265de30c)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-174', '2.0.3', '3:d41c0e29185d5d92dc0aea3d265de30c', 174);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-175::destevezg (generated)::(Checksum: 3:3daa19f6643231b6686e28616fb0ae10)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-175', '2.0.3', '3:3daa19f6643231b6686e28616fb0ae10', 175);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-176::destevezg (generated)::(Checksum: 3:cd285df4a114643b54089a40a1dad806)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-176', '2.0.3', '3:cd285df4a114643b54089a40a1dad806', 176);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-177::destevezg (generated)::(Checksum: 3:f65112b632a308d369d6b09a37808c7f)
ALTER TABLE `kinton_liquibase`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton_liquibase`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-177', '2.0.3', '3:f65112b632a308d369d6b09a37808c7f', 177);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-178::destevezg (generated)::(Checksum: 3:c93bcf222bca4c78a4a459cf33a76a5a)
ALTER TABLE `kinton_liquibase`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-178', '2.0.3', '3:c93bcf222bca4c78a4a459cf33a76a5a', 178);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-179::destevezg (generated)::(Checksum: 3:426a5ef53ece7774a8e83c85a19d8625)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-179', '2.0.3', '3:426a5ef53ece7774a8e83c85a19d8625', 179);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-180::destevezg (generated)::(Checksum: 3:355f1260cb89891ff1b754285d70338d)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-180', '2.0.3', '3:355f1260cb89891ff1b754285d70338d', 180);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-181::destevezg (generated)::(Checksum: 3:fa4e6b2f4a0c16d94c0b5e38d39837ed)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton_liquibase`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-181', '2.0.3', '3:fa4e6b2f4a0c16d94c0b5e38d39837ed', 181);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-182::destevezg (generated)::(Checksum: 3:eeb5e786928b931e91952eabf01b7c08)
ALTER TABLE `kinton_liquibase`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-182', '2.0.3', '3:eeb5e786928b931e91952eabf01b7c08', 182);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-183::destevezg (generated)::(Checksum: 3:8c3160a3110db19310507271709cb854)
ALTER TABLE `kinton_liquibase`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-183', '2.0.3', '3:8c3160a3110db19310507271709cb854', 183);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-184::destevezg (generated)::(Checksum: 3:e695c6ffab0d9f64a2e422d4c98e8924)
ALTER TABLE `kinton_liquibase`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-184', '2.0.3', '3:e695c6ffab0d9f64a2e422d4c98e8924', 184);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-185::destevezg (generated)::(Checksum: 3:77b37486b07c1f03cfd6deb096648875)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-185', '2.0.3', '3:77b37486b07c1f03cfd6deb096648875', 185);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-186::destevezg (generated)::(Checksum: 3:7095d16635c991b37fafb2089658c496)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton_liquibase`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-186', '2.0.3', '3:7095d16635c991b37fafb2089658c496', 186);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-187::destevezg (generated)::(Checksum: 3:a4792b0deaf505765bf4262474922390)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-187', '2.0.3', '3:a4792b0deaf505765bf4262474922390', 187);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-188::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `category`(`name`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-188', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 188);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334562618578-189::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `user`(`user`, `authType`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334562618578-189', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 189);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-1::destevezg (generated)::(Checksum: 3:4cc953d671ea64d307e8d8ff11bd6220)
CREATE TABLE `alerts` (`id` CHAR(36) NOT NULL, `type` VARCHAR(60) NOT NULL, `value` VARCHAR(60) NOT NULL, `description` VARCHAR(240), `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_ALERTS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-1', '2.0.3', '3:4cc953d671ea64d307e8d8ff11bd6220', 190);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-2::destevezg (generated)::(Checksum: 3:8e919ec45e59bcb22749fbbb8f8e7731)
CREATE TABLE `apps_library` (`id_apps_library` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, CONSTRAINT `PK_APPS_LIBRARY` PRIMARY KEY (`id_apps_library`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-2', '2.0.3', '3:8e919ec45e59bcb22749fbbb8f8e7731', 191);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-3::destevezg (generated)::(Checksum: 3:3a64a5ee5cd7e25bfab74647244666c9)
CREATE TABLE `auth_group` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), `description` VARCHAR(50), CONSTRAINT `PK_AUTH_GROUP` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-3', '2.0.3', '3:3a64a5ee5cd7e25bfab74647244666c9', 192);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-4::destevezg (generated)::(Checksum: 3:d5a57e91c407bb3e4286f207929d13ce)
CREATE TABLE `auth_serverresource` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50), `description` VARCHAR(100), `idGroup` INT UNSIGNED, `idRole` INT UNSIGNED NOT NULL, CONSTRAINT `PK_AUTH_SERVERRESOURCE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-4', '2.0.3', '3:d5a57e91c407bb3e4286f207929d13ce', 193);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-5::destevezg (generated)::(Checksum: 3:243584dc6bdab87418bfa47b02f212d2)
CREATE TABLE `auth_serverresource_exception` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResource` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_AUTH_SERVERRESOURCE_EXCEPTION` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-5', '2.0.3', '3:243584dc6bdab87418bfa47b02f212d2', 194);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-6::destevezg (generated)::(Checksum: 3:3554f7b0d62138281b7ef681728b8db8)
CREATE TABLE `category` (`idCategory` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(30) NOT NULL, `isErasable` INT UNSIGNED DEFAULT 1 NOT NULL, `isDefault` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CATEGORY` PRIMARY KEY (`idCategory`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-6', '2.0.3', '3:3554f7b0d62138281b7ef681728b8db8', 195);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-7::destevezg (generated)::(Checksum: 3:72c6c8276941ee0ca3af58f3d5763613)
CREATE TABLE `chef_runlist` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVM` INT UNSIGNED NOT NULL, `name` VARCHAR(100) NOT NULL, `description` VARCHAR(255), `priority` INT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CHEF_RUNLIST` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-7', '2.0.3', '3:72c6c8276941ee0ca3af58f3d5763613', 196);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-8::destevezg (generated)::(Checksum: 3:d4aee32b9b22dd9885a219e2b1598aca)
CREATE TABLE `cloud_usage_stats` (`idDataCenter` INT AUTO_INCREMENT NOT NULL, `serversTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `serversRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `storageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMachinesRunning` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vMemoryUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vStorageTotal` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numUsersCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numVDCCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `numEnterprisesCreated` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_CLOUD_USAGE_STATS` PRIMARY KEY (`idDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-8', '2.0.3', '3:d4aee32b9b22dd9885a219e2b1598aca', 197);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-9::destevezg (generated)::(Checksum: 3:009512f1dc1c54949c249a9f9e30851c)
CREATE TABLE `costCode` (`idCostCode` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(20) NOT NULL, `description` VARCHAR(100) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_COSTCODE` PRIMARY KEY (`idCostCode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-9', '2.0.3', '3:009512f1dc1c54949c249a9f9e30851c', 198);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-10::destevezg (generated)::(Checksum: 3:f7106e028d2bcc1b7d43c185c5cbd344)
CREATE TABLE `costCodeCurrency` (`idCostCodeCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCostCode` INT UNSIGNED, `idCurrency` INT UNSIGNED, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_COSTCODECURRENCY` PRIMARY KEY (`idCostCodeCurrency`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-10', '2.0.3', '3:f7106e028d2bcc1b7d43c185c5cbd344', 199);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-11::destevezg (generated)::(Checksum: 3:a0bea615e21fbe63e4ccbd57c305685e)
CREATE TABLE `currency` (`idCurrency` INT UNSIGNED AUTO_INCREMENT NOT NULL, `symbol` VARCHAR(10) NOT NULL, `name` VARCHAR(20) NOT NULL, `digits` INT DEFAULT 2 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_CURRENCY` PRIMARY KEY (`idCurrency`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-11', '2.0.3', '3:a0bea615e21fbe63e4ccbd57c305685e', 200);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-12::destevezg (generated)::(Checksum: 3:d00b2ae80cbcfe78f3a4240bee567ab1)
CREATE TABLE `datacenter` (`idDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40), `name` VARCHAR(20) NOT NULL, `situation` VARCHAR(100), `network_id` INT UNSIGNED, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DATACENTER` PRIMARY KEY (`idDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-12', '2.0.3', '3:d00b2ae80cbcfe78f3a4240bee567ab1', 201);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-13::destevezg (generated)::(Checksum: 3:770c3642229d8388ffa68060c4eb1ece)
CREATE TABLE `datastore` (`idDatastore` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `rootPath` VARCHAR(42) NOT NULL, `directory` VARCHAR(255) NOT NULL, `enabled` BIT DEFAULT 0 NOT NULL, `size` BIGINT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED NOT NULL, `datastoreUuid` VARCHAR(255), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DATASTORE` PRIMARY KEY (`idDatastore`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-13', '2.0.3', '3:770c3642229d8388ffa68060c4eb1ece', 202);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-14::destevezg (generated)::(Checksum: 3:d87d9bdc9646502e4611d02692f8bfee)
CREATE TABLE `datastore_assignment` (`idDatastore` INT UNSIGNED NOT NULL, `idPhysicalMachine` INT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-14', '2.0.3', '3:d87d9bdc9646502e4611d02692f8bfee', 203);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-15::destevezg (generated)::(Checksum: 3:995b2be641bba4dd5bcc7e670a8d73b0)
CREATE TABLE `dc_enterprise_stats` (`idDCEnterpriseStats` INT AUTO_INCREMENT NOT NULL, `idDataCenter` INT NOT NULL, `idEnterprise` INT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_DC_ENTERPRISE_STATS` PRIMARY KEY (`idDCEnterpriseStats`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-15', '2.0.3', '3:995b2be641bba4dd5bcc7e670a8d73b0', 204);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-16::destevezg (generated)::(Checksum: 3:999e74821b6baea6c51b50714b8f70e3)
CREATE TABLE `dhcpOption` (`idDhcpOption` INT UNSIGNED AUTO_INCREMENT NOT NULL, `dhcp_opt` INT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DHCPOPTION` PRIMARY KEY (`idDhcpOption`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-16', '2.0.3', '3:999e74821b6baea6c51b50714b8f70e3', 205);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-17::destevezg (generated)::(Checksum: 3:945b273b2813740dd085b21b2aa00bdb)
CREATE TABLE `disk_management` (`idManagement` INT UNSIGNED NOT NULL, `idDatastore` INT UNSIGNED);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-17', '2.0.3', '3:945b273b2813740dd085b21b2aa00bdb', 206);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-18::destevezg (generated)::(Checksum: 3:cf9410973f7e5511a7dfcbdfeda698d8)
CREATE TABLE `diskstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `imagePath` VARCHAR(256) NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `state` VARCHAR(50) NOT NULL, `convertionTimestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_DISKSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-18', '2.0.3', '3:cf9410973f7e5511a7dfcbdfeda698d8', 207);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-19::destevezg (generated)::(Checksum: 3:fa9f2de4f33f44d9318909dd2ec59752)
CREATE TABLE `enterprise` (`idEnterprise` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `repositorySoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `repositoryHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `chef_url` VARCHAR(255), `chef_client` VARCHAR(50), `chef_validator` VARCHAR(50), `chef_client_certificate` LONGTEXT, `chef_validator_certificate` LONGTEXT, `isReservationRestricted` BIT DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, `idPricingTemplate` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-19', '2.0.3', '3:fa9f2de4f33f44d9318909dd2ec59752', 208);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-20::destevezg (generated)::(Checksum: 3:1bea8c3af51635f6d8205bf9f0d92750)
CREATE TABLE `enterprise_limits_by_datacenter` (`idDatacenterLimit` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED, `idDataCenter` INT UNSIGNED, `ramSoft` BIGINT NOT NULL, `cpuSoft` BIGINT NOT NULL, `hdSoft` BIGINT NOT NULL, `storageSoft` BIGINT NOT NULL, `repositorySoft` BIGINT NOT NULL, `vlanSoft` BIGINT NOT NULL, `publicIPSoft` BIGINT NOT NULL, `ramHard` BIGINT NOT NULL, `cpuHard` BIGINT NOT NULL, `hdHard` BIGINT NOT NULL, `storageHard` BIGINT NOT NULL, `repositoryHard` BIGINT NOT NULL, `vlanHard` BIGINT NOT NULL, `publicIPHard` BIGINT NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `default_vlan_network_id` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_LIMITS_BY_DATACENTER` PRIMARY KEY (`idDatacenterLimit`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-20', '2.0.3', '3:1bea8c3af51635f6d8205bf9f0d92750', 209);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-21::destevezg (generated)::(Checksum: 3:c67606071cfc197cd0d312b346c48f46)
CREATE TABLE `enterprise_properties` (`idProperties` INT UNSIGNED AUTO_INCREMENT NOT NULL, `enterprise` INT UNSIGNED, CONSTRAINT `PK_ENTERPRISE_PROPERTIES` PRIMARY KEY (`idProperties`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-21', '2.0.3', '3:c67606071cfc197cd0d312b346c48f46', 210);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-22::destevezg (generated)::(Checksum: 3:501eb9f341a105a7a8c396cf25b447ce)
CREATE TABLE `enterprise_properties_map` (`enterprise_properties` INT UNSIGNED NOT NULL, `map_key` VARCHAR(30) NOT NULL, `value` VARCHAR(50));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-22', '2.0.3', '3:501eb9f341a105a7a8c396cf25b447ce', 211);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-23::destevezg (generated)::(Checksum: 3:7b6170d7300f139151fca2a735323a3f)
CREATE TABLE `enterprise_resources_stats` (`idEnterprise` INT AUTO_INCREMENT NOT NULL, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `repositoryReserved` BIGINT UNSIGNED DEFAULT 0, `repositoryUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_ENTERPRISE_RESOURCES_STATS` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-23', '2.0.3', '3:7b6170d7300f139151fca2a735323a3f', 212);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-24::destevezg (generated)::(Checksum: 3:e789296b02a08f7c74330907575566d7)
CREATE TABLE `enterprise_theme` (`idEnterprise` INT UNSIGNED NOT NULL, `company_logo_path` LONGTEXT, `theme` LONGTEXT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ENTERPRISE_THEME` PRIMARY KEY (`idEnterprise`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-24', '2.0.3', '3:e789296b02a08f7c74330907575566d7', 213);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-25::destevezg (generated)::(Checksum: 3:14d5f0dd484bd102fdbd77db70853048)
CREATE TABLE `heartbeatlog` (`id` CHAR(36) NOT NULL, `abicloud_id` VARCHAR(60), `client_ip` VARCHAR(16) NOT NULL, `physical_servers` INT NOT NULL, `virtual_machines` INT NOT NULL, `volumes` INT NOT NULL, `virtual_datacenters` INT NOT NULL, `virtual_appliances` INT NOT NULL, `organizations` INT NOT NULL, `total_virtual_cores_allocated` BIGINT NOT NULL, `total_virtual_cores_used` BIGINT NOT NULL, `total_virtual_cores` BIGINT DEFAULT 0 NOT NULL, `total_virtual_memory_allocated` BIGINT NOT NULL, `total_virtual_memory_used` BIGINT NOT NULL, `total_virtual_memory` BIGINT DEFAULT 0 NOT NULL, `total_volume_space_allocated` BIGINT NOT NULL, `total_volume_space_used` BIGINT NOT NULL, `total_volume_space` BIGINT DEFAULT 0 NOT NULL, `virtual_images` BIGINT NOT NULL, `operating_system_name` VARCHAR(60) NOT NULL, `operating_system_version` VARCHAR(60) NOT NULL, `database_name` VARCHAR(60) NOT NULL, `database_version` VARCHAR(60) NOT NULL, `application_server_name` VARCHAR(60) NOT NULL, `application_server_version` VARCHAR(60) NOT NULL, `java_version` VARCHAR(60) NOT NULL, `abicloud_version` VARCHAR(60) NOT NULL, `abicloud_distribution` VARCHAR(60) NOT NULL, `tstamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_HEARTBEATLOG` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-25', '2.0.3', '3:14d5f0dd484bd102fdbd77db70853048', 214);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-26::destevezg (generated)::(Checksum: 3:62b0608bf4fef06b3f26734faeab98d5)
CREATE TABLE `hypervisor` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPhysicalMachine` INT UNSIGNED NOT NULL, `ip` VARCHAR(39) NOT NULL, `ipService` VARCHAR(39) NOT NULL, `port` INT NOT NULL, `user` VARCHAR(255) DEFAULT 'user' NOT NULL, `password` VARCHAR(255) DEFAULT 'password' NOT NULL, `version_c` INT DEFAULT 0, `type` VARCHAR(255) NOT NULL, CONSTRAINT `PK_HYPERVISOR` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-26', '2.0.3', '3:62b0608bf4fef06b3f26734faeab98d5', 215);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-27::destevezg (generated)::(Checksum: 3:df72bc9c11f31390fe38740ca1af2a55)
CREATE TABLE `initiator_mapping` (`idInitiatorMapping` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idManagement` INT UNSIGNED NOT NULL, `initiatorIqn` VARCHAR(256) NOT NULL, `targetIqn` VARCHAR(256) NOT NULL, `targetLun` INT NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_INITIATOR_MAPPING` PRIMARY KEY (`idInitiatorMapping`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-27', '2.0.3', '3:df72bc9c11f31390fe38740ca1af2a55', 216);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-28::destevezg (generated)::(Checksum: 3:5c602742fbd5483cb90d5f1c48650406)
CREATE TABLE `ip_pool_management` (`idManagement` INT UNSIGNED NOT NULL, `mac` VARCHAR(20), `name` VARCHAR(30), `ip` VARCHAR(20) NOT NULL, `vlan_network_name` VARCHAR(40), `vlan_network_id` INT UNSIGNED, `quarantine` BIT DEFAULT 0 NOT NULL, `available` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-28', '2.0.3', '3:5c602742fbd5483cb90d5f1c48650406', 217);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-29::destevezg (generated)::(Checksum: 3:9acd63c1202d04d062e417c615a6fa63)
CREATE TABLE `license` (`idLicense` INT AUTO_INCREMENT NOT NULL, `data` VARCHAR(1000) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_LICENSE` PRIMARY KEY (`idLicense`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-29', '2.0.3', '3:9acd63c1202d04d062e417c615a6fa63', 218);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-30::destevezg (generated)::(Checksum: 3:cba5489b99643adbaca75913c0f65003)
CREATE TABLE `log` (`idLog` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `description` VARCHAR(250) NOT NULL, `logDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `deleted` BIT DEFAULT 0, CONSTRAINT `PK_LOG` PRIMARY KEY (`idLog`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-30', '2.0.3', '3:cba5489b99643adbaca75913c0f65003', 219);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-31::destevezg (generated)::(Checksum: 3:610191e4c6c085272041ab93b7a4bd88)
CREATE TABLE `metering` (`idMeter` BIGINT UNSIGNED AUTO_INCREMENT NOT NULL, `idDatacenter` INT UNSIGNED, `datacenter` VARCHAR(20), `idRack` INT UNSIGNED, `rack` VARCHAR(20), `idPhysicalMachine` INT UNSIGNED, `physicalmachine` VARCHAR(256), `idStorageSystem` INT UNSIGNED, `storageSystem` VARCHAR(256), `idStoragePool` VARCHAR(40), `storagePool` VARCHAR(256), `idVolume` VARCHAR(50), `volume` VARCHAR(256), `idNetwork` INT UNSIGNED, `network` VARCHAR(256), `idSubnet` INT UNSIGNED, `subnet` VARCHAR(256), `idEnterprise` INT UNSIGNED, `enterprise` VARCHAR(40), `idUser` INT UNSIGNED, `user` VARCHAR(128), `idVirtualDataCenter` INT UNSIGNED, `virtualDataCenter` VARCHAR(40), `idVirtualApp` INT UNSIGNED, `virtualApp` VARCHAR(30), `idVirtualMachine` INT UNSIGNED, `virtualmachine` VARCHAR(256), `severity` VARCHAR(100) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `performedby` VARCHAR(255) NOT NULL, `actionperformed` VARCHAR(100) NOT NULL, `component` VARCHAR(255), `stacktrace` LONGTEXT, CONSTRAINT `PK_METERING` PRIMARY KEY (`idMeter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-31', '2.0.3', '3:610191e4c6c085272041ab93b7a4bd88', 220);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-32::destevezg (generated)::(Checksum: 3:acc689e893485790d347e737a96a3812)
CREATE TABLE `network` (`network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uuid` VARCHAR(40) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK` PRIMARY KEY (`network_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-32', '2.0.3', '3:acc689e893485790d347e737a96a3812', 221);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-33::destevezg (generated)::(Checksum: 3:2f9869de52cfc735802b2954900a0ebe)
CREATE TABLE `network_configuration` (`network_configuration_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `gateway` VARCHAR(40), `network_address` VARCHAR(40) NOT NULL, `mask` INT NOT NULL, `netmask` VARCHAR(20) NOT NULL, `primary_dns` VARCHAR(20), `secondary_dns` VARCHAR(20), `sufix_dns` VARCHAR(40), `fence_mode` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_NETWORK_CONFIGURATION` PRIMARY KEY (`network_configuration_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-33', '2.0.3', '3:2f9869de52cfc735802b2954900a0ebe', 222);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-34::destevezg (generated)::(Checksum: 3:535f2e3555ed12cf15a708e1e9028ace)
CREATE TABLE `node` (`idVirtualApp` INT UNSIGNED NOT NULL, `idNode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `modified` INT NOT NULL, `posX` INT DEFAULT 0 NOT NULL, `posY` INT DEFAULT 0 NOT NULL, `type` VARCHAR(50) NOT NULL, `name` VARCHAR(255) NOT NULL, `ip` VARCHAR(15), `mac` VARCHAR(17), `version_c` INT DEFAULT 0, CONSTRAINT `PK_NODE` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-34', '2.0.3', '3:535f2e3555ed12cf15a708e1e9028ace', 223);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-35::destevezg (generated)::(Checksum: 3:19a67fc950837b5fb2e10098cc45749f)
CREATE TABLE `node_virtual_image_stateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `newName` VARCHAR(255) NOT NULL, `idVirtualApplianceStatefulConversion` INT UNSIGNED NOT NULL, `idNodeVirtualImage` INT UNSIGNED NOT NULL, `idVirtualImageConversion` INT UNSIGNED, `idDiskStatefulConversion` INT UNSIGNED, `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `version_c` INT DEFAULT 0, `idTier` INT UNSIGNED NOT NULL, `idManagement` INT UNSIGNED, CONSTRAINT `PK_NODE_VIRTUAL_IMAGE_STATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-35', '2.0.3', '3:19a67fc950837b5fb2e10098cc45749f', 224);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-36::destevezg (generated)::(Checksum: 3:5cecdb934194d6b6c4c52d5ddafab8a4)
CREATE TABLE `nodenetwork` (`idNode` INT UNSIGNED NOT NULL, CONSTRAINT `PK_NODENETWORK` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-36', '2.0.3', '3:5cecdb934194d6b6c4c52d5ddafab8a4', 225);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-37::destevezg (generated)::(Checksum: 3:98d35e5d1c7727e5a3a97a39ba856315)
CREATE TABLE `noderelationtype` (`idNodeRelationType` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(20), CONSTRAINT `PK_NODERELATIONTYPE` PRIMARY KEY (`idNodeRelationType`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-37', '2.0.3', '3:98d35e5d1c7727e5a3a97a39ba856315', 226);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-38::destevezg (generated)::(Checksum: 3:9874aabd5a932cf4ac5e4c3c2a8518fb)
CREATE TABLE `nodestorage` (`idNode` INT UNSIGNED DEFAULT 0 NOT NULL, CONSTRAINT `PK_NODESTORAGE` PRIMARY KEY (`idNode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-38', '2.0.3', '3:9874aabd5a932cf4ac5e4c3c2a8518fb', 227);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-39::destevezg (generated)::(Checksum: 3:b7aaa890a910a7d749e9aef4186127d6)
CREATE TABLE `nodevirtualimage` (`idNode` INT UNSIGNED NOT NULL, `idVM` INT UNSIGNED, `idImage` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-39', '2.0.3', '3:b7aaa890a910a7d749e9aef4186127d6', 228);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-40::destevezg (generated)::(Checksum: 3:4eb9af1e026910fc2b502b482d337bd3)
CREATE TABLE `one_time_token` (`idOneTimeTokenSession` INT UNSIGNED AUTO_INCREMENT NOT NULL, `token` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ONE_TIME_TOKEN` PRIMARY KEY (`idOneTimeTokenSession`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-40', '2.0.3', '3:4eb9af1e026910fc2b502b482d337bd3', 229);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-41::destevezg (generated)::(Checksum: 3:99947b2f6c92a85be95a29e0e2c8fcd5)
CREATE TABLE `ovf_package` (`id_ovf_package` INT AUTO_INCREMENT NOT NULL, `id_apps_library` INT UNSIGNED NOT NULL, `url` VARCHAR(255) NOT NULL, `name` VARCHAR(255), `description` VARCHAR(255), `iconUrl` VARCHAR(255), `productName` VARCHAR(255), `productUrl` VARCHAR(45), `productVersion` VARCHAR(45), `productVendor` VARCHAR(45), `idCategory` INT UNSIGNED, `diskSizeMb` BIGINT, `version_c` INT DEFAULT 0, `type` VARCHAR(50) NOT NULL, CONSTRAINT `PK_OVF_PACKAGE` PRIMARY KEY (`id_ovf_package`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-41', '2.0.3', '3:99947b2f6c92a85be95a29e0e2c8fcd5', 230);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-42::destevezg (generated)::(Checksum: 3:0c91c376e5e100ecc9c43349cf25a5be)
CREATE TABLE `ovf_package_list` (`id_ovf_package_list` INT AUTO_INCREMENT NOT NULL, `name` VARCHAR(45) NOT NULL, `url` VARCHAR(255), `id_apps_library` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_OVF_PACKAGE_LIST` PRIMARY KEY (`id_ovf_package_list`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-42', '2.0.3', '3:0c91c376e5e100ecc9c43349cf25a5be', 231);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-43::destevezg (generated)::(Checksum: 3:07487550844d3ed2ae36327bbacfa706)
CREATE TABLE `ovf_package_list_has_ovf_package` (`id_ovf_package_list` INT NOT NULL, `id_ovf_package` INT NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-43', '2.0.3', '3:07487550844d3ed2ae36327bbacfa706', 232);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-44::destevezg (generated)::(Checksum: 3:14c0e5b90db5b5a98f63d102a4648fcb)
CREATE TABLE `physicalmachine` (`idPhysicalMachine` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRack` INT UNSIGNED, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `description` VARCHAR(100), `ram` INT NOT NULL, `cpu` INT NOT NULL, `ramUsed` INT NOT NULL, `cpuUsed` INT NOT NULL, `idState` INT UNSIGNED DEFAULT 0 NOT NULL, `vswitchName` VARCHAR(200) NOT NULL, `idEnterprise` INT UNSIGNED, `initiatorIQN` VARCHAR(256), `version_c` INT DEFAULT 0, `ipmiIP` VARCHAR(39), `ipmiPort` INT UNSIGNED, `ipmiUser` VARCHAR(255), `ipmiPassword` VARCHAR(255), CONSTRAINT `PK_PHYSICALMACHINE` PRIMARY KEY (`idPhysicalMachine`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-44', '2.0.3', '3:14c0e5b90db5b5a98f63d102a4648fcb', 233);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-45::destevezg (generated)::(Checksum: 3:9f40d797ba27e2b65f19758f5e186305)
CREATE TABLE `pricingCostCode` (`idPricingCostCode` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idCostCode` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGCOSTCODE` PRIMARY KEY (`idPricingCostCode`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-45', '2.0.3', '3:9f40d797ba27e2b65f19758f5e186305', 234);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-46::destevezg (generated)::(Checksum: 3:ab6e2631515ddb106be9b4d6d3531501)
CREATE TABLE `pricingTemplate` (`idPricingTemplate` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idCurrency` INT UNSIGNED NOT NULL, `name` VARCHAR(256) NOT NULL, `chargingPeriod` INT UNSIGNED NOT NULL, `minimumCharge` INT UNSIGNED NOT NULL, `showChangesBefore` BIT DEFAULT 0 NOT NULL, `standingChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `minimumChargePeriod` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vcpu` DECIMAL(20,5) DEFAULT 0 NOT NULL, `memoryMB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `hdGB` DECIMAL(20,5) DEFAULT 0 NOT NULL, `vlan` DECIMAL(20,5) DEFAULT 0 NOT NULL, `publicIp` DECIMAL(20,5) DEFAULT 0 NOT NULL, `defaultTemplate` BIT DEFAULT 0 NOT NULL, `description` VARCHAR(1000) NOT NULL, `last_update` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTEMPLATE` PRIMARY KEY (`idPricingTemplate`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-46', '2.0.3', '3:ab6e2631515ddb106be9b4d6d3531501', 235);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-47::destevezg (generated)::(Checksum: 3:7e35bf44f08c5d52cc2ab45d6b3bbbc7)
CREATE TABLE `pricingTier` (`idPricingTier` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idPricingTemplate` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `price` DECIMAL(20,5) DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRICINGTIER` PRIMARY KEY (`idPricingTier`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-47', '2.0.3', '3:7e35bf44f08c5d52cc2ab45d6b3bbbc7', 236);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-48::destevezg (generated)::(Checksum: 3:c6d5853d53098ca1973d73422a43f280)
CREATE TABLE `privilege` (`idPrivilege` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(50) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_PRIVILEGE` PRIMARY KEY (`idPrivilege`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-48', '2.0.3', '3:c6d5853d53098ca1973d73422a43f280', 237);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-49::destevezg (generated)::(Checksum: 3:f985977e5664c01a97db84ad82897d32)
CREATE TABLE `rack` (`idRack` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(20) NOT NULL, `shortDescription` VARCHAR(30), `largeDescription` VARCHAR(100), `vlan_id_min` INT UNSIGNED DEFAULT 2, `vlan_id_max` INT UNSIGNED DEFAULT 4094, `vlans_id_avoided` VARCHAR(255) DEFAULT '', `vlan_per_vdc_expected` INT UNSIGNED DEFAULT 8, `nrsq` INT UNSIGNED DEFAULT 10, `haEnabled` BIT DEFAULT 0, `version_c` INT DEFAULT 0, CONSTRAINT `PK_RACK` PRIMARY KEY (`idRack`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-49', '2.0.3', '3:f985977e5664c01a97db84ad82897d32', 238);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-50::destevezg (generated)::(Checksum: 3:0aa39e690fa3b13b6bce812e7904ce34)
CREATE TABLE `rasd` (`address` VARCHAR(256), `addressOnParent` VARCHAR(25), `allocationUnits` VARCHAR(15), `automaticAllocation` INT, `automaticDeallocation` INT, `caption` VARCHAR(15), `changeableType` INT, `configurationName` VARCHAR(15), `connectionResource` VARCHAR(256), `consumerVisibility` INT, `description` VARCHAR(255), `elementName` VARCHAR(255) NOT NULL, `generation` BIGINT, `hostResource` VARCHAR(256), `instanceID` VARCHAR(50) NOT NULL, `limitResource` BIGINT, `mappingBehaviour` INT, `otherResourceType` VARCHAR(50), `parent` VARCHAR(50), `poolID` VARCHAR(50), `reservation` BIGINT, `resourceSubType` VARCHAR(15), `resourceType` INT NOT NULL, `virtualQuantity` INT, `weight` INT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_RASD` PRIMARY KEY (`instanceID`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-50', '2.0.3', '3:0aa39e690fa3b13b6bce812e7904ce34', 239);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-51::destevezg (generated)::(Checksum: 3:040f538d8873944d6be77ba148f6400f)
CREATE TABLE `rasd_management` (`idManagement` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idResourceType` VARCHAR(5) NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `idVM` INT UNSIGNED, `idResource` VARCHAR(50), `idVirtualApp` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, `temporal` INT UNSIGNED, `sequence` INT UNSIGNED, CONSTRAINT `PK_RASD_MANAGEMENT` PRIMARY KEY (`idManagement`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-51', '2.0.3', '3:040f538d8873944d6be77ba148f6400f', 240);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-52::destevezg (generated)::(Checksum: 3:e007dec4c46888665dd0bc6d5b5fbfe9)
CREATE TABLE `register` (`id` CHAR(36) NOT NULL, `company_name` VARCHAR(60) NOT NULL, `company_address` VARCHAR(240) NOT NULL, `company_state` VARCHAR(60) NOT NULL, `company_country_code` VARCHAR(2) NOT NULL, `company_industry` VARCHAR(255), `contact_title` VARCHAR(60) NOT NULL, `contact_name` VARCHAR(60) NOT NULL, `contact_email` VARCHAR(60) NOT NULL, `contact_phone` VARCHAR(60) NOT NULL, `company_size_revenue` VARCHAR(60) NOT NULL, `company_size_employees` VARCHAR(60) NOT NULL, `subscribe_development_news` BIT DEFAULT 0 NOT NULL, `subscribe_commercial_news` BIT DEFAULT 0 NOT NULL, `allow_commercial_contact` BIT DEFAULT 0 NOT NULL, `creation_date` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, CONSTRAINT `PK_REGISTER` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-52', '2.0.3', '3:e007dec4c46888665dd0bc6d5b5fbfe9', 241);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-53::destevezg (generated)::(Checksum: 3:7011c0d44a8b73f84a1c92f95dc2fede)
CREATE TABLE `remote_service` (`idRemoteService` INT UNSIGNED AUTO_INCREMENT NOT NULL, `uri` VARCHAR(255) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `status` INT UNSIGNED DEFAULT 0 NOT NULL, `remoteServiceType` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REMOTE_SERVICE` PRIMARY KEY (`idRemoteService`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-53', '2.0.3', '3:7011c0d44a8b73f84a1c92f95dc2fede', 242);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-54::destevezg (generated)::(Checksum: 3:71b499bb915394af534df15335b9daed)
CREATE TABLE `repository` (`idRepository` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `name` VARCHAR(30), `URL` VARCHAR(255) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_REPOSITORY` PRIMARY KEY (`idRepository`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-54', '2.0.3', '3:71b499bb915394af534df15335b9daed', 243);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-55::destevezg (generated)::(Checksum: 3:ee8d877be94ca46b1c1c98fa757f26e0)
CREATE TABLE `role` (`idRole` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) DEFAULT 'auto_name' NOT NULL, `idEnterprise` INT UNSIGNED, `blocked` BIT DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE` PRIMARY KEY (`idRole`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-55', '2.0.3', '3:ee8d877be94ca46b1c1c98fa757f26e0', 244);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-56::destevezg (generated)::(Checksum: 3:edf01fe80f59ef0f259fc68dcd83d5fe)
CREATE TABLE `role_ldap` (`idRole_ldap` INT AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `role_ldap` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ROLE_LDAP` PRIMARY KEY (`idRole_ldap`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-56', '2.0.3', '3:edf01fe80f59ef0f259fc68dcd83d5fe', 245);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-57::destevezg (generated)::(Checksum: 3:cc062a9e4826b59f11c8365ac69e95bf)
CREATE TABLE `roles_privileges` (`idRole` INT UNSIGNED NOT NULL, `idPrivilege` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-57', '2.0.3', '3:cc062a9e4826b59f11c8365ac69e95bf', 246);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-58::destevezg (generated)::(Checksum: 3:8920e001739682f8d40c928a7a728cf0)
CREATE TABLE `session` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `user` VARCHAR(128) NOT NULL, `key` VARCHAR(100) NOT NULL, `expireDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `idUser` INT UNSIGNED, `authType` VARCHAR(20) NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_SESSION` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-58', '2.0.3', '3:8920e001739682f8d40c928a7a728cf0', 247);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-59::destevezg (generated)::(Checksum: 3:57ba11cd0200671863a484a509c0ebd4)
CREATE TABLE `storage_device` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(256) NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `management_ip` VARCHAR(256) NOT NULL, `management_port` INT UNSIGNED DEFAULT 0 NOT NULL, `iscsi_ip` VARCHAR(256) NOT NULL, `iscsi_port` INT UNSIGNED DEFAULT 0 NOT NULL, `storage_technology` VARCHAR(256), `username` VARCHAR(256), `password` VARCHAR(256), `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_STORAGE_DEVICE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-59', '2.0.3', '3:57ba11cd0200671863a484a509c0ebd4', 248);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-60::destevezg (generated)::(Checksum: 3:43028542c71486175e6524c22aef86ca)
CREATE TABLE `storage_pool` (`idStorage` VARCHAR(40) NOT NULL, `idStorageDevice` INT UNSIGNED NOT NULL, `idTier` INT UNSIGNED NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `totalSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `usedSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `availableSizeInMb` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `name` VARCHAR(256), CONSTRAINT `PK_STORAGE_POOL` PRIMARY KEY (`idStorage`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-60', '2.0.3', '3:43028542c71486175e6524c22aef86ca', 249);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-61::destevezg (generated)::(Checksum: 3:4c03a0fbca76cfad7a60af4a6e47a4ef)
CREATE TABLE `system_properties` (`systemPropertyId` INT UNSIGNED AUTO_INCREMENT NOT NULL, `version_c` INT DEFAULT 0, `name` VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, `description` VARCHAR(255), CONSTRAINT `PK_SYSTEM_PROPERTIES` PRIMARY KEY (`systemPropertyId`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-61', '2.0.3', '3:4c03a0fbca76cfad7a60af4a6e47a4ef', 250);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-62::destevezg (generated)::(Checksum: 3:fd64da3920543e4ceaf993a73f88d28e)
CREATE TABLE `tasks` (`id` INT AUTO_INCREMENT NOT NULL, `status` VARCHAR(20) NOT NULL, `component` VARCHAR(20) NOT NULL, `action` VARCHAR(20) NOT NULL, CONSTRAINT `PK_TASKS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-62', '2.0.3', '3:fd64da3920543e4ceaf993a73f88d28e', 251);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-63::destevezg (generated)::(Checksum: 3:fde7583a3eacc481d6bc111205304a80)
CREATE TABLE `tier` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(40) NOT NULL, `description` VARCHAR(255) NOT NULL, `isEnabled` BIT DEFAULT 1 NOT NULL, `idDataCenter` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_TIER` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-63', '2.0.3', '3:fde7583a3eacc481d6bc111205304a80', 252);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-64::destevezg (generated)::(Checksum: 3:1b0a3cb74ec9cb7c8117dd68a60414b3)
CREATE TABLE `ucs_rack` (`idRack` INT UNSIGNED NOT NULL, `ip` VARCHAR(20) NOT NULL, `port` INT NOT NULL, `user_rack` VARCHAR(255) NOT NULL, `password` VARCHAR(255) NOT NULL, `defaultTemplate` VARCHAR(200), `maxMachinesOn` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-64', '2.0.3', '3:1b0a3cb74ec9cb7c8117dd68a60414b3', 253);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-65::destevezg (generated)::(Checksum: 3:80e11ead54c2de53edbc76d1bcc539f0)
CREATE TABLE `user` (`idUser` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idRole` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `user` VARCHAR(128) NOT NULL, `name` VARCHAR(128) NOT NULL, `surname` VARCHAR(50), `description` VARCHAR(100), `email` VARCHAR(200), `locale` VARCHAR(10) NOT NULL, `password` VARCHAR(32), `availableVirtualDatacenters` VARCHAR(255), `active` INT UNSIGNED DEFAULT 0 NOT NULL, `authType` VARCHAR(20) NOT NULL, `creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_USER` PRIMARY KEY (`idUser`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-65', '2.0.3', '3:80e11ead54c2de53edbc76d1bcc539f0', 254);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-66::destevezg (generated)::(Checksum: 3:2899827cf866dbf4c04b6a367b546af3)
CREATE TABLE `vapp_enterprise_stats` (`idVirtualApp` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `idVirtualDataCenter` INT NOT NULL, `vappName` VARCHAR(45), `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VAPP_ENTERPRISE_STATS` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-66', '2.0.3', '3:2899827cf866dbf4c04b6a367b546af3', 255);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-67::destevezg (generated)::(Checksum: 3:4854d0683726d2b8e23e8c58a77248bd)
CREATE TABLE `vappstateful_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualApp` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VAPPSTATEFUL_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-67', '2.0.3', '3:4854d0683726d2b8e23e8c58a77248bd', 256);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-68::destevezg (generated)::(Checksum: 3:aecbcce0078b6d04274190ba65cfca54)
CREATE TABLE `vdc_enterprise_stats` (`idVirtualDataCenter` INT AUTO_INCREMENT NOT NULL, `idEnterprise` INT NOT NULL, `vdcName` VARCHAR(45), `vmCreated` MEDIUMINT UNSIGNED DEFAULT 0, `vmActive` MEDIUMINT UNSIGNED DEFAULT 0, `volCreated` MEDIUMINT UNSIGNED DEFAULT 0, `volAssociated` MEDIUMINT UNSIGNED DEFAULT 0, `volAttached` MEDIUMINT UNSIGNED DEFAULT 0, `vCpuReserved` BIGINT UNSIGNED DEFAULT 0, `vCpuUsed` BIGINT UNSIGNED DEFAULT 0, `memoryReserved` BIGINT UNSIGNED DEFAULT 0, `memoryUsed` BIGINT UNSIGNED DEFAULT 0, `localStorageReserved` BIGINT UNSIGNED DEFAULT 0, `localStorageUsed` BIGINT UNSIGNED DEFAULT 0, `extStorageReserved` BIGINT UNSIGNED DEFAULT 0, `extStorageUsed` BIGINT UNSIGNED DEFAULT 0, `publicIPsReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `publicIPsUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanReserved` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `vlanUsed` MEDIUMINT UNSIGNED DEFAULT 0 NOT NULL, `version_c` INT DEFAULT 1 NOT NULL);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-68', '2.0.3', '3:aecbcce0078b6d04274190ba65cfca54', 257);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-69::destevezg (generated)::(Checksum: 3:030a2622524d2284c305f928bb82368b)
CREATE TABLE `virtual_appliance_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idConversion` INT UNSIGNED NOT NULL, `idVirtualAppliance` INT UNSIGNED NOT NULL, `idUser` INT UNSIGNED, `forceLimits` BIT, `idNode` INT UNSIGNED, CONSTRAINT `PK_VIRTUAL_APPLIANCE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-69', '2.0.3', '3:030a2622524d2284c305f928bb82368b', 258);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-70::destevezg (generated)::(Checksum: 3:32b825452e11bcbd8ee3dd1ef1e24032)
CREATE TABLE `virtualapp` (`idVirtualApp` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idEnterprise` INT UNSIGNED, `name` VARCHAR(30) NOT NULL, `public` INT UNSIGNED NOT NULL, `high_disponibility` INT UNSIGNED NOT NULL, `error` INT UNSIGNED NOT NULL, `nodeconnections` LONGTEXT, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALAPP` PRIMARY KEY (`idVirtualApp`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-70', '2.0.3', '3:32b825452e11bcbd8ee3dd1ef1e24032', 259);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-71::destevezg (generated)::(Checksum: 3:d14e8e7996c68a1b23e487fd9fdca756)
CREATE TABLE `virtualdatacenter` (`idVirtualDataCenter` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `name` VARCHAR(40), `idDataCenter` INT UNSIGNED NOT NULL, `networktypeID` INT UNSIGNED, `hypervisorType` VARCHAR(255) NOT NULL, `ramSoft` BIGINT DEFAULT 0 NOT NULL, `cpuSoft` BIGINT DEFAULT 0 NOT NULL, `hdSoft` BIGINT DEFAULT 0 NOT NULL, `storageSoft` BIGINT DEFAULT 0 NOT NULL, `vlanSoft` BIGINT DEFAULT 0 NOT NULL, `publicIPSoft` BIGINT DEFAULT 0 NOT NULL, `ramHard` BIGINT DEFAULT 0 NOT NULL, `cpuHard` BIGINT DEFAULT 0 NOT NULL, `hdHard` BIGINT DEFAULT 0 NOT NULL, `storageHard` BIGINT DEFAULT 0 NOT NULL, `vlanHard` BIGINT DEFAULT 0 NOT NULL, `publicIPHard` BIGINT DEFAULT 0 NOT NULL, `default_vlan_network_id` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALDATACENTER` PRIMARY KEY (`idVirtualDataCenter`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-71', '2.0.3', '3:d14e8e7996c68a1b23e487fd9fdca756', 260);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-72::destevezg (generated)::(Checksum: 3:58a1a21cb6b4cc9c516ba7f816580129)
CREATE TABLE `virtualimage` (`idImage` INT UNSIGNED AUTO_INCREMENT NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `pathName` VARCHAR(255) NOT NULL, `hd_required` BIGINT, `ram_required` INT UNSIGNED, `cpu_required` INT, `iconUrl` VARCHAR(255), `idCategory` INT UNSIGNED NOT NULL, `idRepository` INT UNSIGNED, `type` VARCHAR(50) NOT NULL, `ethDriverType` VARCHAR(16), `idMaster` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `shared` INT UNSIGNED DEFAULT 0 NOT NULL, `ovfid` VARCHAR(255), `stateful` INT UNSIGNED NOT NULL, `diskFileSize` BIGINT UNSIGNED NOT NULL, `chefEnabled` BIT DEFAULT 0 NOT NULL, `cost_code` INT DEFAULT 0, `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `creation_user` VARCHAR(128) NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VIRTUALIMAGE` PRIMARY KEY (`idImage`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-72', '2.0.3', '3:58a1a21cb6b4cc9c516ba7f816580129', 261);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-73::destevezg (generated)::(Checksum: 3:d3114ad9be523f3c185c3cbbcbfc042d)
CREATE TABLE `virtualimage_conversions` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idImage` INT UNSIGNED NOT NULL, `sourceType` VARCHAR(50), `targetType` VARCHAR(50) NOT NULL, `sourcePath` VARCHAR(255), `targetPath` VARCHAR(255) NOT NULL, `state` VARCHAR(50) NOT NULL, `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, `size` BIGINT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALIMAGE_CONVERSIONS` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-73', '2.0.3', '3:d3114ad9be523f3c185c3cbbcbfc042d', 262);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-74::destevezg (generated)::(Checksum: 3:53696a97c6c3b0bc834e7bade31af1ae)
CREATE TABLE `virtualmachine` (`idVM` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idHypervisor` INT UNSIGNED, `idImage` INT UNSIGNED, `UUID` VARCHAR(36) NOT NULL, `name` VARCHAR(255) NOT NULL, `description` VARCHAR(255), `ram` INT UNSIGNED, `cpu` INT UNSIGNED, `hd` BIGINT UNSIGNED, `vdrpPort` INT UNSIGNED, `vdrpIP` VARCHAR(39), `state` VARCHAR(50) NOT NULL, `subState` VARCHAR(50), `high_disponibility` INT UNSIGNED NOT NULL, `idConversion` INT UNSIGNED, `idType` INT UNSIGNED DEFAULT 0 NOT NULL, `idUser` INT UNSIGNED, `idEnterprise` INT UNSIGNED, `idDatastore` INT UNSIGNED, `password` VARCHAR(32), `network_configuration_id` INT UNSIGNED, `temporal` INT UNSIGNED, `ethDriverType` VARCHAR(16), `version_c` INT DEFAULT 0, CONSTRAINT `PK_VIRTUALMACHINE` PRIMARY KEY (`idVM`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-74', '2.0.3', '3:53696a97c6c3b0bc834e7bade31af1ae', 263);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-75::destevezg (generated)::(Checksum: 3:a7be54650882a268059c959a6a5ff8bd)
CREATE TABLE `virtualmachinetrackedstate` (`idVM` INT UNSIGNED NOT NULL, `previousState` VARCHAR(50) NOT NULL, CONSTRAINT `PK_VIRTUALMACHINETRACKEDSTATE` PRIMARY KEY (`idVM`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-75', '2.0.3', '3:a7be54650882a268059c959a6a5ff8bd', 264);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-76::destevezg (generated)::(Checksum: 3:01e3a3b9f3ad7580991cc4d4e57ebf42)
CREATE TABLE `vlan_network` (`vlan_network_id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `network_id` INT UNSIGNED NOT NULL, `network_configuration_id` INT UNSIGNED NOT NULL, `network_name` VARCHAR(40) NOT NULL, `vlan_tag` INT UNSIGNED, `networktype` VARCHAR(15) DEFAULT 'INTERNAL' NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, `enterprise_id` INT UNSIGNED, CONSTRAINT `PK_VLAN_NETWORK` PRIMARY KEY (`vlan_network_id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-76', '2.0.3', '3:01e3a3b9f3ad7580991cc4d4e57ebf42', 265);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-77::destevezg (generated)::(Checksum: 3:9c485c100f6a82db157f2531065bde6b)
CREATE TABLE `vlan_network_assignment` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `vlan_network_id` INT UNSIGNED NOT NULL, `idRack` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_VLAN_NETWORK_ASSIGNMENT` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-77', '2.0.3', '3:9c485c100f6a82db157f2531065bde6b', 266);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-78::destevezg (generated)::(Checksum: 3:4f4b8d61f5c02732aa645bbe302b2e0b)
CREATE TABLE `vlans_dhcpOption` (`idVlan` INT UNSIGNED NOT NULL, `idDhcpOption` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-78', '2.0.3', '3:4f4b8d61f5c02732aa645bbe302b2e0b', 267);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-79::destevezg (generated)::(Checksum: 3:1d827e78ada3e840729ac9b5875a8de6)
CREATE TABLE `volume_management` (`idManagement` INT UNSIGNED NOT NULL, `usedSize` BIGINT UNSIGNED DEFAULT 0 NOT NULL, `idSCSI` VARCHAR(256) NOT NULL, `state` INT NOT NULL, `idStorage` VARCHAR(40) NOT NULL, `idImage` INT UNSIGNED, `version_c` INT DEFAULT 0);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-79', '2.0.3', '3:1d827e78ada3e840729ac9b5875a8de6', 268);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-80::destevezg (generated)::(Checksum: 3:5f584d6eab4addc350d1e9d38a26a273)
CREATE TABLE `workload_enterprise_exclusion_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `idEnterprise1` INT UNSIGNED NOT NULL, `idEnterprise2` INT UNSIGNED NOT NULL, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_ENTERPRISE_EXCLUSION_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-80', '2.0.3', '3:5f584d6eab4addc350d1e9d38a26a273', 269);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-81::destevezg (generated)::(Checksum: 3:6b95206f2f58f850e794848fd3f59911)
CREATE TABLE `workload_fit_policy_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `fitPolicy` VARCHAR(20) NOT NULL, `idDatacenter` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_FIT_POLICY_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-81', '2.0.3', '3:6b95206f2f58f850e794848fd3f59911', 270);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-82::destevezg (generated)::(Checksum: 3:71036d19125d40af990eb553c437374e)
CREATE TABLE `workload_machine_load_rule` (`id` INT UNSIGNED AUTO_INCREMENT NOT NULL, `ramLoadPercentage` INT UNSIGNED NOT NULL, `cpuLoadPercentage` INT UNSIGNED NOT NULL, `idDatacenter` INT UNSIGNED, `idRack` INT UNSIGNED, `idMachine` INT UNSIGNED, `version_c` INT DEFAULT 1 NOT NULL, CONSTRAINT `PK_WORKLOAD_MACHINE_LOAD_RULE` PRIMARY KEY (`id`));

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-82', '2.0.3', '3:71036d19125d40af990eb553c437374e', 271);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-83::destevezg (generated)::(Checksum: 3:aa74d712d9cfccf4c578872a99fa0e59)
ALTER TABLE `datastore_assignment` ADD PRIMARY KEY (`idDatastore`, `idPhysicalMachine`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-83', '2.0.3', '3:aa74d712d9cfccf4c578872a99fa0e59', 272);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-84::destevezg (generated)::(Checksum: 3:22e25d11ab6124ead2cbb6fde07eeb66)
ALTER TABLE `ovf_package_list_has_ovf_package` ADD PRIMARY KEY (`id_ovf_package_list`, `id_ovf_package`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-84', '2.0.3', '3:22e25d11ab6124ead2cbb6fde07eeb66', 273);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-85::destevezg (generated)::(Checksum: 3:2dd4badadcd15f6378a42b518d5aab69)
ALTER TABLE `vdc_enterprise_stats` ADD PRIMARY KEY (`idVirtualDataCenter`, `idEnterprise`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Primary Key', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-85', '2.0.3', '3:2dd4badadcd15f6378a42b518d5aab69', 274);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-86::destevezg (generated)::(Checksum: 3:c99e6608c0f45bf70433a743f80d8992)
ALTER TABLE `kinton_liquibase`.`apps_library` ADD CONSTRAINT `fk_idEnterpriseApps` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-86', '2.0.3', '3:c99e6608c0f45bf70433a743f80d8992', 275);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-87::destevezg (generated)::(Checksum: 3:76f80741e3a196a8595c4df2a2cb1a4a)
ALTER TABLE `kinton_liquibase`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK1` FOREIGN KEY (`idGroup`) REFERENCES `kinton_liquibase`.`auth_group` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-87', '2.0.3', '3:76f80741e3a196a8595c4df2a2cb1a4a', 276);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-88::destevezg (generated)::(Checksum: 3:3b937104469a886e54beafe1459cb772)
ALTER TABLE `kinton_liquibase`.`auth_serverresource` ADD CONSTRAINT `auth_serverresourceFK2` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-88', '2.0.3', '3:3b937104469a886e54beafe1459cb772', 277);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-89::destevezg (generated)::(Checksum: 3:212f38afd8fe18b74c7196e14ce66a28)
ALTER TABLE `kinton_liquibase`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK1` FOREIGN KEY (`idResource`) REFERENCES `kinton_liquibase`.`auth_serverresource` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-89', '2.0.3', '3:212f38afd8fe18b74c7196e14ce66a28', 278);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-90::destevezg (generated)::(Checksum: 3:8409beebcfd4a6398dc4c64a6beaa2bb)
ALTER TABLE `kinton_liquibase`.`auth_serverresource_exception` ADD CONSTRAINT `auth_serverresource_exceptionFK2` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-90', '2.0.3', '3:8409beebcfd4a6398dc4c64a6beaa2bb', 279);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-91::destevezg (generated)::(Checksum: 3:f3fcdeb5a6948b40acdd42ffaa2b9ca2)
ALTER TABLE `kinton_liquibase`.`chef_runlist` ADD CONSTRAINT `chef_runlist_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-91', '2.0.3', '3:f3fcdeb5a6948b40acdd42ffaa2b9ca2', 280);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-92::destevezg (generated)::(Checksum: 3:f3d14adb1e350c51997a1e6844ab9940)
ALTER TABLE `kinton_liquibase`.`datacenter` ADD CONSTRAINT `datacenternetwork_FK1` FOREIGN KEY (`network_id`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-92', '2.0.3', '3:f3d14adb1e350c51997a1e6844ab9940', 281);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-93::destevezg (generated)::(Checksum: 3:9fe0560ad10c16c6447e1ed6885f76f4)
ALTER TABLE `kinton_liquibase`.`disk_management` ADD CONSTRAINT `disk_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton_liquibase`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-93', '2.0.3', '3:9fe0560ad10c16c6447e1ed6885f76f4', 282);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-94::destevezg (generated)::(Checksum: 3:0032907cbce09c87640b9ff6a764c480)
ALTER TABLE `kinton_liquibase`.`disk_management` ADD CONSTRAINT `disk_idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-94', '2.0.3', '3:0032907cbce09c87640b9ff6a764c480', 283);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-95::destevezg (generated)::(Checksum: 3:dd62c0e68f4129aac905b4057dc1d099)
ALTER TABLE `kinton_liquibase`.`diskstateful_conversions` ADD CONSTRAINT `idManagement_FK2` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-95', '2.0.3', '3:dd62c0e68f4129aac905b4057dc1d099', 284);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-96::destevezg (generated)::(Checksum: 3:270162361562e2f4bfc315790e4c2436)
ALTER TABLE `kinton_liquibase`.`enterprise` ADD CONSTRAINT `enterprise_pricing_FK` FOREIGN KEY (`idPricingTemplate`) REFERENCES `kinton_liquibase`.`pricingTemplate` (`idPricingTemplate`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-96', '2.0.3', '3:270162361562e2f4bfc315790e4c2436', 285);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-97::destevezg (generated)::(Checksum: 3:551bb6624a1bbf377d366698131eca46)
ALTER TABLE `kinton_liquibase`.`enterprise_limits_by_datacenter` ADD CONSTRAINT `enterprise_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-97', '2.0.3', '3:551bb6624a1bbf377d366698131eca46', 286);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-98::destevezg (generated)::(Checksum: 3:0e34a5a7111441531665633a6d7b9f72)
ALTER TABLE `kinton_liquibase`.`enterprise_properties` ADD CONSTRAINT `FK_enterprise` FOREIGN KEY (`enterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-98', '2.0.3', '3:0e34a5a7111441531665633a6d7b9f72', 287);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-99::destevezg (generated)::(Checksum: 3:04a1d1e90ad4469a47e9708c8089627b)
ALTER TABLE `kinton_liquibase`.`enterprise_properties_map` ADD CONSTRAINT `FK2_enterprise_properties` FOREIGN KEY (`enterprise_properties`) REFERENCES `kinton_liquibase`.`enterprise_properties` (`idProperties`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-99', '2.0.3', '3:04a1d1e90ad4469a47e9708c8089627b', 288);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-100::destevezg (generated)::(Checksum: 3:f91ad4d8c5060389553f3d306358f05b)
ALTER TABLE `kinton_liquibase`.`enterprise_theme` ADD CONSTRAINT `THEME_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-100', '2.0.3', '3:f91ad4d8c5060389553f3d306358f05b', 289);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-101::destevezg (generated)::(Checksum: 3:08431dad180a0d22381e81da51437d58)
ALTER TABLE `kinton_liquibase`.`hypervisor` ADD CONSTRAINT `Hypervisor_FK1` FOREIGN KEY (`idPhysicalMachine`) REFERENCES `kinton_liquibase`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-101', '2.0.3', '3:08431dad180a0d22381e81da51437d58', 290);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-102::destevezg (generated)::(Checksum: 3:ec18fe708288c3727c059f70fec0592d)
ALTER TABLE `kinton_liquibase`.`initiator_mapping` ADD CONSTRAINT `volume_managementFK_1` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-102', '2.0.3', '3:ec18fe708288c3727c059f70fec0592d', 291);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-103::destevezg (generated)::(Checksum: 3:32ea224b9e146eb40936f76867ff7b14)
ALTER TABLE `kinton_liquibase`.`ip_pool_management` ADD CONSTRAINT `id_management_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-103', '2.0.3', '3:32ea224b9e146eb40936f76867ff7b14', 292);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-104::destevezg (generated)::(Checksum: 3:16385e2e83f9ad14bf2d01931632dcf8)
ALTER TABLE `kinton_liquibase`.`ip_pool_management` ADD CONSTRAINT `ippool_vlan_network_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-104', '2.0.3', '3:16385e2e83f9ad14bf2d01931632dcf8', 293);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-105::destevezg (generated)::(Checksum: 3:a3889810f74b3504ba455f5b92503a17)
ALTER TABLE `kinton_liquibase`.`log` ADD CONSTRAINT `log_FK1` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-105', '2.0.3', '3:a3889810f74b3504ba455f5b92503a17', 294);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-106::destevezg (generated)::(Checksum: 3:1c2a45f18c2c9e0205cec68c5f15c8df)
ALTER TABLE `kinton_liquibase`.`node` ADD CONSTRAINT `node_FK2` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-106', '2.0.3', '3:1c2a45f18c2c9e0205cec68c5f15c8df', 295);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-107::destevezg (generated)::(Checksum: 3:3b2d585cddafb8dd8b487c79a25ec210)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idDiskStatefulConversion_FK4` FOREIGN KEY (`idDiskStatefulConversion`) REFERENCES `kinton_liquibase`.`diskstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-107', '2.0.3', '3:3b2d585cddafb8dd8b487c79a25ec210', 296);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-108::destevezg (generated)::(Checksum: 3:aca4613cd7b924079341eb1d6e53d4b6)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`volume_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-108', '2.0.3', '3:aca4613cd7b924079341eb1d6e53d4b6', 297);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-109::destevezg (generated)::(Checksum: 3:20c32675c8298f6834a07abae0b131d5)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idNodeVirtualImage_FK4` FOREIGN KEY (`idNodeVirtualImage`) REFERENCES `kinton_liquibase`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-109', '2.0.3', '3:20c32675c8298f6834a07abae0b131d5', 298);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-110::destevezg (generated)::(Checksum: 3:38e3bcff643d714af308c044fba52bbf)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idTier_FK4` FOREIGN KEY (`idTier`) REFERENCES `kinton_liquibase`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-110', '2.0.3', '3:38e3bcff643d714af308c044fba52bbf', 299);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-111::destevezg (generated)::(Checksum: 3:b443894ec4ff12050a84044d1bcd1e4e)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualApplianceStatefulConversion_FK4` FOREIGN KEY (`idVirtualApplianceStatefulConversion`) REFERENCES `kinton_liquibase`.`vappstateful_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-111', '2.0.3', '3:b443894ec4ff12050a84044d1bcd1e4e', 300);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-112::destevezg (generated)::(Checksum: 3:ba6bbf2f30da240d0ee6984a73d03e19)
ALTER TABLE `kinton_liquibase`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idVirtualImageConversion_FK4` FOREIGN KEY (`idVirtualImageConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-112', '2.0.3', '3:ba6bbf2f30da240d0ee6984a73d03e19', 301);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-113::destevezg (generated)::(Checksum: 3:82d3f1fd372f88015e18d54632a7b55d)
ALTER TABLE `kinton_liquibase`.`nodenetwork` ADD CONSTRAINT `nodeNetwork_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-113', '2.0.3', '3:82d3f1fd372f88015e18d54632a7b55d', 302);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-114::destevezg (generated)::(Checksum: 3:ededbf117cea566d26248a2eed8d500d)
ALTER TABLE `kinton_liquibase`.`nodestorage` ADD CONSTRAINT `nodeStorage_FK1` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-114', '2.0.3', '3:ededbf117cea566d26248a2eed8d500d', 303);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-115::destevezg (generated)::(Checksum: 3:9abeafe76cb303b75da2529d2ed49f33)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK1` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-115', '2.0.3', '3:9abeafe76cb303b75da2529d2ed49f33', 304);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-116::destevezg (generated)::(Checksum: 3:6f31fcadde625681d0780e5bdb930ab1)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualimage_FK3` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`node` (`idNode`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-116', '2.0.3', '3:6f31fcadde625681d0780e5bdb930ab1', 305);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-117::destevezg (generated)::(Checksum: 3:5c05b8f81a9c49441952ac6e288ca4ed)
ALTER TABLE `kinton_liquibase`.`nodevirtualimage` ADD CONSTRAINT `nodevirtualImage_FK2` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-117', '2.0.3', '3:5c05b8f81a9c49441952ac6e288ca4ed', 306);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-118::destevezg (generated)::(Checksum: 3:d7fcd283a8430116755eadb98871038e)
ALTER TABLE `kinton_liquibase`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton_liquibase`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-118', '2.0.3', '3:d7fcd283a8430116755eadb98871038e', 307);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-119::destevezg (generated)::(Checksum: 3:65e4efa31cdcb216c11fbc35119ff4b3)
ALTER TABLE `kinton_liquibase`.`ovf_package` ADD CONSTRAINT `fk_ovf_package_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton_liquibase`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-119', '2.0.3', '3:65e4efa31cdcb216c11fbc35119ff4b3', 308);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-120::destevezg (generated)::(Checksum: 3:6c3d167564dbddcbc4b5da95b4c989cf)
ALTER TABLE `kinton_liquibase`.`ovf_package_list` ADD CONSTRAINT `fk_ovf_package_list_repository` FOREIGN KEY (`id_apps_library`) REFERENCES `kinton_liquibase`.`apps_library` (`id_apps_library`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-120', '2.0.3', '3:6c3d167564dbddcbc4b5da95b4c989cf', 309);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-121::destevezg (generated)::(Checksum: 3:6eef486fa27a9271b265750cf5822329)
ALTER TABLE `kinton_liquibase`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package1` FOREIGN KEY (`id_ovf_package`) REFERENCES `kinton_liquibase`.`ovf_package` (`id_ovf_package`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-121', '2.0.3', '3:6eef486fa27a9271b265750cf5822329', 310);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-122::destevezg (generated)::(Checksum: 3:ba1257e710d03eedabb07d7df82f28ed)
ALTER TABLE `kinton_liquibase`.`ovf_package_list_has_ovf_package` ADD CONSTRAINT `fk_ovf_package_list_has_ovf_package_ovf_package_list1` FOREIGN KEY (`id_ovf_package_list`) REFERENCES `kinton_liquibase`.`ovf_package_list` (`id_ovf_package_list`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-122', '2.0.3', '3:ba1257e710d03eedabb07d7df82f28ed', 311);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-123::destevezg (generated)::(Checksum: 3:40587fdbdc0987f94dc8870d5433a648)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK5` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-123', '2.0.3', '3:40587fdbdc0987f94dc8870d5433a648', 312);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-124::destevezg (generated)::(Checksum: 3:b36b0910dee590233354610d2a82e84c)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK6` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-124', '2.0.3', '3:b36b0910dee590233354610d2a82e84c', 313);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-125::destevezg (generated)::(Checksum: 3:713a6bc8106c0385f397d8d3f5519f89)
ALTER TABLE `kinton_liquibase`.`physicalmachine` ADD CONSTRAINT `PhysicalMachine_FK1` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-125', '2.0.3', '3:713a6bc8106c0385f397d8d3f5519f89', 314);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-126::destevezg (generated)::(Checksum: 3:195b0c68ac2f0abc8a08de9bb1f7d42c)
ALTER TABLE `kinton_liquibase`.`pricingTemplate` ADD CONSTRAINT `Pricing_FK2_Currency` FOREIGN KEY (`idCurrency`) REFERENCES `kinton_liquibase`.`currency` (`idCurrency`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-126', '2.0.3', '3:195b0c68ac2f0abc8a08de9bb1f7d42c', 315);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-127::destevezg (generated)::(Checksum: 3:01a3b1335b2708d52cf2054388db2ed2)
ALTER TABLE `kinton_liquibase`.`rack` ADD CONSTRAINT `Rack_FK1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-127', '2.0.3', '3:01a3b1335b2708d52cf2054388db2ed2', 316);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-128::destevezg (generated)::(Checksum: 3:26fa047add1a70737553b68179e23149)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idResource_FK` FOREIGN KEY (`idResource`) REFERENCES `kinton_liquibase`.`rasd` (`instanceID`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-128', '2.0.3', '3:26fa047add1a70737553b68179e23149', 317);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-129::destevezg (generated)::(Checksum: 3:316c6413e16c240a9536d31cac1a009d)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVirtualApp_FK` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-129', '2.0.3', '3:316c6413e16c240a9536d31cac1a009d', 318);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-130::destevezg (generated)::(Checksum: 3:98547d9da510f98a04525bb1da899fd2)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-130', '2.0.3', '3:98547d9da510f98a04525bb1da899fd2', 319);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-131::destevezg (generated)::(Checksum: 3:9a2fd8be4e660b66984438a523889b99)
ALTER TABLE `kinton_liquibase`.`rasd_management` ADD CONSTRAINT `idVM_FK` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-131', '2.0.3', '3:9a2fd8be4e660b66984438a523889b99', 320);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-132::destevezg (generated)::(Checksum: 3:e4ef52980015af94bdd498643e2c99a9)
ALTER TABLE `kinton_liquibase`.`remote_service` ADD CONSTRAINT `idDatecenter_FK` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-132', '2.0.3', '3:e4ef52980015af94bdd498643e2c99a9', 321);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-133::destevezg (generated)::(Checksum: 3:7dc67ac46da69cc5700b0beb5be00d36)
ALTER TABLE `kinton_liquibase`.`repository` ADD CONSTRAINT `fk_idDataCenter` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-133', '2.0.3', '3:7dc67ac46da69cc5700b0beb5be00d36', 322);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-134::destevezg (generated)::(Checksum: 3:3bfd4d3d0292b3fccd17b7071128b3f6)
ALTER TABLE `kinton_liquibase`.`role` ADD CONSTRAINT `fk_role_1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-134', '2.0.3', '3:3bfd4d3d0292b3fccd17b7071128b3f6', 323);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-135::destevezg (generated)::(Checksum: 3:ff7766e265801918ad4cdf7ecd46c560)
ALTER TABLE `kinton_liquibase`.`role_ldap` ADD CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-135', '2.0.3', '3:ff7766e265801918ad4cdf7ecd46c560', 324);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-136::destevezg (generated)::(Checksum: 3:ee2a223fb099e67cf6fc18b87ae7f591)
ALTER TABLE `kinton_liquibase`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_privileges` FOREIGN KEY (`idPrivilege`) REFERENCES `kinton_liquibase`.`privilege` (`idPrivilege`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-136', '2.0.3', '3:ee2a223fb099e67cf6fc18b87ae7f591', 325);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-137::destevezg (generated)::(Checksum: 3:ac64bed93201f4481d726403fcf64066)
ALTER TABLE `kinton_liquibase`.`roles_privileges` ADD CONSTRAINT `fk_roles_privileges_role` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-137', '2.0.3', '3:ac64bed93201f4481d726403fcf64066', 326);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-138::destevezg (generated)::(Checksum: 3:73100cbb3f5a942160cc64a8424abd5e)
ALTER TABLE `kinton_liquibase`.`session` ADD CONSTRAINT `fk_session_user` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-138', '2.0.3', '3:73100cbb3f5a942160cc64a8424abd5e', 327);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-139::destevezg (generated)::(Checksum: 3:793c781707bced3d1654c84b7c76e77d)
ALTER TABLE `kinton_liquibase`.`storage_device` ADD CONSTRAINT `storage_device_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-139', '2.0.3', '3:793c781707bced3d1654c84b7c76e77d', 328);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-140::destevezg (generated)::(Checksum: 3:9055467ff7c5054137b4f90ebe876da7)
ALTER TABLE `kinton_liquibase`.`storage_pool` ADD CONSTRAINT `storage_pool_FK1` FOREIGN KEY (`idStorageDevice`) REFERENCES `kinton_liquibase`.`storage_device` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-140', '2.0.3', '3:9055467ff7c5054137b4f90ebe876da7', 329);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-141::destevezg (generated)::(Checksum: 3:fd7e4dd4737d5370f20271be9fdd7eb2)
ALTER TABLE `kinton_liquibase`.`storage_pool` ADD CONSTRAINT `storage_pool_FK2` FOREIGN KEY (`idTier`) REFERENCES `kinton_liquibase`.`tier` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-141', '2.0.3', '3:fd7e4dd4737d5370f20271be9fdd7eb2', 330);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-142::destevezg (generated)::(Checksum: 3:09c3d29a363519b3cac07f4202c646fe)
ALTER TABLE `kinton_liquibase`.`tier` ADD CONSTRAINT `tier_FK_1` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-142', '2.0.3', '3:09c3d29a363519b3cac07f4202c646fe', 331);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-143::destevezg (generated)::(Checksum: 3:50b7c3f1e04b85602e866f583a16b66e)
ALTER TABLE `kinton_liquibase`.`ucs_rack` ADD CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-143', '2.0.3', '3:50b7c3f1e04b85602e866f583a16b66e', 332);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-144::destevezg (generated)::(Checksum: 3:ace1843a6d783d646d969ccd823db671)
ALTER TABLE `kinton_liquibase`.`user` ADD CONSTRAINT `FK1_user` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-144', '2.0.3', '3:ace1843a6d783d646d969ccd823db671', 333);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-145::destevezg (generated)::(Checksum: 3:a7e93675d1197b63062e6448fef41df5)
ALTER TABLE `kinton_liquibase`.`user` ADD CONSTRAINT `User_FK1` FOREIGN KEY (`idRole`) REFERENCES `kinton_liquibase`.`role` (`idRole`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-145', '2.0.3', '3:a7e93675d1197b63062e6448fef41df5', 334);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-146::destevezg (generated)::(Checksum: 3:e51d638bc4415d31dbde4fa70d70850c)
ALTER TABLE `kinton_liquibase`.`vappstateful_conversions` ADD CONSTRAINT `idUser_FK3` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-146', '2.0.3', '3:e51d638bc4415d31dbde4fa70d70850c', 335);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-147::destevezg (generated)::(Checksum: 3:d555b66bdf90f7a7030d0f68de9f9352)
ALTER TABLE `kinton_liquibase`.`vappstateful_conversions` ADD CONSTRAINT `idVirtualApp_FK3` FOREIGN KEY (`idVirtualApp`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-147', '2.0.3', '3:d555b66bdf90f7a7030d0f68de9f9352', 336);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-148::destevezg (generated)::(Checksum: 3:426ea6a8d43fbfa8dbf1e3042100d680)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualimage_conversions_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-148', '2.0.3', '3:426ea6a8d43fbfa8dbf1e3042100d680', 337);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-149::destevezg (generated)::(Checksum: 3:aa7373c0c3f49483376a4f81f701eac1)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtual_appliance_conversions_node_FK` FOREIGN KEY (`idNode`) REFERENCES `kinton_liquibase`.`nodevirtualimage` (`idNode`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-149', '2.0.3', '3:aa7373c0c3f49483376a4f81f701eac1', 338);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-150::destevezg (generated)::(Checksum: 3:da2c3e52f06964c57ef3d2271a9b6a0e)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `user_FK` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-150', '2.0.3', '3:da2c3e52f06964c57ef3d2271a9b6a0e', 339);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-151::destevezg (generated)::(Checksum: 3:222c34869af778782ab817dc95cc5b2e)
ALTER TABLE `kinton_liquibase`.`virtual_appliance_conversions` ADD CONSTRAINT `virtualapp_FK` FOREIGN KEY (`idVirtualAppliance`) REFERENCES `kinton_liquibase`.`virtualapp` (`idVirtualApp`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-151', '2.0.3', '3:222c34869af778782ab817dc95cc5b2e', 340);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-152::destevezg (generated)::(Checksum: 3:512ca0846b9cc8eaf543619fe37c2467)
ALTER TABLE `kinton_liquibase`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-152', '2.0.3', '3:512ca0846b9cc8eaf543619fe37c2467', 341);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-153::destevezg (generated)::(Checksum: 3:6ff1055b34ea718a053fff2b8df4c8a5)
ALTER TABLE `kinton_liquibase`.`virtualapp` ADD CONSTRAINT `VirtualApp_FK4` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-153', '2.0.3', '3:6ff1055b34ea718a053fff2b8df4c8a5', 342);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-154::destevezg (generated)::(Checksum: 3:d5d8e8618ab9866e9f9cfb448ae33969)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK7` FOREIGN KEY (`default_vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-154', '2.0.3', '3:d5d8e8618ab9866e9f9cfb448ae33969', 343);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-155::destevezg (generated)::(Checksum: 3:b122233bd5e4bb430579f5f814d36397)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK6` FOREIGN KEY (`idDataCenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-155', '2.0.3', '3:b122233bd5e4bb430579f5f814d36397', 344);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-156::destevezg (generated)::(Checksum: 3:dfbf9f81255b3cd3a3161dcc17511110)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK1` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-156', '2.0.3', '3:dfbf9f81255b3cd3a3161dcc17511110', 345);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-157::destevezg (generated)::(Checksum: 3:c9fba8f99d3108f2bd3b9c9f390eeec7)
ALTER TABLE `kinton_liquibase`.`virtualdatacenter` ADD CONSTRAINT `virtualDataCenter_FK4` FOREIGN KEY (`networktypeID`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-157', '2.0.3', '3:c9fba8f99d3108f2bd3b9c9f390eeec7', 346);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-158::destevezg (generated)::(Checksum: 3:f31789791e81e7135e8124295ee327d2)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `fk_virtualimage_category` FOREIGN KEY (`idCategory`) REFERENCES `kinton_liquibase`.`category` (`idCategory`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-158', '2.0.3', '3:f31789791e81e7135e8124295ee327d2', 347);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-159::destevezg (generated)::(Checksum: 3:0e6a4917ed52ee38daa466c66c3f9cd9)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK9` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-159', '2.0.3', '3:0e6a4917ed52ee38daa466c66c3f9cd9', 348);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-160::destevezg (generated)::(Checksum: 3:3cfe3848114a9224ffa33b2dfb64f115)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK8` FOREIGN KEY (`idMaster`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-160', '2.0.3', '3:3cfe3848114a9224ffa33b2dfb64f115', 349);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-161::destevezg (generated)::(Checksum: 3:733f8240f9c747fbb0f87feef304343c)
ALTER TABLE `kinton_liquibase`.`virtualimage` ADD CONSTRAINT `virtualImage_FK3` FOREIGN KEY (`idRepository`) REFERENCES `kinton_liquibase`.`repository` (`idRepository`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-161', '2.0.3', '3:733f8240f9c747fbb0f87feef304343c', 350);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-162::destevezg (generated)::(Checksum: 3:bf92f9b5a97addff1277e5ff780845a9)
ALTER TABLE `kinton_liquibase`.`virtualimage_conversions` ADD CONSTRAINT `idImage_FK` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-162', '2.0.3', '3:bf92f9b5a97addff1277e5ff780845a9', 351);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-163::destevezg (generated)::(Checksum: 3:9f3ba6cbc69c86e618fe2874a791fa06)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualmachine_conversion_FK` FOREIGN KEY (`idConversion`) REFERENCES `kinton_liquibase`.`virtualimage_conversions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-163', '2.0.3', '3:9f3ba6cbc69c86e618fe2874a791fa06', 352);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-164::destevezg (generated)::(Checksum: 3:67b88aa30df28d3201c7ceebcb52bc94)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `kinton_liquibase`.`datastore` (`idDatastore`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-164', '2.0.3', '3:67b88aa30df28d3201c7ceebcb52bc94', 353);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-165::destevezg (generated)::(Checksum: 3:e12866d3a5ede591cd3c87977d048b4e)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK5` FOREIGN KEY (`idEnterprise`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-165', '2.0.3', '3:e12866d3a5ede591cd3c87977d048b4e', 354);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-166::destevezg (generated)::(Checksum: 3:4f2175c2f4541349202628e309765132)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK1` FOREIGN KEY (`idHypervisor`) REFERENCES `kinton_liquibase`.`hypervisor` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-166', '2.0.3', '3:4f2175c2f4541349202628e309765132', 355);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-167::destevezg (generated)::(Checksum: 3:fedaa460d66535f899c7529f1149ebae)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-167', '2.0.3', '3:fedaa460d66535f899c7529f1149ebae', 356);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-168::destevezg (generated)::(Checksum: 3:c675f4be2a72820110a984d8c47c662c)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK4` FOREIGN KEY (`idUser`) REFERENCES `kinton_liquibase`.`user` (`idUser`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-168', '2.0.3', '3:c675f4be2a72820110a984d8c47c662c', 357);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-169::destevezg (generated)::(Checksum: 3:27a4887055b31cd11ff095d1cfa85916)
ALTER TABLE `kinton_liquibase`.`virtualmachine` ADD CONSTRAINT `virtualMachine_FK6` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton_liquibase`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-169', '2.0.3', '3:27a4887055b31cd11ff095d1cfa85916', 358);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-170::destevezg (generated)::(Checksum: 3:560374d4b47a9b6a771044148b282e70)
ALTER TABLE `kinton_liquibase`.`virtualmachinetrackedstate` ADD CONSTRAINT `VirtualMachineTrackedState_FK1` FOREIGN KEY (`idVM`) REFERENCES `kinton_liquibase`.`virtualmachine` (`idVM`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-170', '2.0.3', '3:560374d4b47a9b6a771044148b282e70', 359);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-171::destevezg (generated)::(Checksum: 3:68d7563ef38aead2c95fdaac0e888719)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_enterprise_FK` FOREIGN KEY (`enterprise_id`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-171', '2.0.3', '3:68d7563ef38aead2c95fdaac0e888719', 360);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-172::destevezg (generated)::(Checksum: 3:312c8e0fff7774e3d6e5524f517e0b82)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_configuration_FK` FOREIGN KEY (`network_configuration_id`) REFERENCES `kinton_liquibase`.`network_configuration` (`network_configuration_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-172', '2.0.3', '3:312c8e0fff7774e3d6e5524f517e0b82', 361);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-173::destevezg (generated)::(Checksum: 3:bef7a80f32d6231b651ba0c87aa0fde1)
ALTER TABLE `kinton_liquibase`.`vlan_network` ADD CONSTRAINT `vlannetwork_network_FK` FOREIGN KEY (`network_id`) REFERENCES `kinton_liquibase`.`network` (`network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-173', '2.0.3', '3:bef7a80f32d6231b651ba0c87aa0fde1', 362);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-174::destevezg (generated)::(Checksum: 3:d41c0e29185d5d92dc0aea3d265de30c)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idRack_FK` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-174', '2.0.3', '3:d41c0e29185d5d92dc0aea3d265de30c', 363);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-175::destevezg (generated)::(Checksum: 3:3daa19f6643231b6686e28616fb0ae10)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_idVirtualDataCenter_FK` FOREIGN KEY (`idVirtualDataCenter`) REFERENCES `kinton_liquibase`.`virtualdatacenter` (`idVirtualDataCenter`) ON UPDATE NO ACTION ON DELETE SET NULL;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-175', '2.0.3', '3:3daa19f6643231b6686e28616fb0ae10', 364);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-176::destevezg (generated)::(Checksum: 3:cd285df4a114643b54089a40a1dad806)
ALTER TABLE `kinton_liquibase`.`vlan_network_assignment` ADD CONSTRAINT `vlan_network_assignment_networkid_FK` FOREIGN KEY (`vlan_network_id`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-176', '2.0.3', '3:cd285df4a114643b54089a40a1dad806', 365);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-177::destevezg (generated)::(Checksum: 3:f65112b632a308d369d6b09a37808c7f)
ALTER TABLE `kinton_liquibase`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_dhcp` FOREIGN KEY (`idDhcpOption`) REFERENCES `kinton_liquibase`.`dhcpOption` (`idDhcpOption`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-177', '2.0.3', '3:f65112b632a308d369d6b09a37808c7f', 366);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-178::destevezg (generated)::(Checksum: 3:c93bcf222bca4c78a4a459cf33a76a5a)
ALTER TABLE `kinton_liquibase`.`vlans_dhcpOption` ADD CONSTRAINT `fk_vlans_dhcp_vlan` FOREIGN KEY (`idVlan`) REFERENCES `kinton_liquibase`.`vlan_network` (`vlan_network_id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-178', '2.0.3', '3:c93bcf222bca4c78a4a459cf33a76a5a', 367);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-179::destevezg (generated)::(Checksum: 3:426a5ef53ece7774a8e83c85a19d8625)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `volumemanagement_FK3` FOREIGN KEY (`idImage`) REFERENCES `kinton_liquibase`.`virtualimage` (`idImage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-179', '2.0.3', '3:426a5ef53ece7774a8e83c85a19d8625', 368);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-180::destevezg (generated)::(Checksum: 3:355f1260cb89891ff1b754285d70338d)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `idManagement_FK` FOREIGN KEY (`idManagement`) REFERENCES `kinton_liquibase`.`rasd_management` (`idManagement`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-180', '2.0.3', '3:355f1260cb89891ff1b754285d70338d', 369);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-181::destevezg (generated)::(Checksum: 3:fa4e6b2f4a0c16d94c0b5e38d39837ed)
ALTER TABLE `kinton_liquibase`.`volume_management` ADD CONSTRAINT `idStorage_FK` FOREIGN KEY (`idStorage`) REFERENCES `kinton_liquibase`.`storage_pool` (`idStorage`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-181', '2.0.3', '3:fa4e6b2f4a0c16d94c0b5e38d39837ed', 370);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-182::destevezg (generated)::(Checksum: 3:eeb5e786928b931e91952eabf01b7c08)
ALTER TABLE `kinton_liquibase`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_1` FOREIGN KEY (`idEnterprise1`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-182', '2.0.3', '3:eeb5e786928b931e91952eabf01b7c08', 371);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-183::destevezg (generated)::(Checksum: 3:8c3160a3110db19310507271709cb854)
ALTER TABLE `kinton_liquibase`.`workload_enterprise_exclusion_rule` ADD CONSTRAINT `FK_eerule_enterprise_2` FOREIGN KEY (`idEnterprise2`) REFERENCES `kinton_liquibase`.`enterprise` (`idEnterprise`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-183', '2.0.3', '3:8c3160a3110db19310507271709cb854', 372);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-184::destevezg (generated)::(Checksum: 3:e695c6ffab0d9f64a2e422d4c98e8924)
ALTER TABLE `kinton_liquibase`.`workload_fit_policy_rule` ADD CONSTRAINT `FK_fprule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-184', '2.0.3', '3:e695c6ffab0d9f64a2e422d4c98e8924', 373);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-185::destevezg (generated)::(Checksum: 3:77b37486b07c1f03cfd6deb096648875)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_datacenter` FOREIGN KEY (`idDatacenter`) REFERENCES `kinton_liquibase`.`datacenter` (`idDataCenter`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-185', '2.0.3', '3:77b37486b07c1f03cfd6deb096648875', 374);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-186::destevezg (generated)::(Checksum: 3:7095d16635c991b37fafb2089658c496)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_machine` FOREIGN KEY (`idMachine`) REFERENCES `kinton_liquibase`.`physicalmachine` (`idPhysicalMachine`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-186', '2.0.3', '3:7095d16635c991b37fafb2089658c496', 375);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-187::destevezg (generated)::(Checksum: 3:a4792b0deaf505765bf4262474922390)
ALTER TABLE `kinton_liquibase`.`workload_machine_load_rule` ADD CONSTRAINT `FK_mlrule_rack` FOREIGN KEY (`idRack`) REFERENCES `kinton_liquibase`.`rack` (`idRack`) ON UPDATE NO ACTION ON DELETE NO ACTION;

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Add Foreign Key Constraint', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-187', '2.0.3', '3:a4792b0deaf505765bf4262474922390', 376);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-188::destevezg (generated)::(Checksum: 3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c)
CREATE UNIQUE INDEX `name` ON `category`(`name`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-188', '2.0.3', '3:a380c7f9fc0bc3ea9fe1e4be4d4fcd2c', 377);

-- Changeset /home/destevezg/abiws/abiquo/database/changelog.xml::1334584506393-189::destevezg (generated)::(Checksum: 3:4eff3205127c7bc1a520db1b06261792)
CREATE UNIQUE INDEX `user_auth_idx` ON `user`(`user`, `authType`);

INSERT INTO `DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Index', 'EXECUTED', '/home/destevezg/abiws/abiquo/database/changelog.xml', '1334584506393-189', '2.0.3', '3:4eff3205127c7bc1a520db1b06261792', 378);

