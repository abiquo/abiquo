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
package com.abiquo.api.tracer;

import java.io.Serializable;

/**
 * Stores contextual information to be used by the tracing system.
 * 
 * @author ibarrera
 */
public class TracerContext implements Serializable
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The current platform hierachy. */
    private String hierachy;

    /** The name of the user who performs the action. */
    private String username;

    // Getters and setters

    public String getHierachy()
    {
        return hierachy;
    }

    public void setHierachy(final String hierachy)
    {
        this.hierachy = hierachy;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

}
