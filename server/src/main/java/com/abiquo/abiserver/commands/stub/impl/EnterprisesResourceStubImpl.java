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
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.EnterprisesResourceStub;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.EnterpriseListResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualhardware.DatacenterLimit;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceWithLimitsDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.util.URIResolver;

public class EnterprisesResourceStubImpl extends AbstractAPIStub implements EnterprisesResourceStub
{

    @Override
    public DataResult<Enterprise> createEnterprise(final Enterprise enterprise)
    {
        String uri = createEnterprisesLink(null, null, null);

        EnterpriseDto dto = fromEnterpriseToDto(enterprise);

        DataResult<Enterprise> result = new DataResult<Enterprise>();

        ClientResponse response = post(uri, dto);
        if (response.getStatusCode() == 201)
        {
            Enterprise data = getEnterprise(response);

            ErrorsDto errorsDcLimits = createDatacenterLimits(enterprise, data);
            ErrorsDto errorsMachines = assignMachines(enterprise, data);

            boolean success = errorsDcLimits == null && errorsMachines == null;
            if (!success)
            {
                String message = "";
                if (errorsDcLimits != null)
                {
                    message += errorsDcLimits.toString();
                }
                if (errorsMachines != null)
                {
                    message += errorsMachines.toString();
                }
            }

            result.setSuccess(success);
            result.setData(data);
        }
        else
        {
            populateErrors(response, result, "createEnterprise");
        }

        return result;
    }

    @Override
    public DataResult<Enterprise> editEnterprise(final Enterprise enterprise)
    {
        return editEnterprise(enterprise, null);
    }

    protected EnterpriseDto fromPricingEnterpriseToDto(final Enterprise enterprise)
    {
        EnterpriseDto dto = new EnterpriseDto();
        dto.setName(enterprise.getName());
        return dto;
    }

    protected EnterpriseDto fromEnterpriseToDto(final Enterprise enterprise)
    {
        EnterpriseDto dto = new EnterpriseDto();
        dto.setName(enterprise.getName());

        ResourceAllocationLimit limits = enterprise.getLimits();
        return (EnterpriseDto) fillLimits(dto, limits);
    }

    protected SingleResourceWithLimitsDto fillLimits(final SingleResourceWithLimitsDto dto,
        final ResourceAllocationLimit limits)
    {
        dto.setCpuCountLimits((int) limits.getCpu().getSoft(), (int) limits.getCpu().getHard());
        dto.setRamLimitsInMb((int) limits.getRam().getSoft(), (int) limits.getRam().getHard());
        dto.setHdLimitsInMb(limits.getHd().getSoft(), limits.getHd().getHard());
        dto.setStorageLimits(limits.getStorage().getSoft(), limits.getStorage().getHard());
        dto.setVlansLimits(limits.getVlan().getSoft(), limits.getVlan().getHard());
        dto.setPublicIPLimits(limits.getPublicIP().getSoft(), limits.getPublicIP().getHard());
        return dto;
    }

    protected ErrorsDto createDatacenterLimits(final Enterprise enterprise, final Enterprise data)
    {
        if (CollectionUtils.isEmpty(enterprise.getDcLimits()))
        {
            return null;
        }

        String uri = createDatacenterLimitsUri(data);

        for (DatacenterLimit limit : enterprise.getDcLimits())
        {
            DatacenterLimitsDto dto = new DatacenterLimitsDto();
            fillLimits(dto, limit.getLimits());

            String datacenterUri = createDatacenterLink(limit.getDatacenter().getId());
            dto.addLink(new RESTLink("datacenter", datacenterUri));

            Resource resource = resource(uri);
            resource = resource.queryParam("datacenter", limit.getDatacenter().getId());
            ClientResponse response = resource.post(dto);
            // ClientResponse response = post(uri, dto);

            if (response.getStatusCode() == 201)
            {
                limit.setEnterprise(data);
                data.addDatacenterLimit(limit);
            }
            else
            {
                return response.getEntity(ErrorsDto.class);
            }
        }

        return null;
    }

    protected ErrorsDto modifyDatacenterLimits(final Enterprise ent)
    {
        // community impl (no limit)
        return null;
    }

    protected ErrorsDto modifyReservedMachines(final Enterprise ent)
    {
        // community impl (no limit)
        return null;
    }

    private ErrorsDto assignMachines(final Enterprise enterprise, final Enterprise data)
    {
        if (CollectionUtils.isEmpty(enterprise.getReservedMachines()))
        {
            return null;
        }

        String uri = createReservedMachinesUri(data);

        for (PhysicalMachine machine : enterprise.getReservedMachines())
        {
            MachineDto dto = MachineResourceStubImpl.fromPhysicalMachineToDto(machine);

            ClientResponse response = post(uri, dto);
            if (response.getStatusCode() == 201)
            {
                data.addReservedMachine(machine);
            }
            else
            {
                return response.getEntity(ErrorsDto.class);
            }
        }

        return null;
    }

    @Override
    public BasicResult deleteEnterprise(final Integer enterpriseId)
    {
        BasicResult result = new BasicResult();

        String uri = createEnterpriseLink(enterpriseId);

        ClientResponse response = delete(uri);
        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteEnterprise");
        }

