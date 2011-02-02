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

package com.abiquo.appliancemanager.transport;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "OVFPackageDiskFormat")
@XmlEnum
public enum OVFPackageDiskFormat
{
    /**
     * Unknow disk type.
     */
    UNKNOWN,

    /**
     * Raw disk type.
     */
    RAW,

    /**
     * Incompatible disk type. Normally split file.
     */
    INCOMPATIBLE,

    /**
     * ESXi's sparse compressed disk type.
     */
    VMDK_STREAM_OPTIMIZED,

    /**
     * ESXi's monolithic flat disk type.
     */
    VMDK_FLAT,

    /**
     * ESXi's monolithic sparse disk type.
     */
    VMDK_SPARSE,

    /**
     * HyperV's monolithic flat disk type.
     */
    VHD_FLAT,

    /**
     * HyperV's monolithic sparse disk type.
     */
    VHD_SPARSE,

    /**
     * VirtualBox's monolithic flat disk type.
     */
    VDI_FLAT,

    /**
     * VirtualBox's monolithic sparse disk type.
     */
    VDI_SPARSE,

    /**
     * QCow's monolithic flat disk type.
     */
    QCOW2_FLAT,

    /**
     * QCow's monolithic sparse disk type.
     */
    QCOW2_SPARSE;

}
