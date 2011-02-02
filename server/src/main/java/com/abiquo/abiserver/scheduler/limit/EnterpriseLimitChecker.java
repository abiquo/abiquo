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

package com.abiquo.abiserver.scheduler.limit;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

public class EnterpriseLimitChecker extends EntityLimitChecker<EnterpriseHB>
{
    private final DAOFactory daoF = HibernateDAOFactory.instance();

    @Override
    public ResourceAllocationLimitHB getLimit(final EnterpriseHB enterprise)
    {
        // enterprise always have limits
        return enterprise.getLimits();
    }

    @Override
    public ResourceAllocationLimitHB getActualAllocation(final EnterpriseHB entity)
    {

        final Integer idEnterprise = entity.getIdEnterprise();

        final EnterpriseDAO daoEnter = daoF.getEnterpriseDAO();

        final ResourceAllocationLimitHB allocated =
            daoEnter.getTotalResourceUtilization(idEnterprise);

        // premium override
        allocated.setStorage(new LimitHB(0, 0));
        allocated.setRepository(new LimitHB(0, 0));
        allocated.setPublicIP(new LimitHB(0, 0));
        allocated.setVlan(new LimitHB(0, 0));

        return allocated;
    }

    @Override
    public List<ResourceLimitStatus> getFilterResourcesStatus(
        final List<ResourceLimitStatus> limitStatus)
    {
        // enterprise use all the resources limits
        return limitStatus;
    }

    @Override
    String getEntityName(EnterpriseHB entity)
    {
        return String.format("Enterprise : %s", entity.getName());
    }
}
