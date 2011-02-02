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

package com.abiquo.abiserver.scheduler.testdata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.model.PopulateModelTest;

/**
 * A concret data set for the scheduler test
 */
public class SchedulerRestrictionsTestDataSet1 implements ISchedulerTestCase
{
    private List<PhysicalmachineHB> machines = new ArrayList<PhysicalmachineHB>();

    private List<VirtualimageHB> imgs = new ArrayList<VirtualimageHB>();

    private List<String> expected = new LinkedList<String>();

    private ResourceAllocationLimitHB limits = new ResourceAllocationLimitHB();

    private PopulateModelTest populate;

    public SchedulerRestrictionsTestDataSet1() throws PersistenceException
    {
        populate = PopulateModelTest.getInstance();

        machines.add(populate.definePhysical("pm1", 2, 1024, 4294967296L, 1));
        machines.add(populate.definePhysical("pm2", 2, 1024, 4294967296L, 1));

        // /

        imgs.add(populate.createVirtualImage("vi1", 1, 512, 2147483648L));
        imgs.add(populate.createVirtualImage("vi2", 1, 512, 2147483648L));
        imgs.add(populate.createVirtualImage("vi3", 1, 512, 2147483648L));
        imgs.add(populate.createVirtualImage("vi4", 1, 512, 2147483648L));

        imgs.add(populate.createVirtualImage("vi5", 1, 512, 2147483648L));

        // /

        expected.add("pm1");
        expected.add("pm1");
        expected.add("pm2");
        expected.add("pm2");

        expected.add(ISchedulerTestCase.NOT_ENOUGH_RESOURCES);

        limits.setCpu(new LimitHB(6, 5));
        limits.setRam(new LimitHB(6144, 5120));
        limits.setHd(new LimitHB(12884901888L, 10737418240L));
    }

    /**
     * @see TestDataBaseUtil.getPhysicalMachines()
     */
    public List<PhysicalmachineHB> getPhysicalMachines()
    {
        return machines;
    }

    /**
     * @see TestDataBaseUtil.getVirtualImages()
     */
    public List<VirtualimageHB> getVirtualImages()
    {
        return imgs;
    }

    @Override
    public List<String> getExpectedPhysicalMachineNameSequence()
    {
        return expected;
    }

    @Override
    public ResourceAllocationLimitHB getResourceAllocationLimit()
    {
        return limits;
    }

}
