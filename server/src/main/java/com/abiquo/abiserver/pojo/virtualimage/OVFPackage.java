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

package com.abiquo.abiserver.pojo.virtualimage;

public class OVFPackage
{

    Integer idOVFPackage;

    String url; // ovfid

    String name;

    String description;

    String productName;

    String productUrl;

    String productVersion;

    String productVendor;

    String category;

    String iconUrl;

    // icon tamany

    String diskFormat;

    Long diskSizeMb;

    public Integer getIdOVFPackage()
    {
        return idOVFPackage;
    }

    public void setIdOVFPackage(Integer idOVFPackage)
    {
        this.idOVFPackage = idOVFPackage;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductUrl()
    {
        return productUrl;
    }

    public void setProductUrl(String productUrl)
    {
        this.productUrl = productUrl;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(String productVersion)
    {
        this.productVersion = productVersion;
    }

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(String productVendor)
    {
        this.productVendor = productVendor;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getDiskFormat()
    {
        return diskFormat;
    }

    public void setDiskFormat(String diskFormat)
    {
        this.diskFormat = diskFormat;
    }

    public Long getDiskSizeMb()
    {
        return diskSizeMb;
    }

    public void setDiskSizeMb(Long diskSizeMb)
    {
        this.diskSizeMb = diskSizeMb;
    }
}
