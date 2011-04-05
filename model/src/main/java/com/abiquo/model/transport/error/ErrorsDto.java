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

package com.abiquo.model.transport.error;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * Represent a collection of errors
 */
@XmlRootElement(name = "errors")
public class ErrorsDto extends WrapperDto<ErrorDto>
{
    @XmlElement(name = "error")
    public List<ErrorDto> getCollection()
    {
        if (collection == null)
        {
            collection = new ArrayList<ErrorDto>();
        }
        return collection;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("");
        for (ErrorDto error : getCollection())
        {
            builder.append(error.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        return 31 * toString().hashCode();
    }
}
