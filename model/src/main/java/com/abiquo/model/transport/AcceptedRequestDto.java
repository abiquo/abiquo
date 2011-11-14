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

/**
 * 
 */
package com.abiquo.model.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.LinkOrder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.CompositeComparator;

/**
 * This Entity is the response of 202. It is a list of links (most of the cases one) and an optional
 * Object (most of the cases the string with the message to response).
 * 
 * @author ssedano
 */
@XmlRootElement(name = "acceptedrequest")
public class AcceptedRequestDto<T> implements Serializable
{
    private static final long serialVersionUID = -7743440222172054557L;

    private static final String STATUS_REL = "status";

    private T entity;

    protected List<RESTLink> links;

    @XmlElement(name = "message", namespace = "", nillable = true)
    public T getEntity()
    {
        return entity;
    }

    public void setEntity(final T entity)
    {
        this.entity = entity;
    }

    @XmlElement(name = "link")
    public List<RESTLink> getLinks()
    {
        if (links != null)
        {
            Collections.<RESTLink> sort(links,
                CompositeComparator.<RESTLink> build(LinkOrder.BY_REL, LinkOrder.BY_TITLE));
        }
        return links;
    }

    public void setLinks(final List<RESTLink> links)
    {
        this.links = links;
    }

    public void setStatusUrlLink(final String url)
    {
        RESTLink link = new RESTLink(STATUS_REL, url);
        addLink(link);
    }

    public void addLink(final RESTLink link)
    {
        if (this.links == null)
        {
            this.links = new ArrayList<RESTLink>();
        }
        this.links.add(link);
    }

    public void addLinks(final List<RESTLink> links)
    {
        if (this.links == null)
        {
            this.links = new ArrayList<RESTLink>();
        }
        this.links.addAll(links);
    }
}
