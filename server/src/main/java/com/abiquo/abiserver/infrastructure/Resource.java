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

package com.abiquo.abiserver.infrastructure;

/**
 * This class represents a resource allocated in a computer system, whatever is virtual or physical.
 * 
 * @author jdevesa
 */
public class Resource
{
    /**
     * Defines which kind of resource we are talking about.
     */
    private String resourcetype;

    /**
     * Name of the resource.
     */
    private String elementName;

    /**
     * Where we can locate this resource.
     */
    private String address;

    /**
     * @return the resourcetype
     */
    public String getResourcetype()
    {
        return resourcetype;
    }

    /**
     * @param resourcetype the resourcetype to set
     */
    public void setResourcetype(final String resourcetype)
    {
        this.resourcetype = resourcetype;
    }

    /**
     * @return the elementName
     */
    public String getElementName()
    {
        return elementName;
    }

    /**
     * @param elementName the elementName to set
     */
    public void setElementName(final String elementName)
    {
        this.elementName = elementName;
    }

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(final String address)
    {
        this.address = address;
    }

}
