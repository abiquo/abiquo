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

package com.abiquo.server.core.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * Represent a collection of Licenses.
 */
@XmlRootElement(name = "licenses")
public class LicensesDto extends WrapperDto<LicenseDto>
{
    private static final long serialVersionUID = 1L;
    
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.licenses+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private Integer availablecores = 0;

    @Override
    public void add(LicenseDto element)
    {
        super.add(element);

        if (License.isActive(element))
        {
            availablecores += element.getNumcores();
        }
    }

    @Override
    @XmlElement(name = "license")
    public List<LicenseDto> getCollection()
    {
        return collection;
    }

    public Integer getAvailablecores()
    {
        return availablecores;
    }

    public void setAvailablecores(Integer availablecores)
    {
        this.availablecores = availablecores;
    }
    
    @Override
    public String getMediaType()
    {
        return LicensesDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
