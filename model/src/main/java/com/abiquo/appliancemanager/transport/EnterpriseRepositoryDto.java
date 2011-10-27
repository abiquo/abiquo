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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.enterprise.Enterprise;

/**
 * Each {@link Enterprise} have a ''folder'' in the {@link DatacenterRepositoryDto}
 */
@XmlRootElement
@XmlType(name = "enterpriseRepository")
public class EnterpriseRepositoryDto extends RepositoryConfigurationDto
{
    private static final long serialVersionUID = -346581740339075827L;

    /** Same as {@link Enterprise} identifier. */
    private Integer id;

    /**
     * Capacity of the {@link DatacenterRepositoryDto}, shared by all the EnterpriseRepositories.
     * TODO consider move to {@link RepositoryConfigurationDto}
     */
    private long repositoryCapacityMb;

    /**
     * Remaining free space in the {@link DatacenterRepositoryDto}, shared by all the
     * EnterpriseRepositories.TODO consider move to {@link RepositoryConfigurationDto}
     */
    private long repositoryRemainingMb;

    /**
     * Used space in the enterprise folder of the datacenter repository.
     * <p>
     * TODO instances of shared waste space in the original enterprise repository
     */
    private long repositoryEnterpriseUsedMb;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public long getRepositoryCapacityMb()
    {
        return repositoryCapacityMb;
    }

    public void setRepositoryCapacityMb(long repositoryCapacityMb)
    {
        this.repositoryCapacityMb = repositoryCapacityMb;
    }

    public long getRepositoryEnterpriseUsedMb()
    {
        return repositoryEnterpriseUsedMb;
    }

    public void setRepositoryEnterpriseUsedMb(long repositoryEnterpriseUsedMb)
    {
        this.repositoryEnterpriseUsedMb = repositoryEnterpriseUsedMb;
    }

    public long getRepositoryRemainingMb()
    {
        return repositoryRemainingMb;
    }

    public void setRepositoryRemainingMb(long repositoryRemainingMb)
    {
        this.repositoryRemainingMb = repositoryRemainingMb;
    }

}
