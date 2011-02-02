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

----------------------------
-- Network Section tables --
----------------------------

--
-- Definition of table `kinton`.`bridgetype`
--

DROP TABLE IF EXISTS `kinton`.`bridgetype`;
CREATE TABLE  `kinton`.`bridgetype` (
  `bridgetypeID` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY  (`bridgetypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`forwardtype`
--

DROP TABLE IF EXISTS `kinton`.`forwardtype`;
CREATE TABLE  `kinton`.`forwardtype` (
  `forwardtypeID` int(11) unsigned NOT NULL auto_increment,
  `mode` varchar(20) character set latin1 NOT NULL,
  `dev` varchar(20) character set latin1 NOT NULL,
  PRIMARY KEY  (`forwardtypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`networktype`
--

DROP TABLE IF EXISTS `kinton`.`networktype`;
CREATE TABLE  `kinton`.`networktype` (
  `networktypeID` int(11) unsigned NOT NULL auto_increment,
  `bridgetypeID` int(11) unsigned NOT NULL,
  `forwardtypeID` int(11) unsigned NOT NULL,
  `vlanID` varchar(20) NOT NULL,
  `uuid` varchar(20) NOT NULL,
  PRIMARY KEY  (`networktypeID`),
  KEY `networktype_bridgetype_FK` USING BTREE (`bridgetypeID`),
  KEY `networktype_forwardtype_FK` (`forwardtypeID`),
  CONSTRAINT `networktype_bridgetype_FK` FOREIGN KEY (`bridgetypeID`) REFERENCES `bridgetype` (`bridgetypeID`),
  CONSTRAINT `networktype_forwardtype_FK` FOREIGN KEY (`forwardtypeID`) REFERENCES `forwardtype` (`forwardtypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Definition of table `kinton`.`dhcptype`
--

DROP TABLE IF EXISTS `kinton`.`dhcptype`;
CREATE TABLE `dhcptype` (
  `dhcptypeID` int(11) unsigned NOT NULL auto_increment,
  `networktypeID` int(11) unsigned NULL,
  `addressDHCP` varchar(15) NOT NULL,
  `netmask` varchar(15) NOT NULL,
  `ipgateway` varchar(20) character set latin1 NULL,
  PRIMARY KEY  (`dhcptypeID`),
  CONSTRAINT `dhctype_networktype_FK` FOREIGN KEY (`networktypeID`) REFERENCES `networktype` (`networktypeID`) ON DELETE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`hosttype`
--

DROP TABLE IF EXISTS `kinton`.`hosttype`;
CREATE TABLE  `kinton`.`hosttype` (
  `hosttypeID` int(11) unsigned NOT NULL auto_increment,
  `dhcptypeID` int(11) unsigned NOT NULL,
  -- `idVirtualApp` int(10) unsigned NOT NULL,
  `mac` varchar(20) character set latin1 NOT NULL,
  `name` varchar(20) character set latin1 NOT NULL,
  `ip` varchar(20) character set latin1 NOT NULL,
  PRIMARY KEY  (`hosttypeID`) ,
  KEY `hosttype_dhcptype_FK` (`dhcptypeID`) ,
  CONSTRAINT `hosttype_dhcptype_FK` FOREIGN KEY (`dhcptypeID`) REFERENCES `dhcptype` (`dhcptypeID`)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Copy elements from old 'externalDHCP' and 'externalDHCPfilter' (if any) to the new tables
--

/*
SET @count = (SELECT COUNT(*) FROM `kinton`.`externalDHCP`);

DELIMITER //
CREATE PROCEDURE dorepeat(p1 INT)
BEGIN
  IF @count > 0 THEN
    SET @x = 1;
    INSERT INTO bridgetype VALUES (1, 'bridge');
    INSERT INTO forwardtype VALUES (1, 'mode', 'dev');
    INSERT INTO networktype VALUES (1, 1, 1, 'VLANID', 'UUID');
    REPEAT
      SET @uri = (SELECT uri FROM externalDHCP WHERE idDHCP=@x);
      SET @port = (SELECT port FROM externalDHCP WHERE idDHCP=@x);
      SET @vdc = (SELECT idVirtualDataCenter FROM externalDHCP WHERE idDHCP=@x);
      SET @ip = (SELECT ipAddress FROM externalDHCPfilter WHERE idFilter=@x);
      INSERT INTO dhcptype VALUES (@x, 1, (SELECT (CONCAT(@uri, ':', @port))), (SELECT mask FROM virtualdatacenter WHERE idVirtualDataCenter=@vdc, NULL);
      INSERT INTO hosttype VALUES (@x, @x, (SELECT macAddress FROM externalDHCPfilter WHERE idFilter=@x), (SELECT (CONCAT('host_', @ip))), @ip);
      SET @x = @x + 1;
      UNTIL @x > p1
    END REPEAT;
  END IF;
END 
//
DELIMITER ;

CALL dorepeat(@count);
*/

--
-- Drop table 'externalDHCPfilter'
--

DROP TABLE IF EXISTS `kinton`.`externalDHCPfilter`;

--
-- Drop table 'externalDHCP'
--

DROP TABLE IF EXISTS `kinton`.`externalDHCP`;
/*
--
-- Drop procedure
--
DROP PROCEDURE dorepeat;
*/