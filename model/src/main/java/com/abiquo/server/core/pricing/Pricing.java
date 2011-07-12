package com.abiquo.server.core.pricing;

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
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Pricing.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Pricing.TABLE_NAME)
public class Pricing extends DefaultEntityBase
{
    public static final String TABLE_NAME = "pricing";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Pricing()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "idPricing";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String STANDING_CHARGE_PERIOD_PROPERTY = "standingChargePeriod";

    private final static boolean STANDING_CHARGE_PERIOD_REQUIRED = false;

    private final static String STANDING_CHARGE_PERIOD_COLUMN = "standingChargePeriod";

    private final static int STANDING_CHARGE_PERIOD_MIN = Integer.MIN_VALUE;

    private final static int STANDING_CHARGE_PERIOD_MAX = Integer.MAX_VALUE;

    @Column(name = STANDING_CHARGE_PERIOD_COLUMN, nullable = !STANDING_CHARGE_PERIOD_REQUIRED)
    @Range(min = STANDING_CHARGE_PERIOD_MIN, max = STANDING_CHARGE_PERIOD_MAX)
    private int standingChargePeriod;

    public int getStandingChargePeriod()
    {
        return this.standingChargePeriod;
    }

    private void setStandingChargePeriod(final int standingChargePeriod)
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

    private final static boolean LIMIT_MAXIMUM_DEPLOYED_CHARGED_REQUIRED = false;

    private final static String LIMIT_MAXIMUM_DEPLOYED_CHARGED_COLUMN =
        "limitMaximumDeployedCharged";

    private final static int LIMIT_MAXIMUM_DEPLOYED_CHARGED_MIN = Integer.MIN_VALUE;

    private final static int LIMIT_MAXIMUM_DEPLOYED_CHARGED_MAX = Integer.MAX_VALUE;

    @Column(name = LIMIT_MAXIMUM_DEPLOYED_CHARGED_COLUMN, nullable = !LIMIT_MAXIMUM_DEPLOYED_CHARGED_REQUIRED)
    @Range(min = LIMIT_MAXIMUM_DEPLOYED_CHARGED_MIN, max = LIMIT_MAXIMUM_DEPLOYED_CHARGED_MAX)
    private int limitMaximumDeployedCharged;

    public int getLimitMaximumDeployedCharged()
    {
        return this.limitMaximumDeployedCharged;
    }

    private void setLimitMaximumDeployedCharged(final int limitMaximumDeployedCharged)
    {
        this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
    }

    public final static String VLAN_PROPERTY = "vlan";

    private final static boolean VLAN_REQUIRED = false;

    private final static int VLAN_LENGTH_MIN = 1;

    private final static int VLAN_LENGTH_MAX = 40;

    private final static boolean VLAN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VLAN_COLUMN = "vlan";

    @Column(name = VLAN_COLUMN, nullable = !VLAN_REQUIRED, length = VLAN_LENGTH_MAX)
    private String vlan;

