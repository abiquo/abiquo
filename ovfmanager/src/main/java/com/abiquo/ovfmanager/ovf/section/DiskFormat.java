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

package com.abiquo.ovfmanager.ovf.section;


/**
 * TODO use com.abiquo.model.enumerator.DiskFormatType (model)
 * <p>
 * Supported virtual disk formats. In order to meet the 5.2 clause an URI is required as disk
 * format. ''the disk format shall be given by a URI which identifies an unencumbered specification
 * on how to interpret the disk format'' TODO add for all the supported disk formats.
 */
@Deprecated
public enum DiskFormat
{
    /**
     * Unknow disk type.
     */
    UNKNOWN(0, "http://unknown"),

    /**
     * Raw disk type.
     */
    RAW(1, "http://raw"),

    /**
     * Incompatible disk type. Normally split file.
     */
    INCOMPATIBLE(2, "http://incompatible"),

    /**
     * ESXi's sparse compressed disk type.
     */
    VMDK_STREAM_OPTIMIZED(3,
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized"),

    /**
     * ESXi's monolithic flat disk type.
     */
    VMDK_FLAT(4,
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_flat"),

    /**
     * ESXi's monolithic sparse disk type.
     */
    VMDK_SPARSE(5,
        "http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#monolithic_sparse"),

    /**
     * HyperV's monolithic flat disk type.
     */
    VHD_FLAT(6, "http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_flat"),

    /**
     * HyperV's monolithic sparse disk type.
     */
    VHD_SPARSE(7,
        "http://technet.microsoft.com/en-us/virtualserver/bb676673.aspx#monolithic_sparse"),

    /**
     * VirtualBox's monolithic flat disk type.
     */
    VDI_FLAT(8, "http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_flat"),

    /**
     * VirtualBox's monolithic sparse disk type.
     */
    VDI_SPARSE(9, "http://forums.virtualbox.org/viewtopic.php?t=8046#monolithic_sparse"),

    /**
     * QCow's monolithic flat disk type.
     */
    QCOW2_FLAT(10, "http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_flat"),

    /**
     * QCow's monolithic sparse disk type.
     */
    QCOW2_SPARSE(11, "http://people.gnome.org/~markmc/qcow-image-format.html#monolithic_sparse");

    /**
     * The URI of the disk format type.
     */
    private final String uri;

    /**
     * Identifier of the disk format type.
     */
    private Integer identifier;

    /**
     * Construct the Enum with an identifier value and an URI.
     * 
     * @param identifier identifier of the disk type.
     * @param uri uri that stores the disk specification.
     */
    private DiskFormat(final Integer identifier, final String uri)
    {
        this.uri = uri;
        this.identifier = identifier;
    }

    /**
     * Return the URI's that specifies the disk.
     * 
     * @return
     */
    public String getDiskFormatUri()
    {
        return uri;
    }

    /**
     * Return a Class value from an URI
     * 
     * @param diskFormatUri uri to search
     * @return a DiskFormat object
     * @throws IllegalArgumentException if the uri does'n identify any DiskFormat value
     */
    public static DiskFormat fromValue(final String diskFormatUri) throws IllegalArgumentException
    {
        for (DiskFormat df : DiskFormat.values())
        {
            if (diskFormatUri.equalsIgnoreCase(df.getDiskFormatUri()))
            {
                return df;
            }
        }

        return DiskFormat.UNKNOWN;
    }

    public static DiskFormat fromName(final String diskFormatName) throws IllegalArgumentException
    {
        for (DiskFormat df : DiskFormat.values())
        {
            if (diskFormatName.equalsIgnoreCase(df.name()))
            {
                return df;
            }
        }

        return DiskFormat.UNKNOWN;
    }

    /**
     * @return the identifier
     */
    public Integer getIdentifier()
    {
        return identifier;
    }

}
