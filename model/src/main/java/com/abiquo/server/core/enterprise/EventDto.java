package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "event")
public class EventDto extends SingleResourceTransportDto
{

    private static final long serialVersionUID = 1L;

    private Long idMeter;

    private Integer idDatacenter;

    private String datacenter;

    private Integer idRack;

    private String rack;

    private Integer idPhysicalMachine;

    private String physicalMachine;

    private Integer idStorageSystem;

    private String storageSystem;

    private String idStoragePool;

    private String storagePool;

    private String idVolume;

    private String volume;

    private Integer idNetwork;

    private String network;

    private Integer idSubnet;

    private String subnet;

    private Integer idEnterprise;

    private String enterprise;

    private Integer idUser;

    private String user;

    private Integer idVDC;

    private String virtualDataCenter;

    private Integer idVirtualApp;

    private String virtualApp;

    private Integer idVirtualMachine;

    private String virtualmachine;

    private String severity;

    private String timestamp;

    private String performedby;

    private String actionperformed;

    private String component;

    private String stacktrace;

    public Long getIdMeter()
    {
        return idMeter;
    }

    public void setIdMeter(final Long idMeter)
    {
        this.idMeter = idMeter;
    }

    public Integer getIdDatacenter()
    {
        return idDatacenter;
    }

    public void setIdDatacenter(final Integer idDatacenter)
    {
        this.idDatacenter = idDatacenter;
    }

    public String getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final String datacenter)
    {
        this.datacenter = datacenter;
    }

    public Integer getIdRack()
    {
        return idRack;
    }

    public void setIdRack(final Integer idRack)
    {
        this.idRack = idRack;
    }

    public String getRack()
    {
        return rack;
    }

    public void setRack(final String rack)
    {
        this.rack = rack;
    }

    public Integer getIdPhysicalMachine()
    {
        return idPhysicalMachine;
    }

    public void setIdPhysicalMachine(final Integer idPhysicalMachine)
    {
        this.idPhysicalMachine = idPhysicalMachine;
    }

    public String getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(final String physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public Integer getIdStorageSystem()
    {
        return idStorageSystem;
    }

    public void setIdStorageSystem(final Integer idStorageSystem)
    {
        this.idStorageSystem = idStorageSystem;
    }

    public String getStorageSystem()
    {
        return storageSystem;
    }

    public void setStorageSystem(final String storageSystem)
    {
        this.storageSystem = storageSystem;
    }

    public String getIdStoragePool()
    {
        return idStoragePool;
    }

    public void setIdStoragePool(final String idStoragePool)
    {
        this.idStoragePool = idStoragePool;
    }

    public String getStoragePool()
    {
        return storagePool;
    }

    public void setStoragePool(final String storagePool)
    {
        this.storagePool = storagePool;
    }

    public String getIdVolume()
    {
        return idVolume;
    }

    public void setIdVolume(final String idVolume)
    {
        this.idVolume = idVolume;
    }

    public String getVolume()
    {
        return volume;
    }

    public void setVolume(final String volume)
    {
        this.volume = volume;
    }

    public Integer getIdNetwork()
    {
        return idNetwork;
    }

    public void setIdNetwork(final Integer idNetwork)
    {
        this.idNetwork = idNetwork;
    }

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork(final String network)
    {
        this.network = network;
    }

    public Integer getIdSubnet()
    {
        return idSubnet;
    }

    public void setIdSubnet(final Integer idSubnet)
    {
        this.idSubnet = idSubnet;
    }

    public String getSubnet()
    {
        return subnet;
    }

    public void setSubnet(final String subnet)
    {
        this.subnet = subnet;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public String getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final String enterprise)
    {
        this.enterprise = enterprise;
    }

    public Integer getIdUser()
    {
        return idUser;
    }

    public void setIdUser(final Integer idUser)
    {
        this.idUser = idUser;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public Integer getIdVDC()
    {
        return idVDC;
    }

    public void setIdVDC(final Integer idVDC)
    {
        this.idVDC = idVDC;
    }

    public String getVirtualDataCenter()
    {
        return virtualDataCenter;
    }

    public void setVirtualDataCenter(final String virtualDataCenter)
    {
        this.virtualDataCenter = virtualDataCenter;
    }

    public Integer getIdVirtualApp()
    {
        return idVirtualApp;
    }

    public void setIdVirtualApp(final Integer idVirtualApp)
    {
        this.idVirtualApp = idVirtualApp;
    }

    public String getVirtualApp()
    {
        return virtualApp;
    }

    public void setVirtualApp(final String virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    public Integer getIdVirtualMachine()
    {
        return idVirtualMachine;
    }

    public void setIdVirtualMachine(final Integer idVirtualMachine)
    {
        this.idVirtualMachine = idVirtualMachine;
    }

    public String getVirtualmachine()
    {
        return virtualmachine;
    }

    public void setVirtualmachine(final String virtualmachine)
    {
        this.virtualmachine = virtualmachine;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(final String severity)
    {
        this.severity = severity;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getPerformedby()
    {
        return performedby;
    }

    public void setPerformedby(final String performedby)
    {
        this.performedby = performedby;
    }

    public String getActionperformed()
    {
        return actionperformed;
    }

    public void setActionperformed(final String actionperformed)
    {
        this.actionperformed = actionperformed;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(final String component)
    {
        this.component = component;
    }

    public String getStacktrace()
    {
        return stacktrace;
    }

    public void setStacktrace(final String stacktrace)
    {
        this.stacktrace = stacktrace;
    }
}
