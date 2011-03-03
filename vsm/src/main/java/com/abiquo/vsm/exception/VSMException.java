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

package com.abiquo.vsm.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Exception thrown by REST resources.
 * 
 * @author ibarrera
 */
public class VSMException extends WebApplicationException
{
    private static final long serialVersionUID = 1L;

    /** The exception message. */
    private String message;

    /**
     * Creates a new <code>VSMException</code>.
     * 
     * @param message The error message.
     */
    public VSMException(String message)
    {
        super(Status.INTERNAL_SERVER_ERROR);
        this.message = message;
    }

    /**
     * Creates a new <code>VSMException</code>.
     * 
     * @param message The error message.
     * @param cause The exception cause.
     */
    public VSMException(String message, Throwable cause)
    {
        super(cause, Status.INTERNAL_SERVER_ERROR);
        this.message = message;
    }

    /**
     * Creates a new <code>VSMException</code>.
     * 
     * @param status The error status code.
     * @param message The error message.
     */
    public VSMException(Status status, String message)
    {
        super(status);
        this.message = message;
    }

    /**
     * Creates a new <code>VSMException</code>.
     * 
     * @param status The error status code.
     * @param message The error message.
     * @param cause The exception cause.
     */
    public VSMException(Status status, String message, Throwable cause)
    {
        super(cause, status);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

}
