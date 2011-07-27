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

package com.abiquo.nodecollector.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.utils.AnnotationUtils;
import com.abiquo.nodecollector.utils.HypervisorCollectorComparator;

/**
 * Class that manages the plugins to load.
 * 
 * @author jdevesa@abiquo.com
 */
public class PluginLoader
{
    public static PluginLoader loadedPlugins;

    private List<HypervisorType> listOfAllowedHypervisors;

    /** The default collector package. */
    private static final String COLLECTOR_PACKAGE =
        PluginLoader.class.getPackage().getName() + ".collectors";

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

    /**
     * @return the only instance of
     */
    public static PluginLoader getInstance()
    {
        if (loadedPlugins == null)
        {
            loadedPlugins = new PluginLoader();
        }

        return loadedPlugins;
    }

    /**
     * Load all the plugins into the static class in order to load only one time at server start.
     */
    public void loadPlugins()
    {
        try
        {
            listOfAllowedHypervisors = new ArrayList<HypervisorType>();

            // Get the property 'abiquo.nodecollector.plugins' to know the allowed hypervisor types.
            String hypervisors = System.getProperty("abiquo.nodecollector.plugins");
            if (hypervisors != null)
            {
                if (!hypervisors.isEmpty())
                {
                    for (String hypervisor : hypervisors.split("\\,"))
                    {
                        try
                        {

                            HypervisorType newHypervisorType =
                                HypervisorType.valueOf(hypervisor.trim());
                            if (!listOfAllowedHypervisors.contains(newHypervisorType))
                            {
                                listOfAllowedHypervisors.add(newHypervisorType);
                                LOGGER.info("Loaded plugin for Hypervisor "
                                    + newHypervisorType.name());
                            }
                            else
                            {
                                LOGGER.warn("Duplicated Hypervisor Type in system properties: "
                                    + hypervisor.trim());
                            }
                        }
                        catch (IllegalArgumentException e)
                        {
                            LOGGER.error("Unknown hypervisor type " + hypervisor + ". Ignored.");
                        }
                    }
                }

            }
            else
            {
                // enable backwards compatible.
                for (HypervisorType hyp : HypervisorType.values())
                {
                    listOfAllowedHypervisors.add(hyp);
                    LOGGER.info("Loaded plugin for Hypervisor " + hyp.name());
                }
            }

            if (listOfAllowedHypervisors.size() == 0)
            {
                LOGGER.warn("Any hypervisor plugin has been loaded. ");
            }

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            LOGGER.error("Could not find Collector classes");
        }

    }

    /**
     * @return the plugins
     */
    public List<HypervisorCollector> getAllPlugins()
    {
        List<HypervisorCollector> collectors = new ArrayList<HypervisorCollector>();

        try
        {
            // Find all Collector classes
            List<Class< ? extends Object>> collectorClasses =
                AnnotationUtils.findAnnotatedClasses(Collector.class, COLLECTOR_PACKAGE);

            if (collectorClasses != null && !collectorClasses.isEmpty())
            {
                // Instantiate collectors and add them to list
                for (Class< ? extends Object> clazz : collectorClasses)
                {
                    HypervisorCollector newInstance =
                        clazz.asSubclass(HypervisorCollector.class).getConstructor().newInstance();

                    // Load only the hypervisors defined in the 'abiquo.nodecollector.plugins'
                    // property.
                    if (listOfAllowedHypervisors.contains(newInstance.getHypervisorType()))
                    {
                        collectors.add(newInstance);
                    }
                }
            }

            Collections.sort(collectors, new HypervisorCollectorComparator());
            return collectors;
        }
        catch (Exception e)
        {
            LOGGER.error("Could not retrieve the list of plugins");
            return collectors;
        }

    }

    /**
     * @return only a plugin
     */
    public HypervisorCollector getPlugin(final HypervisorType type)
    {
        List<HypervisorCollector> allPlugins = getAllPlugins();
        for (HypervisorCollector currentPlugin : allPlugins)
        {
            if (currentPlugin.getHypervisorType().equals(type))
            {
                return currentPlugin;
            }
        }

        return null;
    }

}
