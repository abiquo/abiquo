package com.abiquo.abiserver.commands.stub;

import java.util.List;

import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;

public interface VirtualImageResourceStub
{

    /**
     * @param datacenterId, null indicate stateful images
     * @param idCategory, null indicate return all the categories
     */
    DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        Integer idEnterprise, Integer datacenterId, Integer idCategory, Integer idHypervisorType);

    /**
     * @param datacenterId, null indicate stateful images
     * @param idCategory, null indicate return all the categories
     */
    DataResult<List<VirtualImage>> getVirtualImageByCategory(Integer idEnterprise,
        Integer datacenterId, Integer idCategory);

}
