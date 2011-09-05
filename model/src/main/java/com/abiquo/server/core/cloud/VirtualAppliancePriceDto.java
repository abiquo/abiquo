package com.abiquo.server.core.cloud;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "virtualAppliancePrice")
public class VirtualAppliancePriceDto extends SingleResourceTransportDto
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public VirtualAppliancePriceDto()
    {
    }

    public VirtualAppliancePriceDto(final BigDecimal costCodeCost, final BigDecimal computeCost,
        final BigDecimal networkCost, final BigDecimal storageCost,
        final BigDecimal additionalVolumCost, final BigDecimal totalCost)
    {
        super();
        this.costCodeCost = costCodeCost;
        this.computeCost = computeCost;
        this.networkCost = networkCost;
        this.storageCost = storageCost;
        this.additionalVolumCost = additionalVolumCost;
        this.totalCost = totalCost;
    }

    /** Value for the image being deployed */
    private BigDecimal costCodeCost;

    /** Value for the CPU, memory */
    private BigDecimal computeCost;

    /** Value for public IPs */
    private BigDecimal networkCost;

    /** Value for the datastore */
    private BigDecimal storageCost;

    /** Value for the volume */
    private BigDecimal additionalVolumCost;

    /** Value total */
    private BigDecimal totalCost;

    public BigDecimal getCostCodeCost()
    {
        return costCodeCost;
    }

    public void setCostCodeCost(final BigDecimal costCodeCost)
    {
        this.costCodeCost = costCodeCost;
    }

    public BigDecimal getComputeCost()
    {
        return computeCost;
    }

    public void setComputeCost(final BigDecimal computeCost)
    {
        this.computeCost = computeCost;
    }

    public BigDecimal getNetworkCost()
    {
        return networkCost;
    }

    public void setNetworkCost(final BigDecimal networkCost)
    {
        this.networkCost = networkCost;
    }

    public BigDecimal getStorageCost()
    {
        return storageCost;
    }

    public void setStorageCost(final BigDecimal storageCost)
    {
        this.storageCost = storageCost;
    }

    public BigDecimal getAdditionalVolumCost()
    {
        return additionalVolumCost;
    }

    public void setAdditionalVolumCost(final BigDecimal additionalVolumCost)
    {
        this.additionalVolumCost = additionalVolumCost;
    }

    public BigDecimal getTotalCost()
    {
        return totalCost;
    }

    public void setTotalCost(final BigDecimal totalCost)
    {
        this.totalCost = totalCost;
    }
}
