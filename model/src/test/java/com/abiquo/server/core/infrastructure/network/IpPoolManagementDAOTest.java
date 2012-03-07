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
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class IpPoolManagementDAOTest extends
    DefaultDAOTestBase<IpPoolManagementDAO, IpPoolManagement>
{
    private VirtualDatacenterGenerator virtualDatacenterGenerator;

    private VLANNetworkGenerator vlanNetworkGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.virtualDatacenterGenerator = new VirtualDatacenterGenerator(getSeed());
        this.vlanNetworkGenerator = new VLANNetworkGenerator(getSeed());
    }

    @Override
    protected IpPoolManagementDAO createDao(final EntityManager entityManager)
    {
        return new IpPoolManagementDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<IpPoolManagement> createEntityInstanceGenerator()
    {
        return new IpPoolManagementGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public IpPoolManagementGenerator eg()
    {
        return (IpPoolManagementGenerator) super.eg();
    }

    @Test
    public void findNextIpByPrivateVLANAvailableSuccess()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement ipPoolManagement = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement, entities);

        IpPoolManagement excludedIpPoolManagement = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement, entities);

        persistAll(ds(), entities, ipPoolManagement, excludedIpPoolManagement);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(ipPoolManagement.getVlanNetwork().getId(),
                excludedIpPoolManagement.getIp());

        assertFalse(excludedIpPoolManagement.equals(available));
    }

    @Test
    public void findNextIpByPrivateVLANExcludeMultiple()
    {
        List<Object> entities = new ArrayList<Object>();
        IpPoolManagement ipPoolManagement = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement, entities);

        IpPoolManagement excludedIpPoolManagement1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement1, entities);

        IpPoolManagement excludedIpPoolManagement2 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement2, entities);

        IpPoolManagement excludedIpPoolManagement3 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement3, entities);

        persistAll(ds(), entities, ipPoolManagement, excludedIpPoolManagement1,
            excludedIpPoolManagement2, excludedIpPoolManagement3);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(excludedIpPoolManagement1.getVlanNetwork().getId(),
                excludedIpPoolManagement1.getIp(), excludedIpPoolManagement2.getIp(),
                excludedIpPoolManagement3.getIp());

        assertFalse(excludedIpPoolManagement1.equals(available)
            || excludedIpPoolManagement2.equals(available)
            || excludedIpPoolManagement3.equals(available));
    }

    @Test
    public void findNextIpByPrivateVLANExcludeMultipleNotAvailable()
    {
        List<Object> entities = new ArrayList<Object>();
        IpPoolManagement ipPoolManagement = eg().createUniqueInstance();
        ipPoolManagement.setAvailable(Boolean.FALSE);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement, entities);

        IpPoolManagement excludedIpPoolManagement1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement1, entities);

        IpPoolManagement excludedIpPoolManagement2 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement2, entities);

        IpPoolManagement excludedIpPoolManagement3 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement3, entities);

        persistAll(ds(), entities, ipPoolManagement, excludedIpPoolManagement1,
            excludedIpPoolManagement2, excludedIpPoolManagement3);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(excludedIpPoolManagement1.getVlanNetwork().getId(),
                excludedIpPoolManagement1.getIp(), excludedIpPoolManagement2.getIp(),
                excludedIpPoolManagement3.getIp());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANNotIp()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement excludedIpPoolManagement = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement, entities);

        persistAll(ds(), entities, excludedIpPoolManagement);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(excludedIpPoolManagement.getVlanNetwork().getId(),
                excludedIpPoolManagement.getIp());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANExcludeMultipleNotIp()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement excludedIpPoolManagement1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement1, entities);

        IpPoolManagement excludedIpPoolManagement2 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement2, entities);

        IpPoolManagement excludedIpPoolManagement3 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(excludedIpPoolManagement3, entities);

        persistAll(ds(), entities, excludedIpPoolManagement1, excludedIpPoolManagement2,
            excludedIpPoolManagement3);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(excludedIpPoolManagement1.getVlanNetwork().getId(),
                excludedIpPoolManagement1.getIp(), excludedIpPoolManagement2.getIp(),
                excludedIpPoolManagement3.getIp());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANAvailableAnotherVdc()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement ipPoolManagement1 = eg().createUniqueInstance();
        ipPoolManagement1.setAvailable(Boolean.FALSE);

        VirtualDatacenter vdc1 = virtualDatacenterGenerator.createUniqueInstance();
        ipPoolManagement1.setVirtualDatacenter(vdc1);

        virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc1, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement1, entities);

        IpPoolManagement ipPoolManagement2 = eg().createUniqueInstance();
        ipPoolManagement2.setAvailable(Boolean.TRUE);

        VirtualDatacenter vdc2 = virtualDatacenterGenerator.createUniqueInstance();
        ipPoolManagement2.setVirtualDatacenter(vdc2);

        virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc2, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement2, entities);

        persistAll(ds(), entities, vdc1, vdc2, ipPoolManagement1, ipPoolManagement2);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(ipPoolManagement1.getVlanNetwork().getId());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANNotIpAvailableAnotherVdc()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement ipPoolManagement1 = eg().createUniqueInstance();
        ipPoolManagement1.setAvailable(Boolean.FALSE);

        VirtualDatacenter vdc1 = virtualDatacenterGenerator.createUniqueInstance();
        ipPoolManagement1.setVirtualDatacenter(vdc1);

        virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc1, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement1, entities);

        IpPoolManagement ipPoolManagement2 = eg().createUniqueInstance();
        ipPoolManagement2.setAvailable(Boolean.FALSE);

        VirtualDatacenter vdc2 = virtualDatacenterGenerator.createUniqueInstance();
        ipPoolManagement2.setVirtualDatacenter(vdc2);

        virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(vdc2, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement2, entities);

        persistAll(ds(), entities, vdc1, vdc2, ipPoolManagement1, ipPoolManagement2);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(ipPoolManagement1.getVlanNetwork()
                .getId());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANIpAvailableAnotherVlanNetwork()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement ipPoolManagement1 = eg().createUniqueInstance();
        ipPoolManagement1.setAvailable(Boolean.FALSE);

        VLANNetwork vlan1 = vlanNetworkGenerator.createUniqueInstance();
        ipPoolManagement1.setVlanNetwork(vlan1);

        vlanNetworkGenerator.addAuxiliaryEntitiesToPersist(vlan1, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement1, entities);

        IpPoolManagement ipPoolManagement2 = eg().createUniqueInstance();
        ipPoolManagement2.setAvailable(Boolean.TRUE);

        VLANNetwork vlan2 = vlanNetworkGenerator.createUniqueInstance();
        ipPoolManagement1.setVlanNetwork(vlan2);

        vlanNetworkGenerator.addAuxiliaryEntitiesToPersist(vlan2, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement2, entities);

        persistAll(ds(), entities, vlan1, vlan2, ipPoolManagement1, ipPoolManagement2);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(vlan1.getId());

        Assert.assertNull(available);
    }

    @Test
    public void findNextIpByPrivateVLANNotIpAvailableAnotherVlanNetwork()
    {
        List<Object> entities = new ArrayList<Object>();

        IpPoolManagement ipPoolManagement1 = eg().createUniqueInstance();
        ipPoolManagement1.setAvailable(Boolean.FALSE);

        VLANNetwork vlan1 = vlanNetworkGenerator.createUniqueInstance();
        ipPoolManagement1.setVlanNetwork(vlan1);

        vlanNetworkGenerator.addAuxiliaryEntitiesToPersist(vlan1, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement1, entities);

        IpPoolManagement ipPoolManagement2 = eg().createUniqueInstance();
        ipPoolManagement2.setAvailable(Boolean.FALSE);

        VLANNetwork vlan2 = vlanNetworkGenerator.createUniqueInstance();
        ipPoolManagement1.setVlanNetwork(vlan2);

        vlanNetworkGenerator.addAuxiliaryEntitiesToPersist(vlan2, entities);
        eg().addAuxiliaryEntitiesToPersist(ipPoolManagement2, entities);

        persistAll(ds(), entities, vlan1, vlan2, ipPoolManagement1, ipPoolManagement2);

        IpPoolManagementDAO dao = createDaoForRollbackTransaction();
        IpPoolManagement available =
            dao.findNextIpAvailable(vlan1.getId());

        Assert.assertNull(available);
    }
}