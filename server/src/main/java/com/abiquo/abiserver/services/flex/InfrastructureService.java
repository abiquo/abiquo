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

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.DatacentersResourceStub;
import com.abiquo.abiserver.commands.stub.MachineResourceStub;
import com.abiquo.abiserver.commands.stub.MachinesResourceStub;
import com.abiquo.abiserver.commands.stub.RacksResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineResourceStub;
import com.abiquo.abiserver.commands.stub.impl.DatacentersResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.MachineResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.MachinesResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.RacksResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualMachineResourceStubImpl;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.HypervisorRemoteAccessInfo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;

/**
 * This class defines all services related to Infrastructure management
 * 
 * @author Oliver
 */

public class InfrastructureService
{

    private InfrastructureCommand infrastructureCommand;

    private final VirtualMachineResourceStub vmStub;

    public InfrastructureService()
    {
        vmStub = new VirtualMachineResourceStubImpl();
        try
        {
            infrastructureCommand =
                (InfrastructureCommand) Thread
                    .currentThread()
                    .getContextClassLoader()
                    .loadClass(
                        "com.abiquo.abiserver.commands.impl.InfrastructureCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            infrastructureCommand = new InfrastructureCommandImpl();
        }
    }

    protected VirtualMachineResourceStub proxyVmStub(final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, vmStub, VirtualMachineResourceStub.class);
    }

    private InfrastructureCommand proxyCommand(final UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, infrastructureCommand,
            InfrastructureCommand.class);
    }

    private DatacentersResourceStub proxyDatacentersStub(final UserSession session)
    {
        return APIStubFactory.getInstance(session, new DatacentersResourceStubImpl(),
            DatacentersResourceStub.class);
    }

    private RacksResourceStub proxyRacksStub(final UserSession session)
    {
        return APIStubFactory.getInstance(session, new RacksResourceStubImpl(),
            RacksResourceStub.class);
    }

    private MachineResourceStub proxyMachineStub(final UserSession session)
    {
        return APIStubFactory.getInstance(session, new MachineResourceStubImpl(),
            MachineResourceStub.class);
    }

    private MachinesResourceStub proxyMachinesStub(final UserSession session)
    {
        return APIStubFactory.getInstance(session, new MachinesResourceStubImpl(),
            MachinesResourceStub.class);
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
        DataCenter datacenter =
            proxyDatacentersStub(userSession).getDatacenter(datacenterId).getData();
        return proxyRacksStub(userSession).getRacksByDatacenter(datacenter, filters);
    }

    /**
     * Gets the physical machine list by rack
     * 
     * @param session the session
     * @param rackId the rack identifier
     * @return returns a DataResult, containing an Arraylist of PhysicalMachine
     */
    public BasicResult getPhysicalMachinesByRack(final UserSession session,
        final Integer datacenterId, final Integer rackId, final String filters)
    {
        return proxyMachinesStub(session).getPhysicalMachinesByRack(datacenterId, rackId, filters);
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
        final Integer datacenterId, final Integer rackId, final Integer pmId)
    {
        return proxyMachineStub(session).getVirtualMachinesFromMachine(datacenterId, rackId, pmId);
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
        return proxyDatacentersStub(session).getDatacenters();
    }

    /**
     * Returns all Data Centers
     * 
     * @param session
     * @param user
     * @return a DataResult object, with an ArrayList of DataCenter
     */
    public BasicResult getAllowedDataCenters(final UserSession session,
        final Integer effectiveEnterpriseId)
    {
        return proxyDatacentersStub(session).getDatacenters(effectiveEnterpriseId);
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
        return proxyDatacentersStub(session).createDatacenter(dataCenter);
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
        return proxyDatacentersStub(session).modifyDatacenter(dataCenter);
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
        return proxyDatacentersStub(session).deleteDatacenter(dataCenter);
    }

    /* ______________________________ RACKS _______________________________ */
    /**
     * Creates a new rack in the data base
     * 
     * @return a DataResult, with the Rack created
     */
    public BasicResult createRack(final UserSession session, final Rack rack)
    {
        return proxyRacksStub(session).createRack(rack);
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
        return proxyRacksStub(session).deleteRack(rack);
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
        return proxyRacksStub(session).modifyRack(rack);
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

        return proxyMachinesStub(session).createPhysicalMachine(physicalMachineCreation);

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
        return proxyMachinesStub(session).deletePhysicalMachine(physicalMachine);
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
        return proxyMachinesStub(session).editPhysicalMachine(physicalMachineCreation);
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
     * Deletes the virtual machine
     * 
     * @param sessionKey
     * @param virtualMachine
     * @return
     */
    public BasicResult deleteVirtualMachine(final UserSession session,
        final Integer virtualDatacenterId, final Integer virtualApplianceId,
        final VirtualMachine virtualMachine)
    {

        return proxyVmStub(session).deleteVirtualMachine(virtualDatacenterId, virtualApplianceId,
            virtualMachine);
    }

    /**
     * Edits virtual machine's information
     * 
     * @param sessionKey
     * @param virtualMachine
     * @return
     */
    public BasicResult editVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Boolean force)
    {

        VirtualMachineResourceStub vmachineResource =
            APIStubFactory.getInstance(session, new VirtualMachineResourceStubImpl(),
                VirtualMachineResourceStub.class);

        return vmachineResource.updateVirtualMachine(virtualDatacenterId, virtualApplianceId,
            virtualMachine, force);
    }

    /**
     * Moves a Virtual Machine from a Physical Machine to another. virtualMachine's "assignedTo"
     * attribute will contain the new HyperVisor, to which the virtual machine will be assigned
     * 
     * @param session
     * @param virtualMachine
     * @return
     */
    @Deprecated
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
        return proxyDatacentersStub(session).updateUsedResources(dataCenter.getId());
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
        final Integer idPhysicalMachine)
    {
        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);
        InfrastructureCommand command = proxyCommand(userSession);
        command.checkVirtualInfrastructureState(idPhysicalMachine, userSession, false);
        return basicResult;
    }

    /**
     * Retrieves a list of VirtualDataCenter that belongs to the same Enterprise
     * 
     * @param userSession The UserSession with the user that called this method
     * @param enterprise The Enterprise of which the VirtualDataCenter will be returned
     * @return a BasicResult object, containing an ArrayList<VirtualDataCenter>, with the
     *         VirtualDataCenter assigned to the enterprise
     */
    public BasicResult getVirtualDataCentersByEnterprise(final UserSession userSession,
        final Enterprise enterprise)
    {

        InfrastructureCommand command = proxyCommand(userSession);

        try
        {
            return command.getVirtualDataCentersByEnterprise(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    public DataResult<HypervisorRemoteAccessInfo> getHypervisorRemoteAccessInfo(
        final UserSession userSession, final PhysicalMachine machine)
    {
        MachineResourceStub proxy =
            APIStubFactory.getInstance(userSession, new MachineResourceStubImpl(),
                MachineResourceStub.class);

        return proxy.getHypervisorRemoteAccess(machine);
    }

    protected BasicResult deleteNotManagerVirtualMachines(final UserSession userSession,
        final PhysicalMachine machine)
    {
        return proxyMachineStub(userSession).deleteNotManagedVirtualMachines(machine);
    }

    public BasicResult refreshDatastores(final UserSession session, final Integer datacenterId,
        final Integer rackId, final Integer machineId)
    {
        return proxyMachineStub(session).refreshDatastores(datacenterId, rackId, machineId);
    }
}
