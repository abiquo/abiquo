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

/**
 * Abiquo premium edition
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

package com.abiquo.abiserver.pojo.pricing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import com.abiquo.abiserver.pojo.user.Enterprise;

public class PricingTemplate implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // class attributes
    private Integer id;

    private String name;

    private Enterprise enterprise;

    private Currency currency;

    private boolean showMinimumCharge;

    private boolean showChangesBefore;

    private BigDecimal chargingPeriod;

    private BigDecimal minimumChargePeriod;

    private BigDecimal minimumCharge;

    private BigDecimal standingChargePeriod;

    private BigDecimal limitMaximumDeployedCharged;

    private BigDecimal vlan;

    private BigDecimal publicIp;

    private BigDecimal vCpu;

    private BigDecimal memoryMb;

    private BigDecimal hdGB;

    Set<CostCode> costCodes;

    // Set<Tier> tiers;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public BigDecimal getStandingChargePeriod()
    {
        return standingChargePeriod;
    }

    public void setStandingChargePeriod(final BigDecimal standingChargePeriod)
    {
        this.standingChargePeriod = standingChargePeriod;
    }

    public BigDecimal getLimitMaximumDeployedCharged()
    {
        return limitMaximumDeployedCharged;
    }

    public void setLimitMaximumDeployedCharged(final BigDecimal limitMaximumDeployedCharged)
    {
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
    }

    public BigDecimal getVlan()
    {
        return vlan;
    }

    public void setVlan(final BigDecimal vlan)
    {
        this.vlan = vlan;
    }

    public boolean isShowMinimumCharge()
    {
        return showMinimumCharge;
    }

    public void setShowMinimumCharge(final boolean showMinimumCharge)
    {
        this.showMinimumCharge = showMinimumCharge;
    }

    public BigDecimal getChargingPeriod()
    {
        return chargingPeriod;
    }

    public void setChargingPeriod(final BigDecimal chargingPeriod)
    {
        this.chargingPeriod = chargingPeriod;
    }

    public BigDecimal getMinimumChargePeriod()
    {
        return minimumChargePeriod;
    }

    public void setMinimumChargePeriod(final BigDecimal minimumChargePeriod)
    {
        this.minimumChargePeriod = minimumChargePeriod;
    }

    public BigDecimal getMinimumCharge()
    {
        return minimumCharge;
    }

    public void setMinimumCharge(final BigDecimal minimumCharge)
    {
        this.minimumCharge = minimumCharge;
    }

    public boolean isShowChangesBefore()
    {
        return showChangesBefore;
    }

    public void setShowChangesBefore(final boolean showChangesBefore)
    {
        this.showChangesBefore = showChangesBefore;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }

    public BigDecimal getPublicIp()
    {
        return publicIp;
    }

    public void setPublicIp(final BigDecimal publicIp)
    {
        this.publicIp = publicIp;
    }

    public BigDecimal getvCpu()
    {
        return vCpu;
    }

    public void setvCpu(final BigDecimal vCpu)
    {
        this.vCpu = vCpu;
    }

    public BigDecimal getMemoryMb()
    {
        return memoryMb;
    }

    public void setMemoryMb(final BigDecimal memoryMb)
    {
        this.memoryMb = memoryMb;
    }

    public BigDecimal getHdGB()
    {
        return hdGB;
    }

    public void setHdGB(final BigDecimal hdGB)
    {
        this.hdGB = hdGB;
    }

    public Set<CostCode> getCostCodes()
    {
        return costCodes;
    }

    public void setCostCodes(final Set<CostCode> costCodes)
    {
        this.costCodes = costCodes;
    }

    // public Set<Tier> getTiers()
    // {
    // return tiers;
    // }
    //
    // public void setTiers(final Set<Tier> tiers)
    // {
    // this.tiers = tiers;
    // }

}
