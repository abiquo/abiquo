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

package com.abiquo.server.core.common;

public class DefaultEntityCurrentUsed
{
    private final int cpu;

    private final long ramInMb;

    private final long hdInMb;

    private long storage;

    private long publicIp;

    private long vlanCount;

    public DefaultEntityCurrentUsed(int cpu, long ramInMb, long hdInMb)
    {
        super();
        this.cpu = cpu;
        this.ramInMb = ramInMb;
        this.hdInMb = hdInMb;
    }

    public int getCpu()
    {
        return cpu;
    }

    public long getRamInMb()
    {
        return ramInMb;
    }

    public long getHdInMb()
    {
        return hdInMb;
    }

    //

    public void setStorage(long storage)
    {
        this.storage = storage;
    }

    public long getStorage()
    {
        return storage;
    }

    public long getPublicIp()
    {
        return publicIp;
    }

    public void setPublicIp(long publicIp)
    {
        this.publicIp = publicIp;
    }

    public long getVlanCount()
    {
        return vlanCount;
    }

    public void setVlanCount(long vlanCount)
    {
        this.vlanCount = vlanCount;
    }
}
