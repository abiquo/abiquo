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
package com.abiquo.abiserver.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.infrastructure.InfrastructureElement;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;

public interface InfrastructureCommand
{

    /**
     * Returns the whole infrastructure stored in a data center
     * 
     * @param dataCenter
     * @return
     */
    @Deprecated
    public abstract DataResult<ArrayList<InfrastructureElement>> getInfrastructureByDataCenter(
        final DataCenter dataCenter);

    /**
     * Return all the racks registered into a datacenter. It can filter the rack by name, the
     * datacenter by name and if an enterprise is associated to any physical machine, the enterprise
     * by name.
     * 
     * @param userSession user who performs the action.
     * @param datacenterId identifier of the datacenter.
     * @param filters String value to filter the search.
     * @return the list of the Racks.
     */
    public ArrayList<RackHB> getRacksByDatacenter(final UserSession userSession,
        final Integer datacenterId, final String filters) throws InfrastructureCommandException;

    /**
     * Gets the physical machine list by rack
     * 
     * @param rackId the rack identifier
     * @return the list of physical machine
     * @throws PersistenceException
     * @throws InfrastructureCommandException
     */
    public abstract List<PhysicalMachine> getPhysicalMachinesByRack(final UserSession userSession,
        final Integer rackId, final String filters) throws InfrastructureCommandException;

    /**
     * Gets the list of filtered virtual machines deployed in a single physical machine
     * 
     * @param physicalMachineId identifier of the physical machine.
     * @return the list of matching elements.
     * @throws InfrastructureCommandException ice exception
     */
    public List<VirtualmachineHB> getVirtualMachinesByPhysicalMachine(
        final UserSession userSession, final Integer physicalMachineId)
        throws InfrastructureCommandException;

    /**
     * Returns the {@link HypervisorHB} object by the physical machine one.
     * 
     * @param userSession user who performs the action.
     * @param physicalMachineId identifier of the physical machine.
     * @return the matching object.
     * @throws InfrastructureCommandException to encapsulate any kind of controlled exception
     */
    public HypervisorHB getHypervisorByPhysicalMachine(final UserSession userSession,
        final Integer physicalMachineId) throws InfrastructureCommandException;

    /**
     * Gets the available physical machine list by rack
     * 
     * @param rackId the rack identifier
     * @param enterpriseId TODO
     * @return the list of physical machine
     * @throws PersistenceException
     * @throws InfrastructureCommandException
     */
    public abstract DataResult<ArrayList<PhysicalMachine>> getAvailablePhysicalMachinesByRack(
        Integer rackId, Integer enterpriseId) throws PersistenceException,
        InfrastructureCommandException;

    /**
     * Returns all data centers contained in the data base
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract DataResult<ArrayList<DataCenter>> getDataCenters(final UserSession userSession);

    public abstract DataResult<ArrayList<DataCenter>> getAllowedDataCenters(
        final UserSession userSession);

    /**
     * Creates a new data center in the data base
     * 
     * @param dataCenter
     * @return the Data Center created in DDBB
     */
    public abstract DataResult<DataCenter> createDataCenter(final UserSession userSession,
        final DataCenter dataCenter);

    /**
     * Edits dataCenter's information in the data base
     * 
     * @param dataCenter
     * @return
     */
    public abstract BasicResult editDataCenter(final UserSession userSession,
        final DataCenter dataCenter);

    /**
     * Deletes the selected data center from the data base
     * 
     * @param dataCenter
     * @return
     */
    public abstract BasicResult deleteDataCenter(final UserSession userSession,
        final DataCenter dataCenter);

    /**
     * Creates a new rack in the data base
     */
    public abstract DataResult<Rack> createRack(final UserSession userSession, final Rack rack);

    /**
     * Deletes the rack from the data base
     * 
     * @param userSession The current user session
     * @param rack
     * @return
     */
    public abstract BasicResult deleteRack(final UserSession userSession, final Rack rack);

    /**
     * Edits rack's information in the data base
     * 
     * @param rack
     * @return
     */
    public abstract BasicResult editRack(final UserSession userSession, final Rack rack);

