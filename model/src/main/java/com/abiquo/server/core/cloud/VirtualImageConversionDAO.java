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

package com.abiquo.server.core.cloud;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaVirtualImageConversionDAO")
public class VirtualImageConversionDAO extends DefaultDAOBase<Integer, VirtualImageConversion>
{
    public VirtualImageConversionDAO()
    {
        super(VirtualImageConversion.class);
    }

    public VirtualImageConversionDAO(final EntityManager entityManager)
    {
        super(VirtualImageConversion.class, entityManager);
    }

    private static Criterion sameImage(final VirtualImage image)
    {
        return Restrictions.eq("imageId", image.getId());
    }

    private static Criterion sameTargetFormat(final DiskFormatType format)
    {
        return Restrictions.eq("targetType", format.name());
    }

    private static Criterion sourceFormatNull()
    {
        return Restrictions.isNull("sourceType");
    }

    public VirtualImageConversion getUnbundledConversion(final VirtualImage image,
        final DiskFormatType format)
    {
        // There can be no images
        List<VirtualImageConversion> conversions =
            createCriteria(sameImage(image)).add(sameTargetFormat(format)).add(sourceFormatNull())
                .list();
        // Are there any?
        if (conversions != null && !conversions.isEmpty())
        {
            // This function should be returning the only object
            if (conversions.size() > 1)
            {
                throw new NonUniqueObjectException("There is more than one conversion!",
                    image.getId(),
                    VirtualImageConversion.class.getSimpleName());
            }
            return conversions.get(0);
        }
        return null;
    }
}
