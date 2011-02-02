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

---------------------------------------------------------------------------------------------------------------
-- New table: rangetype.                                                                                     --
-- first_ip, last_ip and mask moved from virtualDataCenter. Data migrated from these rows to rangetype table.--
---------------------------------------------------------------------------------------------------------------


--
-- Definition of table `kinton`.`rangeType`
--

DROP TABLE IF EXISTS `kinton`.`rangetype`;
CREATE TABLE  `kinton`.`rangetype` (
  `rangetypeID` int(11) unsigned NOT NULL auto_increment,
  `first_ip` varchar(15) NOT NULL,
  `last_ip` varchar(15) NOT NULL,
  `mask` int(4) NOT NULL,
  PRIMARY KEY  (`rangetypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Copy elements from old 'first_ip', 'last_ip' and 'mask' (if any) to the new table 'rangetype'
--

SET @count = (SELECT COUNT(*) FROM `kinton`.`virtualdatacenter`);

DELIMITER //
CREATE PROCEDURE dorepeat(p1 INT)
BEGIN
  IF @count > 0 THEN
    SET @x = 1;
    REPEAT
      INSERT INTO rangetype VALUES (@x, (SELECT first_ip FROM virtualdatacenter WHERE idVirtualDataCenter=@x), 
      (SELECT last_ip FROM virtualdatacenter WHERE idVirtualDataCenter=@x), 
      (SELECT mask FROM virtualdatacenter WHERE idVirtualDataCenter=@x));
      SET @x = @x + 1;
      UNTIL @x > p1
    END REPEAT;
  END IF;
END 
//
DELIMITER ;

CALL dorepeat(@count);

--
-- Drop columns 'first_ip', 'last_ip' and 'mask' from virtualdatacenter table
--

ALTER TABLE `kinton`.`virtualdatacenter` DROP COLUMN `first_ip`,
 DROP COLUMN `last_ip`,
 DROP COLUMN `mask`;
 
 --
 -- Change auto increment to 4
 --
 
 ALTER TABLE `kinton`.`virtualdatacenter` AUTO_INCREMENT = 4;


--
-- Drop procedure
--
DROP PROCEDURE dorepeat;