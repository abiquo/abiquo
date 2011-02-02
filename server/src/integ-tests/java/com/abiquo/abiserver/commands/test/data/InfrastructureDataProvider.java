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

package com.abiquo.abiserver.commands.test.data;

import java.util.ArrayList;

import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.SO;

public class InfrastructureDataProvider extends DataProvider
{
    /**
     * Private class constructor
     */
    private InfrastructureDataProvider()
    {
        // Private Constructor
        super();
    }

    /**
     * This method creates a Physical data Center
     * 
     * @return
     */
    public static DataCenter createDataCenter()
    {
        final DataCenter dataCenter = new DataCenter();
        dataCenter.setName("DataCenterTest");
        dataCenter.setSituation("Castelldefels");

        return dataCenter;
    }

    /**
     * This method creates a Rack (Without dataCenter)
     * 
     * @return
     */
    public static Rack createRack()
    {
        final Rack rack = new Rack();
        rack.setName("Rack Test");
        rack.setShortDescription("Rack test");
        rack.setLargeDescription("This is a rack test");

        return rack;
    }

    /**
     * This method creates a PhysicalDataCenter (Without Rack)
     * 
     * @return
     */
    public static PhysicalMachine createPhysicalMachine()
    {
        final PhysicalMachine pMachine = new PhysicalMachine();
        pMachine.setName("MachineTest");

        pMachine.setDescription("Machine Test");

        pMachine.setHd(100000);
        pMachine.setCpu(2);
        pMachine.setCpuRatio(2);
        pMachine.setRam(1024);
        
        //final NetworkModule nModule = new NetworkModule();
        //nModule.setDhcp(false);
        //nModule.setIp("127.0.0.1");

        //ArrayList<NetworkModule> nModuleList = new ArrayList<NetworkModule>();
        //nModuleList.add(nModule);

        //pMachine.setNetworkModuleList(nModuleList);

        return pMachine;
    }
}
