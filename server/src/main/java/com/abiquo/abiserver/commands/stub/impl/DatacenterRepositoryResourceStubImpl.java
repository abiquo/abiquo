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

package com.abiquo.abiserver.commands.stub.impl;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.DatacenterRepositoryResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Repository;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;

public class DatacenterRepositoryResourceStubImpl extends AbstractAPIStub implements
    DatacenterRepositoryResourceStub
{
    public final static String DATACENTER_REPOSITORY_GET_REFRESH_QUERY_PARAM = "refresh";

    public final static String DATACENTER_REPOSITORY_GET_USAGE_QUERY_PARAM = "usage";

    @Override
    public DataResult<Repository> getRepository(final Integer idDatacenter,
        final Integer idEnterprise, final boolean refresh, final boolean includeUsage)
    {
        DataResult<Repository> result = new DataResult<Repository>();

        String uri = createDatacenterRepositoryLink(idEnterprise, idDatacenter);

        Resource repositoryResource = resource(uri).//
            queryParam(DATACENTER_REPOSITORY_GET_REFRESH_QUERY_PARAM, String.valueOf(refresh)).//
            queryParam(DATACENTER_REPOSITORY_GET_USAGE_QUERY_PARAM, String.valueOf(includeUsage));

        ClientResponse response = repositoryResource.get();

        if (response.getStatusCode() / 200 == 1)
        {
            DatacenterRepositoryDto repo = response.getEntity(DatacenterRepositoryDto.class);

            result.setSuccess(true);
            result.setData(transformToFlex(repo));
        }
        else
        {
            populateErrors(response, result, "deleteNotManagedVirtualMachines");
        }

        return result;
    }

    private Repository transformToFlex(final DatacenterRepositoryDto repo)
    {
        Repository r = new Repository();
        DataCenter datacenter = new DataCenter();
        datacenter.setId(repo.getIdFromLink("datacenter"));
        r.setDatacenter(datacenter);
        r.setId(repo.getId());
        r.setName(repo.getName());
        r.setRepositoryCapacityMb(repo.getRepositoryCapacityMb());
        r.setRepositoryRemainingMb(repo.getRepositoryRemainingMb());

        return r;
    }
}
