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

package com.abiquo.commons.amqp.util;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * A collection of helper methods to wrap the use of Jackson.
 * 
 * @author eruiz@abiquo.com
 */
public class JSONUtils
{
    /**
     * Serializes the given Object to JSON and returns the result in array of bytes.
     * 
     * @param value The object to serialize.
     * @return A byte array representing the JSON serialization of the object. A null value if the
     *         serialization fails.
     */
    public static byte[] serialize(Object value)
    {
        ObjectMapper mapper = new ObjectMapper();

        try
        {
            return mapper.writeValueAsBytes(value);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Deserializes the given byte array JSON serialization.
     * 
     * @param bytes A byte array representing the JSON serialization of an object.
     * @param type The Class of the object to deserialize
     * @return The deserialized object or null if the process fails.
     */
    public static <T> T deserialize(byte[] bytes, Class<T> type)
    {
        ObjectMapper mapper = new ObjectMapper();
        String content = new String(bytes);

        try
        {
            return (T) mapper.readValue(content, type);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
