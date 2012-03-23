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
