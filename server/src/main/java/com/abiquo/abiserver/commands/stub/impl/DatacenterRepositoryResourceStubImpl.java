package com.abiquo.abiserver.commands.stub.impl;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.DatacenterRepositoryResourceStub;
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

    private Repository transformToFlex(DatacenterRepositoryDto repo)
    {
        Repository r = new Repository();
        // XXX used ? remove this!
        // r.setDatacenter(datacenter);
        // r.setRepositoryEnterpriseUsedMb(repo.get);
        r.setId(repo.getId());
        r.setName(repo.getName());
        r.setRepositoryCapacityMb(repo.getRepositoryCapacityMb());
        r.setRepositoryRemainingMb(repo.getRepositoryRemainingMb());

        return r;
    }
}
