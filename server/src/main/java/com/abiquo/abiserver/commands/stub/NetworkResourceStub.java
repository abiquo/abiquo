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
package com.abiquo.abiserver.commands.stub;

import java.util.ArrayList;

import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.IpPoolManagement;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * @author jdevesa
 */
public interface NetworkResourceStub
{
    public BasicResult checkVLANTagAvailability(Integer datacenterId, Integer proposedVLANTag,
        Integer currentVlanId);

    public BasicResult createPrivateVLANNetwork(UserSession userSession, Integer vdcId,
        VLANNetworkDto dto);

    public BasicResult createPublicVlan(Integer idDatacenter, String networkName, Integer vlanTag,
        NetworkConfiguration configuration, Enterprise enterprise);

    public BasicResult deletePublicVlan(Integer datacenterId, Integer vlanId);

    public BasicResult editPublicIp(Integer datacenterId, Integer vlanId, Integer idManagement,
        IpPoolManagement ipPoolManagement);

    public BasicResult editPublicIps(Integer datacenterId, Integer vlanNetworkId,
        ArrayList<IpPoolManagement> listOfPublicIPs);

    public BasicResult editPublicVlan(Integer datacenterId, Integer vlanNetworkId, String vlanName,
        Integer vlanTag, NetworkConfiguration configuration, Boolean defaultNetwork,
        Enterprise enterprise);

    public BasicResult getEnterpriseFromReservedVlanId(Integer datacenterId, Integer vlanId);

    public BasicResult getEnterprisesWithNetworksByDatacenter(UserSession userSession,
        Integer datacenterId, Integer offset, Integer numElem, String filterLike)
        throws NetworkCommandException;

    public BasicResult getInfoDHCPServer(UserSession userSession, Integer vdcId)
        throws NetworkCommandException;

    public BasicResult getListNetworkPoolByEnterprise(Integer enterpriseId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws NetworkCommandException;

    public BasicResult getListNetworkPoolByPrivateVLAN(Integer vdcId, Integer vlanId,
        Integer offset, Integer numberOfNodes, String filterLike, String orderBy, Boolean asc,
        Boolean onlyAvailable);

    public BasicResult getListNetworkPoolByVirtualDatacenter(Integer vdcId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws NetworkCommandException;

    public BasicResult getListNetworkPublicPoolByDatacenter(Integer datacenterId, Integer offset,
        Integer numberOfNodes, String filterLike, String orderBy, Boolean asc)
        throws NetworkCommandException;

    public BasicResult getListNetworkPublicPoolByVlan(Integer datacenterId, Integer vlanId,
        Integer offset, Integer numberOfNodes, String filterLike, String orderBy, Boolean asc,
        Boolean all) throws NetworkCommandException;

    public BasicResult getPrivateNetworks(final Integer vdcId);

    public BasicResult getPublicNetwork(final Integer datacenterId, final Integer vlanId);

}
