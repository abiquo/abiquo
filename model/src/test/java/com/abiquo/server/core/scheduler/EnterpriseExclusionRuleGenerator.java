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

package com.abiquo.server.core.scheduler;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class EnterpriseExclusionRuleGenerator extends
    DefaultEntityGenerator<EnterpriseExclusionRule>
{
    private EnterpriseGenerator enterpriseGenerator;

    public EnterpriseExclusionRuleGenerator(SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(EnterpriseExclusionRule obj1, EnterpriseExclusionRule obj2)
    {
        enterpriseGenerator.assertAllPropertiesEqual(obj1.getEnterprise1(), obj2.getEnterprise1());
        enterpriseGenerator.assertAllPropertiesEqual(obj1.getEnterprise2(), obj2.getEnterprise2());
    }

    @Override
    public EnterpriseExclusionRule createUniqueInstance()
    {
        Enterprise enterprise1 =
            enterpriseGenerator.createInstance(newString(nextSeed(), Enterprise.NAME_LENGTH_MIN,
                Enterprise.NAME_LENGTH_MAX));

        Enterprise enterprise2 =
            enterpriseGenerator.createInstance(newString(nextSeed(), Enterprise.NAME_LENGTH_MIN,
                Enterprise.NAME_LENGTH_MAX));

        EnterpriseExclusionRule enterpriseExclusionRule =
            new EnterpriseExclusionRule(enterprise1, enterprise2);

        return enterpriseExclusionRule;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(EnterpriseExclusionRule entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise enterprise1 = entity.getEnterprise1();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise1, entitiesToPersist);
        entitiesToPersist.add(enterprise1);

        Enterprise enterprise2 = entity.getEnterprise2();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise2, entitiesToPersist);
        entitiesToPersist.add(enterprise2);
    }

}
