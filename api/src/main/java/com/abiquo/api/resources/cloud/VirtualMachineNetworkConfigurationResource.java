package com.abiquo.api.resources.cloud;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;

/**
 * <pre>
 * Resource that contains all the methods related to a Virtual Machine configuration. Exposes all
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

    /** Param to map the input values related to configuration. */
    public static final String CONFIGURATION_PARAM = "{" + CONFIGURATION_PATH + "}";

    /** Path to access to 'nics' section. */
    public static final String NICS_PATH = "nics";

    /** Name of the relation in the REST Links to nic values. */
    public static final String NIC = "nic";

    /** Parameter to map the input values related to NICs. */
    public static final String NIC_PARAM = "{" + NIC + "}";

    /** Autowired business logic service. */
    @Autowired
    private NetworkService service;

    /**
     * Returns all the posible network configurations that a machine can hold.
     * 
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
    @Path(CONFIGURATION_PATH + "/" + CONFIGURATION_PARAM)
    public VMNetworkConfigurationDto getVirtualMachineConfigurations(
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
     * Modify a the single network configuration. (Only 'used' parameter can be modified).
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virutal appliance.
     * @param vmId identifier of the virtual machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationsDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @PUT
    @Path(CONFIGURATION_PATH + "/" + CONFIGURATION_PARAM)
    public VMNetworkConfigurationDto updateVirtualMachineConfigurations(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(CONFIGURATION_PATH) @NotNull @Min(1) final Integer vmConfigId,
        final VMNetworkConfigurationDto vmConfig, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VMNetworkConfiguration configuration =
            service.updateVirtualMachineConfiguration(vdcId, vappId, vmId, vmConfigId,
                createPersistentObject(vmConfig));

        return createTransferObject(vdcId, vappId, vmId, configuration, restBuilder);

    }

    /**
     * To get all the NICs attached to a given Virtual Machine.
     * 
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
     * Associate a Private IP to a Virtual Machine NIC. The function parameters need all the target Virtual
     * Machine attributes. The 'nicAssociations' parameter is a {@link LinksDto} object with a {@link RESTLink}
     * that identifies a Private IP.
     * Inside the {@link RESTLink} uri, the Virtual Datacenter identifier must be the same than the input parameter
     * 'vdcId', otherwise it will raise a {@link BadRequestException}.
     * </pre>
     * 
     * <pre>
     * Even the {@link LinksDto} object can handle more than one {@link RESTLink} only the first one with
     * 'rel' attribute equals to "privateip" will be used.
     * </pre>
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicAssociations {@link LinksDto} object with information that identifier the Private
     *            IP.
     * @param restBuilder restBuilder a Context-injected object to create the links of the Dto
     * @return a {@link NicDto} object that represents the new created NIC.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    @Path(NICS_PATH)
    public NicDto associateVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @NotNull final LinksDto nicAssociations, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        // Get the link that identifies the Private IP
        RESTLink privateLink = nicAssociations.searchLink("privateip");

        if (privateLink != null)
        {
            // Parse the URI with the expected parameters and extract the identifier values.
            String buildPath =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                    PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                    PrivateNetworkResource.PRIVATE_NETWORK_PARAM, IpAddressesResource.IP_ADDRESSES,
                    IpAddressesResource.IP_ADDRESS_PARAM);
            MultivaluedMap<String, String> ipsValues =
                URIResolver.resolveFromURI(buildPath, privateLink.getHref());

            // URI needs to have an identifier to a VDC, another one to a Private Network
            // and another one to Private IP
            if (ipsValues == null
                || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !ipsValues.containsKey(PrivateNetworkResource.PRIVATE_NETWORK)
                || !ipsValues.containsKey(IpAddressesResource.IP_ADDRESS))
            {
                throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
            }

            // Private IP must belong to the same Virtual Datacenter where the Virtual Machine
            // belongs to.
            Integer idVdc =
                Integer.parseInt(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!idVdc.equals(vdcId))
            {
                throw new BadRequestException(APIError.VLANS_IP_LINK_INVALID_VDC);
            }

            // Extract the vlanId and ipId values to execute the association.
            Integer vlanId =
                Integer.parseInt(ipsValues.getFirst(PrivateNetworkResource.PRIVATE_NETWORK));
            Integer ipId = Integer.parseInt(ipsValues.getFirst(IpAddressesResource.IP_ADDRESS));
            IpPoolManagement ip =
                service.associateVirtualMachinePrivateNic(vdcId, vappId, vmId, vlanId, ipId);

            return createNICTransferObject(ip, restBuilder);
        }

        // if any link has been found, then raise a BadRequestException
        throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
    }

    /**
     * Remove a Virtual Machine NIC. Release the association between Private IP and NIC.
     * 
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
    public void disassociateVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(NIC) @NotNull @Min(1) final Integer nicOrder,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.releaseNicFromVirtualMachine(vdcId, vappId, vmId, nicOrder);
    }

    /**
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
    @PUT
    @Path(NICS_PATH + "/" + NIC_PARAM)
    public void reorderVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(NIC) @Min(0) final Integer nicOrder, final LinksDto nicAssociations,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Get the link that identifies the current NIC order.
        RESTLink previousLink = nicAssociations.searchLink("nic");

        // Parse the URI with the expected parameters and extract the identifier values.
        String buildPath =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
                VirtualMachineNetworkConfigurationResource.NETWORK,
                VirtualMachineNetworkConfigurationResource.NICS_PATH,
                VirtualMachineNetworkConfigurationResource.NIC_PARAM);
        MultivaluedMap<String, String> ipsValues =
            URIResolver.resolveFromURI(buildPath, previousLink.getHref());

        // Check if the link to the nic link is valid. Must contain the identifiers of
        // Virtual Datacenter, Virtual Appliance, Virtual Machine, and old Nic order
        if (ipsValues == null
            || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
            || !ipsValues.containsKey(VirtualApplianceResource.VIRTUAL_APPLIANCE)
            || !ipsValues.containsKey(VirtualMachineResource.VIRTUAL_MACHINE)
            || !ipsValues.containsKey(VirtualMachineNetworkConfigurationResource.NIC))
        {
            throw new BadRequestException(APIError.VLANS_REORDER_NIC_INVALID_LINK);
        }

        // Since we move NICs between the same Virtual Machine, the extracted values
        // must match with the input parameters 'vdcId', 'vappId' and 'vmId'
        Integer linkIdVdc =
            Integer.valueOf(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
        Integer linkIdVapp =
            Integer.valueOf(ipsValues.getFirst(VirtualApplianceResource.VIRTUAL_APPLIANCE));
        Integer linkIdVm =
            Integer.valueOf(ipsValues.getFirst(VirtualMachineResource.VIRTUAL_MACHINE));
        Integer linkOldOrder =
            Integer.valueOf(ipsValues.getFirst(VirtualMachineNetworkConfigurationResource.NIC));
        if (!linkIdVdc.equals(vdcId) || !linkIdVapp.equals(vappId) || !linkIdVm.equals(vmId))
        {
            throw new BadRequestException(APIError.VLANS_REORDER_NIC_INVALID_LINK_VALUES);
        }

        // Call the business logic with the correct values.
        service.reorderVirtualMachineNic(vdcId, vappId, vmId, linkOldOrder, nicOrder);
    }

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
        dto.setLinks(restBuilder.buildNICLinks(ip));
        return dto;
    }
}
