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

package com.abiquo.abiserver.business.authentication;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;

/**
 * @author slizardo
 */
public class SessionUtil
{

    /**
     * use the method in UserDAO instead of this to avoid Hibernate Session issues
     * 
     * @param name
     * @deprecated
     * @return
     */
    @Deprecated
    public static UserHB findUserHBByName(String name)
    {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        UserHB userHB = new UserHB();

        transaction = session.beginTransaction();

        userHB =
            (UserHB) HibernateUtil.getSession().createCriteria(UserHB.class)
                .add(Restrictions.eq("user", name)).uniqueResult();

        transaction.commit();

        // Exceptions must be catched outside!!

        return userHB;
    }
}
