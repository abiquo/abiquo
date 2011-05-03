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

package com.abiquo.server.core.infrastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Machine.TABLE_NAME, uniqueConstraints = {})
@org.hibernate.annotations.Table(appliesTo = Machine.TABLE_NAME, indexes = {})
public class Machine extends DefaultEntityBase
{

    // ****************************** JPA support
    // *******************************
    public static final String TABLE_NAME = "physicalmachine";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER*
    // call from business code
    protected Machine()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "idPhysicalMachine";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    // ******************************* Properties
    // *******************************
    public final static String NAME_PROPERTY = "name";

    final static boolean NAME_REQUIRED = true;

    final static int NAME_LENGTH_MIN = 1;

    final static int NAME_LENGTH_MAX = 30;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "Name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    final static int DESCRIPTION_LENGTH_MIN = 0;

    final static int DESCRIPTION_LENGTH_MAX = 100;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "Description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public final static String VIRTUAL_RAM_IN_MB_PROPERTY = "virtualRamInMb";

    /* package */final static String VIRTUAL_RAM_IN_MB_COLUMN = "ram";

    /* package */final static int VIRTUAL_RAM_IN_MB_MIN = 1;

    /* package */final static int VIRTUAL_RAM_IN_MB_MAX = Integer.MAX_VALUE;

    /* package */final static boolean VIRTUAL_RAM_IN_MB_REQUIRED = true;

    @Column(name = VIRTUAL_RAM_IN_MB_COLUMN, nullable = false)
    @Range(min = VIRTUAL_RAM_IN_MB_MIN, max = VIRTUAL_RAM_IN_MB_MAX)
    private int virtualRamInMb;

    @Required(value = VIRTUAL_RAM_IN_MB_REQUIRED)
    public int getVirtualRamInMb()
    {
        return this.virtualRamInMb;
    }

    public void setVirtualRamInMb(int virtualRamInMb)
    {
        this.virtualRamInMb = virtualRamInMb;
    }

    public final static String VIRTUAL_CPU_CORES_PROPERTY = "virtualCpuCores";

    /* package */final static String VIRTUAL_CPU_CORES_COLUMN = "cpu";

    /* package */final static int VIRTUAL_CPU_CORES_MIN = 1;

    /* package */final static int VIRTUAL_CPU_CORES_MAX = Integer.MAX_VALUE;

    /* package */final static boolean VIRTUAL_CPU_CORES_REQUIRED = true;

    @Column(name = VIRTUAL_CPU_CORES_COLUMN, nullable = false)
    @Range(min = VIRTUAL_CPU_CORES_MIN, max = VIRTUAL_CPU_CORES_MAX)
    private int virtualCpuCores;

    @Required(value = VIRTUAL_CPU_CORES_REQUIRED)
    public int getVirtualCpuCores()
    {
        return this.virtualCpuCores;
    }

    public void setVirtualCpuCores(int virtualCpuCores)
    {
        this.virtualCpuCores = virtualCpuCores;
    }

    public final static String VIRTUAL_HARD_DISK_IN_BYTES_PROPERTY = "virtualHardDiskInBytes";

    /* package */final static String VIRTUAL_HARD_DISK_IN_BYTES_COLUMN = "hd";

    /* package */final static long VIRTUAL_HARD_DISK_IN_BYTES_MIN = 1;

    /* package */final static long VIRTUAL_HARD_DISK_IN_BYTES_MAX = Long.MAX_VALUE;

    /* package */final static boolean VIRTUAL_HARD_DISK_IN_BYTES_REQUIRED = true;

    @Column(name = VIRTUAL_HARD_DISK_IN_BYTES_COLUMN, nullable = false)
    @Range(min = VIRTUAL_HARD_DISK_IN_BYTES_MIN, max = VIRTUAL_HARD_DISK_IN_BYTES_MAX)
    private long virtualHardDiskInBytes;

    @Required(value = VIRTUAL_HARD_DISK_IN_BYTES_REQUIRED)
    public long getVirtualHardDiskInBytes()
    {
        return this.virtualHardDiskInBytes;
    }

    public void setVirtualHardDiskInBytes(long virtualHardDiskInBytes)
    {
        this.virtualHardDiskInBytes = virtualHardDiskInBytes;
    }

    public final static String REAL_RAM_IN_MB_PROPERTY = "realRamInMb";

    /* package */final static String REAL_RAM_IN_MB_COLUMN = "realram";

    /* package */final static int REAL_RAM_IN_MB_MIN = 0;

    /* package */final static int REAL_RAM_IN_MB_MAX = Integer.MAX_VALUE;

