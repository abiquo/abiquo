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

package com.abiquo.api.persistence.impl;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.abiquo.api.persistence.JpaDAO;
import com.abiquo.server.core.appslibrary.Icon;

@Repository
public class IconDAO extends JpaDAO<Icon, Integer>
{
    @Override
    protected Class<Icon> getPersistentClass()
    {
        return Icon.class;
    }

    private final static String QUERY_GET_BY_PATH = "FROM " + Icon.class.getName() + " WHERE " //
        + "path = :path";

    public Icon findByPath(final String path)
    {
        Icon icon = null;

        try
        {
            icon =
                (Icon) entityManager.createQuery(QUERY_GET_BY_PATH).setParameter("path", path)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {

        }
        return icon;
    }

}
