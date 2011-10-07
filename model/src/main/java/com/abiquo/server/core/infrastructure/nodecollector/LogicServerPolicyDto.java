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

package com.abiquo.server.core.infrastructure.nodecollector;

import javax.xml.bind.annotation.XmlElement;

public class LogicServerPolicyDto
{
    @XmlElement(required = true)
    protected String dn;

    // It can be template (update or initial) or instance
    @XmlElement(required = true)
    protected String name;

    @XmlElement(required = true)
    protected String priority;

    @XmlElement(required = false)
    protected String description;

    /**
     * @return String
     */
    public String getDn()
    {
        return dn;
    }

    /**
     * @param dn void
     */
    public void setDn(final String dn)
    {
        this.dn = dn;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name void
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @return String
     */
    public String getPriority()
    {
        return priority;
    }

    /**
     * @param priority void
     */
    public void setPriority(final String priority)
    {
        this.priority = priority;
    }

    /**
     * @return String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description void
     */
    public void setDescription(final String description)
    {
        this.description = description;
    }

}
