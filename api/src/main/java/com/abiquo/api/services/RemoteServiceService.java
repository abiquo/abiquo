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

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service("remoteServiceService")
@Transactional(readOnly = true)
public class RemoteServiceService extends DefaultApiService
{

    public static final String CHECK_RESOURCE = "check";

    @Autowired
    InfrastructureRep infrastructureRepo;

    public RemoteServiceService()
    {

    }

    public RemoteServiceService(final EntityManager em)
    {
        infrastructureRepo = new InfrastructureRep(em);
    }

    public List<RemoteService> getRemoteServices()
    {
        return infrastructureRepo.findAllRemoteServices();
    }

    public List<RemoteService> getRemoteServicesByDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = infrastructureRepo.findById(datacenterId);
        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return infrastructureRepo.findRemoteServicesByDatacenter(datacenter);
    }

    /**
     * Add a new remoteService
     * 
     * @param rs remoteServoce
     * @param datacenter datacenter where add it
     * @return TransferObject: RemoteServiceDto if OK, ErrorsDto else
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public RemoteServiceDto addRemoteService(final RemoteService rs, final Datacenter datacenter)
    {
        return (RemoteServiceDto) addRemoteService(rs, datacenter, false);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public RemoteServiceDto addRemoteService(final RemoteService rs, final Integer datacenterId)
    {
        Datacenter datacenter = infrastructureRepo.findById(datacenterId);
        return (RemoteServiceDto) addRemoteService(rs, datacenter, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public SingleResourceTransportDto addRemoteService(final RemoteService rs,
        final Datacenter datacenter, final boolean flushErrors)
    {

        if (rs.getType() == null)
        {
            addValidationErrors(APIError.WRONG_REMOTE_SERVICE_TYPE);
            flushErrors();
        }

        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        RemoteServiceDto responseDto = new RemoteServiceDto();

        ErrorsDto errorsDto = checkUniqueness(datacenter, rs, flushErrors);

        if (!flushErrors
            && (errorsDto.getCollection() == null || errorsDto.getCollection().size() > 0))
        {
            return errorsDto;
        }
        else
        {
            RemoteService remoteService =
                datacenter.createRemoteService(rs.getType(), rs.getUri(), 0);

            ErrorsDto configurationErrors =
                validateRemoteService(datacenter, rs, remoteService, responseDto);

            infrastructureRepo.insertRemoteService(remoteService);

            responseDto = createTransferObject(remoteService);
            if (!configurationErrors.isEmpty())
            {
                responseDto.setConfigurationErrors(configurationErrors);
            }
        }

        tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.REMOTE_SERVICES_CREATE,
            responseDto.getType().getName() + " created in " + responseDto.getUri()
                + " for datacenter " + datacenter.getName());

        return responseDto;
    }

    /**
     * Configure the Datacenter repository based on the ''repositoryLocation'' consulted from AM.
     */
    private ErrorsDto createApplianceManager(final Datacenter datacenter,
        final RemoteService remoteService)
    {
        int previousStatus = remoteService.getStatus();

        ErrorsDto configurationErrors = new ErrorsDto();
        if (infrastructureRepo.isRepositoryBeingUsed(datacenter))
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

                try
                {
                    repositoryLocation = amStub.getRepositoryConfiguration().getLocation();
                }
                catch (ApplianceManagerStubException amEx)
                {
                    remoteService.setStatus(STATUS_ERROR);
                    APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
                    configurationErrors.add(new ErrorDto(error.getCode(), remoteService.getType()
                        .getName() + ", " + amEx.getMessage()));

                    return configurationErrors;

                }

                if (infrastructureRepo.existRepositoryInOtherDatacenter(datacenter,
                    repositoryLocation))
                {
                    remoteService.setStatus(STATUS_ERROR);

                    APIError error = APIError.APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED;
                    configurationErrors.add(new ErrorDto(error.getCode(), error.getMessage()));
                    return configurationErrors;
                }

                if (!infrastructureRepo.existRepositoryInSameDatacenter(datacenter,
                    repositoryLocation))
                {
                    infrastructureRepo.createRepository(datacenter, repositoryLocation);
                }
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
        return infrastructureRepo.findRemoteServiceById(id);
    }

    public RemoteService getRemoteService(final Integer datacenterId, final RemoteServiceType type)
    {
        Datacenter datacenter = infrastructureRepo.findById(datacenterId);
        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        List<RemoteService> services =
            infrastructureRepo.findRemoteServiceWithTypeInDatacenter(datacenter, type);
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

        ErrorsDto configurationErrors =
            checkRemoteServiceStatus(old.getDatacenter(), dto.getType(), dto.getUri());
        int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        dto.setStatus(status);

        if (dto.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            checkModifyApplianceManager(old, dto);
        }

        old.setUri(dto.getUri());
        old.setType(dto.getType());
        old.setStatus(dto.getStatus());

        infrastructureRepo.updateRemoteService(old);

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
    private void checkModifyApplianceManager(final RemoteService old, final RemoteServiceDto dto)
    {
        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(dto.getUri());

        if (infrastructureRepo.isRepositoryBeingUsed(old.getDatacenter()))
        {
            if (dto.getStatus() == STATUS_SUCCESS)
            {
                try
                {
                    String newRepositoryLocation =
                        amStub.getRepositoryConfiguration().getLocation();

                    Repository oldRepository =
                        infrastructureRepo.findRepositoryByDatacenter(old.getDatacenter());

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
            String repositoryLocation = amStub.getRepositoryConfiguration().getLocation();

            infrastructureRepo.updateRepositoryLocation(old.getDatacenter(), repositoryLocation);
        }
        else
        // the old repository is not being used and the new am is not properly configured
        {
            infrastructureRepo.deleteRepository(old.getDatacenter());
        }

        // ABICLOUDPREMIUM-719 Do not allow the appliance manager modification if the repository is
        // being used and it changes it location.

        flushErrors();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRemoteService(final Integer id)
    {
        RemoteService remoteService = getRemoteService(id);

        checkRemoteServiceStatusBeforeRemoving(remoteService);

        infrastructureRepo.deleteRemoteService(remoteService);
    }

    protected void checkRemoteServiceStatusBeforeRemoving(final RemoteService remoteService)
    {
        if (remoteService.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            if (infrastructureRepo.isRepositoryBeingUsed(remoteService.getDatacenter()))
            {
                addConflictErrors(APIError.APPLIANCE_MANAGER_REPOSITORY_IN_USE);
                flushErrors();
            }
        }
        if (remoteService.getType() == RemoteServiceType.DHCP_SERVICE
            || remoteService.getType() == RemoteServiceType.VIRTUAL_SYSTEM_MONITOR)
        {
            if (infrastructureRepo.existDeployedVirtualMachines(remoteService.getDatacenter()))
            {
                addConflictErrors(APIError.REMOTE_SERVICE_IS_BEING_USED);
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

    // public ErrorsDto checkStatus(final RemoteServiceType type, final String url)
    // {
    // ErrorsDto configurationErrors = new ErrorsDto();
    // if (type.canBeChecked())
    // {
    // ClientConfig config = new ClientConfig();
    // config.connectTimeout(5);
    //
    // RestClient restClient = new RestClient(config);
    // Resource checkResource =
    // restClient.resource(UriHelper.appendPathToBaseUri(url, CHECK_RESOURCE));
    //
    // try
    // {
    // ClientResponse response = checkResource.get();
    // if (response.getStatusCode() != 200)
    // {
    // APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
    // configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
    // + error.getMessage()));
    // }
    // }
    // catch (WebApplicationException e)
    // {
    // APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
    // configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
    // + error.getMessage() + ", " + e.getMessage()));
    // }
    // catch (ClientRuntimeException e)
    // {
    // APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
    // configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
    // + error.getMessage() + ", " + e.getMessage()));
    // }
    // catch (Exception e)
    // {
    // APIError error = APIError.REMOTE_SERVICE_CONNECTION_FAILED;
    // configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
    // + error.getMessage() + ", " + e.getMessage()));
    // }
    // }
    // return configurationErrors;
    // }

    public ErrorsDto checkRemoteServiceStatus(final Datacenter datacenter,
        final RemoteServiceType type, final String url)
    {
        return checkRemoteServiceStatus(datacenter, type, url, false);
    }

    public ErrorsDto checkRemoteServiceStatus(final Datacenter datacenter,
        final RemoteServiceType type, final String url, final boolean flushErrors)
    {
        ErrorsDto configurationErrors = new ErrorsDto();
        if (type.canBeChecked())
        {
            ClientConfig config = new ClientConfig();
            config.connectTimeout(5000);

            RestClient restClient = new RestClient(config);
            String uriToCheck = UriHelper.appendPathToBaseUri(url, CHECK_RESOURCE);
            Resource checkResource = restClient.resource(uriToCheck);

            try
            {
                ClientResponse response = checkResource.get();
                if (response.getStatusCode() != 200)
                {
                    configurationErrors.add(new ErrorDto(APIError.REMOTE_SERVICE_CONNECTION_FAILED
                        .getCode(), type.getName() + ", "
                        + APIError.REMOTE_SERVICE_CONNECTION_FAILED.getMessage()));
                    if (flushErrors)
                    {
                        switch (response.getStatusCode())
                        {
                            case 404:
                                addNotFoundErrors(APIError.REMOTE_SERVICE_CONNECTION_FAILED);
                                break;
                            case 503:
                                addServiceUnavailableErrors(APIError.REMOTE_SERVICE_CONNECTION_FAILED);
                                break;
                            default:
                                addNotFoundErrors(APIError.REMOTE_SERVICE_CONNECTION_FAILED);
                                break;
                        }
                    }
                }// remote service check fail

                if (type.checkDatacenterId())
                {
                    final String rsDatacenterUuid = response.getEntity(String.class);

                    if (StringUtils.isEmpty(rsDatacenterUuid))
                    {
                        final APIError error = APIError.REMOTE_SERVICE_DATACENTER_UUID_NOT_FOUND;
                        configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                            + error.getMessage()));
                        if (flushErrors)
                        {
                            addConflictErrors(error);
                        }
                    }

                    if (!isValidDatacenterUuid(rsDatacenterUuid, datacenter))
                    {
                        final APIError error = APIError.REMOTE_SERVICE_DATACENTER_UUID_INCONSISTENT;
                        configurationErrors.add(new ErrorDto(error.getCode(), type.getName() + ", "
                            + error.getMessage() + "\n Current datacenter UUID is "
                            + datacenter.getUuid()));
                        if (flushErrors)
                        {
                            addConflictErrors(error);
                        }
                    }
                }// datacenter uuid
            }
            catch (Exception e)
            {
                configurationErrors.add(new ErrorDto(APIError.REMOTE_SERVICE_CONNECTION_FAILED
                    .getCode(), type.getName() + ", "
                    + APIError.REMOTE_SERVICE_CONNECTION_FAILED.getMessage() + ", "
                    + e.getMessage()));
                if (flushErrors)
                {
                    addNotFoundErrors(APIError.REMOTE_SERVICE_CONNECTION_FAILED);
                }
            }
        }

        if (flushErrors)
        {
            flushErrors();
        }

        return configurationErrors;
    }

    /**
     * Checks the datacenter uuid (or set it if not already defined)
     * 
     * @param rsDatacenterId, UUID from the remote service
     * @param datacenter, current datacenter
     * @return true if the informed datacenter uuid is consistent.
     */
    private boolean isValidDatacenterUuid(final String rsDatacenterId, final Datacenter datacenter)
    {
        final String datacenterUuid = datacenter.getUuid();
        if (StringUtils.isEmpty(datacenterUuid))
        {
            datacenter.setUuid(rsDatacenterId);
            infrastructureRepo.update(datacenter);
            return true;
        }
        else if (rsDatacenterId.equals(datacenterUuid))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    // --------------- //
    // PRIVATE METHODS //
    // --------------- //

    private ErrorsDto checkUniqueness(final Datacenter datacenter,
        final RemoteService remoteService, final boolean flushErrors)
    {
        ErrorsDto configurationErrors = new ErrorsDto();

        if (infrastructureRepo.existAnyRemoteServiceWithTypeInDatacenter(datacenter,
            remoteService.getType()))
        {
            APIError error = APIError.REMOTE_SERVICE_TYPE_EXISTS;
            configurationErrors.add(new ErrorDto(error.getCode(), remoteService.getType().getName()
                + " : " + error.getMessage()));
            if (flushErrors)
            {
                addConflictErrors(error);
            }
        }

        if (remoteService.getType().checkUniqueness())
        {
            try
            {
                if (infrastructureRepo.existAnyRemoteServiceWithUri(remoteService.getUri()))
                {
                    APIError error = APIError.REMOTE_SERVICE_URL_ALREADY_EXISTS;
                    configurationErrors.add(new ErrorDto(error.getCode(), remoteService.getType()
                        .getName() + " : " + error.getMessage()));
                    if (flushErrors)
                    {
                        addConflictErrors(error);
                    }
                }
            }
            catch (URISyntaxException e)
            {
                APIError error = APIError.REMOTE_SERVICE_MALFORMED_URL;
                configurationErrors.add(new ErrorDto(error.getCode(), remoteService.getType()
                    .getName() + " : " + error.getMessage()));
                if (flushErrors)
                {
                    addValidationErrors(error);
                }
            }
        }

        if (flushErrors)
        {
            flushErrors();
        }

        return configurationErrors;
    }

    private ErrorsDto validateRemoteService(final Datacenter datacenter, final RemoteService rs,
        final RemoteService remoteService, final RemoteServiceDto responseDto)
    {
        if (!remoteService.isValid())
        {
            addValidationErrors(remoteService.getValidationErrors());
            flushErrors();
        }

        ErrorsDto configurationErrors =
            checkRemoteServiceStatus(datacenter, remoteService.getType(), remoteService.getUri());

        int status = configurationErrors.isEmpty() ? STATUS_SUCCESS : STATUS_ERROR;
        remoteService.setStatus(status);

        if (rs.getType() == RemoteServiceType.APPLIANCE_MANAGER)
        {
            configurationErrors.addAll(createApplianceManager(datacenter, remoteService));
        }

        return configurationErrors;
    }
}
