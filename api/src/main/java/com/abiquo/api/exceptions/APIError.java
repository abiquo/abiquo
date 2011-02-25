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

package com.abiquo.api.exceptions;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Contains all the errors notified by the API.
 * 
 * @author eruiz
 */
public enum APIError
{
    // GENERIC
    MALFORMED_URI("GEN-0", "Malformed URI"), INVALID_ID("GEN-1", "Identifier can't be 0"), CONSTRAINT_VIOLATION(
        "GEN-2", "Invalid document, please make sure all the mandatory fields are right"), UNMARSHAL_EXCEPTION(
        "GEN-3", "Invalid xml document"),
    // INVALID_IP("GEN-4", "Invalid IP"),
    INVALID_PRIVATE_NETWORK_TYPE("GEN-5", "Invalid private network type"), INTERNAL_SERVER_ERROR(
        "GEN-6", "Unexpected error"),

    // DATACENTER
    NON_EXISTENT_DATACENTER("DC-0", "The requested datacenter does not exist"), DATACENTER_DUPLICATED_NAME(
        "DC-3", "There is already a datacenter with that name"), DATACENTER_NOT_ALLOWD("DC-4",
        "The current enterprise can't use this datacenter"),

    // ENTERPRISE
    NON_EXISTENT_ENTERPRISE("EN-0", "The requested enterprise does not exist"), ENTERPRISE_DUPLICATED_NAME(
        "ENTERPRISE-4", "Duplicated name for an enterprise"), ENTERPRISE_DELETE_ERROR_WITH_VDCS(
        "ENTERPRISE-5", "Cannot delete enterprise with associated virtual datacenters"), ENTERPRISE_DELETE_OWN_ENTERPRISE(
        "ENTERPRISE-6", "Cannot delete the current user enterprise"),

    // LIMITS: Common for Enterprise and virtual datacenter
    LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC("LIMIT-6",
        "Invalid vlan hard limit, it cannot be bigger than the number of vlans per virtual datacenter"), LIMITS_DUPLICATED(
        "LIMIT-7", "Duplicated limits by enterprise and datacenter"), LIMITS_NOT_EXIST("LIMIT-8",
        "Limits by enterprise and datacenter don't exist"),

    // VIRTUAL DATACENTER
    NON_EXISTENT_VIRTUAL_DATACENTER("VDC-0", "The requested virtual datacenter does not exist"), VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE(
        "VDC-1", "Invalid hypervisor type for this datacenter"), VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES(
        "VDC-2",
        "This datacenter contains virtual appliances and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_CONTAINS_RESOURCES(
        "VDC-3",
        "This datacenter has volumes attached and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_INVALID_NETWORKS(
        "VDC-4", "This datacenter has networks without IPs!"),

    // VIRTUAL APPLIANCE
    NON_EXISTENT_VIRTUALAPPLIANCE("VAPP-0", "The requested virtual appliance does not exist"),

    // RACK
    NOT_ASSIGNED_RACK_DATACENTER("RACK-0", "The rack is not assigned to the datacenter"), RACK_DUPLICATED_NAME(
        "RACK-3", "There is already a rack with that name in this datacenter"),

    // MACHINE
    NON_EXISTENT_MACHINE("MACHINE-0", "The requested machine does not exist"), NOT_ASSIGNED_MACHINE_DATACENTER_RACK(
        "MACHINE-1", "The machine is not assigned to the datacenter or rack"),

    HYPERVISOR_EXIST_IP("HYPERVISOR-1",
        "Invalid hypervisor IP. Already exist an hypervisor with that IP"), HYPERVISOR_EXIST_SERVICE_IP(
        "HYPERVISOR-2",
        "Invalid hypervisor service IP. Already exist an hypervisor with that service IP"),

    // NETWORK
    NOT_ASSIGNED_NETWORK_VIRTUAL_DATACENTER("NETWORK-0",
        "The private network is not assigned to the datacenter"), NETWORK_INVALID_CONFIGURATION(
        "NET-0", "Invalid network configuration for the virtual datacenter"), NETWORK_GATEWAY_OUT_OF_RANGE(
        "NET-6", "Gateway address out of range. It must be into the ip range address"), NON_EXISTENT_VIRTUAL_NETWORK(
        "NET-7", "The requested virtual network does not exist"), NETWORK_WITHOUT_IPS("NET-8",
        "This network doesn't have IPs"),

