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

package com.abiquo.abiserver.business.hibernate.pojohb.workload;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.pojo.workload.EnterpriseExclusionRule;

/**
 * Defines an Enterprise Exclusion rule.
 * <p>
 * This rule avoids two different enterprises to deploy in the same Physical Machine.
 * 
 * @author ibarrera
 */
public class EnterpriseExclusionRuleHB implements Serializable, IPojoHB<EnterpriseExclusionRule>
{
    /** Serial UID. */
    private static final long serialVersionUID = -2927910290741320060L;

    /** Rule Id. */
    private Integer id;

    /** The first enterprise. */
    private EnterpriseHB enterprise1;

    /** The second enterprise. */
    private EnterpriseHB enterprise2;

    /**
     * Default constructor.
     */
    public EnterpriseExclusionRuleHB()
    {
        super();
    }

    /**
     * Creates a new rule for the given enterprises.
     * 
     * @param enterprise1 The first enterprise.
     * @param enterprise2 The second enterprise.
     */
    public EnterpriseExclusionRuleHB(final EnterpriseHB enterprise1, final EnterpriseHB enterprise2)
    {
        super();
        this.enterprise1 = enterprise1;
        this.enterprise2 = enterprise2;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public EnterpriseHB getEnterprise1()
    {
        return enterprise1;
    }

    public void setEnterprise1(final EnterpriseHB enterprise1)
    {
        this.enterprise1 = enterprise1;
    }

    public EnterpriseHB getEnterprise2()
    {
        return enterprise2;
    }

    public void setEnterprise2(final EnterpriseHB enterprise2)
    {
        this.enterprise2 = enterprise2;
    }

    @Override
    public EnterpriseExclusionRule toPojo()
    {
        EnterpriseExclusionRule rule = new EnterpriseExclusionRule();
        rule.setEnterprise1(getEnterprise1().toPojo());
        rule.setEnterprise2(getEnterprise2().toPojo());
        return rule;
    }

}
