package com.abiquo.server.core.infrastructure.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.cloud.VirtualMachine;

/**
 * The representation of {@link IpPoolManagementDto} when we retrieve it from a
 * {@link VirtualMachine}.
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "nic")
public class NicDto extends SingleResourceTransportDto
{

    /**
     * 
     */
    private static final long serialVersionUID = 3595021795100699222L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String mac;

    public String getMac()
    {
        return mac;
    }

    public void setMac(final String mac)
    {
        this.mac = mac;
    }

    private String ip;

    public String getIp()
    {
        return ip;
    }

    public void setIp(final String ip)
    {
        this.ip = ip;
    }
}
