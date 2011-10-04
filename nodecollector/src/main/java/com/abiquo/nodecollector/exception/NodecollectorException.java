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
package com.abiquo.nodecollector.exception;

import com.abiquo.model.transport.error.CommonError;

/**
 * @author jdevesa
 *
 */
public abstract class NodecollectorException extends Exception
{

    public static final String NODECOLLECTOR_ERROR = "NC_REMOTE_ERROR";
    private CommonError commonError;
    
    /**
     * Default serial version.
     */
    private static final long serialVersionUID = 1L;

    public NodecollectorException(String message, Throwable cause)
    {
        super(message, cause);
        setCommonError(new CommonError(NODECOLLECTOR_ERROR, message));
    }

    public NodecollectorException(String message)
    {
        super(message);
        setCommonError(new CommonError(NODECOLLECTOR_ERROR, message));
    }

    private void setCommonError(CommonError commonError)
    {
        this.commonError = commonError;
    }

    public CommonError getCommonError()
    {
        return commonError;
    }    
}
