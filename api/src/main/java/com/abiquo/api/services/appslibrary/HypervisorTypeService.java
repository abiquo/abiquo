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

package com.abiquo.api.services.appslibrary;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.DefaultApiService;
import com.abiquo.model.enumerator.HypervisorType;

@Service
public class HypervisorTypeService extends DefaultApiService
{
    public HypervisorTypeService()
    {

    }

    public HypervisorTypeService(final EntityManager em)
    {
        // appslibraryRep = new AppsLibraryRep(em);
    }

    @Transactional(readOnly = true)
    public Collection<HypervisorType> getHypervisorTypes()
    {
        return Arrays.asList(HypervisorType.values());
    }

    @Transactional(readOnly = true)
    public HypervisorType getHypervisorType(final Integer HypervisorTypeId)
    {
        return HypervisorType.fromId(HypervisorTypeId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public HypervisorType addHypervisorType(final HypervisorType hypervisorType)
    {
        /*
         * validate(diskFormatType); // Check if there is a category with the same name if
         * (appslibraryRep.existAnyWithName(diskFormatType.getName())) {
         * addConflictErrors(APIError.DISK_FORMAT_TYPE_DUPLICATED_NAME); flushErrors(); }
         * appslibraryRep.insertCategory(category);
         */

        return hypervisorType;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public HypervisorType modifyHypervisorType(final HypervisorType hypervisorType,
        final Integer hypervisorTypeId)
    {
        /*
         * Category old = appslibraryRep.findCategoryById(categoryId); if (old == null) {
         * addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY); flushErrors(); } // Check if there is
         * a category (different than the current one) with the new name if
         * (appslibraryRep.existAnyOtherWithName(old, category.getName())) {
         * addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME); flushErrors(); }
         * old.setName(category.getName()); old.setErasable(category.isErasable()); validate(old);
         * appslibraryRep.updateCategory(old); tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
         * EventType.CATEGORY_MODIFIED, "Category " + category.getName() + " updated "); return old;
         */
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeHypervisorType(final Integer hypervisorTypeId)
    {
        /*
         * Category category = appslibraryRep.findCategoryById(categoryId); if (category == null) {
         * addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY); flushErrors(); } if
         * (!category.isErasable()) { addConflictErrors(APIError.CATEGORY_NOT_ERASABLE);
         * flushErrors(); } tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
         * EventType.CATEGORY_DELETED, "Removing category " + category.getName());
         * appslibraryRep.deleteCategory(category);
         */
    }

}
