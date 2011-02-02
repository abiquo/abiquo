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

import com.abiquo.abiserver.infrastructure.Resource;

/**
 * Auxiliary class This class is used to create a new Physical Machine, along with its Hypervisors
 * And to retrieve the result of a physical machine creation
 * 
 * @author Oliver
 */
public class PhysicalMachineCreation
{
    private PhysicalMachine physicalMachine;

    private ArrayList<HyperVisor> hypervisors;

    private ArrayList<Resource> resources;

    public PhysicalMachine getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(PhysicalMachine physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public ArrayList<HyperVisor> getHypervisors()
    {
        return hypervisors;
    }

    public void setHypervisors(ArrayList<HyperVisor> hypervisors)
    {
        this.hypervisors = hypervisors;
    }

    public void setResources(ArrayList<Resource> resources)
    {
        this.resources = resources;
    }

    public ArrayList<Resource> getResources()
    {
        return resources;
    }

}
