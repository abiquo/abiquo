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
package com.abiquo.virtualfactory.model.config;

import javax.xml.namespace.QName;

/**
 * Bootstrap configuration for virtual machines.
 * 
 * @author ibarrera
 */
public class BootstrapConfiguration
{
    /** Api URI to download bootstrap configuration */
    public final static QName bootstrapConfigURIQname = new QName("bootstrapConfigURI");

    /** One time use Auth token to download the bootstrap configuration */
    public final static QName bootstrapConfigAuthQname = new QName("bootstrapConfigAuth");

    /** The URI that exposes the bootstrap configuration for the virtual machine. */
    private String configURI;

    /** The authentication token used to access the {@link #configURI}. */
    private String auth;

    public String getConfigURI()
    {
        return configURI;
    }

    public void setConfigURI(final String configURI)
    {
        this.configURI = configURI;
    }

    public String getAuth()
    {
        return auth;
    }

    public void setAuth(final String auth)
    {
        this.auth = auth;
    }

}
