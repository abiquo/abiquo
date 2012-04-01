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

package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.cloud.VirtualMachineLock;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;

/**
 * <pre>
 * Resource that contains all the methods related to a Virtual Machine Network Configuration. Exposes all
 * the methods inside the URI
 * http://{host}/api/cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappids
 * }/virtualmachines/{vmids}/network
 * </pre>
 * 
 * <pre>
 * It has two sections: 
 * -configurations: where to assign network configurations to Virtual Machine
 * -nics: where to create/delete NICs for the Virtual Machine.
 * </pre>
 * 
 * @author jdevesa@abiquo.com
 * @wiki In order to deploy a Virtual Machine you need to configure its network parameters. There
 *       are two things to do: NICs: create, delete, edit or reorder virtual machine NICs and assign
 *       to them a free IP address from Internal, External or Public VLANs, or Network
 *       configuration: depending on which NICs you have assigned to the Virtual Machine, you need
 *       to set a default network configurations (gateway, dns, suffixdns). At least one NIC should
 *       be created and one Network configuration set as default to deploy a virtual machine.
 *       Actually, this values are created by default when you create a Virtual Machine
 */
@Parent(VirtualMachineResource.class)
@Controller
@Path(VirtualMachineNetworkConfigurationResource.NETWORK)
public class VirtualMachineNetworkConfigurationResource extends AbstractResource
{
    /** General REST path of the resource */
    public static final String NETWORK = "network";

    /** Path to access to 'configurations' section. */
    public static final String CONFIGURATION_PATH = "configurations";

    /** Rel object to access to 'configurations' section. */
    public static final String CONFIGURATION = "configuration";

    /** Param to map the input values related to configuration. */
    public static final String CONFIGURATION_PARAM = "{" + CONFIGURATION + "}";

    /** Param to set the link to default configuration */
    public static final String DEFAULT_CONFIGURATION = "network_configuration";

    /** Path to access to 'nics' section. */
    public static final String NICS_PATH = "nics";

    /** Name of the relation in the REST Links to nic values. */
    public static final String NIC = "nic";

    /** Parameter to map the input values related to NICs. */
    public static final String NIC_PARAM = "{" + NIC + "}";

    /** edit relation to private ips. */
    public static final String PRIVATE_IP = "privateip";

    /** edit relation to public ips. */
    public static final String PUBLIC_IP = "publicip";

    /** edit relation to external ips. */
    public static final String EXTERNAL_IP = "externalip";

    /** edit relation to external ips. */
    public static final String UNMANAGED_IP = "unmanagedip";

    /** Autowired business logic service. */
    @Autowired
    private NetworkService service;

    @Autowired
    protected VirtualMachineLock vmLock;

