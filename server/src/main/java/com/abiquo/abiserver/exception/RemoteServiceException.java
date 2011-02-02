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
package com.abiquo.abiserver.exception;

/**
 * This class encapsulates all the RemoteService possible exceptions
 * 
 * @author pnavarro@abiquo.com
 */
public class RemoteServiceException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initialize the exception with a message. (Throwing a new exception)
     * 
     * @param message Indication
     */
    public RemoteServiceException(String message)
    {
        super(message);
    }

    /**
     * Initialize the exception with a message an an existing exception. (To encapsulate)
     * 
     * @param message Message indication the reason of the exception
     * @param e The exception to be thrown
     */
    public RemoteServiceException(String message, Throwable e)
    {
        super(message, e);
    }
}
