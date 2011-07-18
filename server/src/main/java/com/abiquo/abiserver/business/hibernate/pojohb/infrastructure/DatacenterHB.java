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

package com.abiquo.abiserver.business.hibernate.pojohb.infrastructure;

// Generated 16-oct-2008 16:52:14 by Hibernate Tools 3.2.1.GA

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.service.RemoteService;

public class DatacenterHB implements java.io.Serializable, IPojoHB<DataCenter>
{
    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(DatacenterHB.class);

    private static final long serialVersionUID = 5404783524189416182L;

    private Integer idDataCenter;

    private String name;

    private String situation;

    private NetworkHB network;

    private Set<RackHB> racks = new HashSet<RackHB>(0);

    /** List of limits established by Enterprise */
    private Set<DatacenterLimitHB> entLimits;

    /** Remote Service list */
    private Set<RemoteServiceHB> remoteServicesHB;

    public DatacenterHB()
    {
    }

    public DatacenterHB(final String name)
    {
        this.name = name;
    }

    public DatacenterHB(final String name, final String situation, final Set<RackHB> racks)
    {
        this.name = name;
        this.situation = situation;
        this.racks = racks;
    }

    public Integer getIdDataCenter()
    {
        return idDataCenter;
    }

    public void setIdDataCenter(final Integer idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getSituation()
    {
        return situation;
    }

    public void setSituation(final String situation)
    {
        this.situation = situation;
    }

    public Set<RackHB> getRacks()
    {
        return racks;
    }

    public void setRacks(final Set<RackHB> racks)
    {
        this.racks = racks;
    }

    public Set<RemoteServiceHB> getRemoteServicesHB()
    {
        return remoteServicesHB;
    }

    public void setRemoteServicesHB(final Set<RemoteServiceHB> remoteServicesHB)
    {
        this.remoteServicesHB = remoteServicesHB;
    }

    public DataCenter toPojo()
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setId(idDataCenter);
        dataCenter.setName(name);
        dataCenter.setSituation(situation);
        if (remoteServicesHB != null)
        {

            ArrayList<RemoteService> remoteServices = new ArrayList<RemoteService>();
            for (RemoteServiceHB remoteServiceHB : remoteServicesHB)
            {
                remoteServices.add(remoteServiceHB.toPojo());
            }
            dataCenter.setRemoteServices(remoteServices);
        }

        if (getNetwork() != null)
        {
            dataCenter.setNetwork(network.toPojo());
        }
        return dataCenter;
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

    public void setEntLimits(Set<DatacenterLimitHB> entLimits)
    {
        this.entLimits = entLimits;
    }

    public Set<DatacenterLimitHB> getEntLimits()
    {
        return entLimits;
    }

}
