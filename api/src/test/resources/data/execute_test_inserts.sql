LOCK TABLES `kinton_test`.`category` WRITE;
DELETE FROM `kinton_test`.`category`;
INSERT INTO `kinton_test`.`category` VALUES  (1,'Others',0,1,null,0),
 (2,'Database servers',1,0,null,0),
 (4,'Applications servers',1,0,null,0),
 (5,'Web servers',1,0,null,0);
UNLOCK TABLES;

COMMIT;
