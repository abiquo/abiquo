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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VolumeManagement.TABLE_NAME)
@DiscriminatorValue(VolumeManagement.DISCRIMINATOR)
public class VolumeManagement extends RasdManagement
{
    public static final String DISCRIMINATOR = "8";

    public static final String TABLE_NAME = "volume_management";

    public VolumeManagement(final StoragePool storagePool, final VirtualImage virtualImage,
        final String idScsi)
    {
        super(DISCRIMINATOR); // TODO use RASD enumerated type
        setStoragePool(storagePool);
        setVirtualImage(virtualImage);
        setIdScsi(idScsi);
    }

    protected VolumeManagement()
    {
        super();
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
    }

    public final static String VIRTUAL_IMAGE_PROPERTY = "virtualImage";

    private final static boolean VIRTUAL_IMAGE_REQUIRED = true;

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

    public final static String ID_SCSI_PROPERTY = "idScsi";

    private final static boolean ID_SCSI_REQUIRED = false;

    private final static int ID_SCSI_LENGTH_MIN = 0;

    private final static int ID_SCSI_LENGTH_MAX = 255;

    private final static boolean ID_SCSI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ID_SCSI_COLUMN = "idSCSI";

    @Column(name = ID_SCSI_COLUMN, nullable = !ID_SCSI_REQUIRED, length = ID_SCSI_LENGTH_MAX)
    private String idScsi;

    @Required(value = ID_SCSI_REQUIRED)
    @Length(min = ID_SCSI_LENGTH_MIN, max = ID_SCSI_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_SCSI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIdScsi()
    {
        return this.idScsi;
    }

    private void setIdScsi(final String idScsi)
    {
        this.idScsi = idScsi;
    }

    public final static String STATE_PROPERTY = "state";

    private final static String STATE_COLUMN = "state";

    private final static int STATE_MIN = Integer.MIN_VALUE;

    private final static int STATE_MAX = Integer.MAX_VALUE;

    @Column(name = STATE_COLUMN, nullable = true)
    @Range(min = STATE_MIN, max = STATE_MAX)
    private int state;

    public int getState()
    {
        return this.state;
    }

    private void setState(final int state)
    {
        this.state = state;
    }

    public final static String USED_SIZE_PROPERTY = "usedSize";

    private final static String USED_SIZE_COLUMN = "usedSize";

    private final static long USED_SIZE_MIN = Long.MIN_VALUE;

    private final static long USED_SIZE_MAX = Long.MAX_VALUE;

    @Column(name = USED_SIZE_COLUMN, nullable = true)
    @Range(min = USED_SIZE_MIN, max = USED_SIZE_MAX)
    private long usedSize;

    public long getUsedSize()
    {
        return this.usedSize;
    }

    private void setUsedSize(final long usedSize)
    {
        this.usedSize = usedSize;
    }
    
    public static enum OrderByEnum
    {
        NAME, 
        ID, 
        VIRTUALDATACENTER, 
        VIRTUALMACHINE, 
        VIRTUALAPPLIANCE,
        TIER,
        TOTALSIZE,
        AVAILABLESIZE,
        USEDSIZE;

        public static OrderByEnum fromValue(String orderBy)
        {
            for(OrderByEnum currentOrder : OrderByEnum.values())
            {
                if (currentOrder.name().equalsIgnoreCase(orderBy))
                {
                    return currentOrder;
                }
            }
            
            return null;
        }        
    }
}