        return result;
    }

    protected String createReservedMachinesUri(final Enterprise enterprise)
    {
        String uri = createEnterpriseLink(enterprise.getId());

        return UriHelper.appendPathToBaseUri(uri, "reservedmachines");
    }

    protected String createDatacenterLimitsUri(final Enterprise enterprise)
    {
        String uri = createEnterpriseLink(enterprise.getId());

        return UriHelper.appendPathToBaseUri(uri, "limits");
    }

    @Override
    public DataResult<EnterpriseListResult> getEnterprises(final ListRequest enterpriseListOptions)
    {
        DataResult<EnterpriseListResult> result = new DataResult<EnterpriseListResult>();

        String uri =
            createEnterprisesLink(enterpriseListOptions.getFilterLike(),
                enterpriseListOptions.getOffset(), enterpriseListOptions.getNumberOfNodes());

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);

            EnterprisesDto responseDto = response.getEntity(EnterprisesDto.class);

            EnterpriseListResult listResult = new EnterpriseListResult();
            Collection<Enterprise> list = new LinkedHashSet<Enterprise>();
            for (EnterpriseDto dto : responseDto.getCollection())
            {
                list.add(Enterprise.create(dto));
            }
            listResult.setEnterprisesList(list);

            Integer total =
                responseDto.getTotalSize() != null ? responseDto.getTotalSize() : list.size();
            listResult.setTotalEnterprises(total);

            result.setData(listResult);
        }
        else
        {
            populateErrors(response, result, "getEnterprises");
        }

        return result;
    }

    @Override
    public DataResult<Enterprise> getEnterprise(final Integer enterpriseId)
    {
        DataResult<Enterprise> result = new DataResult<Enterprise>();

        String uri = createEnterpriseLink(enterpriseId);

        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);

            Enterprise enterprise = getEnterprise(response);

            result.setData(enterprise);
        }
        else
        {
            populateErrors(response, result, "getEnterprise");
        }

        return result;
    }

    @Override
    public DataResult<Collection<VirtualDataCenter>> getVirtualDatacenters(
        final Enterprise enterprise)
    {
        DataResult<Collection<VirtualDataCenter>> result =
            new DataResult<Collection<VirtualDataCenter>>();

        String uri = createVirtualDatacentersFromEnterpriseLink(enterprise.getId());

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
                datacenters.add(VirtualDataCenter.create(vdc, datacenterId, enterprise,
                    network.toPojo()));
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

    protected Enterprise getEnterprise(final ClientResponse response)
    {
        EnterpriseDto responseDto = response.getEntity(EnterpriseDto.class);
        Enterprise enterprise = Enterprise.create(responseDto);
        return enterprise;
    }

    @Override
    public DataResult<EnterpriseListResult> getEnterprisesWithPricingTemplate(
        final ListRequest enterpriseListOptions, final Integer idPricingTemplate,
        final boolean included)
    {
        DataResult<EnterpriseListResult> result = new DataResult<EnterpriseListResult>();

        String uri =
            createEnterprisesLink(enterpriseListOptions.getFilterLike(),
                enterpriseListOptions.getOffset(), enterpriseListOptions.getNumberOfNodes());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (idPricingTemplate != null)
        {
            queryParams.put("idPricingTemplate", new String[] {String.valueOf(idPricingTemplate)});
        }
        queryParams.put("included", new String[] {String.valueOf(included)});

        uri = UriHelper.appendQueryParamsToPath(uri, queryParams, false);

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);

            EnterprisesDto responseDto = response.getEntity(EnterprisesDto.class);

            EnterpriseListResult listResult = new EnterpriseListResult();
            Collection<Enterprise> list = new LinkedHashSet<Enterprise>();
            for (EnterpriseDto dto : responseDto.getCollection())
            {
                list.add(Enterprise.create(dto));
            }
            listResult.setEnterprisesList(list);

            Integer total =
                responseDto.getTotalSize() != null ? responseDto.getTotalSize() : list.size();
            listResult.setTotalEnterprises(total);

            result.setData(listResult);
        }
        else
        {
            populateErrors(response, result, "getEnterprises");
        }

        return result;
    }

    @Override
    public DataResult<Enterprise> editEnterprise(final Enterprise enterprise,
        final Integer idPricingTemplate)
    {
        DataResult<Enterprise> result;
        ErrorsDto errors = modifyDatacenterLimits(enterprise);

        if (errors != null)
        {
            result = new DataResult<Enterprise>();
            result.setSuccess(false);
            result.setMessage(errors.toString());
            return result;
        }

        errors = modifyReservedMachines(enterprise);

        if (errors != null)
        {
            result = new DataResult<Enterprise>();
            result.setSuccess(false);
            result.setMessage(errors.toString());
            return result;
        }

        String uri = createEnterpriseLink(enterprise.getId());

        EnterpriseDto dto;
        if (idPricingTemplate != null)
        {
            dto = fromPricingEnterpriseToDto(enterprise);
            dto.setIdPricingTemplate(idPricingTemplate);
        }
        else
        {
            dto = fromEnterpriseToDto(enterprise);
        }

        result = new DataResult<Enterprise>();

        ClientResponse response = put(uri, dto);
        if (response.getStatusCode() == 200)
        {
            Enterprise data = getEnterprise(response);

            result.setSuccess(true);
            result.setData(data);
        }
        else
        {
            populateErrors(response, result, "editEnterprise");
        }

        return result;
    }
}
