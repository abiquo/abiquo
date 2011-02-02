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
package com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceManagement;

/**
 * @author abiquo
 */
public abstract class ResourceManagementHB implements Serializable, IPojoHB<ResourceManagement>
{

    /**
     * Generated serial number
     */
    private static final long serialVersionUID = -6078141406034651187L;

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
    private VirtualDataCenterHB virtualDataCenter;

    /**
     * Variable which corresponds with column 'idVM'
     */
    private VirtualmachineHB virtualMachine;

    /**
     * Variable which corresponds with column 'rasd'
     */
    private ResourceAllocationSettingData rasd;

    /**
     * Variable which corresponds with column 'idVirtualApp'
     */
    private VirtualappHB virtualApp;

    /**
     * @return the idManagement
     */
    public Integer getIdManagement()
    {
        return idManagement;
    }

    /**
     * @param idManagement the idVolume to set
     */
    public void setIdManagement(Integer idManagement)
    {
        this.idManagement = idManagement;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setIdResourceType(String idResourceType)
    {
        this.idResourceType = idResourceType;
    }

    /**
     * @return the resourceType
     */
    public String getIdResourceType()
    {
        return idResourceType;
    }

    /**
     * @return the virtualDataCenter
     */
    public VirtualDataCenterHB getVirtualDataCenter()
    {
        return virtualDataCenter;
    }

    /**
     * @param virtualDataCenter the virtualDataCenter to set
     */
    public void setVirtualDataCenter(VirtualDataCenterHB virtualDataCenter)
    {
        this.virtualDataCenter = virtualDataCenter;
    }

    /**
     * @return the idVM
     */
    public VirtualmachineHB getVirtualMachine()
    {
        return virtualMachine;
    }

    /**
     * @param idVM the idVM to set
     */
    public void setVirtualMachine(VirtualmachineHB virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    /**
     * @return the rasd
     */
    public ResourceAllocationSettingData getRasd()
    {
        return rasd;
    }

    /**
     * @param idResource the rasd to set
     */
    public void setRasd(ResourceAllocationSettingData rasd)
    {
        this.rasd = rasd;
    }

    /**
     * @return the idVirtualApp
     */
    public VirtualappHB getVirtualApp()
    {
        return virtualApp;
    }

    /**
     * @param idVirtualApp the idVirtualApp to set
     */
    public void setVirtualApp(VirtualappHB virtualApp)
    {
        this.virtualApp = virtualApp;
    }

    /**
     * @SuppressWarnings("unchecked")
     * @Override public VolumeManagement toPojo() { VolumeManagement volumeManagement = new
     *           VolumeManagement(); volumeManagement.setIdVolume(this.idVolume);
     *           volumeManagement.setIdSCSI(this.idSCSI);
     *           volumeManagement.setVirtualDataCenter((VirtualDataCenter)
     *           this.virtualDataCenter.toPojo());
     *           volumeManagement.setVirtualMachine((VirtualMachine) this.virtualMachine.toPojo());
     *           volumeManagement.setState(this.state);
     *           volumeManagement.setIdResource(this.rasd.getInstanceID());
     *           volumeManagement.setIdStorage(this.idStorage);
     *           volumeManagement.setVirtualAppliance((VirtualAppliance) this.virtualApp.toPojo());
     *           //Attributes obtained from the RASD
     *           volumeManagement.setName(this.rasd.getElementName());
     *           volumeManagement.setDescription(this.rasd.getDescription());
     *           volumeManagement.setMaxSize(this.rasd.getLimit());
     *           volumeManagement.setRequestedSize(this.rasd.getReservation()); //TODO: Find out
     *           which value in RASD corresponds to VolumeManagement usedSpace
     *           volumeManagement.setUsedSpace(Long.valueOf('0')); return volumeManagement; }
     */
    // @Override
    // public <T extends IPojo> T toPojo()
    // {
    // return null;
    // }
    /**
     * Checks if ResourceAllocationSettingData object related to this resourceManagement is coherent
     * to be deployed Method to be override for every rasd_management specification
     * 
     * @return true or false
     */
    public boolean checkResourceCoherency()
    {
        ResourceAllocationSettingData myRasd = getRasd();

        if (myRasd.getInstanceID() != null && myRasd.getElementName() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Changes all the variables needed to deallocate a resource. For standard resource, do nothing.
     */
    public abstract void deallocateResource();

}
