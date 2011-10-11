package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class EventDto extends SingleResourceTransportDto
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

    private String component;

    public String getComponent()
    {
        return component;
    }

    public void setComponent(final String component)
    {
        this.component = component;
    }

    private String actionPerformed;

    public String getActionPerformed()
    {
        return actionPerformed;
    }

    public void setActionPerformed(final String actionPerformed)
    {
        this.actionPerformed = actionPerformed;
    }

    private String performedBy;

    public String getPerformedBy()
    {
        return performedBy;
    }

    public void setPerformedBy(final String performedBy)
    {
        this.performedBy = performedBy;
    }

    private String storagePool;

    public String getStoragePool()
    {
        return storagePool;
    }

    public void setStoragePool(final String storagePool)
    {
        this.storagePool = storagePool;
    }

    private String stracktrace;

    public String getStracktrace()
    {
        return stracktrace;
    }

    public void setStracktrace(final String stracktrace)
    {
        this.stracktrace = stracktrace;
    }

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    private String virtualApp;

    public String getVirtualApp()
    {
        return virtualApp;
    }

    public void setVirtualApp(final String virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    private String datacenter;

    public String getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final String datacenter)
    {
        this.datacenter = datacenter;
    }

    private String virtualDatacenter;

    public String getVirtualDatacenter()
    {
        return virtualDatacenter;
    }

    public void setVirtualDatacenter(final String virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    private String enterprise;

    public String getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final String enterprise)
    {
        this.enterprise = enterprise;
    }

    private String storageSystem;

    public String getStorageSystem()
    {
        return storageSystem;
    }

    public void setStorageSystem(final String storageSystem)
    {
        this.storageSystem = storageSystem;
    }

    private String network;

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork(final String network)
    {
        this.network = network;
    }

    private String physicalMachine;

    public String getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(final String physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    private String rack;

    public String getRack()
    {
        return rack;
    }

    public void setRack(final String rack)
    {
        this.rack = rack;
    }

    private String virtualMachine;

    public String getVirtualMachine()
    {
        return virtualMachine;
    }

    public void setVirtualMachine(final String virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    private String volume;

    public String getVolume()
    {
        return volume;
    }

    public void setVolume(final String volume)
    {
        this.volume = volume;
    }

    private String subnet;

    public String getSubnet()
    {
        return subnet;
    }

    public void setSubnet(final String subnet)
    {
        this.subnet = subnet;
    }

    private String severity;

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(final String severity)
    {
        this.severity = severity;
    }

    private String user;

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

}
