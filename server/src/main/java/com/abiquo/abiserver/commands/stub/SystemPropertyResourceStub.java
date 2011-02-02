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
package com.abiquo.abiserver.commands.stub;

import java.util.Collection;

import com.abiquo.abiserver.pojo.config.SystemProperty;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * Stub to connect to the System properties API functionallity.
 * 
 * @author ibarrera
 */
public interface SystemPropertyResourceStub
{

    /**
     * Gets the existing System Properties.
     * 
     * @param component The name of the component used to filter properties.
     * @return The list of properties for the given component.
     */
    public DataResult<Collection<SystemProperty>> getSystemProperties(String component);

    /**
     * Replace the existing System Properties by the given ones.
     * 
     * @param component The component to change.
     * @param properties The replacements for the System properties.
     * @return The new System properties.
     */
    public DataResult<Collection<SystemProperty>> modifySystemProperties(String component,
        Collection<SystemProperty> properties);

}
