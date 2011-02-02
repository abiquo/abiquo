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


/**
 * Thrown when some OVFPackage download fail.
 * 
 * @author apuig
 */
public class DownloadException extends RepositoryException
{

    /** auto-generated serial UID. */
    private static final long serialVersionUID = 5639547252306480733L;

    /** The OVF package ID being download. */
    protected String ovfId;

    /** The File on the OVF package causing this exception. */
    protected String file;

    public DownloadException(String ovfId, String file, String message)
    {
        super(message);
        this.file = file;
        this.ovfId = ovfId;
    }

    public DownloadException(String ovfId, String file, Throwable cause)
    {
        super(cause);
        this.file = file;
        this.ovfId = ovfId;
    }


    public DownloadException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Gets the OVF package ID being download.
     */
    public String getOvfId()
    {
        return ovfId;
    }

    /**
     * Gets the File on the OVF package causing this exception
     */
    public String getFile()
    {
        return file;
    }

    @Override
    public String toString()
    {
    	String cause;
    	if(getMessage() == null)
    	{
    		cause = getCause().getMessage();
    	}
    	else
    	{
    		cause = getMessage();
    	}

        return "File [" + file + "] on package [" + ovfId + "] failed duet " + cause;
    }

}