    /*
     * ______________________________ PHYSICAL MACHINES _______________________________
     */
    /**
     * Creates a new physical machine in the data base.
     * 
     * @param userSession the user in the session
     * @param physicalMachineCreation the infrastructure pojo object
     * @return the DataResult
     * @throws InfrastructureCommandException An Exception is thrown if there was a problem with
     *             Machine data values
     */
    public abstract DataResult<PhysicalMachineCreation> createPhysicalMachine(
        final UserSession userSession, final PhysicalMachineCreation physicalMachineCreation)
        throws InfrastructureCommandException;

    /**
     * Deletes the physical machine from the data base
     * 
     * @param userSession The current user session
     * @param physicalMachine
     * @return
     */
    public abstract BasicResult deletePhysicalMachine(final UserSession userSession,
        final PhysicalMachine physicalMachine);

    /**
     * Edits physical machine's information in the data base TODO: Possibly we need to connect
     * AbiCloud WS too, for example, when we change information related to Network Module
     * 
     * @param userSession
     * @param physicalMachineCreation A PhysicalMachineCreation object containing the
     *            PhysicalMachine that will be edited, and the list of hypervisors to be edited or
     *            created (Hypervisor deletion is not supported)
     * @return Since when we edit a PhysicalMachine, new Hypervisors can be created, this method
     *         returns a DataResult, and when success = true, data attribute will contain an
     *         ArrayList with the Hypervisors that have been created
     * @throws InfrastructureCommandException An Exception is thrown if there was a problem with
     *             Machine data values
     */
    public abstract DataResult<ArrayList<HyperVisor>> editPhysicalMachine(
        final UserSession userSession, final PhysicalMachineCreation physicalMachineCreation)
        throws InfrastructureCommandException;

    /**
     * Creates a new Hypervisor in Data Base
     * 
     * @param userSession the UserSession that called this method
     * @param hypervisor The Hypervisor that will be created in Data Base
     * @return a DataResult object containing the HyperVisor that has been created in DataBase, if
     *         the query had success
     */
    public abstract DataResult<HyperVisor> createHypervisor(final UserSession userSession,
        final HyperVisor hypervisor);

    /**
     * Edits an existing in Hypervisor in DataBase, with the information contained in parameter
     * hypervisor
     * 
     * @param userSession UserSession object with the user's session who called this method
     * @param hypervisor The hypervisor that will be edited, with the new information
     * @return A BasicResult object with the result of the edition
     */
    public abstract BasicResult editHypervisor(final UserSession userSession,
        final HyperVisor hypervisor);

    public abstract BasicResult deleteHypervisor(final HyperVisor hypervisor);

    /**
     * Creates a new Virtual Machine in the Data Base
     * 
     * @param virtualMachine A VirtualMachine object containing the necessary information to create
     *            a new Virtual Machine. UUID and State fields will be ignored, since they will be
     *            generated.
     * @return a DataResult object containing a VirtualMachine object with the Virtual Machine
     *         created
     * @deprecated not used
     */
    @Deprecated
    public abstract DataResult<VirtualMachine> createVirtualMachine(
        final VirtualMachine virtualMachine);

