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

package com.abiquo.server.core.infrastructure.network;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VLANNetworkDAOTest extends DefaultDAOTestBase<VLANNetworkDAO, VLANNetwork>
{

    private VirtualDatacenterGenerator virtualDatacenterGenerator;

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.virtualDatacenterGenerator = new VirtualDatacenterGenerator(getSeed());
    }

    @Override
    protected VLANNetworkDAO createDao(EntityManager em)
    {
        return new VLANNetworkDAO(em);
    }

    @Override
    protected PersistentInstanceTester<VLANNetwork> createEntityInstanceGenerator()
    {
        return new VLANNetworkGenerator(getSeed());
    }

    @Test
    public void testFindVLANNetworksByDatacenter()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VirtualDatacenter vdc1 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc1, additionalEntities);

        VLANNetwork n1_1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(n1_1, additionalEntities);
        VLANNetwork n1_2 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(n1_2, additionalEntities);

        n1_1.setNetwork(vdc1.getNetwork());
        n1_2.setNetwork(vdc1.getNetwork());

        VirtualDatacenter vdc2 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc2, additionalEntities);

        VLANNetwork n2_1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(n2_1, additionalEntities);

        n2_1.setNetwork(vdc2.getNetwork());

        persistAll(ds(), additionalEntities, vdc1, vdc2, n1_1, n1_2, n2_1);

        VLANNetworkDAO dao = createDaoForRollbackTransaction();
        List<VLANNetwork> networks = dao.findVLANNetworks(reload(dao, vdc1));
        Assert.assertFalse(networks.isEmpty());
        Assert.assertEquals(networks.size(), 2);
    }

    @Test
    public void testFindZeroVLANNetworksByDatacenter()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VirtualDatacenter vdc1 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc1, additionalEntities);

        VirtualDatacenter vdc2 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc2, additionalEntities);

        VLANNetwork n2_1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(n2_1, additionalEntities);

        n2_1.setNetwork(vdc2.getNetwork());

        persistAll(ds(), additionalEntities, vdc1, vdc2, n2_1);

        VLANNetworkDAO dao = createDaoForRollbackTransaction();
        List<VLANNetwork> networks = dao.findVLANNetworks(reload(dao, vdc1));
        Assert.assertTrue(networks.isEmpty());
    }
}
