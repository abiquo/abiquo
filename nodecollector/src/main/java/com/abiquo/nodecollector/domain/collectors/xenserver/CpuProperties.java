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

/**
 * 
 */
package com.abiquo.nodecollector.domain.collectors.xenserver;

import java.util.Arrays;
import java.util.Map;

import com.xensource.xenapi.Host;

/**
 * @author admin
 */
public class CpuProperties
{

    /**
     * Static method to return the cpu properties object.
     * 
     * @param host the {@link Host.Record} entity.
     * @return a new instance of CpuProperties class.
     */
    public static CpuProperties of(final Host.Record host)
    {
        return new CpuProperties(host.cpuInfo);
    }

    // properties of the cpu info.
    private Map<String, String> cpuInfo;

    private CpuProperties(final Map<String, String> cpuInfo)
    {
        this.cpuInfo = cpuInfo;
    }

    /**
     * @return the real number of cores.
     */
    public Long getRealCores()
    {
        Long cpuCount = getCpuCount();
        if (hasHyperThreading())
        {
            cpuCount /= 2;
        }
        return cpuCount;
    }

    /**
     * @return the logical count of cores.
     */
    private Long getCpuCount()
    {
        return Long.valueOf(cpuInfo.get("cpu_count"));
    }

    /**
     * @return the flags value in the map.
     */
    private String getFlags()
    {
        return cpuInfo.get("flags");
    }

    /**
     * Inspect the flags to know if the flag for hyperthreading is present.
     * 
     * @return if the cpu has hyperthreading.
     */
    private boolean hasHyperThreading()
    {
        return Arrays.asList(getFlags().split("\\s+")).contains("ht");
    }

}
