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

/**
 * 
 */
package com.abiquo.virtualfactory.exception;

/**
 * @author jdevesa
 *
 */
public class VirtualNetworkException extends Exception
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3785279080652273241L;

    /**
     * Instantiates a new virtual machine exception.
     */
    public VirtualNetworkException()
    {
        super();
    }

    /**
     * Instantiates a new virtual network exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public VirtualNetworkException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Instantiates a new virtual machine exception.
     * 
     * @param message the message
     */
    public VirtualNetworkException(String message)
    {
        super(message);
    }

    /**
     * Instantiates a new virtual machine exception.
     * 
     * @param cause the cause
     */
    public VirtualNetworkException(Throwable cause)
    {
        super(cause);
    }
}
