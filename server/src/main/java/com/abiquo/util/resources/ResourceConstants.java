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

/**
 * @author aodachi
 */
public class ResourceConstants
{

    /**
     * The path of the root directory containing properties stored in a simple XML format.
     */
    public static final String RESOURCES_PROPERTIES_XML_ROOT_DIR = "properties/xml/";

    /**
     * Root directory of properties stored as simple flat files (.properties).
     */
    public static final String RESOURCES_PROPERTIES_TXT_ROOT_DIR = "properties/txt/";

    /**
     * Parent directory of resource bundles stored as properties in simple XML format
     */
    public static final String RESOURCES_LOCALE_XML_ROOT_DIR = "locale/xml/";

    /**
     * Parent directory of resource bundles stored as properties in simple flat files (.properties)
     */
    public static final String RESOURCES_LOCALE_TXT_ROOT_DIR = "locale/txt/";

    public static final ClassLoader CLASS_LOADER = ResourceConstants.class.getClassLoader();

}
