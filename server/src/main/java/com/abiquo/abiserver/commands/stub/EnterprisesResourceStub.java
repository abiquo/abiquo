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
package com.abiquo.abiserver.commands.stub;

import java.util.Collection;

import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.EnterpriseListResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;

public interface EnterprisesResourceStub
{
    public DataResult<Enterprise> createEnterprise(Enterprise enterprise);

    public DataResult<Enterprise> editEnterprise(Enterprise enterprise);

    public BasicResult deleteEnterprise(Integer enterpriseId);

    public DataResult<EnterpriseListResult> getEnterprises(ListRequest enterpriseListOptions);

    public DataResult<Enterprise> getEnterprise(Integer enterpriseId);

    public DataResult<Collection<VirtualDataCenter>> getVirtualDatacenters(
        final Enterprise enterprise);

    public DataResult<EnterpriseListResult> getEnterprisesWithPricingTemplate(
        ListRequest listOptions, Integer idPricingTemplate, boolean included);

    public DataResult<Enterprise> editEnterprise(Enterprise enterprise, Integer id);
}
