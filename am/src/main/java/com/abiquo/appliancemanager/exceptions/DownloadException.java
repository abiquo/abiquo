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

package com.abiquo.appliancemanager.exceptions;

import com.abiquo.am.exceptions.AMError;

/**
 * Thrown when some OVFPackage download fail.
 * 
 * @author apuig
 */
public class DownloadException extends AMException
{

    /** auto-generated serial UID. */
    private static final long serialVersionUID = 5639547252306480733L;

    public DownloadException(String ovfId, String file, String message)
    {
        super(AMError.OVF_DOWNLOAD, String.format("File [%s] on package [%s] failed.\n%s", file,
            ovfId, message));
    }

    public DownloadException(String ovfId, String file, Throwable cause)
    {
        super(AMError.OVF_DOWNLOAD, String.format("File [%s] on package [%s] failed", file, ovfId),
            cause);
    }

    public DownloadException(String message, Throwable cause)
    {
        super(AMError.OVF_DOWNLOAD, message, cause);
    }
}
