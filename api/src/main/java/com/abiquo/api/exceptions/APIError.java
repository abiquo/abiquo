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

import com.abiquo.model.validation.IscsiPath;
import com.abiquo.server.core.infrastructure.management.Rasd;

/**
 * Contains all the errors notified by the API.
 * 
 * @author eruiz
 */
public enum APIError
{
    // STATUSCODES
    STATUS_BAD_REQUEST("400-BAD REQUEST", "Request not valid"), STATUS_UNAUTHORIZED(
        "401-UNAUTHORIZED", "This requests requires user authentication"), STATUS_FORBIDDEN(
        "403-FORBIDDEN", "Access is denied"), STATUS_NOT_FOUND("404-NOT FOUND",
        "The Resource requested does not exist"), STATUS_METHOD_NOT_ALLOWED(
        "405-METHOD NOT ALLOWED", "The resource doesn't expose this method"), STATUS_CONFLICT(
        "409-CONFLICT", "Conflict"), STATUS_UNSUPPORTED_MEDIA_TYPE("415-UNSUPPORTED MEDIA TYPE",
        "Abiquo API currently only supports application/xml Media Type"), STATUS_INTERNAL_SERVER_ERROR(
        "500-INTERNAL SERVER ERROR", "Unexpected exception"), STATUS_UNPROVISIONED(
        "412 - Unprovisioned", "Unprovisioned exception"),

    // GENERIC
    MALFORMED_URI("GEN-0", "Malformed URI"), INVALID_ID("GEN-1", "Identifier can't be 0"), CONSTRAINT_VIOLATION(
        "GEN-2", "Invalid xml document, please make sure all the mandatory fields are right"), UNMARSHAL_EXCEPTION(
        "GEN-3", "Invalid xml document"), FORBIDDEN("GEN-4",
        "Not enough permissions to perform this action"), INVALID_CREDENTIALS("GEN-5",
        "Invalid credentials"), INVALID_LINK("GEN-6", "Invalid link reference"),

    // INVALID_IP("GEN-4", "Invalid IP"),
    INVALID_PRIVATE_NETWORK_TYPE("GEN-6", "Invalid private network type"), INTERNAL_SERVER_ERROR(
        "GEN-7", "Unexpected error"), GENERIC_OPERATION_ERROR("GEN-8",
        "The operation could not be performed. Please, contact the Administrator."), NOT_ENOUGH_PRIVILEGES(
        "GEN-9", "Not enough privileges to perform this operation"), INCOHERENT_IDS("GEN-10",
        "The paramter ID is different from the Entity ID"),

    // DATACENTER
    NON_EXISTENT_DATACENTER("DC-0", "The requested datacenter does not exist"), DATACENTER_DUPLICATED_NAME(
        "DC-3", "There is already a datacenter with that name"), DATACENTER_NOT_ALLOWED("DC-4",
        "The current enterprise can't use this datacenter"), DATACENTER_DELETE_STORAGE("DC-5",
        "Cannot delete datacenter with storage devices associated"), DATACENTER_DELETE_VIRTUAL_DATACENTERS(
        "DC-6", "Cannot delete datacenter with virtual datacenters associated"),

    // ENTERPRISE
    NON_EXISTENT_ENTERPRISE("EN-0", "The requested enterprise does not exist"), ENTERPRISE_DUPLICATED_NAME(
        "ENTERPRISE-4", "Duplicated name for an enterprise"), ENTERPRISE_DELETE_ERROR_WITH_VDCS(
        "ENTERPRISE-5", "Cannot delete enterprise with associated virtual datacenters"), ENTERPRISE_DELETE_OWN_ENTERPRISE(
        "ENTERPRISE-6", "Cannot delete the current user enterprise"), ENTERPRISE_EMPTY_NAME(
        "ENTERPRISE-7", "Enterprise name can't be empty"), ENTERPRISE_WITH_BLOCKED_USER(
        "ENTERPRISE-8",
        "Cannot delete enterprise because some users have roles that cannot be deleted, please change their enterprise before continuing"), MISSING_ENTERPRISE_LINK(
        "ENTERPRISE-9", "Missing link to the enterprise"),

