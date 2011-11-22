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

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class VirtualImageConversionDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private DiskFormatType sourceType;

    public DiskFormatType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(final DiskFormatType sourceType)
    {
        this.sourceType = sourceType;
    }

    private DiskFormatType targetType;

    public DiskFormatType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(final DiskFormatType targetType)
    {
        this.targetType = targetType;
    }

    private String sourcePath;

    public String getSourcePath()
    {
        return sourcePath;
    }

    public void setSourcePath(final String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    private String targetPath;

    public String getTargetPath()
    {
        return targetPath;
    }

    public void setTargetPath(final String targetPath)
    {
        this.targetPath = targetPath;
    }

    private ConversionState state;

    public ConversionState getState()
    {
        return state;
    }

    public void setState(final ConversionState state)
    {
        this.state = state;
    }

    private long size;

    public long getSize()
    {
        return size;
    }

    public void setSize(final long size)
    {
        this.size = size;
    }

}
