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

package com.abiquo.server.core.cloud;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "hypervisortype")
public class HypervisorTypeDto extends SingleResourceTransportDto
{

    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.hypervisortype+xml";

    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private static final long serialVersionUID = -6899075534020087650L;

    private int defaultPort;

    private DiskFormatType baseFormat;

    private Set<DiskFormatType> compatibilityTable;

    private Integer id;

    private String name;

    private String realName;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer Id)
    {
        id = Id;
    }

    public DiskFormatType getBaseFormat()
    {
        return baseFormat;
    }

    public void setBaseFormat(final DiskFormatType baseFormat)
    {
        this.baseFormat = baseFormat;
    }

    public Set<DiskFormatType> getCompatibilityTable()
    {
        return compatibilityTable;
    }

    public void setCompatibilityTable(final Set<DiskFormatType> compatibilityTable)
    {
        this.compatibilityTable = compatibilityTable;
    }

    public int getDefaultPort()
    {
        return defaultPort;
    }

    public void setDefaultPort(final int defaultPort)
    {
        this.defaultPort = defaultPort;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(final String realName)
    {
        this.realName = realName;
    }

    @Override
    public String getMediaType()
    {
        return HypervisorTypeDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
