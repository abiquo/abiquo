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

package com.abiquo.api.resources.config;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.config.IconService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;

@Parent(IconsResource.class)
@Controller
@Path(IconResource.ICON_PARAM)
public class IconResource extends AbstractResource
{

    public static final String ICON = "icon";

    public static final String ICON_PARAM = "{" + ICON + "}";

    @Autowired
    private IconService service;

    @GET
    public IconDto getIcon(@PathParam(ICON) final Integer iconId,
        @Context final IRESTBuilder restBuilder)
    {
        Icon icon = service.findById(iconId);
        return createTransferObject(icon, restBuilder);
    }

    @PUT
    public IconDto modifyIcon(final IconDto iconDto, @PathParam(ICON) final Integer iconId,
        @Context final IRESTBuilder restBuilder)
    {
        Icon icon = service.modifyIcon(iconId, iconDto);
        return createTransferObject(icon, restBuilder);
    }

    @DELETE
    public void deleteIcon(@PathParam(ICON) final Integer iconId,
        @Context final IRESTBuilder restBuilder)
    {
        service.deleteIcon(iconId);
    }

    public static IconDto createTransferObject(final Icon icon, final IRESTBuilder builder)
    {
        IconDto iconDto = new IconDto();
        iconDto.setId(icon.getId());
        iconDto.setName(icon.getName());
        iconDto.setPath(icon.getPath());

        iconDto = addLinks(iconDto, builder);

        return iconDto;
    }

    private static IconDto addLinks(final IconDto icon, final IRESTBuilder builder)
    {
        icon.setLinks(builder.buildIconLinks(icon));

        return icon;
    }
}
