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

/**
 * 
 */
package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.NetworkResourceStub;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;

/**
 * @author jdevesa
 */
public class NetworkResourceStubImpl extends AbstractAPIStub implements NetworkResourceStub
{

    @Override
    public BasicResult getPrivateNetworks(Integer vdcId)
    {
        DataResult<List<VlanNetwork>> result = new DataResult<List<VlanNetwork>>();

        String uri = createPrivateNetworksLink(vdcId);
        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            VLANNetworksDto networksDto = response.getEntity(VLANNetworksDto.class);
            List<VlanNetwork> nets = new ArrayList<VlanNetwork>();

            for (VLANNetworkDto dto : networksDto.getCollection())
            {
                nets.add(createFlexObject(dto));
            }

            result.setData(nets);
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "getRemoteStoragePoolsByDevice");
        }

        return result;
    }

    private VlanNetwork createFlexObject(VLANNetworkDto dto)
    {
        NetworkConfiguration netconf = new NetworkConfiguration();
        netconf.setFenceMode(dto.getNetworkConfiguration().getFenceMode());
        netconf.setGateway(dto.getNetworkConfiguration().getGateway());
        netconf.setMask(dto.getNetworkConfiguration().getMask());
        netconf.setNetmask(dto.getNetworkConfiguration().getNetMask());
        netconf.setNetworkAddress(dto.getNetworkConfiguration().getAddress());
        netconf.setPrimaryDNS(dto.getNetworkConfiguration().getPrimaryDNS());
        netconf.setSecondaryDNS(dto.getNetworkConfiguration().getSecondaryDNS());
        netconf.setSufixDNS(dto.getNetworkConfiguration().getSufixDNS());
        
        VlanNetwork newNet = new VlanNetwork();
        newNet.setConfiguration(netconf);
        newNet.setDefaultNetwork(dto.getDefaultNetwork());
        newNet.setNetworkName(dto.getName());
        newNet.setVlanNetworkId(dto.getId());
        newNet.setVlanTag(dto.getTag());
        
        
        return newNet;
    }

}
