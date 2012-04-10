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

package com.abiquo.server.core.cloud;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.EthernetDriverType;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.chef.RunlistElement;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@FilterDefs({@FilterDef(name = VirtualMachine.NOT_TEMP),
@FilterDef(name = VirtualMachine.ONLY_TEMP)})
@Filters({@Filter(name = VirtualMachine.NOT_TEMP, condition = "temporal is null"),
@Filter(name = VirtualMachine.ONLY_TEMP, condition = "temporal is not null")})
@Table(name = VirtualMachine.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualMachine.TABLE_NAME)
@NamedQueries({@NamedQuery(name = "VIRTUAL_MACHINE.BY_VAPP", query = VirtualMachine.BY_VAPP),
@NamedQuery(name = "VIRTUAL_MACHINE.BY_DC", query = VirtualMachine.BY_DC),
@NamedQuery(name = "VIRTUAL_MACHINE.BY_VMT", query = VirtualMachine.BY_VMT),
@NamedQuery(name = "VIRTUAL_MACHINE.HAS_VMT", query = VirtualMachine.HAS_VMT)})
public class VirtualMachine extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualmachine";

    public static final String BY_VAPP = "SELECT nvi.virtualMachine "
        + "FROM NodeVirtualImage nvi " + "WHERE nvi.virtualAppliance.id = :vapp_id "
        + "AND ( nvi.virtualMachine.name like :filterLike )";

    public static final String BY_DC = "SELECT vm "
        + "FROM VirtualMachine vm, Hypervisor hy, Machine pm "
        + " WHERE vm.hypervisor.id = hy.id and hy.machine = pm.id "
        + " AND pm.datacenter.id = :datacenterId";

    public static final String BY_VMT = "SELECT vm " + "FROM VirtualMachine vm "
        + "WHERE vm.virtualMachineTemplate.id = :virtualMachineTplId";

    public static final String HAS_VMT = "SELECT COUNT(*) " + "FROM VirtualMachine vm "
        + "WHERE vm.virtualMachineTemplate.id = :virtualMachineTplId";

    public static final int MANAGED = 1;

    public static final int NOT_MANAGED = 0;

    /* Name of the filters we use to return the virtual machine temporals or not */
    public static final String NOT_TEMP = "virtualmachine_not_temp";

    public static final String ONLY_TEMP = "virtualmachine_only_temp";

    public VirtualMachine()
    {
    }

    private final static String ID_COLUMN = "idVM";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String HYPERVISOR_PROPERTY = "hypervisor";

    private final static boolean HYPERVISOR_REQUIRED = false; // XXX

    private final static String HYPERVISOR_ID_COLUMN = "idHypervisor";

    @JoinColumn(name = HYPERVISOR_ID_COLUMN, nullable = !HYPERVISOR_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_hypervisor")
    private Hypervisor hypervisor;

    @Required(value = HYPERVISOR_REQUIRED)
    public Hypervisor getHypervisor()
    {
        return this.hypervisor;
    }

    public void setHypervisor(final Hypervisor hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    //

    public final static String VIRTUAL_MACHINE_TEMPLATE_PROPERTY = "virtualMachineTemplate";

    private final static boolean VIRTUAL_MACHINE_TEMPLATE_REQUIRED = false;

    private final static String VIRTUAL_MACHINE_TEMPLATE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_MACHINE_TEMPLATE_ID_COLUMN, nullable = !VIRTUAL_MACHINE_TEMPLATE_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    // , cascade = CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualimage")
    private VirtualMachineTemplate virtualMachineTemplate;

    @Required(value = VIRTUAL_MACHINE_TEMPLATE_REQUIRED)
    public VirtualMachineTemplate getVirtualMachineTemplate()
    {
        return this.virtualMachineTemplate;
    }

    public void setVirtualMachineTemplate(final VirtualMachineTemplate virtualMachineTemplate)
    {
        this.virtualMachineTemplate = virtualMachineTemplate;
    }

    //

    public final static String VIRTUAL_IMAGE_CONVERSION_PROPERTY = "virtualImageConversion";

    private final static boolean VIRTUAL_IMAGE_CONVERSION_REQUIRED = false;

    private final static String VIRTUAL_IMAGE_CONVERSION_ID_COLUMN = "idConversion";

    @JoinColumn(name = VIRTUAL_IMAGE_CONVERSION_ID_COLUMN, nullable = !VIRTUAL_IMAGE_CONVERSION_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    // , cascade = CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_conversion")
    private VirtualImageConversion virtualImageConversion;

    @Required(value = VIRTUAL_IMAGE_CONVERSION_REQUIRED)
    public VirtualImageConversion getVirtualImageConversion()
    {
        return this.virtualImageConversion;
    }

    public void setVirtualImageConversion(final VirtualImageConversion virtualImageConversion)
    {
        this.virtualImageConversion = virtualImageConversion;
    }

    //
    public final static String DATASTORE_PROPERTY = "datastore";

    private final static boolean DATASTORE_REQUIRED = false;

    private final static String DATASTORE_ID_COLUMN = "idDatastore";

    @JoinColumn(name = DATASTORE_ID_COLUMN, nullable = !DATASTORE_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datastore")
    private Datastore datastore;

    @Required(value = DATASTORE_REQUIRED)
    public Datastore getDatastore()
    {
        return this.datastore;
    }

    public void setDatastore(final Datastore datastore)
    {
        this.datastore = datastore;
    }

    //

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 255;

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

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    // private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "Description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    // @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public final static String RAM_PROPERTY = "ram";

    private final static String RAM_COLUMN = "ram";

    private final static int RAM_MIN = Integer.MIN_VALUE;

    private final static int RAM_MAX = Integer.MAX_VALUE;

    @Column(name = RAM_COLUMN, nullable = true)
    @Range(min = RAM_MIN, max = RAM_MAX)
    private int ram;

    public int getRam()
    {
        return this.ram;
    }

    public void setRam(final int ram)
    {
        this.ram = ram;
    }

    public final static String CPU_PROPERTY = "cpu";

    private final static String CPU_COLUMN = "cpu";

    private final static int CPU_MIN = Integer.MIN_VALUE;

    private final static int CPU_MAX = Integer.MAX_VALUE;

    @Column(name = CPU_COLUMN, nullable = true)
    @Range(min = CPU_MIN, max = CPU_MAX)
    private int cpu;

    public int getCpu()
    {
        return this.cpu;
    }

    public void setCpu(final int cpu)
    {
        this.cpu = cpu;
    }

    public final static String HD_PROPERTY = "hdInBytes";

    private final static String HD_COLUMN = "hd";

    private final static long HD_MIN = Long.MIN_VALUE;

    private final static long HD_MAX = Long.MAX_VALUE;

    @Column(name = HD_COLUMN, nullable = true)
    @Range(min = HD_MIN, max = HD_MAX)
    private long hdInBytes;

    public long getHdInBytes()
    {
        return this.hdInBytes;
    }

    public void setHdInBytes(final long hdInBytes)
    {
        this.hdInBytes = hdInBytes;
    }

    public final static String VDRP_PORT_PROPERTY = "vdrpPort";

    private final static String VDRP_PORT_COLUMN = "vdrpPort";

    private final static int VDRP_PORT_MIN = Integer.MIN_VALUE;

    private final static int VDRP_PORT_MAX = Integer.MAX_VALUE;

    @Column(name = VDRP_PORT_COLUMN, nullable = true)
    @Range(min = VDRP_PORT_MIN, max = VDRP_PORT_MAX)
    private int vdrpPort;

    public int getVdrpPort()
    {
        return this.vdrpPort;
    }

    public void setVdrpPort(final int vdrpPort)
    {
        this.vdrpPort = vdrpPort;
    }

    public final static String VDRP_IP_PROPERTY = "vdrpIP";

    private final static boolean VDRP_IP_REQUIRED = false;

    private final static int VDRP_IP_LENGTH_MIN = 0;

    private final static int VDRP_IP_LENGTH_MAX = 255;

    private final static boolean VDRP_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VDRP_IP_COLUMN = "VdrpIP";

    @Column(name = VDRP_IP_COLUMN, nullable = !VDRP_IP_REQUIRED, length = VDRP_IP_LENGTH_MAX)
    private String vdrpIP;

    @Required(value = VDRP_IP_REQUIRED)
    @Length(min = VDRP_IP_LENGTH_MIN, max = VDRP_IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VDRP_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVdrpIP()
    {
        return this.vdrpIP;
    }

    public void setVdrpIP(final String vdrpIp)
    {
        this.vdrpIP = vdrpIp;
    }

    public static final String UUID_PROPERTY = "uuid";

    private static final boolean UUID_REQUIRED = true;

    private static final String UUID_COLUMN = "UUID";

    private final static int UUID_LENGTH_MIN = 0;

    private final static int UUID_LENGTH_MAX = 255;

    @Column(name = UUID_COLUMN, nullable = !UUID_REQUIRED, length = UUID_LENGTH_MAX)
    private String uuid;

    public void setUuid(final String uuid)
    {
        this.uuid = uuid;
    }

    @Required(value = UUID_REQUIRED)
    @Length(min = UUID_LENGTH_MIN, max = UUID_LENGTH_MAX)
    public String getUuid()
    {
        return uuid;
    }

    public final static String HIGH_DISPONIBILITY_PROPERTY = "highDisponibility";

    private final static String HIGH_DISPONIBILITY_COLUMN = "high_disponibility";

    private final static int HIGH_DISPONIBILITY_MIN = Integer.MIN_VALUE;

    private final static int HIGH_DISPONIBILITY_MAX = Integer.MAX_VALUE;

    @Column(name = HIGH_DISPONIBILITY_COLUMN, nullable = true)
    @Range(min = HIGH_DISPONIBILITY_MIN, max = HIGH_DISPONIBILITY_MAX)
    private int highDisponibility;

    public int getHighDisponibility()
    {
        return this.highDisponibility;
    }

    public void setHighDisponibility(final int highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    public final static String ID_TYPE_PROPERTY = "idType";

    private final static String ID_TYPE_COLUMN = "idType";

    private final static int ID_TYPE_MIN = Integer.MIN_VALUE;

    private final static int ID_TYPE_MAX = Integer.MAX_VALUE;

    @Column(name = ID_TYPE_COLUMN, nullable = false)
    @Range(min = ID_TYPE_MIN, max = ID_TYPE_MAX)
    private int idType;

    public int getIdType()
    {
        return this.idType;
    }

    public boolean isManaged()
    {
        return getIdType() == MANAGED;
    }

    public void setIdType(final int idType)
    {
        this.idType = idType;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN, nullable = !ENTERPRISE_REQUIRED)
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

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = false;

    private final static String USER_ID_COLUMN = "idUser";

    @JoinColumn(name = USER_ID_COLUMN, nullable = !USER_REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_user")
    private User user;

    @Required(value = USER_REQUIRED)
    public User getUser()
    {
        return this.user;
    }

    public void setUser(final User user)
    {
        this.user = user;
    }

    public final static String SUB_STATE_PROPERTY = "subState";

    private final static boolean SUB_STATE_REQUIRED = false;

    private final static String SUB_STATE_COLUMN = "subState";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = SUB_STATE_COLUMN, nullable = !SUB_STATE_REQUIRED)
    private State subState;

    @Required(value = SUB_STATE_REQUIRED)
    public State getSubState()
    {
        return this.subState;
    }

    public void setSubState(final State subState)
    {
        this.subState = subState;
    }

    //
    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static String STATE_COLUMN = "state";

    private final static VirtualMachineState STATE_DEFAULT = VirtualMachineState.NOT_ALLOCATED;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    private VirtualMachineState state = STATE_DEFAULT;

    @Required(value = STATE_REQUIRED)
    public VirtualMachineState getState()
    {
        return this.state;
    }

    public void setState(final VirtualMachineState state)
    {
        this.state = state;
    }

    public final static String PASSWORD_PROPERTY = "password";

    private final static boolean PASSWORD_REQUIRED = false;

    private final static int PASSWORD_LENGTH_MIN = 0;

    private final static int PASSWORD_LENGTH_MAX = 32;

    private final static boolean PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PASSWORD_COLUMN = "password";

    @Column(name = PASSWORD_COLUMN, nullable = !PASSWORD_REQUIRED, length = PASSWORD_LENGTH_MAX)
    private String password;

    @Required(value = PASSWORD_REQUIRED)
    @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public final static String CONFIGURATION_PROPERTY = "networkConfiguration";

    private final static boolean CONFIGURATION_REQUIRED = false;

    private final static String CONFIGURATION_ID_COLUMN = "network_configuration_id";

    @JoinColumn(name = CONFIGURATION_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_configuration")
    private NetworkConfiguration networkConfiguration;

    @Required(value = CONFIGURATION_REQUIRED)
    public NetworkConfiguration getNetworkConfiguration()
    {
        return this.networkConfiguration;
    }

    public void setNetworkConfiguration(final NetworkConfiguration configuration)
    {
        this.networkConfiguration = configuration;
    }

    public final static String TEMPORAL_PROPERTY = "temporal";

    private final static String TEMPORAL_COLUMN = "temporal";

    private final static int TEMPORAL_MIN = 1;

    private final static int TEMPORAL_MAX = Integer.MAX_VALUE;

    @Column(name = TEMPORAL_COLUMN, nullable = true)
    @Range(min = TEMPORAL_MIN, max = TEMPORAL_MAX)
    private Integer temporal = null;

    /**
     * This field is used to mark temporal objects used in reconfigure operations. It holds a
     * reference to the id of the original virtual machine.
     * 
     * @return The reference to the id of the original virtual machine.
     */
    public Integer getTemporal()
    {
        return this.temporal;
    }

    public void setTemporal(final Integer temporal)
    {
        this.temporal = temporal;
    }

    /** List of disks */
    @OneToMany(targetEntity = DiskManagement.class)
    @JoinTable(name = "rasd_management", joinColumns = {@JoinColumn(name = "idVM")}, inverseJoinColumns = {@JoinColumn(name = "idManagement")})
    private List<DiskManagement> disks;

    public List<DiskManagement> getDisks()
    {
        return disks != null ? disks : new LinkedList<DiskManagement>();
    }

    public void setDisks(final List<DiskManagement> disks)
    {
        this.disks = disks;
    }

    /** List of volumes */
    @OneToMany(targetEntity = VolumeManagement.class)
    @JoinTable(name = "rasd_management", joinColumns = {@JoinColumn(name = "idVM")}, inverseJoinColumns = {@JoinColumn(name = "idManagement")})
    private List<VolumeManagement> volumes;

    public List<VolumeManagement> getVolumes()
    {
        return volumes != null ? volumes : new LinkedList<VolumeManagement>();
    }

    public void setVolumes(final List<VolumeManagement> volumes)
    {
        this.volumes = volumes;
    }

    /** List of ips */
    @OneToMany(targetEntity = IpPoolManagement.class)
    @JoinTable(name = "rasd_management", joinColumns = {@JoinColumn(name = "idVM")}, inverseJoinColumns = {@JoinColumn(name = "idManagement")})
    private List<IpPoolManagement> ips;

    public List<IpPoolManagement> getIps()
    {
        return ips != null ? ips : new LinkedList<IpPoolManagement>();
    }

    public void setIps(final List<IpPoolManagement> ips)
    {
        this.ips = ips;
    }

    /**
     * List all {@link RasdManagement} (including {@link DiskManagement}, {@link IpPoolManagement}
     * and {@link VolumeManagement} )
     */
    // do not orphanRemoval = true,
    @OneToMany(targetEntity = RasdManagement.class, mappedBy = RasdManagement.VIRTUAL_MACHINE_PROPERTY)
    private List<RasdManagement> rasdManagements;

    public List<RasdManagement> getRasdManagements()
    {
        return rasdManagements;
    }

    public void setRasdManagements(final List<RasdManagement> rasdManagements)
    {
        this.rasdManagements = rasdManagements;
    }

    public final static String ETHERNET_DRIVER_TYPE_PROPERTY = "ethernetDriverType";

    private final static boolean ETHERNET_DRIVER_TYPE_REQUIRED = false;

    private final static String ETHERNET_DRIVER_TYPE_COLUMN = "ethDriverType";

    private final static int ETHERNET_DRIVER_TYPE_COLUMN_LENGTH = 16;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = ETHERNET_DRIVER_TYPE_COLUMN, nullable = !ETHERNET_DRIVER_TYPE_REQUIRED, length = ETHERNET_DRIVER_TYPE_COLUMN_LENGTH)
    private EthernetDriverType ethernetDriverType;

    @Required(value = ETHERNET_DRIVER_TYPE_REQUIRED)
    public EthernetDriverType getEthernetDriverType()
    {
        return this.ethernetDriverType;
    }

    public void setEthernetDriverType(final EthernetDriverType ethernetDriverType)
    {
        this.ethernetDriverType = ethernetDriverType;
    }

    public static final String CHEF_RUNLIST_TABLE = "chef_runlist";

    public static final String CHEF_RUNLIST_PROPERTY = "runlist";

    static final String CHEF_RUNLIST_ID_COLUMN = "id";

    static final String VIRTUALMACHINE_ID_COLUMN = "idVM";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = CHEF_RUNLIST_TABLE, joinColumns = {@JoinColumn(name = CHEF_RUNLIST_ID_COLUMN)}, inverseJoinColumns = {@JoinColumn(name = VIRTUALMACHINE_ID_COLUMN)})
    @OrderBy(RunlistElement.PRIORITY_PROPERTY + " ASC")
    private List<RunlistElement> runlist = new ArrayList<RunlistElement>();

    public List<RunlistElement> getRunlist()
    {
        if (runlist == null)
        {
            runlist = new ArrayList<RunlistElement>();
        }
        return runlist;
    }

    /* package */void addRunlistElement(final RunlistElement element)
    {
        this.runlist.add(element);
    }

    /* package */void removeRunlistElement(final RunlistElement element)
    {
        this.runlist.remove(element);
    }

    /* ******************* Helper methods ******************* */

    public boolean isChefEnabled()
    {
        return getVirtualMachineTemplate().isChefEnabled() && getEnterprise().isChefEnabled();
    }

    public boolean isStateful()
    {
        return virtualMachineTemplate.isStateful();
    }

    // vm imported from PM (no more!)
    public boolean isImported()
    {
        return getVirtualMachineTemplate() == null;
    }

    // vm imported from a PM and added into a Vapp
    // when vm isCaptured then isn't imported
    public boolean isCaptured()
    {
        return getVirtualMachineTemplate().getRepository() == null && !isStateful();
    }

    public VirtualMachine(final String name, final Enterprise enterprise, final User user,
        final Hypervisor hypervisor, final VirtualMachineTemplate virtualMachineTemplate,
        final UUID uuid, final Integer typeId)
    {
        setName(name);
        setEnterprise(enterprise);
        setUser(user);
        setHypervisor(hypervisor);
        setVirtualMachineTemplate(virtualMachineTemplate);
        setUuid(uuid.toString());
        setIdType(typeId);
    }

    public VirtualMachine(final String name, final Enterprise enterprise, final User user,
        final VirtualMachineTemplate virtualMachineTemplate, final UUID uuid, final Integer typeId)
    {
        setName(name);
        setEnterprise(enterprise);
        setUser(user);
        setVirtualMachineTemplate(virtualMachineTemplate);
        setUuid(uuid.toString());
        setIdType(typeId);
    }

    /**
     * This method is intended to clone a {@link VirtualMachine} that shares a reference to a
     * {@link Datastore}, {@link Enterprise}, {@link User} and the {@link VirtualMachineTemplate} .
     * The {@link Datastore} and the {@link Enterprise} are not editable in a {@link VirtualMachine}
     * .
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public VirtualMachine clone()
    {
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setCpu(cpu);
        // We are sharing the same reference
        virtualMachine.setDatastore(datastore);
        virtualMachine.setDescription(description);
        // The enterprise cannot be modified
        virtualMachine.setEnterprise(enterprise);
        virtualMachine.setHdInBytes(hdInBytes);
        virtualMachine.setHighDisponibility(highDisponibility);
        // The hypervisor is selected by the allocator
        virtualMachine.setHypervisor(hypervisor);
        virtualMachine.setIdType(idType);
        virtualMachine.setName(name);
        virtualMachine.setPassword(password);
        virtualMachine.setRam(ram);
        virtualMachine.setState(state);
        // Not editable
        virtualMachine.setUser(user);
        virtualMachine.setUuid(uuid);
        virtualMachine.setVdrpIP(vdrpIP);
        virtualMachine.setVdrpPort(vdrpPort);
        // Not editable
        virtualMachine.setVirtualMachineTemplate(virtualMachineTemplate);
        return virtualMachine;
    }

    /**
     * Ways to order this element in the queries.
     */
    public static enum OrderByEnum
    {
        NAME("name", "nvi.virtualMachine.name"), STATE("state", "nvi.virtualMachine.state");

        public static OrderByEnum fromValue(final String orderBy)
        {
            for (OrderByEnum currentOrder : OrderByEnum.values())
            {
                if (currentOrder.name().equalsIgnoreCase(orderBy))
                {
                    return currentOrder;
                }
            }

            return null;
        }

        private String columnSQL;

        private String columnHQL;

        private OrderByEnum(final String columnSQL, final String columnHQL)
        {
            this.columnSQL = columnSQL;
            this.columnHQL = columnHQL;
        }

        public String getColumnSQL()
        {
            return columnSQL;
        }

        public String getColumnHQL()
        {
            return columnHQL;
        }

    }
}
