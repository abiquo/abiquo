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

/**
 * Each Datacenter has one repository for each enterprise using it.
 * 
 * @author destevez
 */
@XmlRootElement
@XmlType(name = "EnterpriseRepository")
public class EnterpriseRepositoryDto
{

    /**
     * Enterprise Identifier
     */
    private Integer id;

    /**
     * Enterprise Name
     */
    private String name;

    private long repositoryCapacityMb;

    private long repositoryEnterpriseUsedMb;

    private long repositoryRemainingMb;

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
