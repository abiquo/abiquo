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
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.MachineResourceStub;
import com.abiquo.abiserver.commands.stub.impl.MachineResourceStubImpl;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * This class defines all services related to Infrastructure management
 * 
 * @author Oliver
 */

public class InfrastructureService
{

    private InfrastructureCommand infrastructureCommand;

    public InfrastructureService()
    {
        infrastructureCommand = new InfrastructureCommandImpl();

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
    }

    private InfrastructureCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, infrastructureCommand,
            InfrastructureCommand.class);
    }

    /* ______________________________ DATA CENTER _______________________________ */
    /**
     * The command related to this service
     */

    /**
     * Returns the infrastructure stored in a Data Center
     * 
     * @param session
     * @param dataCenter The Data Center we want to recover the infrastructure from
     * @return Returns a DataResult, containing an ArrayList of InfrastructureElement
     */
    @Deprecated
    public BasicResult getInfrastructureByDataCenter(final UserSession session,
        final DataCenter dataCenter)
    {
        InfrastructureCommand command = proxyCommand(session);
        return command.getInfrastructureByDataCenter(dataCenter);
    }

    /**
     * Return all the racks registered into a datacenter. It can filter the rack by name, the
     * datacenter by name and if an enterprise is associated to any physical machine, the enterprise
     * by name.
     * 
     * @param userSession user who performs the action.
     * @param datacenterId identifier of the datacenter.
     * @param filters String value to filter the search.
     * @return a DataResult containing the list of the Racks.
     */
    public BasicResult getRacksByDatacenter(final UserSession userSession,
        final Integer datacenterId, final String filters)
    {
        DataResult<ArrayList<Rack>> dataResult = new DataResult<ArrayList<Rack>>();

        InfrastructureCommand command = proxyCommand(userSession);

        try
        {
            ArrayList<RackHB> commandResult =
                command.getRacksByDatacenter(userSession, datacenterId, filters);
            dataResult.setData(new ArrayList<Rack>());
            for (RackHB singleResult : commandResult)
            {
                dataResult.getData().add(singleResult.toPojo());
            }
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Gets the physical machine list by rack
     * 
     * @param session the session
     * @param rackId the rack identifier
     * @return returns a DataResult, containing an Arraylist of PhysicalMachine
     */
    public BasicResult getPhysicalMachinesByRack(final UserSession session, final Integer rackId,
        final String filters)
    {

        InfrastructureCommand command = proxyCommand(session);
        DataResult<List<PhysicalMachine>> result = new DataResult<List<PhysicalMachine>>();
        try
        {
            List<PhysicalMachine> commandResult =
                command.getPhysicalMachinesByRack(session, rackId, filters);

            result.setData(commandResult);
            
            result.setSuccess(Boolean.TRUE);

        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Gets the available physical machine list by rack
     * 
     * @param session the session
     * @param rackId the rack identifier
     * @param enterpriseId the enterprise id
     * @return returns a DataResult, containing an Arraylist of PhysicalMachine
     */
    public BasicResult getAvailablePhysicalMachinesByRack(final UserSession session,
        final Integer rackId, final Integer enterpriseId)
    {

        InfrastructureCommand command = proxyCommand(session);

        DataResult<ArrayList<PhysicalMachine>> result =
            new DataResult<ArrayList<PhysicalMachine>>();

        try
        {
            result = command.getAvailablePhysicalMachinesByRack(rackId, enterpriseId);
        }
        catch (PersistenceException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Gets the virtual machines deployed in a physical machine
     * 
     * @param session the session
     * @param rackId the rack identifier
     * @return returns a DataResult, containing an Arraylist of virtualmachine
     */
    public BasicResult getVirtualMachineByPhysicalMachine(final UserSession session,
        final Integer pmId)
    {
        InfrastructureCommand command = proxyCommand(session);
        DataResult<ArrayList<VirtualMachine>> result = new DataResult<ArrayList<VirtualMachine>>();
        try
        {
            List<VirtualmachineHB> commandResult =
                command.getVirtualMachinesByPhysicalMachine(session, pmId);

            result.setData(new ArrayList<VirtualMachine>());
            for (VirtualmachineHB singleResult : commandResult)
            {
                result.getData().add(singleResult.toPojo());
            }
            result.setSuccess(Boolean.TRUE);

        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Gets the virtual machines deployed in a physical machine
     * 
     * @param session the session
     * @param rackId the rack identifier
     * @return returns a DataResult, containing an Arraylist of virtualmachine
     */
    public BasicResult getHypervisorByPhysicalMachine(final UserSession session, final Integer pmId)
    {
        InfrastructureCommand command = proxyCommand(session);
        DataResult<HyperVisor> result = new DataResult<HyperVisor>();
        try
        {

            HypervisorHB hyp = command.getHypervisorByPhysicalMachine(session, pmId);
            if (hyp != null)
            {
                result.setData(hyp.toPojo());
            }
            result.setSuccess(Boolean.TRUE);

        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Returns all Data Centers
     * 
     * @param session
     * @param user
     * @return a DataResult object, with an ArrayList of DataCenter
     */
    public BasicResult getDataCenters(final UserSession session)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.getDataCenters(session);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Returns all Data Centers
     * 
     * @param session
     * @param user
     * @return a DataResult object, with an ArrayList of DataCenter
     */
    public BasicResult getAllowedDataCenters(final UserSession session)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.getAllowedDataCenters(session);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Community or premium. XXX IoC do it yourselfe
     */
    private InfrastructureCommand getInfrastructureCommandImplementation()
    {
        final String premiumClass =
            "com.abiquo.abiserver.commands.impl.InfrastructureCommandPremiumImpl";

        InfrastructureCommand instance;
        try
        {
            instance =
                (InfrastructureCommand) Thread.currentThread().getContextClassLoader().loadClass(
                    premiumClass).newInstance();

        }
        catch (final Exception e)
        {
            instance = new InfrastructureCommandImpl();
        }

        return instance;
    }

    /**
     * Creates a new Data Center
     * 
     * @param session
     * @param dataCenter
     * @return a DataResult, with the Data Center created
     */
    public BasicResult createDataCenter(final UserSession session, final DataCenter dataCenter)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.createDataCenter(session, dataCenter);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Edits in the Data Base the information of the Data Center
     * 
     * @param session
     * @param dataCenter
     * @return
     */
    public BasicResult editDataCenter(final UserSession session, final DataCenter dataCenter)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.editDataCenter(session, dataCenter);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Deletes from the Data Base the Data Center
     * 
     * @param session
     * @param dataCenter
     * @return
     */
    public BasicResult deleteDataCenter(final UserSession session, final DataCenter dataCenter)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.deleteDataCenter(session, dataCenter);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /* ______________________________ RACKS _______________________________ */
    /**
     * Creates a new rack in the data base
     * 
     * @return a DataResult, with the Rack created
     */
    public BasicResult createRack(final UserSession session, final Rack rack)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.createRack(session, rack);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Deletes the rack from the data base
     * 
     * @param sessionKey
     * @param rack
     * @return
     */
    public BasicResult deleteRack(final UserSession session, final Rack rack)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.deleteRack(session, rack);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Edits rack's information
     * 
     * @param sessionKey
     * @param rack
     * @return
     */
    public BasicResult editRack(final UserSession session, final Rack rack)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.editRack(session, rack);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /* ______________________________ PHYSICAL MACHINES _______________________________ */
    /**
     * Creates a new Physical Machine in the Data Base, and its hypervisors, if there is any
     * 
     * @param session The user's session that called this method
     * @param physicalMachineCreation A PhysicalMachineCreation object containing the
     *            PhysicalMachine that will be created, and an ArrayList of hypervisors assigned to
     *            this physical machine, that will be created too
     * @return a DataResult, with a PhysicalMachineCreation object containing the Physical Machine
     *         and Hypervisors created
     */
    public BasicResult createPhysicalMachine(final UserSession session,
        final PhysicalMachineCreation physicalMachineCreation)
    {

        InfrastructureCommand command = proxyCommand(session);

        DataResult<PhysicalMachineCreation> result = new DataResult<PhysicalMachineCreation>();

        try
        {
            result = command.createPhysicalMachine(session, physicalMachineCreation);
        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Deletes the physical machine from the data base
     * 
     * @param sessionKey
     * @param physicalMachine
     * @return
     */
    public BasicResult deletePhysicalMachine(final UserSession session,
        final PhysicalMachine physicalMachine)
    {
        InfrastructureCommand command = proxyCommand(session);
        try
        {
            return command.deletePhysicalMachine(session, physicalMachine);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Edits the physical machines's information
     * 
     * @param sessionKey
     * @param physicalMachineCreation A PhysicalMachineCreation object containing the
     *            PhysicalMachine that will be edited, and the list of hypervisors to be edited or
     *            created (Hypervisor deletion is not supported)
     * @return Since when we edit a PhysicalMachine, new Hypervisors can be created, this method
     *         returns a DataResult, and when success = true, data attribute will contain an
     *         ArrayList with the Hypervisors that have been created
     */
    public BasicResult editPhysicalMachine(final UserSession session,
        final PhysicalMachineCreation physicalMachineCreation)
    {

        InfrastructureCommand command = proxyCommand(session);

        DataResult<ArrayList<HyperVisor>> result = new DataResult<ArrayList<HyperVisor>>();

        try
        {
            result = command.editPhysicalMachine(session, physicalMachineCreation);
        }
        catch (InfrastructureCommandException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /* ______________________________ HYPERVISORS _______________________________ */

    /**
     * Creates a new Hypervisor
     * 
     * @param userSession
     * @param hypervisor
     * @return A DataResult object containing the Hypervisor created
     */
    public BasicResult createHypervisor(final UserSession userSession, final HyperVisor hypervisor)
    {
        InfrastructureCommand command = proxyCommand(userSession);
        return command.createHypervisor(userSession, hypervisor);
    }

    /**
     * Edits an existing Hypervisor
     * 
     * @param session
     * @param hypervisor
     * @return a BasicResult object with success = true if the edition was successful
     */
    public BasicResult editHypervisor(final UserSession userSession, final HyperVisor hypervisor)
    {
        InfrastructureCommand command = proxyCommand(userSession);
        return command.editHypervisor(userSession, hypervisor);
    }

    /**
     * Deletes the hypervisor from the data base
     * 
     * @param session
     * @param hypervisor
     * @return A BasicResult object with the result of the deletion
     */
    public BasicResult deleteHypervisor(final UserSession session, final HyperVisor hypervisor)
    {
        InfrastructureCommand command = proxyCommand(session);
        return command.deleteHypervisor(hypervisor);
    }

    /* ______________________________ VIRTUAL MACHINES _______________________________ */

    /**
     * Creates a new Virtual Machine in the Data Base
     * 
     * @param session UserSession object containing the UserSession that is calling this method
     * @param virtualMachine A VirtualMachine object containing the necessary information to create
     *            a new Virtual Machine. UUID and State fields will be ignored, since they will be
     *            generated.
     * @return a DataResult object containing a VirtualMachine object with the Virtual Machine
     *         created
     */
    public BasicResult createVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.createVirtualMachine(virtualMachine);
    }

    /**
     * Deletes the virtual machine
     * 
     * @param sessionKey
     * @param virtualMachine
     * @return
     */
    public BasicResult deleteVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.deleteVirtualMachine(session, virtualMachine);
    }

    /**
     * Edits virtual machine's information
     * 
     * @param sessionKey
     * @param virtualMachine
     * @return
     */
    public BasicResult editVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.editVirtualMachine(session, virtualMachine);
    }

    /**
     * Moves a Virtual Machine from a Physical Machine to another. virtualMachine's "assignedTo"
     * attribute will contain the new HyperVisor, to which the virtual machine will be assigned
     * 
     * @param session
     * @param virtualMachine
     * @return
     */
    public BasicResult moveVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.editVirtualMachine(session, virtualMachine);

    }

    /**
     * Moves a Virtual Machine from a Physical Machine to another. virtualMachine's "assignedTo"
     * attribute will contain the new HyperVisor, to which the virtual machine will be assigned
     * 
     * @param session
     * @param virtualMachine
     * @return
     */
    public BasicResult getHypervisorsTypeByDataCenter(final UserSession session,
        final DataCenter dataCenter)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.getHypervisorsTypeByDataCenter(dataCenter.toPojoHB());
    }

    /**
     * This method updates the information of used resources in a dataCenter
     * 
     * @param session
     * @param virtualMachine
     * @return
     */
    public BasicResult updateUsedResourcesByDatacenter(final UserSession session,
        final DataCenter dataCenter)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.updateUsedResourcesByDatacenter(dataCenter.toPojoHB());

    }

    /**
     * Forces an state refresh in the virtual machine
     * 
     * @param session
     * @param virtualMachine the virtual machine to refresh
     * @return A BasicResult object
     */
    public BasicResult forceRefreshVirtualMachineState(final UserSession session,
        final VirtualMachine virtualMachine)
    {

        InfrastructureCommand command = proxyCommand(session);
        return command.forceRefreshVirtualMachineState(virtualMachine);
    }

    /**
     * Updates the virtual infrastructure state.
     * 
     * @param userSession user session objects
     * @param datacenterId identifier of the datacenter
     * @param ip ip address of the machine.
     * @param idPhysicalmachine identifier of the physical machine state.
     * @return a BasicResult containing the Physical machine state
     */
    public BasicResult checkVirtualInfrastructureState(final UserSession userSession,
        Integer idPhysicalMachine)
    {
        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);
        InfrastructureCommand command = proxyCommand(userSession);
        command.checkVirtualInfrastructureState(idPhysicalMachine, userSession, false);
        return basicResult;
    }

    protected BasicResult deleteNotManagerVirtualMachines(UserSession userSession,
        PhysicalMachine machine)
    {
        MachineResourceStub proxy =
            APIStubFactory.getInstance(userSession, new MachineResourceStubImpl(),
                MachineResourceStub.class);

        try
        {
            return proxy.deleteNotManagedVirtualMachines(machine);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }
}
