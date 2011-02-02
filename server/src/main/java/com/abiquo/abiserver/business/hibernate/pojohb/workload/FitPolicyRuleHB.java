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
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.engine.core.FitPolicy;
import com.abiquo.abiserver.pojo.workload.FitPolicyRule;

/**
 * Defines a FitPolicy Rule.
 * <p>
 * This rule defines which machine is selected by the scheduler, once all other rules have been
 * applied.
 * 
 * @author ibarrera
 */
public class FitPolicyRuleHB implements Serializable, IPojoHB<FitPolicyRule>
{
    /** Serial UID. */
    private static final long serialVersionUID = 7823926949679997455L;

    /** The default FitPolicyRuleHB. */
    public static final FitPolicyRuleHB DEFAULT_FITPOLICY_RULE = new DefaultFitPolicyRule();

    /** The rule id. */
    private Integer id;

    /** The fit Policy */
    private FitPolicy fitPolicy;

    /**
     * The datacenter where the rule applies, or <code>null</code> if it is the default Fit Policy
     * that will be applied to all Datacenters withous a {@link FitPolicyRuleHB}.
     */
    private DatacenterHB datacenter;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public FitPolicy getFitPolicy()
    {
        return fitPolicy;
    }

    public void setFitPolicy(final FitPolicy fitPolicy)
    {
        this.fitPolicy = fitPolicy;
    }

    public DatacenterHB getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final DatacenterHB datacenter)
    {
        this.datacenter = datacenter;
    }

    @Override
    public FitPolicyRule toPojo()
    {
        FitPolicyRule rule = new FitPolicyRule();
        rule.setFitPolicy(fitPolicy.name());

        if (datacenter != null)
        {
            rule.setDatacenter(datacenter.toPojo());
        }

        return rule;
    }

    private static class DefaultFitPolicyRule extends FitPolicyRuleHB
    {
        private static final long serialVersionUID = 1479156414262769638L;

        private DefaultFitPolicyRule()
        {
            setDatacenter(null);
            setFitPolicy(FitPolicy.PROGRESSIVE);
        }
    }

}
