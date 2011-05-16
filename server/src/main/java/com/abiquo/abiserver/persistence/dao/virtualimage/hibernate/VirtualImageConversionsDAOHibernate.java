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

package com.abiquo.abiserver.persistence.dao.virtualimage.hibernate;

import java.util.Collection;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.StateConversionEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageConversionsDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.model.enumerator.DiskFormatType;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageConversionsDAO} interface
 * 
 * @author eruiz@abiquo.com
 */
public class VirtualImageConversionsDAOHibernate extends
    HibernateDAO<VirtualImageConversionsHB, Integer> implements VirtualImageConversionsDAO
{
    // implement extra functionality

    public void saveConversions(final Collection<VirtualImageConversionsHB> conversions)
        throws PersistenceException
    {
        for (VirtualImageConversionsHB conversion : conversions)
        {
            makePersistent(conversion);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<VirtualImageConversionsHB> getConversion(final VirtualimageHB image,
        final DiskFormatType diskFormat)
    {
        return getSession()
            .createSQLQuery(
                "select * from virtualimage_conversions where idImage = :idImage and targetType = :format")
            .addEntity(VirtualImageConversionsHB.class).setParameter("idImage", image.getIdImage())
            .setParameter("format", diskFormat.name()).list();
    }

    @SuppressWarnings("unchecked")
    public Collection<VirtualImageConversionsHB> getFinishedConversion(final VirtualimageHB image,
        final DiskFormatType diskFormat)
    {
        return getSession()
            .createSQLQuery(
                "select * from virtualimage_conversions where idImage = :idImage and targetType = :format and idState = 2")
            .addEntity(VirtualImageConversionsHB.class).setParameter("idImage", image.getIdImage())
            .setParameter("format", diskFormat.name()).list();
    }

    public VirtualImageConversionsHB getUnbundledConversion(final VirtualimageHB image,
        final DiskFormatType diskFormat)
    {
        return (VirtualImageConversionsHB) getSession()
            .createSQLQuery(
                "select * from virtualimage_conversions where idImage = :idImage and targetType = :format and sourceType is null")
            .addEntity(VirtualImageConversionsHB.class).setParameter("idImage", image.getIdImage())
            .setParameter("format", diskFormat.name()).uniqueResult();
    }

    public boolean isConverted(final VirtualimageHB image)
    {
        Long existConversion =
            (Long) getSession()
                .createQuery(
                    "select count(*) from com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB"
                        + " where image.id = :idImage and state = :state").setParameter("idImage",
                    image.getIdImage()).setParameter("state", StateConversionEnum.FINISHED)
                .uniqueResult();

        return existConversion > 0;
    }

    public boolean isConverted(final VirtualimageHB image, DiskFormatType targetType)
    {
        Long existConversion =
            (Long) getSession()
                .createQuery(
                    "select count(*) from com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB"
                        + " where image.id = :idImage and state = :state and targetType = :type")
                .setParameter("idImage", image.getIdImage()).setParameter("state",
                    StateConversionEnum.FINISHED).setParameter("type", targetType).uniqueResult();

        return existConversion > 0;
    }

    @SuppressWarnings("unchecked")
    public Collection<VirtualImageConversionsHB> getConversions(final Collection<Integer> imageIds)
    {
        return getSession().createSQLQuery(
            "select * from virtualimage_conversions where idImage in (:idImage)").addEntity(
            VirtualImageConversionsHB.class).setParameterList("idImage", imageIds).list();
    }
}
