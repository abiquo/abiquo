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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;

/**
 * The Class AbsVirtualMachine.
 */
public abstract class AbsVirtualMachine
{

    protected final static Logger logger = LoggerFactory.getLogger(AbsVirtualMachine.class);

    protected State state;

    protected VirtualMachineConfiguration config;

    /**
     * Standard constructor
     * 
     * @param configuration the configuration file to properly instantiate a virtual machine
     */
    public AbsVirtualMachine(final VirtualMachineConfiguration configuration)

    {
        config = configuration;
        state = State.NOT_DEPLOYED;

    }

    /**
     * Gets the virtual machine configuration object
     * 
     * @return the virtual machine configuration
     */
    public VirtualMachineConfiguration getConfiguration()
    {
        return config;
    }

    /**
     * Gets the state in the hypervisor
     * 
     * @return the state
     */
    public State getStateInHypervisor()
    {
        return state;
    }

    /**
     * Sets the state
     * 
     * @param state the new state
     */
    public void setState(final State state)
    {
        this.state = state;
    }

    /**
     * Gets the state
     * 
     * @return the state
     */
    public State getState()
    {
        return state;
    }

    /**
     * Performs the action related to the state indicated by the argument.
     * 
     * @param state the new state
     * @throws VirtualMachineException TODO
     */
    public void applyState(final State newstate) throws VirtualMachineException
    {
        checkIsCancelled();

        logger.info("Changing the state of virtual machine: {} to : {}", config.getMachineName(),
            newstate.toString());

        // TODO If the checking state is done checking in the hypervisor, analyze if this machine
        // state is needed

        if (state.compareTo(State.DEPLOYED) == 0)
        {
            if (newstate.compareTo(State.POWER_UP) == 0)
            {
                // In this state is when the VM is going to be power up after deploy.
                try
                {
                    powerOnMachine();
                }
                catch (Exception e)
                {
                    logger.info("An error was occurred when powering on the VM after deploy: ", e);
                    logger.info("Proceeding to roll back");
                    try
                    {
                        deleteMachine();
                        state = State.NOT_DEPLOYED;
                    }
                    catch (Exception e1)
                    {
                        state = State.NOT_DEPLOYED;
                        logger
                            .error(
                                "The VM could not be rolled back, after trying to be power up. Cause: ",
                                e);
                    }
                    throw new VirtualMachineException("An error was occurred when powering on the virtual machine:"
                        + config.getMachineId().toString() + " after the deploy. Reason: " + e.getMessage(),
                        e);
                }
                state = State.POWER_UP;
            }
            else if (newstate.compareTo(State.POWER_OFF) == 0)
            {
                powerOffMachine();
                state = State.POWER_OFF;
            }
            else if (newstate.compareTo(State.PAUSE) == 0)
            {
                pauseMachine();
                state = State.PAUSE;
            }
            else if (newstate.compareTo(State.RESUME) == 0)
            {
                resumeMachine();
                state = State.POWER_UP;
            }
        }
        else if (state.compareTo(State.POWER_OFF) == 0)
        {
            if (newstate.compareTo(State.POWER_UP) == 0)
            {
                powerOnMachine();
                state = State.POWER_UP;
            }
            else if (newstate.compareTo(State.POWER_OFF) == 0)
            {
                powerOffMachine();
                state = State.POWER_OFF;
            }
            else if (newstate.compareTo(State.PAUSE) == 0)
            {
                pauseMachine();
                state = State.PAUSE;
            }
            else if (newstate.compareTo(State.RESUME) == 0)
            {
                resumeMachine();
                state = State.POWER_UP;
            }
        }
        else if (state.compareTo(State.POWER_UP) == 0)
        {
            if (newstate.compareTo(State.POWER_OFF) == 0)
            {
                powerOffMachine();
                state = State.POWER_OFF;
            }
            else if (newstate.compareTo(State.PAUSE) == 0)
            {
                pauseMachine();
                state = State.PAUSE;
            }
            else if (newstate.compareTo(State.POWER_UP) == 0)
            {
                powerOnMachine();
                state = State.POWER_UP;
            }
        }
        else if (state.compareTo(State.PAUSE) == 0)
        {
            if (newstate.compareTo(State.POWER_OFF) == 0)
            {
                powerOffMachine();
                state = State.POWER_OFF;
            }
            else if (newstate.compareTo(State.RESUME) == 0)
            {
                resumeMachine();
                state = State.POWER_UP;
            }
            else if (newstate.compareTo(State.PAUSE) == 0)
            {
                pauseMachine();
                state = State.PAUSE;
            }
        }
        else
        {
            throw new VirtualMachineException("MachineState value " + newstate.toString());
        }

        logger.info("Virtual machine: {} changed succesfully to state : {}", config
            .getMachineName(),
            newstate.toString());

    }

    /**
     * Deploys a virtual machine.
     * 
     * @throws Exception
     */
    public abstract void deployMachine() throws VirtualMachineException;

    
    /**
     * Deploys a virtual machine without cloning its primary virtual disk.
     * Method used for HA process deployments
     * 
     * @throws Exception
     */
    public abstract void deployMachineExistingDisk() throws VirtualMachineException;

    
    /**
     * Starts the virtual machine execution
     * 
     * @throws VirtualMachineException
     */
    public abstract void powerOnMachine() throws VirtualMachineException;

    /**
     * Stops the virtual machine execution
     */
    public abstract void powerOffMachine() throws VirtualMachineException;

    /**
     * Pauses the virtual machine execution.
     */
    public abstract void pauseMachine() throws VirtualMachineException;

    /**
     * Resumes the virtual machine execution.
     */
    public abstract void resumeMachine() throws VirtualMachineException;

    /**
     * Resets the virtual machine
     */
    public abstract void resetMachine() throws VirtualMachineException;

    /**
     * Deletes the machine and the attached virtual disks
     */
    public abstract void deleteMachine() throws VirtualMachineException;

    /**
     * Reconfigures the virtual machine parameters
     * 
     * @param newConfiguration the new configuration parameters
     * @throws Exception
     */
    public abstract void reconfigVM(VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException;

    /**
     * Checks if the VM is already created
     * 
     * @return true if the VM already exists in the hypervisor, false if not
     * @throws VirtualMachineException
     */
    public abstract boolean isVMAlreadyCreated() throws VirtualMachineException;

    /**
     * Bundles the virtual machine in the repository, the new virtual disk stored name is the
     * snapshot name passes as a parameter
     * 
     * @param sourcePath the source image path
     * @param destinationPath the destination image path
     * @param snapshotName the snapshot name image path
     * @param isManaged if the virtual machine was managed by abiquo
     * @throws VirtualMachineException
     */
    public abstract void bundleVirtualMachine(String sourcePath, String destinationPath,
        String snapshotName, boolean isManaged) throws VirtualMachineException;

    /**
     * Rolls back the virtual machine
     */
    public void rollBackVirtualMachine()
    {
        try
        {
            deleteMachine();
        }
        catch (Exception e)
        {
            logger.debug("The VM could not be rolled back: {}", e);
        }
    }

    /**
     * Private helper that throws an Exception if the VM is been marked to be cancelled
     * 
     * @throws VirtualMachineException
     */
    public void checkIsCancelled() throws VirtualMachineException
    {
        if (state.compareTo(State.CANCELLED) == 0)
        {
            String msg = "The virtual machine" + config.getMachineId() + " has been cancelled";
            throw new VirtualMachineException(msg);
        }
    }

}
