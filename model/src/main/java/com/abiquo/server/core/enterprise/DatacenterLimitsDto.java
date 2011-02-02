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

    public void setId(Integer id)
    {
        this.id = id;
    }

    private int idDataCenter;

    public int getIdDataCenter()
    {
        return idDataCenter;
    }

    public void setIdDataCenter(int idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    private int idDcEnterpriseStats;

    public int getIdDCEnterpriseStats()
    {
        return idDcEnterpriseStats;
    }

    public void setIdDCEnterpriseStats(int idDcEnterpriseStats)
    {
        this.idDcEnterpriseStats = idDcEnterpriseStats;
    }

    private int idEnterprise;

    public int getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    private long repositorySoftLimitsInMb;

    @XmlElement(name = "repositorySoft")
    public long getRepositorySoftLimitsInMb()
    {
        return repositorySoftLimitsInMb;
    }

    public void setRepositorySoftLimitsInMb(long repositorySoftLimitsInMb)
    {
        this.repositorySoftLimitsInMb = repositorySoftLimitsInMb;
    }

    private long repositoryHardLimitsInMb;

    @XmlElement(name = "repositoryHard")
    public long getRepositoryHardLimitsInMb()
    {
        return repositoryHardLimitsInMb;
    }

    public void setRepositoryHardLimitsInMb(long repositoryHardLimitsInMb)
    {
        this.repositoryHardLimitsInMb = repositoryHardLimitsInMb;
    }

}
