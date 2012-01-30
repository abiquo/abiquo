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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "hypervisortype")
public class HypervisorTypeDto extends SingleResourceTransportDto
{

    private static final long serialVersionUID = -6899075534020087650L;

    private int defaultPort;

    private DiskFormatType baseFormat;

    private DiskFormatType[] compatibilityTable;

    private Integer id;

    private String name;

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

    public DiskFormatType[] getCompatibilityTable()
    {
        return compatibilityTable;
    }

    public void setCompatibilityTable(final DiskFormatType[] compatibilityTable)
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
}
