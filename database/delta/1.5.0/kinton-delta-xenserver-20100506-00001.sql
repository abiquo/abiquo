--
-- Hypervisor Type added for XEN Server
--

/*!40000 ALTER TABLE `hypervisortype` DISABLE KEYS */;
LOCK TABLES `hypervisortype` WRITE;
INSERT INTO `kinton`.`hypervisortype` VALUES   
 (6, 'xenserver', 9363, 7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `hypervisortype` ENABLE KEYS */;

LOCK TABLES `kinton`.`hypervisor_disk_compatibilities` WRITE;
INSERT INTO `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) VALUES (6, 6);
INSERT INTO `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) VALUES (6, 7);
UNLOCK TABLES;
