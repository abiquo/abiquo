/**
 * 
 */
package com.abiquo.server.core.infrastructure.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Configuration network for a virtual machine.
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "vmnetworkconfiguration")
public class VMNetworkConfigurationDto extends SingleResourceTransportDto
{
    /**
     * Generated serial version id.
     */
    private static final long serialVersionUID = -3866622562676820662L;

    public VMNetworkConfigurationDto()
    {

    }

    private Integer id;

    private String gateway;

    private String primaryDNS;

    private String secondaryDNS;

    private String suffixDNS;

    private Boolean used;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(final String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(final String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    public String getSuffixDNS()
    {
        return suffixDNS;
    }

    public void setSuffixDNS(final String suffixDNS)
    {
        this.suffixDNS = suffixDNS;
    }

    public Boolean getUsed()
    {
        return used;
    }

    public void setUsed(final Boolean used)
    {
        this.used = used;
    }

}
