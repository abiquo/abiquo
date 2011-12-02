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
        "401-UNAUTHORIZED", "This request requires user authentication"), STATUS_FORBIDDEN(
        "403-FORBIDDEN", "Access is denied"), STATUS_NOT_FOUND("404-NOT FOUND",
        "The requested resource does not exist"), STATUS_METHOD_NOT_ALLOWED(
        "405-METHOD NOT ALLOWED", "The resource does not expose this method"), STATUS_CONFLICT(
        "409-CONFLICT", "Conflict"), STATUS_UNSUPPORTED_MEDIA_TYPE("415-UNSUPPORTED MEDIA TYPE",
        "Abiquo API currently only supports application/XML Media Type"), STATUS_INTERNAL_SERVER_ERROR(
        "500-INTERNAL SERVER ERROR", "Unexpected exception"), STATUS_UNPROVISIONED(
        "412 - Unprovisioned", "Error releasing resources on the hypervisor"),

    // GENERIC
    MALFORMED_URI("GEN-0", "Malformed URI"), INVALID_ID("GEN-1", "Identifier cannot be 0"), CONSTRAINT_VIOLATION(
        "GEN-2", "Invalid XML document; please make sure all mandatory fields are correct"), UNMARSHAL_EXCEPTION(
        "GEN-3", "Invalid XML document"), FORBIDDEN("GEN-4",
        "Not enough permissions to perform this action"), INVALID_CREDENTIALS("GEN-5",
        "Invalid credentials"), INVALID_LINK("GEN-6", "Invalid link reference"), WHITE_NAME(
        "GEN-7", "The property 'name', must not have whitespace at the beginning or the end."), WHITE_SYMBOL(
        "GEN-8", "The property 'symbol', must not have whitespace at the beginning or the end."), WHITE_DESCRIPTION(
        "GEN-9",
        "The property 'description', must not have whitespace at the beginning or the end."),

    // INVALID_IP("GEN-4", "Invalid IP"),
    INVALID_PRIVATE_NETWORK_TYPE("GEN-6", "Invalid private network type"), INTERNAL_SERVER_ERROR(
        "GEN-7", "Unexpected error"), GENERIC_OPERATION_ERROR("GEN-8",
        "The operation could not be performed. Please contact the Administrator."), NOT_ENOUGH_PRIVILEGES(
        "GEN-9", "Not enough privileges to perform this operation"), INCOHERENT_IDS("GEN-10",
        "The parameter ID is different from the Entity ID"),

    // DATACENTER
    NON_EXISTENT_DATACENTER("DC-0", "The requested datacenter does not exist"), DATACENTER_DUPLICATED_NAME(
        "DC-3", "There is already a datacenter with that name"), DATACENTER_NOT_ALLOWED("DC-4",
        "The current enterprise cannot use this datacenter"), DATACENTER_DELETE_STORAGE("DC-5",
        "Cannot delete datacenter with storage devices associated"), DATACENTER_DELETE_VIRTUAL_DATACENTERS(
        "DC-6", "Cannot delete datacenter with virtual datacenters associated"),

    // ENTERPRISE
    NON_EXISTENT_ENTERPRISE("EN-0", "The requested enterprise does not exist"), ENTERPRISE_DUPLICATED_NAME(
        "ENTERPRISE-4", "Duplicate name for an enterprise"), ENTERPRISE_DELETE_ERROR_WITH_VDCS(
        "ENTERPRISE-5", "Cannot delete enterprise with virtual datacenters associated"), ENTERPRISE_DELETE_OWN_ENTERPRISE(
        "ENTERPRISE-6", "Cannot delete the current user enterprise"), ENTERPRISE_EMPTY_NAME(
        "ENTERPRISE-7", "Enterprise name cannot be empty"), MISSING_ENTERPRISE_LINK("ENTERPRISE-8",
        "Missing enterprise link"), ENTERPRISE_WITH_BLOCKED_USER(
        "ENTERPRISE-9",
        "Cannot delete enterprise because some users have roles that cannot be deleted, please change their enterprise before continuing"), ENTERPRISE_NOT_ALLOWED_DATACENTER(
        "ENTERPRISE-10", "The Enterprise does not have permissions to use the requested datacenter"), INVALID_ENTERPRISE_LINK(
        "ENTERPRISE-11", "Invalid Enterprise identifier in the Enterprise link"), MISSING_PRICING_TEMPLATE_LINK(
        "ENTERPRISE-12", "Missing link to the pricing template"), PRICING_TEMPLATE_PARAM_NOT_FOUND(
        "ENTERPRISE-13", "Missing pricing template parameter"),

    // LIMITS: Common for Enterprise and virtual datacenter
    LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC(
        "LIMIT-6",
        "Invalid VLAN hard limit; this cannot be greater than the number of VLANS per virtual datacenter: {0}"), LIMITS_DUPLICATED(
        "LIMIT-7", "Duplicate limits by enterprise and datacenter"), LIMITS_NOT_EXIST("LIMIT-8",
        "Limits by enterprise and datacenter do not exist"), //
    ENTERPRISE_LIMIT_EDIT_ARE_SURPRASED("LIMIT-9",
        "Cannot edit resource limits; current enterprise allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), //
    DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
        "LIMIT-10",
        "Cannot edit resource limits; current enterprise and datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), DATACENTER_LIMIT_DELETE_VDCS(
        "LIMIT-11",
        "Cannot unassign datacenter from enterprise because it is being used by virtual datacenter(s)."),

    // VIRTUAL DATACENTER
    NON_EXISTENT_VIRTUAL_DATACENTER("VDC-0", "The requested virtual datacenter does not exist"), VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE(
        "VDC-1", "Invalid hypervisor type for this datacenter"), VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES(
        "VDC-2",
        "This datacenter contains virtual appliances and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_CONTAINS_RESOURCES(
        "VDC-3",
        "This datacenter has volumes attached and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_INVALID_NETWORKS(
        "VDC-4", "This datacenter has networks without IP addresses"), VIRTUAL_DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
        "VDC-5",
        "Cannot edit resource limits; current virtual datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), VIRTUAL_DATACENTER_MUST_HAVE_NETWORK(
        "VDC-6", "Virtual Datacenter must be created with a private network"), VIRTUAL_DATACENTER_MINIMUM_VLAN(
        "VDC-7", "Virtual Datacenter must have at least one private VLAN"),

    // VLANS
    VLANS_PRIVATE_MAXIMUM_REACHED("VLAN-0",
        "You have reached the maximum VLANs that you can create in this VirtualDatacenter"), VLANS_DUPLICATED_VLAN_NAME_VDC(
        "VLAN-1", "Cannot create two VLANs with the same name in a VirtualDatacenter"), VLANS_PRIVATE_ADDRESS_WRONG(
        "VLAN-2", "Cannot use any address outside the private range"), VLANS_TOO_BIG_NETWORK(
        "VLAN-3", "For performance reasons, Abiquo does not allow you to create large networks"), VLANS_TOO_BIG_NETWORK_II(
        "VLAN-4", "This network can have a netmask of between 30 and 24. Use a value above 24"), VLANS_TOO_SMALL_NETWORK(
        "VLAN-5", "This network can have a netmask of between 30 and 24. Use a value below 30"), VLANS_INVALID_NETWORK_AND_MASK(
        "VLAN-6", "The network does not match the mask. Check your request"), VLANS_GATEWAY_OUT_OF_RANGE(
        "VLAN-7", "Gateway address out of range. It must be in the IP address range"), VLANS_NON_EXISTENT_VIRTUAL_NETWORK(
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
            + "its VLAN configuration. Please, assign another configuration before to release this IP"), VLANS_NIC_NOT_FOUND(
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
        "Cannot change enterprise because this network is used as default by Virtual Datacenter"), VLANS_NOT_UNMANAGED(
        "VLAN-46", "The virtual network is not Unmanaged "), VLANS_UNMANAGED_WITH_VM_CAN_NOT_BE_DELETED(
        "VLAN-47", "Cannot delete Unmanaged Networks associated with Virtual Machines"),

    // VIRTUAL APPLIANCE
    NON_EXISTENT_VIRTUALAPPLIANCE("VAPP-0", "The requested virtual appliance does not exist"), VIRTUALAPPLIANCE_NOT_DEPLOYED(
        "VAPP-1", "The virtual appliance is not deployed"), VIRTUALAPPLIANCE_NOT_RUNNING("VAPP-2",
        "The virtual appliance is not running"), VIRTUALAPPLIANCE_DEPLOYED("VAPP-1",
        "The virtual appliance is deployed"), VIRTUALAPPLIANCE_NON_MANAGED_IMAGES("VAPP-4",
        "The virtual appliance has non managed images"),

    // VIRTUAL CONVERSION
    NON_EXISTENT_VIRTUALAPPLIANCE_STATEFULCONVERSION("VASC-0",
        "The requested stateful conversion does not exist"), INVALID_VASC_STATE("VASC-1",
        "Invalid expected state"),

    // NODE VIRTUAL IMAGE STATEFUL CONVERSION
    NON_EXISTENT_NODE_VIRTUALIMAGE_STATEFULCONVERSION("NVISC-0",
        "The requested node virtual image stateful conversion does not exist"),

    // RACK
    NOT_ASSIGNED_RACK_DATACENTER("RACK-0", "The rack is not assigned to the datacenter"), RACK_DUPLICATED_NAME(
        "RACK-3", "There is already a rack with that name in this datacenter"), NON_EXISTENT_RACK(
        "RACK-4", "This rack does not exist"), NON_MANAGED_RACK("RACK-5",
        "Machines in this rack cannot be discovered"), NON_UCS_RACK("RACK-6",
        "This rack is not a UCS Rack"), RACK_DUPLICATED_IP("RACK-7",
        "There is already a managed rack with this IP defined"), RACK_CONFIG_ERROR("RACK-8",
        "There is a problem with the details of the UCS Rack"), RACK_CANNOT_REMOVE_VMS("RACK-9",
        "Can not remove this rack because there are some virtual machines deployed on it"),

    // MACHINE
    NON_EXISTENT_MACHINE("MACHINE-0", "The requested machine does not exist"), NOT_ASSIGNED_MACHINE_DATACENTER_RACK(
        "MACHINE-1", "The machine is not assigned to the datacenter or rack"), MACHINE_ANY_DATASTORE_DEFINED(
        "MACHINE-2", "Machine definition should have at least one datastore created and enabled"), MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK(
        "MACHINE-3", "A machine cannot be added to a UCS Rack in this way"), MACHINE_INVALID_VIRTUAL_SWITCH_NAME(
        "MACHINE-4", "Invalid virtual switch name"), INVALID_STATE_CHANGE("MACHINE-5",
        "The requested state change is not valid"), MACHINE_NOT_ACCESIBLE("MACHINE-6",
        "The requested machine could not be contacted"), MACHINE_CANNOT_BE_DELETED(
        "MACHINE-7",
        "Machine cannot be removed because it is managed by the high availability engine. Manually re-enable it to recover managed state."), MACHINE_INVALID_IPMI_CONF(
        "MACHINE-8", "Invalid IPMI configuration."), MACHINE_INVALID_IP_RANGE("MACHINE-9",
        "Invalid ip range"), MACHINE_IQN_MISSING("MACHINE-10",
        "The IQN of the target Physical Machine is not set"),

    HYPERVISOR_EXIST_IP("HYPERVISOR-1",
        "Invalid hypervisor IP. A hypervisor with that IP already exists"), HYPERVISOR_EXIST_SERVICE_IP(
        "HYPERVISOR-2",
        "Invalid hypervisor service IP. A hypervisor with that service IP already exists"), HYPERVISOR_TYPE_MISSING(
        "HYPERVISOR-3", "The Hypervisor technology of the target Hypervisor is not set."),

    // NETWORK
    NETWORK_INVALID_CONFIGURATION("NET-0",
        "Invalid network configuration for the virtual datacenter"), NETWORK_WITHOUT_IPS("NET-8",
        "This network does not have IP addresses"), NETWORK_IP_FROM_BIGGER_THAN_IP_TO("NET-9",
        "Parameter IPFrom is greater than IPTo"), NETWORK_IP_FROM_ERROR("NET-10",
        "Parameter IPFrom is invalid"), NETWORK_IP_TO_ERROR("NET-11", "Parameter IPTo is invalid"),

    // VIRTUAL MACHINE
    VIRTUAL_MACHINE_WITHOUT_HYPERVISOR("VM-0",
        "The virtual machine does not have a hypervisor assigned"), NON_EXISTENT_VIRTUALMACHINE(
        "VM-1", "The requested virtual machine does not exist"), VIRTUAL_MACHINE_ALREADY_IN_PROGRESS(
        "VM-2", "The virtual machine is already in progress"), VIRTUAL_MACHINE_NOT_DEPLOYED("VM-3",
        "The virtual machine is not deployed"), VIRTUAL_MACHINE_STATE_CHANGE_ERROR("VM-4",
        "The virtual machine cannot change to the required state"), VIRTUAL_MACHINE_REMOTE_SERVICE_ERROR(
        "VM-5", "The virtual machine cannot change state due to a communication problem"), VIRTUAL_MACHINE_PAUSE_UNSUPPORTED(
        "VM-6", "The virtual machine does not support the action PAUSE"), VIRTUAL_MACHINE_INVALID_STATE_DEPLOY(
        "VM-7", "The allowed power states for Virtual Machines is NOT_ALLOCATED"), VIRTUAL_MACHINE_INVALID_STATE_DELETE(
        "VM-8", "The allowed power states for Virtual Machines are UNKNOWN and NOT_ALLOCATED"), NON_EXISTENT_VIRTUAL_IMAGE(
        "VM-9", "The requested Virtual Image does not exists"), VIRTUAL_MACHINE_EDIT_STATE("VM-10",
        "The Virtual Machine is in a state that does not allow the request, therefore can't be modified"), VIRTUAL_MACHINE_UNALLOCATED_STATE(
        "VM-11",
        "The Virtual Machine is not in any Hypervisor. Therefore the change of the state cannot be applied"), VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY(
        "VM-12", "The allowed power states for Virtual Machines is ON, OFF, PAUSED  or ALLOCATED"), VIRTUAL_MACHINE_INCOHERENT_STATE(
        "VM-13",
        "Virtual Machine configuration actions can only be performed when the Virtual Machine is NOT-DEPLOYED"), VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED(
        "VM-14",
        "Only the 'used' attribute of the Virtual Machine Network Configuration can be changed"), VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION(
        "VM-15", "It should be at least one 'used' configuration in each Virtual Machine"), VIRTUAL_MACHINE_IMAGE_NOT_IN_DATACENTER(
        "VM-16", "The provided virtual images isn't available in the virtual appliance datacenter"), VIRTUAL_MACHINE_IMAGE_NOT_ALLOWED(
        "VM-17", "The provided virtual image can not be used in the current enterprise"), VIRTUAL_MACHINE_IMAGE_NOT_COMPATIBLE(
        "VM-18", "The virtual image is not compatible and there isn't any compatible conversion"), VIRTUAL_MACHINE_IMAGE_NOT_READY(
        "VM-19",
        "The virtual image have some compatible conversion but aren't ready (in progress or failed)"), VIRTUAL_MACHINE_MUST_BE_NON_MANAGED(
        "VM-20", "To perform this action, the virtual machine must be in NON_MANAGED state"), NODE_VIRTUAL_MACHINE_IMAGE_NOT_EXISTS(
        "VM-21", "The node virtual image does not exist"), VIRTUAL_MACHINE_ESXI_INCOMPATIBLE_DISK_CONTROLLER(
        "VM-22", "ESXi hosts can't deploy an VMDK sparse using SCSI disk controller"),

    // ROLE
    NON_EXISTENT_ROLE("ROLE-0", "The requested role does not exist"), NON_MODIFICABLE_ROLE(
        "ROLE-1", "The requested role cannot be modified"), PRIVILEGE_PARAM_NOT_FOUND("ROLE-2",
        "Missing privilege parameter"), DELETE_ERROR("ROLE-3",
        "The requested role is blocked. It cannot be deleted"), DELETE_ERROR_WITH_USER("ROLE-4",
        "Cannot delete a role with user associated"), DELETE_ERROR_WITH_ROLE_LDAP("ROLE-5",
        "Cannot delete a role with RoleLdap associated"), DUPLICATED_ROLE_NAME_ENT("ROLE-6",
        "Cannot create a role with the same name as an existing role for the same enterprise"), DUPLICATED_ROLE_NAME_GEN(
        "ROLE-7", "Cannot create a global role with the same name as an existing global role"), HAS_NOT_ENOUGH_PRIVILEGE(
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
        "USER-4", "Duplicate username for user"), EMAIL_IS_INVALID("USER-5",
        "The email isn't valid"), NOT_USER_CREACION_LDAP_MODE("USER-6",
        "In LDAP mode cannot create user"), NOT_EDIT_USER_ROLE_LDAP_MODE("USER-7",
        "In LDAP mode cannot modify user's role"), NOT_EDIT_USER_ENTERPRISE_LDAP_MODE("USER-8",
        "In LDAP mode cannot modify user's enterprise"), USER_DELETING_HIMSELF("USER 9",
        "The user cannot delete his own user account"),

    // REMOTE SERVICE
    NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER("RS-0",
        "The remote service is not assigned to the datacenter"), WRONG_REMOTE_SERVICE_TYPE("RS-1",
        "Wrong remote service"), NON_EXISTENT_REMOTE_SERVICE_TYPE("RS-2",
        "The remote service does not exist"), REMOTE_SERVICE_URL_ALREADY_EXISTS("RS-3",
        "The remote service's URL already exists and cannot be duplicated"), REMOTE_SERVICE_MALFORMED_URL(
        "RS-4", "The remote service's URL is not well formed"), REMOTE_SERVICE_POOL_ASIGNED("RS-5",
        "This datacenter already has a storage pool assigned"), REMOTE_SERVICE_TYPE_EXISTS("RS-6",
        "This datacenter already has a remote service of that type"), REMOTE_SERVICE_CONNECTION_FAILED(
        "RS-7", "Failed connection to the remote service"), APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED(
        "AM-0",
        "The repository exported by the current appliance manager is being used in another datacenter"), APPLIANCE_MANAGER_REPOSITORY_IN_USE(
        "AM-1",
        "The current repository holds virtual images being used on some virtual appliances, so it is not possible to remove this remote service. You can modify the appliance manager but only if the same repository is used."), REMOTE_SERVICE_STORAGE_REMOTE_WITH_POOLS(
        "RS-8", "Cannot delete a storage manager with associated storage pools"), REMOTE_SERVICE_IS_BEING_USED(
        "RS-9",
        "Cannot delete a Virtual System Monitor or DHCP Service. There are virtual machines deployed."), REMOTE_SERVICE_WRONG_URL(
        "RS-10", "Provided URL is not valid"), REMOTE_SERVICE_DHCP_WRONG_URI("RS-11",
        "The DHCP uri is invalid"), REMOTE_SERVICE_DATACENTER_UUID_NOT_FOUND("RS-12",
        "The remote service haven't the *abiquo.datacenter.id* property set"), REMOTE_SERVICE_DATACENTER_UUID_INCONSISTENT(
        "RS-13",
        "The remote service is configured with a different datacenter UUID, please adjust the *abiquo.datacenter.id* property in the remote service."),

    // OVF PACKAGE LIST
    OVF_PACKAGE_LIST_NAME_ALREADY_EXIST("OVF-PACKAGE-LIST-0", "OVF Package list name already exist"),

    // OVF PACKAGE
    NON_EXISTENT_OVF_PACKAGE("OVF-PACKAGE-0", "The requested OVF package does not exist"), NON_EXISTENT_OVF_PACKAGE_LIST(
        "OVF-PACKAGE-1", "The requested OVF package list does not exist"), OVF_PACKAGE_CANNOT_TRANSFORM(
        "OVF-PACKAGE-2", "Cannot return the OVFPackage"), INVALID_OVF_INDEX_XML("OVF-PACKAGE-3",
        "Can not find the RepositorySpace"), NON_EXISTENT_REPOSITORY_SPACE("OVF-PACKAGE-4",
        "The requested RepositorySpace does not exist"), INVALID_DISK_FORMAT_TYPE("OVF-PACKAGE-5",
        "Invalid Disk format type URL"),
    // VIRTUAL IMAGE
    VIMAGE_INVALID_ALLOCATION_UNITS("VIMAGE-INVALID-OVF-ALLOCATION-INITS",
        "Virtual image can not be added due invalid allocation units"), VIMAGE_SYNCH_DC_REPO(
        "VIMAGE-SYNCH-DATACENTER-REPOSITORY", "Can't obtain downloaded OVF in the datacenter."), VIMAGE_DATACENTER_REPOSITORY_NOT_FOUND(
        "DATACENTER-REPOSITORY-NOT-CREATED",
        "Datacenter haven't the ApplianceManager properly configured. Repository not created."), VIMAGE_REPOSITORY_CHANGED(
        "VIMAGE-REPOSITORY-CHANGED", "Datacenter repository changes its repository location"), VIMAGE_AM_DOWN(
        "VIMAGE-AM-DOWN", "Check Appliance Manager configuration error"), NON_EXISTENT_VIRTUALIMAGE(
        "VIMAGE-0", "The requested virtual image does not exist"), VIMAGE_IS_NOT_BUNDLE("VIMAGE-1",
        "Provided virtual image is not a bundle"), INVALID_VIMAGE_LINK("VIMAGE-2",
        "Invalid Virtual Image identifier in the Virtual Image link"), INVALID_DATACENTER_RESPOSITORY_LINK(
        "VIMAGE-3", "Invalid Datacenter Repository identifier in the Datacenter Repository link"), VIMAGE_ENTERPRISE_CANNOT_BE_CHANGED(
        "VIMAGE-4", "Change in Enterprise of the Virtual Image is not allowed"), VIMAGE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED(
        "VIMAGE-5", "Change in Datacenter Repository of a Virtual Image is not allowed"), VIMAGE_MASTER_IMAGE_CANNOT_BE_CHANGED(
        "VIMAGE-6", "Master Image of a Virtual Image cannot be changed"), VIMAGE_MASTER_IMAGE_CANNOT_BE_DELETED(
        "VIMAGE-7",
        "The requested virtual image is a master image, master images cannot be deleted"), VIMAGE_STATEFUL_IMAGE_CANNOT_BE_DELETED(
        "VIMAGE-8", "Cannot delete a stateful image"), VIMAGE_SHARED_IMAGE_FROM_OTHER_ENTERPRISE(
        "VIMAGE-9",
        "Cannot delete the requested shared virtual image, because it belongs to another enterprise"),

    // NODE COLLECTOR
    NON_EXISTENT_IP("NC-0", "The requested IP does not exist"), MISSING_IP_PARAMETER("NC-1",
        "Missing IP query parameter"), NC_BAD_CREDENTIALS_TO_RACK("NC-2",
        "Bad credentials attempting to retrieve the list of physical machines from rack "), NC_BAD_CREDENTIALS_TO_MACHINE(
        "NC-3", "Bad credentials attempting to retrieve the machine "), NC_CONNECTION_EXCEPTION(
        "NC-4", "There is a machine running at the given IP but no hypervisor is responding"), NC_NOT_FOUND_EXCEPTION(
        "NC-5", "There is no machine running at the given IP"), NC_UNEXPECTED_EXCEPTION("NC-6",
        "Unexpected exception building the request to discovery manager"), NC_UNAVAILABLE_EXCEPTION(
        "NC-7", "The discovery manager is currently not available"), NC_VIRTUAL_MACHINE_NOT_FOUND(
        "NC-8", "The requested virtual machine not found in the remote hypervisor"),

    // STORAGE POOL
    MISSING_REQUIRED_QUERY_PARAMETER_IQN("SP-1", "Missing IQN query parameter"), CONFLICT_STORAGE_POOL(
        "SP-2", "The id of the storage pool and the id of the submitted object must be the same"), NON_EXISTENT_STORAGE_POOL(
        "SP-3", "The requested storage pool does not exist"), STORAGE_POOL_ERROR_MODIFYING("SP-4",
        "There was an unexpected error while modifying the storage pool"), STORAGE_POOLS_SYNC(
        "SP-5", "Could not get the storage pools from the target device"), STORAGE_POOL_SYNC(
        "SP-6", "Could not get the requested storage pool from the target device"), CONFLICT_VOLUMES_CREATED(
        "SP-7", "Cannot edit or delete the storage pool. There are existing volumes "), STORAGE_POOL_DUPLICATED(
        "SP-8", "Duplicate storage pool"), STORAGE_POOL_TIER_IS_DISABLED("SP-9", "Tier is disabled"), STORAGE_POOL_PARAM_NOT_FOUND(
        "SP-10", "Missing storage pool parameter"), STORAGE_POOL_LINK_DATACENTER_PARAM_NOT_FOUND(
        "SP-11", "Datacenter parameter in storage pool link not found"), STORAGE_POOL_LINK_DEVICE_PARAM_NOT_FOUND(
        "SP-12", "Storage device parameter in storage pool link not found"), MISSING_POOL_LINK(
        "SP-13", "Missing storage pool link"),

    // DATASTORE
    DATASTORE_NON_EXISTENT("DATASTORE-0", "The requested datastore does not exist"), DATASTORE_DUPLICATED_NAME(
        "DATASTORE-4", "Duplicate name for the datastore"), DATASTORE_DUPLICATED_DIRECTORY(
        "DATASTORE-6", "Duplicate directory path for the datastore"), DATASTORE_NOT_ASSIGNED_TO_MACHINE(
        "DATASTORE-7", "The datastore is not assigned to that machine"),

    // SYSTEM PROPERTIES
    NON_EXISTENT_SYSTEM_PROPERTY("SYSPROP-0", "The requested system property does not exist"), SYSTEM_PROPERTIES_DUPLICATED_NAME(
        "SYSPROP-1", "There is already a system property with that name"),

    // ALLOCATOR
    LIMITS_EXCEEDED("LIMIT-0", "The required resources exceed the allowed limits"), LIMIT_EXCEEDED(
        "LIMIT-1", "The required resources exceed the allowed limits"), NOT_ENOUGH_RESOURCES(
        "ALLOC-0", "There are not enough resources to create the virtual machine"), //
    ALLOCATOR_ERROR("ALLOC-1", "Cannot create virtual machine"), //

    CHECK_EDIT_NO_TARGET_MACHINE("EDIT-01",
        "This method requires the virtual machine to be deployed on a target hypervisor"),

    // VIRTUAL SYSTEM MONITOR

    MONITOR_PROBLEM("VSM-0", "An error occurred when monitoring the physical machine"), UNMONITOR_PROBLEM(
        "VSM-1", "An error occurred when shutting down the monitored physical machine"),

    // LICENSE
    LICENSE_UNEXISTING("LICENSE-0", "The requested license does not exist"), LICENSE_INVALID(
        "LICENSE-1", "The license provided is not valid"), LICENSE_CIHPER_INIT_ERROR("LICENSE-2",
        "Could not initialize licensing ciphers"), LICENSE_CIHPER_KEY("LICENSE-3",
        "Could not read licensing cipher key"), LICENSE_OVERFLOW("LICENSE-4",
        "The maximum number of managed cores has been reached"), LICENSE_DUPLICATED("LICENSE-5",
        "The license is already being used"),

    // TIERS
    NON_EXISTENT_TIER("TIER-0", "The requested tier does not exist"), NULL_TIER("TIER-1",
        "Embedded tier of the StoragePool cannot be null"), MISSING_TIER_LINK("TIER-2",
        "Missing link to the tier"), TIER_PARAM_NOT_FOUND("TIER-3", "Missing tiers parameter"), TIER_LINK_DATACENTER_PARAM_NOT_FOUND(
        "TIER-4", "Datacenter parameter in tier link not found"), TIER_LINK_DATACENTER_DIFFERENT(
        "TIER-5",
        "Tier's datacenter does not belong to the same datacenter where you want to create the StoragePool"), TIER_CONFLICT_DISABLING_TIER(
        "TIER-6", "Cannot disable a tier with associated storage pools"),

    // DEVICES
    NON_EXISTENT_DEVICE("DEVICE-0", "The requested device does not exist"), DEVICE_DUPLICATED(
        "DEVICE-1", "Duplicated Storage Device"),

    // STATISTICS
    NON_EXISTENT_STATS("STATS-0", "Non-existent statistical data"), NON_EXISTENT_STATS_FOR_DATACENTER(
        "STATS-1", "Non-existent statistical data for the requested datacenter"), NON_EXISTENT_STATS_FOR_DCLIMITS(
        "STATS-2", "Non-existent statistical data for the requested enterprise in this datacenter"), NON_EXISTENT_STATS_FOR_ENTERPRISE(
        "STATS-3", "Non-existent statistical data for the requested enterprise"), NODECOLLECTOR_ERROR(
        "NODECOLLECTOR-1", "Nodecollector has raised an error"),

    // QUERY PAGGING STANDARD ERRORS
    QUERY_INVALID_PARAMETER("QUERY-0", "Invalid 'by' parameter"), QUERY_NETWORK_TYPE_INVALID_PARAMETER(
        "QUERY-1", "Invalid 'type' parameter. Only 'EXTERNAL' or 'PUBLIC' allowed"),

    VOLUME_GENERIC_ERROR("VOL-0", "Could not create the volume in the selected tier"), VOLUME_NOT_ENOUGH_RESOURCES(
        "VOL-1", "There are not enough resources in the selected tier to create the volume"), VOLUME_NAME_NOT_FOUND(
        "VOL-2", "The name of the volume is required"), NON_EXISTENT_VOLUME("VOL-3",
        "The volume does not exist"), VOLUME_CREATE_ERROR("VOL-4",
        "An unexpected error occurred while creating the volume"), VOLUME_MOUNTED_OR_RESERVED(
        "VOL-5", "The volume cannot be deleted because it is associated with a virtual machine"), VOLUME_SSM_DELETE_ERROR(
        "VOL-6", "Could not physically delete the volume from the target storage device"), VOLUME_DELETE_STATEFUL(
        "VOL-7",
        "The volume cannot be deleted because it is being used in a persistent image process"), VOLUME_DELETE_IN_VIRTUALAPPLIANCE(
        "VOL-8",
        "The persistent volume cannot be deleted because it is being used in a virtual appliance"), VOLUME_ISCSI_NOT_FOUND(
        "VOL-9", "The idScsi of the volume is required"), VOLUME_DECREASE_SIZE_LIMIT_ERROR(
        "VOL-10", "The size of the volume cannot be reduced"), VOLUME_NAME_LENGTH_ERROR("VOL-11",
        "The size of the 'name' field of the volume cannot exceed 256 characters"), VOLUME_ISCSI_INVALID(
        "VOL-12", "The property idScsi " + IscsiPath.ERROR_MESSAGE), VOLUME_SIZE_INVALID("VOL-13",
        "The size property must be a non-zero integer up to " + Rasd.LIMIT_MAX), VOLUME_IN_USE(
        "VOL-14", "The volume cannot be edited because it is being used in a virtual machine"), VOLUME_UPDATE(
        "VOL-15", "An unexpected error occurred and the volume could not be updated"), VOLUME_RESIZE_STATEFUL(
        "VOL-16", "Cannot resize a persistent volume"), VOLUME_RESIZE_GENERIC_ISCSI("VOL-17",
        "Cannot resize a generic Iscsi volume"), SSM_UNREACHABLE("VOL-18",
        "Could not get the Storage Manager remote service"), VOLUME_GRANT_ACCESS_ERROR("VOL-19",
        "Could not add the initiators mappings"), NON_EXISTENT_VOLUME_MAPPING("VOL-20",
        "The requested initiator mapping does not exist"), VOLUME_NOT_ATTACHED("VOL-21",
        "The volume is not attached to the virtual machine"), VOLUME_ATTACH_INVALID_LINK("VOL-22",
        "Invalid link to the volume to attach"), VOLUME_ATTACH_INVALID_VDC_LINK("VOL-23",
        "Invalid virtual datacenter in the link to the volume to attach"), VOLUME_ALREADY_ATTACHED(
        "VOL-24", "The volume is already attached to a virtual machine"), VOLUME_TOO_MUCH_ATTACHMENTS(
        "VOL-25", "The maximum number of attached disks and volumes has been reached"), VOLUME_ATTACH_ERROR(
        "VOL-26",
        "An unexpected error occured while attaching the volume. Please, contact the administrator"), VOLUME_ALREADY_DETACHED(
        "VOL-27", "The volume is already detached"), VOLUME_DETACH_ERROR("VOL-28",
        "An unexpected error occured while detaching the volume. Please, contact the administrator"), VOLUME_RECONFIGURE_ERROR(
        "VOL-29", "An unexpected error occured while reconfiguring storage"),

    // RULES
    NON_EXISTENT_EER("RULE-1", "The requested restrict shared server rule does not exist"), NON_EXISTENT_FPR(
        "RULE-2", "The requested load balance rule does not exist"), NON_EXISTENT_MLR("RULE-3",
        "The requested load level rule does not exist"), ONE_FPR_REQUIRED("RULE-4",
        "At least one load balance rule is required"), ONE_LINK_REQUIRED("RULE-5",
        "Expected one link with the rel attribute possible values (datacenter/rack/machine)"), INVALID_FPR(
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

    // Parsing links
    LINKS_INVALID_LINK("LNK-0", "Invalid link. Check out documentation"), LINKS_ONLY_ACCEPTS_ONE_LINK(
        "LNK-1", "Number of links invalid: This resource only accepts a single link"),

    // CATEGORY
    NON_EXISTENT_CATEGORY("CATEGORY-1", "The requested category does not exist"), CATEGORY_DUPLICATED_NAME(
        "CATEGORY-2", "Duplicated name for the category"), CATEGORY_NOT_ERASABLE("CATEGORY-3",
        "This category is not erasable"), INVALID_CATEGORY_LINK("CATEGORY-4",
        "Invalid Category identifier in the Category link"), CATEGORY_CANNOT_BE_NULL("CATEGORY-5",
        "Category name cannot be null"),

    // ICONS
    ICON_DUPLICATED_PATH("ICON-1", "Duplicated path for an icon"), NON_EXISTENT_ICON("ICON-2",
        "The requested icon does not exist"), NON_EXISENT_ICON_WITH_PATH("ICON-3",
        "No icon found with the requested path"), ICON_IN_USE_BY_VIRTUAL_IMAGES("ICON-4",
        "Cannot delete the icon because it is in use by some virtual image"), INVALID_ICON_LINK(
        "ICON-5", "Invalid Icon identifier in the Icon link"),

    // TASKS
    NON_EXISTENT_TASK("TASK-1", "The requested task does not exist"), TASK_OWNER_NOT_FOUND(
        "TASK-2", ""),

    // PRICING TEMPLATE
    CURRENCY_PARAM_NOT_FOUND("PRICINGTEMPLATE-0", "Missing currency parameter"), ENT_PARAM_NOT_FOUND(
        "PRICINGTEMPLATE-1", "Missing enterprise parameter"), PRICING_TEMPLATE_DUPLICATED_NAME(
        "PRICINGTEMPLATE-2", "Duplicated name for Pricing Template"), NON_EXISTENT_PRICING_TEMPLATE(
        "PRICINGTEMPLATE-3", "The requested Pricing Template does not exist"), DELETE_ERROR_WITH_ENTERPRISE(
        "PRICINGTEMPLATE-4", "Cannot delete a Pricing Template with associated Enterprise"), PRICING_TEMPLATE_MINIMUM_CHARGE_PERIOD(
        "PRICINGTEMPLATE-5", "The smallest charging period is for DAY"), PRICING_TEMPLATE_EMPTY_NAME(
        "PRICINGTEMPLATE-6", "Pricing Template name can't be empty"), MISSING_CURRENCY_LINK(
        "PRICINGTEMPLATE-7", "Missing link to the currency"), CHARGING_PERIOD_VALUES(
        "PRICINGTEMPLATE-8", "Charging period values should be between 0 and 6"),

    // CURRENCY
    NON_EXISTENT_CURRENCY("CURRENCY-0", "The requested Currency does not exist"), ONE_CURRENCY_REQUIRED(
        "CURRENCY-1", "At least one currency is required"), CURRENCY_DUPLICATED_NAME("CURRENCY-2",
        "Duplicated name for Currency"), CURRENCY_DELETE_ERROR("CURRENCY-3",
        "Cannot remove currency associated with a Pricing Model"), CURRENCY_NAME_NOT_FOUND(
        "CURRENCY-4", "Currency name is required"), CURRENCY_SYMBOL_NOT_FOUND("CURRENCY-5",
        "Currency symbol is required"), CURRENCY_NAME_LONG("CURRENCY-6",
        "Currency name maximum lenght is 20 characters"), CURRENCY_SYMBOL_LONG("CURRENCY-7",
        "Currency symbol maximum lenght is 10 characters"), CURRENCY_DIGIT_LONG("CURRENCY-8",
        "Currency digit maximum value is 9"),

    // COST CODE
    NON_EXISTENT_COSTCODE("COSTCODE-0", "The requested Cost Code does not exist"), COSTCODE_PARAM_NOT_FOUND(
        "COSTCODE-1", "Missing  Cost Code parameter"), COSTCODE_DUPLICATED_NAME("COSTCODE-2",
        "Duplicated name for Cost Code"), COSTCODE_NAME_NOT_FOUND("COSTCODE-3",
        "Cost Code name is required"), COSTCODE_DESCRITPION_NOT_FOUND("COSTCODE-4",
        "Cost Code description is required"), COSTCODE_NAME_LONG("COSTCODE-5",
        "Cost Code name maximum lenght is 20 characters"), COSTCODE_DESCRIPTION_LONG("COSTCODE-6",
        "Cost Code description maximum lenght is 100 characters"),

    // COST CODE- CURRENCY
    COSTCODE_CURRENCY_DUPLICATED("COSTCODE_CURRENCY-0",
        "Duplicated value by Cost Code and Currency"), NON_EXISTENT_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-1", "The requested Cost Code -Currency does not exist"), NOT_ASSIGNED_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-2", "The Cost Code -Currency is not assigned to the Cost Code"), NOT_ASSIGNED_COSTCODE_CURRENCY_PRICE(
        "COSTCODE_CURRENCY-3", "Price is required"),

    // PRICING - COST CODE
    PRICING_COSTCODE_DUPLICATED("PRICING_COSTCODE-0",
        "Duplicated value by Cost Code and PricingTemplate"), NON_EXISTENT_PRICING_COSTCODE(
        "PRICING_COSTCODE-1", "The requested Cost Code -PricingTemplate does not exist"),

    // PRICING - TIER
    PRICING_TIER_DUPLICATED("PRICING_TIER-0", "Duplicated value by Tier and PricingTemplate"), NON_EXISTENT_PRICING_TIER(
        "PRICING_TIER-1", "The requested Tier-PricingTemplate does not exist"), PRICING_TIER_WRONG_RELATION(
        "PRICING_TIER-2",
        "The pricing tier doesn't have any relation with the pricing model indicated"), PRICING_TIER_DATACENTER(
        "PRICING_TIER-3", "This tier is not related to the datacenter indicated"),

    // HYPERVISOR TYPE
    NON_EXISTENT_HYPERVISOR_TYPE("HYPERVISOR_TYPE-0",
        "The requested Hypervisor Type does not exist")

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
