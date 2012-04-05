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

import static java.lang.String.valueOf;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualDatacenterResourceStub;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDatacentersListResult;
import com.abiquo.abiserver.pojo.virtualhardware.Limit;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
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
    public DataResult<VirtualDataCenter> createVirtualDatacenter(final VirtualDataCenter vdc,
        final String networkName, final NetworkConfigurationHB netConfig,
        final ResourceManager resourceManager)
    {
        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        dto.setName(vdc.getName());
        dto.setHypervisorType(HypervisorType.fromValue(vdc.getHyperType().getName()));

        addLimits(vdc, dto);

        // TODO: Review these values!

        VLANNetworkDto vlanDto = new VLANNetworkDto();
        vlanDto.setName(networkName);
        vlanDto.setName(networkName);
        vlanDto.setAddress(netConfig.getNetworkAddress());
        vlanDto.setGateway(netConfig.getGateway());
        vlanDto.setMask(netConfig.getMask());
        vlanDto.setPrimaryDNS(netConfig.getPrimaryDNS());
        vlanDto.setSecondaryDNS(netConfig.getSecondaryDNS());
        vlanDto.setSufixDNS(netConfig.getSufixDNS());

        String datacenterLink =
            URIResolver.resolveURI(apiUri, "admin/datacenters/{datacenter}",
                Collections.singletonMap("datacenter", String.valueOf(vdc.getIdDataCenter())));

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
            responseVdc.setDefaultVlan(responseVdc.getDefaultVlan());
            dataResult.setData(responseVdc);
            dataResult.setMessage(resourceManager.getMessage("createVirtualDataCenter.success"));

            factory.endConnection();
        }

        return dataResult;
    }

    private void addLimits(final VirtualDataCenter vdc, final VirtualDatacenterDto dto)
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
    public DataResult<VirtualDataCenter> updateVirtualDatacenter(final VirtualDataCenter vdc,
        final ResourceManager resourceManager)
    {
        DataResult<VirtualDataCenter> dataResult = new DataResult<VirtualDataCenter>();

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
            populateErrors(response, dataResult, "updateVirtualDatacenter");
        }
        else
        {
            VirtualDatacenterDto vdcDto = response.getEntity(VirtualDatacenterDto.class);
            vdc.setHyperType(new HyperVisorType(vdcDto.getHypervisorType()));
            vdc.setName(vdcDto.getName());
            dataResult.setData(vdc);
            dataResult.setSuccess(true);
            dataResult.setMessage(resourceManager.getMessage("editVirtualDataCenter.success"));
        }

        return dataResult;
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
    public BasicResult deleteVirtualDatacenter(final VirtualDataCenter vdc,
        final ResourceManager resourceManager)
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
    public DataResult<Collection<VirtualDataCenter>> getVirtualDatacentersByEnterprise(
        final Enterprise enterprise)
    {
        DataResult<Collection<VirtualDataCenter>> result =
            new DataResult<Collection<VirtualDataCenter>>();

        // Build request URI
        String uri =
            URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters", Collections.emptyMap(),
                Collections.singletonMap("enterprise", new String[] {valueOf(enterprise.getId())}));

        ClientResponse response = get(uri, VirtualDatacentersDto.MEDIA_TYPE);
        if (response.getStatusCode() == 200)
        {
            VirtualDatacentersDto dto = response.getEntity(VirtualDatacentersDto.class);
            Collection<VirtualDataCenter> collection = new LinkedHashSet<VirtualDataCenter>();

            for (VirtualDatacenterDto vdc : dto.getCollection())
            {
                int datacenterId =
                    URIResolver.getLinkId(vdc.searchLink("datacenter"), "admin/datacenters",
                        "{datacenter}", "datacenter");

                // TODO set all limits
                ResourceAllocationLimit limits = new ResourceAllocationLimit();

                Limit publicIpLimit = new Limit();
                publicIpLimit.setHard(vdc.getPublicIpsHard());
                publicIpLimit.setSoft(vdc.getPublicIpsSoft());
                limits.setPublicIP(publicIpLimit);

                VirtualDataCenter pojo = new VirtualDataCenter();
                pojo.setId(vdc.getId());
                pojo.setName(vdc.getName());
                pojo.setLimits(limits);
                pojo.setIdDataCenter(datacenterId);
                pojo.setHyperType(new HyperVisorType(vdc.getHypervisorType()));

                collection.add(pojo);
            }

            result.setSuccess(true);
            result.setData(collection);
        }
        else
        {
            populateErrors(response, result, "getVirtualDatacenters");
        }

        return result;
    }

    @Override
    public DataResult<VirtualDatacentersListResult> getVirtualDatacentersByEnterprise(
        final Enterprise enterprise, final ListRequest listRequest)
    {
        DataResult<VirtualDatacentersListResult> result =
            new DataResult<VirtualDatacentersListResult>();

        StringBuilder buildRequest =
            new StringBuilder(createVirtualDatacentersFromEnterpriseLink(enterprise.getId()));
        VirtualDatacentersListResult listResult = new VirtualDatacentersListResult();

        if (listRequest != null)
        {
            buildRequest.append("?startwith=" + listRequest.getOffset());
            buildRequest.append("&limit=" + listRequest.getNumberOfNodes());
            if (listRequest.getOrderBy() != null && !listRequest.getOrderBy().isEmpty())
            {
                buildRequest.append("&by=" + listRequest.getOrderBy());
            }
            buildRequest.append("&asc=" + (listRequest.getAsc() == true ? "true" : "false"));
            if (listRequest.getFilterLike() != null && !listRequest.getFilterLike().isEmpty())
            {
                try
                {
                    buildRequest.append("&has=" + URIUtil.encodeQuery(listRequest.getFilterLike()));
                }
                catch (URIException e)
                {
                }
            }
        }

        ClientResponse response = get(buildRequest.toString(), VirtualDatacentersDto.MEDIA_TYPE);

        if (response.getStatusCode() == 200)
        {
            VirtualDatacentersDto dto = response.getEntity(VirtualDatacentersDto.class);
            Collection<VirtualDataCenter> collection = new LinkedHashSet<VirtualDataCenter>();

            for (VirtualDatacenterDto vdc : dto.getCollection())
            {

                int datacenterId =
                    URIResolver.getLinkId(vdc.searchLink("datacenter"), "admin/datacenters",
                        "{datacenter}", "datacenter");

                VirtualDataCenter vdctoadd =
                    VirtualDataCenter.create(vdc, datacenterId, enterprise);

                // Get the default network of the vdc.
                RESTLink link = vdc.searchLink("defaultnetwork");
                response = get(link.getHref(), VLANNetworkDto.MEDIA_TYPE);
                VLANNetworkDto vlanDto = response.getEntity(VLANNetworkDto.class);

                vdctoadd.setDefaultVlan(NetworkResourceStubImpl.createFlexObject(vlanDto));

                collection.add(vdctoadd);
            }

            Integer total =
                dto.getTotalSize() != null ? dto.getTotalSize() : dto.getCollection().size();

            listResult.setTotalVirtualDatacenters(total);
            listResult.setVirtualDatacentersList(collection);

            result.setSuccess(true);
            result.setData(listResult);
        }
        else
        {
            populateErrors(response, result, "getVirtualDatacenters");
        }

        return result;
    }

}
