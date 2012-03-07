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

package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.model.validation.IscsiPath;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VolumeManagement.TABLE_NAME)
@FilterDefs({@FilterDef(name = VolumeManagement.NOT_TEMP),
@FilterDef(name = VolumeManagement.ONLY_TEMP)})
@Filters({@Filter(name = VolumeManagement.NOT_TEMP, condition = "temporal is null"),
@Filter(name = VolumeManagement.ONLY_TEMP, condition = "temporal is not null")})
@DiscriminatorValue(VolumeManagement.DISCRIMINATOR)
@NamedQueries({
@NamedQuery(name = VolumeManagement.VOLUMES_ATTACHED_TO_VM, query = VolumeManagement.ATTACHED_TO_VM),
@NamedQuery(name = VolumeManagement.VOLUMES_AVAILABLES, query = VolumeManagement.AVAILABLES),
@NamedQuery(name = VolumeManagement.VOLUMES_BY_VDC, query = VolumeManagement.BY_VDC),
@NamedQuery(name = VolumeManagement.VOLUMES_BY_POOL, query = VolumeManagement.BY_POOL)})
public class VolumeManagement extends RasdManagement
{
    public static final String DISCRIMINATOR = "8"; // CIMResourceTypeEnum.iSCSI_HBA

    public static final String ALLOCATION_UNITS = "MegaBytes";

    public static final String TABLE_NAME = "volume_management";

    // Queries

    public static final String VOLUMES_BY_VDC = "VOLUMES_BY_VDC";

    public static final String VOLUMES_BY_POOL = "VOLUMES_BY_POOL";

    public static final String VOLUMES_ATTACHED_TO_VM = "VOLUMES_ATTACHED_TO_VM";

    public static final String VOLUMES_AVAILABLES = "VOLUMES_AVAILABLES";

    public static final String NOT_TEMP = "volumemanagement_not_temp";

    public static final String ONLY_TEMP = "volumemanagement_only_temp";

    public static final String BY_VDC =
        "SELECT vol FROM VolumeManagement vol LEFT JOIN vol.virtualMachine vm "
            + "LEFT JOIN vol.virtualAppliance vapp WHERE vol.virtualDatacenter.id = :vdcId "
            + "AND (vol.rasd.elementName like :filterLike OR vm.name like :filterLike "
            + "OR vapp.name like :filterLike OR vol.virtualDatacenter.name like :filterLike "
            + "OR vol.storagePool.tier.name like :filterLike )";

    public static final String BY_POOL =
        "SELECT vol FROM VolumeManagement vol LEFT JOIN vol.virtualMachine vm "
            + "LEFT JOIN vol.virtualAppliance vapp WHERE vol.storagePool.idStorage = :poolId "
            + "AND (vol.rasd.elementName like :filterLike "
            + "OR vol.rasd.id like :filterLike OR vm.name like :filterLike "
            + "OR vapp.name like :filterLike OR vol.virtualDatacenter.name like :filterLike "
            + "OR vol.storagePool.tier.name like :filterLike )";

    public static final String ATTACHED_TO_VM =
        "SELECT vol FROM VolumeManagement vol LEFT JOIN vol.virtualMachine vm "
            + "LEFT JOIN vol.virtualAppliance vapp "
            + "WHERE vm.id = :vmId AND vol.state = :state "
            + "AND (vol.rasd.elementName like :filterLike " + "OR vm.name like :filterLike "
            + "OR vapp.name like :filterLike " + "OR vol.virtualDatacenter.name like :filterLike "
            + "OR vol.storagePool.tier.name like :filterLike)"
            + " AND vol.virtualMachineTemplate IS NULL";

    public static final String AVAILABLES =
        "SELECT vol FROM VolumeManagement vol LEFT JOIN vol.virtualMachine vm "
            + "WHERE vol.virtualDatacenter.id = :vdcId AND vm IS NULL "
            + "AND vol.virtualMachineTemplate IS NULL AND vol.rasd.elementName like :filterLike "
            + "AND vol NOT IN (SELECT stateful.volume FROM DiskStatefulConversion stateful)";

