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
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author aodachi
 */
public class XMLResourceBundle extends ResourceBundle
{

    private Properties properties;

    private Enumeration<String> keys;

    public XMLResourceBundle(InputStream inputStream) throws IOException
    {

        properties = new Properties();
        properties.loadFromXML(inputStream);

        keys = (Enumeration<String>) properties.propertyNames();
    }

    @Override
    public Object handleGetObject(String key)
    {
        return properties.getProperty(key);
    }

    @Override
    public Enumeration<String> getKeys()
    {
        return keys;
    }
}
