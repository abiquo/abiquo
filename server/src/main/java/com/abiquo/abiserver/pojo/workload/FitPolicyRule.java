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

package com.abiquo.abiserver.pojo.workload;

import com.abiquo.abiserver.business.hibernate.pojohb.workload.FitPolicyRuleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.engine.core.FitPolicy;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;

public class FitPolicyRule implements IPojo<FitPolicyRuleHB>, PersistentRule
{
    private String fitPolicy;

    private DataCenter datacenter;

    public String getFitPolicy()
    {
        return fitPolicy;
    }

    public void setFitPolicy(final String fitPolicy)
    {
        this.fitPolicy = fitPolicy;
    }

    public DataCenter getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final DataCenter datacenter)
    {
        this.datacenter = datacenter;
    }

    @Override
    public FitPolicyRuleHB toPojoHB()
    {
        FitPolicyRuleHB ruleHB = new FitPolicyRuleHB();
        ruleHB.setFitPolicy(FitPolicy.valueOf(fitPolicy));

        if (datacenter != null)
        {
            ruleHB.setDatacenter(datacenter.toPojoHB());
        }

        return ruleHB;
    }
}
