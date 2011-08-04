/**
 * 
 */
package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
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
}
