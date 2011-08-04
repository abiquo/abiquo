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

public enum ComponentType implements Serializable
{
    UNKNOWN(0, "Unknown component"),
    // Platform is the root of the hierarchy
    PLATFORM(1, "Abicloud Platform"),

    // Datacenter is the root of the physical infrastructure tree
    DATACENTER(2 | PLATFORM.getValue(), "Physical Datacenter"), RACK(4 | DATACENTER.getValue(),
        "Logical Rack for physical servers"), MACHINE(8 | RACK.getValue(),
        "Physical server in the compute cloud"), VIRTUAL_STORAGE(16 | DATACENTER.getValue(),
        "Virtual Storage System"), STORAGE_POOL(32 | VIRTUAL_STORAGE.getValue(),
        "Storage Pool of Volumes"),

    // Enterprise is the root of the logical infrastructure tree
    ENTERPRISE(64 | PLATFORM.getValue(), "Organization"), USER(128 | ENTERPRISE.getValue(),
        "Platform user"), VIRTUAL_DATACENTER(256 | ENTERPRISE.getValue(), "Virtual Datacenter"), VIRTUAL_APPLIANCE(
        512 | VIRTUAL_DATACENTER.getValue(), "Virtual Appliances Architecture"),

    // VirtualMachine, Volume and Network are shared between logical and physical infrastructure
    VIRTUAL_MACHINE(1024 | MACHINE.getValue() | VIRTUAL_APPLIANCE.getValue(), "Virtual Server"), VOLUME(
        2048 | STORAGE_POOL.getValue() | VIRTUAL_APPLIANCE.getValue(), "Storage Volume"), NETWORK(
        4096 | DATACENTER.getValue() | VIRTUAL_APPLIANCE.getValue(), "IP Network"),

    // Appliance manager
    APPLIANCE_MANAGER(8192 | PLATFORM.getValue(), "Appliance Manager"),

    // Image converter
    IMAGE_CONVERTER(16384 | VIRTUAL_APPLIANCE.getValue(), "Image converter"),

    // License management
    LICENSE_MANAGER(32768 | PLATFORM.getValue(), "License Manager"),

    // Stateful converter
    PERSISTENT_CONVERTER(65536 | VIRTUAL_APPLIANCE.getValue(), "Persistent converter"),

    // Abiquo's API
    API(131072, "API"),

    // Workload Engine
    WORKLOAD(262144, "Workload Engine"),

    // Roles
    ROLE(524288, "Role"),

    // Roles
    ROLE_LDAP(1048576, "Role LDAP"),

    // HA
    HIGH_AVAILABILITY(2097152, "High availability engine"),

    // PRICING_ TEMPLATE
    PRICING_TEMPLATE(4194304, "Pricing Template"),

    // COSTCODE_CURRENCY
    COSTCODE_CURRENCY(8388608, "Cost Code - Currency"),

    // COSTCODE
    COSTCODE(16777216, "Cost Code");

    private final int component;

    private final String description;

    private ComponentType(final int component, final String description)
    {
        this.component = component;
        this.description = description;
    }

    public int getValue()
    {
        return component;
    }

    public String getDescription()
    {
        return description;
    }
}
