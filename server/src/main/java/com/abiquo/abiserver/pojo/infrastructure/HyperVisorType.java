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

package com.abiquo.abiserver.pojo.infrastructure;

import com.abiquo.abiserver.pojo.virtualimage.DiskFormatType;
import com.abiquo.server.core.enumerator.HypervisorType;

public class HyperVisorType
{
    private int id;

    private String name;

    private int defaultPort;

    private DiskFormatType baseFormat;

    public HyperVisorType()
    {

    }

    public HyperVisorType(int id)
    {
        this(HypervisorType.fromId(id));
    }

    public HyperVisorType(HypervisorType type)
    {
        this.id = type.id();
        this.defaultPort = type.defaultPort;
        this.name = type.getValue();
        this.baseFormat = new DiskFormatType(type.baseFormat);
    }

    public DiskFormatType getBaseFormat()
    {
        return baseFormat;
    }

    public void setBaseFormat(DiskFormatType baseFormat)
    {
        this.baseFormat = baseFormat;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getDefaultPort()
    {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort)
    {
        this.defaultPort = defaultPort;
    }

    public static HyperVisorType create(HypervisorType type, DiskFormatType baseFormat)
    {
        HyperVisorType hyperVisorType = new HyperVisorType();
        hyperVisorType.setId(type.ordinal());
        hyperVisorType.setName(type.name());
        hyperVisorType.setDefaultPort(type.defaultPort);
        hyperVisorType.setBaseFormat(baseFormat);

        return hyperVisorType;
    }
}
