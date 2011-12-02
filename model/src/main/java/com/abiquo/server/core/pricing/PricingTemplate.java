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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.model.enumerator.PricingPeriod;
import com.abiquo.model.validation.BigDec;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = PricingTemplate.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = PricingTemplate.TABLE_NAME)
public class PricingTemplate extends DefaultEntityBase
{
    public static final String TABLE_NAME = "pricingTemplate";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected PricingTemplate()
    {
        // Just for JPA support
    }

    public PricingTemplate(final String name, final BigDecimal hdGb,
        final BigDecimal standingChargePeriod, final BigDecimal vlan,
        final PricingPeriod chargingPeriod, final BigDecimal minimumChargePeriod,
        final boolean showChangesBefore, final PricingPeriod minimumCharge,
        final Currency currency, final BigDecimal publicIp, final BigDecimal vCpu,
        final BigDecimal memoryMB, final boolean defaultTemplate, final String description)
    {

        setName(name);
        setHdGB(hdGb);
        setStandingChargePeriod(standingChargePeriod);
        setVlan(vlan);
        setShowChangesBefore(showChangesBefore);
        setChargingPeriod(chargingPeriod);
        setMinimumCharge(minimumCharge);
        setMinimumChargePeriod(minimumChargePeriod);
        setCurrency(currency);
        setPublicIp(publicIp);
        setVcpu(vCpu);
        setMemoryMB(memoryMB);
        setDefaultTemplate(defaultTemplate);
        setLastUpdate(new Date());
        setDescription(description);
    }

    public final static String ID_COLUMN = "idPricingTemplate";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    private final static int NAME_LENGTH_MIN = 1;

    private final static int NAME_LENGTH_MAX = 20;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = true;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 1000;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public final static String HD_GB_PROPERTY = "hdGB";

    private final static boolean HD_GB_REQUIRED = true;

    private final static String HD_GB_COLUMN = "hdGB";

    @Column(name = HD_GB_COLUMN, nullable = false)
    private BigDecimal hdGB;

    @Required(value = HD_GB_REQUIRED)
    // @BigDec
    public BigDecimal getHdGB()
    {
        return this.hdGB;
    }

    public void setHdGB(final BigDecimal hdGb)
    {
        this.hdGB = hdGb;
    }

    public final static String STANDING_CHARGE_PERIOD_PROPERTY = "standingChargePeriod";

    private final static boolean STANDING_CHARGE_PERIOD_REQUIRED = true;

    private final static String STANDING_CHARGE_PERIOD_COLUMN = "standingChargePeriod";

    @Column(name = STANDING_CHARGE_PERIOD_COLUMN, nullable = false)
    private BigDecimal standingChargePeriod;

    @Required(value = STANDING_CHARGE_PERIOD_REQUIRED)
    // @BigDec
    public BigDecimal getStandingChargePeriod()
    {
        return this.standingChargePeriod;
    }

    public void setStandingChargePeriod(final BigDecimal standingChargePeriod)
    {
        this.standingChargePeriod = standingChargePeriod;
    }

    public final static String VLAN_PROPERTY = "vlan";

    private final static boolean VLAN_REQUIRED = true;

    private final static String VLAN_COLUMN = "vlan";

    @Column(name = VLAN_COLUMN, nullable = false)
    private BigDecimal vlan;

    @Required(value = VLAN_REQUIRED)
    // @BigDec
    public BigDecimal getVlan()
    {
        return this.vlan;
    }

    public void setVlan(final BigDecimal vlan)
    {
        this.vlan = vlan;
    }

    private final static boolean CHARGING_PERIOD_REQUIRED = true;

    private final static String CHARGING_PERIOD_COLUMN = "chargingPeriod";

    @Enumerated(value = javax.persistence.EnumType.ORDINAL)
    @Column(name = CHARGING_PERIOD_COLUMN, nullable = !CHARGING_PERIOD_REQUIRED)
    private PricingPeriod chargingPeriod;

    @Required(value = CHARGING_PERIOD_REQUIRED)
    public PricingPeriod getChargingPeriod()
    {
        return chargingPeriod;
    }

    public void setChargingPeriod(final PricingPeriod chargingPeriod)
    {
        this.chargingPeriod = chargingPeriod;
    }

    public final static String MINIMUM_CHARGE_PERIOD_PROPERTY = "minimumChargePeriod";

    private final static boolean MINIMUM_CHARGE_PERIOD_REQUIRED = true;

    private final static String MINIMUM_CHARGE_PERIOD_COLUMN = "minimumChargePeriod";

    @Column(name = MINIMUM_CHARGE_PERIOD_COLUMN, nullable = false)
    private BigDecimal minimumChargePeriod;

    @Required(value = MINIMUM_CHARGE_PERIOD_REQUIRED)
    @BigDec
    public BigDecimal getMinimumChargePeriod()
    {
        return this.minimumChargePeriod;
    }

