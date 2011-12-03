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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository
public class TemplateDefinitionRep extends DefaultRepBase
{

    @Autowired
    TemplateDefinitionDAO dao;

    @Autowired
    TemplateDefinitionListDAO listDao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Autowired
    CategoryDAO categoryDao;

    @Autowired
    IconDAO iconDao;

    public TemplateDefinitionRep()
    {

    }

    public TemplateDefinitionRep(final EntityManager em)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = em;

        dao = new TemplateDefinitionDAO(em);
        listDao = new TemplateDefinitionListDAO(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        categoryDao = new CategoryDAO(em);
        iconDao = new IconDAO(em);
    }

    public List<TemplateDefinition> getTemplateDefinitionsByEnterprise(final Integer idEnterprise)
    {
        return dao.findByEnterprise(idEnterprise);
    }

    public TemplateDefinition addTemplateDefinition(final TemplateDefinition templateDef,
        final Enterprise enterprise)
    {
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);

        Category category = categoryDao.findById(templateDef.getCategory().getId());
        if (category == null)
        {
            categoryDao.persist(templateDef.getCategory());
        }

        templateDef.setAppsLibrary(appsLib);
        dao.persist(templateDef);
        dao.flush();

        return templateDef;
    }

    public TemplateDefinition getTemplateDefinition(final Integer id)
    {
        return dao.findById(id);
    }

    public TemplateDefinition updateTemplateDefinition(final Integer templateUrl,
        final TemplateDefinition templateDef, final Enterprise enterprise)
    {
        TemplateDefinition old = dao.findById(templateUrl);

        // TODO - Apply changes and compare etags
        old.setName(templateDef.getName());
        old.setDescription(templateDef.getDescription());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);
        templateDef.setAppsLibrary(appsLib);
        old.setId(templateUrl);
        old.setCategory(templateDef.getCategory());
        old.setType(templateDef.getType());
        old.setIcon(templateDef.getIcon());
        old.setProductName(templateDef.getProductName());
        old.setProductUrl(templateDef.getProductUrl());
        old.setProductVendor(templateDef.getProductVendor());
        old.setProductVersion(templateDef.getProductVersion());
        old.setUrl(templateDef.getUrl());
        old.setTemplateDefinitionLists(templateDef.getTemplateDefinitionLists());

        update(old);

        return old;
    }

    public void update(final TemplateDefinition old)
    {
        dao.flush();

    }

    public void removeTemplateDefinition(final Integer id)
    {
        TemplateDefinition ovfPackage = dao.findById(id);

        // manually remove lists associated
        // As OVFPackage<->OVFPackageLists is a Many-to-many relation, the delete operation
        // must be done manually for the dependant (not owner) side in the relation: OVFPackage in
        // this case
        List<TemplateDefinitionList> lists = ovfPackage.getTemplateDefinitionLists();
        for (TemplateDefinitionList templateList : lists)
        {
            templateList.getTemplateDefinitions().remove(ovfPackage);
            listDao.flush();
        }

        dao.remove(ovfPackage);
    }

    public void removeTemplateDefinitionList(final TemplateDefinitionList templateDefList)
    {
        for (TemplateDefinition ovf : templateDefList.getTemplateDefinitions())
        {
            if (ovf.getTemplateDefinitionLists().size() == 1)
            {
                dao.remove(ovf);
            }
            else
            {
                ovf.getTemplateDefinitionLists().remove(templateDefList);
            }
        }
        listDao.remove(templateDefList);

    }

    public List<TemplateDefinitionList> getTemplateDefinitionLists()
    {
        return listDao.findAll();
    }

    public TemplateDefinitionList findTemplateDefinitionListByNameAndEnterprise(final String name,
        final Enterprise ent)
    {

        return listDao.findByNameAndEnterprise(name, ent);
    }

    public void persistTemplateDefinitionList(final TemplateDefinitionList templateDefList)
    {
        listDao.persist(templateDefList);
    }

    public void updateTemplateDefinitionList(final TemplateDefinitionList templateDefList)
    {
        listDao.flush();
    }

    public TemplateDefinitionList getTemplateDefinitionList(final Integer id)
    {
        return listDao.findById(id);
    }

    public List<TemplateDefinitionList> getTemplateDefinitionListsByEnterprise(
        final Integer idEnterprise)
    {
        return listDao.findByEnterprise(idEnterprise);

    }
}
