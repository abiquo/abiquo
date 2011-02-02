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

package com.abiquo.scheduler;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;

@Controller
@Transactional
public class PopulateAction extends PopulateConstants
{

    @Autowired
    VirtualDatacenterRep vdcRep;

    /**
     * <ul>
     * <li>action.allocate.vm1=m1,m2
     * <li>action.deallocate.vm1
     * </ul>
     */
    public AllocatorAction readAction(String dec)
    {
        AllocatorAction action = new AllocatorAction();

        Assert.assertTrue(dec.startsWith(DEC_ACTION), "Expected action declaration " + dec);

        String[] frg = dec.split(DELIMITER_ENTITIES);

        Assert.assertTrue(frg.length == 3 || frg.length == 4, "Expected action declaration " + dec);

        boolean allocate;

        if (frg[1].equalsIgnoreCase(DEC_ALLOCATE))
        {
            allocate = true;
        }
        else if (frg[1].equalsIgnoreCase(DEC_DEALLOCATE))
        {
            allocate = false;
        }
        else
        {
            throw new PopulateException("Invalid allocation action " + frg[1]);
        }

        String vmachineName;
        if (allocate)
        {
            String equ[] = frg[2].split("=");
            Assert.assertTrue(equ.length == 2, "Invlid action equation" + frg[2]);

            vmachineName = equ[0];

            String targetMachineName = equ[1];

            action.targetMachineName = new LinkedHashSet<String>();
            for (String m : targetMachineName.split(DELIMITER_ATTRIBUTES))
            {
                action.targetMachineName.add(m);
            }
        }
        else
        {
            vmachineName = frg[2];
        }

        VirtualMachine vmachine = vdcRep.findVirtualMachineByName(vmachineName);
        Assert.assertNotNull(vmachine, "virtual machine not found " + vmachineName);

        VirtualAppliance vapp = vdcRep.findVirtualApplianceByVirtualMachine(vmachine);
        Assert.assertNotNull(vapp, "virtual machine not associated to any vapp");

        int virtualDatacenterId = vapp.getVirtualDatacenter().getId();
        int virtualApplianceId = vapp.getId();
        int virtualMachineId = vmachine.getId();

        action.allocate = allocate;
        action.virtualDatacenterId = virtualDatacenterId;
        action.virtualApplianceId = virtualApplianceId;
        action.virtualMachineId = virtualMachineId;

        return action;
    }
}
