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

package com.abiquo.appliancemanager.transport;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.enterprise.Enterprise;

/**
 * Each {@link Enterprise} have a ''folder'' in the {@link DatacenterRepositoryDto}
 */
@XmlRootElement(name = "enterpriseRepository")
public class EnterpriseRepositoryDto extends RepositoryConfigurationDto
{
    private static final long serialVersionUID = -346581740339075827L;

    /** Same as {@link Enterprise} identifier. */
    private Integer id;

    /**
     * Used space in the enterprise folder of the datacenter repository.
     * <p>
     * TODO instances of shared waste space in the original enterprise repository
     */
    private long enterpriseUsedMb;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public long getEnterpriseUsedMb()
    {
        return enterpriseUsedMb;
    }

    public void setEnterpriseUsedMb(final long repositoryEnterpriseUsedMb)
    {
        this.enterpriseUsedMb = repositoryEnterpriseUsedMb;
    }
    
    @Override
    public String getMediaType()
    {
        return MediaType.APPLICATION_XML;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return MediaType.APPLICATION_XML;
    }
}
