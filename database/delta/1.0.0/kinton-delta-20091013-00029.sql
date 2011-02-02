/** This delta ereas all user creation and modification columns and keys */

/*category */

ALTER TABLE category DROP FOREIGN KEY Category_FK1;
ALTER TABLE category DROP COLUMN idUserCreation;
ALTER TABLE category DROP FOREIGN KEY Category_FK2;
ALTER TABLE category DROP COLUMN idUser_lastModification;
ALTER TABLE category DROP COLUMN creationDate;
ALTER TABLE category DROP COLUMN lastModificationDate;


/*datacenter*/

ALTER TABLE datacenter DROP FOREIGN KEY DataCenter_FK1;
ALTER TABLE datacenter DROP COLUMN idUserCreation;
ALTER TABLE datacenter DROP FOREIGN KEY DataCenter_FK2;
ALTER TABLE datacenter DROP COLUMN idUser_lastModification;
ALTER TABLE datacenter DROP COLUMN creationDate;
ALTER TABLE datacenter DROP COLUMN lastModificationDate;


/* enterprise */

ALTER TABLE enterprise DROP FOREIGN KEY enterprise_FK1;
ALTER TABLE enterprise DROP COLUMN idUserCreation;
ALTER TABLE enterprise DROP FOREIGN KEY enterprise_FK2;
ALTER TABLE enterprise DROP COLUMN idUser_lastModification;
ALTER TABLE enterprise DROP COLUMN creationDate;
ALTER TABLE enterprise DROP COLUMN lastModificationDate;


/* hypervisor  */

ALTER TABLE hypervisor DROP FOREIGN KEY Hypervisor_FK3;
ALTER TABLE hypervisor DROP COLUMN idUserCreation;
ALTER TABLE hypervisor DROP FOREIGN KEY Hypervisor_FK4;
ALTER TABLE hypervisor DROP COLUMN idUser_lastModification;
ALTER TABLE hypervisor DROP COLUMN creationDate;
ALTER TABLE hypervisor DROP COLUMN lastModificationDate;


/* icon */

ALTER TABLE icon DROP FOREIGN KEY ICON_FK1;
ALTER TABLE icon DROP COLUMN idUserCreation;
ALTER TABLE icon DROP FOREIGN KEY ICON_FK2;
ALTER TABLE icon DROP COLUMN idUser_lastModification;
ALTER TABLE icon DROP COLUMN creationDate;
ALTER TABLE icon DROP COLUMN lastModificationDate;

/* physicalmachine */

ALTER TABLE physicalmachine DROP FOREIGN KEY PhysicalMachine_FK2;
ALTER TABLE physicalmachine DROP COLUMN idUserCreation;
ALTER TABLE physicalmachine DROP FOREIGN KEY PhysicalMachine_FK3;
ALTER TABLE physicalmachine DROP COLUMN idUser_lastModification;
ALTER TABLE physicalmachine DROP COLUMN creationDate;
ALTER TABLE physicalmachine DROP COLUMN lastModificationDate;


/* rack */

ALTER TABLE rack DROP FOREIGN KEY Rack_FK2;
ALTER TABLE rack DROP COLUMN idUserCreataion;
ALTER TABLE rack DROP FOREIGN KEY Rack_FK3;
ALTER TABLE rack DROP COLUMN idUser_lastModification;
ALTER TABLE rack DROP COLUMN creatioNDate;
ALTER TABLE rack DROP COLUMN lastModificationDate;


/* rasd_management */

ALTER TABLE rasd_management DROP COLUMN user_creation;
ALTER TABLE rasd_management DROP COLUMN user_last_modification;
ALTER TABLE rasd_management DROP COLUMN data_creation;
ALTER TABLE rasd_management DROP COLUMN data_last_modification;


/* repository */

ALTER TABLE repository DROP FOREIGN KEY Repository_FK1;
ALTER TABLE repository DROP COLUMN idUserCreation;
ALTER TABLE repository DROP FOREIGN KEY Repository_FK2;
ALTER TABLE repository DROP COLUMN idUser_lastModification;
ALTER TABLE repository DROP COLUMN creationDate;
ALTER TABLE repository DROP COLUMN lastModificationDate;

/* user */

ALTER TABLE user DROP COLUMN idUserCreation;
ALTER TABLE user DROP COLUMN idUser_lastModification;
ALTER TABLE user DROP COLUMN creationDate;
ALTER TABLE user DROP COLUMN lastModificationDate;


/* virtualapp */

ALTER TABLE virtualapp DROP FOREIGN KEY VirtualApp_FK2;
ALTER TABLE virtualapp DROP COLUMN idUserCreation;
ALTER TABLE virtualapp DROP FOREIGN KEY VirtualApp_FK3;
ALTER TABLE virtualapp DROP COLUMN idUser_lastModification;
ALTER TABLE virtualapp DROP COLUMN creationDate;
ALTER TABLE virtualapp DROP COLUMN lastModificationDate;


/* virtualdatacenter */

ALTER TABLE virtualdatacenter DROP FOREIGN KEY virtualDataCenter_FK2;
ALTER TABLE virtualdatacenter DROP COLUMN idUserCreation;
ALTER TABLE virtualdatacenter DROP FOREIGN KEY virtualDataCenter_FK3;
ALTER TABLE virtualdatacenter DROP COLUMN idUser_lastModification;
ALTER TABLE virtualdatacenter DROP COLUMN creationDate;
ALTER TABLE virtualdatacenter DROP COLUMN lastModificationDate;


/* virtualimage */


ALTER TABLE virtualimage DROP FOREIGN KEY virtualImage_FK5;
ALTER TABLE virtualimage DROP COLUMN idUserCreation;
ALTER TABLE virtualimage DROP FOREIGN KEY virtualImage_FK6;
ALTER TABLE virtualimage DROP COLUMN idUser_lastModification;
ALTER TABLE virtualimage DROP COLUMN creationDate;
ALTER TABLE virtualimage DROP COLUMN lastModificationDate;


/* remote_service */

ALTER TABLE remote_service DROP COLUMN idUserCreation;
ALTER TABLE remote_service DROP COLUMN idUser_lastModification;
ALTER TABLE remote_service DROP COLUMN creationDate;
ALTER TABLE remote_service DROP COLUMN lastModificationDate;
