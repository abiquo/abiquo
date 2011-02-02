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

import org.apache.commons.configuration.ConfigurationException;

/**
 * Class to manage and load the configuration file
 * 
 * @author pnavarro
 */
public class ConfigurationManager
{
    /**
     * Main configuration file
     */
    protected Configuration configuration;

    public ConfigurationManager()
    {
        initConfiguration();
        loadConfiguration();
    }

    protected void initConfiguration()
    {
        setConfiguration(new Configuration());
    }

    /**
     * Loads the XML
     * 
     * @throws ConfigurationException
     */
    protected void loadConfiguration()
    {
        // VMWare
        VmwareHypervisorConfiguration vmwareHyperConfig = new VmwareHypervisorConfiguration();

        vmwareHyperConfig.setDatacenterName("ha-datacenter");
        vmwareHyperConfig.setIgnorecert(true);
        configuration.setVmwareHyperConfig(vmwareHyperConfig);

        // HyperV
        HyperVHypervisorConfiguration hypervConfig = new HyperVHypervisorConfiguration();
        hypervConfig.setDestinationRepositoryPath(System
            .getProperty("abiquo.virtualfactory.hyperv.repositoryLocation"));
        configuration.setHypervConfig(hypervConfig);

        // XenServer
        XenServerHypervisorConfiguration xenServerConfig = new XenServerHypervisorConfiguration();
        xenServerConfig.setAbiquoRepository(System
            .getProperty("abiquo.virtualfactory.xenserver.repositoryLocation"));
        configuration.setXenServerConfig(xenServerConfig);

        // KVM
        configuration.setFullVirt(Boolean.valueOf(System.getProperty(
            "abiquo.virtualfactory.kvm.fullVirt", "true").trim()));

        /*
         * /* Networking
         */

        configuration.setBridgePrefix(System.getProperty(
            "abiquo.virtualFactory.networking.bridgePrefix", "abiquo"));
    }

    /**
     * Gets the configuration
     * 
     * @return the configuration
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Sets the configuration
     * 
     * @param configuration the configuration to set
     */
    public void setConfiguration(final Configuration configuration)
    {
        this.configuration = configuration;
    }
}