    // LIMITS: Common for Enterprise and virtual datacenter
    LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC("LIMIT-6",
        "Invalid vlan hard limit, it cannot be bigger than the number of vlans per virtual datacenter: {0}"), LIMITS_DUPLICATED(
        "LIMIT-7", "Duplicated limits by enterprise and datacenter"), LIMITS_NOT_EXIST("LIMIT-8",
        "Limits by enterprise and datacenter don't exist"), //
    ENTERPRISE_LIMIT_EDIT_ARE_SURPRASED("LIMIT-9",
        "Cannot edit resource limits, current enterprise allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are on HARD limit)"), //
    DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
        "LIMIT-10",
        "Cannot edit resource limits, current enterprise and datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are on HARD limit)"), DATACENTER_LIMIT_DELETE_VDCS(
        "LIMIT-11",
        "Cannot unassign datacenter from enterprise because it is being used by virtual datacenter(s)."),

    // VIRTUAL DATACENTER
    NON_EXISTENT_VIRTUAL_DATACENTER("VDC-0", "The requested virtual datacenter does not exist"), VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE(
        "VDC-1", "Invalid hypervisor type for this datacenter"), VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES(
        "VDC-2",
        "This datacenter contains virtual appliances and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_CONTAINS_RESOURCES(
        "VDC-3",
        "This datacenter has volumes attached and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_INVALID_NETWORKS(
        "VDC-4", "This datacenter has networks without IPs!"), VIRTUAL_DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
        "VDC-5",
        "Cannot edit resource limits, current virtual datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are on HARD limit)"), VIRTUAL_DATACENTER_MUST_HAVE_NETWORK(
        "VDC-6", "Virtual Datacenter must be created with a private network"), VIRTUAL_DATACENTER_MINIMUM_VLAN(
        "VDC-7", "Virtual Datacenter must have at least one private VLAN"),

    // VLANS
    VLANS_PRIVATE_MAXIMUM_REACHED("VLAN-0",
        "You have reached the maximum VLANs you can create in this VirtualDatacenter"), VLANS_DUPLICATED_VLAN_NAME_VDC(
        "VLAN-1", "Can not create two VLANs with the same name in a VirtualDatacenter"), VLANS_PRIVATE_ADDRESS_WRONG(
        "VLAN-2", "Can not use any other address than the private range"), VLANS_TOO_BIG_NETWORK(
        "VLAN-3",
        "For performance reasons, Abiquo does not allow the creation of networks with more than 1024 IP addresses (subnet 22 or lower)."), VLANS_TOO_BIG_NETWORK_II(
        "VLAN-4", "This network allows a netmask up to 24. Try a value between 30 and 24"), VLANS_TOO_SMALL_NETWORK(
        "VLAN-5", "The smallest network allowed has a 30 mask. Try a value between 30 and 24"), VLANS_INVALID_NETWORK_AND_MASK(
        "VLAN-6", "The network does not match the mask. Check your request"), VLANS_GATEWAY_OUT_OF_RANGE(
        "VLAN-7", "Gateway address out of range. It must be into the ip range address"), VLANS_NON_EXISTENT_VIRTUAL_NETWORK(
        "VLAN-8", "The requested virtual network does not exist"), VLANS_AT_LEAST_ONE_DEFAULT_NETWORK(
        "VLAN-9", "There must be at least one default VLAN in each Virtual Datacenter"), VLANS_EDIT_INVALID_VALUES(
        "VLAN-10",
        "Attributes 'address', 'mask' and 'tag' can not be changed by the Edit process of private VLAN."), VLANS_DEFAULT_NETWORK_CAN_NOT_BE_DELETED(
        "VLAN-11", "Default VLAN can not be deleted."), VLANS_WITH_USED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-12", "Can not delete a VLAN with IPs used by Virtual Machines"), VLANS_TAG_MANDATORY_FOR_PUBLIC_VLANS(
        "VLAN-13", "Field 'tag' is mandatory when you create Public VLANs"), VLANS_WITH_PURCHASED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-14", "Can not delete a VLAN with IPs purchased by Enterprises"), VLANS_DUPLICATED_VLAN_NAME_DC(
        "VLAN-15", "Can not create two VLANs with the same name in a Datacenter"), VLANS_TAG_INVALID(
        "VLAN-16", "VLAN tag out of limits"), VLANS_NON_EXISTENT_PUBLIC_IP("VLAN-17",
        "The requested IP object does not exist"), VLANS_IP_EDIT_INVALID_VALUES("VLAN-18",
        "Only 'quarantine' and 'available' attributes can be modified when editing an IP"), VLANS_PUBLIC_EDIT_INVALID_VALUES(
        "VLAN-19",
        "Attributes 'address' and 'mask' can not be changed by the Edit process of public VLAN."), VLANS_PUBLIC_IP_NOT_TO_BE_PURCHASED(
        "VLAN-20", "The IP does not exist or is not available"), VLANS_PUBLIC_IP_NOT_PURCHASED(
        "VLAN-21", "The IP does not exist or is not purchased"), VLANS_PUBLIC_IP_BUSY("VLAN-22",
        "This IP address is currently used by a Virtual Machine. Can not be released"), VLANS_PRIVATE_IP_INVALID_LINK(
        "VLAN-23", "Invalid link to private ip address to create NIC"), VLANS_IP_LINK_INVALID_VDC(
        "VLAN-24", "Invalid Virtual Datacenter identifier in the IP link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE(
        "VLAN-25", "The IP address is already used by another virtual machine"), VLANS_PUBLIC_IP_INVALID_LINK(
        "VLAN-26", "Invalid link to public ip address to create NIC"), VLANS_IP_CAN_NOT_BE_DEASSIGNED_DUE_CONFIGURATION(
        "VLAN-27",
        "Can not release this IP from the virtual machine, because the virtual machine is using its gateway and "
            + "configuration. Please, assign another configuration before to release this IP"), VLANS_NIC_NOT_FOUND(
        "VLAN-28", "The NIC does not exist"), VLANS_CAN_NOT_DELETE_LAST_NIC("VLAN-29",
        "Every virtual machine should have at least one NIC"), VLANS_REORDER_NIC_INVALID_LINK(
        "VLAN-30", "Invalid link to reorder NICs into a Virtual Machine"), VLANS_REORDER_NIC_INVALID_LINK_VALUES(
        "VLAN-31",
        "Invalid link values (virtualdatacenter, virtualappliance and/or virtualmachine identifiers) to reorder NICs into a Virtual Machine."), VLANS_IP_EDIT_NOT_AVAILABLE_PURCHASED(
        "VLAN-32", "Can not set the IP as 'not available' while is purchased by an Enterprise"), VLANS_PUBIC_IP_CAN_NOT_RELEASE(
        "VLAN-33", "Can not release a Public IP while is assigned to a Virtual Machine"), VLANS_NON_EXISTENT_CONFIGURATION(
        "VLAN-34", "The configuration does not exist"), VLANS_CAN_NOT_ASSIGN_TO_DEFAULT_ENTERPRISE(
        "VLAN-35",
        "Can not assign external VLAN as default because it is not assigned to any enterprise"), VLANS_VIRTUAL_DATACENTER_SHOULD_HAVE_A_DEFAULT_VLAN(
        "VLAN-36",
        "Unable to found default VLAN in Virtual Datacenter. Incoherent state in Database"), VLANS_INVALID_ENTERPRISE_LINK(
        "VLAN-37", "Invalid Enterprise identifier in the Enterprise link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_DATACENTER(
        "VLAN-38", "The IP address is already assigned to a Virtual Datacenter"), VLANS_WITH_IPS_ASSIGNED_TO_VDC(
        "VLAN-39", "Can not delete a VLAN with IPs assigned to a Virtual Datacenter"), VLANS_EXTERNAL_VLAN_IN_ANOTHER_DATACENTER(
        "VLAN-40",
        "The requested external VLAN belongs to another datacenter where the Virtual Datacenter is"), VLANS_INVALID_IP_FORMAT(
        "VLAN-41", "IP format is invalid"), VLANS_IP_DOES_NOT_EXISTS("VLAN-42",
        "The IP does not exists"), VLANS_CANNOT_DELETE_DEFAULT("VLAN-43",
        "This is the default VLAN for the Virtual Datacenter and cannot be deleted"), VLANS_EXTERNAL_VLAN_OF_ANOTHER_ENTERPRISE(
        "VLAN-42", "The external VLAN belongs to another enterprise"), VLANS_IP_NOT_AVAILABLE(
        "VLAN-43", "The IP address is not available to be used by a Virtual Machine"), VLANS_NON_EXISTENT_EXTERNAL_IP(
        "VLAN-44", "The requested IP object does not exist"), VLANS_ASSIGNED_TO_ANOTHER_VIRTUAL_DATACENTER(
        "VLAN-45",
        "Cannot change enterprise because this network is used as default by Virtual Datacenter"),

    // VIRTUAL APPLIANCE
    NON_EXISTENT_VIRTUALAPPLIANCE("VAPP-0", "The requested virtual appliance does not exist"), VIRTUALAPPLIANCE_NOT_DEPLOYED(
        "VAPP-1", "The virtual appliance is not deployed"), VIRTUALAPPLIANCE_NOT_RUNNING("VAPP-2",
        "The virtual appliance is not running"),

    // RACK
    NOT_ASSIGNED_RACK_DATACENTER("RACK-0", "The rack is not assigned to the datacenter"), RACK_DUPLICATED_NAME(
        "RACK-3", "There is already a rack with that name in this datacenter"), NON_EXISTENT_RACK(
        "RACK-4", "This rack does not exist "), NON_MANAGED_RACK("RACK-5",
        "Machines in this rack can not be discovered"), NON_UCS_RACK("RACK-6",
        "This rack is not an UCS Rack"), RACK_DUPLICATED_IP("RACK-7",
        "There is already a managed rack with this IP defined"), RACK_CONFIG_ERROR("RACK-8",
        "There is a problem with the details of the UCS Rack"), RACK_CANNOT_REMOVE_VMS("RACK-9",
        "Can not remove this rack because there are some virtual machines deployed on it"),

    // MACHINE
    NON_EXISTENT_MACHINE("MACHINE-0", "The requested machine does not exist"), NOT_ASSIGNED_MACHINE_DATACENTER_RACK(
        "MACHINE-1", "The machine is not assigned to the datacenter or rack"), MACHINE_ANY_DATASTORE_DEFINED(
        "MACHINE-2", "Machine definition should have at least one datastore created and enabled"), MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK(
        "MACHINE-3", "A machine can not be added this way to a UCS Rack"), INVALID_STATE_CHANGE(
        "MACHINE-5", "The requested state transaction is not valid"), MACHINE_NOT_ACCESIBLE(
        "MACHINE-6", "The requested machine could not be contacted"), MACHINE_INVALID_VIRTUAL_SWITCH_NAME(
        "MACHINE-4", "Invalid virtual switch name"), MACHINE_CANNOT_BE_DELETED(
        "MACHINE-7",
        "Machine can not be removed due it is managed by the high availability engine. Reenable it manually to recover managed state."), MACHINE_INVALID_IPMI_CONF(
        "MACHINE-8", "Invalid IPMI configuration."), MACHINE_INVALID_IP_RANGE("MACHINE-9",
        "Invalid ip range"), MACHINE_IQN_MISSING("MACHINE-10",
        "The IQN of the target Physical Machine is not set"),

    HYPERVISOR_EXIST_IP("HYPERVISOR-1",
        "Invalid hypervisor IP. Already exist an hypervisor with that IP"), HYPERVISOR_EXIST_SERVICE_IP(
        "HYPERVISOR-2",
        "Invalid hypervisor service IP. Already exist an hypervisor with that service IP"), HYPERVISOR_TYPE_MISSING(
        "HYPERVISOR-3", "The Hypervisor technology of the target Hypervisor is not set."),

    // NETWORK
    NETWORK_INVALID_CONFIGURATION("NET-0",
        "Invalid network configuration for the virtual datacenter"), NETWORK_WITHOUT_IPS("NET-8",
        "This network doesn't have IPs"), NETWORK_IP_FROM_BIGGER_THAN_IP_TO("NET-9",
        "Parameter IPFrom is greater than IPTo"), NETWORK_IP_FROM_ERROR("NET-10",
        "Parameter IPFrom is invalid"), NETWORK_IP_TO_ERROR("NET-11", "Parameter IPTo is invalid"),

    // VIRTUAL MACHINE
    VIRTUAL_MACHINE_WITHOUT_HYPERVISOR("VM-0", "The virtual machine not have a hypervisor assigned"), NON_EXISTENT_VIRTUALMACHINE(
        "VM-1", "The requested virtual machine does not exist"), VIRTUAL_MACHINE_ALREADY_IN_PROGRESS(
        "VM-2", "The virtual machine is already in progress"), VIRTUAL_MACHINE_NOT_DEPLOYED("VM-3",
        "The virtual machine is not deployed"), VIRTUAL_MACHINE_STATE_CHANGE_ERROR("VM-4",
        "The virtual machine cannot change the state to the required state"), VIRTUAL_MACHINE_REMOTE_SERVICE_ERROR(
        "VM-5", "The virtual machine cannot change the state due to a communication problem"), VIRTUAL_MACHINE_PAUSE_UNSUPPORTED(
        "VM-6", "The virtual machine does not support the action PAUSE"), VIRTUAL_MACHINE_INVALID_STATE_DEPLOY(
        "VM-7", "The allowed power states for Virtual Machines is NOT_ALLOCATED"), VIRTUAL_MACHINE_INVALID_STATE_DELETE(
        "VM-8", "The allowed power states for Virtual Machines are UNKNOWN and NOT_ALLOCATED"), NON_EXISTENT_VIRTUAL_IMAGE(
        "VM-9", "The requested Virtual Image does not exists"), VIRTUAL_MACHINE_EDIT_STATE("VM-10",
        "The Virtual Machine is in a state that does not allow the request, therefore can't be modified"), VIRTUAL_MACHINE_UNALLOCATED_STATE(
        "VM-11",
        "The Virtual Machine is not allocated. Therefore the change of the state cannot be applied"), VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY(
        "VM-12", "The allowed power states for Virtual Machines is ON, OFF, PAUSED  or ALLOCATED"), VIRTUAL_MACHINE_INCOHERENT_STATE(
        "VM-13",
        "Virtual Machine configuration actions can only be performed when the Virtual Machine is NOT-DEPLOYED"), VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED(
        "VM-14",
        "Only the 'used' attribute of the Virtual Machine Network Configuration can be changed"), VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION(
        "VM-15", "It should be at least one 'used' configuration in each Virtual Machine"),

    // ROLE
    NON_EXISTENT_ROLE("ROLE-0", "The requested role does not exist"), NON_MODIFICABLE_ROLE(
        "ROLE-1", "The requested role cannot be modified"), PRIVILEGE_PARAM_NOT_FOUND("ROLE-2",
        "Missing privilege parameter"), DELETE_ERROR("ROLE-3",
        "The requested role is blocked. Cannot be deleted"), DELETE_ERROR_WITH_USER("ROLE-4",
        "Cannot delete a role with associated User"), DELETE_ERROR_WITH_ROLE_LDAP("ROLE-5",
        "Cannot delete a role with associated RoleLdap"), DUPLICATED_ROLE_NAME_ENT("ROLE-6",
        "Cannot create a role with the same name of an existing role for the same enterprise"), DUPLICATED_ROLE_NAME_GEN(
        "ROLE-7", "Cannot create a generic role with the same name of an existing generic role"), HAS_NOT_ENOUGH_PRIVILEGE(
        "ROLE-8", "Hasn't got enough privileges to manage this role"),

    // PRIVILEGE
    NON_EXISTENT_PRIVILEGE("PRIVILEGE-0", "The requested privilege does not exist"),

    // ROLE_LDAP
    NON_EXISTENT_ROLELDAP("ROLELDAP-0", "The requested roleLdap does not exist"), MULTIPLE_ENTRIES_ROLELDAP(
        "ROLELDAP-1", "There are multiple entries for the requested roleLdap"), NOT_ASSIGNED_ROLE(
        "ROLELDAP-2", "The roleLdap must have a Role"),

    // USER
    NOT_ASSIGNED_USER_ENTERPRISE("USER-0", "The user is not assigned to the enterprise"), MISSING_ROLE_LINK(
        "USER-1", "Missing link to the role"), ROLE_PARAM_NOT_FOUND("USER-2",
        "Missing roles parameter"), USER_NON_EXISTENT("USER-3", "The requested user does not exist"), USER_DUPLICATED_NICK(
        "USER-4", "Duplicated nick for the user"), EMAIL_IS_INVALID("USER-5",
        "The email isn't valid"), NOT_USER_CREACION_LDAP_MODE("USER-6",
        "In Ldap mode can not create user"), NOT_EDIT_USER_ROLE_LDAP_MODE("USER-7",
        "In Ldap mode can not modify user's role"), NOT_EDIT_USER_ENTERPRISE_LDAP_MODE("USER-8",
        "In Ldap mode can not modify user's enterprise"), USER_DELETING_HIMSELF("USER 9",
        "The user cannot delete his own user account"),

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
        "The current repository holds virtual images being used on some virtual appliances, so it's not possible to remove this Remote Service. You can modify the Appliance manager but only if the same repository is used."), REMOTE_SERVICE_STORAGE_REMOTE_WITH_POOLS(
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
        "Missing query parameter ip"), NC_BAD_CREDENTIALS_TO_RACK("NC-2",
        "Bad credentials attempting to retrieve the list of physical machines from rack "), NC_BAD_CREDENTIALS_TO_MACHINE(
        "NC-3", "Bad credentials attempting to retrieve the machine "), NC_CONNECTION_EXCEPTION(
        "NC-4", "There is a machine running in the given IP. But any hypervisor responds"), NC_NOT_FOUND_EXCEPTION(
        "NC-5", "There is any machine running in the given IP"), NC_UNEXPECTED_EXCEPTION(
        "NC-6",
        "Hypervisor information could not be discovered or retrieved. This error may be caused by misconfiguration of the platform or an error in the data provided. Please see the Event Log for more detail"), NC_UNAVAILABLE_EXCEPTION(
        "NC-7", "The discovery manager currently is not available"),

    // STORAGE POOL
    MISSING_REQUIRED_QUERY_PARAMETER_IQN("SP-1", "Missing query parameter iqn"), CONFLICT_STORAGE_POOL(
        "SP-2", "The id of the Storage Pool and the id of the submitted object must be the same"), NON_EXISTENT_STORAGE_POOL(
        "SP-3", "The requested Storage Pool does not exist"), STORAGE_POOL_ERROR_MODIFYING("SP-4",
        "There was an unexpected error while modifying the Storage Pool"), STORAGE_POOLS_SYNC(
        "SP-5",
        "Storage plugin not found. Storage plugin is required, please consult the Administrator Guide"), STORAGE_POOL_SYNC(
        "SP-6", "Could not get the requested Storage Pool from the target device"), CONFLICT_VOLUMES_CREATED(
        "SP-7", "Can not edit or delete the Storage Pool. There are volumes created "), STORAGE_POOL_DUPLICATED(
        "SP-8", "Duplicated Storage Pool"), STORAGE_POOL_TIER_IS_DISABLED("SP-9",
        "Tier is disabled"), STORAGE_POOL_PARAM_NOT_FOUND("SP-10", "Missing storage pool parameter"), STORAGE_POOL_LINK_DATACENTER_PARAM_NOT_FOUND(
        "SP-11", "Datacenter param in storage pool link not found"), STORAGE_POOL_LINK_DEVICE_PARAM_NOT_FOUND(
        "SP-12", "Storage device param in storage pool link not found"), MISSING_POOL_LINK("SP-13",
        "Missing storage pool link"),

    // DATASTORE
    DATASTORE_NON_EXISTENT("DATASTORE-0", "The requested datastore does not exist"), DATASTORE_DUPLICATED_NAME(
        "DATASTORE-4", "Duplicated name for the datastore"), DATASTORE_DUPLICATED_DIRECTORY(
        "DATASTORE-6", "Duplicated directory path for the datastore"), DATASTORE_NOT_ASSIGNED_TO_MACHINE(
        "DATASTORE-7", "The datastore is not assigned to that machine"),

    // SYSTEM PROPERTIES
    NON_EXISTENT_SYSTEM_PROPERTY("SYSPROP-0", "The requested system property does not exist"), SYSTEM_PROPERTIES_DUPLICATED_NAME(
        "SYSPROP-1", "There is already a system property with that name"),

    // ALLOCATOR
    LIMITS_EXCEEDED("LIMIT-0", "The required resources exceed the allowed limits"), LIMIT_EXCEEDED(
        "LIMIT-1", "The required resources exceed the allowed limits"), NOT_ENOUGH_RESOURCES(
        "ALLOC-0", "There are not enough resources to create the virtual machine"), //
    ALLOCATOR_ERROR("ALLOC-1", "Can not create virtual machine"), //

    CHECK_EDIT_NO_TARGET_MACHINE("EDIT-01",
        "This method require the virtual machine being deployed on some target hypervisor"),

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
        "Tier's datacenter does not belong to the same datacenter where you want to create the StoragePool"), TIER_CONFLICT_DISABLING_TIER(
        "TIER-6", "Can not disable a Tier with associated Storage Pools"),

    // DEVICES
    NON_EXISTENT_DEVICE("DEVICE-0", "The requested device does not exist"), DEVICE_DUPLICATED(
        "DEVICE-1", "Duplicated Storage Device"),

    // STATISTICS
    NON_EXISTENT_STATS("STATS-0", "Non existent statistical data found"), NON_EXISTENT_STATS_FOR_DATACENTER(
        "STATS-1", "Non existent statistical data found for the requested datacenter"), NON_EXISTENT_STATS_FOR_DCLIMITS(
        "STATS-2",
        "Non existent statistical data found for the requested enterprise in this datacenter"), NON_EXISTENT_STATS_FOR_ENTERPRISE(
        "STATS-3", "Non existent statistical data found for the requested enterprise"), NODECOLLECTOR_ERROR(
        "NODECOLLECTOR-1", "Nodecollector has raised an error"),

    // QUERY PAGGING STANDARD ERRORS
    QUERY_INVALID_PARAMETER("QUERY-0", "Invalid 'by' parameter"), QUERY_NETWORK_TYPE_INVALID_PARAMETER(
        "QUERY-1", "Invalid 'type' parameter. Only 'EXTERNAL' or 'PUBLIC' allowed"),

    VOLUME_GENERIC_ERROR("VOL-0", "Could not create the volume in the selected tier"), VOLUME_NOT_ENOUGH_RESOURCES(
        "VOL-1", "There are not enough resources in the selected tier to create the volume"), VOLUME_NAME_NOT_FOUND(
        "VOL-2", "The name of the volume is required"), NON_EXISTENT_VOLUME("VOL-3",
        "The volume does not exist"), VOLUME_CREATE_ERROR("VOL-4",
        "An unexpected error occured while creating the volume"), VOLUME_MOUNTED_OR_RESERVED(
        "VOL-5", "The volume cannot be deleted because it is associated to a virtual machine"), VOLUME_SSM_DELETE_ERROR(
        "VOL-6", "Could not physically delete the volume from the target storage device"), VOLUME_DELETE_STATEFUL(
        "VOL-7", "The volume cannot be deleted because it is in a persistent image process"), VOLUME_DELETE_IN_VIRTUALAPPLIANCE(
        "VOL-8",
        "The stateful volume cannot be deleted because it is being used in a virtual appliance"), VOLUME_ISCSI_NOT_FOUND(
        "VOL-9", "The idScsi of the volume is required"), VOLUME_DECREASE_SIZE_LIMIT_ERROR(
        "VOL-10", "The size of the volume cannot be decreased"), VOLUME_NAME_LENGTH_ERROR("VOL-11",
        "The size of the 'name' field of the volume cannot exceed 256 characters"), VOLUME_ISCSI_INVALID(
        "VOL-12", "The property idScsi " + IscsiPath.ERROR_MESSAGE), VOLUME_SIZE_INVALID("VOL-13",
        "The size property must be a non-zero integer up to " + Rasd.LIMIT_MAX), VOLUME_IN_USE(
        "VOL-14", "The volume cannot be edited because it is being used in a virtual machine"), VOLUME_UPDATE(
        "VOL-15", "An unexpected error occurred and the volume could not be updated"), VOLUME_RESIZE_STATEFUL(
        "VOL-16", "Cannot resize a persistent volume"), VOLUME_RESIZE_GENERIC_ISCSI("VOL-17",
        "Cannot resize a generic Iscsi volume"), SSM_UNREACHABLE("VOL-18",
        "Could not get the Storage Manager remote service"), VOLUME_NOT_ATTACHED("VOL-19",
        "The volume is not attached to the virtual machine"), VOLUME_ATTACH_INVALID_LINK("VOL-20",
        "Invalid link to the volume to attach"), VOLUME_ATTACH_INVALID_VDC_LINK("VOL-21",
        "Invalid virtual datacenter in the link to the volume to attach"), VOLUME_ALREADY_ATTACHED(
        "VOL-22", "The volume is already attached to a virtual machine"), VOLUME_TOO_MUCH_ATTACHMENTS(
        "VOL-23", "The maximum number of attached disks and volumes has been reached"), VOLUME_ATTACH_ERROR(
        "VOL-24",
        "An unexpected error occured while attaching the volume. Please, contact the administrator"), VOLUME_ALREADY_DETACHED(
        "VOL-25", "The volume is already detached"), VOLUME_DETACH_ERROR("VOL-26",
        "An unexpected error occured while detaching the volume. Please, contact the administrator"),

    // RULES
    NON_EXISTENT_EER("RULE-1", "The requested restrict shared server rule does not exist"), NON_EXISTENT_FPR(
        "RULE-2", "The requested load balance rule does not exist"), NON_EXISTENT_MLR("RULE-3",
        "The requested load level rule does not exist"), ONE_FPR_REQUIRED("RULE-4",
        "At least one load balance rule is required"), ONE_LINK_REQUIRED("RULE-5",
        "It is expected one link with the rel attribute possible values (datacenter/rack/machine)"), INVALID_FPR(
        "RULE-6", "The load balance type indicated is null or invalid"),

    //
    HD_NON_EXISTENT_HARD_DISK("HD-1", "The requested hard disk does not exist"), HD_DISK_0_CAN_NOT_BE_DELETED(
        "HD-2",
        "Disk 0 comes from the Virtual Image and can not be deleted from the Virtual Machine"), HD_INVALID_DISK_SIZE(
        "HD-3", "Invalid disk size."),
    // Chef
    CHEF_ERROR_GETTING_RECIPES("CHEF-0",
        "Could not get the list of available recipes for the enterprise"), CHEF_ERROR_GETTING_ROLES(
        "CHEF-1", "Could not get the list of available roles for the enterprise"), CHEF_ERROR_CONNECTION(
        "CHEF-2", "Cannot connect to the Chef Server"), CHEF_NODE_DOES_NOT_EXIST("CHEF-3",
        "The node does not exist in the Chef Server. "
            + "If the virtual machine is bootstraping, please wait until the process completes."), CHEF_ELEMENT_DOES_NOT_EXIST(
        "CHEF-4", "The given runlist element does not exist in the Chef Server"), CHEF_CANNOT_UPDATE_NODE(
        "CHEF-5", "The node could not be updated in the Chef Server. "
            + "Please, contact the administrator."), CHEF_CANNOT_CONNECT("CHEF-6",
        "Could not connect to the Chef server. Please, contact the administrator."), CHEF_INVALID_ENTERPRISE_DATA(
        "CHEF-7", "Could not connect to the Chef server with the given Validator and Admin data. "
            + "Please verify the credentials"), CHEF_INVALID_ENTERPRISE("CHEF-8",
        "The enterprise is not configured to use Chef"), CHEF_INVALID_VIRTUALMACHINE("CHEF-9",
        "The virtual machine can not use Chef. "
            + "Please, verify that the image is Chef enabled and the Enterprise can use Chef"),

    ;

    /**
     * Internal error code
     */
    String code;

    /**
     * Description message
     */
    String message;

    String cause;

    private APIError(final String code, final String message)
    {
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return String.valueOf(this.code);
    }

    public String getMessage()
    {
        return this.message;
    }

    public void addCause(final String cause)
    {
        this.cause = cause;
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

        System.out.println("\n ************ Wiki errors ************** \n");

        // Outputs all errors in wiki table format
        for (APIError error : errors)
        {
            System.out.println(String.format("| %s | %s | %s |", error.code, error.message,
                error.name()));
        }

        System.out.println("\n ************ Flex client labels ************** \n");

        // Outputs all errors for the Chef client
        for (APIError error : errors)
        {
            System.out.println(String.format("%s=%s", error.code, error.message));
        }
    }

}
