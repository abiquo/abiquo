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

    /** The current platform hierarchy. */
    private String hierarchy;

    /** The id of the user who performs the action. */
    private Integer userId;

    /** The name of the user who performs the action. */
    private String username;

    /** The id of the enterprise of the user who performs the action. */
    private Integer enterpriseId;

    /** The name of the enterprise of the user who performs the action. */
    private String enterpriseName;

    // Getters and setters

    public String getHierarchy()
    {
        return hierarchy;
    }

    public void setHierarchy(final String hierarchy)
    {
        this.hierarchy = hierarchy;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(final Integer userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public Integer getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(final Integer enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(final String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

}
