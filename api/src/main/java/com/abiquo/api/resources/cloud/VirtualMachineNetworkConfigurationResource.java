package com.abiquo.api.resources.cloud;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.List;

import javax.validation.constraints.Min;
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
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;

/**
 * Resource that contains all the methods related to a Virtual Machine configuration. Exposes all
 * the methods inside the URI
 * http://{host}/api/cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappids
 * }/virtualmachines/{vmids}/network
 * 
 * @author jdevesa@abiquo.com
 */
@Parent(VirtualMachineResource.class)
@Controller
@Path(VirtualMachineNetworkConfigurationResource.NETWORK)
public class VirtualMachineNetworkConfigurationResource extends AbstractResource
{
    public static final String NETWORK = "network";

    public static final String CONFIGURATION_PATH = "configurations";

    public static final String CONFIGURATION_PARAM = "{" + CONFIGURATION_PATH + "}";

    public static final String NICS_PATH = "nics";

    public static final String NIC = "nic";

    public static final String NIC_PARAM = "{" + NIC + "}";

    @Autowired
    private NetworkService service;

    /**
     * Returns all the posible network configurations.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virutal appliance.
     * @param vmId identifier of the virtual machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationsDto} object.
     * @throws Exception
     */
    @GET
    @Path(CONFIGURATION_PATH)
    public VMNetworkConfigurationsDto getListOfVirtualMachineConfigurations(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
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
     * Returns a the single network configurations.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virutal appliance.
     * @param vmId identifier of the virtual machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VMNetworkConfigurationsDto} object.
     * @throws Exception
     */
    @GET
    @Path(CONFIGURATION_PATH + "/" + CONFIGURATION_PARAM)
    public VMNetworkConfigurationDto getVirtualMachineConfigurations(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @PathParam(CONFIGURATION_PATH) final Integer vmConfigId,
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
     * @throws Exception
     */
    @PUT
    @Path(CONFIGURATION_PATH + "/" + CONFIGURATION_PARAM)
    public VMNetworkConfigurationDto updateVirtualMachineConfigurations(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @PathParam(CONFIGURATION_PATH) final Integer vmConfigId,
        final VMNetworkConfigurationDto vmConfig, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VMNetworkConfiguration configuration =
            service.updateVirtualMachineConfiguration(vdcId, vappId, vmId, vmConfigId,
                createPersistentObject(vmConfig));

        return createTransferObject(vdcId, vappId, vmId, configuration, restBuilder);

    }

    @GET
    @Path(NICS_PATH)
    public NicsDto getVirtualMachineNics(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
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
     * Create one or several NICs for a Virtual Machine.
     * 
     * @param vdcId
     * @param vappId
     * @param vmId
     * @param nicAssociations
     * @param restBuilder
     * @return
     * @throws Exception
     */
    @POST
    @Path(NICS_PATH)
    public NicDto associateVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final LinksDto nicAssociations, @Context final IRESTBuilder restBuilder) throws Exception
    {

        RESTLink privateLink = nicAssociations.searchLink("privateip");

        if (privateLink != null)
        {
            // Get the privateip
            String buildPath =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                    PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                    PrivateNetworkResource.PRIVATE_NETWORK_PARAM, IpAddressesResource.IP_ADDRESSES,
                    IpAddressesResource.IP_ADDRESS_PARAM);
            MultivaluedMap<String, String> ipsValues =
                URIResolver.resolveFromURI(buildPath, privateLink.getHref());
            // Check if the link to the private link is valid
            if (ipsValues == null
                || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !ipsValues.containsKey(PrivateNetworkResource.PRIVATE_NETWORK)
                || !ipsValues.containsKey(IpAddressesResource.IP_ADDRESS))
            {
                throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
            }

            Integer idVdc =
                Integer.parseInt(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!idVdc.equals(vdcId))
            {
                throw new BadRequestException(APIError.VLANS_IP_LINK_INVALID_VDC);
            }
            Integer vlanId =
                Integer.parseInt(ipsValues.getFirst(PrivateNetworkResource.PRIVATE_NETWORK));
            Integer ipId = Integer.parseInt(ipsValues.getFirst(IpAddressesResource.IP_ADDRESS));

            IpPoolManagement ip =
                service.associateVirtualMachinePrivateNic(vdcId, vappId, vmId, vlanId, ipId);
            return createNICTransferObject(ip, restBuilder);
        }

        throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
    }

    @DELETE
    @Path(NICS_PATH + "/" + NIC_PARAM)
    public void disassociateVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @PathParam(NIC) final Integer nicOrder, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        service.releaseNicFromVirtualMachine(vdcId, vappId, vmId, nicOrder);
    }

    @PUT
    @Path(NICS_PATH + "/" + NIC_PARAM)
    public void reorderVirtualMachineNic(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @PathParam(NIC) @Min(0) final Integer nicOrder, final LinksDto nicAssociations,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        RESTLink previousLink = nicAssociations.searchLink("nic");
        // Get the privateip
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
        // Check if the link to the private link is valid
        if (ipsValues == null
            || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
            || !ipsValues.containsKey(VirtualApplianceResource.VIRTUAL_APPLIANCE)
            || !ipsValues.containsKey(VirtualMachineResource.VIRTUAL_MACHINE)
            || !ipsValues.containsKey(VirtualMachineNetworkConfigurationResource.NIC))
        {
            throw new BadRequestException(APIError.VLANS_REORDER_NIC_INVALID_LINK);
        }

        // Get the parameters from the uri and compare them to the actual link. Only the 'nicOrder'
        // can be
        // changed
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

        service.reorderVirtualMachineNic(vdcId, vappId, vmId, linkOldOrder, nicOrder);
    }

    public static VMNetworkConfiguration createPersistentObject(
        final VMNetworkConfigurationDto vmConfig) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(VMNetworkConfiguration.class, vmConfig);
    }

    public static VMNetworkConfigurationDto createTransferObject(final Integer vdcId,
        final Integer vappId, final Integer vmId, final VMNetworkConfiguration config,
        final IRESTBuilder restBuilder) throws Exception
    {
        VMNetworkConfigurationDto dto =
            ModelTransformer.transportFromPersistence(VMNetworkConfigurationDto.class, config);
        dto.setLinks(restBuilder.buildVMNetworkConfigurationLinks(vdcId, vappId, vmId, config));
        return dto;
    }

    public static NicDto createNICTransferObject(final IpPoolManagement ip,
        final IRESTBuilder restBuilder) throws Exception
    {
        NicDto dto = ModelTransformer.transportFromPersistence(NicDto.class, ip);
        dto.setLinks(restBuilder.buildNICLinks(ip));
        return dto;
    }
}