    /* package */final static boolean REAL_RAM_IN_MB_REQUIRED = true;

    @Column(name = REAL_RAM_IN_MB_COLUMN, nullable = false)
    @Range(min = REAL_RAM_IN_MB_MIN, max = REAL_RAM_IN_MB_MAX)
    private int realRamInMb;

    @Required(value = REAL_RAM_IN_MB_REQUIRED)
    public int getRealRamInMb()
    {
        return this.realRamInMb;
    }

    public void setRealRamInMb(int realRamInMb)
    {
        this.realRamInMb = realRamInMb;
    }

    public final static String REAL_CPU_CORES_PROPERTY = "realCpuCores";

    /* package */final static String REAL_CPU_CORES_COLUMN = "realCpu";

    /* package */final static int REAL_CPU_CORES_MIN = 0;

    /* package */final static int REAL_CPU_CORES_MAX = Integer.MAX_VALUE;

    /* package */final static boolean REAL_CPU_CORES_REQUIRED = true;

    @Column(name = REAL_CPU_CORES_COLUMN, nullable = false)
    @Range(min = REAL_CPU_CORES_MIN, max = REAL_CPU_CORES_MAX)
    private int realCpuCores;

    @Required(value = REAL_CPU_CORES_REQUIRED)
    public int getRealCpuCores()
    {
        return this.realCpuCores;
    }

    public void setRealCpuCores(int realCpuCores)
    {
        this.realCpuCores = realCpuCores;
    }

    public final static String REAL_HARD_DISK_IN_BYTES_PROPERTY = "realHardDiskInBytes";

    /* package */final static String REAL_HARD_DISK_IN_BYTES_COLUMN = "realStorage";

    /* package */final static long REAL_HARD_DISK_IN_BYTES_MIN = 0;

    /* package */final static long REAL_HARD_DISK_IN_BYTES_MAX = Long.MAX_VALUE;

    /* package */final static boolean REAL_HARD_DISK_IN_BYTES_REQUIRED = true;

    @Column(name = REAL_HARD_DISK_IN_BYTES_COLUMN, nullable = false)
    @Range(min = REAL_HARD_DISK_IN_BYTES_MIN, max = REAL_HARD_DISK_IN_BYTES_MAX)
    private long realHardDiskInBytes;

    @Required(value = REAL_HARD_DISK_IN_BYTES_REQUIRED)
    public long getRealHardDiskInBytes()
    {
        return this.realHardDiskInBytes;
    }

    public void setRealHardDiskInBytes(long realHardDiskInBytes)
    {
        this.realHardDiskInBytes = realHardDiskInBytes;
    }

    public final static String VIRTUAL_RAM_USED_IN_MB_PROPERTY = "virtualRamUsedInMb";

    /* package */final static String VIRTUAL_RAM_USED_IN_MB_COLUMN = "ramUsed";

    /* package */final static int VIRTUAL_RAM_USED_IN_MB_MIN = 0;

    /* package */final static int VIRTUAL_RAM_USED_IN_MB_MAX = Integer.MAX_VALUE;

    /* package */final static boolean VIRTUAL_RAM_USED_IN_MB_REQUIRED = true;

    @Column(name = VIRTUAL_RAM_USED_IN_MB_COLUMN, nullable = false)
    @Range(min = VIRTUAL_RAM_USED_IN_MB_MIN, max = VIRTUAL_RAM_USED_IN_MB_MAX)
    private int virtualRamUsedInMb;

    @Required(value = VIRTUAL_RAM_USED_IN_MB_REQUIRED)
    public int getVirtualRamUsedInMb()
    {
        return this.virtualRamUsedInMb;
    }

    public void setVirtualRamUsedInMb(int virtualRamUsedInMb)
    {
        this.virtualRamUsedInMb = virtualRamUsedInMb;
    }

    public final static String VIRTUAL_CPUS_USED_PROPERTY = "virtualCpusUsed";

    /* package */final static String VIRTUAL_CPUS_USED_COLUMN = "cpuUsed";

    /* package */final static long VIRTUAL_CPUS_USED_MIN = 0;

    /* package */final static long VIRTUAL_CPUS_USED_MAX = Integer.MAX_VALUE;

    /* package */final static boolean VIRTUAL_CPUS_USED_REQUIRED = true;

    @Column(name = VIRTUAL_CPUS_USED_COLUMN, nullable = false)
    @Range(min = VIRTUAL_CPUS_USED_MIN, max = VIRTUAL_CPUS_USED_MAX)
    private int virtualCpusUsed;

