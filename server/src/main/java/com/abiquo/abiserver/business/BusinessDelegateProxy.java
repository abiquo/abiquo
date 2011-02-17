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
package com.abiquo.abiserver.business;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.PersistenceException;

import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * Proxy to check user session before calling business logic.
 * 
 * @author ibarrera
 * @see BasicCommand#execute(UserSession, String[], Object[], Class)
 */
public class BusinessDelegateProxy<D> implements InvocationHandler
{
    /** The user session to check. */
    private UserSession userSession;

    /** The target object containing the business logic. */
    private D delegate;

    /**
     * Generates a {@link BusinessDelegateProxy} for the specified class.
     * 
     * @param <T> The type of the target class.
     * @param userSession The user session to check.
     * @param delegate The delegating object.
     * @param proxiedClass The interface class to intercept.
     * @return A new <code>BusinessDelegateProxy</code> to check user session before invoking
     *         business logic.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T getInstance(final UserSession userSession, final T delegate,
        final Class<T> proxiedClass)
    {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[] {proxiedClass}, new BusinessDelegateProxy<T>(userSession, delegate));
    }

    /**
     * Creates a new instance of the delegating proxy.
     * <p>
     * This proxy should only be instantiated using the factory method.
     * 
     * @param userSession The user session.
     * @param delegate The delegating object.
     */
    private BusinessDelegateProxy(final UserSession userSession, final D delegate)
    {
        super();
        this.userSession = userSession;
        this.delegate = delegate;
    }

    /**
     * Intercepts all calls to business logic and checks user session.
     * 
     * @param target The target object to invoke.
     * @param method The target method to invoke.
     * @param args The arguments of the method.
     * @throws Throwable If an exception is thrown on the target method.
     * @see BasicCommand#execute(UserSession, String[], Object[], Class)
     */
    @Override
    public Object invoke(final Object target, final Method method, final Object[] args)
        throws Exception, Throwable
    {
        // Check database connectivity
        if (!HibernateDAOFactory.instance().pingDB())
        {
            throw new PersistenceException("Could not connect to database. "
                + "Please contact the cloud administrator.");
        }

        // Check if the user session is valid and if the user have permissions
        // to execute the target method
        AuthService.getInstance().checkUserPermissions(userSession, method.getName());

        try
        {
            return method.invoke(delegate, args);
        }
        catch (InvocationTargetException ex)
        {
            // Re-throw the exception thrown by the target method
            throw ex.getTargetException();
        }
    }

}
