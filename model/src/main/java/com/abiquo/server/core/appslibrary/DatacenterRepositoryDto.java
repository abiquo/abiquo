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

package com.abiquo.server.core.appslibrary;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "datacenterRepository")
public class DatacenterRepositoryDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 6344939071248048966L;

    private Integer id;

    private String name;

    private String error;

    private String repositoryLocation;

    private long repositoryRemainingMb;

    private long repositoryCapacityMb;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getError()
    {
        return error;
    }

    public void setError(final String error)
    {
        this.error = error;
    }

    public String getRepositoryLocation()
    {
        return repositoryLocation;
    }

    public void setRepositoryLocation(final String repositoryLocation)
    {
        this.repositoryLocation = repositoryLocation;
    }

    public long getRepositoryRemainingMb()
    {
        return repositoryRemainingMb;
    }

    public void setRepositoryRemainingMb(long repositoryRemainingMb)
    {
        this.repositoryRemainingMb = repositoryRemainingMb;
    }

    public long getRepositoryCapacityMb()
    {
        return repositoryCapacityMb;
    }

    public void setRepositoryCapacityMb(long repositoryCapacityMb)
    {
        this.repositoryCapacityMb = repositoryCapacityMb;
    }

}
