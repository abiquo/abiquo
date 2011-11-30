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

import org.apache.commons.lang.StringUtils;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "ovfPackage")
public class OVFPackageDto extends SingleResourceTransportDto
{
    /**
     * 
     */
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

    private String productVersion;

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(final String productVersion)
    {
        this.productVersion = productVersion;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = StringUtils.strip(name);
    }

    private String productVendor;

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(final String productVendor)
    {
        this.productVendor = StringUtils.strip(productVendor);
    }

    private String productUrl;

    public String getProductUrl()
    {
        return productUrl;
    }

    public void setProductUrl(final String productUrl)
    {
        this.productUrl = productUrl;
    }

    private String url;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

    private DiskFormatType type;

    public DiskFormatType getType()
    {
        return type;
    }

    public void setType(final DiskFormatType type)
    {
        this.type = type;
    }

    private String productName;

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = StringUtils.strip(productName);
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
        this.description = StringUtils.strip(description);
    }

    private String diskFormatTypeUri;

    public String getDiskFormatTypeUri()
    {
        return diskFormatTypeUri;
    }

    public void setDiskFormatTypeUri(final String diskFormatTypeUri)
    {
        this.diskFormatTypeUri = diskFormatTypeUri;
    }

}
