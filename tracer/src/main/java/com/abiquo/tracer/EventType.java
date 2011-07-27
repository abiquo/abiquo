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

package com.abiquo.tracer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public enum EventType implements Serializable
{
    // Unknow event.
    UNKNOWN(0, "UNKNOWN", "Unknown event"),

    // User-related events
    USER_LOGIN(100, "USER_LOGIN", "User logged in"), USER_LOGOUT(101, "USER_LOGOUT",
        "User logged out"), USER_CREATE(102, "USER_CREATE", "User created"), USER_DELETE(103,
        "USER_DELETE", "User deleted"), USER_MODIFY(104, "USER_MODIFY", "User modified"), ENTERPRISE_CREATE(
        105, "ENTERPRISE_CREATE", "Enterprise created"), ENTERPRISE_MODIFY(106,
        "ENTERPRISE_MODIFY", "Enterprise modified"), ENTERPRISE_DELETE(107, "ENTERPRISE_DELETE",
        "Enterprise deleted"),

    // Virtual Infrastructure-related events
    VM_POWERON(200, "VM_POWERON", "Started Virtual Machine"), VM_PAUSED(201, "VM_PAUSED",
        "Paused Virtual Machine"), VM_RESUMED(202, "VM_RESUMED", "Resumed Virtual Machine"), VM_POWEROFF(
        203, "VM_POWEROFF", "Stopped Virtual Machine"), VM_DESTROY(204, "VM_DESTROY",
        "Destroyed Virtual Machine"), VM_MOVED(205, "VM_MOVED", "Virtual Machine moved"), VM_CHECK_HEALTH(
        206, "VIRTUAL_MACHINE_HEALTH_STATE", "Virtual Machine checked"), VM_CRASHED(207,
        "VM_CRASHED", "Virtual Machine turned into 'crashed' state"), VM_UNKNOWN(208, "VM_UNKNOWN",
        "Virtual Machine turned into 'unknown' state"), VM_UNDEPLOY_FORCED(209,
        "VM_UNDEPLOY_FORCED", "Virtual Machine undeploy forced"), VDC_CREATE(210, "VDC_CREATE",
        "Created Virtual Datacenter"), VDC_MODIFY(211, "VDC_MODIFY", "Modified Virtual Datacenter"), VDC_DELETE(
        212, "VDC_DELETE", "Deleted Virtual Datacenter"), VAPP_CREATE(213, "VAPP_CREATE",
        "Created Virtual Appliance"), VAPP_MODIFY(214, "VAPP_MODIFY", "Modified Virtual Appliance"), VAPP_DELETE(
        215, "VAPP_DELETE", "Deleted Virtual Appliance"), VAPP_POWERON(216, "VAPP_POWERON",
        "Deployed Virtual Appliance"), VAPP_POWEROFF(217, "VAPP_POWEROFF",
        "Undeployed Virtual Appliance"), VAPP_RUNNING(218, "VAPP_RUNNING",
        "Started Virtual Appliance"), VAPP_MOVE(219, "VAPP_MOVE", "Virtual Appliance moved"), VAPP_COPY(
        220, "VAPP_COPY", "Virtual Appliance copied"), VAPP_REFRESH(221, "VIRTUAL_APPLIANCE_STATE",
        "Virtual Appliance State Refreshed"), VAPP_CRASHED(222, "VAPP_CRASHED",
        "Virtual Appliance turned into 'crashed' state"), VAPP_UNKNOWN(223, "VAPP_UNKNOWN",
        "Virtual Appliance turned into 'unknown' state"), VAPP_BUNDLE(224, "VAPP_BUNDLE",
        "Virtual Appliance bundle started"),

    // Infrastructure-related events
    DC_CREATE(300, "DC_CREATE", "Datacenter Created"), DC_MODIFY(301, "DC_MODIFY",
        "Datacenter Modified"), DC_DELETE(302, "DC_DELETE", "Datacenter Deleted"), RACK_CREATE(303,
        "RACK_CREATE", "Rack Created"), RACK_MODIFY(304, "RACK_MODIFY", "Rack Modified"), RACK_DELETE(
        305, "RACK_DELETE", "Rack Deleted"), RACK_NRSQ_EXCEEDED(306, "NRSQ_EXCEEDED",
        "NRSQ excedded in Rack"), MACHINE_CREATE(307, "MACHINE_CREATE", "Physical Machine created"), MACHINE_MODIFY(
        308, "MACHINE_MODIFY", "Physical Machine modified"), MACHINE_DELETE(309, "MACHINE_DELETE",
        "Physical Machine deleted"), MACHINE_CHECK(310, "MACHINE_CHECK", "Physical Machine checked"), REMOTE_SERVICES_CREATE(
        311, "REMOTE_SERVICES_CREATE", "Remote Service created"), REMOTE_SERVICES_UPDATE(312,
        "REMOTE_SERVICE_UPDATE", "Remote Service updated"), REMOTE_SERVICES_CHECK(313,
        "REMOTE_SERVICES_CHECK", "Remote service checked"), RACK_RETRIEVAL(314, "RACK_RETRIEVAL",
        "Retrieve Rack"),

    // Storage system-related events
    SSM_CREATE(400, "SSM_CREATE", "Storage System Manager created"), SSM_MODIFY(401, "SSM_MODIFY",
        "Storage System Manager modified"), SSM_DELETE(402, "SSM_DELETE",
        "Storage System Manager deleted"), POOL_CREATE(403, "POOL_CREATE", "Storage Pool created"), POOL_MODIFY(
        404, "POOL_MODIFY", "Storage Pool modified"), POOL_DELETE(405, "POOL_DELETE",
        "Storage Pool deleted"), VOLUME_CREATE(406, "VOLUME_CREATE", "Volume created"), VOLUME_MODIFY(
        407, "VOLUME_MODIFY", "Volume modified"), VOLUME_DELETE(408, "VOLUME_DELETE",
        "Volume deleted"), VOLUME_ASSIGN(409, "VOLUME_ATTACHED", "Volume attached"), VOLUME_UNASSIGN(
        410, "VOLUME_DETACHED", "Volume detached"), VOLUME_ATTACH(411, "VOLUME_ATTACH",
        "Volume attached"), VOLUME_DETACH(412, "VOLUME_DETACH", "Volume detached"), GET_INITIATOR_MAPPINGS(
        413, "GET_INITIATOR_MAPPINGS", "Initiator mappings retrieved"),

    // Image-related events
    VI_DOWNLOAD(500, "VI_DOWNLOAD", "Virtual Image download from a Remote Repository"), VI_ADD(501,
        "VI_ADD", "Virtual Image added to the Appliance Library"), VI_DELETE(502, "VI_DELETE",
        "Virtual Image deleted from the Appliance Library"), DISK_CONVERSION(503,
        "DISK_CONVERSION", "Disc conversion started"), RAW_IMPORT_CONVERSION(504,
        "RAW_IMPORT_CONVERSION", "Raw import conversion started"),

    // Stateful related events
    // Stateful related events
    PERSISTENT_PROCESS_START(600, "PERSISTENT_PROCESS_START",
        "A Persistent conversion process has started"), PERSISTENT_RAW_FINISHED(601,
        "PERSISTENT_RAW_FINISHED",
        "A Persistent RAW conversion has finished and it is ready to be dumped to a volume"), PERSISTENT_VOLUME_CREATED(
        602, "PERSISTENT_VOLUME_CREATED", "A Persistent volume has been created"), PERSISTENT_DUMP_ENQUEUED(
        603, "PERSISTENT_DUMP_ENQUEUED", "A Persistent volume dump has been enqueued"), PERSISTENT_DUMP_FINISHED(
        604, "PERSISTENT_DUMP_FINISHED", "A Persistent dump to a volume has finished"), PERSISTENT_PROCESS_FINISHED(
        605, "PERSISTENT_PROCESS_FINISHED", "A Persistent conversion process has finished"), PERSISTENT_PROCESS_FAILED(
        606, "PERSISTENT_PROCESS_FAILED", "A Persistent process has failed"), PERSISTENT_INITIATOR_ADDED(
        607, "PERSISTENT_INITIATOR_ADDED", "Persistent initiator has added"),

    // License related events
    LICENSE_ADDED(700, "LICENSE_ADDED", "A new license has been added to the system"), LICENSE_REMOVED(
        701, "LICENSE_REMOVED", "A license has been removed"), LICENSE_CORRUPT(702,
        "LICENSE_CORRUPT", "The license is corrupted and cannot be used"), LICENSE_EXCEEDED(703,
        "LICENSE_EXCEEDED", "Current license capabilities had been exceeded"), LICENSE_CONFIGURATION(
        704, "LICENSE_CONFIGURATION", "An unexpected error occured in license managing services"),

    // Networking events
    VLAN_CREATED(800, "VLAN_CREATED", "New VLAN created"), VLAN_EDITED(801, "VLAN_EDITED",
        "Vlan edited"), VLAN_DELETED(802, "VLAN_DELETED", "Vlan Deleted"), NIC_ASSIGNED_VIRTUAL_MACHINE(
        803, "NIC_ASSIGNED_VIRTUAL_MACHINE", "NIC assigned to a Virtual Machine"), NIC_RELEASED_VIRTUAL_MACHINE(
        804, "NIC_RELEASED_VIRTUAL_MACHINE", "NIC deassigned from a Virtual Machine"), PUBLIC_IP_ASSIGNED_VDC(
        805, "PUBLIC_IP_ASSIGNED_VDC", "Public IP assigned to a Virtual Datacenter"), PUBLIC_IP_RELEASED_VDC(
        806, "PUBLIC_IP_RELEASED_VDC", "Public IP released from a Virtual Datacenter"), UNDER_QUARANTINE(
        807, "IP_UNDER_QUARANTINE", "IP Address put as a quarantine"), RELEASED_QUARANTINE(808,
        "RELEASED_QUARANTINE", "IP Address released from its quarantine"), PRIVATE_IP_ASSIGN(809,
        "PRIVATE_IP_ASSIGN", "Private IP assigned"), PRIVATE_IP_UNASSIGN(810,
        "PRIVATE_IP_UNASSIGN", "Private IP unassigned"), PUBLIC_IP_ASSIGN(811, "PUBLIC_IP_ASSIGN",
        "Public IP assigned"), PUBLIC_IP_UNASSIGN(812, "PUBLIC_IP_UNASSIGN", "Public IP unassigned"),

    // API events
    API_REQUEST(900, "API_REQUEST", "Functionallity executed by API request"), API_RESPONSE(901,
        "API_RESPONSE", "API response"),

    // Workload Engine Events
    WORKLOAD_LOAD_RULES(1000, "WORKLOAD_LOAD_RULES", "Workload load rules"), WORKLOAD_APPLY_RULES(
        1001, "WORKLOAD_APPLY_RULES", "Workload apply rules"), WORKLOAD_SOFT_LIMIT_EXCEEDED(1002,
        "SOFT_LIMIT_EXCEEDED", "Soft limits exceded"), WORKLOAD_HARD_LIMIT_EXCEEDED(1003,
        "HARD_LIMIT_EXCEEDED", "Hard limits exceeded"),

    // Client-related events
    THEME_UPDATE(1100, "THEME_UPDATE", "Theme updated"), UPDATE_PROPERTIES(1101,
        "UPDATE_PROPERTIES", "Client properties updated"),

    // Role Event
    ROLE_CREATED(1200, "ROLE_CREATED", "Role created"), ROLE_MODIFY(1201, "ROLE_MODIFIED",
        "Role updated"), ROLE_DELETED(1202, "ROLE_DELETED", "Role deleted"), ROLE_PRIVILEGES_MODIFY(
        1203, "ROLE_PRIVILEGES_MODIFY", "Role's privileges modified"),

    // RoleLdap Event
    ROLE_LDAP_CREATED(1300, "ROLE_LDAP_CREATED", "Role ldap created"), ROLE_LDAP_MODIFY(1301,
        "ROLE_LDAP_MODIFIED", "Role ldap updated"), ROLE_LDAP_DELETED(1302, "ROLE_LDAP_DELETED",
        "Role ldap deleted"),

    // HA Engine Events
    MACHINE_DISABLED_BY_HA(1400, "MACHINE_DISABLED_BY_HA", "Machine disabled by HA engine."), VAPP_BLOCKED_BY_HA(
        1401, "VAPP_BLOCKED_BY_HA", "Virtual appliance block by HA engine"), VM_MOVING_BY_HA(1402,
        "VAPP_MOVING_BY_HA", "Virtual machine being moved by HA engine"),

    // ALLOCATION RULES
    ALLOCATION_RULES_ADDED(1500, "ALLOCATION_RULES_APPLIED", "Allocation rules applied"), ALLOCATION_RULES_REMOVED(
        1501, "ALLOCATION_RULE_REMOVED", "Allocation rule removed"),

    // PRICING TEMPLATE
    PRICING_TEMPLATE_CREATED(1600, "PRICING_TEMPLATE_CREATED", "Pricing Template created"), PRICING_TEMPLATE_MODIFIED(
        1601, "PRICING_TEMPLATE_MODIFIED", "Pricing Template updated"), PRICING_TEMPLATE_DELETED(
        1602, "PRICING_TEMPLATE_DELETED", "Pricing Template deleted"),

    ;

    private final int event;

    private final String description;

    private final String long_description;

    private EventType(final int event, final String description, final String longDescription)
    {
        this.event = event;
        this.description = description;
        this.long_description = longDescription;
    }

    public int getValue()
    {
        return event;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLongDescription()
    {
        return this.long_description;
    }

    /**
     * Create a small main process to print the events in wiki mark-up style.
     * 
     * @param args
     */
    public static void main(final String[] args)
    {
        EventType[] events = EventType.values();
        Arrays.sort(events, new Comparator<EventType>()
        {
            @Override
            public int compare(final EventType ev1, final EventType ev2)
            {
                return ev1.getValue() - ev2.getValue();
            }

        });

        // Outputs all errors in wiki table format
        System.out.println("|| Action performed || Description || ");
        for (EventType error : events)
        {
            System.out.println(String.format("| %s | %s |", error.name(),
                error.getLongDescription()));
        }
    }
}