    // VIRTUAL MACHINE
    VIRTUAL_MACHINE_WITHOUT_HYPERVISOR("VM-0", "The virtual machine not have a hypervisor assigned"), NON_EXISTENT_VIRTUALMACHINE(
        "VM-1", "The requested virtual machine does not exist"),

    // ROLE
    NON_EXISTENT_ROLE("ROLE-0", "The requested role does not exist"),

    // USER
    NOT_ASSIGNED_USER_ENTERPRISE("USER-0", "The user is not assigned to the enterprise"), MISSING_ROLE_LINK(
        "USER-1", "Missing link to the role"), ROLE_PARAM_NOT_FOUND("USER-2",
        "Missing roles parameter"), USER_NON_EXISTENT("USER-3", "The requested user does not exist"), USER_DUPLICATED_NICK(
        "USER-4", "Duplicated nick for the user"),

    // REMOTE SERVICE
    NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER("RS-0",
        "The remote service is not assigned to the datacenter"), WRONG_REMOTE_SERVICE_TYPE("RS-1",
        "Wrong remote service"), NON_EXISTENT_REMOTE_SERVICE_TYPE("RS-2",
        "The remote service does not exist"), REMOTE_SERVICE_URL_ALREADY_EXISTS("RS-3",
        "The remote service's url already exist and can't be duplicated"), REMOTE_SERVICE_MALFORMED_URL(
        "RS-4", "The remote service's url is not well formed"), REMOTE_SERVICE_POOL_ASIGNED("RS-5",
        "This datacenter already has a storage pool asigned"), REMOTE_SERVICE_TYPE_EXISTS("RS-6",
        "This datacenter already has a remote service of that type"), REMOTE_SERVICE_CONNECTION_FAILED(
        "RS-7", "Connection failed with the remote service"), APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED(
        "AM-0",
        "The repository exported by the current appliance manager is being used on other datacenter"), APPLIANCE_MANAGER_REPOSITORY_IN_USE(
        "AM-1",
        "The current repository holds virtual images being used on some virtual appliance, appliance manager only can be modified if the same repository is used."), REMOTE_SERVICE_STORAGE_REMOTE_WITH_POOLS(
        "RS-8", "Cannot delete a Storage Manager with associated Storage Pools"), REMOTE_SERVICE_IS_BEING_USED(
        "RS-9",
        "Cannot delete a Virtual System Monitor or DHCP Service. There are virtual machines deployed."), REMOTE_SERVICE_WRONG_URL(
        "RS-10", "Provided URL is not valid"),

    // OVF PACKAGE LIST
    OVF_PACKAGE_LIST_NAME_ALREADY_EXIST("OVF-PACKAGE-LIST-0", "OVF Package list name already exist"),

    // OVF PACKAGE
    NON_EXISTENT_OVF_PACKAGE("OVF-PACKAGE-0", "The requested OVF package does not exist"), NON_EXISTENT_OVF_PACKAGE_LIST(
        "OVF-PACKAGE-1", "The requested OVF package list does not exist"),

    // NODE COLLECTOR
    NON_EXISTENT_IP("NC-0", "The requested IP does not exist"), MISSING_IP_PARAMETER("NC-1",
        "Missing query parameter ip"),

    // STORAGE POOL
    MISSING_REQUIRED_QUERY_PARAMETER_IQN("SP-1", "Missing query parameter iqn"), CONFLICT_STORAGE_POOL(
        "SP-2", "The id of the Storage Pool and the id of the submitted object must be the same"), NON_EXISTENT_STORAGE_POOL(
        "SP-3", "The requested Storage Pool does not exist"), STORAGE_POOL_ERROR_MODIFYING("SP-4",
        "There was an unexpected error while modifying the Storage Pool"), STORAGE_POOLS_SYNC(
        "SP-5", "Could not get the Storage Pools from the target device"), STORAGE_POOL_SYNC(
        "SP-6", "Could not get the requested Storage Pool from the target device"),

