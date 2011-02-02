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

package com.abiquo.abiserver.persistence.dao.virtualhardware.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class ResourceManagementDAOHibernate extends HibernateDAO<ResourceManagementHB, Integer>
    implements ResourceManagementDAO
{

    public static final String GET_RESOURCE_MANAGEMENT_ETHERNET_BY_MAC = "GET_RESOURCE_MANAGEMENT_ETHERNET_BY_MAC";

    @Override
    public ResourceManagementHB getResourceManagementPrivateEthernetByVS(VirtualmachineHB vmHB)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(GET_RESOURCE_MANAGEMENT_ETHERNET_BY_MAC);
            query.setString("idVM", vmHB.getIdVm().toString());

            ResourceManagementHB result = (ResourceManagementHB) query.uniqueResult();

            return result;
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }
}
