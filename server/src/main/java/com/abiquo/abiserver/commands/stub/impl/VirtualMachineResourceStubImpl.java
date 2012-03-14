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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.util.URIResolver;

public class VirtualMachineResourceStubImpl extends AbstractAPIStub implements
    VirtualMachineResourceStub
{
    public VirtualMachineResourceStubImpl()
    {
        super();
    }

    @Override
    public BasicResult updateVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {

        BasicResult result = new BasicResult();

        try
        {
            // Retrieve the VirtualDatacenter to associate the new virtual appliance
            org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                getApiClient().getCloudService().getVirtualDatacenter(virtualDatacenterId)
                    .getVirtualAppliance(virtualApplianceId);

            org.jclouds.abiquo.domain.cloud.VirtualMachine vm =
                vapp.getVirtualMachine(virtualMachine.getId());

            if (!vm.getDescription().equals(virtualMachine.getDescription())
                || vm.getCpu() != virtualMachine.getCpu() || vm.getRam() != virtualMachine.getRam()
                || virtualMachine.getPassword() != null
                && !virtualMachine.getPassword().equals(vm.getPassword()))
            {
                vm.setCpu(virtualMachine.getCpu());
                vm.setRam(virtualMachine.getRam());
                vm.setDescription(virtualMachine.getDescription());
                vm.setPassword(virtualMachine.getPassword());

                // Here we actually perform the request to create the virtual machine
                vm.update();
            }
            // else all updated

            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            populateErrors(e, result, "updateVirtualMachine");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    protected String resolveVirtualMachineUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("vapp", String.valueOf(virtualApplianceId));
        params.put("virtualMachine", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{vapp}/virtualmachines/{virtualMachine}",
                params);
    }

    protected VirtualMachineDto createTransferObject(final VirtualMachine virtualMachine)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(virtualMachine.getCpu());
        dto.setRam(virtualMachine.getRam());
        dto.setDescription(virtualMachine.getDescription());
        dto.setHdInBytes(virtualMachine.getHd());
        dto.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        dto.setPassword(virtualMachine.getPassword());
        dto.setName(virtualMachine.getName());
        dto.setVdrpIP(virtualMachine.getVdrpIP());
        dto.setVdrpPort(virtualMachine.getVdrpPort());
        dto.setUuid(virtualMachine.getUUID());
        return dto;
    }

    @Override
    public BasicResult deleteVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        BasicResult result = new BasicResult();
        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());

        ClientResponse response = delete(vmachineUrl);

        if (response.getStatusCode() == Status.NO_CONTENT.getStatusCode())
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteVirtualMachine");
        }

        return result;
    }

    @Override
    public DataResult editVirtualMachineState(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine,
        final VirtualMachineState virtualMachineState)
    {
        DataResult result = new DataResult();
        String url =
            createEditVirtualMachineStateUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());
        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(virtualMachineState);
        ClientResponse response =
            put(url, dto, AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        if (response.getStatusCode() == Status.ACCEPTED.getStatusCode())
        {
            result.setSuccess(true);
            try
            {
                // Retrieve the VirtualDatacenter to associate the new virtual appliance
                org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                    getApiClient().getCloudService().getVirtualDatacenter(virtualDatacenterId)
                        .getVirtualAppliance(virtualApplianceId);

                org.jclouds.abiquo.domain.cloud.VirtualMachine vm =
                    vapp.getVirtualMachine(virtualMachine.getId());
                result.setData(new State(StateEnum.valueOf(vm.getState().name())));
            }
            finally
            {
                releaseApiClient();
            }

        }
        else
        {
            populateErrors(response, result, "editVirtualMachineState");
        }

        return result;
    }

    @Override
    public DataResult rebootVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        DataResult result = new DataResult();
        String url =
            createVirtualMachineResetUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());

        ClientResponse response = post(url, null);

        if (response.getStatusCode() == Status.ACCEPTED.getStatusCode())
        {
            result.setSuccess(true);
            try
            {
                // Retrieve the VirtualDatacenter to associate the new virtual appliance
                org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                    getApiClient().getCloudService().getVirtualDatacenter(virtualDatacenterId)
                        .getVirtualAppliance(virtualApplianceId);

                org.jclouds.abiquo.domain.cloud.VirtualMachine vm =
                    vapp.getVirtualMachine(virtualMachine.getId());
                result.setData(new State(StateEnum.valueOf(vm.getState().name())));
            }
            finally
            {
                releaseApiClient();
            }
        }
        else
        {
            populateErrors(response, result, "rebootVirtualMachine");
        }

        return result;
    }

}