    // DATASTORE
    DATASTORE_NON_EXISTENT("DATASTORE-0", "The requested datastore does not exist"), DATASTORE_DUPLICATED_NAME(
        "DATASTORE-4", "Duplicated name for the datastore"), DATASTORE_DUPLICATED_DIRECTORY(
        "DATASTORE-6", "Duplicated directory path for the datastore"), DATASTORE_NOT_ASSIGNED_TO_MACHINE(
        "DATASTORE-7", "The datastore is not assigned to that machine"),

    // SYSTEM PROPERTIES
    NON_EXISTENT_SYSTEM_PROPERTY("SYSPROP-0", "The requested system property does not exist"), SYSTEM_PROPERTIES_DUPLICATED_NAME(
        "SYSPROP-1", "There is already a system property with that name"),

    // ALLOCATOR
    LIMIT_EXCEEDED("LIMIT-1", "The required resources exceed the allowed limits"), NOT_ENOUGH_RESOURCES(
        "ALLOC-0", "There isn't enough resources to create the virtual machine"), //
    ALLOCATOR_ERROR("ALLOC-1", "Can not create virtual machine"), //

    // VIRTUAL SYSTEM MONITOR

    MONITOR_PROBLEM("VSM-0", "An error was occurred when monitoring the physical machine"), UNMONITOR_PROBLEM(
        "VSM-1", "An error was occurred when shutting down the monitored physical machine"),

    // LICENSE
    LICENSE_UNEXISTING("LICENSE-0", "The requested license does not exist"), LICENSE_INVALID(
        "LICENSE-1", "The provided license is not valid"), LICENSE_CIHPER_INIT_ERROR("LICENSE-2",
        "Could not initialize licensing ciphers"), LICENSE_CIHPER_KEY("LICENSE-3",
        "Could not read licensing cipher key"), LICENSE_OVERFLOW("LICENSE-4",
        "The maximum number of managed cores has been reached"), LICENSE_DUPLICATED("LICENSE-5",
        "The license already exists"),

    // TIERS
    NON_EXISTENT_TIER("TIER-0", "The requested tier does not exist"), NULL_TIER("TIER-1",
        "Embedded Tier of the StoragePool can not be null"), MISSING_TIER_LINK("TIER-2",
        "Missing link to the tier"), TIER_PARAM_NOT_FOUND("TIER-3", "Missing tiers parameter"), TIER_LINK_DATACENTER_PARAM_NOT_FOUND(
        "TIER-4", "Datacenter param in tier link not found"), TIER_LINK_DATACENTER_DIFFERENT(
        "TIER-5",
        "Tier's datacenter does not belong to the same datacenter where you want to create the StoragePool"),

    // DEVICES
    NON_EXISTENT_DEVICE("DEVICE-0", "The requested tier does not exist"),

    // STATISTICS
    NON_EXISTENT_STATS("STATS-0", "Non existent statistical data found"), NON_EXISTENT_STATS_FOR_DATACENTER(
        "STATS-1", "Non existent statistical data found for the requested datacenter"), NON_EXISTENT_STATS_FOR_DCLIMITS(
        "STATS-2",
        "Non existent statistical data found for the requested enterprise in this datacenter"), NON_EXISTENT_STATS_FOR_ENTERPRISE(
        "STATS-3", "Non existent statistical data found for the requested enterprise"),

    ;

    /**
     * Internal error code
     */
    String code;

    /**
     * Description message
     */
    String message;

    public String getCode()
    {
        return String.valueOf(this.code);
    }

    public String getMessage()
    {
        return this.message;
    }

    APIError(final String code, final String message)
    {
        this.code = code;
        this.message = message;
    }

    public APIError addCause(final String cause)
    {
        this.message = String.format("%s.\ncaused by:%s", this.message, cause);
        return this;
    }

    public static void main(final String[] args)
    {
        APIError[] errors = APIError.values();
        Arrays.sort(errors, new Comparator<APIError>()
        {
            @Override
            public int compare(final APIError err1, final APIError err2)
            {
                return String.CASE_INSENSITIVE_ORDER.compare(err1.code, err2.code);
            }

        });

        // Outputs all errors in wiki table format
        for (APIError error : errors)
        {
            System.out.println(String.format("| %s | %s | %s |", error.code, error.message, error
                .name()));
        }
    }
}
