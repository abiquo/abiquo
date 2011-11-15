package com.abiquo.abiserver.pojo.networking;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DhcpOptionHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;

public class DhcpOption implements IPojo<DhcpOptionHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    /**
     * Dhcp option number.
     */

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

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
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
    public DhcpOptionHB toPojoHB()
    {
        DhcpOptionHB dhcpOptionHB = new DhcpOptionHB();

        dhcpOptionHB.setIdDhcpOption(id);
        dhcpOptionHB.setMask(mask);
        dhcpOptionHB.setNetmask(netmask);
        dhcpOptionHB.setNetworkAddress(networkAddress);
        dhcpOptionHB.setOption(option);

        return dhcpOptionHB;
    }

    public static DhcpOption create(final DhcpOptionDto dto)
    {
        DhcpOption dhcpOption = new DhcpOption();
        dhcpOption.setId(dto.getId());
        dhcpOption.setMask(dto.getMask());
        dhcpOption.setNetmask(dto.getNetmask());
        dhcpOption.setNetworkAddress(dto.getNetworkAddress());
        dhcpOption.setOption(dto.getOption());

        return dhcpOption;
    }

}
