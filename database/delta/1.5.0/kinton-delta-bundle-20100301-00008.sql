insert into state values(8, 'UPDATING_NODES');
insert into state values(9, 'FAILED');

alter table virtual_appliance_conversions add `idNode` int(10) unsigned;
alter table virtual_appliance_conversions add constraint `virtual_appliance_conversions_node_FK` foreign key (`idNode`) references `nodevirtualimage` (`idNode`);

alter table virtual_appliance_conversions modify `idUser` int(10) unsigned;
