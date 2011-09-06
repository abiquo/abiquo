/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
