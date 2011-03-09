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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "storagePoolWithTier")
public class StoragePoolWithTierDto extends SingleResourceTransportDto
{
    
    private static final long serialVersionUID = 1L;
    public static final String MEDIA_TYPE = "application/storagepoolwithtierdto+xml";
    
    private String idStorage;

    public String getIdStorage()
    {
        return idStorage;
    }

    public void setIdStorage(String idStorage)
    {
        this.idStorage = idStorage;
    }
    
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private long totalSizeInMb;

    public long getTotalSizeInMb()
    {
        return totalSizeInMb;
    }

    public void setTotalSizeInMb(long totalSizeInMb)
    {
        this.totalSizeInMb = totalSizeInMb;
    }

    private long usedSizeInMb;

    public long getUsedSizeInMb()
    {
        return usedSizeInMb;
    }

    public void setUsedSizeInMb(long usedSizeInMb)
    {
        this.usedSizeInMb = usedSizeInMb;
    }

    private long availableSizeInMb;

    public long getAvailableSizeInMb()
    {
        return availableSizeInMb;
    }

    public void setAvailableSizeInMb(long availableSizeInMb)
    {
        this.availableSizeInMb = availableSizeInMb;
    }

    private boolean enabled;

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    private TierDto tier;
    
    public void setTier(TierDto tier)
    {
        this.tier = tier;
    }

    public TierDto getTier()
    {
        return tier;
    }


    
    
}
