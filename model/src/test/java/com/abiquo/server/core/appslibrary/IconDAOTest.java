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

package com.abiquo.server.core.appslibrary;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class IconDAOTest extends DefaultDAOTestBase<IconDAO, Icon>
{
    private VirtualImageGenerator virtualImageGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        virtualImageGenerator = new VirtualImageGenerator(getSeed());
    }

    @Override
    protected IconDAO createDao(final EntityManager entityManager)
    {
        return new IconDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Icon> createEntityInstanceGenerator()
    {
        return new IconGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public IconGenerator eg()
    {
        return (IconGenerator) super.eg();
    }

    @Test
    public void testFindByPath()
    {
        Icon icon = eg().createUniqueInstance();
        ds().persistAll(icon);

        IconDAO dao = createDaoForRollbackTransaction();

        Icon result = dao.findByPath(icon.getPath());
        assertNotNull(result);
        assertEquals(result.getName(), icon.getName());
        assertEquals(result.getPath(), icon.getPath());

        result = dao.findByPath(icon.getName() + "UNEXISTING");
        assertNull(result);
    }

    @Test
    public void testIconNotInUseByVirtualImages()
    {
        Icon icon = eg().createUniqueInstance();
        ds().persistAll(icon);

        IconDAO dao = createDaoForRollbackTransaction();

        boolean result = dao.iconInUseByVirtualImages(icon);
        assertFalse(result);
    }

    @Test
    public void testIconInUseByVirtualImages()
    {
        Icon icon = eg().createUniqueInstance();
        VirtualImage virtualImage = virtualImageGenerator.createUniqueInstance();
        virtualImage.setIcon(icon);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        virtualImageGenerator.addAuxiliaryEntitiesToPersist(virtualImage, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, virtualImage);

        IconDAO dao = createDaoForRollbackTransaction();

        boolean result = dao.iconInUseByVirtualImages(icon);
        assertTrue(result);
    }
}
