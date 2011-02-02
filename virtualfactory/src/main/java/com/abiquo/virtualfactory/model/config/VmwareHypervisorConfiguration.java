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
 * Class representing a vmware hypervisor configuration
 * 
 * @author pnavarro
 */
public class VmwareHypervisorConfiguration
{
    /**
     * Datacenter name
     */
    private String datacenterName;

    /**
     * This flag represents if the hypervisor server certificate should be ignored when connecting
     * to the hypervisor
     */
    private Boolean ignorecert;

    /**
     * SAN Datastore name
     */
    private String datastoreSanName;

    /**
     * Gets the datacenter name
     * 
     * @return the datacenterName
     */
    public String getDatacenterName()
    {
        return datacenterName;
    }

    /**
     * Sets the datacenter name
     * 
     * @param datacenterName the datacenterName to set
     */
    public void setDatacenterName(final String datacenterName)
    {
        this.datacenterName = datacenterName;
    }

    /**
     * Gets the ignorecert flag
     * 
     * @return the ignorecert flag
     */
    public Boolean getIgnorecert()
    {
        return ignorecert;
    }

    /**
     * Sets the ignorecert flag
     * 
     * @param ignorecert the ignorecert to set
     */
    public void setIgnorecert(final Boolean ignorecert)
    {
        this.ignorecert = ignorecert;
    }

    /**
     * Gets the SAN datastore name
     * 
     * @return the datastore Name
     */
    public String getDatastoreSanName()
    {
        return datastoreSanName;
    }

    /**
     * Sets the SAN datastore name
     * 
     * @param datastoreName the datastore Name to set
     */
    public void setDatastoreSanName(final String datastoreName)
    {
        this.datastoreSanName = datastoreName;
    }
}
