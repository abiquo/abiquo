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

package com.abiquo.server.core.pricing;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class PricingTemplateDAOTest extends DefaultDAOTestBase<PricingTemplateDAO, PricingTemplate>
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
    protected PricingTemplateDAO createDao(final EntityManager entityManager)
    {
        return new PricingTemplateDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<PricingTemplate> createEntityInstanceGenerator()
    {
        return new PricingTemplateGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public PricingTemplateGenerator eg()
    {
        return (PricingTemplateGenerator) super.eg();
    }

    @Test
    public void findPricingTemplates()
    {
        PricingTemplate pt1 = eg().createUniqueInstance();
        PricingTemplate pt2 = eg().createUniqueInstance();

        ds().persistAll(pt1.getCurrency(), pt2.getCurrency(), pt1, pt2);

        PricingTemplateDAO dao = createDaoForRollbackTransaction();

        Collection<PricingTemplate> pts = dao.find(null, null, false, 0, 25);
        AssertEx.assertSize(pts, 2);

    }

    @Test
    public void findAllPricingTemplatesByName()
    {
        PricingTemplate pt1 = eg().createInstance("pt1");
        PricingTemplate pt2 = eg().createInstance("pt2");

        ds().persistAll(pt1.getCurrency(), pt2.getCurrency(), pt1, pt2);

        PricingTemplateDAO dao = createDaoForRollbackTransaction();

        Collection<PricingTemplate> pts = dao.findAllPricingTemplateByName("pt1");
        AssertEx.assertSize(pts, 1);
        pts = dao.findAllPricingTemplateByName("pt2");
        AssertEx.assertSize(pts, 1);

    }

    @Test
    public void findAllPricingTemplatesByCurrency()
    {
        PricingTemplate pt1 = eg().createInstance("pt1");
        PricingTemplate pt2 = eg().createInstance("pt2");

        ds().persistAll(pt1.getCurrency(), pt2.getCurrency(), pt1, pt2);

        PricingTemplateDAO dao = createDaoForRollbackTransaction();

        Collection<PricingTemplate> pts =
            dao.findPricingTemplatesByCurrency(pt1.getCurrency().getId());
        AssertEx.assertSize(pts, 1);
        pts = dao.findPricingTemplatesByCurrency(pt2.getCurrency().getId());
        AssertEx.assertSize(pts, 1);

    }

}
