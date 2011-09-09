package com.abiquo.server.core.enterprise;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.storage.StoragePool;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Event.TABLE_NAME)
@NamedQueries( {@NamedQuery(name = Event.EVENT_BY_FILTER, query = Event.BY_FILTER)})
public class Event extends DefaultEntityBase
{
    public static final String TABLE_NAME = "metering";

    public static final String EVENT_BY_FILTER = "EVENT_BY_FILTER";

    public static final String BY_FILTER =
        "SELECT event FROM Event event WHERE event.timestamp BETWEEN :timestampInit AND :timestampEnd"
            + " AND event.enterprise.name LIKE :enterprise ORDER BY event.timestamp DESC";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Event()
    {
        // Just for JPA support
    }

    public Event(final String actionPerformed, final String component, final String performedBy,
        final String severity, final int storageSystem, final String stacktrace, final int subnet,
        final int timestamp)
    {
        super();
        this.actionPerformed = actionPerformed;
        this.component = component;
        this.performedBy = performedBy;
        this.severity = severity;
        this.storageSystem = storageSystem;
        this.stracktrace = stacktrace;
        this.subnet = subnet;
        this.timestamp = timestamp;
    }

    private final static String ID_COLUMN = "idMeter";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
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

    private void setComponent(final String component)
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

    private void setActionPerformed(final String actionPerformed)
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

    private void setPerformedBy(final String performedBy)
    {
        this.performedBy = performedBy;
    }

    public final static String STORAGE_POOL_PROPERTY = "storagePool";

    private final static boolean STORAGE_POOL_REQUIRED = false;

    private final static String STORAGE_POOL_ID_COLUMN = "idStorage";

    @JoinColumn(name = STORAGE_POOL_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    private StoragePool storagePool;

    @Required(value = STORAGE_POOL_REQUIRED)
    public StoragePool getStoragePool()
    {
        return this.storagePool;
    }

    public void setStoragePool(final StoragePool storagePool)
    {
        this.storagePool = storagePool;
    }

    public final static String STRACKTRACE_PROPERTY = "stracktrace";

    private final static boolean STRACKTRACE_REQUIRED = false;

    private final static int STRACKTRACE_LENGTH_MIN = 0;

    private final static int STRACKTRACE_LENGTH_MAX = 255;

    private final static boolean STRACKTRACE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String STRACKTRACE_COLUMN = "stracktrace";

    @Column(name = STRACKTRACE_COLUMN, nullable = !STRACKTRACE_REQUIRED, length = STRACKTRACE_LENGTH_MAX)
    private String stracktrace;

    @Required(value = STRACKTRACE_REQUIRED)
    @Length(min = STRACKTRACE_LENGTH_MIN, max = STRACKTRACE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = STRACKTRACE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getStracktrace()
    {
        return this.stracktrace;
    }

    private void setStracktrace(final String stracktrace)
    {
        this.stracktrace = stracktrace;
    }

    public final static String TIMESTAMP_PROPERTY = "timestamp";

    private final static boolean TIMESTAMP_REQUIRED = true;

    private final static String TIMESTAMP_COLUMN = "timestamp";

    private final static int TIMESTAMP_MIN = Integer.MIN_VALUE;

    private final static int TIMESTAMP_MAX = Integer.MAX_VALUE;

    @Column(name = TIMESTAMP_COLUMN, nullable = !TIMESTAMP_REQUIRED)
    @Range(min = TIMESTAMP_MIN, max = TIMESTAMP_MAX)
    private int timestamp;

    public int getTimestamp()
    {
        return this.timestamp;
    }

    private void setTimestamp(final int timestamp)
    {
        this.timestamp = timestamp;
    }

    public final static String VIRTUAL_APP_PROPERTY = "virtualApp";

    private final static boolean VIRTUAL_APP_REQUIRED = false;

    private final static String VIRTUAL_APP_COLUMN = "idVirtualApp";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = VIRTUAL_APP_COLUMN)
    private VirtualAppliance virtualApp;

    @Required(value = VIRTUAL_APP_REQUIRED)
    public VirtualAppliance getVirtualApp()
    {
        return this.virtualApp;
    }

    private void setVirtualApp(final VirtualAppliance virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = false;

    private final static String DATACENTER_COLUMN = "idDatacenter";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = DATACENTER_COLUMN)
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    private void setDatacenter(final Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String VIRTUAL_DATACENTER_PROPERTY = "virtualDatacenter";

    private final static boolean VIRTUAL_DATACENTER_REQUIRED = false;

    private final static String VIRTUAL_DATACENTER_COLUMN = "idVirtualDatacenter";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = VIRTUAL_DATACENTER_COLUMN)
    private VirtualDatacenter virtualDatacenter;

    @Required(value = VIRTUAL_DATACENTER_REQUIRED)
    public VirtualDatacenter getVirtualDatacenter()
    {
        return this.virtualDatacenter;
    }

    private void setVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

    private final static String ENTERPRISE_COLUMN = "idEnterprise";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = ENTERPRISE_COLUMN)
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    private void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String STORAGE_SYSTEM_PROPERTY = "storageSystem";

