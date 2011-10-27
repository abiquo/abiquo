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

package com.abiquo.model.enumerator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "diskFormatType")
@XmlEnum
public enum DiskFormatType
{
    /* 0 */
    UNKNOWN("http://unknown", "Unknown format", DiskFormatTypeAlias.UNKNOWN),

    /* 1 */
    RAW("http://raw", "Disk format device", DiskFormatTypeAlias.RAW),

    /* 2 */
    INCOMPATIBLE("http://incompatible", "Incompatible disk type", DiskFormatTypeAlias.INCOMPATIBLE),

    /* 3 */
    VMDK_STREAM_OPTIMIZED(
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized",
        "VMWare streamOptimized format", DiskFormatTypeAlias.VMDK_STREAM_OPTIMIZED),

    /* 4 */
    VMDK_FLAT(
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat",
        "VMWare Fixed Disk", DiskFormatTypeAlias.VMDK_FLAT),

    /* 5 */
    VMDK_SPARSE(
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse",
        "VMWare Sparse Disk", DiskFormatTypeAlias.VMDK_SPARSE),

    /* 6 */
    VHD_FLAT("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat",
        "VHD Fixed Disk", DiskFormatTypeAlias.VHD),

    /* 7 */
    VHD_SPARSE("http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse",
        "VHD Sparse Disk", DiskFormatTypeAlias.VHD),

    /* 8 */
    VDI_FLAT("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat", "VDI Fixed disk",
        DiskFormatTypeAlias.VDI),

    /* 9 */
    VDI_SPARSE("http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse",
        "VDI Sparse disk", DiskFormatTypeAlias.VDI),

    /* 10 */
    QCOW2_FLAT("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat",
        "QCOW2 Fixed disk", DiskFormatTypeAlias.QCOW2),

    /* 11 */
    QCOW2_SPARSE("http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse",
        "QCOW2 Sparse disk", DiskFormatTypeAlias.QCOW2);

    public final String uri, description;

    public final DiskFormatTypeAlias alias;

    public static final DiskFormatType[] VBOX_COMPATIBLES = new DiskFormatType[] {VMDK_SPARSE,
    VHD_FLAT, VHD_SPARSE, VDI_FLAT, VDI_SPARSE};

    public static final DiskFormatType[] KVM_COMPATIBLES = new DiskFormatType[] {RAW, VMDK_SPARSE,
    VMDK_FLAT, VHD_FLAT, VHD_SPARSE, QCOW2_FLAT, QCOW2_SPARSE};

    public static final DiskFormatType[] XEN_COMPATIBLES = new DiskFormatType[] {VMDK_FLAT};

    public static final DiskFormatType[] VMWARE_COMPATIBLES = new DiskFormatType[] {VMDK_FLAT,
    VMDK_SPARSE};

    public static final DiskFormatType[] HYPERV_COMPATIBLES = new DiskFormatType[] {VHD_FLAT,
    VHD_SPARSE};

    public static final DiskFormatType[] XENSERVER_COMPATIBLES = HYPERV_COMPATIBLES;

    private DiskFormatType(String uri, String description, DiskFormatTypeAlias alias)
    {
        this.uri = uri;
        this.description = description;
        this.alias = alias;
    }

    public int id()
    {
        return ordinal();
    }

    public static DiskFormatType fromId(int id)
    {
        return values()[id];
    }

    public static DiskFormatType fromURI(String URI)
    {
        for (DiskFormatType type : values())
        {
            if (type.uri.equals(URI))
            {
                return type;
            }
        }
        return null;
    }

    public static DiskFormatType fromValue(String v)
    {
        return valueOf(v);
    }
}
