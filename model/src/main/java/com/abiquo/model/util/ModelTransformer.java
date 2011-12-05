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

package com.abiquo.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class ModelTransformer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTransformer.class);

    public static <T> T transportFromPersistence(final Class<T> clazz, final Object persistent)
        throws Exception
    {
        return transform(clazz, clazz, persistent);
    }

    public static <T> T persistenceFromTransport(final Class<T> clazz, final Object transport)
        throws Exception
    {
        return transform(transport.getClass(), clazz, transport);
    }

    public static <T> T transform(final Class sourceClass, final Class<T> targetClass,
        final Object template) throws Exception
    {
        T instance = targetClass.newInstance();
        transform(sourceClass, targetClass, template, instance);
        return instance;
    }

    public static <T> void transform(final Class sourceClass, final Class<T> targetClass,
        final Object source, final T target) throws Exception
    {
        Field[] transportFields = sourceClass.getDeclaredFields();
        Class superClass = sourceClass.getSuperclass();
        while (!superClass.getSimpleName().equalsIgnoreCase("SingleResourceTransportDto"))
        {
            transportFields =
                (Field[]) ArrayUtils.addAll(transportFields, superClass.getDeclaredFields());
            superClass = superClass.getSuperclass();
        }

        for (Field field : transportFields)
        {
            int modifiers = field.getModifiers();
            if (!Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers))
            {
                String name = field.getName();
                try
                {
                    if (fieldExist(name, targetClass) && fieldExist(name, source.getClass())
                        || getterExist(name, source.getClass())
                        && setterExist(name, targetClass, field.getType()))
                    {
                        Object value =
                            getter(name, source.getClass()).invoke(source, new Object[0]);

                        if (setterExist(name, targetClass, field.getType()))
                        {
                            setter(name, targetClass, field.getType()).invoke(target,
                                new Object[] {value});
                        }

                    }
                }
                catch (InvocationTargetException e)
                {
                    LOGGER.debug("Unsupporterd Operation get or set on " + name + " at class "
                        + source.getClass().getSimpleName() + " or "
                        + target.getClass().getSimpleName());
                }
            }
        }
    }

    private static Method getter(final String fieldName, final Class clazz) throws Exception
    {
        try
        {
            return getter("get", fieldName, clazz);
        }
        catch (NoSuchMethodException ex)
        {
            return getter("is", fieldName, clazz);
        }
    }

    private static Method getter(final String prefix, final String fieldName, final Class clazz)
        throws Exception
    {
        String name = prefix + StringUtils.capitalize(fieldName);
        return clazz.getMethod(name, new Class[0]);
    }

    private static Method setter(final String fieldName, final Class clazz, final Class type)
        throws Exception
    {
        String name = "set" + StringUtils.capitalize(fieldName);
        Method method = clazz.getMethod(name, new Class[] {type});

        if (method != null)
        {
            method.setAccessible(true);
        }
        return method;
    }

    private static boolean fieldExist(final String fieldName, final Class clazz) throws Exception
    {
        try
        {
            return clazz.getDeclaredField(fieldName) != null;
        }
        catch (NoSuchFieldException ex)
        {
            return false;
        }
    }

    private static boolean getterExist(final String fieldName, final Class clazz) throws Exception
    {
        try
        {
            return getter(fieldName, clazz) != null;
        }
        catch (NoSuchMethodException ex)
        {
            return false;
        }
    }

    private static boolean setterExist(final String fieldName, final Class clazz, final Class type)
        throws Exception
    {
        try
        {
            return setter(fieldName, clazz, type) != null;
        }
        catch (NoSuchMethodException ex)
        {
            return false;
        }
    }
}
