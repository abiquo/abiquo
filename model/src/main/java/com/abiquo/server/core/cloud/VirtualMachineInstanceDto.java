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

package com.abiquo.server.core.cloud;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * DTO for instance parameters of the virtual machine.
 * 
 * @author eruiz@abiquo.com
 */
@XmlRootElement(name = "virtualmachineinstance")
public class VirtualMachineInstanceDto extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = -372239566628574960L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.virtualmachineinstance+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    protected String instanceName;

    public String getInstanceName()
    {
        return instanceName;
    }

    public void setInstanceName(String snapshotName)
    {
        this.instanceName = snapshotName;
    }
    
    @Override
    public String getMediaType()
    {
        return VirtualMachineInstanceDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
