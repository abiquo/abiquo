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

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.DatacentersResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

/**
 * @author scastro
 */
public class DatacentersResourceStubImpl extends AbstractAPIStub implements DatacentersResourceStub
{

    @Override
    public DataResult<ArrayList<DataCenter>> getDatacenters()
    {
        DataResult<ArrayList<DataCenter>> result = new DataResult<ArrayList<DataCenter>>();
        ArrayList<DataCenter> dcs = new ArrayList<DataCenter>();

        ClientResponse response = get(createDatacenterLink(), FLAT_MEDIA_TYPE);

        if (response.getStatusCode() == 200)
        {
            DatacentersDto responseDto = response.getEntity(DatacentersDto.class);

            if (responseDto.getCollection() != null)
            {
                for (DatacenterDto dcDto : responseDto.getCollection())
                {
                    dcs.add(fromDtoToPojo(dcDto));
                }
            }

            result.setSuccess(true);
            result.setData(dcs);
        }
        else
        {
            populateErrors(response, result, "getDatacenters");
        }

        return result;
    }

    @Override
    public DataResult<DataCenter> getDatacenter(final Integer datacenterId)
    {
        DataResult<DataCenter> result = new DataResult<DataCenter>();

        ClientResponse response = get(createDatacenterLink(datacenterId));

        if (response.getStatusCode() == 200)
        {
            DatacenterDto responseDto = response.getEntity(DatacenterDto.class);

            DataCenter dataCenter = fromDtoToPojo(responseDto);

            result.setSuccess(true);
            result.setData(dataCenter);
        }
        else
        {
            populateErrors(response, result, "getDatacenters");
        }

        return result;
    }

    @Override
    public DataResult<DataCenter> createDatacenter(final DataCenter datacenter)
    {
        DataResult<DataCenter> result = new DataResult<DataCenter>();

        if (!checkRemoteServices(datacenter.getRemoteServices()))
        {
            result.setSuccess(false);
            result.setMessage("Some remote service URIs have invalid syntax");
            return result;
        }

        DatacenterDto dto = fromDatacenterToDto(datacenter);
        dto.setId(null);

        ClientResponse response = post(createDatacenterLink(), dto);

        if (response.getStatusCode() == 201)
        {
            DatacenterDto responseDto = response.getEntity(DatacenterDto.class);

            DataCenter dc = DataCenter.create(responseDto);
            dc.setRemoteServices(new ArrayList<RemoteService>());
            result.setSuccess(true);

            if (responseDto.getRemoteServices() != null
                && !responseDto.getRemoteServices().isEmpty())
            {
                for (RemoteServiceDto rsdto : responseDto.getRemoteServices().getCollection())
                {
                    dc.getRemoteServices().add(RemoteService.create(rsdto, dc.getId()));
                }

                if (responseDto.getRemoteServices().getConfigErrors() != null
                    && !responseDto.getRemoteServices().getConfigErrors().isEmpty())
                {
                    result.setSuccess(false);
                    result
                        .setMessage("Datacenter '"
                            + dc.getName()
                            + "' has been created but some Remote Services had configuration errors. Please check the events to fix the problems.");
                }
            }

            result.setData(dc);
        }
        else
        {
            populateErrors(response, result, "createDatacenter");
        }

        return result;
    }

    @Override
    public DataResult<DataCenter> modifyDatacenter(final DataCenter datacenter)
    {
        DataResult<DataCenter> result = new DataResult<DataCenter>();

        DatacenterDto dto = fromDatacenterToDto(datacenter);

        ClientResponse response = put(createDatacenterLink(datacenter.getId()), dto);

        if (response.getStatusCode() == 200)
        {
            DatacenterDto responseDto = response.getEntity(DatacenterDto.class);
            DataCenter dc = DataCenter.create(responseDto);
            result.setSuccess(true);
            result.setData(dc);
        }
        else
        {
            populateErrors(response, result, "modifyDatacenter");
        }

        return result;
    }

