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

package com.abiquo.abiserver.commands.stub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;

public class VirtualApplianceResourceStub extends AbstractAPIStub
{

    public VirtualApplianceResourceStub()
    {
        super();
    }

    public VirtualmachineHB allocate(Integer virtualDatacenterId, Integer virtualApplianceId,
        VirtualmachineHB vmachineRequ, List<ResourceManagementHB> resMans,
        boolean forceEnterpirseLimits, ErrorManager errorManager)
        throws VirtualApplianceCommandException // TODO
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

    private String resolveVirtualApplianceUrl(Integer virtualDatacenterId,
        Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("{virtualDatacenter}", String.valueOf(virtualDatacenterId));
        params.put("{vapp}", String.valueOf(virtualApplianceId));

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/vapps/{vapp}", params);
    }
}
