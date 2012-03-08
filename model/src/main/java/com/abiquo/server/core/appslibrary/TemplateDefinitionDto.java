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

import com.abiquo.model.enumerator.EthernetDriverType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "templateDefinition")
public class TemplateDefinitionDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -2655629613600887997L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.templatedefinition+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private Integer id;

    private String url;

    private String name;

    private String description;

    private String productName;

    private String productVendor;

    private String productUrl;

    private String productVersion;

    private String diskFormatType;

    private long diskFileSize;

    private EthernetDriverType ethernetDriverType;

    private String iconUrl;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(final String productVersion)
    {
        this.productVersion = productVersion;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = StringUtils.strip(name);
    }

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(final String productVendor)
    {
        this.productVendor = StringUtils.strip(productVendor);
    }

    public String getProductUrl()
    {
        return productUrl;
    }

    public void setProductUrl(final String productUrl)
    {
        this.productUrl = productUrl;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public String getDiskFormatType()
    {
        return diskFormatType;
    }

    public void setDiskFormatType(final String type)
    {
        this.diskFormatType = type;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = StringUtils.strip(productName);
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
        this.description = StringUtils.strip(description);
    }
    
    @Override
    public String getMediaType()
    {
        return TemplateDefinitionDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

    public void setIconUrl(final String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public EthernetDriverType getEthernetDriverType()
    {
        return ethernetDriverType;
    }

    public void setEthernetDriverType(final EthernetDriverType ethernetDriverType)
    {
        this.ethernetDriverType = ethernetDriverType;
    }
}
