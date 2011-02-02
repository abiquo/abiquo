LOCK TABLES `kinton_test`.`category` WRITE;
INSERT INTO `kinton_test`.`category` VALUES  (1,'Others',0,1,0),
 (2,'Database servers',1,0,0),
 (4,'Applications servers',1,0,0),
 (5,'Web servers',1,0,0);
UNLOCK TABLES;

COMMIT;
