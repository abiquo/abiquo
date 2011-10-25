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

package com.abiquo.abiserver.pojo.virtualappliance;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.networking.Network;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;

/**
 * This method manages the virtual Data Center information In abiCloud, an enterprise has any/one or
 * more virtual datacenters.
 * 
 * @author xfernandez
 */
/**
 * @author xfernandez
 */
public class VirtualDataCenter implements IPojo<VirtualDataCenterHB>
{

    /**
     * identification of virtual data center
     */
    private int id;

    /**
     * name of the virtual dataCenter
     */
    private String name;

    /**
     * The enteprise to which this VirtualDataCenter belongs
     */
    private Enterprise enterprise;

    /**
     * The physicalData center
     */
    private int idDataCenter;

    /**
     * Hypervisor Type selected for the VDC
     */
    private HyperVisorType hyperType;

    /**
     * The network of the Virtual Datacenter.
     */
    private Network network;

    private ResourceAllocationLimit limits;

    private VlanNetwork defaultVlan;

    /**
     * variable which corresponds with column 'networktypeID'
     */
    // private AbicloudNetwork networkType;

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public int getIdDataCenter()
    {
        return idDataCenter;
    }

    public void setIdDataCenter(final int idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    public HyperVisorType getHyperType()
    {
        return hyperType;
    }

    public void setHyperType(final HyperVisorType hyperType)
    {
        this.hyperType = hyperType;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(final Network network)
    {
        this.network = network;
    }

    /**
     * @return the network
     */
    public Network getNetwork()
    {
        return network;
    }

    /**
     * @return the limits
     */
    public ResourceAllocationLimit getLimits()
    {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(final ResourceAllocationLimit limits)
    {
        this.limits = limits;
    }

    /**
     * Method to create the hibernate pojo object
     */
    @Override
    public VirtualDataCenterHB toPojoHB()
    {
        final VirtualDataCenterHB virtualDataCenterHB = new VirtualDataCenterHB();
        virtualDataCenterHB.setIdVirtualDataCenter(id);
        virtualDataCenterHB.setName(name);
        virtualDataCenterHB.setEnterpriseHB(enterprise == null ? null : enterprise.toPojoHB());
        virtualDataCenterHB.setIdDataCenter(idDataCenter);
        virtualDataCenterHB.setHypervisorType(hyperType == null ? null : HypervisorType
            .fromValue(hyperType.getName()));
        virtualDataCenterHB.setLimits(limits == null ? null : limits.toPojoHB());
        virtualDataCenterHB.setNetwork(network == null ? null : network.toPojoHB());

        return virtualDataCenterHB;
    }

    public static VirtualDataCenter create(final VirtualDatacenterDto dto, final int datacenterId,
        final Enterprise enterprise, final Network network)
    {
        final VirtualDataCenter virtualDatacenter = new VirtualDataCenter();

        virtualDatacenter.setId(dto.getId());
        virtualDatacenter.setName(dto.getName());
        virtualDatacenter.setEnterprise(enterprise);
        virtualDatacenter.setIdDataCenter(datacenterId);
        virtualDatacenter.setHyperType(new HyperVisorType(dto.getHypervisorType()));

        virtualDatacenter.setLimits(ResourceAllocationLimit.create(dto));
        virtualDatacenter.setNetwork(network);
        virtualDatacenter.setDefaultVlan(VlanNetwork.create(dto.getVlan(), network.getNetworkId(),
            true));

        return virtualDatacenter;
    }

    public VlanNetwork getDefaultVlan()
    {
        return defaultVlan;
    }

    public void setDefaultVlan(final VlanNetwork defaultVlan)
    {
        this.defaultVlan = defaultVlan;
    }

}
