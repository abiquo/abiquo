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
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.scheduler.FitPolicyRule.FitPolicy;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class FitPolicyRuleGenerator extends DefaultEntityGenerator<FitPolicyRule>
{

    DatacenterGenerator datacenterGen;

    public FitPolicyRuleGenerator(SeedGenerator seed)
    {
        super(seed);

        datacenterGen = new DatacenterGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(FitPolicyRule obj1, FitPolicyRule obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, FitPolicyRule.FIT_POLICY_PROPERTY);
    }

    @Override
    public FitPolicyRule createUniqueInstance()
    {
        Datacenter datacenter = datacenterGen.createUniqueInstance();

        FitPolicyRule fitPolicyRule = new FitPolicyRule(datacenter, FitPolicy.PROGRESSIVE);

        return fitPolicyRule;
    }

    public FitPolicyRule createGlobalFitPolicyInstance()
    {
        return new FitPolicyRule(FitPolicy.PROGRESSIVE);
    }
    
    public FitPolicyRule createInstance(Datacenter datacenter)
    {
        FitPolicyRule fitPolicyRule = new FitPolicyRule(datacenter, FitPolicy.PROGRESSIVE);

        return fitPolicyRule;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(FitPolicyRule entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        datacenterGen.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(datacenter);
    }

}
