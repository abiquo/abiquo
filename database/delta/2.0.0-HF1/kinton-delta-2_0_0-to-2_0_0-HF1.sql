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

-- ---------------------------------------------- --
--                  PROCEDURES                    --
-- ---------------------------------------------- --
