/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.server.core.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class UserDAOTest extends DefaultDAOTestBase<UserDAO, User>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected UserDAO createDao(final EntityManager entityManager)
    {
        return new UserDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<User> createEntityInstanceGenerator()
    {
        return new UserGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public UserGenerator eg()
    {
        return (UserGenerator) super.eg();
    }

    @Test
    public void findConnected()
    {
        User user = eg().createUserWithSession();
        User userWithoutSession = eg().createInstance(user.getEnterprise(), user.getRole());

        List<Object> entitiesToPersist = new ArrayList<Object>();
        for (Privilege privilege : user.getRole().getPrivileges())
        {
            entitiesToPersist.add(privilege);
        }
        entitiesToPersist.add(user.getEnterprise());
        entitiesToPersist.add(user.getRole());
        entitiesToPersist.add(user);
        entitiesToPersist.add(userWithoutSession);

        ds().persistAll(entitiesToPersist.toArray());

        UserDAO dao = createDaoForRollbackTransaction();

        Collection<User> users =
            dao.find(user.getEnterprise(), null, null, null, false, true, 0, 25);
        AssertEx.assertSize(users, 1);

        users = dao.find(null, null, null, null, false, true, 0, 25);
        AssertEx.assertSize(users, 1);

        users = dao.find(user.getEnterprise(), null, null, null, false, false, 0, 25);
        AssertEx.assertSize(users, 2);
    }

    @Test
    public void getAbiquoUserByLogin()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(user1.getEnterprise());
        for (Privilege p : user1.getRole().getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(user1.getRole());
        entitiesToPersist.add(user1);
        ds().persistAll(entitiesToPersist.toArray());

        UserDAO dao = createDaoForRollbackTransaction();

        User user = dao.getAbiquoUserByLogin(user1.getNick());
        Assert.assertNotNull(user);
    }

    @Test
    public void getUserByAuth()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(user1.getEnterprise());
        for (Privilege p : user1.getRole().getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(user1.getRole());
        entitiesToPersist.add(user1);
        ds().persistAll(entitiesToPersist.toArray());

        UserDAO dao = createDaoForRollbackTransaction();

        User user = dao.getUserByAuth(user1.getNick(), User.AuthType.ABIQUO);
        Assert.assertNotNull(user);
    }

    @Test
    public void existAnyUserWithNickAndAuth()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(user1.getEnterprise());
        for (Privilege p : user1.getRole().getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(user1.getRole());
        entitiesToPersist.add(user1);
        ds().persistAll(entitiesToPersist.toArray());

        UserDAO dao = createDaoForRollbackTransaction();

        boolean already = dao.existAnyUserWithNickAndAuth(user1.getNick(), User.AuthType.ABIQUO);
        Assert.assertTrue(already);
    }

    // Virtual Datacenter
    @Test
    public void sysadminUserIsAllowedToUseOwnVDC()
    {
        Map<String, Object> map = setupSysadminUser();
        User user = (User) map.get("sysadmin");
        String[] ps = (String[]) map.get("sysadmin.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("sysadmin.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void infrastructureUserIsAllowedToUseOwnVDC()
    {
        Map<String, Object> map = setupInfrastructureUser();
        User user = (User) map.get("infUser");
        String[] ps = (String[]) map.get("infUser.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("infUser.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void userIsAllowedToUseOwnVDC()
    {
        Map<String, Object> map = setupNormalUser(null);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("user.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void sysadminIsAllowedToUseOtherEnterpriseVDC()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(null);
        User user = (User) map.get("sysadmin");
        String[] ps = (String[]) map.get("sysadmin.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("user.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void infrastructureUserIsAllowedToUseOtherEnterpriseVDC()
    {
        Map<String, Object> map = setupSysadminUserAndInfrastructureUser();
        User user = (User) map.get("infUser");
        String[] ps = (String[]) map.get("infUser.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("sysadmin.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void userIsNOTAllowedToUserOtherEnterpriseVDC()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(null);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("sysadmin.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertFalse(isAllowed);
    }

    @Test
    public void userIsNOTAllowedToUserOwnEnterpriseVDCRestricted()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(true);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("user.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertFalse(isAllowed);
    }

    @Test
    public void userIsAllowedToUserOwnEnterpriseVDCRestricted()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(false);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        VirtualDatacenter vdc = (VirtualDatacenter) map.get("user.virtualdatacenter");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToUseVirtualDatacenter(user.getNick(), user.getAuthType().name(), ps,
                vdc.getId());
        Assert.assertTrue(isAllowed);
    }

    // Enterprise
    @Test
    public void sysadminUserIsAllowedToOwnEnterprise()
    {
        Map<String, Object> map = setupSysadminUser();
        User user = (User) map.get("sysadmin");
        String[] ps = (String[]) map.get("sysadmin.privileges");
        Enterprise ent = (Enterprise) map.get("sysadmin.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void userIsAllowedToUseOwnEnterprise()
    {
        Map<String, Object> map = setupNormalUser(null);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        Enterprise ent = (Enterprise) map.get("user.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void infrastructureUserIsAllowedToUseOwnEnteprise()
    {
        Map<String, Object> map = setupInfrastructureUser();
        User user = (User) map.get("infUser");
        String[] ps = (String[]) map.get("infUser.privileges");
        Enterprise ent = (Enterprise) map.get("infUser.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertTrue(isAllowed);

    }

    @Test
    public void sysadminIsAllowedToUseOtherEnterprise()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(null);
        User user = (User) map.get("sysadmin");
        String[] ps = (String[]) map.get("sysadmin.privileges");
        Enterprise ent = (Enterprise) map.get("user.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertTrue(isAllowed);
    }

    @Test
    public void userIsNOTAllowedToUseOtherEnterprise()
    {
        Map<String, Object> map = setupSysadminUserAndNormalUser(null);
        User user = (User) map.get("user");
        String[] ps = (String[]) map.get("user.privileges");
        Enterprise ent = (Enterprise) map.get("sysadmin.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertFalse(isAllowed);
    }

    @Test
    public void infrastructureUserIsAllowedToUseOtherEnteprise()
    {
        Map<String, Object> map = setupSysadminUserAndInfrastructureUser();
        User user = (User) map.get("infUser");
        String[] ps = (String[]) map.get("infUser.privileges");
        Enterprise ent = (Enterprise) map.get("sysadmin.enterprise");

        UserDAO dao = createDaoForRollbackTransaction();

        boolean isAllowed =
            dao.isUserAllowedToEnterprise(user.getNick(), user.getAuthType().name(), ps,
                ent.getId());
        Assert.assertTrue(isAllowed);

    }

    // ----------------------- //
    // Private usefull methods //
    // ----------------------- //
    private Map<String, Object> setupSysadminUser()
    {
        Map<String, Object> map = new HashMap<String, Object>();

        // sysadmin
        User sysadmin = eg().createInstance(User.AuthType.ABIQUO);
        VirtualDatacenter sysadminVdc =
            eg().virtualDatacenterGenerator.createInstance(sysadmin.getEnterprise());
        List<Object> sysadminEntitiesToPersist = new ArrayList<Object>();
        List<Privilege> sysadminPrivileges = sysadmin.getRole().getPrivileges();

        sysadminEntitiesToPersist.add(sysadmin.getEnterprise());
        String[] sysadminPrivs = new String[sysadminPrivileges.size()];
        for (int i = 0; i < sysadminPrivileges.size(); i++)
        {
            Privilege p = sysadminPrivileges.get(i);
            sysadminEntitiesToPersist.add(p);
            sysadminPrivs[i] = p.getName();
        }
        sysadminEntitiesToPersist.add(sysadmin.getRole());
        sysadminEntitiesToPersist.add(sysadmin);
        eg().virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(sysadminVdc,
            sysadminEntitiesToPersist);
        sysadminEntitiesToPersist.add(sysadminVdc);
        ds().persistAll(sysadminEntitiesToPersist.toArray());

        map.put("sysadmin", sysadmin);
        map.put("sysadmin.virtualdatacenter", sysadminVdc);
        map.put("sysadmin.privileges", sysadminPrivs);
        map.put("sysadmin.enterprise", sysadmin.getEnterprise());

        return map;
    }

    private Map<String, Object> setupNormalUser(final Boolean restrictVdc)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        // user
        Role userRole = eg().roleGenerator.createInstance(Privileges.simpleRole());
        User user = eg().createInstance(User.AuthType.ABIQUO, userRole);
        VirtualDatacenter userVdc =
            eg().virtualDatacenterGenerator.createInstance(user.getEnterprise());

        List<Object> userEntitiesToPersist = new ArrayList<Object>();
        List<Privilege> userPrivileges = user.getRole().getPrivileges();
        userEntitiesToPersist.add(user.getEnterprise());
        eg().virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(userVdc,
            userEntitiesToPersist);
        userEntitiesToPersist.add(userVdc);
        ds().persistAll(userEntitiesToPersist.toArray());

        if (restrictVdc != null)
        {
            if (restrictVdc)
            {
                // not allowed
                user.setAvailableVirtualDatacenters(userVdc.getId() - 1 + "," + userVdc.getId() + 1);
            }
            else
            {
                // allowed
                user.setAvailableVirtualDatacenters(userVdc.getId() - 1 + "," + userVdc.getId());
            }
        }

        userEntitiesToPersist = new ArrayList<Object>();
        String[] userPrivs = new String[userPrivileges.size()];
        for (int i = 0; i < userPrivileges.size(); i++)
        {
            Privilege p = userPrivileges.get(i);
            userEntitiesToPersist.add(p);
            userPrivs[i] = p.getName();
        }
        userEntitiesToPersist.add(user.getRole());
        userEntitiesToPersist.add(user);
        ds().persistAll(userEntitiesToPersist.toArray());

        map.put("user", user);
        map.put("user.virtualdatacenter", userVdc);
        map.put("user.privileges", userPrivs);
        map.put("user.enterprise", user.getEnterprise());
        return map;

    }

    private Map<String, Object> setupInfrastructureUser()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        // user
        Role userRole = eg().roleGenerator.createInstance(Privileges.PHYS_DC_RETRIEVE_DETAILS);
        User user = eg().createInstance(User.AuthType.ABIQUO, userRole);
        user.setNick("infUser");
        VirtualDatacenter userVdc =
            eg().virtualDatacenterGenerator.createInstance(user.getEnterprise());

        List<Object> userEntitiesToPersist = new ArrayList<Object>();
        List<Privilege> userPrivileges = user.getRole().getPrivileges();
        userEntitiesToPersist.add(user.getEnterprise());
        eg().virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(userVdc,
            userEntitiesToPersist);
        userEntitiesToPersist.add(userVdc);
        ds().persistAll(userEntitiesToPersist.toArray());

        userEntitiesToPersist = new ArrayList<Object>();
        String[] userPrivs = new String[userPrivileges.size()];
        for (int i = 0; i < userPrivileges.size(); i++)
        {
            Privilege p = userPrivileges.get(i);
            userEntitiesToPersist.add(p);
            userPrivs[i] = p.getName();
        }
        userEntitiesToPersist.add(user.getRole());
        userEntitiesToPersist.add(user);
        ds().persistAll(userEntitiesToPersist.toArray());

        map.put("infUser", user);
        map.put("infUser.virtualdatacenter", userVdc);
        map.put("infUser.privileges", userPrivs);
        map.put("infUser.enterprise", user.getEnterprise());
        return map;

    }

    private Map<String, Object> setupSysadminUserAndNormalUser(final Boolean restrictVdc)
    {
        Map<String, Object> map = setupSysadminUser();
        map.putAll(setupNormalUser(restrictVdc));
        return map;
    }

    private Map<String, Object> setupSysadminUserAndInfrastructureUser()
    {
        Map<String, Object> map = setupSysadminUser();
        map.putAll(setupInfrastructureUser());
        return map;
    }
}
