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

package com.abiquo.virtualfactory.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.abiquo.virtualfactory.exception.VirtualMachineException;

/**
 * The Class VirtualApplianceModel represents a mini model to maintain a list of virtual appliances
 * .
 */
public class VirtualApplianceModel extends Observable
{

    /** The singleton instance for this class. */
    private static VirtualApplianceModel singleton;

    /** The virtual appliances. */
    private static Map<String, VirtualAppliance> virtualAppliances =
        new HashMap<String, VirtualAppliance>();

    /**
     * Instantiates a new virtual appliance model.
     */
    private VirtualApplianceModel()
    {
    }

    /**
     * Singleton accessor.
     * 
     * @return the model
     */
    public static VirtualApplianceModel getModel()
    {
        if (singleton == null)
        {
            singleton = new VirtualApplianceModel();
        }

        return singleton;
    }

    /**
     * Creates the virtual appliance.
     * 
     * @param virtualApplianceId the virtual appliance id
     * @return the virtual appliance
     */
    public VirtualAppliance createVirtualAppliance(String virtualApplianceId)
    {
        VirtualAppliance virtualAppliance = new VirtualAppliance();
        virtualAppliance.setVirtualApplianceId(virtualApplianceId);
        return virtualAppliance;
    }

    /**
     * Private helper to know if the VA has VM's to be deployed
     * 
     * @param virtualAppliance
     * @return
     */
    private boolean hasVMnotDeployed(VirtualAppliance virtualAppliance)
    {
        boolean hasVMnotDeployed = false;
        Collection<AbsVirtualMachine> machines = virtualAppliance.getMachines();
        for (AbsVirtualMachine absVirtualMachine : machines)
        {
            if (absVirtualMachine.getState().compareTo(State.NOT_DEPLOYED) == 0)
            {
                return true;
            }

        }
        return hasVMnotDeployed;
    }

    /**
     * Rolls back a virtual appliance.
     * 
     * @param the virtualAppliance to rollback
     * @throws VirtualMachineException
     * @throws Exception
     * @throws Exception
     */
    public void rollbackVirtualAppliance(VirtualAppliance virtualAppliance)
        throws VirtualMachineException
    {
        List<AbsVirtualMachine> machinesToRemove = new ArrayList<AbsVirtualMachine>();
        machinesToRemove.addAll(virtualAppliance.getMachines());
        for (AbsVirtualMachine machine : machinesToRemove)
        {
            // In the powering off (undeploy time) we don't do the rollback
            if (virtualAppliance.getState().compareTo(State.POWER_OFF) != 0)
            {
                machine.powerOffMachine();
                machine.deleteMachine();
            }
        }
    }

    /**
     * Gets the virtual appliances.
     * 
     * @return the virtual appliances
     */
    public Collection<VirtualAppliance> getVirtualAppliances()
    {
        return virtualAppliances.values();
    }
}
