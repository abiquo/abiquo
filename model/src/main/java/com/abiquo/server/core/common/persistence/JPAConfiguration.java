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
package com.abiquo.server.core.common.persistence;

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
    private static final String[] NOT_TEMPORAL_FILTERS = {VirtualMachine.NOT_TEMP,
    VolumeManagement.NOT_TEMP, IpPoolManagement.NOT_TEMP, DiskManagement.NOT_TEMP};

    private static final String[] ONLY_TEMPORAL_FILTERS = {VirtualMachine.ONLY_TEMP,
    VolumeManagement.ONLY_TEMP, IpPoolManagement.ONLY_TEMP, DiskManagement.ONLY_TEMP};

    public static EntityManager enableDefaultFilters(final EntityManager em)
    {
        return enableNotTemporalFilters(em);
    }

    public static EntityManager disableAllFilters(final EntityManager em)
    {
        if (!em.isOpen())
        {
            throw new IllegalStateException("EntityManager must be open");
        }

        Session session = (Session) em.getDelegate();

        for (String filter : ONLY_TEMPORAL_FILTERS)
        {
            session.disableFilter(filter);
        }
        for (String filter : NOT_TEMPORAL_FILTERS)
        {
            session.disableFilter(filter);
        }

        return em;
    }

    public static EntityManager enableNotTemporalFilters(final EntityManager em)
    {
        if (!em.isOpen())
        {
            throw new IllegalStateException("EntityManager must be open");
        }

        Session session = (Session) em.getDelegate();

        for (String filter : ONLY_TEMPORAL_FILTERS)
        {
            session.disableFilter(filter);
        }
        for (String filter : NOT_TEMPORAL_FILTERS)
        {
            session.enableFilter(filter);
        }

        return em;
    }

    public static EntityManager enableOnlyTemporalFilters(final EntityManager em)
    {
        if (!em.isOpen())
        {
            throw new IllegalStateException("EntityManager must be open");
        }

        Session session = (Session) em.getDelegate();

        for (String filter : NOT_TEMPORAL_FILTERS)
        {
            session.disableFilter(filter);
        }
        for (String filter : ONLY_TEMPORAL_FILTERS)
        {
            session.enableFilter(filter);
        }

        return em;
    }
}
