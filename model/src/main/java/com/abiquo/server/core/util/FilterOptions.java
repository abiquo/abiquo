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

package com.abiquo.server.core.util;

public class FilterOptions
{
    private Integer startwith;

    private Integer limit;

    private String filter;

    private Boolean asc;

    private String orderBy;

    public FilterOptions(final Integer startwith, final Integer limit, final String filter,
        final String orderBy, final Boolean asc)
    {
        setStartwith(startwith);
        setLimit(limit);
        setFilter(filter);
        setOrderBy(orderBy);
        setAsc(asc);
    }

    public void setStartwith(final Integer startwith)
    {
        this.startwith = startwith;
    }

    public Integer getStartwith()
    {
        return startwith;
    }

    public void setLimit(final Integer limit)
    {
        this.limit = limit;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setFilter(final String filter)
    {
        this.filter = filter;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setAsc(final Boolean asc)
    {
        this.asc = asc;
    }

    public Boolean getAsc()
    {
        return asc;
    }

    public void setOrderBy(final String orderBy)
    {
        this.orderBy = orderBy;
    }

    public String getOrderBy()
    {
        return orderBy;
    }
}
