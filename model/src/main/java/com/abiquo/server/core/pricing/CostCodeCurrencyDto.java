package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "costeCodeCurrency")
public class CostCodeCurrencyDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = -5240459003907673943L;

    public CostCodeCurrencyDto()
    {
    }

    public CostCodeCurrencyDto(final BigDecimal purchase)
    {
        this.purchase = purchase;

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

    private BigDecimal purchase;

    public BigDecimal getPurchase()
    {
        return purchase;
    }

    public void setPurchase(final BigDecimal purchase)
    {
        this.purchase = purchase;
    }

    private Integer idCurrency;

    public Integer getIdCurrency()
    {
        return idCurrency;
    }

    public void setIdCurrency(final Integer idCurrency)
    {
        this.idCurrency = idCurrency;
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

}
