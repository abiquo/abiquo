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

package com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualApplianceConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceConversionsDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceConversionsDAO}
 * interface
 * 
 * @author dcalavera@abiquo.com
 */
public class VirtualApplianceConversionsDAOHibernate extends
    HibernateDAO<VirtualApplianceConversionsHB, Integer> implements VirtualApplianceConversionsDAO
{

    @Override
    public List<VirtualApplianceConversionsHB> findByConversion(final int idConversion)
    {
        Query query =
            getSession()
                .createQuery(
                    "from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualApplianceConversionsHB where virtualImageConversion.id = :id");
        query.setParameter("id", idConversion);

        return query.list();
    }

    @Override
    public Collection<VirtualappHB> findByPendingAppliances(final int idPending,
        final Collection<Integer> appliances)
    {
        return getSession()
            .createQuery(
                "select va from VirtualappExtendedHB va, com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualApplianceConversionsHB vac where va.idVirtualApp = vac.virtualAppliance.id and vac.virtualAppliance.id in (:appliances) and vac.id != :idPending")
            .setParameterList("appliances", appliances).setParameter("idPending", idPending).list();
    }

    @Override
    public void makeTransientByVirtualAppliance(final int idVirtualAppliance)
    {
        getSession()
            .createQuery(
                "delete com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualApplianceConversionsHB vac where vac.virtualAppliance.id = :idAppliance")
            .setParameter("idAppliance", idVirtualAppliance).executeUpdate();
    }

    @Override
    public String findDatacenterUUIDByVASConversion(final int idVASC)
    {
        Query query =
            getSession()
                .createQuery(
                    "select distinct(dc.uuid) from vappstateful_conversions vasc left outer join virtualapp va on vasc.idVirtualApp = va.idVirtualApp left outer join virtualdatacenter vdc on va.idVirtualDataCenter = vdc.idVirtualdataCenter left outer join datacenter dc on vdc.idDatacenter = dc.idDataCenter where vasc.id = :id");
        query.setParameter("id", idVASC);

        return (String) query.uniqueResult();
    }

}
