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

package com.abiquo.commons.amqp.impl.tracer.domain;

import java.util.Map;

import com.abiquo.commons.amqp.domain.Queuable;
import com.abiquo.commons.amqp.util.JSONUtils;

/**
 * Transport object for tracing system.
 * 
 * @author eruiz@abiquo.com
 */
public class Trace implements Queuable
{
    /** The name of the user who performs the action. */
    private String username;

    /** The id of the user who performs the action. */
    private int userId;

    /** The name of the enterprise of the user who performs the action. */
    private String enterpriseName;

    /** The id of the enterprise of the user who performs the action. */
    private int enterpriseId;

    /** The severity of the trace. */
    private String severity;

    /** The component that generated the trace. */
    private String component;

    /** The event being traced. */
    private String event;

    /** The current platform hierarchy. */
    private String hierarchy;

    /** The hierarchy data. */
    private Map<String, String> hierarchyData;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(final int userId)
    {
        this.userId = userId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(final String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public int getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(final int enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(final String severity)
    {
        this.severity = severity;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(final String component)
    {
        this.component = component;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent(final String event)
    {
        this.event = event;
    }

    public String getHierarchy()
    {
        return hierarchy;
    }

    public void setHierarchy(final String hierarchy)
    {
        this.hierarchy = hierarchy;
    }

    public Map<String, String> getHierarchyData()
    {
        return hierarchyData;
    }

    public void setHierarchyData(final Map<String, String> hierarchyData)
    {
        this.hierarchyData = hierarchyData;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Severity: ").append(getSeverity());
        builder.append(" Component: ").append(getComponent());
        builder.append(" Event: ").append(getEvent());
        builder.append(" Hierarchy: ").append(getHierarchy());
        builder.append(" Performed by ").append(getUsername());
        builder.append(" from enterprise ").append(getEnterpriseName());

        return builder.toString();
    }

    @Override
    public byte[] toByteArray()
    {
        return JSONUtils.serialize(this);
    }

    public static Trace fromByteArray(final byte[] bytes)
    {
        return JSONUtils.deserialize(bytes, Trace.class);
    }
}
