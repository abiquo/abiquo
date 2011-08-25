package com.abiquo.server.core.appslibrary;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class OVFPackageDto extends SingleResourceTransportDto
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
        this.name = name;
    }

    private String productVendor;

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(final String productVendor)
    {
        this.productVendor = productVendor;
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
        this.productName = productName;
    }

    private long diskSizeMb;

    public long getDiskSizeMb()
    {
        return diskSizeMb;
    }

    public void setDiskSizeMb(final long diskSizeMb)
    {
        this.diskSizeMb = diskSizeMb;
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

}
