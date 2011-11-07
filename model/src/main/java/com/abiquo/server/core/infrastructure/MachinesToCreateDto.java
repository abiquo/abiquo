package com.abiquo.server.core.infrastructure;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "machinesToCreate")
public class MachinesToCreateDto extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = 1682439124759672513L;

    private String ipFrom;

    private String ipTo;

    private String hypervisor;

    private String user;

    private String password;

    private Integer port;

    private String vSwitch;

    public String getIpFrom()
    {
        return ipFrom;
    }

    public void setIpFrom(final String ipFrom)
    {
        this.ipFrom = ipFrom;
    }

    public String getIpTo()
    {
        return ipTo;
    }

    public void setIpTo(final String ipTo)
    {
        this.ipTo = ipTo;
    }

    public String getHypervisor()
    {
        return hypervisor;
    }

    public void setHypervisor(final String hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(final Integer port)
    {
        this.port = port;
    }

    public String getvSwitch()
    {
        return vSwitch;
    }

    public void setvSwitch(final String vSwitch)
    {
        this.vSwitch = vSwitch;
    }
    
    public MachinesToCreateDto(){
        
    }
    
    public MachinesToCreateDto(String ipFrom, String ipTo, String hypervisor, String user, String password, Integer port, String vSwitch){
        this.ipFrom=ipFrom;
        this.ipTo=ipTo;
        this.hypervisor=hypervisor;
        this.user=user;
        this.password=password;
        this.port=port;
        this.vSwitch=vSwitch;
    }

}
