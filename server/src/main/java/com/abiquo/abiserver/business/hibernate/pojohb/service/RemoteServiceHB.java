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

package com.abiquo.abiserver.business.hibernate.pojohb.service;

import java.net.URI;
import java.net.URISyntaxException;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.service.RemoteService;

/**
 * The Class RemoteService represents a remotely manageable service
 */
public class RemoteServiceHB implements java.io.Serializable, IPojoHB<RemoteService>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Variable which corresponds with column 'idRemoteService'. */
    private int idRemoteService;

    /** The id data center. */
    private int idDataCenter;

    /** The uuid. */
    private String uri;

    private URI URI;

    public URI getURI()
    {
        try
        {
            if (URI == null)
                URI = new URI(uri);
            return URI;
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

    /**
     * Remote service URI must always end with a /
     */
    public String getUri()
    {
        return uri;
    }

    public void setUri(final String uri)
    {
        this.uri = uri;
    }

    /** Variable which corresponds with column 'idRemoteServiceType'. */
    private RemoteServiceType remoteServiceType;

    /**
     * Service Availability (0 = Not Available, 1 = Available)
     */
    private Integer status;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(final Integer status)
    {
        this.status = status;
    }

    /**
     * Instantiates a new remote service.
     * 
     * @param remoteService the remote service
     */
    public RemoteServiceHB(final RemoteServiceHB remoteService)
    {
        idRemoteService = remoteService.idRemoteService;
        idDataCenter = remoteService.idDataCenter;
        remoteServiceType = remoteService.remoteServiceType;
        status = remoteService.status;
        uri = remoteService.uri;
    }

    /**
     * Instantiates a new remote service.
     */
    public RemoteServiceHB()
    {
    }

    /**
     * Sets the id remote service.
     * 
     * @param idRemoteService the id remote service
     */
    public void setIdRemoteService(final int idRemoteService)
    {
        this.idRemoteService = idRemoteService;
    }

    /**
     * Gets the id remote service.
     * 
     * @return the uUID_VSS
     */
    public int getIdRemoteService()
    {
        return idRemoteService;
    }

    /**
     * Gets the remote service type .
     * 
     * @return the remote service type
     */
    public RemoteServiceType getRemoteServiceType()
    {
        return remoteServiceType;
    }

    /**
     * Sets the id data center.
     * 
     * @param idDataCenter the new id data center
     */
    public void setIdDataCenter(final int idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    /**
     * Gets the id data center.
     * 
     * @return the id data center
     */
    public int getIdDataCenter()
    {
        return idDataCenter;
    }

    /**
     * Sets the remote service type hb.
     * 
     * @param remoteServiceTypeHB the new remote service type hb
     */
    public void setRemoteServiceType(final RemoteServiceType remoteServiceType)
    {
        this.remoteServiceType = remoteServiceType;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB#toPojo()
     */
    @Override
    public RemoteService toPojo()
    {
        RemoteService remoteService = new RemoteService();
        remoteService.setIdRemoteService(this.getIdRemoteService());
        remoteService.setIdDataCenter(this.getIdDataCenter());
        remoteService.setUri(this.uri);
        remoteService.setStatus(this.getStatus());
        remoteService
            .setRemoteServiceType(new com.abiquo.abiserver.pojo.service.RemoteServiceType(remoteServiceType));
        return remoteService;
    }
}
