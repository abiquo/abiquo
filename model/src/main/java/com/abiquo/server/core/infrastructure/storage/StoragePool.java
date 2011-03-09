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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.GenericEnityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = StoragePool.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = StoragePool.TABLE_NAME)
public class StoragePool extends GenericEnityBase<String>
{
    public static final String TABLE_NAME = "storage_pool";

    protected StoragePool()
    {
    }
    
    public StoragePool(String id, String name, Long availableSize, Long totalSize, Long usedSize, StorageDevice device, Tier tier)
    {
        this.setIdStorage(id);
        this.setName(name);
        this.setAvailableSizeInMb(availableSize);
        this.setTotalSizeInMb(totalSize);
        this.setUsedSizeInMb(usedSize);
        this.setDevice(device);
        this.setTier(tier);
    }

    public final static String ID_STORAGE_PROPERTY = "idStorage";

    private final static boolean ID_STORAGE_REQUIRED = true;

    private final static int ID_STORAGE_LENGTH_MIN = 0;

    private final static int ID_STORAGE_LENGTH_MAX = 255;

    private final static boolean ID_STORAGE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ID_STORAGE_COLUMN = "idStorage";

    @Id
    @Column(name = ID_STORAGE_COLUMN, nullable = !ID_STORAGE_REQUIRED, length = ID_STORAGE_LENGTH_MAX)
    private String idStorage;

    @Required(value = ID_STORAGE_REQUIRED)
    @Length(min = ID_STORAGE_LENGTH_MIN, max = ID_STORAGE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_STORAGE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIdStorage()
    {
        return this.idStorage;
    }

    public void setIdStorage(String idStorage)
    {
        this.idStorage = idStorage;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

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

    public void setName(String name)
    {
        this.name = name;
    }

    public final static String TOTAL_SIZE_IN_MB_PROPERTY = "totalSizeInMb";

    private final static boolean TOTAL_SIZE_IN_MB_REQUIRED = true;

    private final static String TOTAL_SIZE_IN_MB_COLUMN = "totalSizeInMb";

    private final static long TOTAL_SIZE_IN_MB_MIN = 0L;

    private final static long TOTAL_SIZE_IN_MB_MAX = Long.MAX_VALUE;

    @Column(name = TOTAL_SIZE_IN_MB_COLUMN, nullable = !TOTAL_SIZE_IN_MB_REQUIRED)
    @Range(min = TOTAL_SIZE_IN_MB_MIN, max = TOTAL_SIZE_IN_MB_MAX)
    private long totalSizeInMb;

    public long getTotalSizeInMb()
    {
        return this.totalSizeInMb;
    }

    public void setTotalSizeInMb(long totalSizeInMb)
    {
        this.totalSizeInMb = totalSizeInMb;
    }

    public final static String DEVICE_PROPERTY = "device";

    private final static boolean DEVICE_REQUIRED = true;

    private final static String DEVICE_ID_COLUMN = "idStorageDevice";

    @JoinColumn(name = DEVICE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_device")
    private StorageDevice device;

    @Required(value = DEVICE_REQUIRED)
    public StorageDevice getDevice()
    {
        return this.device;
    }

    public void setDevice(StorageDevice device)
    {
        this.device = device;
    }

    public final static String USED_SIZE_IN_MB_PROPERTY = "usedSizeInMb";

    private final static boolean USED_SIZE_IN_MB_REQUIRED = true;

    private final static String USED_SIZE_IN_MB_COLUMN = "usedSizeInMb";

    private final static long USED_SIZE_IN_MB_MIN = 0L;

    private final static long USED_SIZE_IN_MB_MAX = Long.MAX_VALUE;

    @Column(name = USED_SIZE_IN_MB_COLUMN, nullable = !USED_SIZE_IN_MB_REQUIRED)
    @Range(min = USED_SIZE_IN_MB_MIN, max = USED_SIZE_IN_MB_MAX)
    private long usedSizeInMb;

    public long getUsedSizeInMb()
    {
        return this.usedSizeInMb;
    }

    public void setUsedSizeInMb(long usedSizeInMb)
    {
        this.usedSizeInMb = usedSizeInMb;
    }

    public final static String AVAILABLE_SIZE_IN_MB_PROPERTY = "availableSizeInMb";

    private final static boolean AVAILABLE_SIZE_IN_MB_REQUIRED = true;

    private final static String AVAILABLE_SIZE_IN_MB_COLUMN = "availableSizeInMb";

    private final static long AVAILABLE_SIZE_IN_MB_MIN = 0L;

    private final static long AVAILABLE_SIZE_IN_MB_MAX = Long.MAX_VALUE;

    @Column(name = AVAILABLE_SIZE_IN_MB_COLUMN, nullable = !AVAILABLE_SIZE_IN_MB_REQUIRED)
    @Range(min = AVAILABLE_SIZE_IN_MB_MIN, max = AVAILABLE_SIZE_IN_MB_MAX)
    private long availableSizeInMb;

    public long getAvailableSizeInMb()
    {
        return this.availableSizeInMb;
    }

    public void setAvailableSizeInMb(long availableSizeInMb)
    {
        this.availableSizeInMb = availableSizeInMb;
    }

    public final static String TIER_PROPERTY = "tier";

    private final static boolean TIER_REQUIRED = true;

    private final static String TIER_ID_COLUMN = "idTier";

    @JoinColumn(name = TIER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_tier")
    private Tier tier;

    @Required(value = TIER_REQUIRED)
    public Tier getTier()
    {
        return this.tier;
    }

    public void setTier(Tier tier)
    {
        this.tier = tier;
    }

    public final static String ENABLED_PROPERTY = "enabled";

    private final static boolean ENABLED_REQUIRED = true;

    private final static String ENABLED_COLUMN = "isEnabled";

    @Column(name = ENABLED_COLUMN, nullable = !ENABLED_REQUIRED)
    private boolean enabled;

    @Required(value = ENABLED_REQUIRED)
    public boolean getEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String getId()
    {
        return this.getIdStorage();
    }

}
