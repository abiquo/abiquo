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

/**
 * 
 */
package com.abiquo.server.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to encapsulate a common arrayList and set its max result for pagging purposes.
 * 
 * @author jdevesa@abiquo.com
 */
public class PagedList<T> extends ArrayList<T>
{
    /**
     * Generated serial version
     */
    private static final long serialVersionUID = 1723031368985519696L;

    /**
     * Total results of the query.
     */
    private Integer totalResults;

    /**
     * Size of the page
     */
    private Integer pageSize;

    /**
     * Current page
     */
    private Integer currentPage;

    public PagedList()
    {
        super();
    }

    public PagedList(List<T> resultList)
    {
        super(resultList);
    }

    /**
     * @param totalResults the totalResults to set
     */
    public void setTotalResults(Integer totalResults)
    {
        this.totalResults = totalResults;
    }

    /**
     * @return the totalResults
     */
    public Integer getTotalResults()
    {
        return totalResults;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize()
    {
        return pageSize;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(Integer currentPage)
    {
        this.currentPage = currentPage;
    }

    /**
     * @return the currentPage
     */
    public Integer getCurrentPage()
    {
        return currentPage;
    }
}
