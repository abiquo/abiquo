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
package com.abiquo.server.core.config;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "license")
@XmlType(propOrder = {"id", "customerid", "enabledip", "numcores", "expiration", "code"})
public class LicenseDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    /** The license id **/
    private Integer id;

    /** The license code. */
    private String code;

    /** The customer ID. */
    private String customerid;

    /** The enabled IP. */
    // Not used yet. Do not serialize to xml.
    @XmlTransient
    private String enabledip;

    /** The number of cores. */
    private Integer numcores;

    /** The expiration date. */
    private String expiration;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCustomerid()
    {
        return customerid;
    }

    public void setCustomerid(String customerid)
    {
        this.customerid = customerid;
    }

    public String getEnabledip()
    {
        return enabledip;
    }

    public void setEnabledip(String enabledip)
    {
        this.enabledip = enabledip;
    }

    public Integer getNumcores()
    {
        return numcores;
    }

    public void setNumcores(Integer numcores)
    {
        this.numcores = numcores;
    }

    public String getExpiration()
    {
        return expiration;
    }

    public void setExpiration(String expiration)
    {
        this.expiration = expiration;
    }

}
