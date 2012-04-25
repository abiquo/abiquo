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

package com.abiquo.abiserver.services.flex;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.commands.impl.VirtualApplianceCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineResourceStub;
import com.abiquo.abiserver.commands.stub.impl.VirtualApplianceResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualMachineResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.TaskStatus;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachineState;

/**
 * This class defines a wide set of services that are considered "non-blocking services".
 * 
 * @author Oliver
 */

public class NonBlockingService
{
    /**
     * The commands related to this service
     */
    InfrastructureCommand infrastructureCommand;

    VirtualApplianceCommand virtualAppCommand;

    protected VirtualApplianceResourceStub virtualApplianceResourceStub;

    protected VirtualMachineResourceStub vmResourceStub;

    /**
     * Constructor The implemention of the BasicCommand
     */
    public NonBlockingService()
    {
        virtualApplianceResourceStub = new VirtualApplianceResourceStubImpl();
        // to_delete
        vmResourceStub = new VirtualMachineResourceStubImpl();
        try
        {
            infrastructureCommand =
                (InfrastructureCommand) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.commands.impl.InfrastructureCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            infrastructureCommand = new InfrastructureCommandImpl();
        }

        try
        {
            virtualAppCommand =
                (VirtualApplianceCommand) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.commands.impl.VirtualApplianceCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            virtualAppCommand = new VirtualApplianceCommandImpl();
        }
    }

    /* ______________________________ INFRASTRUCTURE _______________________________ */

    /**
     * Retrieves an updated Infrastructre that belongs to a Datacenter
     * 
     * @param session
     * @param dataCenter The Datacenter to return its infrastructure
     * @result a DataResult object, containing an ArrayList of InfrastructureElements with the last
     *         state of the infrastructure for the given datacenter
     */
    public BasicResult checkInfrastructureByDatacenter(final UserSession session,
        final DataCenter dataCenter)
    {
        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);
        return command.getInfrastructureByDataCenter(dataCenter);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult startVirtualMachine(final UserSession session,
        final Integer virtualApplianceId, final Integer virtualDatacenterId,
        final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editVirtualMachineState(
            virtualDatacenterId, virtualApplianceId, virtualMachine, VirtualMachineState.ON);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult startVirtualMachine(final UserSession session, final Integer datacenterId,
        final Integer rackId, final Integer machineId, final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editInfrastructureVirtualMachineState(
            datacenterId, rackId, machineId, virtualMachine, VirtualMachineState.ON);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult pauseVirtualMachine(final UserSession session,
        final Integer virtualApplianceId, final Integer virtualDatacenterId,
        final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editVirtualMachineState(
            virtualDatacenterId, virtualApplianceId, virtualMachine, VirtualMachineState.PAUSED);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult pauseVirtualMachine(final UserSession session, final Integer datacenterId,
        final Integer rackId, final Integer machineId, final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editInfrastructureVirtualMachineState(
            datacenterId, rackId, machineId, virtualMachine, VirtualMachineState.PAUSED);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult rebootVirtualMachine(final UserSession session,
        final Integer virtualApplianceId, final Integer virtualDatacenterId,
        final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).rebootVirtualMachine(virtualDatacenterId,
            virtualApplianceId, virtualMachine);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult rebootVirtualMachine(final UserSession session, final Integer datacenterId,
        final Integer rackId, final Integer machineId, final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).rebootInfrastructureVirtualMachine(
            datacenterId, rackId, machineId, virtualMachine);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult shutdownVirtualMachine(final UserSession session,
        final Integer virtualApplianceId, final Integer virtualDatacenterId,
        final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editVirtualMachineState(
            virtualDatacenterId, virtualApplianceId, virtualMachine, VirtualMachineState.OFF);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult shutdownVirtualMachine(final UserSession session,
        final Integer datacenterId, final Integer rackId, final Integer machineId,
        final VirtualMachine virtualMachine)
    {
        return proxyVirtualMachineResourceStub(session).editInfrastructureVirtualMachineState(
            datacenterId, rackId, machineId, virtualMachine, VirtualMachineState.OFF);
    }

    /* ______________________________ VIRTUAL APPLIANCE _______________________________ */

    /**
     * Performs a "Start" action in the Virtual Machine
     * 
     * @param session
     * @param virtualAppliance
     * @param force, indicating if the virtual appliance should be started even when the soft limit
     *            is exceeded. if false and the soft limit is reached the BasicResult result code is
     *            set to SOFT_LIMT_EXCEEDED.
     * @return a DataResult object, with a com.abiquo.abiserver.pojo.infrastructure.State object
     *         that represents the state "Running"
     */
    public BasicResult startVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final Boolean force)
    {
        return deployVirtualAppliance(session, virtualAppliance, force);
    }

    /**
     * @param session
     * @param virtualAppliance
     * @return BasicResult
     */
    public DataResult deployVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final Boolean force)
    {
        return proxyVirtualApplianceResourceStub(session).deployVirtualAppliance(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance.getId(), force);
    }

    /**
     * Performs a "Shutdown" action in the Virtual Machine
     * 
     * @param session
     * @param virtualAppliance
     * @return a DataResult object, with a com.abiquo.abiserver.pojo.infrastructure.State object
     *         that represents the state "Powered Off"
     */
    public BasicResult shutdownVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        return proxyVirtualApplianceResourceStub(session).undeployVirtualAppliance(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance.getId(), true);
    }

    /**
     * Applies the VirtualAppliance changes in the virtual factory
     * 
     * @param session
     * @param virtualAppliance
     * @return a BasicResult object, containing success = true if the changes were applied
     *         successfully
     */
    public DataResult<VirtualAppliance> applyChangesVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final Boolean force)
    {
        VirtualApplianceResourceStub resourceStub = this.proxyVirtualApplianceResourceStub(session);
        return resourceStub.applyChangesVirtualAppliance(virtualAppliance, session, force);

    }

    /**
     * Deletes a VirtualAppliance that exists in the Data Base
     * 
     * @param session
     * @param virtualAppliance
     * @return a BasicResult object, containing success = true if the deletion was successful
     */
    public BasicResult deleteVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        return proxyVirtualApplianceResourceStub(session).deleteVirtualAppliance(virtualAppliance,
            false);
    }

