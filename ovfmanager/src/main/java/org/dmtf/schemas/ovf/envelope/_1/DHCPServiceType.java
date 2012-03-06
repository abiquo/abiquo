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
// Generated on: 2012.02.23 at 01:07:44 PM CET 
//


package org.dmtf.schemas.ovf.envelope._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DHCP_Service_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DHCP_Service_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="static_rules" type="{http://schemas.dmtf.org/ovf/envelope/1}ip_pool_Type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dhcp_Address" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dhcp_port" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DHCP_Service_Type", propOrder = {
    "staticRules"
})
public class DHCPServiceType {

    @XmlElement(name = "static_rules", required = true)
    protected List<IpPoolType> staticRules;
    @XmlAttribute(name = "dhcp_Address", namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected String dhcpAddress;
    @XmlAttribute(name = "dhcp_port", namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected Integer dhcpPort;

    /**
     * Gets the value of the staticRules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the staticRules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStaticRules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IpPoolType }
     * 
     * 
     */
    public List<IpPoolType> getStaticRules() {
        if (staticRules == null) {
            staticRules = new ArrayList<IpPoolType>();
        }
        return this.staticRules;
    }

    /**
     * Gets the value of the dhcpAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDhcpAddress() {
        return dhcpAddress;
    }

    /**
     * Sets the value of the dhcpAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDhcpAddress(String value) {
        this.dhcpAddress = value;
    }

    /**
     * Gets the value of the dhcpPort property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDhcpPort() {
        return dhcpPort;
    }

    /**
     * Sets the value of the dhcpPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDhcpPort(Integer value) {
        this.dhcpPort = value;
    }

}