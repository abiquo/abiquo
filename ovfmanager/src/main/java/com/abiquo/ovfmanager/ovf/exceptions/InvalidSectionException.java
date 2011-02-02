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

package com.abiquo.ovfmanager.ovf.exceptions;

import org.dmtf.schemas.ovf.envelope._1.SectionType;

/**
 * TODO documentation needed
 */
public class InvalidSectionException extends SectionException
{

    /**
     * 
     */
    private static final long serialVersionUID = 5006493911366453340L;

    public InvalidSectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidSectionException(String message)
    {
        super(message);
    }

    public InvalidSectionException(String artifact, Class< ? extends SectionType> section)
    {
        super("Section " + section.getCanonicalName() + " not allowd on " + artifact);
    }

}