    @Required(value = VLAN_REQUIRED)
    @Length(min = VLAN_LENGTH_MIN, max = VLAN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VLAN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVlan()
    {
        return this.vlan;
    }

    private void setVlan(final String vlan)
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

    private final static boolean MINIMUM_CHARGE_PERIOD_REQUIRED = false;

    private final static String MINIMUM_CHARGE_PERIOD_COLUMN = "minimumChargePeriod";

    private final static int MINIMUM_CHARGE_PERIOD_MIN = Integer.MIN_VALUE;

    private final static int MINIMUM_CHARGE_PERIOD_MAX = Integer.MAX_VALUE;

    @Column(name = MINIMUM_CHARGE_PERIOD_COLUMN, nullable = !MINIMUM_CHARGE_PERIOD_REQUIRED)
    @Range(min = MINIMUM_CHARGE_PERIOD_MIN, max = MINIMUM_CHARGE_PERIOD_MAX)
    private int minimumChargePeriod;

    public int getMinimumChargePeriod()
    {
        return this.minimumChargePeriod;
    }

    private void setMinimumChargePeriod(final int minimumChargePeriod)
    {
        this.minimumChargePeriod = minimumChargePeriod;
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

    public final static String SHOW_CHANGES_BEFORE_DEPLOYEMENT_PROPERTY =
        "showChangesBeforeDeployement";

    private final static boolean SHOW_CHANGES_BEFORE_DEPLOYEMENT_REQUIRED = true;

    private final static String SHOW_CHANGES_BEFORE_DEPLOYEMENT_COLUMN =
        "ShowChangesBeforeDeployement";

    @Column(name = SHOW_CHANGES_BEFORE_DEPLOYEMENT_COLUMN, nullable = !SHOW_CHANGES_BEFORE_DEPLOYEMENT_REQUIRED)
    private boolean showChangesBeforeDeployement;

    @Required(value = SHOW_CHANGES_BEFORE_DEPLOYEMENT_REQUIRED)
    public boolean getShowChangesBeforeDeployement()
    {
        return this.showChangesBeforeDeployement;
    }

    private void setShowChangesBeforeDeployement(final boolean showChangesBeforeDeployement)
    {
        this.showChangesBeforeDeployement = showChangesBeforeDeployement;
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

    private final static boolean PUBLIC_IP_REQUIRED = false;

    private final static int PUBLIC_IP_LENGTH_MIN = 0;

    private final static int PUBLIC_IP_LENGTH_MAX = 39;

    private final static boolean PUBLIC_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PUBLIC_IP_COLUMN = "publicIp";

    @Column(name = PUBLIC_IP_COLUMN, nullable = !PUBLIC_IP_REQUIRED, length = PUBLIC_IP_LENGTH_MAX)
    private String publicIp;

    @Required(value = PUBLIC_IP_REQUIRED)
    @Length(min = PUBLIC_IP_LENGTH_MIN, max = PUBLIC_IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PUBLIC_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPublicIp()
    {
        return this.publicIp;
    }

    private void setPublicIp(final String publicIp)
    {
        this.publicIp = publicIp;
    }

    public final static String V_CPU_PROPERTY = "vCpu";

    private final static boolean V_CPU_REQUIRED = false;

    private final static String V_CPU_COLUMN = "vCpu";

    private final static int V_CPU_MIN = Integer.MIN_VALUE;

    private final static int V_CPU_MAX = Integer.MAX_VALUE;

    @Column(name = V_CPU_COLUMN, nullable = !V_CPU_REQUIRED)
    @Range(min = V_CPU_MIN, max = V_CPU_MAX)
    private int vCpu;

    public int getVCpu()
    {
        return this.vCpu;
    }

    private void setVCpu(final int vCpu)
    {
        this.vCpu = vCpu;
    }

    public final static String MEMORY_MB_PROPERTY = "memoryMb";

    private final static boolean MEMORY_MB_REQUIRED = false;

    private final static String MEMORY_MB_COLUMN = "memoryMb";

    private final static int MEMORY_MB_MIN = Integer.MIN_VALUE;

    private final static int MEMORY_MB_MAX = Integer.MAX_VALUE;

    @Column(name = MEMORY_MB_COLUMN, nullable = !MEMORY_MB_REQUIRED)
    @Range(min = MEMORY_MB_MIN, max = MEMORY_MB_MAX)
    private int memoryMb;

    public int getMemoryMB()
    {
        return this.memoryMb;
    }

    private void setMemoryMB(final int memoryMb)
    {
        this.memoryMb = memoryMb;
    }

    public final static String ASSOCIATION_TABLE = "pricing_costecode";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Privilege.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = CosteCode.ID_COLUMN))
    private List<CosteCode> costeCodes = new ArrayList<CosteCode>();

    public List<CosteCode> getCosteCodes()
    {
        return costeCodes;
    }

    public void setCosteCodes(final List<CosteCode> costeCodes)
    {
        this.costeCodes = costeCodes;
    }

    // ************************* Helper methods ****************************

    public void addCosteCode(final CosteCode costeCode)
    {
        if (costeCodes == null)
        {
            costeCodes = new ArrayList<CosteCode>();
        }
        costeCodes.add(costeCode);
    }

}
