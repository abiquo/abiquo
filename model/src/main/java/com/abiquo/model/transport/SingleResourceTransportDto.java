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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.abiquo.model.rest.RESTLink;

public abstract class SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected RESTLink editLink;

    protected List<RESTLink> links;

    @XmlElement(name = "link")
    public List<RESTLink> getLinks()
    {
        return links;
    }

    public void setLinks(List<RESTLink> links)
    {
        this.links = links;
    }

    public void addLink(RESTLink link)
    {
        if (this.links == null)
        {
            this.links = new ArrayList<RESTLink>();
        }
        this.links.add(link);
    }

    public void addLinks(List<RESTLink> links)
    {
        if (this.links == null)
        {
            this.links = new ArrayList<RESTLink>();
        }
        this.links.addAll(links);
    }

    public RESTLink getEditLink()
    {
        if (editLink == null)
        {
            editLink = searchLink("edit");
        }
        return editLink;
    }

    public void addEditLink(RESTLink edit)
    {
        editLink = edit;
        RESTLink currentEdit = searchLink("edit");
        if (currentEdit != null)
        {
            links.remove(currentEdit);
        }
        links.add(editLink);
    }

    public RESTLink searchLink(String rel)
    {
        if (getLinks() == null)
        {
            setLinks(new ArrayList<RESTLink>());
        }

        for (RESTLink link : getLinks())
        {
            if (link.getRel() != null)
            {
                if (link.getRel().equals(rel))
                {
                    return link;
                }
            }
        }
        return null;
    }

    public RESTLink searchLink(String rel, String title)
    {
        if (getLinks() == null)
        {
            setLinks(new ArrayList<RESTLink>());
        }

        for (RESTLink link : getLinks())
        {
            if (link.getRel().equals(rel) && link.getTitle().equals(title))
            {
                return link;
            }
        }
        return null;
    }

    public RESTLink searchLinkByHref(String href)
    {
        if (getLinks() == null)
        {
            setLinks(new ArrayList<RESTLink>());
        }

        for (RESTLink link : getLinks())
        {
            if (link.getHref().equals(href))
            {
                return link;
            }
        }
        return null;
    }

    public void modifyLink(String rel, String href)
    {
        searchLink(rel).setHref(href);
    }
}