    @Required(value = VIRTUAL_CPUS_USED_REQUIRED)
    public int getVirtualCpusUsed()
    {
        return this.virtualCpusUsed;
    }

    public void setVirtualCpusUsed(int virtualCpusUsed)
    {
        this.virtualCpusUsed = virtualCpusUsed;
    }

    public final static String VIRTUAL_HARD_DISK_USED_IN_BYTES_PROPERTY =
        "virtualHardDiskUsedInBytes";

    /* package */final static String VIRTUAL_HARD_DISK_USED_IN_BYTES_COLUMN = "hdUsed";

    /* package */final static long VIRTUAL_HARD_DISK_USED_IN_BYTES_MIN = 0;

    /* package */final static long VIRTUAL_HARD_DISK_USED_IN_BYTES_MAX = Long.MAX_VALUE;

    /* package */final static boolean VIRTUAL_HARD_DISK_USED_IN_BYTES_REQUIRED = true;

    @Column(name = VIRTUAL_HARD_DISK_USED_IN_BYTES_COLUMN, nullable = false)
    @Range(min = VIRTUAL_HARD_DISK_USED_IN_BYTES_MIN, max = VIRTUAL_HARD_DISK_USED_IN_BYTES_MAX)
    private long virtualHardDiskUsedInBytes;

    @Required(value = VIRTUAL_HARD_DISK_USED_IN_BYTES_REQUIRED)
    public long getVirtualHardDiskUsedInBytes()
    {
        return this.virtualHardDiskUsedInBytes;
    }

    public void setVirtualHardDiskUsedInBytes(long virtualHardDiskUsedInMb)
    {
        this.virtualHardDiskUsedInBytes = virtualHardDiskUsedInMb;
    }

    public final static String VIRTUAL_CPUS_PER_CORE_PROPERTY = "virtualCpusPerCore";

    /* package */final static String VIRTUAL_CPUS_PER_CORE_COLUMN = "cpuRatio";

    /* package */final static int VIRTUAL_CPUS_PER_CORE_MIN = 1;

    /* package */final static int VIRTUAL_CPUS_PER_CORE_MAX = Integer.MAX_VALUE;

    /* package */final static boolean VIRTUAL_CPUS_PER_CORE_REQUIRED = true;

    @Column(name = VIRTUAL_CPUS_PER_CORE_COLUMN, nullable = false)
    @Range(min = VIRTUAL_CPUS_PER_CORE_MIN, max = VIRTUAL_CPUS_PER_CORE_MAX)
    private int virtualCpusPerCore = 1; // default value

    @Required(value = VIRTUAL_CPUS_PER_CORE_REQUIRED)
    public int getVirtualCpusPerCore()
    {
        return this.virtualCpusPerCore;
    }

    public void setVirtualCpusPerCore(int virtualCpusPerCore)
    {
        this.virtualCpusPerCore = virtualCpusPerCore;
    }

    public enum State
    {
        STOPPED, PROVISIONED, NOT_MANAGED, MANAGED, HALTED, UNLICENSED;
    }

    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static String STATE_COLUMN = "idState";

    @Enumerated(value = javax.persistence.EnumType.ORDINAL)
    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    private State state;

