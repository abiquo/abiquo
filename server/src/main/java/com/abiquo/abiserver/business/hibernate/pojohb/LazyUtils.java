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
package com.abiquo.abiserver.business.hibernate.pojohb;

import org.hibernate.Hibernate;

import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Utility class to deal with Lazy relationships.
 * 
 * @author ibarrera
 */
public abstract class LazyUtils
{
    /**
     * Get the given object, taking care of initializing it if necessary.
     * 
     * @param obj The object to get.
     * @return The object initialized.
     */
    public static <T> T lazyGet(final T obj)
    {
        // Check wheter the object is a proxy or not
        if (obj != null && !Hibernate.isInitialized(obj))
        {
            DAOFactory factory = HibernateDAOFactory.instance();
            boolean participate = factory.isTransactionActive();

            // If we have an open transaction don't open another one
            if (!participate)
            {
                factory.beginConnection();
            }

            // This method needs the Hibernate Session
            try
            {
                Hibernate.initialize(obj);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }

            // Don't close the transaction if someone else opened it
            if (!participate)
            {
                factory.endConnection();
            }
        }

        return obj;
    }
}