    /**
     * Retrieves a VirtualAppliance, with the current values in DataBase. Since a client can have an
     * old version of a VirtualAppliance, this service is useful to get the updated state of a
     * Virtual Appliance
     * 
     * @param session
     * @param virtualAppliance The VirtualAppliance to check.
     * @return a DataResult<VirtualAppliance> object with the last updated values in DataBase The
     *         returned VirtualAppliance will contain its list of noded
     */
    public BasicResult checkVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        return proxyVirtualApplianceResourceStub(session).getVirtualApplianceNodes(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance.getId(),
            "checkVirtualAppliance");
    }

    /**
     * Bundles the virtual images associated to the specified nodes of a virtual appliance.
     * 
     * @param session
     * @param virtualAppliance The VirtualAppliance to bundle.
     * @param nodes Selected nodes to bundle.
     * @param updateNodes True if the nodes must be updated with the bundled images.
     * @return A DataResult<VirtualAppliance> object with the last updated values in database.
     */
    public BasicResult bundleVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final ArrayList<Node> nodes,
        final Boolean updateNodes)
    {
        return proxyVirtualApplianceResourceStub(session).instanceVirtualApplianceNodes(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance.getId(), nodes);
    }

    protected VirtualApplianceResourceStub proxyVirtualApplianceResourceStub(
        final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, virtualApplianceResourceStub,
            VirtualApplianceResourceStub.class);
    }

    protected VirtualMachineResourceStub proxyVirtualMachineResourceStub(
        final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, vmResourceStub,
            VirtualMachineResourceStub.class);
    }

    /**
     * @param session
     * @param virtualAppliance
     * @return BasicResult
     */
    public DataResult undeployVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        return proxyVirtualApplianceResourceStub(session).undeployVirtualAppliance(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance.getId(),
            Boolean.FALSE);
    }

    /**
     * @param session
     * @param virtualAppliance
     * @return BasicResult
     */
    public DataResult<List<TaskStatus>> updateTask(final UserSession session, final TaskStatus task)
    {

        return proxyVirtualApplianceResourceStub(session).updateTask(task);
    }

}
