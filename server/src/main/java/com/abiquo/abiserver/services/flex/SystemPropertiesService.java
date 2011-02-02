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
package com.abiquo.abiserver.services.flex;

import java.util.Collection;

import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.SystemPropertyResourceStub;
import com.abiquo.abiserver.commands.stub.impl.SystemPropertyResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.config.SystemProperty;
import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * This class defines all services related with properties
 * 
 * @author xfernandez
 */
public class SystemPropertiesService
{
    /**
     * Replace the existing System Properties by the given ones.
     * 
     * @param userSession The user session.
     * @param properties The replacements for the System properties.
     * @return The new System properties.
     */
    public BasicResult modifySystemProperties(UserSession userSession, String component,
        Collection<SystemProperty> properties)
    {
        SystemPropertyResourceStub proxy =
            APIStubFactory.getInstance(userSession, new SystemPropertyResourceStubImpl(),
                SystemPropertyResourceStub.class);

        try
        {
            return proxy.modifySystemProperties(component, properties);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * This method is used to interact with Properties command and get all the properties related
     * with a specified component
     * 
     * @param userSession the user logged
     * @param component the component to filter
     * @return the list of desired properties
     */
    public BasicResult getSystemProperties(UserSession userSession, String component)
    {
        SystemPropertyResourceStub proxy =
            APIStubFactory.getInstance(userSession, new SystemPropertyResourceStubImpl(),
                SystemPropertyResourceStub.class);

        try
        {
            return proxy.getSystemProperties(component);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

}
