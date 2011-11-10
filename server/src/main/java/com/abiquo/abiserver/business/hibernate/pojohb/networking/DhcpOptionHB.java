package com.abiquo.abiserver.business.hibernate.pojohb.networking;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.networking.DhcpOption;

public class DhcpOptionHB implements java.io.Serializable, IPojoHB<DhcpOption>
{

    private static final long serialVersionUID = -5172429643785560320L;

    private Integer idDhcpOption;

    private String description;

    private int option;

    public Integer getIdDhcpOption()
    {
        return idDhcpOption;
    }

    public void setIdDhcpOption(final Integer idDhcpOption)
    {
        this.idDhcpOption = idDhcpOption;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public int getOption()
    {
        return option;
    }

    public void setOption(final int option)
    {
        this.option = option;
    }

    @Override
    public DhcpOption toPojo()
    {
        DhcpOption d = new DhcpOption();

        d.setId(idDhcpOption);
        d.setDescription(description);
        d.setOption(option);
        return d;
    }

}
