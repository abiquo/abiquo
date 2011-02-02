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

package com.abiquo.mailman.velocity.constant;

/**
 * Velocity utility constants.
 */
public class VelocityConstants
{
    public static final String RESOURCE_LOADER = "resource.loader";

    public static final String RESOURCE_LOADER_VALUE = "ds";

    public static final String RESOURCE_LOADER_DESCRIPTION = "ds.resource.loader.description";

    public static final String RESOURCE_LOADER_DESCRIPTION_VALUE =
        "Velocity DataSource Resource Loader";

    public static final String RESOURCE_LOADER_CLASS = "ds.resource.loader.class";

    public static final String RESOURCE_LOADER_CLASS_VALUE =
        "com.abiquo.mailman.velocity.DBResourceLoader";

    public static final String RESOURCE_LOADER_CACHE = "ds.resource.loader.cache";

    public static final String RESOURCE_LOADER_CACHE_VALUE = "false";

    public static final String RESOURCE_LOADER_CHECK_CACHE =
        "ds.resource.loader.modificationCheckInterval";

    public static final String RESOURCE_LOADER_CHECK_CACHE_VALUE = "0";

    public static final String TEMPLATE = "template";
}
