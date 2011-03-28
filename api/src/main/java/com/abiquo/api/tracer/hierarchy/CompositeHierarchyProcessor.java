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
package com.abiquo.api.tracer.hierarchy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Chained hierarchy processor that delegates the processing method to a list of
 * {@link HierarchyProcessor}.
 * 
 * @author ibarrera
 */
public class CompositeHierarchyProcessor implements HierarchyProcessor
{
    /** The chain of processors. */
    private List<HierarchyProcessor> processors;

    /**
     * Creates an empty composite {@link HierarchyProcessor}.
     */
    public CompositeHierarchyProcessor()
    {
        this.processors = new LinkedList<HierarchyProcessor>();
    }

    @Override
    public void process(final String uri, final Map<String, String> resourceData)
    {
        for (HierarchyProcessor processor : processors)
        {
            processor.process(uri, resourceData);
        }
    }

    public void addProcessor(final HierarchyProcessor processor)
    {
        this.processors.add(processor);
    }

}
