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

package com.abiquo.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.wink.server.utils.LinkBuilders;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatastoreResource;
import com.abiquo.api.resources.DatastoresResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.MachineResource;
import com.abiquo.api.resources.MachinesResource;
import com.abiquo.api.resources.RackResource;
import com.abiquo.api.resources.RacksResource;
import com.abiquo.api.resources.RemoteServiceResource;
import com.abiquo.api.resources.RemoteServicesResource;
import com.abiquo.api.resources.RoleResource;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.resources.UserResource;
import com.abiquo.api.resources.UsersResource;
import com.abiquo.api.resources.VirtualMachinesInfrastructureResource;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.TemplateDefinitionListResource;
import com.abiquo.api.resources.appslibrary.TemplateDefinitionListsResource;
import com.abiquo.api.resources.appslibrary.TemplateDefinitionResource;
import com.abiquo.api.resources.appslibrary.TemplateDefinitionsResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
import com.abiquo.api.resources.cloud.DiskResource;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.PrivateNetworksResource;
import com.abiquo.api.resources.cloud.VirtualApplianceResource;
import com.abiquo.api.resources.cloud.VirtualAppliancesResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.api.resources.cloud.VirtualMachineNetworkConfigurationResource;
import com.abiquo.api.resources.cloud.VirtualMachineResource;
import com.abiquo.api.resources.cloud.VirtualMachineStorageConfigurationResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.resources.config.PrivilegeResource;
import com.abiquo.api.resources.config.SystemPropertyResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RoleLdapDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.FsmsDto;
import com.abiquo.server.core.infrastructure.LogicServersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.OrganizationsDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.pricing.CostCode;
import com.abiquo.server.core.pricing.CostCodeCurrencyDto;
import com.abiquo.server.core.pricing.Currency;
import com.abiquo.server.core.pricing.CurrencyDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.server.core.pricing.PricingTemplateDto;
import com.abiquo.server.core.pricing.PricingTierDto;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRule;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRuleDto;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.abiquo.server.core.scheduler.FitPolicyRuleDto;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.scheduler.MachineLoadRuleDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.util.PagedList;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
// This bean must not be singleton
public class RESTBuilder implements IRESTBuilder
{
    public static final String REL_EDIT = "edit";

    public static final String REL_SELF = "self";

    public static final String FIRST = "first";

    public static final String NEXT = "next";

    public static final String PREV = "previous";

    public static final String LAST = "last";

    protected LinkBuilders linkProcessor;

    @Override
    public RESTBuilder injectProcessor(final LinkBuilders linkProcessor)
    {
        this.linkProcessor = linkProcessor;
        return this;
    }

    public RESTLink buildDatacenterLink(final Integer datacenterId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildDatacenterLink(datacenterId, builder);
    }