    @Override
    public DataResult<DataCenter> deleteDatacenter(final DataCenter datacenter)
    {
        DataResult<DataCenter> result = new DataResult<DataCenter>();

        ClientResponse response = delete(createDatacenterLink(datacenter.getId()));

        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteDatacenter");
        }

        return result;
    }

    @Override
    public BasicResult updateUsedResources(final Integer datacenterId)
    {
        BasicResult result = new BasicResult();

        ClientResponse response = get(createDatacenterLinkUsedResources(datacenterId));

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);
            result.setMessage(InfrastructureCommandImpl.getResourceManager().getMessage(
                "updateUsedResourcesByDatacenter.success"));
        }
        else
        {
            populateErrors(response, result, "updateUsedResources");
        }

        return result;
    }

    // Utility functions

    private boolean checkRemoteServices(final List<RemoteService> remoteServices)
    {
        if (remoteServices == null || remoteServices.isEmpty())
        {
            return false;
        }
        for (RemoteService rs : remoteServices)
        {
            if (!rs.checkUri())
            {
                return false;
            }
        }
        return true;
    }

    private DatacenterDto fromDatacenterToDto(final DataCenter datacenter)
    {
        DatacenterDto newDatacenter = new DatacenterDto();

        newDatacenter.setId(datacenter.getId());
        newDatacenter.setLocation(datacenter.getSituation());
        newDatacenter.setName(datacenter.getName());

        RemoteServicesDto rsdto = new RemoteServicesDto();

        for (RemoteService rs : datacenter.getRemoteServices())
        {
            rsdto.add(fromRemoteServicesToDto(rs));
        }

        newDatacenter.setRemoteServices(rsdto);

        return newDatacenter;
    }

    private RemoteServiceDto fromRemoteServicesToDto(final RemoteService rs)
    {
        RemoteServiceDto rsdto = new RemoteServiceDto();
        rsdto.setId(rs.getIdRemoteService());
        rsdto.setStatus(rs.getStatus());
        rsdto.setType(getRemoteServiceType(rs));
        rsdto.setUri(rs.getUri());
        return rsdto;
    }

    public DataCenter fromDtoToPojo(final DatacenterDto datanceterDto)
    {

        DataCenter dc = DataCenter.create(datanceterDto);
        dc.setRemoteServices(new ArrayList<RemoteService>());
        if (datanceterDto.getRemoteServices() != null
            && !datanceterDto.getRemoteServices().isEmpty())
        {
            for (RemoteServiceDto rsdto : datanceterDto.getRemoteServices().getCollection())
            {
                dc.getRemoteServices().add(RemoteService.create(rsdto, dc.getId()));
            }
        }

        return dc;
    }

    private RemoteServiceType getRemoteServiceType(final RemoteService rs)
    {
        String rst = rs.getServiceMapping();
        if (rst.equals(RemoteServiceType.APPLIANCE_MANAGER.getServiceMapping()))
        {
            return RemoteServiceType.APPLIANCE_MANAGER;
        }
        else if (rst.equals(RemoteServiceType.BPM_SERVICE.getServiceMapping()))
        {
            return RemoteServiceType.BPM_SERVICE;
        }
        else if (rst.equals(RemoteServiceType.DHCP_SERVICE.getServiceMapping()))
        {
            return RemoteServiceType.DHCP_SERVICE;
        }
        else if (rst.equals(RemoteServiceType.NODE_COLLECTOR.getServiceMapping()))
        {
            return RemoteServiceType.NODE_COLLECTOR;
        }
        else if (rst.equals(RemoteServiceType.STORAGE_SYSTEM_MONITOR.getServiceMapping()))
        {
            return RemoteServiceType.STORAGE_SYSTEM_MONITOR;
        }
        else if (rst.equals(RemoteServiceType.VIRTUAL_FACTORY.getServiceMapping()))
        {
            return RemoteServiceType.VIRTUAL_FACTORY;
        }
        else if (rst.equals(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR.getServiceMapping()))
        {
            return RemoteServiceType.VIRTUAL_SYSTEM_MONITOR;
        }
        return null;
    }
}
