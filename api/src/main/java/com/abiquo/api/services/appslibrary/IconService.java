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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.IconDto;

@Service
public class IconService extends DefaultApiService
{
    @Autowired
    private AppsLibraryRep appslibraryRep;

    public IconService()
    {

    }

    public IconService(final EntityManager em)
    {
        appslibraryRep = new AppsLibraryRep(em);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<Icon> getIcons()
    {
        return appslibraryRep.findAllIcons();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon getIconByPath(final String path)
    {
        Icon icon = appslibraryRep.findIconByPath(path);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon findById(final Integer iconId)
    {
        Icon icon = appslibraryRep.findIconById(iconId);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon modifyIcon(final Integer iconId, final IconDto iconDto)
    {
        Icon old = findById(iconId);

        old.setName(iconDto.getName());
        old.setPath(iconDto.getPath());

        appslibraryRep.updateIcon(old);

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteIcon(final Integer iconId)
    {
        Icon icon = findById(iconId);

        if (appslibraryRep.isIconInUseByVirtualImages(icon))
        {
            addConflictErrors(APIError.ICON_IN_USE_BY_VIRTUAL_IMAGES);
            flushErrors();
        }

        appslibraryRep.deleteIcon(icon);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon addIcon(final IconDto iconDto, final IRESTBuilder restBuilder)
    {
        Icon icon = appslibraryRep.findIconByPath(iconDto.getPath());
        if (icon != null)
        {
            addConflictErrors(APIError.ICON_DUPLICATED_PATH);
            flushErrors();
        }

        Icon newIcon = new Icon(iconDto.getName(), iconDto.getPath());
        appslibraryRep.insertIcon(newIcon);

        return newIcon;
    }

}