    protected RESTLink buildDatacenterLink(final Integer datacenterId,
        final AbiquoLinkBuilder builder)
    {
        Map<String, String> params =
            Collections.singletonMap(DatacenterResource.DATACENTER, datacenterId.toString());

        return builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params);
    }

    @Override
    public List<RESTLink> buildDatacenterLinks(final DatacenterDto datacenter)

    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(DatacenterResource.DATACENTER, datacenter.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(DatacenterResource.class, REL_EDIT, params,
            DatacenterDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(RacksResource.class, RacksResource.RACKS_PATH, params,
            RacksDto.BASE_MEDIA_TYPE));
        links
            .add(builder.buildRestLink(RemoteServicesResource.class,
                RemoteServicesResource.REMOTE_SERVICES_PATH, params,
                RemoteServicesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.HYPERVISORS_PATH, DatacenterResource.HYPERVISORS_PATH, params,
            HypervisorTypesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ENTERPRISES_PATH, DatacenterResource.ENTERPRISES_REL, params,
            EnterprisesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.UPDATE_RESOURCES_PATH, DatacenterResource.UPDATE_RESOURCES, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_SINGLE_PATH,
            DatacenterResource.ACTION_DISCOVER_SINGLE_REL, params, MachineDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_MULTIPLE_PATH,
            DatacenterResource.ACTION_DISCOVER_MULTIPLE_REL, params, MachinesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_HYPERVISOR_TYPE,
            DatacenterResource.ACTION_DISCOVER_HYPERVISOR_TYPE_REL, params, MediaType.TEXT_PLAIN));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_MACHINES_CHECK, DatacenterResource.ACTION_MACHINES_CHECK_REL,
            params, MachineStateDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_MACHINES_CHECK_IPMI,
            DatacenterResource.ACTION_MACHINES_CHECK_IPMI_REL, params));

        // links.add(builder.buildRestLink(OVFPackageListsResource.class,
        // OVFPackageListsResource.OVF_PACKAGE_LISTS_PATH, params));
        // links.add(builder.buildRestLink(OVFPackagesResource.class,
        // OVFPackagesResource.OVF_PACKAGES_PATH, params));

        return links;
    }

    @Override
    public List<RESTLink> buildRackLinks(final Integer datacenterId, final RackDto rack)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params, DatacenterDto.BASE_MEDIA_TYPE));

        params.put(RackResource.RACK, rack.getId().toString());

        links.add(builder.buildRestLink(RackResource.class, REL_EDIT, params,
            RackDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(MachinesResource.class, MachinesResource.MACHINES_PATH,
            params, MachinesDto.BASE_MEDIA_TYPE));

        if (rack instanceof UcsRackDto)
        {
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE_TEMPLATE,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE_TEMPLATE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_CLONE,
                RackResource.RACK_ACTION_LOGICSERVERS_CLONE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_DELETE,
                RackResource.RACK_ACTION_LOGICSERVERS_DELETE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_DISSOCIATE,
                RackResource.RACK_ACTION_LOGICSERVERS_DISSOCIATE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS, RackResource.RACK_ACTION_LOGICSERVERS_REL,
                params, LogicServersDto.BASE_MEDIA_TYPE));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_TEMPLATES,
                RackResource.RACK_ACTION_LOGICSERVERS_TEMPLATES_REL, params,
                LogicServersDto.BASE_MEDIA_TYPE));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE_CLONE,
                RackResource.RACK_ACTION_LOGICSERVERS_ASSOCIATE_CLONE_REL, params));
            links.add(builder.buildRestLink(RackResource.class,
                RackResource.RACK_ACTION_ORGANIZATIONS, RackResource.RACK_ACTION_ORGANIZATIONS_REL,
                params, OrganizationsDto.BASE_MEDIA_TYPE));
            links.add(builder.buildRestLink(RackResource.class, RackResource.RACK_ACTION_FSM,
                RackResource.RACK_ACTION_FSM_REL, params, FsmsDto.BASE_MEDIA_TYPE));
        }
        return links;
    }

    @Override
    public List<RESTLink> buildMachineLinks(final Integer datacenterId, final Integer rackId,
        final Boolean managedRack, final Enterprise enterprise, final MachineDto machine)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return this.buildMachineLinks(datacenterId, rackId, managedRack, enterprise, machine,
            builder);
    }

    protected RESTLink buildMachineRackLink(final AbiquoLinkBuilder builder,
        final Map<String, String> params, final Boolean managedRack)
    {
        RESTLink link;

        if (!managedRack)
        {
            link =
                builder.buildRestLink(RackResource.class, RackResource.RACK, params,
                    RackDto.BASE_MEDIA_TYPE);
        }
        else
        {
            link =
                builder.buildRestLink(RackResource.class, RackResource.RACK, params,
                    UcsRackDto.BASE_MEDIA_TYPE);
        }

        return link;
    }

    public List<RESTLink> buildMachineLinks(final Integer datacenterId, final Integer rackId,
        final Boolean managedRack, final Enterprise enterprise, final MachineDto machine,
        final AbiquoLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(RackResource.RACK, rackId.toString());
        params.put(MachineResource.MACHINE, machine.getId().toString());

        links.add(buildMachineRackLink(builder, params, managedRack));
        links.add(builder.buildRestLink(MachineResource.class, REL_EDIT, params,
            MachineDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatastoresResource.class,
            DatastoresResource.DATASTORES_PATH, params, DatastoresDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(VirtualMachinesInfrastructureResource.class,
            VirtualMachinesInfrastructureResource.VIRTUAL_MACHINES_INFRASTRUCTURE_PARAM, params,
            VirtualMachinesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(MachineResource.class,
            MachineResource.MACHINE_ACTION_CHECK, MachineResource.MACHINE_CHECK, params,
            MachineStateDto.BASE_MEDIA_TYPE));

        if (enterprise != null)
        {
            params.put(EnterpriseResource.ENTERPRISE, enterprise.getId().toString());
            links.add(builder.buildRestLink(EnterpriseResource.class,
                EnterpriseResource.ENTERPRISE, params));
        }

        return links;
    }

    @Override
    public List<RESTLink> buildRemoteServiceLinks(final Integer datacenterId,
        final RemoteServiceDto remoteService)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        List<RESTLink> links = buildRemoteServiceLinks(datacenterId, remoteService, builder);

        return links;
    }

    protected List<RESTLink> buildRemoteServiceLinks(final Integer datacenterId,
        final RemoteServiceDto remoteService, final AbiquoLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());

        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params, DatacenterDto.BASE_MEDIA_TYPE));

        params.put(RemoteServiceResource.REMOTE_SERVICE, remoteService.getType().toString()
            .toLowerCase().replace("_", ""));

        if (remoteService.getType().canBeChecked())
        {
            links
                .add(builder.buildRestLink(RemoteServiceResource.class,
                    RemoteServiceResource.CHECK_RESOURCE, InfrastructureService.CHECK_RESOURCE,
                    params));
        }

        links.add(builder.buildRestLink(RemoteServiceResource.class, REL_EDIT, params,
            RemoteServiceDto.BASE_MEDIA_TYPE));
        return links;
    }

    @Override
    public List<RESTLink> buildPrivilegeLink(final PrivilegeDto privilege)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(PrivilegeResource.PRIVILEGE, privilege.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(PrivilegeResource.class, REL_SELF, params,
            PrivilegeDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildPrivilegeListLink(final PrivilegeDto privilege)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(PrivilegeResource.PRIVILEGE, privilege.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(PrivilegeResource.class, "privilege", params,
            PrivilegeDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildRoleLinks(final RoleDto role)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(RoleResource.ROLE, role.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params,
            RoleDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(RoleResource.class,
            RoleResource.ROLE_ACTION_GET_PRIVILEGES_PATH, PrivilegeResource.PRIVILEGES, params,
            PrivilegesDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildRoleLinks(final Integer enterpriseId, final RoleDto role)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        params.put(RoleResource.ROLE, role.getId().toString());
        links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params,
            RoleDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(RoleResource.class,
            RoleResource.ROLE_ACTION_GET_PRIVILEGES_PATH, PrivilegeResource.PRIVILEGES, params,
            RoleDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildEnterpriseLinks(final EnterpriseDto enterprise)
    {
        Map<String, String> params =
            Collections.singletonMap(EnterpriseResource.ENTERPRISE, enterprise.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildEnterpriseLinks(builder, params);
    }

    protected List<RESTLink> buildEnterpriseLinks(final AbiquoLinkBuilder builder,
        final Map<String, String> params)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        links.add(builder.buildRestLink(EnterpriseResource.class, REL_EDIT, params,
            EnterpriseDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(UsersResource.class, UsersResource.USERS_PATH, params,
            UsersDto.BASE_MEDIA_TYPE));

        // apps library
        links.add(builder.buildRestLink(TemplateDefinitionListsResource.class,
            TemplateDefinitionListsResource.TEMPLATE_DEFINITION_LISTS_PATH, params,
            TemplateDefinitionListsDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(TemplateDefinitionsResource.class,
            TemplateDefinitionsResource.TEMPLATE_DEFINITIONS_PATH, params,
            TemplateDefinitionsDto.BASE_MEDIA_TYPE));

        // action get virtual machines by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALMACHINES_PATH,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params,
            VirtualMachinesDto.BASE_MEDIA_TYPE));

        // action get virtual appliances by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALAPPLIANCES_PATH,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH, params,
            VirtualAppliancesDto.MEDIA_TYPE));

        // action get virtual appliances by enterprise
        links.add(builder.buildRelLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALAPPLIANCES_PATH,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH, params,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH));

        // action get virtual appliances by enterprise
        links.add(builder.buildRelLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALAPPLIANCES_PATH,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH, params,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH));

        // action get ips by enterprise
        links.add(builder.buildRelLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_IPS_PATH, IpAddressesResource.IP_ADDRESSES,
            params, IpAddressesResource.IP_ADDRESSES));

        // action get virtual datacenters by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALDATACENTERS_PATH,
            VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH, params,
            VirtualDatacentersDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(EnterpriseResource.class,
            DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
            DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH, params));
        return links;
    }

    @Override
    public List<RESTLink> buildEnterprisePropertiesLinks(final Integer enterpriseId,
        final EnterprisePropertiesDto enterpriseProperties)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildUserLinks(final Integer enterpriseId, final Integer roleId,
        final UserDto user)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));

        params.put(RoleResource.ROLE, roleId.toString());

        links.add(builder.buildRestLink(RoleResource.class, RoleResource.ROLE, params,
            RoleDto.BASE_MEDIA_TYPE));

        params.put(UserResource.USER, user.getId().toString());

        links.add(builder.buildRestLink(UserResource.class, REL_EDIT, params,
            UserDto.BASE_MEDIA_TYPE));

        // virtual machines
        links.add(builder.buildRestLink(UserResource.class,
            UserResource.USER_ACTION_GET_VIRTUALMACHINES_PATH,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params,
            VirtualMachinesDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildStoragePoolLinks(final Integer datacenterId, final Integer deviceId,
        final Integer tierId, final String poolId)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildTemplateDefinitionListLinks(final Integer enterpriseId,
        final TemplateDefinitionListDto templateDefinitionList)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));

        params.put(TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST, templateDefinitionList
            .getId().toString());

        links.add(builder.buildRestLink(TemplateDefinitionListResource.class, REL_EDIT, params,
            TemplateDefinitionListDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(TemplateDefinitionListResource.class,
            TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_PATH,
            TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_REL, params,
            TemplatesStateDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildTemplateDefinitionLinks(final Integer enterpriseId,
        final TemplateDefinitionDto templateDefinition, final Category category)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        if (category != null)
        {
            params.put(CategoryResource.CATEGORY, String.valueOf(category.getId()));
        }

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));
        if (category != null)
        {
            links
                .add(builder.buildRestLink(CategoryResource.class, null, CategoryResource.CATEGORY,
                    category.getName(), params, CategoryDto.BASE_MEDIA_TYPE));
        }

        params.put(TemplateDefinitionResource.TEMPLATE_DEFINITION, templateDefinition.getId()
            .toString());
        links.add(builder.buildRestLink(TemplateDefinitionResource.class, REL_EDIT, params,
            TemplateDefinitionDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildPrivateNetworkLinks(final Integer virtualDatacenterId,
        final VLANNetworkDto network)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params,
            VirtualDatacenterDto.BASE_MEDIA_TYPE));

        params.put(PrivateNetworkResource.PRIVATE_NETWORK, network.getId().toString());

        links.add(builder.buildRestLink(PrivateNetworkResource.class, REL_EDIT, params,
            VLANNetworkDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(IpAddressesResource.class,
            IpAddressesResource.IP_ADDRESSES, params, IpPoolManagementDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildPublicNetworkLinks(final Integer datacenterId,
        final VLANNetwork network)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildPublicNetworksLinks(final Integer datacenterId)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildVirtualDatacenterLinks(final VirtualDatacenter vdc,
        final Integer datacenterId, final Integer enterpriseId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();

        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdc.getId().toString());
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        params.put(PrivateNetworkResource.PRIVATE_NETWORK, vdc.getDefaultVlan().getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class, REL_EDIT, params,
            VirtualDatacenterDto.BASE_MEDIA_TYPE));

        links
            .add(builder.buildRestLink(PrivateNetworksResource.class,
                PrivateNetworksResource.PRIVATE_NETWORKS_PATH, params,
                VLANNetworksDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params, DatacenterDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(VirtualAppliancesResource.class,
            VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH, params,
            VirtualAppliancesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(PrivateNetworkResource.class,
            VirtualDatacenterResource.DEFAULT_NETWORK_REL, params, VLANNetworkDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER_GET_IPS_PATH,
            VirtualDatacenterResource.VIRTUAL_DATACENTER_GET_IPS_REL, params,
            IpPoolManagementDto.BASE_MEDIA_TYPE));
        RESTLink getVlanLink =
            builder.buildRestLink(VirtualDatacenterResource.class,
                VirtualDatacenterResource.DEFAULT_VLAN_PATH,
                VirtualDatacenterResource.DEFAULT_VLAN_REL, params, VLANNetworkDto.BASE_MEDIA_TYPE);
        getVlanLink.setTitle("GET");
        RESTLink setVlanLink =
            builder.buildRestLink(VirtualDatacenterResource.class,
                VirtualDatacenterResource.DEFAULT_VLAN_PATH,
                VirtualDatacenterResource.DEFAULT_VLAN_REL, params, LinksDto.BASE_MEDIA_TYPE);
        setVlanLink.setTitle("PUT");
        links.add(getVlanLink);
        links.add(setVlanLink);
        return links;
    }

    @Override
    public List<RESTLink> buildVirtualApplianceLinks(final VirtualApplianceDto dto,
        final Integer vdcId, final Integer enterpriseId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, dto.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        links.add(builder.buildRestLink(VirtualApplianceResource.class, REL_EDIT, params,
            VirtualApplianceDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params,
            VirtualDatacenterDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachinesResource.class,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params,
            VirtualMachinesDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_STATE_REL,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_STATE_REL, params,
            VirtualApplianceStateDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_GET_IPS_PATH,
            IpAddressesResource.IP_ADDRESSES, params, IpPoolManagementDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_UNDEPLOY_PATH,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_UNDEPLOY_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_DEPLOY_PATH,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_DEPLOY_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_PRICE_PATH,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_PRICE_REL, params, MediaType.TEXT_PLAIN));

        return links;
    }

    @Override
    public List<RESTLink> buildDatastoreLinks(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Datastore datastore)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(RackResource.RACK, rackId.toString());
        params.put(MachineResource.MACHINE, machineId.toString());
        params.put(DatastoreResource.DATASTORE, datastore.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(DatastoreResource.class, REL_EDIT, params,
            DatastoreDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineAdminLinks(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer enterpriseId,
        final Integer userId, final HypervisorType machineType, final VirtualAppliance vapp,
        final Integer vmId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        // Only build the machine hypervisor link if the hypervisor is deployed
        if (datacenterId != null && rackId != null && machineId != null)
        {
            params.put(DatacenterResource.DATACENTER, datacenterId.toString());
            params.put(RackResource.RACK, rackId.toString());
            params.put(MachineResource.MACHINE, machineId.toString());

            RESTLink machineLink =
                builder.buildRestLink(MachineResource.class, MachineResource.MACHINE, params,
                    MachineDto.BASE_MEDIA_TYPE);
            // Title is used in the UI
            machineLink.setTitle(machineType.name());
            links.add(machineLink);
        }

        if (vapp != null)
        {
            params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vapp.getVirtualDatacenter()
                .getId().toString());
            params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vapp.getId().toString());
            params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());
            links.add(builder.buildRestLink(VirtualMachineResource.class,
                VirtualMachineResource.VIRTUAL_MACHINE, params, VirtualMachineDto.BASE_MEDIA_TYPE));
        }

        if (enterpriseId != null)
        {
            params = new HashMap<String, String>();
            params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
            links.add(builder.buildRestLink(EnterpriseResource.class,
                EnterpriseResource.ENTERPRISE, params, EnterpriseDto.BASE_MEDIA_TYPE));

        }

        if (userId != null)
        {
            params = new HashMap<String, String>();
            params.put(UserResource.USER, userId.toString());
            links.add(builder.buildRestLink(UserResource.class, UserResource.USER, params,
                UserDto.BASE_MEDIA_TYPE));

        }

        return links;
    }

    @Override
    public List<RESTLink> buildSystemPropertyLinks(final SystemPropertyDto systemProperty)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(SystemPropertyResource.SYSTEM_PROPERTY, systemProperty.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(SystemPropertyResource.class, REL_EDIT, params,
            SystemPropertyDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineCloudLinks(final Integer vdcId, final Integer vappId,
        final VirtualMachine vm, final boolean chefEnabled, final Integer[] volumeIds,
        final Integer[] diskIds, final List<IpPoolManagement> ips)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vm.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH,
            VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH, params,
            VMNetworkConfigurationsDto.BASE_MEDIA_TYPE));

        if (vm.getNetworkConfiguration() != null)
        {
            params.put(VirtualMachineNetworkConfigurationResource.CONFIGURATION, vm
                .getNetworkConfiguration().getId().toString());
            links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
                VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH + "/"
                    + VirtualMachineNetworkConfigurationResource.CONFIGURATION_PARAM,
                VirtualMachineNetworkConfigurationResource.DEFAULT_CONFIGURATION, params,
                VMNetworkConfigurationDto.BASE_MEDIA_TYPE));
        }

        links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.NICS_PATH,
            VirtualMachineNetworkConfigurationResource.NICS_PATH, params, NicsDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineStorageConfigurationResource.class,
            VirtualMachineStorageConfigurationResource.DISKS_PATH,
            VirtualMachineStorageConfigurationResource.DISKS_PATH, params,
            DisksManagementDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_GET_IPS_PATH,
            IpAddressesResource.IP_ADDRESSES, params, IpsPoolManagementDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_STATE_PATH,
            VirtualMachineResource.VIRTUAL_MACHINE_STATE_REL, params,
            VirtualMachineStateDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_UNDEPLOY_PATH,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_UNDEPLOY_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_DEPLOY_PATH,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_DEPLOY_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_RESET,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_RESET_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_SNAPSHOT,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_SNAPSHOT_REL, params,
            AcceptedRequestDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class, TaskResourceUtils.TASKS_PATH,
            TaskResourceUtils.TASKS_REL, params, TasksDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class, RESTBuilder.REL_EDIT, params,
            VirtualMachineDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineCloudAdminLinks(final Integer vdcId,
        final Integer vappId, final VirtualMachine vm, final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer enterpriseId,
        final Integer userId, final boolean chefEnabled, final Integer[] volumeIds,
        final Integer[] diskIds, final List<IpPoolManagement> ips, final HypervisorType vdcType,
        final VirtualAppliance vapp)
    {

        List<RESTLink> links = new ArrayList<RESTLink>();
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.addAll(buildVirtualMachineAdminLinks(datacenterId, rackId, machineId, enterpriseId,
            userId, vdcType, vapp, vm.getId()));
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());

        RESTLink vdcLink =
            builder.buildRestLink(VirtualDatacenterResource.class,
                VirtualDatacenterResource.VIRTUAL_DATACENTER, params,
                VirtualDatacenterDto.BASE_MEDIA_TYPE);
        // Title is used in the UI
        vdcLink.setTitle(vdcType.name());
        links.add(vdcLink);

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE, params, VirtualApplianceDto.MEDIA_TYPE));

        links.addAll(buildVirtualMachineCloudLinks(vdcId, vappId, vm, chefEnabled, volumeIds,
            diskIds, ips));

        return links;
    }

    @Override
    public List<RESTLink> buildDatacenterRepositoryLinks(final Integer enterpriseId,
        final Integer dcId, final String dcName, final Integer repoId)
    {

        List<RESTLink> links = new ArrayList<RESTLink>();
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> paramsDc = new HashMap<String, String>();
        paramsDc.put(DatacenterResource.DATACENTER, dcId.toString());

        RESTLink dclink =
            builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
                paramsDc, DatacenterDto.BASE_MEDIA_TYPE);
        dclink.setTitle(dcName);
        links.add(dclink);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));

        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, repoId.toString());
        links.add(builder.buildRestLink(DatacenterRepositoryResource.class, REL_EDIT, params,
            DatacenterRepositoryDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(VirtualMachineTemplatesResource.class,
            VirtualMachineTemplatesResource.VIRTUAL_MACHINE_TEMPLATES_PATH, params,
            VirtualMachineTemplatesDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DatacenterRepositoryResource.class,
            DatacenterRepositoryResource.DATACENTER_REPOSITORY_REFRESH_PATH, "refresh", params));

        return links;
    }

    protected List<RESTLink> buildVirtualMachineTemplateLinks(final Integer enterpriseId,
        final Integer dcId, final VirtualMachineTemplate vmtemplate,
        final VirtualMachineTemplate master, final AbiquoLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> paramsDc = new HashMap<String, String>();
        paramsDc.put(DatacenterResource.DATACENTER, dcId.toString());

        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            paramsDc, DatacenterDto.BASE_MEDIA_TYPE));

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE));

        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, dcId.toString());
        links.add(builder.buildRestLink(DatacenterRepositoryResource.class,
            DatacenterRepositoryResource.DATACENTER_REPOSITORY, params,
            DatacenterRepositoryDto.BASE_MEDIA_TYPE));

        params.put(CategoryResource.CATEGORY, vmtemplate.getCategory().getId().toString());
        RESTLink categoryLink =
            builder.buildRestLink(CategoryResource.class, CategoryResource.CATEGORY, params,
                CategoryDto.BASE_MEDIA_TYPE);
        categoryLink.setTitle(vmtemplate.getCategory().getName());
        links.add(categoryLink);

        params.put(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE, vmtemplate.getId()
            .toString());
        RESTLink vmtemplateLink =
            builder.buildRestLink(VirtualMachineTemplateResource.class, REL_EDIT, params,
                VirtualMachineTemplateDto.BASE_MEDIA_TYPE);
        vmtemplateLink.setTitle(vmtemplate.getName());
        links.add(vmtemplateLink);

        // TODO: How to build a link for an imported one??

        if (master != null)
        {
            // Master's enterprise may differ from the current virtual machine template.
            // Datacenter repository id will be the same (the id of the datacenter)
            params.put(EnterpriseResource.ENTERPRISE, master.getEnterprise().getId().toString());
            params.put(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE, master.getId()
                .toString());
            RESTLink masterLink =
                builder.buildRestLink(VirtualMachineTemplateResource.class, "master", params);
            masterLink.setTitle(master.getName());
            links.add(masterLink);
        }

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineTemplateLinks(final Integer enterpriseId,
        final Integer dcId, final VirtualMachineTemplate template,
        final VirtualMachineTemplate master)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildVirtualMachineTemplateLinks(enterpriseId, dcId, template, master, builder);
    }

    @Override
    public RESTLink buildVirtualMachineTemplateLink(final Integer enterpriseId, final Integer dcId,
        final Integer virtualMachineTemplateId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, dcId.toString());
        params.put(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE,
            virtualMachineTemplateId.toString());

        return builder.buildRestLink(VirtualMachineTemplateResource.class,
            VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE, params,
            VirtualMachineTemplateDto.BASE_MEDIA_TYPE);
    }

    @Override
    public List<RESTLink> buildPaggingLinks(final String absolutePath, final PagedList< ? > list)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        // If the list is empty, we don't return the links
        if (list.size() == 0)
        {
            return links;
        }

        // Add FIRST element
        links.add(new RESTLink(FIRST, absolutePath));

        if (list.getCurrentElement() != 0)
        {
            // Previous using the page size avoiding to be less than 0.
            Integer previous = list.getCurrentElement() - list.getPageSize();
            previous =
                previous > list.getTotalResults() ? list.getTotalResults() - 2 * list.getPageSize()
                    : previous;
            previous = previous < 0 ? 0 : previous;

            links.add(new RESTLink(PREV, absolutePath + "?" + AbstractResource.START_WITH + "="
                + previous));
        }
        Integer next = list.getCurrentElement() + list.getPageSize();
        if (next < list.getTotalResults())
        {
            links.add(new RESTLink(NEXT, absolutePath + "?" + AbstractResource.START_WITH + "="
                + next));
        }

        Integer last = list.getTotalResults() - list.getPageSize();
        if (last < 0)
        {
            last = 0;
        }
        links
            .add(new RESTLink(LAST, absolutePath + "?" + AbstractResource.START_WITH + "=" + last));
        return links;
    }

    @Override
    public List<RESTLink> buildRasdLinks(final RasdManagement resource)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        Map<String, String> params = new HashMap<String, String>();
        if (resource.getVirtualDatacenter() != null)
        {
            params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, resource
                .getVirtualDatacenter().getId().toString());
            RESTLink vdcLink =
                builder.buildRestLink(VirtualDatacenterResource.class,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER, params,
                    VirtualDatacenterDto.BASE_MEDIA_TYPE);
            vdcLink.setTitle(resource.getVirtualDatacenter().getName());
            links.add(vdcLink);
            if (resource.getVirtualAppliance() != null)
            {
                params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, resource
                    .getVirtualAppliance().getId().toString());
                RESTLink vappLink =
                    builder.buildRestLink(VirtualApplianceResource.class,
                        VirtualApplianceResource.VIRTUAL_APPLIANCE, params,
                        VirtualApplianceDto.BASE_MEDIA_TYPE);
                vappLink.setTitle(resource.getVirtualAppliance().getName());
                links.add(vappLink);

                if (resource.getVirtualMachine() != null)
                {
                    params.put(VirtualMachineResource.VIRTUAL_MACHINE, resource.getVirtualMachine()
                        .getId().toString());
                    RESTLink vmLink =
                        builder.buildRestLink(VirtualMachineResource.class,
                            VirtualMachineResource.VIRTUAL_MACHINE, params,
                            VirtualMachineDto.BASE_MEDIA_TYPE);
                    vmLink.setTitle(resource.getVirtualMachine().getName());
                    links.add(vmLink);
                }
            }
        }

        return links;
    }

    @Override
    public RESTLink buildEnterpriseLink(final Integer enterpriseId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildEnterpriseLink(enterpriseId, builder);
    }

    protected RESTLink buildEnterpriseLink(final Integer enterpriseId,
        final AbiquoLinkBuilder builder)
    {
        Map<String, String> params =
            Collections.singletonMap(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        return builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params, EnterpriseDto.BASE_MEDIA_TYPE);
    }

    @Override
    public List<RESTLink> buildIpRasdLinks(final IpPoolManagement ip)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, ip.getVirtualDatacenter().getId()
            .toString());
        params.put(PrivateNetworkResource.PRIVATE_NETWORK, ip.getVlanNetwork().getId().toString());
        params.put(IpAddressesResource.IP_ADDRESS, ip.getId().toString());

        List<RESTLink> links = new ArrayList<RESTLink>();
        RESTLink link =
            builder.buildRestLink(PrivateNetworkResource.class,
                PrivateNetworkResource.PRIVATE_NETWORK, params);
        link.setTitle(ip.getVlanNetwork().getName());

        RESTLink ipLink =
            builder.buildRestLink(IpAddressesResource.class, IpAddressesResource.IP_ADDRESS_PARAM,
                REL_SELF, params);
        ipLink.setTitle(VirtualMachineNetworkConfigurationResource.PRIVATE_IP);

        links.add(link);
        links.add(ipLink);

        return links;
    }

    @Override
    public List<RESTLink> buildLicenseLinks(final LicenseDto license)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildLimitsLinks(final Enterprise enterprise,
        final Datacenter datacenter, final DatacenterLimitsDto dto)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildTierLinks(final Integer datacenterId, final Integer tierId)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildStorageDeviceLinks(final Integer datacenterId, final Integer deviceId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildVolumeInfrastructureLinks(final VolumeManagement volume)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildEnterpriseExclusionRuleLinks(
        final EnterpriseExclusionRuleDto enterpriseExclusionDto,
        final EnterpriseExclusionRule enterpriseExclusion)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildVolumeCloudLinks(final VolumeManagement volume)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildMachineLoadRuleLinks(final MachineLoadRuleDto mlrDto,
        final MachineLoadRule mlr)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildRoleLdapLinks(final Integer roleId, final RoleLdapDto roleLdap)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildFitPolicyRuleLinks(final FitPolicyRuleDto fprDto,
        final FitPolicyRule fpr)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildVirtualApplianceStateLinks(final VirtualApplianceStateDto dto,
        final Integer id, final Integer vdcId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, id.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class, "parent", params,
            VirtualDatacenterDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_STATE_REL, REL_EDIT, params,
            VirtualApplianceDto.BASE_MEDIA_TYPE));
        return links;
    }

    @Override
    public List<RESTLink> buildCurrencyLinks(final CurrencyDto currencyDto, final Currency currency)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildPublicIpLinks(final Integer datacenterId, final IpPoolManagement ip)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildPublicIpRasdLinks(final Integer vdcId, final IpPoolManagement ip)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildPricingTemplateLinks(final Integer currencyId,
        final PricingTemplateDto pricingTemplate)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildCostCodeLinks(final Integer costCodeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildCostCodeCurrencyLinks(final CostCode costCode,
        final Currency currency, final CostCodeCurrencyDto dto)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildPricingCostCodeLinks(final CostCode costCode,
        final PricingTemplate pricingTemplate, final PricingCostCodeDto dto)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildPricingTierLinks(final Tier tier,
        final PricingTemplate pricingTemplate, final PricingTierDto dto)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RESTLink> buildVMNetworkConfigurationLinks(final Integer vdcId,
        final Integer vappId, final Integer vmId, final VMNetworkConfiguration config)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());
        params.put(VirtualMachineNetworkConfigurationResource.CONFIGURATION, config.getId()
            .toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH + "/"
                + VirtualMachineNetworkConfigurationResource.CONFIGURATION_PARAM, REL_EDIT, params,
            VMNetworkConfigurationDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineStateLinks(final Integer vappId, final Integer vdcId,
        final Integer vmId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        links.add(builder.buildRestLink(VirtualMachineResource.class, "parent", params,
            VirtualMachineDto.BASE_MEDIA_TYPE));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_STATE_PATH, REL_EDIT, params,
            VirtualMachineStateDto.BASE_MEDIA_TYPE));
        return links;
    }

    @Override
    public List<RESTLink> buildNICLinks(final IpPoolManagement ip)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, ip.getVirtualDatacenter().getId()
            .toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, ip.getVirtualAppliance().getId()
            .toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, ip.getVirtualMachine().getId()
            .toString());
        params.put(VirtualMachineNetworkConfigurationResource.NIC, ip.getId().toString());
        params.put(IpAddressesResource.IP_ADDRESS, ip.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.NICS_PATH + "/"
                + VirtualMachineNetworkConfigurationResource.NIC_PARAM, REL_EDIT, params,
            NicDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(IpAddressesResource.class,
            IpAddressesResource.IP_ADDRESS_PARAM, IpAddressesResource.IP_ADDRESS_PARAM, params,
            IpPoolManagementDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildExternalNetworkLinks(final Integer enterpriseId,
        final VLANNetworkDto dto)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildExternalNetworkByDatacenterLinks(final Integer enterpriseId,
        final Integer limitId, final VLANNetwork network)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildExternalNetworksByDatacenterLinks(final Integer enterpriseId,
        final Integer limitId)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildExternalIpRasdLinks(final Integer entId, final Integer limitId,
        final IpPoolManagement ip)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildDiskLinks(final DiskManagement disk, final Integer vdcId,
        final Integer vappId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, disk.getVirtualMachine().getId()
            .toString());
        params.put(VirtualMachineStorageConfigurationResource.DISK, String.valueOf(disk.getId()));

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineStorageConfigurationResource.class,
            VirtualMachineStorageConfigurationResource.DISKS_PATH + "/"
                + VirtualMachineStorageConfigurationResource.DISK_PARAM, REL_EDIT, params,
            DiskManagementDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public List<RESTLink> buildCategoryLinks(final Category category)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(CategoryResource.CATEGORY, category.getId().toString());
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        if (category.getEnterprise() != null)
        {
            params.put(EnterpriseResource.ENTERPRISE, category.getEnterprise().getId().toString());
            links.add(builder.buildRestLink(EnterpriseResource.class,
                EnterpriseResource.ENTERPRISE, params, EnterpriseDto.BASE_MEDIA_TYPE));
        }
        RESTLink editLink =
            builder.buildRestLink(CategoryResource.class, REL_EDIT, params,
                CategoryDto.BASE_MEDIA_TYPE);
        links.add(editLink);

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualDatacenterDiskLinks(final DiskManagement disk)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, disk.getVirtualDatacenter()
            .getId().toString());
        params.put(DiskResource.DISK, disk.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params,
            VirtualDatacenterDto.BASE_MEDIA_TYPE));
        links.add(builder.buildRestLink(DiskResource.class, REL_EDIT, params,
            DiskManagementDto.BASE_MEDIA_TYPE));

        return links;
    }

    @Override
    public RESTLink buildUserLink(final Integer enterpriseId, final Integer userId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildEnterpriseLink(enterpriseId, builder);
    }

    protected RESTLink buildUserLink(final Integer enterpriseId, final Integer userId,
        final AbiquoLinkBuilder builder)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        params.put(UserResource.USER, userId.toString());
        return builder.buildRestLink(UserResource.class, UserResource.USER, params,
            UserDto.BASE_MEDIA_TYPE);
    }

    @Override
    public List<RESTLink> buildVirtualDatacenterTierLinks(final Integer virtualDatacenterId,
        final Integer id)
    {
        return null;
    }

    @Override
    public RESTLink buildMovedVolumeLinks(final VolumeManagement movedVolume)
    {
        return null;
    }

    @Override
    public RESTLink buildVirtualMachineLink(final Integer vdc, final Integer vapp, final Integer vm)
    {

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdc.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vapp.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vm.toString());

        return builder.buildRestLink(VirtualMachineResource.class, "", params);

    }

    public static RESTLink searchLinkInList(final String rel, final List<RESTLink> list)
    {
        for (RESTLink link : list)
        {
            if (link.getRel() != null)
            {
                if (link.getRel().equals(rel))
                {
                    return link;
                }
            }
        }
        return null;
    }

    public static void deleteLinkFromList(final String rel, final List<RESTLink> list)
    {
        RESTLink link = searchLinkInList(rel, list);

        if (link != null)
        {
            list.remove(link);
        }
    }

    @Override
    public RESTLink buildVirtualMachineTaskLink(final Integer vdc, final Integer vapp,
        final Integer vm, final String taskId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, String.valueOf(vdc));
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, String.valueOf(vapp));
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, String.valueOf(vm));
        params.put(TaskResourceUtils.TASK, taskId);

        return builder.buildRestLink(VirtualMachineResource.class, TaskResourceUtils.TASK_PATH,
            TaskResourceUtils.SELF_REL, params, TaskDto.MEDIA_TYPE);
    }
}
