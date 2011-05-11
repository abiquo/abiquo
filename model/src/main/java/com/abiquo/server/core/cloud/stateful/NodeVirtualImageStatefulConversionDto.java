package com.abiquo.server.core.cloud.stateful;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class NodeVirtualImageStatefulConversionDto extends SingleResourceTransportDto
{
    private String diskStatefulConversion;

    public String getDiskStatefulConversion()
    {
        return diskStatefulConversion;
    }

    public void setDiskStatefulConversion(final String diskStatefulConversion)
    {
        this.diskStatefulConversion = diskStatefulConversion;
    }

    private String newName;

    public String getNewName()
    {
        return newName;
    }

    public void setNewName(final String newName)
    {
        this.newName = newName;
    }

    private String tier;

    public String getTier()
    {
        return tier;
    }

    public void setTier(final String tier)
    {
        this.tier = tier;
    }

    private String virtualApplianceStatefulConversion;

    public String getVirtualApplianceStatefulConversion()
    {
        return virtualApplianceStatefulConversion;
    }

    public void setVirtualApplianceStatefulConversion(
        final String virtualApplianceStatefulConversion)
    {
        this.virtualApplianceStatefulConversion = virtualApplianceStatefulConversion;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String nodeVirtualImage;

    public String getNodeVirtualImage()
    {
        return nodeVirtualImage;
    }

    public void setNodeVirtualImage(final String nodeVirtualImage)
    {
        this.nodeVirtualImage = nodeVirtualImage;
    }

    private String virtualImageConversion;

    public String getVirtualImageConversion()
    {
        return virtualImageConversion;
    }

    public void setVirtualImageConversion(final String virtualImageConversion)
    {
        this.virtualImageConversion = virtualImageConversion;
    }

}
