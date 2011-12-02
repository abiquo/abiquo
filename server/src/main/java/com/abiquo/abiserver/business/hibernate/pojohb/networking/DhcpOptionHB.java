package com.abiquo.abiserver.business.hibernate.pojohb.networking;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.networking.DhcpOption;

public class DhcpOptionHB implements java.io.Serializable, IPojoHB<DhcpOption>
{

    private static final long serialVersionUID = -5172429643785560320L;

    private Integer idDhcpOption;

    private int option;

    /**
     * The IP address of the gateway.
     */
    private String gateway;

    /**
     * The network that defines the address.
     */
    private String networkAddress;

    /**
     * The mask value in the integer way (/24)
     */
    private Integer mask;

    /**
     * The mask value in IP way (255.255.255.0)
     */
    private String netmask;

    public Integer getIdDhcpOption()
    {
        return idDhcpOption;
    }

    public void setIdDhcpOption(final Integer idDhcpOption)
    {
        this.idDhcpOption = idDhcpOption;
    }

    public int getOption()
    {
        return option;
    }

    public void setOption(final int option)
    {
        this.option = option;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public String getNetworkAddress()
    {
        return networkAddress;
    }

    public void setNetworkAddress(final String networkAddress)
    {
        this.networkAddress = networkAddress;
    }

    public Integer getMask()
    {
        return mask;
    }

    public void setMask(final Integer mask)
    {
        this.mask = mask;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public void setNetmask(final String netmask)
    {
        this.netmask = netmask;
    }

    @Override
    public DhcpOption toPojo()
    {
        DhcpOption d = new DhcpOption();

        d.setId(idDhcpOption);
        d.setMask(mask);
        d.setNetmask(netmask);
        d.setNetworkAddress(networkAddress);
        d.setOption(option);
        return d;
    }

}
