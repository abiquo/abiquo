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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualDatacenterResourceStub;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.util.URIResolver;
import com.abiquo.util.resources.ResourceManager;

import edu.emory.mathcs.backport.java.util.Collections;

public class VirtualDatacenterResourceStubImpl extends AbstractAPIStub implements
    VirtualDatacenterResourceStub
{
    public VirtualDatacenterResourceStubImpl()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.stub.impl.VirtualDatacenterResourceStub#createVirtualDatacenter
     * (com.abiquo.abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter, java.lang.String,
     * com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB,
     * com.abiquo.util.resources.ResourceManager, com.abiquo.util.ErrorManager)
     */
    @Override
    @SuppressWarnings("unchecked")
    public DataResult<VirtualDataCenter> createVirtualDatacenter(VirtualDataCenter vdc,
        String networkName, NetworkConfigurationHB netConfig, ResourceManager resourceManager)
    {
        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        dto.setName(vdc.getName());
        dto.setHypervisorType(HypervisorType.fromValue(vdc.getHyperType().getName()));

        addLimits(vdc, dto);

        VLANNetworkDto vlanDto = new VLANNetworkDto();
        vlanDto.setName(networkName);
        vlanDto.setAddress(netConfig.getNetworkAddress());
        vlanDto.setGateway(netConfig.getGateway());
        vlanDto.setMask(netConfig.getMask());
        vlanDto.setPrimaryDNS(netConfig.getPrimaryDNS());
        vlanDto.setSecondaryDNS(netConfig.getSecondaryDNS());
        vlanDto.setSufixDNS(netConfig.getSufixDNS());
        vlanDto.setDefaultNetwork(Boolean.TRUE);

        String datacenterLink =
            URIResolver.resolveURI(apiUri, "admin/datacenters/{datacenter}", Collections
                .singletonMap("datacenter", String.valueOf(vdc.getIdDataCenter())));

        String enterpriseLink = createEnterpriseLink(vdc.getEnterprise().getId());
        URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters", new HashMap<String, String>());
        dto.addLink(new RESTLink("datacenter", datacenterLink));
        dto.addLink(new RESTLink("enterprise", enterpriseLink));

        dto.setVlan(vlanDto);

        String uri = createVirtualDatacentersLink();

        ClientResponse response = post(uri, dto);

        DataResult<VirtualDataCenter> dataResult = new DataResult<VirtualDataCenter>();

        if (response.getStatusCode() != 201)
        {
            populateErrors(response, dataResult, "updateVirtualDatacenter");
        }
        else
        {
            dataResult.setSuccess(true);
            VirtualDatacenterDto responseDto = response.getEntity(VirtualDatacenterDto.class);

            DAOFactory factory = HibernateDAOFactory.instance();
            factory.beginConnection();

            NetworkHB network =
                factory.getNetworkDAO().findByVirtualDatacenter(responseDto.getId());
            VirtualDataCenter responseVdc =
                VirtualDataCenter.create(responseDto, vdc.getIdDataCenter(), vdc.getEnterprise(),
                    network.toPojo());
            responseVdc.setLimits(vdc.getLimits());

            dataResult.setData(responseVdc);
            dataResult.setMessage(resourceManager.getMessage("createVirtualDataCenter.success"));

            factory.endConnection();
        }

        return dataResult;
    }

    private void addLimits(VirtualDataCenter vdc, VirtualDatacenterDto dto)
    {
        if (vdc.getLimits() != null)
        {
            dto.setCpuCountLimits((int) vdc.getLimits().getCpu().getSoft(), (int) vdc.getLimits()
                .getCpu().getHard());
            dto.setHdLimitsInMb(vdc.getLimits().getHd().getSoft(), vdc.getLimits().getHd()
                .getHard());
            dto.setRamLimitsInMb((int) vdc.getLimits().getRam().getSoft(), (int) vdc.getLimits()
                .getRam().getHard());
            dto.setStorageLimits(vdc.getLimits().getStorage().getSoft(), vdc.getLimits()
                .getStorage().getHard());
            dto.setVlansLimits(vdc.getLimits().getVlan().getSoft(), vdc.getLimits().getVlan()
                .getHard());
            dto.setPublicIPLimits(vdc.getLimits().getPublicIP().getSoft(), vdc.getLimits()
                .getPublicIP().getHard());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.stub.impl.VirtualDatacenterResourceStub#updateVirtualDatacenter
     * (com.abiquo.abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter,
     * com.abiquo.util.resources.ResourceManager, com.abiquo.util.ErrorManager)
     */
    @Override
    @SuppressWarnings("unchecked")
    public BasicResult updateVirtualDatacenter(VirtualDataCenter vdc,
        ResourceManager resourceManager)
    {
        BasicResult basicResult = new BasicResult();

        String uri =
            URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters/{virtualDatacenter}",
                Collections.singletonMap("virtualDatacenter", String.valueOf(vdc.getId())));

        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        dto.setId(vdc.getId());
        dto.setName(vdc.getName());

        addLimits(vdc, dto);

        ClientResponse response = put(uri, dto);

        if (response.getStatusCode() != 200)
        {
            populateErrors(response, basicResult, "updateVirtualDatacenter");
        }
        else
        {
            basicResult.setSuccess(true);
            basicResult.setMessage(resourceManager.getMessage("editVirtualDataCenter.success"));
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.stub.impl.VirtualDatacenterResourceStub#deleteVirtualDatacenter
     * (com.abiquo.abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter,
     * com.abiquo.util.resources.ResourceManager)
     */
    @Override
    @SuppressWarnings("unchecked")
    public BasicResult deleteVirtualDatacenter(VirtualDataCenter vdc,
        ResourceManager resourceManager)
    {
        BasicResult basicResult = new BasicResult();

        String uri =
            URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters/{virtualDatacenter}",
                Collections.singletonMap("virtualDatacenter", String.valueOf(vdc.getId())));

        ClientResponse response = delete(uri);

        if (response.getStatusCode() != 204)
        {
            populateErrors(response, basicResult, "deleteVirtualDatacenter");
        }
        else
        {
            basicResult.setSuccess(true);
            basicResult.setMessage(resourceManager.getMessage("deleteVirtualDataCenter.success"));
        }

        return basicResult;
    }

    @Override
    public DataResult<Collection<VirtualDataCenter>> getVirtualDatacenters(Enterprise enterprise,
        DataCenter datacenter)
    {
        DataResult<Collection<VirtualDataCenter>> result =
            new DataResult<Collection<VirtualDataCenter>>();

        String uri = createVirtualDatacentersLink(enterprise, datacenter);

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);
            DAOFactory factory = HibernateDAOFactory.instance();
            factory.beginConnection();

            VirtualDatacentersDto dto = response.getEntity(VirtualDatacentersDto.class);
            Collection<VirtualDataCenter> datacenters = new LinkedHashSet<VirtualDataCenter>();

            for (VirtualDatacenterDto vdc : dto.getCollection())
            {
                int datacenterId =
                    URIResolver.getLinkId(vdc.searchLink("datacenter"), "admin/datacenters",
                        "{datacenter}", "datacenter");

                NetworkHB network = factory.getNetworkDAO().findByVirtualDatacenter(vdc.getId());
                datacenters.add(VirtualDataCenter.create(vdc, datacenterId, enterprise, network
                    .toPojo()));
            }
            result.setData(datacenters);

            factory.endConnection();
        }
        else
        {
            populateErrors(response, result, "getVirtualDatacenters");
        }

        return result;
    }
}
