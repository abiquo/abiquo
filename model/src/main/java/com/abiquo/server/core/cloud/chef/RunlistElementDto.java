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

package com.abiquo.server.core.cloud.chef;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "element")
@XmlType(propOrder = {"name", "description", "priority", "selected"})
public class RunlistElementDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    public static final String EXTENDED_RUNLIST_MIME_TYPE = "application/vnd.extended-runlist+xml";

    // Runlist elements should not return the ID, since the resource is not a regular resource in
    // the DB. It is synchronized with the Chef Server every time it is requested

    private String name;

    private String description;

    private boolean selected;

    private int priority;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(final boolean selected)
    {
        this.selected = selected;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(final int priority)
    {
        this.priority = priority;
    }

}
