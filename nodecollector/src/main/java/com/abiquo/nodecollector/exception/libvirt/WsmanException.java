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

package com.abiquo.nodecollector.exception.libvirt;

/**
 * Encapsulates all the Wiseman exceptions.
 * 
 * @author jdevesa
 */
public class WsmanException extends Exception
{

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>WsmanException</code> with the specified message and cause.
     * 
     * @param message message of the exception
     * @param cause cause of the exception
     */
    public WsmanException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new <code>WsmanException</code> with the specified message and.
     * 
     * @param message message of the exception
     */
    public WsmanException(final String message)
    {
        super(message);
    }

}
