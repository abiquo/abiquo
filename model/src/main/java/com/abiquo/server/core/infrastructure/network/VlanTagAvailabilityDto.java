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

package com.abiquo.server.core.infrastructure.network;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.VlanTagAvailabilityType;
import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Set the transfer object for the type VlanTagAvialiabilityType
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "vlanTagAvailability")
public class VlanTagAvailabilityDto extends SingleResourceTransportDto implements Serializable
{
    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = 8354795972402115520L;

    private VlanTagAvailabilityType available;

    private String message;

    public VlanTagAvailabilityType getAvailable()
    {
        return available;
    }

    public void setAvailable(final VlanTagAvailabilityType available)
    {
        this.available = available;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }
}
