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

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class CategoryService extends DefaultApiService
{
    @Autowired
    private AppsLibraryRep appslibraryRep;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    public CategoryService()
    {

    }

    public CategoryService(final EntityManager em)
    {
        appslibraryRep = new AppsLibraryRep(em);
    }

    /**
     * @param idEnterprise enterprise id for which you want to retrieve local categories
     * @param onlyLocal true returns only local categories, false returns glocal and local
     *            categories
     * @return
     */
    @Transactional(readOnly = true)
    public Collection<Category> getCategories(final Integer idEnterprise, final boolean onlyLocal)
    {
        if (idEnterprise != 0)
        {
            Enterprise enterprise = enterpriseService.getEnterprise(Integer.valueOf(idEnterprise));
            if (!userService.getCurrentUser().getEnterprise().getId().equals(idEnterprise)
                && !onlyLocal)
            {
                // return only global categories
                return appslibraryRep.findAllCategories(0, false);
            }

            // error if enterrpise doesnot exists
        }
        return appslibraryRep.findAllCategories(idEnterprise, onlyLocal);
    }

    @Transactional(readOnly = true)
    public Category getCategory(final Integer categoryId)
    {
        Category category = appslibraryRep.findCategoryById(categoryId);
        if (category == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }
        return category;
    }

    @Transactional(readOnly = true)
    public Category getCategoryByNameAndEnterprise(final String categoryName,
        final Enterprise enterprise)
    {
        Category category = appslibraryRep.findCategoryByName(categoryName, enterprise);
        if (category == null)
        {
            category = appslibraryRep.findCategoryByName(categoryName, null);
            if (category == null)
            {
                addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
                flushErrors();
            }
        }
        return category;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category addCategory(final CategoryDto category)
    {
        Category cat = createPersistenceObject(category);
        validate(cat);

        // Check if there is a category with the same name
        if (appslibraryRep.findCategoryByName(category.getName(), null) != null)
        {
            addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME);
            flushErrors();
        }
        if (cat.getEnterprise() != null)
        {
            if (appslibraryRep.findCategoryByName(category.getName(), cat.getEnterprise()) != null)
            {
                addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME);
                flushErrors();
            }
        }
        else
        {
            if (!securityService.hasPrivilege(Privileges.APPLIB_MANAGE_GLOBAL_CATEGORIES))
            {
                addConflictErrors(APIError.CATEGORY_NO_PRIVELIGES_TO_CREATE_GLOBAL);
                flushErrors();
            }
        }

        appslibraryRep.insertCategory(cat);

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_CREATED,
            "category.created", category.getName());

        return cat;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category modifyCategory(final CategoryDto category, final Integer categoryId)
    {
        Category newCat = createPersistenceObject(category);
        Category old = appslibraryRep.findCategoryById(categoryId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }

        if (old.getEnterprise() != null)
        {
            // changing the local category from one enterprise to other enterprise
            if (newCat.getEnterprise() != null
                && newCat.getEnterprise().getId() != old.getEnterprise().getId())
            {
                addConflictErrors(APIError.CATEGORY_CANNOT_MOVE_LOCAL);
                flushErrors();

            }
            else
            {
                if (!securityService.hasPrivilege(Privileges.APPLIB_MANAGE_GLOBAL_CATEGORIES))
                {
                    addConflictErrors(APIError.CATEGORY_NO_PRIVELIGES_TO_CREATE_GLOBAL);
                    flushErrors();
                }
            }
        }
        else
        {
            // converting a global category to a local category, adding enterprise
            if (newCat.getEnterprise() != null)
            {
                addConflictErrors(APIError.CATEGORY_CANNOT_CHANGE_TO_LOCAL);
                flushErrors();
            }
            else if (appslibraryRep.findCategoryByName(category.getName(), null) != null)
            {
                // Check if there is a category (different than the current one) with the new name
                addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME);
                flushErrors();
            }
        }
        old.setName(category.getName());
        old.setErasable(category.isErasable());
        old.setEnterprise(newCat.getEnterprise());

        validate(old);
        appslibraryRep.updateCategory(old);

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_MODIFIED,
            "category.updated", category.getName());

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeCategory(final Integer categoryId)
    {
        Category category = appslibraryRep.findCategoryById(categoryId);
        if (category == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }

        if (!category.isErasable())
        {
            addConflictErrors(APIError.CATEGORY_NOT_ERASABLE);
            flushErrors();
        }
        if (category.getEnterprise() != null)
        {
            if (!userService.getCurrentUser().getEnterprise().getId().equals(
                category.getEnterprise().getId()))
            {
                // cannot remove local category of other enteprise
                addConflictErrors(APIError.CATEGORY_NO_PRIVELIGES_TO_REMOVE);
                flushErrors();
            }
        }
        else
        {
            if (!securityService.hasPrivilege(Privileges.APPLIB_MANAGE_GLOBAL_CATEGORIES))
            {
                addConflictErrors(APIError.CATEGORY_NO_PRIVELIGES_TO_REMOVE);
                flushErrors();
            }
        }
        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_DELETED,
            "category.removed", category.getName());

        appslibraryRep.deleteCategory(category);
    }

    private Category createPersistenceObject(final CategoryDto dto)
    {
        RESTLink enterpriseLink = dto.searchLink(EnterpriseResource.ENTERPRISE);
        // check the links

        Category category = new Category(dto.getName());
        category.setId(dto.getId());
        category.setErasable(dto.isErasable());
        if (enterpriseLink != null)
        {
            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, enterpriseLink.getHref());

            if (map == null || !map.containsKey(EnterpriseResource.ENTERPRISE))
            {
                addValidationErrors(APIError.INVALID_ENTERPRISE_LINK);
                flushErrors();
            }
            Integer enterpriseIdFromLink =
                Integer.parseInt(map.getFirst(EnterpriseResource.ENTERPRISE));
            Enterprise enterprise = enterpriseService.getEnterprise(enterpriseIdFromLink);
            category.setEnterprise(enterprise);
        }

        return category;
    }
}
