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

package com.abiquo.abiserver.pojo.infrastructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.networking.Network;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.server.core.infrastructure.DatacenterDto;

public class DataCenter implements IPojo<DatacenterHB>
{

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(DataCenter.class);

    /* ------------- Public atributes ------------- */

    private int id;

    private String name;

    private String situation;

    private Network network;

    /** remote services list */
    private ArrayList<RemoteService> remoteServices;

    /* ------------- Constructor ------------- */
    public DataCenter()
    {
        id = 0;
        name = "";
        situation = "";
    }

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

    public String getSituation()
    {
        return situation;
    }

    public void setSituation(final String situation)
    {
        this.situation = situation;
    }

    public ArrayList<RemoteService> getRemoteServices()
    {
        return remoteServices;
    }

    public void setRemoteServices(final ArrayList<RemoteService> remoteServices)
    {
        this.remoteServices = remoteServices;
    }

    public DatacenterHB toPojoHB()
    {
        DatacenterHB dataCenterHB = new DatacenterHB();
        dataCenterHB.setIdDataCenter(getId());
        dataCenterHB.setName(name);
        dataCenterHB.setSituation(situation);
        if (remoteServices != null)
        {
            Set<RemoteServiceHB> remoteServicesHB = new HashSet<RemoteServiceHB>(0);
            for (RemoteService remoteService : remoteServices)
            {
                remoteServicesHB.add(remoteService.toPojoHB());
            }
            dataCenterHB.setRemoteServicesHB(remoteServicesHB);
        }
        if (getNetwork() != null)
        {
            dataCenterHB.setNetwork(network.toPojoHB());
        }
        return dataCenterHB;
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

    public static DataCenter create(DatacenterDto dto)
    {
        DataCenter dataCenter = new DataCenter();
        dataCenter.setId(dto.getId());
        dataCenter.setName(dto.getName());
        dataCenter.setSituation(dto.getLocation());

        return dataCenter;
    }

}
