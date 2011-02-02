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

package com.abiquo.virtualfactory.model.config;

/**
 * Main configuration class
 * 
 * @author pnavarro
 */
public class Configuration
{
    /**
     * Vmware hypervisor configuration
     */
    private VmwareHypervisorConfiguration vmwareHyperConfig;

    /** HyperV hypervisor configuration. */
    private HyperVHypervisorConfiguration hypervConfig;

    /** HyperV hypervisor configuration. */
    private XenServerHypervisorConfiguration xenServerConfig;

    private String bridgePrefix;

    private long rimpTimeout;
    
    /** Determine if the LibVirt machine will support full virtualization.*/
    private boolean isFullVirt;
    

    public void setRimpTimeout(final long rimpTimpeout)
    {
        this.rimpTimeout = rimpTimpeout;
    }

    public long getRimpTimeout()
    {
        return rimpTimeout;
    }

    /**
     * NAS remote path
     */
    // private String remotePath;

    /**
     * NAS remote hot
     */
    // private String remoteHost;

    /**
     * Gets the vmware hypervisor configuration
     * 
     * @return the vmwareHyperConfig
     */
    public VmwareHypervisorConfiguration getVmwareHyperConfig()
    {
        return vmwareHyperConfig;
    }

    /**
     * Sets the vmware hypervisor configuration
     * 
     * @param vmwareHyperConfig the vmwareHyperConfig to set
     */
    public void setVmwareHyperConfig(final VmwareHypervisorConfiguration vmwareHyperConfig)
    {
        this.vmwareHyperConfig = vmwareHyperConfig;
    }

    /**
     * Gets the hypervConfig
     * 
     * @return the hypervConfig
     */
    public HyperVHypervisorConfiguration getHypervConfig()
    {
        return hypervConfig;
    }

    /**
     * Sets the hypervConfig
     * 
     * @param hypervConfig the hypervConfig to set
     */
    public void setHypervConfig(final HyperVHypervisorConfiguration hypervConfig)
    {
        this.hypervConfig = hypervConfig;
    }

    /**
     * Gets the xenServerConfig.
     * 
     * @return the xenServerConfig
     */
    public XenServerHypervisorConfiguration getXenServerConfig()
    {
        return xenServerConfig;
    }

    /**
     * Sets the xenServerConfig.
     * 
     * @param xenServerConfig the xenServerConfig to set
     */
    public void setXenServerConfig(final XenServerHypervisorConfiguration xenServerConfig)
    {
        this.xenServerConfig = xenServerConfig;
    }

    /**
     * Gets the bridge prefix
     * 
     * @return the bridgePrefix
     */
    public String getBridgePrefix()
    {
        return bridgePrefix;
    }

    /**
     * Sets the bridge previx
     * 
     * @param bridgePrefix the new bridge prefix
     */
    public void setBridgePrefix(String bridgePrefix)
    {
        this.bridgePrefix = bridgePrefix;

    }

    public boolean isFullVirt()
    {
        return isFullVirt;
    }

    public void setFullVirt(boolean isFullVirt)
    {
        this.isFullVirt = isFullVirt;
    }

    /**
     * Gest the NAS remote Path
     * 
     * @return the remotePath public String getRemotePath() { return remotePath; } /** Sets the NAS
     *         remote path
     * @param remotePath the remotePath to set public void setRemotePath(String remotePath) {
     *            this.remotePath = remotePath; } /** Gets the NAS remote host
     * @return the remotehost public String getRemoteHost() { return remoteHost; } /** Sets the NAS
     *         remote host
     * @param remotehost the remotehost to set public void setRemoteHost(String remotehost) {
     *            this.remoteHost = remotehost; }
     */

}
