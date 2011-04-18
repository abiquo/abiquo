ALTER TABLE `kinton`.`role` DROP COLUMN `securityLevel` , 
DROP COLUMN `largeDescription` , DROP COLUMN `shortDescription` , 
DROP COLUMN `type` , ADD COLUMN `name` VARCHAR(20) NOT NULL  AFTER `version_c` , 
ADD COLUMN `idEnterprise` INT(10) UNSIGNED NULL DEFAULT NULL  AFTER `name` ,
ADD COLUMN `blocked` TINYINT(1)  NOT NULL DEFAULT 0  AFTER `idEnterprise` , 
  ADD CONSTRAINT `fk_role_enterprise`
  FOREIGN KEY (`idEnterprise` )
  REFERENCES `kinton`.`enterprise` (`idEnterprise` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_role_enterprise` (`idEnterprise` ASC) ;

UPDATE  `kinton`.`role`  Set name ='SYS_ADMIN', blocked=1 where idRole=1;
UPDATE  `kinton`.`role`  Set name ='USER' where idRole=2;
UPDATE  `kinton`.`role`  Set name ='ENTERPRISE_ADMIN'where idRole=3;