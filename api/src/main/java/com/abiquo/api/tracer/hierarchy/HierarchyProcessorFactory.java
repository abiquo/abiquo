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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Loads all the hierarchy processors and builds a {@link CompositeHierarchyProcessor} that will be
 * used to process all requests.
 * 
 * @author ibarrera
 */
@Service
public class HierarchyProcessorFactory extends AbstractFactoryBean<HierarchyProcessor>
{
    /** The Spring application context. */
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected HierarchyProcessor createInstance() throws Exception
    {
        // Load all processors from the Spring context
        Map<String, HierarchyProcessor> processors =
            applicationContext.getBeansOfType(getObjectType());

        // Build the composite processor
        CompositeHierarchyProcessor globalProcessor = new CompositeHierarchyProcessor();
        for (HierarchyProcessor processor : processors.values())
        {
            globalProcessor.addProcessor(processor);
        }

        return globalProcessor;
    }

    @Override
    public Class<HierarchyProcessor> getObjectType()
    {
        return HierarchyProcessor.class;
    }
}
