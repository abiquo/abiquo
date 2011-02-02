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

package com.abiquo.util.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akpor
 */
public class ResourceBundleControl extends ResourceBundle.Control
{

    private static final Logger logger = LoggerFactory.getLogger(ResourceBundleControl.class);

    public ResourceBundleControl()
    {
        super();
    }

    @Override
    public List<String> getFormats(String baseName)
    {

        List<String> list = super.getFormats(baseName);

        ArrayList<String> arr = new ArrayList<String>();

        for (String str : list)
        {
            arr.add(str);
        }

        arr.add("xml");

        return arr;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
        ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException,
        IOException
    {

        // Get the bundleName, if locale.toString.length () > 0 bundleName = baseName_locale (e.g
        // baseName_es_Es) if not bundleName = baseName
        String bundleName = toBundleName(baseName, locale);

        logger.debug("BaseName:" + baseName + " locale:" + locale + " format:" + format
            + " bundleName: " + bundleName);

        if (format.equalsIgnoreCase("java.class"))
        {
            try
            {
                return (ResourceBundle) ResourceConstants.CLASS_LOADER.loadClass(bundleName)
                    .newInstance();
            }
            catch (Exception e)
            {
                return null;
            }

        }
        else if (format.equalsIgnoreCase("java.properties"))
        {

            String fileName =
                ResourceConstants.RESOURCES_LOCALE_TXT_ROOT_DIR + bundleName + ".properties";

            InputStream inStream = loader.getResourceAsStream(fileName);

            if (inStream != null)
            {
                return new PropertyResourceBundle(inStream);
            }
            else
            {
                return null;
            }

        }
        else if (format.equalsIgnoreCase("xml"))
        {

            String fileName = ResourceConstants.RESOURCES_LOCALE_XML_ROOT_DIR + bundleName + ".xml";

            return new XMLResourceBundle(loader.getResourceAsStream(fileName));

        }
        else
        {
            return null;
        }

    }

}
