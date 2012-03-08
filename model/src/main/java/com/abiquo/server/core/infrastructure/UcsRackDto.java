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

package com.abiquo.server.core.infrastructure;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ucsrack")
public class UcsRackDto extends RackDto
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.ucsrack+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    
    private static final long serialVersionUID = 1L;

    private Integer port;

    public Integer getPort()
    {
        return port;
    }

    public void setPort(final Integer port)
    {
        this.port = port;
    }

    private String ip;

    public String getIp()
    {
        return ip;
    }

    public void setIp(final String ip)
    {
        this.ip = ip;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    private String user;

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    private String defaultTemplate;

    public String getDefaultTemplate()
    {
        return this.defaultTemplate;
    }

    public void setDefaultTemplate(final String defaultTemplate)
    {
        this.defaultTemplate = defaultTemplate;
    }

    private Integer maxMachinesOn;

    public Integer getMaxMachinesOn()
    {
        return maxMachinesOn;
    }

    public void setMaxMachinesOn(final Integer maxMachinesOn)
    {
        this.maxMachinesOn = maxMachinesOn;
    }
    
    @Override
    public String getMediaType()
    {
        return UcsRackDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
