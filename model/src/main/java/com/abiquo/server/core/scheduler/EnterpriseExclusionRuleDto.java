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

package com.abiquo.server.core.scheduler;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.Link;
import com.abiquo.model.transport.Links;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "enterpriseExclusionRule")
@Links( {
@Link(href = "http://example.com/api/admin/enterprises/{idEnterprise}", rel = "enterprise", title = "enterprise1", required = true),
@Link(href = "http://example.com/api/admin/enterprises/{idEnterprise}", rel = "enterprise", title = "enterprise2", required = true)})
public class EnterpriseExclusionRuleDto extends SingleResourceTransportDto
{
    public static final String BASE_MEDIA_TYPE =
        "application/vnd.abiquo.enterpriseexclusionrule+xml";

    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    @Override
    public String getMediaType()
    {
        return EnterpriseExclusionRuleDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
