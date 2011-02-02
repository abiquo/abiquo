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
package com.abiquo.server.core.config;

import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class SystemPropertyDAOTest extends DefaultDAOTestBase<SystemPropertyDAO, SystemProperty>
{

    @Override
    protected SystemPropertyDAO createDao(EntityManager entityManager)
    {
        return new SystemPropertyDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<SystemProperty> createEntityInstanceGenerator()
    {
        return new SystemPropertyGenerator(getSeed());
    }

    @Test
    public void test_existsAnyWithName()
    {
        SystemProperty systemProperty = createUniqueEntity();
        systemProperty.setName("A name");
        ds().persistAll(systemProperty);

        SystemPropertyDAO dao = createDaoForRollbackTransaction();

        assertTrue(dao.existsAnyWithName("A name"));
        assertFalse(dao.existsAnyWithName("UNEXISTING_NAME"));
    }

    @Test
    public void test_existsAnyOtherWithName()
    {
        SystemProperty property = createUniqueEntity();
        SystemProperty property2 = createUniqueEntity();
        property.setName("A name");
        property2.setName("Second name");
        ds().persistAll(property, property2);

        SystemPropertyDAO dao = createDaoForRollbackTransaction();
        property = dao.findById(property.getId());

        assertFalse(dao.existsAnyOtherWithName(property, "UNEXISTING_NAME"));
        assertFalse(dao.existsAnyOtherWithName(property, "A name"));
        assertTrue(dao.existsAnyOtherWithName(property, "Second name"));
    }

    @Test
    public void test_findByName()
    {
        SystemProperty systemProperty = createUniqueEntity();
        systemProperty.setName("Some name");
        ds().persistAll(systemProperty);

        SystemPropertyDAO dao = createDaoForRollbackTransaction();
        SystemProperty found = dao.findByName("Some name");

        assertEquals(dao.findByName("UNEXISTING_NAME"), null);
        eg().assertAllPropertiesEqual(systemProperty, found);
    }

    @Test
    public void test_findByComponent()
    {
        SystemProperty server1 = createUniqueEntity();
        SystemProperty server2 = createUniqueEntity();
        SystemProperty server3 = createUniqueEntity();
        SystemProperty vsm = createUniqueEntity();
        SystemProperty noComponent = createUniqueEntity();

        server1.setName("server.prop1");
        server2.setName("server.prop2");
        server3.setName("server.prop3");
        vsm.setName("vsm.prop1");
        noComponent.setName("serverNoComponent");

        ds().persistAll(server1, server2, server3, vsm);

        SystemPropertyDAO dao = createDaoForRollbackTransaction();

        List<SystemProperty> serverProps = dao.findByComponent("server");
        List<SystemProperty> vsmProps = dao.findByComponent("vsm");
        List<SystemProperty> clientProps = dao.findByComponent("client");

        assertSize(serverProps, 3);
        assertSize(vsmProps, 1);
        assertSize(clientProps, 0);
    }

}
