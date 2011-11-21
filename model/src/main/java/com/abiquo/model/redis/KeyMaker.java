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

package com.abiquo.model.redis;

/**
 * Helper class to build keys for redis models.
 * 
 * @author eruiz
 */
public class KeyMaker
{
    protected String namespace;

    public KeyMaker(String namespace)
    {
        this.namespace = namespace;
    }

    public KeyMaker(Class< ? > clazz)
    {
        this.namespace = clazz.getSimpleName();
    }

    public String make(String... namespaces)
    {
        StringBuilder builder = new StringBuilder(this.namespace);

        for (String name : namespaces)
        {
            builder.append(":").append(name);
        }

        return builder.toString();
    }
}
