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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.api.persistence.JpaDAO;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.enterprise.EnterpriseRep;

@Repository
public class AppsLibraryDAO extends JpaDAO<AppsLibrary, Integer>
{
    private final static String QUERY_GET_BY_ENTER =
        "FROM " + AppsLibrary.class.getName() + " WHERE " + "enterprise.id = :idEnterprise";

    @Autowired
    EnterpriseRep enterpirseDao;

    @Override
    protected Class<AppsLibrary> getPersistentClass()
    {
        return AppsLibrary.class;
    }

    public AppsLibrary findByEnterprise(final Integer idEnterprise)
    {
        AppsLibrary appsLib;
        try
        {
            appsLib =
                (AppsLibrary) entityManager.createQuery(QUERY_GET_BY_ENTER).setParameter(
                    "idEnterprise", idEnterprise).getSingleResult();
        }
        catch (Throwable e)
        {

            appsLib = new AppsLibrary();
            appsLib.setEnterprise(enterpirseDao.findById(idEnterprise));
            appsLib = this.makePersistent(appsLib);
        }

        return appsLib;
    }

}
