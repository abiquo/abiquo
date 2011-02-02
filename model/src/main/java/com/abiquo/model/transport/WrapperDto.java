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

package com.abiquo.model.transport;

import java.util.ArrayList;
import java.util.List;

public abstract class WrapperDto<T> extends SingleResourceTransportDto
{
    protected List<T> collection = new ArrayList<T>();

    private Integer totalSize;

    public abstract List<T> getCollection();

    public void add(T element)
    {
        collection.add(element);
    }

    public boolean isEmpty()
    {
        return collection.isEmpty();
    }

    public void addAll(WrapperDto<T> errors)
    {
        collection.addAll(errors.getCollection());
    }

    public void setTotalSize(Integer totalSize)
    {
        this.totalSize = totalSize;
    }

    public Integer getTotalSize()
    {
        return totalSize;
    }
}
