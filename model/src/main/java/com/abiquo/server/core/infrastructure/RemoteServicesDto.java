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

package com.abiquo.server.core.infrastructure;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;
import com.abiquo.model.transport.error.ErrorsDto;

@XmlRootElement(name = "remoteServices")
public class RemoteServicesDto extends WrapperDto<RemoteServiceDto>
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.remoteservices+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    /**
     * Default serial version.
     */
    private static final long serialVersionUID = 1L;

    private ErrorsDto configErrors;

    public ErrorsDto getConfigErrors()
    {
        return configErrors;
    }

    public void setConfigErrors(final ErrorsDto configErrors)
    {
        this.configErrors = configErrors;
    }

    @Override
    @XmlElement(name = "remoteService")
    public List<RemoteServiceDto> getCollection()
    {
        if (collection == null)
        {
            collection = new ArrayList<RemoteServiceDto>();
        }
        return collection;
    }
    
    @Override
    public String getMediaType()
    {
        return RemoteServicesDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
