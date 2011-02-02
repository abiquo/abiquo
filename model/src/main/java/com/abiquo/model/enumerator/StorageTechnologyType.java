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
    ZFS(3260, "/fs_rest/"),
    LVM(3260, "/fs_rest/"),
    NEXENTA(3260, "/fs_rest/"),
    NETAPP(3260, "/fs_rest/");

    private Integer port;

    private String mapping;

    private StorageTechnologyType(final Integer port, final String mapping)
    {
        this.port = port;
        this.mapping = mapping;
    }

    public Integer getPort()
    {
        return port;
    }

    public String getMapping()
    {
        return mapping;
    }
}
