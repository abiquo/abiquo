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

package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaIconDAO")
/* package */class IconDAO extends DefaultDAOBase<Integer, Icon>
{
    private static final String VIRTUALIMAGES_BY_ICON =
        "FROM com.abiquo.server.core.appslibrary.VirtualImage WHERE icon = :icon";

    public IconDAO()
    {
        super(Icon.class);
    }

    public IconDAO(final EntityManager entityManager)
    {
        super(Icon.class, entityManager);
    }

    public Icon findByPath(final String path)
    {
        return findUniqueByProperty(Icon.PATH_PROPERTY, path);
    }

    public boolean iconInUseByVirtualImages(final Icon icon)
    {
        Query query = getSession().createQuery(VIRTUALIMAGES_BY_ICON);
        query.setParameter("icon", icon);
        return !query.list().isEmpty();
    }
}
