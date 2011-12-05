package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Event.TABLE_NAME)
public class Event extends DefaultEntityBase
{
    public static final String TABLE_NAME = "metering";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Event()
    {
        // Just for JPA support
    }

    public Event(final String actionPerformed, final String component, final String dc,
        final String enterprise, final String network, final String performedBy,
        final String machine, final String rack, final String severity, final String sp,
        final String storageSystem, final String stacktrace, final String subnet,
        final Date timestamp, final String user, final String vapp, final String vdc,
        final String vm, final String volume)
    {
        super();

        setActionPerformed(actionPerformed);
        setComponent(component);
        setPerformedBy(performedBy);
        setSeverity(severity);
        setStorageSystem(storageSystem);
        setStacktrace(stacktrace);
        setSubnet(subnet);
        setTimestamp(timestamp);
        setDatacenter(dc);
        setEnterprise(enterprise);
        setNetwork(network);
        setPhysicalMachine(machine);
        setRack(rack);
        setStoragePool(sp);
        setUser(user);
        setVirtualApp(vapp);
        setVirtualDatacenter(vdc);
        setVirtualMachine(vm);
        setVolume(volume);
    }

    private final static String ID_COLUMN = "idMeter";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false, columnDefinition = "BIGINT")
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String COMPONENT_PROPERTY = "component";

    private final static boolean COMPONENT_REQUIRED = false;

    private final static int COMPONENT_LENGTH_MIN = 0;

    private final static int COMPONENT_LENGTH_MAX = 255;

    private final static boolean COMPONENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String COMPONENT_COLUMN = "component";

    @Column(name = COMPONENT_COLUMN, nullable = !COMPONENT_REQUIRED, length = COMPONENT_LENGTH_MAX)
    private String component;

    @Required(value = COMPONENT_REQUIRED)
    @Length(min = COMPONENT_LENGTH_MIN, max = COMPONENT_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = COMPONENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getComponent()
    {
        return this.component;
    }

    public void setComponent(final String component)
    {
        this.component = component;
    }

    public final static String ACTION_PERFORMED_PROPERTY = "actionPerformed";

    private final static boolean ACTION_PERFORMED_REQUIRED = true;

    /* package */final static int ACTION_PERFORMED_LENGTH_MIN = 0;

    /* package */final static int ACTION_PERFORMED_LENGTH_MAX = 255;

    private final static boolean ACTION_PERFORMED_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ACTION_PERFORMED_COLUMN = "actionperformed";

    @Column(name = ACTION_PERFORMED_COLUMN, nullable = !ACTION_PERFORMED_REQUIRED, length = ACTION_PERFORMED_LENGTH_MAX)
    private String actionPerformed;

    @Required(value = ACTION_PERFORMED_REQUIRED)
    @Length(min = ACTION_PERFORMED_LENGTH_MIN, max = ACTION_PERFORMED_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ACTION_PERFORMED_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getActionPerformed()
    {
        return this.actionPerformed;
    }

    public void setActionPerformed(final String actionPerformed)
    {
        this.actionPerformed = actionPerformed;
    }

    public final static String PERFORMED_BY_PROPERTY = "performedBy";

    private final static boolean PERFORMED_BY_REQUIRED = true;

    private final static int PERFORMED_BY_LENGTH_MIN = 0;

    private final static int PERFORMED_BY_LENGTH_MAX = 255;

    private final static boolean PERFORMED_BY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PERFORMED_BY_COLUMN = "performedby";

    @Column(name = PERFORMED_BY_COLUMN, nullable = !PERFORMED_BY_REQUIRED, length = PERFORMED_BY_LENGTH_MAX)
    private String performedBy;

    @Required(value = PERFORMED_BY_REQUIRED)
    @Length(min = PERFORMED_BY_LENGTH_MIN, max = PERFORMED_BY_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PERFORMED_BY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPerformedBy()
    {
        return this.performedBy;
    }

    public void setPerformedBy(final String performedBy)
    {
        this.performedBy = performedBy;
    }

    public final static String STORAGE_POOL_PROPERTY = "storagePool";

    private final static boolean STORAGE_POOL_REQUIRED = false;

    private final static String STORAGE_POOL_COLUMN = "storagePool";

    private final static int STORAGE_POOL_LENGTH_MIN = 0;

    private final static int STORAGE_POOL_LENGTH_MAX = 255;

    private final static boolean STORAGE_POOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = STORAGE_POOL_COLUMN, nullable = !STORAGE_POOL_REQUIRED, length = STORAGE_POOL_LENGTH_MAX)
    private String storagePool;

    @Required(value = STORAGE_POOL_REQUIRED)
    @Length(min = STORAGE_POOL_LENGTH_MIN, max = STORAGE_POOL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = STORAGE_POOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getStoragePool()
    {
        return this.storagePool;
    }

    public void setStoragePool(final String storagePool)
    {
        this.storagePool = storagePool;
    }

    public final static String STACKTRACE_PROPERTY = "stacktrace";

    private final static boolean STACKTRACE_REQUIRED = false;

    private final static int STACKTRACE_LENGTH_MIN = 0;

    private final static int STACKTRACE_LENGTH_MAX = 255;

    private final static boolean STACKTRACE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String STACKTRACE_COLUMN = "stacktrace";

    @Column(name = STACKTRACE_COLUMN, nullable = !STACKTRACE_REQUIRED, length = STACKTRACE_LENGTH_MAX, columnDefinition = "TEXT")
    private String stacktrace;

    @Required(value = STACKTRACE_REQUIRED)
    @Length(min = STACKTRACE_LENGTH_MIN, max = STACKTRACE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = STACKTRACE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getStacktrace()
    {
        return this.stacktrace;
    }

    public void setStacktrace(final String stacktrace)
    {
        this.stacktrace = stacktrace;
    }

    public final static String TIMESTAMP_PROPERTY = "timestamp";

    private final static boolean TIMESTAMP_REQUIRED = true;

    private final static String TIMESTAMP_COLUMN = "timestamp";

    @Column(name = TIMESTAMP_COLUMN, nullable = !TIMESTAMP_REQUIRED)
    // @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Required(value = TIMESTAMP_REQUIRED)
    public Date getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public final static String VIRTUAL_APP_PROPERTY = "virtualApp";

    private final static boolean VIRTUAL_APP_REQUIRED = false;

    private final static String VIRTUAL_APP_COLUMN = "virtualApp";

    private final static int VIRTUAL_APP_LENGTH_MIN = 0;

    private final static int VIRTUAL_APP_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_APP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = VIRTUAL_APP_COLUMN, nullable = !VIRTUAL_APP_REQUIRED, length = VIRTUAL_APP_LENGTH_MAX)
    private String virtualApp;

    @Required(value = VIRTUAL_APP_REQUIRED)
    @Length(min = VIRTUAL_APP_LENGTH_MIN, max = VIRTUAL_APP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_APP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVirtualApp()
    {
        return this.virtualApp;
    }

    public void setVirtualApp(final String virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = false;

    private final static String DATACENTER_COLUMN = "datacenter";

    private final static int DATACENTER_LENGTH_MIN = 0;

    private final static int DATACENTER_LENGTH_MAX = 255;

    private final static boolean DATACENTER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = DATACENTER_COLUMN, nullable = !DATACENTER_REQUIRED, length = DATACENTER_LENGTH_MAX)
    private String datacenter;

    @Required(value = DATACENTER_REQUIRED)
    @Length(min = DATACENTER_LENGTH_MIN, max = DATACENTER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DATACENTER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(final String datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String VIRTUAL_DATACENTER_PROPERTY = "virtualDatacenter";

    private final static boolean VIRTUAL_DATACENTER_REQUIRED = false;

    private final static String VIRTUAL_DATACENTER_COLUMN = "virtualDataCenter";

    private final static int VIRTUAL_DATACENTER_LENGTH_MIN = 0;

    private final static int VIRTUAL_DATACENTER_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_DATACENTER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = VIRTUAL_DATACENTER_COLUMN, nullable = !VIRTUAL_DATACENTER_REQUIRED, length = VIRTUAL_DATACENTER_LENGTH_MAX)
    private String virtualDatacenter;

    @Required(value = VIRTUAL_DATACENTER_REQUIRED)
    @Length(min = VIRTUAL_DATACENTER_LENGTH_MIN, max = VIRTUAL_DATACENTER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_DATACENTER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVirtualDatacenter()
    {
        return this.virtualDatacenter;
    }

    public void setVirtualDatacenter(final String virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

    private final static String ENTERPRISE_COLUMN = "enterprise";

    private final static int ENTERPRISE_LENGTH_MIN = 0;

    private final static int ENTERPRISE_LENGTH_MAX = 255;

    private final static boolean ENTERPRISE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = ENTERPRISE_COLUMN, nullable = !ENTERPRISE_REQUIRED, length = ENTERPRISE_LENGTH_MAX)
    private String enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    @Length(min = ENTERPRISE_LENGTH_MIN, max = ENTERPRISE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ENTERPRISE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(final String enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String STORAGE_SYSTEM_PROPERTY = "storageSystem";

    private final static boolean STORAGE_SYSTEM_REQUIRED = false;

    private final static String STORAGE_SYSTEM_COLUMN = "storageSystem";

    private final static int STORAGE_SYSTEM_LENGTH_MIN = 0;

    private final static int STORAGE_SYSTEM_LENGTH_MAX = 255;

    private final static boolean STORAGE_SYSTEM_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = STORAGE_SYSTEM_COLUMN, nullable = !STORAGE_SYSTEM_REQUIRED, length = STORAGE_SYSTEM_LENGTH_MAX)
    private String storageSystem;

    @Required(value = STORAGE_SYSTEM_REQUIRED)
    @Length(min = STORAGE_SYSTEM_LENGTH_MIN, max = STORAGE_SYSTEM_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = STORAGE_SYSTEM_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getStorageSystem()
    {
        return this.storageSystem;
    }

    public void setStorageSystem(final String storageSystem)
    {
        this.storageSystem = storageSystem;
    }

    public final static String NETWORK_PROPERTY = "network";

    private final static boolean NETWORK_REQUIRED = false;

    private final static String NETWORK_COLUMN = "network";

    private final static int NETWORK_LENGTH_MIN = 0;

    private final static int NETWORK_LENGTH_MAX = 255;

    private final static boolean NETWORK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = NETWORK_COLUMN, nullable = !NETWORK_REQUIRED, length = NETWORK_LENGTH_MAX)
    private String network;

    @Required(value = NETWORK_REQUIRED)
    @Length(min = NETWORK_LENGTH_MIN, max = NETWORK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NETWORK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNetwork()
    {
        return this.network;
    }

    public void setNetwork(final String network)
    {
        this.network = network;
    }

    public final static String PHYSICAL_MACHINE_PROPERTY = "physicalMachine";

    private final static boolean PHYSICAL_MACHINE_REQUIRED = false;

    private final static String PHYSICAL_MACHINE_COLUMN = "physicalmachine";

    private final static int PHYSICAL_MACHINE_LENGTH_MIN = 0;

    private final static int PHYSICAL_MACHINE_LENGTH_MAX = 255;

    private final static boolean PHYSICAL_MACHINE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = PHYSICAL_MACHINE_COLUMN, nullable = !PHYSICAL_MACHINE_REQUIRED, length = PHYSICAL_MACHINE_LENGTH_MAX)
    private String physicalMachine;

    @Required(value = PHYSICAL_MACHINE_REQUIRED)
    @Length(min = PHYSICAL_MACHINE_LENGTH_MIN, max = PHYSICAL_MACHINE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PHYSICAL_MACHINE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPhysicalMachine()
    {
        return this.physicalMachine;
    }

    public void setPhysicalMachine(final String physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public final static String RACK_PROPERTY = "rack";

    private final static boolean RACK_REQUIRED = false;

    private final static String RACK_COLUMN = "rack";

    private final static int RACK_LENGTH_MIN = 0;

    private final static int RACK_LENGTH_MAX = 255;

    private final static boolean RACK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = RACK_COLUMN, nullable = !RACK_REQUIRED, length = RACK_LENGTH_MAX)
    private String rack;

    @Required(value = RACK_REQUIRED)
    @Length(min = RACK_LENGTH_MIN, max = RACK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = RACK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getRack()
    {
        return this.rack;
    }

    public void setRack(final String rack)
    {
        this.rack = rack;
    }

    public final static String VIRTUAL_MACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUAL_MACHINE_REQUIRED = false;

    private final static String VIRTUAL_MACHINE_COLUMN = "virtualmachine";

    private final static int VIRTUAL_MACHINE_LENGTH_MIN = 0;

    private final static int VIRTUAL_MACHINE_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_MACHINE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = VIRTUAL_MACHINE_COLUMN, nullable = !VIRTUAL_MACHINE_REQUIRED, length = VIRTUAL_MACHINE_LENGTH_MAX)
    private String virtualMachine;

    @Required(value = VIRTUAL_MACHINE_REQUIRED)
    @Length(min = VIRTUAL_MACHINE_LENGTH_MIN, max = VIRTUAL_MACHINE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_MACHINE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVirtualMachine()
    {
        return this.virtualMachine;
    }

    public void setVirtualMachine(final String virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public final static String VOLUME_PROPERTY = "volume";

    private final static boolean VOLUME_REQUIRED = false;

    private final static String VOLUME_COLUMN = "volume";

    private final static int VOLUME_LENGTH_MIN = 0;

    private final static int VOLUME_LENGTH_MAX = 255;

    private final static boolean VOLUME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = VOLUME_COLUMN, nullable = !VOLUME_REQUIRED, length = VOLUME_LENGTH_MAX)
    private String volume;

    @Required(value = VOLUME_REQUIRED)
    @Length(min = VOLUME_LENGTH_MIN, max = VOLUME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VOLUME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVolume()
    {
        return this.volume;
    }

    public void setVolume(final String volume)
    {
        this.volume = volume;
    }

    public final static String SUBNET_PROPERTY = "subnet";

    private final static boolean SUBNET_REQUIRED = false;

    private final static String SUBNET_COLUMN = "subnet";

    private final static int SUBNET_LENGTH_MIN = 0;

    private final static int SUBNET_LENGTH_MAX = 255;

    private final static boolean SUBNET_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = SUBNET_COLUMN, nullable = !SUBNET_REQUIRED, length = SUBNET_LENGTH_MAX)
    private String subnet;

    @Required(value = SUBNET_REQUIRED)
    @Length(min = SUBNET_LENGTH_MIN, max = SUBNET_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SUBNET_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSubnet()
    {
        return this.subnet;
    }

    public void setSubnet(final String subnet)
    {
        this.subnet = subnet;
    }

    public final static String SEVERITY_PROPERTY = "severity";

    private final static boolean SEVERITY_REQUIRED = true;

    private final static int SEVERITY_LENGTH_MIN = 0;

    private final static int SEVERITY_LENGTH_MAX = 255;

    private final static boolean SEVERITY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SEVERITY_COLUMN = "severity";

    @Column(name = SEVERITY_COLUMN, nullable = !SEVERITY_REQUIRED, length = SEVERITY_LENGTH_MAX)
    private String severity;

    @Required(value = SEVERITY_REQUIRED)
    @Length(min = SEVERITY_LENGTH_MIN, max = SEVERITY_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SEVERITY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSeverity()
    {
        return this.severity;
    }

    public void setSeverity(final String severity)
    {
        this.severity = severity;
    }

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = false;

    private final static String USER_COLUMN = "user";

    private final static int USER_LENGTH_MIN = 0;

    private final static int USER_LENGTH_MAX = 255;

    private final static boolean USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Column(name = USER_COLUMN, nullable = !USER_REQUIRED, length = USER_LENGTH_MAX)
    private String user;

    @Required(value = USER_REQUIRED)
    @Length(min = USER_LENGTH_MIN, max = USER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUser()
    {
        return this.user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

}
