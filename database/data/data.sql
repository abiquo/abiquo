INSERT IGNORE INTO enterprise_resources_stats (idEnterprise,vCpuReserved,vCpuUsed,memoryReserved,memoryUsed,localStorageReserved,localStorageUsed) VALUES (1 ,0,0,0,0,0,0);
--
UPDATE IGNORE cloud_usage_stats SET numEnterprisesCreated = numEnterprisesCreated+1 WHERE idDataCenter = -1;
UPDATE IGNORE cloud_usage_stats SET numUsersCreated = numUsersCreated+2 WHERE idDataCenter = -1;
-- myLocalMachine


--
-- Checks ALL Tables in DB and adds the 'version_c' column required for Hibernate Persistence 
--
