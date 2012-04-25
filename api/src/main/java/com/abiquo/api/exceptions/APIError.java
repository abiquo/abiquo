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
        "409-CONFLICT", "Conflict"), STATUS_HEADER_VERSION_MANDATORY("412-PRECONDITION FAILED",
        "Header 'X-abiquo-version' is mandatory"), STATUS_NOT_ACCEPTABLE_VERSION(
        "406-NOT ACCEPTABLE", "Invalid version parameter for 'Accept' header"), STATUS_UNSUPPORTED_MEDIA_TYPE_VERSION(
        "415-UNSUPPORTED MEDIA TYPE", "Invalid 'Content-type' version"), STATUS_UNSUPPORTED_MEDIA_TYPE(
        "415-UNSUPPORTED MEDIA TYPE",
        "The Abiquo API currently only supports application/XML Media Type"), STATUS_INTERNAL_SERVER_ERROR(
        "500-INTERNAL SERVER ERROR", "Unexpected exception"), STATUS_UNPROVISIONED(
        "412 - Unprovisioned", "Error releasing resources on the hypervisor"), SERVICE_UNAVAILABLE_ERROR(
        "503- Service Unavailable", "Service unavailable: try again in a few moments"),

    // GENERIC
    MALFORMED_URI("GEN-0", "Malformed URI"), INVALID_ID("GEN-1", "Identifier cannot be 0"), CONSTRAINT_VIOLATION(
        "GEN-2", "Invalid XML document; please make sure all mandatory fields are correct"), UNMARSHAL_EXCEPTION(
        "GEN-3", "Invalid XML document"), FORBIDDEN("GEN-4",
        "Not enough permissions to perform this action"), INVALID_CREDENTIALS("GEN-5",
        "Invalid credentials"), INVALID_LINK("GEN-6", "Invalid link reference"), WHITE_NAME(
        "GEN-7", "The property 'name' must not have whitespace at the beginning or the end"), WHITE_SYMBOL(
        "GEN-8", "The property 'symbol' must not have whitespace at the beginning or the end"), WHITE_DESCRIPTION(
        "GEN-9", "The property 'description' must not have whitespace at the beginning or the end"), REQUIRED_ID(
        "GEN-10", "Identifier is required"),

    // INVALID_IP("GEN-4", "Invalid IP"),
    INVALID_PRIVATE_NETWORK_TYPE("GEN-6", "Invalid private network type"), INTERNAL_SERVER_ERROR(
        "GEN-7", "Unexpected error"), GENERIC_OPERATION_ERROR("GEN-8",
        "The operation could not be performed. Please contact the Administrator"), NOT_ENOUGH_PRIVILEGES(
        "GEN-9", "Not enough privileges to perform this operation"), INCOHERENT_IDS("GEN-10",
        "The parameter ID is different from the Entity ID"),

    // DATACENTER
    NON_EXISTENT_DATACENTER("DC-0", "The requested datacenter does not exist"), DATACENTER_DUPLICATED_NAME(
        "DC-3", "There is already a datacenter with that name"), DATACENTER_NOT_ALLOWED("DC-4",
        "The current enterprise cannot use this datacenter"), DATACENTER_DELETE_STORAGE("DC-5",
        "Cannot delete datacenter with storage devices associated"), DATACENTER_DELETE_VIRTUAL_DATACENTERS(
        "DC-6", "Cannot delete datacenter with virtual datacenters associated"), DATACENTER_QUEUE_NOT_CONFIGURED(
        "DC-7",
        "Datacenter queues are not configured (check the BPM and virtual factory remote services)"),

    // ENTERPRISE
    NON_EXISTENT_ENTERPRISE("EN-0", "The requested enterprise does not exist"), ENTERPRISE_DUPLICATED_NAME(
        "ENTERPRISE-4", "Duplicate name for an enterprise"), ENTERPRISE_DELETE_ERROR_WITH_VDCS(
        "ENTERPRISE-5", "Cannot delete enterprise with virtual datacenters associated"), ENTERPRISE_DELETE_OWN_ENTERPRISE(
        "ENTERPRISE-6", "Cannot delete the current user's enterprise"), ENTERPRISE_EMPTY_NAME(
        "ENTERPRISE-7", "Enterprise name cannot be empty"), MISSING_ENTERPRISE_LINK("ENTERPRISE-8",
        "Missing enterprise link"), ENTERPRISE_WITH_BLOCKED_USER(
        "ENTERPRISE-9",
        "Cannot delete enterprise because some users have roles that cannot be deleted, please change their enterprise before continuing"), ENTERPRISE_NOT_ALLOWED_DATACENTER(
        "ENTERPRISE-10", "The Enterprise does not have permission to use the requested datacenter"), INVALID_ENTERPRISE_LINK(
        "ENTERPRISE-11", "Invalid Enterprise identifier in the Enterprise link"), MISSING_PRICING_TEMPLATE_LINK(
        "ENTERPRISE-12", "Missing link to the pricing template"), PRICING_TEMPLATE_PARAM_NOT_FOUND(
        "ENTERPRISE-13", "Missing pricing template parameter"), NON_EXISTENT_ENTERPRISE_PROPS(
        "EN-0", "The requested enterprise properties do not exist"),

    // LIMITS: Common for Enterprise and virtual datacenter
    LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC(
        "LIMIT-6",
        "Invalid VLAN hard limit; this cannot be greater than the number of VLANS per virtual datacenter: {0}"), LIMITS_DUPLICATED(
        "LIMIT-7", "Duplicate limits by enterprise and datacenter"), LIMITS_NOT_EXIST("LIMIT-8",
        "Limits by enterprise and datacenter do not exist"), //
    ENTERPRISE_LIMIT_EDIT_ARE_SURPASSED("LIMIT-9",
        "Cannot edit resource limits; current enterprise allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), //
    DATACENTER_LIMIT_EDIT_ARE_SURPASSED(
        "LIMIT-10",
        "Cannot edit resource limits; current enterprise and datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), DATACENTER_LIMIT_DELETE_VDCS(
        "LIMIT-11",
        "Cannot unassign datacenter from enterprise because it is being used by virtual datacenter(s)."),

    // VIRTUAL DATACENTER
    NON_EXISTENT_VIRTUAL_DATACENTER("VDC-0", "The requested virtual datacenter does not exist"), VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE(
        "VDC-1", "Invalid hypervisor type for this virtual datacenter"), VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES(
        "VDC-2",
        "This virtual datacenter contains virtual appliances and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_CONTAINS_RESOURCES(
        "VDC-3",
        "This virtual datacenter has volumes attached and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_INVALID_NETWORKS(
        "VDC-4", "This virtual datacenter has networks without IP addresses"), VIRTUAL_DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
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
        "Attributes 'address', 'mask' and 'tag' cannot be changed when editing a private VLAN."), VLANS_DEFAULT_NETWORK_CAN_NOT_BE_DELETED(
        "VLAN-11", "The VLAN cannot be deleted because it is the Default VLAN of this Enterprise"), VLANS_WITH_USED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-12", "Cannot delete a VLAN with IPs used by Virtual Machines"), VLANS_TAG_MANDATORY_FOR_PUBLIC_VLANS(
        "VLAN-13", "Field 'tag' is mandatory when you create Public VLANs"), VLANS_WITH_PURCHASED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-14", "Cannot delete a VLAN with IPs purchased by Enterprises"), VLANS_DUPLICATED_VLAN_NAME_DC(
        "VLAN-15", "Cannot create two VLANs with the same name in a Datacenter"), VLANS_TAG_INVALID(
        "VLAN-16", "VLAN tag out of limits"), VLANS_NON_EXISTENT_PUBLIC_IP("VLAN-17",
        "The requested IP object does not exist"), VLANS_IP_EDIT_INVALID_VALUES("VLAN-18",
        "Only 'quarantine' and 'available' attributes can be modified when editing an IP"), VLANS_PUBLIC_EDIT_INVALID_VALUES(
        "VLAN-19",
        "Attributes 'address' and 'mask' cannot be changed when editing a Public, External or Unmanaged Network."), VLANS_PUBLIC_IP_NOT_TO_BE_PURCHASED(
        "VLAN-20", "The IP does not exist or is not available"), VLANS_PUBLIC_IP_NOT_PURCHASED(
        "VLAN-21", "The IP does not exist or is not purchased"), VLANS_PUBLIC_IP_BUSY("VLAN-22",
        "This IP address is currently used by a Virtual Machine. It cannot be released"), VLANS_PRIVATE_IP_INVALID_LINK(
        "VLAN-23", "Invalid link to private IP address to create NIC"), VLANS_IP_LINK_INVALID_VDC(
        "VLAN-24", "Invalid Virtual Datacenter identifier in the IP link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE(
        "VLAN-25", "The IP address is already used by another virtual machine"), VLANS_PUBLIC_IP_INVALID_LINK(
        "VLAN-26", "Invalid link to public IP address to create NIC"), VLANS_IP_CAN_NOT_BE_DEASSIGNED_DUE_CONFIGURATION(
        "VLAN-27",
        "Cannot release this IP from the virtual machine because the configured default gateway is in the same subnet. "
            + "Please choose a different gateway before removing this IP"), VLANS_NIC_NOT_FOUND(
        "VLAN-28", "The NIC does not exist"), VLANS_CAN_NOT_DETACH_LAST_NIC("VLAN-29",
        "Every virtual machine should have at least one NIC"), VLANS_REORDER_NIC_INVALID_LINK(
        "VLAN-30", "Invalid link to reorder NICs on a Virtual Machine"), VLANS_REORDER_NIC_INVALID_LINK_VALUES(
        "VLAN-31",
        "Invalid link values (virtualdatacenter, virtualappliance and/or virtualmachine identifiers) for reordering NICs on a Virtual Machine"), VLANS_IP_EDIT_NOT_AVAILABLE_PURCHASED(
        "VLAN-32", "Cannot set the IP as 'not available' while it is purchased by an Enterprise"), VLANS_PUBIC_IP_CAN_NOT_RELEASE(
        "VLAN-33", "Cannot release a Public IP while it is assigned to a Virtual Machine"), VLANS_NON_EXISTENT_CONFIGURATION(
        "VLAN-34", "The configuration does not exist"), VLANS_CAN_NOT_ASSIGN_TO_DEFAULT_ENTERPRISE(
        "VLAN-35",
        "Cannot assign the external VLAN as default because it is not assigned to any enterprise"), VLANS_VIRTUAL_DATACENTER_SHOULD_HAVE_A_DEFAULT_VLAN(
        "VLAN-36",
        "Unable to find default VLAN in Virtual Datacenter. Inconsistent state in Database"), VLANS_INVALID_ENTERPRISE_LINK(
        "VLAN-37", "Invalid Enterprise identifier in the Enterprise link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_DATACENTER(
        "VLAN-38", "The IP address is already assigned to a Virtual Datacenter"), VLANS_WITH_IPS_ASSIGNED_TO_VDC(
        "VLAN-39", "Cannot delete a VLAN with IPs assigned to a Virtual Datacenter"), VLANS_EXTERNAL_VLAN_IN_ANOTHER_DATACENTER(
        "VLAN-40",
        "The requested external VLAN belongs to a different datacenter, not the one where the Virtual Datacenter is"), VLANS_INVALID_IP_FORMAT(
        "VLAN-41", "IP format is invalid"), VLANS_IP_DOES_NOT_EXISTS("VLAN-42",
        "The IP does not exist"), VLANS_CANNOT_DELETE_DEFAULT("VLAN-43",
        "This is the default VLAN for the Virtual Datacenter and it cannot be deleted"), VLANS_EXTERNAL_VLAN_OF_ANOTHER_ENTERPRISE(
        "VLAN-42", "The external VLAN belongs to another enterprise"), VLANS_IP_NOT_AVAILABLE(
        "VLAN-43", "The IP address is not available to be used by a Virtual Machine"), VLANS_NON_EXISTENT_EXTERNAL_IP(
        "VLAN-44", "The requested IP object does not exist"), VLANS_ASSIGNED_TO_ANOTHER_VIRTUAL_DATACENTER(
        "VLAN-45",
        "Cannot change enterprise because this network is used as the default by a Virtual Datacenter"), VLANS_NOT_UNMANAGED(
        "VLAN-46", "The virtual network is not Unmanaged "), VLANS_UNMANAGED_WITH_VM_CAN_NOT_BE_DELETED(
        "VLAN-47", "Cannot delete Unmanaged Networks associated with Virtual Machines"), VLANS_MISSING_ENTERPRISE_LINK(
        "VLAN-48", "Enterprise link with rel 'enterprise' is mandatory "), VLANS_IP_IS_IN_QUARANTINE(
        "VLAN-49", "The IP %s is in quarantine"),

    // VIRTUAL APPLIANCE
    NON_EXISTENT_VIRTUALAPPLIANCE("VAPP-0", "The requested virtual appliance does not exist"), VIRTUALAPPLIANCE_NOT_DEPLOYED(
        "VAPP-1", "The virtual appliance is not deployed"), VIRTUALAPPLIANCE_NOT_RUNNING("VAPP-2",
        "The virtual appliance is not running"), VIRTUALAPPLIANCE_DEPLOYED("VAPP-3",
        "The virtual appliance is deployed"), VIRTUALAPPLIANCE_NON_MANAGED_IMAGES("VAPP-4",
        "The virtual appliance has non-managed VM templates"), VIRTUALAPPLIANCE_INVALID_STATE_DELETE(
        "VAPP-5",
        "The virtual appliance cannot be deleted in this state. It should be NOT_DEPLOYED or UNKNOWN"), VIRTUALAPPLIANCE_MOVE_MISSING_VDC(
        "VAPP-6",
        "The virtual appliance cannot be moved because it has no link to its virtual datacenter"), VIRTUALAPPLIANCE_INVALID_STATE_MOVE(
        "VAPP-8", "The virtual appliance cannot be moved in this state. It should be NOT_DEPLOYED"), VIRTUALAPPLIANCE_INVALID_DC_MOVE_COPY(
        "VAPP-7",
        "The virtual appliance cannot be moved or copied because the target virtual datacenter is not in the same datacenter"), VIRTUALAPPLIANCE_MOVE_COPY_CAPTURED_VM(
        "VAPP-9",
        "The virtual appliance cannot be moved or copied because it contains captured virtual machines"), VIRTUALAPPLIANCE_MOVE_COPY_INCOMPATIBLE_VM(
        "VAPP-10",
        "The virtual appliance cannot be moved or copied because it contains virtual machine templates that are not compatible with the target hypervisor"), VIRTUALAPPLIANCE_COPY_PERSISTENT_VM(
        "VAPP-11",
        "The virtual appliance cannot be copied because it contains persistent virtual machine templates"), VIRTUALAPPLIANCE_EMPTY(
        "VAPP-12", "The virtual appliance does not contain any virtual machines"), VIRTUALAPPLIANCE_INVALID_STATE_COPY(
        "VAPP-13",
        "The virtual appliance cannot be copied in this state. It should be NOT_DEPLOYED"),

    // VIRTUAL CONVERSION
    NON_EXISTENT_VIRTUALAPPLIANCE_STATEFULCONVERSION("VASC-0",
        "The requested persistent conversion does not exist"), INVALID_VASC_STATE("VASC-1",
        "Invalid expected state"),

    // NODE VIRTUAL IMAGE STATEFUL CONVERSION
    NON_EXISTENT_NODE_VIRTUALIMAGE_STATEFULCONVERSION("NVISC-0",
        "The requested virtual machine node for the persistent conversion does not exist"),

    // RACK
    NOT_ASSIGNED_RACK_DATACENTER("RACK-0", "The rack is not assigned to the datacenter"), RACK_DUPLICATED_NAME(
        "RACK-3", "There is already a rack with that name in this datacenter"), NON_EXISTENT_RACK(
        "RACK-4", "This rack does not exist"), NON_MANAGED_RACK("RACK-5",
        "Machines in this rack cannot be discovered"), NON_UCS_RACK("RACK-6",
        "This rack is not a UCS Rack"), RACK_DUPLICATED_IP("RACK-7",
        "There is already a managed rack with this IP defined"), RACK_CONFIG_ERROR("RACK-8",
        "There is a problem with the details of the UCS Rack"), RACK_CANNOT_REMOVE_VMS("RACK-9",
        "Cannot remove this rack because there are some virtual machines deployed on it"), RACK_DEFAULT_TEMPLATE_ERROR(
        "RACK-10",
        "This UCS Rack has no default Service Profile Template. You must either select one from the list or add a default Service Profile Template"),

    // MACHINE
    NON_EXISTENT_MACHINE("MACHINE-0", "The requested machine does not exist"), NOT_ASSIGNED_MACHINE_DATACENTER_RACK(
        "MACHINE-1", "The machine is not assigned to the datacenter or rack"), MACHINE_ANY_DATASTORE_DEFINED(
        "MACHINE-2", "Machine definition should have at least one datastore created and enabled"), MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK(
        "MACHINE-3", "A machine cannot be added to a UCS Rack in this way"), MACHINE_INVALID_VIRTUAL_SWITCH_NAME(
        "MACHINE-4", "Invalid virtual switch name"), INVALID_STATE_CHANGE("MACHINE-5",
        "The requested state change is not valid"), MACHINE_NOT_ACCESIBLE("MACHINE-6",
        "The requested machine could not be contacted"), MACHINE_CANNOT_BE_DELETED(
        "MACHINE-7",
        "Machine cannot be removed because it is being managed by the high availability engine. Manually re-enable it to recover managed state."), MACHINE_INVALID_IPMI_CONF(
        "MACHINE-8", "Invalid IPMI configuration"), MACHINE_INVALID_IP_RANGE("MACHINE-9",
        "Invalid IP range"), MACHINE_IQN_MISSING("MACHINE-10",
        "The IQN of the target Physical Machine is not set"), MANAGED_MACHINE_CANNOT_CHANGE_NAME(
        "MACHINE-11", "The Machine is in a managed Rack and its name cannot be changed."), MACHINE_RESERVED_ENTERPRISE(
        "MACHINE-12",
        "The requested machine cannot be reserved because another enterprise has already reserved it."), MACHINE_NOT_RESERVED(
        "MACHINE-13", "The requested machine cannot be released because it is not reserved"), MACHINE_ALREADY_RESERVED(
        "MACHINE-14", "The requested machine is already reserved."),

    HYPERVISOR_EXIST_IP("HYPERVISOR-1",
        "Invalid hypervisor IP. A hypervisor with that IP already exists"), HYPERVISOR_EXIST_SERVICE_IP(
        "HYPERVISOR-2",
        "Invalid hypervisor service IP. A hypervisor with that service IP already exists"), HYPERVISOR_TYPE_MISSING(
        "HYPERVISOR-3", "The Hypervisor type of the target Hypervisor is not set"),

    // NETWORK
    NETWORK_INVALID_CONFIGURATION("NET-0",
        "Invalid network configuration for the virtual datacenter"), NETWORK_WITHOUT_IPS("NET-8",
        "This network has no IP addresses"), NETWORK_IP_FROM_BIGGER_THAN_IP_TO("NET-9",
        "Parameter IPFrom is greater than IPTo"), NETWORK_IP_FROM_ERROR("NET-10",
        "Parameter IPFrom is invalid"), NETWORK_IP_TO_ERROR("NET-11", "Parameter IPTo is invalid"), NETWORK_INVALID_CONFIGURATION_LINK(
        "NET-12", "Invalid link to configure the Virtual Machine's network"), NETWORK_LINK_INVALID_VDC(
        "NET-13", "Invalid Virtual Datacenter identifier in the configuration link"), NETWORK_LINK_INVALID_VAPP(
        "NET-14", "Invalid Virtual Appliance identifier in the configuration link"), NETWORK_LINK_INVALID_VM(
        "NET-15", "Invalid Virtual Machine identifier in the configuration link"), NETWORK_LINK_INVALID_CONFIG(
        "NET-16",
        "Invalid Configuration identifier in the configuration link. Configuration ID does not belong to any VLAN configuration used by this Virtual Machine"),

    // VIRTUAL MACHINE
    VIRTUAL_MACHINE_WITHOUT_HYPERVISOR("VM-0",
        "The virtual machine does not have a hypervisor assigned"), NON_EXISTENT_VIRTUALMACHINE(
        "VM-1", "The requested virtual machine does not exist"), VIRTUAL_MACHINE_ALREADY_IN_PROGRESS(
        "VM-2", "The virtual machine is already locked by another operation"), VIRTUAL_MACHINE_NOT_DEPLOYED(
        "VM-3", "The virtual machine is not deployed"), VIRTUAL_MACHINE_STATE_CHANGE_ERROR("VM-4",
        "The virtual machine cannot change to the required state"), VIRTUAL_MACHINE_REMOTE_SERVICE_ERROR(
        "VM-5", "The virtual machine cannot change state due to a communication problem"), VIRTUAL_MACHINE_PAUSE_UNSUPPORTED(
        "VM-6", "The virtual machine does not support the action PAUSE"), VIRTUAL_MACHINE_INVALID_STATE_DEPLOY(
        "VM-7", "The allowed state for deploying Virtual Machines is NOT_ALLOCATED"), VIRTUAL_MACHINE_INVALID_STATE_DELETE(
        "VM-8", "The allowed states for deleting Virtual Machines are UNKNOWN and NOT_ALLOCATED"), NON_EXISTENT_VIRTUAL_IMAGE(
        "VM-9", "The requested Virtual Machine Template does not exist"), VIRTUAL_MACHINE_EDIT_STATE(
        "VM-10",
        "The Virtual Machine is in a state that does not allow the request, therefore it cannot be modified"), VIRTUAL_MACHINE_UNALLOCATED_STATE(
        "VM-11",
        "The Virtual Machine is not in any Hypervisor. Therefore the change of state cannot be applied"), VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY(
        "VM-12",
        "The allowed power states for Virtual Machine deployment are ON, OFF, PAUSED, UNKNOWN  or ALLOCATED"), VIRTUAL_MACHINE_INCOHERENT_STATE(
        "VM-13",
        "Virtual Machine configuration actions can only be performed when the Virtual Machine is NOT-ALLOCATED or OFF"), VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED(
        "VM-14",
        "Only the 'used' attribute of the Virtual Machine Network Configuration can be changed"), VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION(
        "VM-15",
        "There should be at least one 'used' network configuration in each Virtual Machine"), VIRTUAL_MACHINE_MACHINE_TEMPLATE_NOT_IN_DATACENTER(
        "VM-16",
        "The virtual machine template supplied isn't available in the virtual appliance's datacenter"), VIRTUAL_MACHINE_MACHINE_TEMPLATE_NOT_ALLOWED(
        "VM-17", "The virtual machine template supplied cannot be used in the current enterprise"), VIRTUAL_MACHINE_IMAGE_NOT_COMPATIBLE(
        "VM-18",
        "The virtual machine template is not compatible and there is no compatible conversion"), VIRTUAL_MACHINE_IMAGE_NOT_READY(
        "VM-19",
        "The virtual machine template has a compatible conversion but it is not ready (in progress or failed)"), VIRTUAL_MACHINE_MUST_BE_NON_MANAGED(
        "VM-20", "To perform this action, the virtual machine must be in NON_MANAGED state"), NODE_VIRTUAL_MACHINE_IMAGE_NOT_EXISTS(
        "VM-21", "The virtual machine node does not exist"), VIRTUAL_MACHINE_ESXI_INCOMPATIBLE_DISK_CONTROLLER(
        "VM-22", "ESXi hosts cannot deploy a VMDK sparse using a SCSI disk controller"), VIRTUAL_MACHINE_BACKUP_NOT_FOUND(
        "VM-23",
        "Cannot restore the original virtual machine (after a failed reconfigure); the original virtual machine info was not found"), RESOURCE_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE(
        "VM-24", "The resource is already used by another virtual machine"), VIRTUAL_MACHINE_INVALID_STATE_RESET(
        "VM-26", "The allowed power state for Reset Virtual Machines is ON"), VIRTUAL_MACHINE_INVALID_STATE_SNAPSHOT(
        "VM-27", "The allowed power state for Snapshot Virtual Machines is OFF"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_MANAGED(
        "VM-28", "Cannot reconfigure a non-managed virtual machine template"), VIRTUAL_MACHINE_RECONFIGURE_NOT_MANAGED(
        "VM-29", "Cannot reconfigure the template of a non-managed virtual machine"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER(
        "VM-30",
        "Cannot reconfigure to change the virtual machine template to another master (only instances or persistent)"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_ATTACHED_PRESISTENT(
        "VM-31",
        "The persistent virtual machine template supplied for reconfigure is already attached to a virtual machine"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_IN_THE_HYPERVISOR(
        "VM-32",
        "Cannot reconfigure the virtual machine template when the virtual machine is present in the hypervisor"), VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE(
        "VM-33", "We do not currently allow imported virtual machines to be reconfigured"), VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY(
        "VM-33", "Only 'cpu' and 'ram' can be reconfigured in imported virtual machines"), VIRTUAL_MACHINE_IMPORTED_WILL_BE_DELETED(
        "VM-44",
        "You are trying to undeploy an imported virtual machine. If you undeploy it, the virtual machine template cannot be recovered. If you are confident of this action, please call this functionality again with the 'forceUndeploy=true' option"), RESOURCES_ALREADY_ASSIGNED(
        "VM-45", "Some of the resources indicated are already used"), VIRTUAL_MACHINE_AT_LEAST_ONE_NIC_SHOULD_BE_LINKED(
        "VM-46",
        "At least a link to an IP address should be informed when attaching or changing Virtual Machine NICs"), VIRTUAL_MACHINE_AT_LEAST_ONE_DISK_SHOULD_BE_LINKED(
        "VM-47",
        "At least a link to a Hard Disk should be informed when attaching or changing Virtual Machine Hard Disks"), VIRTUAL_MACHINE_DISK_ALREADY_ATTACHED_TO_THIS_VIRTUALMACHINE(
        "VM-48", "Disk already attached to this virtual machine"),

    // ROLE
    NON_EXISTENT_ROLE("ROLE-0", "The requested role does not exist"), NON_MODIFICABLE_ROLE(
        "ROLE-1", "The requested role cannot be modified"), PRIVILEGE_PARAM_NOT_FOUND("ROLE-2",
        "Missing privilege parameter"), DELETE_ERROR("ROLE-3",
        "The requested role is blocked. It cannot be deleted"), DELETE_ERROR_WITH_USER("ROLE-4",
        "Cannot delete a role with a user associated"), DELETE_ERROR_WITH_ROLE_LDAP("ROLE-5",
        "Cannot delete a role with a RoleLdap associated"), DUPLICATED_ROLE_NAME_ENT(
        "ROLE-6",
        "Cannot create a role with the same name as an existing role for the same enterprise or with the same name as an existing global role"), DUPLICATED_ROLE_NAME_GEN(
        "ROLE-7", "Cannot create a global role with the same name as an existing role"), HAS_NOT_ENOUGH_PRIVILEGE(
        "ROLE-8", "Not enough privileges to manage this role"), ROLE_NAME_BLANK("ROLE-9",
        "Property name must not be blank"),

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
        "The email is not valid"), NOT_USER_CREACION_LDAP_MODE("USER-6",
        "In LDAP mode cannot create user"), NOT_EDIT_USER_ROLE_LDAP_MODE("USER-7",
        "In LDAP mode cannot modify user's role"), NOT_EDIT_USER_ENTERPRISE_LDAP_MODE("USER-8",
        "In LDAP mode cannot modify user's enterprise"), USER_DELETING_HIMSELF("USER 9",
        "The user cannot delete his own user account"), USER_NICK_CANNOT_BE_CHANGED("USER 10",
        "Cannot change the user nick (username)"), USER_PASSWORD_IS_NECESSARY("USER 11",
        "The field password is required"), USER_NAME_IS_NECESSARY("USER 12",
        "The field name is required"), USER_NICK_IS_NECESSARY("USER 13",
        "The field nick (username) is required"), USER_VDC_RESTRICTED("USER 14",
        "Your enterprise does not allow you to manage this virtual datacenter"),

    // REMOTE SERVICE
    NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER("RS-0",
        "The remote service is not assigned to the datacenter"), WRONG_REMOTE_SERVICE_TYPE("RS-1",
        "Wrong remote service type"), NON_EXISTENT_REMOTE_SERVICE_TYPE("RS-2",
        "The remote service does not exist"), REMOTE_SERVICE_URL_ALREADY_EXISTS("RS-3",
        "The remote service URL already exists and cannot be duplicated"), REMOTE_SERVICE_MALFORMED_URL(
        "RS-4", "The remote service URL is not well formed"), REMOTE_SERVICE_POOL_ASIGNED("RS-5",
        "This datacenter already has a storage pool assigned"), REMOTE_SERVICE_TYPE_EXISTS("RS-6",
        "This datacenter already has a remote service of that type"), REMOTE_SERVICE_CONNECTION_FAILED(
        "RS-7", "Failed connection to the remote service"), REMOTE_SERVICE_CANNOT_BE_CHECKED(
        "RS-8", "This remote service is not available to be checked"), APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED(
        "AM-0",
        "The repository exported by the current appliance manager is being used in another datacenter"), APPLIANCE_MANAGER_REPOSITORY_IN_USE(
        "AM-1",
        "The current repository holds virtual images being used in some virtual appliances, so it is not possible to remove this remote service. You can change the appliance manager but only if the same repository is used."), REMOTE_SERVICE_STORAGE_REMOTE_WITH_POOLS(
        "RS-9", "Cannot delete a storage manager with associated storage pools"), REMOTE_SERVICE_DHCP_IS_BEING_USED(
        "RS-10", "Cannot delete a DHCP Service. There are virtual machines deployed."), REMOTE_SERVICE_VSM_IS_BEING_USED(
        "RS-11",
        "Cannot delete a Virtual System Monitor Service. There are virtual machines deployed."), REMOTE_SERVICE_WRONG_URL(
        "RS-12", "The URL supplied is not valid"), REMOTE_SERVICE_DHCP_WRONG_URI("RS-13",
        "The URI of the DHCP service is invalid"), REMOTE_SERVICE_DATACENTER_UUID_NOT_FOUND(
        "RS-14", "The remote service does not have the *abiquo.datacenter.id* property set"), REMOTE_SERVICE_DATACENTER_UUID_INCONSISTENT(
        "RS-15",
        "The remote service is configured with a different datacenter UUID, please adjust the *abiquo.datacenter.id* property of the remote service."), REMOTE_SERVICE_UNDEFINED_PORT(
        "RS-16", "A port must be defined in the URI"), REMOTE_SERVICE_NON_POOLABLE("RS-17",
        "The remote service indicated cannot be used in a remote service client pool"), REMOTE_SERVICE_ERROR_BORROWING(
        "RS-18",
        "An unexpected error occurred while getting the remote service client from the client pool"), APPLIANCE_MANAGER_CALL(
        "AM-2", "Failed Appliance Manager communication"),
    //
    AM_CLIENT("AM-0", "Failed Appliance Manager communication"), AM_TIMEOUT("AM-1",
        "Timeout during Appliance Manager communication"), AM_UNAVAILABE("AM-2",
        "AM service unavailable; please check the URL of the service."), AM_FAILED_REQUEST(
        "AM-3",
        "Failed Appliance Manager request. "
            + "It is possible that the repositoryLocation property is not correct, NFS is not available or NFS privileges do not allow access to the server."),

    // OVF PACKAGE LIST
    TEMPLATE_DEFINITION_LIST_NAME_ALREADY_EXIST("OVF-PACKAGE-LIST-0",
        "OVF Package list name already exists"), //
    TEMPLATE_DEFINITION_LIST_REFRESH_NO_URL(
        "OVF-PACKAGE-LIST-1",
        "The template definition list isn't associated to any url (ovfindex.xml), so it can't be refreshed form the source"), //
    TEMPLATE_DEFINITION_LIST_NAME_NOT_FOUND("OVF-PACKAGE-LIST-2",
        "OVF Package list name is required"),

    // OVF PACKAGE
    NON_EXISTENT_OVF_PACKAGE("OVF-PACKAGE-0", "The requested OVF package does not exist"), NON_EXISTENT_TEMPLATE_DEFINITION_LIST(
        "OVF-PACKAGE-1", "The requested OVF package list does not exist"), OVF_PACKAGE_CANNOT_TRANSFORM(
        "OVF-PACKAGE-2", "Cannot return the Template Definition"), INVALID_OVF_INDEX_XML(
        "OVF-PACKAGE-3", "Cannot find the RepositorySpace"), NON_EXISTENT_REPOSITORY_SPACE(
        "OVF-PACKAGE-4", "The requested RepositorySpace does not exist"), INVALID_DISK_FORMAT_TYPE(
        "OVF-PACKAGE-5", "Invalid Disk format type URL"), INVALID_TEMPLATE_OVF_URL("OVF-PACKAGE-6",
        "Invalid OVF URL in the Template Definition"),
    // VIRTUAL IMAGE
    VIMAGE_INVALID_ALLOCATION_UNITS("VIMAGE-INVALID-OVF-ALLOCATION-INITS",
        "Virtual machine template cannot be added due to invalid allocation units"), VMTEMPLATE_SYNCH_DC_REPO(
        "VIMAGE-SYNCH-DATACENTER-REPOSITORY",
        "Cannot obtain downloaded templates in the datacenter repository"), VIMAGE_DATACENTER_REPOSITORY_NOT_FOUND(
        "DATACENTER-REPOSITORY-NOT-CREATED",
        "Datacenter Repository not configured; check Datacenter's Appliance Manager. Contact Infrastructure Administrator"), VMTEMPLATE_REPOSITORY_CHANGED(
        "VIMAGE-REPOSITORY-CHANGED", "Datacenter repository location has changed"), VIMAGE_AM_DOWN(
        "VIMAGE-AM-DOWN", "Check Appliance Manager configuration error"), NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE(
        "VIMAGE-0", "The requested virtual machine template does not exist"), VIMAGE_IS_NOT_BUNDLE(
        "VIMAGE-1", "The virtual machine template supplied is not an instance"), INVALID_VMTEMPLATE_LINK(
        "VIMAGE-2",
        "Invalid Virtual Machine Template identifier in the Virtual Machine Template link"), INVALID_DATACENTER_RESPOSITORY_LINK(
        "VIMAGE-3", "Invalid Datacenter Repository identifier in the Datacenter Repository link"), VMTEMPLATE_ENTERPRISE_CANNOT_BE_CHANGED(
        "VIMAGE-4", "Changing the Enterprise of the Virtual Machine Template is not allowed"), VMTEMPLATE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED(
        "VIMAGE-5",
        "Changing the Datacenter Repository of a Virtual Machine Template is not allowed"), VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_CHANGED(
        "VIMAGE-6", "Master Template of a Virtual Machine Template cannot be changed"), VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_DELETED(
        "VIMAGE-7",
        "The requested virtual machine template is a master template; master templates cannot be deleted"), VMTEMPLATE_STATEFUL_TEMPLATE_CANNOT_BE_DELETED(
        "VIMAGE-8", "Cannot delete a persistent image"), VMTEMPLATE_SHARED_TEMPLATE_FROM_OTHER_ENTERPRISE(
        "VIMAGE-9",
        "Cannot delete the requested shared virtual machine template because it belongs to another enterprise"), VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_DELETED(
        "VIMAGE-10",
        "The Virtual Machine Template is being used by Virtual Machines and cannot be deleted"), VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_UNSHARED(
        "VIMAGE-11",
        "The Virtual Machine Template is being used by Virtual Machines and cannot be modified to not shared"), VIMAGE_MALFORMED_ICON_URI(
        "VIMAGE-12", "The URL of the Icon is not well formed"),

    // NODE COLLECTOR
    NON_EXISTENT_IP("NC-0", "The requested IP does not exist"), MISSING_IP_PARAMETER("NC-1",
        "Missing IP query parameter"), NC_BAD_CREDENTIALS_TO_RACK("NC-2",
        "Bad credentials attempting to retrieve the list of physical machines from rack "), NC_BAD_CREDENTIALS_TO_MACHINE(
        "NC-3", "Bad credentials attempting to retrieve the machine "), NC_CONNECTION_EXCEPTION(
        "NC-4", "There is a machine running at the given IP but no hypervisor is responding"), NC_NOT_FOUND_EXCEPTION(
        "NC-5", "There is no machine running at the given IP"), NC_UNEXPECTED_EXCEPTION("NC-6",
        "Unexpected exception building the request to discovery manager"), NC_UNAVAILABLE_EXCEPTION(
        "NC-7", "The discovery manager is currently not available"), NC_VIRTUAL_MACHINE_NOT_FOUND(
        "NC-8", "The requested virtual machine was not found on the remote hypervisor"), NC_NOT_MANAGED_HOST(
        "NC-9", "The requested host is not managed"), NC_INVALID_IP("NC-10",
        "The IP format is invalid"), NC_BAD_CONFIGURATION(
        "NC-11",
        "Access is denied, please check whether the [domain-username-password] are correct. Also, if not already done please check the GETTING STARTED and FAQ sections in readme.htm. They provide information on how to correctly configure the Windows machine for DCOM access, so as to avoid such exceptions. "),

    // STORAGE POOL
    MISSING_REQUIRED_QUERY_PARAMETER_IQN("SP-1", "Missing IQN query parameter"), CONFLICT_STORAGE_POOL(
        "SP-2", "The ID of the storage pool and the ID of the object supplied must be the same"), NON_EXISTENT_STORAGE_POOL(
        "SP-3", "The requested storage pool does not exist"), STORAGE_POOL_ERROR_MODIFYING("SP-4",
        "There was an unexpected error while modifying the storage pool"), STORAGE_POOLS_SYNC(
        "SP-5", "Could not get the storage pools from the target device"), STORAGE_POOL_SYNC(
        "SP-6", "Could not get the requested storage pool from the target device"), CONFLICT_VOLUMES_CREATED(
        "SP-7", "Cannot edit or delete the storage pool. There are existing volumes"), STORAGE_POOL_DUPLICATED(
        "SP-8", "Duplicate storage pool"), STORAGE_POOL_TIER_IS_DISABLED("SP-9", "Tier is disabled"), STORAGE_POOL_PARAM_NOT_FOUND(
        "SP-10", "Missing storage pool parameter"), STORAGE_POOL_LINK_DATACENTER_PARAM_NOT_FOUND(
        "SP-11", "The Datacenter parameter was not found in the storage pool link"), STORAGE_POOL_LINK_DEVICE_PARAM_NOT_FOUND(
        "SP-12", "The Storage device parameter was not found in the storage pool link"), MISSING_POOL_LINK(
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
        "LIMIT-1", "The required resources exceed the allowed limits"), SOFT_LIMIT_EXCEEDED(
        "LIMIT-2", "The required resources exceed the soft limits"), NOT_ENOUGH_RESOURCES(
        "ALLOC-0", "There are not enough resources to create the virtual machine"), //
    ALLOCATOR_ERROR("ALLOC-1", "Cannot create virtual machine"), //

    CHECK_EDIT_NO_TARGET_MACHINE("EDIT-01",
        "This method requires the virtual machine to be deployed on a target hypervisor"),

    // VIRTUAL SYSTEM MONITOR

    MONITOR_PROBLEM("VSM-0", "An error occurred when monitoring the physical machine"), UNMONITOR_PROBLEM(
        "VSM-1", "An error occurred when shutting down the monitored physical machine"), SUBSCRIPTION_PROBLEM(
        "VSM-2", "An error occurred when subscribing the virtual machine"), UNSUBSCRIPTION_PROBLEM(
        "VSM-3", "An error occurred when unsubscribing the virtual machine"), REFRESH_STATE_PROBLEM(
        "VSM-4", "An error occurred when refreshing the virtual machine state"), INVALIDATE_STATE_PROBLEM(
        "VSM-5", "An error occurred when resetting the last known state of the virtual machine"), VSM_UNAVAILABE(
        "VSM-6", "VSM service unavailable; check the URL of the service."),

    // LICENSE
    LICENSE_UNEXISTING("LICENSE-0", "The requested license does not exist"), LICENSE_INVALID(
        "LICENSE-1", "The license provided is not valid"), LICENSE_CIHPER_INIT_ERROR("LICENSE-2",
        "Could not initialize licensing ciphers"), LICENSE_CIHPER_KEY("LICENSE-3",
        "Could not read licensing cipher key"), LICENSE_OVERFLOW("LICENSE-4",
        "The maximum number of managed cores has been reached"), LICENSE_DUPLICATED("LICENSE-5",
        "The license is already being used"),

    // TIERS
    NON_EXISTENT_TIER("TIER-0", "The requested storage tier does not exist"), NULL_TIER("TIER-1",
        "Embedded tier of the StoragePool cannot be null"), MISSING_TIER_LINK("TIER-2",
        "Missing link to the storage tier"), TIER_PARAM_NOT_FOUND("TIER-3",
        "Missing storage tier parameter"), TIER_LINK_DATACENTER_PARAM_NOT_FOUND("TIER-4",
        "Datacenter parameter in storage tier link not found"), TIER_LINK_DATACENTER_DIFFERENT(
        "TIER-5",
        "The storage tier is not in the same datacenter where you are trying to create the StoragePool"), TIER_CONFLICT_DISABLING_TIER(
        "TIER-6", "Cannot disable a tier with associated storage pools"), TIER_DISABLED("TIER-7",
        "The requested storage tier is disabled"), TIER_LINK_VIRTUALDATACENTER_PARAM_NOT_FOUND(
        "TIER-8", "VirtualDatacenter parameter was not found in storage tier link"), TIER_LINK_VIRTUALDATACENTER_DIFFERENT(
        "TIER-9",
        "The storage tier's virtualdatacenter link does not match the virtualdatacenter supplied"),

    // DEVICES
    NON_EXISTENT_DEVICE("DEVICE-0", "The requested device does not exist"), DEVICE_DUPLICATED(
        "DEVICE-1", "Duplicate Storage Device"),

    // STATISTICS
    NON_EXISTENT_STATS("STATS-0", "No statistical data found"), NON_EXISTENT_STATS_FOR_DATACENTER(
        "STATS-1", "No statistical data found for the requested datacenter"), NON_EXISTENT_STATS_FOR_DCLIMITS(
        "STATS-2", "No statistical data found for the requested enterprise in this datacenter"), NON_EXISTENT_STATS_FOR_ENTERPRISE(
        "STATS-3", "No statistical data found for the requested enterprise"), NODECOLLECTOR_ERROR(
        "NODECOLLECTOR-1", "Nodecollector has raised an error"),

    // QUERY PAGGING STANDARD ERRORS
    QUERY_INVALID_PARAMETER("QUERY-0", "Invalid 'by' parameter"), QUERY_NETWORK_TYPE_INVALID_PARAMETER(
        "QUERY-1", "Invalid 'type' parameter. Only 'EXTERNAL', 'UNMANAGED' or 'PUBLIC' allowed"),

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
        "Cannot resize a generic iSCSI volume"), SSM_UNREACHABLE("VOL-18",
        "Could not get the Storage Manager remote service"), VOLUME_GRANT_ACCESS_ERROR("VOL-19",
        "Could not add the initiator mappings"), NON_EXISTENT_VOLUME_MAPPING("VOL-20",
        "The requested initiator mapping does not exist"), VOLUME_NOT_ATTACHED("VOL-21",
        "The volume is not attached to the virtual machine"), VOLUME_ATTACH_INVALID_LINK("VOL-22",
        "Invalid link to the volume to attach"), VOLUME_ATTACH_INVALID_VDC_LINK("VOL-23",
        "Invalid virtual datacenter in the link to the volume to attach"), VOLUME_ALREADY_ATTACHED(
        "VOL-24", "The volume is already attached to a virtual machine"), VOLUME_TOO_MUCH_ATTACHMENTS(
        "VOL-25", "The maximum number of attached disks and volumes has been reached"), VOLUME_ATTACH_ERROR(
        "VOL-26",
        "An unexpected error occurred while attaching the volume. Please contact the Administrator"), VOLUME_ALREADY_DETACHED(
        "VOL-27", "The volume is already detached"), VOLUME_DETACH_ERROR("VOL-28",
        "An unexpected error occurred while detaching the volume. Please contact the Administrator"), VOLUME_RECONFIGURE_ERROR(
        "VOL-29", "An unexpected error occurred while reconfiguring storage"), VOLUME_WRONG_NEW_VIRTUALDATACENTER(
        "VOL-39", "The volume can only be moved between Virtual Datacenters in the same Datacenter"),

    // SSM
    SSM_GET_POOLS_ERROR("SSM-1", "Could not get the storage pools on the target storage device"), SSM_GET_POOL_ERROR(
        "SSM-2", "Could not get the given storage pool on the target storage device"), SSM_GET_VOLUMES_ERROR(
        "SSM-3", "Could not get the volumes in the given storage pool"), SSM_GET_VOLUME_ERROR(
        "SSM-4", "Could not get the given volume in the given storage pool"), SSM_CREATE_VOLUME_ERROR(
        "SSM-5", "Could not create the volume on the target storage device"), SSM_DELETE_VOLUME_ERROR(
        "SSM-6", "Could not delete the volume from the target storage device"), SSM_UPDATE_ERROR(
        "SSM-7", "Could not update the volume on the target storage devide"), SSM_ADD_INITIATOR_ERROR(
        "SSM-8", "Could not add the given iSCSI initiator on the target storage device"), SSM_REMOVE_INITIATOR_ERROR(
        "SSM-9", "Could not remove the given iSCSI initiator from the target storage device"),

    // RULES
    NON_EXISTENT_EER("RULE-1", "The requested restrict shared server rule does not exist"), NON_EXISTENT_FPR(
        "RULE-2", "The requested load balance rule does not exist"), NON_EXISTENT_MLR("RULE-3",
        "The requested load level rule does not exist"), ONE_FPR_REQUIRED("RULE-4",
        "At least one load balance rule is required"), ONE_LINK_REQUIRED("RULE-5",
        "Expected one link with the rel attribute; possible values (datacenter/rack/machine)"), INVALID_FPR(
        "RULE-6", "The load balance type indicated is null or invalid"),

    //
    HD_NON_EXISTENT_HARD_DISK("HD-1", "The requested hard disk does not exist"), HD_DISK_0_CAN_NOT_BE_DELETED(
        "HD-2",
        "Disk 0 comes from the Virtual Machine Template and cannot be deleted from the Virtual Machine"), HD_INVALID_DISK_SIZE(
        "HD-3", "Invalid disk size."), HD_CURRENTLY_ALLOCATED("HD-4",
        "Cannot perform this action because the hard disk is currently attached to a virtual machine"), HD_ATTACH_INVALID_LINK(
        "HD-5", "Invalid link to the hard disk to attach"), HD_ATTACH_INVALID_VDC_LINK("HD-6",
        "Invalid virtual datacenter in the link to the volume to attach"), HD_CREATION_NOT_UNAVAILABLE(
        "HD-7",
        "Cannot perform this action because hard disk creation is not available for this hypervisor"),

    // Chef
    CHEF_ERROR_GETTING_RECIPES("CHEF-0",
        "Could not get the list of available recipes for the enterprise"), CHEF_ERROR_GETTING_ROLES(
        "CHEF-1", "Could not get the list of available Chef roles for the enterprise"), CHEF_ERROR_CONNECTION(
        "CHEF-2", "Cannot connect to the Chef Server"), CHEF_NODE_DOES_NOT_EXIST("CHEF-3",
        "The node does not exist on the Chef Server. "
            + "If the virtual machine is bootstrapping, please wait until the process completes."), CHEF_ELEMENT_DOES_NOT_EXIST(
        "CHEF-4", "The given runlist element does not exist on the Chef Server"), CHEF_CANNOT_UPDATE_NODE(
        "CHEF-5", "The node could not be updated on the Chef Server. "
            + "Please contact the Administrator."), CHEF_CANNOT_CONNECT("CHEF-6",
        "Could not connect to the Chef server. Please contact the Administrator."), CHEF_INVALID_ENTERPRISE_DATA(
        "CHEF-7", "Could not connect to the Chef server with the given Admin data. "
            + "Please verify the credentials"), CHEF_INVALID_ENTERPRISE("CHEF-8",
        "The enterprise is not configured to use Chef"), CHEF_INVALID_VIRTUALMACHINE("CHEF-9",
        "The virtual machine cannot use Chef. "
            + "Please verify that the image is Chef enabled and the Enterprise can use Chef"), CHEF_INVALID_VALIDATOR_KEY(
        "CHEF-10",
        "The validator certificate supplied is not a valid private key. Please verify the key format."), CHEF_INVALID_CLIENT_KEY(
        "CHEF-11",
        "The provided admin certificate is not a valid private key. Please verify the key format."), CHEF_MALFORMED_URL(
        "CHEF-12", "The provided chef server URL is not well formed."), CHEF_CLIENT_DOES_NOT_EXIST(
        "CHEF-13", "The validator client supplied does not exist"),

    // Parsing links
    LINKS_INVALID_LINK("LNK-0", "Invalid link. Check documentation"), LINKS_ONLY_ACCEPTS_ONE_LINK(
        "LNK-1", "Invalid number of links: This resource only accepts a single link"), LINKS_VIRTUAL_MACHINE_TEMPLATE_NOT_FOUND(
        "LNK-2", "Virtual Machine Template link with rel 'virtualmachinetemplate' is mandatory "), LINKS_VIRTUAL_MACHINE_TEMPLATE_INVALID_URI(
        "LNK-3", "Virtual Machine Template invalid link"),

    // CATEGORY
    NON_EXISTENT_CATEGORY("CATEGORY-1", "The requested category does not exist"), CATEGORY_DUPLICATED_NAME(
        "CATEGORY-2", "A category with this name already exists."), CATEGORY_NOT_ERASABLE(
        "CATEGORY-3", "This category cannot be deleted"), INVALID_CATEGORY_LINK("CATEGORY-4",
        "Invalid Category identifier in the Category link"), CATEGORY_CANNOT_BE_NULL("CATEGORY-5",
        "Category name cannot be null"), CATEGORY_CANNOT_MOVE_LOCAL("CATEGORY-6",
        "Cannot move a local category to another enterprise."), CATEGORY_NO_PRIVELIGES_TO_CREATE_GLOBAL(
        "CATEGORY-7", "Current user does not have enough privileges to create a global category."), CATEGORY_CANNOT_CHANGE_TO_LOCAL(
        "CATEGORY-8", "Cannot change a global category to a local category."), CATEGORY_NO_PRIVELIGES_TO_REMOVE(
        "CATEGORY-9", "Current user does not have enough privileges to remove this category."),
    // ICONS
    ICON_DUPLICATED_PATH("ICON-1", "Duplicate path for an icon"), NON_EXISTENT_ICON("ICON-2",
        "The requested icon does not exist"), NON_EXISENT_ICON_WITH_PATH("ICON-3",
        "No icon found with the requested path"), ICON_IN_USE_BY_VIRTUAL_IMAGES("ICON-4",
        "Cannot delete the icon because it is in use by a virtual machine template"), INVALID_ICON_LINK(
        "ICON-5", "Invalid Icon identifier in the Icon link"),

    // TASKS
    NON_EXISTENT_TASK("TASK-1", "The requested task does not exist"), TASK_OWNER_NOT_FOUND(
        "TASK-2", "The owner of the requested task could not be found"),

    // PRICING TEMPLATE
    CURRENCY_PARAM_NOT_FOUND("PRICINGTEMPLATE-0", "Missing currency parameter"), ENT_PARAM_NOT_FOUND(
        "PRICINGTEMPLATE-1", "Missing enterprise parameter"), PRICING_TEMPLATE_DUPLICATED_NAME(
        "PRICINGTEMPLATE-2", "Duplicate name for Pricing Template"), NON_EXISTENT_PRICING_TEMPLATE(
        "PRICINGTEMPLATE-3", "The requested Pricing Template does not exist"), DELETE_ERROR_WITH_ENTERPRISE(
        "PRICINGTEMPLATE-4", "Cannot delete a Pricing Template with associated Enterprise"), PRICING_TEMPLATE_MINIMUM_CHARGE_PERIOD(
        "PRICINGTEMPLATE-5", "The smallest charging period is DAY"), PRICING_TEMPLATE_EMPTY_NAME(
        "PRICINGTEMPLATE-6", "Pricing Template name cannot be empty"), MISSING_CURRENCY_LINK(
        "PRICINGTEMPLATE-7", "Missing link to the currency"), CHARGING_PERIOD_VALUES(
        "PRICINGTEMPLATE-8", "Charging period values should be between 2 and 6"), MINIMUM_CHARGE_EMPTY(
        "PRICINGTEMPLATE-9", "Check Minimum Charge value is not null or wrong type"), MINIMUM_CHARGE_VALUES(
        "PRICINGTEMPLATE-10", "Minimum Charge values should be between 0 and 6"),

    // CURRENCY
    NON_EXISTENT_CURRENCY("CURRENCY-0", "The requested Currency does not exist"), ONE_CURRENCY_REQUIRED(
        "CURRENCY-1", "At least one currency is required"), CURRENCY_DUPLICATED_NAME("CURRENCY-2",
        "Duplicate name for Currency"), CURRENCY_DELETE_ERROR("CURRENCY-3",
        "Cannot remove currency associated with a Pricing Model"), CURRENCY_NAME_NOT_FOUND(
        "CURRENCY-4", "Currency name is required"), CURRENCY_SYMBOL_NOT_FOUND("CURRENCY-5",
        "Currency symbol is required"), CURRENCY_NAME_LONG("CURRENCY-6",
        "Currency name maximum length is 20 characters"), CURRENCY_SYMBOL_LONG("CURRENCY-7",
        "Currency symbol maximum length is 10 characters"), CURRENCY_DIGIT_LONG("CURRENCY-8",
        "Currency digit maximum value is 9"),

    // COST CODE
    NON_EXISTENT_COSTCODE("COSTCODE-0", "The requested Cost Code does not exist"), COSTCODE_PARAM_NOT_FOUND(
        "COSTCODE-1", "Missing  Cost Code parameter"), COSTCODE_DUPLICATED_NAME("COSTCODE-2",
        "Duplicate name for Cost Code"), COSTCODE_NAME_NOT_FOUND("COSTCODE-3",
        "Cost Code name is required"), COSTCODE_DESCRITPION_NOT_FOUND("COSTCODE-4",
        "Cost Code description is required"), COSTCODE_NAME_LONG("COSTCODE-5",
        "Cost Code name maximum length is 20 characters"), COSTCODE_DESCRIPTION_LONG("COSTCODE-6",
        "Cost Code description maximum length is 100 characters"),

    // COST CODE- CURRENCY
    COSTCODE_CURRENCY_DUPLICATED("COSTCODE_CURRENCY-0", "Duplicate value by Cost Code and Currency"), NON_EXISTENT_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-1", "The requested Cost Code -Currency does not exist"), NOT_ASSIGNED_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-2", "The Cost Code -Currency is not assigned to the Cost Code"), NOT_ASSIGNED_COSTCODE_CURRENCY_PRICE(
        "COSTCODE_CURRENCY-3", "Price is required"),

    // PRICING - COST CODE
    PRICING_COSTCODE_DUPLICATED("PRICING_COSTCODE-0",
        "Duplicate value by Cost Code and PricingTemplate"), NON_EXISTENT_PRICING_COSTCODE(
        "PRICING_COSTCODE-1", "The requested Cost Code -PricingTemplate does not exist"),

    // PRICING - TIER
    PRICING_TIER_DUPLICATED("PRICING_TIER-0", "Duplicate value by Tier and PricingTemplate"), NON_EXISTENT_PRICING_TIER(
        "PRICING_TIER-1", "The requested Tier-PricingTemplate does not exist"), PRICING_TIER_WRONG_RELATION(
        "PRICING_TIER-2", "The pricing tier is not related to the pricing model indicated"), PRICING_TIER_DATACENTER(
        "PRICING_TIER-3", "This tier is not related to the datacenter indicated"), NOT_ASSIGNED_PRICING_TIER_PRICE(
        "PRICING_TIER-4", "Price is required"), NOT_TIER_IN_PRICING_TIER("PRICING_TIER_5",
        "The tier indicated in the link is not related to this pricing tier"),

    // HYPERVISOR TYPE
    INVALID_HYPERVISOR_TYPE("HYPERVISOR_TYPE-0", "The requested Hypervisor Type is invalid"),

    // DHCP_OPTION
    NON_EXISTENT_DHCP_OPTION("DHCP_OPTION-0", "The requested DHCP option does not exist"), DHCP_OPTION_PARAM_NOT_FOUND(
        "DHCP_OPTION-12", "Missing DHCP option parameter"),

    // REDIS
    REDIS_CONNECTION_FAILED("REDIS-0", "Failed connection to Redis"),

    // RABBITMQ
    RABBITMQ_CONNECTION_FAILED("RABBITMQ-0", "Failed connection to RabbitMQ"), ;

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
            System.out.println(String.format("| %s | %s | %s |", error.code, error.message, error
                .name()));
        }

        System.out.println("\n ************ Flex client labels ************** \n");

        // Outputs all errors for the Chef client
        for (APIError error : errors)
        {
            System.out.println(String.format("%s=%s", error.code, error.message));
        }
    }

}
