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

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;

/**
 * Provide an input scenario to run the scheduler test. TODO create SOFT_LIMIT tag
 */
public interface ISchedulerTestCase
{
    /**
     * Constant to use on ''getExpectedPhysicalMachieNameSequence'' when a HardLimitException is
     * expected.
     */
    public static String HARD_LIMIT_REACH = "HARD";

    /**
     * Constant to use on ''getExpectedPhysicalMachieNameSequence'' when a SchedulerException is
     * expected.
     */
    public static String NOT_ENOUGH_RESOURCES = "RESOURCES";

    /**
     * PhysicalMachines to be used. Only required to fill the ''name'' and the resource properties
     * (cpu,ram,hc, cpuRatio), all the other attributes are managed on
     * {@link PopulatePhysicalMachines}
     */
    List<PhysicalmachineHB> getPhysicalMachines();

    /** VirtualImages to be scheduled (in order) into physicalMachines. */
    List<VirtualimageHB> getVirtualImages();

    /** The resource allocation limits on the scenario. */
    ResourceAllocationLimitHB getResourceAllocationLimit();

    /**
     * A list of physicalMachine name (or HARD_LIMIT_REACH, NOT_ENOUGH_RESOURCES) as expected
     * scheduler result
     */
    List<String> getExpectedPhysicalMachineNameSequence();

}
