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

package com.abiquo.abiserver.pojo.user;

public class UserListOptions
{
    private int offset;

    private int length;

    private String filter;

    private String orderBy;

    private Boolean asc;

    private Enterprise byEnterprise;

    private Boolean loggedOnly;

    public UserListOptions()
    {
        offset = 0;
        length = 100;
        filter = "";
        orderBy = "name";
        asc = true;
        byEnterprise = null;
        loggedOnly = false;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }

    public Boolean getAsc()
    {
        return asc;
    }

    public void setAsc(Boolean asc)
    {
        this.asc = asc;
    }

    public Enterprise getByEnterprise()
    {
        return byEnterprise;
    }

    public void setByEnterprise(Enterprise byEnterprise)
    {
        this.byEnterprise = byEnterprise;
    }

    public Boolean getLoggedOnly()
    {
        return loggedOnly;
    }

    public void setLoggedOnly(Boolean loggedOnly)
    {
        this.loggedOnly = loggedOnly;
    }
}
