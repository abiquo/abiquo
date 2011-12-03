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

package com.abiquo.api.resources.appslibrary;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.client.Resource;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualMachineTemplateService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStub;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;

@Parent(VirtualMachineTemplatesResource.class)
@Path(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE_PARAM)
@Controller
public class VirtualMachineTemplateResource extends AbstractResource
{
    public final static String VIRTUAL_MACHINE_TEMPLATE = "virtualmachinetemplate";

    public final static String VIRTUAL_MACHINE_TEMPLATE_PARAM = "{" + VIRTUAL_MACHINE_TEMPLATE
        + "}";

    @Autowired
    private VirtualMachineTemplateService vmtemplateService;

    @Autowired
    private InfrastructureService infrastructureService;

    @GET
    public VirtualMachineTemplateDto getVirtualMachineTemplate(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer datacenterId,
        @PathParam(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE) final Integer virtualMachineTemplateId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineTemplate vmTemplate =
            vmtemplateService.getVirtualMachineTemplate(enterpriseId, datacenterId,
                virtualMachineTemplateId);

        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vmTemplate, enterpriseId, datacenterId, amUri, restBuilder);
    }

    @PUT
    public VirtualMachineTemplateDto editVirtualMachineTemplate(
        @PathParam(EnterpriseResource.ENTERPRISE) @NotNull @Min(1) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE) @NotNull @Min(1) final Integer virtualMachineTemplateId,
        final VirtualMachineTemplateDto vmtemplateDto, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VirtualMachineTemplate vmtemplate =
            vmtemplateService.updateVirtualMachineTemplate(enterpriseId, datacenterId,
                virtualMachineTemplateId, vmtemplateDto);

        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vmtemplate, enterpriseId, datacenterId, amUri, restBuilder);

    }

    @DELETE
    public void removeVirtualMachineTemplate(
        @PathParam(EnterpriseResource.ENTERPRISE) @NotNull @Min(1) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE) @NotNull @Min(1) final Integer virtualMachineTemplateId,
        @Context final IRESTBuilder restBuilder)
    {
        vmtemplateService.deleteVirtualMachineTemplate(enterpriseId, datacenterId,
            virtualMachineTemplateId);
    }

    /**
     * Return the {@link VirtualMachineTemplateDto}o object from the POJO
     * {@link VirtualMachineTemplate}
     */
    protected static VirtualMachineTemplateDto createTransferObject(
        final VirtualMachineTemplate vmtemplate, final Integer enterpId, final Integer dcId,
        final String amUri, final IRESTBuilder builder) throws Exception
    {
        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();
        dto.setId(vmtemplate.getId());
        dto.setCpuRequired(vmtemplate.getCpuRequired());
        dto.setDescription(vmtemplate.getDescription());
        dto.setDiskFileSize(vmtemplate.getDiskFileSize());
        dto.setHdRequired(vmtemplate.getHdRequiredInBytes());
        dto.setName(vmtemplate.getName());
        dto.setPath(vmtemplate.getPath());
        dto.setRamRequired(vmtemplate.getRamRequired());
        dto.setShared(vmtemplate.isShared());
        dto.setDiskFormatType(vmtemplate.getDiskFormatType().name());
        dto.setCostCode(vmtemplate.getCostCode());
        dto.setCreationDate(vmtemplate.getCreationDate());
        dto.setCreationUser(vmtemplate.getCreationUser());

        return addLinks(builder, dto, enterpId, dcId, vmtemplate, amUri);
    }

    private static VirtualMachineTemplateDto addLinks(final IRESTBuilder builder,
        final VirtualMachineTemplateDto dto, final Integer enterpriseId, final Integer dcId,
        final VirtualMachineTemplate vmtemplate, final String amUri)
    {
        dto.setLinks(builder.buildVirtualMachineTemplateLinks(enterpriseId, dcId, vmtemplate,
            vmtemplate.getMaster()));
        addApplianceManagerLinks(dto, amUri, enterpriseId, vmtemplate.getOvfid());
        return dto;
    }

    private static void addApplianceManagerLinks(final VirtualMachineTemplateDto dto,
        final String amUri, final Integer enterpriseId, final String ovfid)
    {
        if (ovfid != null)
        {
            ApplianceManagerResourceStub am = new ApplianceManagerResourceStub(amUri);
            Resource resource = am.ovfPackage(enterpriseId.toString(), ovfid);
            String href = resource.getUriBuilder().build(new Object[] {}).toString();

            dto.addLink(new RESTLink("templatedefinition", ovfid));
            dto.addLink(new RESTLink("template", href));
            dto.addLink(new RESTLink("templatestatus", href + "?format=status"));
            dto.addLink(new RESTLink("ovfdocument", href + "?format=envelope"));
            dto.addLink(new RESTLink("diskfile", href + "?format=diskFile"));
        }
    }
}