    @Required(value = STATE_REQUIRED)
    public State getState()
    {
        return this.state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public final static String VIRTUAL_SWITCH_PROPERTY = "virtualSwitch";

    public final static boolean VIRTUAL_SWITCH_REQUIRED = true;

    private final static int VIRTUAL_SWITCH_LENGTH_MIN = 0;

    private final static int VIRTUAL_SWITCH_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_SWITCH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VIRTUAL_SWITCH_COLUMN = "vswitchName";

    @Column(name = VIRTUAL_SWITCH_COLUMN, nullable = !VIRTUAL_SWITCH_REQUIRED, length = VIRTUAL_SWITCH_LENGTH_MAX)
    private String virtualSwitch;

    @Required(value = VIRTUAL_SWITCH_REQUIRED)
    @Length(min = VIRTUAL_SWITCH_LENGTH_MIN, max = VIRTUAL_SWITCH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_SWITCH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVirtualSwitch()
    {
        return this.virtualSwitch;
    }

    private void setVirtualSwitch(String virtualSwitch)
    {
        this.virtualSwitch = virtualSwitch;
    }

    // ********************************* Associations
    // ***************************
    public final static String DATACENTER_PROPERTY = "datacenter";

    final static boolean DATACENTER_REQUIRED = true;

    private final static String DATACENTER_ID_COLUMN = "idDataCenter";

    @JoinColumn(name = DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datacenter")
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    private void setDatacenter(Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String RACK_PROPERTY = "rack";

    private final static boolean RACK_REQUIRED = false;

    private final static String RACK_ID_COLUMN = "idRack";

    @JoinColumn(name = RACK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_rack")
    private Rack rack;

    @Required(value = RACK_REQUIRED)
    public Rack getRack()
    {
        return this.rack;
    }

    public void setRack(Rack rack)
    {
        this.rack = rack;
    }

    public boolean rackIsInDatacenter(Rack rack)
    {
        assert rack != null;

        return getDatacenter().getId() == rack.getDatacenter().getId();
    }

    public final static String HYPERVISOR_PROPERTY = "hypervisor";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "machine")
    private Hypervisor hypervisor;

    @Required(value = false)
    public Hypervisor getHypervisor()
    {
        return this.hypervisor;
    }

    public void setHypervisor(Hypervisor hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    // code in Machine
    public static final String DATASTORES_ASSOCIATION_TABLE = "datastore_assignment";

    public static final String DATASTORES_PROPERTY = "datastores";

    static final String DATASTORES_ID_COLUMN = "idDatastore";

    static final String MACHINES_ID_COLUMN = "idPhysicalMachine";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = DATASTORES_ASSOCIATION_TABLE, joinColumns = {@JoinColumn(name = MACHINES_ID_COLUMN)}, inverseJoinColumns = {@JoinColumn(name = DATASTORES_ID_COLUMN)})
    private List<Datastore> datastores = new ArrayList<Datastore>();

    public List<Datastore> getDatastores()
    {
        return Collections.unmodifiableList(this.datastores);
    }

    /* package */void addToDatastores(Datastore value)
    {
        assert value != null;
        assert !this.datastores.contains(value);

        this.datastores.add(value);
    }

    /* package */void removeFromDatastores(Datastore value)
    {
        assert value != null;
        assert this.datastores.contains(value);

        this.datastores.remove(value);
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise")
    private Enterprise enterprise;

    @Required(value = false)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    //
    public final static String INITIATOR_IQN_PROPERTY = "initiatorIQN";

    private final static boolean INITIATOR_IQN_REQUIRED = false;

    final static int INITIATOR_IQN_LENGTH_MIN = 0;

    final static int INITIATOR_IQN_LENGTH_MAX = 256;

    private final static boolean INITIATOR_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String INITIATOR_IQN_COLUMN = "initiatorIQN";

    @Column(name = INITIATOR_IQN_COLUMN, nullable = !INITIATOR_IQN_REQUIRED, length = INITIATOR_IQN_LENGTH_MAX)
    private String initiatorIQN;

    // TODO custom IQN validation
    @Required(value = INITIATOR_IQN_REQUIRED)
    @Length(min = INITIATOR_IQN_LENGTH_MIN, max = INITIATOR_IQN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = INITIATOR_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getInitiatorIQN()
    {
        return this.initiatorIQN;
    }

    public void setInitiatorIQN(String initiatorIQN)
    {
        this.initiatorIQN = initiatorIQN;
    }

    // *************************** Mandatory constructors
    // ***********************
    /* package */Machine(Datacenter datacenter, String name, String description,
        int virtualRamInMb, int realRamInMb, int virtualRamUsedInMb, long virtualHardDiskInMb,
        long realHardDiskInMb, long virtualHardDiskUsed, int realCpuCores, int virtualCpuCores,
        int virtualCpusUsed, int virtualCpusPerCore, State state, String virtualSwitch)
    {
        setDatacenter(datacenter);
        setName(name);
        setDescription(description);

        setVirtualRamInMb(virtualRamInMb);
        setRealRamInMb(realRamInMb);
        setVirtualRamUsedInMb(virtualRamUsedInMb);

        setVirtualHardDiskInBytes(virtualHardDiskInMb);
        setRealHardDiskInBytes(realHardDiskInMb);
        setVirtualHardDiskUsedInBytes(virtualHardDiskUsed);

        setRealCpuCores(realCpuCores);
        setVirtualCpuCores(virtualCpuCores);
        setVirtualCpusUsed(virtualCpusUsed);
        setVirtualCpusPerCore(virtualCpusPerCore);

        setState(state);
        setVirtualSwitch(virtualSwitch);
    }

    // ********************************** Others
    // ********************************

    public Hypervisor createHypervisor(HypervisorType type, String ip,
        String ipService, int port, String user, String password)
    {
        return new Hypervisor(this, type, ip, ipService, port, user, password);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
