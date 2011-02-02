-- Script for enterprise and resource_allocation_limit merge
-- eruiz@abiquo.com

-- Dummy table
DROP TABLE IF EXISTS `kinton`.`dummy_table`;
CREATE TABLE  `kinton`.`dummy_table` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(40) NOT NULL,
  `deleted` int(1) unsigned NOT NULL default '0',
  `ramSoft` bigint(20) NOT NULL,
  `cpuSoft` bigint(20) NOT NULL,
  `hdSoft` bigint(20)  NOT NULL,
  `ramHard` bigint(20) NOT NULL,
  `cpuHard` bigint(20) NOT NULL,
  `hdHard` bigint(20)  NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Store resource allocation limits by enterprise in dummy_table
INSERT INTO `kinton`.`dummy_table` (id, name, deleted, ramSoft, cpuSoft, hdSoft, ramHard, cpuHard, hdHard)
SELECT e.idEnterprise, e.name, e.deleted, r.ramSoft, r.cpuSoft, r.hdSoft, r.ramHard, r.cpuHard, r.hdHard
FROM enterprise e, resource_allocation_limit r
WHERE e.idRAL = r.idRAL;

-- Add the resource_allocation_limits fields to enterprise.
-- Remove idRAL FK and column.
ALTER TABLE `kinton`.`enterprise`
ADD COLUMN `ramSoft` BIGINT(20)  NOT NULL DEFAULT 0,
ADD COLUMN `cpuSoft` BIGINT(20)  NOT NULL DEFAULT 0,
ADD COLUMN `hdSoft` BIGINT(20)  NOT NULL DEFAULT 0,
ADD COLUMN `ramHard` BIGINT(20)  NOT NULL DEFAULT 0,
ADD COLUMN `cpuHard` BIGINT(20)  NOT NULL DEFAULT 0,
ADD COLUMN `hdHard` BIGINT(20)  NOT NULL DEFAULT 0,
DROP FOREIGN KEY enterprise_FK3, 
DROP COLUMN `idRAL`;

-- Drop resource_allocation_limit table
DROP TABLE `kinton`.`resource_allocation_limit`;

-- Dump dummy_table to enterprise
ALTER TABLE `kinton`.`enterprise` DISABLE KEYS;
SET FOREIGN_KEY_CHECKS=0;

TRUNCATE `kinton`.`enterprise`;

INSERT INTO `kinton`.`enterprise` (idEnterprise, name, deleted, ramSoft, cpuSoft, hdSoft, ramHard, cpuHard, hdHard)
SELECT id, name, deleted, ramSoft, cpuSoft, hdSoft, ramHard, cpuHard, hdHard
FROM dummy_table;

SET FOREIGN_KEY_CHECKS=1;
ALTER TABLE `kinton`.`enterprise` ENABLE KEYS;

-- Drop dummy_table
DROP TABLE `kinton`.`dummy_table`;
