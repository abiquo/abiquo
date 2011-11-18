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
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
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
        return Restrictions.eq(VirtualImageConversion.VIRTUAL_IMAGE_PROPERTY, image);
    }

    private static Criterion targetFormatIn(final DiskFormatType... formats)
    {
        return Restrictions.in(VirtualImageConversion.TARGET_TYPE_PROPERTY, formats);
    }

    private static Criterion sourceFormatNull()
    {
        return Restrictions.isNull(VirtualImageConversion.SOURCE_TYPE_PROPERTY);
    }

    /**
     * Get all the provided hypervisor compatible {@link VirtualImageConversion} for a given
     * {@link HypervisorType}
     * <p>
     * Before calling this method assure the virtualImage format IS NOT the hypervisorType base
     * format or compatible (conversion not needed). @see
     * {@link VirtualMachineServicePremium#shouldFindConversion}
     * 
     * @return the list of all compatible {@link VirtualImageConversion} in <b>ANY state</b>.
     *         {@link VirtualMachineServicePremium#selectConversion} will check the state and pick
     *         the most suitable format.
     */
    @SuppressWarnings("unchecked")
    public List<VirtualImageConversion> compatilbeConversions(final VirtualImage virtualImage,
        final HypervisorType hypervisorType)
    {
        final Criterion compat =
            Restrictions.and(sameImage(virtualImage),
                targetFormatIn(hypervisorType.compatibilityTable));

        return createCriteria(compat).list();
    }

    /**
     * Returns a list of {@link HypervisorType} from all hypervisors in a datacenter.
     */
    private final String QUERY_IMAGE_CONVERTED = "SELECT count(vic) " + //
        "FROM com.abiquo.server.core.appslibrary.VirtualImageConversion vic " + //
        "WHERE vic.virtualImage.id = :idVirtualImage";

    /**
     * List of {@link HypervisorType} from all hypervisors in a datacenter.
     * 
     * @param datacenterId {@link Hypervisor} machines datacenter.
     * @return list of {@link HypervisorType} from all hypervisors in a datacenter.
     */
    public boolean isVirtualImageConverted(final Integer vImageId, final DiskFormatType format)
    {
        Query query = getSession().createQuery(QUERY_IMAGE_CONVERTED);
        query.setParameter("idVirtualImage", vImageId);

        return (Integer) query.uniqueResult() > 0;
    }

    @Deprecated
    // use selectConversion TODO delthis
    @SuppressWarnings("unchecked")
    public VirtualImageConversion getUnbundledConversion(final VirtualImage image,
        final DiskFormatType format)
    {
        // There can be no images
        List<VirtualImageConversion> conversions =
            createCriteria(sameImage(image)).add(targetFormatIn(format)).add(sourceFormatNull())
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
