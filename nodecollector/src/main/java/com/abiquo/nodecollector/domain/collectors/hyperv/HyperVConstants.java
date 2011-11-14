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
package com.abiquo.nodecollector.domain.collectors.hyperv;

/**
 * Hyper-V constants.
 * 
 * @author ibarrera
 */
public final class HyperVConstants
{

    /** The virtualization namespace. */
    public static final String VIRTUALIZATION_NS = "root\\virtualization";

    /** The CIM namespace. */
    public static final String CIM_NS = "root\\cimv2";

    /** Default drive where virtual image disks are stored. */
    public static final String DEFAULT_IMAGE_DRIVE = "C:";

    /** Hyper-V resource type for virtual hard disks. */
    public static final int VIRTUAL_DISK_RESOURCE_TYPE = 21;

    /** Hyper-V resource sub-type for virtual hard disks. */
    public static final String VIRTUAL_DISK_RESOURCE_SUBTYPE = "Microsoft Virtual Hard Disk";

    /** Hyper-V resource type for virtual or physical hard disks. */
    public static final int DISK_RESOURCE_TYPE = 22;

    /** Hyper-V resource sub-type for virtual hard disks. */
    public static final String DISKSYNTHETIC = "MICROSOFT SYNTHETIC DISK DRIVE";

    /** Hyper-V resource sub-type for physical hard disks (attached volumes). */
    public static final String DISKPHYSICAL = "MICROSOFT PHYSICAL DISK DRIVE";

    /** The Number Of Processor field in the Msvm_SummaryInformation object. */
    public static final Integer NUMBER_OF_PROCESSORS_FIELD = 4;

    /** The prefix for the default Initiator IQN on Windows hosts. */
    public static final String DEFAULT_INITIATOR_NAME_PREFIX = "iqn.1991-05.com.microsoft:";

    /** The path in the Windows Registry where the Initiator Name is stored. */
    public static final String INITIATOR_REGISTRY_PATH =
        "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\iSCSI\\Discovery";

    /** The key in the Windows Registry where the Initiator Name is stored. */
    public static final String INITIATOR_REGISTRY_KEY = "DefaultInitiatorName";

    /**
     * Private constructors for utility classes.
     */
    private HyperVConstants()
    {

    }
}
