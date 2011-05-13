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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    protected UserDAO createDao(EntityManager entityManager)
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
        ds().persistAll(user.getEnterprise(), user.getRole(), user, userWithoutSession);

        UserDAO dao = createDaoForRollbackTransaction();

        Collection<User> users = dao.find(user.getEnterprise(), null, null, false, true, 0, 25);
        AssertEx.assertSize(users, 1);

        users = dao.find(null, null, null, false, true, 0, 25);
        AssertEx.assertSize(users, 1);

        users = dao.find(user.getEnterprise(), null, null, false, false, 0, 25);
        AssertEx.assertSize(users, 2);
    }

    @Test
    public void getAbiquoUserByLogin()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        ds().persistAll(user1.getEnterprise(), user1.getRole(), user1);

        UserDAO dao = createDaoForRollbackTransaction();

        User user = dao.getAbiquoUserByLogin(user1.getNick());
        AssertEx.assertNotNull(user);
    }

    @Test
    public void getUserByAuth()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        ds().persistAll(user1.getEnterprise(), user1.getRole(), user1);

        UserDAO dao = createDaoForRollbackTransaction();

        User user = dao.getUserByAuth(user1.getNick(), User.AuthType.ABIQUO);
        AssertEx.assertNotNull(user);
    }

    @Test
    public void existAnyUserWithNickAndAuth()
    {
        User user1 = eg().createInstance(User.AuthType.ABIQUO);
        ds().persistAll(user1.getEnterprise(), user1.getRole(), user1);

        UserDAO dao = createDaoForRollbackTransaction();

        boolean already = dao.existAnyUserWithNickAndAuth(user1.getNick(), User.AuthType.ABIQUO);
        AssertEx.assertTrue(already);
    }
}
