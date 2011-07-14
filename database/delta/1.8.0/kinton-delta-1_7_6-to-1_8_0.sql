DELETE FROM `kinton`.`system_properties` WHERE name = 'client.infra.useVirtualBox';
-- [ABICLOUDPREMIUM-1476] Changes to fit the LDAP integration.
alter table kinton.user modify user varchar(128) NOT NULL;
alter table kinton.user add authType varchar(20) NOT NULL;
alter table kinton.user modify column password varchar(32);
update kinton.user set authtype = 'ABIQUO';
alter table kinton.session modify user varchar(128) NOT NULL;
alter table kinton.user modify name varchar(128) NOT NULL;
alter table kinton.metering modify user varchar(128) NOT NULL;
alter table kinton.session add authType varchar(20) NOT NULL;
--
-- Drop table `kinton`.`auth_clientresource_exception`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource_exception`;

--
-- Drop table `kinton`.`auth_clientresource`
--

DROP TABLE IF EXISTS `kinton`.`auth_clientresource`;

--
-- Definition of table `kinton`.`role`
--

ALTER TABLE `kinton`.`role` DROP COLUMN `securityLevel` , 
DROP COLUMN `largeDescription` , DROP COLUMN `shortDescription` , 
DROP COLUMN `type` , ADD COLUMN `name` VARCHAR(40) NOT NULL  AFTER `version_c` , 
ADD COLUMN `idEnterprise` INT(10) UNSIGNED NULL DEFAULT NULL  AFTER `name` ,
ADD COLUMN `blocked` TINYINT(1)  NOT NULL DEFAULT 0  AFTER `idEnterprise` , 
  ADD CONSTRAINT `fk_role_enterprise`
  FOREIGN KEY (`idEnterprise` )
  REFERENCES `kinton`.`enterprise` (`idEnterprise` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_role_enterprise` (`idEnterprise` ASC) ;

UPDATE  `kinton`.`role`  Set name ='CLOUD_ADMIN', blocked=1 where idRole=1;
UPDATE  `kinton`.`role`  Set name ='USER' where idRole=2;
UPDATE  `kinton`.`role`  Set name ='ENTERPRISE_ADMIN'where idRole=3;

--
-- Definition of table `kinton`.`privilege`
--

