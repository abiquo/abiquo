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
 * Abiquo premium edition
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

package com.abiquo.abiserver.pojo.virtualappliance;

import java.util.ArrayList;
import java.util.Collection;

public class VirtualDatacentersListResult
{

    // The List of VirtualDataCenter (limited by a length) that match the ListResult given to
    // retrieve the list of VirtualDataCenters
    private Collection<VirtualDataCenter> virtualDatacentersList;

    // The total number of VirtualDataCenters that match the ListResult given to
    // retrieve the list of VirtualDataCenters
    private int totalVirtualDatacenters;

    public VirtualDatacentersListResult()
    {
        virtualDatacentersList = new ArrayList<VirtualDataCenter>();
        totalVirtualDatacenters = 0;
    }

    public Collection<VirtualDataCenter> getVirtualDatacentersList()
    {
        return virtualDatacentersList;
    }

    public void setVirtualDatacentersList(final Collection<VirtualDataCenter> virtualDatacentersList)
    {
        this.virtualDatacentersList = virtualDatacentersList;
    }

    public int getTotalVirtualDatacenters()
    {
        return totalVirtualDatacenters;
    }

    public void setTotalVirtualDatacenters(final int totalVirtualDatacenters)
    {
        this.totalVirtualDatacenters = totalVirtualDatacenters;
    }
}
