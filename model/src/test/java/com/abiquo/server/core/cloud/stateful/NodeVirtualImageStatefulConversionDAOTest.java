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

package com.abiquo.server.core.cloud.stateful;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class NodeVirtualImageStatefulConversionDAOTest extends
    DefaultDAOTestBase<NodeVirtualImageStatefulConversionDAO, NodeVirtualImageStatefulConversion>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected NodeVirtualImageStatefulConversionDAO createDao(final EntityManager entityManager)
    {
        return new NodeVirtualImageStatefulConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<NodeVirtualImageStatefulConversion> createEntityInstanceGenerator()
    {
        return new NodeVirtualImageStatefulConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public NodeVirtualImageStatefulConversionGenerator eg()
    {
        return (NodeVirtualImageStatefulConversionGenerator) super.eg();
    }

    @Test
    public void test_findNodeVirtualImageStatefulConversionsByVirtualAppliance()
    {
        NodeVirtualImageStatefulConversion nvisc = eg().createUniqueInstance();

        // linking same entities (dc, ent, vapp)
        VirtualApplianceStatefulConversion vasc = nvisc.getVirtualApplianceStatefulConversion();
        Enterprise ent = vasc.getVirtualAppliance().getVirtualDatacenter().getEnterprise();
        Datacenter dc = vasc.getVirtualAppliance().getVirtualDatacenter().getDatacenter();
        VirtualImage vi = nvisc.getNodeVirtualImage().getVirtualImage();
        VirtualMachine vm = nvisc.getNodeVirtualImage().getVirtualMachine();

        vasc.getUser().setEnterprise(ent);
        vi.setEnterprise(ent);
        vm.setEnterprise(ent);
        vasc.getUser().getRole().setEnterprise(ent);

        nvisc.getTier().setDatacenter(dc);
        vi.getRepository().setDatacenter(dc);

        nvisc.getNodeVirtualImage().setVirtualAppliance(vasc.getVirtualAppliance());

        vm.setUser(vasc.getUser());
        vasc.getUser().getRole().setPrivileges(null);

        // persist
        ds().persistAll(dc, ent, vasc.getVirtualAppliance().getVirtualDatacenter(),
            vasc.getVirtualAppliance(), vasc.getUser().getRole(), vasc.getUser(), vasc,
            vi.getCategory(), vi.getRepository(), vi, vm, nvisc.getNodeVirtualImage(),
            nvisc.getTier(), nvisc);

        NodeVirtualImageStatefulConversionDAO dao = createDaoForRollbackTransaction();

        Collection<NodeVirtualImageStatefulConversion> list =
            dao.findByVirtualAppliance(vasc.getVirtualAppliance());

        assertTrue(!list.isEmpty());
        assertSize(list, 1);
        assertEquals(list.iterator().next().getId(), nvisc.getId());
    }
}