    public static final String BY_VAPP =
        "SELECT vol FROM VolumeManagement vol LEFT JOIN vol.virtualMachine vm "
            + "LEFT JOIN vol.virtualAppliance vapp WHERE vapp.id = :vappId";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public VolumeManagement()
    {
        // Just for JPA support
    }

    public VolumeManagement(final String uuid, final String name, final long sizeInMB,
        final String idScsi, final StoragePool pool, final VirtualDatacenter virtualDatacenter)
    {
        super(DISCRIMINATOR);

        // RasdManagement properties
        Rasd rasd = new Rasd(uuid, name, Integer.valueOf(DISCRIMINATOR));
        rasd.setAddress(pool.getDevice().getIscsiIp());
        rasd.setAllocationUnits(ALLOCATION_UNITS);
        rasd.setAutomaticAllocation(0);
        rasd.setAutomaticDeallocation(0);

        setRasd(rasd);
        setVirtualDatacenter(virtualDatacenter);

        // Volume properties
        setStoragePool(pool);
        setIdScsi(idScsi);
        setState(VolumeState.DETACHED);
        setSizeInMB(sizeInMB);
    }

    public final static String STORAGE_POOL_PROPERTY = "storagePool";

    private final static boolean STORAGE_POOL_REQUIRED = true;

    private final static String STORAGE_POOL_ID_COLUMN = "idStorage";

    @JoinColumn(name = STORAGE_POOL_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_storagePool")
    private StoragePool storagePool;

    @Required(value = STORAGE_POOL_REQUIRED)
    public StoragePool getStoragePool()
    {
        return this.storagePool;
    }

    public void setStoragePool(final StoragePool storagePool)
    {
        this.storagePool = storagePool;
        getRasd().setPoolId(storagePool.getId());
    }

    public final static String VIRTUAL_MACHINE_TEMPLATE_PROPERTY = "virtualMachineTemplate";

    private final static boolean VIRTUAL_MACHINE_TEMPLATE_REQUIRED = false;

    private final static String VIRTUAL_MACHINE_TEMPLATE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_MACHINE_TEMPLATE_ID_COLUMN)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualImage")
    private VirtualMachineTemplate virtualMachineTemplate;

    @Required(value = VIRTUAL_MACHINE_TEMPLATE_REQUIRED)
    public VirtualMachineTemplate getVirtualMachineTemplate()
    {
        return this.virtualMachineTemplate;
    }

    public void setVirtualMachineTemplate(final VirtualMachineTemplate virtualMachineTemplate)
    {
        this.virtualMachineTemplate = virtualMachineTemplate;

        if (virtualMachineTemplate != null)
        {
            this.virtualMachineTemplate.setStateful(true);
            this.virtualMachineTemplate.setPath(getIdScsi());
        }
    }

    public boolean isStateful()
    {
        return virtualMachineTemplate != null;
    }

    public final static String ID_SCSI_PROPERTY = "idScsi";

    private final static boolean ID_SCSI_REQUIRED = false;

    public final static int ID_SCSI_LENGTH_MIN = 0;

    public final static int ID_SCSI_LENGTH_MAX = 255;

    private final static boolean ID_SCSI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ID_SCSI_COLUMN = "idSCSI";

    @Column(name = ID_SCSI_COLUMN, nullable = !ID_SCSI_REQUIRED, length = ID_SCSI_LENGTH_MAX)
    private String idScsi;

