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
package com.abiquo.api.services.cloud;

import static com.abiquo.model.enumerator.VirtualMachineState.NOT_DEPLOYED;

import java.util.List;

import javax.persistence.EntityManager;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.ovf.OVFGeneratorService;
import com.abiquo.api.util.EventingSupport;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;

/**
 * Implements the business logic of the class {@link VirtualAppliance}
 * 
 * @author jdevesa@abiquo.com
 */
@Service
@Transactional(readOnly = true)
public class VirtualApplianceService extends DefaultApiService
{

    private static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    VirtualDatacenterService vdcService;

    @Autowired
    OVFGeneratorService ovfService;

    @Autowired
    RemoteServiceService remoteServiceService;

    @Autowired
    VirtualMachineAllocatorService allocatorService;

    @Autowired
    VirtualApplianceRep virtualApplianceRepo;

    @Autowired
    UserService userService;

    @Autowired
    VirtualMachineService vmService;

    public VirtualApplianceService()
    {

    }

    public VirtualApplianceService(final EntityManager em)
    {
        this.virtualApplianceRepo = new VirtualApplianceRep(em);
        this.repo = new VirtualDatacenterRep(em);
        this.vdcService = new VirtualDatacenterService(em);
        this.remoteServiceService = new RemoteServiceService(em);
        this.userService = new UserService(em);
    }

    /**
     * Retrieves the list of virtual appliances by an unique virtual datacenter
     * 
     * @param vdcId identifier of the virtualdatacenter.
     * @return the list of {@link VirtualAppliance} pojo
     */
    public List<VirtualAppliance> getVirtualAppliancesByVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);
        return (List<VirtualAppliance>) repo.findVirtualAppliancesByVirtualDatacenter(vdc);
    }

    public VirtualAppliance getVirtualApplianceByVirtualMachine(final VirtualMachine virtualMachine)
    {
        return virtualApplianceRepo.findVirtualApplianceByVirtualMachine(virtualMachine);
    }

    /**
     * Returns the virtual appliance identi
     * 
     * @param vdcId i
     * @param vappId
     * @return
     */
    public VirtualAppliance getVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        if (vappId == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vappId);
        if (vapp == null || !vapp.getVirtualDatacenter().getId().equals(vdcId))
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        return vapp;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void startVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        Datacenter datacenter = virtualAppliance.getVirtualDatacenter().getDatacenter();

        try
        {
            if (virtualAppliance.getState() == VirtualMachineState.NOT_DEPLOYED)
            {
                allocate(virtualAppliance);

                virtualAppliance.setState(VirtualMachineState.IN_PROGRESS);
                repo.updateVirtualAppliance(virtualAppliance);

                EnvelopeType envelop = ovfService.createVirtualApplication(virtualAppliance);

                Document docEnvelope = OVFSerializer.getInstance().bindToDocument(envelop, false);

                RemoteService vsm =
                    remoteServiceService.getRemoteService(datacenter.getId(),
                        RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

                RemoteService vf =
                    remoteServiceService.getRemoteService(datacenter.getId(),
                        RemoteServiceType.VIRTUAL_FACTORY);

                long timeout = Long.valueOf(ConfigService.getServerTimeout());

                Resource resource =
                    ResourceFactory.create(vf.getUri(), RESOURCE_URI, timeout, docEnvelope,
                        ResourceFactory.LATEST);

                EventingSupport.subscribeToAllVA(virtualAppliance, vsm.getUri());

                changeState(resource, envelop, VirtualMachineState.RUNNING.toResourceState());
            }
        }
        catch (Exception e)
        {
            virtualAppliance.setState(VirtualMachineState.NOT_DEPLOYED);
            repo.updateVirtualAppliance(virtualAppliance);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void addImage(final Integer virtualDatacenterId, final Integer virtualApplianceId,
        final VirtualImageDto image)
    {

    }

    private void allocate(final VirtualAppliance virtualAppliance)
    {
        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            allocatorService.allocateVirtualMachine(node.getVirtualMachine().getId(),
                virtualAppliance.getId(), false);
        }
    }

    private void changeState(final Resource resource, final EnvelopeType envelope,
        final String machineState) throws Exception
    {
        EnvelopeType envelopeRunning = ovfService.changeStateVirtualMachine(envelope, machineState);
        Document docEnvelopeRunning =
            OVFSerializer.getInstance().bindToDocument(envelopeRunning, false);

        resource.put(docEnvelopeRunning);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualAppliance createVirtualAppliance(final Integer vdcId,
        final VirtualApplianceDto dto)
    {
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        VirtualAppliance vapp =
            new VirtualAppliance(vdc.getEnterprise(),
                vdc,
                dto.getName(),
                NOT_DEPLOYED,
                NOT_DEPLOYED);

        vapp.setHighDisponibility(dto.getHighDisponibility());
        vapp.setPublicApp(dto.getPublicApp());
        vapp.setNodeconnections(dto.getNodecollections());

        if (!vapp.isValid())
        {
            addValidationErrors(vapp.getValidationErrors());
            flushErrors();
        }

        repo.insertVirtualAppliance(vapp);

        return vapp;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualAppliance updateVirtualAppliance(final Integer vdcId, final Integer vappId,
        final VirtualApplianceDto dto)
    {
        VirtualAppliance vapp = getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());

        vapp.setName(dto.getName());

        repo.updateVirtualAppliance(vapp);

        return vapp;
    }
}
