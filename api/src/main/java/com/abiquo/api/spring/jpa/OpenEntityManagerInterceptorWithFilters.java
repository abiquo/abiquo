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
package com.abiquo.api.spring.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.WebRequestInterceptor;

import com.abiquo.server.core.common.persistence.JPAConfiguration;

/**
 * OpenEntityManagerInView interceptor that is not coupled to the {@link WebRequestInterceptor}
 * interface.
 * <p>
 * This interceptor should be used in beans that need the Hibernate Session and are out of the scope
 * of Web requests, such as scheduled tasks.
 * 
 * @author Ignasi Barrera
 * @see OpenEntityManagerInViewFilter
 */
public class OpenEntityManagerInterceptorWithFilters extends EntityManagerFactoryAccessor implements
    MethodInterceptor
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(OpenEntityManagerInterceptorWithFilters.class);

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable
    {
        // Set the participate flag, to avoid binding again the EntityManagerHolder, and to avoid
        // unbinding it if it is already part of another transaction
        boolean participate = false;

        if (TransactionSynchronizationManager.hasResource(getEntityManagerFactory()))
        {
            // If there is an active transaction, just reuse it
            participate = true;
        }
        else
        {
            LOGGER.debug("Opening JPA EntityManager in OpenEntityManagerInterceptor");

            try
            {
                // If we are not part of another transaction, create the EntityManager and bind it
                // to the TransactionSynchronizationManager
                EntityManager em = createEntityManager();
                TransactionSynchronizationManager.bindResource(getEntityManagerFactory(),
                    new EntityManagerHolder(em));
            }
            catch (PersistenceException ex)
            {
                throw new DataAccessResourceFailureException("Could not create JPA EntityManager",
                    ex);
            }
        }

        Object ret = null;

        try
        {
            ret = invocation.proceed();
        }
        finally
        {
            // Only unbind the resource if we are not participating in an existing transaction
            if (!participate)
            {
                EntityManagerHolder emHolder =
                    (EntityManagerHolder) TransactionSynchronizationManager
                        .unbindResource(getEntityManagerFactory());
                logger.debug("Closing JPA EntityManager in OpenEntityManagerInterceptor");
                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
            }
        }

        return ret;
    }

    /**
     * Enable default hibernate filters after creating the {@link EntityManager}
     */
    @Override
    protected EntityManager createEntityManager() throws IllegalStateException
    {
        return JPAConfiguration.enableDefaultFilters(super.createEntityManager());
    }

}
