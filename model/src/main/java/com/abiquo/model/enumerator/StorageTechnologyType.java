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

public enum StorageTechnologyType
{
    ZFS(8080, 3260),
    LVM(8080, 3260),
    NEXENTA(8180, 3260),
    NETAPP(8280, 3260);
    
    private Integer defaultManagementPort;
    private Integer defaultSCSIPort;
    
    private static final String DEFAULT_CONTEXT_PATH = "/fs_rest/";

    private StorageTechnologyType(final Integer defaultManagementPort, final Integer defaultSCSIPort)
    {
        this.defaultManagementPort = defaultManagementPort;
        this.defaultSCSIPort = defaultSCSIPort;
    }

    public Integer getManagementPort()
    {
        return defaultManagementPort;
    }
    
    public Integer getISCSIPort()
    {
        return defaultSCSIPort;
    }

    public String getMapping()
    {
        return DEFAULT_CONTEXT_PATH;
    }
}
