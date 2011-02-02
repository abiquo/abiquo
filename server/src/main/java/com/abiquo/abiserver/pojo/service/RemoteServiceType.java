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

package com.abiquo.abiserver.pojo.service;

public class RemoteServiceType implements java.io.Serializable
{
    private String name;

    private String serviceMapping;

    private String protocol;

    private Integer port;

    private String valueOf;

    public RemoteServiceType()
    {

    }

    public RemoteServiceType(
        com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType type)
    {
        this.name = type.getName();
        this.serviceMapping = type.getServiceMapping();
        this.port = type.getDefaultPort();
        this.protocol = type.getDefaultProtocol();
        this.valueOf = type.name();
    }

    public RemoteServiceType(com.abiquo.model.enumerator.RemoteServiceType type)
    {
        this.name = type.getName();
        this.serviceMapping = type.getServiceMapping();
        this.port = type.getDefaultPort();
        this.protocol = type.getDefaultProtocol();
        this.valueOf = type.name();
    }

    public String getValueOf()
    {
        return valueOf;
    }

    public void setValueOf(String valueOf)
    {
        this.valueOf = valueOf;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Gets the serviceMapping.
     * 
     * @return the serviceMapping
     */
    public String getServiceMapping()
    {
        return serviceMapping;
    }

    /**
     * Sets the serviceMapping.
     * 
     * @param serviceMapping the serviceMapping to set
     */
    public void setServiceMapping(final String serviceMapping)
    {
        this.serviceMapping = serviceMapping;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getPort()
    {
        return port;
    }

    public com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType toEnum()
    {
        return com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType
            .valueOf(valueOf);
    }
}
