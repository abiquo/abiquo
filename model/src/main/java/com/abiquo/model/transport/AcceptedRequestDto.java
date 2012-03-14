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

package com.abiquo.model.transport;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.rest.RESTLink;

/**
 * This Entity is the response of 202. It is a list of links (most of the cases one) and an optional
 * Object (most of the cases the string with the message to response).
 * 
 * @author ssedano
 */
@XmlRootElement(name = "acceptedrequest")
public class AcceptedRequestDto<T> extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = -7743440222172054557L;

    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.acceptedrequest+xml";

    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private static final String STATUS_REL = "status";

    private T entity;

    @XmlElement(name = "message", namespace = "", nillable = true, required = false)
    public T getEntity()
    {
        return entity;
    }

    public void setEntity(final T entity)
    {
        this.entity = entity;
    }

    public void setStatusUrlLink(final String url)
    {
        addLink(new RESTLink(STATUS_REL, url));
    }

    public RESTLink getStatusLink()
    {
        return searchLink(STATUS_REL);
    }

    @Override
    public String getMediaType()
    {
        return AcceptedRequestDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
