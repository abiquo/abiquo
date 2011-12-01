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

package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class InitiatorMappingDAOTest extends
    DefaultDAOTestBase<InitiatorMappingDAO, InitiatorMapping>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        // FIXME: Remember to add all entities that have to be removed during tearDown in the
        // method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected InitiatorMappingDAO createDao(EntityManager entityManager)
    {
        return new InitiatorMappingDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<InitiatorMapping> createEntityInstanceGenerator()
    {
        return new InitiatorMappingGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public InitiatorMappingGenerator eg()
    {
        return (InitiatorMappingGenerator) super.eg();
    }

    @Test
    public void testfindByVolumeAndInitiator()
    {
        InitiatorMapping m = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(m, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, m);
        InitiatorMappingDAO dao = createDaoForRollbackTransaction();
        String initm = m.getInitiatorIqn();
        Integer idVm = m.getVolumeManagement().getId();

        InitiatorMapping im1 = dao.findByVolumeAndInitiator(idVm, initm);
        assertNotNull(im1);
        eg().assertAllPropertiesEqual(m, im1);
    }

}
