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

package com.abiquo.server.core.infrastructure.nodecollector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The object contains the current needed values of the blade locator led.
 * <p>
 * Java class for BladeLocatorLedDto complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BladeLocatorLedDto">
 *   &lt;complexContent>
 *       &lt;sequence>
 *        &lt;element name="dn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adminStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="color" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bladeDn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BladeLocatorLedDto", propOrder = {"dn", "adminStatus", "color", "bladeDn"})
public class BladeLocatorLedDto
{
    @XmlElement(required = true)
    protected String dn;

    @XmlElement(required = true)
    protected String adminStatus;

    @XmlElement(required = true)
    protected String color;

    @XmlElement(required = true)
    protected String bladeDn;

    /**
     * Gets the value of the status property.
     * 
     * @return possible object is {@link String }
     */
    public String getAdminStatus()
    {
        return adminStatus;
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
    public void setAdminStatus(final String adminStatus)
    {
        this.adminStatus = adminStatus;
    }

    /**
     * Gets the value of the bladeDn property.
     * 
     * @return possible object is {@link String }
     */
    public String getBladeDn()
    {
        return bladeDn;
    }

    /**
     * Sets the value of the bladeDn property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setBladeDn(final String bladeDn)
    {
        this.bladeDn = bladeDn;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     */
    public String getColor()
    {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setColor(final String color)
    {
        this.color = color;
    }

}
