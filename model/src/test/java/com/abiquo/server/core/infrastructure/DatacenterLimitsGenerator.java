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

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class DatacenterLimitsGenerator extends DefaultEntityGenerator<DatacenterLimits>
{

    private DatacenterGenerator datacenterGenerator;

    private EnterpriseGenerator enterpriseGenerator;

    public DatacenterLimitsGenerator(SeedGenerator seed)
    {
        super(seed);
        this.datacenterGenerator = new DatacenterGenerator(seed);
        this.enterpriseGenerator = new EnterpriseGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(DatacenterLimits obj1, DatacenterLimits obj2)
    {
        // AssertEx.assertPropertiesEqualSilent(obj1, obj2,
        // DatacenterLimits.ID_DATA_CENTER_PROPERTY,
        // com.abiquo.server.core.enterprise.ID_DC_ENTERPRISE_STATS_PROPERTY,
        // DatacenterLimits.ID_ENTERPRISE_PROPERTY);
    }

    @Override
    public DatacenterLimits createUniqueInstance()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();
        Enterprise enterprise = this.enterpriseGenerator.createUniqueInstance();

        return createInstance(enterprise, datacenter);
    }

    public DatacenterLimits createInstance(Datacenter datacenter)
    {
        Enterprise enterprise = this.enterpriseGenerator.createUniqueInstance();

        return createInstance(enterprise, datacenter);
    }

    public DatacenterLimits createInstance(Enterprise enterprise, Datacenter datacenter)
    {
        DatacenterLimits datacenterLimits = new DatacenterLimits(enterprise, datacenter);

        return datacenterLimits;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(DatacenterLimits entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        this.datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(entity.getDatacenter());

        Enterprise enterprise = entity.getEnterprise();
        this.enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(entity.getEnterprise());

    }

}
