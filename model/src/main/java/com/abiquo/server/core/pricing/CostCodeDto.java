package com.abiquo.server.core.pricing;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "costCode")
public class CostCodeDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CostCodeDto()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public CostCodeDto(final String variable)
    {
        super();
        this.variable = variable;
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

    private String variable;

    public String getVariable()
    {
        return variable;
    }

    public void setVariable(final String variable)
    {
        this.variable = variable;
    }

}
