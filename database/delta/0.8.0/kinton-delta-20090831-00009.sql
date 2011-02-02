-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51a-3ubuntu5.4


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

---------------------------------------------------------------------
-- New column 'idRAL' on 'kinton'.enterprise' table                --
-- An element inserted was modificated with new changes            --
---------------------------------------------------------------------

--
-- New column 'idRAL' on 'kinton'.enterprise' table
--

ALTER TABLE `kinton`.`enterprise` ADD COLUMN `idRAL` INT(11) UNSIGNED NOT NULL DEFAULT 1 AFTER `deleted`,
 ADD INDEX `enterprise_FK3`(`idRAL`),
 ADD CONSTRAINT `enterprise_FK3` FOREIGN KEY `enterprise_FK3` (`idRAL`)
    REFERENCES `resource_allocation_limit` (`idRAL`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

--
-- Modify `kinton`.`enterprise` auto_increment
--    
ALTER TABLE `kinton`.`enterprise` AUTO_INCREMENT = 3;

--
-- Inserted row modificated
--

DELETE FROM `kinton`.`enterprise`;
INSERT INTO `kinton`.`enterprise` VALUES  (1,'Abiquo',1,'2008-10-20 00:00:00',NULL,NULL,0,1);