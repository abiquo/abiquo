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

import static com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource.createTransferObject;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualMachineTemplateService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.StatefulInclusion;
import com.abiquo.model.validation.IncludeStateful;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;

@Parent(DatacenterRepositoryResource.class)
@Path(VirtualMachineTemplatesResource.VIRTUAL_MACHINE_TEMPLATES_PATH)
@Controller
public class VirtualMachineTemplatesResource extends AbstractResource
{
    public final static String VIRTUAL_MACHINE_TEMPLATES_PATH = "virtualmachinetemplates";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_CATEGORY_QUERY_PARAM = "categoryName";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM =
        "hypervisorTypeName";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_VDC_QUERY_PARAM = "virtualdatacenter";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_STATEFUL_QUERY_PARAM = "stateful";

    public final static String VIRTUAL_MACHINE_TEMPLATE_IMPORTED = "imported";

    @Autowired
    private VirtualMachineTemplateService service;

    @Autowired
    private InfrastructureService infrastructureService;

    /**
     * Returns all virtual machine templates
     * 
     * @title Retrieve all virtual machine templates
     * @param enterpriseId identifier of the enterprise
     * @param datacenterId identifier of the datacenter
     * @param categoryName name of a catagory
     * @param hypervisorTypeName type of an hypervisor
     * @param virtualdatacenterId identifier of a virtual datacenter
     * @param stateful boolean to include stateful templates
     * @param imported boolean to include imported templates
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {virtualMachineTemplateDto} object with all virtual machine templates
     * @throws Exception
     */
    @GET
    @Produces(VirtualMachineTemplatesDto.MEDIA_TYPE)
    public VirtualMachineTemplatesDto getVirtualMachineTemplates(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer datacenterId,
        @QueryParam(VIRTUAL_MACHINE_TEMPLATE_GET_CATEGORY_QUERY_PARAM) final String categoryName,
        @QueryParam(VIRTUAL_MACHINE_TEMPLATE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM) final String hypervisorTypeName,
        @QueryParam(VIRTUAL_MACHINE_TEMPLATE_GET_VDC_QUERY_PARAM) final Integer virtualdatacenterId,
        @QueryParam(VIRTUAL_MACHINE_TEMPLATE_GET_STATEFUL_QUERY_PARAM) @IncludeStateful(required = false) final String stateful,
        @QueryParam(VIRTUAL_MACHINE_TEMPLATE_IMPORTED) @DefaultValue("false") final Boolean imported,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO use categoryName and hypervisorType (optinals)
        // TODO query params : categoryName and HyeprvisorType.name()
        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        List<VirtualMachineTemplate> all = null;

        if (stateful == null)
        {
            all =
                service.getVirtualMachineTemplates(enterpriseId, datacenterId, categoryName,
                    hypervisorTypeName, imported);
        }
        else
        {
            if (categoryName != null)
            {
                all =
                    service.findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
                        enterpriseId, datacenterId, virtualdatacenterId, categoryName,
                        StatefulInclusion.valueOf(stateful.toUpperCase()));
            }
            else
            {
                all =
                    service.findStatefulVirtualMachineTemplatesByDatacenter(enterpriseId,
                        datacenterId, virtualdatacenterId,
                        StatefulInclusion.valueOf(stateful.toUpperCase()));
            }
        }

        VirtualMachineTemplatesDto templatessDto = new VirtualMachineTemplatesDto();

        if (!CollectionUtils.isEmpty(all))
        {
            for (VirtualMachineTemplate vmtemplate : all)
            {
                templatessDto.getCollection()
                    .add(
                        createTransferObject(vmtemplate, enterpriseId, datacenterId, amUri,
                            restBuilder));
            }
        }

        return templatessDto;
    }
}
