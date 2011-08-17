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

package com.abiquo.server.core.cloud;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.persistence.DefaultJpaDataAccessTestBase;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementGenerator;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

public class VirtualDatacenterRepTest extends DefaultJpaDataAccessTestBase
{
    private VirtualDatacenterGenerator virtualDatacenterGenerator;

    private VLANNetworkGenerator vlanGenerator;

    private IpPoolManagementGenerator ipGenerator;

    private RemoteServiceGenerator remoteServiceGenerator;

    private VirtualDatacenterGenerator eg()
    {
        return this.virtualDatacenterGenerator;
    }

    private VLANNetworkGenerator vlanEg()
    {
        return vlanGenerator;
    }

    @Override
    @BeforeMethod
    public void methodSetUp()
    {
        super.methodSetUp();
        this.virtualDatacenterGenerator = new VirtualDatacenterGenerator(getSeed());
        this.vlanGenerator = new VLANNetworkGenerator(getSeed());
        this.ipGenerator = new IpPoolManagementGenerator(getSeed());
        this.remoteServiceGenerator = new RemoteServiceGenerator(getSeed());
    }

    @Test
    public void test_findVirtualDatacenterById()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VirtualDatacenter vdc = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vdc, additionalEntities);

        persistAll(ds(), additionalEntities, vdc);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findById(vdc.getId()));
    }

    @Test
    public void test_findAllDatacenters() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VirtualDatacenter vdc1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vdc1, additionalEntities);

        VirtualDatacenter vdc2 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vdc2, additionalEntities);

        persistAll(ds(), additionalEntities, vdc1, vdc2);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());

        List<VirtualDatacenter> result = (List<VirtualDatacenter>) rep.findAll();
        assertNotNull(result);
        assertEquals(result.size(), 2);
    }

    @Test
    public void testFindVLANNetworkById()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VLANNetwork lan = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(lan, additionalEntities);

        persistAll(ds(), additionalEntities, lan);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findVlanById(lan.getId()));
    }

    @Test
    public void testFindAllVLANNetworks()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VLANNetwork n1 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n1, additionalEntities);
        VLANNetwork n2 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n2, additionalEntities);

        persistAll(ds(), additionalEntities, n1, n2);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Collection<VLANNetwork> lans = rep.findAllVlans();
        Assert.assertNotNull(lans);
        Assert.assertEquals(lans.size(), 2);
    }

    @Test
    public void testFindAllVLANNetworksByVirtualDatacenter()
    {
        List<Object> additionalEntities = new ArrayList<Object>();

        VirtualDatacenter vdc1 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc1, additionalEntities);

        VLANNetwork n1_1 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n1_1, additionalEntities);
        VLANNetwork n1_2 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n1_2, additionalEntities);

        n1_1.setNetwork(vdc1.getNetwork());
        n1_2.setNetwork(vdc1.getNetwork());

        VirtualDatacenter vdc2 = virtualDatacenterGenerator.createUniqueInstance();
        this.virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc2, additionalEntities);

        VLANNetwork n2_1 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n2_1, additionalEntities);

        n2_1.setNetwork(vdc2.getNetwork());

        persistAll(ds(), additionalEntities, vdc1, vdc2, n1_1, n1_2, n2_1);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Collection<VLANNetwork> networks = rep.findVlansByVirtualDatacener(vdc1);
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

        VLANNetwork n2_1 = vlanEg().createUniqueInstance();
        vlanEg().addAuxiliaryEntitiesToPersist(n2_1, additionalEntities);

        n2_1.setNetwork(vdc2.getNetwork());

        persistAll(ds(), additionalEntities, vdc1, vdc2, n2_1);

        VirtualDatacenterRep rep =
            new VirtualDatacenterRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Collection<VLANNetwork> networks = rep.findVlansByVirtualDatacener(vdc1);
        Assert.assertTrue(networks.isEmpty());
    }

    @Test
    public void deleteVirtualDatacenter()
    {
        VirtualDatacenter vdc = virtualDatacenterGenerator.createUniqueInstance();
        IpPoolManagement ip = ipGenerator.createInstance(vdc, vdc.getNetwork());
        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE,
                vdc.getDatacenter());
        ip.getDhcp().setRemoteService(rs);

        List<Object> additionalEntities = new ArrayList<Object>();
        ipGenerator.addAuxiliaryEntitiesToPersist(ip, additionalEntities);

        persistAll(ds(), additionalEntities, ip);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        VirtualDatacenterRep rep = new VirtualDatacenterRep(em);
        VirtualDatacenter vdc1 = rep.findById(vdc.getId());

        try
        {
            rep.delete(vdc1);
            EntityManagerHelper.commitAndClose(em);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
