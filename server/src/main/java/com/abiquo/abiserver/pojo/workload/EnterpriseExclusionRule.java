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

import com.abiquo.abiserver.business.hibernate.pojohb.workload.EnterpriseExclusionRuleHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.user.Enterprise;

/**
 * This pojo class store the information of the EnterpriseExclusionRule. On abiCloud, an user
 * belongs to an EnterpriseExclusionRule.
 * 
 * @author abiquo
 */
public class EnterpriseExclusionRule implements IPojo<EnterpriseExclusionRuleHB>, PersistentRule
{
    private Enterprise enterprise1;

    private Enterprise enterprise2;

    public Enterprise getEnterprise1()
    {
        return enterprise1;
    }

    public void setEnterprise1(final Enterprise enterprise1)
    {
        this.enterprise1 = enterprise1;
    }

    public Enterprise getEnterprise2()
    {
        return enterprise2;
    }

    public void setEnterprise2(final Enterprise enterprise2)
    {
        this.enterprise2 = enterprise2;
    }

    /**
     * This method transform the current EnterpriseExclusionRule pojo object to a
     * EnterpriseExclusionRule hibernate pojo object
     */
    public EnterpriseExclusionRuleHB toPojoHB()
    {
        EnterpriseExclusionRuleHB ruleHB = new EnterpriseExclusionRuleHB();
        ruleHB.setEnterprise1(enterprise1.toPojoHB());
        ruleHB.setEnterprise2(enterprise2.toPojoHB());
        return ruleHB;
    }

}
