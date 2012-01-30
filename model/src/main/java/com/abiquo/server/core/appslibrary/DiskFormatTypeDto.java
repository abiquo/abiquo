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

package com.abiquo.server.core.appslibrary;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.DiskFormatTypeAlias;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "diskformtattype")
public class DiskFormatTypeDto extends SingleResourceTransportDto
{

    private String uri, description;

    private DiskFormatTypeAlias alias;

    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer Id)
    {
        id = Id;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(final String Uri)
    {
        uri = Uri;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String Description)
    {
        description = Description;
    }

    public DiskFormatTypeAlias getAlias()
    {
        return alias;
    }

    public void setAlias(final DiskFormatTypeAlias Alias)
    {
        alias = Alias;
    }

    /*
     * public static final DiskFormatType[] VBOX_COMPATIBLES = new DiskFormatType[] {VMDK_SPARSE,
     * VHD_FLAT, VHD_SPARSE, VDI_FLAT, VDI_SPARSE}; public static final DiskFormatType[]
     * KVM_COMPATIBLES = new DiskFormatType[] {RAW, VMDK_SPARSE, VMDK_FLAT, VHD_FLAT, VHD_SPARSE,
     * QCOW2_FLAT, QCOW2_SPARSE}; public static final DiskFormatType[] XEN_COMPATIBLES = new
     * DiskFormatType[] {VMDK_FLAT}; public static final DiskFormatType[] VMWARE_COMPATIBLES = new
     * DiskFormatType[] {VMDK_FLAT, VMDK_SPARSE}; public static final DiskFormatType[]
     * HYPERV_COMPATIBLES = new DiskFormatType[] {VHD_FLAT, VHD_SPARSE}; public static final
     * DiskFormatType[] XENSERVER_COMPATIBLES = HYPERV_COMPATIBLES;
     */

}
