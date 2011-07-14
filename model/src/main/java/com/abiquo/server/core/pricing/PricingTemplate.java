package com.abiquo.server.core.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.infrastructure.storage.Tier;
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
        final BigDecimal standingChargePeriod, final Enterprise enterprise,
        final BigDecimal limitMaximumDeployedCharged, final BigDecimal vlan,
        final boolean showMinimumCharge, final int chargingPeriod,
        final BigDecimal minimumChargePeriod, final boolean showChangesBefore,
        final int minimumCharge, final Currency currency, final BigDecimal publicIp,
        final BigDecimal vCpu, final BigDecimal memoryMb)
    {
        super();
        this.name = name;
        this.hdGb = hdGb;
        this.standingChargePeriod = standingChargePeriod;
        this.enterprise = enterprise;
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
        this.vlan = vlan;
        this.showMinimumCharge = showMinimumCharge;
        this.chargingPeriod = chargingPeriod;
        this.minimumChargePeriod = minimumChargePeriod;
        this.showChangesBefore = showChangesBefore;
        this.minimumCharge = minimumCharge;
        this.currency = currency;
        this.publicIp = publicIp;
        this.vCpu = vCpu;
        this.memoryMb = memoryMb;
    }

    public final static String ID_COLUMN = "pricingTemplateId";

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

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

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

    private void setName(final String name)
    {
        this.name = name;
    }

    public final static String HD_GB_PROPERTY = "hdGb";

    private final static String HD_GB_COLUMN = "hdGb";

    @Column(name = HD_GB_COLUMN, nullable = true)
    private BigDecimal hdGb;

    public BigDecimal getHdGB()
    {
        return this.hdGb;
    }

    private void setHdGB(final BigDecimal hdGb)
    {
        this.hdGb = hdGb;
    }

    public final static String STANDING_CHARGE_PERIOD_PROPERTY = "standingChargePeriod";

    private final static String STANDING_CHARGE_PERIOD_COLUMN = "standingChargePeriod";

    @Column(name = STANDING_CHARGE_PERIOD_COLUMN, nullable = true)
    private BigDecimal standingChargePeriod;

    public BigDecimal getStandingChargePeriod()
    {
        return this.standingChargePeriod;
    }

    private void setStandingChargePeriod(final BigDecimal standingChargePeriod)
    {
        this.standingChargePeriod = standingChargePeriod;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = true;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise")
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String LIMIT_MAXIMUM_DEPLOYED_CHARGED_PROPERTY =
        "limitMaximumDeployedCharged";

    private final static String LIMIT_MAXIMUM_DEPLOYED_CHARGED_COLUMN =
        "limitMaximumDeployedCharged";

    @Column(name = LIMIT_MAXIMUM_DEPLOYED_CHARGED_COLUMN, nullable = true)
    private BigDecimal limitMaximumDeployedCharged;

    public BigDecimal getLimitMaximumDeployedCharged()
    {
        return this.limitMaximumDeployedCharged;
    }

    private void setLimitMaximumDeployedCharged(final BigDecimal limitMaximumDeployedCharged)
    {
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
    }

    public final static String VLAN_PROPERTY = "vlan";

    private final static String VLAN_COLUMN = "vlan";

    @Column(name = VLAN_COLUMN, nullable = true)
    private BigDecimal vlan;

    public BigDecimal getVlan()
    {
        return this.vlan;
    }

    private void setVlan(final BigDecimal vlan)
    {
        this.vlan = vlan;
    }

    public final static String SHOW_MINIMUM_CHARGE_PROPERTY = "showMinimumCharge";

    private final static boolean SHOW_MINIMUM_CHARGE_REQUIRED = true;

    private final static String SHOW_MINIMUM_CHARGE_COLUMN = "ShowMinimumCharge";

    @Column(name = SHOW_MINIMUM_CHARGE_COLUMN, nullable = !SHOW_MINIMUM_CHARGE_REQUIRED)
    private boolean showMinimumCharge;

    @Required(value = SHOW_MINIMUM_CHARGE_REQUIRED)
    public boolean getShowMinimumCharge()
    {
        return this.showMinimumCharge;
    }

    private void setShowMinimumCharge(final boolean showMinimumCharge)
    {
        this.showMinimumCharge = showMinimumCharge;
    }

    public final static String CHARGING_PERIOD_PROPERTY = "chargingPeriod";

    private final static boolean CHARGING_PERIOD_REQUIRED = false;

    private final static String CHARGING_PERIOD_COLUMN = "chargingPeriod";

    private final static int CHARGING_PERIOD_MIN = Integer.MIN_VALUE;

    private final static int CHARGING_PERIOD_MAX = Integer.MAX_VALUE;

    @Column(name = CHARGING_PERIOD_COLUMN, nullable = !CHARGING_PERIOD_REQUIRED)
    @Range(min = CHARGING_PERIOD_MIN, max = CHARGING_PERIOD_MAX)
    private int chargingPeriod;

    public int getChargingPeriod()
    {
        return this.chargingPeriod;
    }

    private void setChargingPeriod(final int chargingPeriod)
    {
        this.chargingPeriod = chargingPeriod;
    }

    public final static String MINIMUM_CHARGE_PERIOD_PROPERTY = "minimumChargePeriod";

    private final static String MINIMUM_CHARGE_PERIOD_COLUMN = "minimumChargePeriod";

    @Column(name = MINIMUM_CHARGE_PERIOD_COLUMN, nullable = true)
    private BigDecimal minimumChargePeriod;

    public BigDecimal getMinimumChargePeriod()
    {
        return this.minimumChargePeriod;
    }

    private void setMinimumChargePeriod(final BigDecimal minimumChargePeriod)
    {
        this.minimumChargePeriod = minimumChargePeriod;
    }

    public final static String SHOW_CHANGES_BEFORE_PROPERTY = "showChangesBefore";

    private final static boolean SHOW_CHANGES_BEFORE_REQUIRED = true;

    private final static String SHOW_CHANGES_BEFORE_COLUMN = "ShowChangesBefore";

    @Column(name = SHOW_CHANGES_BEFORE_COLUMN, nullable = !SHOW_CHANGES_BEFORE_REQUIRED)
    private boolean showChangesBefore;

    @Required(value = SHOW_CHANGES_BEFORE_REQUIRED)
    public boolean getShowChangesBefore()
    {
        return this.showChangesBefore;
    }

    private void setShowChangesBefore(final boolean showChangesBefore)
    {
        this.showChangesBefore = showChangesBefore;
    }

    public final static String MINIMUM_CHARGE_PROPERTY = "minimumCharge";

    private final static boolean MINIMUM_CHARGE_REQUIRED = false;

    private final static String MINIMUM_CHARGE_COLUMN = "minimumCharge";

    private final static int MINIMUM_CHARGE_MIN = Integer.MIN_VALUE;

    private final static int MINIMUM_CHARGE_MAX = Integer.MAX_VALUE;

    @Column(name = MINIMUM_CHARGE_COLUMN, nullable = !MINIMUM_CHARGE_REQUIRED)
    @Range(min = MINIMUM_CHARGE_MIN, max = MINIMUM_CHARGE_MAX)
    private int minimumCharge;

    public int getMinimumCharge()
    {
        return this.minimumCharge;
    }

    private void setMinimumCharge(final int minimumCharge)
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

    private final static String PUBLIC_IP_COLUMN = "publicIp";

    @Column(name = PUBLIC_IP_COLUMN, nullable = true)
    private BigDecimal publicIp;

    public BigDecimal getPublicIp()
    {
        return this.publicIp;
    }

    private void setPublicIp(final BigDecimal publicIp)
    {
        this.publicIp = publicIp;
    }

    public final static String V_CPU_PROPERTY = "vCpu";

    private final static String V_CPU_COLUMN = "vCpu";

    @Column(name = V_CPU_COLUMN, nullable = true)
    private BigDecimal vCpu;

    public BigDecimal getVCpu()
    {
        return this.vCpu;
    }

    private void setVCpu(final BigDecimal vCpu)
    {
        this.vCpu = vCpu;
    }

    public final static String MEMORY_MB_PROPERTY = "memoryMb";

    private final static String MEMORY_MB_COLUMN = "memoryMb";

    @Column(name = MEMORY_MB_COLUMN, nullable = true)
    private BigDecimal memoryMb;

    public BigDecimal getMemoryMB()
    {
        return this.memoryMb;
    }

    private void setMemoryMB(final BigDecimal memoryMb)
    {
        this.memoryMb = memoryMb;
    }

    public final static String ASSOCIATION_TABLE = "pricing_costecode";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Privilege.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = CostCode.ID_COLUMN))
    private List<CostCode> costCodes = new ArrayList<CostCode>();

    public List<CostCode> getCostCodes()
    {
        return costCodes;
    }

    public void setCostCodes(final List<CostCode> costCodes)
    {
        this.costCodes = costCodes;
    }

    public final static String ASSOCIATION_TABLE_TIER = "pricing_tier";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Privilege.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE_TIER, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = Tier.ID_COLUMN))
    private List<Tier> tiers = new ArrayList<Tier>();

    public List<Tier> getTiers()
    {
        return tiers;
    }

    public void setTiers(final List<Tier> tiers)
    {
        this.tiers = tiers;
    }

    // ************************* Helper methods ****************************

    public void addCostCode(final CostCode costCode)
    {
        if (costCodes == null)
        {
            costCodes = new ArrayList<CostCode>();
        }
        costCodes.add(costCode);
    }

    public void addTier(final Tier tier)
    {
        if (tiers == null)
        {
            tiers = new ArrayList<Tier>();
        }
        tiers.add(tier);
    }

}
