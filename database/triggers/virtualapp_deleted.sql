CREATE TRIGGER virtualapp_deleted AFTER DELETE ON virtualapp
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    DELETE FROM vapp_enterprise_stats WHERE idVirtualApp = OLD.idVirtualApp;
  END IF;
  END;
