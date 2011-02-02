/* Added new client auth resources and new auth group, necessary for ABICLOUD-229, 228 and 231 bugs / improvements */

INSERT INTO `kinton`.`auth_group` VALUES (4,'APPLIANCE_LIBRARY','Flex and server Appliance Library Management');

INSERT INTO `kinton`.`auth_clientresource` VALUES
 (10,'CREATE_ENTERPRISE','Option to create enterprises in User Management',3,2),
 (11,'EDIT_PUBLIC_VIRTUAL_IMAGE','Permission to edit a public Virtual Image in Appliance Library',4,2),
 (12,'DELETE_PUBLIC_VIRTUAL_IMAGE','Permission to delete a public Virtual Image in Appliance Library',4,2);