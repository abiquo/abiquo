USE kinton;
--
-- ACCOUNTING SCRIPTS
-- remove 'accounting' path when calling this script
--
source accounting/accounting-schema.sql;
source accounting/accounting-procedures.sql;
source accounting/accounting-triggers.sql;
source accounting/additional_views.sql;

-- CRON Sample to Activate Accounting: CHECK user/passwd config!
-- Runs every hour
-- 0 * * * * mysql -uroot -hlocalhost -P3306 -proot -e "CALL kinton.UpdateAccounting();"

-- Runs every Sunday at 12:00 and deletes records older than a week.
-- 0 12 * * 0 mysql -uroot -hlocalhost -P3306 -proot -e "CALL kinton.DeleteOldRegisteredEvents(168);"
-- or
-- Runs once a day at midnight
-- 0 0 * * * mysql -uroot -hlocalhost -P3306 -proot -e "CALL kinton.DeleteOldRegisteredEvents(24);"


-- Runs periodically Accounting Events : Available for MySQL > 5.1
-- SET GLOBAL event_scheduler = ON;
-- DROP EVENT IF EXISTS update_accounting_event;
-- CREATE EVENT update_accounting_event ON SCHEDULE EVERY 1 HOUR
-- DO
-- CALL UpdateAccounting();
--

-- DROP EVENT IF EXISTS delete_old_registered_accounting_event;
-- CREATE EVENT delete_old_registered_accounting_event ON SCHEDULE EVERY 24 HOUR
-- DO
-- CALL DeleteOldRegisteredEvents(24);

-- or 

-- DROP EVENT IF EXISTS delete_old_registered_accounting_event;
-- CREATE EVENT delete_old_registered_accounting_event ON SCHEDULE EVERY 168 HOUR
-- DO
-- CALL DeleteOldRegisteredEvents(168);
-- 


