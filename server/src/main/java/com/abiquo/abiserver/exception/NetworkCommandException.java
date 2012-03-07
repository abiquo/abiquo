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
 * This class encapsulates all the NetworkCommand possible exceptions
 * 
 * @author jdevesa@abiquo.com
 */
public class NetworkCommandException extends AbiCloudException
{
    /**
     * Generated serial version
     */
    private static final long serialVersionUID = -6181873600795081677L;

    /**
     * Initialize the exception with a message. (Throwing a new exception)
     * 
     * @param message Indication
     */
    public NetworkCommandException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param abicloudErrorCode
     */
    public NetworkCommandException(String message, int abicloudErrorCode)
    {
        super(message, abicloudErrorCode);
    }

    /**
     * Initialize the exception with a message an an existing exception. (To encapsulate)
     * 
     * @param message Message indication the reason of the exception
     * @param e The exception to be thrown
     */
    public NetworkCommandException(String message, Throwable e)
    {
        super(message, e);
    }

    /**
     * Create the exception with existing message and error code
     * 
     * @param message indication the reason of the exception
     * @param e The exception to be thrown
     * @param abicloudErrorCode code error
     */
    public NetworkCommandException(String message, Throwable e, int abicloudErrorCode)
    {
        super(message, e, abicloudErrorCode);
    }

    /**
     * Create the exception only with the throwable argument
     * 
     * @param e The exception to be thrown
     */
    public NetworkCommandException(Throwable e)
    {
        super(e);
    }

    /**
     * Create the exception with the throwable and the error code
     * 
     * @param e The exception to be thrown
     * @param abicloudErrorCode code error
     */
    public NetworkCommandException(Throwable e, int abicloudErrorCode)
    {
        super(e, abicloudErrorCode);
    }
}
