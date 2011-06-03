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
package com.abiquo.virtualfactory.machine.impl;

import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.utils.hyperv.MsvmVirtualSystemManagementServiceExtended;
import com.hyper9.jwbem.SWbemServices;

/**
 * Hyper-v virtual machine implementation using DCOM through WMI
 * 
 * @author pnavarro
 */
public class HyperVMachine extends AbsHyperVMachine
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(HyperVMachine.class);

    /**
     * Default constructor
     * 
     * @param configuration the virtual machine configuration
     * @throws VirtualMachineException
     */
    public HyperVMachine(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        super(configuration);
    }

    /**
     * Private helper to configure the booting virtual disks resources
     * 
     * @throws Exception
     */
    @Override
    public void configureVirtualDiskResources() throws Exception
    {
        logger.debug("Configuring Virtual disks resources");

        // Getting the IDE controller
        String ideControllerPath = getIDEControllerPathByAddress(0);

        if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
        {
            configureVHDDisk(ideControllerPath, 0);
        }
    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#deleteMachine()
     */
    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        try
        {
            // Forcing reconnect to be sure that the CIM service is properly connected
            hyperVHypervisor.forceReconnect();
            Thread.sleep(2000);
            SWbemServices virtService = hyperVHypervisor.getVirtualizationService();
            MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
                MsvmVirtualSystemManagementServiceExtended
                    .getManagementServiceExtended(virtService);
            Thread.sleep(2000);

            vmDispatch = getVmDispatch(this.getConfiguration().getMachineName());

            if (vmDispatch != null)
            {
                virtualSysteManagementServiceExt.destroyVirtualSystem(vmDispatch);

                // Just deleted the base disk when the virtual disk is stateless
                if (!config.getVirtualDiskBase().isHa())
                {
                    deleteBaseDisk();
                }
            }
            else
            {
                String message = "We couldn't delete the Virtual machine since it doesn't exist";
                logger.error(message);
            }

            try
            {
                deconfigureNetwork();
            }
            catch (Exception e)
            {
                logger.error(
                    "An error was occurred then deconfiguring the networking resources: {}", e);
            }

            hyperVHypervisor.logout();
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    @Override
    public void configureExtendedDiskResources(final VirtualMachineConfiguration vmConfig)
        throws Exception
    {
        logger
            .info("This is a premium functionality. Not extended disk resources will be attached");

    }

    protected String getIDEControllerPathByAddress(final int address) throws Exception
    {
        IJIDispatch ideControllerDispatch = getIdeControllerByAddress(address);

        // Getting the dispatcher of the ide resource added Path
        IJIDispatch idePathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(ideControllerDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        return idePathDispatcher.get("Path").getObjectAsString2();
    }
}
