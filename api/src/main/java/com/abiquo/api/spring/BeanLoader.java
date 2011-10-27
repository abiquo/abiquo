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

package com.abiquo.api.spring;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class BeanLoader implements ApplicationContextAware
{
    private static BeanLoader instance;

    private ApplicationContext applicationContext;

    private BeanLoader()
    {
    }

    public static BeanLoader getInstance()
    {
        if (instance == null)
        {
            instance = new BeanLoader();
        }
        return instance;
    }

    public <T> T getBean(final Class<T> beanClass)
    {
        if (applicationContext == null)
        {
            throw new IllegalStateException("ApplicationContext has not been set. "
                + "BeanLoader must be configured in Spring's application context in order to be used.");
        }
        return BeanFactoryUtils.beanOfType(applicationContext, beanClass);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final String beanName, final Class<T> expectedClass)
    {
        if (applicationContext == null)
        {
            throw new IllegalStateException("ApplicationContext has not been set. "
                + "BeanLoader must be configured in Spring's application context in order to be used.");
        }
        return (T) applicationContext.getBean(beanName);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
}
