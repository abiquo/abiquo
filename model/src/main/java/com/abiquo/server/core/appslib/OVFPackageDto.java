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

package com.abiquo.server.core.appslib;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.enumerator.DiskFormatType;

@XmlRootElement(name = "ovfPackage")
public class OVFPackageDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String productVersion;

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(String productVersion)
    {
        this.productVersion = productVersion;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String productVendor;

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(String productVendor)
    {
        this.productVendor = productVendor;
    }

    private String productUrl;

    public String getProductUrl()
    {
        return productUrl;
    }

    public void setProductUrl(String productUrl)
    {
        this.productUrl = productUrl;
    }

    private String url;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    private DiskFormatType type;

    public DiskFormatType getType()
    {
        return type;
    }

    public void setType(DiskFormatType type)
    {
        this.type = type;
    }

    private String productName;

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    private long diskSizeMb;

    public long getDiskSizeMb()
    {
        return diskSizeMb;
    }

    public void setDiskSizeMb(int diskSizeMb)
    {
        this.diskSizeMb = diskSizeMb;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    private CategoryDto category;

    public void setCategory(CategoryDto category)
    {
        this.category = category;
    }

    public CategoryDto getCategory()
    {
        return category;
    }

    private IconDto icon;

    public void setIcon(IconDto icon)
    {
        this.icon = icon;
    }

    public IconDto getIcon()
    {
        return icon;
    }

}
