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

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.config.IconDto;

@XmlRootElement(name = "virtualImage")
public class VirtualImageDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private int idFormat;

    public int getIdFormat()
    {
        return idFormat;
    }

    public void setIdFormat(final int idFormat)
    {
        this.idFormat = idFormat;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private int stateful;

    public int getStateful()
    {
        return stateful;
    }

    public void setStateful(final int stateful)
    {
        this.stateful = stateful;
    }

    private int treaty;

    public int getTreaty()
    {
        return treaty;
    }

    public void setTreaty(final int treaty)
    {
        this.treaty = treaty;
    }

    private int cpuRequired;

    public int getCpuRequired()
    {
        return cpuRequired;
    }

    public void setCpuRequired(final int cpuRequired)
    {
        this.cpuRequired = cpuRequired;
    }

    private String pathName;

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName(final String pathName)
    {
        this.pathName = pathName;
    }

    private String ovfid;

    public String getOvfid()
    {
        return ovfid;
    }

    public void setOvfid(final String ovfid)
    {
        this.ovfid = ovfid;
    }

    private int ramRequired;

    public int getRamRequired()
    {
        return ramRequired;
    }

    public void setRamRequired(final int ramRequired)
    {
        this.ramRequired = ramRequired;
    }

    private long hdRequired;

    public long getHdRequired()
    {
        return hdRequired;
    }

    public void setHdRequired(final long hdRequired)
    {
        this.hdRequired = hdRequired;
    }

    private int deleted;

    public int getDeleted()
    {
        return deleted;
    }

    public void setDeleted(final int deleted)
    {
        this.deleted = deleted;
    }

    private int idCategory;

    public int getIdCategory()
    {
        return idCategory;
    }

    public void setIdCategory(final int idCategory)
    {
        this.idCategory = idCategory;
    }

    private long diskFileSize;

    public long getDiskFileSize()
    {
        return diskFileSize;
    }

    public void setDiskFileSize(final long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    private IconDto icon;

    public IconDto getIcon()
    {
        return icon;
    }

    public void setIcon(final IconDto icon)
    {
        this.icon = icon;
    }

    private int idRepository;

    public int getIdRepository()
    {
        return idRepository;
    }

    public void setIdRepository(final int idRepository)
    {
        this.idRepository = idRepository;
    }

}
