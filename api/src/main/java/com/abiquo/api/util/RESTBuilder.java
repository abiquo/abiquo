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
import com.abiquo.api.resources.UserResource;
import com.abiquo.api.resources.UsersResource;
import com.abiquo.api.resources.VirtualMachinesInfrastructureResource;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.resources.appslibrary.OVFPackageListResource;
import com.abiquo.api.resources.appslibrary.OVFPackageListsResource;
import com.abiquo.api.resources.appslibrary.OVFPackageResource;
import com.abiquo.api.resources.appslibrary.OVFPackagesResource;
import com.abiquo.api.resources.appslibrary.VirtualImageResource;
import com.abiquo.api.resources.appslibrary.VirtualImagesResource;
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
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RoleLdapDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
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
import com.abiquo.server.core.util.PagedList;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
// This bean must not be singleton
public class RESTBuilder implements IRESTBuilder
{
    public static final String REL_EDIT = "edit";

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
        links.add(builder.buildRestLink(DatacenterResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(RacksResource.class, RacksResource.RACKS_PATH, params));
        links.add(builder.buildRestLink(RemoteServicesResource.class,
            RemoteServicesResource.REMOTE_SERVICES_PATH, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.HYPERVISORS_PATH, DatacenterResource.HYPERVISORS_PATH, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ENTERPRISES_PATH, DatacenterResource.ENTERPRISES_REL, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.UPDATE_RESOURCES_PATH, DatacenterResource.UPDATE_RESOURCES, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_SINGLE, DatacenterResource.ACTION_DISCOVER_REL,
            params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_MULTIPLE,
            DatacenterResource.ACTION_DISCOVER_MULTIPLE_REL, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.ACTION_DISCOVER_HYPERVISOR_TYPE,
            DatacenterResource.ACTION_DISCOVER_HYPERVISOR_TYPE_REL, params));

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
            params));

        params.put(RackResource.RACK, rack.getId().toString());

        links.add(builder.buildRestLink(RackResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(MachinesResource.class, MachinesResource.MACHINES_PATH,
            params));

        return links;
    }

    @Override
    public List<RESTLink> buildMachineLinks(final Integer datacenterId, final Integer rackId,
        final Boolean managedRack, final MachineDto machine)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return this.buildMachineLinks(datacenterId, rackId, managedRack, machine, builder);
    }

    public List<RESTLink> buildMachineLinks(final Integer datacenterId, final Integer rackId,
        final Boolean managedRack, final MachineDto machine, final AbiquoLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(RackResource.RACK, rackId.toString());
        params.put(MachineResource.MACHINE, machine.getId().toString());

        links.add(builder.buildRestLink(RackResource.class, RackResource.RACK, params));
        links.add(builder.buildRestLink(MachineResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(DatastoresResource.class,
            DatastoresResource.DATASTORES_PATH, params));
        links.add(builder.buildRestLink(VirtualMachinesInfrastructureResource.class,
            VirtualMachinesInfrastructureResource.VIRTUAL_MACHINES_INFRASTRUCTURE_PARAM, params));
        links.add(builder.buildActionLink(MachineResource.class,
            MachineResource.MACHINE_ACTION_CHECK, MachineResource.MACHINE_CHECK, params));

        if (managedRack)
        {
            links.add(builder.buildActionLink(MachineResource.class,
                MachineResource.MACHINE_ACTION_POWER_ON_PATH,
                MachineResource.MACHINE_ACTION_POWER_ON_REL, params));
            links.add(builder.buildActionLink(MachineResource.class,
                MachineResource.MACHINE_ACTION_POWER_OFF_PATH,
                MachineResource.MACHINE_ACTION_POWER_OFF_REL, params));
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
            params));

        params.put(RemoteServiceResource.REMOTE_SERVICE, remoteService.getType().toString()
            .toLowerCase().replaceAll("_", ""));

        if (remoteService.getType().canBeChecked())
        {
            links
                .add(builder.buildRestLink(RemoteServiceResource.class,
                    RemoteServiceResource.CHECK_RESOURCE, InfrastructureService.CHECK_RESOURCE,
                    params));
        }

        links.add(builder.buildRestLink(RemoteServiceResource.class, REL_EDIT, params));
        return links;
    }

    @Override
    public List<RESTLink> buildPrivilegeLink(final PrivilegeDto privilege)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(PrivilegeResource.PRIVILEGE, privilege.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(PrivilegeResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildRoleLinks(final RoleDto role)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params =
            Collections.singletonMap(RoleResource.ROLE, role.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(RoleResource.class,
            RoleResource.ROLE_ACTION_GET_PRIVILEGES_PATH, "privileges", params));

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
        links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));
        links.add(builder.buildRestLink(RoleResource.class,
            RoleResource.ROLE_ACTION_GET_PRIVILEGES_PATH, PrivilegeResource.PRIVILEGE, params));

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

        links.add(builder.buildRestLink(EnterpriseResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(UsersResource.class, UsersResource.USERS_PATH, params));

        // apps library
        links.add(builder.buildRestLink(OVFPackageListsResource.class,
            OVFPackageListsResource.OVF_PACKAGE_LISTS_PATH, params));
        links.add(builder.buildRestLink(OVFPackagesResource.class,
            OVFPackagesResource.OVF_PACKAGES_PATH, params));

        // action get virtual machines by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALMACHINES_PATH,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params));

        // action get ips by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_IPS_PATH, IpAddressesResource.IP_ADDRESSES,
            params));

        // action get virtual datacenters by enterprise
        links.add(builder.buildRestLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALDATACENTERS_PATH,
            VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH, params));

        return links;
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
            params));

        params.put(RoleResource.ROLE, roleId.toString());

        links.add(builder.buildRestLink(RoleResource.class, RoleResource.ROLE, params));

        params.put(UserResource.USER, user.getId().toString());

        links.add(builder.buildRestLink(UserResource.class, REL_EDIT, params));

        // virtual machines
        links.add(builder.buildRestLink(UserResource.class,
            UserResource.USER_ACTION_GET_VIRTUALMACHINES_PATH,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params));

        return links;
    }

    @Override
    public List<RESTLink> buildStoragePoolLinks(final Integer datacenterId, final Integer deviceId,
        final Integer tierId, final String poolId)
    {
        return null;
    }

    @Override
    public List<RESTLink> buildOVFPackageListLinks(final Integer enterpriseId,
        final OVFPackageListDto ovfPackageList)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(OVFPackageListResource.OVF_PACKAGE_LIST, ovfPackageList.getId().toString());

        ovfPackageList.addEditLink(builder.buildRestLink(OVFPackageListResource.class, REL_EDIT,
            params));

        return links;
    }

    @Override
    public List<RESTLink> buildOVFPackageLinks(final Integer enterpriseId,
        final OVFPackageDto ovfPackage)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(OVFPackageResource.OVF_PACKAGE, ovfPackage.getId().toString());
        ovfPackage.addEditLink(builder.buildRestLink(OVFPackageResource.class, REL_EDIT, params));

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
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params));

        params.put(PrivateNetworkResource.PRIVATE_NETWORK, network.getId().toString());

        links.add(builder.buildRestLink(PrivateNetworkResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(IpAddressesResource.class,
            IpAddressesResource.IP_ADDRESSES, params));

        return links;
    }

    @Override
    public List<RESTLink> buildPublicNetworkLinks(final Integer datacenterId,
        final VLANNetwork network)
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
        links.add(builder.buildRestLink(VirtualDatacenterResource.class, REL_EDIT, params));

        links.add(builder.buildRestLink(PrivateNetworksResource.class,
            PrivateNetworksResource.PRIVATE_NETWORKS_PATH, params));
        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));
        links.add(builder.buildRestLink(VirtualAppliancesResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE, params));
        links.add(builder.buildRestLink(PrivateNetworkResource.class, "defaultnetwork", params));
        links.add(builder.buildActionLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_IPS,
            IpAddressesResource.IP_ADDRESSES, params));
        links.add(builder.buildActionLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_DHCP_INFO, "dhcpinfo", params));
        RESTLink getVlanLink =
            builder.buildActionLink(VirtualDatacenterResource.class,
                VirtualDatacenterResource.ACTION_DEFAULT_VLAN, "defaultvlan", params);
        getVlanLink.setType("GET");
        RESTLink setVlanLink =
            builder.buildActionLink(VirtualDatacenterResource.class,
                VirtualDatacenterResource.ACTION_DEFAULT_VLAN, "defaultvlan", params);
        setVlanLink.setType("PUT");
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

        links.add(builder.buildRestLink(VirtualApplianceResource.class, REL_EDIT, params));

        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        links.add(builder.buildRestLink(VirtualMachinesResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE, params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_RESUME, "resume", params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_PAUSE, "pause", params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_POWEROFF, "power off", params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_POWERON, "power on", params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_STATE, "state", params));
        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_GET_IPS,
            IpAddressesResource.IP_ADDRESSES, params));

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
        links.add(builder.buildRestLink(DatastoreResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineAdminLinks(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer enterpriseId,
        final Integer userId)
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
            links
                .add(builder.buildRestLink(MachineResource.class, MachineResource.MACHINE, params));
        }

        if (enterpriseId != null)
        {
            params = new HashMap<String, String>();
            params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
            links.add(builder.buildRestLink(EnterpriseResource.class,
                EnterpriseResource.ENTERPRISE, params));

        }

        if (userId != null)
        {
            params = new HashMap<String, String>();
            params.put(UserResource.USER, userId.toString());
            links.add(builder.buildRestLink(UserResource.class, UserResource.USER, params));

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
        links.add(builder.buildRestLink(SystemPropertyResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildIconLinks(final IconDto icon)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(IconResource.ICON, icon.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(IconResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineCloudLinks(final Integer vdcId, final Integer vappId,
        final Integer vmId, final boolean chefEnabled)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH,
            VirtualMachineNetworkConfigurationResource.CONFIGURATION, params));
        links.add(builder.buildActionLink(VirtualMachineNetworkConfigurationResource.class,
            VirtualMachineNetworkConfigurationResource.NICS_PATH,
            VirtualMachineNetworkConfigurationResource.NIC, params));
        links.add(builder.buildActionLink(VirtualMachineStorageConfigurationResource.class,
            VirtualMachineStorageConfigurationResource.DISKS_PATH,
            VirtualMachineStorageConfigurationResource.DISK, params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_GET_IPS,
            IpAddressesResource.IP_ADDRESSES, params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_POWER_ON, "poweron", params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_POWER_OFF, "poweroff", params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_RESUME, "resume", params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_PAUSE, "pause", params));
        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_STATE, "state", params));
        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_ACTION_DEPLOY, "deploy", params));
        links.add(builder.buildRestLink(VirtualMachineResource.class, "edit", params));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineCloudAdminLinks(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Integer enterpriseId, final Integer userId,
        final boolean chefEnabled)
    {

        List<RESTLink> links = new ArrayList<RESTLink>();
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.addAll(buildVirtualMachineAdminLinks(datacenterId, rackId, machineId, enterpriseId,
            userId));
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params));
        links.addAll(buildVirtualMachineCloudLinks(vdcId, vappId, vmId, chefEnabled));

        return links;
    }

    @Override
    public List<RESTLink> buildDatacenterRepositoryLinks(final Integer enterpriseId,
        final Integer dcId, final Integer repoId)
    {

        List<RESTLink> links = new ArrayList<RESTLink>();
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> paramsDc = new HashMap<String, String>();
        paramsDc.put(DatacenterResource.DATACENTER, dcId.toString());
        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            paramsDc));

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, repoId.toString());
        links.add(builder.buildRestLink(DatacenterRepositoryResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(VirtualImagesResource.class,
            VirtualImagesResource.VIRTUAL_IMAGES_PATH, params));

        return links;
    }

    protected List<RESTLink> buildVirtualImageLinks(final Integer enterpriseId, final Integer dcId,
        final VirtualImage image, final VirtualImage master, final AbiquoLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> paramsDc = new HashMap<String, String>();
        paramsDc.put(DatacenterResource.DATACENTER, dcId.toString());

        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            paramsDc));

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, dcId.toString());
        links.add(builder.buildRestLink(DatacenterRepositoryResource.class,
            DatacenterRepositoryResource.DATACENTER_REPOSITORY, params));

        params.put(CategoryResource.CATEGORY, image.getCategory().getId().toString());
        RESTLink categoryLink =
            builder.buildRestLink(CategoryResource.class, CategoryResource.CATEGORY, params);
        categoryLink.setTitle(image.getCategory().getName());
        links.add(categoryLink);

        params.put(VirtualImageResource.VIRTUAL_IMAGE, image.getId().toString());
        RESTLink imageLink = builder.buildRestLink(VirtualImageResource.class, REL_EDIT, params);
        imageLink.setTitle(image.getName());
        links.add(imageLink);

        if (image.getIcon() != null)
        {
            params.put(IconResource.ICON, image.getIcon().getId().toString());
            RESTLink iconLink =
                builder.buildRestLink(IconResource.class, IconResource.ICON, params);
            iconLink.setTitle(image.getIcon().getPath()); // TODO do not use title (altRef)
            links.add(iconLink);
        }

        // TODO: How to build a link for an imported one??

        if (master != null)
        {
            // Master's enterprise may differ from the current virtual image.
            // Datacenter repository id will be the same (the id of the datacenter)
            params.put(EnterpriseResource.ENTERPRISE, master.getEnterprise().getId().toString());
            params.put(VirtualImageResource.VIRTUAL_IMAGE, master.getId().toString());
            RESTLink masterLink =
                builder.buildRestLink(VirtualImageResource.class, "master", params);
            masterLink.setTitle(master.getName());
            links.add(masterLink);
        }

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualImageLinks(final Integer enterpriseId, final Integer dcId,
        final VirtualImage image, final VirtualImage master)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        return buildVirtualImageLinks(enterpriseId, dcId, image, master, builder);
    }

    @Override
    public RESTLink buildVirtualImageLink(final Integer enterpriseId, final Integer dcId,
        final Integer virtualImageId)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        params.put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, dcId.toString());
        params.put(VirtualImageResource.VIRTUAL_IMAGE, virtualImageId.toString());

        return builder.buildRestLink(VirtualImageResource.class,
            VirtualImageResource.VIRTUAL_IMAGE, params);
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
                    VirtualDatacenterResource.VIRTUAL_DATACENTER, params);
            vdcLink.setTitle(resource.getVirtualDatacenter().getName());
            links.add(vdcLink);
            if (resource.getVirtualAppliance() != null)
            {
                params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, resource
                    .getVirtualAppliance().getId().toString());
                RESTLink vappLink =
                    builder.buildRestLink(VirtualApplianceResource.class,
                        VirtualApplianceResource.VIRTUAL_APPLIANCE, params);
                vappLink.setTitle(resource.getVirtualAppliance().getName());
                links.add(vappLink);

                if (resource.getVirtualMachine() != null)
                {
                    params.put(VirtualMachineResource.VIRTUAL_MACHINE, resource.getVirtualMachine()
                        .getId().toString());
                    RESTLink vmLink =
                        builder.buildRestLink(VirtualMachineResource.class,
                            VirtualMachineResource.VIRTUAL_MACHINE, params);
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
            params);
    }

    @Override
    public List<RESTLink> buildIpRasdLinks(final IpPoolManagement ip)
    {
        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);

        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, ip.getVirtualDatacenter().getId()
            .toString());
        params.put(PrivateNetworkResource.PRIVATE_NETWORK, ip.getVlanNetwork().getId().toString());

        List<RESTLink> links = new ArrayList<RESTLink>();
        RESTLink link =
            builder.buildRestLink(PrivateNetworkResource.class,
                PrivateNetworkResource.PRIVATE_NETWORK, params);
        link.setTitle(ip.getVlanNetwork().getName());

        links.add(link);

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
        links.add(builder.buildRestLink(VirtualDatacenterResource.class, "parent", params));

        links.add(builder.buildActionLink(VirtualApplianceResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_STATE, REL_EDIT, params));
        return links;
    }

    @Override
    public List<RESTLink> buildCurrencyLinks(final CurrencyDto currencyDto, final Currency currency)
    {
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
    public List<RESTLink> buildPublicNetworksLinks(final Integer datacenterId)

    {
        // TODO Auto-generated method stub
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
    public List<RESTLink> buildVMNetworkConfigurationLinks(final Integer vdcId,
        final Integer vappId, final Integer vmId, final VMNetworkConfiguration config)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());
        params.put(VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH, config.getId()
            .toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links
            .add(builder.buildRestLink(VirtualMachineNetworkConfigurationResource.class,
                VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH + "/"
                    + VirtualMachineNetworkConfigurationResource.CONFIGURATION_PARAM, REL_EDIT,
                params));

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

        links.add(builder.buildRestLink(VirtualMachineResource.class, "parent", params));

        links.add(builder.buildRestLink(VirtualMachineResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE_STATE, REL_EDIT, params));
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
                + VirtualMachineNetworkConfigurationResource.NIC_PARAM, REL_EDIT, params));
        links.add(builder.buildRestLink(IpAddressesResource.class,
            IpAddressesResource.IP_ADDRESS_PARAM, IpAddressesResource.IP_ADDRESS_PARAM, params));

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
        params.put(VirtualMachineStorageConfigurationResource.DISK,
            String.valueOf(disk.getAttachmentOrder()));

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineStorageConfigurationResource.class,
            VirtualMachineStorageConfigurationResource.DISKS_PATH + "/"
                + VirtualMachineStorageConfigurationResource.DISK_PARAM, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildCategoryLinks(final CategoryDto categorydto)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(CategoryResource.CATEGORY, categorydto.getId().toString());

        AbiquoLinkBuilder builder = AbiquoLinkBuilder.createBuilder(linkProcessor);
        RESTLink editLink = builder.buildRestLink(CategoryResource.class, REL_EDIT, params);
        links.add(editLink);

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualDatacenterDiskLinks(final DiskManagement disk)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
