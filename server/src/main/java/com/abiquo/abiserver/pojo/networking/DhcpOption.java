package com.abiquo.abiserver.pojo.networking;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DhcpOptionHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;

public class DhcpOption implements IPojo<DhcpOptionHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    private int option;

    private String description;

    /* ------------- Constructor ------------- */
    public DhcpOption()
    {
        id = 0;
        option = 0;
        description = "";

    }

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    @Override
    public DhcpOptionHB toPojoHB()
    {
        DhcpOptionHB dhcpOptionHB = new DhcpOptionHB();

        dhcpOptionHB.setIdDhcpOption(id);
        dhcpOptionHB.setDescription(description);
        dhcpOptionHB.setOption(option);

        return dhcpOptionHB;
    }

    public static DhcpOption create(final DhcpOptionDto dto)
    {
        DhcpOption dhcpOption = new DhcpOption();
        dhcpOption.setId(dto.getId());
        dhcpOption.setDescription(dto.getDescription());
        dhcpOption.setOption(dto.getOption());

        return dhcpOption;
    }

}
