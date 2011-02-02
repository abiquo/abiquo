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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Encapsulates information returned by the Storage System Manager module.
 */
@XmlRootElement(name = "storageSystemInfo")
public class StorageSystemInfoDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 3860550980811162094L;

    private long size;

    private long used;

    private long available;

    private List<String> volumes;

    private List<String> pools;

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public long getUsed()
    {
        return used;
    }

    public void setUsed(long used)
    {
        this.used = used;
    }

    public long getAvailable()
    {
        return available;
    }

    public void setAvailable(long available)
    {
        this.available = available;
    }

    @XmlElementWrapper(name = "volumes")
    @XmlElement(name = "volume")
    public List<String> getVolumes()
    {
        return volumes;
    }

    public void setVolumes(List<String> volumes)
    {
        this.volumes = volumes;
    }

    @XmlElementWrapper(name = "pools")
    @XmlElement(name = "pool")
    public List<String> getPools()
    {
        return pools;
    }

    public void setPools(List<String> pools)
    {
        this.pools = pools;
    }

}
