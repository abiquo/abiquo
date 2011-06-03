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
import java.util.List;

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
}
