--
-- Definition of table `kinton`.`role`
--

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

--
-- Definition of table `kinton`.`privilege`
--

CREATE TABLE `privilege` (
  `idPrivilege` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPrivilege`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`roles_privileges`
--

CREATE  TABLE `kinton`.`roles_privileges` (
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `idPrivilege` INT(10) UNSIGNED NOT NULL ,
  `version_c` int(11) default 0,
  INDEX `fk_roles_privileges_role` (`idRole` ASC) ,
  INDEX `fk_roles_privileges_privileges` (`idPrivilege` ASC) ,
  CONSTRAINT `fk_roles_privileges_role`
    FOREIGN KEY (`idRole` )
    REFERENCES `kinton`.`role` (`idRole` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_roles_privileges_privileges`
    FOREIGN KEY (`idPrivilege` )
    REFERENCES `kinton`.`privilege` (`idPrivilege` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

--
-- Definition of table `kinton`.`role_ldap`
--

CREATE  TABLE `kinton`.`role_ldap` (
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `role_ldap` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  INDEX `fk_role_ldap_role` (`idRole` ASC) ,
  PRIMARY KEY (`idRole`, `role_ldap`) ,
  CONSTRAINT `fk_role_ldap_role`
    FOREIGN KEY (`idRole` )
    REFERENCES `kinton`.`role` (`idRole` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
