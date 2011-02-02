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

package com.abiquo.server.core.infrastructure;

import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaRemoteServiceDAO")
public class RemoteServiceDAO extends DefaultDAOBase<Integer, RemoteService>
{
    public RemoteServiceDAO()
    {
        super(RemoteService.class);
    }

    public RemoteServiceDAO(EntityManager entityManager)
    {
        super(RemoteService.class, entityManager);
    }

    private static Criterion equalUri(String uri)
    {
        assert !StringUtils.isEmpty(uri);

        return Restrictions.eq(RemoteService.URI_PROPERTY, uri);
    }

    private static Criterion equalDatacenter(Datacenter datacenter)
    {
        return Restrictions.eq(RemoteService.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion equalType(RemoteServiceType type)
    {
        return Restrictions.eq(RemoteService.TYPE_PROPERTY, type);
    }

    public boolean existRemoteServiceUri(String uri) throws URISyntaxException
    {
        assert !StringUtils.isEmpty(uri);

        return this.existsAnyByCriterions(equalUri(uri));
    }

    public List<RemoteService> findByDatacenter(Datacenter datacenter)
    {
        return findByCriterions(equalDatacenter(datacenter));
    }

    public List<RemoteService> findByDatacenterAndType(Datacenter datacenter, RemoteServiceType type)
    {
        return findByCriterions(equalDatacenter(datacenter), equalType(type));
    }

    public String getRemoteServiceUri(Datacenter datacenter, RemoteServiceType type)
    {
        List<RemoteService> nodecollectors = findByDatacenterAndType(datacenter, type);

        if (nodecollectors.size() != 1 || nodecollectors.get(0).getStatus() == 0)
        {
            final String cause =
                String.format("Datacenter [%s] doesn't have the ''[%s]'' remote service "
                    + "configured", datacenter.getName(), type.getName());

            throw new PersistenceException(cause); // TODO infrastructure exception
        }

        return nodecollectors.get(0).getUri();

    }
}
