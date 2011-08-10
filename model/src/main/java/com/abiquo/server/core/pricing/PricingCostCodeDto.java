package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "pricingCostCode")
public class PricingCostCodeDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private BigDecimal price;

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }

    private Integer idCostCode;

    public Integer getIdCostCode()
    {
        return idCostCode;
    }

    public void setIdCostCode(final Integer idCostCode)
    {
        this.idCostCode = idCostCode;
    }

    private Integer idPricing;

    public Integer getIdPricing()
    {
        return idPricing;
    }

    public void setIdPricing(final Integer idPricing)
    {
        this.idPricing = idPricing;
    }

}