    /**
     * Deletes the virtual machine. 1. From the data base 2. Connect with AbiCloud WS to delete it
     * from the Physical Machine
     * 
     * @param virtualMachine
     * @return
     */
    public abstract BasicResult deleteVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Edits virtual machine's information in the data base
     * 
     * @param virtualMachine
     * @return
     */
    public abstract BasicResult editVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Performs a "Start" action in the Virtual Machine
     * 
     * @param virtualMachine
     * @return a DataResult object, with a State object that represents the state "Running"
     */
    public abstract DataResult<State> startVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Performs a "Pause" action in the Virtual Machine
     * 
     * @param virtualMachine
     * @return a DataResult object, with a State object that represents the state "Paused"
     */
    public abstract DataResult<State> pauseVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Performs a "Reboot" action in the Virtual Machine
     * 
     * @param virtualMachine
     * @return a DataResult object, with a State object that represents the state "Running"
     */
    public abstract DataResult<State> rebootVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Performs a "Shutdown" action in the Virtual Machine
     * 
     * @param virtualMachine
     * @return a DataResult object, with a State object that represents the state "Powered Off"
     */
    public abstract DataResult<State> shutdownVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Moves a virtual machine from a Physical Machine to another virtualMachine's "assignedTo"
     * attribute will contain the new HyperVisor, to which the virtual machine will be assigned.
     * 
     * @param virtualMachine
     * @return
     */
    public abstract BasicResult moveVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine);

    /**
     * Checks the current state of a list of virtual machines.
     * 
     * @param virtualMachinesToCheck ArrayList with the list of virtual machines to check
     * @return A DataResult object containing a list of the same virtual machines with their state
     *         updated
     */
    @SuppressWarnings("unchecked")
    public abstract DataResult<ArrayList<VirtualMachine>> checkVirtualMachinesState(
        final ArrayList<VirtualMachine> virtualMachinesToCheck);

    public abstract DataResult<ArrayList<HyperVisorType>> getHypervisorsTypeByDataCenter(
        final DatacenterHB dataCenter);

    /**
     * @return the factory
     */
    public abstract DAOFactory getFactory();

    /**
     * @param factory the factory to set
     */
    public abstract void setFactory(final DAOFactory factory);

    /**
     * this method updates the used resources on a physical datacenter.
     * 
     * @param dataCenter the dataCenter object
     * @return the result of the operation
     */
    public abstract BasicResult updateUsedResourcesByDatacenter(final DatacenterHB dataCenter);

    /**
     * This method checks the IP address parameter in the Physical Machine object.
     * 
     * @param ip IP Address value
     * @throws InfrastructureCommandException An Exception is thrown if there was a problem checking
     *             the IP address
     */
    public abstract void checkIPAddress(final String ip) throws InfrastructureCommandException;

    /**
     * This method checks the basic parameters in the Physical Machine object.
     * 
     * @param physicalMachine
     * @throws InfrastructureCommandException
     */
    public abstract void checkPhysicalMachineData(final PhysicalMachine physicalMachine)
        throws InfrastructureCommandException;

    /**
     * Forces an state refresh in the virtual machine
     * 
     * @param virtualAppliance the virtual appliances to refresh
     * @return BasicResult with the operation result
     */
    public abstract BasicResult forceRefreshVirtualMachineState(final VirtualMachine virtualMachine);

    /**
     * Check that all remote service fields are filled.
     * 
     * @param remoteService The remote service to check.
     * @return The validation result.
     */
    public abstract boolean validateRemoteService(final RemoteServiceHB remoteService);

    /**
     * Checks if another DataCenter exists with the same name as provided
     * 
     * @param name name to check
     * @return
     */
    public abstract boolean checkExistingDataCenterNames(final String name)
        throws PersistenceException;

    /**
     * Checks the virtual infrastructure related to the physical machine
     * 
     * @param physicalMachineId the physical machine identifier to check the virtual infrastructure
     * @param userSession
     * @param isAutomaticCheck TODO
     * @return
     */
    public BasicResult checkVirtualInfrastructureState(final Integer physicalMachineId,
        UserSession userSession, Boolean isAutomaticCheck);

    /**
     * This method deletes the reference of a vMachine in a pMachine
     * 
     * @param vMachine the vMachine to update
     */
    public void deletePhysicalMachineReference(VirtualmachineHB vMachine, UserSession user);

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualDataCentersByEnterprise(com
     * .abiquo.abiserver.pojo.user.Enterprise)
     */
    public DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterprise(
        final UserSession userSession, final Enterprise enterprise);

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * getVirtualDataCentersByEnterpriseAndDatacenter(com.abiquo.abiserver.pojo.user.Enterprise,
     * com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    public DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterpriseAndDatacenter(
        final UserSession userSession, final Enterprise enterprise, final DataCenter datacenter);
}
