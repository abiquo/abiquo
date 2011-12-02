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

package com.abiquo.api.common;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatacentersResource;
import com.abiquo.api.resources.DatastoreResource;
import com.abiquo.api.resources.DatastoresResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.LoginResource;
import com.abiquo.api.resources.MachineResource;
import com.abiquo.api.resources.MachinesResource;
import com.abiquo.api.resources.RackResource;
import com.abiquo.api.resources.RacksResource;
import com.abiquo.api.resources.RemoteServiceResource;
import com.abiquo.api.resources.RemoteServicesResource;
import com.abiquo.api.resources.RoleResource;
import com.abiquo.api.resources.RolesResource;
import com.abiquo.api.resources.UserResource;
import com.abiquo.api.resources.UsersResource;
import com.abiquo.api.resources.VirtualMachinesInfrastructureResource;
import com.abiquo.api.resources.appslibrary.CategoriesResource;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.DiskFormatTypesResource;
import com.abiquo.api.resources.appslibrary.HypervisorTypesResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.resources.appslibrary.IconsResource;
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
import com.abiquo.api.resources.config.PrivilegesResource;
import com.abiquo.api.resources.config.SystemPropertiesResource;
import com.abiquo.api.resources.config.SystemPropertyResource;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.enumerator.RemoteServiceType;

public class UriTestResolver
{
    public static final String API_URI = "http://localhost:9009/api";

    public static String resolveURI(final String pathTemplate, final Map<String, String> values)
    {
        return URIResolver.resolveURI(API_URI, pathTemplate, values);
    }

    public static String resolveURI(final String pathTemplate, final Map<String, String> values,
        final Map<String, String[]> queryParams)
    {
        return URIResolver.resolveURI(API_URI, pathTemplate, values, queryParams);
    }

    public static String resolveEnterprisesURI()
    {
        String uri =
            resolveURI(EnterprisesResource.ENTERPRISES_PATH, new HashMap<String, String>());
        return uri;
    }

    public static String resolveEnterpriseURI(final Integer enterpriseId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH + "/",
                EnterpriseResource.ENTERPRISE_PARAM);

