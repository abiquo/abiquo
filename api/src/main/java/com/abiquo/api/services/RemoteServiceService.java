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
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

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
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.Repository;

@Service("remoteServiceService")
@Transactional(readOnly = true)
public class RemoteServiceService extends DefaultApiService
{

    public static final String CHECK_RESOURCE = "check";

    @Autowired
    DatacenterRep datacenterRepo;

    public RemoteServiceService()
    {

    }

    public RemoteServiceService(EntityManager em)
    {
        datacenterRepo = new DatacenterRep(em);
    }

    public List<RemoteService> getRemoteServices()
    {
        return datacenterRepo.findAllRemoteServices();
    }

    public List<RemoteService> getRemoteServicesByDatacenter(Integer datacenterId)
    {
        Datacenter datacenter = datacenterRepo.findById(datacenterId);
        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        return datacenterRepo.findRemoteServicesByDatacenter(datacenter);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RemoteServiceDto addRemoteService(final RemoteServiceDto dto, final Integer datacenterId)
    {
        Datacenter datacenter = datacenterRepo.findById(datacenterId);
        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        checkUniqueness(datacenter, dto);        

        RemoteService remoteService =
            datacenter.createRemoteService(dto.getType(), dto.getUri(), 0);

        if (!remoteService.isValid())
        {
            validationErrors.addAll(remoteService.getValidationErrors());
            flushErrors();
        }
        
        ErrorsDto configurationErrors = checkStatus(dto.getType(), dto.getUri());

        int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        remoteService.setStatus(status);

        if (dto.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            configurationErrors.addAll(createApplianceManager(datacenter, remoteService));
        }

        datacenterRepo.insertRemoteService(remoteService);

        RemoteServiceDto responseDto = createTransferObject(remoteService);
        if (!configurationErrors.isEmpty())
        {
            responseDto.setConfigurationErrors(configurationErrors);
        }

        return responseDto;
    }

    /**
     * Configure the Datacenter repository based on the ''repositoryLocation'' consulted from AM.
     */
    private ErrorsDto createApplianceManager(Datacenter datacenter, RemoteService remoteService)
    {
        int previousStatus = remoteService.getStatus();

        ErrorsDto configurationErrors = new ErrorsDto();
        if (datacenterRepo.isRepositoryBeingUsed(datacenter))
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

                if (datacenterRepo.existRepositoryInOtherDatacenter(datacenter, repositoryLocation))
                {
                    remoteService.setStatus(STATUS_ERROR);

                    APIError error = APIError.APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED;
                    configurationErrors.add(new ErrorDto(error.getCode(), error.getMessage()));
                    return configurationErrors;
                }

                datacenterRepo.createRepository(datacenter, repositoryLocation);
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

    public RemoteService getRemoteService(final Integer id)
    {
        return datacenterRepo.findRemoteServiceById(id);
    }

    public RemoteService getRemoteService(final Integer datacenterId, final RemoteServiceType type)
    {
        Datacenter datacenter = datacenterRepo.findById(datacenterId);
        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        List<RemoteService> services =
            datacenterRepo.findRemoteServiceWithTypeInDatacenter(datacenter, type);
        RemoteService remoteService = null;

        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            remoteService = services.get(0);
        }
        else
        {
            throw new NotFoundException(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
        }

        return remoteService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RemoteServiceDto modifyRemoteService(Integer id, RemoteServiceDto dto)
        throws URISyntaxException
    {
        RemoteService old = getRemoteService(id);

        ErrorsDto configurationErrors = checkStatus(dto.getType(), dto.getUri());
        int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        dto.setStatus(status);

        if (dto.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            checkModifyApplianceManager(old, dto);
        }

        old.setUri(dto.getUri());
        old.setType(dto.getType());
        old.setStatus(dto.getStatus());

        datacenterRepo.updateRemoteService(old);

        RemoteServiceDto responseDto = createTransferObject(old);

        if (!configurationErrors.isEmpty())
        {
            responseDto.setConfigurationErrors(configurationErrors);
        }
        return responseDto;
    }

    /**
     * If the current datacenter have a repository being used then the new appliance manager MUST
     * use the same repository uri. Also updates the repository location (if the old isn't being
     * used).
     */
    private void checkModifyApplianceManager(RemoteService old, RemoteServiceDto dto)
    {
       ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(dto.getUri());

        if (datacenterRepo.isRepositoryBeingUsed(old.getDatacenter()))
        {
            if (dto.getStatus() == STATUS_SUCCESS)
            {
                try
                {
                    String newRepositoryLocation =
                        amStub.getAMConfiguration().getRepositoryLocation();

                    Repository oldRepository =
                        datacenterRepo.findRepositoryByDatacenter(old.getDatacenter());

                    String oldRepositoryLocation = oldRepository.getUrl();

                    if (!oldRepositoryLocation.equalsIgnoreCase(newRepositoryLocation))
                    {                        
                        errors.add(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);                        
                    }
                }
                catch (WebApplicationException e)
                {
                    errors.add(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);                    
                }
            }
            else
            // STATUES_ERROR
            {
                errors.add(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
            }
        }
        else if (dto.getStatus() == STATUS_SUCCESS)
        {
            String repositoryLocation = amStub.getAMConfiguration().getRepositoryLocation();

            datacenterRepo.updateRepositoryLocation(old.getDatacenter(), repositoryLocation);
        }
        else
        // the old repository is not being used and the new am is not properly configured
        {
            datacenterRepo.deleteRepository(old.getDatacenter());
        }

        // ABICLOUDPREMIUM-719 Do not allow the appliance manager modification if the repository is
        // being used and it changes it location.

        flushErrors();        
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRemoteService(Integer id)
    {
        RemoteService remoteService = getRemoteService(id);

        checkRemoteServiceStatusBeforeRemoving(remoteService);

        datacenterRepo.deleteRemoteService(remoteService);
    }

    protected void checkRemoteServiceStatusBeforeRemoving(RemoteService remoteService)
    {
        if (remoteService.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            if (datacenterRepo.isRepositoryBeingUsed(remoteService.getDatacenter()))
            {
                errors.add(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
                flushErrors();
            }
        }
        if ((remoteService.getType() == RemoteServiceType.DHCP_SERVICE)
            || (remoteService.getType() == RemoteServiceType.VIRTUAL_SYSTEM_MONITOR))
        {
            if (datacenterRepo.existDeployedVirtualMachines(remoteService.getDatacenter()))
            {
                errors.add(APIError.REMOTE_SERVICE_IS_BEING_USED);
                flushErrors();
            }
        }
    }

    public boolean isAssignedTo(final Integer datacenterId, final String remoteServiceMapping)
    {
        RemoteServiceType type = RemoteServiceType.valueOf(remoteServiceMapping.toUpperCase());

        return isAssignedTo(datacenterId, type);
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

    public ErrorsDto checkStatus(RemoteServiceType type, String url)
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

    private void checkUniqueness(Datacenter datacenter, RemoteServiceDto remoteService)
    {
        if (remoteService.getType().checkUniqueness())
        {
            try
            {
                if (datacenterRepo.existAnyRemoteServiceWithUri(remoteService.getUri()))
                {
                    throw new APIException(Status.BAD_REQUEST,
                        APIError.REMOTE_SERVICE_URL_ALREADY_EXISTS);
                }
            }
            catch (URISyntaxException e)
            {
                throw new APIException(Status.BAD_REQUEST, APIError.REMOTE_SERVICE_MALFORMED_URL);
            }
        }
        else if (datacenterRepo.existAnyRemoteServiceWithTypeInDatacenter(datacenter,
            remoteService.getType()))
        {
            throw new APIException(Status.BAD_REQUEST, APIError.REMOTE_SERVICE_TYPE_EXISTS);
        }
    }
}