    public void setMinimumChargePeriod(final BigDecimal minimumChargePeriod)
    {
        this.minimumChargePeriod = minimumChargePeriod;
    }

    public final static String SHOW_CHANGES_BEFORE_PROPERTY = "showChangesBefore";

    private final static boolean SHOW_CHANGES_BEFORE_REQUIRED = true;

    private final static String SHOW_CHANGES_BEFORE_COLUMN = "ShowChangesBefore";

    @Column(name = SHOW_CHANGES_BEFORE_COLUMN, nullable = !SHOW_CHANGES_BEFORE_REQUIRED)
    private boolean showChangesBefore;

    @Required(value = SHOW_CHANGES_BEFORE_REQUIRED)
    public final static String MINIMUM_CHARGE_PROPERTY = "minimumCharge";

    public boolean isShowChangesBefore()
    {
        return showChangesBefore;
    }

    public void setShowChangesBefore(final boolean showChangesBefore)
    {
        this.showChangesBefore = showChangesBefore;
    }

    private final static boolean MINIMUM_CHARGE_REQUIRED = true;

    private final static String MINIMUM_CHARGE_COLUMN = "minimumCharge";

    @Column(name = MINIMUM_CHARGE_COLUMN, nullable = !MINIMUM_CHARGE_REQUIRED)
    @Enumerated(value = javax.persistence.EnumType.ORDINAL)
    private PricingPeriod minimumCharge;

    @Required(value = MINIMUM_CHARGE_REQUIRED)
    public PricingPeriod getMinimumCharge()
    {
        return this.minimumCharge;
    }

    public void setMinimumCharge(final PricingPeriod minimumCharge)
    {
        this.minimumCharge = minimumCharge;
    }

    public final static String CURRENCY_PROPERTY = "currency";

    private final static boolean CURRENCY_REQUIRED = true;

    private final static String CURRENCY_ID_COLUMN = "idCurrency";

    @JoinColumn(name = CURRENCY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_currency")
    private Currency currency;

    @Required(value = CURRENCY_REQUIRED)
    public Currency getCurrency()
    {
        return this.currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }

    public final static String PUBLIC_IP_PROPERTY = "publicIp";

    private final static boolean PUBLIC_IP_REQUIRED = true;

    private final static String PUBLIC_IP_COLUMN = "publicIp";

    @Column(name = PUBLIC_IP_COLUMN, nullable = false)
    private BigDecimal publicIp;

    @Required(value = PUBLIC_IP_REQUIRED)
    // @BigDec
    public BigDecimal getPublicIp()
    {
        return publicIp;
    }

    public void setPublicIp(final BigDecimal publicIp)
    {
        this.publicIp = publicIp;
    }

    public final static String V_CPU_PROPERTY = "vcpu";

    private final static boolean V_CPU_REQUIRED = true;

    private final static String V_CPU_COLUMN = "vcpu";

    @Column(name = V_CPU_COLUMN, nullable = false)
    private BigDecimal vcpu;

    @Required(value = V_CPU_REQUIRED)
    // @BigDec
    public BigDecimal getVcpu()
    {
        return vcpu;
    }

    public void setVcpu(final BigDecimal vcpu)
    {
        this.vcpu = vcpu;
    }

    public final static String MEMORY_MB_PROPERTY = "memoryMB";

    private final static boolean MEMORY_MB_REQUIRED = true;

    private final static String MEMORY_MB_COLUMN = "memoryMB";

    @Column(name = MEMORY_MB_COLUMN, nullable = false)
    private BigDecimal memoryMB;

    @Required(value = MEMORY_MB_REQUIRED)
    // @BigDec
    public BigDecimal getMemoryMB()
    {
        return memoryMB;
    }

    public void setMemoryMB(final BigDecimal memoryMB)
    {
        this.memoryMB = memoryMB;
    }

    public final static String DEFAULT_TEMPLATE_PROPERTY = "defaultTemplate";

    private final static boolean DEFAULT_TEMPLATE_REQUIRED = true;

    private final static String DEFAULT_TEMPLATE_COLUMN = "defaultTemplate";

    @Column(name = DEFAULT_TEMPLATE_COLUMN, nullable = !DEFAULT_TEMPLATE_REQUIRED)
    private boolean defaultTemplate;

    public boolean isDefaultTemplate()
    {
        return defaultTemplate;
    }

    public void setDefaultTemplate(final boolean defaultTemplate)
    {
        this.defaultTemplate = defaultTemplate;
    }

    private final static String LAST_UPDATE_COLUMN = "last_update";

    @Column(name = LAST_UPDATE_COLUMN, nullable = false)
    private Date lastUpdate;

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(final Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    @OneToMany(targetEntity = PricingCostCode.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "pricingTemplate")
    private List<PricingCostCode> costCodeByPricing = new ArrayList<PricingCostCode>();

    @OneToMany(targetEntity = PricingTier.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "pricingTemplate")
    private List<PricingTier> pricingTier = new ArrayList<PricingTier>();

}
