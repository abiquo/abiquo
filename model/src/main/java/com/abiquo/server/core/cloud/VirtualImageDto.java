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
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "virtualImage")
@XmlType(propOrder = {"id", "name", "description", "pathName", "diskFormatType", "diskFileSize",
"cpuRequired", "ramRequired", "hdRequired", "shared", "stateful", "costCode"})
public class VirtualImageDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String diskFormatType;

    private String name;

    private boolean stateful;

    private int cpuRequired;

    private String pathName;

    private int ramRequired;

    private long hdRequired;

    private long diskFileSize;

    private String description;

    private boolean shared;

    private String costCode;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getDiskFormatType()
    {
        return diskFormatType;
    }

    public void setDiskFormatType(final String diskFormatType)
    {
        this.diskFormatType = diskFormatType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public boolean isStateful()
    {
        return stateful;
    }

    public void setStateful(final boolean stateful)
    {
        this.stateful = stateful;
    }

    public int getCpuRequired()
    {
        return cpuRequired;
    }

    public void setCpuRequired(final int cpuRequired)
    {
        this.cpuRequired = cpuRequired;
    }

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName(final String pathName)
    {
        this.pathName = pathName;
    }

    public int getRamRequired()
    {
        return ramRequired;
    }

    public void setRamRequired(final int ramRequired)
    {
        this.ramRequired = ramRequired;
    }

    public long getHdRequired()
    {
        return hdRequired;
    }

    public void setHdRequired(final long hdRequired)
    {
        this.hdRequired = hdRequired;
    }

    public long getDiskFileSize()
    {
        return diskFileSize;
    }

    public void setDiskFileSize(final long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public boolean isShared()
    {
        return shared;
    }

    public void setShared(final boolean shared)
    {
        this.shared = shared;
    }

    public String getCostCode()
    {
        return costCode;
    }

    public void setCostCode(final String costCode)
    {
        this.costCode = costCode;
    }

}