        return resolveURI(template, Collections.singletonMap(EnterpriseResource.ENTERPRISE,
            enterpriseId.toString()));
    }

    public static String resolveEnterpriseActionGetIPsURI(final Integer entId)
    {
        return resolveEnterpriseURI(entId) + "/"
            + EnterpriseResource.ENTERPRISE_ACTION_GET_IPS_PATH;
    }

    public static String resolveEnterpriseActionGetVirtualMachinesURI(final Integer entId)
    {
        return resolveEnterpriseURI(entId) + "/"
            + EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALMACHINES_PATH;
    }

    public static String resolveEnterprisesByDatacenterURI(final Integer datacenterId)
    {
        return resolveDatacenterURI(datacenterId) + "/" + DatacenterResource.ENTERPRISES_PATH;
    }

    public static String resolveRolesURI()
    {
        String uri = resolveURI(RolesResource.ROLES_PATH, new HashMap<String, String>());
        return uri;
    }

    public static String resolvePrivilegeURI(final Integer privilegeId)
    {
        String template =
            buildPath(PrivilegesResource.PRIVILEGES_PATH, PrivilegeResource.PRIVILEGE_PARAM);

        return resolveURI(template, Collections.singletonMap(PrivilegeResource.PRIVILEGE,
            privilegeId.toString()));
    }

    public static String resolveRoleURI(final Integer roleId)
    {
        String template = buildPath(RolesResource.ROLES_PATH, RoleResource.ROLE_PARAM);

        return resolveURI(template, Collections.singletonMap(RoleResource.ROLE, roleId.toString()));
    }

    public static String resolveRoleActionGetPrivilegesURI(final Integer entId)
    {
        return resolveRoleURI(entId) + "/" + RoleResource.ROLE_ACTION_GET_PRIVILEGES_PATH;
    }

    public static String resolveUsersURI(final Serializable enterpriseId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                UsersResource.USERS_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, enterpriseId.toString());

        return resolveURI(template, values);
    }

    public static String resolveUserURI(final Integer enterpriseId, final Integer userId)
    {
        return resolveUserURI(enterpriseId.toString(), userId);
    }

    public static String resolveUserURI(final String enterpriseWildcardOrId, final Integer userId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                UsersResource.USERS_PATH, UserResource.USER_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, enterpriseWildcardOrId);
        values.put(UserResource.USER, userId.toString());

        return resolveURI(template, values);
    }

    public static String resolveUserActionGetVirtualMachinesURI(final Integer enterpriseId,
        final Integer userId)
    {
        return resolveUserURI(enterpriseId, userId) + "/"
            + UserResource.USER_ACTION_GET_VIRTUALMACHINES_PATH;
    }

    public static String resolveDatacentersURI()
    {
        String uri =
            resolveURI(DatacentersResource.DATACENTERS_PATH, new HashMap<String, String>());
        return uri;
    }

    public static String resolveDatacenterURI(final Integer datacenterId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM);
        return resolveURI(template, Collections.singletonMap(DatacenterResource.DATACENTER,
            datacenterId.toString()));
    }

    public static String resolveRacksURI(final Integer datacenterId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH);
        return resolveURI(template, Collections.singletonMap(DatacenterResource.DATACENTER,
            datacenterId.toString()));
    }

    public static String resolveRackURI(final Integer datacenterId, final Integer rackId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH, RackResource.RACK_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RackResource.RACK, rackId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/admin/datacenters/{$datacenterId}/racks/{$rackId}/machines
     * 
     * @param datacenterId identifier of the datacenter to resolve the URI
     * @param rackId identifier of the rack to resolve the URI
     * @return the resolved URI
     */
    public static String resolveMachinesURI(final Integer datacenterId, final Integer rackId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH, RackResource.RACK_PARAM, MachinesResource.MACHINES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RackResource.RACK, rackId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/admin/datacenters/{$datacenterId}/racks/{$rackId}/machines/{$machineId}
     * 
     * @param datacenterId identifier of the datacenter to resolve the URI
     * @param rackId identifier of the rack to resolve the URI
     * @param machineId identifier of the machine to resolve the URI.
     * @return the resolved URI
     */
    public static String resolveMachineURI(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH, RackResource.RACK_PARAM, MachinesResource.MACHINES_PATH,
                MachineResource.MACHINE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RackResource.RACK, rackId.toString());
        values.put(MachineResource.MACHINE, machineId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/admin/datacenters/{$datacenterId}/racks/{$rackId}/machines
     * /{$machineId}/datastores
     * 
     * @param datacenterId identifier of the datacenter to resolve the URI
     * @param rackId identifier of the rack to resolve the URI
     * @param machineId identifier of the machine to resolve the URI.
     * @return the resolved URI
     */
    public static String resolveDatastoresURI(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH, RackResource.RACK_PARAM, MachinesResource.MACHINES_PATH,
                MachineResource.MACHINE_PARAM, DatastoresResource.DATASTORES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RackResource.RACK, rackId.toString());
        values.put(MachineResource.MACHINE, machineId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/admin/datacenters/{$datacenterId}/racks/{$rackId}/machines
     * /{$machineId}/datastores/{$datastoreId}
     * 
     * @param datacenterId identifier of the datacenter to resolve the URI
     * @param rackId identifier of the rack to resolve the URI
     * @param machineId identifier of the machine to resolve the URI.
     * @param datastoreId identifier of the datastore to resolve the URI.
     * @return the resolved URI
     */
    public static String resolveDatastoreURI(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Integer datastoreId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RacksResource.RACKS_PATH, RackResource.RACK_PARAM, MachinesResource.MACHINES_PATH,
                MachineResource.MACHINE_PARAM, DatastoresResource.DATASTORES_PATH,
                DatastoreResource.DATASTORE_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RackResource.RACK, rackId.toString());
        values.put(MachineResource.MACHINE, machineId.toString());
        values.put(DatastoreResource.DATASTORE, datastoreId.toString());

        return resolveURI(template, values);
    }

    public static String resolveDatacenterURIActionDiscoverHypervidor(final Integer datacenterId,
        final String ip)
    {
        return resolveDatacenterURI(datacenterId) + "/"
            + DatacenterResource.ACTION_DISCOVER_HYPERVISOR_TYPE + "?ip=" + ip;
    }

    public static String resolveDatacenterURIActionDiscover(final Integer datacenterId)
    {
        return resolveDatacenterURI(datacenterId) + "/" + DatacenterResource.ACTION_DISCOVER_SINGLE;
    }

    public static String resolveDatacenterURIActionDiscoverMultiple(final Integer datacenterId)
    {
        return resolveDatacenterURI(datacenterId) + "/"
            + DatacenterResource.ACTION_DISCOVER_MULTIPLE;
    }

    /**
     * Creates something like
     * http://example.com/admin/datacenters/{$datacenterId}/racks/{$rackId}/machines
     * /{$machineId}/action/virtualmachines
     * 
     * @param datacenterId identifier of the datacenter to resolve the URI
     * @param rackId identifier of the rack to resolve the URI
     * @param machineId identifier of the machine to resolve the URI.
     * @return the resolved URI
     */
    public static String resolveMachineActionGetVirtualMachinesURI(final Integer datacenterId,
        final Integer rackId, final Integer machineId)
    {
        return UriHelper.appendPathToBaseUri(resolveMachineURI(datacenterId, rackId, machineId),
            VirtualMachinesInfrastructureResource.VIRTUAL_MACHINES_INFRASTRUCTURE_PARAM);
    }

    public static String resolveRemoteServicesURI(final Integer datacenterId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RemoteServicesResource.REMOTE_SERVICES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());

        return resolveURI(template, values);
    }

    public static String resolveRemoteServiceURI(final Integer datacenterId,
        final RemoteServiceType type)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                RemoteServicesResource.REMOTE_SERVICES_PATH,
                RemoteServiceResource.REMOTE_SERVICE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());
        values.put(RemoteServiceResource.REMOTE_SERVICE, type.toString().toLowerCase().replace("_",
            ""));

        return resolveURI(template, values);
    }

    public static String resolveOVFPackageListsURI(final Integer enterpriseId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                OVFPackageListsResource.OVF_PACKAGE_LISTS_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        return resolveURI(template, values);

    }

    public static String resolveOVFPackageListURI(final Integer enterpriseId,
        final Integer ovfPackageListId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                OVFPackageListsResource.OVF_PACKAGE_LISTS_PATH,
                OVFPackageListResource.OVF_PACKAGE_LIST_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values.put(OVFPackageListResource.OVF_PACKAGE_LIST, ovfPackageListId.toString());

        return resolveURI(template, values);

    }

    public static String resolveOVFPackagesURI(final Integer enterpriseId)
    {

        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                OVFPackagesResource.OVF_PACKAGES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));

        return resolveURI(template, values);
    }

    // public static String resolveOVFPackageInstancesURI(final Integer datacenterId,
    // final String remoteServiceType, final Integer enterpriseId)
    // {
    //
    // String template =
    // buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
    // RemoteServicesResource.REMOTE_SERVICES_PATH,
    // RemoteServiceResource.REMOTE_SERVICE_PARAM,
    // EnterpriseRepositoriesResource.ENTERPRISE_REP_PATH,
    // EnterpriseRepositoryResource.ENTERPRISE_PARAM,
    // OVFPackageInstancesResource.OVF_PACKAGE_INSTANCES_PATH);
    //
    // Map<String, String> values = new HashMap<String, String>();
    // values.put(DatacenterResource.DATACENTER, String.valueOf(datacenterId));
    // values.put(RemoteServiceResource.REMOTE_SERVICE, remoteServiceType);
    // values.put(EnterpriseRepositoryResource.ENTERPRISE, String.valueOf(enterpriseId));
    //
    // return resolveURI(template, values);
    // }
    //
    // public static String resolveOVFPackageInstanceURI(final Integer datacenterId,
    // final String remoteServiceType, final Integer enterpriseId, final String ovfUrl)
    // {
    //
    // String template =
    // buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
    // RemoteServicesResource.REMOTE_SERVICES_PATH,
    // RemoteServiceResource.REMOTE_SERVICE_PARAM,
    // EnterpriseRepositoriesResource.ENTERPRISE_REP_PATH,
    // EnterpriseRepositoryResource.ENTERPRISE_PARAM,
    // OVFPackageInstancesResource.OVF_PACKAGE_INSTANCES_PATH,
    // OVFPackageInstanceResource.OVF_PACKAGE_INSTANCE_PARAM);
    //
    // Map<String, String> values = new HashMap<String, String>();
    // values.put(DatacenterResource.DATACENTER, String.valueOf(datacenterId));
    // values.put(RemoteServiceResource.REMOTE_SERVICE, remoteServiceType);
    // values.put(EnterpriseRepositoryResource.ENTERPRISE, String.valueOf(enterpriseId));
    // values.put(OVFPackageInstanceResource.OVF_PACKAGE_INSTANCE, ovfUrl);
    //
    // return resolveURI(template, values);
    // }

    public static String resolveOVFPackageURI(final Integer enterpriseId, final Integer ovfPackageId)
    {

        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                OVFPackagesResource.OVF_PACKAGES_PATH, OVFPackageResource.OVF_PACKAGE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values.put(OVFPackageResource.OVF_PACKAGE, ovfPackageId.toString());

        return resolveURI(template, values);
    }

    public static String resolveURI(final Integer datacenterId)
    {
        String template =
            DatacentersResource.DATACENTERS_PATH + "/" + DatacenterResource.DATACENTER_PARAM + "/"
                + RacksResource.RACKS_PATH;
        return resolveURI(template, Collections.singletonMap(DatacenterResource.DATACENTER,
            datacenterId.toString()));
    }

    public static String resolvePrivateNetworksURI(final Integer virtualDatacenterId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                PrivateNetworksResource.PRIVATE_NETWORKS_PATH);

        return resolveURI(template, Collections.singletonMap(
            VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString()));
    }

    public static String resolvePrivateNetworkURI(final Integer virtualDatacenterId,
        final Integer networkId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                PrivateNetworkResource.PRIVATE_NETWORK_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());
        values.put(PrivateNetworkResource.PRIVATE_NETWORK, networkId.toString());

        return resolveURI(template, values);
    }

    public static String resolvePrivateNetworkIPsURI(final Integer virtualDatacenterId,
        final Integer vlanId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                PrivateNetworkResource.PRIVATE_NETWORK_PARAM, IpAddressesResource.IP_ADDRESSES);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());
        values.put(PrivateNetworkResource.PRIVATE_NETWORK, vlanId.toString());

        return resolveURI(template, values);

    }

    /**
     * Creates something like http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/
     * 
     * @param vdcId identifier of the virtual datacenter
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualAppliancesURI(final Integer virtualDatacenterId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{vappId}/
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualApplianceURI(final Integer vdcId, final Integer vappId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{vappId}/action/ips
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualApplianceActionGetIPsURI(final Integer vdcId,
        final Integer vappId)
    {
        return resolveVirtualApplianceURI(vdcId, vappId)
            + VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_GET_IPS;
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachinesURI(final Integer vdcId, final Integer vappId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/{vmId}/
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachineURI(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        values.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/{vmId}/state
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @param vmId identifier of the virtual machine
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachineStateURI(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
                VirtualMachineResource.VIRTUAL_MACHINE_STATE);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        values.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/{vmId}/storage/disks
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @param vmId identifier of the virtual machine
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachineDisksUri(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
                VirtualMachineStorageConfigurationResource.STORAGE,
                VirtualMachineStorageConfigurationResource.DISKS_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        values.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/{vmId}/storage/disks/{diskOrder}
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @param vmId identifier of the virtual machine
     * @param diskOrder identifier of the disk inside the virtual machine.
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachineDiskUri(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskOrder)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
                VirtualMachineStorageConfigurationResource.STORAGE,
                VirtualMachineStorageConfigurationResource.DISKS_PATH,
                VirtualMachineStorageConfigurationResource.DISK_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vdcId.toString());
        values.put(VirtualApplianceResource.VIRTUAL_APPLIANCE, vappId.toString());
        values.put(VirtualMachineResource.VIRTUAL_MACHINE, vmId.toString());
        values.put(VirtualMachineStorageConfigurationResource.DISK, diskOrder.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/cloud/virtualdatacenters/{vdcId}/virtualappliances/{
     * vappId}/virtualmachines/{vmId}/action/ips
     * 
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual apliance
     * @return URI of the virtual appliance resource into string object
     */
    public static String resolveVirtualMachineActionGetIPsURI(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        return resolveVirtualMachineURI(vdcId, vappId, vmId) + "/"
            + VirtualMachineNetworkConfigurationResource.NETWORK + "/"
            + VirtualMachineNetworkConfigurationResource.NICS_PATH;
    }

    public static String resolveVirtualDatacenterURI(final Integer virtualDatacenterId)
    {
        String template =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, virtualDatacenterId.toString());

        return resolveURI(template, values);
    }

    public static String resolveVirtualDatacenterActionGetIPsURI(final Integer virtualDatacenterId)
    {
        return resolveVirtualDatacenterURI(virtualDatacenterId)
            + VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_IPS;
    }

    public static String resolveVirtualDatacenterActionGetDHCPInfoURI(
        final Integer virtualDatacenterId)
    {
        return resolveVirtualDatacenterURI(virtualDatacenterId)
            + VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_DHCP_INFO;
    }

    public static String resolveVirtualDatacentersURI()
    {
        String uri =
            resolveURI(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                new HashMap<String, String>());
        return uri;
    }

    public static String resolveHypervisorTypesURI(final Integer datacenterId)
    {
        String template =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM,
                DatacenterResource.HYPERVISORS_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(DatacenterResource.DATACENTER, datacenterId.toString());

        return resolveURI(template, values);
    }

    /**
     * Creates something like
     * http://example.com/admin/enterprises/{enterpriseId}/datacenterrepositories/{datacenterId}/
     */
    public static String resolveDatacenterRepositoryURI(final Integer enterpriseId,
        final Integer datacenterId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values
            .put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, String.valueOf(datacenterId));

        return resolveURI(template, values);
    }

    public static String resolveVirtualImagesURI(final Integer enterpriseId,
        final Integer datacenterId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM, //
                VirtualImagesResource.VIRTUAL_IMAGES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values
            .put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, String.valueOf(datacenterId));

        return resolveURI(template, values);
    }

    public static String resolveStatefulVirtualImagesURI(final Integer enterpriseId,
        final Integer datacenterId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM, //
                VirtualImagesResource.VIRTUAL_IMAGES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values
            .put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, String.valueOf(datacenterId));

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put(VirtualImagesResource.VIRTUAL_IMAGE_GET_STATEFUL_QUERY_PARAM,
            new String[] {"true"});

        return resolveURI(template, values, queryParams);
    }

    public static String resolveStatefulVirtualImagesURIWithCategory(final Integer enterpriseId,
        final Integer datacenterId, final String categoryName)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM, //
                VirtualImagesResource.VIRTUAL_IMAGES_PATH);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values
            .put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, String.valueOf(datacenterId));

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put(VirtualImagesResource.VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM,
            new String[] {categoryName});
        queryParams.put(VirtualImagesResource.VIRTUAL_IMAGE_GET_STATEFUL_QUERY_PARAM,
            new String[] {"true"});

        return resolveURI(template, values, queryParams);
    }

    public static String resolveVirtualImageURI(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualImageId)
    {
        String template =
            buildPath(EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM, //
                VirtualImagesResource.VIRTUAL_IMAGES_PATH, VirtualImageResource.VIRTUAL_IMAGE_PARAM);

        Map<String, String> values = new HashMap<String, String>();
        values.put(EnterpriseResource.ENTERPRISE, String.valueOf(enterpriseId));
        values
            .put(DatacenterRepositoryResource.DATACENTER_REPOSITORY, String.valueOf(datacenterId));
        values.put(VirtualImageResource.VIRTUAL_IMAGE, String.valueOf(virtualImageId));

        return resolveURI(template, values);
    }

    public static String resolveSystemPropertiesURI()
    {
        String uri =
            resolveURI(SystemPropertiesResource.SYSTEM_PROPERTIES_PATH,
                new HashMap<String, String>());
        return uri;
    }

    public static String resolveSystemPropertiesURIByName(final String name)
    {
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put(SystemPropertiesResource.NAME_QUERY_PARAM, new String[] {name});

        String uri =
            resolveURI(SystemPropertiesResource.SYSTEM_PROPERTIES_PATH,
                new HashMap<String, String>(), queryParams);
        return uri;
    }

    public static String resolveSystemPropertiesURIByComponent(final String component)
    {
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put(SystemPropertiesResource.COMPONENT_QUERY_PARAM, new String[] {component});

        String uri =
            resolveURI(SystemPropertiesResource.SYSTEM_PROPERTIES_PATH,
                new HashMap<String, String>(), queryParams);
        return uri;
    }

    public static String resolveSystemPropertyURI(final Integer propertyId)
    {
        String template =
            buildPath(SystemPropertiesResource.SYSTEM_PROPERTIES_PATH,
                SystemPropertyResource.SYSTEM_PROPERTY_PARAM);
        return resolveURI(template, Collections.singletonMap(
            SystemPropertyResource.SYSTEM_PROPERTY, propertyId.toString()));
    }

    public static String resolveLoginURI()
    {
        return resolveURI(LoginResource.LOGIN_PATH, new HashMap<String, String>());
    }

    /**
     * Creates something like http://example.com/config/categories/${categoryId}
     * 
     * @param categoryId identifier of the category
     * @return the an URI-like string to call the 'ips' action.
     */
    public static String resolveCategoryURI(final Integer categoryId)
    {
        String template =
            buildPath(CategoriesResource.CATEGORIES_PATH, CategoryResource.CATEGORY_PARAM);

        return resolveURI(template, Collections.singletonMap(CategoryResource.CATEGORY, categoryId
            .toString()));
    }

    public static String resolveCategoriesURI()
    {
        String uri = resolveURI(CategoriesResource.CATEGORIES_PATH, new HashMap<String, String>());
        return uri;
    }

    public static String resolveDiskFormatTypesURI()
    {
        String uri =
            resolveURI(DiskFormatTypesResource.DISK_FORMAT_TYPES_PATH,
                new HashMap<String, String>());
        return uri;
    }

    public static String resolveHypervisorTypesURI()
    {
        String uri =
            resolveURI(HypervisorTypesResource.HYPERVISOR_TYPES_PATH, new HashMap<String, String>());
        return uri;
    }

    public static String resolveIconURI(final Integer iconId)
    {
        String template = buildPath(IconsResource.ICONS_PATH, IconResource.ICON_PARAM);
        return resolveURI(template, Collections.singletonMap(IconResource.ICON, iconId.toString()));
    }

    public static String resolveIconsURI()
    {
        return resolveURI(IconsResource.ICONS_PATH, new HashMap<String, String>());
    }

}
