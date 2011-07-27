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

package com.abiquo.tracer;

import java.io.Serializable;

public class TracerTo implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 9074466790937384117L;

    private ComponentType component;

    private EventType event;

    private SeverityType severity;

    private Platform platform;

    private UserInfo user;

    private long timestamp;

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ComponentType getComponent()
    {
        return component;
    }

    public void setComponent(ComponentType component)
    {
        this.component = component;
    }

    public EventType getEvent()
    {
        return event;
    }

    public void setEvent(EventType event)
    {
        this.event = event;
    }

    public SeverityType getSeverity()
    {
        return severity;
    }

    public void setSeverity(SeverityType severity)
    {
        this.severity = severity;
    }

    public UserInfo getUser()
    {
        return user;
    }

    public void setUser(UserInfo user)
    {
        this.user = user;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String toString()
    {
        return String.format(
            "Severity[%s], Event[%s], Component[%s], Timestamp[%d], Description[%s], User[%s]",
            severity.toString(), event.toString(), component.toString(), timestamp, description,
            (user != null ? user.getUsername() : "noname"));
    }

    public void setPlatform(Platform platform)
    {
        this.platform = platform;
    }

    public Platform getPlatform()
    {
        return platform;
    }
}
