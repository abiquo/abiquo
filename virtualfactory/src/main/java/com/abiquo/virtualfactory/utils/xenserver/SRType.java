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
package com.abiquo.virtualfactory.utils.xenserver;

import java.util.EnumSet;

/**
 * Storage Repository types available in XenServer.
 * 
 * @author ibarrera
 */
public enum SRType
{
    /**
     * Local LVM. Represents disks within a locally-attached Volume Group.
     */
    LVM,

    /**
     * Local EXT3 VHD. Represents disks as VHD files stored on a local path.
     */
    EXT,

    /**
     * Udev. Represents devices plugged in using the <code>udev</code> device manager as VDIs.
     */
    UDEV,

    /**
     * ISO. Handles CD images stored as files in ISO format. This SR type is useful for creating
     * shared ISO libraries.
     */
    ISO,

    /**
     * EqualLogic. Maps LUNs to VDIs on a EqualLogic array group, allowing for the use of fast
     * snapshot and clone features on the array.
     */
    EQUAL,

    /**
     * NetApp. Maps LUNs to VDIs on a NetApp server, enabling the use of fast snapshot and clone
     * features on the filer.
     */
    NETAPP,

    /**
     * Software iSCSI. Provides support for shared SRs on iSCSI LUNs. iSCSI is supported using the
     * open-iSCSI software iSCSI initiator or by using a supported iSCSI Host Bus Adapter (HBA).
     */
    ISCSI,

    /**
     * Hardware Host Bus Adapters.
     */
    HBA,

    /**
     * LVM over iSCSI. Represents disks as Logical Volumes within a Volume Group created on an iSCSI
     * LUN.
     */
    LVMOISCSI,

    /**
     * NFS VHD. Stores disks as VHD files on a remote NFS filesystem.
     */
    NFS,

    /**
     * LVM over hardware HBA. Represents disks as VHDs on Logical Volumes within a Volume Group
     * created on an HBA LUN providing, for example, hardware-based iSCSI or FC support.
     */
    LVMOHBA,

    /**
     * Citrix Storage Link Gateway Service SR. Allows use of the Citrix StorageLink service for
     * native access to a range of iSCSI and Fibre Channel arrays and automated fabric/initiator and
     * array configuration features.
     */
    CSLG,

    /**
     * File Storage Repository.
     */
    FILE,

    /**
     * Dummy Storage Repository.
     */
    DUMMY;

    /**
     * Gets the {@link SRType} object for the given String.
     * 
     * @param value The name of the <code>SRType</code> object.
     * @return the <code>SRType</code> object.
     */
    public static SRType fromValue(final String value)
    {
        return SRType.valueOf(value.toUpperCase());
    }

    /**
     * Gets the Storage Repository Types that are valid for deployment.
     * 
     * @return The Storage Repository Types that are valid for deployment.
     */
    public static EnumSet<SRType> validForDeployment()
    {
        return EnumSet.of(LVM, EXT, UDEV, EQUAL, NETAPP, ISCSI, HBA, LVMOISCSI, NFS, LVMOHBA);
    }

    /**
     * Checks if the Storage Repository Type is valid for deployment.
     * 
     * @return Boolean indicating if the Storage Repository Type is valid for deployment.
     */
    public boolean isValidForDeployment()
    {
        return validForDeployment().contains(this);
    }
}
