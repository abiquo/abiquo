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

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.util.AddressingUtils;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualMachineConfigSpec;

/**
 * Achieve communications to ESXi 3.5 VMWare bare metal hypervisor using the Java/Axis SDK bindings.
 * TODO: check getVms, no multiple VM for a single VmwareMachine instance
 * 
 * @author pnavarro, apuig
 */
public class VmwareMachine extends AbsVmwareMachine
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(VmwareMachine.class);

    public VmwareMachine(VirtualMachineConfiguration configuration) throws VirtualMachineException
    {
        super(configuration);
    }

    /**
     * Used during creation sets the additional configuration into the VM.
     * 
     * @param computerResMOR, the computer resource related to the current VM.
     * @param hostMOR, the host related to the current VM.
     * @return a configuration containing the specified resources
     */
    @Override
    public VirtualMachineConfigSpec configureVM(ManagedObjectReference computerResMOR,
        ManagedObjectReference hostMOR) throws VirtualMachineException
    {
        VirtualMachineConfigSpec vmConfigSpec;

        VMUtils vmUtils = new VMUtils(utils.getAppUtil());

        String rdmIQN = null;

        try
        {

            // TODO #createVMConfigSpec defines not convenient default data, change this
            vmConfigSpec =
                vmUtils.createVmConfigSpec(machineName, config.getVirtualDiskBase()
                    .getTargetDatastore(), config.getVirtualDiskBase().getCapacity(),
                    computerResMOR, hostMOR, config.getVnicList(), rdmIQN, disks);

            vmConfigSpec =
                vmUtils.createVmConfigSpec(machineName, config.getVirtualDiskBase()
                    .getTargetDatastore(), config.getVirtualDiskBase().getCapacity(),
                    computerResMOR, hostMOR, new ArrayList<VirtualNIC>(), rdmIQN, disks);

            vmConfigSpec.setName(machineName);
            vmConfigSpec.setAnnotation("VirtualMachine Annotation");
            vmConfigSpec.setMemoryMB(config.getMemoryRAM() / 1048576);
            vmConfigSpec.setNumCPUs(config.getCpuNumber());
            vmConfigSpec.setGuestId(utils.getOption("guestosid"));

            configureVNC(vmConfigSpec);
        }
        catch (Exception e)
        {
            final String msg = "Can not create the Virtual machine configuration specification";
            throw new VirtualMachineException(msg, e);
        }

        return vmConfigSpec;
    }

    /**
     * Used to configure VNC (enable, port, password)
     * 
     * @param vmConfigSpec
     */
    @Override
    public void configureVNC(VirtualMachineConfigSpec vmConfigSpec) throws VirtualMachineException
    {
        if (AddressingUtils.isValidPort(String.valueOf(config.getRdPort())))
        {
            OptionValue vncEnabled = new OptionValue();
            vncEnabled.setKey("RemoteDisplay.vnc.enabled");
            vncEnabled.setValue("true");

            OptionValue vncPort = new OptionValue();
            vncPort.setKey("RemoteDisplay.vnc.port");
            vncPort.setValue(config.getRdPort());

            OptionValue vncPwd = null;
            if (vmConfig.getRdPassword() != null && !vmConfig.getRdPassword().equals(""))
            {
                vncPwd = new OptionValue();
                vncPwd.setKey("RemoteDisplay.vnc.password");
                vncPwd.setValue(this.vmConfig.getRdPassword());

                vmConfigSpec.setExtraConfig(new OptionValue[] {vncEnabled, vncPort, vncPwd});
            }
            else
            {
                vmConfigSpec.setExtraConfig(new OptionValue[] {vncEnabled, vncPort, vncPwd});
            }
        }
    }

}
