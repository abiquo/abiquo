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
package com.abiquo.appliancemanager.exceptions;

import com.abiquo.am.exceptions.AMError;

public class AMException extends RuntimeException
{

    final AMError error;

    public AMError getError()
    {
        return error;
    }

    public AMException(final AMError error)
    {
        super(error.getMessage());
        this.error = error;
    }

    public AMException(final AMError error, final String msg)
    {
        super(String.format("%s\nDetail:\n%s", error.getMessage(), msg));
        this.error = error;
    }

    public AMException(final AMError error, final String msg, final Throwable th)
    {
        super(String.format("%s\nDetail:\n%s", error.getMessage(), msg), th);
        this.error = error;
    }

    public AMException(final AMError error, final Throwable th)
    {
        super(error.getMessage(), th);
        this.error = error;
    }
    
    
    

    public static String getErrorMessage(final Integer statusCode, final String fileUrl)
    {

        if (statusCode == 401)
        {
            return String.format("[Unauthorized] You might not have permissions to read "
                + "the file or folder at the following location: %s", fileUrl);
        }
        else if (statusCode == 403)
        {
            return String.format("[Forbidden] You might not have permissions to read "
                + "the file or folder at the following location: %s", fileUrl);
        }
        else if (statusCode == 404)
        {
            return String.format(
                "[Not Found] The file or folder at the location: %s does not exist", fileUrl);
        }
        else
        {
            return String.format("%d -  at : %s", statusCode, fileUrl);
        }
    }
    

}
