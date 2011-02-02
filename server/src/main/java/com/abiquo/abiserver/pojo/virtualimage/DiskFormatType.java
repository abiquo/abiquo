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

package com.abiquo.abiserver.pojo.virtualimage;

public class DiskFormatType
{
    private int id;

    private String name;

    private String uri;

    private String description;

    private String alias;

    public DiskFormatType()
    {

    }

    public DiskFormatType(int id)
    {
        this(com.abiquo.server.core.enumerator.DiskFormatType.fromId(id));
    }

    public DiskFormatType(com.abiquo.server.core.enumerator.DiskFormatType type)
    {
        this.id = type.id();
        this.name = type.name();
        this.uri = type.uri;
        this.description = type.description;
        this.alias = type.alias.toString();
    }

    public com.abiquo.server.core.enumerator.DiskFormatType toEnum()
    {
        return com.abiquo.server.core.enumerator.DiskFormatType.fromURI(uri);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

}
