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
package com.abiquo.server.core.infrastructure.nodecollector;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains information about the current state of the machine. If the state is MANAGED, the
 * statusInfo will not be retrieved.
 * 
 * @author ibarrera
 */
@XmlRootElement(name = "status")
public class StatusDto implements Serializable
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The current state of the Host. */
    private String state;

    /** The reason of the state. */
    private String statusInfo;

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * Sets the state.
     * 
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Gets the statusInfo.
     * 
     * @return the statusInfo
     */
    public String getStatusInfo()
    {
        return statusInfo;
    }

    /**
     * Sets the statusInfo.
     * 
     * @param statusInfo the statusInfo to set
     */
    public void setStatusInfo(String statusInfo)
    {
        this.statusInfo = statusInfo;
    }

}
