DELETE FROM `kinton`.`system_properties` WHERE name = 'client.infra.useVirtualBox';

DROP TRIGGER IF EXISTS `kinton`.`update_virtualmachine_update_stats`;
DROP TRIGGER IF EXISTS `kinton`.`virtualdatacenter_updated`;

DELIMITER |
CREATE TRIGGER `kinton`.`update_virtualmachine_update_stats` AFTER UPDATE ON `kinton`.`virtualmachine`
    FOR EACH ROW BEGIN
        DECLARE idDataCenterObj INTEGER;
        DECLARE idVirtualAppObj INTEGER;
        DECLARE idVirtualDataCenterObj INTEGER;
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
        -- Register Accounting Events
        IF EXISTS( SELECT * FROM `information_schema`.ROUTINES WHERE ROUTINE_SCHEMA='kinton' AND ROUTINE_TYPE='PROCEDURE' AND ROUTINE_NAME='AccountingVMRegisterEvents' ) THEN
       		 IF EXISTS(SELECT * FROM virtualimage vi WHERE vi.idImage=NEW.idImage AND vi.idRepository IS NOT NULL) THEN CALL AccountingVMRegisterEvents(NEW.idVM, NEW.idType, OLD.state, NEW.state, NEW.ram, NEW.cpu, NEW.hd);
        
       		 END IF;          
       	END IF;	 
    END IF;
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
DELIMITER ;
