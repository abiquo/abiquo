LOCK TABLES `kinton_test`.`category` WRITE;
DELETE FROM `kinton_test`.`category`;
INSERT INTO `kinton_test`.`category` values (1, 'Others', 0, 1,0);
UNLOCK TABLES;