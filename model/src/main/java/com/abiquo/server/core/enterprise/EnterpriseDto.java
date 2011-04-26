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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceWithLimitsDto;

@XmlRootElement(name = "enterprise")
public class EnterpriseDto extends SingleResourceWithLimitsDto
{
    private Integer id;

    private String name;

    private long repositorySoft;

    private long repositoryHard;

    private boolean isReservationRestricted;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getRepositorySoft()
    {
        return repositorySoft;
    }

    public void setRepositorySoft(long repositorySoft)
    {
        this.repositorySoft = repositorySoft;
    }

    public long getRepositoryHard()
    {
        return repositoryHard;
    }

    public void setRepositoryHard(long repositoryHard)
    {
        this.repositoryHard = repositoryHard;
    }

    public void setRepositoryLimits(long soft, long hard)
    {
        this.repositorySoft = soft;
        this.repositoryHard = hard;
    }

    public boolean getIsReservationRestricted()
    {
        return isReservationRestricted;
    }

    public void setIsReservationRestricted(boolean isReservationRestricted)
    {
        this.isReservationRestricted = isReservationRestricted;
    }
}
