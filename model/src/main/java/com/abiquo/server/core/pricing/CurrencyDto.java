package com.abiquo.server.core.pricing;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "currency")
public class CurrencyDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CurrencyDto()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public CurrencyDto(final String name, final String simbol)
    {
        super();
        this.name = name;
        this.simbol = simbol;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private String simbol;

    public String getSimbol()
    {
        return simbol;
    }

    public void setSimbol(final String simbol)
    {
        this.simbol = simbol;
    }

}
