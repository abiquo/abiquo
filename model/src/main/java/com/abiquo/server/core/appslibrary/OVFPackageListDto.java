package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class OVFPackageListDto extends SingleResourceTransportDto
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

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
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

    private List<OVFPackageDto> ovfPackages;

    public List<OVFPackageDto> getOvfPackages()
    {
        return ovfPackages;
    }

    public void setOvfPackages(final List<OVFPackageDto> ovfPackages)
    {
        this.ovfPackages = ovfPackages;
    }

}
