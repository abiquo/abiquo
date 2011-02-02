--
-- Adding premium hypervisors
--

/*!40000 ALTER TABLE `hypervisortype` DISABLE KEYS */;
LOCK TABLES `hypervisortype` WRITE;
INSERT INTO `kinton`.`hypervisortype` VALUES (4, 'vmx-04', 443, 4);
INSERT INTO `kinton`.`hypervisortype` VALUES (5, 'hyperv-301', 5985, 7);

UNLOCK TABLES;
/*!40000 ALTER TABLE `hypervisortype` ENABLE KEYS */;

/*!40000 ALTER TABLE `hypervisor_disk_compatibilities` DISABLE KEYS */;
LOCK TABLES `kinton`.`hypervisor_disk_compatibilities` WRITE;
insert into `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) values (4, 1);
insert into `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) values (4, 4);
insert into `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) values (5, 6);
insert into `kinton`.`hypervisor_disk_compatibilities` (idHypervisor, idFormat) values (5, 7);

UNLOCK TABLES;
/*!40000 ALTER TABLE `hypervisor_disk_compatibilities` ENABLE KEYS */;
