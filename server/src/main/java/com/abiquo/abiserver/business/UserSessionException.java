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
package com.abiquo.abiserver.business;

import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * Wrapper class to handle session exception problems and error messages related to use of
 * {@link BusinessDelegateProxy} in Flex Services.
 * 
 * @author destevez
 */
public class UserSessionException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private BasicResult result;

    public UserSessionException(BasicResult paramResult)
    {
        super(paramResult.getMessage());

        if (paramResult.getResultCode() == BasicResult.SESSION_INVALID)
        {
            paramResult.setMessage("Invalid Session. Please Log In again");
        }
        else if (paramResult.getResultCode() == BasicResult.SESSION_TIMEOUT)
        {
            paramResult.setMessage("Session timeout. Please Log In again");
        }
        else if (paramResult.getResultCode() == BasicResult.SESSION_MAX_NUM_REACHED)
        {
            paramResult.setMessage("Too many users logged in the same time. Please wait");
        }
        else if (paramResult.getResultCode() == BasicResult.NOT_AUTHORIZED
            || paramResult.getMessage().equals("Forbidden"))
        {
            paramResult.setMessage("You do not have enough permissions to perform this task.");
        }
        else
        {
            paramResult.setMessage("Unhandled session exception");
        }

        setResult(paramResult);
    }

    public void setResult(BasicResult result)
    {
        this.result = result;
    }

    public BasicResult getResult()
    {
        return result;
    }

}
