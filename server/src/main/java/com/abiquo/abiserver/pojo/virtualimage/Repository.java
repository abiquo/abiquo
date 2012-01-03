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

package com.abiquo.abiserver.pojo.virtualimage;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;

public class Repository implements IPojo<RepositoryHB>
{
    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    private String URL;

    private DataCenter datacenter;

    private Long repositoryCapacityMb;

    private Long repositoryEnterpriseUsedMb;

    private Long repositoryRemainingMb;

    public Long getRepositoryCapacityMb()
    {
        return repositoryCapacityMb;
    }

    public void setRepositoryCapacityMb(final Long repositoryCapacityMb)
    {
        this.repositoryCapacityMb = repositoryCapacityMb;
    }

    public Long getRepositoryEnterpriseUsedMb()
    {
        return repositoryEnterpriseUsedMb;
    }

    public void setRepositoryEnterpriseUsedMb(final Long repositoryEnterpriseUsedMb)
    {
        this.repositoryEnterpriseUsedMb = repositoryEnterpriseUsedMb;
    }

    public Long getRepositoryRemainingMb()
    {
        return repositoryRemainingMb;
    }

    public void setRepositoryRemainingMb(final Long repositoryRemainingMb)
    {
        this.repositoryRemainingMb = repositoryRemainingMb;
    }

    /* ------------- Constructor ------------- */
    public Repository()
    {
        id = 0;
        name = "";
        URL = "";
    }

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
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

    public String getURL()
    {
        return URL;
    }

    public void setURL(final String url)
    {
        URL = url;
    }

    public DataCenter getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final DataCenter datacenter)
    {
        this.datacenter = datacenter;
    }

    @Override
    public RepositoryHB toPojoHB()
    {
        RepositoryHB repositoryHB = new RepositoryHB();

        repositoryHB.setIdRepository(id);
        repositoryHB.setName(name);
        repositoryHB.setUrl(URL);
        // repositoryHB.setEnterprise(enterpirse.toPojoHB());
        if (datacenter != null)
        {
            repositoryHB.setDatacenter(datacenter.toPojoHB());
        }

        return repositoryHB;
    }

}
