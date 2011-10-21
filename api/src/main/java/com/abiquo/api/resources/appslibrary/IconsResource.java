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

package com.abiquo.api.resources.appslibrary;

import java.util.ArrayList;
import java.util.Collection;

import static com.abiquo.api.resources.appslibrary.IconResource.createTransferObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.appslibrary.IconService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.IconsDto;

@Path(IconsResource.ICONS_PATH)
@Controller
@Workspace(workspaceTitle = "Virtual Images Icons", collectionTitle = "Icons")
public class IconsResource extends AbstractResource
{

    public static final String ICONS_PATH = "config/icons";

    public static final String PATH = "path";

    @Autowired
    private IconService service;

    @GET
    public IconsDto getIcons(@QueryParam(PATH) final String path,
        @Context final IRESTBuilder restBuilder)
    {
        Collection<Icon> icons = new ArrayList<Icon>();
        if (path == null)
        {
            icons = service.getIcons();
        }
        else
        {
            Icon icon = service.getIconByPath(path);
            icons.add(icon);
        }
        IconsDto iconsDto = new IconsDto();
        for (Icon icon : icons)
        {
            IconDto icondto = createTransferObject(icon, restBuilder);
            iconsDto.add(icondto);
        }
        return iconsDto;

    }

    @POST
    public IconDto addIcon(final IconDto iconDto, @Context final IRESTBuilder restBuilder)
    {
        Icon icon = service.addIcon(iconDto, restBuilder);
        return createTransferObject(icon, restBuilder);

    }
}
