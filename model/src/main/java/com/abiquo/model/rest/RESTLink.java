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

package com.abiquo.model.rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.wink.common.model.synd.SyndLink;

/**
 * extension to use the link fields as attributes in the Xml
 * 
 * @author dcalavera
 */
@XmlRootElement(name = "link")
public class RESTLink
{
    @XmlAttribute
    private String length;

    @XmlAttribute
    private String title;

    @XmlAttribute
    private String hreflang;

    @XmlAttribute
    private String rel;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String href;

    public RESTLink()
    {

    }

    public RESTLink(String rel, String href)
    {
        this.rel = rel;
        this.href = href;
    }

    public RESTLink(SyndLink other)
    {
        this.length = other.getLength();
        this.title = other.getTitle();
        this.hreflang = other.getHreflang();
        this.rel = other.getRel();
        this.type = other.getType();
        this.href = other.getHref();
    }

    @XmlTransient
    public String getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    @XmlTransient
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @XmlTransient
    public String getHreflang()
    {
        return hreflang;
    }

    public void setHreflang(String hreflang)
    {
        this.hreflang = hreflang;
    }

    @XmlTransient
    public String getRel()
    {
        return rel;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    @XmlTransient
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlTransient
    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }
}
