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

package com.abiquo.api.services.config;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDAO;
import com.abiquo.server.core.config.IconDto;

@Service
public class IconService extends DefaultApiService
{

    @Autowired
    private IconDAO dao;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<Icon> getIcons()
    {
        return dao.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon getIconByPath(final String path)
    {

        Icon icon = dao.findByPath(path);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon findById(final Integer iconId)
    {
        Icon icon = dao.findById(iconId);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon modifyIcon(final Integer iconId, final IconDto iconDto)
    {
        Icon old = dao.findById(iconId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }

        old.setName(iconDto.getName());
        old.setPath(iconDto.getPath());

        dao.update(old);

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteIcon(final Integer iconId)
    {
        Icon icon = dao.findById(iconId);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        if (dao.iconInUseByVirtualImages(icon))
        {
            addConflictErrors(APIError.ICON_IN_USE_BY_VIRTUAL_IMAGES);
            flushErrors();
        }
        dao.remove(icon);
        dao.flush();

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon addIcon(final IconDto iconDto, final IRESTBuilder restBuilder)
    {
        Icon icon = dao.findByPath(iconDto.getPath());
        if (icon != null)
        {
            addConflictErrors(APIError.ICON_DUPLICATED_PATH);
            flushErrors();
        }

        Icon newIcon = new Icon(iconDto.getPath());
        newIcon.setName(iconDto.getName());
        dao.persist(newIcon);
        dao.flush();

        return newIcon;
    }

}
