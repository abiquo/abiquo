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

package com.abiquo.server.core.infrastructure;

import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class UcsRackDAOTest extends DefaultDAOTestBase<UcsRackDAO, UcsRack>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        // FIXME: Remember to add all entities that have to be removed during tearDown in the
        // method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected UcsRackDAO createDao(EntityManager entityManager)
    {
        return new UcsRackDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<UcsRack> createEntityInstanceGenerator()
    {
        return new UcsRackGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public UcsRackGenerator eg()
    {
        return (UcsRackGenerator) super.eg();
    }

    @Test
    public void findAllUcsRacksByDatacenter()
    {
        DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
        UcsRackGenerator ucsRackGenerator = new UcsRackGenerator(getSeed());

        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
        UcsRack UcsRack2_1 = ucsRackGenerator.createInstance(datacenter2);
        UcsRack2_1.setName("ucsRack1");
        UcsRack UcsRack2_2 = ucsRackGenerator.createInstance(datacenter2);
        UcsRack2_2.setName("ucsRack2");
        ds().persistAll(datacenter1, datacenter2, UcsRack2_1, UcsRack2_2);

        UcsRackDAO dao = createDaoForRollbackTransaction();
        List<UcsRack> ucsRacks = dao.findAllUcsRacksByDatacenter(reload(dao, datacenter1));
        Assert.assertTrue(ucsRacks.isEmpty());

        ucsRacks = dao.findAllUcsRacksByDatacenter(reload(dao, datacenter2));
        try
        {
            assertEqualsPropertyForList(Rack.NAME_PROPERTY, ucsRacks, "ucsRack1", "ucsRack2");
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

}
