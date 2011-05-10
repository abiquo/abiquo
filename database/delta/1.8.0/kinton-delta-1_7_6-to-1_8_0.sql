UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_START" WHERE actionperformed="STATEFUL_PROCESS_START";
UPDATE kinton.metering SET actionperformed="PERSISTENT_RAW_FINISHED" WHERE actionperformed="STATEFUL_RAW_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_VOLUME_CREATED" WHERE actionperformed="STATEFUL_VOLUME_CREATED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_ENQUEUED" WHERE actionperformed="STATEFUL_DUMP_ENQUEUED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_FINISHED" WHERE actionperformed="STATEFUL_DUMP_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FINISHED" WHERE actionperformed="STATEFUL_PROCESS_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FAILED" WHERE actionperformed="STATEFUL_PROCESS_FAILED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_INITIATOR_ADDED" WHERE actionperformed="STATEFUL_INITIATOR_ADDED";