CREATE TABLE `kinton`.`privilege` (
  `idPrivilege` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idPrivilege`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`privilege`
--

/*!40000 ALTER TABLE `kinton`.`privilege` DISABLE KEYS */;
LOCK TABLES `kinton`.`privilege` WRITE;
INSERT INTO `kinton`.`privilege` VALUES
 (1,'ENTERPRISE_ENUMERATE',0),
 (2,'ENTERPRISE_ADMINISTER_ALL',0),
 (3,'ENTERPRISE_RESOURCE_SUMMARY_ENT',0),
 (4,'PHYS_DC_ENUMERATE',0),
 (5,'PHYS_DC_RETRIEVE_RESOURCE_USAGE',0),
 (6,'PHYS_DC_MANAGE',0),
 (7,'PHYS_DC_RETRIEVE_DETAILS',0),
 (8,'PHYS_DC_ALLOW_MODIFY_SERVERS',0),
 (9,'PHYS_DC_ALLOW_MODIFY_NETWORK',0),
 (10,'PHYS_DC_ALLOW_MODIFY_STORAGE',0),
 (11,'PHYS_DC_ALLOW_MODIFY_ALLOCATION',0),
 (12,'VDC_ENUMERATE',0),
 (13,'VDC_MANAGE',0),
 (14,'VDC_MANAGE_VAPP',0),
 (15,'VDC_MANAGE_NETWORK',0),
 (16,'VDC_MANAGE_STORAGE',0),
 (17,'VAPP_CUSTOMISE_SETTINGS',0),
 (18,'VAPP_DEPLOY_UNDEPLOY',0),
 (19,'VAPP_ASSIGN_NETWORK',0),
 (20,'VAPP_ASSIGN_VOLUME',0),
 (21,'VAPP_PERFORM_ACTIONS',0),
 (22,'VAPP_CREATE_STATEFUL',0),
 (23,'VAPP_CREATE_INSTANCE',0),
 (24,'APPLIB_VIEW',0),
 (25,'APPLIB_ALLOW_MODIFY',0),
 (26,'APPLIB_UPLOAD_IMAGE',0),
 (27,'APPLIB_MANAGE_REPOSITORY',0),
 (28,'APPLIB_DOWNLOAD_IMAGE',0),
 (29,'APPLIB_MANAGE_CATEGORIES',0),
 (30,'USERS_VIEW',0),
 (31,'USERS_MANAGE_ENTERPRISE',0),
 (32,'USERS_MANAGE_USERS',0),
 (33,'USERS_MANAGE_OTHER_ENTERPRISES',0),
 (34,'USERS_PROHIBIT_VDC_RESTRICTION',0),
 (35,'USERS_VIEW_PRIVILEGES',0),
 (36,'USERS_MANAGE_ROLES',0),
 (37,'USERS_MANAGE_ROLES_OTHER_ENTERPRISES',0),
 (38,'USERS_MANAGE_SYSTEM_ROLES',0),
 (39,'USERS_MANAGE_LDAP_GROUP',0),
 (40,'USERS_ENUMERATE_CONNECTED',0),
 (41,'SYSCONFIG_VIEW',0),
 (42,'SYSCONFIG_ALLOW_MODIFY',0),
 (43,'EVENTLOG_VIEW_ENTERPRISE',0),
 (44,'EVENTLOG_VIEW_ALL',0),
 (45,'APPLIB_VM_COST_CODE',0),
 (46,'USERS_MANAGE_ENTERPRISE_BRANDING',0),
 (47,'SYSCONFIG_SHOW_REPORTS',0),
 (48,'USERS_DEFINE_AS_MANAGER',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`privilege` ENABLE KEYS */;

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
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kinton`.`roles_privileges`
--

/*!40000 ALTER TABLE `kinton`.`roles_privileges` DISABLE KEYS */;
LOCK TABLES `kinton`.`roles_privileges` WRITE;
INSERT INTO `kinton`.`roles_privileges` VALUES
 (1,1,0),(1,2,0),(1,3,0),(1,4,0),(1,5,0),(1,6,0),(1,7,0),(1,8,0),(1,9,0),(1,10,0),(1,11,0),(1,12,0),(1,13,0),(1,14,0),(1,15,0),(1,16,0),(1,17,0),(1,18,0),(1,19,0),(1,20,0),(1,21,0),(1,22,0),
 (1,23,0),(1,24,0),(1,25,0),(1,26,0),(1,27,0),(1,28,0),(1,29,0),(1,30,0),(1,31,0),(1,32,0),(1,33,0),(1,34,0),(1,35,0),(1,36,0),(1,37,0),(1,38,0),(1,39,0),(1,40,0),(1,41,0),(1,42,0),(1,43,0),(1,44,0),(1,45,0),(1,48,0),
 (3,3,0),(3,12,0),(3,13,0),(3,14,0),(3,15,0),(3,16,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,24,0),(3,25,0),(3,26,0),(3,27,0),(3,28,0),(3,29,0),(3,30,0),(3,32,0),(3,34,0),(3,43,0),(3,48,0),
 (2,12,0),(2,14,0),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,43,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`roles_privileges` ENABLE KEYS */;


--
-- Definition of table `kinton`.`role_ldap`
--
DROP TABLE IF EXISTS `kinton`.`role_ldap`;

CREATE  TABLE `kinton`.`role_ldap` (
  `idRole_ldap` INT(3) NOT NULL AUTO_INCREMENT ,
  `idRole` INT(10) UNSIGNED NOT NULL ,
  `role_ldap` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idRole_ldap`) ,
  KEY `fk_role_ldap_role` (`idRole`) ,
  CONSTRAINT `fk_role_ldap_role` FOREIGN KEY (`idRole` ) REFERENCES `kinton`.`role` (`idRole` ) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into kinton.role_ldap(idRole, role_ldap,  version_c) values ((select idRole from kinton.role where type = 'SYS_ADMIN'), 'LDAP_SYS_ADMIN', 0);
insert into kinton.role_ldap(idRole, role_ldap, version_c) values ((select idRole from kinton.role where type = 'USER'), 'LDAP_USER', 0);
insert into kinton.role_ldap(idRole, role_ldap, version_c) values ((select idRole from kinton.role where type = 'ENTERPRISE_ADMIN'), 'LDAP_ENTERPRISE_ADMIN', 0);

-- [ABICLOUDPREMIUM-1487] The stateful can be done to a pre-selected volume
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD COLUMN `idManagement` int(10) unsigned;
ALTER TABLE `kinton`.`node_virtual_image_stateful_conversions` ADD CONSTRAINT `idManagement_FK4` FOREIGN KEY (`idManagement`) REFERENCES `volume_management` (`idManagement`);

-- VNC password 
ALTER TABLE `kinton`.`virtualmachine` ADD COLUMN `password` VARCHAR(32) DEFAULT NULL;

ALTER TABLE kinton.virtualimage ADD cost_code VARCHAR(50);

DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;
DROP TRIGGER IF EXISTS `kinton`.`update_volume_management_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_rasd_management_update_stats`;


alter table physicalmachine modify column vswitchName varchar(200) NOT NULL;

--
-- Definition of table `kinton`.`ucs_rack`
--
DROP TABLE IF EXISTS `kinton`.`ucs_rack`;
CREATE TABLE  `kinton`.`ucs_rack` (
  `idRack` int(15) unsigned NOT NULL,
  `ip` varchar(20) NOT NULL,
  `port` int(5) NOT NULL,
  `user_rack` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  KEY `id_rack_FK` (`idRack`),
  CONSTRAINT `id_rack_FK` FOREIGN KEY (`idRack`) REFERENCES `rack` (`idRack`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DELIMITER |

CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE costCodeObj VARCHAR(50);
	-- For debugging purposes only
        -- INSERT INTO debug_msg (msg) VALUES (CONCAT('UPDATE: ', OLD.idType, NEW.idType, OLD.state, NEW.state));	
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
        --  Updating enterprise_resources_stats: VCPU Used, Memory Used, Local Storage Used
        IF OLD.idHypervisor IS NULL OR (OLD.idHypervisor != NEW.idHypervisor) THEN
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE NEW.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        ELSE 
            SELECT pm.idDataCenter INTO idDataCenterObj
            FROM hypervisor hy, physicalmachine pm
            WHERE OLD.idHypervisor=hy.id
            AND hy.idPhysicalMachine=pm.idPhysicalMachine;
        END IF;     
        --
        SELECT n.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM nodevirtualimage nvi, node n, virtualapp vapp
        WHERE NEW.idVM = nvi.idVM
        AND nvi.idNode = n.idNode
        AND vapp.idVirtualApp = n.idVirtualApp;     

	IF NEW.idType = 1 AND OLD.idType = 0 THEN
		-- Imported !!!
		UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		IF NEW.state = "RUNNING" THEN 	
			UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualApp = idVirtualAppObj;
		        UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;
		        UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
		        WHERE idDataCenter = idDataCenterObj;       
		        UPDATE IGNORE enterprise_resources_stats 
		            SET vCpuUsed = vCpuUsed + NEW.cpu,
		                memoryUsed = memoryUsed + NEW.ram,
		                localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idEnterprise = NEW.idEnterprise;
		        UPDATE IGNORE dc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + NEW.cpu,
		            memoryUsed = memoryUsed + NEW.ram,
		            localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
		        UPDATE IGNORE vdc_enterprise_stats 
		        SET     vCpuUsed = vCpuUsed + NEW.cpu,
		            memoryUsed = memoryUsed + NEW.ram,
		            localStorageUsed = localStorageUsed + NEW.hd
		        WHERE idVirtualDataCenter = idVirtualDataCenterObj;	
		END IF;
	ELSEIF NEW.idType = 1 AND (NEW.state != OLD.state) THEN
            IF NEW.state = "RUNNING" THEN 
                -- New Active
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning+1
                WHERE idDataCenter = idDataCenterObj;       
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed + NEW.cpu,
                        memoryUsed = memoryUsed + NEW.ram,
                        localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed + NEW.cpu,
                    memoryUsed = memoryUsed + NEW.ram,
                    localStorageUsed = localStorageUsed + NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            ELSEIF OLD.state = "RUNNING" THEN           
                -- Active Out
                UPDATE IGNORE vapp_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmActive = vmActive-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
                UPDATE IGNORE cloud_usage_stats SET vMachinesRunning = vMachinesRunning-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET vCpuUsed = vCpuUsed - NEW.cpu,
                        memoryUsed = memoryUsed - NEW.ram,
                        localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise;
                UPDATE IGNORE dc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idEnterprise = NEW.idEnterprise AND idDataCenter = idDataCenterObj;
                UPDATE IGNORE vdc_enterprise_stats 
                SET     vCpuUsed = vCpuUsed - NEW.cpu,
                    memoryUsed = memoryUsed - NEW.ram,
                    localStorageUsed = localStorageUsed - NEW.hd
                WHERE idVirtualDataCenter = idVirtualDataCenterObj; 
-- cloud_usage_stats Used Stats (vCpuUsed, vMemoryUsed, vStorageUsed) are updated from update_physical_machine_update_stats trigger
            END IF;     
            IF OLD.state = "NOT_DEPLOYED" OR OLD.state = "UNKNOWN"  THEN -- OR OLD.idType != NEW.idType
                -- VMachine Deployed or VMachine imported
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal+1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated+1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            ELSEIF NEW.state = "NOT_DEPLOYED" OR NEW.state = "CRASHED" OR (NEW.state = "UNKNOWN" AND OLD.state != "CRASHED") THEN 
                -- VMachine Undeployed
                UPDATE IGNORE cloud_usage_stats SET vMachinesTotal = vMachinesTotal-1
                WHERE idDataCenter = idDataCenterObj;
                UPDATE IGNORE vapp_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE vdc_enterprise_stats SET vmCreated = vmCreated-1
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;         
        END IF;
        
        SELECT IF(vi.cost_code IS NULL, "", vi.cost_code) INTO costCodeObj
        FROM virtualimage vi
        WHERE vi.idImage = NEW.idImage;
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
         IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN     
      	  CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd, costCodeObj);
        END IF;              
    END IF;
    END IF
    END;
        
|
CREATE TRIGGER `kinton`.`virtualdatacenter_updated` AFTER UPDATE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
    DECLARE vlanNetworkIdObj INTEGER;    
        	  DECLARE networkNameObj VARCHAR(40);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN   
            -- INSERT INTO debug_msg (msg) VALUES (CONCAT('OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'),'NEW.networktypeID ', IFNULL(NEW.networktypeID,'NULL')));
            -- Checks for changes
            IF OLD.name != NEW.name THEN
                -- Name changed !!!
                UPDATE IGNORE vdc_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                -- Changes also in Vapp stats
                UPDATE IGNORE vapp_enterprise_stats SET vdcName = NEW.name
                WHERE idVirtualApp IN (SELECT idVirtualApp FROM virtualapp WHERE idVirtualDataCenter=NEW.idVirtualDataCenter);
            END IF; 
            UPDATE IGNORE vdc_enterprise_stats 
            SET vCpuReserved = vCpuReserved - OLD.cpuHard + NEW.cpuHard,
                memoryReserved = memoryReserved - OLD.ramHard + NEW.ramHard,
                localStorageReserved = localStorageReserved - OLD.hdHard + NEW.hdHard,
                -- publicIPsReserved = publicIPsReserved - OLD.publicIPHard + NEW.publicIPHard,
                extStorageReserved = extStorageReserved - OLD.storageHard + NEW.storageHard,
                vlanReserved = vlanReserved - OLD.vlanHard + NEW.vlanHard
            WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;            
        END IF;
        IF OLD.networktypeID IS NOT NULL AND NEW.networktypeID IS NULL THEN
        -- Remove VlanUsed
	    BEGIN
		DECLARE done INTEGER DEFAULT 0;
		DECLARE cursorVlan CURSOR FOR SELECT DISTINCT vn.network_id, vn.network_name FROM vlan_network vn WHERE vn.network_id = OLD.networktypeID;
		DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;
		    
		OPEN cursorVlan;
		    
		REPEAT
		   FETCH cursorVlan into vlanNetworkIdObj, networkNameObj;
		   IF NOT done THEN

		    -- INSERT INTO debug_msg (msg) VALUES (CONCAT('VDC UPDATED -> OLD.networktypeID ', IFNULL(OLD.networktypeID,'NULL'), 'Enterprise: ',IFNULL(OLD.idEnterprise,'NULL'),' VDC: ',IFNULL(OLD.idVirtualDataCenter,'NULL'),IFNULL(vlanNetworkIdObj,'NULL'),IFNULL(networkNameObj,'NULL')));
			IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVLANRegisterEvents' ) THEN
				CALL AccountingVLANRegisterEvents('DELETE_VLAN',vlanNetworkIdObj, networkNameObj, OLD.idVirtualDataCenter,OLD.idEnterprise);
			END IF;
			-- Statistics
			UPDATE IGNORE cloud_usage_stats
				SET     vlanUsed = vlanUsed - 1
				WHERE idDataCenter = -1;
			UPDATE IGNORE enterprise_resources_stats 
				SET     vlanUsed = vlanUsed - 1
				WHERE idEnterprise = OLD.idEnterprise;
			UPDATE IGNORE vdc_enterprise_stats 
				SET     vlanUsed = vlanUsed - 1
			    WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
		   END IF;    
		UNTIL done END REPEAT;
		CLOSE cursorVlan;
	    END;
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`virtualdatacenter_deleted` BEFORE DELETE ON `kinton`.`virtualdatacenter`
    FOR EACH ROW BEGIN
	DECLARE currentIdManagement INTEGER DEFAULT -1;
	DECLARE currentDataCenter INTEGER DEFAULT -1;
	DECLARE currentIpAddress VARCHAR(20) DEFAULT '';
	DECLARE no_more_ipsfreed INT;
	DECLARE curIpFreed CURSOR FOR SELECT dc.idDataCenter, ipm.ip, ra.idManagement	
           FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management ra
           WHERE ipm.dhcp_service_id=nc.dhcp_service_id
           AND vn.network_configuration_id = nc.network_configuration_id
           AND vn.network_id = dc.network_id
           AND ra.idManagement = ipm.idManagement
           AND ra.idVirtualDataCenter = OLD.idVirtualDataCenter;
	   DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_ipsfreed = 1;	  
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
            UPDATE IGNORE cloud_usage_stats SET numVDCCreated = numVDCCreated-1 WHERE idDataCenter = OLD.idDataCenter;  
            -- Remove Stats
            DELETE FROM vdc_enterprise_stats WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;	
           -- 	
	SET no_more_ipsfreed = 0;	    
	    OPEN curIpFreed;  	    	
   		my_loop:WHILE(no_more_ipsfreed=0) DO 
   		FETCH curIpFreed INTO currentDataCenter, currentIpAddress, currentIdManagement;
		IF no_more_ipsfreed=1 THEN
                	LEAVE my_loop;
	         END IF;
--		INSERT INTO debug_msg (msg) VALUES (CONCAT('IP_FREED: ',currentIpAddress, ' - idManagement: ', currentIdManagement, ' - OLD.idVirtualDataCenter: ', OLD.idVirtualDataCenter, ' - idEnterpriseObj: ', OLD.idEnterprise));
		-- We reset MAC and NAME for the reserved IPs. Java code should do this!
		UPDATE ip_pool_management set mac=NULL, name=NULL WHERE idManagement = currentIdManagement;
		IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
               		CALL AccountingIPsRegisterEvents('IP_FREED',currentIdManagement,currentIpAddress,OLD.idVirtualDataCenter, OLD.idEnterprise);
           	END IF;                    
		UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = currentDataCenter;
		UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = currentDataCenter;
		UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = OLD.idEnterprise;	
	    END WHILE my_loop;	       
	    CLOSE curIpFreed;
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`update_rasd_management_update_stats` AFTER UPDATE ON `kinton`.`rasd_management`
    FOR EACH ROW BEGIN
        DECLARE state VARCHAR(50);
        DECLARE idState INTEGER;
        DECLARE idImage INTEGER;
        DECLARE idDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE ipAddress VARCHAR(20);
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN                                   
            --	   
            IF OLD.idResourceType = 8 THEN
                -- vol Attached ?? -- is stateful
                SELECT IF(count(*) = 0, 0, vm.state), idImage INTO idState, idImage
                FROM volume_management vm
                WHERE vm.idManagement = OLD.idManagement;     
                --
		-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateRASD: ',idState,' - ', IFNULL(OLD.idVirtualApp, 'OLD.idVirtualApp es NULL'), IFNULL(NEW.idVirtualApp, 'NEW.idVirtualApp es NULL')));	
		-- Detectamos cambios de VDC: V2V
		IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter AND OLD.idVirtualApp = NEW.idVirtualApp THEN
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1, volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1, volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			IF idState = 1 THEN
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
				UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			END IF;
		ELSE 			
		        IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NOT NULL AND OLD.idVirtualDataCenter != NEW.idVirtualDataCenter THEN
				-- Volume was changed to another VDC not in a V2V operation (cold move)
		            UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			END IF;
			-- Volume added from a Vapp
			IF OLD.idVirtualApp IS NULL AND NEW.idVirtualApp IS NOT NULL THEN       
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualApp = NEW.idVirtualApp;      
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    IF idState = 1 THEN
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = NEW.idVirtualApp;
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    END IF;                         
			END IF;
			-- Volume removed from a Vapp
			IF OLD.idVirtualApp IS NOT NULL AND NEW.idVirtualApp IS NULL THEN
			    UPDATE IGNORE vapp_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
			    IF idState = 1 THEN
				SELECT vdc.idEnterprise, vdc.idDataCenter INTO idEnterpriseObj, idDataCenterObj
				FROM virtualdatacenter vdc
				WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;
				SELECT r.limitResource INTO reservedSize
				FROM rasd r
				WHERE r.instanceID = OLD.idResource;
				-- INSERT INTO debug_msg (msg) VALUES (CONCAT('Updating ExtStorage: ',idState,' - ', IFNULL(idDataCenterObj, 'idDataCenterObj es NULL'), IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), reservedSize));	
				UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed-reservedSize WHERE idDataCenter = idDataCenterObj;
				UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
				UPDATE IGNORE enterprise_resources_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idEnterprise = idEnterpriseObj;
				UPDATE IGNORE dc_enterprise_stats 
				    SET     extStorageUsed = extStorageUsed - reservedSize
				    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
				UPDATE IGNORE vdc_enterprise_stats 
				    SET     volAttached = volAttached - 1, extStorageUsed = extStorageUsed - reservedSize
				WHERE idVirtualDataCenter = OLD.idVirtualDatacenter;
			        UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualApp = OLD.idVirtualApp;
			    END IF;                 
			END IF;
			-- Volume added to VDC
			IF OLD.idVirtualDataCenter IS NULL AND NEW.idVirtualDataCenter IS NOT NULL THEN        
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached 
			    IF idState = 1 THEN
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;                     
			    END IF;
			END IF;
			-- Volume removed from VDC
			IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN                 
			    UPDATE IGNORE vdc_enterprise_stats SET volCreated = volCreated-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;   
			    UPDATE IGNORE vdc_enterprise_stats SET volAssociated = volAssociated-1 WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
			    -- Stateful are always Attached
			    IF idState = 1 THEN
			        UPDATE IGNORE vdc_enterprise_stats SET volAttached = volAttached-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;                     
			    END IF;
			END IF;                         
                END IF;
            END IF;
            -- From old `autoDetachVolume`
            -- UPDATE IGNORE volume_management v set v.state = 0
            -- WHERE v.idManagement = OLD.idManagement;
            -- Checks for used IPs
            IF OLD.idVM IS NULL AND NEW.idVM IS NOT NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed + 1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed + 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
            END IF;
            -- Checks for unused IPs
            IF OLD.idVM IS NOT NULL AND NEW.idVM IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter INTO idDataCenterObj
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
                AND NEW.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPUsed
                IF idDataCenterObj IS NOT NULL THEN
                    -- Query for enterprise 
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = NEW.idVirtualDataCenter;
                    -- 
                    -- UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    UPDATE IGNORE enterprise_resources_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE dc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                        WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats 
                        SET     publicIPsUsed = publicIPsUsed - 1
                    WHERE idVirtualDataCenter = NEW.idVirtualDataCenter;
                END IF;
            END IF;
            -- Checks for unreserved IPs
            IF OLD.idVirtualDataCenter IS NOT NULL AND NEW.idVirtualDataCenter IS NULL THEN
                -- Query for datacenter
                SELECT dc.idDataCenter, ipm.ip INTO idDataCenterObj, ipAddress
                FROM ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc
                WHERE ipm.dhcp_service_id=nc.dhcp_service_id
                AND vn.network_configuration_id = nc.network_configuration_id
                AND vn.network_id = dc.network_id
                AND OLD.idManagement = ipm.idManagement;
                -- Datacenter found ---> Not PublicIPReserved
                IF idDataCenterObj IS NOT NULL THEN
                    UPDATE IGNORE cloud_usage_stats SET publicIPsUsed = publicIPsUsed-1 WHERE idDataCenter = idDataCenterObj;
                    -- Registers Accounting Event
                    SELECT vdc.idEnterprise INTO idEnterpriseObj
                    FROM virtualdatacenter vdc
                    WHERE vdc.idVirtualDataCenter = OLD.idVirtualDataCenter;                    
                    UPDATE IGNORE enterprise_resources_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idEnterprise = idEnterpriseObj;
                    UPDATE IGNORE vdc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idVirtualDataCenter = OLD.idVirtualDataCenter;
                    UPDATE IGNORE dc_enterprise_stats SET publicIPsReserved = publicIPsReserved-1 WHERE idDataCenter = idDataCenterObj;
                    IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingIPsRegisterEvents' ) THEN
                        CALL AccountingIPsRegisterEvents('IP_FREED',OLD.idManagement,ipAddress,OLD.idVirtualDataCenter, idEnterpriseObj);
                    END IF;                    
                END IF;
            END IF;
        END IF;
    END;
|
CREATE TRIGGER `kinton`.`update_volume_management_update_stats` AFTER UPDATE ON `kinton`.`volume_management`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
        DECLARE idEnterpriseObj INTEGER;
        DECLARE reservedSize BIGINT;
        DECLARE incr INTEGER;
        IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN       
        SET incr = NEW.usedSize-OLD.usedSize;
        SELECT sd.idDataCenter INTO idDataCenterObj
        FROM storage_pool sp, storage_device sd
        WHERE OLD.idStorage = sp.idStorage
        AND sp.idStorageDevice = sd.id;
        --      
        SELECT vapp.idVirtualApp, vapp.idVirtualDataCenter INTO idVirtualAppObj, idVirtualDataCenterObj
        FROM rasd_management rasd, virtualapp vapp
        WHERE OLD.idManagement = rasd.idManagement
        AND rasd.idVirtualApp = vapp.idVirtualApp;
        --
        SELECT vdc.idEnterprise INTO idEnterpriseObj
        FROM virtualdatacenter vdc
        WHERE vdc.idVirtualDataCenter = idVirtualDataCenterObj;
        --
        SELECT r.limitResource INTO reservedSize
        FROM rasd_management rm, rasd r
        WHERE rm.idManagement = NEW.idManagement
        AND r.instanceID = rm.idResource;
        --
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVol: ',IFNULL(idEnterpriseObj, 'idEnterpriseObj es NULL'), IFNULL(idVirtualDataCenterObj, 'idVirtualDataCenterObj es NULL'), IFNULL(idDataCenterObj, 'idDataCenterObj es NULL')));
	-- INSERT INTO debug_msg (msg) VALUES (CONCAT('UpdateVol: ',OLD.state, NEW.state, reservedSize));
	-- 
        IF NEW.state != OLD.state THEN
            IF NEW.state = 1 THEN 
                UPDATE IGNORE cloud_usage_stats SET storageUsed = storageUsed+reservedSize WHERE idDataCenter = idDataCenterObj;
		UPDATE IGNORE vapp_enterprise_stats SET volAttached = volAttached+1 WHERE idVirtualApp = idVirtualAppObj;
                UPDATE IGNORE enterprise_resources_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idEnterprise = idEnterpriseObj;
                UPDATE IGNORE dc_enterprise_stats 
                    SET     extStorageUsed = extStorageUsed +  reservedSize
                    WHERE idDataCenter = idDataCenterObj AND idEnterprise = idEnterpriseObj;
                UPDATE IGNORE vdc_enterprise_stats 
                    SET     volAttached = volAttached + 1, extStorageUsed = extStorageUsed +  reservedSize
                WHERE idVirtualDataCenter = idVirtualDataCenterObj;
            END IF;     
	-- IF OLD.state = 1 ====> This is done in update_rasd_management_update_stats
        END IF;
    END IF;
    END;


 
|
DELIMITER ;

--
-- Datastore rootPath longer
--

alter table kinton.datastore modify rootPath varchar(42) NOT NULL;

--
-- System properties
--


/*!40000 ALTER TABLE `kinton`.`system_properties` DISABLE KEYS */;
LOCK TABLES `kinton`.`system_properties` WRITE;
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.showHelp","1","Show (1) or hide (0) the help icon within the plateform"), 
 ("client.wiki.showDefaultHelp","0","Use (1) or not (0) the default help URL within the plateform"), 
 ("client.wiki.defaultURL","http://community.abiquo.com/display/ABI18/Abiquo+Documentation+Home","The default URL opened when not specific help URL is specified"),
 ("client.wiki.infra.createDatacenter","http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-CreatingaDatacenter","datacenter creation wiki"), 
 ("client.wiki.infra.editDatacenter","http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-ModifyingaDatacenter","datacenter edition wiki"), 
 ("client.wiki.infra.editRemoteService","http://community.abiquo.com/display/ABI18/Managing+Datacenters#ManagingDatacenters-RemoteServices","remote service edition wiki"), 
 ("client.wiki.infra.createPhysicalMachine","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingPhysicalMachinesonStandardRacks","physical machine creation wiki"),
 ("client.wiki.infra.mailNotification","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-SendingEmailNotifications","mail notification wiki"),
 ("client.wiki.infra.addDatastore","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-DatastoresManagement","Datastore manager wiki"),
 ("client.wiki.infra.createRack","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingRacks","rack creation wiki"),
 ("client.wiki.infra.createMultiplePhysicalMachine","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-CreatingMultiplePhysicalMachines","multiple physical machine creation wiki"),
 ("client.wiki.network.publicVlan","http://community.abiquo.com/display/ABI18/Manage+Network+Configuration#ManageNetworkConfiguration-ManagePublicVLANs","public vlan creation wiki"),
 ("client.wiki.storage.storageDevice","http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-ManagingManagedStorageDevices","storage device creation wiki"),
 ("client.wiki.storage.storagePool","http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-StoragePools","storage pool creation wiki"), 
 ("client.wiki.storage.tier","http://community.abiquo.com/display/ABI18/Managing+External+Storage#ManagingExternalStorage-TierManagement","tier edition wiki"),
 ("client.wiki.allocation.global","http://community.abiquo.com/display/ABI18/Manage+Allocation+Rules#ManageAllocationRules-GlobalRulesManagement","global rules wiki"),
 ("client.wiki.allocation.datacenter","http://community.abiquo.com/display/ABI18/Manage+Allocation+Rules#ManageAllocationRules-DatacenterRulesManagement","datacenter rules wiki"),
 ("client.wiki.vdc.createVdc","http://community.abiquo.com/display/ABI18/Manage+Virtual+Datacenters#ManageVirtualDatacenters-CreatingaVirtualDatacenter","virtual datacenter creation wiki"),
 ("client.wiki.vdc.createVapp","http://community.abiquo.com/display/ABI18/Basic+operations#BasicOperations-CreatingaNewVirtualAppliance","virtual app creation wiki"),
 ("client.wiki.vdc.createPrivateNetwork","http://community.abiquo.com/display/ABI18/Manage+Networks#ManageNetworks-PrivateIPAddresses","VDC private network creation wiki"),
 ("client.wiki.vdc.createPublicNetwork","http://community.abiquo.com/display/ABI18/Manage+Networks#ManageNetworks-PublicIPReservation","VDC public network creation wiki"),
 ("client.wiki.vdc.createVolume","http://community.abiquo.com/display/ABI18/Manage+Virtual+Storage#ManageVirtualStorage-CreatingaVolumeofManagedStorage","VDC virtual volume creation wiki"),
 ("client.wiki.vm.editVirtualMachine","http://community.abiquo.com/display/ABI18/Configure+Virtual+Machines","Virtual Machine edition wiki"),
 ("client.wiki.vm.bundleVirtualMachine","http://community.abiquo.com/display/ABI18/Configure+a+Virtual+Appliance#ConfigureaVirtualAppliance-Configure","Bundles VM wiki"),
 ("client.wiki.vm.createNetworkInterface","http://community.abiquo.com/display/ABI18/Configure+Virtual+Machines#ConfigureVirtualMachines-CreatingaNewNetworkInterface","Network Interface creation wiki"),
 ("client.wiki.vm.createInstance","http://community.abiquo.com/display/ABI18/Create+Virtual+Machine+instances","Virtual Machine instance creation wiki"),
 ("client.wiki.vm.createStateful","http://community.abiquo.com/display/ABI18/Create+Persistent+Virtual+Machines","Virtual Machine stateful creation wiki"),
 ("client.wiki.vm.captureVirtualMachine","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-ImportaRetrievedVirtualMachine","Capture Virtual Machine wiki"),
 ("client.wiki.apps.uploadVM","http://community.abiquo.com/display/ABI18/Adding+Virtual+Images+to+the+Appliance+Library#AddingVirtualImagestotheApplianceLibrary-UploadingfromtheLocalFilesystem","Virtual Image upload wiki"),
 ("client.wiki.user.createEnterprise","http://community.abiquo.com/display/ABI18/Manage+Enterprises#ManageEnterprises-CreatingorEditinganEnterprise","Enterprise creation wiki"),
 ("client.wiki.user.dataCenterLimits","http://community.abiquo.com/display/ABI18/Manage+Enterprises#ManageEnterprises-RestrictingDatacenterAccess","Datacenter Limits wiki"),
 ("client.wiki.user.createUser","http://community.abiquo.com/display/ABI18/Manage+Users#ManageUsers-CreatingorEditingaUser","User creation wiki"),
 ("client.wiki.user.createRole","http://community.abiquo.com/display/ABI18/Manage+Roles+and+Privileges","Role creation wiki"),
 ("client.wiki.config.general","http://community.abiquo.com/display/ABI18/Configuration+view","Configuration wiki"),
 ("client.wiki.config.heartbeat","http://community.abiquo.com/display/ABI18/Configuration+view#Configurationview-Heartbeating","Heartbeat configuration wiki"),
 ("client.wiki.config.licence","http://community.abiquo.com/display/ABI18/Configuration+view#ConfigurationView-LicenseManagement","Licence configuration wiki"),
 ("client.wiki.config.registration","http://community.abiquo.com/display/ABI18/Configuration+view#Configurationview-ProductRegistration","Registration wiki"),
 ("client.main.billingUrl","","URL displayed when the report header logo is clicked, if empty the report button will not be displayed");
UNLOCK TABLES;
/*!40000 ALTER TABLE `kinton`.`system_properties` ENABLE KEYS */;

ALTER TABLE `kinton`.`enterprise` ADD `isReservationRestricted` tinyint(1) DEFAULT 0

-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update volume_management set state = 1 where state = 2;

-- [ABICLOUDPREMIUM-1933] Change the default value
UPDATE  `kinton`.`system_properties`  Set value ='1' where name='client.dashboard.showStartUpAlert';



-- 
-- DO NOT DELETE
-- [ABICLOUDPREMIUM-1460] Statistics Information MUST be updated in each upgrade
CALL `kinton`.`CalculateCloudUsageStats`();
CALL `kinton`.`CalculateEnterpriseResourcesStats`();
CALL `kinton`.`CalculateVappEnterpriseStats`();
CALL `kinton`.`CalculateVdcEnterpriseStats`();
-- These calls should be included in every DB Delta
-- 
-- Racks can be HA enabled
ALTER TABLE `kinton`.`rack` ADD COLUMN `haEnabled` boolean default false COMMENT 'TRUE - This rack is enabled for the HA functionality';

-- PhysicalMachine can have 2 new states
ALTER TABLE `kinton`.`physicalmachine` MODIFY COLUMN `idState` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 - STOPPED
1 - NOT PROVISIONED
2 - NOT MANAGED
3 - MANAGED
4 - HALTED
5 - UNLICENSED
6 - HA_IN_PROGRESS
7 - DISABLED_FOR_HA';


-- Racks can be HA enabled
ALTER TABLE `kinton`.`datastore` ADD COLUMN `datastoreUuid` VARCHAR(255) default NULL COMMENT 'Datastore UUID set by Abiquo to identify shared datastores.';
ALTER TABLE `kinton`.`datastore` DROP COLUMN `shared`;

-- ipmi
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `ipmiIP` VARCHAR(39)  DEFAULT NULL AFTER `version_c`,
 ADD COLUMN `ipmiPort` INT(5) UNSIGNED DEFAULT NULL AFTER `ipmiIP`,
 ADD COLUMN `ipmiUser` VARCHAR(255)  DEFAULT NULL AFTER `ipmiPort`,
 ADD COLUMN `ipmiPassword` VARCHAR(255)  DEFAULT NULL AFTER `ipmiUser`;


-- Statistics trigger updates

DROP TRIGGER IF EXISTS `kinton`.`delete_physicalmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`update_physicalmachine_update_stats`;

DELIMITER |
CREATE TRIGGER `kinton`.`delete_physicalmachine_update_stats` AFTER DELETE ON `kinton`.`physicalmachine`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
    IF OLD.idState = 3 THEN
      UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1 WHERE idDataCenter = OLD.idDataCenter;
      UPDATE IGNORE cloud_usage_stats
        SET vCpuUsed=vCpuUsed-OLD.cpuUsed,
          vMemoryUsed=vMemoryUsed-OLD.ramUsed,
          vStorageUsed=vStorageUsed-OLD.hdUsed
      WHERE idDataCenter = OLD.idDataCenter;
    END IF;
    IF OLD.idState NOT IN (2, 6, 7) THEN
      UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1 WHERE idDataCenter = OLD.idDataCenter;
      UPDATE IGNORE cloud_usage_stats
        SET vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
          vMemoryTotal=vMemoryTotal-OLD.ram,
          vStorageTotal=vStorageTotal-OLD.hd
      WHERE idDataCenter = OLD.idDataCenter;
    END IF;
  END IF;
  END;
|
CREATE TRIGGER `kinton`.`update_physicalmachine_update_stats` AFTER UPDATE ON `kinton`.`physicalmachine`
  FOR EACH ROW BEGIN
    IF (@DISABLE_STATS_TRIGGERS IS NULL) THEN
      IF OLD.idState != NEW.idState THEN
        IF OLD.idState IN (2, 7) THEN
          -- Machine not managed changes into managed; or disabled_by_ha to Managed
          UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal+1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
          SET vCpuTotal=vCpuTotal + (NEW.cpu*NEW.cpuRatio),
            vMemoryTotal=vMemoryTotal + NEW.ram,
            vStorageTotal=vStorageTotal + NEW.hd
          WHERE idDataCenter = NEW.idDataCenter;
        END IF;
        IF NEW.idState IN (2,7) THEN
          -- Machine managed changes into not managed or DisabledByHA
          UPDATE IGNORE cloud_usage_stats SET serversTotal=serversTotal-1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
          SET vCpuTotal=vCpuTotal-(OLD.cpu*OLD.cpuRatio),
            vMemoryTotal=vMemoryTotal-OLD.ram,
            vStorageTotal=vStorageTotal-OLD.hd
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        IF NEW.idState = 3 THEN
        -- Stopped / Halted / Not provisioned passes to Managed (Running)
          UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning+1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed+NEW.cpuUsed,
              vMemoryUsed=vMemoryUsed+NEW.ramUsed,
              vStorageUsed=vStorageUsed+NEW.hdUsed
          WHERE idDataCenter = NEW.idDataCenter;
        ELSEIF OLD.idState = 3 THEN
        -- Managed (Running) passes to Stopped / Halted / Not provisioned
          UPDATE IGNORE cloud_usage_stats SET serversRunning = serversRunning-1 WHERE idDataCenter = NEW.idDataCenter;
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed-OLD.cpuUsed,
              vMemoryUsed=vMemoryUsed-OLD.ramUsed,
              vStorageUsed=vStorageUsed-OLD.hdUsed
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
      ELSE
      -- No State Changes
        IF NEW.idState NOT IN (2, 6, 7) THEN
	-- If Machine is in a not managed state, changes into resources are ignored, Should we add 'Disabled' state to this condition?
          UPDATE IGNORE cloud_usage_stats
            SET vCpuTotal=vCpuTotal+((NEW.cpu-OLD.cpu)*NEW.cpuRatio),
              vMemoryTotal=vMemoryTotal + (NEW.ram-OLD.ram),
              vStorageTotal=vStorageTotal + (NEW.hd-OLD.hd)
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
        --
        IF NEW.idState = 3 THEN
          UPDATE IGNORE cloud_usage_stats
            SET vCpuUsed=vCpuUsed + (NEW.cpuUsed-OLD.cpuUsed),
              vMemoryUsed=vMemoryUsed + (NEW.ramUsed-OLD.ramUsed),
              vStorageUsed=vStorageUsed + (NEW.hdUsed-OLD.hdUsed)
          WHERE idDataCenter = OLD.idDataCenter;
        END IF;
      END IF;
    END IF;
  END;
|
DELIMITER ;


-- [UCS]Add the specific wiki link for discovering blades form
INSERT INTO `kinton`.`system_properties` (`name`, `value`, `description`) VALUES
 ("client.wiki.infra.discoverBlades","http://community.abiquo.com/display/ABI18/Manage+Racks+and+Physical+Machines#ManageRacksandPhysicalMachines-DiscoveringBladesonManagedRacks","discover UCS blades wiki");

UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_START" WHERE actionperformed="STATEFUL_PROCESS_START";
UPDATE kinton.metering SET actionperformed="PERSISTENT_RAW_FINISHED" WHERE actionperformed="STATEFUL_RAW_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_VOLUME_CREATED" WHERE actionperformed="STATEFUL_VOLUME_CREATED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_ENQUEUED" WHERE actionperformed="STATEFUL_DUMP_ENQUEUED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_DUMP_FINISHED" WHERE actionperformed="STATEFUL_DUMP_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FINISHED" WHERE actionperformed="STATEFUL_PROCESS_FINISHED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_PROCESS_FAILED" WHERE actionperformed="STATEFUL_PROCESS_FAILED";
UPDATE kinton.metering SET actionperformed="PERSISTENT_INITIATOR_ADDED" WHERE actionperformed="STATEFUL_INITIATOR_ADDED";

--
-- ONETIMETOKEN TABLE
--
DROP  TABLE IF EXISTS `kinton`.`one_time_token`;
CREATE  TABLE `kinton`.`one_time_token` (`idOneTimeTokenSession` int(3) unsigned NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(128) NOT NULL ,
  `version_c` int(11) default 0,
  PRIMARY KEY (`idOneTimeTokenSession`)) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

