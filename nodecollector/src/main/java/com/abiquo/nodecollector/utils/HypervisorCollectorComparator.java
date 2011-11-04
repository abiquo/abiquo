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
package com.abiquo.nodecollector.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.domain.HypervisorCollector;


/**
 * Comparator used to sort {@link HypervisorCollector} in node collector response.
 * 
 * @author ibarrera
 */
public class HypervisorCollectorComparator implements Comparator<HypervisorCollector>, Serializable
{
    /**
     * Generated serial version.
     */
    private static final long serialVersionUID = 1160486717610965593L;

    
    @Override
    public int compare(HypervisorCollector o1, HypervisorCollector o2)
    {
        return o1.getClass().getAnnotation(Collector.class).order()
        - o2.getClass().getAnnotation(Collector.class).order();
    }
}
