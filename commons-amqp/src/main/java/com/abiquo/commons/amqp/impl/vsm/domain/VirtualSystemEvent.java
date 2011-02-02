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

package com.abiquo.commons.amqp.impl.vsm.domain;

import com.abiquo.commons.amqp.domain.Queuable;
import com.abiquo.commons.amqp.util.JSONUtils;

public class VirtualSystemEvent implements Queuable
{
    protected String virtualSystemId;

    protected String virtualSystemType;

    protected String virtualSystemAddress;

    protected String eventType;

    public VirtualSystemEvent()
    {

    }

    public VirtualSystemEvent(String virtualSystemId, String virtualSystemType,
        String virtualSystemAddress, String eventType)
    {
        this.virtualSystemId = virtualSystemId;
        this.virtualSystemType = virtualSystemType;
        this.virtualSystemAddress = virtualSystemAddress;
        this.eventType = eventType;
    }

    public String getVirtualSystemId()
    {
        return virtualSystemId;
    }

    public void setVirtualSystemId(String virtualSystemId)
    {
        this.virtualSystemId = virtualSystemId;
    }

    public String getVirtualSystemType()
    {
        return virtualSystemType;
    }

    public void setVirtualSystemType(String virtualSystemType)
    {
        this.virtualSystemType = virtualSystemType;
    }

    public String getVirtualSystemAddress()
    {
        return virtualSystemAddress;
    }

    public void setVirtualSystemAddress(String virtualSystemAddress)
    {
        this.virtualSystemAddress = virtualSystemAddress;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    @Override
    public byte[] toByteArray()
    {
        return JSONUtils.serialize(this);
    }

    public static VirtualSystemEvent fromByteArray(byte[] bytes)
    {
        return JSONUtils.deserialize(bytes, VirtualSystemEvent.class);
    }
}
