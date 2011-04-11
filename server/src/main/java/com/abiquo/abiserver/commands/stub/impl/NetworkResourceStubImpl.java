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
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.IpPoolManagement;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListResponse;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
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

    @Override
    public BasicResult getListNetworkPoolByEnterprise(Integer enterpriseId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws NetworkCommandException
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();
        ListResponse<IpPoolManagement> listResponse = new ListResponse<IpPoolManagement>();

        StringBuilder buildRequest = new StringBuilder(createEnterpriseIPsLink(enterpriseId));
        buildRequest.append("?startwith=" + offset);
        buildRequest.append("&limit=" + numElem);
        buildRequest.append("&by=" + transformOrderBy(orderBy));
        buildRequest.append("&asc=" + ((asc) ? "true" : "false"));
        if (!filterLike.isEmpty())
        {
            buildRequest.append("&has=" + filterLike);
        }

        ClientResponse response = get(buildRequest.toString());

        if (response.getStatusCode() == 200)
        {
            IpsPoolManagementDto ips = response.getEntity(IpsPoolManagementDto.class);
            List<IpPoolManagement> flexIps = new ArrayList<IpPoolManagement>();

            for (IpPoolManagementDto ip : ips.getCollection())
            {
                IpPoolManagement flexIp = createFlexObject(ip);
                flexIp.setEnterpriseId(enterpriseId);
                flexIps.add(flexIp);
            }
            listResponse.setList(flexIps);
            listResponse.setTotalNumEntities(ips.getTotalSize());

            dataResult.setData(listResponse);
            dataResult.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, dataResult, "getListNetworkPoolByEnterprise");
        }

        return dataResult;
    }

    @Override
    public BasicResult getListNetworkPoolByVirtualDatacenter(Integer vdcId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws NetworkCommandException
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();
        ListResponse<IpPoolManagement> listResponse = new ListResponse<IpPoolManagement>();

        StringBuilder buildRequest =
            new StringBuilder(createVirtualDatacenterPrivateIPsLink(vdcId));
        buildRequest.append("?startwith=" + offset);
        buildRequest.append("&limit=" + numElem);
        buildRequest.append("&by=" + transformOrderBy(orderBy));
        buildRequest.append("&asc=" + ((asc) ? "true" : "false"));
        if (!filterLike.isEmpty())
        {
            buildRequest.append("&has=" + filterLike);
        }

        ClientResponse response = get(buildRequest.toString());

        if (response.getStatusCode() == 200)
        {
            IpsPoolManagementDto ips = response.getEntity(IpsPoolManagementDto.class);
            List<IpPoolManagement> flexIps = new ArrayList<IpPoolManagement>();

            for (IpPoolManagementDto ip : ips.getCollection())
            {
                IpPoolManagement flexIp = createFlexObject(ip);
                flexIps.add(flexIp);
            }
            listResponse.setList(flexIps);
            listResponse.setTotalNumEntities(ips.getTotalSize());

            dataResult.setData(listResponse);
            dataResult.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, dataResult, "getListNetworkPoolByEnterprise");
        }

        return dataResult;
    }

    @Override
    public BasicResult getEnterprisesWithNetworksByDatacenter(UserSession userSession,
        Integer datacenterId, Integer offset, Integer numElem, String ipLike)
        throws NetworkCommandException
    {
        DataResult<ListResponse<Enterprise>> dataResult =
            new DataResult<ListResponse<Enterprise>>();

        List<Enterprise> listEnt = new ArrayList<Enterprise>();

        StringBuilder buildRequest = new StringBuilder(createDatacenterLink(datacenterId));
        buildRequest.append("/action/enterprises");
        buildRequest.append("?network=true");
        buildRequest.append("&startwith=" + offset);
        buildRequest.append("&limit=" + numElem);

        ClientResponse response = get(buildRequest.toString());

        if (response.getStatusCode() == 200)
        {
            EnterprisesDto enterprises = response.getEntity(EnterprisesDto.class);
            for (EnterpriseDto entdto : enterprises.getCollection())
            {

                Enterprise e = Enterprise.create(entdto);
                listEnt.add(e);
            }
            ListResponse<Enterprise> listResponse = new ListResponse<Enterprise>();
            listResponse.setList(listEnt);
            listResponse.setTotalNumEntities(listEnt.size());

            dataResult.setData(listResponse);
            dataResult.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, dataResult, "getEnterprisesWithNetworksByDatacenter");
        }
        return dataResult;

    }

    public BasicResult getInfoDHCPServer(UserSession userSession, Integer vdcId)
        throws NetworkCommandException
    {
        DataResult<String> dataResult = new DataResult<String>();
        StringBuilder buildRequest = new StringBuilder(createVirtualDatacentersLink());
        buildRequest.append("/" + vdcId.toString());
        buildRequest.append("/action/dhcpinfo");

        ClientResponse response = get(buildRequest.toString());
        if (response.getStatusCode() == 200)
        {
            String dhcpinfo = response.getEntity(String.class);
            dataResult.setData(dhcpinfo);
            dataResult.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, dataResult, "getEnterprisesWithNetworksByDatacenter");
        }
        return dataResult;
    }

    private String transformOrderBy(String orderBy)
    {
        if (orderBy == null)
        {
            return "ip";
        }
        else if (orderBy.equalsIgnoreCase("vlannetworkname"))
        {
            return "vlan";
        }
        else if (orderBy.equalsIgnoreCase("virtualappliancename"))
        {
            return "virtualappliance";
        }
        else if (orderBy.equalsIgnoreCase("virtualmachinename"))
        {
            return "virtualmachine";
        }
        else
            return orderBy;
    }

    private IpPoolManagement createFlexObject(IpPoolManagementDto ip)
    {
        IpPoolManagement flexIp = new IpPoolManagement();

        flexIp.setIdManagement(ip.getId());
        flexIp.setIp(ip.getIp());
        flexIp.setMac(ip.getMac());
        flexIp.setQuarantine(ip.getQuarantine());
        flexIp.setConfigureGateway(ip.getConfigurationGateway());
        flexIp.setName(ip.getName());

        for (RESTLink currentLink : ip.getLinks())
        {
            if (currentLink.getRel().equalsIgnoreCase("privatenetwork"))
            {
                flexIp.setVlanNetworkName(currentLink.getTitle());
            }
            else if (currentLink.getRel().equalsIgnoreCase("virtualdatacenter"))
            {
                flexIp.setVirtualDatacenterName(currentLink.getTitle());
                flexIp.setVirtualDatacenterId(Integer.valueOf(currentLink.getHref().substring(
                    currentLink.getHref().lastIndexOf("/") + 1)));
            }
            else if (currentLink.getRel().equalsIgnoreCase("virtualappliance"))
            {
                flexIp.setVirtualApplianceName(currentLink.getTitle());
                flexIp.setVirtualApplianceId(Integer.valueOf(currentLink.getHref().substring(
                    currentLink.getHref().lastIndexOf("/") + 1)));
            }
            else if (currentLink.getRel().equalsIgnoreCase("virtualmachine"))
            {
                flexIp.setVirtualMachineName(currentLink.getTitle());
                flexIp.setVirtualMachineId(Integer.valueOf(currentLink.getHref().substring(
                    currentLink.getHref().lastIndexOf("/") + 1)));
            }
        }

        return flexIp;
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
