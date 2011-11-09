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

import java.util.List;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.cloud.VirtualMachineDeployDto;
import com.abiquo.util.ErrorManager;

public class VirtualApplianceResourceStubImpl extends AbstractAPIStub implements
    VirtualApplianceResourceStub
{

    public VirtualApplianceResourceStubImpl()
    {
        super();
    }

    @Override
    public DataResult deployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Boolean forceEnterpriseSoftLimits)
    {
        DataResult result = new DataResult();
        String link = createVirtualApplianceDeployLink(virtualDatacenterId, virtualApplianceId);

        VirtualMachineDeployDto options = new VirtualMachineDeployDto();
        options.setForceEnterpriseSoftLimits(forceEnterpriseSoftLimits);

        ClientResponse response = post(link, options);

        if (response.getStatusCode() == 202)
        {
            result.setSuccess(Boolean.TRUE);
            AcceptedRequestDto entity = response.getEntity(AcceptedRequestDto.class);
            result.setData(entity.getLinks());

        }
        else
        {
            populateErrors(response, result, "deployVirtualAppliance");
        }
        return result;
    }

    @Override
    public DataResult undeployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        DataResult result = new DataResult();
        String link = createVirtualApplianceUndeployLink(virtualDatacenterId, virtualApplianceId);

        ClientResponse response = post(link, null);

        if (response.getStatusCode() == 202)
        {
            result.setSuccess(Boolean.TRUE);
            AcceptedRequestDto entity = response.getEntity(AcceptedRequestDto.class);
            result.setData(entity.getLinks());

        }
        else
        {
            populateErrors(response, result, "undeployVirtualAppliance");
        }
        return result;
    }

    @Override
    public VirtualmachineHB allocate(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualmachineHB vmachineRequ,
        final List<ResourceManagementHB> resMans, final boolean forceEnterpirseLimits,
        final ErrorManager errorManager) throws VirtualApplianceCommandException // TODO
    {
        // VirtualImageWithResourcesDto vimageReq =
        // createVirtualImageAndResourceDto(vmachineRequ, resMans, forceEnterpirseLimits);
        //
        // String vappUrl = resolveVirtualApplianceUrl(virtualDatacenterId, virtualApplianceId);
        //
        // ClientResponse response = post(vappUrl, vimageReq);
        //
        // if (response.getStatusCode() != 201)
        // {
        // onError(response);
        // }
        //
        // Integer vmachineId = response.getEntity(Integer.class);
        //
        // BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
        // EventType.VAPP_CREATE, getCurrentUser(), null, "XXX", "Selected virtual machine", null,
        // null, null, null, null);
        //
        // DAOFactory dao = HibernateDAOFactory.instance();
        // dao.beginConnection();
        // VirtualmachineHB vmachine = dao.getVirtualMachineDAO().findById(vmachineId);
        //
        // /**
        // * TODO substitute vmachineRequ with vmachine on the current virutal appliance
        // */
        //
        // dao.endConnection();
        //
        // return vmachine;
        return null;
    }

    /**
     * vimage constains the modified cpu, ram and hd
     */
    // VirtualImageWithResourcesDto createVirtualImageAndResourceDto(VirtualmachineHB vmachine,
    // List<ResourceManagementHB> resMans, boolean forceEnterpirseLimits)
    // {
    // VirtualImageWithResourcesDto vimageReq = new VirtualImageWithResourcesDto();
    //
    // vimageReq.setVirtualImageId(vmachine.getImage().getIdImage());
    //
    // vimageReq.setRequiredCpu(vmachine.getCpu());
    // vimageReq.setRequiredRam(vmachine.getRam());
    // vimageReq.setRequiredHd(vmachine.getHd());
    //
    // vimageReq.setForeceEnterpriseSoftLimits(forceEnterpirseLimits);
    //
    // Collection<Integer> rasdIds = new HashSet<Integer>();
    // for (ResourceManagementHB rasdHb : resMans)
    // {
    // rasdIds.add(rasdHb.getIdManagement());
    // }
    //
    // vimageReq.setRasdIds(rasdIds);
    //
    // return vimageReq;
    // }
}
