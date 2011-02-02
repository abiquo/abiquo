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

/**
 * This class is used when a client wants to retrieve a list of IPs (public or private). Using this
 * class, that request can include filtering or pagination information
 * 
 * @author Oliver
 */
public class ListRequest
{
    private Integer offset;

    private String filterLike;

    private Integer numberOfNodes;

    private Boolean asc;

    private String orderBy;

    /**
     * @return the offset
     */
    public Integer getOffset()
    {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }

    /**
     * @return the numberOfNodes
     */
    public Integer getNumberOfNodes()
    {
        return numberOfNodes;
    }

    /**
     * @param numberOfNodes the numberOfNodes to set
     */
    public void setNumberOfNodes(Integer numberOfNodes)
    {
        this.numberOfNodes = numberOfNodes;
    }

    public String getFilterLike()
    {
        return filterLike;
    }

    public void setFilterLike(String filterLike)
    {
        this.filterLike = filterLike;
    }

    /**
     * Flag indicating if the returned IP list should be orderer ascendant or descendant
     * 
     * @param asc
     */
    public Boolean getAsc()
    {
        return asc;
    }

    public void setAsc(Boolean asc)
    {
        this.asc = asc;
    }

    /**
     * String with the name of the attribute which the IP list should be ordered by
     * 
     * @return
     */
    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }
}
