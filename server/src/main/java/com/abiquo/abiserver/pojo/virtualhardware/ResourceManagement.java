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

package com.abiquo.abiserver.pojo.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * Common values of the resource management objects.
 * 
 * @author jdevesa@abiquo.com
 *
 */
public abstract class ResourceManagement implements Serializable, IPojo<ResourceManagementHB>
{
    /**
     * Default serial version.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Variable which corresponds with column 'idManagement'
     */
    private Integer idManagement;

    /**
     * Resource Management type
     */
    private String idResourceType;

    /**
     * Variable which corresponds with column 'idVirtualDataCenter'
     */
    private Integer virtualDatacenterId;
    private String virtualDatacenterName;

    /**
     * Variable which corresponds with column 'idVM'
     */
    private Integer virtualMachineId;
    private String virtualMachineName;

    /**
     * Variable which corresponds with column 'idVirtualApp'
     */
    private Integer virtualApplianceId;
    private String virtualApplianceName;
    /**
     * @return the idManagement
     */
    public Integer getIdManagement()
    {
        return idManagement;
    }
    /**
     * @param idManagement the idManagement to set
     */
    public void setIdManagement(Integer idManagement)
    {
        this.idManagement = idManagement;
    }
    /**
     * @return the idResourceType
     */
    public String getIdResourceType()
    {
        return idResourceType;
    }
    /**
     * @param idResourceType the idResourceType to set
     */
    public void setIdResourceType(String idResourceType)
    {
        this.idResourceType = idResourceType;
    }
    /**
     * @return the virtualDatacenterId
     */
    public Integer getVirtualDatacenterId()
    {
        return virtualDatacenterId;
    }
    /**
     * @param virtualDatacenterId the virtualDatacenterId to set
     */
    public void setVirtualDatacenterId(Integer virtualDatacenterId)
    {
        this.virtualDatacenterId = virtualDatacenterId;
    }
    /**
     * @return the virtualDatacenterName
     */
    public String getVirtualDatacenterName()
    {
        return virtualDatacenterName;
    }
    /**
     * @param virtualDatacenterName the virtualDatacenterName to set
     */
    public void setVirtualDatacenterName(String virtualDatacenterName)
    {
        this.virtualDatacenterName = virtualDatacenterName;
    }
    /**
     * @return the virtualMachineId
     */
    public Integer getVirtualMachineId()
    {
        return virtualMachineId;
    }
    /**
     * @param virtualMachineId the virtualMachineId to set
     */
    public void setVirtualMachineId(Integer virtualMachineId)
    {
        this.virtualMachineId = virtualMachineId;
    }
    /**
     * @return the virtualMachineName
     */
    public String getVirtualMachineName()
    {
        return virtualMachineName;
    }
    /**
     * @param virtualMachineName the virtualMachineName to set
     */
    public void setVirtualMachineName(String virtualMachineName)
    {
        this.virtualMachineName = virtualMachineName;
    }
    /**
     * @return the virtualApplianceId
     */
    public Integer getVirtualApplianceId()
    {
        return virtualApplianceId;
    }
    /**
     * @param virtualApplianceId the virtualApplianceId to set
     */
    public void setVirtualApplianceId(Integer virtualApplianceId)
    {
        this.virtualApplianceId = virtualApplianceId;
    }
    /**
     * @return the virtualApplianceName
     */
    public String getVirtualApplianceName()
    {
        return virtualApplianceName;
    }
    /**
     * @param virtualApplianceName the virtualApplianceName to set
     */
    public void setVirtualApplianceName(String virtualApplianceName)
    {
        this.virtualApplianceName = virtualApplianceName;
    }
    
    

}
