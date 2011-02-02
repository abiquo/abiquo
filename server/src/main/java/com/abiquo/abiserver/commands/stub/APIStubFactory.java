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

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * Proxy to check user session before performig remote API calls.
 * 
 * @author ibarrera
 */
public class APIStubFactory
{
    /**
     * Generates a {@link BusinessDelegateProxy} for the specified stub.
     * 
     * @param <T> The type of the target class.
     * @param userSession The user session to check.
     * @param delegate The delegating object.
     * @param proxiedClass The interface class to intercept.
     * @return A new <code>BusinessDelegateProxy</code> to check user session before invoking
     *         business logic.
     */
    public static <T extends Object> T getInstance(final UserSession userSession, final T delegate,
        final Class<T> proxiedClass)
    {
        if (delegate instanceof AbstractAPIStub)
        {
            // Set the current session in the delegate Objects
            ((AbstractAPIStub) delegate).currentSession = userSession;
        }

        return BusinessDelegateProxy.getInstance(userSession, delegate, proxiedClass);
    }

}
