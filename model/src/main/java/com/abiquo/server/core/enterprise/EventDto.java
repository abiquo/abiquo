package com.abiquo.server.core.enterprise;

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

    private int storagePool;

    public int getStoragePool()
    {
        return storagePool;
    }

    public void setStoragePool(final int storagePool)
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

    private int timestamp;

    public int getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final int timestamp)
    {
        this.timestamp = timestamp;
    }

    private int virtualApp;

    public int getVirtualApp()
    {
        return virtualApp;
    }

    public void setVirtualApp(final int virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    private int datacenter;

    public int getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final int datacenter)
    {
        this.datacenter = datacenter;
    }

    private int virtualDatacenter;

    public int getVirtualDatacenter()
    {
        return virtualDatacenter;
    }

    public void setVirtualDatacenter(final int virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    private int enterprise;

    public int getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final int enterprise)
    {
        this.enterprise = enterprise;
    }

    private int storageSystem;

    public int getStorageSystem()
    {
        return storageSystem;
    }

    public void setStorageSystem(final int storageSystem)
    {
        this.storageSystem = storageSystem;
    }

    private int network;

    public int getNetwork()
    {
        return network;
    }

    public void setNetwork(final int network)
    {
        this.network = network;
    }

    private int physicalMachine;

    public int getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(final int physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    private int rack;

    public int getRack()
    {
        return rack;
    }

    public void setRack(final int rack)
    {
        this.rack = rack;
    }

    private int virtualMachine;

    public int getVirtualMachine()
    {
        return virtualMachine;
    }

    public void setVirtualMachine(final int virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    private int volume;

    public int getVolume()
    {
        return volume;
    }

    public void setVolume(final int volume)
    {
        this.volume = volume;
    }

    private int subnet;

    public int getSubnet()
    {
        return subnet;
    }

    public void setSubnet(final int subnet)
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

    private int user;

    public int getUser()
    {
        return user;
    }

    public void setUser(final int user)
    {
        this.user = user;
    }

}
