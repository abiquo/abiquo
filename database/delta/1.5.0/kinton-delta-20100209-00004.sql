INSERT INTO `kinton`.`remote_service` VALUES  (3,3,'http://localhost:8080/vsm/','default_vsm','0d3930a4-1580-11df-abc5-00221907568c',1);
INSERT INTO `kinton`.`remote_service_type` VALUES  (3,'VirtualSystemMonitor');

--
-- Definition of table `kinton`.`virtualsystemmonitor`
--

DROP TABLE IF EXISTS `kinton`.`virtualsystemmonitor`;
CREATE TABLE  `kinton`.`virtualsystemmonitor` (
  `idRemoteService` int(10) unsigned NOT NULL,
  KEY `virtualfactory_FK1` (`idRemoteService`),
  CONSTRAINT `virtualsystemmonitor_FK1` FOREIGN KEY (`idRemoteService`) REFERENCES `remote_service` (`idRemoteService`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`virtualsystemmonitor`
--

/*!40000 ALTER TABLE `virtualfactory` DISABLE KEYS */;
LOCK TABLES `virtualsystemmonitor` WRITE;
INSERT INTO `kinton`.`virtualsystemmonitor` VALUES  (3);
UNLOCK TABLES;
/*!40000 ALTER TABLE `virtualfactory` ENABLE KEYS */;


--
-- Definition of table `kinton`.`virtualsystemmonitor`
--

DROP TABLE IF EXISTS `kinton`.`nodecollector`;
CREATE TABLE  `kinton`.`nodecollector` (
  `idRemoteService` int(10) unsigned NOT NULL,
  KEY `virtualfactory_FK1` (`idRemoteService`),
  CONSTRAINT `nodecollector_FK1` FOREIGN KEY (`idRemoteService`) REFERENCES `remote_service` (`idRemoteService`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;