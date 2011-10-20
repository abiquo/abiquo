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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datastore;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualMachine.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualMachine.TABLE_NAME)
@NamedQueries({@NamedQuery(name = "VIRTUAL_MACHINE.BY_VAPP", query = VirtualMachine.BY_VAPP),
@NamedQuery(name = "VIRTUAL_MACHINE.BY_DC", query = VirtualMachine.BY_DC)})
public class VirtualMachine extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualmachine";

    public static final String BY_VAPP = "SELECT nvi.virtualMachine "
        + "FROM NodeVirtualImage nvi " + "WHERE nvi.virtualAppliance.id = :vapp_id";

    public static final String BY_DC = "SELECT vm "
        + "FROM VirtualMachine vm, Hypervisor hy, Machine pm "
        + " WHERE vm.hypervisor.id = hy.id and hy.machine = pm.id "
        + " AND pm.datacenter.id = :datacenterId";

    public static final int MANAGED = 1;

    public static final int NOT_MANAGED = 0;

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

    @JoinColumn(name = HYPERVISOR_ID_COLUMN)
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

    public final static String VIRTUAL_IMAGE_PROPERTY = "virtualImage";

    private final static boolean VIRTUAL_IMAGE_REQUIRED = true;

    private final static String VIRTUAL_IMAGE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_IMAGE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    // , cascade = CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualimage")
    private VirtualImage virtualImage;

    @Required(value = VIRTUAL_IMAGE_REQUIRED)
    public VirtualImage getVirtualImage()
    {
        return this.virtualImage;
    }

    public void setVirtualImage(final VirtualImage virtualImage)
    {
        this.virtualImage = virtualImage;
    }

    //
    public final static String DATASTORE_PROPERTY = "datastore";

    private final static boolean DATASTORE_REQUIRED = false;

    private final static String DATASTORE_ID_COLUMN = "idDatastore";

    @JoinColumn(name = DATASTORE_ID_COLUMN)
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

    private final static boolean NAME_REQUIRED = false;

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
    private Integer idType;

    public Integer getIdType()
    {
        return this.idType;
    }

    public boolean isManaged()
    {
        return getIdType() == MANAGED;
    }

    public void setIdType(final Integer idType)
    {
        this.idType = idType;
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

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = true;

    private final static String USER_ID_COLUMN = "idUser";

    @JoinColumn(name = USER_ID_COLUMN)
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

    private final static int PASSWORD_LENGTH_MAX = 255;

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

    public VirtualMachine(final String name, final Enterprise enterprise, final User user,
        final Hypervisor hypervisor, final VirtualImage virtualImage, final UUID uuid,
        final Integer typeId)
    {
        setName(name);
        setEnterprise(enterprise);
        setUser(user);
        setHypervisor(hypervisor);
        setVirtualImage(virtualImage);
        setUuid(uuid.toString());
        setIdType(typeId);
    }

    public VirtualMachine(final String name, final Enterprise enterprise, final User user,
        final VirtualImage virtualImage, final UUID uuid, final Integer typeId)
    {
        setName(name);
        setEnterprise(enterprise);
        setUser(user);
        setVirtualImage(virtualImage);
        setUuid(uuid.toString());
        setIdType(typeId);
    }

}
