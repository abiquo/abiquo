 ----------------------------------------------------------------------------
-- Adding default virtual factory remote service in the default datacenter --
-----------------------------------------------------------------------------

 
--
-- Insert new element in `kinton`.`remote_service`
--

 /*!40000 ALTER TABLE `remote_service` DISABLE KEYS */;
 LOCK TABLES `remote_service` WRITE;
 INSERT INTO `kinton`.`remote_service` VALUES  (1,1,'http://localhost:8080/virtualfactory/',1,NULL,'default_vf','2009-09-02 23:24:00','2009-09-02 23:24:01','c348d87e-1e8f-448a-9817-ea0fc81aa9d7',1);
 UNLOCK TABLES;
 /*!40000 ALTER TABLE `remote_service` ENABLE KEYS */;


--
-- Insert new element in `kinton`.`virtualfactory`
--

 /*!40000 ALTER TABLE `virtualfactory` DISABLE KEYS */;
 LOCK TABLES `virtualfactory` WRITE;
 INSERT INTO `kinton`.`virtualfactory` VALUES  (1);
 UNLOCK TABLES;
 /*!40000 ALTER TABLE `virtualfactory` ENABLE KEYS */;