    @Required(value = ID_SCSI_REQUIRED)
    @Length(min = ID_SCSI_LENGTH_MIN, max = ID_SCSI_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_SCSI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @IscsiPath
    public String getIdScsi()
    {
        return this.idScsi;
    }

    public void setIdScsi(final String idScsi)
    {
        this.idScsi = idScsi;
        getRasd().setConnection(idScsi);
        if (isStateful())
        {
            // If the volume is stateful update the virtual machine template too
            this.virtualMachineTemplate.setPath(idScsi);
        }
    }

    public final static String STATE_PROPERTY = "state";

    private final static String STATE_COLUMN = "state";

    @Enumerated(value = javax.persistence.EnumType.ORDINAL)
    @Column(name = STATE_COLUMN, nullable = true)
    private VolumeState state;

    public VolumeState getState()
    {
        return this.state;
    }

    // Must not be used. Use the state change methods
    public void setState(final VolumeState state)
    {
        this.state = state;
    }

    public final static String USED_SIZE_PROPERTY = "usedSizeInMB";

    private final static String USED_SIZE_COLUMN = "usedSize";

    private final static long USED_SIZE_MIN = Long.MIN_VALUE;

    private final static long USED_SIZE_MAX = Long.MAX_VALUE;

    @Column(name = USED_SIZE_COLUMN, nullable = true)
    @Range(min = USED_SIZE_MIN, max = USED_SIZE_MAX)
    private long usedSizeInMB;

    public long getUsedSizeInMB()
    {
        return this.usedSizeInMB;
    }

    public void setUsedSizeInMB(final long usedSizeInMB)
    {
        this.usedSizeInMB = usedSizeInMB < 0 ? 0L : usedSizeInMB;
    }

    // **************************** Rasd delegating methods ***************************

    public String getUuid()
    {
        return getRasd().getId();
    }

    public String getName()
    {
        return getRasd().getElementName();
    }

    public void setName(final String name)
    {
        getRasd().setElementName(name);
    }

    public long getSizeInMB()
    {
        Long size = getRasd().getLimit();
        return size == null ? 0L : size;
    }

    public void setSizeInMB(final long sizeInMB)
    {
        getRasd().setLimit(sizeInMB < 0 ? 0L : sizeInMB);
    }

    public long getAvailableSizeInMB()
    {
        Long reservation = getRasd().getReservation();
        return reservation == null ? 0L : reservation;
    }

    public void setAvailableSizeInMB(final long availableSizeInMB)
    {
        getRasd().setReservation(availableSizeInMB < 0 ? 0L : availableSizeInMB);
    }

    // ********************************** Volume state transitions ********************************

    @Override
    public void attach(final int sequence, final VirtualMachine vm)
    {
        if (state != VolumeState.DETACHED)
        {
            throw new IllegalStateException("Volume should be in " + VolumeState.DETACHED.name()
                + " state");
        }

        if (vm == null)
        {
            throw new IllegalStateException("Virtual machine can not be null");
        }

        setSequence(sequence);
        setVirtualMachine(vm);
        setState(VolumeState.ATTACHED);
    }

    @Override
    public void detach()
    {
        if (state != VolumeState.ATTACHED)
        {
            throw new IllegalStateException("Volume should be in " + VolumeState.ATTACHED.name()
                + " state");
        }

        getRasd().setGeneration(null);
        setVirtualMachine(null);
        setVirtualAppliance(null);
        setState(VolumeState.DETACHED);
    }

    @Override
    public boolean isAttached()
    {
        return state == VolumeState.ATTACHED && getVirtualMachine() != null;
    }

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static enum OrderByEnum
    {
        NAME("elementname", "vol.rasd.elementName"), ID("idman", "vol.id"), VIRTUALDATACENTER(
            "vdcname", "vol.virtualDatacenter.name"), VIRTUALMACHINE("vmname", "vm.name"), VIRTUALAPPLIANCE(
            "vaname", "vapp.name"), TIER("tier", "vol.storagePool.tier.name"), TOTALSIZE("size",
            "vol.rasd.limit"), AVAILABLESIZE("available", "vol.rasd.reservation"), USEDSIZE("used",
            "vol.usedSizeInMB"), STATE("state", "vol.state");

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
