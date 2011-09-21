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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "rules")
public class RulesDto extends SingleResourceTransportDto
{

    private FitPolicyRulesDto fitPolicyRules;

    private MachineLoadRulesDto machineLoadRules;

    private EnterpriseExclusionsRulesDto enterpriseExclusionRules;

    public FitPolicyRulesDto getFitPolicyRules()
    {
        return fitPolicyRules;
    }

    public void setFitPolicyRules(final FitPolicyRulesDto fitPolicyRules)
    {
        this.fitPolicyRules = fitPolicyRules;
    }

    public MachineLoadRulesDto getMachineLoadRules()
    {
        return machineLoadRules;
    }

    public void setMachineLoadRules(final MachineLoadRulesDto machineLoadRules)
    {
        this.machineLoadRules = machineLoadRules;
    }

    public EnterpriseExclusionsRulesDto getEnterpriseExclusionRules()
    {
        return enterpriseExclusionRules;
    }

    public void setEnterpriseExclusionRules(
        final EnterpriseExclusionsRulesDto enterpriseExclusionRules)
    {
        this.enterpriseExclusionRules = enterpriseExclusionRules;
    }

}
