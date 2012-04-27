-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: delta/2.0.0-HF1_new/deltachangelog.xml
-- Ran at: 4/27/12 12:47 PM
-- Against: root@destevez.bcn.abiquo.com@jdbc:mysql://10.60.12.230:3306/kinton
-- Liquibase version: 2.0.3
-- *********************************************************************

-- Lock Database
-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::1335280201177-1::destevezg (generated)::(Checksum: 3:fd4f14896387e0fd842ac1cbc5b89239)
CREATE TABLE `kinton`.`accounting_event_detail_HF1` (`idAccountingEvent` BIGINT AUTO_INCREMENT NOT NULL, `startTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `endTime` TIMESTAMP DEFAULT '0000-00-00 00:00:00' NOT NULL, `idAccountingResourceType` TINYINT NOT NULL, `resourceType` VARCHAR(255) NOT NULL, `resourceUnits` BIGINT NOT NULL, `resourceName` VARCHAR(511) NOT NULL, `idEnterprise` INT UNSIGNED NOT NULL, `idVirtualDataCenter` INT UNSIGNED NOT NULL, `idVirtualApp` INT UNSIGNED, `idVirtualMachine` INT UNSIGNED, `enterpriseName` VARCHAR(255) NOT NULL, `virtualDataCenter` VARCHAR(255) NOT NULL, `virtualApp` VARCHAR(255), `virtualMachine` VARCHAR(255), `costCode` INT, `idStorageTier` INT, `version_c` INT DEFAULT 0, CONSTRAINT `PK_ACCOUNTING_EVENT_DETAIL_HF1` PRIMARY KEY (`idAccountingEvent`));

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevezg (generated)', '', NOW(), 'Create Table', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', '1335280201177-1', '2.0.3', '3:fd4f14896387e0fd842ac1cbc5b89239', 1);

-- Changeset delta/2.0.0-HF1_new/deltachangelog.xml::ABICLOUDPREMIUM-432432::destevez::(Checksum: 3:1fd743ccdf084124370692129205fcd8)
DROP TRIGGER IF EXISTS kinton.datacenter_created;

CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);

END IF;

END;

INSERT INTO `kinton`.`DATABASECHANGELOG` (`AUTHOR`, `COMMENTS`, `DATEEXECUTED`, `DESCRIPTION`, `EXECTYPE`, `FILENAME`, `ID`, `LIQUIBASE`, `MD5SUM`, `ORDEREXECUTED`) VALUES ('destevez', '', NOW(), 'SQL From File', 'EXECUTED', 'delta/2.0.0-HF1_new/deltachangelog.xml', 'ABICLOUDPREMIUM-432432', '2.0.3', '3:1fd743ccdf084124370692129205fcd8', 2);

-- Release Database Lock
-- Release Database Lock
