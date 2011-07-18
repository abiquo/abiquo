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

package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "pricingTemplate")
public class PricingTemplateDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = -6898276066732200634L;

    public PricingTemplateDto()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public PricingTemplateDto(final String name, final BigDecimal hdGb,
        final BigDecimal standingChargePeriod, final BigDecimal limitMaximumDeployedCharged,
        final BigDecimal vlan, final boolean showMinimumCharge, final int chargingPeriod,
        final BigDecimal minimumChargePeriod, final boolean showChangesBefore,
        final int minimumCharge, final BigDecimal publicIp, final BigDecimal vCpu,
        final BigDecimal memoryMb)
    {
        super();
        this.name = name;
        this.hdGb = hdGb;
        this.standingChargePeriod = standingChargePeriod;
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
        this.vlan = vlan;
        this.showMinimumCharge = showMinimumCharge;
        this.chargingPeriod = chargingPeriod;
        this.minimumChargePeriod = minimumChargePeriod;
        this.showChangesBefore = showChangesBefore;
        this.minimumCharge = minimumCharge;
        this.publicIp = publicIp;
        this.vCpu = vCpu;
        this.memoryMb = memoryMb;
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

    private BigDecimal hdGb;

    public BigDecimal getHdGB()
    {
        return hdGb;
    }

    public void setHdGB(final BigDecimal hdGb)
    {
        this.hdGb = hdGb;
    }

    private BigDecimal standingChargePeriod;

    public BigDecimal getStandingChargePeriod()
    {
        return standingChargePeriod;
    }

    public void setStandingChargePeriod(final BigDecimal standingChargePeriod)
    {
        this.standingChargePeriod = standingChargePeriod;
    }

    private BigDecimal limitMaximumDeployedCharged;

    public BigDecimal getLimitMaximumDeployedCharged()
    {
        return limitMaximumDeployedCharged;
    }

    public void setLimitMaximumDeployedCharged(final BigDecimal limitMaximumDeployedCharged)
    {
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
    }

    private BigDecimal vlan;

    public BigDecimal getVlan()
    {
        return vlan;
    }

    public void setVlan(final BigDecimal vlan)
    {
        this.vlan = vlan;
    }

    private boolean showMinimumCharge;

    public boolean getShowMinimumCharge()
    {
        return showMinimumCharge;
    }

    public void setShowMinimumCharge(final boolean showMinimumCharge)
    {
        this.showMinimumCharge = showMinimumCharge;
    }

    private int chargingPeriod;

    public int getChargingPeriod()
    {
        return chargingPeriod;
    }

    public void setChargingPeriod(final int chargingPeriod)
    {
        this.chargingPeriod = chargingPeriod;
    }

    private BigDecimal minimumChargePeriod;

    public BigDecimal getMinimumChargePeriod()
    {
        return minimumChargePeriod;
    }

    public void setMinimumChargePeriod(final BigDecimal minimumChargePeriod)
    {
        this.minimumChargePeriod = minimumChargePeriod;
    }

    private boolean showChangesBefore;

    public boolean getShowChangesBefore()
    {
        return showChangesBefore;
    }

    public void setShowChangesBefore(final boolean showChangesBefore)
    {
        this.showChangesBefore = showChangesBefore;
    }

    private int minimumCharge;

    public int getMinimumCharge()
    {
        return minimumCharge;
    }

    public void setMinimumCharge(final int minimumCharge)
    {
        this.minimumCharge = minimumCharge;
    }

    private BigDecimal publicIp;

    public BigDecimal getPublicIp()
    {
        return publicIp;
    }

    public void setPublicIp(final BigDecimal publicIp)
    {
        this.publicIp = publicIp;
    }

    private BigDecimal vCpu;

    public BigDecimal getVCpu()
    {
        return vCpu;
    }

    public void setVCpu(final BigDecimal vCpu)
    {
        this.vCpu = vCpu;
    }

    private BigDecimal memoryMb;

    public BigDecimal getMemoryMB()
    {
        return memoryMb;
    }

    public void setMemoryMB(final BigDecimal memoryMb)
    {
        this.memoryMb = memoryMb;
    }

}
