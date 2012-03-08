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

package com.abiquo.server.core.appslibrary;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.EthernetDriverType;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualMachineTemplate.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualMachineTemplate.TABLE_NAME)
public class VirtualMachineTemplate extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualimage";
    
    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public VirtualMachineTemplate()
    {
        // Just for JPA support
    }

    public VirtualMachineTemplate(final Enterprise enterprise, final String name,
        final DiskFormatType diskFormatType, final String path, final long diskFileSize,
        final Category category, final String creationUser)
    {
        super();
        this.enterprise = enterprise;
        this.name = name;
        this.diskFormatType = diskFormatType;
        this.path = path;
        this.diskFileSize = diskFileSize;
        this.category = category;
        this.creationUser = creationUser;
    }

    public VirtualMachineTemplate(final Enterprise enterprise, final String name,
        final DiskFormatType diskFormatType, final String path, final long diskFileSize,
        final Category category, final String creationUser, final VolumeManagement volume)
    {
        super();
        this.enterprise = enterprise;
        this.name = name;
        this.diskFormatType = diskFormatType;
        this.path = path;
        this.diskFileSize = diskFileSize;
        this.category = category;
        this.volume = volume;
        this.stateful = volume != null;
        this.creationUser = creationUser;
    }

    public final static String ID_COLUMN = "idImage";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String DISKFORMAT_TYPE_PROPERTY = "diskFormatType";

    private final static boolean DISKFORMAT_TYPE_REQUIRED = true;

    private final static String DISKFORMAT_TYPE_COLUMN = "type";

    private final static int DISKFORMAT_TYPE_COLUMN_LENGTH = 50;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = DISKFORMAT_TYPE_COLUMN, nullable = !DISKFORMAT_TYPE_REQUIRED, length = DISKFORMAT_TYPE_COLUMN_LENGTH)
    private DiskFormatType diskFormatType;

    @Required(value = DISKFORMAT_TYPE_REQUIRED)
    public DiskFormatType getDiskFormatType()
    {
        return this.diskFormatType;
    }

    public void setDiskFormatType(final DiskFormatType diskFormatType)
    {
        this.diskFormatType = diskFormatType;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 255;

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

    public final static String STATEFUL_PROPERTY = "stateful";

    private final static String STATEFUL_COLUMN = "stateful";

    private final static boolean STATEFUL_REQUIRED = true;

    @Column(name = STATEFUL_COLUMN, columnDefinition = "int", nullable = false)
    private boolean stateful = false;

    @Required(value = STATEFUL_REQUIRED)
    public boolean isStateful()
    {
        return this.stateful; // && getVolume() != null;
    }

    /**
     * This method should be only called from VolumeManagement.setVirtualMachineTemplate()
     */
    public void setStateful(final boolean stateful)
    {
        this.stateful = stateful;
    }

    public final static String VOLUME_PROPERTY = "volume";

    private final static boolean VOLUME_REQUIRED = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "virtualMachineTemplate")
    private VolumeManagement volume;

    @Required(value = VOLUME_REQUIRED)
    public VolumeManagement getVolume()
    {
        return volume;
    }

    // Set volume is not available. The link to the volume must be performed by calling
    // VolumeManagement.setVirtualMachineTemplate

    public final static String SHARED_PROPERTY = "shared";

    private final static String SHARED_COLUMN = "shared";

    private final static boolean SHARED_REQUIRED = true;

    @Column(name = SHARED_COLUMN, columnDefinition = "int", nullable = false)
    private boolean shared = false;

    @Required(value = SHARED_REQUIRED)
    public boolean isShared()
    {
        return this.shared;
    }

    public void setShared(final boolean shared)
    {
        this.shared = shared;
    }

    public final static String CPU_REQUIRED_PROPERTY = "cpuRequired";

    private final static String CPU_REQUIRED_COLUMN = "cpu_required";

    private final static int CPU_REQUIRED_MIN = Integer.MIN_VALUE;

    private final static int CPU_REQUIRED_MAX = Integer.MAX_VALUE;

    @Column(name = CPU_REQUIRED_COLUMN, nullable = true)
    @Range(min = CPU_REQUIRED_MIN, max = CPU_REQUIRED_MAX)
    private int cpuRequired;

    public int getCpuRequired()
    {
        return this.cpuRequired;
    }

    public void setCpuRequired(final int cpuRequired)
    {
        this.cpuRequired = cpuRequired;
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

    public final static String PATH_PROPERTY = "path";

    private final static boolean PATH_REQUIRED = true;

    /* package */final static int PATH_LENGTH_MIN = 0;

    /* package */final static int PATH_LENGTH_MAX = 255;

    private final static boolean PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PATH_COLUMN = "pathName";

    @Column(name = PATH_COLUMN, nullable = !PATH_REQUIRED, length = PATH_LENGTH_MAX)
    private String path;

    @Required(value = PATH_REQUIRED)
    @Length(min = PATH_LENGTH_MIN, max = PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPath()
    {
        return this.path;
    }

    public void setPath(final String path)
    {
        this.path = path;
    }

    public final static String OVFID_PROPERTY = "ovfid";

    final static boolean OVFID_REQUIRED = false;

    /* package */final static int OVFID_LENGTH_MIN = 0;

    /* package */final static int OVFID_LENGTH_MAX = 255;

    final static boolean OVFID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String OVFID_COLUMN = "ovfid";

    @Column(name = OVFID_COLUMN, nullable = !OVFID_REQUIRED, length = OVFID_LENGTH_MAX)
    private String ovfid;

    @Required(value = OVFID_REQUIRED)
    @Length(min = OVFID_LENGTH_MIN, max = OVFID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = OVFID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getOvfid()
    {
        return this.ovfid;
    }

    public void setOvfid(final String ovfid)
    {
        this.ovfid = ovfid;
    }

    public final static String RAM_REQUIRED_PROPERTY = "ramRequired";

    private final static String RAM_REQUIRED_COLUMN = "ram_required";

    private final static int RAM_REQUIRED_MIN = Integer.MIN_VALUE;

    private final static int RAM_REQUIRED_MAX = Integer.MAX_VALUE;

    @Column(name = RAM_REQUIRED_COLUMN, nullable = true)
    @Range(min = RAM_REQUIRED_MIN, max = RAM_REQUIRED_MAX)
    private int ramRequired;

    public int getRamRequired()
    {
        return this.ramRequired;
    }

    public void setRamRequired(final int ramRequired)
    {
        this.ramRequired = ramRequired;
    }

    public final static String HD_REQUIRED_PROPERTY = "hdRequiredInBytes";

    private final static String HD_REQUIRED_COLUMN = "hd_required";

    private final static long HD_REQUIRED_MIN = Long.MIN_VALUE;

    private final static long HD_REQUIRED_MAX = Long.MAX_VALUE;

    @Column(name = HD_REQUIRED_COLUMN, nullable = true)
    @Range(min = HD_REQUIRED_MIN, max = HD_REQUIRED_MAX)
    private long hdRequiredInBytes;

    public long getHdRequiredInBytes()
    {
        return this.hdRequiredInBytes;
    }

    public void setHdRequiredInBytes(final long hdRequiredInBytes)
    {
        this.hdRequiredInBytes = hdRequiredInBytes;
    }

    public final static String MASTER_PROPERTY = "master";

    private final static boolean MASTER_REQUIRED = false;

    private final static String MASTER_ID_COLUMN = "idMaster";

    @JoinColumn(name = MASTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_master")
    private VirtualMachineTemplate master;

    @Required(value = MASTER_REQUIRED)
    public VirtualMachineTemplate getMaster()
    {
        return this.master;
    }

    public void setMaster(final VirtualMachineTemplate master)
    {
        this.master = master;
    }

    public final static String CATEGORY_PROPERTY = "category";

    private final static boolean CATEGORY_REQUIRED = true;

    private final static String CATEGORY_ID_COLUMN = "idCategory";

    @JoinColumn(name = CATEGORY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_category")
    private Category category;

    @Required(value = CATEGORY_REQUIRED)
    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(final Category category)
    {
        this.category = category;
    }

    public final static String DISK_FILE_SIZE_PROPERTY = "diskFileSize";

    private final static String DISK_FILE_SIZE_COLUMN = "diskFileSize";

    private final static long DISK_FILE_SIZE_MIN = Long.MIN_VALUE;

    private final static long DISK_FILE_SIZE_MAX = Long.MAX_VALUE;

    @Column(name = DISK_FILE_SIZE_COLUMN, nullable = false)
    @Range(min = DISK_FILE_SIZE_MIN, max = DISK_FILE_SIZE_MAX)
    private long diskFileSize;

    public long getDiskFileSize()
    {
        return this.diskFileSize;
    }

    public void setDiskFileSize(final long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = true;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public final static String ICON_URL_PROPERTY = "iconUrl";

    private final static boolean ICON_URL_REQUIRED = false;

    private final static int ICON_URL_LENGTH_MIN = 0;

    private final static int ICON_URL_LENGTH_MAX = 255;

    private final static boolean ICON_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ICON_URL_COLUMN = "iconUrl";

    @Column(name = ICON_URL_COLUMN, nullable = !ICON_URL_REQUIRED, length = ICON_URL_LENGTH_MAX)
    private String iconUrl;

    @Required(value = ICON_URL_REQUIRED)
    @Length(min = ICON_URL_LENGTH_MIN, max = ICON_URL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ICON_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public void setIconUrl(final String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public final static String COST_CODE_PROPERTY = "costCode";

    private final static boolean COST_CODE_REQUIRED = false;

    private final static int COST_CODE_LENGTH_MIN = 0;

    private final static int COST_CODE_LENGTH_MAX = 50;

    private final static String COST_CODE_COLUMN = "cost_code";

    @Column(name = COST_CODE_COLUMN, nullable = !COST_CODE_REQUIRED)
    private int costCode;

    @Required(value = COST_CODE_REQUIRED)
    public int getCostCode()
    {
        return costCode;
    }

    public void setCostCode(final int costCode)
    {
        this.costCode = costCode;
    }

    public final static String REPOSITORY_PROPERTY = "repository";

    private final static boolean REPOSITORY_REQUIRED = false;

    private final static String REPOSITORY_ID_COLUMN = "idRepository";

    @JoinColumn(name = REPOSITORY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_repository")
    private Repository repository;

    @Required(value = REPOSITORY_REQUIRED)
    public Repository getRepository()
    {
        return repository;
    }

    public void setRepository(final Repository repository)
    {
        this.repository = repository;
    }

    public final static String CHEF_PROPERTY = "chefEnabled";

    public final static String CHEF_COLUMN = "chefEnabled";

    private final static boolean CHEF_REQUIRED = true;

    @Column(name = CHEF_COLUMN, nullable = false)
    private boolean chefEnabled = false;

    @Required(value = CHEF_REQUIRED)
    public boolean isChefEnabled()
    {
        return chefEnabled;
    }

    public void setChefEnabled(final boolean chefEnabled)
    {
        this.chefEnabled = chefEnabled;
    }

    public final static String CREATION_DATE_PROPERTY = "creationDate";

    private final static boolean CREATION_DATE_REQUIRED = false;

    private final static String CREATION_DATE_COLUMN = "creation_date";

    @Column(name = CREATION_DATE_COLUMN, nullable = !CREATION_DATE_REQUIRED)
    private Date creationDate;

    @Required(value = CREATION_DATE_REQUIRED)
    public Date getCreationDate()
    {
        return creationDate;
    }

    public final static String CREATION_USER_PROPERTY = "creationUser";

    private final static boolean CREATION_USER_REQUIRED = true;

    /* package */final static int CREATION_USER_LENGTH_MIN = 0;

    /* package */final static int CREATION_USER_LENGTH_MAX = 128;

    private final static boolean CREATION_USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CREATION_USER_COLUMN = "creation_user";

    @Column(name = CREATION_USER_COLUMN, nullable = !CREATION_USER_REQUIRED, length = CREATION_USER_LENGTH_MAX)
    private String creationUser;

    @Required(value = CREATION_USER_REQUIRED)
    @Length(min = CREATION_USER_LENGTH_MIN, max = CREATION_USER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = CREATION_USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getCreationUser()
    {
        return creationUser;
    }

    public void setCreationUser(final String creationUser)
    {
        this.creationUser = creationUser;
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

    // Creation date does not have a setter, since it will be auto generated.

    /*          *********************** Helper methods ************************* */

    public void setRequirements(final int cpu, final int ram, final long hd)
    {
        setCpuRequired(cpu);
        setRamRequired(ram);
        setHdRequiredInBytes(hd);
    }

    public boolean isManaged()
    {
        return getRepository() != null || isStateful();
    }

    public boolean isMaster()
    {
        return getMaster() == null;
    }

    public String getFileRef()
    {
        return getName() + "." + getId();
    }

    public String getNotManagedBundlePath()
    {
        return getEnterprise().getId() + "/bundle/" + getName();
    }

    public String getNotManagedBundleName()
    {
        String name = getName() + "-snapshot";
        if (isVhd(getPath()))
        {
            name = vhdPath(name);
        }
        return name;
    }

    private boolean isVhd(final String path)
    {
        return path.endsWith(".vhd");
    }

    private String vhdPath(final String path)
    {
        return path + ".vhd";
    }

    public final static String CONVERSIONS_PROPERTY = "conversions";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "virtualMachineTemplate")
    private final List<VirtualImageConversion> conversions =
        new ArrayList<VirtualImageConversion>();

    protected void addConversion(final VirtualImageConversion conversion)
    {
        conversions.add(conversion);
    }

}
