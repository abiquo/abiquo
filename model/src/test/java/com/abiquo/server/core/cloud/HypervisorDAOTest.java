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

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class HypervisorDAOTest extends DefaultDAOTestBase<HypervisorDAO, Hypervisor>
{

    @Override
    protected HypervisorDAO createDao(final EntityManager entityManager)
    {
        return new HypervisorDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Hypervisor> createEntityInstanceGenerator()
    {
        return new HypervisorGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public HypervisorGenerator eg()
    {
        return (HypervisorGenerator) super.eg();
    }

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Test
    public void existAnyWithIp()
    {
        Hypervisor hypervisor = eg().createUniqueInstance();

        ds().persistAll(hypervisor.getMachine().getDatacenter(), hypervisor.getMachine().getRack(),
            hypervisor.getMachine(), hypervisor);
        /** ##### ##### */

        HypervisorDAO dao = createDaoForRollbackTransaction();

        Assert.assertTrue(dao.existsAnyWithIpAndDatacenter(hypervisor.getIp(), hypervisor
            .getMachine().getDatacenter().getId()));

        Assert.assertTrue(dao.existsAnyWithIpServiceAndDatacenter(hypervisor.getIpService(),
            hypervisor.getMachine().getDatacenter().getId()));
    }

    @Test
    public void existAnyWithIpSameDatacenter()
    {
        Hypervisor hypervisor = eg().createUniqueInstance();

        ds().persistAll(hypervisor.getMachine().getDatacenter(), hypervisor.getMachine().getRack(),
            hypervisor.getMachine(), hypervisor);
        /** ##### ##### */

        HypervisorDAO dao = createDaoForRollbackTransaction();

        Assert.assertTrue(dao.existsAnyWithIpAndDatacenter(hypervisor.getIp(), hypervisor
            .getMachine().getDatacenter().getId()));
    }

}
