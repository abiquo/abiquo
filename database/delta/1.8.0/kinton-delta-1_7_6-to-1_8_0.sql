
-- [ABICLOUDPREMIUM-1598]
ALTER TABLE `kinton`.`enterprise` ADD `isReservationRestricted` tinyint(1) DEFAULT 0

UPDATE kinton.metering SET actionperformed="PERSISTENCE_PROCESS_START" WHERE actionperformed="STATEFUL_PROCESS_START";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_RAW_FINISHED" WHERE actionperformed="STATEFUL_RAW_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_VOLUME_CREATED" WHERE actionperformed="STATEFUL_VOLUME_CREATED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_DUMP_ENQUEUED" WHERE actionperformed="STATEFUL_DUMP_ENQUEUED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_DUMP_FINISHED" WHERE actionperformed="STATEFUL_DUMP_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_PROCESS_FINISHED" WHERE actionperformed="STATEFUL_PROCESS_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_PROCESS_FAILED" WHERE actionperformed="STATEFUL_PROCESS_FAILED";
UPDATE kinton.metering SET actionperformed="PERSISTENCE_INITIATOR_ADDED" WHERE actionperformed="STATEFUL_INITIATOR_ADDED";

-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update volume_management set state = 1 where state = 2;

-- [ABICLOUDPREMIUM-1616]
ALTER TABLE kinton.virtualimage ADD cost_code VARCHAR(50);

-- [ABICLOUDPREMIUM-1476] Changes to fit the LDAP integration.
alter table kinton.user modify user varchar(128) NOT NULL;
alter table kinton.user add authType varchar(20) NOT NULL;
alter table kinton.user modify column password varchar(32);
update kinton.user set authtype = 'ABIQUO';
