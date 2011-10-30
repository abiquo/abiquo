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

import static com.abiquo.api.resources.RemoteServiceResource.createTransferObject;
import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_ERROR;
import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_SUCCESS;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.services.stub.NodecollectorServiceStub;
import com.abiquo.api.services.stub.VsmServiceStub;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.UcsRack;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

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

    public InfrastructureService()
    {

    }

    public InfrastructureService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        remoteServiceService = new RemoteServiceService(em);
        virtualMachineService = new VirtualMachineService(em);
        machineService = new MachineService(em);
        tracer = new TracerLogger();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack addRack(final Rack rack, final Integer datacenterId)
    {
        Datacenter datacenter = this.getDatacenter(datacenterId);

        // Check if there is a rack with the same name in the Datacenter
        if (repo.existsAnyRackWithName(datacenter, rack.getName()))
        {
            tracer.log(SeverityType.MINOR, ComponentType.RACK, EventType.RACK_CREATE,
                "Rack with duplicated name " + rack.getName());
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

        tracer.log(SeverityType.INFO, ComponentType.RACK, EventType.RACK_CREATE,
            "Rack '" + rack.getName() + "' has been created succesfully");

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
        Datacenter datacenter = getDatacenter(datacenterId);
        RemoteService nodecollector =
            getRemoteService(datacenter.getId(), RemoteServiceType.NODE_COLLECTOR);

        // getting machines
        HypervisorType hyType = HypervisorType.fromValue(hypervisor);
        List<Machine> discoveredMachines =
            nodecollectorServiceStub.getRemoteHypervisors(nodecollector, ipFromOK, ipToOK, hyType,
                user, password, port);

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
            repo.insertDatastore(datastore);
        }

        if (!anyEnabled)
        {
            addValidationErrors(APIError.MACHINE_ANY_DATASTORE_DEFINED);
            flushErrors();
        }

        // Insert the machine into database
        machine.setVirtualCpusPerCore(1);
        machine.setDatacenter(datacenter);
        machine.setRack(rack);

        if (machine.getVirtualSwitch().contains("/"))
        {
            addValidationErrors(APIError.MACHINE_INVALID_VIRTUAL_SWITCH_NAME);
            flushErrors();
        }

        validate(machine.getHypervisor());
        validate(machine);

        // Part 2: Insert the and machine into database.
        if (repo.existAnyHypervisorWithIp(machine.getHypervisor().getIp()))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_IP);
        }

        if (repo.existAnyHypervisorWithIpService(machine.getHypervisor().getIpService()))
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
        vsmServiceStub.monitor(vsmRS.getUri(), machine.getHypervisor().getIp(), machine
            .getHypervisor().getPort(), machine.getHypervisor().getType().name(), machine
            .getHypervisor().getUser(), machine.getHypervisor().getPassword());

        tracer.log(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_CREATE, "Machine '"
            + machine.getName() + "' has been created succesfully");

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
        RemoteServiceType type = RemoteServiceType.valueOf(remoteServiceMapping.toUpperCase());

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
                "Rack with duplicated name " + rack.getName());
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

        tracer.log(
            SeverityType.INFO,
            ComponentType.RACK,
            EventType.RACK_MODIFY,
            "Rack '" + old.getName() + "' has been modified [Name: " + rack.getName()
                + ", Short description: " + rack.getShortDescription() + ", HA enabled: "
                + (rack.isHaEnabled() ? "yes" : "no") + "]");

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
                    virtualMachineService.deleteNotManagedVirtualMachines(machine.getHypervisor());
                    machineService.removeMachine(machine.getId(), force);
                }
            }
        }

        repo.deleteRack(rack);
        tracer.log(SeverityType.INFO, ComponentType.RACK, EventType.RACK_DELETE,
            "Rack " + rack.getName() + " deleted");
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
        RemoteService old = getRemoteService(id);

        ErrorsDto configurationErrors = checkRemoteServiceStatus(dto.getType(), dto.getUri());
        int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        dto.setStatus(status);

        if (dto.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            checkModifyApplianceManager(old, dto);
        }

        old.setUri(dto.getUri());
        old.setType(dto.getType());
        old.setStatus(dto.getStatus());

        repo.updateRemoteService(old);

        RemoteServiceDto responseDto = createTransferObject(old);

        if (!configurationErrors.isEmpty())
        {
            responseDto.setConfigurationErrors(configurationErrors);
        }

        tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.REMOTE_SERVICES_UPDATE,
            dto.getType().getName() + " updated");

        return responseDto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeRemoteService(final Integer id)
    {
        RemoteService remoteService = getRemoteService(id);

        checkRemoteServiceStatusBeforeRemoving(remoteService);

        repo.deleteRemoteService(remoteService);

        tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.REMOTE_SERVICES_DELETE,
            remoteService.getType().getName() + " deleted");
    }

    protected void checkRemoteServiceStatusBeforeRemoving(final RemoteService remoteService)
    {
        if (remoteService.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            if (repo.isRepositoryBeingUsed(remoteService.getDatacenter()))
            {
                addConflictErrors(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
                flushErrors();
            }
        }
        if (remoteService.getType() == RemoteServiceType.DHCP_SERVICE
            || remoteService.getType() == RemoteServiceType.VIRTUAL_SYSTEM_MONITOR)
        {
            if (repo.existDeployedVirtualMachines(remoteService.getDatacenter()))
            {
                addConflictErrors(APIError.REMOTE_SERVICE_IS_BEING_USED);
                flushErrors();
            }
        }
    }

    public ErrorsDto checkRemoteServiceStatus(final RemoteServiceType type, final String url)
    {
        ErrorsDto configurationErrors = new ErrorsDto();
        if (type.canBeChecked())
        {
            ClientConfig config = new ClientConfig();
            config.connectTimeout(5000);

            RestClient restClient = new RestClient(config);
            Resource checkResource =
                restClient.resource(UriHelper.appendPathToBaseUri(url, CHECK_RESOURCE));

            try
            {
                ClientResponse response = checkResource.get();
                if (response.getStatusCode() != 200)
                {
                    APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                    configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                        + error.getMessage()));
                }
            }
            catch (WebApplicationException e)
            {
                APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                    + error.getMessage() + ", " + e.getMessage()));
            }
            catch (ClientRuntimeException e)
            {
                APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                    + error.getMessage() + ", " + e.getMessage()));
            }
            catch (Exception e)
            {
                APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                    + error.getMessage() + ", " + e.getMessage()));
            }
        }
        return configurationErrors;
    }

    // PROTECTED METHODS
    protected void checkUniqueness(final Datacenter datacenter, final RemoteServiceDto remoteService)
    {
        if (remoteService.getType().checkUniqueness())
        {
            try
            {
                if (repo.existAnyRemoteServiceWithUri(remoteService.getUri()))
                {
                    addConflictErrors(APIError.REMOTE_SERVICE_URL_ALREADY_EXISTS);
                    flushErrors();
                }
            }
            catch (URISyntaxException e)
            {
                addValidationErrors(APIError.REMOTE_SERVICE_MALFORMED_URL);
                flushErrors();
            }
        }
        else if (repo
            .existAnyRemoteServiceWithTypeInDatacenter(datacenter, remoteService.getType()))
        {
            addConflictErrors(APIError.REMOTE_SERVICE_TYPE_EXISTS);
            flushErrors();
        }
    }

    /**
     * Configure the Datacenter repository based on the ''repositoryLocation'' consulted from AM.
     */
    protected ErrorsDto createApplianceManager(final Datacenter datacenter,
        final RemoteService remoteService)
    {
        int previousStatus = remoteService.getStatus();

        ErrorsDto configurationErrors = new ErrorsDto();
        if (repo.isRepositoryBeingUsed(datacenter))
        {
            remoteService.setStatus(STATUS_ERROR);

            APIError error = APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE;
            configurationErrors.add(new ErrorDto(error.getCode(), error.getMessage()));
            return configurationErrors;
        }

        if (previousStatus == STATUS_SUCCESS)
        {
            String repositoryLocation = null;

            try
            {
                ApplianceManagerResourceStubImpl amStub =
                    new ApplianceManagerResourceStubImpl(remoteService.getUri());

                repositoryLocation = amStub.getRepositoryConfiguration().getRepositoryLocation();

                if (repo.existRepositoryInOtherDatacenter(datacenter, repositoryLocation))
                {
                    remoteService.setStatus(STATUS_ERROR);

                    APIError error = APIError.APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED;
                    configurationErrors.add(new ErrorDto(error.getCode(), error.getMessage()));
                    return configurationErrors;
                }

                repo.createRepository(datacenter, repositoryLocation);
            }
            catch (WebApplicationException e)
            {
                remoteService.setStatus(STATUS_ERROR);
                APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                configurationErrors.add(new ErrorDto(error.getCode(), remoteService.getType()
                    .getName() + ", " + error.getMessage()));
                return configurationErrors;
            }
        }

        // we don't want to serialize the errors if they are empty
        return configurationErrors;
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

    /**
     * If the current datacenter have a repository being used then the new appliance manager MUST
     * use the same repository uri. Also updates the repository location (if the old isn't being
     * used).
     */
    protected void checkModifyApplianceManager(final RemoteService old, final RemoteServiceDto dto)
    {
        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(dto.getUri());

        if (repo.isRepositoryBeingUsed(old.getDatacenter()))
        {
            if (dto.getStatus() == STATUS_SUCCESS)
            {
                try
                {
                    String newRepositoryLocation =
                        amStub.getRepositoryConfiguration().getRepositoryLocation();

                    Repository oldRepository = repo.findRepositoryByDatacenter(old.getDatacenter());

                    String oldRepositoryLocation = oldRepository.getUrl();

                    if (!oldRepositoryLocation.equalsIgnoreCase(newRepositoryLocation))
                    {
                        addConflictErrors(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
                    }
                }
                catch (WebApplicationException e)
                {
                    addConflictErrors(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
                }
            }
            else
            // STATUES_ERROR
            {
                addConflictErrors(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
            }
        }
        else if (dto.getStatus() == STATUS_SUCCESS)
        {
            String repositoryLocation = amStub.getRepositoryConfiguration().getRepositoryLocation();

            repo.updateRepositoryLocation(old.getDatacenter(), repositoryLocation);
        }
        else
        // the old repository is not being used and the new am is not properly configured
        {
            repo.deleteRepository(old.getDatacenter());
        }

        // ABICLOUDPREMIUM-719 Do not allow the appliance manager modification if the repository is
        // being used and it changes it location.

        flushErrors();
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
        Machine machine = machineService.getMachine(machineId);
        return virtualMachineService.findByHypervisor(machine.getHypervisor());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUsedResourcesByMachine(final Integer machineId)
    {
        Machine machine = machineService.getMachine(machineId);
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
            if (vm.getState() != null && !vm.getState().equals(VirtualMachineState.NOT_DEPLOYED))
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
}
