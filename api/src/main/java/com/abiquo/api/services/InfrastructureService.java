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

package com.abiquo.api.services;

import java.beans.PropertyDescriptor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.services.stub.NodecollectorServiceStub;
import com.abiquo.api.services.stub.VsmServiceStub;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.model.util.AddressingUtils;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachinesToCreateDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.UcsRack;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.softwarementors.commons.collections.ListUtils;

/*
 *  THIS CLASS RESOURCE IS USED AS THE DEFAULT ONE TO DEVELOP THE REST AND 
 *  FOR THIS REASON IS OVER-COMMENTED AND DOESN'T HAVE JAVADOC! PLEASE DON'T COPY-PASTE ALL OF THIS
 *  COMMENTS BECAUSE IS WILL BE SO UGLY TO MAINTAIN THE CODE IN THE API!
 *
 */

// Annotate it as a @Service and set the default @Transactional method attributes.
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class InfrastructureService extends DefaultApiService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureService.class);

    public static final String CHECK_RESOURCE = "check";

    @Autowired
    protected InfrastructureRep repo;

    @Autowired
    protected RemoteServiceService remoteServiceService;

    @Autowired
    protected VirtualMachineService virtualMachineService;

    @Autowired
    protected MachineService machineService;

    @Autowired
    protected NodecollectorServiceStub nodecollectorServiceStub;

    @Autowired
    protected VsmServiceStub vsmServiceStub;

    @Autowired
    protected VirtualDatacenterRep vdcRep;

    public InfrastructureService()
    {

    }

    public InfrastructureService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        vdcRep = new VirtualDatacenterRep(em);
        remoteServiceService = new RemoteServiceService(em);
        virtualMachineService = new VirtualMachineService(em);
        tracer = new TracerLogger();
    }

    public InfrastructureService(final EntityManager em,
        final NodecollectorServiceStub ncserviceStub)
    {
        this(em);
        nodecollectorServiceStub = ncserviceStub;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack addRack(final Rack rack, final Integer datacenterId)
    {
        Datacenter datacenter = this.getDatacenter(datacenterId);

        // Check if there is a rack with the same name in the Datacenter
        if (repo.existsAnyRackWithName(datacenter, rack.getName()))
        {
            tracer.log(SeverityType.MINOR, ComponentType.RACK, EventType.RACK_CREATE,
                "rack.duplicatedname", rack.getName());
            addConflictErrors(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        // Set the default values if they are not initialized.
        if (rack.getVlanIdMin() == null)
        {
            rack.setVlanIdMin(Rack.VLAN_ID_MIN_DEFAULT_VALUE);
        }
        if (rack.getVlanIdMax() == null)
        {
            rack.setVlanIdMax(Rack.VLAN_ID_MAX_DEFAULT_VALUE);
        }
        if (rack.getVlanPerVdcReserved() == null)
        {
            rack.setVlanPerVdcReserved(Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE);
        }
        if (rack.getNrsq() == null)
        {
            rack.setNrsq(Rack.NRSQ_DEFAULT_VALUE);
        }
        if (rack.getNrsq() == null)
        {
            rack.setNrsq(Rack.NRSQ_DEFAULT_VALUE);
        }

        // Set the datacenter that belongs to
        rack.setDatacenter(datacenter);

        // Call the inherited 'validate' function in the DefaultApiService
        validate(rack);
        repo.insertRack(rack);

        tracer.log(SeverityType.INFO, ComponentType.RACK, EventType.RACK_CREATE, "rack.created",
            rack.getName());

        return rack;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<Machine> addMachines(final List<Machine> machinesToCreate,
        final Integer datacenterId, final Integer rackId)
    {
        List<Machine> machinesCreated = new ArrayList<Machine>();
        for (Machine currentMachine : machinesToCreate)
        {
            machinesCreated.add(addMachine(currentMachine, datacenterId, rackId));
        }

        return machinesCreated;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> addMachines(final Integer datacenterId, final Integer rackId,
        final MachinesToCreateDto createInfo) throws Exception
    {

        validateCreateInfo(createInfo);

        return addMachines(datacenterId, rackId, createInfo.getIpFrom(), createInfo.getIpTo(),
            createInfo.getHypervisor(), createInfo.getUser(), createInfo.getPassword(), createInfo
                .getPort(), createInfo.getvSwitch());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> addMachines(final Integer datacenterId, final Integer rackId,
        final String ipFrom, final String ipTo, final String hypervisor, final String user,
        final String password, final Integer port, final String vSwitch)
    {
        List<Machine> createdMachines = new ArrayList<Machine>();

        // Create the IPAddress objects again to check the IP correct format
        IPAddress ipFromOK = IPAddress.newIPAddress(ipFrom.toString());
        IPAddress ipToOK = IPAddress.newIPAddress(ipTo.toString());

        if (ipFromOK.isBiggerThan(ipToOK))
        {
            addConflictErrors(new CommonError(APIError.MACHINE_INVALID_IP_RANGE.getCode(),
                "IP From can not be bigger than IP To!"));
        }

        // prepare NODE COLLECTOR
        // Datacenter datacenter = getDatacenter(datacenterId);
        // RemoteService nodecollector =
        // getRemoteService(datacenter.getId(), RemoteServiceType.NODE_COLLECTOR);

        // getting machines
        HypervisorType hyType = HypervisorType.fromValue(hypervisor);
        List<Machine> discoveredMachines =
        // nodecollectorServiceStub.getRemoteHypervisors(nodecollector, ipFromOK, ipToOK, hyType,
            // user, password, port);
            this.discoverRemoteHypevisors(datacenterId, ipFromOK, ipToOK, hyType, user, password,
                port, vSwitch);

        Map<String, Object> map = new HashMap<String, Object>();
        Set<CommonError> errors = new HashSet<CommonError>();
        // saving machines
        for (Machine machine : discoveredMachines)
        {
            try
            {
                enableMaxFreeSpaceDatastore(machine);
                machine.setVirtualSwitch(vSwitch);
                Machine m = addMachine(machine, datacenterId, rackId);
                createdMachines.add(m);
            }
            catch (APIException ex)
            {
                errors.addAll(addIpInErrors(ex.getErrors(), machine.getHypervisor().getIp()));
            }
        }

        map.put("machines", createdMachines);
        if (!errors.isEmpty())
        {
            map.put("errors", errors);
        }
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Machine addMachine(final Machine machine, final Integer datacenterId,
        final Integer rackId)
    {
        machine.setId(null);

        // Gets the rack. It throws the NotFoundException if needed.
        Rack rack = getRack(datacenterId, rackId);
        Datacenter datacenter = rack.getDatacenter();

        UcsRack ucsRack = repo.findUcsRackById(rackId);
        if (ucsRack != null)
        {
            addConflictErrors(APIError.MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK);
            flushErrors();
        }

        checkAvailableCores(machine);

        Boolean anyEnabled = Boolean.FALSE;
        for (Datastore datastore : machine.getDatastores())
        {
            if (datastore.isEnabled())
            {
                anyEnabled = Boolean.TRUE;
            }

            validate(datastore);

            // updates shared datastores
            List<Datastore> datastoresShared = repo.findShares(datastore);
            for (Datastore dstore : datastoresShared)
            {
                dstore.setSize(datastore.getSize());
                dstore.setUsedSize(datastore.getUsedSize());
            }

            repo.insertDatastore(datastore);
        }

        if (!anyEnabled)
        {
            addValidationErrors(APIError.MACHINE_ANY_DATASTORE_DEFINED);
            flushErrors();
        }

        // Insert the machine into database
        machine.setDatacenter(datacenter);
        machine.setRack(rack);

        if (machine.getVirtualSwitch().contains("/"))
        {
            addValidationErrors(APIError.MACHINE_INVALID_VIRTUAL_SWITCH_NAME);
            flushErrors();
        }

        validate(machine.getHypervisor());

        // [ABICLOUDPREMIUM-2996] These values cannot be changed. Must always reflect the real ones.
        // Even if the POST to create the machine was made with the information from NodeCollector,
        // we need to make sure those values have not been changed.
        Machine remoteMachine =
            discoverRemoteHypervisor(datacenterId, IPAddress.newIPAddress(machine.getHypervisor()
                .getIp()), machine.getHypervisor().getType(), machine.getHypervisor().getUser(),
                machine.getHypervisor().getPassword(), machine.getHypervisor().getPort());
        machine.setState(remoteMachine.getState());
        machine.setVirtualRamInMb(remoteMachine.getVirtualRamInMb());
        machine.setVirtualCpuCores(remoteMachine.getVirtualCpuCores());

        validate(machine);
        // Part 2: Insert the and machine into database.
        if (repo
            .existAnyHypervisorWithIpInDatacenter(machine.getHypervisor().getIp(), datacenterId))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_IP);
        }

        if (repo.existAnyHypervisorWithIpServiceInDatacenter(
            machine.getHypervisor().getIpService(), datacenterId))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_SERVICE_IP);
        }
        flushErrors();

        repo.insertMachine(machine);
        if (machine.getHypervisor().getId() == null || machine.getHypervisor().getId().equals(0))
        {
            repo.insertHypervisor(machine.getHypervisor());
        }

        // Get the remote service to monitor the machine
        RemoteService vsmRS =
            getRemoteService(datacenter.getId(), RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
        vsmServiceStub.monitor(vsmRS, machine.getHypervisor());

        tracer.log(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_CREATE,
            "machine.created", machine.getName(), machine.getHypervisor().getIp(), machine
                .getHypervisor().getType(), machine.getState());

        if (machine.getInitiatorIQN() == null)
        {
            tracer.log(SeverityType.WARNING, ComponentType.MACHINE, EventType.MACHINE_CREATE,
                "machine.withoutiqn", machine.getName(), machine.getHypervisor().getIp());
        }
        return machine;
    }

    // Return a rack.
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Rack getRack(final Integer datacenterId, final Integer rackId)
    {
        // Find the rack by itself and by its datacenter.
        Rack rack = repo.findRackByIds(datacenterId, rackId);
        if (rack == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_RACK);
            flushErrors();
        }
        return rack;
    }

    public Rack getRackById(final Integer rackId)
    {
        // Find the rack by itself and by its datacenter.
        Rack rack = repo.findRackById(rackId);
        if (rack == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_RACK);
            flushErrors();
        }
        return rack;
    }

    // GET the list of Racks by Datacenter.
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Rack> getRacksByDatacenter(final Integer datacenterId)
    {
        return getRacksByDatacenter(datacenterId, null);
    }

    // GET the list of Racks by Datacenter.
    public List<Rack> getRacksByDatacenter(final Integer datacenterId, final String filter)
    {
        // get the datacenter.
        Datacenter datacenter = this.getDatacenter(datacenterId);
        return repo.findRacks(datacenter, filter);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(datacenterId, rackId);

        return isAssignedTo(datacenterId, rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Rack rack)
    {
        return rack != null && rack.getDatacenter().getId().equals(datacenterId);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isAssignedTo(final Integer datacenterId, final RemoteServiceType type)
    {
        RemoteService remoteService = null;

        if (type != null)
        {
            remoteService = getRemoteService(datacenterId, type);
        }

        return type != null && remoteService != null
            && remoteService.getDatacenter().getId().equals(datacenterId);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isAssignedTo(final Integer datacenterId, final String remoteServiceMapping)
    {
        RemoteServiceType type =
            RemoteServiceType.valueFromName(remoteServiceMapping.toUpperCase());

        return isAssignedTo(datacenterId, type);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack modifyRack(final Integer datacenterId, final Integer rackId, final Rack rack)
    {
        Rack old = getRack(datacenterId, rackId);

        // Check
        if (repo.existsAnyOtherRackWithName(old, rack.getName()))
        {
            tracer.log(SeverityType.MINOR, ComponentType.RACK, EventType.RACK_CREATE,
                "rack.duplicatedname", rack.getName());
            addConflictErrors(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(rack.getName());
        old.setShortDescription(rack.getShortDescription());
        old.setLongDescription(rack.getLongDescription());
        old.setHaEnabled(rack.isHaEnabled());

        if (hasVlanConfig(rack))
        {
            old.setNrsq(rack.getNrsq());
            old.setVlanIdMax(rack.getVlanIdMax());
            old.setVlanIdMin(rack.getVlanIdMin());
            old.setVlanPerVdcReserved(rack.getVlanPerVdcReserved());
            old.setVlansIdAvoided(rack.getVlansIdAvoided());
        }

        validate(old);
        repo.updateRack(old);

        tracer.log(SeverityType.INFO, ComponentType.RACK, EventType.RACK_MODIFY, "rack.updated",
            old.getName(), rack.getName(), rack.getShortDescription(), rack.isHaEnabled() ? "yes"
                : "no");

        return old;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void removeRack(final Rack rack)
    {
        removeRack(rack, false);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void removeRack(final Rack rack, final boolean force)
    {

        List<Machine> machines = getMachines(rack);
        if (machines != null)
        {
            for (Machine machine : machines)
            {
                if (machine.getHypervisor() != null)
                {
                    machineService.removeMachine(machine.getId(), force);
                }
            }
        }

        deleteMachineRulesFromRack(rack);

        repo.deleteRack(rack);
        tracer.log(SeverityType.INFO, ComponentType.RACK, EventType.RACK_DELETE, "rack.deleted",
            rack.getName());
    }

    protected void deleteMachineRulesFromRack(final Rack rack)
    {
        // PREMIUM
    }

    public List<Machine> getMachines(final Rack rack)
    {
        return repo.findRackMachines(rack);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<RemoteService> getRemoteServices()
    {
        return repo.findAllRemoteServices();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<RemoteService> getRemoteServicesByDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = repo.findById(datacenterId);
        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return repo.findRemoteServicesByDatacenter(datacenter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public RemoteServiceDto addRemoteService(final RemoteService rs, final Integer datacenterId)
    {
        return remoteServiceService.addRemoteService(rs, datacenterId);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public RemoteService getRemoteService(final Integer id)
    {
        return repo.findRemoteServiceById(id);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public RemoteService getRemoteService(final Integer datacenterId, final RemoteServiceType type)
    {
        Datacenter datacenter = repo.findById(datacenterId);
        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        List<RemoteService> services = repo.findRemoteServiceWithTypeInDatacenter(datacenter, type);
        RemoteService remoteService = null;

        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            remoteService = services.get(0);
        }
        else
        {
            addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
            flushErrors();
        }

        return remoteService;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public RemoteServiceDto modifyRemoteService(final Integer id, final RemoteServiceDto dto)
        throws URISyntaxException
    {
        return remoteServiceService.modifyRemoteService(id, dto);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeRemoteService(final Integer id)
    {
        remoteServiceService.removeRemoteService(id);
    }

    /*
     * Get the Datacenter and check if it exists.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Datacenter getDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = repo.findById(datacenterId);

        if (datacenter == null)
        {
            // Adding the NON_EXISTENT_DATACENTER to the list of NotFoundErrors and flush them.
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return datacenter;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Repository getRepositoryFromLocation(final String location)
    {
        return repo.findRepositoryByLocation(location);
    }

    // @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    // public Integer getDatacenterIdByRepository(Repository repository)
    // {
    // return repository.getDatacenter().getId();
    // }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Repository getRepository(final Datacenter dc)
    {
        Repository rep = repo.findRepositoryByDatacenter(dc);
        if (rep == null)
        {
            addConflictErrors(APIError.VIMAGE_DATACENTER_REPOSITORY_NOT_FOUND);
            flushErrors();
        }
        return rep;
    }

    public Collection<VirtualMachine> getVirtualMachinesByMachine(final Integer machineId)
    {
        Machine machine = repo.findMachineById(machineId);
        return virtualMachineService.findByHypervisor(machine.getHypervisor());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUsedResourcesByMachine(final Integer machineId)
    {
        Machine machine = repo.findMachineById(machineId);
        updateUsedResourcesByMachine(machine);
    }

    public void updateUsedResourcesByMachine(final Machine machine)
    {
        Collection<VirtualMachine> vms = getVirtualMachinesByMachine(machine.getId());

        Integer ramUsed = 0;
        Integer cpuUsed = 0;
        long hdUsed = 0;

        for (VirtualMachine vm : vms)
        {
            if (vm.getState() != null && !vm.getState().equals(VirtualMachineState.NOT_ALLOCATED))
            {
                ramUsed += vm.getRam();
                cpuUsed += vm.getCpu();
                hdUsed += vm.getHdInBytes();
            }
        }

        machine.setVirtualRamUsedInMb(ramUsed);
        machine.setVirtualCpusUsed(cpuUsed);

        repo.updateMachine(machine);
    }

    public boolean hasVlanConfig(final Rack rack)
    {
        return rack.getNrsq() != null && rack.getVlanIdMax() != null && rack.getVlanIdMin() != null
            && rack.getVlanPerVdcReserved() != null;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public HypervisorType discoverRemoteHypervisorType(final Integer datacenterId, final String ip)
    {
        Datacenter datacenter = getDatacenter(datacenterId);
        RemoteService nodecollector =
            getRemoteService(datacenter.getId(), RemoteServiceType.NODE_COLLECTOR);
        return nodecollectorServiceStub.getRemoteHypervisorType(nodecollector, ip);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Machine discoverRemoteHypervisor(final Integer datacenterId, final IPAddress ip,
        final HypervisorType hypType, final String user, final String password, final Integer port)
    {
        Datacenter datacenter = getDatacenter(datacenterId);
        RemoteService nodecollector =
            getRemoteService(datacenter.getId(), RemoteServiceType.NODE_COLLECTOR);

        Machine machine =
            nodecollectorServiceStub.getRemoteHypervisor(nodecollector, ip, hypType, user,
                password, port);

        return machine;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Machine> discoverRemoteHypevisors(final Integer datacenterId,
        final IPAddress ipFrom, final IPAddress ipTo, final HypervisorType hypType,
        final String user, final String password, final Integer port, final String vswitch)
    {
        Datacenter datacenter = getDatacenter(datacenterId);
        RemoteService nodecollector =
            getRemoteService(datacenter.getId(), RemoteServiceType.NODE_COLLECTOR);

        List<Machine> candidateMachines =
            nodecollectorServiceStub.getRemoteHypervisors(nodecollector, ipFrom, ipTo, hypType,
                user, password, port);
        if (vswitch != null)
        {
            candidateMachines = filterByVSwitch(candidateMachines, vswitch);
        }
        // We do not allow machines from ucs to be added if already exists in Abiquo.
        return excludeAlreadyInAbiquo(datacenterId, candidateMachines);
    }

    public MachineState checkMachineState(final Integer datacenterId, final String ip,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer port)
    {

        RemoteService nodecollector =
            getRemoteService(datacenterId, RemoteServiceType.NODE_COLLECTOR);

        // getting all Virtual Machines from Hypervisor (managed and not managed) (NODE COLLETOR)
        Machine m =
            nodecollectorServiceStub.getRemoteHypervisor(nodecollector, IPAddress.newIPAddress(ip),
                hypervisorType, user, password, port);
        return m.getState();
    }

    public void isStonithUp(final Integer datacenterId, final String ip, final String user,
        final String password, final Integer port)
    {

        RemoteService nodecollector =
            getRemoteService(datacenterId, RemoteServiceType.NODE_COLLECTOR);

        if (!nodecollectorServiceStub.isStonithUp(nodecollector, ip, port, user, password))
        {
            addValidationErrors(APIError.MACHINE_INVALID_IPMI_CONF);
            flushErrors();
        }
    }

    public void checkAvailableCores(final Machine machine)
    {
        // PREMIUM
    }

    // ----------------- //
    // ---- PRIVATE ---- //
    // ----------------- //

    private void enableMaxFreeSpaceDatastore(final Machine machine)
    {
        if (machine.getDatastores() != null && !machine.getDatastores().isEmpty())
        {
            Datastore datastoreToEnable = null;
            long freeSpace = 0;
            for (Datastore d : machine.getDatastores())
            {
                if (freeSpace < d.getSize() - d.getUsedSize())
                {
                    freeSpace = d.getSize() - d.getUsedSize();
                    datastoreToEnable = d;
                }
            }

            if (datastoreToEnable != null)
            {
                datastoreToEnable.setEnabled(true);
            }
            else
            {
                // if any datastore has free space, select the first
                machine.getDatastores().get(0).setEnabled(true);
            }
        }
        else
        {
            // no datastores to enable
            addConflictErrors(APIError.MACHINE_ANY_DATASTORE_DEFINED);
            flushErrors();
        }

    }

    private Set<CommonError> addIpInErrors(final Set<CommonError> errors, final String ip)
    {
        Set<CommonError> newErrors = new HashSet<CommonError>();

        if (errors != null && !errors.isEmpty())
        {
            for (CommonError commonError : errors)
            {
                CommonError newError =
                    new CommonError(commonError.getCode(), "[" + ip + "] "
                        + commonError.getMessage());
                newErrors.add(newError);
            }
        }

        return newErrors;
    }

    /**
     * @param datacenterId Datacenter id.
     * @param candidateMachines machines that we retrieved.
     * @return List<Machine> that are not in Abiquo.
     */
    protected List<Machine> excludeAlreadyInAbiquo(final Integer datacenterId,
        final List<Machine> candidateMachines)
    {
        List<Machine> machines = new ArrayList<Machine>();
        // We do not allow machines from ucs to be added if already exists in Abiquo.
        for (Machine m : candidateMachines)
        {
            if (repo.existAnyHypervisorWithIpInDatacenter(m.getHypervisor().getIp(), datacenterId))
            {
                LOGGER
                    .warn("Discovering blades: There is a machine already in Abiquo with this ip!!");
                continue;
            }
            machines.add(m);
        }
        return machines;
    }

    /**
     * Filters the candidateMachines into the ones that have the vswitch with @param vswitch.
     * 
     * @param candidateMachines candidate machines to be returned.
     * @param vswitch name of the switch to filter
     */
    protected List<Machine> filterByVSwitch(final List<Machine> candidateMachines,
        final String vswitch)
    {
        List<Machine> filteredVSwitch = new ArrayList<Machine>();

        // Filter the machines that doesn't have the switch name
        for (Machine currentMachine : candidateMachines)
        {
            String[] switches = currentMachine.getVirtualSwitch().split("/");
            for (String currentSwitch : switches)
            {
                if (currentSwitch.equalsIgnoreCase(vswitch))
                {
                    // The machine has the vswitch!
                    currentMachine.setVirtualSwitch(vswitch);
                    filteredVSwitch.add(currentMachine);
                }
            }
        }

        return filteredVSwitch;
    }

    private void validateCreateInfo(final MachinesToCreateDto dto) throws Exception
    {
        String[] properties =
            {"ipFrom", "ipTo", "hypervisor", "user", "password", "port", "vSwitch"};

        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(dto.getClass()))
        {
            if (ListUtils.createList(properties).contains(pd.getName()))
            {
                if (pd.getReadMethod().invoke(dto) == null)
                {
                    addValidationErrors(new CommonError(APIError.STATUS_BAD_REQUEST.getCode(), pd
                        .getName()
                        + " can't be null"));
                    flushErrors();
                }
            }
        }

        if (!AddressingUtils.isValidIP(dto.getIpFrom()))
        {
            addValidationErrors(new CommonError(APIError.STATUS_BAD_REQUEST.getCode(),
                "Invalid ip From"));
            flushErrors();
        }

        if (!AddressingUtils.isValidIP(dto.getIpTo()))
        {
            addValidationErrors(new CommonError(APIError.STATUS_BAD_REQUEST.getCode(),
                "Invalid ip To"));
            flushErrors();
        }

        if (IPAddress.newIPAddress(dto.getIpFrom()).isBiggerThan(
            IPAddress.newIPAddress(dto.getIpTo())))
        {
            addValidationErrors(new CommonError(APIError.STATUS_BAD_REQUEST.getCode(),
                "ip From can't be bigger than ip To"));
            flushErrors();
        }
    }

    /**
     * Checks one by one all {@link RemoteService} associated with the @{link Datacenter}.
     * 
     * @param datacenterId
     * @return ErrorsDto
     */
    public ErrorsDto checkRemoteServiceStatusByDatacenter(final Datacenter datacenter)
    {

        List<RemoteService> remoteServicesByDatacenter =
            getRemoteServicesByDatacenter(datacenter.getId());
        ErrorsDto errors = new ErrorsDto();

        for (RemoteService r : remoteServicesByDatacenter)
        {
            ErrorsDto checkRemoteServiceStatus =
                remoteServiceService.checkRemoteServiceStatus(datacenter, r.getType(), r.getUri());
            errors.addAll(checkRemoteServiceStatus);
        }
        return errors;
    }

    /**
     * Returns a single virtual machine based on its infrastructure properties and its id.
     * 
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the physical machine
     * @param vmId identifier of the virtual machine
     * @return the object {@link VirtualMachine} found.
     */
    public VirtualMachine getVirtualMachineFromInfrastructure(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer vmId)
    {
        /** check if the machine exists. */
        Machine pm = repo.findMachineByIds(datacenterId, rackId, machineId);
        if (pm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        return virtualMachineService.getVirtualMachineByHypervisor(pm.getHypervisor(), vmId);
    }

    /**
     * Returns the list of virtual machines based on its infrastructure deployment site.
     * 
     * @param datacenterId identifier of the datacenter.
     * @param rackId identifier of the rack
     * @param machineId identifier of the machin
     * @return the list of {@link VirtualMachine} deployed in the physical machine.
     */
    public List<VirtualMachine> getVirtualMachinesFromInfrastructure(final Integer datacenterId,
        final Integer rackId, final Integer machineId)
    {
        /** check if the machine exists. */
        Machine pm = repo.findMachineByIds(datacenterId, rackId, machineId);
        if (pm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        return (List<VirtualMachine>) virtualMachineService.findByHypervisor(pm.getHypervisor());
    }

    /**
     * Deletes machine non managed by abiquo.
     * 
     * @param datacenterId
     * @param rackId
     * @param machineId
     * @param trace
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteNotManagedVirtualMachines(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {

        if (!machineService.isAssignedTo(datacenterId, rackId, machineId))
        {
            addNotFoundErrors(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
            flushErrors();
        }

        Hypervisor hypervisor = machineService.getMachine(machineId).getHypervisor();

        if (hypervisor == null)
        {
            addNotFoundErrors(APIError.VIRTUAL_MACHINE_WITHOUT_HYPERVISOR);
            flushErrors();
        }

        deleteNotManagedVirtualMachines(hypervisor);
        updateUsedResourcesByMachine(machineId);

        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.MACHINE,
                EventType.MACHINE_DELETE_VMS_NOTMANAGED, "virtualMachine.notManagedVMDeleted",
                hypervisor.getIp());
        }
    }

    public ErrorsDto checkRemoteServiceStatus(final Datacenter datacenter,
        final RemoteServiceType type, final String url)
    {
        return checkRemoteServiceStatus(datacenter, type, url, false);
    }

    public ErrorsDto checkRemoteServiceStatus(final Datacenter datacenter,
        final RemoteServiceType type, final String url, final boolean flushErrors)
    {
        return remoteServiceService.checkRemoteServiceStatus(datacenter, type, url, flushErrors);
    }

    protected void deleteNotManagedVirtualMachines(final Hypervisor hypervisor)
    {
        List<VirtualMachine> vmachines = repo.getNotManagedVirtualMachines(hypervisor);
        RemoteService vsm =
            getRemoteService(hypervisor.getMachine().getDatacenter().getId(),
                RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
        for (VirtualMachine vm : vmachines)
        {
            try
            {
                vsmServiceStub.unsubscribe(vsm, vm);
            }
            catch (InternalServerErrorException ex)
            {
                LOGGER
                    .error(String
                        .format(
                            "An unexpected error has ocurred when try to unsubscribe the virtual machine '%s', it probably unsubscribed yet",
                            vm.getName()));
            }
            vdcRep.deleteVirtualMachine(vm);
        }
    }

    /**
     * We check how many empty machines are in a rack. Then we power on or off to fit the
     * configuration. In 2.0 only in {@link UcsRack}.
     * 
     * @param Rack we are deploy void
     * @since 2.0
     */
    public void adjustPoweredMachinesInRack(final Rack rack)
    {
        // PREMIUM
    }

    protected void powerOnMachine(final List<Machine> machines)
    {
        // PREMIUM
    }

    protected void shutDownMachine(final List<Machine> machines)
    {
        // PREMIUM

    }

    public Machine powerOn(final int machineId)
    {
        // XXX community impl
        LOGGER.error("[powerOn] community not implemented");
        return null;

    }

    public Machine powerOff(final int machineId, final MachineState state)
    {
        // XXX community impl
        LOGGER.error("[powerOff] community not implemented");
        return null;
    }

    @Transactional(readOnly = true)
    public VirtualAppliance getVirtualApplianceFromVirtualMachineHelper(final VirtualMachine vm)
    {
        return vdcRep.findVirtualApplianceByVirtualMachine(vm);
    }
}
