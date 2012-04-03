DROP TRIGGER IF EXISTS kinton.datacenter_deleted;
CREATE TRIGGER kinton.datacenter_deleted AFTER DELETE ON kinton.datacenter
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
	DELETE FROM dc_enterprise_stats WHERE idDataCenter = OLD.idDataCenter;
      	DELETE FROM cloud_usage_stats WHERE idDataCenter = OLD.idDataCenter;
    END IF;
  END;
