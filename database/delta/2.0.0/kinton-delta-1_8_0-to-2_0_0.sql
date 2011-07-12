-- WARNING
-- Please maintain order of delta when merging or adding new lines
-- 1st -> alter existing schema tables
-- 2st -> new created schema tables
-- 3rd -> insert/update data
-- 4th -> Triggers
-- 5th -> SQL Procedures


-- [ABICLOUDPREMIUM-2057]
UPDATE kinton.metering SET actionperformed="VAPP_INSTANCE" WHERE actionperformed="VAPP_BUNDLE";

