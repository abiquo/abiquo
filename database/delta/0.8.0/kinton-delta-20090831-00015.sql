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

----------------------------------------------------------
-- Column added on `kinton`.`physicalmachine`           --
-- Element inserted on `kinton`.`physicalmachine`       --
-- Auto increment changed in `kinton`.`physicalmachine` --
----------------------------------------------------------

--
-- Column 'cpuRatio' added to `kinton`.`physicalmachine`
--

ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `cpuRatio` int(7)  NOT NULL AFTER `hd`;


--
-- Element inserted on `kinton`.`physicalmachine`
--
DELETE FROM `kinton`.`physicalmachine`;
INSERT INTO `kinton`.`physicalmachine` VALUES  (1,1,1,'myMachine','My local machine',4096,2,10737418240,0,2,1,'2009-02-03 00:00:00',1,'2009-03-30 21:00:51',0,0,0);


--
-- Auto increment changed from 2 to 3 in `kinton`.`physicalmachine` 
--
ALTER TABLE `kinton`.`physicalmachine` AUTO_INCREMENT = 3;