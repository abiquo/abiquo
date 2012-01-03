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
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import com.abiquo.server.core.common.persistence.JPAConfiguration;

/**
 * OpenEntityManagerInViewFilter with the default hibernate filters enabled.
 * 
 * @author Ignasi Barrera
 * @see OpenEntityManagerInViewFilter
 */
public class OpenEntityManagerInViewWithFilters extends OpenEntityManagerInViewFilter
{

    @Override
    protected EntityManager createEntityManager(final EntityManagerFactory emf)
    {
        return JPAConfiguration.enableDefaultFilters(super.createEntityManager(emf));
    }

}
