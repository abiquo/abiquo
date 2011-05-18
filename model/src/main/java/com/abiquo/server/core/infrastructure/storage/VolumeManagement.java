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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.model.validation.IscsiPath;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VolumeManagement.TABLE_NAME)
@DiscriminatorValue(VolumeManagement.DISCRIMINATOR)
@NamedQueries( {@NamedQuery(name = "VOLUMES_BY_VDC", query = VolumeManagement.BY_VDC)})
public class VolumeManagement extends RasdManagement
{
    public static final String DISCRIMINATOR = "8";

    public static final String ALLOCATION_UNITS = "MegaBytes";

    public static final String TABLE_NAME = "volume_management";

    // Queries

    public static final String BY_VDC =
        "SELECT vol FROM VolumeManagement vol " + "LEFT JOIN vol.virtualMachine vm "
            + "LEFT JOIN vol.virtualAppliance vapp " + "WHERE vol.virtualDatacenter.id = :vdcId "
            + "AND (" + "vol.rasd.elementName like :filterLike " + "OR vm.name like :filterLike "
            + "OR vapp.name like :filterLike " + "OR vol.virtualDatacenter.name like :filterLike "
            + "OR vol.storagePool.tier.name like :filterLike " + ")";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected VolumeManagement()
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

        // TODO: Remove these fields?
        setUsedSizeInMB(0);
        setAvailableSizeInMB(sizeInMB);
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

    public final static String VIRTUAL_IMAGE_PROPERTY = "virtualImage";

    private final static boolean VIRTUAL_IMAGE_REQUIRED = false;

    private final static String VIRTUAL_IMAGE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_IMAGE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualImage")
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

    public boolean isStateful()
    {
        return virtualImage != null;
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

    private void setIdScsi(final String idScsi)
    {
        this.idScsi = idScsi;
        getRasd().setConnection(idScsi);
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
    private void setState(final VolumeState state)
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

    public void associate()
    {
        if (state != VolumeState.DETACHED)
        {
            throw new IllegalStateException("Volume should be in state "
                + VolumeState.DETACHED.name());
        }

        setState(VolumeState.ATTACHED);
    }

    public void disassociate()
    {
        if (state != VolumeState.ATTACHED)
        {
            throw new IllegalStateException("Volume should be in state "
                + VolumeState.ATTACHED.name());
        }

        setState(VolumeState.DETACHED);
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
            "vdcname", "vol.virtualDatacenter.name"), VIRTUALMACHINE("vmname",
            "vol.virtualMachine.name"), VIRTUALAPPLIANCE("vaname", "vapp.name"), TIER("tier",
            "vol.storagePool.tier.name"), TOTALSIZE("size", "vol.rasd.limit"), AVAILABLESIZE(
            "available", "vol.rasd.reservation"), USEDSIZE("used", "vol.usedSizeInMB"), STATE(
            "state", "vol.state");

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
