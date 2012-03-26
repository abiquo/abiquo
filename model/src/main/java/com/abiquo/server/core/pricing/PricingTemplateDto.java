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
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "pricingTemplate")
public class PricingTemplateDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -6898276066732200634L;

    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.pricingtemplate+xml";

    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    public PricingTemplateDto()
    {
    }

    public PricingTemplateDto(final String name, final BigDecimal hdGb,
        final BigDecimal standingChargePeriod, final BigDecimal vlan, final Integer chargingPeriod,
        final BigDecimal minimumChargePeriod, final boolean showChangesBefore,
        final Integer minimumCharge, final BigDecimal publicIp, final BigDecimal vCpu,
        final BigDecimal memoryGB, final boolean defaultTemplate, final String description)
    {
        this.name = name;
        this.hdGB = hdGb;
        this.standingChargePeriod = standingChargePeriod;
        this.vlan = vlan;
        this.chargingPeriod = chargingPeriod;
        this.minimumChargePeriod = minimumChargePeriod;
        this.showChangesBefore = showChangesBefore;
        this.minimumCharge = minimumCharge;
        this.publicIp = publicIp;
        this.vcpu = vCpu;
        this.memoryGB = memoryGB;
        this.defaultTemplate = defaultTemplate;
        this.description = description;
        this.lastUpdate = new Date();

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

    @NotNull
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    private BigDecimal hdGB;

    public BigDecimal getHdGB()
    {
        return hdGB;
    }

    public void setHdGB(final BigDecimal hdGB)
    {
        this.hdGB = hdGB;
    }

    private BigDecimal vcpu;

    public BigDecimal getVcpu()
    {
        return vcpu;
    }

    public void setVcpu(final BigDecimal vcpu)
    {
        this.vcpu = vcpu;
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

    public boolean isShowMinimumCharge()
    {
        return showMinimumCharge;
    }

    public void setShowMinimumCharge(final boolean showMinimumCharge)
    {
        this.showMinimumCharge = showMinimumCharge;
    }

    private Integer chargingPeriod;

    public Integer getChargingPeriod()
    {
        return chargingPeriod;
    }

    public void setChargingPeriod(final Integer chargingPeriod)
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

    public boolean isShowChangesBefore()
    {
        return showChangesBefore;
    }

    public void setShowChangesBefore(final boolean showChangesBefore)
    {
        this.showChangesBefore = showChangesBefore;
    }

    private Integer minimumCharge;

    public Integer getMinimumCharge()
    {
        return minimumCharge;
    }

    public void setMinimumCharge(final Integer minimumCharge)
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

    private BigDecimal memoryGB;

    public BigDecimal getMemoryGB()
    {
        return memoryGB;
    }

    public void setMemoryGB(final BigDecimal memoryGB)
    {
        this.memoryGB = memoryGB;
    }

    private Date lastUpdate;

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(final Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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

    private boolean defaultTemplate;

    public boolean isDefaultTemplate()
    {
        return defaultTemplate;
    }

    public void setDefaultTemplate(final boolean defaultTemplate)
    {
        this.defaultTemplate = defaultTemplate;
    }

    @Override
    public String getMediaType()
    {
        return PricingTemplateDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
