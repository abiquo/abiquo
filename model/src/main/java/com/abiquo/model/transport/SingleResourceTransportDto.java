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
import java.util.Comparator;
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

    public void setLinks(final List<RESTLink> links)
    {
        this.links = links;
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

    public RESTLink getEditLink()
    {
        if (editLink == null)
        {
            editLink = searchLink("edit");
        }
        return editLink;
    }

    public void addEditLink(final RESTLink edit)
    {
        editLink = edit;
        RESTLink currentEdit = searchLink("edit");
        if (currentEdit != null)
        {
            links.remove(currentEdit);
        }
        links.add(editLink);
    }

    public RESTLink searchLink(final String rel)
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

    public List<RESTLink> searchLinks(final String rel)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

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
                    links.add(link);
                }
            }
        }
        return links;
    }

    public RESTLink searchLink(final String rel, final String title)
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

    public RESTLink searchLinkByHref(final String href)
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

    public void modifyLink(final String rel, final String href)
    {
        searchLink(rel).setHref(href);
    }

    public Integer getIdFromLink(final String rel)
    {
        String href = this.searchLink(rel).getHref();
        return Integer.getInteger(href.substring(href.lastIndexOf("/"), (href.length() - 1)));
    }

    public static enum LinkOrder implements Comparator<RESTLink>
    {
        BY_REL
        {
            @Override
            public int compare(final RESTLink link0, final RESTLink link1)
            {
                if (link0.getRel() == null || link1.getRel() == null)
                {
                    return 0;
                }

                return String.CASE_INSENSITIVE_ORDER.compare(link0.getRel(), link1.getRel());
            }
        },

        BY_TITLE
        {
            @Override
            public int compare(final RESTLink link0, final RESTLink link1)
            {
                if (link0.getTitle() == null || link1.getTitle() == null)
                {
                    return 0;
                }

                return String.CASE_INSENSITIVE_ORDER.compare(link0.getTitle(), link1.getTitle());
            }
        }
    }
}
