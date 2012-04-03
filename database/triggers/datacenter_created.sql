DROP TRIGGER IF EXISTS kinton.datacenter_created;
CREATE TRIGGER kinton.datacenter_created AFTER INSERT ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      INSERT IGNORE INTO cloud_usage_stats (idDataCenter) VALUES (NEW.idDataCenter);
    END IF;
  END;
