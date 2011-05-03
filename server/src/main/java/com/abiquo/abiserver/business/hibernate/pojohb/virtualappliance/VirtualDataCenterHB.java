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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * Virtual Data Center hibernate object. In abiCloud, an enterprise can have any, one or many
 * virtual data centers
 * 
 * @author xfernandez
 */
public class VirtualDataCenterHB implements java.io.Serializable, IPojoHB<VirtualDataCenter>
{

    /**
     * serial version UID object.
     */
    private static final long serialVersionUID = 4033491980148971926L;

    /**
     * identification of virtual Data Center.
     */
    private Integer idVirtualDataCenter;

    /**
     * Virtual data center name.
     */
    private String name;

    /**
     * The physical DataCenter
     */
    private int idDataCenter;

    /**
     * The enterprise to which the VirtualDataCenter belongs to
     */
    private EnterpriseHB enterpriseHB;

    /**
     * Hypervisor Type selected for the VDC
     */
    private HypervisorType hypervisorType;

    /**
     * The network of the virtual datacenter
     */
    private NetworkHB network;

    private ResourceAllocationLimitHB limits;

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Integer getIdVirtualDataCenter()
    {
        return idVirtualDataCenter;
    }

    public void setIdVirtualDataCenter(final Integer idVirtualDataCenter)
    {
        this.idVirtualDataCenter = idVirtualDataCenter;
    }

    public EnterpriseHB getEnterpriseHB()
    {
        return enterpriseHB;
    }

    public void setEnterpriseHB(final EnterpriseHB enterpriseHB)
    {
        this.enterpriseHB = enterpriseHB;
    }

    public int getIdDataCenter()
    {
        return idDataCenter;
    }

    public void setIdDataCenter(final int idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(final NetworkHB network)
    {
        this.network = network;
    }

    /**
     * @return the network
     */
    public NetworkHB getNetwork()
    {
        return network;
    }

    public ResourceAllocationLimitHB getLimits()
    {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(final ResourceAllocationLimitHB limits)
    {
        this.limits = limits;
    }

    /**
     * Method to create the generic pojo object.
     */
    public VirtualDataCenter toPojo()
    {
        VirtualDataCenter virtualDataCenter = new VirtualDataCenter();
        virtualDataCenter.setId(idVirtualDataCenter);
        virtualDataCenter.setName(name);
        virtualDataCenter.setEnterprise(enterpriseHB != null ? enterpriseHB.toPojo() : null);
        virtualDataCenter.setIdDataCenter(idDataCenter);
        virtualDataCenter.setHyperType(new HyperVisorType(hypervisorType));
        virtualDataCenter.setNetwork(network != null ? network.toPojo() : null);
        virtualDataCenter.setLimits(limits != null ? limits.toPojo() : null);

        return virtualDataCenter;
    }

    public HypervisorType getHypervisorType()
    {
        return hypervisorType;
    }

    public void setHypervisorType(final HypervisorType hypervisorType)
    {
        this.hypervisorType = hypervisorType;
    }

}
