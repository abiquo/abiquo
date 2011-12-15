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
package com.abiquo.api.spring.jpa;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

/**
 * Utility methods to configure JPA.
 * 
 * @author Ignasi Barrera
 */
public class JPAConfiguration
{
    public static EntityManager enableDefaultFilters(final EntityManager em)
    {
        if (!em.isOpen())
        {
            throw new IllegalStateException("EntityManager must be open");
        }

        // Default filters to enable
        Session session = (Session) em.getDelegate();
        session.enableFilter(VirtualMachine.NOT_TEMP);
        session.enableFilter(VolumeManagement.NOT_TEMP);
        session.enableFilter(IpPoolManagement.NOT_TEMP);
        session.enableFilter(DiskManagement.NOT_TEMP);

        return em;
    }
}
