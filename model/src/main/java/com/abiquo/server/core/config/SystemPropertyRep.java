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
package com.abiquo.server.core.config;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;

@Repository
public class SystemPropertyRep extends DefaultRepBase
{
    @Autowired
    private SystemPropertyDAO dao;

    public SystemPropertyRep()
    {
    }

    public SystemPropertyRep(final EntityManager em)
    {
        this.dao = new SystemPropertyDAO(em);
    }

    public SystemProperty findById(final Integer id)
    {
        return this.dao.findById(id);
    }

    public SystemProperty findByName(final String name)
    {
        return this.dao.findByName(name);
    }

    public Collection<SystemProperty> findByComponent(final String component)
    {
        return this.dao.findByComponent(component);
    }

    public Collection<SystemProperty> findAll()
    {
        return this.dao.findAll();
    }

    public void insert(final SystemProperty systemProperty)
    {
        this.dao.persist(systemProperty);
        this.dao.flush();
    }

    public void delete(final SystemProperty systemProperty)
    {
        this.dao.remove(systemProperty);
        this.dao.flush();
    }

    public void update(final SystemProperty systemProperty)
    {
        this.dao.flush();
    }

    public boolean existsAnyWithName(final String name)
    {
        return dao.existsAnyWithName(name);
    }

    public boolean existsAnyOtherWithName(final SystemProperty systemProperty, final String name)
    {
        return dao.existsAnyOtherWithName(systemProperty, name);
    }
}
