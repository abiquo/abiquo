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

package com.abiquo.abiserver.pojo.result;

public class BasicResult
{

    /* Result codes */
    public final static int STANDARD_RESULT = 0;

    public final static int SESSION_INVALID = 1;

    public final static int SESSION_TIMEOUT = 2;

    public final static int SESSION_MAX_NUM_REACHED = 3;

    public final static int USER_INVALID = 4;

    public final static int AUTHORIZATION_NEEDED = 5;

    public final static int NOT_AUTHORIZED = 6;

    public final static int VIRTUAL_IMAGE_IN_USE = 7;
    
    /** When NonBlocking service try to start a virtual application. */
    public final static int SOFT_LIMT_EXCEEDED = 8;
    
    /** When NonBlocking service try to start a virtual application. */
    public final static int HARD_LIMT_EXCEEDED = 9;
    
    /** When NonBlocking service try to start a virtual application. */
    public final static int CLOUD_LIMT_EXCEEDED = 10;
    
    /** When we try to delete a virtual image not managed by abicloud. */
    public final static int NOT_MANAGED_VIRTUAL_IMAGE=11;
    
    /** When we try to release a public ip from an enterprise used by a node. */
    public final static int PUBLIC_IP_USED=12;
       
    private Boolean success;

    private String message;

    private int resultCode;

    public BasicResult()
    {
        success = false;
        message = "";
        resultCode = BasicResult.STANDARD_RESULT;
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }

}
