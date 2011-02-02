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
package com.abiquo.vsm.monitor;

import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Mock {@link AbstractMonitor} to test {@link MonitorManager} wihtout connecting to the target
 * physical machines.
 * 
 * @author ibarrera
 */
public abstract class MockMonitor extends AbstractMonitor
{

    @Override
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws MonitorException
    {
        // Empty
    }

    @Override
    public void shutdown()
    {
        // Do nothing
    }

    @Override
    public void start()
    {
        // Do nothing
    }

    @Monitor(type = Type.HYPERV_301)
    static class SingleMachineMonitor extends MockMonitor
    {
        static final Type TYPE = Type.HYPERV_301;

        @Override
        public int getMaxNumberOfHypervisors()
        {
            return 1;
        }
    }

    @Monitor(type = Type.VMX_04)
    static class MulipleMachineMonitor extends MockMonitor
    {
        static final Type TYPE = Type.VMX_04;

        @Override
        public int getMaxNumberOfHypervisors()
        {
            return 4;
        }
    }

    @Monitor(type = Type.XEN_3)
    static class InfiniteMachineMonitor extends MockMonitor
    {
        static final Type TYPE = Type.XEN_3;

        @Override
        public int getMaxNumberOfHypervisors()
        {
            return 0;
        }
    }

}
