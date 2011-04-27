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
import com.abiquo.api.resources.appslibrary.OVFPackageListResource;
import com.abiquo.api.resources.appslibrary.OVFPackageListsResource;
import com.abiquo.api.resources.appslibrary.OVFPackageResource;
import com.abiquo.api.resources.appslibrary.OVFPackagesResource;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.PrivateNetworksResource;
import com.abiquo.api.resources.cloud.VirtualApplianceResource;
import com.abiquo.api.resources.cloud.VirtualAppliancesResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualMachineResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.resources.config.SystemPropertyResource;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.util.PagedList;

@Component
public class RESTBuilder implements IRESTBuilder
{
    protected static final String REL_EDIT = "edit";

    protected static final String FIRST = "first";

    protected static final String NEXT = "next";

    protected static final String PREV = "previous";

    protected static final String LAST = "last";

    protected LinkBuilders linkProcessor;

    @Override
    public RESTBuilder injectProcessor(final LinkBuilders linkProcessor)
    {
        this.linkProcessor = linkProcessor;
        return this;
    }

    public RESTLink buildDatacenterLink(final Integer datacenterId)
    {
        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        return buildDatacenterLink(datacenterId, builder);
    }

    protected RESTLink buildDatacenterLink(final Integer datacenterId, final RESTLinkBuilder builder)
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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(DatacenterResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(RacksResource.class, RacksResource.RACKS_PATH, params));
        links.add(builder.buildRestLink(RemoteServicesResource.class,
            RemoteServicesResource.REMOTE_SERVICES_PATH, params));
        links.add(builder.buildRestLink(DatacenterResource.class,
            DatacenterResource.HYPERVISORS_PATH, DatacenterResource.HYPERVISORS_PATH, params));

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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
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
        final MachineDto machine)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(RackResource.RACK, rackId.toString());
        params.put(MachineResource.MACHINE, machine.getId().toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(RackResource.class, RackResource.RACK, params));
        links.add(builder.buildRestLink(MachineResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(DatastoresResource.class,
            DatastoresResource.DATASTORES_PATH, params));
        links.add(builder.buildActionLink(MachineResource.class,
            MachineResource.MACHINE_ACTION_GET_VIRTUALMACHINES,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params));

        return links;
    }

    @Override
    public List<RESTLink> buildRemoteServiceLinks(final Integer datacenterId,
        final RemoteServiceDto remoteService)
    {
        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        List<RESTLink> links = buildRemoteServiceLinks(datacenterId, remoteService, builder);

        return links;
    }

    protected List<RESTLink> buildRemoteServiceLinks(final Integer datacenterId,
        final RemoteServiceDto remoteService, final RESTLinkBuilder builder)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());

        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params));

        params.put(RemoteServiceResource.REMOTE_SERVICE, remoteService.getType().toString()
            .toLowerCase());

        links.add(builder.buildRestLink(RemoteServiceResource.class, REL_EDIT, params));
        return links;
    }

    // public List<RESTLink> buildRoleLinks(RoleDto role)
    // {
    // List<RESTLink> links = new ArrayList<RESTLink>();
    //
    // Map<String, String> params =
    // Collections.singletonMap(RoleResource.ROLE, role.getId().toString());
    //
    // RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
    // links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params));
    //
    // return links;
    // }

    @Override
    public List<RESTLink> buildRoleLinks(final Integer enterpriseId, final RoleDto role)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(RoleResource.ROLE, role.getId().toString());

        links.add(builder.buildRestLink(RoleResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildEnterpriseLinks(final EnterpriseDto enterprise)
    {
        Map<String, String> params =
            Collections.singletonMap(EnterpriseResource.ENTERPRISE, enterprise.getId().toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        return buildEnterpriseLinks(builder, params);
    }

    protected List<RESTLink> buildEnterpriseLinks(final RESTLinkBuilder builder,
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
        links.add(builder.buildActionLink(EnterpriseResource.class,
            EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALMACHINES,
            VirtualMachinesResource.VIRTUAL_MACHINES_PATH, params));

        // action get ips by enterprise
        links
            .add(builder.buildActionLink(EnterpriseResource.class,
                EnterpriseResource.ENTERPRISE_ACTION_GET_IPS, IpAddressesResource.IP_ADDRESSES,
                params));

        return links;
    }

    @Override
    public List<RESTLink> buildUserLinks(final Integer enterpriseId, final Integer roleId,
        final UserDto user)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(RoleResource.ROLE, roleId.toString());

        links.add(builder.buildRestLink(RoleResource.class, RoleResource.ROLE, params));

        params.put(UserResource.USER, user.getId().toString());

        links.add(builder.buildRestLink(UserResource.class, REL_EDIT, params));

        // virtual machines
        links.add(builder.buildActionLink(UserResource.class,
            UserResource.USER_ACTION_GET_VIRTUALMACHINES,
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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(OVFPackageListResource.OVF_PACKAGE_LIST, ovfPackageList.getId().toString());

        links.add(builder.buildRestLink(OVFPackageListResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildOVFPackageLinks(final Integer enterpriseId,
        final OVFPackageDto ovfPackage)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params.put(OVFPackageResource.OVF_PACKAGE, ovfPackage.getId().toString());

        links.add(builder.buildRestLink(OVFPackageResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildPrivateNetworkLinks(final Integer virtualDatacenterId,
        final VLANNetworkDto network)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params));

        params.put(PrivateNetworkResource.PRIVATE_NETWORK, network.getId().toString());

        links.add(builder.buildRestLink(PrivateNetworkResource.class, REL_EDIT, params));
        links.add(builder.buildRestLink(IpAddressesResource.class,
            IpAddressesResource.IP_ADDRESSES, params));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualDatacenterLinks(final VirtualDatacenterDto vdc,
        final Integer datacenterId, final Integer enterpriseId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();

        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdc.getId().toString());
        params.put(DatacenterResource.DATACENTER, datacenterId.toString());
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualDatacenterResource.class, REL_EDIT, params));

        links.add(builder.buildRestLink(PrivateNetworksResource.class,
            PrivateNetworksResource.PRIVATE_NETWORKS_PATH, params));
        links.add(builder.buildRestLink(DatacenterResource.class, DatacenterResource.DATACENTER,
            params));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));
        links.add(builder.buildRestLink(VirtualAppliancesResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE, params));
        links.add(builder.buildActionLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_IPS,
            IpAddressesResource.IP_ADDRESSES, params));

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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);

        links.add(builder.buildRestLink(VirtualApplianceResource.class, REL_EDIT, params));

        links.add(builder.buildRestLink(VirtualDatacenterResource.class,
            VirtualDatacenterResource.VIRTUAL_DATACENTER, params));
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        links.add(builder.buildRestLink(VirtualMachinesResource.class,
            VirtualMachineResource.VIRTUAL_MACHINE, params));

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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
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

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        // Only build the machine hypervisor link if the hypervisor is deployed
        if (datacenterId != null && rackId != null && machineId != null)
        {
            params.put(DatacenterResource.DATACENTER, datacenterId.toString());
            params.put(RackResource.RACK, rackId.toString());
            params.put(MachineResource.MACHINE, machineId.toString());
            links
                .add(builder.buildRestLink(MachineResource.class, MachineResource.MACHINE, params));
        }

        params = new HashMap<String, String>();
        params.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        links.add(builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params));

        params = new HashMap<String, String>();
        params.put(UserResource.USER, userId.toString());
        links.add(builder.buildRestLink(UserResource.class, UserResource.USER, params));

        return links;
    }

    @Override
    public List<RESTLink> buildSystemPropertyLinks(final SystemPropertyDto systemProperty)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        Map<String, String> params = new HashMap<String, String>();
        params.put(SystemPropertyResource.SYSTEM_PROPERTY, systemProperty.getId().toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(SystemPropertyResource.class, REL_EDIT, params));

        return links;
    }

    @Override
    public List<RESTLink> buildIPAddressLink(final Integer vlanId, final IpPoolManagementDto ip)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(PrivateNetworkResource.PRIVATE_NETWORK_PARAM, vlanId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(PrivateNetworkResource.class,
            PrivateNetworkResource.PRIVATE_NETWORK, params));

        return links;
    }

    @Override
    public List<RESTLink> buildVirtualMachineCloudLinks(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Map<String, String> params = new HashMap<String, String>();
        params.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        params.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        params.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        links.add(builder.buildRestLink(VirtualMachineResource.class, REL_EDIT, params));
        links.add(builder.buildActionLink(VirtualMachineResource.class,
            VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_GET_IPS,
            IpAddressesResource.IP_ADDRESSES, params));

        return links;
    }

    @Override
    public List<RESTLink> buildPaggingLinks(final String absolutePath, final PagedList list)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();
        Integer lastPage = list.getTotalResults() / list.getPageSize();
        links.add(new RESTLink(FIRST, absolutePath));
        if (list.getCurrentPage() != 0 && lastPage != 0)
        {
            links.add(new RESTLink(PREV, absolutePath + "?" + AbstractResource.PAGE + "="
                + (list.getCurrentPage() - 1)));
        }
        if (list.getCurrentPage() != lastPage && lastPage != 0)
        {
            links.add(new RESTLink(NEXT, absolutePath + "?" + AbstractResource.PAGE + "="
                + (list.getCurrentPage() + 1)));
        }
        links.add(new RESTLink(LAST, absolutePath + "?" + AbstractResource.PAGE + "=" + lastPage));
        return links;
    }

    @Override
    public RESTLink buildEnterpriseLink(final Integer enterpriseId)
    {
        RESTLinkBuilder builder = RESTLinkBuilder.createBuilder(linkProcessor);
        return buildEnterpriseLink(enterpriseId, builder);
    }

    protected RESTLink buildEnterpriseLink(final Integer enterpriseId, final RESTLinkBuilder builder)
    {
        Map<String, String> params =
            Collections.singletonMap(EnterpriseResource.ENTERPRISE, enterpriseId.toString());
        return builder.buildRestLink(EnterpriseResource.class, EnterpriseResource.ENTERPRISE,
            params);
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
}
