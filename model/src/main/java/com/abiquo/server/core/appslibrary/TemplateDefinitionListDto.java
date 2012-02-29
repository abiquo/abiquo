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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

@XmlRootElement(name = "templateDefinitionList")
public class TemplateDefinitionListDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -394035712365582338L;
    
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.templatedefinitionlist+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    
    private Integer id;

    private String name;

    private String url;

    private TemplateDefinitionsDto templateDefinitions;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public TemplateDefinitionsDto getTemplateDefinitions()
    {
        return templateDefinitions; // TODO GET or CREATE
    }

    public void setTemplateDefinitions(final TemplateDefinitionsDto ovfPackages)
    {
        this.templateDefinitions = ovfPackages;
    }
    
    @Override
    public String getMediaType()
    {
        return TemplateDefinitionListDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
