-- ---------------------------------------------- --
--                  TABLE DROP                    --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--                 TABLE CREATION                 --
-- ---------------------------------------------- --


-- ---------------------------------------------- --
--         CONSTRAINTS (alter table, etc)         --
-- ---------------------------------------------- --

-- ---------------------------------------------- --
--   DATA CHANGES (insert, update, delete, etc)   --
-- ---------------------------------------------- --

	-- CHEF --
	-- Dumping data for table kinton.privilege
	SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='51' AND name='USERS_MANAGE_CHEF_ENTERPRISE';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.privilege VALUES (51,'USERS_MANAGE_CHEF_ENTERPRISE',0);
	END IF;

	-- CHEF --
	-- Dumping data for table kinton.roles_privileges
	--
	SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='51';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.roles_privileges VALUES (1,51,0);
	END IF;

	-- New System Properties
	SELECT COUNT(*) INTO @existsCount FROM kinton.system_properties WHERE name='client.main.showHardDisk' AND value='1' AND description='Show (1) or hide (0) hard disk tab';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.system_properties (name, value, description) VALUES ('client.main.showHardDisk','1','Show (1) or hide (0) hard disk tab');
	END IF;

	-- New Privilege
	SELECT COUNT(*) INTO @existsCount FROM kinton.privilege WHERE idPrivilege='52' AND name='MANAGE_HARD_DISKS';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.privilege VALUES (52,'MANAGE_HARD_DISKS',0);
	END IF;

	-- Assign New Privilege to Cloud Admin
	SELECT COUNT(*) INTO @existsCount FROM kinton.roles_privileges WHERE idRole='1' AND idPrivilege='52';
	IF @existsCount = 0 THEN 
		INSERT INTO kinton.roles_privileges VALUES (1,52,0);
	END IF;

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --
