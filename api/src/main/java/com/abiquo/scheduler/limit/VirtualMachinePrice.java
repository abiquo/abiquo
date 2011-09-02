package com.abiquo.scheduler.limit;

import java.math.BigDecimal;

/**
 * Holds the virtual machine price
 * 
 * @author aprete
 */
public class VirtualMachinePrice
{
    public enum VirtualMachineCost
    {
        COST_CODE, COMPUTE, NETWORK, STORAGE, ADDITIONAL_VOLUME, TOTAL;
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
