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
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.stub.VsmServiceStub;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.UcsRack;

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
    public static final String CHECK_RESOURCE = "check";

    // Declare the Repo. It only should use ONE repo.
    @Autowired
    InfrastructureRep repo;

    @Autowired
    RemoteServiceService remoteServiceService;

    @Autowired
    protected VsmServiceStub vsmServiceStub;

    public InfrastructureService()
    {

    }

    public InfrastructureService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        remoteServiceService = new RemoteServiceService(em);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack addRack(final Rack rack, final Integer datacenterId)
    {
        Datacenter datacenter = this.getDatacenter(datacenterId);

        // Check if there is a rack with the same name in the Datacenter
        if (repo.existsAnyRackWithName(datacenter, rack.getName()))
        {
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
        if (rack.getVlanPerVdcExpected() == null)
        {
            rack.setVlanPerVdcExpected(Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE);
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

        return rack;
    }

    @Transactional(propagation = Propagation.REQUIRED)
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
    public Machine addMachine(final Machine machine, final Integer datacenterId,
        final Integer rackId)
    {

        // Gets the rack. It throws the NotFoundException if needed.
        Rack rack = getRack(datacenterId, rackId);
        Datacenter datacenter = rack.getDatacenter();

        UcsRack ucsRack = repo.findUcsRackById(rackId);
        if (ucsRack != null)
        {
            addConflictErrors(APIError.MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK);
            flushErrors();
        }

        Long realHardDiskInBytes = 0l;
        Long virtualHardDiskInBytes = 0l;
        Long virtualHardDiskUsedInBytes = 0l;
        Boolean anyEnabled = Boolean.FALSE;
        for (Datastore datastore : machine.getDatastores())
        {
            if (datastore.isEnabled())
            {
                anyEnabled = Boolean.TRUE;
            }
            realHardDiskInBytes = virtualHardDiskInBytes = datastore.getSize();
            virtualHardDiskUsedInBytes = datastore.getUsedSize();

            validate(datastore);
            repo.insertDatastore(datastore);
        }

        if (!anyEnabled)
        {
            addValidationErrors(APIError.MACHINE_ANY_DATASTORE_DEFINED);
            flushErrors();
        }

        // Insert the machine into database
        machine.setRealHardDiskInBytes(realHardDiskInBytes);
        machine.setVirtualHardDiskInBytes(virtualHardDiskInBytes);
        machine.setVirtualHardDiskUsedInBytes(virtualHardDiskUsedInBytes);
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

        // Get the remote service to monitor the machine
        RemoteService vsmRS =
            getRemoteService(datacenter.getId(), RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
        vsmServiceStub.monitor(vsmRS.getUri(), machine.getHypervisor().getIp(), machine
            .getHypervisor().getPort(), machine.getHypervisor().getType().name(), machine
            .getHypervisor().getUser(), machine.getHypervisor().getPassword());

        return machine;
    }

    // Return a rack.
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

    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(datacenterId, rackId);

        return isAssignedTo(datacenterId, rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Rack rack)
    {
        return rack != null && rack.getDatacenter().getId().equals(datacenterId);
    }

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
            addConflictErrors(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(rack.getName());
        old.setShortDescription(rack.getShortDescription());
        old.setLongDescription(rack.getLongDescription());

        validate(old);
        repo.updateRack(old);
        return old;
    }

    public void removeRack(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(datacenterId, rackId);
        repo.deleteRack(rack);
    }

    public List<RemoteService> getRemoteServices()
    {
        return repo.findAllRemoteServices();
    }

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

    @Transactional(propagation = Propagation.REQUIRED)
    public RemoteServiceDto addRemoteService(final RemoteService rs, final Integer datacenterId)
    {

        return remoteServiceService.addRemoteService(rs, datacenterId);
        // Datacenter datacenter = repo.findById(datacenterId);
        // if (datacenter == null)
        // {
        // addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
        // flushErrors();
        // }
        //
        // checkUniqueness(datacenter, dto);
        //
        // RemoteService remoteService =
        // datacenter.createRemoteService(dto.getType(), dto.getUri(), 0);
        //
        // if (!remoteService.isValid())
        // {
        // addValidationErrors(remoteService.getValidationErrors());
        // flushErrors();
        // }
        //
        // ErrorsDto configurationErrors = checkRemoteServiceStatus(remoteService.getType(),
        // remoteService.getUri());
        //
        // int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        // remoteService.setStatus(status);
        //
        // if (dto.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        // {
        // configurationErrors.addAll(createApplianceManager(datacenter, remoteService));
        // }
        //
        // repo.insertRemoteService(remoteService);
        //
        // RemoteServiceDto responseDto = createTransferObject(remoteService);
        // if (!configurationErrors.isEmpty())
        // {
        // responseDto.setConfigurationErrors(configurationErrors);
        // }
        //
        // return responseDto;

    }

    public RemoteService getRemoteService(final Integer id)
    {
        return repo.findRemoteServiceById(id);
    }

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

    @Transactional(propagation = Propagation.REQUIRED)
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
        return responseDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRemoteService(final Integer id)
    {
        RemoteService remoteService = getRemoteService(id);

        checkRemoteServiceStatusBeforeRemoving(remoteService);

        repo.deleteRemoteService(remoteService);
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
        if ((remoteService.getType() == RemoteServiceType.DHCP_SERVICE)
            || (remoteService.getType() == RemoteServiceType.VIRTUAL_SYSTEM_MONITOR))
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
            config.connectTimeout(5);

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

                repositoryLocation = amStub.getAMConfiguration().getRepositoryLocation();

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
    protected Datacenter getDatacenter(final Integer datacenterId)
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
                        amStub.getAMConfiguration().getRepositoryLocation();

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
            String repositoryLocation = amStub.getAMConfiguration().getRepositoryLocation();

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

}
