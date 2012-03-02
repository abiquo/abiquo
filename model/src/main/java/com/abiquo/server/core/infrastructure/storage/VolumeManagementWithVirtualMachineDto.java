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

import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualDatacenterWithDatacenterDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

@XmlRootElement(name = "volume")
public class VolumeManagementWithVirtualMachineDto extends VolumeManagementDto
{
    private static final long serialVersionUID = 1L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.iscsivolumewithvirtualmachine+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private VirtualMachineDto virtualMachine;

    private VirtualApplianceDto virtualAppliance;

    private VirtualDatacenterWithDatacenterDto virtualDatacenter;

    private StoragePoolWithDeviceDto storagePoolWithDevice;

    public void setVirtualMachine(final VirtualMachineDto virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public VirtualMachineDto getVirtualMachine()
    {
        return virtualMachine;
    }

    public void setVirtualDatacenterWithDatacenter(
        final VirtualDatacenterWithDatacenterDto virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public VirtualDatacenterWithDatacenterDto getVirtualDatacenterWithDatacenter()
    {
        return virtualDatacenter;
    }

    public void setStoragePoolWithDevice(final StoragePoolWithDeviceDto storagePoolWithDevice)
    {
        this.storagePoolWithDevice = storagePoolWithDevice;
    }

    public StoragePoolWithDeviceDto getStoragePoolWithDevice()
    {
        return storagePoolWithDevice;
    }

    public void setVirtualAppliance(final VirtualApplianceDto virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }

    public VirtualApplianceDto getVirtualAppliance()
    {
        return virtualAppliance;
    }
    
    @Override
    public String getMediaType()
    {
        return VolumeManagementWithVirtualMachineDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
