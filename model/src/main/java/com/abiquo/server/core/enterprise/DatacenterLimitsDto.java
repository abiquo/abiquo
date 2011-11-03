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

package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceWithLimitsDto;

@XmlRootElement(name = "limit")
public class DatacenterLimitsDto extends SingleResourceWithLimitsDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private int idDcEnterpriseStats;

    public int getIdDcEnterpriseStats()
    {
        return idDcEnterpriseStats;
    }

    public void setIdDcEnterpriseStats(final int idDcEnterpriseStats)
    {
        this.idDcEnterpriseStats = idDcEnterpriseStats;
    }

    private long repositorySoftLimitsInMb;

    @XmlElement(name = "repositorySoft")
    public long getRepositorySoftLimitsInMb()
    {
        return repositorySoftLimitsInMb;
    }

    public void setRepositorySoftLimitsInMb(final long repositorySoftLimitsInMb)
    {
        this.repositorySoftLimitsInMb = repositorySoftLimitsInMb;
    }

    private long repositoryHardLimitsInMb;

    @XmlElement(name = "repositoryHard")
    public long getRepositoryHardLimitsInMb()
    {
        return repositoryHardLimitsInMb;
    }

    public void setRepositoryHardLimitsInMb(final long repositoryHardLimitsInMb)
    {
        this.repositoryHardLimitsInMb = repositoryHardLimitsInMb;
    }

}