    private final static boolean STORAGE_SYSTEM_REQUIRED = false;

    private final static String STORAGE_SYSTEM_COLUMN = "idStorageSystem";

    private final static int STORAGE_SYSTEM_MIN = Integer.MIN_VALUE;

    private final static int STORAGE_SYSTEM_MAX = Integer.MAX_VALUE;

    @Column(name = STORAGE_SYSTEM_COLUMN, nullable = !STORAGE_SYSTEM_REQUIRED)
    @Range(min = STORAGE_SYSTEM_MIN, max = STORAGE_SYSTEM_MAX)
    private int storageSystem;

    public int getStorageSystem()
    {
        return this.storageSystem;
    }

    private void setStorageSystem(final int storageSystem)
    {
        this.storageSystem = storageSystem;
    }

    public final static String NETWORK_PROPERTY = "network";

    private final static boolean NETWORK_REQUIRED = false;

    private final static String NETWORK_COLUMN = "idNetwork";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = NETWORK_COLUMN)
    private Network network;

    @Required(value = NETWORK_REQUIRED)
    public Network getNetwork()
    {
        return this.network;
    }

    private void setNetwork(final Network network)
    {
        this.network = network;
    }

    public final static String PHYSICAL_MACHINE_PROPERTY = "physicalMachine";

    private final static boolean PHYSICAL_MACHINE_REQUIRED = false;

    private final static String PHYSICAL_MACHINE_COLUMN = "idPhysicalMachine";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = PHYSICAL_MACHINE_COLUMN)
    private Machine physicalMachine;

    @Required(value = PHYSICAL_MACHINE_REQUIRED)
    public Machine getPhysicalMachine()
    {
        return this.physicalMachine;
    }

    private void setPhysicalMachine(final Machine physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public final static String RACK_PROPERTY = "rack";

    private final static boolean RACK_REQUIRED = false;

    private final static String RACK_COLUMN = "idRack";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = RACK_COLUMN)
    private Rack rack;

    @Required(value = RACK_REQUIRED)
    public Rack getRack()
    {
        return this.rack;
    }

    private void setRack(final Rack rack)
    {
        this.rack = rack;
    }

    public final static String VIRTUAL_MACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUAL_MACHINE_REQUIRED = false;

    private final static String VIRTUAL_MACHINE_COLUMN = "idVirtualMachine";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = VIRTUAL_MACHINE_COLUMN, nullable = !VIRTUAL_MACHINE_REQUIRED)
    private VirtualMachine virtualMachine;

    @Required(value = VIRTUAL_MACHINE_REQUIRED)
    public VirtualMachine getVirtualMachine()
    {
        return this.virtualMachine;
    }

    private void setVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public final static String VOLUME_PROPERTY = "volume";

    private final static boolean VOLUME_REQUIRED = false;

    private final static String VOLUME_COLUMN = "idVolume";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = VOLUME_COLUMN)
    private VolumeManagement volume;

    @Required(value = VOLUME_REQUIRED)
    public VolumeManagement getVolume()
    {
        return this.volume;
    }

    private void setVolume(final VolumeManagement volume)
    {
        this.volume = volume;
    }

    public final static String SUBNET_PROPERTY = "subnet";

    private final static boolean SUBNET_REQUIRED = false;

    private final static String SUBNET_COLUMN = "idSubnet";

    private final static int SUBNET_MIN = Integer.MIN_VALUE;

    private final static int SUBNET_MAX = Integer.MAX_VALUE;

    @Column(name = SUBNET_COLUMN, nullable = !SUBNET_REQUIRED)
    @Range(min = SUBNET_MIN, max = SUBNET_MAX)
    private int subnet;

    public int getSubnet()
    {
        return this.subnet;
    }

    private void setSubnet(final int subnet)
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

    private void setSeverity(final String severity)
    {
        this.severity = severity;
    }

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = false;

    private final static String USER_COLUMN = "idUser";

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = USER_COLUMN)
    private User user;

    @Required(value = USER_REQUIRED)
    public User getUser()
    {
        return this.user;
    }

    private void setUser(final User user)
    {
        this.user = user;
    }

}
