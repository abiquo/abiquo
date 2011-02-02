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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "ovfPackage")
public class OVFPackageDto extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = 273004070322922926L;

    private Integer id;

    private String description;

    private String url;

    private String productName;

    private String productUrl;

    private String productVersion;

    private String productVendor;

    private String categoryName;

    private String iconPath;

    private String diskFormatTypeUri;

    private Long diskSizeMb;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;
    }

    public String getDiskFormatTypeUri()
    {
        return diskFormatTypeUri;
    }

    public void setDiskFormatTypeUri(String diskFormatTypeUri)
    {
        this.diskFormatTypeUri = diskFormatTypeUri;
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
