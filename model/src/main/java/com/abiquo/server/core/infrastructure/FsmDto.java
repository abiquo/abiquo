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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.24 at 11:14:06 AM CET 
//

package com.abiquo.server.core.infrastructure;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 */
@XmlRootElement(name = "logicServer")
public class FsmDto extends SingleResourceTransportDto
{
    public static final String MEDIA_TYPE = "application/vnd.abiquo.fsm+xml";
    
    protected String dn;

    protected String status;

    protected String progress;

    protected String description;

    protected String error;

    /**
     * Gets the value of the status property.
     * 
     * @return possible object is {@link String }
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Gets the value of the associated property.
     * 
     * @return possible object is {@link String }
     */

    public String getDn()
    {
        return dn;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setDn(final String dn)
    {
        this.dn = dn;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setStatus(final String value)
    {
        this.status = value;
    }

    /**
     * Gets the value of the progress property.
     * 
     * @return possible object is {@link String }
     */
    public String getProgress()
    {
        return progress;
    }

    /**
     * Sets the value of the progress property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setProgress(final String value)
    {
        this.progress = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setDescription(final String value)
    {
        this.description = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return possible object is {@link String }
     */
    public String getError()
    {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setError(final String value)
    {
        this.error = value;
    }

    @Override
    public String getMediaType()
    {
        return FsmDto.MEDIA_TYPE;
    }
}
