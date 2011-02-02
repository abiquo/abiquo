--
-- Hypervisor user and password modifications
--
ALTER TABLE `kinton`.`hypervisor` ADD `user` varchar(255) NOT NULL DEFAULT 'user';
ALTER TABLE `kinton`.`hypervisor` ADD `password` varchar(255) NOT NULL DEFAULT 'password';

--
-- Physical Machine modifications
--
ALTER TABLE `kinton`.`physicalmachine` CHANGE COLUMN `realhd` `realStorage` BIGINT(20) UNSIGNED NOT NULL;

--
-- Definition of table `kinton`.`datastore`
--
CREATE TABLE  `kinton`.`datastore` (
  `idDatastore` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `rootPath` varchar(36) NOT NULL,
  `directory` varchar(255) NOT NULL,
  `shared` boolean NOT NULL default 0,
  `enabled` boolean NOT NULL default 0,
  `size` bigint(20) unsigned NOT NULL,
  `usedSize` bigint(20) unsigned NOT NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idDatastore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Definition of table `kinton`.`datastore_assignment`
--

CREATE TABLE `kinton`.`datastore_assignment` (
  `idDatastore` INT(10) UNSIGNED NOT NULL,
  `idPhysicalMachine` int(20) UNSIGNED default NULL,
  `version_c` integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (`idDatastore`,`idPhysicalMachine`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

--
-- Virtual Machine modifications for datastore
--

ALTER TABLE virtualmachine ADD `idDatastore` int(10) unsigned default NULL;
ALTER TABLE virtualmachine ADD CONSTRAINT `virtualMachine_datastore_FK` FOREIGN KEY (`idDatastore`) REFERENCES `datastore` (`idDatastore`);


--
-- Drop state table and its relationships
--

alter table virtualmachine add state varchar(50) not null;
alter table virtualapp add state varchar(50) not null;
alter table virtualapp add subStateS varchar(50) not null;

update virtualmachine v set v.state = (select s.description from state s where s.idState = v.idState);
update virtualapp v set v.state = (select s.description from state s where s.idState = v.idState);
update virtualapp v set v.subStateS = (select s.description from state s where s.idState = v.subState);

alter table virtualapp drop foreign key VirtualApp_FK1;
alter table virtualmachine drop foreign key virtualMachine_FK2;

alter table virtualmachine drop column idState;
alter table virtualapp drop column idState;
alter table virtualapp drop column subState;
alter table virtualapp change subStateS subState varchar(50) not null;

set foreign_key_checks = 0;
drop table state;
set foreign_key_checks = 1;

--
-- drop state_conversion table and its relationships
--

alter table virtualimage_conversions add state varchar(50) not null;
update virtualimage_conversions c set c.state = (select s.description from state_conversion s where s.idState = c.idState);
alter table virtualimage_conversions drop foreign key idState_FK;
alter table virtualimage_conversions drop column idState;

set foreign_key_checks = 0;
drop table state_conversion;
set foreign_key_checks = 1;

--
-- drop nodetype table and its relationships
--

alter table node add `type` varchar(50) not null;
update node set `type`='VIRTUAL_IMAGE' where idNodetype = 1;
update node set `type`='STORAGE' where idNodetype = 2;
update node set `type`='NETWORK' where idNodetype = 3;
alter table node drop foreign key node_FK1;
alter table node drop column idNodeType;

drop table nodetype;

drop table resource_type;

drop table so;

--
-- drop remote service subtables
--

drop table appliancemanager;
drop table nodecollector;
drop table virtualsystemmonitor;
drop table virtualfactory;

alter table heartbeatlog add total_virtual_cores bigint(20) not null default 0;
alter table heartbeatlog add total_virtual_memory bigint(20) not null default 0;
alter table heartbeatlog add total_volume_space bigint(20) not null default 0;

update heartbeatlog set total_virtual_cores = 0, total_virtual_memory = 0, total_volume_space = 0;

--
-- drop superflous fields in remote service
--

alter table remote_service modify serviceMapping varchar(255);
update remote_service set serviceMapping = null where serviceMapping = '';
alter table remote_service add uri varchar(255) not null;
update remote_service set uri = concat_ws('/', concat(replace(protocol, '://', ''), ':/'), concat_ws(':', domainName, port), serviceMapping);
alter table remote_service drop column protocol;
alter table remote_service drop column domainName;
alter table remote_service drop column port;
alter table remote_service drop column serviceMapping;
alter table remote_service drop column uuid;
alter table remote_service drop column name;
