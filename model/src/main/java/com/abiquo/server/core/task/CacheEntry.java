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

package com.abiquo.server.core.task;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.model.redis.RedisEntityBase;

/**
 * A simple cache entry, with id and a list of strings.
 * 
 * @author eruiz
 */
public class CacheEntry extends RedisEntityBase
{
    protected String id;

    protected List<String> values;

    public CacheEntry(final String id)
    {
        this.id = id;
        this.values = new ArrayList<String>();
    }

    @Override
    protected String getIdAsString()
    {
        return getId();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<String> getValues()
    {
        return values;
    }
}
