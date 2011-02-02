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

package com.abiquo.server.core.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.softwarementors.bzngine.entities.test.EntityTestBase;

public abstract class GenericEntityTestBase<ID extends Serializable, T extends GenericEnityBase<ID>>
    extends EntityTestBase<T>
{

    // @Test
    public void toString_forCodeCoverage()
    {
        Assert.assertFalse(createUniqueEntity().toString().equals(""));
    }

    @Test
    public void test_isValid()
    {
        Assert.assertTrue(createUniqueEntity().isValid());
    }

    private <V> boolean executeIsValidXXXMethod(Class< ? > entityClass, String property,
        Class<V> valueClass, V value) throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, SecurityException, NoSuchMethodException
    {
        assert entityClass != null;
        assert !StringUtils.isEmpty(property);
        assert valueClass != null;

        Class< ? >[] noGroups = new Class[] {};
        Method isValidMethod =
            entityClass.getMethod("isValid" + StringUtils.capitalize(property), valueClass,
                Class[].class);
        return ((Boolean) isValidMethod.invoke(null, value, noGroups)).booleanValue();

    }

    @SuppressWarnings("unchecked")
    // We have a typecast to a generic class...
    private <V> Set<ConstraintViolation< ? >> executeGetXXXValidationInformationMethod(
        Class< ? > entityClass, String property, Class<V> valueClass, V value)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
        SecurityException, NoSuchMethodException
    {
        assert entityClass != null;
        assert !StringUtils.isEmpty(property);
        assert valueClass != null;

        Class< ? >[] noGroups = new Class[] {};
        Method validationInformationMethod =
            entityClass.getMethod("get" + StringUtils.capitalize(property)
                + "ValidationInformation", valueClass, Class[].class);
        return (Set<ConstraintViolation< ? >>) validationInformationMethod.invoke(null, value,
            noGroups);
    }

    protected <V> void assertPropertyValidationSupportOk(Class< ? > entityClass, String property,
        Class<V> valueClass, V validValue, V invalidValue) throws SecurityException,
        NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
        InvocationTargetException
    {
        assert entityClass != null;
        assert !StringUtils.isEmpty(property);
        assert valueClass != null;

        Assert.assertTrue(executeIsValidXXXMethod(entityClass, property, valueClass, validValue));
        Assert
            .assertFalse(executeIsValidXXXMethod(entityClass, property, valueClass, invalidValue));
        Assert.assertTrue(executeGetXXXValidationInformationMethod(entityClass, property,
            valueClass, validValue).isEmpty());
        Assert.assertFalse(executeGetXXXValidationInformationMethod(entityClass, property,
            valueClass, invalidValue).isEmpty());
    }

    protected <V> void assertPropertyValidationSupportOk(Class< ? > entityClass, String property,
        Class<V> valueClass, V validValue) throws SecurityException, NoSuchMethodException,
        IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        assert entityClass != null;
        assert !StringUtils.isEmpty(property);
        assert valueClass != null;

        Assert.assertTrue(executeIsValidXXXMethod(entityClass, property, valueClass, validValue));
        Assert.assertTrue(executeGetXXXValidationInformationMethod(entityClass, property,
            valueClass, validValue).isEmpty());
    }
}
