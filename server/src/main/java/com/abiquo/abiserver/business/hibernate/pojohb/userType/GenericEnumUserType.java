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

package com.abiquo.abiserver.business.hibernate.pojohb.userType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;
import org.hibernate.type.TypeFactory;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class GenericEnumUserType implements UserType, ParameterizedType
{
    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";

    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";

    private Class< ? extends Enum> enumClass;

    private Class< ? > identifierType;

    private Method identifierMethod;

    private Method valueOfMethod;

    private NullableType type;

    private int[] sqlTypes;

    public void setParameterValues(Properties parameters)
    {
        String enumClassName = parameters.getProperty("enumClass");
        try
        {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        }
        catch (ClassNotFoundException cfne)
        {
            throw new HibernateException("Enum class not found", cfne);
        }

        String identifierMethodName =
            parameters.getProperty("identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);

        try
        {
            identifierMethod = enumClass.getMethod(identifierMethodName, new Class[0]);
            identifierType = identifierMethod.getReturnType();
        }
        catch (Exception e)
        {
            throw new HibernateException("Failed to obtain identifier method", e);
        }

        type = (NullableType) TypeFactory.basic(identifierType.getName());

        if (type == null)
            throw new HibernateException("Unsupported identifier type " + identifierType.getName());

        sqlTypes = new int[] {type.sqlType()};

        String valueOfMethodName =
            parameters.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);

        try
        {
            valueOfMethod = enumClass.getMethod(valueOfMethodName, new Class[] {identifierType});
        }
        catch (Exception e)
        {
            throw new HibernateException("Failed to obtain valueOf method", e);
        }
    }

    public Class returnedClass()
    {
        return enumClass;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
        throws HibernateException, SQLException
    {
        Object identifier = type.get(rs, names[0]);
        if (rs.wasNull())
        {
            return null;
        }

        try
        {
            return valueOfMethod.invoke(enumClass, new Object[] {identifier});
        }
        catch (Exception e)
        {
            throw new HibernateException("Exception while invoking valueOf method '"
                + valueOfMethod.getName() + "' of " + "enumeration class '" + enumClass + "'", e);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
        throws HibernateException, SQLException
    {
        try
        {
            if (value == null)
            {
                st.setNull(index, type.sqlType());
            }
            else
            {
                Object identifier = identifierMethod.invoke(value, new Object[0]);
                type.set(st, identifier, index);
            }
        }
        catch (Exception e)
        {
            throw new HibernateException("Exception while invoking identifierMethod '"
                + identifierMethod.getName() + "' of " + "enumeration class '" + enumClass + "'", e);
        }
    }

    public int[] sqlTypes()
    {
        return sqlTypes;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException
    {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException
    {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException
    {
        return x.hashCode();
    }

    public boolean isMutable()
    {
        return false;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return original;
    }
}