    /**
     * Returns all the posible network configurations that a machine can hold.
     * 
     * @title Retrive the posible network configuration
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationsDto} object that contains all the
     *         {@link VMNetworkConfigurationDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Path(CONFIGURATION_PATH)
    @Produces(VMNetworkConfigurationsDto.MEDIA_TYPE)
    public VMNetworkConfigurationsDto getListOfVirtualMachineConfigurations(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VMNetworkConfigurationsDto dtos = new VMNetworkConfigurationsDto();
        List<VMNetworkConfiguration> configurations =
            service.getVirtualMachineConfigurations(vdcId, vappId, vmId);

        for (VMNetworkConfiguration config : configurations)
        {
            dtos.add(createTransferObject(vdcId, vappId, vmId, config, restBuilder));
        }

        return dtos;
    }

    /**
     * Returns a the single network configuration.
     * 
     * @title Retrive a single network configuration
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the virtual machine.
     * @param mvConfigId identifier of the configuration to retrieve.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Deprecated
    // There is any use case where this method could be useful
    @Path(CONFIGURATION_PATH + "/" + CONFIGURATION)
    @Produces(VMNetworkConfigurationDto.MEDIA_TYPE)
    public VMNetworkConfigurationDto getVirtualMachineConfiguration(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(CONFIGURATION_PATH) @NotNull @Min(1) final Integer vmConfigId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VMNetworkConfiguration configuration =
            service.getVirtualMachineConfiguration(vdcId, vappId, vmId, vmConfigId);

        return createTransferObject(vdcId, vappId, vmId, configuration, restBuilder);

    }

    /**
     * Modify the single network configuration. (Only 'used' parameter can be modified).
     * 
     * @title Modify the single network configuration
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virutal appliance.
     * @param vmId identifier of the virtual machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationsDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @PUT
    @Path(CONFIGURATION_PATH)
    @Consumes(LinksDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto< ? > changeVirtualMachineNetworkConfiguration(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @NotNull final LinksDto configurationRef, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeReconfiguring(vdcId, vappId, vmId);

        try
        {
            Object result =
                service.changeNetworkConfiguration(vdcId, vappId, vmId, configurationRef,
                    originalState);

            // The attach method may return a Tarantino task identifier if the operation requires a
            // reconfigure. Otherwise it will return null.
            if (result != null)
            {
                AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
                response.setStatusUrlLink("http://status");
                response.setEntity(result);
                return response;
            }

            // If there is no async task the VM must be unlocked here
            vmLock.unlockVirtualMachine(vmId, originalState);
            return null;
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reconfigure fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * To get all the NICs attached to a given Virtual Machine.
     * 
     * @title Retrieve the attached NICs
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return The wrapper object {@link NicsDto} that contains all the {@link NicDto} attached to
     *         the Virtual Machine.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Path(NICS_PATH)
    @Produces(NicsDto.MEDIA_TYPE)
    public NicsDto getVirtualMachineNics(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        List<IpPoolManagement> all =
            service.getListIpPoolManagementByVirtualMachine(vdcId, vappId, vmId);
        NicsDto ips = new NicsDto();
        for (IpPoolManagement ip : all)
        {
            ips.add(createNICTransferObject(ip, restBuilder));
        }

        return ips;
    }

    /**
     * <pre>
     * Associate IPs to a Virtual Machine NIC. The function parameters need all the target Virtual
     * Machine attributes. The 'nicAssociations' parameter is a {@link LinksDto} object with a {@link RESTLink}
     * that identifies IPs.
     * </pre>
     * 
     * @title Associate Ips
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicRefs {@link LinksDto} object with information that identifier the Private IP.
     * @param restBuilder restBuilder a Context-injected object to create the links of the Dto
     * @return a {@link NicDto} object that represents the new created NIC.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    @Path(NICS_PATH)
    @Consumes(LinksDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto< ? > attachNICs(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @NotNull final LinksDto nicRefs, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeReconfiguring(vdcId, vappId, vmId);

        try
        {
            Object result = service.attachNICs(vdcId, vappId, vmId, nicRefs, originalState);

            // The attach method may return a Tarantino task identifier if the operation requires a
            // reconfigure. Otherwise it will return null.
            if (result != null)
            {
                AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
                response.setStatusUrlLink("http://status");
                response.setEntity(result);
                return response;
            }

            // If there is no async task the VM must be unlocked here
            vmLock.unlockVirtualMachine(vmId, originalState);
            return null;
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reconfigure fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * <pre>
     * Associate IPs to a Virtual Machine NIC. The function parameters need all the target Virtual
     * Machine attributes. The 'nicAssociations' parameter is a {@link LinksDto} object with a {@link RESTLink}
     * that identifies IPs.
     * </pre>
     * 
     * @title Change the associated IPs
     * @wiki To change the used configuration, you should send a link to the configuration parameter
     *       retrieved in the GET method of this page. There are only two available Virtual Machine
     *       States to change the Configuration of a Virtual Machine: NOT_ALLOCATED and OFF. If the
     *       machine is NOT_ALLOCATED, the response code will be 204 - NOT CONTENT and the machine
     *       changes will be already committed. If the machine is OFF, abiquo API will perform an
     *       asynchronous task. The response code will be 202 - ACCEPTED and in the response body
     *       will be an URI link to know how the task is going on. The changes won't be committed
     *       until the task is finished.
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicRefs {@link LinksDto} object with information that identifier the Private IP.
     * @param restBuilder restBuilder a Context-injected object to create the links of the Dto
     * @return a {@link NicDto} object that represents the new created NIC.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @PUT
    @Path(NICS_PATH)
    @Consumes(LinksDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto< ? > changeNICs(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @NotNull final LinksDto nicRefs, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeReconfiguring(vdcId, vappId, vmId);

        try
        {
            Object result = service.changeNICs(vdcId, vappId, vmId, nicRefs, originalState);

            // The attach method may return a Tarantino task identifier if the operation requires a
            // reconfigure. Otherwise it will return null.
            if (result != null)
            {
                AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
                response.setStatusUrlLink("http://status");
                response.setEntity(result);
                return response;
            }

            // If there is no async task the VM must be unlocked here
            vmLock.unlockVirtualMachine(vmId, originalState);
            return null;
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reconfigure fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * Returns a single ip according on its id in Virtual Machine
     * 
     * @title Retrieve a single IP
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param nicId identifier of the ip inside the virtual machine
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DiskManagementDto} object that contains all the {@link DiskManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Deprecated
    // there is any use case we need this method.
    @Path(NICS_PATH + "/" + NIC_PARAM)
    @Produces(NicDto.MEDIA_TYPE)
    public NicDto getIp(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(NIC) @NotNull @Min(0) final Integer nicId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        IpPoolManagement ip =
            service.getIpPoolManagementByVirtualMachine(vdcId, vappId, vmId, nicId);

        return createNICTransferObject(ip, restBuilder);
    }

    /**
     * Remove a Virtual Machine NIC. Release the association between Private IP and NIC.
     * 
     * @title Remove a Virtual Machine NIC
     * @wiki Delete a NIC from a Virtual Machine and release its associated IP address. Once you
     *       perform a GET operation over a Virtual Machine, you will see the "edit" rel in each NIC
     *       links. You should use this link and the DELETE operation to delete the NIC
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicOrder Identifier of the NIC inside the Virtual Machine.
     * @param restBuilder restBuilder a Context-injected object to create the links of the Dto
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @DELETE
    @Path(NICS_PATH + "/" + NIC_PARAM)
    public AcceptedRequestDto< ? > detachNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(NIC) @NotNull @Min(0) final Integer nicId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeReconfiguring(vdcId, vappId, vmId);

        try
        {
            Object result = service.detachNIC(vdcId, vappId, vmId, nicId, originalState);

            // The attach method may return a Tarantino task identifier if the operation requires a
            // reconfigure. Otherwise it will return null.
            if (result != null)
            {
                AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
                response.setStatusUrlLink("http://status");
                response.setEntity(result);
                return response;
            }

            // If there is no async task the VM must be unlocked here
            vmLock.unlockVirtualMachine(vmId, originalState);
            return null;
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reconfigure fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * TODO: modify this!!
     * 
     * <pre>
     * Identifiers of the NICs are ordered values that correspond to its ETHX value when the 
     * Virtual Machine is deployed. In this way, the NIC with nicOrder O will correspond to ETH0, nicOrder 1 will correspond to ETH1, and so on.
     * </pre>
     * 
     * <pre>
     * This method is used to reorder two NICs to associate them later in the desired ethernet value.
     * The parameters of this method correspond to the target nicOrder we want to use, and the 'nicAssociation'
     * parameter has the {link @RESTLink} object with the URI of the current nicOrder of the NIC. In other words, we
     * move the NIC from 'nicAssociations' to 'nicOrder' value.
     * </pre>
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicOrder Identifier of the target 'order' value.
     * @param nicAssociations current {@link RESTLink} to the 'order' value. Idenfified by the
     *            "rel=nic" relation.
     * @param restBuilder restBuilder a Context-injected object to create the links of the Dto
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    // @PUT
    // @Path(NICS_PATH + "/" + NIC_PARAM)
    // public void reorderVirtualMachineNic(
    // @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer
    // vdcId,
    // @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
    // @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
    // @PathParam(NIC) @Min(0) final Integer nicOrder, final LinksDto nicAssociations,
    // @Context final IRESTBuilder restBuilder) throws Exception
    // {
    // Get the link that identifies the current NIC order.
    // RESTLink previousLink = nicAssociations.searchLink("nic");

    // Parse the URI with the expected parameters and extract the identifier values.
    // String buildPath =
    // buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
    // VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
    // VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
    // VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
    // VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
    // VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
    // VirtualMachineNetworkConfigurationResource.NETWORK,
    // VirtualMachineNetworkConfigurationResource.NICS_PATH,
    // VirtualMachineNetworkConfigurationResource.NIC_PARAM);
    // MultivaluedMap<String, String> ipsValues =
    // URIResolver.resolveFromURI(buildPath, previousLink.getHref());

    // Check if the link to the nic link is valid. Must contain the identifiers of
    // Virtual Datacenter, Virtual Appliance, Virtual Machine, and old Nic order
    // if (ipsValues == null
    // || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
    // || !ipsValues.containsKey(VirtualApplianceResource.VIRTUAL_APPLIANCE)
    // || !ipsValues.containsKey(VirtualMachineResource.VIRTUAL_MACHINE)
    // || !ipsValues.containsKey(VirtualMachineNetworkConfigurationResource.NIC))
    // {
    // throw new BadRequestException(APIError.VLANS_REORDER_NIC_INVALID_LINK);
    // }

    // Since we move NICs between the same Virtual Machine, the extracted values
    // must match with the input parameters 'vdcId', 'vappId' and 'vmId'
    // Integer linkIdVdc =
    // Integer.valueOf(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
    // Integer linkIdVapp =
    // Integer.valueOf(ipsValues.getFirst(VirtualApplianceResource.VIRTUAL_APPLIANCE));
    // Integer linkIdVm =
    // Integer.valueOf(ipsValues.getFirst(VirtualMachineResource.VIRTUAL_MACHINE));
    // Integer linkOldOrder =
    // Integer.valueOf(ipsValues.getFirst(VirtualMachineNetworkConfigurationResource.NIC));
    // if (!linkIdVdc.equals(vdcId) || !linkIdVapp.equals(vappId) || !linkIdVm.equals(vmId))
    // {
    // throw new BadRequestException(APIError.VLANS_REORDER_NIC_INVALID_LINK_VALUES);
    // }

    // Call the business logic with the correct values.
    // service.reorderVirtualMachineNic(vdcId, vappId, vmId, linkOldOrder, nicOrder);
    // }

    /**
     * Static method that converts transfer object {@link VMNetworkConfigurationDto} object to
     * persistence object {@link VMNetworkConfiguration}
     * 
     * @param vmConfig input transfer object.
     * @return the corresponding persistence object.
     * @throws Exception Serialization unhandled exception.
     */
    public static VMNetworkConfiguration createPersistentObject(
        final VMNetworkConfigurationDto vmConfig) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(VMNetworkConfiguration.class, vmConfig);
    }

    /**
     * Static method that converts the persistence {@link VMNetworkConfiguration} object to transfer
     * {@link VMNetworkConfigurationDto} object. It also adds REST self-discover {@link RESTLink}s
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param config Input persistence object.
     * @param restBuilder Context-injected {@link RESTLink} builder to create the links.
     * @return the corresponding transfer object.
     * @throws Exception Serialization unhandled exception.
     */
    public static VMNetworkConfigurationDto createTransferObject(final Integer vdcId,
        final Integer vappId, final Integer vmId, final VMNetworkConfiguration config,
        final IRESTBuilder restBuilder) throws Exception
    {
        VMNetworkConfigurationDto dto =
            ModelTransformer.transportFromPersistence(VMNetworkConfigurationDto.class, config);
        dto.setLinks(restBuilder.buildVMNetworkConfigurationLinks(vdcId, vappId, vmId, config));
        return dto;
    }

    /**
     * Static method that converts the persistence {@link IpPoolManagement} object to transfer
     * {@link NicDto} object. It also adds REST self-discover {@link RESTLink}s
     * 
     * @param ip Input persistence object.
     * @param restBuilder Context-injected {@link RESTLink} builder to create the links.
     * @return the corresponding transfer object.
     * @throws Exception Serialization unhandled exception.
     */
    public static NicDto createNICTransferObject(final IpPoolManagement ip,
        final IRESTBuilder restBuilder) throws Exception
    {
        NicDto dto = ModelTransformer.transportFromPersistence(NicDto.class, ip);
        dto.setSequence(ip.getSequence());
        dto.setLinks(restBuilder.buildNICLinks(ip));
        return dto;
    }
}
