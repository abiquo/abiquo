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

package com.abiquo.abiserver.pojo.result;

import java.util.List;

/**
 * Object used to return a subset of a list of elements, where a variable containing the total
 * number of elements is usually needed
 * 
 * @author Oliver
 * @param <E>
 */
public class ListResponse<E>
{

    private List<E> list;

    private int totalNumEntities;

    public List<E> getList()
    {
        return list;
    }

    public void setList(List<E> list)
    {
        this.list = list;
    }

    public int getTotalNumEntities()
    {
        return totalNumEntities;
    }

    public void setTotalNumEntities(int totalNumEntities)
    {
        this.totalNumEntities = totalNumEntities;
    }
}
