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

package com.abiquo.server.core.statistics;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "vappResources")
public class VirtualAppResourcesDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String vdcName;

    public String getVdcName()
    {
        return vdcName;
    }

    public void setVdcName(String vdcName)
    {
        this.vdcName = vdcName;
    }

    private String vappName;

    public String getVappName()
    {
        return vappName;
    }

    public void setVappName(String vappName)
    {
        this.vappName = vappName;
    }

    private int volAttached;

    public int getVolAttached()
    {
        return volAttached;
    }

    public void setVolAttached(int volAttached)
    {
        this.volAttached = volAttached;
    }

    private int vmCreated;

    public int getVmCreated()
    {
        return vmCreated;
    }

    public void setVmCreated(int vmCreated)
    {
        this.vmCreated = vmCreated;
    }

    private int volAssociated;

    public int getVolAssociated()
    {
        return volAssociated;
    }

    public void setVolAssociated(int volAssociated)
    {
        this.volAssociated = volAssociated;
    }

    private int vmActive;

    public int getVmActive()
    {
        return vmActive;
    }

    public void setVmActive(int vmActive)
    {
        this.vmActive = vmActive;
    }

    private int idEnterprise;

    public int getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }
    
    private int idVirtualDataCenter;

    public int getIdVirtualDataCenter()
    {
        return idVirtualDataCenter;
    }

    public void setIdVirtualDataCenter(int idVirtualDataCenter)
    {
        this.idVirtualDataCenter = idVirtualDataCenter;
    }

}
