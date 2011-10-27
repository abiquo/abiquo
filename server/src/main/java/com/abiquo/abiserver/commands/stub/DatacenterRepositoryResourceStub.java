package com.abiquo.abiserver.commands.stub;

import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Repository;

public interface DatacenterRepositoryResourceStub
{

    public DataResult<Repository> getRepository(final Integer idDatacenter, final Integer idEnterprise,
        final boolean refresh, final boolean includeUsage);
}
