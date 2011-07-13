--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (49,'PRICING_VIEW',0),
 (50,'PRICING_MANAGE_PRICING',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,49,0),(1,50,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;


/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.pricing.createCurrency","","Currency creation wiki");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;
